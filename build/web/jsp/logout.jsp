<%@ page language="java" import="java.util.*,java.sql.*,java.text.Format,java.text.SimpleDateFormat,org.apache.log4j.*" pageEncoding="ISO-8859-1"%>

<jsp:useBean id='user' class='user.UserBean'  scope='session'/>
<jsp:useBean id='connect' class='ConnectBean.ConnectBean'  scope='session'/>
<jsp:useBean id='security' class='security.Authz'  scope='session'/>

<%
    ServletContext context = this.getServletContext();
    String username = user.getUsername();

    //Clear the UserBean
    user.setUsername("");
    user.setForename("");
    user.setSurname("");
    user.setRole("");
    user.setCountry("");
    user.setCenter("");
    user.setSessionLogin("");
    user.setSearchFilter("");

    //Destroy the current connection(s)
    Connection connection = connect.getConnection();
    Connection paramConnection = connect.getParamConnection();
    Connection secConnection = connect.getSecConnection();
    Connection ccConnection = connect.getCcConnection();
    
    if(connection != null){
        connection.close();
    }
    if(paramConnection != null){
        paramConnection.close();
    }
    if(secConnection != null){
        secConnection.close();
    }
    if(ccConnection != null){
        ccConnection.close();
    }
    
    security.setRecordEditable(false);
    
    //Logging configuration
    String log4jConfigFile = context.getInitParameter("log4j_property_file");
    Logger logger = Logger.getLogger("rootLogger");
    logger.setLevel(Level.DEBUG);
    PropertyConfigurator.configure(log4jConfigFile);

    logger.debug(" === User '" + username + "' logged out through logout.jsp === ");
    
    session.invalidate();

%>

<!-- Forward back to login page -->
<jsp:forward page="/jsp/login.jsp?logout=1"/>
