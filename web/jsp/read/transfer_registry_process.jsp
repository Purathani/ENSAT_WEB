<%@ page language="java" import="java.util.*,java.sql.*" pageEncoding="ISO-8859-1"%>

<jsp:useBean id='connect' class='ConnectBean.ConnectBean'  scope='session'/>
<jsp:useBean id='transfer' class='update_main.Transfer' scope='session'/>

<%
String pid = request.getParameter("pid");
String centerid = request.getParameter("centerid");
String study = request.getParameter("study");

Connection connection = connect.getConnection();
Statement statement = connection.createStatement();

//Update the database string in the Identification table
if(!study.equals("")){
    transfer.updateIdentification(study, centerid, pid, statement);    
}

//Retrieve the data from NAPACA_DiagnosticProcedures
String[] napacaData = transfer.getNapacaData(centerid,pid,statement);
Vector<Vector> napacaImagingData = transfer.getNapacaImagingData(centerid,pid,statement);

Vector<Vector> biomaterialData = transfer.getBiomaterialData(centerid,pid,statement);
Vector<Vector> biomaterialAliquotData = transfer.getBiomaterialAliquotData(centerid,pid,statement);
Vector<Vector> biomaterialNormalTissueData = transfer.getBiomaterialNormalTissueData(centerid,pid,statement);

//Now do the mapping (dependent on the database being switched to)
transfer.mapNewSectionData(centerid,pid,statement,napacaData,napacaImagingData,biomaterialData,biomaterialAliquotData,biomaterialNormalTissueData,study);
%>

<jsp:forward page="/jsp/read/transfer_registry_result.jsp">
    <jsp:param name="study" value="<%=study%>"/>    
</jsp:forward>
