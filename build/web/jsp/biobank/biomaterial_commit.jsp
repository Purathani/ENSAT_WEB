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

<%    
ServletContext context = this.getServletContext();
Connection conn = connect.getConnection();

//Logging configuration
String log4jConfigFile = context.getInitParameter("log4j_property_file");
Logger logger = Logger.getLogger("rootLogger");
logger.setLevel(Level.DEBUG);
PropertyConfigurator.configure(log4jConfigFile);
        
String username = user.getUsername();
logger.debug("('" + username + "') - biomaterial_commit.jsp");

String centerid = request.getParameter("centerid");
if(centerid == null){
    centerid = "";
}

String dbn = request.getParameter("dbn");
if(dbn == null){
    dbn = "";
}

Vector<Vector> unoccupiedRows = presentation.getFreezerPosToUpload();
boolean uploaded = presentation.uploadFreezerManifest(centerid,dbn,unoccupiedRows,conn);
//boolean uploaded = true;

%>

<%
if(uploaded){
%>

<p><strong>Biomaterial localization information uploaded successfully</strong></p>

<%
}else{
%>
<p><strong>Localization information upload was unsuccessful - please try again</strong></p>
<%
}
%>

<p><a href="./jsp/biobank/biomaterial_upload.jsp?centerid=<%=centerid%>">Upload new localization file</a></p>

<jsp:include page="/jsp/page/page_foot.jsp" />

</td> 
      </tr> 
    </table> 
    </td> 
    
</tr>

</table>


