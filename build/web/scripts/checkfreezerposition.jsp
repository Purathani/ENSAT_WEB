<%@ page language="java" import="java.util.*,java.sql.*,java.text.Format,java.text.SimpleDateFormat" pageEncoding="ISO-8859-1"%>

<jsp:useBean id='connect' class='ConnectBean.ConnectBean' scope='session'/>

<%    
Connection conn = connect.getConnection();

String dbn = request.getParameter("dbn");
String centerid = request.getParameter("centerid");
if(centerid == null){
    centerid = "";
}

String freezerInfo = request.getParameter("freezerinfo");
if(freezerInfo == null){
    freezerInfo = "";
}

//Expecting "[freezerNum]|[shelfNum]|[rackNum]|[boxNum]|[positionNum]"
StringTokenizer st = new StringTokenizer(freezerInfo,"|");
String freezerNum = "";
String shelfNum = "";
String rackNum = "";
String boxNum = "";
String positionNum = "";

while(st.hasMoreTokens()){
    freezerNum = st.nextToken();    
    shelfNum = st.nextToken();
    rackNum = st.nextToken();
    boxNum = st.nextToken();
    positionNum = st.nextToken();    
}

boolean positionOccupied = false;

String sql = "SELECT * FROM " + dbn + "_Biomaterial_Freezer_Information WHERE center_id=? AND freezer_number=? AND shelf_number=? AND rack_number=? AND box_number=? AND position_number=?;";

PreparedStatement ps = conn.prepareStatement(sql);
ps.setString(1,centerid);
ps.setString(2,freezerNum);
ps.setString(3,shelfNum);
ps.setString(4,rackNum);
ps.setString(5,boxNum);
ps.setString(6,positionNum);

ResultSet rs = ps.executeQuery();
positionOccupied = rs.next();
rs.close();
%>
    
<%=positionOccupied%>