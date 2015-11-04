<%@ page language="java" import="java.util.*,java.sql.*,org.apache.log4j.*" pageEncoding="ISO-8859-1"%>

<jsp:include page="/jsp/page/check_credentials.jsp" />
<jsp:include page="/jsp/page/check_input.jsp" />
<jsp:include page="/jsp/page/page_head.jsp" />

<td colspan="2" width="81%" align="left" valign="top" background="/images/possbk.jpg"><table width="1200" border="0" cellspacing="0" cellpadding="10"> 
      <tr> 
        <td width="15" align="left" valign="top">&nbsp;</td> 
        <td align="left" valign="top"><!-- #BeginEditable "MainText" --> 

<jsp:include page="/jsp/page/page_nav.jsp" />

<jsp:useBean id='connect' class='ConnectBean.ConnectBean' scope='session'/>
<jsp:useBean id='presentation' class='summaryinfo.SummaryInfo' scope='session'/>
<jsp:useBean id='user' class='user.UserBean'  scope='session'/>
<%--<jsp:useBean id='generate_manifest' class='com.ensat.qr.GenerateAliquotManifestExcel'  scope='session'/>--%>



<%    
ServletContext context = this.getServletContext();
Connection conn = connect.getConnection();
Connection ccConn = connect.getCcConnection();
    
//Logging configuration
String log4jConfigFile = context.getInitParameter("log4j_property_file");
Logger logger = Logger.getLogger("rootLogger");
logger.setLevel(Level.DEBUG);
PropertyConfigurator.configure(log4jConfigFile);
        
String username = user.getUsername();
logger.debug("('" + username + "') - freezer_inventory.jsp");

String centerid = request.getParameter("centerid");
if(centerid == null){
    centerid = "";
}

String component = "";
String cptid = "";
component = request.getParameter("component");
cptid = request.getParameter("cptid");
if(component == null){
    component = "freezer";
}
if(cptid == null){
    cptid = "1";
}

//String freezerHtml = presentation.getFreezerInventory(centerid);
String freezerHtml = "";
if(!centerid.equals("")){
    freezerHtml = presentation.getFreezerStructureHtml(conn,ccConn,centerid, component, cptid);
}

String aliquotsTransferredHtml = "";
String aliquotsReceivedHtml = "";
if(!centerid.equals("")){
    aliquotsTransferredHtml = presentation.getAliquotsTransferredDetail(conn,centerid);
    aliquotsReceivedHtml = presentation.getAliquotsReceived(conn,centerid);
}

%>

<h2>Biobank Freezer Inventory - <%=centerid%></h2>

<%
if(centerid.equals("")){
%>
<p><strong>No center selected</strong></p>
<%
}else{
%>



<%=freezerHtml%>

<%
}
%>

<h2>Biobank Samples Transferred - <%=centerid%></h2>

<%
if(centerid.equals("")){
%>
<p><strong>No center selected</strong></p>
<%
}else{

%>

<%=aliquotsTransferredHtml%>

<%
}
%>

<h2>Biobank Samples Received - <%=centerid%></h2>

<%
if(centerid.equals("")){
%>
<p><strong>No center selected</strong></p>
<%
}else{
%>

<%=aliquotsReceivedHtml%>

<%
}
%>




<jsp:include page="/jsp/page/page_foot.jsp" />

</td> 
      </tr> 
    </table> 
    </td> 
    
</tr>

</table>


