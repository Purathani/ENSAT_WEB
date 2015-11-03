<%@ page language="java" import="java.util.*,java.sql.*,org.apache.log4j.*" pageEncoding="ISO-8859-1"%>

<jsp:useBean id='connect' class='ConnectBean.ConnectBean'  scope='session'/>
<jsp:useBean id='transfer' class='update_main.Transfer' scope='session'/>
<jsp:useBean id='user' class='user.UserBean'  scope='session'/>

<%
ServletContext context = getServletContext();

//HAVE ANOTHER FUNCTION HERE TO CLEAR THE MAPPING OBJECT HERE?

String dbn = request.getParameter("dbn");
String dbid = request.getParameter("dbid");
String pid = request.getParameter("pid");
String centerid = request.getParameter("centerid");
String study = request.getParameter("study");

//Logging configuration
String log4jConfigFile = context.getInitParameter("log4j_property_file");
Logger logger = Logger.getLogger("rootLogger");
logger.setLevel(Level.DEBUG);
PropertyConfigurator.configure(log4jConfigFile);

String username = user.getUsername();
logger.debug("('" + username + "') - transfer_process.jsp");

Connection connection = connect.getConnection();
//NEED TO HAVE A THINK ABOUT HOW THIS WILL BE ADDED
transfer.addStudyInclusionParameters(connection, pid,centerid,request);
transfer.addTableValues(connection, pid, centerid, request);

boolean transferSuccess = transfer.updateStudyDatabase(study,centerid,pid);

//Clear out the mapping object
transfer.clearMapping();
%>

<jsp:forward page="/jsp/read/transfer_result.jsp">
    <jsp:param name="dbid" value="<%=dbid%>"/>
    <jsp:param name="dbn" value="<%=dbn%>"/>    
</jsp:forward>