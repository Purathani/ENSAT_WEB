<%@ page language="java" import="java.util.*,java.sql.*,org.apache.log4j.*" pageEncoding="ISO-8859-1"%>

<jsp:include page="/jsp/page/check_credentials.jsp" />
<jsp:include page="/jsp/page/check_input.jsp" />
<jsp:include page="/jsp/page/page_head.jsp" />

<td colspan="2" width="81%" align="left" valign="top" background="/images/possbk.jpg"><table width="1200" border="0" cellspacing="0" cellpadding="10"> 
      <tr> 
        <td width="15" align="left" valign="top">&nbsp;</td> 
        <td align="left" valign="top"><!-- #BeginEditable "MainText" --> 

<jsp:include page="/jsp/page/page_nav.jsp" />

<jsp:useBean id='connect' class='ConnectBean.ConnectBean' scope='session'/>
<jsp:useBean id='presentation' class='summaryinfo.SummaryInfo' scope='session'/>
<jsp:useBean id='user' class='user.UserBean'  scope='session'/>

<%    
ServletContext context = this.getServletContext();
Connection conn = connect.getConnection();

//Logging configuration
String log4jConfigFile = context.getInitParameter("log4j_property_file");
Logger logger = Logger.getLogger("rootLogger");
logger.setLevel(Level.DEBUG);
PropertyConfigurator.configure(log4jConfigFile);
        
String username = user.getUsername();
logger.debug("('" + username + "') - biomaterial_upload.jsp");

String centerid = request.getParameter("centerid");
if(centerid == null){
    centerid = "";
}

boolean freezerPresent = presentation.freezerCheck(conn, centerid);

%>

<h2>Biomaterial Localization Upload - <%=centerid%></h2>

<%
if(centerid.equals("")){
%>
<p><strong>No center selected</strong></p>
<%
}else if(!freezerPresent){        
%>
<p><strong>No freezers available</strong> - please contact the system administrator (astell@unimelb.edu.au) to add freezer information</p>
<%
}else{
%>

<p>
    Use this page to do a bulk upload of biomaterial samples to the registry. The only accepted format are .xls or .xlsx files in the format shown in <a href="./jsp/biobank/biomaterial_sample.xlsx">this example</a>.
</p>

<form enctype="multipart/form-data" action="./jsp/biobank/biomaterial_process.jsp?centerid=<%=centerid%>" method="POST">    

    <table border="1" cellpadding="5">
        <tr>
            <td>
                Tumor type:
            </td>
            <td>
    <select name="dbn">
        <option value="">[Select...]</option>
        <option value="ACC">ACC</option>
        <option value="Pheo">Pheo</option>
        <option value="NAPACA">NAPACA</option>
        <option value="APA">APA</option>
    </select>
            </td>
        </tr>
        <tr>
            <td colspan="2">
    <input type="hidden" name="MAX_FILE_SIZE" value="500"/>
    <input type="file" name="f1"/>
            </td>            
        </tr>        
        <tr>
            <td colspan="2">
                <input type="submit" name="upload_file" value="Upload File"/>    
            </td>
        </tr>
    </table>
</form>

<%
}
%>

<jsp:include page="/jsp/page/page_foot.jsp" />

</td> 
      </tr> 
    </table> 
    </td> 
    
</tr>

</table>


