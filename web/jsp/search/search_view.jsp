<%@ page language="java" import="java.util.*,org.apache.log4j.*,java.sql.*" pageEncoding="ISO-8859-1"%>

<jsp:include page="/jsp/page/check_credentials.jsp" />
<jsp:include page="/jsp/page/check_input.jsp" />
<jsp:include page="/jsp/page/page_head.jsp" />

<td colspan="2" width="81%" align="left" valign="top" background="/images/possbk.jpg"><table width="1200" border="0" cellspacing="0" cellpadding="10"> 
      <tr> 
        <td width="15" align="left" valign="top">&nbsp;</td> 
        <td align="left" valign="top"><!-- #BeginEditable "MainText" --> 

<jsp:include page="/jsp/page/page_nav.jsp" />

<jsp:useBean id='searchQuery' class='search.SearchUtilities'  scope='session'/>
<jsp:useBean id='search' class='search.Search'  scope='session'/>
<jsp:useBean id='user' class='user.UserBean'  scope='session'/>
<jsp:useBean id='connect' class='ConnectBean.ConnectBean'  scope='session'/>

<%
ServletContext context = this.getServletContext();
Connection paramConn = connect.getParamConnection();
Connection conn = connect.getConnection();
Connection ccConn = connect.getCcConnection();
    
String username = user.getUsername();
search.setUsername(username);

String lineColour = search.getLineColour();
ResultSet searchResults = search.getSearchResults();

//Clear the search bean ahead of this search
//searchQuery.setSearchQuerySql("");
//searchQuery.setParameters(new ArrayList<String>());
//searchQuery.setConditions(new ArrayList<String>());

String dbn = request.getParameter("dbn");
Vector<Vector> searchParameters = search.getSearchParameters(request, paramConn);
Vector<Vector> searchMenus = search.getMenus(searchParameters, paramConn);
Vector<String> paramTables = search.getSearchParameterTables(searchParameters,dbn);

//Logging configuration
String log4jConfigFile = context.getInitParameter("log4j_property_file");
Logger logger = Logger.getLogger("rootLogger");
logger.setLevel(Level.DEBUG);
PropertyConfigurator.configure(log4jConfigFile);
logger.debug("('" + username + "') - search_view.jsp");

%>

<h1>ENSAT Search (<%=dbn%>)</h1>

<%

if(username.equals("astell@unimelb.edu.au")){
    
%>
<p><a href="./jsp/search/ltphpgl.jsp">LTPHPGL Study Numbers</a></p>
<p><a href="./jsp/search/bio_test.jsp">Biomaterial Numbers</a></p>
<p><a href="./jsp/search/french_check.jsp">French Check Numbers</a></p>
<%
}
%>


<%--<%
if(username.equals("anthony") || username.equals("felix") || username.equals("martin")
        || username.equals("richard") || username.equals("irina") || username.equals("stephang")
        || username.equals("michaela") || username.equals("margarita") || username.equals("matthias")
         || username.equals("laurence") || username.equals("segolene") || username.equals("rossella")){
%>--%>

<p><div id="search"></div></p>

<table width="100%">
    <tr>
        <td valign="top">
<form action="./jsp/search/search_process.jsp?dbn=<%=dbn%>" method="POST" onsubmit="return validateSearchForm(this,'search');">
<table border="1px" width="100%">

<!-- Main canvas table has two panels (L and R) -->
<tr>
<td width="50%" valign="top">

<table cellpadding="10" width="100%">

<tr>
    <td valign="top">

<div align="left">
    
    <h3>Conditions to apply</h3>

    <p>In this section, select the conditions that you wish to apply to your query.
    Continuous types - such as dates and numbers - have an option to put in start and end values.
    If you enter only a start or only an end value, the query will run with only that option as a &gt;/&lt; comparator.</p>
    
    <p><em>(e.g. if you entered a start value of "1970" for year of birth, then the query will return all records with year of birth &gt; 1970)</em></p>
    
    <p>Click on a table to show the input options</p>
    
    <hr/>
    
    <%
    String conditionHtml = search.getSearchConditionHtml(searchParameters, searchMenus, paramTables, lineColour,conn, ccConn);    
       %>
    <%= conditionHtml %>
        
</div>
    </td>
</tr>
</table>

</td>
</tr>
<tr>
<td>
    
<table cellpadding="10" width="100%">
<tr>
    <td>
        <div align="left">
    
    <h3>Parameters to view</h3>
    
    <p>In this section, select the parameters (up to a maximum of 5) that you want to view the output of based on the query that you are running.</p>
    
    <p>Click on a table to show the check-box options</p>
    
    <hr/>
    
    <%
    String parameterHtml = search.getSearchParameterHtml(searchParameters, paramTables, lineColour);    
    %>
    <%= parameterHtml %>

</div>

</td>
</tr>
</table>


</td>
</tr>
</table>
<input type="submit" name="search_records" value="Search Records"/>
</form>

        </td>
        <td width="50%" valign="top">

<!-- R panel -->
<table border="1px" width="100%">
    <tr>
<td width="50%" valign="top">

<table cellpadding="10" width="100%">

<tr>
    <td>
        <%
        if(searchResults != null){
        %>
        <jsp:include page="/jsp/search/search_result.jsp">
            <jsp:param name="dbn" value="<%=dbn%>"></jsp:param>
        </jsp:include>
        <%
         }else{
        %>
        <h3>Results</h3>
        Please run a query. Results will be displayed here.
        <%
         }
        %>
</td>
</tr>
</table>


</td>
<!-- End of R panel -->
</tr>
</table>

        </td>
    </tr>
</table>


<%--<%
}
%>--%>

<jsp:include page="/jsp/page/page_foot.jsp" />
</td> 
      </tr> 
    </table> 
    </td> 
    
</tr>
</table>
