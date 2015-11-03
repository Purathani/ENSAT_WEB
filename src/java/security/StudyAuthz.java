/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package security;

/**
 *
 * @author anthony
 */
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.Statement;
import java.sql.PreparedStatement;
import java.sql.ResultSet;

import java.util.List;
import java.util.ArrayList;
import org.apache.log4j.Level;
import org.apache.log4j.Logger;
import org.apache.log4j.PropertyConfigurator;

public class StudyAuthz {

    private static final Logger logger = Logger.getLogger(StudyAuthz.class);

    public StudyAuthz() {
    }

    private Statement connect() throws Exception {
        //String connectionURL = "jdbc:mysql://localhost:3306/ensat_security";
        String connectionURL = "jdbc:mysql://192.168.101.63:3306/ensat_security";
        Class.forName("com.mysql.jdbc.Driver").newInstance();
        
        Connection connection = DriverManager.getConnection(connectionURL, "root", "ps4Xy2a");
        Statement statement = connection.createStatement();
        return statement;
    }

    public String getRecordUploader(String pid, String centerid, Statement stmt) {
        ResultSet rs = null;
        String uploader = "";
        String sql = "SELECT uploader FROM Identification WHERE ensat_id=" + pid + " AND center_id='" + centerid + "';";
        try {
            rs = stmt.executeQuery(sql);
            if (rs != null) {
                while (rs.next()) {
                    uploader = rs.getString(1);
                }
            }
        } catch (Exception e) {
            System.out.println("Error (getRecordUploader): " + e.getMessage());
        }
        return uploader;
    }

    public ArrayList<String> getPatientIdList(String study) {

        ArrayList<String> patientIdList = new ArrayList<String>();

        if (study.equals("tma")) {

            patientIdList.add("FRPA1-0001");
            patientIdList.add("FRPA1-0002");
            patientIdList.add("FRPA1-0003");
            patientIdList.add("FRPA1-0004");
            patientIdList.add("FRPA1-0005");
            patientIdList.add("FRPA1-0006");
            patientIdList.add("FRPA1-0007");
            patientIdList.add("FRPA1-0008");
            patientIdList.add("FRPA1-0009");
            patientIdList.add("FRPA1-0010");
            patientIdList.add("FRPA1-0011");
            patientIdList.add("FRPA1-0013");
            patientIdList.add("FRPA1-0014");
            patientIdList.add("FRPA1-0015");
            patientIdList.add("FRPA1-0016");
            patientIdList.add("FRPA1-0017");
            patientIdList.add("FRPA1-0018");
            patientIdList.add("FRPA1-0019");
            patientIdList.add("FRPA1-0020");
            patientIdList.add("FRPA1-0022");
            patientIdList.add("FRPA1-0023");
            patientIdList.add("FRPA1-0024");
            patientIdList.add("FRPA1-0025");
            patientIdList.add("FRPA1-0028");
            patientIdList.add("FRPA1-0029");
            patientIdList.add("FRPA1-0030");
            patientIdList.add("FRPA1-0031");
            patientIdList.add("FRPA1-0032");
            patientIdList.add("FRPA1-0033");
            patientIdList.add("FRPA1-0034");
            patientIdList.add("FRPA1-0036");
            patientIdList.add("FRPA1-0037");
            patientIdList.add("FRPA1-0038");
            patientIdList.add("FRPA1-0039");
            patientIdList.add("FRPA1-0040");
            patientIdList.add("FRPA1-0041");
            patientIdList.add("FRPA1-0042");
            patientIdList.add("FRPA1-0044");
            patientIdList.add("FRPA1-0047");
            patientIdList.add("FRPA1-0048");
            patientIdList.add("FRPA1-0049");
            patientIdList.add("FRPA1-0050");
            patientIdList.add("FRPA1-0051");
            patientIdList.add("FRPA1-0052");
            patientIdList.add("FRPA1-0053");
            patientIdList.add("FRPA1-0054");
            patientIdList.add("FRPA1-0055");
            patientIdList.add("FRPA1-0056");
            patientIdList.add("FRPA1-0057");
            patientIdList.add("FRPA1-0058");
            patientIdList.add("FRPA1-0059");
            patientIdList.add("FRPA1-0060");
            patientIdList.add("FRPA1-0061");
            patientIdList.add("FRPA1-0063");
            patientIdList.add("FRPA1-0064");
            patientIdList.add("FRPA1-0065");
            patientIdList.add("FRPA1-0066");
            patientIdList.add("FRPA1-0067");
            patientIdList.add("FRPA1-0068");
            patientIdList.add("FRPA1-0069");
            patientIdList.add("FRPA1-0070");
            patientIdList.add("FRPA1-0071");
            patientIdList.add("FRPA1-0073");
            patientIdList.add("FRPA1-0075");
            patientIdList.add("FRPA1-0076");
            patientIdList.add("FRPA1-0077");
            patientIdList.add("FRPA1-0078");
            patientIdList.add("FRPA1-0079");
            patientIdList.add("FRPA1-0080");
            patientIdList.add("FRPA1-0081");
            patientIdList.add("FRPA1-0082");
            patientIdList.add("FRPA1-0083");
            patientIdList.add("FRPA1-0084");
            patientIdList.add("FRPA1-0085");
            patientIdList.add("FRPA1-0086");
            patientIdList.add("FRPA1-0087");
            patientIdList.add("FRPA1-0089");
            patientIdList.add("FRPA1-0090");
            patientIdList.add("FRPA1-0091");
            patientIdList.add("FRPA1-0092");
            patientIdList.add("GYMU-0008");
            patientIdList.add("GYMU-0014");
            patientIdList.add("GYMU-0026");
            patientIdList.add("GYMU-0027");
            patientIdList.add("GYMU-0038");
            patientIdList.add("GYMU-0041");
            patientIdList.add("GYMU-0042");
            patientIdList.add("GYMU-0046");
            patientIdList.add("GYMU-0047");
            patientIdList.add("GYMU-0048");
            patientIdList.add("GYMU-0049");
            patientIdList.add("GYMU-0056");
            patientIdList.add("GYMU-0061");
            patientIdList.add("GYMU-0069");
            patientIdList.add("GYMU-0082");
            patientIdList.add("GYMU-0264");
            patientIdList.add("GYMU-0274");
            patientIdList.add("GYMU-0326");
            patientIdList.add("GYMU-0373");
            patientIdList.add("GYMU-0396");
            patientIdList.add("GYMU-0401");
            patientIdList.add("GYMU-0441");
            patientIdList.add("ITFL-0015");
            patientIdList.add("ITFL-0016");
            patientIdList.add("ITFL-0020");
            patientIdList.add("ITFL-0023");
            patientIdList.add("ITFL-0062");
            patientIdList.add("ITFL-0063");
            patientIdList.add("ITFL-0067");
            patientIdList.add("ITFL-0068");
            patientIdList.add("ITFL-0069");
            patientIdList.add("ITFL-0070");
            patientIdList.add("ITFL-0077");
            patientIdList.add("ITFL-0082");
            patientIdList.add("ITFL-0082");
            patientIdList.add("ITFL-0096");
            patientIdList.add("ITFL-0098");
            patientIdList.add("ITFL-0104");
            patientIdList.add("ITFL-0169");
            patientIdList.add("ITFL-0170");
            patientIdList.add("ITFL-0171");
            patientIdList.add("ITFL-0172");
            patientIdList.add("ITFL-0173");
            patientIdList.add("ITFL-0174");
            patientIdList.add("ITFL-0175");
            patientIdList.add("ITFL-0176");
            patientIdList.add("ITFL-0177");
            patientIdList.add("ITFL-0178");
            patientIdList.add("ITFL-0179");
            patientIdList.add("ITFL-0180");
            patientIdList.add("ITFL-0181");
            patientIdList.add("ITFL-0182");
            patientIdList.add("ITFL-0183");
            patientIdList.add("ITFL-0184");
            patientIdList.add("ITFL-0185");
            patientIdList.add("SPMA-0006");
            patientIdList.add("SPMA-0007");
            patientIdList.add("SPMA-0026");
            patientIdList.add("SPMA-0040");
            patientIdList.add("SPMA-0041");
            patientIdList.add("SPMA-0045");
            patientIdList.add("SPMA-0048");
            patientIdList.add("SPMA-0051");
            patientIdList.add("SPMA-0052");
            patientIdList.add("SPMA-0053");
            patientIdList.add("SPMA-0053");
            patientIdList.add("SPMA-0054");
            patientIdList.add("SPMA-0057");
            patientIdList.add("SPMA-0059");
            patientIdList.add("SPMA-0064");
            patientIdList.add("SPMA-0067");
            patientIdList.add("SPMA-0076");
            patientIdList.add("SPMA-0083");
            patientIdList.add("SPMA-0084");
            patientIdList.add("SPMA-0086");
            patientIdList.add("SPMA-0087");
            patientIdList.add("SPMA-0088");
            patientIdList.add("SPMA-0089");
            patientIdList.add("SPMA-0090");
            patientIdList.add("SPMA-0091");
            patientIdList.add("SPMA-0092");
            patientIdList.add("SPMA-0093");
            patientIdList.add("SPMA-0095");
            patientIdList.add("SPMA-0096");
            patientIdList.add("SPMA-0097");
            patientIdList.add("SPMA-0099");
            patientIdList.add("SPMA-0099");
            patientIdList.add("SPMA-0100");
            patientIdList.add("SPMA-0104");
            patientIdList.add("SPMA-0109");
            patientIdList.add("SPMA-0121");
            patientIdList.add("SPMA-0123");
            patientIdList.add("SPMA-0124");
            patientIdList.add("SPMA-0125");
            patientIdList.add("SPMA-0126");
            patientIdList.add("SPMA-0127");            
            patientIdList.add("SPMA-0135");
            patientIdList.add("SPMA-0136");
            patientIdList.add("SPMA-0137");
            patientIdList.add("SPMA-0138");
            patientIdList.add("SPMA-0139");
            patientIdList.add("SPMA-0140"); 
            patientIdList.add("SPMA-0141");
            patientIdList.add("NLRO-0001");
            patientIdList.add("NLRO-0002");
            patientIdList.add("NLRO-0003");
            patientIdList.add("NLRO-0004");
            patientIdList.add("NLRO-0005");
            patientIdList.add("NLRO-0006");
            patientIdList.add("NLRO-0007");

        } else if (study.equals("accstudy")) {

            patientIdList.add("GYWU-0055");
            patientIdList.add("GYWU-0006");
            patientIdList.add("GYWU-0031");
            patientIdList.add("GYWU-0319");
            patientIdList.add("GYWU-0597");
            patientIdList.add("GYWU-0615");
            patientIdList.add("GYWU-0171");
            patientIdList.add("GYWU-0020");
            patientIdList.add("GYMU-0312");
            patientIdList.add("GYWU-0052");
            patientIdList.add("GYWU-0011");
            patientIdList.add("GYWU-0767");
            patientIdList.add("GYWU-0154");
            patientIdList.add("GYWU-0659");
            patientIdList.add("GYWU-0810");
            patientIdList.add("GYWU-0016");
            patientIdList.add("GYWU-0002");
            patientIdList.add("GYWU-0015");
            patientIdList.add("GYWU-0380");
            patientIdList.add("GYWU-0523");
            patientIdList.add("GYWU-0401");
            patientIdList.add("GYWU-0577");
            patientIdList.add("GYWU-0500");
            patientIdList.add("GYWU-0360");
            patientIdList.add("GYWU-0364");
            patientIdList.add("GYWU-0516");
            patientIdList.add("GYWU-0048");
            patientIdList.add("GYWU-0215");
            patientIdList.add("GYWU-0106");
            patientIdList.add("GYWU-0371");
            patientIdList.add("GYWU-0012");
            patientIdList.add("GYWU-0549");
            patientIdList.add("GYWU-0368");
            patientIdList.add("GYWU-0560");
            patientIdList.add("GYWU-0375");
            patientIdList.add("GYWU-0439");
            patientIdList.add("GYWU-0040");
            patientIdList.add("GYMU-0224");
            patientIdList.add("GYWU-0486");
            patientIdList.add("GYWU-0319");
            patientIdList.add("GYMU-0256");
            patientIdList.add("GYMU-0250");
            patientIdList.add("GYWU-0592");
            patientIdList.add("GYWU-0046");
            patientIdList.add("GYWU-0022");
            patientIdList.add("GYWU-0136");
            patientIdList.add("GYWU-0077");
            patientIdList.add("GYWU-0061");
            patientIdList.add("ITFL-0033");
            patientIdList.add("ITFL-0040");
            patientIdList.add("ITFL-0031");
            patientIdList.add("ITFL-0038");
            patientIdList.add("ITFL-0034");
            patientIdList.add("ITFL-0035");
            patientIdList.add("ITFL-0045");
            patientIdList.add("ITFL-0027");
            patientIdList.add("ITFL-0037");
            patientIdList.add("ITPD-0051");
            patientIdList.add("ITPD-0050");
            patientIdList.add("FRPA3-0164");
            patientIdList.add("FRPA3-0097");
            patientIdList.add("FRPA3-0160");
            patientIdList.add("FRPA3-0025");
            patientIdList.add("FRPA3-0016");
            patientIdList.add("FRPA3-0107");
            patientIdList.add("FRPA3-0245");
            patientIdList.add("FRPA3-0070");
            patientIdList.add("FRPA3-0011");
            patientIdList.add("FRPA3-0244");
            patientIdList.add("FRPA3-0099");
        }

        return patientIdList;
    }

    public List<String> getCountries() {

        List<String> countries = new ArrayList<String>();
        String sql = "SELECT country FROM User";
        try {
            Statement stmt = this.connect();
            ResultSet rs = stmt.executeQuery(sql);

            while (rs.next()) {
                String countryIn = rs.getString(1);
                countries.add(countryIn);
            }

            rs.close();
            stmt.close();
        } catch (Exception e) {
            System.out.println("Error (getCountries): " + e.getMessage());
        }

        return countries;
    }

    public List<String> getUsernames() {

        List<String> usernames = new ArrayList<String>();
        String sql = "SELECT username FROM User";
        try {
            Statement stmt = this.connect();
            ResultSet rs = stmt.executeQuery(sql);
            while (rs.next()) {
                String usernameIn = rs.getString(1);
                usernames.add(usernameIn);
            }
            rs.close();
            stmt.close();
        } catch (Exception e) {
            System.out.println("Error (getUsernames): " + e.getMessage());
        }

        return usernames;
    }

    private String standardisePid(String pid) {
        String pidOut = "";
        if (pid.length() == 3) {
            pidOut = "0" + pid;
        } else if (pid.length() == 2) {
            pidOut = "00" + pid;
        } else if (pid.length() == 1) {
            pidOut = "000" + pid;
        } else {
            pidOut = pid;
        }
        return pidOut;
    }
}
