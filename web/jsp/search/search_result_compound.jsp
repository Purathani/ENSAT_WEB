<%@ page language="java" import="java.util.*,java.sql.*,search.SearchResult,org.apache.log4j.*" pageEncoding="ISO-8859-1"%>

<jsp:include page="/jsp/page/check_credentials.jsp" />
<%--<jsp:include page="/jsp/page/check_input.jsp" />--%>
<jsp:include page="/jsp/page/page_head.jsp" />

<td colspan="2" width="81%" align="left" valign="top" background="/images/possbk.jpg"><table width="1200" border="0" cellspacing="0" cellpadding="10"> 
      <tr> 
        <td width="15" align="left" valign="top">&nbsp;</td> 
        <td align="left" valign="top"><!-- #BeginEditable "MainText" --> 

<jsp:include page="/jsp/page/page_nav.jsp" />

<jsp:useBean id='searchQuery' class='search.SearchUtilities'  scope='session'/>
<jsp:useBean id='search' class='search.Search'  scope='session'/>
<jsp:useBean id='searchsub' class='search.SearchSub'  scope='session'/>
<jsp:useBean id='user' class='user.UserBean'  scope='session'/>
<jsp:useBean id='connect' class='ConnectBean.ConnectBean'  scope='session'/>

<%
ServletContext context = this.getServletContext();

String username = user.getUsername();
String country = user.getCountry();

String dbn = request.getParameter("dbn");
String dbid = request.getParameter("dbid");
String repeatid = request.getParameter("repeat");
if(repeatid == null){
    repeatid = "0";
}
String modality = request.getParameter("modality");
String[] tablenames = {modality};
Vector<Vector> parameters = search.getParameters(tablenames, request, context);

    //Retrieve data parameter information
    String[] subTablenames = new String[1];
    subTablenames[0] = searchsub.getSubTablename(modality,dbn);
    Vector<Vector> subParameters = searchsub.getParameters(subTablenames,request,context);
    

//log4j
Logger logger = Logger.getLogger("rootLogger");
logger.setLevel(Level.DEBUG);
PropertyConfigurator.configure("/home/astell/logs/log4j_ensat.properties");
logger.debug("('" + username + "') - search_result_compound.jsp (modality = " + modality + ")");
    

%>

<h2><%=dbn%> Search Results</h2>

<%
ResultSet rs = null;
SearchResult sr = new SearchResult();

/*List<String> parameterInputs = searchQuery.getParameters();
List<String> conditions = searchQuery.getConditions();
List<String> comparators = searchQuery.getComparators();
List<String> tables = new ArrayList<String>();*/

if(repeatid.equals("0")){
    
    logger.debug("('" + username + "') - Compound search query run for first time...");

    logger.debug("('" + username + "') - SEARCH INPUT FROM ORIGINAL QUERY:");
        List<String> parametersOrig = searchQuery.getParameters();
        List<String> conditionsOrig = searchQuery.getConditions();
        List<String> comparatorsOrig = searchQuery.getComparators();
        List<String> tablesOrig = searchQuery.getTables();

        //Add to the parameters and conditions here (subject to the parameter selections previously)
        sr = searchsub.compileSearchResults(parametersOrig, conditionsOrig, comparatorsOrig, tablesOrig, request, subParameters);

}else{
    
    logger.debug("('" + username + "') - Compound search query is a repeat from previous query...");

    logger.debug("('" + username + "') - SEARCH INPUT FROM PREVIOUS QUERY:");
    //Check for the parameters in queries already stored       
    List<String> parametersOrig = searchQuery.getParameters();
    List<String> conditionsOrig = searchQuery.getConditions();
    List<String> comparatorsOrig = searchQuery.getComparators();
    List<String> tablesOrig = searchQuery.getTables();

    //Add to the parameters and conditions here (subject to the parameter selections previously)
    sr = searchsub.getRepeatSearchResults(parametersOrig, conditionsOrig, comparatorsOrig, tablesOrig, request, subParameters);    

}//End of repeatid clause

String searchQuerySql = search.compileSearchQuery(sr, dbn);
rs = search.runSearchQuery(searchQuerySql,context);
    
int rowCount = search.getRowCount(rs);

//Set the features in the search bean for the next run
searchQuery.setParameters(sr.getParameters());
searchQuery.setConditions(sr.getConditions());
searchQuery.setComparators(sr.getComparators());
searchQuery.setTables(sr.getTables());

%>

<table border="1px" width="100%">

<!-- Main canvas table has two panels (L and R) -->
<tr>
<td width="66%" valign="top">

<!-- L panel -->
<table border="1px" width="100%" cellpadding="5">
<tr>
    <td>
        
<%--<%
List<String> parameterTest = sr.getParameters();
List<String> conditionTest = sr.getConditions();
List<String> comparatorTest = sr.getComparators();
List<String> tableTest = sr.getTables();
%>
parameterTest.size(): <%=parameterTest.size()%><br/>
conditionTest.size(): <%=conditionTest.size()%><br/>
comparatorTest.size(): <%=comparatorTest.size()%><br/>
tableTest.size(): <%=tableTest.size()%><br/>

<%=searchQuerySql%>--%>
        

<form action="./jsp/search/search_result_compound.jsp?dbid=<%=dbid%>&dbn=<%=dbn%>&modality=<%=modality%>&repeat=1" method="POST">

    <%
    String searchHeaderInfo = search.getSearchHeaderInfo(rs, rowCount, sr);    
    %>
    <%= searchHeaderInfo %>
<p><input type="submit" value="Repeat Search" name="searchrepeat"/></p>
</form>

<%--<p><a href="./jsp/search/search_export.jsp?dbid=<%=dbid%>&dbn=<%=dbn%>&exportall=0">Export these results</a></p>
<p><a href="./jsp/search/search_export.jsp?dbid=<%=dbid%>&dbn=<%=dbn%>&exportall=1">Export all your patient data</a></p>--%>
<p><a href="./jsp/search/search_view.jsp?dbid=<%=dbid%>&dbn=<%=dbn%>">Run a new search</a></p>

<table border="1px" cellpadding="5">
<%
String tableHeaderInfo = search.getTableHeaderInfo();
%>
    <%= tableHeaderInfo %>

<%
String tableBodyInfo = search.getTableBodyInfo(rs, dbid, dbn, username, country);
%>
    <%= tableBodyInfo %>
</table>

</td>
</tr>
</table>
<!-- End of L panel -->

</td>
<!-- R panel -->
<td width="34%" valign="top">

<table cellpadding="10">

<tr>
<td>
<div align="left">
    <h1>Associated Record Search</h1>
    <%
    Vector<Vector> subTables = search.compileSubTableList(dbn);        
    %>
    <table width="100%" cellpadding="10">
    <%        
        String assocTableListHtml = search.getAssocTableHtml(subTables,dbid,dbn);        
    %>  
    <%= assocTableListHtml %>
    </table>
</div>

</td>
</tr>
</table>

<table cellpadding="10" width="100%">
<tr>
<td>
<div align="center">
    <hr/>
    <p><a href="./jsp/dbhome.jsp?dbid=<%=dbid%>&dbn=<%=dbn%>&page=1"><%=dbn%> Home</a></p>
</div>
</td>
</tr>
</table>

</td>
<!-- End of R panel -->
</tr>
</table>


<jsp:include page="/jsp/page/page_foot.jsp" />