<%@ page language="java" import="java.util.*,java.sql.*,org.apache.log4j.*,java.io.*,org.apache.poi.xssf.usermodel.*" pageEncoding="ISO-8859-1"%>

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
<jsp:useBean id='utilities' class='update_main.Utilities' scope='session'/>
<jsp:useBean id='user' class='user.UserBean'  scope='session'/>

<%    
    //Testing github
ServletContext context = this.getServletContext();    
Connection conn = connect.getConnection();

String userCenter = user.getCenter();
    
//Logging configuration
String log4jConfigFile = context.getInitParameter("log4j_property_file");
Logger logger = Logger.getLogger("rootLogger");
logger.setLevel(Level.DEBUG);
PropertyConfigurator.configure(log4jConfigFile);
        
String username = user.getUsername();
logger.debug("('" + username + "') - materials_used.jsp");

//Read in all the checked biomaterials that have been used up and set the flag in the table appropriately
String[] checkedAliquots = request.getParameterValues("aliquots_used");
String confirmUsedUpdateHtml = presentation.markAliquotsAsUsed(checkedAliquots, conn);

//Read in all the checked biomaterials that have been transferred and set the flag in the table appropriately
//[VxV of bio_id/center code is what we end up with in this method]
Enumeration inputs = request.getParameterNames();
//String testInput = request.getParameter("material_transfer_USBDGNV0824146");
//logger.debug("testInput " + testInput);
String confirmTransferredUpdateHtml = presentation.markAliquotsAsTransferred(request, inputs, conn);

%>

<p>The following aliquots have been marked as used:

<%=confirmUsedUpdateHtml%>

</p>

<p>The following aliquots have been marked as transferred:

<%=confirmTransferredUpdateHtml%>

</p>



<p><strong><a href="
              /jsp/biobank/freezer_inventory.jsp?centerid=<%=userCenter%>">Back to Freezer Inventory</a></strong></p>

<jsp:include page="/jsp/page/page_foot.jsp" />

</td> 
      </tr> 
    </table> 
    </td> 
    
</tr>

</table>


