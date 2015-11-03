<%@ page language="java" import="java.util.*,org.apache.log4j.*,java.sql.*" pageEncoding="ISO-8859-1"%>

<jsp:useBean id='searchQuery' class='search.SearchUtilities'  scope='session'/>
<jsp:useBean id='search' class='search.Search'  scope='session'/>
<jsp:useBean id='user' class='user.UserBean'  scope='session'/>
<jsp:useBean id='connect' class='ConnectBean.ConnectBean'  scope='session'/>

<div align="left">
    <h3>Summary Results</h3>

    <%
    Connection conn = connect.getConnection();
        
    String dbn = (String) request.getParameter("dbn");    
    String pageNum = (String) request.getParameter("page");
    String showHideFlagSearch = "hide";
    if(pageNum == null){        
        pageNum = "1";        
    }else{
        showHideFlagSearch = "show";
    }
    try{
        int pageNumInt = Integer.parseInt(pageNum);        
        if(pageNumInt < 1){
            pageNum = "1";
        }
    }catch(NumberFormatException nfe){
        pageNum = "1";
    }
    ResultSet searchResults = search.getSearchResults();
    %>
    
    <%
    String recordNumStr = "";
    recordNumStr = "" + search.getRowCount(searchResults);
    %>
    <p>Total number of records returned: <strong><%=recordNumStr%></strong></p>
    
    <p><a href="javascript:showQuerySummary();">Summary of query executed</a> (click to show/hide)</p>    
    <%    
    String searchQueryStr = "";
    searchQueryStr = search.getSearchQuerySummary();    
    %>
    <div id="query_summary" class="hide">
    <%=searchQueryStr%>
    </div>
    
    <%
    Vector<Vector> centerDistn = search.getCenterDistribution(searchResults);
    %>
    <p><a href="javascript:showCenterDistribution();">Center distribution</a> (click to show/hide)</p>
    
    <div id="center_distribution_summary" class="hide">
    <table border="1px" cellpadding="10">
        <tr><th>Center ID</th><th>Number</th></tr>        
    <%
    for(int i=0; i<centerDistn.size(); i++){
        Vector<String> centerInfoIn = centerDistn.get(i);        
    %>
    <tr>
        <th><%=centerInfoIn.get(0)%></th>
        <td><%=centerInfoIn.get(1)%></td>
    </tr>
    <%
    }
    %>
    </table>
    </div>

    <%
    Vector<Vector> countryDistn = search.getCountryDistribution(searchResults);
    %>
    <p><a href="javascript:showCountryDistribution();">Country distribution</a> (click to show/hide)</p>

    
    <div id="country_distribution_summary" class="hide">
    <table border="1px" cellpadding="10">
        <tr><th>Country</th><th>Number</th></tr>        
    <%
    for(int i=0; i<countryDistn.size(); i++){
        Vector<String> countryInfoIn = countryDistn.get(i);        
    %>
    <tr>
        <th><%=countryInfoIn.get(0)%></th>
        <td><%=countryInfoIn.get(1)%></td>
    </tr>
    <%
    }
    %>
    </table>
    </div>
    
    <hr/>

    <h3>Detailed Results</h3>
    
    <%
    //String username = user.getUsername();
    
    //String querySql = "";
    
    %>
    <p><a href="./jsp/search/search_export.jsp?mod=4&dbn=<%=dbn%>&fromsearch=true">Export these results</a> (only records that you have access to)</p>
    
    <p><a href="javascript:showDetailedRecordOutput();">Search result</a> (only records that you have access to - click to show/hide)</p>
    
    <%
    String resultMatrixDetail = search.getResultMatrixDetail(searchResults, pageNum, dbn, conn);    
    %>
    <div id="detailed_record_output" class="<%=showHideFlagSearch%>">
    <p><em>To modify view, select new parameters - maximum of 5 - in the section marked "Parameters to view"</em></p>
    <%=resultMatrixDetail%>    
    </div>
    
</div>
