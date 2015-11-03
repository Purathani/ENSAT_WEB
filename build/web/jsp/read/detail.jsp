
<%@ page language="java" import="java.util.*,java.sql.*,java.text.*,org.apache.log4j.*" pageEncoding="ISO-8859-1"%>

<jsp:useBean id='user' class='user.UserBean'  scope='session'/>
<jsp:useBean id='connect' class='ConnectBean.ConnectBean'  scope='session'/>
<jsp:useBean id='update' class='update_main.Update' scope='session'/>

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
String path = request.getContextPath();
String baseUrl = request.getScheme()+"://"+request.getServerName()+":"+request.getServerPort()+path+"/";

Connection paramConn = connect.getParamConnection();
Connection connection = connect.getConnection();

String userCenter = user.getCenter(); 
String username = user.getUsername();
update.setUsername(username);
String dbn = request.getParameter("dbn");
String dbid = request.getParameter("dbid");
String pid = request.getParameter("pid");
String centerid = request.getParameter("centerid");
pid = update.standardisePid(pid);
String lineColour = update.getLineColour(dbn);

//Logging configuration
String log4jConfigFile = context.getInitParameter("log4j_property_file");
Logger logger = Logger.getLogger("rootLogger");
logger.setLevel(Level.DEBUG);
PropertyConfigurator.configure(log4jConfigFile);

logger.debug("('" + username + "') - detail.jsp (Ensat ID = '" + centerid + "-" + pid + "')");

//Retrieve data parameter information
String[] tablenames = null;

if(dbn.equals("ACC")){    
    tablenames = new String[3];
    tablenames[0] = "Identification";
    tablenames[1] = "ACC_DiagnosticProcedures";
    tablenames[2] = "ACC_TumorStaging";
}else if(dbn.equals("Pheo")){    
    tablenames = new String[2];
    tablenames[0] = "Identification";
    tablenames[1] = "Pheo_PatientHistory";
}else if(dbn.equals("NAPACA")){    
    tablenames = new String[2];
    tablenames[0] = "Identification";
    tablenames[1] = "NAPACA_DiagnosticProcedures";
}else if(dbn.equals("APA")){    
    tablenames = new String[2];
    tablenames[0] = "Identification";
    tablenames[1] = "APA_PatientHistory";
}

Vector<Vector> parameters = update.getParameters(tablenames, pid, centerid, connection, paramConn,dbn);
Vector<Vector> menus = update.getMenus(parameters, paramConn);
menus = update.getDynamicMenus(parameters, menus, userCenter,connection,"","",dbn);

String javascriptValidationArray = update.getJavascriptValidationArray(parameters,baseUrl);
%>
<%=javascriptValidationArray%>

<p><div id="update"></div></p>            

<table width="100%" cellpadding="5" border="1">
    <tr>
        <td valign="top">

<form action="./jsp/update/update_confirm.jsp?dbid=<%=dbid%>&dbn=<%=dbn%>&pid=<%=pid%>&centerid=<%=centerid%>" method="POST" onsubmit="return validateForm(this,'update','<%=dbn%>','<%=centerid%>','<%=baseUrl%>');">
<h1><%=dbn%> Record <%=centerid%>-<%=pid%> details</h1>



<%if(dbn.equals("ACC")){%>
<p><a href='./jsp/read/status_report.jsp?dbid=<%=dbid%>&dbn=<%=dbn%>&pid=<%=pid%>&centerid=<%=centerid%>'>Individual Summary Status Report</a></p>
<%}else if(dbn.equals("Pheo")){%>
<p><a href='./jsp/read/transfer_study.jsp?dbid=<%=dbid%>&dbn=<%=dbn%>&pid=<%=pid%>&centerid=<%=centerid%>&study=pmt'>Transfer to PMT</a></p>
<%}else if(dbn.equals("NAPACA")){%>

<p>Transfer patient to: 
<ul>
    <li><a href='./jsp/read/transfer_registry.jsp?pid=<%=pid%>&centerid=<%=centerid%>&study=ACC'>ACC</a></li>
    <li><a href='./jsp/read/transfer_registry.jsp?pid=<%=pid%>&centerid=<%=centerid%>&study=Pheo'>Pheo</a></li>
    <li><a href='./jsp/read/transfer_registry.jsp?pid=<%=pid%>&centerid=<%=centerid%>&study=APA'>APA</a></li>
</ul>
</p>

<p><a href='./jsp/read/transfer_study.jsp?dbid=<%=dbid%>&dbn=<%=dbn%>&pid=<%=pid%>&centerid=<%=centerid%>&study=pmt'>Transfer to PMT</a></p>
<%
}    
%>

<p><a href="./jsp/read/timeline.jsp?dbid=<%=dbid%>&dbn=<%=dbn%>&pid=<%=pid%>&centerid=<%=centerid%>">Patient Timeline</a></p>

<table border="1px" width="100%" cellpadding="5">
    <%
    String parameterHtml = update.getParameterHtml(parameters, menus, lineColour,dbn,"",connection,centerid,pid,"",baseUrl);
    %>
    <%= parameterHtml %>
</table>
<!-- End of L panel -->

<input type="submit" name="update_record" value="Update Details" />
</form>
</td>
<!-- R panel -->
<td width="50%" valign="top">

<table cellpadding="10" width="100%">

<tr>
<td>
    
<div align="left">
    <h1>Associated Record Information</h1>
        <%
    Vector<Vector> subTables = update.compileSubTableList(dbn,username,centerid,pid,connection);    
    %>
    <table width="100%" cellpadding="10">
    <%        
        String assocTableListHtml = update.getAssocTableHtml(subTables,dbid,dbn,centerid,pid);
    %>  
    <%= assocTableListHtml %>
    </table>
</div>

<hr/>

<h1>Summary of Associated Record Information</h1>

<table border="1px" cellpadding="5" width="100%">
    
        <%        
        String assocInfoListHtml = update.getAssocInfoHtml(dbid,dbn,centerid,pid,connection);
    %>  
    <%= assocInfoListHtml %>
</table>
    

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
</td> 
      </tr> 
    </table> 
    </td> 
    
</tr>
</table>

