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
//Try the log4j stuff here
Logger logger = Logger.getLogger("rootLogger");
logger.setLevel(Level.DEBUG);
PropertyConfigurator.configure("/root/logs/log4j_ensat.properties");
        
String username = user.getUsername();
logger.debug("('" + username + "') - study_docs.jsp");

%>

<h2>Study protocols and SOPs</h2>


<h3>EURINE-ACT</h3>

<p>
<ul>
    <li><a href="./jsp/docs/ensat_eurineact_sop.pdf" target="_blank">SOPs for EURINE-ACT</a></li>
</ul>
</p>

<h3>MAPP-Prono</h3>

<p>
<ul>
    <li><a href="./jsp/docs/mapp_prono_outline.pdf" target="_blank">Study outline for MAPP-Prono</a></li>
</ul>
</p>

<h3>HairCo-2</h3>

<p>
<ul>
    <li><a href="./jsp/docs/hairco2.pdf" target="_blank">Study outline for HairCo-2</a></li>
</ul>
</p>

<h3>Long-term PHPGL</h3>

<p>
<ul>
    <li><a href="./jsp/docs/ltphpgl.pdf" target="_blank">Study outline for Long-term PHPGL</a></li>
</ul>
</p>

<h3>MIBG Impact</h3>

<p>
<ul>
    <li><a href="./jsp/docs/mibg_impact_study.pdf" target="_blank">Study outline for MIBG Impact</a></li>
</ul>
</p>

<h3>PMT3</h3>
<p>
<ul>
    <li><a href="./jsp/docs/a1_pmt3_study_overview.pdf" target="_blank">A1 - Study overview</a></li>
    <li><a href="./jsp/docs/a2_pmt3_intellectualproperty_guidelines.pdf" target="_blank">A2 - Study intellectual property guidelines</a></li>
    <li><a href="./jsp/docs/b1_visit_files_accrual_sop.pdf" target="_blank">B1 - Subject visit files and records</a></li>
    <li><a href="./jsp/docs/b2_pmt_visit_plan.pdf" target="_blank">B2 - Subject visit plan record</a></li>
    <li><a href="./jsp/docs/c1_patient_unique_identifiers_sop.pdf" target="_blank">C1 - Unique identifiers for human subject research</a></li>
    <li><a href="./jsp/docs/c2_specimen_unique_identifiers_sop.pdf" target="_blank">C2 - Unique identifiers for human specimens</a></li>
    <li><a href="./jsp/docs/c3_pmt3_specimen_uniqueid_procedures_sop.pdf" target="_blank">C3 - Unique identifier sticky label procedures</a></li>
    <li><a href="./jsp/docs/c4_pmt3_specimen_labels_example.pdf" target="_blank">C4 - PMT3 specimen labels</a></li>
    <li><a href="./jsp/docs/d1_pmt3_study_sample_flow.ppt" target="_blank">D1 - Sample collection flow sheet</a></li>
    <li><a href="./jsp/docs/e1_specimen_tubes_cryoboxes_labels.pdf" target="_blank">E1 - Specimen storage tubes and cryoboxes</a></li>
    <li><a href="./jsp/docs/e2_pmt3_specimen_processing.pdf" target="_blank">E2 - Specimen processing</a></li>
    <li><a href="./jsp/docs/e3_plasma_mets_cats_collection_shipping_sop.pdf" target="_blank">E3 - Plasma metanephrines and catecholamines blood sample and shipping</a></li>
    <li><a href="./jsp/docs/e4_specimen_chilling_sop.pdf" target="_blank">E4 - Specimen chilling</a></li>
    <li><a href="./jsp/docs/e5_sop_24hr_urine.pdf" target="_blank">E5 - 24hr urine collection</a></li>
    <li><a href="./jsp/docs/e6_dna_blood_collection_shipping_sop.pdf" target="_blank">E6 - Blood DNA sample and shipping</a></li>
    <li><a href="./jsp/docs/f1_pmt3_sample_manifest_template.xls" target="_blank">F1 - Sample manifest</a></li>    
</ul>
</p>


<jsp:include page="/jsp/page/page_foot.jsp" />

</td> 
      </tr> 
    </table> 
    </td> 
    
</tr>

</table>


