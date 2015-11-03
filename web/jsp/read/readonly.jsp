<%@ page language="java" import="java.util.*,java.sql.*,org.apache.log4j.*" pageEncoding="ISO-8859-1"%>

<jsp:useBean id='user' class='user.UserBean'  scope='session'/>

<jsp:include page="/jsp/page/check_credentials.jsp" />
<jsp:include page="/jsp/page/check_input.jsp" />
<jsp:include page="/jsp/page/page_head.jsp" />

<td colspan="2" width="81%" align="left" valign="top" background="/images/possbk.jpg"><table width="1200" border="0" cellspacing="0" cellpadding="10"> 
      <tr> 
        <td width="15" align="left" valign="top">&nbsp;</td> 
        <td align="left" valign="top"><!-- #BeginEditable "MainText" --> 

<jsp:include page="/jsp/page/page_nav.jsp" />

<jsp:useBean id='connect' class='ConnectBean.ConnectBean'  scope='session'/>
<jsp:useBean id='readonly' class='update_main.ReadOnly' scope='session'/>

<% 
ServletContext context = this.getServletContext();

String dbn = request.getParameter("dbn");
String dbid = request.getParameter("dbid");
String pid = request.getParameter("pid");
String centerid = request.getParameter("centerid");
String lineColour = readonly.getLineColour(dbn);

    //Add leading zero's
    if(pid.length() == 1){
        pid = "000" + pid;
    }else if(pid.length() == 2){
        pid = "00" + pid;
    }else if(pid.length() == 3){
        pid = "0" + pid;
    }

Connection conn = connect.getConnection();
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

//Logging configuration
String log4jConfigFile = context.getInitParameter("log4j_property_file");
Logger logger = Logger.getLogger("rootLogger");
logger.setLevel(Level.DEBUG);
PropertyConfigurator.configure(log4jConfigFile);

String username = user.getUsername();
logger.debug("('" + username + "') - readonly.jsp (Ensat ID = '" + centerid + "-" + pid + "')");

Vector<Vector> parameters = readonly.getParameters(tablenames, pid, centerid, conn, paramConn);

String consentLevel = readonly.getParameterValue("consent_obtained", "Identification", conn, centerid, pid);
if(consentLevel == null){
    consentLevel = "";
}
boolean ensatConsent = consentLevel.equals("European ENSAT Partners") || consentLevel.equals("International Collaborators");

%>

<table width="100%" cellpadding="5">
    <tr>
        <td valign="top">


<h1><%=dbn%> Detail</h1>

<h3>Record <%=centerid%>-<%=pid%> (Read Only)</h3>

<p><a href="./jsp/dbhome.jsp?dbid=<%=dbid%>&dbn=<%=dbn%>">Back to <%=dbn%> Home</a></p>

<table border="1px" width="100%" cellpadding="5">
    <%
    String parameterHtml = readonly.getParameterHtml(parameters, lineColour);
    %>
    <%= parameterHtml %>
</table>

<!-- End of L panel -->

</td>
<!-- R panel -->
<td width="50%" valign="top">

<%-- Beginning of ENSAT partners section --%>    
<%
if(!ensatConsent){
%>
    
<table cellpadding="10" width="100%">

<tr>
<td>
<div align="left">
    <h1>Associated Record Information</h1>

    <%
    Vector<Vector> subTables = readonly.compileSubTableList(dbn);    
    %>
    <table width="100%" cellpadding="10">
    <%        
        String assocTableListHtml = readonly.getAssocTableHtml(subTables,dbid,dbn,centerid,pid);
    %>  
    <%= assocTableListHtml %>
    </table>
</div>

<hr/>

<h1>Summary of Associated Record Information</h1>

<table border="1px" cellpadding="5" width="100%">
    
    <%        
        String assocInfoListHtml = readonly.getAssocInfoHtml(dbid,dbn,centerid,pid,conn);
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
    <p><a href="./jsp/dbhome.jsp?dbid=<%=dbid%>&dbn=<%=dbn%>"><%=dbn%> Home</a></p>
</div>
</td>
</tr>
</table>

<%
}
%>  
<%-- end of ENSAT partners clause --%>

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