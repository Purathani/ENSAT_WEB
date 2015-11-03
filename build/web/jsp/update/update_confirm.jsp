<%@ page language="java" import="java.util.*,java.sql.*,org.apache.log4j.*" pageEncoding="ISO-8859-1"%>

<jsp:include page="/jsp/page/check_credentials.jsp" />
<jsp:include page="/jsp/page/check_input.jsp" />
<jsp:include page="/jsp/page/check_userdetail.jsp" />
<jsp:include page="/jsp/page/page_head.jsp" />

<jsp:useBean id='user' class='user.UserBean'  scope='session'/>
<jsp:useBean id='connect' class='ConnectBean.ConnectBean'  scope='session'/>
<jsp:useBean id='update' class='update_main.Update' scope='session'/>

<td colspan="2" width="81%" align="left" valign="top" background="/images/possbk.jpg"><table width="1200" border="0" cellspacing="0" cellpadding="10"> 
      <tr> 
        <td width="15" align="left" valign="top">&nbsp;</td> 
        <td align="left" valign="top"><!-- #BeginEditable "MainText" --> 

<jsp:include page="/jsp/page/page_nav.jsp" />

<%
ServletContext context = this.getServletContext();
Connection paramConn = connect.getParamConnection();

String dbn = request.getParameter("dbn");
String dbid = request.getParameter("dbid");
String pid = request.getParameter("pid");
pid = update.standardisePid(pid);
String centerid = request.getParameter("centerid");

String lineColour = update.getLineColour(dbn);
String[] tablenames = null;
if(dbn.equals("ACC")){    

    //Retrieve data parameter information
    tablenames = new String[3];
    tablenames[0] = "Identification";
    tablenames[1] = "ACC_DiagnosticProcedures";
    tablenames[2] = "ACC_TumorStaging";

}else if(dbn.equals("Pheo")){    
    
    //Retrieve data parameter information
    tablenames = new String[2];
    tablenames[0] = "Identification";
    tablenames[1] = "Pheo_PatientHistory";
    
}else if(dbn.equals("NAPACA")){    

    //Retrieve data parameter information
    tablenames = new String[2];
    tablenames[0] = "Identification";
    tablenames[1] = "NAPACA_DiagnosticProcedures";
        
}else if(dbn.equals("APA")){    

    //Retrieve data parameter information
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
logger.debug("('" + username + "') - update_confirm.jsp");

Vector<Vector> parameters = update.getParameters(tablenames, request, paramConn);
%>


<table width="100%">
    
    <tr>
        <td>

<h1>Update <%=dbn%> record - confirm</h1>

<form action="./jsp/update/update_process.jsp?dbid=<%=dbid%>&dbn=<%=dbn%>&pid=<%=pid%>&centerid=<%=centerid%>" method="POST">

<table border="1px" width="75%">    
    <%
        String lastPageConfirmHtml = update.getLastPageParamConfirmHtml(parameters, lineColour,dbn,request);
    %>
    <%= lastPageConfirmHtml %>
</table>

<input type="submit" name="update_record" value="Update Record"/>
</form>

<form action="./jsp/read/detail.jsp?dbid=<%=dbid%>&dbn=<%=dbn%>&pid=<%=pid%>&centerid=<%=centerid%>" method="POST">    
    
<%
String hiddenParamHtml = update.getLastPageParamHtml(parameters,request);
%> 
<%= hiddenParamHtml %>
    
    <input type="submit" name="create_return" value="Back"/>
</form>

        </td>
    </tr>
</table>

<jsp:include page="/jsp/page/page_foot.jsp" />
</td> 
      </tr> 
    </table> 
    </td> 
    
</tr>
</table>

