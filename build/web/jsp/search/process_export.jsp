<%@ page language="java" import="java.util.*,java.sql.*,java.io.*,java.text.Format,java.text.SimpleDateFormat,search.ExportThread,org.apache.log4j.*" pageEncoding="ISO-8859-1"%>

<jsp:useBean id='searchQuery' class='search.SearchUtilities'  scope='session'/>
<jsp:useBean id='studyauthz' class='security.StudyAuthz'  scope='session'/>
<jsp:useBean id='user' class='user.UserBean'  scope='session'/>
<jsp:useBean id='connect' class='ConnectBean.ConnectBean'  scope='session'/>

<%

Connection connection = connect.getConnection();
ServletContext context = getServletContext();  
String contextPath = request.getContextPath();
String EXPORT_ROOT = context.getInitParameter("export_storage_root");
String username = user.getUsername();
String userCenter = user.getCenter();
String userCountry = user.getCountry();

String filepath = searchQuery.getFilepath(userCountry);
Format formatter = new SimpleDateFormat("dd MM yyyy HH:mm:ss");
java.util.Date date = new java.util.Date(session.getLastAccessedTime());
String formDate = formatter.format(date);
String filename = searchQuery.getFilename(username, formDate);

//Logging configuration
String log4jConfigFile = context.getInitParameter("log4j_property_file");
Logger logger = Logger.getLogger("rootLogger");
logger.setLevel(Level.DEBUG);
PropertyConfigurator.configure(log4jConfigFile);
logger.debug("('" + username + "') - process_export.jsp");

//Set up the meta-variables that are required for the processing
searchQuery.setExportLocation(EXPORT_ROOT, contextPath, filepath, filename);
searchQuery.setUserDetails(username,userCenter);
searchQuery.setConnection(connection);

//Analyse the form here and pick out which patients have been added to the list and add to the searchQuery session bean
String[] patientIDListSelected = request.getParameterValues("patient_selection");
searchQuery.setExportUserList(patientIDListSelected);

search.ExportThreadPoolExecutor etpe = (search.ExportThreadPoolExecutor) context.getAttribute("export_thread_pool");
if(etpe != null){
    ExportThread et = new ExportThread(searchQuery, username,connection);
    String threadName = "" + filename;
    Thread exportThread = new Thread(et,threadName);
    logger.debug("Export thread started (" + exportThread.getName() + ")...");
    etpe.runTask(exportThread);
}

//Then redirect to waiting page
String redirectionPage = "./export_wait.jsp";
response.sendRedirect(redirectionPage);

%>
