/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package MailBean;

/**
 *
 * @author astell
 */
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.ResultSetMetaData;
import java.util.Vector;
import java.sql.Connection;

import java.text.DateFormat;
import java.text.SimpleDateFormat;

import org.apache.log4j.Level;
import org.apache.log4j.Logger;
import org.apache.log4j.PropertyConfigurator;

public class Notifications {

    private static final Logger logger = Logger.getLogger(Notifications.class);
    private String username = "";

    public Notifications() {
        //Set up logger        
        logger.setLevel(Level.DEBUG);
        PropertyConfigurator.configure("/home/astell/logs/log4j_ensat.properties");
    }

    public void setUsername(String _username) {
        username = _username;
    }

    public Vector<Vector> getPatientIDs(String sql, Connection conn) {

        //Pick out the relevant patient IDs
        //(This will change when the study selection becomes generic)
        String patientSelectSql = "SELECT * FROM Identification WHERE eurine_act_inclusion='Yes';";

        //Generic string will look something like the below
        /*
         * String patientSelectSql = "SELECT * FROM Identification,
         * Associated_Studies WHERE
         * Identification.ensat_id=Associated_Studies.ensat_id ";
         * patientSelectSql += " AND
         * Identification.center_id=Associated_Studies.center_id AND
         * study_names='" + studyNameIn + "';";
         */

        Vector<Vector> patientInfo = new Vector<Vector>();
        try {
            PreparedStatement ps = conn.prepareStatement(patientSelectSql);
            ResultSet patientSelectRs = ps.executeQuery();

            while (patientSelectRs.next()) {
                Vector<String> patient = new Vector<String>();
                for (int i = 0; i < 12; i++) {
                    patient.add(patientSelectRs.getString(i + 1));
                }
                patientInfo.add(patient);
            }
        } catch (Exception e) {
            logger.debug("('" + username + "') Error (getPatientIDs): " + e.getMessage());
            System.out.println("('" + username + "') Error (getPatientIDs): " + e.getMessage());
        }
        
        //System.out.println("patientInfo: " + patientInfo.size());
        
        return patientInfo;


    }

    public Vector<Vector> removeBelowTimeInterval(Vector<Vector> patients, int timeIntervalWeeks) {

        //Get current date
        java.util.Date currentDate = new java.util.Date();

        //Express time interval in milliseconds
        int timeIntervalMillis = timeIntervalWeeks * 7 * 24 * 60 * 60 * 1000;

        Vector<Vector> patientsOut = new Vector<Vector>();
        int patientNum = patients.size();

        for (int i = 0; i < patientNum; i++) {
            Vector<String> patientIn = patients.get(i);
            String recordDateStr = patientIn.get(4); //CHECK THIS IS THE CORRECT ELEMENT
            java.util.Date recordDate = new java.util.Date(recordDateStr);
            if ((currentDate.getTime() - recordDate.getTime()) >= timeIntervalMillis) {
                patientsOut.add(patientIn);
            }
        }
        return patientsOut;
    }

    public Vector<Vector> checkStudyConditions(String studyName, Vector<Vector> patients, Connection conn) {

        java.util.HashMap timeChecks = this.compileTimeChecks(studyName);

        Vector<String> dbns = this.compileDatabases(studyName);

        //For each patient ID
        //Pull out all the biomaterial forms that have been recorded (for EURINE-ACT this will only be ACC and NAPACA)
        Vector<Vector> bioForms = this.getBioForms(dbns, conn);

        //Check the conditions and remove the patients that meet them
        patients = this.checkSpecificConditions(timeChecks, bioForms, patients);

        return patients;
    }

    private Vector<Vector> checkSpecificConditions(java.util.HashMap timeChecks, Vector<Vector> bioForms, Vector<Vector> patients) {

        DateFormat dateFormatter = new SimpleDateFormat("yyyy-MM-dd");
        
        //Get current date
        java.util.Date currentDate = new java.util.Date();
        System.out.println("currentDate: " + currentDate.toString());

        //Count the number of bio-forms held for each patient in this list and populate array with each count
        Vector<Vector> patientsOut = new Vector<Vector>();
        int patientNum = patients.size();
        int[] patientCounts = new int[patientNum];
        int bioFormSize = bioForms.size();
        //int dbNum = bioForms.length;
        //System.out.println("patientNum: " + patientNum);
        //System.out.println("dbNum: " + dbNum);
        
        for (int i = 0; i < patientNum; i++) {
        //for (int i = 0; i < 1; i++) {
            Vector<String> patientIn = patients.get(i);
            String patientId = patientIn.get(1) + "-" + patientIn.get(0);
            //System.out.println("patientId: " + patientId);
            int patientBioCount = 0;            
            for (int k = 0; k < bioFormSize; k++) {
            //for (int k = 0; k < 1; k++) {
                Vector<String> bioFormIn = bioForms.get(k);
                //System.out.println("bioFormIn: " + bioFormIn.toString());
                String bioFormId = bioFormIn.get(1) + "-" + bioFormIn.get(2);                    
                //System.out.println("bioFormId: " + bioFormId);
                if (bioFormId.equals(patientId)) {
                    patientBioCount++;
                }
            }            
            patientCounts[i] = patientBioCount;
            //System.out.println("patientId: " + patientId + ", count: " + patientBioCount);
        }

        //Now figure out how many weeks it's been since the registration for each patient
        int[] patientWeeks = new int[patientNum];
        for (int i = 0; i < patientNum; i++) {
            Vector<String> patientIn = patients.get(i);
            String patientId = patientIn.get(1) + "-" + patientIn.get(0);
            //Get the record date
            String recordDateStr = patientIn.get(5);
            java.util.Date recordDate = null;
            try{
                recordDate = dateFormatter.parse(recordDateStr);
            }catch(Exception e){
                System.out.println("Error: " + e.getMessage());
            }
            long intervalMillis = currentDate.getTime() - recordDate.getTime();
            int intervalWeeks = (int) (((((intervalMillis / 1000) / 60) / 60) / 24) / 7);
            System.out.println("patientId: " + patientId + "");
            System.out.println("intervalWeeks: " + intervalWeeks);
            System.out.println("patientCount: " + patientCounts[i]);
            patientWeeks[i] = intervalWeeks;
        }

        boolean[] patientsValid = new boolean[patientNum];
        for (int i = 0; i < patientNum; i++) {
            patientsValid[i] = false;
        }

        for (int i = 0; i < patientNum; i++) {

            if (patientCounts[i] == 1 && (patientWeeks[i] < 12)) {
                patientsValid[i] = true;
            } else if (patientCounts[i] == 2 && (patientWeeks[i] > 13 && patientWeeks[i] < 24)) {
                patientsValid[i] = true;
            } else if (patientCounts[i] == 3 && (patientWeeks[i] > 25 && patientWeeks[i] < 36)) {
                patientsValid[i] = true;
            } else if (patientCounts[i] == 4 && (patientWeeks[i] > 37 && patientWeeks[i] < 48)) {
                patientsValid[i] = true;
            }

        }

        for (int i = 0; i < patientNum; i++) {
            Vector<String> patientIn = patients.get(i);
            String patientId = patientIn.get(1) + "-" + patientIn.get(0);
            //System.out.println("patientId: " + patientId + ", valid: " + patientsValid[i]);
            
            if (!patientsValid[i]) {
                patientsOut.add(patients.get(i));
            }
        }
        return patientsOut;
    }

    private Vector<Vector> getBioForms(Vector<String> dbns, Connection conn) {

        int dbNum = dbns.size();
        Vector<Vector> bioForms = new Vector<Vector>();
        for (int i = 0; i < dbNum; i++) {
            String bioFormCheckSql = "SELECT DISTINCT biomaterial_date,center_id,ensat_id FROM " + dbns.get(i) + "_Biomaterial ORDER BY center_id,ensat_id;";

            //Run the check
            try {
                PreparedStatement ps = conn.prepareStatement(bioFormCheckSql);
                ResultSet bioFormRs = ps.executeQuery();

                ResultSetMetaData rsmd = bioFormRs.getMetaData();
                int columnNum = rsmd.getColumnCount();

                while (bioFormRs.next()) {
                    Vector<String> bioForm = new Vector<String>();
                    for (int j = 0; j < columnNum; j++) {
                        String paramIn = bioFormRs.getString(j+1);
                        //System.out.println("paramIn: " + paramIn);
                        bioForm.add(paramIn);
                    }
                    bioForm.add(dbns.get(i));
                    bioForms.add(bioForm);
                }
            } catch (Exception e) {
                logger.debug("('" + username + "') Error (checkStudyConditions): " + e.getMessage());
            }
        }
        return bioForms;
    }

    private java.util.HashMap compileTimeChecks(String study) {

        java.util.HashMap timeChecks = new java.util.HashMap();
        if (study.equals("eurineact")) {
            //Compile the number vs time-period checks that must be performed [number:weeks]
            timeChecks.put(1, new Integer(12)); //0 - 12 (1)
            timeChecks.put(2, new Integer(24)); //13 - 24 (2)
            timeChecks.put(3, new Integer(36)); //25 - 36 (3)
            timeChecks.put(4, new Integer(48)); //37 - 48 (4)
        }
        return timeChecks;
    }

    private Vector<String> compileDatabases(String study) {
        Vector<String> dbns = new Vector<String>();
        if (study.equals("eurineact")) {
            dbns.add("ACC");
            dbns.add("NAPACA");
        }
        return dbns;
    }

    public Vector<Vector> checkNotifyFlags(Vector<Vector> patients, Connection conn) {

        //Get current date
        java.util.Date currentDate = new java.util.Date();
        
        //Check the notify_flag for this patient (1,2 or 3)
        int patientNum = patients.size();
        boolean[] requiresAction = new boolean[patientNum];
        for(int i=0; i<patientNum; i++){
            Vector<String> patientIn = patients.get(i);
            String patientNotifyFlag = patientIn.get(10); //CHECK THIS INDEX
            //Get the record date
            String recordDateStr = patientIn.get(4); //CHECK THIS
            java.util.Date recordDate = new java.util.Date(recordDateStr);
            long intervalMillis = currentDate.getTime() - recordDate.getTime();
            int intervalWeeks = (int) (((((intervalMillis / 1000) / 60) / 60) / 24) / 7);            
            int intervalWeekSinceTimecheck = intervalWeeks % 12;            
            
            if(patientNotifyFlag.equals("0")){
                //If intervalWeeksSinceTimecheck >= 8 - EMAIL PI
                //Move notify_flag to 1
                
            }else if(patientNotifyFlag.equals("1")){
                //If intervalWeeksSinceTimecheck >= 11 - EMAIL PI
                //Move notify_flag to 2
                
            }else if(patientNotifyFlag.equals("2")){
                //If intervalWeeksSinceTimecheck >= 0 && intervalWeeks != 0 - EMAIL WIEBKE/IRINA
                //Reset notify_flag to 0
                
            }
            
            
        }
        
        //Check what the notify_flag of this patient SHOULD be
        //(e.g. if we are at 11 weeks, that is notify_flag=2, but the notify_flag may still be at 1 - requiring action)        
        /**
         * 0 = No emails sent yet
         * 1 = Emailed once
         * 2 = Emailed twice
         */

        


        //Add the ENSAT ID and investigator name/email to the output details


        return null;
    }

    public String sendInvestigatorEmails(Vector<Vector> patients) {
        return "";
    }
}
