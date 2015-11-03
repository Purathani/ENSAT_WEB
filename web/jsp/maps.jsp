<%@ page language="java" import="java.util.*,java.sql.*,org.apache.log4j.*" pageEncoding="ISO-8859-1"%>

<jsp:include page="/jsp/page/check_credentials.jsp" />
<jsp:include page="/jsp/page/check_input.jsp" />
<jsp:include page="/jsp/page/page_head.jsp" />

<td colspan="2" width="81%" align="left" valign="top" background="/images/possbk.jpg"><table width="1200" border="0" cellspacing="0" cellpadding="10"> 
      <tr> 
        <td width="15" align="left" valign="top">&nbsp;</td> 
        <td align="left" valign="top"><!-- #BeginEditable "MainText" --> 

<jsp:include page="/jsp/page/page_nav.jsp" />

<jsp:useBean id='connect' class='ConnectBean.ConnectBean' scope='session'/>
<jsp:useBean id='presentation' class='summaryinfo.SummaryInfo' scope='session'/>
<jsp:useBean id='user' class='user.UserBean'  scope='session'/>

<%    
//Try the log4j stuff here
Logger logger = Logger.getLogger("rootLogger");
logger.setLevel(Level.DEBUG);
PropertyConfigurator.configure("/root/logs/log4j_ensat.properties");
        
String username = user.getUsername();
logger.debug("('" + username + "') - maps.jsp");

Connection conn = connect.getConnection();

String regionView = "150";
String regionTitle = "Europe";
String regionViewIn = request.getParameter("region_view");
if(regionViewIn == null){
    regionViewIn = "";
    regionView = "150";
    regionTitle = "Europe";
}

if(regionViewIn.equals("2")){
    regionView = "'world'";
    regionTitle = "World";
}

String typeDisp = "Total";
String typeIn = request.getParameter("type");
if(typeIn == null){
    typeIn = "all";
}
if(typeIn.equals("ACC") || typeIn.equals("Pheo") || typeIn.equals("NAPACA") || typeIn.equals("APA")){
    typeDisp = typeIn;
}

%>

<h2>International coverage of the ENSAT registry - <%=regionTitle%> (<%=typeDisp%>)</h2>

<table cellpadding="10">
    <tr>
        <td valign="top">
<h3>Region</h3>

<p><a href="/jsp/maps.jsp?region_view=1">Europe</a></p>
<p><a href="/jsp/maps.jsp?region_view=2">World</a></p>

        </td>
        <td valign="top">

<h3>Tumor type (Europe)</h3>

<p><a href="/jsp/maps.jsp?region_view=1&type=ACC">ACC</a></p>
<p><a href="/jsp/maps.jsp?region_view=1&type=Pheo">Pheo</a></p>
<p><a href="/jsp/maps.jsp?region_view=1&type=NAPACA">NAPACA</a></p>
<p><a href="/jsp/maps.jsp?region_view=1&type=APA">APA</a></p>

        </td>
</tr>
</table>

<%
Vector<Vector> countryNumbers = presentation.getCountryNumbers(conn,typeIn);
int countryNumbersSize = countryNumbers.size();
String jsCountryNumber = "";
for(int i=0; i < countryNumbersSize; i++){
    Vector<String> countryNumber = countryNumbers.get(i);
    jsCountryNumber += "['" + presentation.getCodeName(countryNumber.get(0)) + "'," + countryNumber.get(1) + "]";
    if(i != countryNumbersSize-1){
        jsCountryNumber += ",";
    }
}
%>

<script type='text/javascript' src='https://www.google.com/jsapi'></script>
    
<script type='text/javascript'>
     google.load('visualization', '1', {'packages': ['geochart']});
     google.setOnLoadCallback(drawRegionsMap);

      function drawRegionsMap() {
        var data = google.visualization.arrayToDataTable([
          ['Country', 'Number of records'],
          <%= jsCountryNumber%>
        ]);
        
        var options = {region: <%=regionView%>};

        var chart = new google.visualization.GeoChart(document.getElementById('chart_div'));
        chart.draw(data, options);
    };
</script>
    

<div id="chart_div" style="width: 900px; height: 500px;"></div>


<jsp:include page="/jsp/page/page_foot.jsp" />

</td> 
      </tr> 
    </table> 
    </td> 
    
</tr>

</table>


