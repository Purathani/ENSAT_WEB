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

//Logging configuration
String log4jConfigFile = context.getInitParameter("log4j_property_file");
Logger logger = Logger.getLogger("rootLogger");
logger.setLevel(Level.DEBUG);
PropertyConfigurator.configure(log4jConfigFile);

String username = user.getUsername();
logger.debug("('" + username + "') - modality/update_result.jsp (modality = '" + modality + "')");


%>


<h1><%=dbn%> Record <%=centerid%>-<%=pid%> Detail</h1>

<table border="1px" width="100%">

<!-- Main canvas table has two panels (L and R) -->
<tr>
<td width="50%">

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


<h1>Record has been successfully updated</h1>

<h3>Return to <a href="./jsp/read/detail.jsp?dbid=<%=dbid%>&dbn=<%=dbn%>&pid=<%=pid%>&centerid=<%=centerid%>">Patient <%=centerid%>-<%=pid%> Detail</a></h3>
<h3>Return to <a href="./jsp/modality/home.jsp?dbid=<%=dbid%>&dbn=<%=dbn%>&pid=<%=pid%>&centerid=<%=centerid%>&modality=<%=modality%>"><%=dbn%> <%=pageTitle%> Home</a></h3>

<h3><a href="./jsp/modality/create/create_view.jsp?dbid=<%=dbid%>&dbn=<%=dbn%>&pid=<%=pid%>&centerid=<%=centerid%>&modality=<%=modality%>">Create New <%=pageTitle%> Record</a></h3>
<%
if(modality.equals("biomaterial")){
    %>
<h3><a href="./jsp/modality/read/print_label.jsp?dbid=<%=dbid%>&dbn=<%=dbn%>&pid=<%=pid%>&centerid=<%=centerid%>&modality=<%=modality%>&modid=<%=modid%>">Print Labels <%=centerid%>-<%=pid%> (<%=modid%>)</a></h3>
<%
}

} //End of modality selection clause
%>

</div>
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

