/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package update_main;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.Statement;
import java.sql.PreparedStatement;
import java.sql.ResultSet;

import java.util.Vector;

import java.text.SimpleDateFormat;
import java.text.Format;

import java.io.DataInputStream;

import org.apache.log4j.Level;
import org.apache.log4j.Logger;
import org.apache.log4j.PropertyConfigurator;

import org.apache.poi.xssf.usermodel.*;
import org.apache.poi.ss.usermodel.DateUtil;

/**
 *
 * @author astell
 */
public class Utilities {

    private static final Logger logger = Logger.getLogger(Utilities.class);

    public Utilities() {
    }

    public String getLineColour(String dbn) {
        String lineColour = "";
        if (dbn.equals("ACC")) {
            lineColour = "class=\"parameter-line-double\"";
        } else if (dbn.equals("Pheo")) {
            lineColour = "class=\"parameter-line-double-pheo\"";
        } else if (dbn.equals("NAPACA")) {
            lineColour = "class=\"parameter-line-double-napaca\"";
        } else if (dbn.equals("APA")) {
            lineColour = "class=\"parameter-line-double-apa\"";
        }
        return lineColour;
    }

    public boolean getIdPresent(String patientSearch) {
        boolean idPresent = false;
        if (patientSearch != null) {
            int dividerIndex = patientSearch.indexOf("-");
            if (dividerIndex != -1) {
                idPresent = true;
            }
        }
        return idPresent;
    }

    public String getEnsatId(String patientSearch) {
        String ensatId = "";
        int dividerIndex = patientSearch.indexOf("-");
        if (dividerIndex != -1) {
            ensatId = patientSearch.substring(dividerIndex + 1, patientSearch.length()).trim();
        }
        return ensatId;
    }

    public String getCenterId(String patientSearch) {
        String centerId = "";
        int dividerIndex = patientSearch.indexOf("-");
        if (dividerIndex != -1) {
            centerId = patientSearch.substring(0, dividerIndex).trim();
            centerId = centerId.toUpperCase();
        }
        return centerId;
    }

    public ResultSet getRowCount(String ensatId, String centerId, Statement stmt) {

        String sql = "SELECT ensat_id,center_id,ensat_database, uploader, consent_obtained FROM Identification WHERE center_id='" + centerId + "' AND ensat_id=" + ensatId + ";";
        ResultSet rs = null;
        try {
            rs = stmt.executeQuery(sql);
        } catch (Exception e) {
            logger.debug("Error (getRowCount): " + e.getMessage());
        }
        return rs;
    }

    public String getUploader(ResultSet rs) {
        String uploader = "";

        try {
            rs.beforeFirst();
            while (rs.next()) {
                uploader = rs.getString(4);
            }
        } catch (Exception e) {
            logger.debug("Error (getUploader): " + e.getMessage());
        }
        return uploader;
    }

    public String getConsent(ResultSet rs) {
        String consent = "";

        try {
            rs.beforeFirst();
            while (rs.next()) {
                consent = rs.getString(5);
            }
        } catch (Exception e) {
            logger.debug("Error (getConsent): " + e.getMessage());
        }
        return consent;
    }

    public int getRowCount(ResultSet rs) {
        int rowCount = 0;
        try {
            while (rs.next()) {
                rowCount++;
            }
        } catch (Exception e) {
            logger.debug("Error (getRowCount): " + e.getMessage());
        }
        return rowCount;
    }

    public String getDbn(ResultSet rs) {
        String dbn = "";

        try {
            rs.beforeFirst();
            while (rs.next()) {
                dbn = rs.getString(3);
            }
        } catch (Exception e) {
            logger.debug("Error (getDbn): " + e.getMessage());
        }
        return dbn;
    }

    public String getDbid(String dbn) {
        String dbid = "";
        if (dbn.equals("ACC")) {
            dbid = "1";
        } else if (dbn.equals("Pheo")) {
            dbid = "2";
        } else if (dbn.equals("NAPACA")) {
            dbid = "3";
        } else if (dbn.equals("APA")) {
            dbid = "4";
        }
        return dbid;
    }

    public String getEnsatStage(String centerid, String pid, Connection conn) {

        String ensatStage = "Not classified";
        String sql = "SELECT ensat_classification FROM ACC_TumorStaging WHERE center_id=? AND ensat_id=?;";
        try {
            PreparedStatement ps = conn.prepareStatement(sql);
            ps.setString(1,centerid);
            ps.setString(2,pid);
            ResultSet rs = ps.executeQuery();
            if (rs.next()) {
                ensatStage = rs.getString(1);
                if (ensatStage == null || ensatStage.equals("null")) {
                    ensatStage = "Not classified";
                }
            }
        } catch (Exception e) {
            logger.debug("Error (getEnsatStage): " + e.getMessage());
        }
        return ensatStage;
    }

    public String getDateDiagnosis(String centerid, String pid, Connection conn) {

        String dateDiagnosis = "";
        String sql = "SELECT date_of_diagnosis FROM ACC_DiagnosticProcedures WHERE center_id=? AND ensat_id=?;";
        try {
            PreparedStatement ps = conn.prepareStatement(sql);
            ps.setString(1,centerid);
            ps.setString(2,pid);            
            ResultSet rs = ps.executeQuery();
            if (rs.next()) {
                dateDiagnosis = rs.getString(1);
                if (dateDiagnosis == null) {
                    dateDiagnosis = "";
                }
            }
        } catch (Exception e) {
            logger.debug("Error (getDateDiagnosis): " + e.getMessage());
        }
        return dateDiagnosis;
    }

    public String getDateDiagnosisYear(String dateDiagnosis) {
        String dateDiagnosisYear = "";
        int yearHyphen = dateDiagnosis.indexOf("-");
        if (yearHyphen != -1) {
            dateDiagnosisYear = dateDiagnosis.substring(0, yearHyphen);
        }
        return dateDiagnosisYear;
    }

    public int getDiagnosisAge(String centerid, String pid, Connection conn, String dateDiagnosisYear) {

        int diagnosisAge = 0;
        String yearBirth = "";
        String sql = "SELECT year_of_birth FROM Identification WHERE center_id=? AND ensat_id=?;";
        try {
            PreparedStatement ps = conn.prepareStatement(sql);
            ps.setString(1,centerid);
            ps.setString(2,pid);            
            ResultSet rs = ps.executeQuery();
            
            if (rs.next()) {
                yearBirth = rs.getString(1);
            }
        } catch (Exception e) {
            logger.debug("Error (getDiagnosisAge): " + e.getMessage());
        }

        //Do a calculation of dateDiagnosisYear - yearBirth
        try {
            diagnosisAge = Integer.parseInt(dateDiagnosisYear) - Integer.parseInt(yearBirth);
        } catch (NumberFormatException e) {            
            //logger.debug("Diagnosis year / year of birth - one date is blank (" + centerid + "-" + pid + ")");
            diagnosisAge = -1;
        }
        return diagnosisAge;
    }

    public ResultSet getPatientStatusCheck(String centerid, String pid, Connection conn) {
        ResultSet rs = null;
        String sql = "SELECT * FROM ACC_FollowUp WHERE center_id=? AND ensat_id=? ORDER BY followup_date DESC;";
        try {
            PreparedStatement ps = conn.prepareStatement(sql);
            ps.setString(1,centerid);
            ps.setString(2,pid);            
            rs = ps.executeQuery();
            
        } catch (Exception e) {
            logger.debug("Error (getPatientStatusCheck): " + e.getMessage());
        }
        return rs;
    }

    public String getFollowupDate(ResultSet rs) {
        String followupDate = "";
        try {
            if (rs.next()) {
                followupDate = rs.getString(4);
            }
        } catch (Exception e) {
            logger.debug("Error (getFollowupDate): " + e.getMessage());
        }
        return followupDate;
    }

    public String getPatientAliveStr(ResultSet rs) {

        String patientStatus = "";
        try {
            rs.beforeFirst();
            if (rs.next()) {
                patientStatus = rs.getString(5);
            }

            if (patientStatus.equals("Death related to ACC or treatment toxicity") || patientStatus.equals("Death not related to ACC")) {
                return "No";
            } else {
                //Run a consistency check here
                rs.beforeFirst();

                boolean inconsistencyFound = false;
                while (rs.next() && !inconsistencyFound) {
                    patientStatus = rs.getString(5);
                    if (patientStatus.equals("Death related to ACC or treatment toxicity") || patientStatus.equals("Death not related to ACC")) {
                        inconsistencyFound = true;
                    }
                }

                if (inconsistencyFound) {
                    return "Inconsistent records";
                } else {
                    return "Yes";
                }

            }

        } catch (Exception e) {
            logger.debug("Error (getPatientAliveStr): " + e.getMessage());
            return "Inconsistent records";
        }
    }

    public long getDateInterval(String followupDate, String patientAliveStr, String dateDiagnosis, String dateResection) {

        //boolean patientDead = (patientAliveStr.equals("No"));
        /*logger.debug("followupDate (d1): " + followupDate);
        logger.debug("dateDiagnosis (d2): " + dateDiagnosis);
        logger.debug("dateResection (d3): " + dateResection);*/

        SimpleDateFormat df = new SimpleDateFormat("yyyy-MM-dd");

        java.util.Date d1 = null;
        java.util.Date d2 = null;
        java.util.Date d3 = null;

        //THIS CLAUSE IS DUE TO THE ONCOLOGY STANDARD OF COUNTING ONLY UNTIL THE LAST DOCUMENTED ALIVE DATE (THEREFORE DEATH CONDITION - ABOVE - REMOVED)
        if (followupDate != null && !followupDate.equals("")) {
            try {
                d1 = df.parse(followupDate);
            } catch (Exception e) {
                //logger.debug("Error (getDateInterval): " + e.getMessage() + " - followupDate");
                logger.debug("Parsing format error (date of follow-up date)");
            }
        } else {
            d1 = new java.util.Date();
        }

        if (dateResection != null && !dateResection.equals("")) {
            try {
                d3 = df.parse(dateResection);
            } catch (Exception e) {
                //logger.debug("Error (getDateInterval): " + e.getMessage() + " - dateResection");
                logger.debug("Parsing format error (date of resection)");
            }
        } else {
            d3 = new java.util.Date();
        }

        try {
            d2 = df.parse(dateDiagnosis);
        } catch (Exception e) {
            //logger.debug("Error (getDateInterval): " + e.getMessage() + " - dateDiagnosis");
            //logger.debug("Parsing format error (date of diagnosis)");
        }

        long dateInterval = 0;
        if (d2 != null) {
            //dateInterval = (d1.getTime() - d2.getTime())/86400000;                        
            if (d2.getTime() < d3.getTime()) {
                dateInterval = (d1.getTime() - d2.getTime());
            } else {
                dateInterval = (d1.getTime() - d3.getTime());
            }
        }
        return dateInterval;
    }

    public long getDateInDays(long dateIn) {
        return dateIn / 86400000;
    }

    public long getDateInYears(long dateIn) {
        return dateIn / 365;
    }

    public ResultSet getResectionSet(String centerid, String pid, Connection conn) {

        //Disease-free survival, only for patients with resection, R0: dateOfFirstFollowUp - dateFirstDiagnosis
        //History of recurrence, only for patients with resection, R0: evidence in follow-up
        //QUERY LISTS FORMS IN ASCENDING ORDER - INTERVALS ARE COUNTED FROM FIRST SURGERY (ACCORDING TO MARTIN)
        String sql = "SELECT * FROM ACC_Surgery WHERE center_id=? AND ensat_id=? ORDER BY surgery_date ASC;";
        ResultSet rs = null;
        try {
            PreparedStatement ps = conn.prepareStatement(sql);
            ps.setString(1,centerid);
            ps.setString(2,pid);            
            rs = ps.executeQuery();
        } catch (Exception e) {
            logger.debug("Error (getResectionSet): " + e.getMessage());
        }
        return rs;

    }

    public boolean getResectionStatus(ResultSet rs) {

        boolean r0found = false;
        String resectionStatus = "";
        try {
            while (rs.next() && !r0found) {
                resectionStatus = rs.getString(7);
                r0found = resectionStatus.equals("R0");
            }
        } catch (Exception e) {
            logger.debug("Error (getResectionStatus): " + e.getMessage());
        }

        return r0found;
    }

    public String getResectionDateStr(ResultSet rs) {

        //SimpleDateFormat df = new SimpleDateFormat("yyyy-MM-dd");
        //java.util.Date dr0 = null;
        boolean r0found = false;
        String resectionDateStr = "";
        try {
            rs.beforeFirst();
            while (rs.next() && !r0found) {
                String resectionStatus = rs.getString(7);
                resectionDateStr = rs.getString(4);
                r0found = resectionStatus.equals("R0");
            }

            /*if(dr0 != null){
             dr0 = df.parse(resectionDateStr);
             }*/
        } catch (Exception e) {
            logger.debug("Error (getResectionDateStr): " + e.getMessage());
        }
        //return dr0.getTime();
        return resectionDateStr;
    }

    public long getResectionDate(ResultSet rs) {

        long resectionDateLong = 0;

        SimpleDateFormat df = new SimpleDateFormat("yyyy-MM-dd");

        java.util.Date dr0 = null;
        boolean r0found = false;
        String resectionDateStr = "";
        try {
            rs.beforeFirst();
            while (rs.next() && !r0found) {
                String resectionStatus = rs.getString(7);
                resectionDateStr = rs.getString(4);
                r0found = resectionStatus.equals("R0");
            }

            //logger.debug("resectionDateStr: " + resectionDateStr);

            //if(dr0 != null){
            dr0 = df.parse(resectionDateStr);
            resectionDateLong = dr0.getTime();
            //}
            //logger.debug("resectionDateLong: " + resectionDateLong);

        } catch (Exception e) {
            logger.debug("Error (getResectionDate): " + e.getMessage());
        }
        return resectionDateLong;
        //return resectionDateStr;
    }

    public ResultSet getDiseaseStatusCheck(String centerid, String pid, String resectionDateStr, Connection conn) {
        ResultSet rs = null;
        String sql = "SELECT * FROM ACC_FollowUp WHERE center_id=? AND ensat_id=? AND followup_date > ? ORDER BY followup_date ASC;";
        try {
            PreparedStatement ps = conn.prepareStatement(sql);
            ps.setString(1,centerid);
            ps.setString(2,pid);            
            ps.setString(3,resectionDateStr);
            rs = ps.executeQuery();
            
        } catch (Exception e) {
            logger.debug("Error (getDiseaseStatusCheck): " + e.getMessage());
        }
        return rs;
    }

    public String getRecurrenceEvidenceStr(ResultSet rs) {

        //SimpleDateFormat df = new SimpleDateFormat("yyyy-MM-dd");
        boolean recurrenceFound = false;
        //java.util.Date dateRecurrence = null;
        try {
            rs.beforeFirst();
            while (rs.next() && !recurrenceFound) {
                //String thisFollowupDate = rs.getString(4);
                String patientStatusRecurrenceCheck = rs.getString(5);

                if (patientStatusRecurrenceCheck.equals("Alive with disease")) {
                    //dateRecurrence = df.parse(thisFollowupDate);
                    recurrenceFound = true;
                }
            }
        } catch (Exception e) {
            logger.debug("Error (getRecurrenceEvidenceStr): " + e.getMessage());
        }
        String recurrenceEvidenceStr = "";
        if (recurrenceFound) {
            recurrenceEvidenceStr = "Yes";
        } else {
            recurrenceEvidenceStr = "No";
        }
        return recurrenceEvidenceStr;
    }

    public long getRecurrenceDate(ResultSet rs) {

        SimpleDateFormat df = new SimpleDateFormat("yyyy-MM-dd");

        boolean recurrenceFound = false;
        long dateRecurrence = 0;
        try {
            rs.beforeFirst();
            while (rs.next() && !recurrenceFound) {
                String thisFollowupDate = rs.getString(4);
                String patientStatusRecurrenceCheck = rs.getString(5);

                if (patientStatusRecurrenceCheck.equals("Alive with disease")) {
                    dateRecurrence = df.parse(thisFollowupDate).getTime();
                    recurrenceFound = true;
                }
            }
        } catch (Exception e) {
            logger.debug("Error (getRecurrenceEvidenceStr): " + e.getMessage());
        }
        return dateRecurrence;
    }

    public java.util.Date getRecurrenceDate(String centerid, String pid, String resectionDateStr, Statement stmt, boolean recurrenceFound, java.util.Date dateRecurrence) {

        SimpleDateFormat df = new SimpleDateFormat("yyyy-MM-dd");

        boolean recurrenceSurgeryFound = false;
        String thisSurgeryDate = "";
        try {
            //NOW RUN A CHECK OF THE SURGERY FORMS - IT IS IMPLICIT THAT IF SURGERY HAS BEEN PERFORMED THEN RECURRENCE HAS HAPPENED    
            ResultSet rs = stmt.executeQuery("SELECT * FROM ACC_Surgery WHERE center_id='" + centerid + "' AND ensat_id=" + pid + " AND surgery_date > '" + resectionDateStr + "' ORDER BY surgery_date ASC;");
            if (rs.next()) {
                thisSurgeryDate = rs.getString(4);
                recurrenceSurgeryFound = true;
            }

            if (recurrenceFound && recurrenceSurgeryFound) {
                //Compare the dates - if the surgery date is earlier than the original recurrence date (compareTo < 0), then set the recurrence date to the surgery one, otherwise leave as is 
                java.util.Date dateSurgeryRecurrence = df.parse(thisSurgeryDate);
                if (dateSurgeryRecurrence.compareTo(dateRecurrence) < 0) {
                    dateRecurrence = dateSurgeryRecurrence;
                }
            }
        } catch (Exception e) {
            logger.debug("Error (getRecurrenceDate): " + e.getMessage());
        }

        return dateRecurrence;
    }

    public long getDiseaseFreeSurvival(long dr0, String followupDate, long dateRecurrence, boolean recurrenceFound) {

        SimpleDateFormat df = new SimpleDateFormat("yyyy-MM-dd");
        java.util.Date d1Date = null;
        //THIS CLAUSE IS DUE TO THE ONCOLOGY STANDARD OF COUNTING ONLY UNTIL THE LAST DOCUMENTED ALIVE DATE (THEREFORE DEATH CONDITION - ABOVE - REMOVED)
        if (followupDate != null && !followupDate.equals("")) {
            try {
                d1Date = df.parse(followupDate);
            } catch (Exception e) {
                logger.debug("Error (getDiseaseFreeSurvival): " + e.getMessage());
            }
        } else {
            d1Date = new java.util.Date();
        }

        long d1 = d1Date.getTime();

        /*logger.debug("dr0 (dateResection): " + dr0);
        logger.debug("d1 (followupDate): " + d1);
        logger.debug("dateRecurrence: " + dateRecurrence);
        logger.debug("recurrenceFound: " + recurrenceFound);*/

        long diseaseFreeSurvival = 0;
        if (dr0 != 0) {
            if (recurrenceFound) {
                diseaseFreeSurvival = (dateRecurrence - dr0) / 86400000;
            } else {
                java.util.Date dnow = new java.util.Date();
                //SAME APPLIES HERE ABOUT THE LAST DOCUMENTED ALIVE DATE (HENCE USING d1)
                if (d1 != 0) {
                    diseaseFreeSurvival = (d1 - dr0) / 86400000;
                } else {
                    diseaseFreeSurvival = (dnow.getTime() - dr0) / 86400000;
                }
            }
        }
        //logger.debug("diseaseFreeSurvival: " + diseaseFreeSurvival);

        return diseaseFreeSurvival;

        /*SimpleDateFormat df = new SimpleDateFormat("yyyy-MM-dd");
         long diseaseFreeSurvival = 0;
         boolean recurrenceFound = false;
         try {
         java.util.Date d2 = df.parse(dateDiagnosis);
         while (rs.next() && !recurrenceFound) {
         String thisFollowupDate = rs.getString(4);                
         String patientStatusRecurrenceCheck = rs.getString(5);
         java.util.Date d3 = null;
         if (patientStatusRecurrenceCheck.equals("Alive with disease")) {
         d3 = df.parse(thisFollowupDate);                    
         recurrenceFound = true;
         } else {
         //Set the date to NOW            
         d3 = new java.util.Date();
         }
         diseaseFreeSurvival = (d3.getTime() - d2.getTime()) / 86400000;
         }
         } catch (Exception e) {
         System.out.println("Error: " + e.getMessage());
         }
         return diseaseFreeSurvival;*/
    }

    public boolean getCompleteRemission(ResultSet rs) {

        boolean recurrenceFound = false;
        try {
            while (rs.next() && !recurrenceFound) {
                String patientStatusRecurrenceCheck = rs.getString(5);
                if (patientStatusRecurrenceCheck.equals("Alive with disease")) {
                    recurrenceFound = true;
                }
            }
        } catch (Exception e) {
            logger.debug("Error (getCompleteRemission): " + e.getMessage());
        }
        boolean completeRemission = !recurrenceFound;
        return completeRemission;
    }

    public String getLostToFollowUp(ResultSet rs) {

        String lostToFollowUp = "";
        try {
            rs.beforeFirst();
            if (rs.next()) {
                lostToFollowUp = rs.getString(7);
            }
        } catch (Exception e) {
            logger.debug("Error (getLostToFollowUp): " + e.getMessage());
        }
        return lostToFollowUp;

    }

    public String getUrineNum(String centerid, String pid, Connection conn, String type) {

        //I've called this "getUrineNum" but it can actually relate to any of the points on the biomaterial forms
        int urineNum = 0;
        String[] bioTables = {"ACC_Biomaterial", "NAPACA_Biomaterial"};

        for (int i = 0; i < bioTables.length; i++) {
            //String sql = "SELECT " + type + " FROM " + bioTables[i] + " WHERE center_id='" + centerid + "' AND ensat_id=" + pid + ";";
            String sql = "SELECT " + type + " FROM " + bioTables[i] + " WHERE center_id=? AND ensat_id=?;";
            try {
                PreparedStatement ps = conn.prepareStatement(sql);
                ps.setString(1,centerid);
                ps.setString(2,pid);
                
                ResultSet rs = ps.executeQuery();
                while (rs.next()) {
                    String urinePresent = rs.getString(1);
                    if (urinePresent == null) {
                        urinePresent = "";
                    }
                    if (urinePresent.equals("Yes")) {
                        urineNum++;
                    }
                }
            } catch (Exception e) {
                logger.debug("Error (getUrineNum - " + type + "): " + e.getMessage());
            }
        }
        return "" + urineNum;
    }

    public String getMitotanePresence(String centerid, String pid, Connection conn) {

        boolean mitotanePresence = false;

        //String sql = "SELECT ensat_id,center_id FROM ACC_Mitotane WHERE center_id='" + centerid + "' AND ensat_id=" + pid + ";";
        String sql = "SELECT ensat_id,center_id FROM ACC_Mitotane WHERE center_id=? AND ensat_id=?;";
        try {
            PreparedStatement ps = conn.prepareStatement(sql);
            ps.setString(1,centerid);
            ps.setString(2,pid);            
            ResultSet rs = ps.executeQuery();
            
            mitotanePresence = rs.next();
        } catch (Exception e) {
            logger.debug("Error (getMitotanePresence): " + e.getMessage());
        }

        if (mitotanePresence) {
            return "Y";
        } else {
            return "N";
        }
    }

    public String getAdiuvoPresence(String centerid, String pid, Connection conn) {

        boolean adiuvoPresence = false;

        //String sql = "SELECT study_name FROM Associated_Studies WHERE center_id='" + centerid + "' AND ensat_id=" + pid + ";";
        String sql = "SELECT study_name FROM Associated_Studies WHERE center_id=? AND ensat_id=?;";
        try {
            PreparedStatement ps = conn.prepareStatement(sql);
            ps.setString(1,centerid);
            ps.setString(2,pid);
            
            ResultSet rs = ps.executeQuery();
            while (rs.next() && !adiuvoPresence) {
                String studyName = rs.getString(1);
                if (studyName.equals("adiuvo")) {
                    adiuvoPresence = true;
                }
            }
        } catch (Exception e) {
            logger.debug("Error (getAdiuvoPresence): " + e.getMessage());
        }

        if (adiuvoPresence) {
            return "Y";
        } else {
            return "N";
        }
    }
    
    public String getAssociatedStudies(String centerid, String pid, Connection conn) {

        String sql = "SELECT DISTINCT study_label FROM Associated_Studies WHERE center_id=? AND ensat_id=?;";
        String assocStudyStr = "";
        try {
            PreparedStatement ps = conn.prepareStatement(sql);
            ps.setString(1,centerid);
            ps.setString(2,pid);
            
            ResultSet rs = ps.executeQuery();
            while (rs.next()) {
                String studyName = rs.getString(1);
                if(studyName == null){
                    studyName = "";
                }                
                assocStudyStr += studyName + ", ";
            }
        } catch (Exception e) {
            logger.debug("Error (getAdiuvoPresence): " + e.getMessage());
        }
        //Trim 2 characters off the end
        if(assocStudyStr.length() > 2){
            assocStudyStr = assocStudyStr.substring(0,assocStudyStr.length()-2);
        }

        return assocStudyStr;
    }
    
    public String getTransferFromNapaca(String centerid, String pid, Connection conn) {

        boolean transferFromNapaca = false;
        boolean inNapaca = false;

        String sql = "SELECT ensat_database FROM Identification WHERE center_id=? AND ensat_id=?;";
        try {
            PreparedStatement ps = conn.prepareStatement(sql);
            ps.setString(1,centerid);
            ps.setString(2,pid);
            
            ResultSet rs = ps.executeQuery();
            if(rs.next()){
                String ensatDb = rs.getString(1);
                if(ensatDb == null){
                    ensatDb = "";
                }
                if(ensatDb.equals("NAPACA")){
                    inNapaca = true;
                }
            }
            rs.close();
            ps.close();
            
            if(!inNapaca){
                String napacaCheckSql = "SELECT * FROM NAPACA_DiagnosticProcedures WHERE center_id=? AND ensat_id=?;";
                PreparedStatement napacaCheckPs = conn.prepareStatement(napacaCheckSql);
                napacaCheckPs.setString(1,centerid);
                napacaCheckPs.setString(2,pid);
                ResultSet napacaCheckRs = napacaCheckPs.executeQuery();
                if(napacaCheckRs.next()){
                    transferFromNapaca = true;
                }
            }
        } catch (Exception e) {
            logger.debug("Error (getTransferFromNapaca): " + e.getMessage());
        }

        if (transferFromNapaca) {
            return "Y";
        } else {
            return "";
        }
    }

    /*public Connection getAjaxConnection() {

        Connection connection = null;
        try {
            String connectionURL = "jdbc:mysql://192.168.101.63:3306/ensat_v3";
            //String connectionURL = "jdbc:mysql://localhost:3306/ensat_v3";
            Class.forName("com.mysql.jdbc.Driver").newInstance();
            connection = DriverManager.getConnection(connectionURL, "root", "ps4Xy2a");            
        } catch (Exception e) {
            logger.debug("Error (getAjaxConnection): " + e.getMessage());
        }
        return connection;
    }*/

    public int[] getFreezerCapacities(String centerid, Connection conn) {

        int PARAM_NUM = 5;
        int[] capacities = new int[PARAM_NUM];
        String sql = "SELECT * FROM Freezer_Structure WHERE center_id=?;";

        try {
            PreparedStatement ps = conn.prepareStatement(sql);
            ps.setString(1, centerid);

            ResultSet rs = ps.executeQuery();
            if (rs.next()) {
                for (int i = 0; i < PARAM_NUM; i++) {
                    String capacityIn = rs.getString(i + 3);
                    System.out.println("capacityIn: " + capacityIn);
                    if(capacityIn == null){
                        capacityIn = "0";
                    }                    
                    capacities[i] = Integer.parseInt(capacityIn);
                }
            }
        } catch (Exception e) {
            logger.debug("Error (getFreezerCapacities): " + e.getMessage());            
        }

        return capacities;
    }

    public String checkForGap(Vector<Vector> freezerDataIn, int[] capacities) {

        //logger.debug("size (with blanks and nulls): " + freezerDataIn.size());            
        //System.out.println("size (with blanks and nulls): " + freezerDataIn.size());            
        
        Vector<Vector> freezerDataProcess = new Vector<Vector>();
        int dataInNum = freezerDataIn.size();
        for(int i=0; i<dataInNum; i++){
            Vector<String> freezerDataInLine = freezerDataIn.get(i);
            
            if(freezerDataInLine.get(3) != null &&
                    !freezerDataInLine.get(3).trim().equals("") &&
                    !freezerDataInLine.get(3).trim().equals("null") &&
                    freezerDataInLine.get(4) != null &&
                    !freezerDataInLine.get(4).trim().equals("") &&
                    !freezerDataInLine.get(4).trim().equals("null") &&
                    freezerDataInLine.get(5) != null &&
                    !freezerDataInLine.get(5).trim().equals("") &&
                    !freezerDataInLine.get(5).trim().equals("null") &&
                    freezerDataInLine.get(6) != null &&
                    !freezerDataInLine.get(6).trim().equals("") &&
                    !freezerDataInLine.get(6).trim().equals("null") &&
                    freezerDataInLine.get(7) != null &&
                    !freezerDataInLine.get(7).trim().equals("") &&
                    !freezerDataInLine.get(7).trim().equals("null") &&
                    freezerDataInLine.get(8) != null &&
                    !freezerDataInLine.get(8).trim().equals("") &&
                    !freezerDataInLine.get(8).trim().equals("null")
                            ){
                freezerDataProcess.add(freezerDataInLine);
            }
            //logger.debug("(unsorted): " + freezerDataInLine);                        
        }
        
        logger.debug("--- SORTING... ---");
        //System.out.println("--- SORTING... ---");
        logger.debug("size to process: " + freezerDataProcess.size());            
        //System.out.println("size to process: " + freezerDataProcess.size());
        
        //The freezerData needs to be ordered correctly first (merge sort?)
        SortBean.FreezerPosSort freezerSort = new SortBean.FreezerPosSort(freezerDataProcess);        
        Vector<Vector> freezerData = freezerSort.getFreezerPositions();
            
        String freezerStrOut = "";
        boolean gapFound = false;
        int elemCount = 0;
        String lastPosnElem = "";
        String lastBoxElem = "";
        while (elemCount < freezerData.size() && !gapFound) {

            //Binary search tree recursive is the most efficient way to do this...    
            Vector<String> freezerLineIn = freezerData.get(elemCount);
            String thisPosnElem = freezerLineIn.get(8);
            String thisBoxElem = freezerLineIn.get(7);
            
            if (!thisBoxElem.equals(lastBoxElem) && !lastBoxElem.equals("")) {
                int lastPosnElemInt = -1;
                try {
                    lastPosnElemInt = Integer.parseInt(lastPosnElem);
                } catch (NumberFormatException nfe) {
                    lastPosnElemInt = -1;
                }

                if (lastPosnElemInt < capacities[4] && lastPosnElemInt != -1) {
                    gapFound = true;
                    freezerStrOut = "";
                    for (int i = 0; i < 4; i++) {
                        freezerStrOut += freezerLineIn.get(i + 3) + "_";
                    }
                    freezerStrOut += lastBoxElem + "_" + (lastPosnElemInt + 1);
                }
            }else{
                int lastPosnElemInt = -1;
                int thisPosnElemInt = -1;
                try {
                    lastPosnElemInt = Integer.parseInt(lastPosnElem);
                    thisPosnElemInt = Integer.parseInt(thisPosnElem);
                } catch (NumberFormatException nfe) {
                    lastPosnElemInt = -1;
                    thisPosnElemInt = -1;
                }
                if((thisPosnElemInt-lastPosnElemInt) > 1){
                    gapFound = true;
                    freezerStrOut = "";
                    for (int i = 0; i < 4; i++) {
                        freezerStrOut += freezerLineIn.get(i + 3) + "_";
                    }
                    freezerStrOut += thisBoxElem + "_" + (lastPosnElemInt + 1);
                }
            }

            lastBoxElem = thisBoxElem;
            lastPosnElem = thisPosnElem;
            elemCount++;
        }
        
        logger.debug("freezerStrOut: " + freezerStrOut);
        
        return freezerStrOut;
    }

    public byte[] convertDataStream(int formDataLength, DataInputStream in) {

        byte dataBytes[] = new byte[formDataLength];
        try {
            int byteRead = 0;
            int totalBytesRead = 0;
            //this loop converting the uploaded file into byte code
            while (totalBytesRead < formDataLength) {
                byteRead = in.read(dataBytes, totalBytesRead, formDataLength);
                totalBytesRead += byteRead;
            }
        } catch (Exception e) {
            logger.debug("Error (convertDataStream): " + e.getMessage());
        }
        return dataBytes;
    }

    public String getDbnParameter(String file) {

        String dbn = "";
        int dbnIndex = file.indexOf("dbn\"");
        String dbnStr = file.substring(dbnIndex, dbnIndex + 19);
        int endIndex = dbnStr.indexOf("-");
        dbn = dbnStr.substring(4, endIndex);
        dbn = dbn.trim();
        return dbn;
    }

    public Vector<String> getHeaderIndexes() {
        Vector<String> headerIndexes = new Vector<String>();
        headerIndexes.add("ENSAT-ID");
        headerIndexes.add("date");
        headerIndexes.add("material");
        headerIndexes.add("aliquot");
        headerIndexes.add("freezer");
        headerIndexes.add("f-shelf");
        headerIndexes.add("rack");
        headerIndexes.add("r-shelf");
        headerIndexes.add("box");
        headerIndexes.add("pos");
        return headerIndexes;
    }
    
    public Vector<Vector> cleanFreezerRows(Vector<Vector> excelOut){
        
        Vector<Vector> excelOutDisp = new Vector<Vector>();
        int rowNum = excelOut.size();
        for(int i=0; i < rowNum; i++){    
            Vector<String> rowIn = excelOut.get(i);
            int rowSize = rowIn.size();
            boolean rowHasData = false;            
            Vector<String> rowDisp = new Vector<String>();
            for(int j=0; j < rowSize; j++){
                String valueIn = rowIn.get(j);
                if(valueIn == null){
                    valueIn = "";
                }
                if(!valueIn.equals("")){
                    //If the row does have data, clean up the blanks within that row
                    //I THINK THIS IS AN UNSAFE OPERATION - SHOULD REALLY TAG IT TO THE HEADER?
                    rowDisp.add(valueIn);
                    rowHasData = true;
                }                
            }
            if(rowHasData){
                excelOutDisp.add(rowDisp);
                //excelOutDisp.add(rowIn);                
            }
        }
        return excelOutDisp;
    }
    
    public Vector<String> getRowData(XSSFSheet sheet, int r, String centerid, Vector<String> headerIndexes, Vector<Integer> headerIndexInts){

        Format formatter = new SimpleDateFormat("yyyy-MM-dd");
        Vector<String> rowIn = new Vector<String>();
        XSSFRow row = sheet.getRow(r);
            /*if (row == null) {
                continue;
            }*/                    

        int cellNum = row.getPhysicalNumberOfCells();
            
        //Manually set the column number (as per specified spreadsheet)
        //cellNum = 12;                    
        cellNum = 30;
        //out.println("ROW " + row.getRowNum() + " has " + cellNum + " cell(s).<br/>");
                    
        for (int c = 0; c < cellNum; c++) {
                    
                boolean printThisCell = false;
                XSSFCell cell = row.getCell(c);
		String value = "";

                if(cell != null){
                    if(cell.getCellType() == XSSFCell.CELL_TYPE_FORMULA){
                        value = cell.getCellFormula();
                        if(headerIndexInts.contains(c)){
                            printThisCell = true;
                        }
                    }else if(cell.getCellType() == XSSFCell.CELL_TYPE_NUMERIC){
                        value = "" + cell.getNumericCellValue();                        
                        //Remove all the added decimal places
                        if(value.indexOf(".") != -1){
                            value = value.substring(0,value.indexOf("."));
                        }    
                        if(headerIndexInts.contains(c)){
                            printThisCell = true;
                        }
                    }else if(cell.getCellType() == XSSFCell.CELL_TYPE_STRING){
                        value = cell.getStringCellValue();
                        if(value == null){
                            value = "";
                        }
                        value = value.trim();
                        if(r == 0){
                            boolean tagFound = false;
                            int tagCount = 0;
                            //System.out.println("headerIndexes.size(): " + headerIndexes.size());
                            //System.out.println("value: " + value);
                            while(tagCount < headerIndexes.size() && !tagFound){
                                if(value.equalsIgnoreCase(headerIndexes.get(tagCount)) && !value.equals("")){
                                    //System.out.println("TAG FOUND: " + value);
                                    headerIndexInts.set(tagCount, new Integer(c));
                                    tagFound = true;                                    
                                    //headerCounts[k]++;
                                }else{
                                    tagCount++;
                                }
                            }                       
                            if(tagFound){
                                printThisCell = true;
                            }
                        }else{
                            if(headerIndexInts.contains(c)){
                                printThisCell = true;
                            }                            
                        }
                    }else{
                        value = "";
                    }
                    
                    
                    if(printThisCell){
                        value = value.trim();
                        //Add the center ID to the ENSAT ID number
                        if(headerIndexInts.get(0).equals(new Integer(c)) && (r != 0)){                                                        
                            value = centerid + "-" + value;
                        }else if(headerIndexInts.get(1).equals(new Integer(c)) && (r != 0)){
                            java.util.Date valueDate = new java.util.Date();
                            try{
                                double valueDouble = Double.parseDouble(value);
                                valueDate = DateUtil.getJavaDate(valueDouble);                                
                            }catch(Exception e){
                                logger.debug("Error: " + e.getMessage());                                
                            }
                            value = formatter.format(valueDate);                            
                        }
                        rowIn.add(value);
                    }                    
                }else{
                    rowIn.add("");
                }                
            }
        return rowIn;        
    }
}
