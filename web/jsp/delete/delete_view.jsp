<%@ page language="java" import="java.util.*,java.sql.*,org.apache.log4j.*" pageEncoding="ISO-8859-1"%>

<jsp:include page="/jsp/page/check_credentials.jsp" />
<jsp:include page="/jsp/page/check_input.jsp" />
<jsp:include page="/jsp/page/check_userdetail.jsp" />
<jsp:include page="/jsp/page/page_head.jsp" />

<td colspan="2" width="81%" align="left" valign="top" background="/images/possbk.jpg"><table width="1200" border="0" cellspacing="0" cellpadding="10"> 
      <tr> 
        <td width="15" align="left" valign="top">&nbsp;</td> 
        <td align="left" valign="top"><!-- #BeginEditable "MainText" --> 


<jsp:include page="/jsp/page/page_nav.jsp" />

<jsp:useBean id='connect' class='ConnectBean.ConnectBean'  scope='session'/>
<jsp:useBean id='delete' class='delete_main.Delete' scope='session'/>
<jsp:useBean id='user' class='user.UserBean'  scope='session'/>

<%
ServletContext context = this.getServletContext();
Connection paramConn = connect.getParamConnection();

String username = user.getUsername();
delete.setUsername(username);

String pid = request.getParameter("pid");
pid = delete.standardisePid(pid);
String centerid = request.getParameter("centerid");
String dbn = request.getParameter("dbn"); 
String dbid = request.getParameter("dbid"); 
String lineColour = delete.getLineColour(dbn);

Connection connection = connect.getConnection();

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


logger.debug("('" + username + "') - delete_view.jsp");

Vector<Vector> parameters = delete.getParameters(tablenames, pid, centerid, connection , paramConn);
%>

<table width="100%" cellpadding="5">
    <tr>
        <td valign="top">


<h1>Delete <%=dbn%> record</h1>

<h3>Record <%=centerid%>-<%=pid%></h3>
<p><div id="deletewarningflag">NOTE: Are you sure you want to delete this record? Once deleted it cannot be recovered.</div></p>

<table border="1px" width="75%" cellpadding="10">
    <%
    String parameterHtml = delete.getParameterHtml(parameters, lineColour);
    %>
    <%= parameterHtml %>
</table>

<form action="./jsp/delete/delete_process.jsp" method="POST">
    <input type="hidden" name="dbn" value="<%=dbn%>"/>
    <input type="hidden" name="pid" value="<%=pid%>"/>
    <input type="hidden" name="centerid" value="<%=centerid%>"/>
    <input type="hidden" name="dbid" value="<%=dbid%>"/>
    <input type="submit" name="delete_record" value="Delete Record"/>
</form>

    <jsp:include page="/jsp/page/page_foot.jsp" />
</td> 
      </tr> 
    </table> 
    </td> 
    
</tr>
</table>


