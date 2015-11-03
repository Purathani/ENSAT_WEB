/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package security;

import javax.servlet.http.HttpSession;
import org.junit.AfterClass;
import org.junit.Test;
import static org.junit.Assert.*;
import org.junit.BeforeClass;
import user.UserBean;

/**
 *
 * @author astell
 */
public class UserCheckTest {
    
    public UserCheckTest() {
    }

    @BeforeClass
    public static void setUpClass() throws Exception {
    }

    @AfterClass
    public static void tearDownClass() throws Exception {
    }

    /**
     * Test of checkUserDetails method, of class UserCheck.
     */
    @Test
    public void testCheckUserDetails_3args() {
        System.out.println("checkUserDetails");
        String username = "";
        String password = "";
        String dbName = "";
        UserCheck instance = new UserCheck();
        int expResult = 0;
        int result = instance.checkUserDetails(username, password, dbName, "", "", "",null);
        assertEquals(expResult, result);
    }

    /**
     * Test of checkUserDetails method, of class UserCheck.
     */
    @Test
    public void testCheckUserDetails_String_String() {
        System.out.println("checkUserDetails");
        String username = "";
        String dbName = "";
        UserCheck instance = new UserCheck();
        int expResult = 0;
        int result = instance.checkUserDetails(username, dbName, "", "", "",null);
        assertEquals(expResult, result);
    }

    /**
     * Test of setUserDetails method, of class UserCheck.
     */
    @Test
    public void testSetUserDetails() {
        System.out.println("setUserDetails");
        String username = "";
        String password = "";
        String dbName = "";
        HttpSession session = null;
        UserBean user = null;
        UserCheck instance = new UserCheck();
        UserBean expResult = null;
        UserBean result = instance.setUserDetails(username, password, dbName, session, user, "", "", "",null);
        assertEquals(expResult, result);
    }
}
