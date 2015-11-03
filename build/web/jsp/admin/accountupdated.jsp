<%@ page language="java" import="java.util.*,org.apache.log4j.*" pageEncoding="ISO-8859-1"%>

<jsp:include page="/jsp/page/check_credentials.jsp" />
<jsp:include page="/jsp/page/page_head.jsp" />

<td colspan="2" width="81%" align="left" valign="top" background="/images/possbk.jpg"><table width="1200" border="0" cellspacing="0" cellpadding="10"> 
      <tr> 
        <td width="15" align="left" valign="top">&nbsp;</td> 
        <td align="left" valign="top"><!-- #BeginEditable "MainText" --> 

<jsp:include page="/jsp/page/page_nav.jsp" />

<jsp:useBean id='user' class='user.UserBean'  scope='session'/>

<%
//log4j
Logger logger = Logger.getLogger("rootLogger");
logger.setLevel(Level.DEBUG);
PropertyConfigurator.configure("/root/logs/log4j_ensat.properties");
String username = user.getUsername();
logger.debug("('" + username + "') - accountupdated.jsp");
%>



<h2>Account successfully updated.</h2>

<h3>Return to <a href="./jsp/admin/account.jsp">Account details</a></h3>

<h3>Return to <a href="./jsp/home.jsp">ENSAT home</a></h3>

<jsp:include page="/jsp/page/page_foot.jsp" />
</td> 
      </tr> 
    </table> 
    </td> 
    
</tr>
</table>

