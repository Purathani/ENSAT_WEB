<%@ page language="java" import="java.util.*,java.sql.*,org.apache.log4j.*" pageEncoding="ISO-8859-1"%>

<jsp:include page="/jsp/page/check_credentials.jsp" />

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

String password1 = request.getParameter("password1");
String surname = request.getParameter("surname");
String forename = request.getParameter("forename");
//String email = request.getParameter("email");
String institution = request.getParameter("institution");

String updateOption = "";
String updateValue = "";
if(password1 != null){
    updateOption = "password";
    updateValue = password1;
}else if(surname != null || forename != null){
    updateOption = "name";
    updateValue = forename + "-" + surname;
}/*else if(email != null){
    updateOption = "email";
    updateValue = email;
}*/else if(institution != null){
    updateOption = "institution";
    updateValue = institution;
}

//Logging configuration
String log4jConfigFile = context.getInitParameter("log4j_property_file");
Logger logger = Logger.getLogger("rootLogger");
logger.setLevel(Level.DEBUG);
PropertyConfigurator.configure(log4jConfigFile);

logger.debug("('" + username + "') - updatedetails.jsp");

security_mgmt.updateDetails(username, dbName, updateOption, updateValue, host, dbUsername, dbPassword);

%>

<jsp:forward page="/jsp/admin/accountupdated.jsp"/>
