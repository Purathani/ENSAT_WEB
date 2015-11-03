/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package security;

import java.sql.Statement;
import org.junit.AfterClass;
import org.junit.Test;
import static org.junit.Assert.*;
import org.junit.BeforeClass;

/**
 *
 * @author astell
 */
public class AuthzTest {
    
    public AuthzTest() {
    }

    @BeforeClass
    public static void setUpClass() throws Exception {
    }

    @AfterClass
    public static void tearDownClass() throws Exception {
    }

    /**
     * Test of setRecordEditable method, of class Authz.
     */
    @Test
    public void testSetRecordEditable() {
        System.out.println("setRecordEditable");
        boolean _recordEditable = false;
        Authz instance = new Authz();
        instance.setRecordEditable(_recordEditable);
    }

    /**
     * Test of getRecordUploader method, of class Authz.
     */
    @Test
    public void testGetRecordUploader() {
        System.out.println("getRecordUploader");
        String pid = "";
        String centerid = "";
        Statement stmt = null;
        Authz instance = new Authz();
        String expResult = "";
        String result = instance.getRecordUploader(pid, centerid, stmt);
        assertEquals(expResult, result);
    }

    /**
     * Test of getRecordEditable method, of class Authz.
     */
    @Test
    public void testGetRecordEditable() {
        System.out.println("getRecordEditable");
        Authz instance = new Authz();
        boolean expResult = false;
        boolean result = instance.getRecordEditable();
        assertEquals(expResult, result);
    }

    /**
     * Test of modifyRecordEditable method, of class Authz.
     */
    @Test
    public void testModifyRecordEditable() {
        System.out.println("modifyRecordEditable");
        String username = "";
        String uploader = "";
        String pid = "";
        String centerid = "";
        Authz instance = new Authz();
        instance.modifyRecordEditable(username, uploader, pid, centerid, null,null);
    }
}
