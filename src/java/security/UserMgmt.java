/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package security;

import java.security.MessageDigest;
import java.security.SecureRandom;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.Statement;
import org.apache.log4j.Logger;

import user.UserBean;

/**
 *
 * @author astell
 */
public class UserMgmt {
    
    private static final Logger logger = Logger.getLogger(UserMgmt.class);

    public UserMgmt() {
    }

    private Statement connect(String dbName, String host, String username, String password) throws Exception {

        String connectionURL = "jdbc:mysql://" + host + ":3306/" + dbName;

        Class.forName("com.mysql.jdbc.Driver").newInstance();        
        Connection connection = DriverManager.getConnection(connectionURL, username, password);
        Statement statement = connection.createStatement();

        return statement;
    }

    public String[] getExtraUserInfo(String username, String dbName, String host, String dbUsername, String dbPassword) {

        String[] extraInfo = new String[2];
        ResultSet rs = null;

        try {
            Statement statement = this.connect(dbName, host, dbUsername, dbPassword);

            String sql = "SELECT email_address,institution FROM User WHERE username='" + username + "';";
            rs = statement.executeQuery(sql);

            if (rs.next()) {
                extraInfo[0] = rs.getString(1);
                extraInfo[1] = rs.getString(2);
            }
            rs.close();
            statement.close();

        } catch (Exception e) {
            System.out.println("Error (getExtraUserInfo): " + e.getMessage());
        }
        return extraInfo;
    }

    public String superUserList(String dbName, String host, String dbUsername, String dbPassword) {

        String outputListHtml = "";
        String sql = "SELECT DISTINCT email_address FROM User WHERE (email_address!='astell@unimelb.edu.au' AND email_address!='') ORDER BY email_address ASC;";
        ResultSet rs = null;

        try {
            Statement statement = this.connect(dbName, host, dbUsername, dbPassword);

            rs = statement.executeQuery(sql);

            if (rs != null) {
                while (rs.next()) {
                    String userStr = rs.getString(1);
                    outputListHtml += "<option value='" + userStr + "'>" + userStr + "</option>";
                }
            }

            rs.close();
            statement.close();
        } catch (Exception e) {
            System.out.println("Error (superUserList): " + e.getMessage());
        }
        return outputListHtml;
    }

    public void updateDetails(String username, String dbName, String updateOption, String updateValue, String host, String dbUsername, String dbPassword) {

        String updatePhrase = "";
        if (updateOption.equals("password")) {
            String salt = this.generateSaltValue();
            String inputToHash = salt + updateValue;
            String hashedInput = "";
            
            try{
                hashedInput = this.toSHA256(inputToHash.getBytes("UTF-8"));            
            }catch(Exception e){
                logger.debug("Error (updateDetails): " + e.getMessage());
            }
            
            if(!hashedInput.equals("")){
                updatePhrase = "password_sha2='" + hashedInput + "',salt='" + salt + "',password=''";
            }
        }/* else if (updateOption.equals("email")) {
            updatePhrase = "email_address='" + updateValue + "'";
        }*/ else if (updateOption.equals("institution")) {
            updatePhrase = "institution='" + updateValue + "'";
        }

        try {
            Statement statement = this.connect(dbName, host, dbUsername, dbPassword);
            String sql = "";
            if (!updateOption.equals("name")) {
                sql = "UPDATE User SET " + updatePhrase + " WHERE email_address='" + username + "';";
                int updated = statement.executeUpdate(sql);
            } else {
                String forename = updateValue.substring(0, updateValue.indexOf("-"));
                String surname = updateValue.substring(updateValue.indexOf("-") + 1, updateValue.length());
                String sql1 = "UPDATE User SET forename='" + forename + "' WHERE email_address='" + username + "'; ";
                String sql2 = "UPDATE User SET surname='" + surname + "' WHERE email_address='" + username + "';";
                int updated1 = statement.executeUpdate(sql1);
                int updated2 = statement.executeUpdate(sql2);
            }
            logger.debug("sql: " + sql);
            
            statement.close();

        } catch (Exception e) {
            logger.debug("Error (updateDetails): " + e.getMessage());
        }

    }

    public UserBean changeUser(String userselect, String dbName, boolean isSuperUser, UserBean user, String host, String dbUsername, String dbPassword) {

        //UserBean user = new UserBean();
        String emailUsername = "";
        String forename = "";
        String surname = "";
        String country = "";
        String center = "";

        ResultSet rs = null;
        try {
            Statement statement = this.connect(dbName, host, dbUsername, dbPassword);
            String sql = "";

            //Retrieve all the information associated with the selected user
            sql = "SELECT user_id, username, forename, surname, email_address, institution, country, center FROM User WHERE email_address='" + userselect + "';";
            rs = statement.executeQuery(sql);
            
            //System.out.println("sql: " + sql);

            if (rs.next()) {
                emailUsername = rs.getString(5); //changing this to email_address
                forename = rs.getString(3);
                surname = rs.getString(4);
                country = rs.getString(7);
                center = rs.getString(8);
            }
            rs.close();
            statement.close();

            //Now set these details in the user bean for this session
            user.setUsername(emailUsername);
            user.setForename(forename);
            user.setSurname(surname);
            user.setCountry(country);
            user.setCenter(center);
            user.setIsSuperUser(isSuperUser);
        } catch (Exception e) {
            System.out.println("Error (changeUser): " + e.getMessage());
        }
        return user;
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
