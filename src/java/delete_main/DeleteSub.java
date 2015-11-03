/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package delete_main;

import ConnectBean.ConnectionAuxiliary;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.Statement;
import java.sql.ResultSet;
import java.sql.ResultSetMetaData;
import java.sql.PreparedStatement;
import java.text.DateFormat;
import java.text.SimpleDateFormat;

import java.util.Vector;
import java.util.StringTokenizer;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.ServletContext;

import org.apache.log4j.Level;
import org.apache.log4j.Logger;
import org.apache.log4j.PropertyConfigurator;

/**
 *
 * @author astell
 */
public class DeleteSub extends Delete {
    
    private static final Logger logger = Logger.getLogger(DeleteSub.class);
    private String username = "";
    
    public DeleteSub() {
    }
    
    public void setUsername(String _username) {
        username = _username;
    }
    
    //public Vector<Vector> getParameters(String[] _tablenames, String pid, String centerid, String modid, String subTableIdName, Statement stmtValues, ServletContext context) {
    public Vector<Vector> getParameters(String[] _tablenames, String pid, String centerid, String modid, String subTableIdName, Connection conn, Connection paramConn) {

        String[] tablenames = _tablenames;
        String sql = "";
        //sql = "SELECT * FROM Parameter WHERE param_table='" + tablenames[0] + "'";
        //sql = "SELECT param_name,param_table,param_label FROM Parameter WHERE param_table='" + tablenames[0] + "'";
        sql = "SELECT param_name,param_table,param_label FROM Parameter WHERE param_table=?";

        if (tablenames.length > 1) {
            for (int i = 1; i < tablenames.length; i++) {
                //sql += " OR param_table='" + tablenames[i] + "'";
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
                    //sql += " OR param_table='" + tablenames[i] + "'";
                    //sql += " OR param_table=?";
                    ps.setString(i+1,tablenames[i]);
                }
            }
            
            ResultSet rs = ps.executeQuery();
            //ResultSet rs = stmt.executeQuery(sql);
            
            //Get the column number
            ResultSetMetaData rsmd = rs.getMetaData();
            int columnCount = rsmd.getColumnCount();
            
            while (rs.next()) {
                Vector<String> rowIn = new Vector<String>();
                for (int i = 0; i < columnCount; i++) {
                    rowIn.add(rs.getString(i + 1));
                }
                parameters.add(rowIn);                

            }
        } catch (Exception e) {
            logger.debug("Error (getParameters): " + e.getMessage());
        }

        //Now add the values to the parameter vector and return
        parameters = this.getParameterValues(tablenames, parameters, pid, centerid, modid, subTableIdName, conn);
        return parameters;
    }
    
    //public Vector<Vector> getParameterValues(String[] tablenames, Vector<Vector> parameters, String pid, String centerid, String modid, String subTableIdName, Statement stmtValues) {
    public Vector<Vector> getParameterValues(String[] tablenames, Vector<Vector> parameters, String pid, String centerid, String modid, String subTableIdName, Connection connValues) {

        int paramNum = parameters.size();
        int tableNum = tablenames.length;
        String sql = "";
        sql += "SELECT ";
        for (int i = 0; i < paramNum; i++) {
            Vector<String> parameterIn = parameters.get(i);            
            String paramName = parameterIn.get(0);
            //logger.debug("paramName: " + paramName);
            boolean parameterIsMultiple = this.getParameterIsMultiple(paramName);
            //logger.debug("parameterIsMultiple: " + parameterIsMultiple);
            
            if(!parameterIsMultiple){
                sql += "" + parameterIn.get(1) + "." + parameterIn.get(0) + ", ";
            }
        }
        sql = sql.substring(0, sql.length() - 2);
        sql += " FROM ";
        for (int i = 0; i < tableNum; i++) {
            sql += "" + tablenames[i] + ", ";
        }
        sql = sql.substring(0, sql.length() - 2);
        //sql += " WHERE ensat_id=" + pid + " AND center_id='" + centerid + "' AND " + subTableIdName + "=" + modid + ";";
        sql += " WHERE ensat_id=? AND center_id=? AND " + subTableIdName + "=?;";
        
        //logger.debug("sql (getParameterValues - DeleteSub): " + sql);
        
        Vector<Vector> parametersOut = new Vector<Vector>();
        try {
            
            PreparedStatement ps = connValues.prepareStatement(sql);
            ps.setString(1,pid);
            ps.setString(2,centerid);
            ps.setString(3,modid);            
            ResultSet rs = ps.executeQuery();
            
            //ResultSet rs = stmtValues.executeQuery(sql);

            int paramCount = 0;
            while (rs.next()) {
                for (int i = 0; i < paramNum; i++) {
                    Vector<String> parameterIn = parameters.get(i);
                    String paramName = parameterIn.get(0);
                    //System.out.println("paramName: " + paramName);
                    boolean parameterIsMultiple = this.getParameterIsMultiple(paramName);

                    String valueIn = "";
                    if (!parameterIsMultiple) {                    
                        valueIn = rs.getString(paramCount + 1);
                        if (valueIn == null) {
                            valueIn = "";
                        }
                        parameterIn.add(valueIn);
                        paramCount++;
                    }                    
                    parametersOut.add(parameterIn);
                }
            }
            
            //Now need to re-use the same statement to get the multiple values
            //Match the multiple tablename to the one of the form being called
            String multipleTablename = "";
            if (tablenames[0].equals("ACC_Chemotherapy")) {
                multipleTablename = "ACC_Chemotherapy_Regimen";
            }else if(tablenames[0].equals("ACC_Biomaterial")){
                multipleTablename = "ACC_Biomaterial_Normal_Tissue";
            }else if(tablenames[0].equals("APA_Biomaterial")){
                multipleTablename = "APA_Biomaterial_Normal_Tissue";
            }else if(tablenames[0].equals("Pheo_Biomaterial")){
                multipleTablename = "Pheo_Biomaterial_Normal_Tissue";
            }else if(tablenames[0].equals("NAPACA_Biomaterial")){
                multipleTablename = "NAPACA_Biomaterial_Normal_Tissue";
            }else if(tablenames[0].equals("ACC_FollowUp")){
                multipleTablename = "ACC_FollowUp_Organs";
            }else if(tablenames[0].equals("ACC_Radiofrequency")){
                multipleTablename = "ACC_Radiofrequency_Loc";
            }else if(tablenames[0].equals("ACC_Radiotherapy")){
                multipleTablename = "ACC_Radiotherapy_Loc";
            }else if(tablenames[0].equals("ACC_Surgery")){
                multipleTablename = "ACC_Surgery_Extended";
            }

            //Need to run the full loop again because the resultsets can't be nested
            for (int i = 0; i < paramNum; i++) {
                Vector<String> parameterIn = parameters.get(i);
                String paramName = parameterIn.get(0);
                boolean parameterIsMultiple = this.getParameterIsMultiple(paramName);

                if (parameterIsMultiple) {
                    String multipleSql = this.getMultipleSql(multipleTablename, pid, centerid, subTableIdName, modid);
                    //logger.debug("multipleSql: " + multipleSql);

                    PreparedStatement ps2 = connValues.prepareStatement(multipleSql);
                    ps2.setString(1,pid);
                    ps2.setString(2,centerid);
                    ps2.setString(3,modid);            
                    ResultSet rsMult = ps2.executeQuery();
                                        
                    //ResultSet rsMult = stmtValues.executeQuery(multipleSql);

                    String multValue = "";
                    while (rsMult.next()) {
                        multValue += "" + rsMult.getString(5) + "|";
                    }
                    //Remove the final "|"
                    if(!multValue.equals("")){
                        multValue = multValue.substring(0, multValue.length() - 1);
                    }
                    //System.out.println("multValue: " + multValue);
                    //System.out.println("paramName(" + i + "): " + paramName);
                    parameterIn.add(multValue);
                    parametersOut.set(i,parameterIn);
                }
            }
            
        } catch (Exception e) {
            logger.debug("Error (getParameterValues): " + e.getMessage());
        }
        
        /*for(int i=0; i<paramNum; i++){
            Vector<String> parameterIn = parameters.get(i);
            for(int j=0; j<parameterIn.size(); j++){
                System.out.println("parameter(" + i + ")(" + j + "):" + parameterIn.get(j));
            }
        }*/
        
        

        return parametersOut;
    }
    
    private String getMultipleSql(String tablename, String pid, String centerid, String subTableIdName, String modid) {
        String sql = "";
        //sql += "SELECT * FROM " + tablename + " WHERE ensat_id=" + pid + " AND center_id='" + centerid + "' AND " + subTableIdName + "=" + modid + ";";
        sql += "SELECT * FROM " + tablename + " WHERE ensat_id=? AND center_id=? AND " + subTableIdName + "=?;";
        return sql;
    }
    
    
    private boolean getParameterIsMultiple(String paramName) {

        return paramName.equals("normal_tissue_options")
                || paramName.equals("normal_tissue_paraffin_options")
                || paramName.equals("normal_tissue_dna_options")
                || paramName.equals("chemotherapy_regimen")
                || paramName.equals("followup_organs")
                || paramName.equals("radiofrequency_location")
                || paramName.equals("radiotherapy_location")
                || paramName.equals("surgery_extended")
                || paramName.equals("surgery_first")
                || paramName.equals("nmr_location")
                || paramName.equals("ct_location")
                || paramName.equals("preop_blockade_agents")
                || paramName.equals("intraop_bp_control_agents")                
                || paramName.equals("associated_studies");
    }

    public String getLastPageParamHtml(Vector<Vector> parameters) {

        Vector<String> lastPageParamNames = new Vector<String>();
        Vector<String> lastPageParamValues = new Vector<String>();

        int paramSize = parameters.size();

        for (int i = 0; i < paramSize; i++) {
            Vector<String> paramIn = parameters.get(i);
            lastPageParamNames.add(paramIn.get(1));
            lastPageParamValues.add(paramIn.get(9));
        }

        String outputStr = "";

        outputStr += "<table border=\"1px\" width=\"75%\">";
        outputStr += "<tr>";
        outputStr += "<td>";

        outputStr += this.getHiddenParams(lastPageParamNames, lastPageParamValues);

        outputStr += "</td>";
        outputStr += "</tr>";
        outputStr += "</table>";
        return outputStr;
    }

    public String getHiddenParams(Vector<String> lastPageParamNames, Vector<String> lastPageParamValues) {

        String outputStr = "";
        int paramNum = lastPageParamNames.size();
        for (int i = 0; i < paramNum; i++) {
            outputStr += "<input type=\"hidden\" name=\"" + lastPageParamNames.get(i) + "\" value=\"" + lastPageParamValues.get(i) + "\"/>";
        }
        return outputStr;
    }

    public String getLastPageParamConfirmHtml(Vector<Vector> parameters, String lineColour) {

        Vector<String> lastPageParamNames = new Vector<String>();
        Vector<String> lastPageParamValues = new Vector<String>();
        Vector<String> lastPageParamLabels = new Vector<String>();

        int paramSize = parameters.size();

        for (int i = 0; i < paramSize; i++) {
            Vector<String> paramIn = parameters.get(i);
            lastPageParamNames.add(paramIn.get(1));
            lastPageParamLabels.add(paramIn.get(4));
            lastPageParamValues.add(paramIn.get(9));
        }

        String outputStr = "";
        outputStr += "<table border=\"1px\" width=\"75%\" cellpadding=\"5\">";

        int paramNum = lastPageParamNames.size();
        for (int i = 0; i < paramNum; i++) {
            outputStr += "<tr ";
            if (i % 2 != 0) {
                outputStr += lineColour;
            }
            outputStr += ">";
            outputStr += "<td width=\"50%\">" + lastPageParamLabels.get(i) + ":</td><td><strong>" + lastPageParamValues.get(i) + "</strong></td>";
            outputStr += "<input type=\"hidden\" name=\"" + lastPageParamNames.get(i) + "\" value=\"" + lastPageParamValues.get(i) + "\"/>";
            outputStr += "</tr>";
        }
        outputStr += "</table>";
        return outputStr;
    }

    //public Vector<Vector> getMainParameters(String[] _tablenames, String pid, String centerid, Statement stmtValues, ServletContext context) {
    public Vector<Vector> getMainParameters(String[] _tablenames, String pid, String centerid, Connection conn, Connection paramConn) {

        String[] tablenames = _tablenames;
        String sql = "";
        //sql = "SELECT * FROM Parameter WHERE param_table='" + tablenames[0] + "'";
        sql = "SELECT * FROM Parameter WHERE param_table=?";

        if (tablenames.length > 1) {
            for (int i = 1; i < tablenames.length; i++) {
                //sql += " OR param_table='" + tablenames[i] + "'";
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
                    //sql += " OR param_table='" + tablenames[i] + "'";
                    //sql += " OR param_table=?";
                    ps.setString(i+1,tablenames[i]);
                }
            }
            
            ResultSet rs = ps.executeQuery();
            //ResultSet rs = stmt.executeQuery(sql);
            
            //Get the column number
            ResultSetMetaData rsmd = rs.getMetaData();
            int columnCount = rsmd.getColumnCount();
            
            while (rs.next()) {
                Vector<String> rowIn = new Vector<String>();
                for (int i = 0; i < columnCount; i++) {
                    rowIn.add(rs.getString(i + 1));
                }
                parameters.add(rowIn);                
            }
        } catch (Exception e) {
            logger.debug("Error (getMainParameters): " + e.getMessage());
        }

        //Now add the values to the parameter vector and return
        parameters = this.getMainParameterValues(tablenames, parameters, pid, centerid, conn);
        return parameters;
    }

    //public Vector<Vector> getMainParameterValues(String[] tablenames, Vector<Vector> parameters, String pid, String centerid, Statement stmtValues) {
    public Vector<Vector> getMainParameterValues(String[] tablenames, Vector<Vector> parameters, String pid, String centerid, Connection connValues) {

        int paramNum = parameters.size();
        int tableNum = tablenames.length;
        String sql = "";
        sql += "SELECT ";
        for (int i = 0; i < paramNum; i++) {
            Vector<String> parameterIn = parameters.get(i);
            boolean paramException = this.getParamException(parameterIn.get(1));
            if (!paramException) {
                sql += "" + parameterIn.get(8) + "." + parameterIn.get(1) + ", ";
            }
        }
        sql = sql.substring(0, sql.length() - 2);
        sql += " FROM ";
        for (int i = 0; i < tableNum; i++) {
            sql += "" + tablenames[i] + ", ";
        }
        sql = sql.substring(0, sql.length() - 2);
        sql += " WHERE ";
        
        if(tableNum > 1){        
            for(int i = 1; i < tableNum; i++){        
                sql += "Identification.ensat_id=" + tablenames[i] + ".ensat_id AND ";
                sql += "Identification.center_id=" + tablenames[i] + ".center_id AND ";
            }
        }
        sql = sql.substring(0,sql.length()-4);        
        //sql += " AND Identification.ensat_id=" + pid + " AND Identification.center_id='" + centerid + "';";
        sql += " AND Identification.ensat_id=? AND Identification.center_id=?;";

        Vector<Vector> parametersOut = new Vector<Vector>();
        try {
            PreparedStatement ps = connValues.prepareStatement(sql);
            ps.setString(1,pid);
            ps.setString(2,centerid);            
            ResultSet rs = ps.executeQuery();
            
            //ResultSet rs = stmtValues.executeQuery(sql);

            while (rs.next()) {
                int index = 0;
                for (int i = 0; i < paramNum; i++) {
                    
                    Vector<String> parameterIn = parameters.get(i);
                    boolean paramException = this.getParamException(parameterIn.get(1));

                    if(!paramException){
                        String valueIn = rs.getString(index + 1);
                        if (valueIn == null) {
                            valueIn = "";
                        }
                    
                        parameterIn.add(valueIn);
                        parametersOut.add(parameterIn);
                        index++;
                    }
                }
            }
        } catch (Exception e) {
            logger.debug("Error (getMainParameterValues): " + e.getMessage());
        }

        return parametersOut;        
    }

    public String getMainParameterHtml(Vector<Vector> parameters, String lineColour) {

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
            outputStr += rowIn.get(4) + ":";
            outputStr += "</td>";
            outputStr += "<td>";
            outputStr += "<strong>" + rowIn.get(10) + "</strong>";
            outputStr += "</td>";
            outputStr += "</tr>";
        }
        return outputStr;        
    }

    public String getPageTitle(String modality, String dbn) {

        String pageTitle = "";
        if (dbn.equals("ACC")) {

            if (modality.equals("biomaterial")) {
                pageTitle = "Biomaterial";
            } else if (modality.equals("followup")) {
                pageTitle = "Follow Up";
            } else if (modality.equals("radiofrequency")) {
                pageTitle = "Radiofrequency";
            } else if (modality.equals("surgery")) {
                pageTitle = "Surgery";
            } else if (modality.equals("mitotane")) {
                pageTitle = "Mitotane";
            } else if (modality.equals("chemotherapy")) {
                pageTitle = "Chemotherapy";
            } else if (modality.equals("radiotherapy")) {
                pageTitle = "Radiotherapy";
            } else if (modality.equals("chemoembolisation")) {
                pageTitle = "Chemoembolisation";
            } else if (modality.equals("pathology")) {
                pageTitle = "Pathology";
            } else if (modality.equals("metabolomics")) {
                pageTitle = "Steroid Metabolomics";
            } else {
                pageTitle = "None";
            }

        } else if (dbn.equals("Pheo")) {

            if (modality.equals("biomaterial")) {
                pageTitle = "Biomaterial";
            } else if (modality.equals("followup")) {
                pageTitle = "Follow Up";
            } else if (modality.equals("clinical")) {
                pageTitle = "Clinical Assessment";
            } else if (modality.equals("biochemical")) {
                pageTitle = "Biochemical Assessment";
            } else if (modality.equals("imaging")) {
                pageTitle = "Imaging Tests";
            } else if (modality.equals("surgery")) {
                pageTitle = "Surgical Interventions";
            } else if (modality.equals("tumordetails")) {
                pageTitle = "Tumor Details";
            } else if (modality.equals("interventions")) {
                pageTitle = "Non-Surgical Interventions";
            } else if (modality.equals("genetics")) {
                pageTitle = "Genetics";
            } else if (modality.equals("surgical_procedures")) {
                pageTitle = "Surgical Procedures";
            } else if (modality.equals("morphological_progression")) {
                pageTitle = "Morphological Progression";
            } else if (modality.equals("biological_assessment")) {
                pageTitle = "Biological Assessment";
            }

        } else if (dbn.equals("NAPACA")) {

            if (modality.equals("followup")) {
                pageTitle = "Follow-Up";
            } else if (modality.equals("biomaterial")) {
                pageTitle = "Biomaterial";
            } else if (modality.equals("imaging")) {
                pageTitle = "Imaging";
            } else if (modality.equals("surgery")) {
                pageTitle = "Surgery";
            } else if (modality.equals("pathology")) {
                pageTitle = "Pathology";
            }

        } else if (dbn.equals("APA")) {            

            if (modality.equals("followup")) {
                pageTitle = "Follow-Up";
            } else if (modality.equals("biomaterial")) {
                pageTitle = "Biomaterial";
            } else if (modality.equals("imaging")) {
                pageTitle = "Imaging";
            } else if (modality.equals("surgery")) {
                pageTitle = "Surgery";
            } else if (modality.equals("cardio")) {
                pageTitle = "Cardiovascular Events";
            } else if (modality.equals("clinical")) {
                pageTitle = "Clinical Assessment";
            } else if (modality.equals("biochemical")) {
                pageTitle = "Biochemical Assessment";
            }



        }
        return pageTitle;

    }
    
    //public void deleteForm(Statement stmt, String pid, String centerid, String dbn, String tablename, String modid, String tableIdname) {
    public void deleteForm(Connection conn, String pid, String centerid, String dbn, String tablename, String modid, String tableIdname) {
        
        try {
            //String sql = "DELETE FROM " + tablename + " WHERE ensat_id=" + pid + " AND center_id='" + centerid + "' AND " + tableIdname + "=" + modid + ";";
            String sql = "DELETE FROM " + tablename + " WHERE ensat_id=? AND center_id=? AND " + tableIdname + "=?;";
            PreparedStatement ps = conn.prepareStatement(sql);
            ps.setString(1,pid);
            ps.setString(2,centerid);
            ps.setString(3,modid);
            int update = ps.executeUpdate();
            
            logger.debug("'" + username + "' - FORM DELETED (Ensat ID: " + centerid + "-" + pid + ", " + tablename + " = " + modid + ")");
            //int update = stmt.executeUpdate(sql);
        } catch (Exception e) {
            logger.debug("Error (deleteForm): " + e.getMessage());
        }
    }
    
    public String getSubTablename(String modality, String dbn){
        
        String tablenameOut = "";
        if(dbn.equals("ACC")){
            if(modality.equals("biomaterial")){
                tablenameOut = "ACC_Biomaterial";
            }else if(modality.equals("chemoembolisation")){
                tablenameOut = "ACC_Chemoembolisation";
            }else if(modality.equals("chemotherapy")){
                tablenameOut = "ACC_Chemotherapy";
            }else if(modality.equals("followup")){
                tablenameOut = "ACC_FollowUp";
            }else if(modality.equals("mitotane")){
                tablenameOut = "ACC_Mitotane";
            }else if(modality.equals("pathology")){
                tablenameOut = "ACC_Pathology";
            }else if(modality.equals("radiofrequency")){
                tablenameOut = "ACC_Radiofrequency";
            }else if(modality.equals("radiotherapy")){
                tablenameOut = "ACC_Radiotherapy";
            }else if(modality.equals("surgery")){
                tablenameOut = "ACC_Surgery";
            }else if(modality.equals("metabolomics")){
                tablenameOut = "ACC_Metabolomics";
            }
        }else if(dbn.equals("Pheo")){
            if (modality.equals("biomaterial")) {
                tablenameOut = "Pheo_Biomaterial";
            } else if (modality.equals("biochemical")) {
                tablenameOut = "Pheo_BiochemicalAssessment";
            } else if (modality.equals("imaging")) {
                tablenameOut = "Pheo_ImagingTests";
            } else if (modality.equals("interventions")) {
                tablenameOut = "Pheo_NonSurgicalInterventions";
            } else if (modality.equals("clinical")) {
                tablenameOut = "Pheo_ClinicalAssessment";
            } else if (modality.equals("surgery")) {
                tablenameOut = "Pheo_Surgery";
            } else if (modality.equals("followup")) {
                tablenameOut = "Pheo_FollowUp";
            } else if (modality.equals("genetics")) {
                tablenameOut = "Pheo_Genetics";
            } else if (modality.equals("tumordetails")) {
                tablenameOut = "Pheo_TumorDetails";
            }else if (modality.equals("surgical_procedures")) {
                tablenameOut = "Pheo_Surgery_Procedures";
            } else if (modality.equals("morphological_progression")) {
                tablenameOut = "Pheo_Morphological_Progression";
            } else if (modality.equals("biological_assessment")) {
                tablenameOut = "Pheo_Biological_Assessment";
            }
            
        }else if(dbn.equals("NAPACA")){
            if (modality.equals("biomaterial")) {
                tablenameOut = "NAPACA_Biomaterial";
            } else if (modality.equals("imaging")) {
                tablenameOut = "NAPACA_Imaging";
            } else if (modality.equals("surgery")) {
                tablenameOut = "NAPACA_Surgery";
            } else if (modality.equals("followup")) {
                tablenameOut = "NAPACA_FollowUp";
            } else if (modality.equals("pathology")) {
                tablenameOut = "NAPACA_Pathology";
            } else if (modality.equals("metabolomics")) {
                tablenameOut = "NAPACA_Metabolomics";
            }
        }else if(dbn.equals("APA")){
            if (modality.equals("biomaterial")) {
                tablenameOut = "APA_Biomaterial";
            } else if (modality.equals("biochemical")) {
                tablenameOut = "APA_BiochemicalAssessment";
            } else if (modality.equals("imaging")) {
                tablenameOut = "APA_Imaging";
            } else if (modality.equals("followup")) {
                tablenameOut = "APA_FollowUp";
            } else if (modality.equals("cardio")) {
                tablenameOut = "APA_Cardio";
            } else if (modality.equals("complication")) {
                tablenameOut = "APA_Complication";
            } else if (modality.equals("clinical")) {
                tablenameOut = "APA_ClinicalAssessment";
            } else if (modality.equals("surgery")) {
                tablenameOut = "APA_Surgery";
            }
        }
        
        return tablenameOut;
    }
    
    public String getSubTableIdName(String modality, String dbn){
        
        String tableIdOut = "";
        if(dbn.equals("Pheo")){
            if(modality.equals("clinical")
                    || modality.equals("biochemical")){
                tableIdOut = "" + modality + "_assessment_id";
            }else if(modality.equals("imaging")){
                tableIdOut = dbn.toLowerCase() + "_" + modality + "_tests_id";
            }else if(modality.equals("interventions")){
                tableIdOut = dbn.toLowerCase() + "_non_surgical_" + modality + "_id";
            }else if(modality.equals("tumordetails")){
                tableIdOut = dbn.toLowerCase() + "_tumor_details_id";
            } else if (modality.equals("surgical_procedures")) {
                tableIdOut = dbn.toLowerCase() + "_surgery_procedure_id";                
            } else if (modality.equals("morphological_progression")) {
                tableIdOut = dbn.toLowerCase() + "_morphprog_id";                
            } else if (modality.equals("biological_assessment")) {
                tableIdOut = dbn.toLowerCase() + "_biologassess_id";                
            } else{
                tableIdOut = dbn.toLowerCase() + "_" + modality + "_id";            
            }
        }else if(dbn.equals("APA")){
            if(modality.equals("clinical")
                    || modality.equals("biochemical")){
                tableIdOut = "" + modality + "_assessment_id";
            }else{
                tableIdOut = dbn.toLowerCase() + "_" + modality + "_id";            
            }
        }else{        
            tableIdOut = dbn.toLowerCase() + "_" + modality + "_id";            
        }
        return tableIdOut;
    }
}
