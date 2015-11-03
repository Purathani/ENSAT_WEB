<%@ page language="java" import="java.util.*,org.apache.log4j.*" pageEncoding="ISO-8859-1"%>

<jsp:include page="/jsp/page/check_credentials.jsp" />
<%--<jsp:include page="/jsp/page/check_input.jsp" />--%>
<jsp:include page="/jsp/page/page_head.jsp" />

<td colspan="2" width="81%" align="left" valign="top" background="/images/possbk.jpg"><table width="1200" border="0" cellspacing="0" cellpadding="10"> 
      <tr> 
        <td width="15" align="left" valign="top">&nbsp;</td> 
        <td align="left" valign="top"><!-- #BeginEditable "MainText" --> 

<jsp:include page="/jsp/page/page_nav.jsp" />

<jsp:useBean id='user' class='user.UserBean'  scope='session'/>

<%
ServletContext context = getServletContext();    
    
String study = request.getParameter("study");

//Logging configuration
String log4jConfigFile = context.getInitParameter("log4j_property_file");
Logger logger = Logger.getLogger("rootLogger");
logger.setLevel(Level.DEBUG);
PropertyConfigurator.configure(log4jConfigFile);

String username = user.getUsername();
logger.debug("('" + username + "') - transfer_registry_result.jsp");

%>

<h1>Record has been successfully transferred to <%=study%></h1>

<h3>Return to <a href="./jsp/dbhome.jsp?dbid=3&dbn=NAPACA">NAPACA Home</a></h3>

<jsp:include page="/jsp/page/page_foot.jsp" />
</td> 
      </tr> 
    </table> 
    </td> 
    
</tr>
</table>

