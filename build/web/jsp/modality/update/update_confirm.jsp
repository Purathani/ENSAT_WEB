<%@ page language="java" import="java.util.*,java.sql.*,org.apache.log4j.*" pageEncoding="ISO-8859-1"%>

<jsp:include page="/jsp/page/check_credentials.jsp" />
<jsp:include page="/jsp/page/check_input.jsp" />
<jsp:include page="/jsp/page/check_usermoddetail.jsp" />
<jsp:include page="/jsp/page/page_head.jsp" />

<td colspan="2" width="81%" align="left" valign="top" background="/images/possbk.jpg"><table width="1200" border="0" cellspacing="0" cellpadding="10"> 
      <tr> 
        <td width="15" align="left" valign="top">&nbsp;</td> 
        <td align="left" valign="top"><!-- #BeginEditable "MainText" --> 

<jsp:include page="/jsp/page/page_nav.jsp" />

<jsp:useBean id='connect' class='ConnectBean.ConnectBean'  scope='session'/>
<jsp:useBean id='updatesub' class='update_main.UpdateSub'  scope='session'/>
<jsp:useBean id='user' class='user.UserBean'  scope='session'/>

<%
ServletContext context = this.getServletContext();

String dbn = request.getParameter("dbn");
String dbid = request.getParameter("dbid");
String pid = request.getParameter("pid");
pid = updatesub.standardisePid(pid);
String centerid = request.getParameter("centerid");
String modality = request.getParameter("modality");
String modid = request.getParameter("modid");

String lineColour = updatesub.getLineColour(dbn);

Connection connection = connect.getConnection();
Connection paramConn = connect.getParamConnection();

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

//THINK THIS SHOULD BE ESCAPED TO A GENERALISED DISPLAY CLASS AND JSP FILE
Vector<Vector> mainParameters = updatesub.getMainParameters(tablenames, pid, centerid, connection, paramConn);

String pageTitle = updatesub.getPageTitle(modality, dbn);

//Retrieve data parameter information
String[] subTablenames = new String[1];
subTablenames[0] = updatesub.getSubTablename(modality, dbn);
Vector<Vector> parameters = updatesub.getParameters(subTablenames, request, paramConn);

//Logging configuration
String log4jConfigFile = context.getInitParameter("log4j_property_file");
Logger logger = Logger.getLogger("rootLogger");
logger.setLevel(Level.DEBUG);
PropertyConfigurator.configure(log4jConfigFile);

String username = user.getUsername();
logger.debug("('" + username + "') - modality/update_confirm.jsp (modality = '" + modality + "')");


%>

<h1><%=dbn%> Record <%=centerid%>-<%=pid%> Detail</h1>

<table border="1px" width="100%">

<!-- Main canvas table has two panels (L and R) -->
<tr>
<td width="50%" valign="top">

<!-- L panel -->
<table border="1px" width="100%">
    <%
    String mainParameterHtml = updatesub.getMainParameterHtml(mainParameters, lineColour,dbn);
    %>
    <%= mainParameterHtml %>
</table>
<!-- End of L panel -->

</td>
<!-- R panel -->
<td width="50%" valign="top">

<div align="center">

    <%
    if(pageTitle.equals("None")){
        %>
        <h2>No modality selected</h2>
        <%
        }else{
    %>


<h1><%=dbn%> Update <%=pageTitle%> Record</h1>

<%-- ADD IN DETAILS FOR EURINE-ACT INCLUSION HERE --%>
<%--<%
if(dbn.equals("NAPACA") || dbn.equals("ACC")){
    if(modality.equals("biomaterial") && eurineActInclusion.equals("Yes")){
%>
<p>
<div id="eurineactflag">
    This patient has been indicated to be part of the EURINE-ACT study. For valid data entry the following samples are required:<br/><br/>
</div>
    <table width="50%">
        <tr><td>
            <div id="eurineactflag">
    <ul>
        <li>10 ml 24h urine (with volume information in ml/24h)</li>
        <li>10 ml spot urine</li>
        <li>1 ml serum</li>
        <li>1.5 ml heparin-plasma</li>
    </ul>
    </div>
        </td></tr>
    </table>


</p>
<%
}
}
%>--%>

<form action="./jsp/modality/update/update_process.jsp?dbid=<%=dbid%>&dbn=<%=dbn%>&pid=<%=pid%>&centerid=<%=centerid%>&modality=<%=modality%>&modid=<%=modid%>" method="POST">


<!--<table border="1px" cellpadding="5">-->
<%
String lastPageConfirmHtml = updatesub.getLastPageParamConfirmHtml(parameters, lineColour,dbn, request);
%>
<%= lastPageConfirmHtml %>
<!--</table>-->

<input type="submit" name="update_record" value="Update Details"/>
</form>

<%
} //End of modality selection clause
%>

</div>

<table cellpadding="10" width="100%">
<tr>
<td>
    <div align="center">
    <hr/>
    </div>
</td>
</tr>
<tr>
<td>

<div align="center">
<table width="100%">
<tr>
<td>
<div align="center">
    <p><a href="./jsp/read/detail.jsp?dbid=<%=dbid%>&dbn=<%=dbn%>&pid=<%=pid%>&centerid=<%=centerid%>">Patient <%=centerid%>-<%=pid%> Detail</a></p>
</div>
</td>
<td>
<div align="left">
    <p><a href="./jsp/dbhome.jsp?dbid=<%=dbid%>&dbn=<%=dbn%>"><%=dbn%> Home</a></p>
</div>
</td>
</tr>
</table>
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


