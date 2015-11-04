/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package summaryinfo;

import java.sql.*;

import org.junit.Assert.*;

import SortBean.EnsatList;

import org.apache.log4j.Level;
import org.apache.log4j.Logger;
import org.apache.log4j.PropertyConfigurator;

import java.util.Enumeration;
import java.util.Vector;
import java.util.StringTokenizer;

import ConnectBean.ConnectionAuxiliary;
import java.text.SimpleDateFormat;
import javax.servlet.ServletContext;
import javax.servlet.http.HttpServletRequest;
import java.util.Date;
import java.util.HashMap;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.Map;

/**
 *
 * @author astell
 */
public class SummaryInfo {

    private static final Logger logger = Logger.getLogger(SummaryInfo.class);

    private String username = "";
    private Vector<Vector> freezerPosToUpload = new Vector<Vector>();

    public SummaryInfo() {

    }

    public void setLogfileName(String logfileConfigName) {
        //Set up logger        
        logger.setLevel(Level.DEBUG);
        PropertyConfigurator.configure(logfileConfigName);
    }

    public void setUsername(String _username) {
        username = _username;
    }

    public void setFreezerPosToUpload(Vector<Vector> _freezerPosToUpload) {

        int posNum = _freezerPosToUpload.size();
        for (int i = 0; i < posNum; i++) {
            Vector<String> posIn = _freezerPosToUpload.get(i);
            Vector<String> posOut = new Vector<String>();
            int posInNum = posIn.size();
            for (int j = 0; j < posInNum; j++) {
                String pos = posIn.get(j);
                posOut.add(pos);
            }
            freezerPosToUpload.add(posOut);
        }
    }

    public Vector<Vector> getFreezerPosToUpload() {
        return freezerPosToUpload;
    }

    public String getRankingHtml(Connection conn, String[] ensatSections) {

        int[] activeCenterCount = new int[5];
        for (int i = 0; i < 5; i++) {
            if (i != 4) {
                activeCenterCount[i] = this.countActiveCenters(conn, ensatSections[i]);
                if (i == 0) {
                }
            } else {
                activeCenterCount[i] = this.countActiveCenters(conn, "");
            }
        }

        String outputStr = "";
        outputStr += "<table border=\"1\" cellpadding=\"5\">";
        outputStr += "<tr>";

        for (int i = 0; i < 5; i++) {

            outputStr += "<td valign=\"top\">";

            //Calculate the rankings for total patients here
            String[][] rankings = new String[3][10];
            rankings = this.rankActiveCenters(conn, ensatSections[i], activeCenterCount[i]);

            outputStr += "<table border=\"1\" cellpadding=\"5\">";
            outputStr += "<th colspan=\"3\">";

            if (i != 4) {
                outputStr += "<div align=\"center\">" + ensatSections[i] + "</div>";
            } else {
                outputStr += "<div align=\"center\">Total</div>";
            }
            outputStr += "</th>";
            outputStr += "<tr>";
            outputStr += "<td>";
            outputStr += "<em>#</em>";
            outputStr += "</td>";
            outputStr += "<td>";
            outputStr += "<em>Center</em>";
            outputStr += "</td>";
            outputStr += "<td>";
            outputStr += "<em>Patients</em>";
            outputStr += "</td>";
            outputStr += "</tr>";

            for (int k = 0; k < 10; k++) {
                if (!rankings[0][k].equals("") && !rankings[2][k].trim().equals("0")) {
                    outputStr += "<tr>";
                    for (int j = 0; j < 3; j++) {
                        if (!rankings[j][k].equals("")) {
                            outputStr += "<td>" + rankings[j][k] + "</td>";
                        }
                    }
                    outputStr += "</tr>";
                }
            }
            outputStr += "</table>";
            outputStr += "</td>";
        }
        outputStr += "</tr>";
        outputStr += "</table>";
        return outputStr;
    }

    public String getSummaryHtml(Connection conn, String[] ensatSections) {

        String outputStr = "";

        outputStr += "<table width=\"50%\" border=\"1\" cellpadding=\"5\">";
        outputStr += "<tr>";
        outputStr += "<th></th>";

        for (int i = 0; i < 5; i++) {
            if (i != 4) {
                outputStr += "<th><div align=\"center\">" + ensatSections[i] + "</div></th>";
            } else {
                outputStr += "<th><div align=\"center\">Total</div></th>";
            }
        }

        outputStr += "</tr>";
        outputStr += "<tr>";
        outputStr += "<td>Records</td>";

        int[] rowCount = new int[5];
        for (int i = 0; i < 5; i++) {
            if (i != 4) {
                rowCount[i] = this.countRecords(conn, ensatSections[i]);
            } else {
                rowCount[i] = this.countRecords(conn, "");
            }
            outputStr += "<td>";
            if (i == 4) {
                outputStr += "<strong>";
            }
            outputStr += rowCount[i];
            if (i == 4) {
                outputStr += "</strong>";
            }
            outputStr += "</td>";

        }
        outputStr += "</tr>";
        /*outputStr += "<tr>";
         outputStr += "<td>Patients Alive</td>";

         int[] aliveCount = new int[5];
         int overallAliveCount = 0;
         for (int i = 0; i < 5; i++) {
         if (i != 4) {
         int deadCount = this.countDeadPatients(stmt, ensatSections[i]);
         int lostToFollowupCount = this.countLostToFollowup(stmt, ensatSections[i]);
         aliveCount[i] = rowCount[i] - (deadCount+lostToFollowupCount);
         overallAliveCount += aliveCount[i];
         } else {
         aliveCount[i] = overallAliveCount;
         }
         outputStr += "<td>";
         if(i == 4){
         outputStr += "<strong>";
         }
         outputStr += aliveCount[i];
         if(i == 4){
         outputStr += "</strong>";
         }
         outputStr += "</td>";                        
         }
         outputStr += "</tr>";*/
        outputStr += "<tr>";
        outputStr += "<td>Biosamples</td>";

        int overallBiosampleCount = 0;
        int[] rowCountBiosamples = new int[5];
        for (int i = 0; i < 5; i++) {
            if (i != 4) {
                rowCountBiosamples[i] = this.countBiosamples(conn, ensatSections[i]);
                overallBiosampleCount += rowCountBiosamples[i];
            } else {
                rowCountBiosamples[i] = overallBiosampleCount;
            }
            outputStr += "<td>";
            if (i == 4) {
                outputStr += "<strong>";
            }
            outputStr += rowCountBiosamples[i];
            if (i == 4) {
                outputStr += "</strong>";
            }
            outputStr += "</td>";
        }

        outputStr += "</tr>";
        outputStr += "<tr>";
        outputStr += "<td>Clinical Annotations</td>";

        int overallClinicalAnnotCount = 0;
        int[] rowCountClinicalAnnots = new int[5];
        for (int i = 0; i < 5; i++) {
            if (i != 4) {
                rowCountClinicalAnnots[i] = this.countClinicalAnnotations(conn, ensatSections[i]);
                overallClinicalAnnotCount += rowCountClinicalAnnots[i];
            } else {
                rowCountClinicalAnnots[i] = overallClinicalAnnotCount;
            }

            outputStr += "<td>";
            if (i == 4) {
                outputStr += "<strong>";
            }
            outputStr += rowCountClinicalAnnots[i];
            if (i == 4) {
                outputStr += "</strong>";
            }
            outputStr += "</td>";
        }
        outputStr += "</tr>";
        outputStr += "<tr>";
        outputStr += "<td>Annotations Per Patient (Mean)</td>";

        for (int i = 0; i < 5; i++) {
            float annotationPerPatient = 0;
            String annotationPerPatientStr = "";
            if (rowCount[i] != 0) {
                annotationPerPatient = (float) rowCountClinicalAnnots[i] / rowCount[i];
            }
            annotationPerPatientStr = "" + annotationPerPatient;
            annotationPerPatientStr = this.formatDecimalPlaces(annotationPerPatientStr);

            outputStr += "<td>";
            if (i == 4) {
                outputStr += "<strong>";
            }
            outputStr += annotationPerPatientStr;
            if (i == 4) {
                outputStr += "</strong>";
            }
            outputStr += "</td>";
        }
        outputStr += "</tr>";
        outputStr += "<tr>";
        outputStr += "<td>Biosamples Per Patient (Mean)</td>";

        for (int i = 0; i < 5; i++) {
            float biosamplePerPatient = 0;
            String biosamplePerPatientStr = "";
            if (rowCount[i] != 0) {
                biosamplePerPatient = (float) rowCountBiosamples[i] / rowCount[i];
            }
            biosamplePerPatientStr = "" + biosamplePerPatient;
            biosamplePerPatientStr = this.formatDecimalPlaces(biosamplePerPatientStr);

            outputStr += "<td>";
            if (i == 4) {
                outputStr += "<strong>";
            }
            outputStr += biosamplePerPatientStr;
            if (i == 4) {
                outputStr += "</strong>";
            }
            outputStr += "</td>";

        }

        outputStr += "</tr>";
        outputStr += "<tr>";
        outputStr += "<td>Active Centers</td>";

        int[] activeCenterCount = new int[5];
        for (int i = 0; i < 5; i++) {
            if (i != 4) {
                activeCenterCount[i] = this.countActiveCenters(conn, ensatSections[i]);
            } else {
                activeCenterCount[i] = this.countActiveCenters(conn, "");
            }

            outputStr += "<td>";
            if (i == 4) {
                outputStr += "<strong>";
            }
            outputStr += activeCenterCount[i];
            if (i == 4) {
                outputStr += "</strong>";
            }
            outputStr += "</td>";

        }
        outputStr += "</tr>";
        outputStr += "</table>";
        return outputStr;
    }

    public int countRecords(Connection conn, String _database) {

        int recordCount = 0;
        String database = "" + _database;
        database = database.trim();
        String sql = "";
        if (!database.equals("")) {
            sql = "SELECT ensat_id, center_id FROM Identification WHERE ensat_database=?;";
        } else {
            sql = "SELECT ensat_id, center_id FROM Identification;";
        }
        try {

            PreparedStatement ps = conn.prepareStatement(sql);
            if (!database.equals("")) {
                ps.setString(1, database);
            }
            ResultSet rs = ps.executeQuery();
            while (rs.next()) {
                recordCount++;
            }
        } catch (Exception e) {
            logger.debug("ERROR: " + e.getMessage());
            recordCount = 0;
        }

        return recordCount;
    }

    public int countActiveCenters(Connection conn, String _database) {

        int recordCount = 0;
        String database = "" + _database;
        database = database.trim();
        String sql = "";
        if (!database.equals("")) {
            sql = "SELECT DISTINCT center_id,ensat_database FROM Identification WHERE Identification.ensat_database=?;";
        } else {
            sql = "SELECT DISTINCT center_id FROM Identification";
        }
        try {

            PreparedStatement ps = conn.prepareStatement(sql);
            if (!database.equals("")) {
                ps.setString(1, database);
            }
            ResultSet rs = ps.executeQuery();
            while (rs.next()) {
                recordCount++;
            }
        } catch (Exception e) {
            recordCount = 0;
        }

        return recordCount;
    }

    public String[][] rankActiveCenters(Connection conn, String _database, int _totalCenterNumber) {

        //Retrieve the parameters
        int centerNumber = _totalCenterNumber;
        String database = "" + _database;
        database = database.trim();

        //Initialise the arrays
        String[] centers = new String[centerNumber];
        int[] centerNumbers = new int[centerNumber];
        for (int j = 0; j < centerNumber; j++) {
            centers[j] = "";
            centerNumbers[j] = 0;
        }

        //Get a list of the distinct center labels
        String sql = "";
        if (database.equals("")) {
            sql = "SELECT DISTINCT center_id FROM Identification;";
        } else {
            sql = "SELECT DISTINCT center_id FROM Identification WHERE ensat_database=?;";
        }

        try {
            PreparedStatement ps = conn.prepareStatement(sql);
            if (!database.equals("")) {
                ps.setString(1, database);
            }
            ResultSet rs = ps.executeQuery();
            for (int i = 0; i < centerNumber; i++) {
                rs.next();
                centers[i] = rs.getString(1);
            }
            rs.close();
        } catch (Exception e) {
            logger.debug("('" + username + "') - Error (rankActiveCenters): " + e.getMessage());
        }

        //Get the numbers for each of the center labels
        try {
            for (int i = 0; i < centerNumber; i++) {
                String centerSql = "";
                if (database.equals("")) {
                    centerSql = "SELECT ensat_id,center_id FROM Identification WHERE center_id=?;";
                } else {
                    centerSql = "SELECT ensat_id,center_id FROM Identification WHERE center_id=? AND ensat_database=?;";
                }

                PreparedStatement ps = conn.prepareStatement(centerSql);
                if (database.equals("")) {
                    ps.setString(1, centers[i]);
                } else {
                    ps.setString(1, centers[i]);
                    ps.setString(2, database);
                }

                ResultSet rs_center = ps.executeQuery();
                int centerRowCount = 0;
                while (rs_center.next()) {
                    centerRowCount++;
                }
                centerNumbers[i] = centerRowCount;
                ps.close();
            }
        } catch (Exception e) {
            logger.debug("('" + username + "') - Error (rankActiveCenters #2): " + e.getMessage());
        }

        //Now input these into the rankings array (initialise first)
        String[][] rankings = new String[3][10];
        for (int j = 0; j < 3; j++) {
            for (int k = 0; k < 10; k++) {
                rankings[j][k] = "";
            }
        }

        //Create an EnsatList object (for sorting)
        //MERGE-SORT APPEARS TO FAIL WHEN TWO CENTERS HAVE THE SAME NUMBERS...
        SortBean.EnsatList el = new SortBean.EnsatList(centerNumber, centers, centerNumbers);
        SortBean.SortBean sort = new SortBean.SortBean();
        el = sort.sort(el);

        int elemCount = 0;
        int elemLowerLimit = centerNumber - 11;
        if (centerNumber < 10) {
            elemLowerLimit = 0;
        }

        for (int j = (centerNumber - 1); j > elemLowerLimit; j--) {
            rankings[0][elemCount] = "" + (elemCount + 1);
            rankings[1][elemCount] = el.getCenter(j);
            rankings[2][elemCount] = "" + el.getCenterNumber(j);
            elemCount++;
        }
        return rankings;
    }

    public int countDeadPatients(Statement stmt, String _database) {

        int recordCount = 0;
        String database = "" + _database;
        database = database.trim();
        String sql = "";
        //Construct the SQL for the appropriate database to retrieve the number of patients that are recorded dead
        if (database.equals("ACC")) {
            sql = "SELECT DISTINCT Identification.ensat_id, Identification.center_id, Identification.ensat_database, ACC_FollowUp.followup_comment"
                    + " FROM Identification, ACC_FollowUp"
                    + " WHERE Identification.ensat_id=ACC_FollowUp.ensat_id"
                    + " AND Identification.center_id=ACC_FollowUp.center_id"
                    + " AND ACC_FollowUp.patient_status LIKE '%eath%' AND ensat_database='ACC';";
        } else if (database.equals("Pheo")) {
            sql = "SELECT DISTINCT Identification.ensat_id, Identification.center_id, Identification.ensat_database, Pheo_FollowUp.alive"
                    + " FROM Identification, Pheo_FollowUp"
                    + " WHERE Identification.ensat_id=Pheo_FollowUp.ensat_id"
                    + " AND Identification.center_id=Pheo_FollowUp.center_id"
                    + " AND Pheo_FollowUp.alive='No';";
            //+ " AND ensat_database='Pheo';";
        } else if (database.equals("NAPACA")) {
            sql = "SELECT DISTINCT Identification.ensat_id, Identification.center_id, Identification.ensat_database, NAPACA_FollowUp.followup_alive"
                    + " FROM Identification, NAPACA_FollowUp"
                    + " WHERE Identification.ensat_id=NAPACA_FollowUp.ensat_id"
                    + " AND Identification.center_id=NAPACA_FollowUp.center_id"
                    + " AND NAPACA_FollowUp.followup_alive='No'"
                    + " AND ensat_database='NAPACA';";
        } else if (database.equals("APA")) {
            sql = "SELECT DISTINCT Identification.ensat_id, Identification.center_id, Identification.ensat_database, APA_FollowUp.followup_alive"
                    + " FROM Identification, APA_FollowUp "
                    + "WHERE Identification.ensat_id=APA_FollowUp.ensat_id"
                    + " AND Identification.center_id=APA_FollowUp.center_id"
                    + " AND APA_FollowUp.followup_alive = 'No'"
                    + " AND ensat_database='APA';";
        }

        try {

            ResultSet rs = stmt.executeQuery(sql);
            while (rs.next()) {
                recordCount++;
            }
        } catch (Exception e) {
            recordCount = 0;
        }

        return recordCount;
    }

    public int countLostToFollowup(Statement stmt, String _database) {

        int identCount = 0;
        int followupCount = 0;
        String database = "" + _database;
        database = database.trim();
        String sqlIdent = "SELECT DISTINCT Identification.ensat_id, Identification.center_id"
                + " FROM Identification WHERE ensat_database='" + database + "';";
        String sqlFollowup = "SELECT DISTINCT Identification.ensat_id, Identification.center_id"
                + " FROM Identification, " + database + "_FollowUp"
                + " WHERE Identification.ensat_id=" + database + "_FollowUp.ensat_id"
                + " AND Identification.center_id=" + database + "_FollowUp.center_id;";
        try {
            ResultSet rs = stmt.executeQuery(sqlIdent);
            while (rs.next()) {
                identCount++;
            }

            rs = stmt.executeQuery(sqlFollowup);
            while (rs.next()) {
                followupCount++;
            }

        } catch (Exception e) {
            logger.debug("('" + username + "') - Error (countLostToFollowup): " + e.getMessage());
            identCount = 0;
            followupCount = 0;
        }

        int lostToFollowupCount = identCount - followupCount;
        return lostToFollowupCount;
    }

    public int countBiosamples(Connection conn, String _database) {

        int recordCount = 0;
        String database = "" + _database;
        database = database.trim();
        String sql = "";
        if (!database.equals("")) {
            sql = "SELECT * FROM " + database + "_Biomaterial";
        }

        try {

            PreparedStatement ps = conn.prepareStatement(sql);
            ResultSet rs = ps.executeQuery();
            while (rs.next()) {
                if (!database.equals("Pheo")) {
                    for (int i = 1; i < 18; i++) {
                        String columnInput = rs.getString(i);
                        if (i > 4 && i < 13) {
                            if (columnInput != null && columnInput.equals("Yes")) {
                                recordCount++;
                            }
                        }
                        if (i == 14 || i == 15 || i == 16 || i == 17) {
                            if (columnInput != null && columnInput.equals("Yes")) {
                                recordCount++;
                            }
                        }
                    }
                } else {
                    //Pheo has an extra data-point
                    for (int i = 1; i < 19; i++) {
                        String columnInput = rs.getString(i);
                        if (i > 4 && i < 13) {
                            if (columnInput != null && columnInput.equals("Yes")) {
                                recordCount++;
                            }
                        }
                        if (i == 14 || i == 15 || i == 16 || i == 17 || i == 18) {
                            if (columnInput != null && columnInput.equals("Yes")) {
                                recordCount++;
                            }
                        }
                    }
                }
            }
        } catch (Exception e) {
            recordCount = 0;
        }

        return recordCount;
    }

    public int countClinicalAnnotations(Connection conn, String _database) {

        int recordCount = 0;
        String database = "" + _database;
        database = database.trim();

        String[] tables = null;
        if (database.equals("ACC")) {
            tables = new String[9];
            tables[0] = "ACC_Biomaterial";
            tables[1] = "ACC_Chemoembolisation";
            tables[2] = "ACC_Chemotherapy";
            tables[3] = "ACC_FollowUp";
            tables[4] = "ACC_Mitotane";
            tables[5] = "ACC_Pathology";
            tables[6] = "ACC_Radiofrequency";
            tables[7] = "ACC_Radiotherapy";
            tables[8] = "ACC_Surgery";
        } else if (database.equals("Pheo")) {
            tables = new String[7];
            tables[0] = "Pheo_Biomaterial";
            tables[1] = "Pheo_BiochemicalAssessment";
            tables[2] = "Pheo_ClinicalAssessment";
            tables[3] = "Pheo_FollowUp";
            tables[4] = "Pheo_ImagingTests";
            tables[5] = "Pheo_NonSurgicalInterventions";
            tables[6] = "Pheo_Surgery";
        } else if (database.equals("NAPACA")) {
            tables = new String[5];
            tables[0] = "NAPACA_Biomaterial";
            tables[1] = "NAPACA_FollowUp";
            tables[2] = "NAPACA_Imaging";
            tables[3] = "NAPACA_Pathology";
            tables[4] = "NAPACA_Surgery";
        } else if (database.equals("APA")) {
            tables = new String[8];
            tables[0] = "APA_Biomaterial";
            tables[1] = "APA_BiochemicalAssessment";
            tables[2] = "APA_Cardio";
            tables[3] = "APA_ClinicalAssessment";
            tables[4] = "APA_Complication";
            tables[5] = "APA_FollowUp";
            tables[6] = "APA_Imaging";
            tables[7] = "APA_Surgery";
        }

        try {

            for (int i = 0; i < tables.length; i++) {
                String sql = "SELECT * FROM " + tables[i] + ";";
                PreparedStatement ps = conn.prepareStatement(sql);
                ResultSet rs = ps.executeQuery();
                while (rs.next()) {
                    recordCount++;
                }
            }
        } catch (Exception e) {
            recordCount = 0;
        }

        return recordCount;
    }

    public String formatDecimalPlaces(String _str) {
        String formatStr = _str;
        if (formatStr.indexOf(".") != -1) {
            String strAfterDecimal = formatStr.substring(formatStr.indexOf("."), formatStr.length());
            int pointsAfterDecimal = strAfterDecimal.length();
            if (pointsAfterDecimal > 3) {
                pointsAfterDecimal = 3;
            }
            formatStr = formatStr.substring(0, formatStr.indexOf(".") + pointsAfterDecimal);
        } else {
            formatStr = "0";
        }
        return formatStr;
    }

    public String getStudyHtml(Connection conn, String[] ensatSections) {

        String outputStr = "";

        outputStr += "<table width=\"50%\" border=\"1\" cellpadding=\"5\">";
        outputStr += "<tr>";
        outputStr += "<th></th>";
        for (int i = 0; i < 5; i++) {
            if (i != 4) {
                outputStr += "<th><div align=\"center\">" + ensatSections[i] + "</div></th>";
            } else {
                outputStr += "<th><div align=\"center\">Total</div></th>";
            }
        }

        //Add the individual study headers
        outputStr += "<th><div align=\"center\">Principal Investigator</div></th>";
        outputStr += "<th><div align=\"center\">Study Protocols</div></th>";
        outputStr += "<th><div align=\"center\">Study sites/eCRFs</div></th>";

        outputStr += "</tr>";

        Vector<Vector> studyNumbers = this.getStudyNumbers(conn, ensatSections);
        int studyNum = studyNumbers.size();

        for (int i = 0; i < studyNum; i++) {

            Vector<String> studyIn = studyNumbers.get(i);
            outputStr += "<tr>";
            for (int j = 0; j < studyIn.size(); j++) {
                outputStr += "<td>";
                outputStr += studyIn.get(j);
                outputStr += "</td>";
            }
            outputStr += "</tr>";
        }

        outputStr += "</table>";
        return outputStr;
    }

    private Vector<String> getStudyNames(Connection conn) {

        Vector<String> studyNames = new Vector<String>();
        try {
            String studySql = "SELECT study_label FROM Studies;";
            Statement stmt = conn.createStatement();
            ResultSet rs = stmt.executeQuery(studySql);
            while (rs.next()) {
                String studyNameIn = rs.getString(1);
                studyNames.add(studyNameIn);
            }
        } catch (Exception e) {
            logger.debug("('" + username + "') - Error (getStudyNames): " + e.getMessage());
        }
        return studyNames;
    }

    private Vector<Vector> getStudyNumbers(Connection conn, String[] ensatSections) {

        Vector<Vector> studyNumbers = new Vector<Vector>();
        Vector<String> studyNames = this.getStudyNames(conn);
        Vector<Vector> extendedStudyInfo = this.getExtendedStudyInfo(conn);

        int studyNum = studyNames.size();
        try {
            for (int i = 0; i < studyNum; i++) {

                Vector<Vector> studyNumOverall = new Vector<Vector>();
                String studyNameIn = studyNames.get(i);
                String studyNumSql = "SELECT DISTINCT Associated_Studies.ensat_id,Associated_Studies.center_id,ensat_database FROM Associated_Studies, Identification WHERE Associated_Studies.ensat_id=Identification.ensat_id AND Associated_Studies.center_id=Identification.center_id  AND study_label=?;";

                PreparedStatement ps = conn.prepareStatement(studyNumSql);
                ps.setString(1, studyNameIn);
                ResultSet rs = ps.executeQuery();

                while (rs.next()) {
                    Vector<String> patientIdIn = new Vector<String>();
                    for (int j = 0; j < 3; j++) {
                        String valueIn = rs.getString(j + 1);
                        if (valueIn == null) {
                            valueIn = "";
                        }
                        patientIdIn.add(valueIn);
                    }
                    studyNumOverall.add(patientIdIn);
                }

                int overallNum = studyNumOverall.size();
                String[] sectionNumSplit = new String[ensatSections.length];
                for (int k = 0; k < ensatSections.length - 1; k++) {
                    int typeCount = 0;
                    String sectionTypeIn = ensatSections[k];
                    for (int j = 0; j < overallNum; j++) {
                        Vector<String> studyLineIn = studyNumOverall.get(j);
                        if (sectionTypeIn.equals(studyLineIn.get(2))) {
                            typeCount++;
                        }
                    }
                    sectionNumSplit[k] = "" + typeCount;
                }
                sectionNumSplit[ensatSections.length - 1] = "" + overallNum;

                Vector<String> thisExtendedStudyInfo = new Vector<String>();
                boolean studyFound = false;
                int studyCount = 0;
                while (studyCount < extendedStudyInfo.size() && !studyFound) {
                    Vector<String> extendedStudyInfoIn = extendedStudyInfo.get(studyCount);
                    if (extendedStudyInfoIn.get(0).equals(studyNameIn)) {
                        studyFound = true;
                        for (int k = 0; k < 4; k++) {
                            String infoIn = extendedStudyInfoIn.get(k + 1);
                            thisExtendedStudyInfo.add(infoIn);
                        }
                    } else {
                        studyCount++;
                    }
                }
                int extendedStudyInfoColSize = thisExtendedStudyInfo.size();

                if (overallNum != 0) {
                    Vector<String> studyInfo = new Vector<String>();
                    studyInfo.add(studyNameIn);
                    for (int k = 0; k < sectionNumSplit.length; k++) {
                        studyInfo.add(sectionNumSplit[k]);
                    }
                    for (int k = 0; k < extendedStudyInfoColSize; k++) {
                        String infoIn = (String) thisExtendedStudyInfo.get(k);
                        if (k == 0) {
                            //Add the mailto option
                            String emailIn = (String) thisExtendedStudyInfo.get(1);
                            if (!emailIn.equals("")) {
                                infoIn = "<a href=\"mailto:" + emailIn + "\">" + infoIn + "</a>";
                            }
                            //logger.debug("infoIn(" + k + "): " + infoIn);
                            studyInfo.add(infoIn);
                        } else if (k != 1) {
                            //Add the hyperlink option
                            if (!infoIn.equals("")) {
                                infoIn = "<a target=\"_blank\" href=\"" + infoIn + "\">" + studyNameIn + "</a>";
                            }
                            //logger.debug("infoIn(" + k + "): " + infoIn);
                            studyInfo.add(infoIn);
                        }
                    }
                    studyNumbers.add(studyInfo);
                }
            }

        } catch (Exception e) {
            logger.debug("('" + username + "') - Error (getStudyNumbers): " + e.getMessage());
        }

        return studyNumbers;
    }

    private Vector<Vector> getExtendedStudyInfo(Connection conn) {

        Vector<Vector> extendedStudyInfo = new Vector<Vector>();
        String extendedSql = "SELECT study_label,study_pi,pi_email,study_sop,study_link FROM Studies;";

        try {
            PreparedStatement ps = conn.prepareStatement(extendedSql);
            ResultSet rs = ps.executeQuery();
            ResultSetMetaData rsmd = rs.getMetaData();
            int colNum = rsmd.getColumnCount();
            while (rs.next()) {
                Vector<String> indStudyInfo = new Vector<String>();
                for (int i = 0; i < colNum; i++) {
                    String infoIn = rs.getString(i + 1);
                    indStudyInfo.add(infoIn);
                }
                extendedStudyInfo.add(indStudyInfo);
            }

        } catch (Exception e) {
            logger.debug("('" + username + "') - Error (getExtendedStudyInfo): " + e.getMessage());
        }
        return extendedStudyInfo;
    }

    public Vector<Vector> getCountryNumbers(Connection conn, String type) {

        Vector<Vector> countryNumbers = new Vector<Vector>();
        try {
            String patientSql = "SELECT center_id FROM Identification;";
            if (type != "all") {
                patientSql = "SELECT center_id FROM Identification WHERE ensat_database=?;";
            }
            PreparedStatement stmt = conn.prepareStatement(patientSql);
            if (type != "all") {
                stmt.setString(1, type);
            }

            ResultSet rs = stmt.executeQuery();
            while (rs.next()) {
                String centerIn = rs.getString(1);
                if (centerIn == null) {
                    centerIn = "";
                }

                String countryCode = "";
                if (centerIn.length() > 2) {
                    countryCode = centerIn.substring(0, 2);
                }
                int countryNumbersSize = countryNumbers.size();

                int codeCount = 0;
                boolean codeFound = false;
                while (!codeFound && codeCount < countryNumbersSize) {
                    Vector<String> countryCodeIn = countryNumbers.get(codeCount);
                    if (countryCode.equals(countryCodeIn.get(0))) {
                        codeFound = true;
                    } else {
                        codeCount++;
                    }
                }

                Vector<String> countryNumberIn = new Vector<String>();
                if (codeFound) {
                    countryNumberIn = countryNumbers.get(codeCount);
                    String numberIn = countryNumberIn.get(1);
                    int numberInInt = Integer.parseInt(numberIn);
                    numberInInt++;
                    countryNumberIn.set(1, "" + numberInInt);
                    countryNumbers.set(codeCount, countryNumberIn);
                } else {
                    countryNumberIn.add(countryCode);
                    countryNumberIn.add("1");
                    if (!countryCode.equals("")) {
                        countryNumbers.add(countryNumberIn);
                    }
                }
            }
        } catch (Exception e) {
            logger.debug("('" + username + "') - Error (getCountryNumbers): " + e.getMessage());
        }
        return countryNumbers;
    }

    public String getCodeName(String code) {

        String countryNameOut = "";
        if (code.equals("BG")) {
            countryNameOut = "Bulgaria";
        } else if (code.equals("BZ")) {
            countryNameOut = "Brazil";
        } else if (code.equals("CR")) {
            countryNameOut = "Croatia";
        } else if (code.equals("FR")) {
            countryNameOut = "France";
        } else if (code.equals("GR")) {
            countryNameOut = "Greece";
        } else if (code.equals("GY")) {
            countryNameOut = "Germany";
        } else if (code.equals("HY")) {
            countryNameOut = "Hungary";
        } else if (code.equals("IT")) {
            countryNameOut = "Italy";
        } else if (code.equals("IR")) {
            countryNameOut = "Ireland";
        } else if (code.equals("NL")) {
            countryNameOut = "Netherlands";
        } else if (code.equals("PL")) {
            countryNameOut = "Poland";
        } else if (code.equals("PT")) {
            countryNameOut = "Portugal";
        } else if (code.equals("RU")) {
            countryNameOut = "Russia";
        } else if (code.equals("SP")) {
            countryNameOut = "Spain";
        } else if (code.equals("GB")) {
            countryNameOut = "Great Britain";
        } else if (code.equals("AU")) {
            countryNameOut = "Australia";
        } else if (code.equals("NY")) {
            countryNameOut = "Norway";
        } else if (code.equals("SW")) {
            countryNameOut = "Sweden";
        } else if (code.equals("SL")) {
            countryNameOut = "Slovenia";
        } else if (code.equals("SZ")) {
            countryNameOut = "Switzerland";
        } else if (code.equals("TR")) {
            countryNameOut = "Turkey";
        } else if (code.equals("US")) {
            countryNameOut = "United States";
        }
        return countryNameOut;
    }

    public String getValidCountryCode(String code) {

        //According to ISO 3166-1
        String countryNameOut = "";
        if (code.equals("BG")) {
            countryNameOut = "BG";
        } else if (code.equals("BZ")) {
            countryNameOut = "BR";
        } else if (code.equals("BL")) {
            countryNameOut = "BE";
        } else if (code.equals("CR")) {
            countryNameOut = "HR";
        } else if (code.equals("FR")) {
            countryNameOut = "FR";
        } else if (code.equals("GR")) {
            countryNameOut = "GR";
        } else if (code.equals("GY")) {
            countryNameOut = "DE";
        } else if (code.equals("HY")) {
            countryNameOut = "HU";
        } else if (code.equals("IT")) {
            countryNameOut = "IT";
        } else if (code.equals("NL")) {
            countryNameOut = "NL";
        } else if (code.equals("PL")) {
            countryNameOut = "PL";
        } else if (code.equals("PT")) {
            countryNameOut = "PT";
        } else if (code.equals("RU")) {
            countryNameOut = "RU";
        } else if (code.equals("SP")) {
            countryNameOut = "ES";
        } else if (code.equals("GB")) {
            countryNameOut = "GB";
        } else if (code.equals("AU")) {
            countryNameOut = "AU";
        } else if (code.equals("NY")) {
            countryNameOut = "NO";
        } else if (code.equals("SW")) {
            countryNameOut = "SE";
        } else if (code.equals("SZ")) {
            countryNameOut = "CH";
        } else if (code.equals("US")) {
            countryNameOut = "US";
        } else if (code.equals("TR")) {
            countryNameOut = "TR";
        } else if (code.equals("SL")) {
            countryNameOut = "SI";
        } else if (code.equals("SB")) {
            countryNameOut = "RS";
        } else if (code.equals("IR")) {
            countryNameOut = "IE";
        } else if (code.equals("RO")) {
            countryNameOut = "RO";
        }
        return countryNameOut;
    }

    public Vector<Vector> getCities(ServletContext context, String countryCode) {

        Vector<Vector> cities = new Vector<Vector>();
        try {
            ConnectionAuxiliary aux = new ConnectionAuxiliary();
            Connection conn = aux.getAuxiliaryConnection(context, "center_callout");

            String sql = "";
            if (!countryCode.equals("")) {
                sql = "SELECT city FROM Center_Callout WHERE center_id LIKE ? ORDER BY center_id;";
            } else {
                sql = "SELECT city FROM Center_Callout ORDER BY center_id;";
            }

            PreparedStatement stmt = conn.prepareStatement(sql);
            if (!countryCode.equals("")) {
                stmt.setString(1, "" + countryCode + "%");
            }
            ResultSet rs = stmt.executeQuery();
            while (rs.next()) {

                String city = rs.getString(1);
                int cityNum = cities.size();

                int cityCount = 0;
                boolean cityFound = false;
                while (!cityFound && cityCount < cityNum) {
                    Vector<String> cityIn = cities.get(cityCount);
                    if (city.equals(cityIn.get(0))) {
                        cityFound = true;
                    } else {
                        cityCount++;
                    }
                }

                Vector<String> cityNumberIn = new Vector<String>();
                if (cityFound) {
                    cityNumberIn = cities.get(cityCount);
                    String numberIn = cityNumberIn.get(1);
                    int numberInInt = Integer.parseInt(numberIn);
                    numberInInt++;
                    cityNumberIn.set(1, "" + numberInInt);
                    cities.set(cityCount, cityNumberIn);
                } else {
                    cityNumberIn.add(city);
                    cityNumberIn.add("1");
                    if (!city.equals("")) {
                        cities.add(cityNumberIn);
                    }
                }
            }
        } catch (Exception e) {
            logger.debug("('" + username + "') - Error (getCountryNumbers): " + e.getMessage());
        }
        return cities;
    }

    public String getCenterCodeTable(ServletContext context) {

        String centerCodeTableOut = "<table width=\"100%\" border=\"1\" cellpadding=\"5\">";

        centerCodeTableOut += "<tr>";
        centerCodeTableOut += "<th><div align=\"center\">Center Code</div></th>";
        centerCodeTableOut += "<th><div align=\"center\">City / State</div></th>";
        centerCodeTableOut += "<th><div align=\"center\">Principal Investigator</div></th>";
        centerCodeTableOut += "<th><div align=\"center\">Current<br/> Record<br/> Number</div></th>";
        centerCodeTableOut += "</tr>";

        try {
            ConnectionAuxiliary aux = new ConnectionAuxiliary();
            Connection conn = aux.getAuxiliaryConnection(context, "center_callout");

            String sql = "SELECT center_id,investigator_name,investigator_email,ensat_id,city FROM Center_Callout ORDER BY center_id;";
            PreparedStatement stmt = conn.prepareStatement(sql);

            ResultSet rs = stmt.executeQuery();
            String lastCountryCode = "";
            while (rs.next()) {
                String centerIn = rs.getString(1);
                if (centerIn == null) {
                    centerIn = "";
                }
                String invName = rs.getString(2);
                String invEmail = rs.getString(3);
                if (invName == null) {
                    invName = "";
                }
                if (invEmail == null) {
                    invEmail = "";
                }
                String recordNumIn = rs.getString(4);
                if (recordNumIn == null) {
                    recordNumIn = "";
                }
                String cityIn = rs.getString(5);
                if (cityIn == null) {
                    cityIn = "";
                }

                String countryCode = "";
                if (centerIn.length() > 2) {
                    countryCode = centerIn.substring(0, 2);
                }
                if (!countryCode.equals(lastCountryCode)) {
                    String countryLink = "<a href='/jsp/centers_pis.jsp?country_code=" + countryCode + "'>" + this.getCodeName(countryCode) + "</a>";
                    centerCodeTableOut += "<tr><th colspan='4'><div align='left'>" + countryLink + "</div></th></tr>";
                    lastCountryCode = countryCode;
                }

                centerCodeTableOut += "<tr>";
                centerCodeTableOut += "<td>";
                centerCodeTableOut += "" + centerIn + "";
                centerCodeTableOut += "</td>";
                centerCodeTableOut += "<td>";
                centerCodeTableOut += "" + cityIn + "";
                centerCodeTableOut += "</td>";
                centerCodeTableOut += "<td>";
                centerCodeTableOut += "<a href='mailto:" + invEmail + "'>" + invName + "</a>";
                centerCodeTableOut += "</td>";
                centerCodeTableOut += "<td>";
                centerCodeTableOut += "" + recordNumIn + "";
                centerCodeTableOut += "</td>";
                centerCodeTableOut += "</tr>";

                /*int countryNumbersSize = countryNumbers.size();

                 int codeCount = 0;
                 boolean codeFound = false;
                 while (!codeFound && codeCount < countryNumbersSize) {
                 Vector<String> countryCodeIn = countryNumbers.get(codeCount);
                 if (countryCode.equals(countryCodeIn.get(0))) {
                 codeFound = true;
                 } else {
                 codeCount++;
                 }
                 }

                 Vector<String> countryNumberIn = new Vector<String>();
                 if (codeFound) {
                 countryNumberIn = countryNumbers.get(codeCount);
                 String numberIn = countryNumberIn.get(1);
                 int numberInInt = Integer.parseInt(numberIn);
                 numberInInt++;
                 countryNumberIn.set(1, "" + numberInInt);
                 countryNumbers.set(codeCount, countryNumberIn);
                 } else {
                 countryNumberIn.add(countryCode);
                 countryNumberIn.add("1");
                 if (!countryCode.equals("")) {
                 countryNumbers.add(countryNumberIn);
                 }
                 }*/
            }

            centerCodeTableOut += "</table>";
        } catch (Exception e) {
            logger.debug("('" + username + "') - Error (getCenterCodeTable): " + e.getMessage());
        }
        return centerCodeTableOut;

    }

    public Vector<Vector> getAllFreezerData(String centerid, Connection conn) {

        Vector<Vector> freezerData = new Vector<Vector>();

        int MATERIAL_PARAM_NUM = 11;
        String[] types = {"ACC", "Pheo", "NAPACA", "APA"};
        for (int i = 0; i < types.length; i++) {
            String sql = "SELECT ensat_id,material,aliquot_sequence_id,freezer_number,freezershelf_number,rack_number,shelf_number,box_number,position_number,bio_id,material_used FROM " + types[i] + "_Biomaterial_Freezer_Information WHERE center_id=? ORDER BY freezer_number,freezershelf_number,rack_number,shelf_number,box_number,position_number;";

            try {
                PreparedStatement ps = conn.prepareStatement(sql);
                ps.setString(1, centerid);

                ResultSet rs = ps.executeQuery();
                while (rs.next()) {
                    Vector<String> materialIn = new Vector<String>();
                    for (int j = 0; j < MATERIAL_PARAM_NUM; j++) {
                        String valueIn = rs.getString(j + 1);
                        if (valueIn == null) {
                            valueIn = "";
                        }
                        materialIn.add(valueIn);
                    }
                    freezerData.add(materialIn);
                }
                rs.close();
            } catch (Exception e) {
                logger.debug("('" + username + "') - Error (getAllFreezerData): " + e.getMessage());
            }
        }

        /*for (int i = 0; i < freezerData.size(); i++) {
         Vector<String> valueIn = freezerData.get(i);
         logger.debug(valueIn.toString());
         }*/
        return freezerData;
    }

    public Vector<Vector> getAllFreezerData(String centerid, Connection conn, boolean useDate) {

        Vector<Vector> freezerData = new Vector<Vector>();

        int MATERIAL_PARAM_NUM = 9;
        if (useDate) {
            MATERIAL_PARAM_NUM = 10;
        }
        String[] types = {"ACC", "Pheo", "NAPACA", "APA"};
        for (int i = 0; i < types.length; i++) {
            String sql = "SELECT " + types[i] + "_Biomaterial.ensat_id,material,aliquot_sequence_id,freezer_number,freezershelf_number,rack_number,shelf_number,box_number,position_number FROM " + types[i] + "_Biomaterial_Freezer_Information WHERE center_id=? ORDER BY freezer_number,freezershelf_number,rack_number,shelf_number,box_number,position_number;";
            if (useDate) {
                sql = "SELECT " + types[i] + "_Biomaterial.ensat_id,material,aliquot_sequence_id,freezer_number,freezershelf_number,rack_number,shelf_number,box_number,position_number,biomaterial_date FROM " + types[i] + "_Biomaterial_Freezer_Information," + types[i] + "_Biomaterial WHERE " + types[i] + "_Biomaterial_Freezer_Information.ensat_id=" + types[i] + "_Biomaterial.ensat_id AND " + types[i] + "_Biomaterial_Freezer_Information.center_id=" + types[i] + "_Biomaterial.center_id AND " + types[i] + "_Biomaterial_Freezer_Information." + types[i].toLowerCase() + "_biomaterial_id=" + types[i] + "_Biomaterial." + types[i].toLowerCase() + "_biomaterial_id "
                        + "AND " + types[i] + "_Biomaterial.center_id=? ORDER BY freezer_number,freezershelf_number,rack_number,shelf_number,box_number,position_number;";
            }

            logger.debug("sql: " + sql);
            try {
                PreparedStatement ps = conn.prepareStatement(sql);
                ps.setString(1, centerid);

                ResultSet rs = ps.executeQuery();
                while (rs.next()) {
                    Vector<String> materialIn = new Vector<String>();
                    for (int j = 0; j < MATERIAL_PARAM_NUM; j++) {
                        String valueIn = rs.getString(j + 1);
                        if (valueIn == null) {
                            valueIn = "";
                        }
                        materialIn.add(valueIn);
                    }
                    freezerData.add(materialIn);
                }
                rs.close();
            } catch (Exception e) {
                logger.debug("('" + username + "') - Error (getAllFreezerData): " + e.getMessage());
            }
        }

        logger.debug("center_id: " + centerid);
        logger.debug("freezerData.size(): " + freezerData.size());
        for (int i = 0; i < freezerData.size(); i++) {
            Vector<String> freezerLineIn = freezerData.get(i);
            logger.debug(freezerLineIn);
        }

        return freezerData;
    }

    public boolean freezerCheck(Connection conn, String centerid) {

        boolean freezerPresent = false;
        int freezerCount = 0;
        try {
            //Get the structural information for this center
            String sql = "SELECT * FROM Freezer_Structure WHERE center_id=? ORDER BY freezer_id;";
            PreparedStatement stmt = conn.prepareStatement(sql);
            stmt.setString(1, centerid);
            ResultSet rs = stmt.executeQuery();

            while (rs.next()) {
                freezerCount++;
            }

            rs.close();
            freezerPresent = freezerCount != 0;

        } catch (Exception e) {
            logger.debug("('" + username + "') - Error (freezerCheck): " + e.getMessage());
        }
        return freezerPresent;
    }

    public String getFreezerStructureHtml(Connection conn, Connection ccConn, String centerid, String component, String cptid) {

        //Get the transfer menu here (run once only)
        Vector<String> centerCodes = new Vector<String>();
        try {
            String sql = "SELECT DISTINCT center_id FROM Center_Callout ORDER BY center_id;";
            PreparedStatement ps = ccConn.prepareStatement(sql);
            ResultSet rs = ps.executeQuery();
            while (rs.next()) {
                String centerIn = rs.getString(1);
                if (centerIn == null) {
                    centerIn = "";
                }
                if (!centerIn.equals("")) {
                    centerCodes.add(centerIn);
                }
            }
        } catch (Exception e) {
            logger.debug("Error (getFreezerStructureHtml): " + e.getMessage());
        }

        if (component.equals("")) {
            return "<p><strong>No freezer component selected</strong></p>";
        }

        int FREEZER_ITEMS = 7;
        String freezerStructureOut = "<table border='1' cellpadding='5' width='100%'>";
        Vector<String> centerFreezer = new Vector<String>();

        try {
            //Get the structural information for this center
            String sql = "SELECT * FROM Freezer_Structure WHERE center_id=? ORDER BY freezer_id;";
            PreparedStatement stmt = conn.prepareStatement(sql);
            stmt.setString(1, centerid);
            ResultSet rs = stmt.executeQuery();

            int freezerCount = 0;
            while (rs.next()) {
                for (int i = 0; i < FREEZER_ITEMS; i++) {
                    String itemIn = rs.getString(i + 1);
                    centerFreezer.add(itemIn);
                }
                freezerCount++;
            }

            rs.close();

            if (freezerCount == 0) {
                return "<p><strong>No freezers available</strong> - please contact the system administrator (astell@unimelb.edu.au) to add freezer information</p>";
            }

            //Now get all the actual freezer information for this center
            Vector<Vector> freezerData = this.getAllFreezerData(centerid, conn);

            //Add a navigation bar if the component is not 'freezer'
            if (!component.equals("freezer")) {
                freezerStructureOut += "<tr>";
                freezerStructureOut += "<th colspan='2'>";

                String cpt1 = cptid.substring(0, cptid.indexOf("_")); //1
                String cpt1remainder = cptid.substring(cptid.indexOf("_") + 1, cptid.length()); //2_3_4_2
                String cpt2 = cpt1remainder.substring(0, cpt1remainder.indexOf("_")); //2
                String cpt2remainder = cpt1remainder.substring(cpt1remainder.indexOf("_") + 1, cpt1remainder.length()); //3_4_2

                freezerStructureOut += "Freezer " + cpt1;
                freezerStructureOut += " -&gt; ";
                freezerStructureOut += "Freezer Shelf " + cpt2;
                freezerStructureOut += " -&gt; ";

                //Dissect up the component ID
                if (component.equals("rack")) {
                    String cpt3 = cpt2remainder;
                    freezerStructureOut += "Rack " + cpt3;
                } else if (component.equals("rack_shelf")) {
                    String cpt3 = cpt2remainder.substring(0, cpt2remainder.indexOf("_")); //3
                    String cpt3remainder = cpt2remainder.substring(cpt2remainder.indexOf("_") + 1, cpt2remainder.length()); //4
                    String cpt4 = cpt3remainder;

                    freezerStructureOut += "Rack " + cpt3;
                    freezerStructureOut += " -&gt; ";
                    freezerStructureOut += "Rack Shelf " + cpt4;
                } else if (component.equals("box")) {
                    String cpt3 = cpt2remainder.substring(0, cpt2remainder.indexOf("_")); //3
                    String cpt3remainder = cpt2remainder.substring(cpt2remainder.indexOf("_") + 1, cpt2remainder.length()); //4_2
                    String cpt4 = cpt3remainder.substring(0, cpt3remainder.indexOf("_")); //4
                    String cpt4remainder = cpt3remainder.substring(cpt3remainder.indexOf("_") + 1, cpt3remainder.length()); //2
                    String cpt5 = cpt4remainder;

                    freezerStructureOut += "Rack " + cpt3;
                    freezerStructureOut += " -&gt; ";
                    freezerStructureOut += "Rack Shelf " + cpt4;
                    freezerStructureOut += " -&gt; ";
                    freezerStructureOut += "Box " + cpt5;
                }
                //freezerStructureOut += cptid;

                freezerStructureOut += "</th>";
                freezerStructureOut += "</tr>";
            }

            freezerStructureOut += "<tr>";

            int freezerCapacity = Integer.parseInt(centerFreezer.get(2));
            int freezerShelfCapacity = Integer.parseInt(centerFreezer.get(3));

            //Show the freezers in the left panel
            freezerStructureOut += "<td valign='top' width='50%'>";
            freezerStructureOut += "<div align='center'>";
            freezerStructureOut += "<table cellpadding='5'>";
            freezerStructureOut += "<tr>";
            for (int i = 0; i < freezerCount; i++) {

                freezerStructureOut += "<td>";
                freezerStructureOut += "<div align='center'>";
                freezerStructureOut += "<strong>Freezer #" + (i + 1) + "</strong><br/><br/>";
                freezerStructureOut += "<table border='1' cellpadding='5'>";
                for (int j = 0; j < freezerCapacity; j++) {
                    freezerStructureOut += "<tr><td>";

                    freezerStructureOut += "<div align='center'>";
                    freezerStructureOut += "<strong>Freezer Shelf #" + (j + 1) + "<br/><br/>";
                    freezerStructureOut += "<table border='1' cellpadding='5'>";
                    freezerStructureOut += "<tr>";

                    for (int k = 0; k < freezerShelfCapacity; k++) {
                        freezerStructureOut += "<td>";
                        String componentIdStr = "" + (i + 1) + "_" + (j + 1) + "_" + (k + 1) + "";
                        freezerStructureOut += "<a href='./jsp/biobank/freezer_inventory.jsp?centerid=" + centerid + "&component=rack&cptid=" + componentIdStr + "'>Rack " + (k + 1) + "</a><br/>";
                        freezerStructureOut += "</td>";
                    }

                    freezerStructureOut += "</tr>";
                    freezerStructureOut += "</table>";
                    freezerStructureOut += "</div>";
                    freezerStructureOut += "</td></tr>";
                }
                freezerStructureOut += "</table>";
                freezerStructureOut += "</div>";
                freezerStructureOut += "</td>";

            }
            freezerStructureOut += "</tr>";
            freezerStructureOut += "</table>";
            freezerStructureOut += "</div>";
            freezerStructureOut += "</td>";

            //Component detail will be in the right panel
            int rackCapacity = Integer.parseInt(centerFreezer.get(4));
            int rackShelfCapacity = Integer.parseInt(centerFreezer.get(5));
            int boxCapacity = Integer.parseInt(centerFreezer.get(6));

            if (!component.equals("freezer")) {
                freezerStructureOut += "<td valign='top'>";

                freezerStructureOut += "<div align='center'>";
                String componentTitle = "";
                int cptCapacity = 0;
                String subComponent = "";
                String subComponentTitle = "";
                if (component.equals("rack")) {
                    componentTitle = "Rack";
                    cptCapacity = rackCapacity;
                    subComponent = "rack_shelf";
                    subComponentTitle = "Rack Shelf";
                } else if (component.equals("rack_shelf")) {
                    componentTitle = "Rack Shelf";
                    cptCapacity = rackShelfCapacity;
                    subComponent = "box";
                    subComponentTitle = "Box";
                } else if (component.equals("box")) {
                    componentTitle = "Box";
                    cptCapacity = boxCapacity;
                    subComponent = "position";
                    subComponentTitle = "Position";
                }

                freezerStructureOut += "<strong>" + componentTitle + " #" + cptid + "</strong><br/><br/>";

                freezerStructureOut += "<form action='./jsp/biobank/materials_used.jsp' method='POST'>";
                freezerStructureOut += "<table border='1' cellpadding='5'>";
                if (component.equals("box")) {
                    freezerStructureOut += "<tr><th><div align='center'>Position</div></th><th><div align='center'>Bio ID</div></th><th><div align='center'>ENSAT ID: material (aliquot sequence)</div></th><th><div align='center'>Material<br/>used</div></th><th><div align='center'>Material<br/>to transfer</div></th></tr>";
                }
                for (int m = 0; m < cptCapacity; m++) {
                    freezerStructureOut += "<tr><td>";
                    freezerStructureOut += "<div align='center'>";

                    String componentIdStr = cptid + "_" + (m + 1) + "";
                    if (component.equals("box")) {
                        freezerStructureOut += "#" + (m + 1) + "</div></td><td><div align='center'>" + this.getPositionBiomaterialInfo(cptid, "" + (m + 1), freezerData, centerid, centerCodes);
                    } else {
                        freezerStructureOut += "<a href='./jsp/biobank/freezer_inventory.jsp?centerid=" + centerid + "&component=" + subComponent + "&cptid=" + componentIdStr + "'>" + subComponentTitle + " " + (m + 1) + "</a>";
                    }

                    freezerStructureOut += "</div>";
                    freezerStructureOut += "</td></tr>";
                }
                freezerStructureOut += "</table>";

                if (component.equals("box")) {
                    freezerStructureOut += "<div align='center'><input type='submit' value='Mark selected aliquots as used'> <input type='submit' value='Transfer Aliquots'></div></form>";
                }

                freezerStructureOut += "</div>";
                freezerStructureOut += "</td>";
            }

            freezerStructureOut += "</tr>";
            freezerStructureOut += "</table>";

        } catch (Exception e) {
            logger.debug("('" + username + "') - Error (getFreezerStructureHtml): " + e.getMessage());
        }
        return freezerStructureOut;

    }

    private String getPositionBiomaterialInfo(String cptid, String posnNum, Vector<Vector> freezerData, String centerid, Vector<String> centerCodes) {

        StringTokenizer st = new StringTokenizer(cptid, "_");

        String freezerId = st.nextToken();
        String freezerShelfId = st.nextToken();
        String rackId = st.nextToken();
        String rackShelfId = st.nextToken();
        String boxId = st.nextToken();

        String ensatIdOut = "";
        String materialOut = "";
        String aliquotSequenceNumOut = "";
        String bioIdOut = "";
        String usedOut = "";

        int freezerValuesNum = freezerData.size();
        boolean valueFound = false;
        int freezerValueCount = 0;
        while (freezerValueCount < freezerValuesNum && !valueFound) {

            Vector<String> freezerValueIn = freezerData.get(freezerValueCount);

            String freezerIn = freezerValueIn.get(3);
            String freezerShelfIn = freezerValueIn.get(4);
            String rackIn = freezerValueIn.get(5);
            String rackShelfIn = freezerValueIn.get(6);
            String boxIn = freezerValueIn.get(7);
            String posnIn = freezerValueIn.get(8);

            if (freezerIn.equals(freezerId)
                    && freezerShelfIn.equals(freezerShelfId)
                    && rackIn.equals(rackId)
                    && rackShelfIn.equals(rackShelfId)
                    && boxIn.equals(boxId)
                    && posnIn.equals(posnNum)) {
                valueFound = true;
                ensatIdOut = freezerValueIn.get(0);
                materialOut = freezerValueIn.get(1);
                aliquotSequenceNumOut = freezerValueIn.get(2);
                bioIdOut = freezerValueIn.get(9);
                usedOut = freezerValueIn.get(10);
            } else {
                freezerValueCount++;
            }
        }

        if (!valueFound) {
            return "</div></td><td bgcolor='#cccccc'><div align='center'>Unoccupied";
        } else if (usedOut.equalsIgnoreCase("yes")) {
            String bioInfoOut = "</div></td><td bgcolor='#cccccc'><div align='center'>Unoccupied<br/>";
            bioInfoOut += "<strong>Previously occupied by:</strong><br/>";
            bioInfoOut += "" + bioIdOut + " " + centerid + "-" + ensatIdOut + ": " + materialOut + " (" + aliquotSequenceNumOut + ")";
            return bioInfoOut;
        } else {
            String bioInfoOut = "<strong>" + bioIdOut + "</strong></div></td><td><div align='center'><strong>" + centerid + "-" + ensatIdOut + ":</strong> " + materialOut + " (" + aliquotSequenceNumOut + ")";
            bioInfoOut += "</div></td><td><div align='center'>"
                    + "<input name='aliquots_used' type='checkbox' value='material_used_" + bioIdOut + "'/>";
            bioInfoOut += "</div></td><td><div align='center'>" + this.getCenterCodesMenu(centerCodes, bioIdOut);
            return bioInfoOut;
        }
    }

    public String markAliquotsAsUsed(String[] aliquotValues, Connection conn) {

        if (aliquotValues != null) {

            //Filter out what aliquots should be updated and add them to a list
            Vector<String> aliquotsToUpdate = new Vector<String>();
            int aliquotNum = aliquotValues.length;
            for (int i = 0; i < aliquotNum; i++) {
                String usedValueIn = aliquotValues[i];
                if (usedValueIn == null) {
                    usedValueIn = "";
                }
                if (usedValueIn.equalsIgnoreCase("null")) {
                    usedValueIn = "";
                }
                if (!usedValueIn.equals("")) {
                    String bioIdValueIn = "";
                    if (usedValueIn.lastIndexOf("_") != -1) {
                        bioIdValueIn = usedValueIn.substring(usedValueIn.lastIndexOf("_") + 1, usedValueIn.length());
                    }
                    aliquotsToUpdate.add(bioIdValueIn);
                }
            }

            //Create output string for confirmation
            String confirmHtml = "<p><table border='1' cellpadding='5'>";
            confirmHtml += "<tr><th>Aliquot ID</th></tr>";
            for (int i = 0; i < aliquotsToUpdate.size(); i++) {
                confirmHtml += "<tr><td>" + aliquotsToUpdate.get(i) + "</td></tr>";
            }
            confirmHtml += "</table></p>";

            //Compile the SQL query to run the update        
            String sqlConds = "";
            for (int i = 0; i < aliquotsToUpdate.size(); i++) {
                sqlConds += " bio_id=? OR ";
            }
            //Trim the last "OR"
            sqlConds = sqlConds.substring(0, sqlConds.length() - 4);
            sqlConds += ";";

            try {
                String[] tumorTypes = {"ACC", "Pheo", "NAPACA", "APA"};
                for (int i = 0; i < tumorTypes.length; i++) {
                    String sql = "UPDATE " + tumorTypes[i] + "_Biomaterial_Freezer_Information SET material_used='Yes' WHERE ";
                    sql += sqlConds;

                    PreparedStatement ps = conn.prepareStatement(sql);
                    for (int j = 0; j < aliquotsToUpdate.size(); j++) {
                        ps.setString((j + 1), aliquotsToUpdate.get(j));
                    }

                    int updateBiomaterialForm = ps.executeUpdate();
                }

            } catch (Exception e) {
                logger.debug("Error (markAliquotsAsUsed): " + e.getMessage());
            }
            return confirmHtml;
        } else {
            logger.debug("No aliquots marked as used...");
            return "<p><strong>None</strong></p>";
        }
    }

    //This method was modified by Purathani 
    public String markAliquotsAsTransferred(HttpServletRequest request, Enumeration inputs, Connection conn) {

        Vector<Vector> aliquotValues = new Vector<Vector>();

        while (inputs.hasMoreElements()) {
            String input = (String) inputs.nextElement();
            if (input == null) {
                input = "";
            }
            if (input.contains("material_transfer")) {
                String valueIn = request.getParameter(input);
                if (valueIn == null) {
                    valueIn = "";
                }
                if (valueIn.equalsIgnoreCase("null")) {
                    valueIn = "";
                }
                if (!valueIn.equals("")) {
                    String bioIdIn = "";
                    //if(input.lastIndexOf("_") != -1){
                    bioIdIn = input.substring(input.lastIndexOf("_") + 1, input.length());
                    //}
                    Vector<String> aliquotIn = new Vector<String>();
                    aliquotIn.add(bioIdIn);
                    aliquotIn.add(valueIn);
                    aliquotValues.add(aliquotIn);
                }
            }
        }

        //Create output string for confirmation
        String confirmHtml = "<p><table border='1' cellpadding='5'>";
        confirmHtml += "<tr><th>Aliquot ID</th><th>Transferred to</th></tr>";
        for (int i = 0; i < aliquotValues.size(); i++) {
            Vector<String> aliquotIn = aliquotValues.get(i);
            confirmHtml += "<tr><td>" + aliquotIn.get(0) + "</td><td>" + aliquotIn.get(1) + "</td></tr>";
        }
        confirmHtml += "</table></p>";

        //Compile the SQL query to run the update        
        try {
            String[] tumorTypes = {"ACC", "Pheo", "NAPACA", "APA"};
            for (int i = 0; i < tumorTypes.length; i++) {
                for (int j = 0; j < aliquotValues.size(); j++) {
                    Vector<String> aliquotIn = aliquotValues.get(j);                    
                    String sql = "UPDATE " + tumorTypes[i] + "_Biomaterial_Freezer_Information SET material_transferred=?,material_used='Yes' WHERE bio_id=?;";
                    PreparedStatement ps = conn.prepareStatement(sql);
                    ps.setString(1, aliquotIn.get(1));
                    ps.setString(2, aliquotIn.get(0));
                    int updateBiomaterialForm = ps.executeUpdate();
                    ps.close();
                }
            }
            
            Date today = new Date();
            String transferd_date = new SimpleDateFormat("yyyy-MM-dd").format(today);
           
            HashMap destination_center_map  = new HashMap();
            int transfer_group_id = 0;
            
            for (int i = 0; i < aliquotValues.size(); i++) {
                Vector<String> aliquotIn = aliquotValues.get(i);
                String selectfreezerSql = "SELECT acc_biomaterial_location_id,acc_biomaterial_id, ensat_id,center_id    FROM acc_biomaterial_freezer_information WHERE bio_id = " + aliquotIn.get(0);
                PreparedStatement ps3 = conn.prepareStatement(selectfreezerSql);
                ResultSet rs3 = ps3.executeQuery();
                
                 
                if(rs3.first()){
                String ACC_BIOMATERIAL_ID = rs3.getString(2);
                String ENSAT_ID = rs3.getString(3);
                String CENTER_ID = rs3.getString(4);
                String ACC_BIOMATERIAL_LOCATION_ID = rs3.getString(1);
                String DESTINATION_CENTER_ID = aliquotIn.get(1);
                ps3.close();
               
                if(!destination_center_map.containsKey( aliquotIn.get(1)))
                {
                    transfer_group_id = getNextTransferGroupId(conn); 
                    destination_center_map.put( aliquotIn.get(1),transfer_group_id);
                }
                
                // Insert biomaterial detail which are to be transferred into Acc_biomaterial_aliquots_transfer table with unique group_id
                String insertTransferBioMaterialSQL = "INSERT INTO ACC_BIOMATERIAL_ALIQUOTS_TRANSFER(ACC_BIOMATERIAL_ID,ENSAT_ID,"
                        + "CENTER_ID,ACC_BIOMATERIAL_LOCATION_ID,ACC_BIOMATERIAL_TRANSFER_GROUP_ID,DESTINATION_CENTER_ID,TRANSFERED_DATE,STATUS)"
                        + "VALUES"
                        + "(?,?,?,?,?,?,?,?)";
                PreparedStatement ps4 = conn.prepareStatement(insertTransferBioMaterialSQL);
                ps4.setString(1, ACC_BIOMATERIAL_ID);
                ps4.setString(2, ENSAT_ID);
                ps4.setString(3, CENTER_ID);
                ps4.setString(4, ACC_BIOMATERIAL_LOCATION_ID);
                ps4.setString(5, destination_center_map.get(aliquotIn.get(1)).toString());
                ps4.setString(6, DESTINATION_CENTER_ID);
                ps4.setString(7, transferd_date);
                ps4.setString(8, "BEGIN");
                int updateBiomaterialForm = ps4.executeUpdate();
                ps4.close();
                }

            }
        } catch (Exception e) {
            logger.debug("Error (markAliquotsAsTransferred): " + e.getMessage());
        }
        return confirmHtml;
    }

    private String getCenterCodesMenu(Vector<String> centerCodes, String bioId) {

        String menuOut = "<select name='material_transfer_" + bioId + "'>";
        menuOut += "<option value=''>[Select...]</option>";
        for (int i = 0; i < centerCodes.size(); i++) {
            String centerCode = centerCodes.get(i);
            menuOut += "<option value='" + centerCode + "'>" + centerCode + "</option>";
        }
        menuOut += "</select>";

        return menuOut;
    }

    public boolean checkOccupancy(Vector<Vector> freezerData, Vector<String> rowIn) {

        boolean matchFound = false;
        int dataRowCount = 0;
        while (dataRowCount < freezerData.size() && !matchFound) {

            Vector<String> freezerDataIn = freezerData.get(dataRowCount);
            if (freezerDataIn.get(3).equals(rowIn.get(4))
                    && freezerDataIn.get(4).equals(rowIn.get(5))
                    && freezerDataIn.get(5).equals(rowIn.get(6))
                    && freezerDataIn.get(6).equals(rowIn.get(7))
                    && freezerDataIn.get(7).equals(rowIn.get(8))
                    && freezerDataIn.get(8).equals(rowIn.get(9))) {
                matchFound = true;
            } else {
                dataRowCount++;
            }
        }
        return matchFound;
    }

    public boolean checkCapacities(int[] capacities, Vector<String> rowIn) {

        boolean overCapacity = false;
        int rowInSize = rowIn.size();
        int OFFSET = 5;

        int rowValueCount = OFFSET;
        while (rowValueCount < (rowInSize - 2) && !overCapacity) {
            String valueIn = rowIn.get(rowValueCount);
            int valueInInt = -1;
            try {
                valueInInt = Integer.parseInt(valueIn);
            } catch (NumberFormatException nfe) {
                valueInInt = -1;
            }
            if (valueInInt > capacities[rowValueCount - OFFSET]) {
                overCapacity = true;
            } else {
                rowValueCount++;
            }
        }
        return overCapacity;
    }

    private Vector<String> getBioParamNames() {

        Vector<String> bioParams = new Vector<String>();
        bioParams.add("tumor_tissue_frozen");
        bioParams.add("tumor_tissue_ensat_sop");
        bioParams.add("tumor_tissue_paraffin");
        bioParams.add("tumor_tissue_dna");
        bioParams.add("leukocyte_dna");
        bioParams.add("plasma");

        bioParams.add("heparin_plasma");
        bioParams.add("serum");
        bioParams.add("24h_urine");
        bioParams.add("24h_urine_vol");
        bioParams.add("spot_urine");
        bioParams.add("normal_tissue");

        bioParams.add("normal_tissue_paraffin");
        bioParams.add("normal_tissue_dna");
        bioParams.add("whole_blood");
        bioParams.add("blood_clot");

        bioParams.add("associated_study");
        bioParams.add("associated_study_phase_visit");
        bioParams.add("freezer_information");

        return bioParams;
    }

    public boolean uploadFreezerManifest(String centerid, String dbn, Vector<Vector> unoccupiedRows, Connection conn) {

        Vector<String> bioParams = this.getBioParamNames();

        boolean success = true;
        int rowNum = unoccupiedRows.size();
        logger.debug("rowNum: " + rowNum);
        try {
            //Find the unique parameter names in the list for each patient (unique on: ensat ID, date, paramName)
            Vector<Vector> uniqueParamNames = new Vector<Vector>();
            for (int i = 0; i < rowNum; i++) {
                Vector<String> rowIn = unoccupiedRows.get(i);
                String ensatIdIn = rowIn.get(0);
                String dateIn = rowIn.get(1);
                String paramNameIn = rowIn.get(2);

                boolean entryFound = false;
                int uniqueCount = 0;
                while (!entryFound && uniqueCount < uniqueParamNames.size()) {
                    Vector<String> uniqueIn = uniqueParamNames.get(uniqueCount);
                    if (uniqueIn.get(0).equals(ensatIdIn)
                            && uniqueIn.get(2).equals(paramNameIn)
                            && uniqueIn.get(1).equals(dateIn)) {
                        //Add 1 to the aliquot count, set that back into the VxV, and break out of the loop
                        entryFound = true;
                        String countIn = uniqueIn.get(3);
                        int countInInt = Integer.parseInt(countIn);
                        countInInt++;
                        uniqueIn.set(3, "" + countInInt);
                        uniqueParamNames.set(uniqueCount, uniqueIn);
                    } else {
                        uniqueCount++;
                    }
                }
                if (!entryFound) {
                    //Add a new entry to the list, and add to the VxV
                    Vector<String> rowOut = new Vector<String>();
                    rowOut.add(ensatIdIn);
                    rowOut.add(dateIn);
                    rowOut.add(paramNameIn);
                    rowOut.add("1"); //new entry so aliquot count is 1
                    uniqueParamNames.add(rowOut);
                }
            }

            /*for(int i=0; i<unoccupiedRows.size(); i++){
             logger.debug(unoccupiedRows.get(i));
             }*/
            //UniqueParamNames has the following construction: ENSAT-ID, date, material, unique count, [form ID, new form]
            logger.debug("uniqueParamNames compiled for " + centerid + "...");
            /*for(int i=0; i<uniqueParamNames.size(); i++){
             logger.debug(uniqueParamNames.get(i));
             }*/

            //THIS IS THE PROBLEM BIT - WE NEED TO ESTABLISH IF THERE IS A FORM ID ALREADY THERE THAT CORRESPONDS TO THIS MATERIAL
            //IF IT IS, USE THIS AS THE REFERENCE POINT
            //IF NOT, USE THE NEXT ONE IN LINE (ABOVE)
            //Add the form IDs to the uniqueParamNames VxV
            logger.debug("Now check the form IDs within this list (are they new or do they already exist)...");
            uniqueParamNames = this.getFormIds(dbn, conn, uniqueParamNames);
            /*for(int i=0; i<uniqueParamNames.size(); i++){
             logger.debug(uniqueParamNames.get(i));
             }*/

            logger.debug("Now insert the root biomaterial forms into the database...");
            for (int i = 0; i < uniqueParamNames.size(); i++) {

                Vector<String> uniqueParam = uniqueParamNames.get(i);
                String newForm = uniqueParam.get(5);
                String paramValueIn = this.getParamTypeDisp(uniqueParam.get(2));

                if (newForm.equals("Y")) {
                    String bioSql = "INSERT INTO " + dbn + "_Biomaterial (";
                    bioSql += "" + dbn.toLowerCase() + "_biomaterial_id,ensat_id,center_id,biomaterial_date,";

                    //Get the form Id by matching the unoccupiedRows from the uniqueParamNames
                    for (int j = 0; j < bioParams.size(); j++) {
                        String paramNameIn = bioParams.get(j);
                        bioSql += "" + paramNameIn + ",";
                    }
                    //Trim the last comma
                    bioSql = bioSql.substring(0, bioSql.length() - 1);

                    bioSql += ") VALUES (?,?,?,?,";
                    for (int j = 0; j < bioParams.size(); j++) {
                        bioSql += "?,";
                    }
                    //Trim the last comma
                    bioSql = bioSql.substring(0, bioSql.length() - 1);
                    bioSql += ");";

                    String ensatId = uniqueParam.get(0);
                    int hyphenIndex = ensatId.indexOf("-");
                    String pid = "";
                    if (hyphenIndex != -1) {
                        pid = ensatId.substring(hyphenIndex + 1, ensatId.length());
                    }
                    String bioDate = uniqueParam.get(1);
                    String formId = uniqueParam.get(4);

                    PreparedStatement ps1_1 = conn.prepareStatement(bioSql);
                    ps1_1.setString(1, formId);
                    ps1_1.setString(2, pid);
                    ps1_1.setString(3, centerid);
                    ps1_1.setString(4, bioDate);
                    for (int j = 0; j < bioParams.size(); j++) {
                        boolean paramException = bioParams.get(j).equalsIgnoreCase("24h_urine_vol")
                                || bioParams.get(j).equalsIgnoreCase("associated_study")
                                || bioParams.get(j).equalsIgnoreCase("associated_study_phase_visit")
                                || bioParams.get(j).equalsIgnoreCase("freezer_information");
                        String paramValueToInsert = "No";
                        if (bioParams.get(j).equalsIgnoreCase(paramValueIn)) {
                            paramValueToInsert = "Yes";
                        } else if (paramException) {
                            paramValueToInsert = "";
                        }
                        ps1_1.setString(j + 5, paramValueToInsert);
                    }
                    /*if(i == 0){
                     logger.debug("bioSql: " + bioSql);
                     }*/
                    int update = ps1_1.executeUpdate();
                }
            }

            logger.debug("Checking for the last individual aliquot ID number...");
            //Run a check to get the last aliquot_id
            String aliquotIdCheckSql = "SELECT " + dbn.toLowerCase() + "_biomaterial_location_id FROM " + dbn + "_Biomaterial_Freezer_Information ORDER BY " + dbn.toLowerCase() + "_biomaterial_location_id DESC;";
            PreparedStatement ps2 = conn.prepareStatement(aliquotIdCheckSql);
            ResultSet rs2 = ps2.executeQuery();
            String aliquotId = "";
            //Grab the top value only
            if (rs2.next()) {
                aliquotId = rs2.getString(1);
            }
            int aliquotIdInt = 0;
            try {
                aliquotIdInt = Integer.parseInt(aliquotId);
            } catch (NumberFormatException nfe) {
                aliquotIdInt = 0;
            }
            aliquotIdInt++;
            aliquotId = "" + aliquotIdInt;

            logger.debug("Insert aliquot information...");
            //Now execute the aliquot SQL (one for each unique parameter name in the list)
            for (int i = 0; i < uniqueParamNames.size(); i++) {
            //for(int i=0; i<1; i++){

                Vector<String> uniqueIn = uniqueParamNames.get(i);
                //logger.debug("uniqueIn: " + uniqueIn);
                String aliquotSql = "INSERT INTO " + dbn + "_Biomaterial_Aliquots VALUES(?,?,?,?,?,?);";

                String ensatId = uniqueIn.get(0);
                int hyphenIndex = ensatId.indexOf("-");
                String pid = "";
                if (hyphenIndex != -1) {
                    pid = ensatId.substring(hyphenIndex + 1, ensatId.length());
                }
                String paramName = uniqueIn.get(2);
                String aliquotNum = uniqueIn.get(3);
                String formId = uniqueIn.get(4);

                PreparedStatement ps2_1 = conn.prepareStatement(aliquotSql);
                ps2_1.setString(1, aliquotId);
                ps2_1.setString(2, formId);
                ps2_1.setString(3, pid);
                ps2_1.setString(4, centerid);
                ps2_1.setString(5, paramName);
                ps2_1.setString(6, aliquotNum);

                int aliquotIdIntInner = Integer.parseInt(aliquotId);
                aliquotIdIntInner++;
                aliquotId = "" + aliquotIdIntInner;

                int update = ps2_1.executeUpdate();
            }

            logger.debug("Checking for the last individual freezer information ID number...");
            //Run a check to get the last freezer_location_id            
            String freezerLocIdCheckSql = "SELECT " + dbn.toLowerCase() + "_biomaterial_location_id FROM " + dbn + "_Biomaterial_Freezer_Information ORDER BY " + dbn.toLowerCase() + "_biomaterial_location_id DESC;";
            PreparedStatement ps3 = conn.prepareStatement(freezerLocIdCheckSql);
            ResultSet rs3 = ps3.executeQuery();
            String freezerLocId = "";
            //Grab the top value only
            if (rs3.next()) {
                freezerLocId = rs3.getString(1);
            }

            int freezerLocIdInt = 0;
            try {
                freezerLocIdInt = Integer.parseInt(freezerLocId);
            } catch (NumberFormatException nfe) {
                freezerLocIdInt = 0;
            }
            freezerLocIdInt++;
            String freezerLocationId = "" + freezerLocIdInt;

            logger.debug("Insert freezer information...");
            //Now execute the update of the freezer location information
            //String freezerLocSql = "";

            for (int i = 0; i < rowNum; i++) {

                String freezerLocSql = "INSERT INTO " + dbn + "_Biomaterial_Freezer_Information VALUES(";
                //Need the freezer_location_id
                //Need the biomaterial_form_id
                //Ensat ID and centerid are transposed
                //Remove the date value (not relevant to this table)
                freezerLocSql += "?,?,?,?,?,?,?,?,?,?,?,?,?,?,?";
                freezerLocSql += ");";

                PreparedStatement ps4 = conn.prepareStatement(freezerLocSql);

                Vector<String> rowIn = unoccupiedRows.get(i);
                //Vector<String> uniqueIn = uniqueParamNames.get(i);
                int rowInSize = rowIn.size();
                String ensatId = rowIn.get(0);
                int hyphenIndex = ensatId.indexOf("-");
                String pid = "";
                if (hyphenIndex != -1) {
                    pid = ensatId.substring(hyphenIndex + 1, ensatId.length());
                }

                String date = rowIn.get(1);
                String param = rowIn.get(2);
                String aliquotNum = rowIn.get(3);

                //String formId = uniqueIn.get(4);
                //Get the form Id by matching the unoccupiedRows from the uniqueParamNames (search and retrieve from this)
                String fullEnsatId = centerid + "-" + pid;
                //logger.debug("fullEnsatId: " + fullEnsatId);
                int uniqueCount = 0;
                boolean uniqueFound = false;
                String formId = "";
                while (uniqueCount < uniqueParamNames.size() && !uniqueFound) {
                    Vector<String> paramIn = uniqueParamNames.get(uniqueCount);
                    /*if(uniqueCount == 0){
                     logger.debug("paramIn: " + paramIn);
                     }*/
                    String ensatIdIn = paramIn.get(0);
                    String dateIn = paramIn.get(1);
                    String paramNameIn = paramIn.get(2);

                    if (fullEnsatId.equals(ensatIdIn)
                            && param.equals(paramNameIn)
                            && date.equals(dateIn)) {
                        uniqueFound = true;
                        formId = paramIn.get(4);
                    }
                    uniqueCount++;
                }

                ps4.setString(1, freezerLocationId);
                ps4.setString(2, formId);
                ps4.setString(3, pid);
                ps4.setString(4, centerid);
                ps4.setString(5, aliquotNum);
                ps4.setString(6, param);
                for (int j = 4; j < rowInSize; j++) {
                    ps4.setString(j + 3, rowIn.get(j));
                }
                ps4.setString(13, ""); //Bio ID
                ps4.setString(14, "No"); //Material used
                ps4.setString(15, ""); //Material transferred (center code)

                int freezerLocationIdInt = Integer.parseInt(freezerLocationId);
                freezerLocationIdInt++;
                freezerLocationId = "" + freezerLocationIdInt;

                int executeUpdate = ps4.executeUpdate();
                //Assumes 0 = no records (failure), anything else = success
                boolean successThisRun = (executeUpdate != 0);
                if (!successThisRun) {
                    success = false;
                }
            }
            conn.close();

        } catch (Exception e) {
            logger.debug("('" + username + "') - Error (uploadFreezerManifest): " + e.getMessage());
        }
        return success;
    }

    private Vector<Vector> getFormIds(String dbn, Connection conn, Vector<Vector> uniqueParamNames) throws Exception {

        //Find the baseline bio ID
        String bioIdCheckSql = "SELECT " + dbn.toLowerCase() + "_biomaterial_id FROM " + dbn + "_Biomaterial ORDER BY " + dbn.toLowerCase() + "_biomaterial_id DESC;";
        PreparedStatement ps1 = conn.prepareStatement(bioIdCheckSql);
        ResultSet rs1 = ps1.executeQuery();
        String bioId = "";
        //Grab the top value only
        if (rs1.next()) {
            bioId = rs1.getString(1);
        }
        int bioIdInt = 0;
        try {
            bioIdInt = Integer.parseInt(bioId);
        } catch (NumberFormatException nfe) {
            bioIdInt = 0;
        }
        bioIdInt++;
            //bioId = "" + bioIdInt;

        logger.debug("last bioId in this section (" + dbn + ") is: " + bioIdInt);

        //For each param name, check if there is a form ID that corresponds       
        for (int i = 0; i < uniqueParamNames.size(); i++) {
            Vector<String> paramIn = uniqueParamNames.get(i);
            String ensatId = paramIn.get(0);
            //logger.debug("ensatId: " + ensatId);
            int hyphenIndex = ensatId.indexOf("-");
            if (hyphenIndex != -1) {
                String pid = ensatId.substring(ensatId.indexOf("-") + 1, ensatId.length());
                String centerid = ensatId.substring(0, ensatId.indexOf("-"));
                String bioDate = paramIn.get(1);
                String paramType = paramIn.get(2);
                String paramTypeDisp = this.getParamTypeDisp(paramType);
                //logger.debug("paramTypeDisp: " + paramTypeDisp);
                String formCheckSql = "SELECT " + dbn.toLowerCase() + "_biomaterial_id FROM " + dbn + "_Biomaterial WHERE biomaterial_date=? AND ensat_id=? AND center_id=? AND " + paramTypeDisp + "=?;";
                //logger.debug("formCheckSql: " + formCheckSql);
                PreparedStatement formCheckPs = conn.prepareStatement(formCheckSql);
                formCheckPs.setString(1, bioDate);
                formCheckPs.setString(2, pid);
                formCheckPs.setString(3, centerid);
                formCheckPs.setString(4, "Yes");

                String bioIdIn = "";
                ResultSet formCheckRs = formCheckPs.executeQuery();
                boolean newForm = true;
                if (formCheckRs.next()) {
                    bioIdIn = formCheckRs.getString(1);
                    newForm = false;
                }
                if (bioIdIn.equals("")) {
                    bioIdIn = "" + bioIdInt;
                    bioIdInt++;
                }
                paramIn.add(bioIdIn);
                if (newForm) {
                    paramIn.add("Y");
                } else {
                    paramIn.add("N");
                }
                uniqueParamNames.set(i, paramIn);
            }
        }
        return uniqueParamNames;
    }

    private String getParamTypeDisp(String paramType) {
        String paramTypeDisp = "";
        if (paramType.equals("urine")) {
            paramTypeDisp = "24h_urine";
        } else {
            paramTypeDisp = paramType;
        }
        return paramTypeDisp;
    }

    private Vector<Vector> orderExcelOutDisp(Vector<Vector> excelOutDisp) {

        /**
         * Indices to display
         *
         * ENSAT-ID: 0 date: 1 material: 2 aliquot: 3 Freezer: 4 F-Shelf: 5 (7)
         * Rack: 6 (8) R-Shelf: 7 (5) Box: 8 (6) Pos: 9
         */
        Vector<String> headerRowIn = excelOutDisp.get(0);
        int headerRowSize = headerRowIn.size();
        int[] dispIndices = new int[headerRowSize];

        for (int i = 0; i < headerRowSize; i++) {
            String elementIn = headerRowIn.get(i);
            if (elementIn.equalsIgnoreCase("ENSAT-ID")) {
                dispIndices[0] = i;
            } else if (elementIn.equalsIgnoreCase("date")) {
                dispIndices[1] = i;
            } else if (elementIn.equalsIgnoreCase("material")) {
                dispIndices[2] = i;
            } else if (elementIn.equalsIgnoreCase("aliquot")) {
                dispIndices[3] = i;
            } else if (elementIn.equalsIgnoreCase("Freezer")) {
                dispIndices[4] = i;
            } else if (elementIn.equalsIgnoreCase("F-Shelf")) {
                dispIndices[5] = i;
            } else if (elementIn.equalsIgnoreCase("Rack")) {
                dispIndices[6] = i;
            } else if (elementIn.equalsIgnoreCase("R-Shelf")) {
                dispIndices[7] = i;
            } else if (elementIn.equalsIgnoreCase("Box")) {
                dispIndices[8] = i;
            } else if (elementIn.equalsIgnoreCase("Pos")) {
                dispIndices[9] = i;
            }
        }

        int rowNumDisp = excelOutDisp.size();
        Vector<Vector> excelOutDispOrdered = new Vector<Vector>();
        for (int i = 0; i < rowNumDisp; i++) {

            Vector<String> rowIn = excelOutDisp.get(i);
            int rowSize = rowIn.size();
            logger.debug("(excelOutDispOrdered): " + rowSize);
            if (rowSize >= 10) {
                String[] rowElements = new String[rowSize];
                Vector<String> rowOut = new Vector<String>();

                for (int j = 0; j < rowSize; j++) {
                    rowElements[j] = rowIn.get(j);
                }
                for (int j = 0; j < rowSize; j++) {
                    rowOut.add(rowElements[dispIndices[j]]);
                }
                excelOutDispOrdered.add(rowOut);
            }
        }
        return excelOutDispOrdered;
    }

    public String getFreezerSheetOutput(Vector<Vector> excelOutDisp, int sheetIndex, Vector<Vector> freezerData, int[] capacities) {

        String sheetOutput = "";
        sheetOutput += "<h2>Sheet " + (sheetIndex + 1) + "</h2>";
        sheetOutput += "<table border='1' cellpadding='5'>";
        //int rowNumDisp = excelOutDisp.size();

        //Re-order the data based on the headers        
        Vector<Vector> excelOutDispOrdered = this.orderExcelOutDisp(excelOutDisp);
        int rowNumDisp = excelOutDispOrdered.size();

        for (int i = 0; i < rowNumDisp; i++) {

            Vector<String> rowIn = excelOutDispOrdered.get(i);
            int rowSize = rowIn.size();
            sheetOutput += "<tr>";
            for (int j = 0; j < rowSize; j++) {
                String valueIn = rowIn.get(j);
                if (valueIn == null) {
                    valueIn = "";
                }
                if (i == 0) {
                    sheetOutput += "<th>" + valueIn + "</th>";
                } else {
                    sheetOutput += "<td>" + valueIn + "</td>";
                }
            }

            //Now check the occupancy
            if (i == 0) {
                sheetOutput += "<th>Occupied</th>";
            } else {
                boolean occupied = this.checkOccupancy(freezerData, rowIn);
                if (occupied) {
                    sheetOutput += "<td><div class='errorLabel'>Yes</div></td>";
                } else {
                    boolean overCapacity = this.checkCapacities(capacities, rowIn);
                    if (overCapacity) {
                        sheetOutput += "<td><div class='errorLabel'>Invalid (beyond capacity)</div></td>";
                    } else {
                        sheetOutput += "<td>No</td>";
                        freezerPosToUpload.add(rowIn);
                    }
                }
            }
            sheetOutput += "</tr>";
        }
        sheetOutput += "</table>";
        return sheetOutput;
    }

    //This method was modified by Purathani 
    public String getAliquotsTransferred(Connection conn, String centerid) {

        String outputHtml = "";
        HashMap destination_center_map  = new HashMap();
        int center_count = 1;
        Vector<Vector> aliquotsTransferred = new Vector<Vector>();
        try {
            // Get transferred biomaterial sample detail from transfer table for given center id
            //String sql = "SELECT center_id,ensat_id,material,aliquot_sequence_id,bio_id,material_transferred FROM ACC_Biomaterial_Freezer_Information WHERE center_id=? AND material_transferred!=?;";
            String sql = "SELECT F.center_id, F.ensat_id, F.material,F.aliquot_sequence_id,F.bio_id,F.material_transferred, T.status, T.transfered_date FROM " +
                           "ACC_Biomaterial_Freezer_Information as F left join acc_biomaterial_aliquots_transfer as T ON " +
                            "F.acc_biomaterial_location_id = T.acc_biomaterial_location_id AND F.ensat_id = T.ensat_id " +
                            "AND F.center_id = T.center_id AND F.acc_biomaterial_id = T.acc_biomaterial_id " +
                            " WHERE F.center_id= ? AND F.material_transferred!= ? ;";
            PreparedStatement ps = conn.prepareStatement(sql);
            ps.setString(1, centerid);
            ps.setString(2, "");
            ResultSet rs = ps.executeQuery();
            while (rs.next()) {
                Vector<String> bioDetailsIn = new Vector<String>();
                for (int i = 0; i < 8; i++) {
                    String bioDetailIn = rs.getString(i + 1);
                    if (bioDetailIn == null) {
                        bioDetailIn = "";
                    }
                    bioDetailsIn.add(bioDetailIn);       
                 }
                 aliquotsTransferred.add(bioDetailsIn);
            }
            int transfer_group_id = 5;
            
            /*Iterator it = destination_center_map.entrySet().iterator();
            while (it.hasNext()) {
                Map.Entry pair = (Map.Entry)it.next();
               // System.out.println(pair.getKey() + " = " + pair.getValue());
               // it.remove(); // avoids a ConcurrentModificationException
               transfer_group_id = getNextTransferGroupId(conn); 
               */
            outputHtml += "<form method='post' action='./jsp/biobank/qr_code_generate.jsp?trans_id=" + transfer_group_id +"'>";
            outputHtml += "<table border='1' cellpadding='5'>";
            outputHtml += "<tr><th>ENSAT ID</th><th>Material</th><th>Bio ID</th><th>To center</th><th>Status</th><th>TransferDate</th></tr>";

            for (int i = 0; i < aliquotsTransferred.size(); i++) {
                Vector<String> aliquotIn = aliquotsTransferred.get(i);
                outputHtml += "<tr>";
                for (int j = 0; j < aliquotIn.size(); j++) {
                    if (j == 0) {
                        outputHtml += "<td>" + aliquotIn.get(j) + "-" + aliquotIn.get(j + 1) + "</td>";
                    } else if (j == 2) {
                        outputHtml += "<td>" + aliquotIn.get(j) + " (" + aliquotIn.get(j + 1) + ")</td>";
                    } else if (j == 1 || j == 3 ) {
                        outputHtml += "";
                    } else if (j == 4) {
                        outputHtml += "<td>" + aliquotIn.get(j) + "</td>";
                    } else if (j == 5) {
                        String strIn = aliquotIn.get(j);
                        if (strIn == null) {
                            strIn = "";
                        }
                        String transferCenter = strIn; //strIn.substring(0, strIn.indexOf(" - "));
                        String transferStatus = "";//strIn.substring(strIn.indexOf(" - ") + 3, strIn.length());
                        outputHtml += "<td>" + transferCenter + "</td>" ;
                    }
                    else if (j == 6) {
                        outputHtml += "<td>" + aliquotIn.get(j) + "</td>";
                    } 
                    else if (j == 7) {
                        outputHtml += "<td>" + aliquotIn.get(j) + "</td>";
                    } 
                }
                outputHtml += "</tr>";
            }
            outputHtml += "</table>";
            outputHtml += "<input type='submit' value='Generate QR code' text='dfds'/>";
            outputHtml += "</form>";
            
            
        } catch (Exception e) {
            logger.debug("Error (getAliquotsTransferred): " + e.getMessage());
        }

        return outputHtml;
    }
    
     //This method was created by Purathani 
    public String getAliquotsTransferredDetail(Connection conn, String centerid) {

        String outputHtml = "";
        Date today = new Date();
        String transferd_date = new SimpleDateFormat("yyyy-MM-dd").format(today);
           
        try {
            String sql = "SELECT distinct acc_biomaterial_transfer_group_id from acc_biomaterial_aliquots_transfer where transfered_date = ?";
            PreparedStatement ps = conn.prepareStatement(sql);
            ps.setString(1, transferd_date);
            
            ResultSet rs = ps.executeQuery();
            Vector<String> transIds = new Vector<String>();
            while (rs.next()) {
                String tras_group_id = rs.getString(1);
                if (tras_group_id == null) {
                    tras_group_id = "";
                }
                transIds.add(tras_group_id);       
            }
          
            for (int i = 0; i < transIds.size(); i++) {
                String group_id = transIds.get(i);
                outputHtml += generateTransferredAliquotsTable(conn, centerid, group_id);
                
            }
        } catch (Exception e) {
            logger.debug("Error (getAliquotsTransferred): " + e.getMessage());
        }

        return outputHtml;
    }
    
    //This method was created by Purathani 
    public String generateTransferredAliquotsTable(Connection conn, String centerid, String group_id) {

        String outputHtml = "";
       
        Vector<Vector> aliquotsTransferred = new Vector<Vector>();
        try {
            // Get biomaterial sample detail which are to be transferred to a destination center
            //String sql = "SELECT center_id,ensat_id,material,aliquot_sequence_id,bio_id,material_transferred FROM ACC_Biomaterial_Freezer_Information WHERE center_id=? AND material_transferred!=?;";
            String sql = "SELECT F.center_id, F.ensat_id, F.material,F.aliquot_sequence_id,F.bio_id,F.material_transferred, T.status, T.transfered_date FROM " +
                           "ACC_Biomaterial_Freezer_Information as F left join acc_biomaterial_aliquots_transfer as T ON " +
                            "F.acc_biomaterial_location_id = T.acc_biomaterial_location_id AND F.ensat_id = T.ensat_id " +
                            "AND F.center_id = T.center_id AND F.acc_biomaterial_id = T.acc_biomaterial_id " +
                            " WHERE F.center_id= ? AND F.material_transferred!= ? AND T.acc_biomaterial_transfer_group_id = ? ;";
            PreparedStatement ps = conn.prepareStatement(sql);
            ps.setString(1, centerid);
            ps.setString(2, "");
            ps.setString(3, group_id);
            ResultSet rs = ps.executeQuery();
            while (rs.next()) {
                Vector<String> bioDetailsIn = new Vector<String>();
                for (int i = 0; i < 8; i++) {
                    String bioDetailIn = rs.getString(i + 1);
                    if (bioDetailIn == null) {
                        bioDetailIn = "";
                    }
                    bioDetailsIn.add(bioDetailIn);       
                 }
                 aliquotsTransferred.add(bioDetailsIn);
            }
         
            outputHtml += "<form method='post' action='./jsp/biobank/qr_code_generate.jsp?trans_id=" + group_id +"'>";
            outputHtml += "<table border='1' cellpadding='5'>";
            outputHtml += "<tr><th>ENSAT ID</th><th>Material</th><th>Bio ID</th><th>To center</th><th>Status</th><th>TransferDate</th></tr>";

            for (int i = 0; i < aliquotsTransferred.size(); i++) {
                Vector<String> aliquotIn = aliquotsTransferred.get(i);
                outputHtml += "<tr>";
                for (int j = 0; j < aliquotIn.size(); j++) {
                    if (j == 0) {
                        outputHtml += "<td>" + aliquotIn.get(j) + "-" + aliquotIn.get(j + 1) + "</td>";
                    } else if (j == 2) {
                        outputHtml += "<td>" + aliquotIn.get(j) + " (" + aliquotIn.get(j + 1) + ")</td>";
                    } else if (j == 1 || j == 3 ) {
                        outputHtml += "";
                    } else if (j == 4) {
                        outputHtml += "<td>" + aliquotIn.get(j) + "</td>";
                    } else if (j == 5) {
                        String strIn = aliquotIn.get(j);
                        if (strIn == null) {
                            strIn = "";
                        }
                        String transferCenter = strIn; //strIn.substring(0, strIn.indexOf(" - "));
                        String transferStatus = "";//strIn.substring(strIn.indexOf(" - ") + 3, strIn.length());
                        outputHtml += "<td>" + transferCenter + "</td>" ;
                    }
                    else if (j == 6) {
                        outputHtml += "<td>" + aliquotIn.get(j) + "</td>";
                    } 
                    else if (j == 7) {
                        outputHtml += "<td>" + aliquotIn.get(j) + "</td>";
                    } 
                }
                outputHtml += "</tr>";
            }
            outputHtml += "</table>";
            outputHtml += "<input type='submit' value='Generate QR code' />";
            outputHtml += "</form>";
            
            outputHtml += "&nbsp;&nbsp;<form action='GenerateExcel' method='post'>"
                    + "<input type='submit' id = 'button_export' name='button_export' value='Export as Excel' />"
                    + "<input type='hidden' name='centerid' value='"+centerid+"'/>"
                    + "<input type='hidden' name='group_id' value='"+group_id+"'/>"
                    + "</form>";
            
            
        } catch (Exception e) {
            logger.debug("Error (getAliquotsTransferred): " + e.getMessage());
        }

        return outputHtml;
    }
    
    // This method was created by Purathani
    public int getNextTransferGroupId(Connection conn)
    {
        // generate unique transfer group id 
        int trans_group_id = 0;
           try {
               String seq_val = ""; 
               String sql = "SELECT sequence_cur_value FROM sequence_data WHERE sequence_name = 'sq_transfer_group'";
               PreparedStatement ps = conn.prepareStatement(sql);
               ResultSet rs = ps.executeQuery();
               while (rs.next()) { 
                 seq_val = rs.getString(1);    
               }
               trans_group_id = Integer.parseInt(seq_val);
               
               String sql2 = "UPDATE sequence_data set sequence_cur_value  = sequence_cur_value  + sequence_increment where sequence_name = 'sq_transfer_group'";
               PreparedStatement ps2 = conn.prepareStatement(sql2);
               ps2.executeUpdate();
           }
        catch (Exception e) {
            logger.debug("Error (generate sequence group id): " + e.getMessage());
        }
       return trans_group_id;
    }

    public String getAliquotsReceived(Connection conn, String centerid) {

        String outputHtml = "";

        Vector<Vector> aliquotsReceived = new Vector<Vector>();
        try {
            String sql = "SELECT center_id,ensat_id,material,aliquot_sequence_id,bio_id,material_transferred FROM ACC_Biomaterial_Freezer_Information WHERE material_transferred LIKE ?;";
            PreparedStatement ps = conn.prepareStatement(sql);
            ps.setString(1, centerid + "%");
            ResultSet rs = ps.executeQuery();
            while (rs.next()) {
                Vector<String> bioDetailsIn = new Vector<String>();
                for (int i = 0; i < 6; i++) {
                    String bioDetailIn = rs.getString(i + 1);
                    if (bioDetailIn == null) {
                        bioDetailIn = "";
                    }
                    bioDetailsIn.add(bioDetailIn);
                }
                aliquotsReceived.add(bioDetailsIn);
            }

            outputHtml += "<form action='post' method='biobank/confirm_receipt.jsp'>";
            outputHtml += "<table border='1' cellpadding='5'>";
            outputHtml += "<tr><th>ENSAT ID</th><th>Material</th><th>Bio ID</th><th>From center</th><th>Status</th><th>Confirm receipt</th></tr>";

            for (int i = 0; i < aliquotsReceived.size(); i++) {
                Vector<String> aliquotIn = aliquotsReceived.get(i);
                outputHtml += "<tr>";
                for (int j = 0; j < aliquotIn.size(); j++) {
                    if (j == 0) {
                        outputHtml += "<td>" + aliquotIn.get(j) + "-" + aliquotIn.get(j + 1) + "</td>";
                    } else if (j == 2) {
                        outputHtml += "<td>" + aliquotIn.get(j) + " (" + aliquotIn.get(j + 1) + ")</td>";
                    } else if (j == 1 || j == 3 || j == 6) {
                        outputHtml += "";
                    } else if (j == 4) {
                        outputHtml += "<td>" + aliquotIn.get(j) + "</td>";
                    } else if (j == 5) {
                        String strIn = aliquotIn.get(j);
                        if (strIn == null) {
                            strIn = "";
                        }
                        //String transferCenter = strIn.substring(0,strIn.indexOf(" - "));
                        String transferCenter = aliquotIn.get(0);
                        String transferStatus = strIn.substring(strIn.indexOf(" - ") + 3, strIn.length());
                        outputHtml += "<td>" + transferCenter + "</td><td>" + transferStatus + "</td>";
                        if (transferStatus.equalsIgnoreCase("in transit")) {
                            outputHtml += "<td><div align='center'><input type='checkbox' value='confirm_receipt_" + aliquotIn.get(4) + "'/></div></td>";
                        } else {
                            outputHtml += "<td></td>";
                        }
                    }
                }
                outputHtml += "</tr>";
            }
            outputHtml += "</table>";

            outputHtml += "<input type='submit' value='Confirm receipt of selected aliquots'/>";
            outputHtml += "</form>";

        } catch (Exception e) {
            logger.debug("Error (getAliquotsReceived): " + e.getMessage());
        }

        return outputHtml;
    }

}
