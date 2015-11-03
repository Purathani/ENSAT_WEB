<%@ page language="java" import="java.util.*,java.sql.*" pageEncoding="ISO-8859-1"%>

<jsp:useBean id='connect' class='ConnectBean.ConnectBean'  scope='session'/>
<jsp:useBean id='delete' class='delete_main.Delete' scope='session'/>

<%
String dbn = request.getParameter("dbn");
String dbid = request.getParameter("dbid");
String pid = request.getParameter("pid");
pid = delete.standardisePid(pid);
String centerid = request.getParameter("centerid");

Vector<Vector> subTables = delete.compileSubTableList(dbn);
Connection connection = connect.getConnection();

delete.deleteSubTables(subTables, connection, pid, centerid,dbn);
delete.deleteMainTables(connection, pid, centerid, dbn);
%>

<jsp:forward page="/jsp/delete/delete_result.jsp">
    <jsp:param name="dbid" value="<%=dbid%>"/>
    <jsp:param name="dbn" value="<%=dbn%>"/>
</jsp:forward>
             
             