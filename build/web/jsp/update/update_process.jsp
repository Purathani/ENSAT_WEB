<%@ page language="java" import="java.util.*,java.sql.*,org.apache.log4j.*" pageEncoding="ISO-8859-1"%>

<jsp:useBean id='connect' class='ConnectBean.ConnectBean'  scope='session'/>
<jsp:useBean id='update' class='update_main.Update' scope='session'/>
<jsp:useBean id='mail' class='MailBean.MailBean'  scope='session'/>

<%
ServletContext context = getServletContext();

Connection connection = connect.getConnection();
Connection paramConn = connect.getParamConnection();

//Logging configuration
String log4jConfigFile = context.getInitParameter("log4j_property_file");
Logger logger = Logger.getLogger("rootLogger");
logger.setLevel(Level.DEBUG);
PropertyConfigurator.configure(log4jConfigFile);

String dbn = request.getParameter("dbn");
String dbid = request.getParameter("dbid");
String pid = request.getParameter("pid");
pid = update.standardisePid(pid);
String centerid = request.getParameter("centerid");

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

Vector<Vector> parameters = update.getParameters(tablenames,request, paramConn);

//Now execute update
update.executeParameterUpdate(pid, centerid,"Identification", parameters, connection, request);

//Need to have a check in here to see if the Associated Studies entry has changed
//Check database to see if this patient is already registered for EURINE-ACT
boolean inEurineAct = update.getPatientInStudy(centerid, pid, "EURINE-ACT", connection);

//Check parameter list to see if this patient is being enrolled in EURINE-ACT
boolean enrollingInEurineAct = update.getPatientInStudyList(parameters, "EURINE-ACT");

//If the patient is being enrolled in EURINE-ACT and wasn't there before then send a mail to the relevant addresses
if(enrollingInEurineAct){
    if(!inEurineAct){
        String[] studies = {"eurineact"};
        String[] studyAddresses = update.getStudyAddresses(studies, connection);
        String[] studyLabels = update.getStudyLabels(studies, connection);
        String mailId = "" + centerid + "-" + pid;
        mail.sendMail(request, response, mailId,studies,studyAddresses,studyLabels);
    }
}

update.executeParameterUpdate(pid, centerid,"Associated_Studies", parameters, connection, request);

//Execute updates of the tertiary main tables
if(dbn.equals("ACC")){
    update.executeParameterUpdate(pid, centerid,"ACC_DiagnosticProcedures", parameters, connection, request);
    update.executeParameterUpdate(pid, centerid,"ACC_TumorStaging", parameters, connection, request);
    update.executeParameterUpdate(pid, centerid,"ACC_Imaging", parameters, connection, request);
}else if(dbn.equals("Pheo")){
    update.executeParameterUpdate(pid, centerid,"Pheo_PatientHistory", parameters, connection, request);
    update.executeParameterUpdate(pid, centerid,"Pheo_FirstDiagnosisPresentation", parameters, connection, request);
    update.executeParameterUpdate(pid, centerid,"Pheo_OtherOrgans", parameters, connection, request);
    
    update.executeParameterUpdate(pid, centerid,"Pheo_HormoneSymptoms", parameters, connection, request);
    update.executeParameterUpdate(pid, centerid,"Pheo_TumorSymptoms", parameters, connection, request);
    update.executeParameterUpdate(pid, centerid,"Pheo_FirstDiagnosisTNM", parameters, connection, request);
    update.executeParameterUpdate(pid, centerid,"Pheo_MalignantDiagnosisTNM", parameters, connection, request);
}else if(dbn.equals("NAPACA")){
    update.executeParameterUpdate(pid, centerid,"NAPACA_DiagnosticProcedures", parameters, connection, request);
}else if(dbn.equals("APA")){    
    update.executeParameterUpdate(pid, centerid,"APA_PatientHistory", parameters, connection, request);
}

//And now redirect to the appropriate page afterwards
response.sendRedirect("./update_result.jsp?dbid=" + dbid + "&dbn=" + dbn + "&pid=" + pid + "&centerid=" + centerid);
%>
