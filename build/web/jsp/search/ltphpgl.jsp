<%@ page language="java" import="java.util.*,java.sql.*,java.io.*,java.text.Format,java.text.SimpleDateFormat,org.apache.log4j.*" pageEncoding="ISO-8859-1"%>

<jsp:include page="/jsp/page/check_credentials.jsp" />
<jsp:include page="/jsp/page/check_input.jsp" />
<jsp:include page="/jsp/page/page_head.jsp" />

<td colspan="2" width="81%" align="left" valign="top" background="/images/possbk.jpg"><table width="1200" border="0" cellspacing="0" cellpadding="10"> 
      <tr> 
        <td width="15" align="left" valign="top">&nbsp;</td> 
        <td align="left" valign="top"><!-- #BeginEditable "MainText" --> 

<jsp:include page="/jsp/page/page_nav.jsp" />

<jsp:useBean id='user' class='user.UserBean'  scope='session'/>
<jsp:useBean id='searchexport' class='search.SearchExport'  scope='session'/>

<%
ServletContext context = getServletContext() ;  
String username = user.getUsername();

//log4j
Logger logger = Logger.getLogger("rootLogger");
logger.setLevel(Level.DEBUG);
PropertyConfigurator.configure("/root/logs/log4j_ensat.properties");
logger.debug("('" + username + "') - ltphpgl.jsp");

%>
<h2>LTPHPGL numbers</h2>

<%
if(username.equals("astell@unimelb.edu.au")){
    //int studyNumbers = searchexport.getStudyNumbers("ltphpgl",context);
    Vector<Vector> ltphpglIds = searchexport.getLimitedStudyNumbers("ltphpgl",context);
%>
<p><a href="./jsp/search/search_export.jsp?mod=6&study=ltphpgl">Export these results</a></p>
<%
}
%>




<p><hr/></p>

<p><strong>Return to <a href="./jsp/home.jsp">ENSAT Home</a></strong></p>

<jsp:include page="/jsp/page/page_foot.jsp" />
</td> 
      </tr> 
    </table> 
    </td> 
    
</tr>
</table>

