<%@ page language="java" import="java.util.*,java.sql.*,java.io.*,java.text.Format,java.text.SimpleDateFormat" pageEncoding="ISO-8859-1"%>

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

<h2>Processing export...</h2>

<p>Your export request is being processed. Please check back to the <a href="./jsp/search/export_results.jsp">results page</a> soon.</p>

<p><hr/></p>

<p><strong>Return to <a href="./jsp/search/display_export.jsp">Export function</a></strong></p>

<p><strong>Return to <a href="./jsp/home.jsp">ENSAT Home</a></strong></p>

<jsp:include page="/jsp/page/page_foot.jsp" />
</td> 
      </tr> 
    </table> 
    </td> 
    
</tr>
</table>

