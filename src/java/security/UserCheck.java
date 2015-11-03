/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package security;

/**
 *
 * @author astell
 */
import java.sql.*;
import java.text.Format;
import java.text.SimpleDateFormat;

import javax.servlet.http.HttpSession;

import user.UserBean;

import java.sql.PreparedStatement;
import java.security.MessageDigest;
import java.security.SecureRandom;
import org.apache.commons.dbcp2.BasicDataSource;
import org.apache.log4j.Level;
import org.apache.log4j.Logger;
import org.apache.log4j.PropertyConfigurator;

public class UserCheck {
    
    private static final Logger logger = Logger.getLogger(UserCheck.class);

    public UserCheck() {
        logger.debug("Running user check (login request)...");        
    }
    
    private Connection connect(String dbName, String host, String username, String password) throws Exception {

        String connectionURL = "jdbc:mysql://" + host + ":3306/" + dbName;

        //Class.forName("com.mysql.jdbc.Driver").newInstance();
        //Connection connection = DriverManager.getConnection(connectionURL, username, password);
        
            BasicDataSource ds = new BasicDataSource();            
            ds.setDriverClassName("com.mysql.jdbc.Driver");            
            ds.setUsername(username);            
            ds.setPassword(password);            
            ds.setUrl(connectionURL);
        
            return ds.getConnection();
        //return connection;
    }

    public int checkUserDetails(String emailUsername, String password, String dbName, String host, String dbUsername, String dbPassword, Connection secConn) {

        /**
        * responseFlag:
        * 
        * 0 = user present, account active, membership up-to-date (OK login)
        * 1 = user not present (i.e. credentials are wrong)
        * 2 = user present, account not active
        * 3 = user present, account active, membership out-of-date
        */
        int responseFlag = 1;
        ResultSet rs = null;
        try {
            
            if(secConn == null){
                secConn = this.connect(dbName, host, dbUsername, dbPassword);
            }
            //String sql = "SELECT user_id, username, password, password_sha2, salt FROM User WHERE username=?;";            
            //String sql = "SELECT user_id, username, password, password_sha2, salt, User.active, Membership.active_status FROM User,Membership WHERE User.user_id=Membership.user_id AND User.username=?;";
            String sql = "SELECT User.user_id, username, password, password_sha2, salt, User.active, Membership.active_status FROM User,Membership WHERE User.user_id=Membership.user_id AND User.email_address=?;";
            
            PreparedStatement ps = secConn.prepareStatement(sql);
            ps.setString(1,emailUsername);            
            rs = ps.executeQuery();
            
            //Test if the username is listed
            int userCount = 0;
            boolean accountActive = false;
            boolean membershipCurrent = false;
            if(rs != null){
                if(rs.next()){
                    
                    accountActive = rs.getString(6).equals("yes");
                    membershipCurrent = rs.getString(7).equals("Active");
                
                //Test the SHA2 entry
                String sha2entry = rs.getString(4);
                if(sha2entry == null){
                    sha2entry = "";
                }
                
                
                if(!sha2entry.equals("")){
                    //Hash the password (+salt) with SHA2 algorithm
                    String salt = rs.getString(5);
                    String hashInput = salt + password;
                    logger.debug("Login credentials checked against SHA-256 (" + emailUsername + ")...");
                    String hashedInput = this.toSHA256(hashInput.getBytes("UTF-8"));
                    
                    if(hashedInput.equals(sha2entry)){
                        userCount++;
                    }                    
                    
                }else{
                    
                    //Match on SHA1 entry
                    String hashedPassword = this.toSHA1(password.getBytes("UTF-8"));                    
                    String sha1entry = rs.getString(3);
                    if(hashedPassword.equals(sha1entry)){
                        userCount++;
                        
                        //Now set the new password in SHA2, as well as the newly-generated salt value, and blank the sha1 entry
                        String salt = this.generateSaltValue();                    
                        String hashInput = salt + password;
                        logger.debug("Login credentials checked against SHA-256 (new assignment from SHA-1)...");
                        String hashedInput = this.toSHA256(hashInput.getBytes("UTF-8"));                    
                    
                        //String updateSha2Sql = "UPDATE User SET password_sha2=?,salt=?,password='' WHERE username=?;";
                        String updateSha2Sql = "UPDATE User SET password_sha2=?,salt=?,password='' WHERE email_address=?;";
                        PreparedStatement sha2ps = secConn.prepareStatement(updateSha2Sql);
                        sha2ps.setString(1,hashedInput);
                        sha2ps.setString(2,salt);
                        sha2ps.setString(3,emailUsername);
                        int updateResult = sha2ps.executeUpdate();
                    }
                }
                }else{
                    userCount = 0;
                }
            }else{
                userCount = 0;
            }            
            rs.close();
            
            //If they've made it this far and userCount is 1, then the presence and credentials are good
            if(userCount == 1){                
                //Now check the active flag
                if(!accountActive){
                    responseFlag = 2; //Account is deactivated
                }else{
                    //Account is active
                    if(!membershipCurrent){
                        responseFlag = 3; //Membership has lapsed
                    }else{
                        //Account membership is current
                        responseFlag = 0; //All good, can login
                    }
                }
            }else{
                responseFlag = 1; //Credentials are incorrect
            }
            
        } catch (Exception e) {
            logger.debug("(" + emailUsername + ") - Error (checkUserDetails - using password): " + e.getMessage());
        }
        
        //Log any unsuccessful login attempts
        if(responseFlag != 0){
            logger.debug("Login request unsuccessful (username='" + emailUsername + "')");        
            if(responseFlag == 1){
                logger.debug("Credentials incorrect / membership entry not present (username='" + emailUsername + "')");
            }else if(responseFlag == 2){
                logger.debug("Account is deactivated (username='" + emailUsername + "')");
            }else if(responseFlag == 1){
                logger.debug("Membership has lapsed (username='" + emailUsername + "')");
            }
        }
        
        return responseFlag;
    }
    
    public int checkUserDetails(String username, String dbName, String host, String dbUsername, String dbPassword, Connection secConn) {

        int userCount = 0;
        ResultSet rs = null;

        try {
            
            if(secConn == null){
                secConn = this.connect(dbName, host, dbUsername, dbPassword);            
            }
            //String sql = "SELECT user_id, username, forename, surname, role, country, center FROM User WHERE username=?;";
            String sql = "SELECT user_id, username, forename, surname, role, country, center FROM User WHERE email_address=?;";
            
            PreparedStatement ps = secConn.prepareStatement(sql);
            ps.setString(1,username);                        
            rs = ps.executeQuery();
            
            if (rs != null) {
                while (rs.next()) {
                    userCount++;
                }
            }
            rs.close();            
        } catch (Exception e) {
            logger.debug("(" + username + ") - Error (checkUserDetails - no password): " + e.getMessage());
        }
        
        //Log any unsuccessful login attempts
        if(userCount != 1){
            logger.debug("Login request unsuccessful (username='" + username + "')");        
        }
        
        return userCount;
    }

    public UserBean setUserDetails(String emailUsername, String password, String dbName, HttpSession session, UserBean user, String host, String dbUsername, String dbPassword, Connection conn) {

        String username = "";
        String forename = "";
        String surname = "";
        String role = "";
        String country = "";
        String center = "";
        String emailAddress = "";

        ResultSet rs = null;
        try {
            //Statement statement = this.connect(dbName);
            if(conn == null){
                conn = this.connect(dbName, host, dbUsername, dbPassword);
            }
            String sql = "SELECT user_id, username, password, password_sha2, salt, forename, surname, role, country, center, email_address FROM User WHERE email_address=?;";
            
            PreparedStatement ps = conn.prepareStatement(sql);
            ps.setString(1,emailUsername);            
            rs = ps.executeQuery();

            if (rs.next()) {
                String saltIn = rs.getString(5);                            
                String inputToCheck = saltIn + password;
                String hashedInput = this.toSHA256(inputToCheck.getBytes("UTF-8"));
            
                String sha2entry = rs.getString(4);                
                if(hashedInput.equals(sha2entry)){
                    username = rs.getString(2);
                    forename = rs.getString(6);
                    surname = rs.getString(7);
                    role = rs.getString(8);
                    country = rs.getString(9);
                    center = rs.getString(10);
                    emailAddress = rs.getString(11);

                }
            }
            rs.close();
            conn.close();            
        } catch (Exception e) {
            logger.debug("(" + emailUsername + ") - Error (setUserDetails): " + e.getMessage());
        }

        //Now set these details in the user bean and return it to the JSP page
        //Set the UserBean
        user.setUsername(emailAddress);
        user.setForename(forename);
        user.setSurname(surname);
        user.setRole(role);
        user.setCountry(country);
        user.setCenter(center);
        user.setIsSuperUser(false);

        //Get the current time and set as session time
        Format formatter = new SimpleDateFormat("HH:mm");
        java.util.Date date = new java.util.Date(session.getLastAccessedTime());
        String currentTime = formatter.format(date);
        user.setSessionLogin(currentTime);
        
        //Log the successful login
        logger.debug("=== User '" + emailUsername + "' successfully logged in at " + currentTime + " ===");        
        return user;
    }
    
    public String toSHA1(byte[] convertme) {
        MessageDigest md = null;
        try {
            md = MessageDigest.getInstance("SHA1");
        }catch(Exception e) {
            e.printStackTrace();
        } 
        
        //return new String(md.digest(convertme));
        String convertedStr = byteArrayToHexString(md.digest(convertme));
        logger.debug("Login credentials checked against SHA-1...");
        return convertedStr;
    }
    
    public String toSHA256(byte[] convertme) {
        MessageDigest md = null;
        try {
            md = MessageDigest.getInstance("SHA-256");
        }catch(Exception e) {
            e.printStackTrace();
        } 
        
        //return new String(md.digest(convertme));
        String convertedStr = byteArrayToHexString(md.digest(convertme));        
        return convertedStr;
    }
    
    public String byteArrayToHexString(byte[] b) {
        String result = "";
        for (int i=0; i < b.length; i++) {
            result += Integer.toString( ( b[i] & 0xff ) + 0x100, 16).substring( 1 );
        }
    return result;
    }
    
    
    private String generateSaltValue(){
        
        int SALT_BYTE_SIZE = 24;
        String saltStr = "";
        byte[] salt = null;
        try{
            
            // Generate a random salt
            SecureRandom random = new SecureRandom();
            salt = new byte[SALT_BYTE_SIZE];
            random.nextBytes(salt);
        
        }catch(Exception e){
            logger.debug("Error (generateSaltValue): " + e.getMessage());
        }

        saltStr = salt.toString();        
        return saltStr;
    }

}
