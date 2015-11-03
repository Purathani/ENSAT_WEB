/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package create_main;

import ConnectBean.ConnectBean;
import java.sql.Connection;
import java.sql.Statement;
import java.util.Vector;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.ServletContext;
import org.junit.AfterClass;
import org.junit.Test;
import static org.junit.Assert.*;
import org.junit.BeforeClass;

/**
 *
 * @author astell
 */
public class CreateTest {
    
    static Connection conn = null;
    static Statement stmt = null;
    
    
    public CreateTest() {
    }

    @BeforeClass
    public static void setUpClass() throws Exception {
        System.out.println("Setting up CreateTest...");
        
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
        System.out.println("Finishing CreateTest...");        
        stmt.close();
        conn.close();                
    }

    /**
     * Test of getLineColour method, of class Create.
     */
    @Test
    public void testGetLineColour() {
        System.out.println("getLineColour");
        String dbn = "";
        Create instance = new Create();
        String expResult = "";
        String result = instance.getLineColour(dbn);
        assertEquals(expResult, result);
    }

    /**
     * Test of getParameters method, of class Create.
     */
    @Test
    public void testGetParameters() {
        System.out.println("getParameters");
        String[] _tablenames = null;
        HttpServletRequest request = null;
        ServletContext context = null;
        Create instance = new Create();
        Vector expResult = null;
        Vector result = instance.getParameters(_tablenames, request,null);
        assertEquals(expResult, result);
    }

    /**
     * Test of getMenus method, of class Create.
     */
    @Test
    public void testGetMenus() {
        System.out.println("getMenus");
        Vector<Vector> parameters = null;
        Create instance = new Create();
        Vector expResult = null;
        ServletContext context = null;
        Vector result = instance.getMenus(parameters,null);
        assertEquals(expResult, result);
    }

    /**
     * Test of getDynamicMenus method, of class Create.
     */
    @Test
    public void testGetDynamicMenus() {
        System.out.println("getDynamicMenus");
        Vector<Vector> parameters = null;
        
        Vector<Vector> menus = new Vector<Vector>();
        String userCenter = "GYMU";
        Create instance = new Create();
        
        ServletContext context = null;
        String[] tablenames = {"Identification"};
        parameters = instance.getParameters(tablenames, null, null);
        
        Vector expResult = null;
        Vector result = instance.getDynamicMenus(parameters, menus, userCenter, null,null,"","",null);
        assertEquals(expResult, result);
    }

    /**
     * Test of getParameterHtml method, of class Create.
     */
    @Test
    public void testGetParameterHtml() {
        System.out.println("getParameterHtml");
        Vector<Vector> parameters = null;
        Vector<Vector> menus = null;
        String lineColour = "";
        Create instance = new Create();
        ServletContext context = null;
        
        String userCenter = "GBBI";
        String dbn = "APA";
        String[] tablenames = {"APA_PatientHistory"};
        parameters = instance.getParameters(tablenames, null, null);
        menus = instance.getMenus(parameters, null);
        menus = instance.getDynamicMenus(parameters, menus, userCenter, null,null,"","",null);
        
        String expResult = "";
        String result = instance.getParameterHtml(parameters, menus, lineColour, dbn, null,"");
        assertEquals(expResult, result);
    }

    /**
     * Test of getLastPageParamHtml method, of class Create.
     */
    @Test
    public void testGetLastPageParamHtml() {
        System.out.println("getLastPageParamHtml");
        Vector<Vector> parameters = null;
        Create instance = new Create();
        String expResult = "";
        String dbn = "";
        String result = instance.getLastPageParamHtml(parameters,dbn, null);
        assertEquals(expResult, result);
    }

    /**
     * Test of getHiddenParams method, of class Create.
     */
    @Test
    public void testGetHiddenParams() {
        System.out.println("getHiddenParams");
        Vector<String> lastPageParamNames = null;
        Vector<String> lastPageParamValues = null;
        Create instance = new Create();
        String expResult = "";
        String dbn = "";
        String result = instance.getHiddenParams(lastPageParamNames, lastPageParamValues, dbn, null);
        assertEquals(expResult, result);
    }

    /**
     * Test of getLastPageParamConfirmHtml method, of class Create.
     */
    @Test
    public void testGetLastPageParamConfirmHtml() {
        System.out.println("getLastPageParamConfirmHtml");
        Vector<Vector> parameters = null;
        String dbn = "ACC";
        String lineColour = "";
        Create instance = new Create();
        String expResult = "";
        HttpServletRequest request = null;
        String result = instance.getLastPageParamConfirmHtml(parameters, lineColour, request, dbn);
        assertEquals(expResult, result);
    }

    /**
     * Test of getNextId method, of class Create.
     */
    @Test
    public void testGetNextId() {
        System.out.println("getNextId");
        String centerId = "";
        String dbName = "";
        ServletContext context = null;
        Create instance = new Create();
        int expResult = 0;
        int result = instance.getNextId(centerId, null);
        assertEquals(expResult, result);
    }

    /**
     * Test of setNextId method, of class Create.
     */
    @Test
    public void testSetNextId() {
        System.out.println("setNextId");
        String centerId = "";
        ServletContext context = null;
        int nextId = 0;
        Create instance = new Create();
        instance.setNextId(centerId, nextId, null);
    }

    /**
     * Test of executeParameterUpdate method, of class Create.
     */
    @Test
    public void testExecuteParameterUpdate() {
        System.out.println("executeParameterUpdate");
        String username = "anthony";
        String dbn = "ACC";
        String centerId = "GBBI";
        int nextId = 24;
        String tablename = "ACC_DiagnosticProcedures";
        
        Create instance = new Create();
        String[] tablenames = {"Identification","ACC_DiagnosticProcedures","ACC_TumorStaging"};
        
        Vector<Vector> parameters = instance.getParameters(tablenames, null, null);
        Statement statement = stmt;
        HttpServletRequest request = null;
        
        instance.executeParameterUpdate(username, dbn, nextId, centerId, tablename, parameters, conn, request);
    }

    /**
     * Test of getParameterValue method, of class Create.
     */
    @Test
    public void testGetParameterValue() {
        System.out.println("getParameterValue");
        String paramName = "";
        Vector<Vector> parameters = null;
        Create instance = new Create();
        String expResult = "";
        String result = instance.getParameterValue(paramName, parameters);
        assertEquals(expResult, result);
    }
}
