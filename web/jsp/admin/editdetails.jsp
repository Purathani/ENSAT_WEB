<%@ page language="java" import="java.util.*,java.sql.*,org.apache.log4j.*" pageEncoding="ISO-8859-1"%>

<jsp:include page="/jsp/page/check_credentials.jsp" />
<jsp:include page="/jsp/page/check_input.jsp" />
<jsp:include page="/jsp/page/page_head.jsp" />

<td colspan="2" width="81%" align="left" valign="top" background="/images/possbk.jpg"><table width="1200" border="0" cellspacing="0" cellpadding="10"> 
      <tr> 
        <td width="15" align="left" valign="top">&nbsp;</td> 
        <td align="left" valign="top"><!-- #BeginEditable "MainText" --> 

<jsp:include page="/jsp/page/page_nav.jsp" />

<jsp:useBean id='user' class='user.UserBean'  scope='session'/>
<jsp:useBean id='security_mgmt' class='security.UserMgmt'  scope='session'/>

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
String mod = request.getParameter("mod");
if(mod == null){
    mod = "";
}

String forename = "";
String surname = "";
String country = "";
String email = "";
String institution = "";
String center = "";

        forename = user.getForename();
        surname = user.getSurname();
        
        String[] extraInfo = security_mgmt.getExtraUserInfo(username, dbName, host, dbUsername, dbPassword);               
        email = extraInfo[0];
        institution = extraInfo[1];

        country = user.getCountry();
        center = user.getCenter();
        
//log4j
//Logging configuration
String log4jConfigFile = context.getInitParameter("log4j_property_file");
Logger logger = Logger.getLogger("rootLogger");
logger.setLevel(Level.DEBUG);
PropertyConfigurator.configure(log4jConfigFile);
logger.debug("('" + username + "') - editdetails.jsp");
        
        
        
%>



<h2>Edit Account Details (<%= forename%> <%= surname %> - <%= country %>, <%= center %>)</h2>

<form action="./jsp/admin/detailsconfirm.jsp" method="POST">
    <input type="hidden" name="mod" value="<%= mod %>"/>
	<table border="1px" cellpadding="5">

<%
if(mod.equals("1")){
    %>
                <tr><td></td><th>Current</th><th>New</th>
                <tr><td>Password</td><td>*******</td><td>Enter new password then confirm:<br/><input type="password" size="30" name="password1" onfocus="inform=true;" onblur="inform=false;"/><br/><input type="password" size="30" name="password2" onfocus="inform=true;" onblur="inform=false;"/></td></tr>
                <tr><td colspan="2"><input type="submit" name="accountedit" value="Update"/></td></tr>
                </table>
                </form>
	<%
    }else if(mod.equals("2")){
    %>
    <tr><td></td><th>Current</th><th>New</th>
    <tr><td>Full Name</td><td><%= forename %> <%= surname %></td><td>Forename: <input type="text" size="30" name="forename" onfocus="inform=true;" onblur="inform=false;"/><br/>Surname: <input type="text" size="30" name="surname" onfocus="inform=true;" onblur="inform=false;"/></td></tr>
    <tr><td colspan="2"><input type="submit" name="accountedit" value="Update"/></td></tr>
    </table>
    </form>
<%--	<%
    }else if(mod.equals("3")){
    %>
    <!--<tr><td></td><th>Current</th><th>New</th>
    <tr><td>Email</td><td><%= email %></td><td><input type="text" size="30" name="email" onfocus="inform=true;" onblur="inform=false;"/></td></tr>
    <tr><td colspan="2"><input type="submit" name="accountedit" value="Update"/></td></tr>
	</table>
    </form>--%>
    <%
    }else if(mod.equals("4")){
    %>
    <tr><td></td><th>Current</th><th>New</th>
    <tr><td>Organization</td><td><%= institution %></td><td><input type="text" size="30" name="institution" onfocus="inform=true;" onblur="inform=false;"/></td></tr>
    <tr><td colspan="2"><input type="submit" name="accountedit" value="Update"/></td></tr>
    </table>
    </form>
	<%
    }else{
    %>
    <tr><td>No details selected</td></tr>
    </table>
    </form>
    
    <form action="./jsp/admin/account.jsp" method="POST">
    <input type="submit" name="accountedit" value="Back"/>
    </form>
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


