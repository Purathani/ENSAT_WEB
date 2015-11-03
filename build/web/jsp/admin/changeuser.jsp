<%@ page language="java" import="java.util.*,java.sql.*,org.apache.log4j.*" pageEncoding="ISO-8859-1"%>

<jsp:include page="/jsp/page/check_credentials.jsp" />

<jsp:useBean id='user' class='user.UserBean'  scope='session'/>
<jsp:useBean id='security_mgmt' class='security.UserMgmt'  scope='session'/>

<%

ServletContext context = getServletContext() ;  
String versionParam = context.getInitParameter("version");
String dbName = "";
String contextStr = "";
if(versionParam.equals("test")){
    dbName = "ensat_security_test";        
    contextStr = "/test_ensat";
}else{
    dbName = "ensat_security";            
}

String host = context.getInitParameter("server_name");
String dbUsername = context.getInitParameter("username");
String dbPassword = context.getInitParameter("password");


String username = user.getUsername();
boolean isSuperUser = user.getIsSuperUser();

String userselect = request.getParameter("user_select");
String reset = request.getParameter("reset");

if(reset != null){
    userselect = "astell@unimelb.edu.au";
    isSuperUser = false;
}else{
    if(userselect == null || userselect.equals("")){
        userselect = "astell@unimelb.edu.au";
        isSuperUser = false;
    }else{
        isSuperUser = true;
    }       
}

//if(username.equals("anthony")){
    user = security_mgmt.changeUser(userselect,dbName,isSuperUser,user, host, dbUsername, dbPassword);  
//}

//Logging configuration
String log4jConfigFile = context.getInitParameter("log4j_property_file");
Logger logger = Logger.getLogger("rootLogger");
logger.setLevel(Level.DEBUG);
PropertyConfigurator.configure(log4jConfigFile);
String usernamePrint = user.getUsername();
logger.debug("('" + username + " as: "+ usernamePrint + "') - changeuser.jsp");

    
    
response.sendRedirect(contextStr + "/jsp/admin/account.jsp");    
%>
