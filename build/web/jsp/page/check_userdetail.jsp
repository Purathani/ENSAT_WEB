<%@ page language="java" import="java.util.*,java.sql.*,java.text.Format,java.text.SimpleDateFormat" pageEncoding="ISO-8859-1"%>

<jsp:useBean id='user' class='user.UserBean'  scope='session'/>
<jsp:useBean id='connect' class='ConnectBean.ConnectBean'  scope='session'/>
<jsp:useBean id='security' class='security.Authz'  scope='session'/>

<%
ServletContext context = this.getServletContext();
String username = user.getUsername();

String pid = request.getParameter("pid");
String centerid = request.getParameter("centerid");
String dbn = request.getParameter("dbn");
String dbid = request.getParameter("dbid");

String host = context.getInitParameter("server_name");
String dbUsername = context.getInitParameter("username");
String dbPassword = context.getInitParameter("password");

Connection connection = connect.getConnection();
Connection secConn = connect.getSecConnection();
Statement statement = connection.createStatement();
String uploader = security.getRecordUploader(pid, centerid, statement);
boolean readOnly = true;

/**
* ESTABLISH AT THIS POINT WHETHER THE RECORD IS EDITABLE OR NOT
*/
security.modifyRecordEditable(username, uploader, pid, centerid, connection,secConn);
boolean recordEditable = security.getRecordEditable();        
readOnly = !recordEditable;        

if(readOnly){    
%>
<jsp:forward page="/jsp/read/readonly.jsp">
    <jsp:param name="dbid" value="<%=dbid%>"/>
    <jsp:param name="dbn" value="<%=dbn%>"/>
    <jsp:param name="pid" value="<%=pid%>"/>
    <jsp:param name="centerid" value="<%=centerid%>"/>
</jsp:forward>
<%
}
%>
