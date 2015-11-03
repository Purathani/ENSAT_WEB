<%@ page language="java" import="java.util.*" pageEncoding="ISO-8859-1"%>

<jsp:include page="/jsp/page/index_head.jsp" />

<td colspan="2" width="81%" align="left" valign="top" background="/images/possbk.jpg"><table width="1200" border="0" cellspacing="0" cellpadding="10"> 
      <tr> 
        <td width="15" align="left" valign="top">&nbsp;</td> 
        <td align="left" valign="top"><!-- #BeginEditable "MainText" --> 

<%--<jsp:include page="/jsp/page/index_nav.jsp" />--%>

<%
String incorrectLogin = request.getParameter("incorrectlogin");
String sessionExpired = request.getParameter("sessionexpired");
String loggedOut = request.getParameter("logout");

boolean showFailMessage = false;
String failMessage = "Login incorrect - please try again.";
/*if(incorrectLogin == null){
    incorrectLogin = "";
}
if(incorrectLogin.equals("")){
    incorrectLogin = "1";
}*/
boolean showExpiredMessage = false;
boolean showLoggedOutMessage = false;
if(incorrectLogin != null){        
    showFailMessage = true;
    if(incorrectLogin.equals("2")){
        //This clause means the account has been deactivated
        failMessage = "This account has been deactivated. Please contact the <a href='mailto:astell@unimelb.edu.au'>registry administrator</a> to re-activate.";        
    }else if(incorrectLogin.equals("3")){
        //This clause means the ENSAT membership needs to be brought up to date
        failMessage = "Your access to the registry has been denied because your ENSAT membership has expired.<br/>";
        failMessage += "To renew or update your membership, log in to <a target='_blank' href='http://ensat.org/Sys/Profile'>your profile</a> with your email and password and follow suggested actions.<br/>";
        failMessage += "For technical queries please contact the <a href='mailto:astell@unimelb.edu.au'>registry administrator</a>.";
    }
}else if(sessionExpired != null){    
    showExpiredMessage = true;
}else if(loggedOut != null){
    showLoggedOutMessage = true;
}

%>

<p>&nbsp;</p>
<p>&nbsp;</p>
<p>&nbsp;</p>

<p>
<div align="center">
    
    <%
    if(showFailMessage){
    %>
    <strong><%=failMessage%></strong>
    <%
    }else if(showExpiredMessage){
    %>
    <strong>Session expired - please login again.</strong>
    <%
    }else if(showLoggedOutMessage){
    %>
    <strong>Thank you - you have successfully logged out.</strong>
    <%
    }else{
    %>
    <strong>Please log in:</strong><br/>
    <div id="minimuminputflag">
        Note: your username is now your registered email address. If you are unsure what this is please contact the <a href='mailto:astell@unimelb.edu.au'>registry administrator</a>.
    </div>
    <%
    }
    %>
    
    <br/>
    <br/>

<!-- Detect whether the user has Javascript enabled or not -->
<noscript>
<div id="minimuminputflag">
    This site requires Javascript to be enabled in order to run the full feature-set. Please enable this option in your browser.
</div>
</noscript>
    
    <br/>
    <br/>
<form action="./jsp/checklogin.jsp" method="POST">
    <table>
    <tr><td>Username</td><td><input type="text" name="uname" size="30"/></td></tr>
    <tr><td>Password</td><td><input type="password" name="pword" size="30"/></td></tr>    
    <tr><td colspan="2"><div align="center"><input type="submit" value="Login"/></div></td></tr>
    </table>
</form>
</div>
</p>

<p>&nbsp;</p>
<p>&nbsp;</p>
<p>&nbsp;</p>



    <jsp:include page="/jsp/page/page_foot.jsp" />
</td> 
      </tr> 
    </table> 
    </td> 
    <%--<tr><td><jsp:include page="/jsp/page/page_foot.jsp" /></td></tr>--%>
</tr>
</table>







