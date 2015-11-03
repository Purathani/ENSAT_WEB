<%@ page language="java" import="java.util.*,java.sql.*,org.apache.log4j.*" pageEncoding="ISO-8859-1"%>

<jsp:useBean id='user' class='user.UserBean' scope='session'/>
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
String path = request.getContextPath();
String baseUrl = request.getScheme()+"://"+request.getServerName()+":"+request.getServerPort()+path+"/";
Connection conn = connect.getConnection();
Connection paramConn = connect.getParamConnection();

String dbn = request.getParameter("dbn"); 
String dbid = request.getParameter("dbid");

String lineColour = create.getLineColour(dbn);

//Retrieve data parameter information from previous page
String[] tablenames = new String[1];
tablenames[0] = "Identification";
Vector<Vector> lastPageParameters = create.getParameters(tablenames,request,paramConn);

String tsParameterHtml = "";
Vector<Vector> tsParameters = null;
if(dbn.equals("ACC")){    
        
    //Parameters and menus for tumor staging - extra for ACC
    tablenames[0] = "ACC_TumorStaging";
    tsParameters = create.getParameters(tablenames,request,paramConn);
    Vector<Vector> tsMenus = create.getMenus(tsParameters,paramConn);
    tsParameterHtml = create.getParameterHtml(tsParameters, tsMenus, lineColour,dbn,conn,baseUrl);
    
    tablenames[0] = "ACC_DiagnosticProcedures";
%>
    
<%
}else if(dbn.equals("Pheo")){    
    tablenames[0] = "Pheo_PatientHistory";
}else if(dbn.equals("NAPACA")){    
    tablenames[0] = "NAPACA_DiagnosticProcedures";
}else if(dbn.equals("APA")){    
    tablenames[0] = "APA_PatientHistory";
}

//Logging configuration
String log4jConfigFile = context.getInitParameter("log4j_property_file");
Logger logger = Logger.getLogger("rootLogger");
logger.setLevel(Level.DEBUG);
PropertyConfigurator.configure(log4jConfigFile);

String username = user.getUsername();
logger.debug("('" + username + "') - create_view_2.jsp");

Vector<Vector> parameters = create.getParameters(tablenames,request,paramConn);
Vector<Vector> menus = create.getMenus(parameters,paramConn);

String javascriptValidationArray = "";
if(dbn.equals("ACC")){
    Vector<Vector> validationParameters = new Vector<Vector>();        
    int parametersSize = parameters.size();
    for(int i=0; i < parametersSize; i++){
        validationParameters.add(parameters.get(i));        
    }
    int tsParametersSize = tsParameters.size();
    for(int i=0; i < tsParametersSize; i++){
        validationParameters.add(tsParameters.get(i));        
    }
    javascriptValidationArray = create.getJavascriptValidationArray(validationParameters,baseUrl);    
}else{
    javascriptValidationArray = create.getJavascriptValidationArray(parameters,baseUrl);    
}
%>
<%=javascriptValidationArray%>

<h1>Create <%=dbn%> Record (#2)</h1>

<p><div id="create"></div></p>

<form action="./jsp/create/create_confirm.jsp?dbid=<%=dbid%>&dbn=<%=dbn%>" method="POST" onsubmit="return validateForm(this,'create','<%=dbn%>','','<%=baseUrl%>');">

    <table>
        <tr>
            <td>
    <p>
    <div id="minimuminputflag">
        The input parameters on this page are optional and can be filled in later if necessary.
    </div>
    </p>
            </td>
        </tr>
    </table>
        
<%
String lastPageParamHtml = create.getLastPageParamHtml(lastPageParameters,dbn,request);
%> 
<%= lastPageParamHtml %>

<h2>
<%
if(dbn.equals("NAPACA")){
    out.print("Diagnostic Procedures (at time of primary diagnosis)");
}else if(dbn.equals("ACC")){
    out.print("Diagnostic Procedures");
}else if(dbn.equals("Pheo") || dbn.equals("APA")){
    out.print("Patient History");
}
%>
</h2>

<table border="1px" width="75%" cellpadding="10">
    <%
    String parameterHtml = create.getParameterHtml(parameters, menus, lineColour,dbn,conn,baseUrl);
    %>
    <%= parameterHtml %>
</table>

<%
if(dbn.equals("ACC")){
%>
<h2>Tumor Staging (at time of primary diagnosis)</h2>

<table border="1px" width="75%" cellpadding="10">
    <%= tsParameterHtml %>
</table>
<%
}
%>


<input type="submit" name="create_record" value="Confirm Details"/>
</form>

<form action="./jsp/create/create_view.jsp?dbid=1&dbn=<%=dbn%>&backid=1" method="POST" onsubmit="return validate_form(this,'create');">

<%= lastPageParamHtml %>
<input type="submit" name="create_record" value="Back"/>    
</form>

<jsp:include page="/jsp/page/page_foot.jsp" />
</td> 
      </tr> 
    </table> 
    </td> 
    
</tr>
</table>


