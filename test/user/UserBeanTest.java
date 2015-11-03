/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package user;

import org.junit.AfterClass;
import org.junit.Test;
import static org.junit.Assert.*;
import org.junit.BeforeClass;

/**
 *
 * @author astell
 */
public class UserBeanTest {
    
    public UserBeanTest() {
    }

    @BeforeClass
    public static void setUpClass() throws Exception {
    }

    @AfterClass
    public static void tearDownClass() throws Exception {
    }

    /**
     * Test of getUsername method, of class UserBean.
     */
    @Test
    public void testGetUsername() {
        System.out.println("getUsername");
        UserBean instance = new UserBean();
        String expResult = "";
        String result = instance.getUsername();
        assertEquals(expResult, result);
    }

    /**
     * Test of setUsername method, of class UserBean.
     */
    @Test
    public void testSetUsername() {
        System.out.println("setUsername");
        String _username = "";
        UserBean instance = new UserBean();
        instance.setUsername(_username);
    }

    /**
     * Test of getCountry method, of class UserBean.
     */
    @Test
    public void testGetCountry() {
        System.out.println("getCountry");
        UserBean instance = new UserBean();
        String expResult = "";
        String result = instance.getCountry();
        assertEquals(expResult, result);
    }

    /**
     * Test of setCountry method, of class UserBean.
     */
    @Test
    public void testSetCountry() {
        System.out.println("setCountry");
        String _country = "";
        UserBean instance = new UserBean();
        instance.setCountry(_country);
    }

    /**
     * Test of getCenter method, of class UserBean.
     */
    @Test
    public void testGetCenter() {
        System.out.println("getCenter");
        UserBean instance = new UserBean();
        String expResult = "";
        String result = instance.getCenter();
        assertEquals(expResult, result);
    }

    /**
     * Test of setCenter method, of class UserBean.
     */
    @Test
    public void testSetCenter() {
        System.out.println("setCenter");
        String _center = "";
        UserBean instance = new UserBean();
        instance.setCenter(_center);
    }

    /**
     * Test of getRole method, of class UserBean.
     */
    @Test
    public void testGetRole() {
        System.out.println("getRole");
        UserBean instance = new UserBean();
        String expResult = "";
        String result = instance.getRole();
        assertEquals(expResult, result);
    }

    /**
     * Test of setRole method, of class UserBean.
     */
    @Test
    public void testSetRole() {
        System.out.println("setRole");
        String _role = "";
        UserBean instance = new UserBean();
        instance.setRole(_role);
    }

    /**
     * Test of getSurname method, of class UserBean.
     */
    @Test
    public void testGetSurname() {
        System.out.println("getSurname");
        UserBean instance = new UserBean();
        String expResult = "";
        String result = instance.getSurname();
        assertEquals(expResult, result);
    }

    /**
     * Test of setSurname method, of class UserBean.
     */
    @Test
    public void testSetSurname() {
        System.out.println("setSurname");
        String _surname = "";
        UserBean instance = new UserBean();
        instance.setSurname(_surname);
    }

    /**
     * Test of getForename method, of class UserBean.
     */
    @Test
    public void testGetForename() {
        System.out.println("getForename");
        UserBean instance = new UserBean();
        String expResult = "";
        String result = instance.getForename();
        assertEquals(expResult, result);
    }

    /**
     * Test of setForename method, of class UserBean.
     */
    @Test
    public void testSetForename() {
        System.out.println("setForename");
        String _forename = "";
        UserBean instance = new UserBean();
        instance.setForename(_forename);
    }

    /**
     * Test of getSessionLogin method, of class UserBean.
     */
    @Test
    public void testGetSessionLogin() {
        System.out.println("getSessionLogin");
        UserBean instance = new UserBean();
        String expResult = "";
        String result = instance.getSessionLogin();
        assertEquals(expResult, result);
    }

    /**
     * Test of setSessionLogin method, of class UserBean.
     */
    @Test
    public void testSetSessionLogin() {
        System.out.println("setSessionLogin");
        String _sessionLogin = "";
        UserBean instance = new UserBean();
        instance.setSessionLogin(_sessionLogin);
    }

    /**
     * Test of getIsSuperUser method, of class UserBean.
     */
    @Test
    public void testGetIsSuperUser() {
        System.out.println("getIsSuperUser");
        UserBean instance = new UserBean();
        boolean expResult = false;
        boolean result = instance.getIsSuperUser();
        assertEquals(expResult, result);
    }

    /**
     * Test of setIsSuperUser method, of class UserBean.
     */
    @Test
    public void testSetIsSuperUser() {
        System.out.println("setIsSuperUser");
        boolean _isSuperUser = false;
        UserBean instance = new UserBean();
        instance.setIsSuperUser(_isSuperUser);
    }

    /**
     * Test of getSearchFilter method, of class UserBean.
     */
    @Test
    public void testGetSearchFilter() {
        System.out.println("getSearchFilter");
        UserBean instance = new UserBean();
        String expResult = "";
        String result = instance.getSearchFilter();
        assertEquals(expResult, result);
    }

    /**
     * Test of setSearchFilter method, of class UserBean.
     */
    @Test
    public void testSetSearchFilter() {
        System.out.println("setSearchFilter");
        String _searchFilter = "";
        UserBean instance = new UserBean();
        instance.setSearchFilter(_searchFilter);
    }
}
