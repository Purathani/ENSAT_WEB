/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package delete_main;

import ConnectBean.ConnectBean;
import java.sql.Connection;
import java.sql.Statement;
import java.util.Vector;
import javax.servlet.ServletContext;
import org.junit.AfterClass;
import org.junit.Test;
import static org.junit.Assert.*;
import org.junit.BeforeClass;

/**
 *
 * @author astell
 */
public class DeleteTest {
    
    static Connection conn = null;
    static Statement stmt = null;
    
    public DeleteTest() {
    }

    @BeforeClass
    public static void setUpClass() throws Exception {
        System.out.println("Setting up DeleteTest...");
        
        //Set up the database connection
        String dbName = "test_ensat";
        String dbConnectionStr = "jdbc:mysql://stell-2.rc.melbourne.nectar.org.au:3306/" + dbName;
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
        System.out.println("Finishing DeleteTest...");        
        stmt.close();
        conn.close();                        
    }

    /**
     * Test of getLineColour method, of class Delete.
     */
    @Test
    public void testGetLineColour() {
        System.out.println("getLineColour");
        String dbn = "";
        Delete instance = new Delete();
        String expResult = "";
        String result = instance.getLineColour(dbn);
        assertEquals(expResult, result);
    }

    /**
     * Test of getParameters method, of class Delete.
     */
    @Test
    public void testGetParameters() {
        System.out.println("getParameters");
            String[] tablenames = new String[3];
    tablenames[0] = "Identification";
    tablenames[1] = "ACC_DiagnosticProcedures";
    tablenames[2] = "ACC_TumorStaging";

        String pid = "0001";
        String centerid = "GBBI";        
        Delete instance = new Delete();
        Vector expResult = null;
        ServletContext context = null;
        Vector result = instance.getParameters(tablenames, pid, centerid, conn, null);
        assertEquals(expResult, result);
    }

    /**
     * Test of getParameterValues method, of class Delete.
     */
    @Test
    public void testGetParameterValues() {
        System.out.println("getParameterValues");
        String[] tablenames = null;
        Vector<Vector> parameters = null;
        String pid = "";
        String centerid = "";
        Statement stmtValues = null;
        Connection connValues = null;
        Delete instance = new Delete();
        Vector expResult = null;
        Vector result = instance.getParameterValues(tablenames, parameters, pid, centerid, connValues);
        assertEquals(expResult, result);
    }

    /**
     * Test of getParameterHtml method, of class Delete.
     */
    @Test
    public void testGetParameterHtml() {
        System.out.println("getParameterHtml");
        
        String[] tablenames = new String[3];
    tablenames[0] = "Identification";
    tablenames[1] = "ACC_DiagnosticProcedures";
    tablenames[2] = "ACC_TumorStaging";

        String pid = "0001";
        String centerid = "GBBI";        
        Delete instance = new Delete();
        Vector expResult = null;
        ServletContext context = null;
        Vector<Vector> parameters = instance.getParameters(tablenames, pid, centerid, conn, null);
        
        String lineColour = "";
        String result = instance.getParameterHtml(parameters, lineColour);
        assertEquals(expResult, result);
    }

    /**
     * Test of compileSubTableList method, of class Delete.
     */
    @Test
    public void testCompileSubTableList() {
        System.out.println("compileSubTableList");
        String dbn = "";
        Delete instance = new Delete();
        Vector<Vector> expResult = null;
        Vector<Vector> result = instance.compileSubTableList(dbn);
        
        int resultNum = result.size();
        /*for(int i=0; i<resultNum; i++){
            System.out.println("subTables[" + i + "]: " + result[i]);
        }*/
        
        assertEquals(expResult, result);
    }

    /**
     * Test of deleteSubTables method, of class Delete.
     */
    @Test
    public void testDeleteSubTables() {
        System.out.println("deleteSubTables");        
        String pid = "";
        String centerid = "";
        String dbn = "ACC";
        Delete instance = new Delete();
        Vector<Vector> subTables = instance.compileSubTableList(dbn);        
        
        //instance.deleteSubTables(subTables, stmt, pid, centerid);
    }

    /**
     * Test of deleteMainTables method, of class Delete.
     */
    @Test
    public void testDeleteMainTables() {
        System.out.println("deleteMainTables");
        Statement stmt = null;
        String pid = "";
        String centerid = "";
        String dbn = "";
        Delete instance = new Delete();
        instance.deleteMainTables(conn, pid, centerid, dbn);
    }
}
