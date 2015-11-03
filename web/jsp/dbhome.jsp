<%@ page language="java" import="java.util.*,java.sql.*,java.text.Format,java.text.SimpleDateFormat,org.apache.log4j.*" pageEncoding="ISO-8859-1"%>

<jsp:include page="/jsp/page/check_credentials.jsp" />
<jsp:include page="/jsp/page/check_input.jsp" />
<jsp:include page="/jsp/page/page_head.jsp" />

<td colspan="2" width="81%" align="left" valign="top" background="/images/possbk.jpg"><table width="1200" border="0" cellspacing="0" cellpadding="10"> 
      <tr> 
        <td width="15" align="left" valign="top">&nbsp;</td> 
        <td align="left" valign="top"><!-- #BeginEditable "MainText" --> 

<jsp:include page="/jsp/page/page_nav.jsp" />

<jsp:useBean id='user' class='user.UserBean'  scope='session'/>
<jsp:useBean id='connect' class='ConnectBean.ConnectBean'  scope='session'/>
<jsp:useBean id='dblisting' class='summaryinfo.DatabaseListing'  scope='session'/>

<%
//Set up the database connection object(s)
Connection connection = connect.getConnection();
Connection secConn = connect.getSecConnection();

//Retrieve the switch between test and production
ServletContext context = getServletContext() ;  
String versionParam = context.getInitParameter("version");
String mainDb = "";
String dbNameStr = "";
if(versionParam.equals("test")){
    dbNameStr = "db_name_test";
}else{    
    dbNameStr = "db_name_prod";
}
mainDb = context.getInitParameter(dbNameStr);

//Retrieve the parameters to identify the database
String dbn = request.getParameter("dbn");
String dbid = request.getParameter("dbid");

//Retrieve the parameters for the list ordering and paging
String pageNum = request.getParameter("page");
if(pageNum == null){
    pageNum = "1";
}
String ensatIdOrdering = request.getParameter("ensatidorder");
if(ensatIdOrdering == null){
    ensatIdOrdering = "0";
}

//Retrieve user-related parameters
String username = user.getUsername();
String country = user.getCountry();
String userCenter = user.getCenter();
String searchFilter = user.getSearchFilter();
if(searchFilter == null || searchFilter.equals("null") || searchFilter.equals("")){
    searchFilter = "all";
}

//Change the column header number based on the database selection
int extraColumnNum = 0;
if(dbn.equals("ACC")){    
    extraColumnNum = 3;
}else if(dbn.equals("Pheo")){    
    extraColumnNum = 3;
}else if(dbn.equals("NAPACA")){    
    extraColumnNum = 5;
}else if(dbn.equals("APA")){    
    extraColumnNum = 3;
}

//Logging configuration
String log4jConfigFile = context.getInitParameter("log4j_property_file");
Logger logger = Logger.getLogger("rootLogger");
logger.setLevel(Level.DEBUG);
PropertyConfigurator.configure(log4jConfigFile);

logger.debug("('" + username + "') - dbhome.jsp [" + dbn + "]");

String dialogTitle = "Matching patient criteria";
String extraText = "<ul><li>Sex</li><li>Year of birth</li><li>Date of ENSAT registration</li></ul>";

%>

<div id="dialog1" title="<%=dialogTitle%>">
  <p><%=extraText%></p>
</div>


<h2><%=dbn%> Home</h2>

<p><a href="./jsp/create/create_view.jsp?dbid=<%=dbid%>&dbn=<%=dbn%>">Create New Record</a></p>

<%

//Compile the patient list into a resultset (to pass on in the subsequent methods)
ResultSet patientList = dblisting.compilePatientList(connection, searchFilter, ensatIdOrdering, dbn, userCenter);

//Get the number of records that will be displayed in the list
int rowCount = dblisting.getRowCount(searchFilter, ensatIdOrdering, dbn, userCenter, patientList);

//Now construct the HTML that will be used for paging
String pagingHtml = "";
if(rowCount != 0){
    pagingHtml = dblisting.getPagingHtml(rowCount, pageNum, searchFilter, dbn, dbid, ensatIdOrdering);
}

%>
<%= pagingHtml %>

<table border="1px" cellpadding="5">
	<thead>
<%
String listingHeaderHtml = dblisting.getListingHeaderHtml(extraColumnNum,dbn,dbid,pageNum);
%>
<%= listingHeaderHtml %>
	</thead>
        
<tbody>
<%
//Now construct the record contents of the table
String patientHtml = dblisting.getTableHtml(context, secConn, connection, pageNum, dbn, dbid, mainDb, username, country, patientList);
%>
<%= patientHtml %>

</tbody>
</table>

<%= pagingHtml %>

<jsp:include page="/jsp/page/page_foot.jsp" />
</td> 
      </tr> 
    </table> 
    </td> 
    
</tr>
</table>