/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package update_main;

import ConnectBean.ConnectBean;
import java.sql.Connection;
import java.sql.Statement;
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
public class UpdateTest {
    
    static Connection conn = null;
    static Statement stmt = null;
    
    
    public UpdateTest() {
    }

    @BeforeClass
    public static void setUpClass() throws Exception {
        
        System.out.println("Setting up UpdateTest...");
        
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
        
        System.out.println("Finishing UpdateTest...");        
        stmt.close();
        conn.close();                
    }

    /**
     * Test of getLineColour method, of class Update.
     */
    @Test
    public void testGetLineColour() {
        System.out.println("getLineColour");
        String dbn = "";
        Update instance = new Update();
        String expResult = "";
        String result = instance.getLineColour(dbn);
        assertEquals(expResult, result);
        // TODO review the generated test code and remove the default call to fail.
        fail("The test case is a prototype.");
    }

    /**
     * Test of getParameters method, of class Update.
     */
    @Test
    public void testGetParameters() {
        System.out.println("getParameters");
        String[] _tablenames = {"Identification","ACC_DiagnosticProcedures","ACC_TumorStaging"};
        String pid = "0002";
        String centerid = "GBLE";
        Statement stmtValues = stmt;
        Connection connValues = conn;
        ServletContext context = null;
        Update instance = new Update();
        Vector expResult = null;
        Vector result = instance.getParameters(_tablenames, pid, centerid, connValues, null,"");
        assertEquals(expResult, result);
    }

    /**
     * Test of getMenus method, of class Update.
     */
    @Test
    public void testGetMenus() {
        System.out.println("getMenus");
        Vector<Vector> parameters = null;
        ServletContext context = null;
        Update instance = new Update();
        Vector expResult = null;
        Vector result = instance.getMenus(parameters, null);
        assertEquals(expResult, result);
        // TODO review the generated test code and remove the default call to fail.
        fail("The test case is a prototype.");
    }

    /**
     * Test of getParameterHtml method, of class Update.
     */
    @Test
    public void testGetParameterHtml() {
        System.out.println("getParameterHtml");
        Vector<Vector> parameters = null;
        Vector<Vector> menus = null;
        String lineColour = "";
        String dbn = "ACC";
        Update instance = new Update();
        ServletContext context = null;
        
        String userCenter = "GBBI";
        String[] tablenames = {"APA_PatientHistory"};
        String pid = "0002";
        String centerid = "GBLE";
        parameters = instance.getParameters(tablenames, pid, centerid, null, null,"");
        menus = instance.getMenus(parameters, null);        
        
        String expResult = "";
        String result = instance.getParameterHtml(parameters, menus, lineColour,dbn,"",null,centerid,pid,"","");
        assertEquals(expResult, result);
        
    }

    /**
     * Test of getLastPageParamHtml method, of class Update.
     */
    @Test
    public void testGetLastPageParamHtml() {
        System.out.println("getLastPageParamHtml");
        Vector<Vector> parameters = null;
        Update instance = new Update();
        String expResult = "";
        String result = instance.getLastPageParamHtml(parameters,null);
        assertEquals(expResult, result);
        // TODO review the generated test code and remove the default call to fail.
        fail("The test case is a prototype.");
    }

    /**
     * Test of getHiddenParams method, of class Update.
     */
    @Test
    public void testGetHiddenParams() {
        System.out.println("getHiddenParams");
        Vector<String> lastPageParamNames = null;
        Vector<String> lastPageParamValues = null;
        Update instance = new Update();
        String expResult = "";
        String result = instance.getHiddenParams(lastPageParamNames, lastPageParamValues,null);
        assertEquals(expResult, result);
        // TODO review the generated test code and remove the default call to fail.
        fail("The test case is a prototype.");
    }

    /**
     * Test of getLastPageParamConfirmHtml method, of class Update.
     */
    @Test
    public void testGetLastPageParamConfirmHtml() {
        System.out.println("getLastPageParamConfirmHtml");
        Vector<Vector> parameters = null;
        HttpServletRequest request = null;
        String lineColour = "";
        String dbn = "ACC";
        Update instance = new Update();
        String expResult = "";
        String result = instance.getLastPageParamConfirmHtml(parameters, lineColour,dbn,request);
        assertEquals(expResult, result);
        // TODO review the generated test code and remove the default call to fail.
        fail("The test case is a prototype.");
    }

    /**
     * Test of executeParameterUpdate method, of class Update.
     */
    @Test
    public void testExecuteParameterUpdate() {
        System.out.println("executeParameterUpdate");
        String pid = "";
        String centerid = "";
        String tablename = "";
        Vector<Vector> parameters = null;
        Statement statement = null;
        Connection connection = null;
        HttpServletRequest request = null;
        Update instance = new Update();
        instance.executeParameterUpdate(pid, centerid, tablename, parameters, connection, request);
        // TODO review the generated test code and remove the default call to fail.
        fail("The test case is a prototype.");
    }

    /**
     * Test of getParameterValue method, of class Update.
     */
    @Test
    public void testGetParameterValue() {
        System.out.println("getParameterValue");
        String paramName = "";
        Vector<Vector> parameters = null;
        Update instance = new Update();
        String expResult = "";
        String result = instance.getParameterValue(paramName, parameters);
        assertEquals(expResult, result);
        // TODO review the generated test code and remove the default call to fail.
        fail("The test case is a prototype.");
    }

    /**
     * Test of getAssocTableHtml method, of class Update.
     */
    @Test
    public void testGetAssocTableHtml() {
        System.out.println("getAssocTableHtml");
        Vector<Vector> subTables = null;
        /*String[] subTables = {"ACC_Biomaterial",
                "ACC_Chemoembolisation",
                "ACC_Chemotherapy_Regimen",
                "ACC_Chemotherapy",
                "ACC_FollowUp_Organs",
                "ACC_FollowUp",
                "ACC_Mitotane",
                "ACC_Pathology",
                "ACC_Radiofrequency_Loc",
                "ACC_Radiofrequency",
                "ACC_Radiotherapy_Loc",
                "ACC_Radiotherapy",
                "ACC_Surgery_First",
                "ACC_Surgery_Extended",
                "ACC_Surgery"};*/
        String dbid = "1";
        String dbn = "ACC";
        String centerid = "GBLE";
        String pid = "0002";
        Update instance = new Update();
        String expResult = "";
        String result = instance.getAssocTableHtml(subTables, dbid, dbn, centerid, pid);
        assertEquals(expResult, result);
        
    }

    /**
     * Test of compileSubTableList method, of class Update.
     */
    @Test
    public void testCompileSubTableList() {
        System.out.println("compileSubTableList");
        String dbn = "";
        String username = "";
        Update instance = new Update();
        Vector<Vector> expResult = null;
        Vector<Vector> result = instance.compileSubTableList(dbn,username,"","",null);
        assertEquals(expResult, result);
        // TODO review the generated test code and remove the default call to fail.
        fail("The test case is a prototype.");
    }

    /**
     * Test of getAssocInfoHtml method, of class Update.
     */
    @Test
    public void testGetAssocInfoHtml() {
        System.out.println("getAssocInfoHtml");
        String dbid = "";
        String dbn = "";
        String centerid = "";
        String pid = "";
        Statement stmt = null;
        Connection conn = null;
        Update instance = new Update();
        String expResult = "";
        String result = instance.getAssocInfoHtml(dbid, dbn, centerid, pid, conn);
        assertEquals(expResult, result);
        // TODO review the generated test code and remove the default call to fail.
        fail("The test case is a prototype.");
    }
}
