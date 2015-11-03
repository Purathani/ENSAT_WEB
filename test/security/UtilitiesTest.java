/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package security;

import java.util.Enumeration;
import javax.servlet.http.HttpServletRequest;
import org.junit.AfterClass;
import org.junit.Test;
import static org.junit.Assert.*;
import org.junit.BeforeClass;

/**
 *
 * @author astell
 */
public class UtilitiesTest {
    
    public UtilitiesTest() {
    }

    @BeforeClass
    public static void setUpClass() throws Exception {
    }

    @AfterClass
    public static void tearDownClass() throws Exception {
    }

    /**
     * Test of passwordCheck method, of class Utilities.
     */
    @Test
    public void testPasswordCheck() {
        System.out.println("passwordCheck");
        String password1 = "";
        String password2 = "";
        Utilities instance = new Utilities();
        boolean expResult = false;
        boolean result = instance.passwordCheck(password1, password2);
        assertEquals(expResult, result);
    }

    /**
     * Test of emailCheck method, of class Utilities.
     */
    @Test
    public void testEmailCheck() {
        System.out.println("emailCheck");
        String email = "";
        Utilities instance = new Utilities();
        boolean expResult = false;
        boolean result = instance.emailCheck(email);
        assertEquals(expResult, result);
    }

    /**
     * Test of institutionCheck method, of class Utilities.
     */
    @Test
    public void testInstitutionCheck() {
        System.out.println("institutionCheck");
        String institution = "";
        Utilities instance = new Utilities();
        boolean expResult = false;
        boolean result = instance.institutionCheck(institution);
        assertEquals(expResult, result);
    }

    /**
     * Test of nameCheck method, of class Utilities.
     */
    @Test
    public void testNameCheck() {
        System.out.println("nameCheck");
        String surname = "";
        String forename = "";
        Utilities instance = new Utilities();
        boolean expResult = false;
        boolean result = instance.nameCheck(surname, forename);
        assertEquals(expResult, result);
    }

    /**
     * Test of checkInput method, of class Utilities.
     */
    @Test
    public void testCheckInput() {
        System.out.println("checkInput");
        Enumeration inputs = null;
        HttpServletRequest request = null;
        Utilities instance = new Utilities();
        boolean expResult = false;
        boolean result = instance.checkInput(inputs, request, "", "", "");
        assertEquals(expResult, result);
    }
}
