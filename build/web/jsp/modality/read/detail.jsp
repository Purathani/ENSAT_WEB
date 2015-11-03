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
String path = request.getContextPath();
String baseUrl = request.getScheme()+"://"+request.getServerName()+":"+request.getServerPort()+path+"/";

//Logging configuration
String log4jConfigFile = context.getInitParameter("log4j_property_file");
Logger logger = Logger.getLogger("rootLogger");
logger.setLevel(Level.DEBUG);
PropertyConfigurator.configure(log4jConfigFile);

String username = user.getUsername();
updatesub.setUsername(username);

String userCenter = user.getCenter(); 
String dbn = request.getParameter("dbn");
String dbid = request.getParameter("dbid");
String pid = request.getParameter("pid");
pid = updatesub.standardisePid(pid);
String centerid = request.getParameter("centerid");
String modality = request.getParameter("modality");
String modid = request.getParameter("modid");

logger.debug("('" + username + "') - modality/detail.jsp (modality = '" + modality + "')");

String lineColour = updatesub.getLineColour(dbn);

Connection connection = connect.getConnection();
Statement statement = connection.createStatement();
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

String metabolomicsFilename = "";
String[] subTablenames = null;
Vector<Vector> parameters = new Vector<Vector>();

subTablenames = new String[1];
subTablenames[0] = updatesub.getSubTablename(modality,dbn);
String subTableIdName = updatesub.getSubTableIdName(modality, dbn);

if(!modality.equals("metabolomics")){

//Retrieve data parameter information
parameters = updatesub.getParameters(subTablenames, pid, centerid, modid, subTableIdName, connection, paramConn);

int paramNum = parameters.size();


}else{

modid = request.getParameter("modid");       
parameters = updatesub.setupParameters(subTablenames, modality, request,centerid,pid,modid, subTableIdName, connection,dbn);  
metabolomicsFilename = "metabolomics_" + centerid + "_" + pid + "_" + modid + ".pdf";

}

//Retrieve associated menu information
Vector<Vector> menus = updatesub.getMenus(parameters,paramConn);
menus = updatesub.getDynamicMenus(parameters, menus, userCenter,connection,pid,centerid,dbn);

String javascriptValidationArray = updatesub.getJavascriptValidationArray(parameters,baseUrl);

if(modality.equals("biomaterial")){
    Vector<String> aliquotValues = updatesub.getAliquotValues(pid, centerid, modid, dbn, statement, subTablenames[0]);
    parameters.add(aliquotValues);
    Vector<String> freezerValues = updatesub.getFreezerValues(pid, centerid, modid, dbn, statement);
    parameters.add(freezerValues);
}

String dialogTitle1 = "Definition - absolute washout";
String dialogTitle2 = "Definition - relative washout";
String extraText1 = "100 * (HU_max - HU_10min)/(HU_max - HU_nativ)";
String extraText2 = "100 * (HU_max - HU_10min)/(HU_max)";

%>
<%--<%=javascriptValidationArray%>--%>

<div id="dialog1" title="<%=dialogTitle1%>">
  <p><%=extraText1%></p>
</div>

<div id="dialog2" title="<%=dialogTitle2%>">
  <p><%=extraText2%></p>
</div>


<h1><%=dbn%> Record <%=centerid%>-<%=pid%> Detail</h1>

<table border="1px" width="100%">

<!-- Main canvas table has two panels (L and R) -->
<tr>
<td width="50%" valign="top">

<!-- L panel -->
<table border="1px" width="100%" cellpadding="10">
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


<h2><%=dbn%> Update <%=pageTitle%> Record <%=modid%></h2>

<p><div id="update_subform"></div></p>

<form name="f1"  action="./jsp/modality/update/update_confirm.jsp?dbid=<%=dbid%>&dbn=<%=dbn%>&pid=<%=pid%>&centerid=<%=centerid%>&modality=<%=modality%>&modid=<%=modid%>" method="POST" onsubmit="return validateForm(this,'update_subform','<%=dbn%>','<%=centerid%>','<%=baseUrl%>');">

<%-- Biochemical form has it's own unit selection --%>
<%
if(dbn.equals("Pheo") && modality.equals("biochemical")){
    %>
    <p>
    <table border="1px">
        <tr>
            <td>Plasma units:</td>
            <td><select name="plasma_units" onblur="loadXMLDoc(this.value,this.name);">
                <option value="">[Select...]</option>
                <option value="pgml">pg/ml</option>
                <option selected value="ngL">ng/L</option>
                <option value="nmolL">nmol/L</option>
            </select></td>
        </tr>
        <tr>
            <td>Urinary units:</td>
            <td><select name="urinary_units" onblur="loadXMLDoc(this.value,this.name);">
                <option value="">[Select...]</option>
                <option value="ugday">&micro;g/day</option>
                <option selected value="mgday">mg/day</option>
                <option value="umolday">&micro;mol/day</option>
            </select></td>
        </tr>
    </table></p>
<%
}
%>

<%-- Surgery and Imaging have associated tumor details listed here --%>
<%
if(dbn.equals("Pheo") && (modality.equals("surgery") || modality.equals("imaging"))){
    %>
    <p>
    <table border="1px">
       <tr class="parameter-line-double-pheo">
            <td width="50%">Associated tumor details <input type="button" name="tumor_details" value="?" onclick="return clarification(this.name)"/>:</td><td>
                    <%
                    String tumorCheckLabel = "";
                    if(modality.equals("surgery")){
                        tumorCheckLabel = "Pathology";
                    }else{
                        tumorCheckLabel = "Imaging";
                    }
                    ResultSet rsTumorCheck = statement.executeQuery("SELECT diagnosis_method, pheo_tumor_details_id FROM Pheo_TumorDetails WHERE ensat_id=" + pid + " AND center_id='" + centerid + "';");
                    int tumorCount = 0;
                    while(rsTumorCheck.next()){
                        if(rsTumorCheck.getString(1).equals(tumorCheckLabel)){
                            out.print("<strong>Tumor " + rsTumorCheck.getString(2) + "</strong><br/>");
                        }
                        tumorCount++;
                    }
                    if(tumorCount == 0){
                        out.print("<div id=\"pheo_tumor_warning\">NOTE: tumor details must be filled in to correspond with method of diagnosis</div>");
                    }
                    rsTumorCheck.close();
                    %>
                </td>
            </tr>

    </table></p>
<%
}
%>

<%
if(modality.equals("biomaterial")){
%>
<div class='errorLabel'>
Please note the new freezer location options on the biomaterial pages. These relate to position information in biomaterial freezers only at selected ENSAT centers only and are not mandatory. We will be expanding these locations to include as many center patterns as possible within the ENSAT consortium.
Please contact the system administrator (astell@unimelb.edu.au) for any queries.</div>
<%
}
%>


<table border="1px">
    <%
    String parameterHtml = updatesub.getParameterHtml(parameters, menus, lineColour,dbn, subTablenames[0],connection,centerid,pid,modid,baseUrl);
    %>
    <%= parameterHtml %>
</table>

<%
if(modality.equals("metabolomics")){
%>
<p><iframe src="./metabolomics_upload/uploads/<%=metabolomicsFilename%>" width="500" height="375"></iframe></p>
<%
}
%>


<table border="1px" cellpadding="5">
    
</table>

<input type="submit" name="update_record" value="Update Details"/>
</form>

<p><a href="./jsp/modality/home.jsp?dbid=<%=dbid%>&dbn=<%=dbn%>&pid=<%=pid%>&centerid=<%=centerid%>&modality=<%=modality%>"/><strong>Back to <%=pageTitle%> summary</strong></a></p>

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

