<%@ page language="java" import="java.util.*,java.sql.*,java.text.Format,java.text.SimpleDateFormat" pageEncoding="ISO-8859-1"%>

<jsp:useBean id='connect' class='ConnectBean.ConnectBean' scope='session'/>

<%
Connection ccConn = connect.getCcConnection();    
    
String paramstr = request.getParameter("centerid");
if(paramstr == null){
    paramstr = "";
}

String invName = "";
String invEmail = "";

String sql = "SELECT DISTINCT investigator_name,investigator_email FROM Center_Callout WHERE center_id=?;";
PreparedStatement ps = ccConn.prepareStatement(sql);
ps.setString(1, paramstr);
ResultSet rs = ps.executeQuery();

if(rs.next()){
    invName = rs.getString(1);
    invEmail = rs.getString(2);
}

boolean isNetwork = paramstr.equals("NLDAN");

rs.close();

%>

<table width='100%' cellpadding='5'>
    <tr><td width='50%'>Referral doctor:</td><td><strong><%=invName%></strong><input type='hidden' name='local_investigator' value='<%=invName%>'/></td></tr>
    <tr><td width='50%'>Email:</td><td><strong><%=invEmail%></strong><input type='hidden' name='investigator_email' value='<%=invEmail%>'/></td></tr>
    <%
    if(isNetwork){
    %>
    <tr><td width='50%'>Center name:</td><td><input type='text' size='30' name='network_center_name' onfocus="inform=true;" onblur="inform=false;"/></td></tr>
    <%
    }
    %>
</table>