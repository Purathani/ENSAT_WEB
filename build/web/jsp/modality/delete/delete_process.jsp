<%@ page language="java" import="java.util.*,java.sql.*" pageEncoding="ISO-8859-1"%>

<jsp:useBean id='connect' class='ConnectBean.ConnectBean'  scope='session'/>
<jsp:useBean id='deletesub' class='delete_main.DeleteSub'  scope='session'/>

<%
String dbn = request.getParameter("dbn");
String dbid = request.getParameter("dbid");
String pid = request.getParameter("pid");
pid = deletesub.standardisePid(pid);
String centerid = request.getParameter("centerid");
String modality = request.getParameter("modality");
String modid = request.getParameter("modid");

Connection connection = connect.getConnection();

String[] subTablenames = new String[1];
subTablenames[0] = deletesub.getSubTablename(modality,dbn);
String tableIdName = deletesub.getSubTableIdName(modality,dbn);
//deletesub.deleteForm(statement, pid, centerid, dbn, subTablenames[0], modid, tableIdName);
deletesub.deleteForm(connection, pid, centerid, dbn, subTablenames[0], modid, tableIdName);
if(subTablenames[0].equals("ACC_Chemotherapy")){    
    //deletesub.deleteForm(statement, pid, centerid, dbn, "ACC_Chemotherapy_Regimen", modid, tableIdName);    
    deletesub.deleteForm(connection, pid, centerid, dbn, "ACC_Chemotherapy_Regimen", modid, tableIdName);    
}else if(subTablenames[0].equals("ACC_FollowUp")){
    //deletesub.deleteForm(statement, pid, centerid, dbn, "ACC_FollowUp_Organs", modid, tableIdName);    
    deletesub.deleteForm(connection, pid, centerid, dbn, "ACC_FollowUp_Organs", modid, tableIdName);    
}else if(subTablenames[0].equals("ACC_Radiofrequency")){
    //deletesub.deleteForm(statement, pid, centerid, dbn, "ACC_Radiofrequency_Loc", modid, tableIdName);    
    deletesub.deleteForm(connection, pid, centerid, dbn, "ACC_Radiofrequency_Loc", modid, tableIdName);    
}else if(subTablenames[0].equals("ACC_Radiotherapy")){
    //deletesub.deleteForm(statement, pid, centerid, dbn, "ACC_Radiotherapy_Loc", modid, tableIdName);    
    deletesub.deleteForm(connection, pid, centerid, dbn, "ACC_Radiotherapy_Loc", modid, tableIdName);    
}else if(subTablenames[0].equals("ACC_Surgery")){
    //deletesub.deleteForm(statement, pid, centerid, dbn, "ACC_Surgery_Extended", modid, tableIdName);
    deletesub.deleteForm(connection, pid, centerid, dbn, "ACC_Surgery_Extended", modid, tableIdName);
    //deletesub.deleteForm(statement, pid, centerid, dbn, "ACC_Surgery_First", modid, tableIdName);    
    deletesub.deleteForm(connection, pid, centerid, dbn, "ACC_Surgery_First", modid, tableIdName);
}else if(subTablenames[0].equals("ACC_Biomaterial")){
    //deletesub.deleteForm(statement, pid, centerid, dbn, "ACC_Biomaterial_Normal_Tissue", modid, tableIdName);    
    deletesub.deleteForm(connection, pid, centerid, dbn, "ACC_Biomaterial_Normal_Tissue", modid, tableIdName);
    deletesub.deleteForm(connection, pid, centerid, dbn, "ACC_Biomaterial_Aliquots", modid, tableIdName);
    deletesub.deleteForm(connection, pid, centerid, dbn, "ACC_Biomaterial_Freezer_Information", modid, tableIdName);
}else if(subTablenames[0].equals("Pheo_Biomaterial")){
    //deletesub.deleteForm(statement, pid, centerid, dbn, "Pheo_Biomaterial_Normal_Tissue", modid, tableIdName);    
    deletesub.deleteForm(connection, pid, centerid, dbn, "Pheo_Biomaterial_Normal_Tissue", modid, tableIdName);
    deletesub.deleteForm(connection, pid, centerid, dbn, "Pheo_Biomaterial_Aliquots", modid, tableIdName);
    deletesub.deleteForm(connection, pid, centerid, dbn, "Pheo_Biomaterial_Freezer_Information", modid, tableIdName);
}else if(subTablenames[0].equals("NAPACA_Biomaterial")){
    //deletesub.deleteForm(statement, pid, centerid, dbn, "NAPACA_Biomaterial_Normal_Tissue", modid, tableIdName);    
    deletesub.deleteForm(connection, pid, centerid, dbn, "NAPACA_Biomaterial_Normal_Tissue", modid, tableIdName);
    deletesub.deleteForm(connection, pid, centerid, dbn, "NAPACA_Biomaterial_Aliquots", modid, tableIdName);
    deletesub.deleteForm(connection, pid, centerid, dbn, "NAPACA_Biomaterial_Freezer_Information", modid, tableIdName);
}else if(subTablenames[0].equals("APA_Biomaterial")){
    //deletesub.deleteForm(statement, pid, centerid, dbn, "APA_Biomaterial_Normal_Tissue", modid, tableIdName);    
    deletesub.deleteForm(connection, pid, centerid, dbn, "APA_Biomaterial_Normal_Tissue", modid, tableIdName);
    deletesub.deleteForm(connection, pid, centerid, dbn, "APA_Biomaterial_Aliquots", modid, tableIdName);
    deletesub.deleteForm(connection, pid, centerid, dbn, "APA_Biomaterial_Freezer_Information", modid, tableIdName);
}
%>

<jsp:forward page="/jsp/modality/delete/delete_result.jsp">
<jsp:param name="dbid" value="<%=dbid%>"/>
<jsp:param name="dbn" value="<%=dbn%>"/>
</jsp:forward>
