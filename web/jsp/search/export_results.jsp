<%@ page language="java" import="java.util.*,java.sql.*,java.io.*,java.text.Format,java.text.SimpleDateFormat,org.apache.log4j.*" pageEncoding="ISO-8859-1"%>

<jsp:include page="/jsp/page/check_credentials.jsp" />
<jsp:include page="/jsp/page/check_input.jsp" />
<jsp:include page="/jsp/page/page_head.jsp" />

<td colspan="2" width="81%" align="left" valign="top" background="/images/possbk.jpg"><table width="1200" border="0" cellspacing="0" cellpadding="10"> 
      <tr> 
        <td width="15" align="left" valign="top">&nbsp;</td> 
        <td align="left" valign="top"><!-- #BeginEditable "MainText" --> 

<jsp:include page="/jsp/page/page_nav.jsp" />

<jsp:useBean id='searchQuery' class='search.SearchUtilities'  scope='session'/>
<jsp:useBean id='user' class='user.UserBean'  scope='session'/>
<jsp:useBean id='searchexport' class='search.SearchExport'  scope='session'/>

<%
ServletContext context = getServletContext();
String EXPORT_ROOT = context.getInitParameter("export_storage_root");
String username = user.getUsername();
String userCountry = user.getCountry();

//Logging configuration
String log4jConfigFile = context.getInitParameter("log4j_property_file");
Logger logger = Logger.getLogger("rootLogger");
logger.setLevel(Level.DEBUG);
PropertyConfigurator.configure(log4jConfigFile);

logger.debug("('" + username + "') - export_results.jsp");


%>
<h2>ENSAT Export Results</h2>

<%
//List the available files based on previous exports
String fileListHtml = searchexport.getFileListHtml(username, userCountry, EXPORT_ROOT);
%>
<%= fileListHtml %>

<p><hr/></p>

<p><strong>Return to <a href="./jsp/search/display_export.jsp">Export page</a></strong></p>

<p><strong>Return to <a href="./jsp/home.jsp">ENSAT Home</a></strong></p>

<jsp:include page="/jsp/page/page_foot.jsp" />
</td> 
      </tr> 
    </table> 
    </td> 
    
</tr>
</table>

