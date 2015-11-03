<%@ page language="java" import="java.util.*,java.sql.*,org.apache.log4j.*" pageEncoding="ISO-8859-1"%>

<jsp:include page="/jsp/page/check_credentials.jsp" />
<%--<jsp:include page="/jsp/page/check_input.jsp" />--%>
<jsp:include page="/jsp/page/page_head.jsp" />

<td colspan="2" width="81%" align="left" valign="top" background="/images/possbk.jpg"><table width="1200" border="0" cellspacing="0" cellpadding="10"> 
      <tr> 
        <td width="15" align="left" valign="top">&nbsp;</td> 
        <td align="left" valign="top"><!-- #BeginEditable "MainText" --> 

<jsp:include page="/jsp/page/page_nav.jsp" />

<jsp:useBean id='user' class='user.UserBean'  scope='session'/>
<jsp:useBean id='utility' class='security.Utilities'  scope='session'/>

<%
ServletContext context = this.getServletContext();
String mod = request.getParameter("mod");

boolean passwordChange = false;
boolean passwordValid = true;
boolean nameChange = false;
boolean emailChange = false;
boolean institutionChange = false;
boolean otherValid = true;

String password1 = request.getParameter("password1");
String password2 = request.getParameter("password2");
String surname = request.getParameter("surname");
String forename = request.getParameter("forename");
//String email = request.getParameter("email");
String institution = request.getParameter("institution");

if(password1 != null && !password1.equals("") && password2 != null && !password2.equals("")){
    passwordChange = true;
    passwordValid = utility.passwordCheck(password1, password2);
}
if(surname != null && !surname.equals("") && forename != null && !forename.equals("")){
    nameChange = true;
    otherValid = utility.nameCheck(surname,forename);
}
/*if(email != null && !email.equals("")){
    emailChange = true;
    otherValid = utility.emailCheck(email);
}*/
if(institution != null && !institution.equals("")){
    institutionChange = true;
    otherValid = utility.institutionCheck(institution);    
}

//Logging configuration
String log4jConfigFile = context.getInitParameter("log4j_property_file");
Logger logger = Logger.getLogger("rootLogger");
logger.setLevel(Level.DEBUG);
PropertyConfigurator.configure(log4jConfigFile);

String username = user.getUsername();
logger.debug("('" + username + "') - detailsconfirm.jsp");


%>

<h2>Confirm New Account Details</h2>

<form action="./jsp/admin/updatedetails.jsp" method="POST">
	<table border="1px" cellpadding="5">
    <%
    if(passwordChange){
        if(passwordValid){
        %>
    <tr><th>Password</th><td>*******</td><input type="hidden" name="password1" value="<%= password1 %>"/></tr>
    <tr><td colspan="2"><input type="submit" name="accountedit" value="Update"/></td></tr>
    </table>
    </form>
    <%
        }else{
        %>
        <tr><td>Password is not valid. It must be at least eight characters long, contain one number, one upper case and one lower case character.</td></tr>
        </table>
        </form>
        <%
        }
    }else if(nameChange){
        if(otherValid){
        %>
    <tr><th>Full name</th><td><%= forename %> <%= surname %></td><input type="hidden" name="forename" value="<%= forename %>"/><input type="hidden" name="surname" value="<%= surname %>"/></tr>
    <tr><td colspan="2"><input type="submit" name="accountedit" value="Update"/></td></tr>
    </table>
    </form>
        <%
        }else{
        %>
        <tr><td>Name is not valid. It must be less than 100 characters long.</td></tr>
    </table>
    </form>
    <%
    }
    
        %>
        
        <%--<%
    }else if(emailChange){
        if(otherValid){
        %>
    <tr><th>Email</th><td><%= email %></td><input type="hidden" name="email" value="<%= email %>"/></tr>
    <tr><td colspan="2"><input type="submit" name="accountedit" value="Update"/></td></tr>
        </table>
    </form>
        <%
        }else{
        %>
        <tr><td>Email is not valid. It must be less than 100 characters long.</td></tr>
    </table>
    </form>--%>
    <%    
    }else if(institutionChange){
          if(otherValid){
        %>
    <tr><th>Institution</th><td><%= institution %></td><input type="hidden" name="institution" value="<%= institution %>"/></tr>
    <tr><td colspan="2"><input type="submit" name="accountedit" value="Update"/></td></tr>
        </table>
    </form>
        <%
        }else{
        %>
        <tr><td>Institution is not valid. It must be less than 100 characters long.</td></tr>
    </table>
    </form>
    <%
    }
    }else{
    %>

    <tr><td>Please fill in all boxes to update details</td></tr>
    </table>
    </form>
    <%
    }
    %>

<form action="./jsp/admin/editdetails.jsp?mod=<%= mod %>" method="POST">
    <input type="submit" name="accountback" value="Back"/>
</form>

    <jsp:include page="/jsp/page/page_foot.jsp" />
</td> 
      </tr> 
    </table> 
    </td> 
    
</tr>
</table>


