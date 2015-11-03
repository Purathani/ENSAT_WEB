<%@ page language="java" import="java.util.*,java.sql.*,java.text.Format,java.text.SimpleDateFormat" pageEncoding="ISO-8859-1"%>

<jsp:useBean id='connect' class='ConnectBean.ConnectBean' scope='session'/>

<%
Connection paramConn = connect.getParamConnection();

String paramNameIn = request.getParameter("param");
if(paramNameIn == null){
    paramNameIn = "";
}

String rangeResponse = "";

String sql = "SELECT lower_bound,upper_bound FROM Parameter_Range WHERE param_name=?;";

PreparedStatement ps = paramConn.prepareStatement(sql);
ps.setString(1,paramNameIn);
ResultSet rs = ps.executeQuery();

if(rs.next()){
    rangeResponse = rs.getString(1) + "|" + rs.getString(2);
}else{
    rangeResponse = "|";
}

rs.close();
%>
    
<%=rangeResponse%>