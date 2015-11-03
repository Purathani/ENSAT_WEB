/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package summaryinfo;

import ConnectBean.ConnectBean;
import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.Statement;

import javax.servlet.ServletContext;

import java.util.List;
import java.util.Vector;
import org.junit.AfterClass;
import org.junit.Test;
import static org.junit.Assert.*;
import org.junit.BeforeClass;

import javax.servlet.ServletContext;

/**
 *
 * @author astell
 */
public class DatabaseListingTest {
    
    static Connection conn = null;
    static Statement stmt = null;
    
    public DatabaseListingTest() {
    }

    @BeforeClass
    public static void setUpClass() throws Exception {
        
        System.out.println("Setting up DatabaseListingTest...");
        
        //Set up the database connection
        String dbName = "test_ensat";        
        ConnectBean connect = new ConnectBean();
        String driverName = "com.mysql.jdbc.Driver";
        String serverName = "stell-2.rc.melbourne.nectar.org.au";
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
        System.out.println("Finishing DatabaseListingTest...");        
        stmt.close();
        conn.close();        
    }

    /**
     * Test of getListingHeaderHtml method, of class DatabaseListing.
     */
    @Test
    public void testGetListingHeaderHtml() {
        System.out.println("getListingHeaderHtml");
        int extraColumnNum = 0;
        String dbn = "";
        String dbid = "";
        String pageNum = "";
        DatabaseListing instance = new DatabaseListing();
        String expResult = "";
        String result = instance.getListingHeaderHtml(extraColumnNum, dbn, dbid, pageNum);
        assertEquals(expResult, result);
    }

    /**
     * Test of countryCheck method, of class DatabaseListing.
     */
    @Test
    public void testCountryCheck() {
        System.out.println("countryCheck");
        ServletContext context = null;
        DatabaseListing instance = new DatabaseListing();
        List[] expResult = null;
        List[] result = instance.countryCheck(null);
        assertArrayEquals(expResult, result);
    }

    /**
     * Test of patientConflictCheck method, of class DatabaseListing.
     */
    @Test
    public void testPatientConflictCheck() {
        System.out.println("patientConflictCheck");    
        String mainDb = "test_ensat";
        ServletContext context = null;
        DatabaseListing instance = new DatabaseListing();
        Vector<Vector> expResult = null;
        Vector<Vector> result = instance.patientConflictCheck(null);
        assertEquals(expResult, result);
    }

    /**
     * Test of getRowCount method, of class DatabaseListing.
     */
    @Test
    public void testGetRowCount() {
        System.out.println("getRowCount");        
        String _searchFilter = "local";
        String _ensatIdOrdering = "1";
        String _ensatDatabase = "";
        String _userCenter = "GBBI";
        ResultSet rs = null;
        DatabaseListing instance = new DatabaseListing();
        int expResult = 1;
        int result = instance.getRowCount(_searchFilter, _ensatIdOrdering, _ensatDatabase, _userCenter, rs);
        assertEquals(expResult, result);
    }

    /**
     * Test of compilePatientList method, of class DatabaseListing.
     */
    @Test
    public void testCompilePatientList() {
        System.out.println("compilePatientList");        
        String _searchFilter = "local";
        String _ensatIdOrdering = "1";
        String _ensatDatabase = "";
        String _userCenter = "GBBI";
        DatabaseListing instance = new DatabaseListing();
        ResultSet expResult = null;
        ResultSet result = instance.compilePatientList(null,_searchFilter, _ensatIdOrdering, _ensatDatabase, _userCenter);
        assertEquals(expResult, result);
    }

    /**
     * Test of getPatientList method, of class DatabaseListing.
     */
    /*@Test
    public void testGetPatientList() {
        System.out.println("getPatientList");
        DatabaseListing instance = new DatabaseListing();
        ResultSet expResult = null;
        ResultSet result = instance.getPatientList();
        assertEquals(expResult, result);
    }*/

    /**
     * Test of getPagingHtml method, of class DatabaseListing.
     */
    @Test
    public void testGetPagingHtml() {
        System.out.println("getPagingHtml");
        int rowCount = 0;
        String pageNum = "";
        String searchFilter = "";
        String dbn = "";
        String dbid = "";
        String ensatIdOrdering = "";
        DatabaseListing instance = new DatabaseListing();
        String expResult = "";
        String result = instance.getPagingHtml(rowCount, pageNum, searchFilter, dbn, dbid, ensatIdOrdering);
        assertEquals(expResult, result);
    }

    /**
     * Test of getTableHtml method, of class DatabaseListing.
     */
    @Test
    public void testGetTableHtml() throws Exception {
        System.out.println("getTableHtml");        
        String pageNum = "1";
        String dbn = "ACC";
        String dbid = "1";
        String dbName = "ensat_security_test";
        String username = "anthony";
        String country = "UK";
        DatabaseListing instance = new DatabaseListing();
        
        String _searchFilter = "all";
        String _ensatIdOrdering = "1";
        String _ensatDatabase = "ACC";
        String _userCenter = "GBBI";
        ResultSet rs = instance.compilePatientList(null, _searchFilter, _ensatIdOrdering, _ensatDatabase, _userCenter);        
        
        /*int rowCount = 0;
        while(rs.next()){
            rowCount++;
        }
        System.out.println("rowCount: " + rowCount);*/
        
        String mainDb = "test_ensat";
        String expResult = "";
        ServletContext context = null;
        String result = instance.getTableHtml(context,null,null, pageNum, dbn, dbid, mainDb, username, country, rs);
        assertEquals(expResult, result);
    }

    /**
     * Test of getIndividualHtml method, of class DatabaseListing.
     */
    @Test
    public void testGetIndividualHtml() {
        System.out.println("getIndividualHtml");
        List<PatientRecord> records = null;
        String lineColour = "";
        Vector<Vector> patientConflictIDs = null;
        int patientConflictCheckNum = 0;
        List<String>[] countryCheck = null;
        List<String> followupStr = null;
        List<String> deceasedStr = null;
        List<String> incompleteDataStr = null;
        Vector<Vector> potentialTransferStr = null;
        int potentialTransferCount = 0;
        String dbn = "";
        String dbid = "";
        String dbName = "";
        String username = "";
        String country = "";
        DatabaseListing instance = new DatabaseListing();
        String expResult = "";
        String result = instance.getIndividualHtml(records, lineColour, patientConflictIDs, patientConflictCheckNum, countryCheck, followupStr, deceasedStr, incompleteDataStr, potentialTransferStr, potentialTransferCount, dbn, dbid, username, country, "", "", "",null,null);
        assertEquals(expResult, result);
    }

    /**
     * Test of actionRequired method, of class DatabaseListing.
     */
    @Test
    public void testActionRequired() {
        System.out.println("actionRequired");
        String dbn = "";
        List<String> followupStr = null;
        List<String> deceasedStr = null;
        Vector<Vector> patientConflictIDs = null;
        int patientConflictCheckNum = 0;
        List<String> incompleteDataStr = null;
        Vector<Vector> potentialTransferStr = null;
        int potentialTransferCount = 0;
        String centerid = "";
        String pid = "";
        DatabaseListing instance = new DatabaseListing();
        String expResult = "";
        String result = instance.actionRequired(
                dbn,
                followupStr,
                deceasedStr,
                patientConflictIDs,
                patientConflictCheckNum,
                incompleteDataStr,
                potentialTransferStr,
                potentialTransferCount,
                centerid,
                pid);
        assertEquals(expResult, result);
    }

    /**
     * Test of formatMonth method, of class DatabaseListing.
     */
    @Test
    public void testFormatMonth() {
        System.out.println("formatMonth");
        String recordDateMonth = "01";
        DatabaseListing instance = new DatabaseListing();
        String expResult = "Jan";
        String result = instance.formatMonth(recordDateMonth);
        assertEquals(expResult, result);
    }

    /**
     * Test of formatID method, of class DatabaseListing.
     */
    @Test
    public void testFormatID() {
        System.out.println("formatID");
        String pid = "1";
        DatabaseListing instance = new DatabaseListing();
        String expResult = "0001";
        String result = instance.formatID(pid);
        assertEquals(expResult, result);
    }

    /**
     * Test of listIncompleteData method, of class DatabaseListing.
     */
    @Test
    public void testListIncompleteData() {
        System.out.println("listIncompleteData");        
        String dbn = "ACC";
        String mainDb = "test_ensat";
        ServletContext context = null;
        DatabaseListing instance = new DatabaseListing();
        
        List expResult = null;
        
        List result = instance.listIncompleteData(null, mainDb, dbn);
        assertEquals(expResult, result);
    }

    /**
     * Test of listPotentialTransfers method, of class DatabaseListing.
     */
    @Test
    public void testListPotentialTransfers() {
        System.out.println("listPotentialTransfers");        
        String mainDb = "test_ensat";
        DatabaseListing instance = new DatabaseListing();
        Vector<Vector> expResult = null;
        ServletContext context = null;
        Vector<Vector> result = instance.listPotentialTransfers(mainDb,null);
        assertEquals(expResult, result);
    }
}
