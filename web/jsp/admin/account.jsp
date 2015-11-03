<%@ page language="java" import="java.util.*,java.sql.*,org.apache.log4j.*" pageEncoding="ISO-8859-1"%>

<jsp:include page="/jsp/page/check_credentials.jsp" />
<jsp:include page="/jsp/page/check_input.jsp" />
<jsp:include page="/jsp/page/page_head.jsp" />

<td colspan="2" width="81%" align="left" valign="top" background="/images/possbk.jpg"><table width="1200" border="0" cellspacing="0" cellpadding="10"> 
      <tr> 
        <td width="15" align="left" valign="top">&nbsp;</td> 
        <td align="left" valign="top"><!-- #BeginEditable "MainText" --> 

<jsp:useBean id='user' class='user.UserBean'  scope='session'/>
<jsp:useBean id='security_mgmt' class='security.UserMgmt'  scope='session'/>

<jsp:include page="/jsp/page/page_nav.jsp" />

<%

ServletContext context = getServletContext() ;  
String versionParam = context.getInitParameter("version");
String dbName = "";
if(versionParam.equals("test")){
    dbName = "ensat_security_test";        
}else{
    dbName = "ensat_security";        
}

String host = context.getInitParameter("server_name");
String dbUsername = context.getInitParameter("username");
String dbPassword = context.getInitParameter("password");

String username = user.getUsername();
boolean isSuperUser = user.getIsSuperUser();

String forename = "";
String surname = "";
String country = "";
String email = "";
String institution = "";

        forename = user.getForename();
        surname = user.getSurname();        
        country = user.getCountry();
        
        String[] extraInfo = security_mgmt.getExtraUserInfo(username, dbName, host, dbUsername, dbPassword);               
        email = extraInfo[0];
        institution = extraInfo[1];
        
//Logging configuration
String log4jConfigFile = context.getInitParameter("log4j_property_file");
Logger logger = Logger.getLogger("rootLogger");
logger.setLevel(Level.DEBUG);
PropertyConfigurator.configure(log4jConfigFile);
logger.debug("('" + username + "') - account.jsp");

%>



<h2>Account Details</h2>

	<table border="1px" cellpadding="5">
		
                <tr><th>User Name</th><td><%= username %></td><td></td></tr>
                <tr><th>Password</th><td>*******</td><td><a href="./jsp/admin/editdetails.jsp?mod=1">Edit</a></td></tr>
				<tr><th>Full Name</th><td><%= forename %> <%= surname %></td><td><a href="./jsp/admin/editdetails.jsp?mod=2">Edit</a></td></tr>
				<%--<tr><th>Email</th><td><%= email %></td><td><a href="./jsp/admin/editdetails.jsp?mod=3">Edit</a></td></tr>--%>
				<tr><th>Organization</th><td><%= institution %></td><td><a href="./jsp/admin/editdetails.jsp?mod=4">Edit</a></td></tr>
				<tr><th>Country</th><td><%= country %></td><td></td></tr>
                                
                                <%
                                if(username.equals("astell@unimelb.edu.au")){
                                %>
                                <form action="./jsp/admin/changeuser.jsp" method="POST">
                                <tr><th>Set Account</th>
                                    <td>
                                        <select name="user_select">
                                            <option value=''>[Select...]</option>
                                <%
                                    String outputHtml = security_mgmt.superUserList(dbName,host,dbUsername,dbPassword);
                                %>                      
                                <%= outputHtml %>
                                        </select>
                                    </td>
                                    <td><input type="submit" name="set_account" value="Select Username"/></td></tr>
                                </form>
                                <%
                                }
                                %>
                                
                                <%
                                if(isSuperUser){
                                %>
                                <form action="./jsp/admin/changeuser.jsp" method="POST">
                                    <tr><th>Return to "astell@unimelb.edu.au"</th><td><input type="hidden" name="reset" value="1"/></td><td><input type="submit" name="set_account" value="Reset Username"/></td></tr>
                                </form>
                                <%
                                }
                                %>
	</table>
<jsp:include page="/jsp/page/page_foot.jsp" />
</td> 
      </tr> 
    </table> 
    </td> 
    
</tr>
</table>


