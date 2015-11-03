/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package security;

import org.junit.AfterClass;
import org.junit.Test;
import static org.junit.Assert.*;
import org.junit.BeforeClass;
import user.UserBean;

/**
 *
 * @author astell
 */
public class UserMgmtTest {
    
    public UserMgmtTest() {
    }

    @BeforeClass
    public static void setUpClass() throws Exception {
    }

    @AfterClass
    public static void tearDownClass() throws Exception {
    }

    /**
     * Test of getExtraUserInfo method, of class UserMgmt.
     */
    @Test
    public void testGetExtraUserInfo() {
        System.out.println("getExtraUserInfo");
        String username = "";
        String dbName = "";
        UserMgmt instance = new UserMgmt();
        String[] expResult = null;
        String[] result = instance.getExtraUserInfo(username, dbName, "", "", "");
        assertEquals(expResult, result);
    }

    /**
     * Test of superUserList method, of class UserMgmt.
     */
    @Test
    public void testSuperUserList() {
        System.out.println("superUserList");
        String dbName = "";
        UserMgmt instance = new UserMgmt();
        String expResult = "";
        String result = instance.superUserList(dbName, "", "", "");
        assertEquals(expResult, result);
    }

    /**
     * Test of updateDetails method, of class UserMgmt.
     */
    @Test
    public void testUpdateDetails() {
        System.out.println("updateDetails");
        String username = "";
        String dbName = "";
        String updateOption = "";
        String updateValue = "";
        UserMgmt instance = new UserMgmt();
        instance.updateDetails(username, dbName, updateOption, updateValue, "", "", "");
    }

    /**
     * Test of changeUser method, of class UserMgmt.
     */
    @Test
    public void testChangeUser() {
        System.out.println("changeUser");
        String userselect = "";
        String dbName = "";
        UserBean user = null;
        boolean isSuperUser = false;
        UserMgmt instance = new UserMgmt();
        UserBean expResult = null;
        UserBean result = instance.changeUser(userselect, dbName, isSuperUser,user, "", "", "");
        assertEquals(expResult, result);
    }
}
