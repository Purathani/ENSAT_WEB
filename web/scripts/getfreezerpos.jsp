<%@ page language="java" import="java.util.*,java.sql.*,java.text.Format,java.text.SimpleDateFormat" pageEncoding="ISO-8859-1"%>

<jsp:useBean id='connect' class='ConnectBean.ConnectBean'  scope='session'/>
<%    
ServletContext context = this.getServletContext();
summaryinfo.SummaryInfo presentation = new summaryinfo.SummaryInfo();
update_main.Utilities utilities = new update_main.Utilities();

String paramstr = request.getParameter("centerid");
if(paramstr == null){
    paramstr = "GYMU";
}

Connection conn = connect.getConnection();
String freezerStrOut = "1_1_1_1_1_1"; //DEFAULT VALUE

//========= Query the freezer capacities ===============
int[] capacities = utilities.getFreezerCapacities(paramstr, conn);

//========= Load in the freezer positions  ===============
Vector<Vector> freezerData = presentation.getAllFreezerData(paramstr, conn);


//========= Check for the next gap  ===============
freezerStrOut = utilities.checkForGap(freezerData, capacities);

if(freezerStrOut == null){
    freezerStrOut = "";    
}
if(freezerStrOut.trim().equals("")){
    freezerStrOut = "1_1_1_1_1_1";
}
%>
<%=freezerStrOut%>