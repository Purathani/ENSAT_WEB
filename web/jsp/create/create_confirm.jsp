<%@ page language="java" import="java.util.*,java.sql.*,java.text.Format,java.text.SimpleDateFormat,org.apache.log4j.*" pageEncoding="ISO-8859-1"%>

<jsp:useBean id='user' class='user.UserBean'  scope='session'/>
<jsp:useBean id='connect' class='ConnectBean.ConnectBean'  scope='session'/>
<jsp:useBean id='create' class='create_main.Create' scope='session'/>

<jsp:include page="/jsp/page/check_credentials.jsp" />
<jsp:include page="/jsp/page/check_input.jsp" />
<jsp:include page="/jsp/page/page_head.jsp" />

<td colspan="2" width="81%" align="left" valign="top" background="/images/possbk.jpg"><table width="1200" border="0" cellspacing="0" cellpadding="10"> 
      <tr> 
        <td width="15" align="left" valign="top">&nbsp;</td> 
        <td align="left" valign="top"><!-- #BeginEditable "MainText" --> 


<jsp:include page="/jsp/page/page_nav.jsp" />

<%
ServletContext context = getServletContext();
Connection paramConn = connect.getParamConnection();

String dbn = request.getParameter("dbn");
String dbid = request.getParameter("dbid");

String lineColour = create.getLineColour(dbn);
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
logger.debug("('" + username + "') - create_confirm.jsp");

Vector<Vector> parameters = create.getParameters(tablenames, request, paramConn);
%>

<h1>Create <%=dbn%> Record - confirm</h1>

<form action="./jsp/create/create_process.jsp?dbid=1&dbn=<%=dbn%>" method="POST">

<%
String lastPageConfirmHtml = create.getLastPageParamConfirmHtml(parameters, lineColour, request, dbn);
%>
<%= lastPageConfirmHtml %>

<input type="submit" name="create_record" value="Create Record"/>
</form>

<form action="./jsp/create/create_view_2.jsp?dbid=<%=dbid%>&dbn=<%=dbn%>" method="POST">    
    
<%
String hiddenParamHtml = create.getLastPageParamHtml(parameters,dbn, request);
%> 
<%= hiddenParamHtml %>
    
    <input type="submit" name="create_return" value="Back"/>
</form>

    <jsp:include page="/jsp/page/page_foot.jsp" />
        </td> 
      </tr> 
    </table> 
    </td> 
    
</tr>
</table>

