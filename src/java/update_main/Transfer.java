/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package update_main;

import ConnectBean.ConnectionAuxiliary;
import delete_main.*;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.DriverManager;
import java.sql.Statement;
import java.sql.ResultSet;
import java.sql.ResultSetMetaData;

import java.util.Vector;
import java.util.HashMap;
import javax.servlet.ServletContext;

import javax.servlet.http.HttpServletRequest;

import org.apache.log4j.Level;
import org.apache.log4j.Logger;
import org.apache.log4j.PropertyConfigurator;


/**
 *
 * @author astell
 */
public class Transfer {
    
    private static final Logger logger = Logger.getLogger(Transfer.class);

    private Vector<Vector> parameterMappings = null;
    
    public Transfer() {
        parameterMappings = new Vector<Vector>();        
    }
    
    public void populateMapping(String studyName, String ensatSection){   
        
        /**
         * Use this as the overall mapping between studies
         * Populate the ENSAT values in the left column first
         */        
        if(studyName.equals("pmt")){
            
            //Identification
            this.addValueLine("Identification.ensat_id","Identification.study_id");
            this.addValueLine("Identification.center_id","Identification.center_id");            
            this.addValueLine("Identification.record_date","Identification.record_date");
            this.addValueLine("Identification.local_investigator","Identification.local_investigator");            
            this.addValueLine("Identification.investigator_email","Identification.investigator_email");
            this.addValueLine("Identification.sex","Identification.sex");
            this.addValueLine("Identification.year_of_birth","Identification.year_of_birth");
            this.addValueLine("Identification.consent_obtained","Identification.consent_obtained");
            this.addValueLine("Identification.uploader","Identification.uploader");
            
            //Pheo_PatientHistory
            //this.addValueLine("Pheo_PatientHistory.ensat_id","Demographics.demographics_id");
            //this.addValueLine("Pheo_PatientHistory.ensat_id","Demographics.study_id");
            //this.addValueLine("Pheo_PatientHistory.center_id","Demographics.center_id");            
            this.addValueLine("Identification.ensat_id","Demographics.demographics_id");
            this.addValueLine("Identification.ensat_id","Demographics.study_id");
            this.addValueLine("Identification.center_id","Demographics.center_id");            
            this.addValueLine("Pheo_PatientHistory.height_at_time_consent","Demographics.height");
            this.addValueLine("Pheo_PatientHistory.weight_at_time_consent","Demographics.weight");            
            this.addValueLine("Pheo_PatientHistory.systolic_bp_at_time_consent","Demographics.systolic_bp_1");
            this.addValueLine("Pheo_PatientHistory.diastolic_bp_at_time_consent","Demographics.diastolic_bp_1");
                        
            //this.addValueLine("Pheo_PatientHistory.ensat_id","Medical_History.medical_history_id");
            //this.addValueLine("Pheo_PatientHistory.ensat_id","Medical_History.study_id");
            //this.addValueLine("Pheo_PatientHistory.center_id","Medical_History.center_id");                        
            this.addValueLine("Identification.ensat_id","Medical_History.medical_history_id");
            this.addValueLine("Identification.ensat_id","Medical_History.study_id");
            this.addValueLine("Identification.center_id","Medical_History.center_id");                        
            this.addValueLine("Pheo_PatientHistory.history_of_hypertension","Medical_History.history_of_hypertension");
            this.addValueLine("Pheo_PatientHistory.year_of_hypertension_diagnosis","Medical_History.hypertension_year");
            this.addValueLine("Pheo_PatientHistory.pheo_operation_before_consent_date","Medical_History.history_of_ppgl");
            this.addValueLine("Pheo_PatientHistory.residual_disease","Medical_History.residual_disease");
            this.addValueLine("Pheo_PatientHistory.disease_metastatic","Medical_History.metastatic");
            
            //NAPACA_DiagnosticProcedures
            this.addValueLine("NAPACA_DiagnosticProcedures.ensat_id","Demographics.demographics_id");
            this.addValueLine("NAPACA_DiagnosticProcedures.ensat_id","Demographics.study_id");
            this.addValueLine("NAPACA_DiagnosticProcedures.center_id","Demographics.center_id");            
            this.addValueLine("NAPACA_DiagnosticProcedures.ensat_id","Medical_History.medical_history_id");
            this.addValueLine("NAPACA_DiagnosticProcedures.ensat_id","Medical_History.study_id");
            this.addValueLine("NAPACA_DiagnosticProcedures.center_id","Medical_History.center_id");                                                
            this.addValueLine("NAPACA_DiagnosticProcedures.weight","Demographics.weight");
            this.addValueLine("NAPACA_DiagnosticProcedures.height","Demographics.height");            
            this.addValueLine("NAPACA_DiagnosticProcedures.hypertension_presentation","Medical_History.history_of_hypertension");
            this.addValueLine("NAPACA_DiagnosticProcedures.hypertension_year","Medical_History.hypertension_year");
            this.addValueLine("NAPACA_DiagnosticProcedures.tumor_size","Demographics.height");
                        
            //Pheo_TumorDetails (case #1)
            /*this.addValueLine("Pheo_TumorDetails.pheo_tumor_details_id","Tumor_Details.tumor_details_id");            
            this.addValueLine("Pheo_TumorDetails.ensat_id","Tumor_Details.study_id");
            this.addValueLine("Pheo_TumorDetails.center_id","Tumor_Details.center_id");
            
            //this.addValueLine("Pheo_TumorDetails.tumor_date","Tumor_Details.tumor_year"); -- this is a partial transfer
            //this.addValueLine("Pheo_TumorDetails.tumor_date","Tumor_Details.tumor_month"); -- this is a partial transfer
            this.addValueLine("Pheo_TumorDetails.tumor_resected","Tumor_Details.tumor_resected");
            this.addValueLine("Pheo_TumorDetails.tumor_a_or_e","Tumor_Details.tumor_a_or_e");
            this.addValueLine("Pheo_TumorDetails.tumor_site","Tumor_Details.tumor_details");
            
            this.addValueLine("Pheo_TumorDetails.largest_size_x","Tumor_Details.tumor_dim_x");
            this.addValueLine("Pheo_TumorDetails.largest_size_y","Tumor_Details.tumor_dim_y");
            this.addValueLine("Pheo_TumorDetails.largest_size_z","Tumor_Details.tumor_dim_z");
            
            //Pheo_OtherOrgans (case #2)
            this.addValueLine("Pheo_OtherOrgans.pheo_other_organs_id","Other_Maligs.other_maligs_id");
            this.addValueLine("Pheo_OtherOrgans.ensat_id","Other_Maligs.study_id");
            this.addValueLine("Pheo_OtherOrgans.center_id","Other_Maligs.center_id");
            this.addValueLine("Pheo_OtherOrgans.other_organ","Other_Maligs.other_maligs");
            
            //NAPACA_DiagnosticProcedures --> Other_Maligs (case #3)
            //this.addValueLine("NAPACA_DiagnosticProcedures.pheo_other_organs_id","Other_Maligs.other_maligs_id"); -- this one will always be "1"
            this.addValueLine("NAPACA_DiagnosticProcedures.ensat_id","Other_Maligs.study_id");
            this.addValueLine("NAPACA_DiagnosticProcedures.center_id","Other_Maligs.center_id");                                                            
            this.addValueLine("NAPACA_DiagnosticProcedures.which_malignancies","Other_Maligs.other_maligs");*/
            
        }
    }
    
    private void addValueLine(String ensatParameter, String studyParameter){        
        Vector<String> valueLine = new Vector<String>();
        valueLine.add(ensatParameter); //ENSAT value
        valueLine.add(studyParameter); //Other study (PMT) value            
        parameterMappings.add(valueLine);
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

    public Vector<Vector> getParameters(String[] _tablenames, String pid, String centerid, Connection conn, Connection paramConn) {

        String[] tablenames = _tablenames;
        String sql = "";        
        sql = "SELECT param_name,param_table,param_label FROM Parameter WHERE param_table=?";

        if (tablenames.length > 1) {
            for (int i = 1; i < tablenames.length; i++) {                
                sql += " OR param_table=?";
            }
        }
        sql += ";";

        Vector<Vector> parameters = new Vector<Vector>();
        try {
            PreparedStatement ps = paramConn.prepareStatement(sql);
            ps.setString(1,tablenames[0]);            
            
            if (tablenames.length > 1) {
                for (int i = 1; i < tablenames.length; i++) {
                    ps.setString(i + 1, tablenames[i]);                    
                }
            }
            
            ResultSet rs = ps.executeQuery();
            while (rs.next()) {
                Vector<String> paramIn = new Vector<String>();
                paramIn.add(rs.getString(1));
                paramIn.add(rs.getString(2));
                paramIn.add(rs.getString(3));
                parameters.add(paramIn);
            }
        } catch (Exception e) {
            logger.debug("Error (getParameters): " + e.getMessage());
        }

        //Now add the values to the parameter vector and return
        parameters = this.getParameterValues(tablenames, parameters, pid, centerid, conn);
        return parameters;
    }

    public Vector<Vector> getParameterValues(String[] tablenames, Vector<Vector> parameters, String pid, String centerid, Connection connValues) {

        int paramNum = parameters.size();
        int tableNum = tablenames.length;
        String sql = "";
        sql += "SELECT ";
        for (int i = 0; i < paramNum; i++) {
            Vector<String> parameterIn = parameters.get(i);
                        
            if(!parameterIn.get(0).equals("associated_studies")){
                sql += "" + parameterIn.get(1) + "." + parameterIn.get(0) + ", ";
            }
        }
        sql = sql.substring(0, sql.length() - 2);
        sql += " FROM ";
        for (int i = 0; i < tableNum; i++) {
            sql += "" + tablenames[i] + ", ";
        }
        sql = sql.substring(0, sql.length() - 2);
        sql += " WHERE ensat_id=? AND center_id=?;";

        Vector<Vector> parametersOut = new Vector<Vector>();
        try {
            PreparedStatement ps = connValues.prepareStatement(sql);
            ps.setString(1,pid);
            ps.setString(2,centerid);
            ResultSet rs = ps.executeQuery();

            while (rs.next()) {
                for (int i = 0; i < paramNum; i++) {
                    Vector<String> parameterIn = parameters.get(i);
                    if(!parameterIn.get(0).equals("associated_studies")){                    
                        String valueIn = rs.getString(i + 1);
                        if (valueIn == null) {
                            valueIn = "";
                        }                        
                        parameterIn.add(valueIn);
                        parametersOut.add(parameterIn);
                    }
                }
            }
        } catch (Exception e) {
            logger.debug("Error (getParameterValues): " + e.getMessage());
        }

        return parametersOut;
    }

    public String getParameterHtml(Vector<Vector> parameters, String lineColour) {

        String outputStr = "";

        //Get the number of rows here        
        int paramNum = parameters.size();

        //For each row
        for (int i = 0; i < paramNum; i++) {

            Vector<String> rowIn = parameters.get(i);

            outputStr += "<tr ";
            if (i % 2 != 0) {
                outputStr += lineColour;
            }
            outputStr += ">";
            outputStr += "<td width=\"50%\">";
            outputStr += rowIn.get(2) + ":";
            outputStr += "</td>";
            outputStr += "<td>";
            outputStr += "<strong>" + rowIn.get(3) + "</strong>";
            outputStr += "</td>";
            outputStr += "</tr>";
        }
        return outputStr;
    }

    public String getTransferHtml(String studyName) {

        String outputStr = "";
        
        if(studyName.equals("pmt")){

        outputStr += "<table width=\"100%\">";
        outputStr += "<tr>";
        outputStr += "<td>";
        outputStr += "<table width=\"100%\">";
        outputStr += "<tr>";
        outputStr += "<td width=\"50%\">Main Inclusion Criteria:</td><td>";
        outputStr += "<select name=\"main_inclusion\" onblur=\"loadXMLDoc(this.value,this.name);dispOthers(this.value,'main_inclusion');\" onchange=\"dispOthers(this.value,'main_inclusion');\">";
        outputStr += "<option value=\"\">[Select...]</option>";
        outputStr += "<option value=\"Suspicion based primarily on signs and symptoms\">Suspicion based primarily on signs and symptoms</option>";
        outputStr += "<option value=\"Therapy resistant hypertension\">Therapy resistant hypertension</option>";
        outputStr += "<option value=\"Incidental finding on imaging for unrelated condition\">Incidental finding on imaging for unrelated condition</option>";
        outputStr += "<option value=\"Routine screening due to known mutation or hereditary syndrome\">Routine screening due to known mutation or hereditary syndrome</option>";
        outputStr += "<option value=\"Routine screening due to previous history of pheochromocytoma\">Routine screening due to previous history of pheochromocytoma</option>";
        outputStr += "<option value=\"Other\">Other</option>";
        outputStr += "</select>";
        outputStr += "<div id=\"myDiv_main_inclusion\"></div></td>";
        outputStr += "</tr>";
        outputStr += "<tr>";
        outputStr += "<td width=\"50%\">Date of informed consent:</td>";
        outputStr += "<td>";
        outputStr += "<table>";
        outputStr += "<tr>";
        outputStr += "<td>";
        
        outputStr += "<input name=\"date_informed_consent\" type=\"text\" class=\"datepicker\" id=\"date_informed_consent_id\" size=\"30\" value=\"\" onfocus=\"inform=true;\" onblur=\"inform=false;\" onchange=\"parameterValidate(this.value,this.name,false,'date');\" /><div id=\"date_informed_consent\"></div>";
        
        outputStr += "</td>";
        outputStr += "</tr>";
        outputStr += "</table>";
        outputStr += "</td>";
        outputStr += "</tr>";
        outputStr += "</table>";
        outputStr += "</td>";
        outputStr += "</tr>";
        outputStr += "</table>";
        
        }

        return outputStr;
    }

    public void updateIdentification(String study, String centerid, String pid, Statement stmt) {
        String sql = "UPDATE Identification SET ensat_database='" + study + "' WHERE center_id='" + centerid + "' AND ensat_id=" + pid + ";";
        try {
            int updateIdent = stmt.executeUpdate(sql);
        } catch (Exception e) {
            logger.debug("Error (updateIdentification): " + e.getMessage());
        }
    }

    public String[] getNapacaData(String centerid, String pid, Statement stmt) {

        String[] outputValues = null;
        String sql = "SELECT * FROM NAPACA_DiagnosticProcedures WHERE center_id='" + centerid + "' AND ensat_id=" + pid + ";";
        try {
            ResultSet rs = stmt.executeQuery(sql);

            ResultSetMetaData rsmd = rs.getMetaData();
            int columnCount = rsmd.getColumnCount();
            outputValues = new String[columnCount];
            while (rs.next()) {
                for (int i = 0; i < columnCount; i++) {
                    outputValues[i] = rs.getString(i + 1);
                }
            }
            rs.close();

        } catch (Exception e) {
            logger.debug("Error (getNapacaData): " + e.getMessage());
        }
        return outputValues;
    }
    
    public Vector<Vector> getNapacaImagingData(String centerid, String pid, Statement stmt) {

        Vector<Vector> outputValues = new Vector<Vector>();
        String sql = "SELECT * FROM NAPACA_Imaging WHERE center_id='" + centerid + "' AND ensat_id=" + pid + ";";
        try {
            ResultSet rs = stmt.executeQuery(sql);
                        
            ResultSetMetaData rsmd = rs.getMetaData();
            int columnCount = rsmd.getColumnCount();
            while (rs.next()) {                
                Vector<String> imagingInput = new Vector<String>();
                for (int i = 0; i < columnCount; i++) {
                    imagingInput.add(rs.getString(i+1));                    
                }
                outputValues.add(imagingInput);
            }
            rs.close();

        } catch (Exception e) {
            logger.debug("Error (getNapacaImagingData): " + e.getMessage());
        }
        return outputValues;
    }
    
    public Vector<Vector> getBiomaterialData(String centerid, String pid, Statement stmt) {

        Vector<Vector> biomaterialData = new Vector<Vector>();
        String sql = "SELECT * FROM NAPACA_Biomaterial WHERE center_id='" + centerid + "' AND ensat_id=" + pid + ";";
        try {
            ResultSet rs = stmt.executeQuery(sql);

            ResultSetMetaData rsmd = rs.getMetaData();
            int columnCount = rsmd.getColumnCount();
            
            while (rs.next()) {
                Vector<String> bioFormIn = new Vector<String>();
                for (int i = 0; i < columnCount; i++) {
                    bioFormIn.add(rs.getString(i + 1));
                }
                biomaterialData.add(bioFormIn);
            }
            rs.close();

        } catch (Exception e) {
            logger.debug("Error (getBiomaterialData): " + e.getMessage());
        }
        return biomaterialData;
    }
    
    public Vector<Vector> getBiomaterialAliquotData(String centerid, String pid, Statement stmt) {

        Vector<Vector> biomaterialData = new Vector<Vector>();
        String sql = "SELECT * FROM NAPACA_Biomaterial_Aliquots WHERE center_id='" + centerid + "' AND ensat_id=" + pid + ";";
        try {
            ResultSet rs = stmt.executeQuery(sql);

            ResultSetMetaData rsmd = rs.getMetaData();
            int columnCount = rsmd.getColumnCount();
            
            while (rs.next()) {
                Vector<String> bioFormIn = new Vector<String>();
                for (int i = 0; i < columnCount; i++) {
                    bioFormIn.add(rs.getString(i + 1));
                }
                biomaterialData.add(bioFormIn);
            }
            rs.close();

        } catch (Exception e) {
            logger.debug("Error (getBiomaterialAliquotData): " + e.getMessage());
        }
        return biomaterialData;
    }
    
    public Vector<Vector> getBiomaterialNormalTissueData(String centerid, String pid, Statement stmt) {

        Vector<Vector> biomaterialData = new Vector<Vector>();
        String sql = "SELECT * FROM NAPACA_Biomaterial_Normal_Tissue WHERE center_id='" + centerid + "' AND ensat_id=" + pid + ";";
        try {
            ResultSet rs = stmt.executeQuery(sql);

            ResultSetMetaData rsmd = rs.getMetaData();
            int columnCount = rsmd.getColumnCount();
            
            while (rs.next()) {
                Vector<String> bioFormIn = new Vector<String>();
                for (int i = 0; i < columnCount; i++) {
                    bioFormIn.add(rs.getString(i + 1));
                }
                biomaterialData.add(bioFormIn);
            }
            rs.close();

        } catch (Exception e) {
            logger.debug("Error (getBiomaterialNormalTissueData): " + e.getMessage());
        }
        return biomaterialData;
    }

    public void mapNewSectionData(String centerid, String pid, Statement stmt, String[] napacaData, Vector<Vector> napacaImagingData, Vector<Vector> biomaterialData, Vector<Vector> biomaterialAliquotData, Vector<Vector> biomaterialNormalTissueData, String study) {

        //Turn nulls into blanks
        int napacaDataLength = napacaData.length;
        for(int i=0; i<napacaDataLength; i++){
            if(napacaData[i] == null){
                napacaData[i] = "";
            }
        }
        
        String[] outputDpValues = null;
        String outputDpSql = "";
        String outputDpSql2 = "";
        String[] outputPheoTumorDetails = null;
        if (study.equals("ACC")) {

            outputDpValues = new String[14];

            outputDpValues[0] = napacaData[2];
            outputDpValues[1] = napacaData[3];
            outputDpValues[2] = napacaData[4];
            if(!napacaData[5].equals("") && !napacaData[6].equals("")){
                outputDpValues[3] = "'" + napacaData[5] + "-" + napacaData[6] + "-1'";
            }else{
                outputDpValues[3] = "null";
            }
            outputDpValues[4] = napacaData[7];
            outputDpValues[5] = napacaData[8];

            outputDpValues[6] = napacaData[9];
            outputDpValues[7] = napacaData[10];
            outputDpValues[8] = napacaData[11];
            outputDpValues[9] = napacaData[12];

            outputDpValues[10] = napacaData[13];
            outputDpValues[11] = napacaData[15];
            outputDpValues[12] = napacaData[45];
            outputDpValues[13] = napacaData[46];

            outputDpSql = "INSERT INTO ACC_DiagnosticProcedures VALUES(" + pid + ",'" + centerid + "'," + outputDpValues[3] + ",'','','" + outputDpValues[0] + "','" + outputDpValues[1] + "','" + outputDpValues[2] + "',";
            outputDpSql += "'','" + outputDpValues[4] + "','','" + outputDpValues[5] + "','" + outputDpValues[6] + "','" + outputDpValues[7] + "','" + outputDpValues[8] + "','" + outputDpValues[9] + "',";
            outputDpSql += "'" + outputDpValues[10] + "','','" + outputDpValues[11] + "','','','','','','','','" + outputDpValues[12] + "','" + outputDpValues[13] + "');";

            outputDpSql2 = "INSERT INTO ACC_TumorStaging VALUES(" + pid + ",'" + centerid + "','','','','','','','','','','','','','','');";

        } else if (study.equals("Pheo")) {

            outputDpValues = new String[3];

            outputDpValues[0] = napacaData[2];
            outputDpValues[1] = napacaData[3];
            outputDpValues[2] = napacaData[13];

            outputDpSql = "INSERT INTO Pheo_PatientHistory VALUES(" + pid + ",'" + centerid + "','" + outputDpValues[2] + "','','" + outputDpValues[0] + "','" + outputDpValues[1] + "',";
            outputDpSql += "'','','','','','','','','','','','');";
            
            //Now get the imaging information - put into pheo tumor details with the following mapping:
            /*
             * ensat_id
             * center_id
             * tumor_date
             * largest_size_x
             * tumor_site
             * diagnosis_method (="Imaging")
             */
            int imagingFormNum = napacaImagingData.size();
            outputPheoTumorDetails = new String[imagingFormNum];
            
            //Sort out the ID check here
            String lastId = this.getLastPheoTumorDetailsID(stmt);   
            int lastIdInt = Integer.parseInt(lastId);
        
            for(int i=0; i<imagingFormNum; i++){
                Vector<String> imagingFormIn = napacaImagingData.get(i);
                outputPheoTumorDetails[i] = "INSERT INTO Pheo_TumorDetails VALUES(";
                outputPheoTumorDetails[i] += "" + lastIdInt + ",";
                outputPheoTumorDetails[i] += "" + imagingFormIn.get(1) + ","; //ensat_id
                outputPheoTumorDetails[i] += "'" + imagingFormIn.get(2) + "',"; //center_id
                outputPheoTumorDetails[i] += "'" + imagingFormIn.get(3) + "',"; //tumor_date                
                String tumorSize = "";
                if(imagingFormIn != null){
                    if(!imagingFormIn.get(5).equals("")){
                        tumorSize = imagingFormIn.get(5);
                    }else{
                        tumorSize = imagingFormIn.get(6);
                    }                
                }
                outputPheoTumorDetails[i] += "'" + tumorSize + "',"; //largest_size_x
                outputPheoTumorDetails[i] += "'',";
                outputPheoTumorDetails[i] += "'',";
                outputPheoTumorDetails[i] += "'',";
                outputPheoTumorDetails[i] += "'',";
                outputPheoTumorDetails[i] += "'" + imagingFormIn.get(4) + "',"; //tumor_site
                outputPheoTumorDetails[i] += "'',";
                outputPheoTumorDetails[i] += "'',";
                outputPheoTumorDetails[i] += "'',";
                outputPheoTumorDetails[i] += "'Imaging'"; //diagnosis_method
                outputPheoTumorDetails[i] += ");";         
                lastIdInt++;
            }
            

        } else if (study.equals("APA")) {

            outputDpValues = new String[1];

            outputDpValues[0] = napacaData[13];

            outputDpSql = "INSERT INTO APA_PatientHistory VALUES(" + pid + ",'" + centerid + "','','" + outputDpValues[0] + "',";
            outputDpSql += "'','','','','','');";
        }
                
        //Execute the mapping
        try {
            //System.out.println("outputDpSql: " + outputDpSql);
            int update = stmt.executeUpdate(outputDpSql);
        } catch (Exception e) {
            logger.debug("Error (mapNewSectionData): " + e.getMessage());
        }

        if (study.equals("ACC")) {
            try {
                //System.out.println("outputDpSql2: " + outputDpSql2);
                int update = stmt.executeUpdate(outputDpSql2);
            } catch (Exception e) {
                logger.debug("Error: " + e.getMessage());
            }
        }else if(study.equals("Pheo")){            
            if(outputPheoTumorDetails.length != 0){
                for(int i=0; i<outputPheoTumorDetails.length; i++){
                    try {
                        //logger.debug("outputPheoTumorDetails: " + outputPheoTumorDetails[i]);
                        int update = stmt.executeUpdate(outputPheoTumorDetails[i]);
                    } catch (Exception e) {
                        logger.debug("Error: " + e.getMessage());
                    }
                }            
            }
        }
        
        //Do the biomaterial transfer here too
        int bioFormNum = biomaterialData.size();
        //logger.debug("bioFormNum: " + bioFormNum);
        
        //Sort out the ID check here
        String lastId = this.getLastBiomaterialID(study,stmt);
        
        //Set up an ID mapping here so that the aliquots and normal tissue can relate back to the correct biomaterial form
        //Format is: originalBiomaterialID | "lastID" (current)
        HashMap idMap = new HashMap();
        
        Vector<String> bioFormSql = new Vector<String>();
        for(int i=0; i<bioFormNum; i++){
            Vector<String> bioDataIn = biomaterialData.get(i);
            String biomaterialSql = "";
            biomaterialSql += "INSERT INTO " + study + "_Biomaterial VALUES(";
            
            for(int j=0; j<bioDataIn.size(); j++){            
                if(j == 0){
                    biomaterialSql += "'" + lastId + "',";
                    idMap.put(bioDataIn.get(0),lastId);
                }else{                
                    biomaterialSql += "'" + bioDataIn.get(j) + "',";
                }
            }
            //Remove trailing comma
            biomaterialSql = biomaterialSql.substring(0,biomaterialSql.length()-1);
            biomaterialSql += ");";
            bioFormSql.add(biomaterialSql);
            
            //Move the ID on one
            int lastIdInt = Integer.parseInt(lastId);
            lastIdInt++;
            lastId = "" + lastIdInt;            
        }
        
        //Execute the biomaterial updates
        for(int i=0; i<bioFormNum; i++){            
            String bioSql = bioFormSql.get(i);
            //logger.debug("" + bioSql);
            try {            
                int update = stmt.executeUpdate(bioSql);
            } catch (Exception e) {
                logger.debug("Error (mapNewSectionData - biomaterial): " + e.getMessage());
            }            
        }
        
        //Need methods for Biomaterial_Normal_Tissue and Aliquots too...
        int bioFormAliquotNum = biomaterialAliquotData.size();
        //logger.debug("bioFormAliquotNum: " + bioFormAliquotNum);
        
        //Sort out the ID check here
        String lastAliquotId = this.getLastBiomaterialAliquotID(study,stmt);        
        
        Vector<String> bioFormAliquotSql = new Vector<String>();
        for(int i=0; i<bioFormAliquotNum; i++){
            Vector<String> bioAliquotDataIn = biomaterialAliquotData.get(i);
            String biomaterialAliquotSql = "";
            biomaterialAliquotSql += "INSERT INTO " + study + "_Biomaterial_Aliquots VALUES(";
            
            for(int j=0; j<bioAliquotDataIn.size(); j++){            
                if(j == 0){
                    biomaterialAliquotSql += "'" + lastAliquotId + "',";
                }else if(j == 1){
                    String thisBioFormId = (String) idMap.get(bioAliquotDataIn.get(1));
                    biomaterialAliquotSql += "'" + thisBioFormId + "',";                
                }else{                
                    biomaterialAliquotSql += "'" + bioAliquotDataIn.get(j) + "',";
                }
            }
            //Remove trailing comma
            biomaterialAliquotSql = biomaterialAliquotSql.substring(0,biomaterialAliquotSql.length()-1);
            biomaterialAliquotSql += ");";
            bioFormAliquotSql.add(biomaterialAliquotSql);
            
            //Move the ID on one
            int lastAliquotIdInt = Integer.parseInt(lastAliquotId);
            lastAliquotIdInt++;
            lastAliquotId = "" + lastAliquotIdInt;            
        }
        
        //Execute the aliquot updates
        for(int i=0; i<bioFormAliquotNum; i++){            
            String bioAliquotSql = bioFormAliquotSql.get(i);
            //logger.debug("" + bioAliquotSql);
            try {            
                int update = stmt.executeUpdate(bioAliquotSql);
            } catch (Exception e) {
                logger.debug("Error (mapNewSectionData - aliquots): " + e.getMessage());
            }            
        }
        
        //Now for normal tissue
        int bioFormNormalTissueNum = biomaterialNormalTissueData.size();
        //logger.debug("bioFormNormalTissueNum: " + bioFormNormalTissueNum);
        
        //Sort out the ID check here
        String lastNormalTissueId = this.getLastBiomaterialNormalTissueID(study,stmt);        
        
        Vector<String> bioFormNormalTissueSql = new Vector<String>();
        for(int i=0; i<bioFormNormalTissueNum; i++){
            Vector<String> bioNormalTissueDataIn = biomaterialNormalTissueData.get(i);
            String biomaterialNormalTissueSql = "";
            biomaterialNormalTissueSql += "INSERT INTO " + study + "_Biomaterial_Normal_Tissue VALUES(";
            
            for(int j=0; j<bioNormalTissueDataIn.size(); j++){            
                if(j == 0){
                    biomaterialNormalTissueSql += "'" + lastNormalTissueId + "',";
                }else if(j == 1){
                    String thisBioFormId = (String) idMap.get(bioNormalTissueDataIn.get(1));
                    biomaterialNormalTissueSql += "'" + thisBioFormId + "',";                
                }else{                
                    biomaterialNormalTissueSql += "'" + bioNormalTissueDataIn.get(j) + "',";
                }
            }
            //Remove trailing comma
            biomaterialNormalTissueSql = biomaterialNormalTissueSql.substring(0,biomaterialNormalTissueSql.length()-1);
            biomaterialNormalTissueSql += ");";
            bioFormNormalTissueSql.add(biomaterialNormalTissueSql);
            
            //Move the ID on one
            int lastNormalTissueIdInt = Integer.parseInt(lastNormalTissueId);
            lastNormalTissueIdInt++;
            lastNormalTissueId = "" + lastNormalTissueIdInt;            
        }
        
        //Execute the normal tissue updates
        for(int i=0; i<bioFormNormalTissueNum; i++){            
            String bioNormalTissueSql = bioFormNormalTissueSql.get(i);
            //logger.debug("" + bioNormalTissueSql);
            try {            
                int update = stmt.executeUpdate(bioNormalTissueSql);
            } catch (Exception e) {
                logger.debug("Error (mapNewSectionData - normal tissue): " + e.getMessage());
            }            
        }
    }
    
    private String getLastBiomaterialID(String study, Statement statement){
        
        String sql = "SELECT " + study.toLowerCase() + "_biomaterial_id FROM " + study + "_Biomaterial ORDER BY " + study.toLowerCase() + "_biomaterial_id DESC;";        
        String lastId = "1";
        try{
            ResultSet rs = statement.executeQuery(sql);
            if(rs.next()){
                lastId = rs.getString(1);
            }
            int lastIdInt = Integer.parseInt(lastId);
            lastIdInt++;
            lastId = "" + lastIdInt;            
        }catch(Exception e){
            logger.debug("Error (getLastBiomaterialID): " + e.getMessage());
        }        
        return lastId;        
    }
    
    private String getLastBiomaterialAliquotID(String study, Statement statement){
        
        String sql = "SELECT " + study.toLowerCase() + "_biomaterial_aliquot_id FROM " + study + "_Biomaterial_Aliquots ORDER BY " + study.toLowerCase() + "_biomaterial_aliquot_id DESC;";        
        String lastId = "1";
        try{
            ResultSet rs = statement.executeQuery(sql);
            if(rs.next()){
                lastId = rs.getString(1);
            }
            int lastIdInt = Integer.parseInt(lastId);
            lastIdInt++;
            lastId = "" + lastIdInt;            
        }catch(Exception e){
            logger.debug("Error (getLastBiomaterialAliquotID): " + e.getMessage());
        }        
        return lastId;        
    }
    
    private String getLastBiomaterialNormalTissueID(String study, Statement statement){
        
        String sql = "SELECT " + study.toLowerCase() + "_biomaterial_normal_tissue_id FROM " + study + "_Biomaterial_Normal_Tissue ORDER BY " + study.toLowerCase() + "_biomaterial_normal_tissue_id DESC;";        
        String lastId = "1";
        try{
            ResultSet rs = statement.executeQuery(sql);
            if(rs.next()){
                lastId = rs.getString(1);
            }
            int lastIdInt = Integer.parseInt(lastId);
            lastIdInt++;
            lastId = "" + lastIdInt;            
        }catch(Exception e){
            logger.debug("Error (getLastBiomaterialNormalTissueID): " + e.getMessage());
        }        
        return lastId;        
    }
    
    private String getLastPheoTumorDetailsID(Statement statement){
        
        String sql = "SELECT pheo_tumor_details_id FROM Pheo_TumorDetails ORDER BY pheo_tumor_details_id DESC;";        
        String lastId = "1";
        try{
            ResultSet rs = statement.executeQuery(sql);
            if(rs.next()){
                lastId = rs.getString(1);
            }
            int lastIdInt = Integer.parseInt(lastId);
            lastIdInt++;
            lastId = "" + lastIdInt;            
        }catch(Exception e){
            logger.debug("Error (getLastPheoTumorDetailsID): " + e.getMessage());
        }        
        return lastId;        
    }

    public void addStudyInclusionParameters(Connection conn, String pid, String centerid, HttpServletRequest request) {

        Vector<String> studyInclusionParameters = new Vector<String>();
        
        studyInclusionParameters.add(pid);
        studyInclusionParameters.add(pid);
        studyInclusionParameters.add(centerid);
        
        //Set the main_criteria
        String inclusionCriteria = request.getParameter("main_inclusion");
        if(inclusionCriteria == null){
            inclusionCriteria = "";
        }
        if (inclusionCriteria.equals("Other")) {
            inclusionCriteria = request.getParameter("main_inclusion_other");
        }        
        studyInclusionParameters.add(inclusionCriteria);
        
        //Set the date_of_consent
        String consentDate = request.getParameter("date_informed_consent");
        if(consentDate == null){
            consentDate = "";
        }        
        studyInclusionParameters.add(consentDate);  
        
        int studyInclusionNum = studyInclusionParameters.size();                
        String[] inclusionParamNames = {"inclusion_id",
                                        "study_id",
                                        "center_id",
                                        "main_criteria",
                                        "date_of_consent"
                                        };
        
        //Now add the inclusion values to the mapping
        for(int i=0; i<studyInclusionNum; i++){
            String fqHeader = "Inclusion." + inclusionParamNames[i];
            String valueIn = studyInclusionParameters.get(i);            
            Vector<String> paramMapping = new Vector<String>();
            paramMapping.add("");
            paramMapping.add(fqHeader);
            
            //Change the date around for this one
            if(fqHeader.equals("Inclusion.date_of_consent")){
                valueIn = this.reformatDate(valueIn);
            }            
            paramMapping.add(valueIn);            
            parameterMappings.add(paramMapping);
        }
    }
    
    public void addTableValues(Connection conn, String pid, String centerid, HttpServletRequest request){
        
        String [] tableNames = this.getDistinctTableNames(0);
        int distinctTableNum = tableNames.length;        
        for(int i=0; i<distinctTableNum; i++){          
            boolean twod = tableNames[i].equals("Pheo_TumorDetails") || tableNames[i].equals("Pheo_OtherOrgans");            
            this.addTableInput(tableNames[i],conn,pid,centerid,twod);            
        }        
    }
    
    private String[] getDistinctTableNames(int index){
        
        Vector<String> tableParams = new Vector<String>();
        for(int i=0; i<parameterMappings.size(); i++){            
            String parameterName = (String) parameterMappings.get(i).get(index);
            int dotIndex = parameterName.indexOf(".");            
            if(dotIndex != -1){
                String tableNameIn = parameterName.substring(0,dotIndex);
                if(!tableParams.contains(tableNameIn)){
                    tableParams.add(tableNameIn);
                }            
            }
        }
        int distinctTableNum = tableParams.size();
        String[] distinctTableNames = new String[distinctTableNum];
        for(int i=0; i<distinctTableNum; i++){
            distinctTableNames[i] = tableParams.get(i);            
        }
        return distinctTableNames;
    }

    public void addTableInput(String tablename, Connection conn, String pid, String centerid, boolean twod) {
        
        //Retrieve the relevant parameter names
        Vector<String> tableParams = new Vector<String>();
        for(int i=0; i<parameterMappings.size(); i++){            
            String parameterName = (String) parameterMappings.get(i).get(0);
            int dotIndex = parameterName.indexOf(".");
            if(dotIndex != -1){
                String tableNameIn = parameterName.substring(0,dotIndex);                
                if(tableNameIn.equals(tablename) && !tableParams.contains(parameterName)){
                    tableParams.add(parameterName);
                }            
            }
        }
        int parameterNum = tableParams.size();
        
        //Retrieve information from selected table
        String sql = "SELECT ";
        for(int i=0; i<parameterNum; i++){
            sql += "" + tableParams.get(i) + ", ";
        }
        sql = sql.substring(0,sql.length()-2);        
        sql += " FROM " + tablename + " WHERE ensat_id=? AND center_id=?;";
        
        Vector<Vector> tableInput = new Vector<Vector>();        
        if(parameterNum > 0 && !tablename.equals("Inclusion")){            
            try {
                PreparedStatement ps = conn.prepareStatement(sql);
                ps.setString(1,pid);
                ps.setString(2,centerid);      
                ResultSet rs = ps.executeQuery();
            
                ResultSetMetaData rsmd = rs.getMetaData();
                int columnCount = rsmd.getColumnCount();
                
                if (!twod) {
                    if (rs.next()) {
                        for (int i = 0; i < columnCount; i++) {
                            String valueIn = rs.getString(i + 1);
                            if (valueIn == null) {
                                valueIn = "";
                            }
                            Vector<String> valuesIn = new Vector<String>();
                            valuesIn.add(valueIn);
                            tableInput.add(valuesIn);
                        }
                    }
                }else {
                    //Prepend the row number to the value in the case of 2-dimensional info requirements
                    int rowCount = 0;
                    Vector<Vector> valuesIn = new Vector<Vector>();
                    while (rs.next()) {                                                
                        for (int i = 0; i < columnCount; i++) {
                            String valueIn = rs.getString(i + 1);
                            if (valueIn == null) {
                                valueIn = "";
                            }
                            valuesIn.get(i).add(valueIn);
                        }                        
                        rowCount++;
                    }                    
                    for(int i=0; i<columnCount; i++){
                        tableInput.add(valuesIn.get(i));
                    }                    
                }
            } catch (Exception e) {
                logger.debug("Error (addTableInput): " + e.getMessage());
            }
        
            //Now add the tableInput values to the overall mapping
            if(tableInput.size() > 0){
                for(int i=0; i<parameterNum; i++){                
                    String paramIn = tableParams.get(i);      
                    
                    //NEED TO MODIFY THIS FOR THE 2-D STUFF SOMEHOW...
                    //ADD THEM TO THE END OF THE VALUES AS AN UNBOUNDED VALUE SET..?
                    //String valueIn = tableInput.get(i);            
                    Vector<String> valuesIn = tableInput.get(i);
                    for(int j=0; j<parameterMappings.size(); j++){                
                        Vector<String> paramMappingIn = parameterMappings.get(j);
                        if(paramMappingIn.get(0).equals(paramIn)){                            
                            for(int k=0; k<valuesIn.size(); k++){
                                paramMappingIn.add(valuesIn.get(k));
                            }
                            parameterMappings.set(j,paramMappingIn);
                        }
                    }
                }    
            }
        }
    }
    
    private Vector<String> getFullStudyTableParamList(String tablename, String study){
        
        Vector<String> paramList = new Vector<String>();
        
        //Connect to the study database
        Connection conn = null;
        if(study.equals("pmt")){
            conn = this.connectToPMT();        
        }
        
        String sql = "SELECT * FROM " + tablename + " LIMIT 1;";
        try{
            PreparedStatement ps = conn.prepareStatement(sql);
            ResultSet rs = ps.executeQuery();
            ResultSetMetaData rsmd = rs.getMetaData();
            int colCount = rsmd.getColumnCount();
            
            if(rs.next()){
                for(int i=0; i<colCount; i++){                
                    String headerIn = rsmd.getColumnLabel(i+1);
                    if(headerIn != null){
                        String fqHeader = "" + tablename + "." + headerIn;
                        paramList.add(fqHeader);
                    }
                }            
            }            
        }catch(Exception e){
            logger.debug("Error (getFullStudyTableParamList): " + e.getMessage());
        }
        return paramList;
    }
    
    private String compileInputSql(String tablename, String study) {
        
        String sql = "";        
        boolean twod = tablename.equals("Pheo_TumorDetails") || tablename.equals("Pheo_OtherOrgans");
        
        //logger.debug("Compiling SQL for " + tablename + "...");

        //Retrieve the relevant parameter names        
        Vector<Vector> params = new Vector<Vector>();
        Vector<String> studyParamNames = new Vector<String>();
            
        for(int i=0; i<parameterMappings.size(); i++){            
                
            Vector<String> parameterMappingIn = parameterMappings.get(i);
            int valueNumber = 1;
            if(twod){
                valueNumber = parameterMappingIn.size() - 2;
            }                
            String parameterName = (String) parameterMappingIn.get(1);
            String[] parameterValue = new String[valueNumber];
            for(int j=0; j<valueNumber; j++){
                parameterValue[j] = "";
            }                
            if(parameterMappingIn.size() > 2){
                for(int j=0; j<valueNumber; j++){
                    int valueIndex = j + 2;
                    parameterValue[j] = (String) parameterMappingIn.get(valueIndex);
                }
            }
            int dotIndex = parameterName.indexOf(".");
            String tableNameIn = "";
            if(dotIndex != -1){
                tableNameIn = parameterName.substring(0,dotIndex);
            }
            if(tableNameIn.equals(tablename)){                
                boolean nameFound = false;
                int nameCount = 0;
                while(nameCount < params.size() && !nameFound){                    
                    Vector<String> paramNamesIn = params.get(nameCount);
                    if(paramNamesIn.get(0).equals(parameterName)){
                        nameFound = true;
                    }else{
                        nameCount++;
                    }
                }                
                if(!nameFound){                
                    Vector<String> paramIn = new Vector<String>();
                    paramIn.add(parameterName);
                    for(int j=0; j<valueNumber; j++){
                        paramIn.add(parameterValue[j]);
                    }
                    params.add(paramIn);                
                    studyParamNames.add(parameterName);
                }
            }            
        }
        
        int valueNumber = 1;
        if(twod){
            valueNumber = params.get(0).size() - 2;
        }            
        for(int j=0; j<valueNumber; j++){                
            Vector<Vector> theseParams = new Vector<Vector>();
            int retrievalIndex = j + 1;                
            for(int k=0; k<params.size(); k++){                
                Vector<String> thisParamSetIn = params.get(k);
                Vector<String> thisParamSetOut = new Vector<String>();
                thisParamSetOut.add((String)thisParamSetIn.get(0)); //PMT parameter name
                thisParamSetOut.add((String)thisParamSetIn.get(retrievalIndex)); //Parameter value (at the appropriate index location)
                theseParams.add(thisParamSetOut);
            }
            sql += this.compileSql(tablename,theseParams,studyParamNames,study);
            //logger.debug("sql: " + sql);
        }
        return sql;
    }
    
    private String compileSql(String tablename, Vector<Vector> params, Vector<String> studyParamNames, String study){
        
        String sql = "";
        
        //logger.debug("Compiling SQL for " + tablename + "...");
        
        //FILL OUT THE NULL-->BLANKS STUFF
        //Get the full list of parameter names from the PMT/study database of this tablename
        Vector<String> fullTableParamList = this.getFullStudyTableParamList(tablename, study);        
        int fullTableParamNum = fullTableParamList.size();
        for(int i=0; i<fullTableParamNum; i++){
            String paramNameIn = fullTableParamList.get(i);
            if(!studyParamNames.contains(paramNameIn)){
                Vector<String> paramIn = new Vector<String>();
                paramIn.add(paramNameIn);
                paramIn.add("");
                params.add(paramIn);   
            }
        }
        int parameterNum = params.size();
        
        sql = "INSERT INTO " + tablename + " (";        
        for(int i=0; i<parameterNum; i++){
            sql += "" + (String) params.get(i).get(0) + ", ";
        }
        sql = sql.substring(0,sql.length()-2);
        sql += ") VALUES(";
        
        for(int i=0; i<parameterNum; i++){
            String paramNameIn = (String) params.get(i).get(0);
            /*if(tablename.equals("Demographics")){
                logger.debug("paramNameIn: " + paramNameIn);
            }*/
            String paramValueIn = "";
            if(paramNameIn.equals("Identification.phase")
                    || paramNameIn.equals("Demographics.month_of_birth")){
                paramValueIn = "1";
                //sql += "" + paramValueIn + ", ";
            }else if(paramNameIn.equals("Medical_History.tumor_number")
                    || paramNameIn.equals("Medical_History.metastatic_bones")
                    || paramNameIn.equals("Medical_History.metastatic_lymph")
                    || paramNameIn.equals("Medical_History.metastatic_lung")
                    || paramNameIn.equals("Medical_History.metastatic_liver")
                    ){
                //sql += "0, ";
                paramValueIn = "0";
                //sql += "" + paramValueIn + ", ";
            }else{            
                
                //Adding extra ID number for non-Identification tables (Demographics etc)
                /*if(!tablename.equals("Identification") && !tablename.equals("Inclusion")){
                    if(paramNameIn.endsWith("study_id")){    
                        if(tablename.equals("Inclusion")){
                            logger.debug("Additional point for inclusion table: " + (String) params.get(i).get(1));
                        }
                        sql += "'" + (String) params.get(i).get(1) + "', ";
                    }
                }*/        
                
                if(paramNameIn.endsWith("study_id") || paramNameIn.endsWith("center_id")){
                    paramValueIn = (String) params.get(i).get(1);
                }else{                
                    paramValueIn = (String) params.get(i).get(1);
                }
                //sql += "'" + paramValueIn + "', ";
            }
            /*if(tablename.equals("Demographics")){
                logger.debug("paramValueIn: " + paramValueIn);
            }*/
            sql += "'" + paramValueIn + "', ";
        }
        sql = sql.substring(0,sql.length()-2);
        sql += ");";
        
        //logger.debug("Finished compiling SQL: " + sql);
        
        return sql;
    }
    

    public void executeInputUpdate(String sql, String study) {        
        
        Connection conn = null;        
        //logger.debug("transfer sql: " + sql);                
        if(study.equals("pmt")){
            conn = this.connectToPMT();        
        }        
        try {
            Statement stmt = conn.createStatement();
            int update = stmt.executeUpdate(sql);
        } catch (Exception e) {
            logger.debug("Error (executeInputUpdate): " + e.getMessage());
        }
    }
    
    private Connection connectToPMT(){        
        String dbName = "pheo_ppgl";
        String driverName = "com.mysql.jdbc.Driver";
        String serverName = "103.6.253.195";
        String port = "3307";
        String username = "root";
        String password = "ps4Xy2a";
        Connection conn = this.connectToStudy(dbName, driverName, serverName, port, username, password);
        return conn;        
    }
    
    private Connection connectToStudy(String dbName, String driverName, String serverName, String port, String username, String password){
        
        String connectionURL = "jdbc:mysql://" + serverName + ":" + port + "/" + dbName;
        Connection conn = null;
        try {
            Class.forName(driverName).newInstance();
            conn = DriverManager.getConnection(connectionURL,username,password);            
        } catch (Exception e) {
            logger.debug("Error (connectToStudy): " + e.getMessage());
        }        
        return conn;
    }
    
    public boolean checkForPresenceInDatabase(String centerid, String pid, String study){
        
        Connection conn = null;
        if(study.equals("pmt")){
            conn = this.connectToPMT();        
        }
        boolean alreadyTransferred = false;        
        String sql = "";
        sql += "SELECT study_id,center_id FROM Identification WHERE center_id=? AND study_id=?;";
        try{
            PreparedStatement ps = conn.prepareStatement(sql);
            ps.setString(1,centerid);
            ps.setString(2,pid);                        
            ResultSet rs = ps.executeQuery();
            alreadyTransferred = rs.next();
        }catch(Exception e){
            logger.debug("Error (checkForPresenceInDatabase): " + e.getMessage());
        }                
        return alreadyTransferred;
    }

    public boolean updateStudyDatabase(String study, String centerid, String pid) {

        boolean alreadyTransferred = this.checkForPresenceInDatabase(centerid, pid, study);                        
        if (!alreadyTransferred) {        
            
            //Compile the PMT SQL statements from the mapping (index = 1)
            String[] tableNames = this.getDistinctTableNames(1);
            int tableNum = tableNames.length;                        
            String[] inputSql = new String[tableNum];
            for (int i = 0; i < tableNum; i++) {                
                inputSql[i] = this.compileInputSql(tableNames[i], study);
            }                    
            
            //Now transfer record            
            for (int i = 0; i < tableNum; i++) {                
                this.executeInputUpdate(inputSql[i], study);
            }
        }
        return alreadyTransferred;
        //return true; //THIS IS THE SAFE DEFAULT (ALREADY TRANSFERRED = TRUE => WON'T TRANSFER)
    }
    
    public String getStudyLabel(String studyName){
        String studyLabel = "";
        if(studyName.equals("pmt")){
            studyLabel = "PMT";
        }
        return studyLabel;
    }
    
    public void clearMapping(){
        parameterMappings = new Vector<Vector>();        
    }
    
    private String reformatDate(String dateIn){
        
        String dateOut = "";
        
        if(dateIn == null){
            dateIn = "";
        }
        
        int firstHyphen = dateIn.indexOf("-");
        int secondHyphen = dateIn.lastIndexOf("-");
        if(firstHyphen != -1 && secondHyphen != -1){            
            String dayStr = dateIn.substring(0,firstHyphen);
            String monthStr = dateIn.substring(firstHyphen+1,secondHyphen);
            String yearStr = dateIn.substring(secondHyphen+1,dateIn.length());            
            dateOut = "" + yearStr + "-" + monthStr + "-" + dayStr;
        }else{
            dateOut = dateIn;
        }        
        return dateOut;
    }
    
    
    
}
