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
import java.sql.PreparedStatement;

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
public class Delete {
    
    private static final Logger logger = Logger.getLogger(Delete.class);
    private String username = "";

    public Delete() {
    }
    
    public void setUsername(String _username) {
        username = _username;
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
    
    protected boolean getParamException (String paramIn){
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

    public Vector<Vector> getParameterValues(String[] tablenames, Vector<Vector> parameters, String pid, String centerid, Connection conn) {

        int paramNum = parameters.size();
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
        sql += " AND Identification.ensat_id=? AND Identification.center_id=?;";

        Vector<Vector> parametersOut = new Vector<Vector>();
        try {
            PreparedStatement ps = conn.prepareStatement(sql);
            ps.setString(1,pid);
            ps.setString(2,centerid);
            ResultSet rs = ps.executeQuery();
            
            if (rs.next()) {
                int index = 0;
                for (int i = 0; i < paramNum; i++) {
                    
                    Vector<String> parameterIn = parameters.get(i);
                    boolean paramException = this.getParamException(parameterIn.get(0));
                    if(!paramException){
                            String valueIn = "";
                            valueIn = rs.getString(index + 1);
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
            String paramName = rowIn.get(0);
            String paramValue = rowIn.get(2);

            outputStr += "<tr ";
            if (i % 2 != 0) {
                outputStr += lineColour;
            }
            outputStr += ">";
            outputStr += "<td width=\"50%\">";
            outputStr += rowIn.get(2) + ":";
            outputStr += "</td>";
            outputStr += "<td>";
            
            //Run a check for multiple values
            StringTokenizer st = new StringTokenizer(paramValue,"|");
            if(st.countTokens() > 1){
                paramValue = "";
                while(st.hasMoreTokens()){
                    paramValue += st.nextToken() + "<br/>";
                }
            }
            outputStr += "<strong>" + paramValue + "</strong>";
            outputStr += "</td>";
            outputStr += "</tr>";
        }
        return outputStr;
    }

    public Vector<Vector> compileSubTableList(String dbn) {

        Vector<Vector> subTables = new Vector<Vector>();

        //Check for sub-forms and delete any that are encountered
        if (dbn.equals("ACC")) {            
            Vector<String> tableInfo = new Vector<String>();
            tableInfo.add("ACC_Biomaterial"); //Db tablename
            tableInfo.add("Biomaterial"); //Printed name
            tableInfo.add("biomaterial"); //HTML link name
            subTables.add(tableInfo);
            tableInfo = new Vector<String>();
            tableInfo.add("ACC_Chemoembolisation"); //Db tablename
            tableInfo.add("Chemoembolisation"); //Printed name
            tableInfo.add("chemoembolisation"); //HTML link name
            subTables.add(tableInfo);
            tableInfo = new Vector<String>();
            tableInfo.add("ACC_Chemotherapy"); //Db tablename
            tableInfo.add("Chemotherapy"); //Printed name
            tableInfo.add("chemotherapy"); //HTML link name
            subTables.add(tableInfo);
            tableInfo = new Vector<String>();
            tableInfo.add("ACC_FollowUp"); //Db tablename
            tableInfo.add("Follow-up"); //Printed name
            tableInfo.add("followup"); //HTML link name
            subTables.add(tableInfo);
            tableInfo = new Vector<String>();
            tableInfo.add("ACC_Mitotane"); //Db tablename
            tableInfo.add("Mitotane"); //Printed name
            tableInfo.add("mitotane"); //HTML link name
            subTables.add(tableInfo);
            tableInfo = new Vector<String>();
            tableInfo.add("ACC_Pathology"); //Db tablename
            tableInfo.add("Pathology"); //Printed name
            tableInfo.add("pathology"); //HTML link name
            subTables.add(tableInfo);
            tableInfo = new Vector<String>();
            tableInfo.add("ACC_Radiofrequency"); //Db tablename
            tableInfo.add("Radiofrequency"); //Printed name
            tableInfo.add("radiofrequency"); //HTML link name
            subTables.add(tableInfo);
            tableInfo = new Vector<String>();
            tableInfo.add("ACC_Radiotherapy"); //Db tablename
            tableInfo.add("Radiotherapy"); //Printed name
            tableInfo.add("radiotherapy"); //HTML link name
            subTables.add(tableInfo);
            tableInfo = new Vector<String>();
            tableInfo.add("ACC_Surgery"); //Db tablename
            tableInfo.add("Surgery"); //Printed name
            tableInfo.add("surgery"); //HTML link name
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
            tableInfo.add("APA_FollowUp"); //Db tablename
            tableInfo.add("Follow-Up"); //Printed name
            tableInfo.add("followup"); //HTML link name
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
        }
        return subTables;
    }

    public void deleteSubTables(Vector<Vector> subTables, Connection conn, String pid, String centerid, String dbn) {

        int subTableLength = subTables.size();

        //Run the subtable delete
        try {
            logger.debug("('" + username + "') - " + dbn + " Record sub-tables deleted (Ensat ID: " + centerid + "-" + pid + ")");
            
            for (int i = 0; i < subTableLength; i++) {
                String checkSql = "SELECT ensat_id,center_id FROM " + subTables.get(i).get(0) + " WHERE ensat_id=? AND center_id=?;";
                
                PreparedStatement ps = conn.prepareStatement(checkSql);
                ps.setString(1,pid);
                ps.setString(2,centerid);
                ResultSet tmpRs = ps.executeQuery();
                                
                boolean recordFound = tmpRs.next();
                if (recordFound) {
                    String deleteSql = "DELETE FROM " + subTables.get(i).get(0) + " WHERE ensat_id=? AND center_id=?;";
                    PreparedStatement ps2 = conn.prepareStatement(deleteSql);
                    ps2.setString(1,pid);
                    ps2.setString(2,centerid);                    
                    int updateSubDelete = ps2.executeUpdate();                                    
                }
            }
        } catch (Exception e) {
            logger.debug("Error (deleteSubTables): " + e.getMessage());
        }
    }

    public void deleteMainTables(Connection conn, String pid, String centerid, String dbn) {

        //Run the main-table delete
        try {
            logger.debug("('" + username + "') - " + dbn + " Record deleted (Ensat ID: " + centerid + "-" + pid + ")");
            if (dbn.equals("ACC")) {
                String sql = "DELETE FROM ACC_DiagnosticProcedures WHERE ensat_id = ? AND center_id=?;";
                PreparedStatement ps = conn.prepareStatement(sql);
                ps.setString(1,pid);
                ps.setString(2,centerid);
                int update2 = ps.executeUpdate();                
                
                String sql2 = "DELETE FROM ACC_TumorStaging WHERE ensat_id = ? AND center_id=?;";
                PreparedStatement ps2 = conn.prepareStatement(sql);
                ps2.setString(1,pid);
                ps2.setString(2,centerid);
                int update3 = ps2.executeUpdate();                
                
            } else if (dbn.equals("Pheo")) {
                String sql = "DELETE FROM Pheo_PatientHistory WHERE ensat_id = ? AND center_id=?;";
                PreparedStatement ps = conn.prepareStatement(sql);
                ps.setString(1,pid);
                ps.setString(2,centerid);
                int update2 = ps.executeUpdate();                
                
            } else if (dbn.equals("NAPACA")) {                
                String sql = "DELETE FROM NAPACA_DiagnosticProcedures WHERE ensat_id = ? AND center_id=?;";
                PreparedStatement ps = conn.prepareStatement(sql);
                ps.setString(1,pid);
                ps.setString(2,centerid);
                int update2 = ps.executeUpdate();                
            } else if (dbn.equals("APA")) {
                
                String sql = "DELETE FROM APA_PatientHistory WHERE ensat_id = ? AND center_id=?;";
                PreparedStatement ps = conn.prepareStatement(sql);
                ps.setString(1,pid);
                ps.setString(2,centerid);
                int update2 = ps.executeUpdate();                
            }
            
            String sql = "DELETE FROM Identification WHERE ensat_id = ? AND center_id=?;";
            PreparedStatement ps = conn.prepareStatement(sql);
            ps.setString(1,pid);
            ps.setString(2,centerid);
            int update = ps.executeUpdate();
            
            String sqlAssoc = "DELETE FROM Associated_Studies WHERE ensat_id = ? AND center_id=?;";
            PreparedStatement psAssoc = conn.prepareStatement(sqlAssoc);
            psAssoc.setString(1,pid);
            psAssoc.setString(2,centerid);
            int updateAssoc = psAssoc.executeUpdate();

        } catch (Exception e) {
            logger.debug("Error (deleteMainTables): " + e.getMessage());
        }
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
}
