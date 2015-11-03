<%@ page language="java" import="java.util.*,java.sql.*,org.apache.log4j.*" pageEncoding="ISO-8859-1"%>

<jsp:include page="/jsp/page/check_credentials.jsp" />
<%--<jsp:include page="/jsp/page/check_input.jsp" />--%>
<jsp:include page="/jsp/page/page_head.jsp" />

<td colspan="2" width="81%" align="left" valign="top" background="/images/possbk.jpg"><table width="1200" border="0" cellspacing="0" cellpadding="10"> 
      <tr> 
        <td width="15" align="left" valign="top">&nbsp;</td> 
        <td align="left" valign="top"><!-- #BeginEditable "MainText" --> 

<jsp:include page="/jsp/page/page_nav.jsp" />

<jsp:useBean id='connect' class='ConnectBean.ConnectBean'  scope='session'/>
<jsp:useBean id='transfer' class='update_main.Transfer' scope='session'/>
<jsp:useBean id='user' class='user.UserBean'  scope='session'/>

<%
ServletContext context = getServletContext();
String pid = request.getParameter("pid");
String centerid = request.getParameter("centerid");
String study = request.getParameter("study");
String lineColour = transfer.getLineColour("NAPACA");

    //Add leading zero's
    if(pid.length() == 1){
        pid = "000" + pid;
    }else if(pid.length() == 2){
        pid = "00" + pid;
    }else if(pid.length() == 3){
        pid = "0" + pid;
    }

Connection connection = connect.getConnection();
Connection paramConn = connect.getParamConnection();

//Retrieve data parameter information
String[] tablenames = new String[1];
tablenames[0] = "Identification";
Vector<Vector> parameters = transfer.getParameters(tablenames, pid, centerid, connection,paramConn);

//Logging configuration
String log4jConfigFile = context.getInitParameter("log4j_property_file");
Logger logger = Logger.getLogger("rootLogger");
logger.setLevel(Level.DEBUG);
PropertyConfigurator.configure(log4jConfigFile);

String username = user.getUsername();
logger.debug("('" + username + "') - transfer_registry.jsp");
logger.debug("('" + username + "') - Transferring patient " + centerid + "-" + pid + " to " + study + "...");

%>

<h1>Registry Transfer </h1>

<h3>Record <%=centerid%>-<%=pid%></h3>
<p>Are you sure you want to transfer this record to <%=study%>?</p>

<table border="1px" width="75%" cellpadding="10">
    <%
    String parameterHtml = transfer.getParameterHtml(parameters, lineColour);
    %>
    <%= parameterHtml %>
</table>

<form action="./jsp/read/transfer_registry_process.jsp" method="POST">
    
    <input type="hidden" name="pid" value="<%=pid%>"/>
    <input type="hidden" name="centerid" value="<%=centerid%>"/>
    <input type="hidden" name="study" value="<%=study%>"/>
    <input type="submit" name="transfer_record" value="Transfer Record"/>
</form>

    <jsp:include page="/jsp/page/page_foot.jsp" />
</td> 
      </tr> 
    </table> 
    </td> 
    
</tr>
</table>

