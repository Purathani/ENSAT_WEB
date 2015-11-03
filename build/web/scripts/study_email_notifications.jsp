<%@ page language="java" import="java.util.*,java.sql.*,java.text.Format,java.text.SimpleDateFormat" pageEncoding="ISO-8859-1"%>

<jsp:useBean id='notifications' class='MailBean.Notifications' scope='session'/>
<jsp:useBean id='connect' class='ConnectBean.ConnectBean'  scope='session'/>

<%
//Set up the database connection objects
Connection connection = connect.getConnection();

//Check which study is being asked for (will start with EURINE-ACT)
String studyNameIn = request.getParameter("study");

//TEST VALUE - WILL BECOME MORE GENERIC LATER
studyNameIn = "eurineact";

//Retrieve all the patient IDs associated with this study
Vector<Vector> patients = notifications.getPatientIDs(studyNameIn, connection);

//Remove all the patients in the above set that have record_date < 12 weeks
//int timeIntervalWeeks = 12;
//patients = notifications.removeBelowTimeInterval(patients, timeIntervalWeeks);

//Check that the conditions of the study are being met [number of forms vs weeks]
//Remove those that are meeting them
patients = notifications.checkStudyConditions(studyNameIn,patients,connection);

//Check the notify flags
//Remove those that don't need emailing
//patients = notifications.checkNotifyFlags(patients,connection);

//Send emails to those that are left 
//(one to each investigator listing those that are left - and changing the notify_flags appropriately)
//String result = notifications.sendInvestigatorEmails(patients);

%>
<p>Patients: <%=patients.size()%></p>