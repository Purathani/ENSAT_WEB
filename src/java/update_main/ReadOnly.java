/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package update_main;

import ConnectBean.ConnectionAuxiliary;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.Statement;
import java.sql.ResultSet;
import java.sql.PreparedStatement;
import java.text.DateFormat;
import java.text.SimpleDateFormat;

import java.util.Vector;
import java.util.StringTokenizer;
import javax.servlet.ServletContext;

import org.apache.log4j.Level;
import org.apache.log4j.Logger;
import org.apache.log4j.PropertyConfigurator;


/**
 *
 * @author astell
 */
public class ReadOnly {
    
    private static final Logger logger = Logger.getLogger(ReadOnly.class);

    public ReadOnly() {
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
                    ps.setString(i+1,tablenames[i]);
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
    
    private boolean getParamException (String paramIn){
        return paramIn.equals("system_organ")
                || paramIn.equals("presentation_first_tumor")
                || paramIn.equals("imaging")
                || paramIn.equals("associated_studies")
                || paramIn.equals("hormone_symptoms")
                || paramIn.equals("tumor_symptoms")
                || paramIn.equals("first_diagnosis_tnm")
                || paramIn.equals("malignant_diagnosis_tnm")
                        ;
    }

    public Vector<Vector> getParameterValues(String[] tablenames, Vector<Vector> parameters, String pid, String centerid, Connection connValues) {

        int paramNum = parameters.size();
        //System.out.println("paramNum (getParameterValues): " + paramNum);
        int tableNum = tablenames.length;
        String sql = "";

        //Parameters
        sql += "SELECT ";
        for (int i = 0; i < paramNum; i++) {
            Vector<String> parameterIn = parameters.get(i);
            boolean paramException = this.getParamException(parameterIn.get(0));
            if(!paramException){
                sql += "" + parameterIn.get(1) + "." + parameterIn.get(0) + ", ";
            }
        }
        sql = sql.substring(0, sql.length() - 2);

        //Tables
        sql += " FROM ";
        for (int i = 0; i < tableNum; i++) {
            sql += "" + tablenames[i] + ", ";
        }
        sql = sql.substring(0, sql.length() - 2);

        //Joining (Identification is always the first table)
        sql += " WHERE " + tablenames[0] + ".ensat_id=" + tablenames[1] + ".ensat_id ";
        if(tableNum > 2){
            sql += " AND " + tablenames[0] + ".ensat_id=" + tablenames[2] + ".ensat_id ";
        }
        sql += " AND " + tablenames[0] + ".center_id=" + tablenames[1] + ".center_id ";
        if(tableNum > 2){
            sql += " AND " + tablenames[0] + ".center_id=" + tablenames[2] + ".center_id ";
        }

        //Specific conditions
        //sql += " AND Identification.ensat_id=" + pid + " AND Identification.center_id='" + centerid + "';";
        sql += " AND Identification.ensat_id=? AND Identification.center_id=?;";

        //System.out.println("sql (getParameterValues): " + sql);

        Vector<Vector> parametersOut = new Vector<Vector>();
        try {
            PreparedStatement ps = connValues.prepareStatement(sql);
            ps.setString(1,pid);
            ps.setString(2,centerid);
            
            ResultSet rs = ps.executeQuery();

            if (rs.next()) {
                int paramCount = 0;
                for (int i = 0; i < paramNum; i++) {
                    
                    Vector<String> parameterIn = parameters.get(i);
                    boolean paramException = this.getParamException(parameterIn.get(0));
                    
                    if(!paramException){
                        String valueIn = "";
                        valueIn = rs.getString(paramCount + 1);  
                        if (valueIn == null) {
                            valueIn = "";
                        }
                        parameterIn.add(valueIn);
                        parametersOut.add(parameterIn);  
                        paramCount++;
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
        //System.out.println("paramNum (getParameterHtml): " + paramNum);

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
        //System.out.println("outputStr (getParameterHtml): " + outputStr);
        return outputStr;
    }

public Vector<Vector> compileSubTableList(String dbn) {

        Vector<Vector> subTables = new Vector<Vector>();

        //Check for sub-forms and delete any that are encountered
        if (dbn.equals("ACC")) {            
            Vector<String> tableInfo = new Vector<String>();
            tableInfo.add("ACC_Surgery"); //Db tablename
            tableInfo.add("Surgery"); //Printed name
            tableInfo.add("surgery"); //HTML link name
            subTables.add(tableInfo);
            tableInfo = new Vector<String>();
            tableInfo.add("ACC_Pathology"); //Db tablename
            tableInfo.add("Pathology"); //Printed name
            tableInfo.add("pathology"); //HTML link name
            subTables.add(tableInfo);
            tableInfo = new Vector<String>();
            tableInfo.add("ACC_Mitotane"); //Db tablename
            tableInfo.add("Mitotane"); //Printed name
            tableInfo.add("mitotane"); //HTML link name
            subTables.add(tableInfo);
            tableInfo = new Vector<String>();
            tableInfo.add("ACC_Chemotherapy"); //Db tablename
            tableInfo.add("Chemotherapy"); //Printed name
            tableInfo.add("chemotherapy"); //HTML link name
            subTables.add(tableInfo);
            tableInfo = new Vector<String>();
            tableInfo.add("ACC_Radiotherapy"); //Db tablename
            tableInfo.add("Radiotherapy"); //Printed name
            tableInfo.add("radiotherapy"); //HTML link name
            subTables.add(tableInfo);
            tableInfo = new Vector<String>();
            tableInfo.add("ACC_Chemoembolisation"); //Db tablename
            tableInfo.add("Chemoembolisation"); //Printed name
            tableInfo.add("chemoembolisation"); //HTML link name
            subTables.add(tableInfo);
            tableInfo = new Vector<String>();
            tableInfo.add("ACC_Radiofrequency"); //Db tablename
            tableInfo.add("Radiofrequency"); //Printed name
            tableInfo.add("radiofrequency"); //HTML link name
            subTables.add(tableInfo);
            tableInfo = new Vector<String>();
            tableInfo.add("ACC_Biomaterial"); //Db tablename
            tableInfo.add("Biomaterial"); //Printed name
            tableInfo.add("biomaterial"); //HTML link name
            subTables.add(tableInfo);
            tableInfo = new Vector<String>();
            tableInfo.add("ACC_FollowUp"); //Db tablename
            tableInfo.add("Follow-up"); //Printed name
            tableInfo.add("followup"); //HTML link name
            subTables.add(tableInfo);
                
        } else if (dbn.equals("Pheo")) {
            Vector<String> tableInfo = new Vector<String>();
            tableInfo.add("Pheo_ClinicalAssessment"); //Db tablename
            tableInfo.add("Clinical Assessment"); //Printed name
            tableInfo.add("clinical"); //HTML link name
            subTables.add(tableInfo);
            tableInfo = new Vector<String>();
            tableInfo.add("Pheo_BiochemicalAssessment"); //Db tablename
            tableInfo.add("Biochemical Assessment"); //Printed name
            tableInfo.add("biochemical"); //HTML link name
            subTables.add(tableInfo);
            tableInfo = new Vector<String>();
            tableInfo.add("Pheo_ImagingTests"); //Db tablename
            tableInfo.add("Imaging Tests"); //Printed name
            tableInfo.add("imaging"); //HTML link name
            subTables.add(tableInfo);
            tableInfo = new Vector<String>();
            tableInfo.add("Pheo_TumorDetails"); //Db tablename
            tableInfo.add("Tumor Details"); //Printed name
            tableInfo.add("tumordetails"); //HTML link name
            subTables.add(tableInfo);
            tableInfo = new Vector<String>();
            tableInfo.add("Pheo_Surgery"); //Db tablename
            tableInfo.add("Surgery"); //Printed name
            tableInfo.add("surgery"); //HTML link name
            subTables.add(tableInfo);
            tableInfo = new Vector<String>();
            tableInfo.add("Pheo_Biomaterial"); //Db tablename
            tableInfo.add("Biomaterial"); //Printed name
            tableInfo.add("biomaterial"); //HTML link name
            subTables.add(tableInfo);
            tableInfo = new Vector<String>();
            tableInfo.add("Pheo_NonSurgicalInterventions"); //Db tablename
            tableInfo.add("Non-Surgical Interventions"); //Printed name
            tableInfo.add("interventions"); //HTML link name
            subTables.add(tableInfo);
            tableInfo = new Vector<String>();
            tableInfo.add("Pheo_FollowUp"); //Db tablename
            tableInfo.add("Follow-Up"); //Printed name
            tableInfo.add("followup"); //HTML link name
            subTables.add(tableInfo);            
            tableInfo = new Vector<String>();
            tableInfo.add("Pheo_Genetics"); //Db tablename
            tableInfo.add("Genetics"); //Printed name
            tableInfo.add("genetics"); //HTML link name
            subTables.add(tableInfo);            
            
            tableInfo = new Vector<String>();
            tableInfo.add("Pheo_Surgery_Procedures"); //Db tablename
            tableInfo.add("Surgery Procedures"); //Printed name
            tableInfo.add("surgical_procedures"); //HTML link name
            subTables.add(tableInfo);            
            tableInfo = new Vector<String>();
            tableInfo.add("Pheo_Morphological_Progression"); //Db tablename
            tableInfo.add("Morphological Progression"); //Printed name
            tableInfo.add("morphological_progression"); //HTML link name
            subTables.add(tableInfo);            
            tableInfo = new Vector<String>();
            tableInfo.add("Pheo_Biological_Assessment"); //Db tablename
            tableInfo.add("Biological Assessment"); //Printed name
            tableInfo.add("biological_assessment"); //HTML link name
            subTables.add(tableInfo);            
            
        } else if (dbn.equals("NAPACA")) {
            Vector<String> tableInfo = new Vector<String>();
            tableInfo.add("NAPACA_FollowUp"); //Db tablename
            tableInfo.add("Follow-Up"); //Printed name
            tableInfo.add("followup"); //HTML link name
            subTables.add(tableInfo);            
            tableInfo = new Vector<String>();
            tableInfo.add("NAPACA_Biomaterial"); //Db tablename
            tableInfo.add("Biomaterial"); //Printed name
            tableInfo.add("biomaterial"); //HTML link name
            subTables.add(tableInfo);
            tableInfo = new Vector<String>();
            tableInfo.add("NAPACA_Surgery"); //Db tablename
            tableInfo.add("Surgery"); //Printed name
            tableInfo.add("surgery"); //HTML link name
            subTables.add(tableInfo);
            tableInfo = new Vector<String>();
            tableInfo.add("NAPACA_Imaging"); //Db tablename
            tableInfo.add("Imaging"); //Printed name
            tableInfo.add("imaging"); //HTML link name
            subTables.add(tableInfo);
            tableInfo = new Vector<String>();
            tableInfo.add("NAPACA_Pathology"); //Db tablename
            tableInfo.add("Pathology"); //Printed name
            tableInfo.add("pathology"); //HTML link name
            subTables.add(tableInfo);
        } else if (dbn.equals("APA")) {
            Vector<String> tableInfo = new Vector<String>();
            tableInfo.add("APA_ClinicalAssessment"); //Db tablename
            tableInfo.add("Clinical Assessment"); //Printed name
            tableInfo.add("clinical"); //HTML link name
            subTables.add(tableInfo);
            tableInfo = new Vector<String>();
            tableInfo.add("APA_BiochemicalAssessment"); //Db tablename
            tableInfo.add("Biochemical Assessment"); //Printed name
            tableInfo.add("biochemical"); //HTML link name
            subTables.add(tableInfo);
            tableInfo = new Vector<String>();
            tableInfo.add("APA_Imaging"); //Db tablename
            tableInfo.add("Imaging"); //Printed name
            tableInfo.add("imaging"); //HTML link name
            subTables.add(tableInfo);
            tableInfo = new Vector<String>();
            tableInfo.add("APA_Surgery"); //Db tablename
            tableInfo.add("Surgery"); //Printed name
            tableInfo.add("surgery"); //HTML link name
            subTables.add(tableInfo);            
            tableInfo = new Vector<String>();
            tableInfo.add("APA_Biomaterial"); //Db tablename
            tableInfo.add("Biomaterial"); //Printed name
            tableInfo.add("biomaterial"); //HTML link name
            subTables.add(tableInfo);
            tableInfo = new Vector<String>();
            tableInfo.add("APA_Cardio"); //Db tablename
            tableInfo.add("Cardiovascular Assessment"); //Printed name
            tableInfo.add("cardio"); //HTML link name
            subTables.add(tableInfo);            
            tableInfo = new Vector<String>();
            tableInfo.add("APA_Complication"); //Db tablename
            tableInfo.add("Complications"); //Printed name
            tableInfo.add("complications"); //HTML link name
            subTables.add(tableInfo);            
            tableInfo = new Vector<String>();
            tableInfo.add("APA_FollowUp"); //Db tablename
            tableInfo.add("Follow-Up"); //Printed name
            tableInfo.add("followup"); //HTML link name
            subTables.add(tableInfo);                        
            tableInfo = new Vector<String>();
            tableInfo.add("APA_Genetics"); //Db tablename
            tableInfo.add("Genetics"); //Printed name
            tableInfo.add("genetics"); //HTML link name            
        }
        return subTables;
    }

    public String getAssocTableHtml(Vector<Vector> subTables, String dbid, String dbn, String centerid, String pid) {

        String outputStr = "";
        int ROW_SIZE = 3;

        int subTableNum = subTables.size();
        int rowNum = subTableNum / ROW_SIZE;
        int rowNumRemainder = subTableNum % ROW_SIZE;
        if (rowNumRemainder != 0) {
            rowNum++;
        }

        int elemCount = 0;
        for (int i = 0; i < rowNum; i++) {
            if (rowNumRemainder != 0 && i == (rowNum - 1)) {
                outputStr += "<tr>" + this.getAssocTableRowHtml(subTables, dbid, dbn, centerid, pid, rowNumRemainder, elemCount) + "</tr>";                
                elemCount = elemCount + rowNumRemainder;
            } else {
                outputStr += "<tr>" + this.getAssocTableRowHtml(subTables, dbid, dbn, centerid, pid, ROW_SIZE, elemCount) + "</tr>";
                //System.out.println("elemCount: " + elemCount);
                elemCount = elemCount + ROW_SIZE;
            }
            
        }
        //System.out.println("outputStr (getAssocTableHtml): " + outputStr);        
        
        return outputStr;
    }

    private String getAssocTableRowHtml(Vector<Vector> subTables, String dbid, String dbn, String centerid, String pid, int rowSize, int elemCount) {

        String outputStr = "";
        for (int i = 0; i < rowSize; i++) {
            Vector<String> subTableIn = subTables.get(elemCount + i);            
            outputStr += "<td><a href=\"./jsp/modality/readonly.jsp?dbid=" + dbid + "&dbn=" + dbn + "&pid=" + pid + "&centerid=" + centerid + "&modality=" + subTableIn.get(2) + "\"><strong>" + subTableIn.get(1) + "</a></strong></td>";
        }
        
        //System.out.println("outputStr (getAssocTableRowHtml): " + outputStr);
        return outputStr;
    }
    
    protected String formatEnsatId(String ensatId){
        
        String ensatIdOut = "";
        if(ensatId.length() == 3){
            ensatIdOut = "0" + ensatId;
        }else if(ensatId.length() == 2){
            ensatIdOut = "00" + ensatId;
        }else if(ensatId.length() == 1){
            ensatIdOut = "000" + ensatId;
        }else{
            ensatIdOut = ensatId;
        }
        return ensatIdOut;
    }

    public String getAssocInfoHtml(String dbid, String dbn, String centerid, String pid, Connection conn) {

        String outputStr = "";

        outputStr += this.getAssocInfoHeaderHtml();

        outputStr += this.getAssocInfoBodyHtml(dbid, dbn, centerid, pid, conn);

        return outputStr;
    }

    private String getAssocInfoHeaderHtml() {

        String outputStr = "";

        outputStr += "<thead>";
        outputStr += "<tr>";

        int COLUMN_NUM = 4;
        String[] columnHeaders = new String[COLUMN_NUM];
        columnHeaders[0] = "ENSAT ID";
        columnHeaders[1] = "Form ID";
        columnHeaders[2] = "Date";
        columnHeaders[3] = "Record Information";

        for (int i = 0; i < COLUMN_NUM; i++) {
            outputStr += "<th>";
            outputStr += columnHeaders[i];
            outputStr += "</th>";
        }

        outputStr += "<th>";
        outputStr += "</th>";
        outputStr += "</tr>";
        outputStr += "</thead>";

        return outputStr;
    }

    private String getAssocInfoBodyHtml(String dbid, String dbn, String centerid, String pid, Connection conn) {

        /*int COLUMN_NUM = 4;
        String outputStr = "";
        outputStr += "<tbody>";

        String sql = "SELECT * FROM " + dbn + "_FollowUp WHERE ensat_id=" + pid + " AND center_id='" + centerid + "'" + " ORDER BY followup_date DESC;";

        try {
            ResultSet rs = stmt.executeQuery(sql);

            String modid = "";
            String ensatid = "";
            while (rs.next()) {

                outputStr += "<tr>";
                modid = rs.getString(1);
                ensatid = rs.getString(2);
                centerid = rs.getString(3);
                for (int i = 0; i < COLUMN_NUM; i++) {

                    if (i == 0) {
                        outputStr += "<td>" + centerid + "-" + ensatid + "</td>";
                    } else if (i == 1) {
                        outputStr += "<td>" + modid + "</td>";
                    } else if (i == 2) {

                        String inputDate = rs.getString(4);
                        StringTokenizer st = new StringTokenizer(inputDate, "-");
                        String inputParam_year = st.nextToken();
                        String inputParam_month = st.nextToken();
                        String inputParam_day = st.nextToken();

                        String dispMonth = "";
                        if (inputParam_month.equals("01")) {
                            dispMonth = "Jan";
                        } else if (inputParam_month.equals("02")) {
                            dispMonth = "Feb";
                        } else if (inputParam_month.equals("03")) {
                            dispMonth = "Mar";
                        } else if (inputParam_month.equals("04")) {
                            dispMonth = "Apr";
                        } else if (inputParam_month.equals("05")) {
                            dispMonth = "May";
                        } else if (inputParam_month.equals("06")) {
                            dispMonth = "Jun";
                        } else if (inputParam_month.equals("07")) {
                            dispMonth = "Jul";
                        } else if (inputParam_month.equals("08")) {
                            dispMonth = "Aug";
                        } else if (inputParam_month.equals("09")) {
                            dispMonth = "Sep";
                        } else if (inputParam_month.equals("10")) {
                            dispMonth = "Oct";
                        } else if (inputParam_month.equals("11")) {
                            dispMonth = "Nov";
                        } else if (inputParam_month.equals("12")) {
                            dispMonth = "Dec";
                        }
                        outputStr += "<td>" + inputParam_day + " " + dispMonth + " " + inputParam_year + "</td>";
                    } else {

                        //THIS BIT VARIES DEPENDING ON THE DATABASE
                        int followupIndex = 1;
                        if(dbn.equals("ACC")){
                            followupIndex = 5;
                        }else if(dbn.equals("Pheo")){
                            followupIndex = 10;
                        }else if(dbn.equals("NAPACA")){
                            followupIndex = 21;
                        }else if(dbn.equals("APA")){
                            followupIndex = 32;
                        }
                        
                        if (i == 3) {
                            outputStr += "<td>" + rs.getString(followupIndex) + "</td>";
                        } else {
                            outputStr += "<td>" + rs.getString(i + 1) + "</td>";
                        }
                    }
                }
                outputStr += "<td><a href='./jsp/modality/read/readonly.jsp?dbid=" + dbid + "&dbn=" + dbn + "&pid=" + pid + "&centerid=" + centerid + "&modality=followup&modid=" + modid + "'>Detail</a></td></tr>";
            }
            rs.close();
        } catch (Exception e) {
            System.out.println("Error: " + e.getMessage());
        }
        outputStr += "</tbody>";
        return outputStr;*/
        
                String outputStr = "";
        ResultSet rs = null;
        int outputCount = 0;
        int columnNum = 4;

        outputStr += "<tbody>";
        
        String[] listQueryTables = null;
        String[] listQueryInfoOutput = null;
        String[] listQueryModalities = null;
        String[] listQueryDatenames = null;

        if(dbn.equals("ACC")){
        
            listQueryTables = new String[9];
            listQueryInfoOutput = new String[9];
            listQueryModalities = new String[9];
            listQueryDatenames = new String[9];
            
            listQueryTables[0] = "ACC_Biomaterial";
            listQueryTables[1] = "ACC_Chemoembolisation";
            listQueryTables[2] = "ACC_Chemotherapy";
            listQueryTables[3] = "ACC_FollowUp";
            listQueryTables[4] = "ACC_Mitotane";
            listQueryTables[5] = "ACC_Pathology";
            listQueryTables[6] = "ACC_Radiofrequency";
            listQueryTables[7] = "ACC_Radiotherapy";
            listQueryTables[8] = "ACC_Surgery";
            
            listQueryInfoOutput[0] = "Biomaterial";
            listQueryInfoOutput[1] = "Chemoembolisation";
            listQueryInfoOutput[2] = "Chemotherapy";
            listQueryInfoOutput[3] = "Follow-Up";
            listQueryInfoOutput[4] = "Mitotane";
            listQueryInfoOutput[5] = "Pathology";
            listQueryInfoOutput[6] = "Radiofrequency";
            listQueryInfoOutput[7] = "Radiotherapy";
            listQueryInfoOutput[8] = "Surgery";
            
            listQueryModalities[0] = "biomaterial";
            listQueryModalities[1] = "chemoembolisation";
            listQueryModalities[2] = "chemotherapy";
            listQueryModalities[3] = "followup";
            listQueryModalities[4] = "mitotane";
            listQueryModalities[5] = "pathology";
            listQueryModalities[6] = "radiofrequency";
            listQueryModalities[7] = "radiotherapy";
            listQueryModalities[8] = "surgery";
            
            listQueryDatenames[0] = "biomaterial_date";
            listQueryDatenames[1] = "chemoembolisation_date";
            listQueryDatenames[2] = "chemotherapy_initiation";
            listQueryDatenames[3] = "followup_date";
            listQueryDatenames[4] = "mitotane_initiation";
            listQueryDatenames[5] = "pathology_date";
            listQueryDatenames[6] = "radiofrequency_date";
            listQueryDatenames[7] = "radiotherapy_date";
            listQueryDatenames[8] = "surgery_date";
            
        }else if(dbn.equals("APA")){
            
            listQueryTables = new String[8];
            listQueryInfoOutput = new String[8];
            listQueryModalities = new String[8];
            listQueryDatenames = new String[8];
                        
            listQueryTables[0] = "APA_BiochemicalAssessment";
            listQueryTables[1] = "APA_Imaging";
            listQueryTables[2] = "APA_Cardio";
            listQueryTables[3] = "APA_Complication";
            listQueryTables[4] = "APA_ClinicalAssessment";
            listQueryTables[5] = "APA_Biomaterial";
            listQueryTables[6] = "APA_Surgery";
            listQueryTables[7] = "APA_FollowUp";            
            
            listQueryInfoOutput[0] = "Biochemical Assessment";
            listQueryInfoOutput[1] = "Imaging";
            listQueryInfoOutput[2] = "Cardio Events";
            listQueryInfoOutput[3] = "Complications";
            listQueryInfoOutput[4] = "Clinical Assessment";
            listQueryInfoOutput[5] = "Biomaterial";
            listQueryInfoOutput[6] = "Surgery";
            listQueryInfoOutput[7] = "Follow-Up";
            
            listQueryModalities[0] = "biochemical";
            listQueryModalities[1] = "imaging";
            listQueryModalities[2] = "cardio";
            listQueryModalities[3] = "complications";
            listQueryModalities[4] = "clinical";
            listQueryModalities[5] = "biomaterial";
            listQueryModalities[6] = "surgery";
            listQueryModalities[7] = "followup";            
            
            listQueryDatenames[0] = "assessment_date";
            listQueryDatenames[1] = "imaging_date";
            listQueryDatenames[2] = "event_date";
            listQueryDatenames[3] = "event_date";
            listQueryDatenames[4] = "assessment_date";
            listQueryDatenames[5] = "biomaterial_date";
            listQueryDatenames[6] = "intervention_date";
            listQueryDatenames[7] = "followup_date";            
                        
        }else if(dbn.equals("NAPACA")){
            
            listQueryTables = new String[5];
            listQueryInfoOutput = new String[5];
            listQueryModalities = new String[5];
            listQueryDatenames = new String[5];
            
            listQueryTables[0] = "NAPACA_Imaging";
            listQueryTables[1] = "NAPACA_Pathology";
            listQueryTables[2] = "NAPACA_Biomaterial";
            listQueryTables[3] = "NAPACA_Surgery";
            listQueryTables[4] = "NAPACA_FollowUp";
            
            listQueryInfoOutput[0] = "Imaging";
            listQueryInfoOutput[1] = "Pathology";
            listQueryInfoOutput[2] = "Biomaterial";
            listQueryInfoOutput[3] = "Surgery";
            listQueryInfoOutput[4] = "Follow-Up";
            
            listQueryModalities[0] = "imaging";
            listQueryModalities[1] = "pathology";
            listQueryModalities[2] = "biomaterial";
            listQueryModalities[3] = "surgery";
            listQueryModalities[4] = "followup";
            
            listQueryDatenames[0] = "imaging_date";
            listQueryDatenames[1] = "pathology_date";
            listQueryDatenames[2] = "biomaterial_date";
            listQueryDatenames[3] = "surgery_date";
            listQueryDatenames[4] = "followup_date";
            
        }else if(dbn.equals("Pheo")){
            
            listQueryTables = new String[9];
            listQueryInfoOutput = new String[9];
            listQueryModalities = new String[9];
            listQueryDatenames = new String[6];
                        
            listQueryTables[0] = "Pheo_BiochemicalAssessment";
            listQueryTables[1] = "Pheo_ImagingTests";
            listQueryTables[2] = "Pheo_NonSurgicalInterventions";
            listQueryTables[3] = "Pheo_ClinicalAssessment";
            listQueryTables[4] = "Pheo_Biomaterial";
            listQueryTables[5] = "Pheo_Surgery";
            listQueryTables[6] = "Pheo_FollowUp";
            listQueryTables[7] = "Pheo_Genetics";
            listQueryTables[8] = "Pheo_TumorDetails";
            
            listQueryInfoOutput[0] = "Biochemical Assessment";
            listQueryInfoOutput[1] = "Imaging";
            listQueryInfoOutput[2] = "Non-Surgical Interventions";
            listQueryInfoOutput[3] = "Clinical Assessment";
            listQueryInfoOutput[4] = "Biomaterial";
            listQueryInfoOutput[5] = "Surgery";
            listQueryInfoOutput[6] = "Follow-Up";
            listQueryInfoOutput[7] = "Genetics";
            listQueryInfoOutput[8] = "Tumor Details";
            
            listQueryModalities[0] = "biochemical";
            listQueryModalities[1] = "imaging";
            listQueryModalities[2] = "interventions";
            listQueryModalities[3] = "clinical";
            listQueryModalities[4] = "biomaterial";
            listQueryModalities[5] = "surgery";
            listQueryModalities[6] = "followup";
            listQueryModalities[7] = "genetics";
            listQueryModalities[8] = "tumordetails";
            
            listQueryDatenames[0] = "assessment_date";
            listQueryDatenames[1] = "biomaterial_date";
            listQueryDatenames[2] = "surgery_date";
            listQueryDatenames[3] = "followup_date";
            listQueryDatenames[4] = "genetics_date";
            listQueryDatenames[5] = "tumor_date";
        }
        
        int outputNum = 0;
        String[][] outputHtml = null;

        try {
            
            for (int j = 0; j < listQueryTables.length; j++) {
                
                String listQuerySql = "";
                if(dbn.equals("Pheo")){
                    if (j < 3) {
                        //listQuerySql = "SELECT * FROM " + listQueryTables[j] + " WHERE ensat_id=" + pid + " AND center_id='" + centerid + "';";
                        listQuerySql = "SELECT * FROM " + listQueryTables[j] + " WHERE ensat_id=? AND center_id=?;";
                    } else {
                        //listQuerySql = "SELECT * FROM " + listQueryTables[j] + " WHERE ensat_id=" + pid + " AND center_id='" + centerid + "'" + " ORDER BY " + listQueryDatenames[j - 3] + " DESC;";
                        listQuerySql = "SELECT * FROM " + listQueryTables[j] + " WHERE ensat_id=? AND center_id=? ORDER BY " + listQueryDatenames[j - 3] + " DESC;";
                    }
                }else{
                    //listQuerySql = "SELECT * FROM " + listQueryTables[j] + " WHERE ensat_id=" + pid + " AND center_id='" + centerid + "'" + " ORDER BY " + listQueryDatenames[j] + " DESC;";
                    listQuerySql = "SELECT * FROM " + listQueryTables[j] + " WHERE ensat_id=? AND center_id=? ORDER BY " + listQueryDatenames[j] + " DESC;";
                }
                
                PreparedStatement ps = conn.prepareStatement(listQuerySql);
                ps.setString(1,pid);
                ps.setString(2,centerid);
                
                rs = ps.executeQuery();
                while (rs.next()) {
                    outputNum++;
                }
            }

            outputHtml = new String[outputNum][2];

            for (int j = 0; j < listQueryTables.length; j++) {
                
                String listQuerySql = "";
                if(dbn.equals("Pheo")){
                    if (j < 3) {                        
                        listQuerySql = "SELECT * FROM " + listQueryTables[j] + " WHERE ensat_id=? AND center_id=?;";
                    } else {                        
                        listQuerySql = "SELECT * FROM " + listQueryTables[j] + " WHERE ensat_id=? AND center_id=? ORDER BY " + listQueryDatenames[j - 3] + " DESC;";
                    }
                }else{                    
                    listQuerySql = "SELECT * FROM " + listQueryTables[j] + " WHERE ensat_id=? AND center_id=? ORDER BY " + listQueryDatenames[j] + " DESC;";
                }
                
                PreparedStatement ps = conn.prepareStatement(listQuerySql);
                ps.setString(1,pid);
                ps.setString(2,centerid);
                rs = ps.executeQuery();

                String modid = "";
                String ensatid = "";
                while (rs.next()) {

                    outputHtml[outputCount][0] = "<tr>";
                    modid = rs.getString(1);
                    ensatid = rs.getString(2);
                    ensatid = this.formatEnsatId(ensatid);
                    centerid = rs.getString(3);
                    for (int i = 0; i < columnNum; i++) {

                        if (i == 0) {
                            outputHtml[outputCount][0] += "<td>" + centerid + "-" + ensatid + "</td>";
                        } else if (i == 1) {
                            outputHtml[outputCount][0] += "<td>" + modid + "</td>";
                        } else if (i == 2) {

                            String inputDate = "";

                            boolean multipleDates = (j < 3) && dbn.equals("Pheo");

                            if (multipleDates) {
                                //inputDate = "";      
                                String[] inputDates = null;
                                int[] inputIndex = null;
                                if (j == 0) {
                                    //Dates are on 5, 9, 11, 14, 17, 19
                                    inputIndex = new int[6];
                                    inputIndex[0] = 5;
                                    inputIndex[1] = 9;
                                    inputIndex[2] = 11;
                                    inputIndex[3] = 14;
                                    inputIndex[4] = 17;
                                    inputIndex[5] = 19;
                                } else if (j == 1) {
                                    //Dates are on 4,8,12,16,20,24,27,32
                                    inputIndex = new int[8];
                                    inputIndex[0] = 4;
                                    inputIndex[1] = 8;
                                    inputIndex[2] = 12;
                                    inputIndex[3] = 16;
                                    inputIndex[4] = 20;
                                    inputIndex[5] = 24;
                                    inputIndex[6] = 27;
                                    inputIndex[7] = 32;
                                } else if (j == 2) {
                                    //Dates are on 4,7,10,11,14,17,20,21,24
                                    inputIndex = new int[9];
                                    inputIndex[0] = 4;
                                    inputIndex[1] = 7;
                                    inputIndex[2] = 10;
                                    inputIndex[3] = 11;
                                    inputIndex[4] = 14;
                                    inputIndex[5] = 17;
                                    inputIndex[6] = 20;
                                    inputIndex[7] = 21;
                                    inputIndex[8] = 24;
                                }

                                inputDates = new String[inputIndex.length];
                                for (int k = 0; k < inputIndex.length; k++) {
                                    inputDates[k] = rs.getString((inputIndex[k] + 1));
                                    if (inputDates[k] == null) {
                                        inputDates[k] = "";
                                    }
                                }
                                boolean dateFound = false;
                                int dateCount = 0;
                                while (!dateFound && dateCount < 6) {
                                    StringTokenizer st = new StringTokenizer(inputDates[dateCount], "-");
                                    dateFound = st.countTokens() >= 3;
                                    dateCount++;
                                }
                                if (dateFound) {
                                    inputDate = inputDates[dateCount - 1];
                                }

                            }else{ //End of multipleDates clause                            
                                if (listQueryModalities[j].equals("chemotherapy") || listQueryModalities[j].equals("mitotane")) {
                                    inputDate = rs.getString(5);
                                } else {
                                    inputDate = rs.getString(4);
                                }
                            }
                            
                            //System.out.println("inputDate: " + inputDate);

                            if (inputDate == null) {
                                inputDate = "";
                            }

                            outputHtml[outputCount][1] = inputDate;
                            StringTokenizer st = new StringTokenizer(inputDate, "-");

                            String inputParam_year = "";
                            String inputParam_month = "";
                            String inputParam_day = "";

                            if (!inputDate.equals("")) {
                                inputParam_year = st.nextToken();
                                inputParam_month = st.nextToken();
                                inputParam_day = st.nextToken();
                            }

                            String dispMonth = "";
                            if (inputParam_month.equals("01")) {
                                dispMonth = "Jan";
                            } else if (inputParam_month.equals("02")) {
                                dispMonth = "Feb";
                            } else if (inputParam_month.equals("03")) {
                                dispMonth = "Mar";
                            } else if (inputParam_month.equals("04")) {
                                dispMonth = "Apr";
                            } else if (inputParam_month.equals("05")) {
                                dispMonth = "May";
                            } else if (inputParam_month.equals("06")) {
                                dispMonth = "Jun";
                            } else if (inputParam_month.equals("07")) {
                                dispMonth = "Jul";
                            } else if (inputParam_month.equals("08")) {
                                dispMonth = "Aug";
                            } else if (inputParam_month.equals("09")) {
                                dispMonth = "Sep";
                            } else if (inputParam_month.equals("10")) {
                                dispMonth = "Oct";
                            } else if (inputParam_month.equals("11")) {
                                dispMonth = "Nov";
                            } else if (inputParam_month.equals("12")) {
                                dispMonth = "Dec";
                            }
                            outputHtml[outputCount][0] += "<td>" + inputParam_day + " " + dispMonth + " " + inputParam_year + "</td>";

                        } else {
                            if (i == 3) {
                                outputHtml[outputCount][0] += "<td>" + listQueryInfoOutput[j] + "</td>";
                            }
                        }
                    }
                    outputHtml[outputCount][0] += "<td><a href='./jsp/modality/read/readonly.jsp?dbid=" + dbid + "&dbn=" + dbn + "&pid=" + pid + "&centerid=" + centerid + "&modality=" + listQueryModalities[j] + "&modid=" + modid + "'>Detail</a></td>";
                    //outputHtml[outputCount][0] += "<td><a href='./jsp/modality/delete/delete_view.jsp?dbid=" + dbid + "&dbn=" + dbn + "&pid=" + pid + "&centerid=" + centerid + "&modality=" + listQueryModalities[j] + "&modid=" + modid + "'>Delete</a></td></tr>";
                    outputCount++;
                }
                //System.out.println("TEST...");
            }
            rs.close();
        } catch (Exception e) {
            logger.debug("Error (within listQuery): " + e.getMessage());
        }

        String[] htmlOutputStr = new String[outputNum];
        java.util.Date[] htmlDates = new java.util.Date[outputNum];
        DateFormat formatter = new SimpleDateFormat("yyyy-MM-dd");
        try {
            for (int i = 0; i < outputNum; i++) {
                if (!outputHtml[i][1].equals("--") && !outputHtml[i][1].equals("")) {
                    htmlDates[i] = (java.util.Date) formatter.parse(outputHtml[i][1]);
                } else {
                    htmlDates[i] = (java.util.Date) formatter.parse("1900-01-01");
                }
                htmlOutputStr[i] = outputHtml[i][0];
            }
            /*for (int i = 0; i < outputNum; i++) {
                htmlDates[i] = (java.util.Date) formatter.parse(outputHtml[i][1]);
                htmlOutputStr[i] = outputHtml[i][0];
            }*/
        } catch (Exception e) {
            logger.debug("Error (sorting): " + e.getMessage());
        }
        SortBean.DateSort dateSort = new SortBean.DateSort(htmlOutputStr, htmlDates, outputNum);
        SortBean.HtmlOutput[] htmlOutputs = new SortBean.HtmlOutput[outputNum];
        for (int i = 0; i < outputCount; i++) {
            htmlOutputs[i] = dateSort.getHtmlOutput(i);
        }

        for (int i = 0; i < outputNum; i++) {
            outputStr += htmlOutputs[i].getHtmlOutput();
        }

        outputStr += "</tbody>";
        return outputStr;
    }
    
    public String standardisePid(String pid){        
        String pidOut = "";
        if(pid.length()==3){
            pidOut = "0" + pid;
        }else if(pid.length()==2){
            pidOut = "00" + pid;
        }else if(pid.length()==1){
            pidOut = "000" + pid;
        }else{
            pidOut = pid;
        }
        return pidOut;
    }
    
    public String getParameterValue(String paramName, String tablename, Connection conn, String centerid, String pid) {

        String sql = "SELECT " + paramName + " FROM " + tablename + " WHERE ensat_id=? AND center_id=?;";
        String paramValue = "";

        try{
            PreparedStatement ps = conn.prepareStatement(sql);
            ps.setString(1, pid);
            ps.setString(2, centerid);

            ResultSet rs = ps.executeQuery();
            if (rs.next()) {
                paramValue = rs.getString(1);
            }
        }catch(Exception e){
            logger.info("Error: " + e.getMessage());
        }

        return paramValue;
    }
    
}
