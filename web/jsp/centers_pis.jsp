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
logger.debug("('" + username + "') - centers_pis.jsp");

//Connection conn = connect.getConnection();

ServletContext context = this.getServletContext();

String regionView = "";
String countryCode = request.getParameter("country_code");
if(countryCode == null){    
    countryCode = "";
}
if(countryCode.equals("")){
    regionView = "'150'";
}else{
    String validCountryCode = presentation.getValidCountryCode(countryCode);    
    regionView = "'" + validCountryCode + "'";    
}

String countryName = presentation.getCodeName(countryCode);

String centerCodeTableOut = presentation.getCenterCodeTable(context);

%>

<h2>Center codes and Principal Investigators registered in the ENSAT Registry</h2>

<p>Click on country name to view those specific centers</p>
<%
Vector<Vector> cities = presentation.getCities(context,countryCode);
int cityNum = cities.size();
String jsCityNumber = "";
for(int i=0; i < cityNum; i++){
    Vector<String> cityIn = cities.get(i);    
    jsCityNumber += "['" + cityIn.get(0) + "']";
    if(i != cityNum-1){
        jsCityNumber += ",";
    }
}
%>

<script type='text/javascript' src='https://www.google.com/jsapi'></script>
    
<script type='text/javascript'>
     google.load('visualization', '1', {'packages': ['geochart']});
     google.setOnLoadCallback(drawRegionsMap);

      function drawRegionsMap() {
        var data = google.visualization.arrayToDataTable([
          //['City',  'Number of institutions'],
          ['City'],
          <%= jsCityNumber%>
        ]);
        
        var options = {
            region: <%=regionView%>,
            displayMode: 'markers'            
        };

        var chart = new google.visualization.GeoChart(document.getElementById('chart_div'));
        chart.draw(data, options);
    };
</script>

<table width="100%" border="1" cellpadding="5">
    <tr>
        <td valign="top">
            <%=centerCodeTableOut%>
        </td>
        <%--<%
        if(!countryCode.equals("")){
        %>--%>
        <td valign="top">
            <div align="center">
                <br/>
                <br/>
                <br/>
                <div id="chart_div" style="width: 550px; height: 300px;"></div>
            </div>
        </td>
        <%--<%
        }    
        %>--%>
    </tr>
</table>








<jsp:include page="/jsp/page/page_foot.jsp" />

</td> 
      </tr> 
    </table> 
    </td> 
    
</tr>

</table>


