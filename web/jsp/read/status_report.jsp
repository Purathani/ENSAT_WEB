<%@ page language="java" import="java.util.*,java.sql.*,java.text.*,org.apache.log4j.*" pageEncoding="ISO-8859-1"%>

<jsp:include page="/jsp/page/check_credentials.jsp" />
<jsp:include page="/jsp/page/check_userdetail.jsp" />
<jsp:include page="/jsp/page/page_head.jsp" />

<td colspan="2" width="81%" align="left" valign="top" background="/images/possbk.jpg"><table width="1200" border="0" cellspacing="0" cellpadding="10"> 
      <tr> 
        <td width="15" align="left" valign="top">&nbsp;</td> 
        <td align="left" valign="top"><!-- #BeginEditable "MainText" --> 

<jsp:include page="/jsp/page/page_nav.jsp" />

<jsp:useBean id='user' class='user.UserBean'  scope='session'/>
<jsp:useBean id='connect' class='ConnectBean.ConnectBean'  scope='session'/>
<jsp:useBean id='utilities' class='update_main.Utilities' scope='session'/>

<%
ServletContext context = this.getServletContext();    
    
String dbn = request.getParameter("dbn");
String dbid = request.getParameter("dbid");
String pid = request.getParameter("pid");
String centerid = request.getParameter("centerid");

   //Add leading zero's
    if(pid.length() == 1){
        pid = "000" + pid;
    }else if(pid.length() == 2){
        pid = "00" + pid;
    }else if(pid.length() == 3){
        pid = "0" + pid;
    }

//Logging configuration
String log4jConfigFile = context.getInitParameter("log4j_property_file");
Logger logger = Logger.getLogger("rootLogger");
logger.setLevel(Level.DEBUG);
PropertyConfigurator.configure(log4jConfigFile);

String username = user.getUsername();
logger.debug("('" + username + "') - status_report.jsp (Ensat ID = '" + centerid + "-" + pid + "')");


%>

<h1><%=dbn%> Patient Status Report</h1>

<h3>Record <%=centerid%>-<%=pid%></h3>

<%

String lineColour = utilities.getLineColour(dbn);

Connection conn = connect.getConnection();

String ensatStage = utilities.getEnsatStage(centerid,pid,conn);
String dateDiagnosis = utilities.getDateDiagnosis(centerid,pid,conn);
String dateDiagnosisYear = utilities.getDateDiagnosisYear(dateDiagnosis);

int diagnosisAge = 0;
String followupDate = "";
String lostToFollowUp = "";
boolean patientDead = false;
String patientAliveStr = "";
long dateInterval = 0;
long dateIntervalDays = 0;
long dateIntervalYears = 0;

boolean r0 = false;
String dateResectionStr = "";
long dateResection = 0;
boolean completeRemission = false;
String recurrenceEvidenceStr = "";
long recurrenceDate = 0;
long diseaseFreeSurvival = 0;
long diseaseFreeSurvivalYears = 0;

if(!dateDiagnosis.equals("")){

diagnosisAge = utilities.getDiagnosisAge(centerid, pid, conn, dateDiagnosisYear);

ResultSet statusCheck = utilities.getPatientStatusCheck(centerid, pid, conn);
followupDate = utilities.getFollowupDate(statusCheck);
lostToFollowUp = utilities.getLostToFollowUp(statusCheck);
patientAliveStr = utilities.getPatientAliveStr(statusCheck);

//Disease-free survival, only for patients with resection, R0: dateOfFirstFollowUp - dateFirstDiagnosis
//History of recurrence, only for patients with resection, R0: evidence in follow-up
ResultSet resectionSet = utilities.getResectionSet(centerid, pid, conn);
r0 = utilities.getResectionStatus(resectionSet);
dateResection = utilities.getResectionDate(resectionSet);
dateResectionStr = utilities.getResectionDateStr(resectionSet);

//Overall survival
//If patient is alive, calculate dateLastFollowUp - dateFirstDiagnosis; if dead, calculate dateDeath - dateFirstDiagnosis (both are functionally the same when extracted from the database)
dateInterval = utilities.getDateInterval(followupDate,patientAliveStr,dateDiagnosis, dateResectionStr);
dateIntervalDays = utilities.getDateInDays(dateInterval);
dateIntervalYears = utilities.getDateInYears(dateIntervalDays);

if(r0){    
    
    ResultSet diseaseStatusCheck = utilities.getDiseaseStatusCheck(centerid,pid,dateResectionStr, conn);
    recurrenceEvidenceStr = utilities.getRecurrenceEvidenceStr(diseaseStatusCheck);
    
    recurrenceDate = utilities.getRecurrenceDate(diseaseStatusCheck);
    
    boolean recurrenceFound = recurrenceEvidenceStr.equals("Yes");
    
    diseaseFreeSurvival = utilities.getDiseaseFreeSurvival(dateResection,followupDate,recurrenceDate,recurrenceFound);    
    diseaseFreeSurvivalYears = utilities.getDateInYears(diseaseFreeSurvival);
    completeRemission = utilities.getCompleteRemission(diseaseStatusCheck);    
}
}

%>

<table border="1px" width="100%" cellpadding="5">

<tr>
    <td>

<table width="100%">
    <td width="50%">ENSAT Stage:</td><td><strong><%= ensatStage %></strong></td>
    <tr <%=lineColour%>>
        <td width="50%">Age at first diagnosis:</td>
        <td>
            <strong>
                <%
                if(diagnosisAge == -1){
                    %>
                    --
                    <%
                    }else{
                    %>
                <%= "" + diagnosisAge %>
                <%
                }
                %>
            </strong>
        </td>
    </tr>
    <tr><td width="50%">Last follow-up <% if(patientDead){ %> (date of death)<%}%>:</td><td><strong><%= followupDate %></strong></td></tr>
        <tr <%=lineColour%>><td width="50%">Overall survival:</td><td><strong><%= "" + dateIntervalDays %> days (<%= "" + dateIntervalYears %> years)</strong></td></tr>
        <tr><td width="50%">Lost to follow-up:</td><td><strong><%= lostToFollowUp %></strong></td></tr>
    <tr>
        <td width="50%">Patient alive:</td><td><strong><%= patientAliveStr %></strong></td></tr>
    <%
    if(completeRemission){
    %>    
    <tr <%=lineColour%>>
        <td width="50%">Patient has complete remission</td></tr>
    <%
    }
    %>
</table>

    </td>
</tr>
<tr>
    <td>
        <%if(r0){%>        
        <table width="100%">
            <tr <%= lineColour %>><td width="50%">Resection status:</td><td><strong>R0</strong></td></tr>
            <tr><td width="50%">Disease-free survival (time to first recurrence):</td><td><strong><%= "" + diseaseFreeSurvival %> days</strong></td></tr>
            <tr <%=lineColour%>><td width="50%">History of recurrence:</td><td><strong><%= "" + recurrenceEvidenceStr %></strong></td></tr>
        </table>
            <%}else{%>
        <table width="100%">
            <tr>
                <td colspan="2">Patient does not have R0 resection status</td></tr> 
            </tr>
        </table>
            <%}%>
    </td>
</tr>

</table>
            
<table cellpadding="10" width="100%">
    <tr>
        <td colspan="2">
            <div align="center">
            <hr/>
            </div>
        </td>
    </tr>
<tr>
<td>
<div align="center">
    <p><a href="./jsp/read/detail.jsp?dbid=<%=dbid%>&dbn=<%=dbn%>&pid=<%=pid%>&centerid=<%=centerid%>">Patient <%=centerid%>-<%=pid%></a></p>
</div>
</td>
<td>
<div align="center">
    <p><a href="./jsp/dbhome.jsp?dbid=<%=dbid%>&dbn=<%=dbn%>&page=1"><%=dbn%> Home</a></p>
</div>
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

