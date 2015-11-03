package search;

import ConnectBean.ConnectBean;
import java.sql.Connection;
import java.sql.Statement;
import java.util.List;
import java.util.ArrayList;
import java.util.Vector;
import javax.servlet.http.HttpServletRequest;
import org.junit.AfterClass;
import org.junit.Test;
import static org.junit.Assert.*;
import org.junit.BeforeClass;

/**
 *
 * @author astell
 */
public class SearchSubTest {
    
    static Connection conn = null;
    static Statement stmt = null;
    
    public SearchSubTest() {
    }

    @BeforeClass
    public static void setUpClass() throws Exception {
        System.out.println("Setting up SearchSubTest...");
        
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
        System.out.println("Finishing SearchSubTest...");        
        stmt.close();
        conn.close();        
    }

    /**
     * Test of compileSearchResults method, of class SearchSub.
     */
    @Test
    public void testCompileSearchResults() {
        System.out.println("TEST compileSearchResults...");
        
        List<String> parametersOrig = new ArrayList<String>();
        List<String> conditionsOrig = new ArrayList<String>();
        List<String> comparatorsOrig = new ArrayList<String>();
        List<String> tablesOrig = new ArrayList<String>();
        
        String[] _tablenames = {"Identification","ACC_DiagnosticProcedures","ACC_TumorStaging"};
        HttpServletRequest request = null;
        
        SearchSub instance = new SearchSub();
        Vector<Vector> parameters = instance.getParameters(_tablenames, null, null);
        
        SearchResult expResult = new SearchResult();
        SearchResult result = instance.compileSearchResults(parametersOrig, conditionsOrig, comparatorsOrig, tablesOrig, request, parameters);
        
        List<String> resultParameters = result.getParameters();
        System.out.println("Result parameter size: " + resultParameters.size());
        
        System.out.println("Equality assertion moved because SearchResult objects will not be equal...");
        //assertEquals(expResult, result);                
    }

    /**
     * Test of getPageTitle method, of class SearchSub.
     */
    @Test
    public void testGetPageTitle() {
        System.out.println("TEST getPageTitle...");
        
        String modality = "biomaterial";
        String dbn = "ACC";
        SearchSub instance = new SearchSub();
        
        String expResult = "Biomaterial";
        String result = instance.getPageTitle(modality, dbn);
        
        System.out.println("Result (getPageTitle): " + result);
        assertEquals(expResult, result);        
    }

    /**
     * Test of getSubTablename method, of class SearchSub.
     */
    @Test
    public void testGetSubTablename() {
        System.out.println("TEST getSubTablename...");
        
        String modality = "biomaterial";
        String dbn = "ACC";
        SearchSub instance = new SearchSub();
        
        String expResult = "ACC_Biomaterial";
        String result = instance.getSubTablename(modality, dbn);
        
        System.out.println("Result (getSubTablename): " + result);
        assertEquals(expResult, result);        
    }

    /**
     * Test of getSubTableIdName method, of class SearchSub.
     */
    @Test
    public void testGetSubTableIdName() {
        System.out.println("TEST getSubTableIdName...");
        
        String modality = "biomaterial";
        String dbn = "ACC";
        SearchSub instance = new SearchSub();
        
        String expResult = "acc_biomaterial_id";
        String result = instance.getSubTableIdName(modality, dbn);
        
        System.out.println("Result (getSubTableIdName): " + result);
        assertEquals(expResult, result);        
    }
}
