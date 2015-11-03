<%@ page language="java" import="java.util.*,java.sql.*,org.apache.log4j.*" pageEncoding="ISO-8859-1"%>

<jsp:include page="/jsp/page/check_credentials.jsp" />
<%--<jsp:include page="/jsp/page/check_input.jsp" />--%>
<jsp:include page="/jsp/page/page_head.jsp" />

<td colspan="2" width="81%" align="left" valign="top" background="/images/possbk.jpg"><table width="1200" border="0" cellspacing="0" cellpadding="10"> 
      <tr> 
        <td width="15" align="left" valign="top">&nbsp;</td> 
        <td align="left" valign="top"><!-- #BeginEditable "MainText" --> 

<jsp:include page="/jsp/page/page_nav.jsp" />

<jsp:useBean id='connect' class='ConnectBean.ConnectBean' scope='session'/>
<jsp:useBean id='presentation' class='summaryinfo.SummaryInfo' scope='session'/>
<jsp:useBean id='user' class='user.UserBean'  scope='session'/>
<jsp:useBean id='quality' class='update_main.DataQuality' scope='session'/>

<%    
ServletContext context = this.getServletContext();
Connection conn = connect.getConnection();
Connection ccConn = connect.getCcConnection();        
    
//Logging configuration
String log4jConfigFile = context.getInitParameter("log4j_property_file");
Logger logger = Logger.getLogger("rootLogger");
logger.setLevel(Level.DEBUG);
PropertyConfigurator.configure(log4jConfigFile);
        
String username = user.getUsername();
logger.debug("('" + username + "') - completeness.jsp");

//Menu of centers
String centerMenu = quality.getCenterMenu(ccConn);

//Menu of studies
String studyMenu = quality.getStudyMenu(conn);

//Menu of studies
String typeMenu = quality.getTypeMenu();

//Get patient list
Vector<String> patientListCenter = new Vector<String>();
String centerid = request.getParameter("centerid");
if(centerid == null){
    centerid = "";
}else{
    patientListCenter = quality.getPatientList("centerid",centerid,conn);
}

Vector<String> patientListStudy = new Vector<String>();
String study = request.getParameter("study");
if(study == null){
    study = "";
}else{
    patientListStudy = quality.getPatientList("study",study,conn);
}

Vector<String> patientListType = new Vector<String>();
String type = request.getParameter("type");
if(type == null){
    type = "";
}else{
    patientListType = quality.getPatientList("type",type,conn);
}

//Combine the three
Vector<String> patientList = new Vector<String>();
patientList = quality.consolidatePatientList(patientListCenter, patientListStudy, patientListType);


/*for(int i=0; i<patientList.size(); i++){
    logger.debug("patientList(" + i + "): " + patientList.get(i));
}*/

logger.debug("patientList.size(): " + patientList.size());

//Now crunch the numbers
Vector<Vector> tablesPercentage = quality.getTablesPercentage(patientList, conn, type, study);

/*for(int i=0; i<tablesPercentage.size(); i++){
    Vector<String> tablePercentIn = tablesPercentage.get(i);
    logger.debug("tablePercentIn(" + i + ",0): " + tablePercentIn.get(0));
    logger.debug("tablePercentIn(" + i + ",1): " + tablePercentIn.get(1));
    logger.debug("----");
}*/


//Now feed this into a HighCharts graph - convert the percentage into JSON
String xAxisCatNames = quality.getXAxisNamesData(tablesPercentage,false); //['Apples', 'Bananas', 'Oranges']
String seriesData = quality.getXAxisNamesData(tablesPercentage,true);; //[1, 0, 4]
String tagDisp = "all";
if(centerid.equals("") && !study.equals("") && type.equals("")){
    tagDisp = study + " study";
}else if(study.equals("") && !centerid.equals("") && type.equals("")){
    tagDisp = "" + centerid + "";    
}else if(study.equals("") && centerid.equals("") && !type.equals("")){
    tagDisp = "" + type + "";    
}else if(!study.equals("") && !centerid.equals("") && type.equals("")){
    tagDisp = study + " (" + centerid + ")";    
}else if(!study.equals("") && centerid.equals("") && type.equals("")){
    tagDisp = study + " - " + type + "";    
}else if(!study.equals("") && !centerid.equals("") && !type.equals("")){
    tagDisp = study + " (" + centerid + ") - " + type;
}else if(study.equals("") && !centerid.equals("") && !type.equals("")){
    tagDisp = " " + centerid + " - " + type;    
}


%>

<h3>ENSAT Registry Completeness</h3>

<p>Select any combination of center ID, study and tumor type (including leaving any blank) to see the completeness results within the registry</p>

<form method="POST" action="./jsp/quality/completeness.jsp">

<h3>Select center (returns all tables):</h3>

<p><%=centerMenu%></p>

<h3>Select study (returns tables associated with the study types):</h3>

<p><%=studyMenu%></p>

<h3>Select tumor type (returns tables for each tumor type):</h3>

<p><%=typeMenu%></p>

<input type="submit" name="select_filter" value="Select filter"/>
</form>

<script>
    $(function () { 
    $('#container').highcharts({
        chart: {
            type: 'bar'
        },
        title: {
            text: 'Completeness % (<%=tagDisp%>)'
        },
        xAxis: {
            categories: <%=xAxisCatNames%>
        },
        yAxis: {
            title: {
                text: '% complete (<%=tagDisp %>)'
            }
        },
        series: [{
            name: '<%=tagDisp%>',
            data: <%=seriesData%>
        }]
    });
});
</script>

<hr/>

<%
if(!tagDisp.equals("all")){
%>
<div id="container" style="width:100%; height:1500px; border: 1px"></div>
<%
}else{
%>
<p><div id="eurineactflag">Please select a filter</div></p>
<%
}
%>

<jsp:include page="/jsp/page/page_foot.jsp" />

</td> 
      </tr> 
    </table> 
    </td> 
    
</tr>

</table>


