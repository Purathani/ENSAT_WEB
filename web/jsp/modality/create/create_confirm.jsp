<%@ page language="java" import="java.util.*,java.sql.*,org.apache.log4j.*" pageEncoding="ISO-8859-1"%>

<jsp:include page="/jsp/page/check_credentials.jsp" />
<jsp:include page="/jsp/page/check_input.jsp" />
<jsp:include page="/jsp/page/page_head.jsp" />

<td colspan="2" width="81%" align="left" valign="top" background="/images/possbk.jpg"><table width="1200" border="0" cellspacing="0" cellpadding="10"> 
      <tr> 
        <td width="15" align="left" valign="top">&nbsp;</td> 
        <td align="left" valign="top"><!-- #BeginEditable "MainText" --> 

<jsp:include page="/jsp/page/page_nav.jsp" />

<jsp:useBean id='connect' class='ConnectBean.ConnectBean'  scope='session'/>
<jsp:useBean id='createsub' class='create_main.CreateSub'  scope='session'/>
<jsp:useBean id='user' class='user.UserBean'  scope='session'/>

<%
ServletContext context = this.getServletContext();

String dbn = request.getParameter("dbn");
String dbid = request.getParameter("dbid");
String pid = request.getParameter("pid");
pid = createsub.standardisePid(pid);
String centerid = request.getParameter("centerid");
String modality = request.getParameter("modality");


String lineColour = createsub.getLineColour(dbn);

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
Vector<Vector> mainParameters = createsub.getMainParameters(tablenames, pid, centerid, connection,paramConn);

//Need a clause here to match modality with database table name
String pageTitle = "";
String[] subTablenames = null;
Vector<Vector> parameters = null;

String metabolomicsFilename = "";
String modid = "";

if(!modality.equals("metabolomics")){

//Retrieve data parameter information
subTablenames = new String[1];
subTablenames[0] = createsub.getSubTablename(modality,dbn);
parameters = createsub.getParameters(subTablenames,request,paramConn);

}else{

modid = request.getParameter("modid");       
parameters = createsub.setupParameters(modality, request,centerid,pid,modid);    
metabolomicsFilename = "metabolomics_" + centerid + "_" + pid + "_" + modid + ".pdf";
}

//Logging configuration
String log4jConfigFile = context.getInitParameter("log4j_property_file");
Logger logger = Logger.getLogger("rootLogger");
logger.setLevel(Level.DEBUG);
PropertyConfigurator.configure(log4jConfigFile);

String username = user.getUsername();
logger.debug("('" + username + "') - modality/create_confirm.jsp (modality = '" + modality + "')");


%>

<table width="100%" cellpadding="5" border="1">
    <tr>
        <td valign="top">


<h1><%=dbn%> Record <%=centerid%>-<%=pid%> Detail</h1>

<table border="1px" width="100%" cellpadding="5">
    <%
    String mainParameterHtml = createsub.getMainParameterHtml(mainParameters, lineColour,dbn);
    %>
    <%= mainParameterHtml %>
</table>

<!-- End of L panel -->

</td>
<!-- R panel -->
<td width="50%" valign="top">
<p>

<div align="center">

    <%
    if(pageTitle.equals("None")){
        %>
        <h2>No modality selected</h2>
        <%
        }else{
    %>

<h1><%=dbn%> Create <%=pageTitle%> Record</h1>

<p>
<form action="./jsp/modality/create/create_process.jsp?dbid=<%=dbid%>&dbn=<%=dbn%>&pid=<%=pid%>&centerid=<%=centerid%>&modality=<%=modality%>" method="POST">

<%
if(dbn.equals("Pheo") && (modality.equals("imaging") || modality.equals("surgery"))){
%>
<p><div id="pheo_tumor_warning">NOTE: tumor details must be filled in to correspond with previous PHPGL operations</div></p>
<%
}
%>
    
    <%
String lastPageConfirmHtml = createsub.getLastPageParamConfirmHtml(parameters, lineColour, request,dbn);
%>
<%= lastPageConfirmHtml %>

<%
if(modality.equals("metabolomics")){
       
%>
<input type="hidden" name="filename" value="<%=metabolomicsFilename%>"/>

<input type="hidden" name="dbid" value="<%=dbid%>"/>
<input type="hidden" name="dbn" value="<%=dbn%>"/>
<input type="hidden" name="pid" value="<%=pid%>"/>
<input type="hidden" name="centerid" value="<%=centerid%>"/>
<input type="hidden" name="modality" value="<%=modality%>"/>
<input type="hidden" name="modid" value="<%=modid%>"/>


<%
}
%>



<input type="submit" name="create_record" value="Confirm Details"/>
</form>
</p>

<%
if(modality.equals("metabolomics")){
%>
<p><iframe src="./metabolomics_upload/uploads/<%=metabolomicsFilename%>" width="500" height="375"></iframe></p>
<%
}
%>


<p>
<form action="./jsp/modality/create/create_view.jsp?dbid=<%=dbid%>&dbn=<%=dbn%>&pid=<%=pid%>&centerid=<%=centerid%>&modality=<%=modality%>" method="POST">

<%
String hiddenParamHtml = createsub.getLastPageParamHtml(parameters,dbn,request);
%> 
<%= hiddenParamHtml %>
    
    <input type="submit" name="create_return" value="Back"/>
</form>
</p>

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
    <p><a href="./jsp/dbhome.jsp?dbid=<%=dbid%>&dbn=<%=dbn%>&page=1"><%=dbn%> Home</a></p>
</div>
</td>
</tr>
</table>
</div>

</td>
</tr>
</table>
</p>

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

