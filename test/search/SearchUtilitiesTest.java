/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package search;

import ConnectBean.ConnectBean;
import java.sql.Connection;
import java.sql.Statement;
import java.util.List;
import org.junit.AfterClass;
import org.junit.Test;
import static org.junit.Assert.*;
import org.junit.BeforeClass;

/**
 *
 * @author astell
 */
public class SearchUtilitiesTest {
    
    static Connection conn = null;
    static Statement stmt = null;
    
    public SearchUtilitiesTest() {
        
    }

    @BeforeClass
    public static void setUpClass() throws Exception {
        System.out.println("Setting up SearchUtilitiesTest...");
        
        //Set up the database connection
        String dbName = "test_ensat";        
        ConnectBean connect = new ConnectBean();
        String driverName = "com.mysql.jdbc.Driver";
        //String serverName = "stell-2.rc.melbourne.nectar.org.au";
        String serverName = "localhost";
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
        System.out.println("Finishing SearchUtilitiesTest...");        
        stmt.close();
        conn.close();                        
    }

    /**
     * Test of getSearchQuerySql method, of class SearchUtilities.
     */
    @Test
    public void testGetSearchQuerySql() {
        System.out.println("TEST getSearchQuerySql...");
        SearchUtilities instance = new SearchUtilities();
        String expResult = "";
        String result = instance.getSearchQuerySql();
        System.out.println("Result (getSearchQuerySql): " + result);
        assertEquals(expResult, result);        
    }

    /**
     * Test of setSearchQuerySql method, of class SearchUtilities.
     */
    @Test
    public void testSetSearchQuerySql() {
        System.out.println("TEST setSearchQuerySql...");
        
        String _searchQuerySql = "SELECT * FROM Identification;";
        System.out.println("searchQuerySql:" + _searchQuerySql);
        
        SearchUtilities instance = new SearchUtilities();
        instance.setSearchQuerySql(_searchQuerySql);        
    }

    /**
     * Test of getParameters method, of class SearchUtilities.
     */
    @Test
    public void testGetParameters() {
        System.out.println("TEST getParameters...");
        SearchUtilities instance = new SearchUtilities();
        List expResult = null;
        List result = instance.getParameters();
        assertEquals(expResult, result);        
    }

    /**
     * Test of setParameters method, of class SearchUtilities.
     */
    @Test
    public void testSetParameters() {
        System.out.println("TEST setParameters...");
        List<String> _parameters = null;
        SearchUtilities instance = new SearchUtilities();
        instance.setParameters(_parameters);        
    }

    /**
     * Test of getConditions method, of class SearchUtilities.
     */
    @Test
    public void testGetConditions() {
        System.out.println("TEST getConditions...");
        SearchUtilities instance = new SearchUtilities();
        List expResult = null;
        /*List result = instance.getConditions();
        assertEquals(expResult, result);        */
    }

    /**
     * Test of setConditions method, of class SearchUtilities.
     */
    @Test
    public void testSetConditions() {
        System.out.println("TEST setConditions...");
        List<String> _conditions = null;
        SearchUtilities instance = new SearchUtilities();
        //instance.setConditions(_conditions);        
    }

    /**
     * Test of getComparators method, of class SearchUtilities.
     */
    @Test
    public void testGetComparators() {
        System.out.println("TEST getComparators...");
        SearchUtilities instance = new SearchUtilities();
        List expResult = null;
        /*List result = instance.getComparators();
        assertEquals(expResult, result);        */
    }

    /**
     * Test of setComparators method, of class SearchUtilities.
     */
    @Test
    public void testSetComparators() {
        System.out.println("TEST setComparators...");
        List<String> _comparators = null;
        SearchUtilities instance = new SearchUtilities();
        //instance.setComparators(_comparators);        
    }

    /**
     * Test of getTables method, of class SearchUtilities.
     */
    @Test
    public void testGetTables() {
        System.out.println("TEST getTables...");
        SearchUtilities instance = new SearchUtilities();
        List expResult = null;
        List result = instance.getTables();
        assertEquals(expResult, result);        
    }

    /**
     * Test of setTables method, of class SearchUtilities.
     */
    @Test
    public void testSetTables() {
        System.out.println("TEST setTables...");
        List<String> _tables = null;
        SearchUtilities instance = new SearchUtilities();
        instance.setTables(_tables);        
    }
}
