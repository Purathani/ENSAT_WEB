<%@ page language="java" import="java.util.*,java.sql.*" pageEncoding="ISO-8859-1"%>

<jsp:useBean id='connect' class='ConnectBean.ConnectBean'  scope='session'/>
<jsp:useBean id='updatesub' class='update_main.UpdateSub'  scope='session'/>

<%
//ServletContext context = this.getServletContext();

String dbn = request.getParameter("dbn");
String dbid = request.getParameter("dbid");
String pid = request.getParameter("pid");
pid = updatesub.standardisePid(pid);
String centerid = request.getParameter("centerid");
String modality = request.getParameter("modality");
String modid = request.getParameter("modid"); 

Connection connection = connect.getConnection();
Connection paramConn = connect.getParamConnection();

//Retrieve data parameter information
String[] subTablenames = new String[1];
subTablenames[0] = updatesub.getSubTablename(modality, dbn);
Vector<Vector> parameters = updatesub.getParameters(subTablenames,request,paramConn);

//Now execute update
String tableIdName = updatesub.getSubTableIdName(modality, dbn);
updatesub.executeParameterUpdate(tableIdName,modid,subTablenames[0], pid, centerid, parameters, connection, request);
if(subTablenames[0].equals("ACC_Chemotherapy")){
    String multipleTablename = "ACC_Chemotherapy_Regimen";
    String multipleTableIdName = updatesub.getMultipleSubTableIdName(multipleTablename);
        
    //NOTE THE FIRST tableIdName IS THE ONE OF THE PARENT SUB-FORM: THIS IS INTENTIONAL
    updatesub.executeParameterUpdate(tableIdName, modid, multipleTablename, pid, centerid, parameters, connection, request);    
}else if(subTablenames[0].equals("ACC_FollowUp")){
    String multipleTablename = "ACC_FollowUp_Organs";
    String multipleTableIdName = updatesub.getMultipleSubTableIdName(multipleTablename);
    
    //NOTE THE FIRST tableIdName IS THE ONE OF THE PARENT SUB-FORM: THIS IS INTENTIONAL
    updatesub.executeParameterUpdate(tableIdName, modid, multipleTablename, pid, centerid, parameters, connection, request);    
}else if(subTablenames[0].equals("ACC_Radiofrequency")){
    String multipleTablename = "ACC_Radiofrequency_Loc";
    String multipleTableIdName = updatesub.getMultipleSubTableIdName(multipleTablename);
    
    //NOTE THE FIRST tableIdName IS THE ONE OF THE PARENT SUB-FORM: THIS IS INTENTIONAL
    updatesub.executeParameterUpdate(tableIdName, modid, multipleTablename, pid, centerid, parameters, connection, request);    
}else if(subTablenames[0].equals("ACC_Radiotherapy")){
    String multipleTablename = "ACC_Radiotherapy_Loc";
    String multipleTableIdName = updatesub.getMultipleSubTableIdName(multipleTablename);
    
    //NOTE THE FIRST tableIdName IS THE ONE OF THE PARENT SUB-FORM: THIS IS INTENTIONAL
    updatesub.executeParameterUpdate(tableIdName, modid, multipleTablename, pid, centerid, parameters, connection, request);    
}else if(subTablenames[0].equals("Pheo_TumorDetails")){
    String multipleTablename = "Pheo_MetastasesLocation";
    String multipleTableIdName = updatesub.getMultipleSubTableIdName(multipleTablename);
    
    //NOTE THE FIRST tableIdName IS THE ONE OF THE PARENT SUB-FORM: THIS IS INTENTIONAL
    updatesub.executeParameterUpdate(tableIdName, modid, multipleTablename, pid, centerid, parameters, connection, request);    
}else if(subTablenames[0].equals("Pheo_ImagingTests")){
    String multipleTablename = "Pheo_ImagingTests_CTLoc";
    String multipleTableIdName = updatesub.getMultipleSubTableIdName(multipleTablename);
    
    //NOTE THE FIRST tableIdName IS THE ONE OF THE PARENT SUB-FORM: THIS IS INTENTIONAL
    updatesub.executeParameterUpdate(tableIdName, modid, multipleTablename, pid, centerid, parameters, connection, request);
    multipleTablename = "Pheo_ImagingTests_NMRLoc";
    multipleTableIdName = updatesub.getMultipleSubTableIdName(multipleTablename);    
    
    //NOTE THE FIRST tableIdName IS THE ONE OF THE PARENT SUB-FORM: THIS IS INTENTIONAL
    updatesub.executeParameterUpdate(tableIdName, modid, multipleTablename, pid, centerid, parameters, connection, request);
}else if(subTablenames[0].equals("Pheo_Surgery")){
    String multipleTablename = "Pheo_Surgery_PreOp";
    String multipleTableIdName = updatesub.getMultipleSubTableIdName(multipleTablename);
    
    //NOTE THE FIRST tableIdName IS THE ONE OF THE PARENT SUB-FORM: THIS IS INTENTIONAL
    updatesub.executeParameterUpdate(tableIdName, modid, multipleTablename, pid, centerid, parameters, connection, request);
    multipleTablename = "Pheo_Surgery_IntraOp";
    multipleTableIdName = updatesub.getMultipleSubTableIdName(multipleTablename);    
    
    //NOTE THE FIRST tableIdName IS THE ONE OF THE PARENT SUB-FORM: THIS IS INTENTIONAL
    updatesub.executeParameterUpdate(tableIdName, modid, multipleTablename, pid, centerid, parameters, connection, request);
    multipleTablename = "Pheo_Surgery_Procedure";
    multipleTableIdName = updatesub.getMultipleSubTableIdName(multipleTablename);    
    
    //NOTE THE FIRST tableIdName IS THE ONE OF THE PARENT SUB-FORM: THIS IS INTENTIONAL
    updatesub.executeParameterUpdate(tableIdName, modid, multipleTablename, pid, centerid, parameters, connection, request);
}else if(subTablenames[0].equals("ACC_Surgery")){
    String multipleTablename = "ACC_Surgery_Extended";
    String multipleTableIdName = updatesub.getMultipleSubTableIdName(multipleTablename);
    
    //NOTE THE FIRST tableIdName IS THE ONE OF THE PARENT SUB-FORM: THIS IS INTENTIONAL
    updatesub.executeParameterUpdate(tableIdName, modid, multipleTablename, pid, centerid, parameters, connection, request);
    multipleTablename = "ACC_Surgery_First";
    multipleTableIdName = updatesub.getMultipleSubTableIdName(multipleTablename);    
    
    //NOTE THE FIRST tableIdName IS THE ONE OF THE PARENT SUB-FORM: THIS IS INTENTIONAL
    updatesub.executeParameterUpdate(tableIdName, modid, multipleTablename, pid, centerid, parameters, connection, request);
}else if(subTablenames[0].equals("ACC_Biomaterial")){
    String multipleTablename = "ACC_Biomaterial_Normal_Tissue";
    String multipleTableIdName = updatesub.getMultipleSubTableIdName(multipleTablename);
    
    //NOTE THE FIRST tableIdName IS THE ONE OF THE PARENT SUB-FORM: THIS IS INTENTIONAL
    updatesub.executeParameterUpdate(tableIdName, modid, multipleTablename, pid, centerid, parameters, connection, request);    
}else if(subTablenames[0].equals("Pheo_Biomaterial")){
    String multipleTablename = "Pheo_Biomaterial_Normal_Tissue";
    String multipleTableIdName = updatesub.getMultipleSubTableIdName(multipleTablename);
    
    //NOTE THE FIRST tableIdName IS THE ONE OF THE PARENT SUB-FORM: THIS IS INTENTIONAL
    updatesub.executeParameterUpdate(tableIdName, modid, multipleTablename, pid, centerid, parameters, connection, request);    
}else if(subTablenames[0].equals("NAPACA_Biomaterial")){
    String multipleTablename = "NAPACA_Biomaterial_Normal_Tissue";
    String multipleTableIdName = updatesub.getMultipleSubTableIdName(multipleTablename);
        
    //NOTE THE FIRST tableIdName IS THE ONE OF THE PARENT SUB-FORM: THIS IS INTENTIONAL
    updatesub.executeParameterUpdate(tableIdName, modid, multipleTablename, pid, centerid, parameters, connection, request);    
}else if(subTablenames[0].equals("APA_Biomaterial")){
    String multipleTablename = "APA_Biomaterial_Normal_Tissue";
    String multipleTableIdName = updatesub.getMultipleSubTableIdName(multipleTablename);
    
    //NOTE THE FIRST tableIdName IS THE ONE OF THE PARENT SUB-FORM: THIS IS INTENTIONAL
    updatesub.executeParameterUpdate(tableIdName, modid, multipleTablename, pid, centerid, parameters, connection, request);    
}


%>

<jsp:forward page="/jsp/modality/update/update_result.jsp">
<jsp:param name="dbid" value="<%=dbid%>"/>
<jsp:param name="dbn" value="<%=dbn%>"/>
<jsp:param name="pid" value="<%=pid%>"/>
<jsp:param name="centerid" value="<%=centerid%>"/>
<jsp:param name="modid" value="<%=modid%>"/>
</jsp:forward>
