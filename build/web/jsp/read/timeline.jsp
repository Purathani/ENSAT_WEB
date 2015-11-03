<%--  
    Document   : timeline
    Created on : 1 juil. 2013, 10:48:46
    Author     : Pierre-Yves
--%>

<%@ page language="java" import="java.util.*,java.sql.*,java.text.*,org.apache.log4j.*" pageEncoding="ISO-8859-1"%>

<jsp:useBean id='user' class='user.UserBean'  scope='session'/>
<jsp:useBean id='connect' class='ConnectBean.ConnectBean'  scope='session'/>
<jsp:useBean id='update' class='update_main.Update' scope='session'/>
<jsp:useBean id='updateSub' class='update_main.UpdateSub' scope='session'/>

<jsp:include page="/jsp/page/check_credentials.jsp" />
<jsp:include page="/jsp/page/check_input.jsp" />
<jsp:include page="/jsp/page/check_userdetail.jsp" />
<jsp:include page="/jsp/page/page_head.jsp" />


<td colspan="2" width="81%" align="left" valign="top" background="/images/possbk.jpg"><table width="1200" border="0" cellspacing="0" cellpadding="10">
        <tr> 
            <td width="15" align="left" valign="top">&nbsp;</td> 
            <td align="left" valign="top"><!-- #BeginEditable "MainText" -->    
                         
                <jsp:include page="/jsp/page/page_nav.jsp" />
                
<%

ServletContext context = this.getServletContext();

String userCountry = user.getCountry();
String username = user.getUsername();
update.setUsername(username);
String dbn = request.getParameter("dbn");
String dbid = request.getParameter("dbid");
String pid = request.getParameter("pid");
String centerid = request.getParameter("centerid");
String modality = request.getParameter("modality");
if (modality == null) {
    modality = "";
}
pid = update.standardisePid(pid);


String detailSwitch = request.getParameter("detail");
String detailSwitchOpp = "";
String detailSwitchTag = "";
if (detailSwitch == null) {
    detailSwitch = "off";    
}

if(detailSwitch.equals("on")){
    detailSwitchOpp = "off";
    detailSwitchTag = "less";    
}else{
    detailSwitchOpp = "on";
    detailSwitchTag = "more";
}



//Logging configuration
String log4jConfigFile = context.getInitParameter("log4j_property_file");
Logger logger = Logger.getLogger("rootLogger");
logger.setLevel(Level.DEBUG);
PropertyConfigurator.configure(log4jConfigFile);

logger.debug("('" + username + "') - timeline.jsp (Ensat ID = '" + centerid + "-" + pid + "')");

Connection connection = connect.getConnection();
Connection paramConn = connect.getParamConnection();

//Retrieve data parameter information
String[] tablenames = update.getTablenames(dbn);


//POSSIBLE BUG..? 
//Problem in here with normal_tissue_option
Vector<Vector> parameters = update.getParameters(tablenames, pid, centerid, connection, paramConn,dbn);
int dateNum = update.getDateNum(parameters);
String dataRowStr = update.getDataRowStr(parameters, dateNum, pid, centerid, connection, detailSwitch);
                    
//Picking out individual parameters for rendering                    
String sex = updateSub.getParameterValue("sex", parameters);
String doctor = updateSub.getParameterValue("local_investigator", parameters);
String doctormail = updateSub.getParameterValue("investigator_email", parameters);
String consent = update.getParameterValue("consent_obtained", parameters);
String countryCode = "";
if(consent.equals("National")){
    countryCode = "(" + centerid.substring(0,2) + ")";
}

//Get style parameters
String borderColor = update.getBorderColor(dbn);
String backgroundColor = update.getBackgroundColor(dbn);
String chartSize = "300";
%>

<style type="text/css">
div.timeline-event {
    border-color: <%=borderColor%>;    
    background-color: <%=backgroundColor%>;
    text-align: left;
}
div.timeline-event-selected {
    border-color: black;
    background-color: #FFFFFF;
}
</style>  

    <script type="text/javascript">
      google.load("visualization", "1");

      // Set callback to run when API is loaded
      google.setOnLoadCallback(drawVisualization);

      // Called when the Visualization API is loaded.
      function drawVisualization() {
        // Create and populate a data table.
        var data = new google.visualization.DataTable();
        data.addColumn('datetime', 'start');
        data.addColumn('datetime', 'end');
        data.addColumn('string', 'content');
        
        <%=dataRowStr%>

        // specify options
        var options = {
          "width":  "100%",
          "height": "<%=chartSize%>px",
          "style": "box" // optional
        };

        // Instantiate our timeline object.
        var timeline = new links.Timeline(document.getElementById('mytimeline'));

        // Draw our timeline with the created data and options
        timeline.draw(data, options);
      }
   </script>
                
                <table width="100%" cellpadding="5">                    
                    <tr>
                        <td>
                            <div align="left">
                                <h1><%=dbn%> Timeline</h1>
                                <h2><%=centerid%>-<%=pid%></h2>
                                <p>The timeline below shows all notable clinical events for this patient.</p>
                                <p>Click and drag to view horizontally or use the mouse scroll-wheel to resize.</p>                                
                                <p><a href="./jsp/read/timeline.jsp?dbid=<%=dbid%>&dbn=<%=dbn%>&pid=<%=pid%>&centerid=<%=centerid%>&detail=<%=detailSwitchOpp%>">
                                    Click here for <%=detailSwitchTag%> detail
                                    </a></p>
                            </div>
                        </td>
                        <td>
                            <div align="right">
                                Clinician: <strong><a href="mailto:<%= doctormail%>"><%=doctor%></a></strong><br/><br/>
                                Sharing consent level: <strong><%=consent%> <%=countryCode%></strong>                            
                            </div>
                        </td>
                </table>
                
                <table width="100%" border="1" height="<%=chartSize%>">
                    <tr>
                        <td height="<%=chartSize%>">
                            <div id="mytimeline"></div>
                        </td>
                    </tr>
                </table>
                            
                            <br/>
                            <br/>
                            
                
                <div align="center">
                    <a href="./jsp/dbhome.jsp?dbid=<%=dbid%>&dbn=<%=dbn%>&page=1"><%=dbn%> Home</a>
                    &nbsp;&nbsp;&nbsp;&nbsp;
                    <a href="./jsp/read/detail.jsp?dbid=<%=dbid%>&dbn=<%=dbn%>&pid=<%=pid%>&centerid=<%=centerid%>"> <%=centerid%>-<%=pid%></a>
                </div>
                
                
                
                <jsp:include page="/jsp/page/page_foot.jsp" />
                
            </td> 
            
            
            