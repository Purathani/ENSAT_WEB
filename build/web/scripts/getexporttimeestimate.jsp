<%@ page language="java" import="java.util.*,java.sql.*,java.text.Format,java.text.SimpleDateFormat, search.SearchUtilities" pageEncoding="ISO-8859-1"%>

<jsp:useBean id='searchQuery' class='search.SearchUtilities'  scope='session'/>
<jsp:useBean id='connect' class='ConnectBean.ConnectBean' scope='session'/>

<%
Connection conn = connect.getConnection();    
    
String patientNumStr = request.getParameter("patientnumber");
String dbn = request.getParameter("dbn");
if(dbn == null){
    dbn = "";
}
String mod = request.getParameter("mod");
if(mod == null){
    mod = "";
}

int patientNum = 1;
try{
    patientNum = Integer.parseInt(patientNumStr);    
}catch(NumberFormatException nfe){
    patientNum = 1;
}

int paramNum = 1;
        
//Analyse the query for the number of parameters
        
//If mod=1 --> 4 then the parameter number is the total for the whole database
//Else if mod=5 or mod=6 then it depends on the query
if(mod.equals("1") || mod.equals("2") || mod.equals("3") || mod.equals("4")){
    paramNum = searchQuery.getTotalParamNum(dbn, conn);
}else{
    
    //HOW DO WE FEED THE NON-STANDARD QUERY IN HERE?
    String querySql = "";    
    paramNum = searchQuery.analyseQueryParams(querySql);
}
        
//Multiply the patient number by the parameter number
int timeMins = patientNum * paramNum;

%>

The query will take approximately <%=timeMins%> minutes to process.