<%@ page language="java" import="java.util.*,java.sql.*,org.apache.log4j.*" pageEncoding="ISO-8859-1"%>

<jsp:useBean id='user' class='user.UserBean'  scope='session'/>
<jsp:useBean id='connect' class='ConnectBean.ConnectBean'  scope='session'/>
<jsp:useBean id='mail' class='MailBean.MailBean'  scope='session'/>
<jsp:useBean id='create' class='create_main.Create' scope='session'/>

<%

String username = user.getUsername();
Connection connection = connect.getConnection();
Connection ccConn = connect.getCcConnection();
Connection paramConn = connect.getParamConnection();

ServletContext context = getServletContext() ;  
String versionParam = context.getInitParameter("version");

//Logging configuration
String log4jConfigFile = context.getInitParameter("log4j_property_file");
Logger logger = Logger.getLogger("rootLogger");
logger.setLevel(Level.DEBUG);
PropertyConfigurator.configure(log4jConfigFile);

logger.debug("('" + username + "') - create_process.jsp");

String dbn = request.getParameter("dbn");
String dbid = request.getParameter("dbid");


String[] tablenames = null;
if(dbn.equals("ACC")){

    //Retrieve data parameter information
    tablenames = new String[3];
    tablenames[0] = "Identification";
    tablenames[1] = "ACC_DiagnosticProcedures";
    tablenames[2] = "ACC_TumorStaging";

}else if(dbn.equals("Pheo")){
        
    //Retrieve data parameter information
    tablenames = new String[2];
    tablenames[0] = "Identification";
    tablenames[1] = "Pheo_PatientHistory";
    
}else if(dbn.equals("NAPACA")){    

    //Retrieve data parameter information
    tablenames = new String[2];
    tablenames[0] = "Identification";
    tablenames[1] = "NAPACA_DiagnosticProcedures";
        
}else if(dbn.equals("APA")){

    //Retrieve data parameter information
    tablenames = new String[2];
    tablenames[0] = "Identification";
    tablenames[1] = "APA_PatientHistory";        
}

Vector<Vector> parameters = create.getParameters(tablenames,request,paramConn);

//Need to find the specific value of the centerId
String centerId = create.getParameterValue("center_id", parameters);

//Need to retrieve the last ID from the database (for that center) and calculate next ID
int nextId = 0;
//THIS IS WHERE THE ID CALL-OUT IS
nextId = create.getNextId(centerId, ccConn);

//Now execute update
create.executeParameterUpdate(username, dbn, nextId,centerId,"Identification", parameters, connection, request);
create.executeParameterUpdate(username, dbn, nextId,centerId,"Associated_Studies", parameters, connection, request);

//Feed this value back into the center_callout database
create.setNextId(centerId, nextId, ccConn);

//Execute updates of the tertiary main tables
if(dbn.equals("ACC")){
    create.executeParameterUpdate(username, dbn, nextId,centerId,"ACC_DiagnosticProcedures", parameters, connection, request);
    create.executeParameterUpdate(username, dbn, nextId,centerId,"ACC_TumorStaging", parameters, connection, request);
    create.executeParameterUpdate(username, dbn, nextId,centerId,"ACC_Imaging", parameters, connection, request);
}else if(dbn.equals("Pheo")){
    create.executeParameterUpdate(username, dbn, nextId,centerId,"Pheo_PatientHistory", parameters, connection, request);
    create.executeParameterUpdate(username, dbn, nextId,centerId,"Pheo_FirstDiagnosisPresentation", parameters, connection, request);
    create.executeParameterUpdate(username, dbn, nextId,centerId,"Pheo_OtherOrgans", parameters, connection, request);    
    create.executeParameterUpdate(username, dbn, nextId,centerId,"Pheo_HormoneSymptoms", parameters, connection, request);
    create.executeParameterUpdate(username, dbn, nextId,centerId,"Pheo_TumorSymptoms", parameters, connection, request);
    create.executeParameterUpdate(username, dbn, nextId,centerId,"Pheo_FirstDiagnosisTNM", parameters, connection, request);
    create.executeParameterUpdate(username, dbn, nextId,centerId,"Pheo_MalignantDiagnosisTNM", parameters, connection, request);
}else if(dbn.equals("NAPACA")){
    create.executeParameterUpdate(username, dbn, nextId,centerId,"NAPACA_DiagnosticProcedures", parameters, connection, request);
}else if(dbn.equals("APA")){    
    create.executeParameterUpdate(username, dbn, nextId,centerId,"APA_PatientHistory", parameters, connection, request);
}

String nextIdStr = "" + nextId;


//Find out which studies the patient belongs to, get the study addresses and send mail
String[] studies = create.getStudiesIncluded(request);    
String[] studyAddresses = create.getStudyAddresses(studies, connection);
String[] studyLabels = create.getStudyLabels(studies, connection);
String mailId = "" + centerId + "-" + nextIdStr;
mail.sendMail(request, response, mailId,studies,studyAddresses,studyLabels);

//And now redirect to the appropriate page afterwards
response.sendRedirect("./create_result.jsp?dbid=" + dbid + "&dbn=" + dbn + "&createdid=" + nextIdStr + "&centerid=" + centerId);
%>