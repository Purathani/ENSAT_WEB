<%@ page language="java" import="java.util.*,java.sql.*,org.apache.log4j.*" pageEncoding="ISO-8859-1"%>

<jsp:useBean id='user' class='user.UserBean'  scope='session'/>
<jsp:useBean id='connect' class='ConnectBean.ConnectBean'  scope='session'/>
<jsp:useBean id='utilities' class='update_main.Utilities' scope='session'/>
<jsp:useBean id='security' class='security.Authz'  scope='session'/>

<% 
ServletContext context = this.getServletContext();    
    
String patientSearch = request.getParameter("patient_search");
if(patientSearch == null){
    patientSearch = "";    
}
patientSearch = patientSearch.trim();

String host = context.getInitParameter("server_name");
String dbUsername = context.getInitParameter("username");
String dbPassword = context.getInitParameter("password");

String dbn = "";
String dbid = "";
String ensatId = "";
String centerId = "";

String username = user.getUsername();
String country = user.getCountry();

//Logging configuration
String log4jConfigFile = context.getInitParameter("log4j_property_file");
Logger logger = Logger.getLogger("rootLogger");
logger.setLevel(Level.DEBUG);
PropertyConfigurator.configure(log4jConfigFile);

logger.debug("('" + username + "') - patient_direct.jsp (patient_search = '" + patientSearch + "')");

Connection connection = connect.getConnection();
Connection secConn = connect.getSecConnection();
Statement statement = connection.createStatement();

boolean idPresent = utilities.getIdPresent(patientSearch);

//Use securityShow to lock down the access based on consent level, recordEditable to indicate read or write access
boolean securityShow = false;
boolean recordEditable = false;

if(idPresent){
    ensatId = utilities.getEnsatId(patientSearch);
    centerId = utilities.getCenterId(patientSearch);
    
    ensatId = ensatId.trim();
    centerId = centerId.trim();
    
    ResultSet idCheck = utilities.getRowCount(ensatId, centerId, statement);    
    int rowCount = utilities.getRowCount(idCheck);
    dbn = utilities.getDbn(idCheck);
    dbid = utilities.getDbid(dbn);
    idPresent = (rowCount == 1);
    
    String uploader = utilities.getUploader(idCheck);
    String consent = utilities.getConsent(idCheck);
    
    //logger.debug("before modifyRecordEditable...");
    
    security.modifyRecordEditable(username, uploader, ensatId, centerId, connection, secConn);    
    recordEditable = security.getRecordEditable();    
    securityShow = security.getSecurityShow(consent, uploader, country,host,dbUsername,dbPassword);
    
    //logger.debug("after modifyRecordEditable...");
}

//logger.debug("outside idPresent clause...");

//Can't include in the clause above as the idPresent boolean is modified within that (using same boolean for multiple checks)
if(idPresent && securityShow){    
    
    //Set centerId to upper case if not already there
     centerId = centerId.toUpperCase();
     
     if(recordEditable){
    %>
<jsp:forward page="/jsp/read/detail.jsp">
    <jsp:param name="dbid" value="<%=dbid%>"/>
    <jsp:param name="dbn" value="<%=dbn%>"/>
    <jsp:param name="pid" value="<%=ensatId%>"/>
    <jsp:param name="centerid" value="<%=centerId%>"/>
</jsp:forward>
    <%
       }else{
          %>
<jsp:forward page="/jsp/read/readonly.jsp">
    <jsp:param name="dbid" value="<%=dbid%>"/>
    <jsp:param name="dbn" value="<%=dbn%>"/>
    <jsp:param name="pid" value="<%=ensatId%>"/>
    <jsp:param name="centerid" value="<%=centerId%>"/>
</jsp:forward>
          <%
       }
}else{
    %>
    <jsp:forward page="/jsp/home.jsp"/>
    <%    
}
%>
