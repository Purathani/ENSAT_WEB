<%@ page language="java" import="java.util.*,java.sql.*,java.io.*,java.text.Format,java.text.SimpleDateFormat,org.apache.log4j.*" pageEncoding="ISO-8859-1"%>
<%@page import="search.GenericExporter, search.CsvExporter, search.ExcelExporter"%>

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
<jsp:useBean id='studyauthz' class='security.StudyAuthz'  scope='session'/>
<jsp:useBean id='user' class='user.UserBean'  scope='session'/>
<jsp:useBean id='connect' class='ConnectBean.ConnectBean'  scope='session'/>
<jsp:useBean id='searchexport' class='search.SearchExport'  scope='session'/>

<%
ServletContext context = this.getServletContext();
    
String username = user.getUsername();
String userCenter = user.getCenter();
String userCountry = user.getCountry();

String fromSearchStr = request.getParameter("fromsearch");
if(fromSearchStr == null){
    fromSearchStr = "";
}
boolean fromSearch = !(fromSearchStr.equals(""));

/**
    mod 1 = all records belonging to user
    mod 2 = all records belonging to user's center
    mod 3 = all records belonging to user's country
    mod 4 = all records belonging to a particular tumor type
    mod 5 = all records belonging to a particular stored query
    mod 6 = all records belonging to a particular study
*/
String mod = request.getParameter("mod");
if(mod == null){
    mod = "";
}

String dbn = "";
String query = "";
String study = "";

if(mod.equals("4")){
    dbn = request.getParameter("dbn");
    if(dbn == null){
        dbn = "";
    }
}else if(mod.equals("5")){
    query = request.getParameter("query");
    if(query == null){
        query = "";
    }
}else if(mod.equals("6")){
    study = request.getParameter("study");
    if(study == null){
        study = "";
    }
}

//Logging configuration
String log4jConfigFile = context.getInitParameter("log4j_property_file");
Logger logger = Logger.getLogger("rootLogger");
logger.setLevel(Level.DEBUG);
PropertyConfigurator.configure(log4jConfigFile);

logger.debug("('" + username + "') - search_export.jsp");
logger.debug("('" + username + "') - Exporting data...");

%>

<h1>Export Patient Record Set</h1>

<%
Connection connection = connect.getConnection();

//Need to compile the relevant SQL here
String querySql = "";
if(!fromSearch){
    if(!study.equals("ltphpgl")){
        querySql = searchQuery.compileExportQuerySql(mod, dbn, study, query, username, userCountry, userCenter);
    }
}else{
    querySql = search.getSearchQuery();
}

//Set the query into the searchQuery session bean along with tables, parameters and calculated columns (content information)
searchQuery.setSearchQuerySql(querySql);
logger.debug("querySql: " + querySql);
searchQuery.setTablesToExport(dbn,mod,query,study,userCenter,userCountry);
searchQuery.setParametersToExport(mod,query,study,userCenter,userCountry,dbn);

//Compile list of IDs based on the SQL query
ArrayList<String> patientIdList = null;
logger.debug("fromSearch: " + fromSearch + ", username: " + username);
if(fromSearch){
    if(username.equals("segolene.hescot@u-psud.fr")){
        patientIdList = searchQuery.getPatientIdList(querySql, connection);    
        logger.debug("patientIdList.size() (#1): " + patientIdList.size());
    }else if(username.equals("oncotrial.sanluigi@gmail.com")){
        patientIdList = searchQuery.getPatientIdList(querySql, connection);
        logger.debug("patientIdList.size() (#1.5): " + patientIdList.size());
    }else if(username.equals("felix.beuschlein@med.uni-muenchen.de")){
        patientIdList = searchQuery.getPatientIdList(querySql, connection);
        logger.debug("patientIdList.size() (#1.5): " + patientIdList.size());
    }else{
        patientIdList = searchQuery.getPatientIdList(querySql, connection, username);
        logger.debug("patientIdList.size() (#2): " + patientIdList.size());
    }    
}else{
    
    if(!study.equals("ltphpgl")){
        patientIdList = searchQuery.getPatientIdList(querySql, connection);    
        logger.debug("patientIdList.size() (#3): " + patientIdList.size());
    }else{
        Vector<Vector> ltphpglIds = searchexport.getLimitedStudyNumbers("ltphpgl",context);
        patientIdList = new ArrayList<String>();
        for(int i=0; i<ltphpglIds.size(); i++){
            Vector<String> idIn = ltphpglIds.get(i);
            String ensatId = searchQuery.formatPatientId(idIn.get(0), idIn.get(1));
            patientIdList.add(ensatId);
        }
    }
}

//Restrict to the user's center only
if(fromSearch){    
    if(!username.equals("segolene.hescot@u-psud.fr") && !username.equals("oncotrial.sanluigi@gmail.com")){
        patientIdList = searchQuery.restrictList(patientIdList, userCenter);
    }
}

boolean noRecords = (patientIdList.size() == 0);

//Get a time estimate for the export processing
String timeEstimate = searchQuery.getTimeEstimate(patientIdList, mod, dbn, study, querySql, connection);

String explanatoryText = searchQuery.getExportText(mod, dbn, study, query, username, userCountry, userCenter);
%>

<p><%=explanatoryText%></p>

<%
if(!noRecords){
%>
    
<p>
    The following list of patient IDs will be returned with this export. Select those that you would like to be part of the download.
</p>

<p>
<div id="timeEstimate">
    The query will take approximately <strong><%=timeEstimate%></strong> minutes to process.
</div>
</p>

<p>
<form id="patient_selection_form" name="patient_selection_form" action="./jsp/search/process_export.jsp" method="POST">
    
<%
String patientSelectionFormHtml = searchQuery.getPatientSelectionFormHtml(patientIdList,userCenter);
%>  
<%=patientSelectionFormHtml%>

<input type="hidden" name="dbn" value="<%=dbn%>"/>

<input type="submit" value="Select patients"/>
</form>

<script type="text/javascript">
    //The JS below works as a change monitor to the form on the page - still need to send the function to the external JS file
    //Want the getExportTimeEstimate('dbn','mod'); method in there
    //document.getElementById("patient_selection_form").addEventListener('change', function(){ alert("Change fired #2...");}, false);
</script>

</p>

<%
}else{
%>
<p>There are no patients associated with this query. Please return to the export page and try another query.</p>
<%
}
%>

<p><hr/></p>

<p><strong>Return to <a href="./jsp/search/display_export.jsp">Export function</a></strong></p>

<p><strong>Return to <a href="./jsp/home.jsp">ENSAT Home</a></strong></p>

<jsp:include page="/jsp/page/page_foot.jsp" />
</td> 
      </tr> 
    </table> 
    </td> 
    
</tr>
</table>

