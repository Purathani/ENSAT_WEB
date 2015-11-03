<%@ page language="java" import="java.util.*,java.sql.*,java.text.Format,java.text.SimpleDateFormat" pageEncoding="ISO-8859-1"%>

<jsp:useBean id='user' class='user.UserBean'  scope='session'/>
<jsp:useBean id='usercheck' class='security.UserCheck'  scope='session'/>
<jsp:useBean id='connect' class='ConnectBean.ConnectBean' scope='session'/>

<%
ServletContext context = getServletContext() ;  
String versionParam = context.getInitParameter("version");
String dbName = "";
String dbParamStr = "";
if(versionParam.equals("test")){
    dbParamStr = "security_db_name_test";
}else{
    dbParamStr = "security_db_name_prod";
}
dbName = context.getInitParameter(dbParamStr);

String host = context.getInitParameter("server_name");
String dbUsername = context.getInitParameter("username");
String dbPassword = context.getInitParameter("password");

String username = user.getUsername();

Connection secConn = connect.getSecConnection();

//Check user details here and redirect to login page if not there (i.e. just opened page without logging into a session)
int userCount = usercheck.checkUserDetails(username, dbName,host,dbUsername,dbPassword,secConn);

%>

<%
if(userCount!=1){    
%>
<jsp:forward page="/jsp/login.jsp"/>
<%
}
%>
