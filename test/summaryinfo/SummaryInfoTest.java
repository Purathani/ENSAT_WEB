/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package summaryinfo;

import java.sql.Statement;
import java.sql.Connection;
import org.junit.AfterClass;
import org.junit.Test;
import static org.junit.Assert.*;
import org.junit.BeforeClass;

import ConnectBean.ConnectBean;

/**
 *
 * @author astell
 */
public class SummaryInfoTest {

    static Connection conn = null;
    static Statement stmt = null;

    public SummaryInfoTest() {
    }

    @BeforeClass
    public static void setUpClass() throws Exception {
        
        System.out.println("Setting up SummaryInfoTest...");
        
        //Set up the database connection
        String dbName = "test_ensat";
        String dbConnectionStr = "jdbc:mysql://stell-2.rc.melbourne.nectar.org.au:3306/" + dbName;
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
        System.out.println("Finishing SummaryInfoTest...");        
        stmt.close();
        conn.close();
    }

    /**
     * Test of getRankingHtml method, of class SummaryInfo.
     */
    @Test
    public void testGetRankingHtml() {
        System.out.println("getRankingHtml");        
        String[] ensatSections = {"ACC", "Pheo", "NAPACA", "APA", ""};
        SummaryInfo instance = new SummaryInfo();
        String expResult = "";        
        String result = instance.getRankingHtml(conn, ensatSections);
        assertEquals(expResult, result);
    }

    /**
     * Test of getSummaryHtml method, of class SummaryInfo.
     */
    @Test
    public void testGetSummaryHtml() {
        System.out.println("getSummaryHtml");        
        String[] ensatSections = {"ACC", "Pheo", "NAPACA", "APA", ""};
        SummaryInfo instance = new SummaryInfo();
        String expResult = "";
        String result = instance.getSummaryHtml(conn, ensatSections);
        assertEquals(expResult, result);
        
    }

    /**
     * Test of countRecords method, of class SummaryInfo.
     */
    @Test
    public void testCountRecords() {
        System.out.println("countRecords - testing all databases");        
        String _database = "";
        int expResult = 2;
        SummaryInfo instance = new SummaryInfo();        
        int result = instance.countRecords(conn, _database);        
        assertEquals(expResult, result);        
    }

    /**
     * Test of countActiveCenters method, of class SummaryInfo.
     */
    @Test
    public void testCountActiveCenters() {
        System.out.println("countActiveCenters - testing all databases");        
        String _database = "";
        SummaryInfo instance = new SummaryInfo();
        int expResult = 2;
        int result = instance.countActiveCenters(conn, _database);
        assertEquals(expResult, result);        
    }

    /**
     * Test of rankActiveCenters method, of class SummaryInfo.
     */
    @Test
    public void testRankActiveCenters() {
        System.out.println("rankActiveCenters - testing all databases");        
        String _database = "ACC";
        int _totalCenterNumber = 2;
        SummaryInfo instance = new SummaryInfo();
        
        String[][] expResult = null;
        String[][] result = instance.rankActiveCenters(conn, _database, _totalCenterNumber);
        assertArrayEquals(expResult, result);
        
    }

    /**
     * Test of countDeadPatients method, of class SummaryInfo.
     */
    @Test
    public void testCountDeadPatients() {
        System.out.println("countDeadPatients");        
        String _database = "";
        SummaryInfo instance = new SummaryInfo();
        int expResult = 0;
        int result = instance.countDeadPatients(stmt, _database);
        assertEquals(expResult, result);
        
    }

    /**
     * Test of countBiosamples method, of class SummaryInfo.
     */
    @Test
    public void testCountBiosamples() {
        System.out.println("countBiosamples");        
        String _database = "";
        SummaryInfo instance = new SummaryInfo();
        int expResult = 0;
        int result = instance.countBiosamples(conn, _database);
        assertEquals(expResult, result);
        
    }

    /**
     * Test of countClinicalAnnotations method, of class SummaryInfo.
     */
    @Test
    public void testCountClinicalAnnotations() {
        System.out.println("countClinicalAnnotations");        
        String _database = "";
        SummaryInfo instance = new SummaryInfo();
        int expResult = 0;
        int result = instance.countClinicalAnnotations(conn, _database);
        assertEquals(expResult, result);
        
    }

    /**
     * Test of formatDecimalPlaces method, of class SummaryInfo.
     */
    @Test
    public void testFormatDecimalPlaces() {
        System.out.println("formatDecimalPlaces");
        String _str = "1.314159";
        SummaryInfo instance = new SummaryInfo();
        String expResult = "1.31";
        String result = instance.formatDecimalPlaces(_str);
        assertEquals(expResult, result);
        
    }
}
