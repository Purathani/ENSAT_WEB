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
    //Moving Github on
ServletContext context = getServletContext() ;  
//Logging configuration
String log4jConfigFile = context.getInitParameter("log4j_property_file");
Logger logger = Logger.getLogger("rootLogger");
logger.setLevel(Level.DEBUG);
PropertyConfigurator.configure(log4jConfigFile);

//This is the initial setting up of the connections that will be used from now on in the session
Connection conn = connect.getConnection();
Connection secConn = connect.getSecConnection();
Connection paramConn = connect.getParamConnection();
Connection ccConn = connect.getCcConnection();

connect.setConnections(context,conn,secConn,paramConn,ccConn);
conn = connect.getConnection();
secConn = connect.getSecConnection();
paramConn = connect.getParamConnection();
ccConn = connect.getCcConnection();
if(conn == null){
    logger.debug("conn is null...");
}

String[] ensatSections = {"ACC","Pheo","NAPACA","APA",""};


//Set logfile parameter in any auxiliary classes here
presentation.setLogfileName(log4jConfigFile);
        
String username = user.getUsername();
logger.debug("('" + username + "') - home.jsp");

String userCenter = user.getCenter();

%>

<p><h2><a href="/ENSAT//jsp/study_docs.jsp">Study protocols and SOPs</a></h2></p>

<p><h2><a href="/ENSAT//jsp/maps.jsp">International Coverage Map</a></h2></p>

<p><h2><a href="/ENSAT//jsp/centers_pis.jsp">Centers and Principal Investigators</a></h2></p>

<%
if(username.equals("astell@unimelb.edu.au") || username.equals("test@testuser.com") || username.equals("rsinnott@unimelb.edu.au") || username.equals("graeme.eisenhofer@uniklinium-dresden.de")){
%>

<h2>Biobank (<%=userCenter%>)</h2>

<p>
<ul>
    <li><strong><a href="/ENSAT/jsp/biobank/freezer_inventory.jsp?centerid=<%=userCenter%>">Freezer Inventory</a></strong></li>
    <li><strong><a href="/ENSAT/jsp/biobank/biomaterial_upload.jsp?centerid=<%=userCenter%>">Biomaterial localization Upload</a></strong></li>
</ul>
</p>
<%
}
%>

<%
if(username.equals("astell@unimelb.edu.au")
        || username.equals("rsinnott@unimelb.edu.au") || username.equals("test@testuser.com")){
%>

<h2>Data Completeness</h2>

<p>
<ul>
    <!--<li><strong><a href="/jsp/quality/dqs.jsp">Data Quality</a></strong></li>-->
    <li><strong><a href="/jsp/quality/completeness.jsp">Completeness</a></strong></li>    
</ul>
</p>
<%
}
%>



<h2>Summary</h2>

<p>
<%
String summaryHtml = presentation.getSummaryHtml(conn, ensatSections);
%>
<%=summaryHtml%>
</p>

<h2>Associated Study and Registry Distribution</h2>

<%
String studyHtml = presentation.getStudyHtml(conn, ensatSections);
%>
<%= studyHtml%>


<h2>Rankings</h2>

<p>
    <%    
    String rankingHtml = presentation.getRankingHtml(conn, ensatSections);    
    %>
    <%=rankingHtml%>
</p>

<jsp:include page="/jsp/page/page_foot.jsp" />

</td> 
      </tr> 
    </table> 
    </td> 
    
</tr>

</table>


