<%@ page language="java" import="java.util.*,java.sql.*,search.SearchResult,org.apache.log4j.*" pageEncoding="ISO-8859-1"%>

<jsp:useBean id='searchQuery' class='search.SearchUtilities'  scope='session'/>
<jsp:useBean id='search' class='search.Search'  scope='session'/>
<jsp:useBean id='searchsub' class='search.SearchSub'  scope='session'/>
<jsp:useBean id='user' class='user.UserBean'  scope='session'/>
<jsp:useBean id='connect' class='ConnectBean.ConnectBean'  scope='session'/>

<%
ServletContext context = this.getServletContext();
Connection paramConn = connect.getParamConnection();
Connection conn = connect.getConnection();

//Logging configuration
String log4jConfigFile = context.getInitParameter("log4j_property_file");
Logger logger = Logger.getLogger("rootLogger");
logger.setLevel(Level.DEBUG);
PropertyConfigurator.configure(log4jConfigFile);

//String username = user.getUsername();
String dbn = request.getParameter("dbn");

//Need to grab the conditions and the parameters to view
Vector<String> conditions = search.getConditions(request,paramConn);
Vector<String> viewParams = search.getViewParameters(request);

//Use these to compile the SQL that will perform the search
search.setConditions(conditions);
search.setViewParameters(viewParams);
String searchSql = search.compileSearchSQL(conditions, viewParams, dbn);
search.setSearchQuery(searchSql);

logger.debug("searchSql: " + searchSql);
System.out.println("(DEBUG) searchSql: " + searchSql);

//Run the search using the compiled SQL and pass back the resultset
ResultSet searchResults = search.runSearchQuery(searchSql, conn);
search.setSearchResults(searchResults);

//Forward to the search_view page, using the searchResults object to trigger processing within search_result
String searchViewPage = "./search_view.jsp?dbn=" + dbn + "&page=1";
response.sendRedirect(searchViewPage);

%>
