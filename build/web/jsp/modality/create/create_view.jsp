
<%@ page language="java" import="java.util.*,java.sql.*,java.text.Format,java.text.SimpleDateFormat,org.apache.log4j.*" pageEncoding="ISO-8859-1"%>

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
String path = request.getContextPath();
String baseUrl = request.getScheme()+"://"+request.getServerName()+":"+request.getServerPort()+path+"/";

String dbn = request.getParameter("dbn");
String dbid = request.getParameter("dbid");
String pid = request.getParameter("pid");
pid = createsub.standardisePid(pid);
String centerid = request.getParameter("centerid"); 
String modality = request.getParameter("modality");

String lineColour = createsub.getLineColour(dbn);

Connection connection = connect.getConnection();
Connection paramConn = connect.getParamConnection();
Connection ccConn = connect.getCcConnection();

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
//Vector<Vector> mainParameters = createsub.getMainParameters(tablenames, pid, centerid, statement, context);
Vector<Vector> mainParameters = createsub.getMainParameters(tablenames, pid, centerid, connection, paramConn);

String pageTitle = createsub.getPageTitle(modality, dbn);

//Retrieve data parameter information
String[] subTablenames = new String[1];
subTablenames[0] = createsub.getSubTablename(modality,dbn);
Vector<Vector> parameters = createsub.getParameters(subTablenames,request,paramConn);

//Retrieve associated menu information
Vector<Vector> menus = createsub.getMenus(parameters,paramConn);
menus = createsub.getDynamicMenus(parameters, menus, "",ccConn,connection,pid,centerid,dbn);

//Specifically for EURINE-ACT text
//String eurineActInclusion = createsub.getParameterValue("eurine_act_inclusion", mainParameters);
String eurineActInclusion = createsub.getStudyInclusion("eurineact",pid,centerid,connection);

//Logging configuration
String log4jConfigFile = context.getInitParameter("log4j_property_file");
Logger logger = Logger.getLogger("rootLogger");
logger.setLevel(Level.DEBUG);
PropertyConfigurator.configure(log4jConfigFile);

String username = user.getUsername();
createsub.setUsername(username);
logger.debug("('" + username + "') - modality/create_view.jsp (modality = '" + modality + "')");

String javascriptValidationArray = createsub.getJavascriptValidationArray(parameters,baseUrl);

String dialogTitle1 = "Definition - absolute washout";
String dialogTitle2 = "Definition - relative washout";
String extraText1 = "100 * (HU_max - HU_10min)/(HU_max - HU_nativ)";
String extraText2 = "100 * (HU_max - HU_10min)/(HU_max)";

%>
<%=javascriptValidationArray%>

<div id="dialog1" title="<%=dialogTitle1%>">
  <p><%=extraText1%></p>
</div>

<div id="dialog2" title="<%=dialogTitle2%>">
  <p><%=extraText2%></p>
</div>


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

<div align="center">

    <%
    if(pageTitle.equals("None")){
    %>
        <h2>No modality selected</h2>
    <%
    }else if(modality.equals("metabolomics")){    
        
        String metabolomicsTablename = "" + dbn + "_Metabolomics";
        String metabolomicsId = "" + dbn.toLowerCase() + "_metabolomics_id";
        logger.debug("metabolomicsTablename: " + metabolomicsTablename);
        logger.debug("metabolomicsId " + metabolomicsId);
        
        String modid = "" + createsub.getNextId(metabolomicsTablename, metabolomicsId, pid, centerid, connection);        
    %>
    <h1>Create <%=pageTitle%> Form</h1>
    <jsp:include page="/scripts/fileupload.jsp">
        <jsp:param name="modid" value="<%=modid%>"/>        
    </jsp:include>
    <%
    }else{
    %>


<h1>Create <%=dbn %> <%=pageTitle%> Form</h1>

<%-- ADD IN DETAILS FOR EURINE-ACT INCLUSION HERE --%>
<%
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
%>


<p><div id="create_subform"></div></p>

<form name="f1" action="./jsp/modality/create/create_confirm.jsp?dbid=<%=dbid%>&dbn=<%=dbn%>&pid=<%=pid%>&centerid=<%=centerid%>&modality=<%=modality%>" method="POST" onsubmit="return validateForm(this,'create_subform','<%=dbn%>','<%=centerid%>','<%=baseUrl%>');">

<%-- Biochemical form has it's own unit selection --%>
<%
if(dbn.equals("Pheo") && modality.equals("biochemical")){
    %>
    <p><em>Please select units to input biochemical data.</em></p>
    <p><em>If no selection is made, the default units of ng/L for plasma and mg/day for urinary will be used.</em></p>
    <p>
    <table border="1px">
        <tr>
            <td>Plasma units:</td>
            <td><select name="plasma_units" onblur="loadXMLDoc(this.value,this.name);">
                <option value="">[Select...]</option>
                <option value="pgml">pg/ml</option>
                <option value="ngL">ng/L</option>
                <option value="nmolL">nmol/L</option>
            </select></td>
        </tr>
        <tr>
            <td>Urinary units:</td>
            <td><select name="urinary_units" onblur="loadXMLDoc(this.value,this.name);">
                <option value="">[Select...]</option>
                <option value="ugday">&micro;g/day</option>
                <option value="mgday">mg/day</option>
                <option value="umolday">&micro;mol/day</option>
            </select></td>
        </tr>
    </table></p>
<%
}else if(dbn.equals("APA") && modality.equals("biochemical")){
%>
    <p><em>Please select units to input biochemical data.</em></p>
    <!--<p><em>If no selection is made, the default units of ng/L for aldosterone, &micro;g/L for cortisol and pg/ml for renin  will be used.</em></p>-->
    <p>
    <table border="1px">
        <tr>
            <td>Aldosterone units:</td>
            <td><select name="aldosterone_units" onblur="loadXMLDoc(this.value,this.name);">
                <option value="">[Select...]</option>
                <option value="ngL">ng/L</option>                
                <option value="pmolL">pmol/L</option>
            </select></td>
        </tr>
        <tr>
            <td>Cortisol units:</td>
            <td><select name="cortisol_units" onblur="loadXMLDoc(this.value,this.name);">
                <option value="">[Select...]</option>
                <option value="ugL">&micro;g/L</option>                
                <option value="nmolL">nmol/L</option>
            </select></td>
        </tr>
        <tr>
            <td>Renin units:</td>
            <td><select name="renin_units" onblur="loadXMLDoc(this.value,this.name);">
                <option value="">[Select...]</option>
                <option value="pgml">pg/ml</option>
                <option value="muiL">mUI/L</option>                
            </select></td>
        </tr>
    </table></p>

<%
}
%>

<%-- Imaging and Surgery have warning about adding tumor details --%>
<%
if(dbn.equals("Pheo") && (modality.equals("imaging") || modality.equals("surgery"))){
%>
<p><div id="pheo_tumor_warning">NOTE: tumor details must be filled in to correspond with previous PHPGL operations</div></p>
<%
}
%>

<%--<%
if(modality.equals("biomaterial")){
%>
<div class='errorLabel'>
Please note the new freezer location options on the biomaterial pages. These relate to position information in biomaterial freezers only at selected ENSAT centers only and are not mandatory. We will be expanding these locations to include as many center patterns as possible within the ENSAT consortium.
Please contact the system administrator (astell@unimelb.edu.au) for any queries.</div>
<%
}
%>--%>


<table border="1px" cellpadding="5">
    <%
    String parameterHtml = createsub.getParameterHtml(parameters, menus, lineColour, dbn, connection, baseUrl);
    %>
    <%= parameterHtml %>
</table>

<input type="submit" name="create_record" value="Confirm Details"/>
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
    <p><a href="./jsp/dbhome.jsp?dbid=<%=dbid%>&dbn=<%=dbn%>&page=1"><%=dbn%> Home</a></p>
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

