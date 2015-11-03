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
<jsp:useBean id='connect' class='ConnectBean.ConnectBean'  scope='session'/>
<jsp:useBean id='searchexport' class='search.SearchExport'  scope='session'/>

<%
ServletContext context = getServletContext() ;  
Connection conn = connect.getConnection();
    
String username = user.getUsername();
String userCenter = user.getCenter();
String userCountry = user.getCountry();

//Logging configuration
String log4jConfigFile = context.getInitParameter("log4j_property_file");
Logger logger = Logger.getLogger("rootLogger");
logger.setLevel(Level.DEBUG);
PropertyConfigurator.configure(log4jConfigFile);

logger.debug("('" + username + "') - display_export.jsp");

%>
<h2>ENSAT Export Function</h2>

<p>Use this feature to export to spreadsheet format (.xlsx or .csv) patient information in the ENSAT database.</p>
<p>NOTE: depending on your security privileges, you may not have access to every record in the query that you run. To run a new individual query, please use the <a href="/jsp/search/search_view.jsp?dbn=ACC">search function</a> to compile and run it. 
    If you think you are missing access to certain records or would like to submit a new stored procedure please contact the site administrator (astell@unimelb.edu.au)</p>

<p>You can view all of your previous exports <a href="./jsp/search/export_results.jsp">here</a>.

<hr/>

<h2>Export</h2>

    <p>All records belonging to <a href="/jsp/search/search_export.jsp?mod=1">you</a> (username: <strong><%=username%></strong>)</p>

<%
if(username.equals("astell@unimelb.edu.au") || username.equals("felix.beuschlein@med.uni-muenchen.de") || username.equals("fassnacht_m@ukw.de") || username.equals("rsinnott@unimelb.edu.au") || username.equals("stephan.gloeckner@uniklinikum-dresden.de") || username.equals("rossella.libe@cch.aphp.fr ")){
%>
<hr/>  
    
    <p>All records belonging to <a href="/jsp/search/search_export.jsp?mod=2">your center</a> (center: <strong><%=userCenter%></strong>)</p>
    
    <p>All records belonging to <a href="/jsp/search/search_export.jsp?mod=3">your country</a> (country: <strong><%=userCountry%></strong>)</p>
    
    <p>    
        All records belonging to a particular tumor type:
        <ul>
            <li><a href="/jsp/search/search_export.jsp?mod=4&dbn=ACC">ACC</a></li>
            <li><a href="/jsp/search/search_export.jsp?mod=4&dbn=Pheo">Pheo</a></li>
            <li><a href="/jsp/search/search_export.jsp?mod=4&dbn=NAPACA">NAPACA</a></li>
            <li><a href="/jsp/search/search_export.jsp?mod=4&dbn=APA">APA</a></li>
        </ul>    
    </p>
    
    <p>    
        Records from a particular stored query (grouped by tumor type):
        <ul>
            <li>All            
                <ul>
                    <li><a href="/jsp/search/search_export.jsp?mod=5&dbn=All&query=laterality">Laterality of all tumor types</a></li>
                </ul>            
            </li>
            
            <li>ACC
                <ul>
                    <li><a href="/jsp/search/search_export.jsp?mod=5&dbn=ACC&query=acc_quickcheck">ACC Quick Check</a></li>
                    <li><a href="/jsp/search/search_export.jsp?mod=5&dbn=ACC&query=summary_only">ACC Summary Status (only)</a></li>
                    <li><a href="/jsp/search/search_export.jsp?mod=5&dbn=ACC&query=summary_all">ACC Summary Status (+ rest of ACC records)</a></li>
                </ul>            
            </li>
            
            <li>Pheo</li>
            
            <li>NAPACA</li>
            
            <li>APA</li>
                        
        </ul>    
    </p>
    
    <p>    
        Records from a particular study (grouped by tumor type):
        
        <%
        String studyLinksStr = searchexport.getStudyLinks(conn);
        %>        
        <%=studyLinksStr%>
    </p>

<%
}else if(username.equals("laurence.amar@egp.aphp.fr")){
%>

<p>    
        Records from a particular study (grouped by tumor type):
        <li><a href="/jsp/search/search_export.jsp?mod=6&dbn=Pheo&study=ltphpgl">LTPHPGL</a></li>
</p>
    
<%
}else if(username.equals("dipti.rao@radboudumc.nl")){
%>

<p>All records belonging to <a href="/jsp/search/search_export.jsp?mod=2">your center</a> (center: <strong><%=userCenter%></strong>)</p>    

<%
}else if(username.equals("mariko.sue@uniklinikum-dresden.de")){
%>

<p>    
        Records from a particular study (grouped by tumor type):
        <li><a href="/jsp/search/search_export.jsp?mod=6&dbn=Pheo&study=pmt3">PMT3</a></li>
</p>

<%
}else if(username.equals("a.vanberkel@aig.umcn.nl") || username.equals("h.timmers@endo.umcn.nl") || username.equals("j.lenders@aig.umcn.nl")){
%>

<p>    
        Records from a particular study (grouped by tumor type):
        <li><a href="/jsp/search/search_export.jsp?mod=6&dbn=Pheo&study=mibg_impact">MIBG Impact</a></li>
</p>

<%
}else if(username.equals("bancos.irina@mayo.edu") || username.equals("bchortis@hotmail.com")){
%>

<p>    
        Records from a particular study (grouped by tumor type):
        <li><a href="/jsp/search/search_export.jsp?mod=6&dbn=ACC&study=eurineact">EURINE-ACT</a></li>
        <li><a href="/jsp/search/search_export.jsp?mod=6&dbn=ACC&study=adiuvo">ADIUVO</a></li>
        <li><a href="/jsp/search/search_export.jsp?mod=6&dbn=ACC&study=adiuvo_observational">ADIUVO Observational</a></li>
</p>

<%
}else if(username.equals("segolene.hescot@u-psud.fr")){
%>

<p>All records belonging to <a href="/jsp/search/search_export.jsp?mod=2">your center</a> (center: <strong><%=userCenter%></strong>)</p>

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

