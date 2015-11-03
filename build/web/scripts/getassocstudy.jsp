<%@ page language="java" import="java.util.*,java.sql.*,java.text.Format,java.text.SimpleDateFormat,org.apache.log4j.*" pageEncoding="ISO-8859-1"%>

<jsp:useBean id='connect' class='ConnectBean.ConnectBean' scope='session'/>

<%
ServletContext context = this.getServletContext();
Connection paramConn = connect.getParamConnection();
    
String studyNameIn = request.getParameter("study");
boolean noSubMenu = false;
if(studyNameIn == null || studyNameIn.equals("")){
    noSubMenu = true;    
}

//Logging configuration
String log4jConfigFile = context.getInitParameter("log4j_property_file");
Logger logger = Logger.getLogger("rootLogger");
logger.setLevel(Level.DEBUG);
PropertyConfigurator.configure(log4jConfigFile);

if(!noSubMenu){
String menuId = "";

String[] studyNames = {"PMT","FIRST-MAPPP","German Cushing Registry","German Conn Registry"};
String[] menuIds = {"51","52","53","61"};

boolean studyNameFound = false;
int studyCount = 0;
while(!studyNameFound && studyCount < studyNames.length){
    if(studyNameIn.equals(studyNames[studyCount])){
        studyNameFound = true;
        menuId = menuIds[studyCount];
    }else{
        studyCount++;
    }
}

String[] optionNames = null;
int optionCount = 0;

String sql = "SELECT * FROM MenuOption WHERE option_menu_id=?;";
PreparedStatement ps = paramConn.prepareStatement(sql);
ps.setString(1,menuId);

ResultSet rs = ps.executeQuery();

while(rs.next()){
    optionCount++;
}
optionNames = new String[optionCount];
rs.beforeFirst();
optionCount = 0;
while(rs.next()){
    optionNames[optionCount] = rs.getString(2);
    optionCount++;
}

rs.close();

%>
    
<select name="associated_study_phase_visit" onchange="study_selection_withphase('<%=studyNameIn%>',this.value)">
    <option value="">[Select...]</option>
        <%
        for(int i=0; i<optionCount; i++){
        %>
        <option value="<%=optionNames[i]%>"><%=optionNames[i]%></option>
        <%
        }
        %>
    </select>
<%
}
%>
