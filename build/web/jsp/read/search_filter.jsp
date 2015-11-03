<%@ page language="java" import="java.util.*,java.sql.*,org.apache.log4j.*" pageEncoding="ISO-8859-1"%>

<jsp:useBean id='user' class='user.UserBean'  scope='session'/>
<jsp:useBean id='connect' class='ConnectBean.ConnectBean'  scope='session'/>

<%
ServletContext context = this.getServletContext();
    
//Logging configuration
String log4jConfigFile = context.getInitParameter("log4j_property_file");
Logger logger = Logger.getLogger("rootLogger");
logger.setLevel(Level.DEBUG);
PropertyConfigurator.configure(log4jConfigFile);
    
String dbn = request.getParameter("dbn");
String dbid = request.getParameter("dbid");
String searchFilter = request.getParameter("search_filter");
String userCenter = user.getCenter();
logger.debug("Filtering list on " + dbn + " by '" + searchFilter + "' with center ID: " + userCenter + "");
user.setSearchFilter(searchFilter);
%>

<jsp:forward page="/jsp/dbhome.jsp">
    <jsp:param name="dbn" value="<%=dbn%>"/>
    <jsp:param name="dbid" value="<%=dbid%>"/>
    <jsp:param name="page" value="1"/>
</jsp:forward>
