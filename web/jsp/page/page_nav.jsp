<%@ page language="java" import="java.util.*,java.sql.*,java.text.Format,java.text.SimpleDateFormat" pageEncoding="ISO-8859-1"%>

<jsp:useBean id='user' class='user.UserBean'  scope='session'/>

<%
String forename = user.getForename();
boolean isSuperUser = user.getIsSuperUser();
String searchFilter = request.getParameter("search_filter");
if(searchFilter == null){
    searchFilter = "";
}

String id = request.getParameter("dbid");
String dbn = request.getParameter("dbn");
if(id == null) {
	%>
		<div class="tab-pane">
			<ul>
				<li>
					<a href="./jsp/home.jsp">Home</a>
				</li>
				<li>
					<a href="./jsp/dbhome.jsp?dbid=1&dbn=ACC&page=1">ACC</a>
				</li>
				<li>
					<a href="./jsp/dbhome.jsp?dbid=2&dbn=Pheo&page=1">Pheo</a>
				</li>
				<li>
					<a href="./jsp/dbhome.jsp?dbid=3&dbn=NAPACA&page=1">NAPACA</a>
				</li>
                                <li>
                                        <a href="./jsp/dbhome.jsp?dbid=4&dbn=APA&page=1">APA</a>                    
				</li>                                
                                <li>
					<form method="POST" action="./jsp/read/patient_direct.jsp">
                                            <input type="text" size="10" name="patient_search" onfocus="inform=true;" onblur="inform=false"/>
                                            <input type="submit" name="patient_search_button" value="Search"/>
                                        </form>
				</li>
			</ul>
                        
            <div align="right">Welcome,
                <%
                if(isSuperUser){
                %>
                <b>Anthony (logged in as <%=forename%>)</b>
                <%
                }else{
                %>
                <b><%=forename%></b>
                <%
                }
                %>
                | <a href="./jsp/admin/account.jsp">Account Details</a>
                | <a href="./jsp/logout.jsp">Sign Out</a>                  
            <div id="session_clock"></div>
            
            </div>
		</div>
        

	<% } else  {%>
		<div class="tab-pane">
			<ul>
				<li>
					<a href="./jsp/home.jsp">ENSAT Home</a>
				</li>
				<li>
					<a href="./jsp/dbhome.jsp?dbid=<%=id%>&dbn=<%=dbn %>&page=1"><%=dbn %> Home</a>
				</li>
				<li>
					<a href="./jsp/search/search_view.jsp?dbn=<%=dbn%>"><%=dbn %> Search</a>
				</li>
                                <li>
					<a href="./jsp/search/display_export.jsp">Export</a>
				</li>                                
                                <li>
					<form method="POST" action="./jsp/read/patient_direct.jsp">
                                            <input type="text" size="10" name="patient_search" onfocus="inform=true;" onblur="inform=false;"/>
                                            <input type="submit" name="patient_search_button" value="Search"/>
                                        </form>
				</li>
                                <li>
                                <form action="./jsp/read/search_filter.jsp?dbn=<%=dbn%>&dbid=<%=id%>&page=1" method="POST">
                                    <select name="search_filter">
                                        <option value="">[Select...]</option>
                                        <option <% if(searchFilter.equals("all")){%> selected <%}%> value="all">All</option>                                        
                                        <option <% if(searchFilter.equals("national")){%> selected <%}%> value="national">National</option>
                                        <option <% if(searchFilter.equals("local")){%> selected <%}%> value="local">Local</option>
                                    </select>
                                    <input type="submit" name="consent_filter_button" value="Filter"/>
                                </form>
                                </li>
			</ul>
            <div align="right">Welcome, 
                <%
                if(isSuperUser){
                %>
                <b>Anthony (logged in as <%=forename%>)</b>
                <%
                }else{
                %>
                <b><%=forename%></b>
                <%
                }
                %>
                 | <a href="./jsp/logout.jsp">Sign Out</a>
                 <div id="session_clock"></div>
            
            
            </div>
		</div>
	<%} %>
	<br />
	<br />
	<br />
