package search;

import ConnectBean.ConnectBean;
import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.Statement;
import java.util.List;
import java.util.ArrayList;
import java.util.Vector;
import javax.servlet.ServletContext;
import javax.servlet.http.HttpServletRequest;
import org.junit.AfterClass;
import org.junit.Test;
import static org.junit.Assert.*;
import org.junit.BeforeClass;

/**
 *
 * @author astell
 */
public class SearchTest {
    
    static Connection conn = null;
    static Statement stmt = null;
    
    public SearchTest() {
    }

    @BeforeClass
    public static void setUpClass() throws Exception {
        System.out.println("Setting up SearchTest...");
        
        //Set up the database connection
        String dbName = "test_ensat";        
        ConnectBean connect = new ConnectBean();
        String driverName = "com.mysql.jdbc.Driver";
        String serverName = "stell-2.rc.melbourne.nectar.org.au";
        //String serverName = "localhost";
        String port = "3306";
        String username = "ensat";
        String password = "ensat_melb)";
        connect.setConnection(dbName, driverName, serverName, port, username, password);
        conn = connect.getConnection();
        try{
            stmt = conn.createStatement();
            System.out.println("Connection successful...");
        }catch(Exception e){
            System.out.println("Error: " + e.getMessage());
        }        
    }

    @AfterClass
    public static void tearDownClass() throws Exception {
        System.out.println("Finishing SearchTest...");        
        stmt.close();
        conn.close();        
    }

    /**
     * Test of getLineColour method, of class Search.
     */
    @Test
    public void testGetLineColour() {
        System.out.println("TEST getLineColour...");
        
        Search instance = new Search();
        
        String expResult = "class=\"parameter-line-double-search\"";
        
        String result = instance.getLineColour();
        System.out.println("Result (getLineColour): " + result);
        assertEquals(expResult, result);        
    }

    /**
     * Test of getParameters method, of class Search.
     */
    @Test
    public void testGetParameters() {
        System.out.println("TEST getParameters...");
        
        String[] _tablenames = {"Identification","ACC_DiagnosticProcedures","ACC_TumorStaging"};
        ServletContext context = null;
        HttpServletRequest request = null;
        Search instance = new Search();
        Vector expResult = null;
        Vector result = instance.getParameters(_tablenames, request, context);
        
        System.out.println("Result size (getParameters): " + result.size());
        
        System.out.println("Equality assertion removed for Vector object...");
        //assertEquals(expResult, result);        
    }

    /**
     * Test of getMenus method, of class Search.
     */
    @Test
    public void testGetMenus() {
        System.out.println("TEST getMenus...");
        
        //String[] _tablenames = {"Identification","ACC_DiagnosticProcedures","ACC_TumorStaging"};
        String[] _tablenames = {"ACC_Biomaterial"};
        ServletContext context = null;
        Search instance = new Search();
        Vector<Vector> parameters = instance.getParameters(_tablenames, null, context);
        Vector expResult = null;
        Vector<Vector> result = instance.getMenus(parameters, null);
        
        System.out.println("Result size (getMenus): " + result.size());
        
        for(int i=0; i<result.size(); i++){
            Vector<String> menuIn = result.get(i);
            System.out.println("=====");
            for(int j=0; j<menuIn.size(); j++){
                System.out.println("menu item: " + menuIn.get(j));
            }
        }
        
        System.out.println("Equality assertion removed for Vector object...");
        //assertEquals(expResult, result);        
    }

    /**
     * Test of getParameterHtml method, of class Search.
     */
    @Test
    public void testGetParameterHtml() {
        System.out.println("TEST getParameterHtml...");
        Search instance = new Search();
        
        String[] _tablenames = {"Identification","ACC_DiagnosticProcedures","ACC_TumorStaging"};
        Vector<Vector> parameters = instance.getParameters(_tablenames, null, null);
        Vector<Vector> menus = instance.getMenus(parameters, null);
        String lineColour = instance.getLineColour();
        String expResult = "";
        String result = instance.getParameterHtml(parameters, menus, lineColour);
        
        System.out.println("Equality assertion as result too big for effective string rendering...");
        //System.out.println("Result (getParameterHtml): " + result);
        //assertEquals(expResult, result);        
    }

    /**
     * Test of getAssocTableRowHtml method, of class Search.
     */
    @Test
    public void testGetAssocTableRowHtml() {
        System.out.println("TEST getAssocTableRowHtml...");
        
        String dbid = "1";
        System.out.println("dbid: " + dbid);
        String dbn = "ACC";
        System.out.println("dbn: " + dbn);
        Search instance = new Search();
        Vector<Vector> subTables = instance.compileSubTableList(dbn);
        
        String expResult = "<p><a href=\"./jsp/search/search_result.jsp?dbid=1&dbn=ACC&modality=biomaterial&mainsearch=listall&showformsearch=1\"><strong>Biomaterial</strong></a></p><p><a href=\"./jsp/search/search_result.jsp?dbid=1&dbn=ACC&modality=chemoembolisation&mainsearch=listall&showformsearch=1\"><strong>Chemoembolisation</strong></a></p><p><a href=\"./jsp/search/search_result.jsp?dbid=1&dbn=ACC&modality=chemotherapy&mainsearch=listall&showformsearch=1\"><strong>Chemotherapy</strong></a></p><p><a href=\"./jsp/search/search_result.jsp?dbid=1&dbn=ACC&modality=followup&mainsearch=listall&showformsearch=1\"><strong>Follow-up</strong></a></p><p><a href=\"./jsp/search/search_result.jsp?dbid=1&dbn=ACC&modality=mitotane&mainsearch=listall&showformsearch=1\"><strong>Mitotane</strong></a></p><p><a href=\"./jsp/search/search_result.jsp?dbid=1&dbn=ACC&modality=pathology&mainsearch=listall&showformsearch=1\"><strong>Pathology</strong></a></p><p><a href=\"./jsp/search/search_result.jsp?dbid=1&dbn=ACC&modality=radiofrequency&mainsearch=listall&showformsearch=1\"><strong>Radiofrequency</strong></a></p><p><a href=\"./jsp/search/search_result.jsp?dbid=1&dbn=ACC&modality=radiotherapy&mainsearch=listall&showformsearch=1\"><strong>Radiotherapy</strong></a></p><p><a href=\"./jsp/search/search_result.jsp?dbid=1&dbn=ACC&modality=surgery&mainsearch=listall&showformsearch=1\"><strong>Surgery</strong></a></p>";
        String result = instance.getAssocTableRowHtml(subTables, dbid, dbn);
        
        System.out.println("Result (getAssocTableRowHtml): " + result);
        assertEquals(expResult, result);
    }

    /**
     * Test of compileSearchResults method, of class Search.
     */
    @Test
    public void testCompileSearchResults() {
        System.out.println("TEST compileSearchResults...");
        
        String[] _tablenames = {"Identification","ACC_DiagnosticProcedures","ACC_TumorStaging"};
        HttpServletRequest request = null;
        Search instance = new Search();
        Vector<Vector> parameters = instance.getParameters(_tablenames, null, null);
        
        SearchResult expResult = null;
        SearchResult result = instance.compileSearchResults(parameters, request);
                
        List<String> resultParameters = result.getParameters();
        System.out.println("Result parameter size: " + resultParameters.size());
        
        System.out.println("Equality assertion removed for SearchResult object...");
        //assertEquals(expResult, result);        
    }

    /**
     * Test of getRepeatSearchResults method, of class Search.
     */
    @Test
    public void testGetRepeatSearchResults() {
        System.out.println("TEST getRepeatSearchResults...");
        
        List<String> parametersOrig = null;
        List<String> conditionsOrig = null;
        List<String> comparatorsOrig = null;
        List<String> tablesOrig = null;
        
        HttpServletRequest request = null;
        
        String[] _tablenames = {"Identification","ACC_DiagnosticProcedures","ACC_TumorStaging"};
        Search instance = new Search();
        Vector<Vector> parameters = instance.getParameters(_tablenames, null, null);
        SearchResult expResult = null;
        SearchResult result = instance.getRepeatSearchResults(parametersOrig, conditionsOrig, comparatorsOrig, tablesOrig, request, parameters);
        
        List<String> resultParameters = result.getParameters();
        System.out.println("Result parameter size: " + resultParameters.size());
        
        System.out.println("Equality assertion removed for SearchResult object...");
        //assertEquals(expResult, result);        
    }

    /**
     * Test of getShowFormSearchResults method, of class Search.
     */
    @Test
    public void testGetShowFormSearchResults() {
        System.out.println("TEST getShowFormSearchResults...");
        
        List<String> parametersOrig = null;
        List<String> conditionsOrig = null;
        List<String> comparatorsOrig = null;
        List<String> tablesOrig = null;
        
        String[] _tablenames = {"Identification","ACC_DiagnosticProcedures","ACC_TumorStaging"};
        Search instance = new Search();
        Vector<Vector> parameters = instance.getParameters(_tablenames, null, null);
        SearchResult expResult = null;
        SearchResult result = instance.getShowFormSearchResults(parametersOrig, conditionsOrig, comparatorsOrig, tablesOrig, parameters);
        
        List<String> resultParameters = result.getParameters();
        System.out.println("Result parameter size: " + resultParameters.size());
        
        System.out.println("Equality assertion removed for SearchResult object...");
        //assertEquals(expResult, result);        
    }

    /**
     * Test of compileSearchQuery method, of class Search.
     */
    @Test
    public void testCompileSearchQuery() {
        System.out.println("TEST compileSearchQuery...");
        SearchResult sr = new SearchResult();
        sr.setComparators(new ArrayList<String>());
        sr.setConditions(new ArrayList<String>());
        sr.setParameters(new ArrayList<String>());
        sr.setTables(new ArrayList<String>());
        
        String dbn = "ACC";
        Search instance = new Search();
        String expResult = "SELECT DISTINCT Identification.ensat_id, Identification.center_id, Identification.local_investigator, Identification.investigator_email, Identification.record_date, Identification.date_first_reg, Identification.sex, Identification.year_of_birth, Identification.consent_obtained, Identification.uploader FROM Identification AND Identification.ensat_database='" + dbn + "';";
        String result = instance.compileSearchQuery(sr, dbn);
        
        System.out.println("Result (compileSearchQuery): " + result);        
        assertEquals(expResult, result);        
    }

    /**
     * Test of runSearchQuery method, of class Search.
     */
    @Test
    public void testRunSearchQuery() {
        System.out.println("TEST runSearchQuery...");
        String searchQuerySql = "";
        ServletContext context = null;
        Search instance = new Search();
        ResultSet expResult = null;
        ResultSet result = instance.runSearchQuery(searchQuerySql, null);
        assertEquals(expResult, result);
        
    }

    /**
     * Test of getRowCount method, of class Search.
     */
    @Test
    public void testGetRowCount() {
        System.out.println("TEST getRowCount...");
        ResultSet rs = null;
        Search instance = new Search();
        int expResult = 0;
        int result = instance.getRowCount(rs);
        
        System.out.println("Result (getRowCount): " + result);
        assertEquals(expResult, result);        
    }

    /**
     * Test of getSearchHeaderInfo method, of class Search.
     */
    @Test
    public void testGetSearchHeaderInfo() {
        System.out.println("TEST getSearchHeaderInfo...");
        
        ResultSet rs = null;
        int rowCount = 0;
        SearchResult sr = new SearchResult();
        sr.setComparators(new ArrayList<String>());
        sr.setConditions(new ArrayList<String>());
        sr.setParameters(new ArrayList<String>());
        sr.setTables(new ArrayList<String>());
        
        Search instance = new Search();
        String expResult = "<p>There are <strong>" + rowCount + "</strong> records matching the following query:<br/><br/><table cellpadding=\"5\" border=\"1\"><tr><th><div align='center'>Parameter</div></th><th><div align='center'>Condition</div></th><th></th></tr></table></p>";
        String result = instance.getSearchHeaderInfo(rs, rowCount, sr);
        
        System.out.println("Result (getSearchHeaderInfo): " + result);
        assertEquals(expResult, result);        
    }

    /**
     * Test of compileAllSearchQuery method, of class Search.
     */
    @Test
    public void testCompileAllSearchQuery() {
        System.out.println("TEST compileAllSearchQuery...");
        
        String dbn = "ACC";
        System.out.println("dbn: " + dbn);
        
        ServletContext context = null;
        Search instance = new Search();
        String expResult = "SELECT * FROM Identification WHERE Identification.ensat_database='" + dbn + "';";
        String result = instance.compileAllSearchQuery(dbn, context);
        
        System.out.println("Result (compileAllSearchQuery): " + result);
        assertEquals(expResult, result);        
    }

    /**
     * Test of getSummaryInfo method, of class Search.
     */
    @Test
    public void testGetSummaryInfo() {
        System.out.println("TEST getSummaryInfo...");
        
        int rowCount = 3;
        System.out.println("rowCount: " + rowCount);
        Search instance = new Search();
        String expResult = "<p>There are <strong>" + rowCount + "</strong> records records in the database</p>";
        String result = instance.getSummaryInfo(rowCount);
        
        System.out.println("Result (getSummaryInfo): " + result);
        assertEquals(expResult, result);        
    }

    /**
     * Test of getTableHeaderInfo method, of class Search.
     */
    @Test
    public void testGetTableHeaderInfo() {
        System.out.println("TEST getTableHeaderInfo...");
        Search instance = new Search();
        String expResult = "<thead><tr><th>ENSAT ID</th><th>Referral Doctor</th><th>Record Date</th><th>Date of First Registration</th><th>Sex</th><th>Year of Birth</th><th>Consent Level Obtained</th><th></th><th></th></tr></thead>";
        String result = instance.getTableHeaderInfo();
        
        System.out.println("Result (getTableHeaderInfo): " + result);
        assertEquals(expResult, result);        
    }

    /**
     * Test of getTableBodyInfo method, of class Search.
     */
    @Test
    public void testGetTableBodyInfo() {
        System.out.println("TEST getTableBodyInfo...");
        
        ResultSet rs = null;
        String dbid = "";
        String dbn = "";
        String username = "";
        String country = "";
        Search instance = new Search();
        String expResult = "<tbody></tbody>";
        String result = instance.getTableBodyInfo(rs, dbid, dbn, username, country, "", "", "",null,null);
        
        System.out.println("Result (getTableBodyInfo): " + result);
        assertEquals(expResult, result);        
    }

    /**
     * Test of compileSubTableList method, of class Search.
     */
    @Test
    public void testCompileSubTableList() {
        System.out.println("TEST compileSubTableList...");
        
        String dbn = "ACC";
        System.out.println("dbn: " + dbn);
        
        Search instance = new Search();
        Vector expResult = null;
        Vector result = instance.compileSubTableList(dbn);
        
        System.out.println("Result size (compileSubTableList): " + result.size());

        System.out.println("Equality assertion removed for Vector object...");
        //assertEquals(expResult, result);        
    }

    /**
     * Test of getAssocTableHtml method, of class Search.
     */
    @Test
    public void testGetAssocTableHtml() {
        System.out.println("TEST getAssocTableHtml...");
        
        String dbid = "1";
        System.out.println("dbid: " + dbid);
        String dbn = "ACC";
        System.out.println("dbn: " + dbn);
        Search instance = new Search();
        Vector<Vector> subTables = instance.compileSubTableList(dbn);
        
        String expResult = "<tr><td><a href=\"./jsp/search/search_result.jsp?dbid=1&dbn=ACC&modality=biomaterial&mainsearch=custom&showformsearch=1\"><strong>Biomaterial</a></strong></td></tr><tr><td><a href=\"./jsp/search/search_result.jsp?dbid=1&dbn=ACC&modality=chemoembolisation&mainsearch=custom&showformsearch=1\"><strong>Chemoembolisation</a></strong></td></tr><tr><td><a href=\"./jsp/search/search_result.jsp?dbid=1&dbn=ACC&modality=chemotherapy&mainsearch=custom&showformsearch=1\"><strong>Chemotherapy</a></strong></td></tr><tr><td><a href=\"./jsp/search/search_result.jsp?dbid=1&dbn=ACC&modality=followup&mainsearch=custom&showformsearch=1\"><strong>Follow-up</a></strong></td></tr><tr><td><a href=\"./jsp/search/search_result.jsp?dbid=1&dbn=ACC&modality=mitotane&mainsearch=custom&showformsearch=1\"><strong>Mitotane</a></strong></td></tr><tr><td><a href=\"./jsp/search/search_result.jsp?dbid=1&dbn=ACC&modality=pathology&mainsearch=custom&showformsearch=1\"><strong>Pathology</a></strong></td></tr><tr><td><a href=\"./jsp/search/search_result.jsp?dbid=1&dbn=ACC&modality=radiofrequency&mainsearch=custom&showformsearch=1\"><strong>Radiofrequency</a></strong></td></tr><tr><td><a href=\"./jsp/search/search_result.jsp?dbid=1&dbn=ACC&modality=radiotherapy&mainsearch=custom&showformsearch=1\"><strong>Radiotherapy</a></strong></td></tr><tr><td><a href=\"./jsp/search/search_result.jsp?dbid=1&dbn=ACC&modality=surgery&mainsearch=custom&showformsearch=1\"><strong>Surgery</a></strong></td></tr>";
        String result = instance.getAssocTableHtml(subTables, dbid, dbn);
        
        System.out.println("Result (getAssocTableHtml): " + result);
        assertEquals(expResult, result);        
    }
}
