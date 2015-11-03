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
import java.sql.ResultSetMetaData;
import java.sql.PreparedStatement;
import java.text.DateFormat;
import java.text.SimpleDateFormat;

import java.sql.SQLException;
import java.util.Random;

import java.util.Vector;
import java.util.StringTokenizer;
import javax.servlet.ServletContext;

import javax.servlet.http.HttpServletRequest;

import org.apache.log4j.Level;
import org.apache.log4j.Logger;
import org.apache.log4j.PropertyConfigurator;

/**
 *
 * @author astell
 */
public class UpdateSub extends Update {

    private static final Logger logger = Logger.getLogger(UpdateSub.class);
    private String username = "";

    public UpdateSub() {
    }

    public void setUsername(String _username) {
        username = _username;
    }

    public Vector<Vector> getParameters(String[] _tablenames, String pid, String centerid, String modid, String subTableIdName, Connection conn, Connection paramConn) {

        //System.out.println("TESTING GENETICS FORM...");

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
        sql += " ORDER BY param_order_id ASC;";

        Vector<Vector> parameters = new Vector<Vector>();
        try {
            PreparedStatement ps = paramConn.prepareStatement(sql);
            ps.setString(1, tablenames[0]);
            if (tablenames.length > 1) {
                for (int i = 1; i < tablenames.length; i++) {
                    //sql += " OR param_table='" + tablenames[i] + "'";
                    //sql += " OR param_table=?";
                    ps.setString(i + 1, tablenames[i]);
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
                
                String paramName = rowIn.get(1);
                //logger.debug("paramName (getParameters - UpdateSub): " + paramName);
                
                parameters.add(rowIn);
            }
        } catch (Exception e) {
            logger.debug("('" + username + "') Error (getParameters): " + e.getMessage());
        }

        //Now add the values to the parameter vector and return
        parameters = this.getParameterValues(tablenames, parameters, pid, centerid, modid, subTableIdName, conn);
        return parameters;
    }

    public Vector<Vector> getParameterValues(String[] tablenames, Vector<Vector> parameters, String pid, String centerid, String modid, String subTableIdName, Connection connValues) {

        int paramNum = parameters.size();
        int tableNum = tablenames.length;
        String sql = "";
        sql += "SELECT ";
        for (int i = 0; i < paramNum; i++) {
            Vector<String> parameterIn = parameters.get(i);
            String paramName = parameterIn.get(1);
            //System.out.println("paramName (getParameterValues - updateSub): " + paramName);
            boolean parameterIsMultiple = this.getParameterIsMultiple(paramName);
            if (!parameterIsMultiple) {
                sql += "" + parameterIn.get(8) + "." + parameterIn.get(1) + ", ";
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

        //logger.debug("sql (getParameterValues - UpdateSub): " + sql);

        Vector<Vector> parametersOut = new Vector<Vector>();
        try {
            PreparedStatement ps = connValues.prepareStatement(sql);
            ps.setString(1, pid);
            ps.setString(2, centerid);
            ps.setString(3, modid);
            ResultSet rs = ps.executeQuery();

            String surgeryValueIn = "";
            while (rs.next()) {
                int paramCount = 0;
                for (int i = 0; i < paramNum; i++) {
                    Vector<String> parameterIn = parameters.get(i);
                    String paramName = parameterIn.get(1);
                    boolean parameterIsMultiple = this.getParameterIsMultiple(paramName); //MIGHT REQUIRE STUBBING THIS OUT TO "false" (CHECK LATER...)

                    String valueIn = "";
                    if (!parameterIsMultiple) {
                        //System.out.println("paramName: " + paramName);
                        //System.out.println("i: " + i);
                        valueIn = rs.getString(paramCount + 1);
                        //logger.debug("valueIn: " + valueIn);
                        if (valueIn == null) {
                            valueIn = "";
                        }
                        parameterIn.add(valueIn);

                        //logger.debug("(checking surgery values) - paramName: " + paramName);
                        //logger.debug("(checking surgery values) - valueIn: " + valueIn);

                        if (paramName.equals("surgery_type")) {
                            surgeryValueIn = valueIn;
                        }

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
            } else if (tablenames[0].equals("NAPACA_Biomaterial")) {
                multipleTablename = "NAPACA_Biomaterial_Normal_Tissue";
            } else if (tablenames[0].equals("ACC_Biomaterial")) {
                multipleTablename = "ACC_Biomaterial_Normal_Tissue";
            } else if (tablenames[0].equals("Pheo_Biomaterial")) {
                multipleTablename = "Pheo_Biomaterial_Normal_Tissue";
            } else if (tablenames[0].equals("APA_Biomaterial")) {
                multipleTablename = "APA_Biomaterial_Normal_Tissue";
            } else if (tablenames[0].equals("ACC_FollowUp")) {
                multipleTablename = "ACC_FollowUp_Organs";
            } else if (tablenames[0].equals("ACC_Radiofrequency")) {
                multipleTablename = "ACC_Radiofrequency_Loc";
            } else if (tablenames[0].equals("ACC_Radiotherapy")) {
                multipleTablename = "ACC_Radiotherapy_Loc";
            } else if (tablenames[0].equals("Pheo_TumorDetails")) {
                multipleTablename = "Pheo_MetastasesLocation";
            } else if (tablenames[0].equals("Pheo_ImagingTests")) {
                multipleTablename = "Pheo_ImagingTests_CTLoc";
            } else if (tablenames[0].equals("ACC_Surgery")) {

                //logger.debug("(checking surgery values) - surgeryValueIn: " + surgeryValueIn);
                if (surgeryValueIn.equals("First")) {
                    multipleTablename = "ACC_Surgery_First";
                } else {
                    multipleTablename = "ACC_Surgery_Extended";
                }
            }

            //Need to run the full loop again because the resultsets can't be nested
            for (int i = 0; i < paramNum; i++) {
                Vector<String> parameterIn = parameters.get(i);
                String paramName = parameterIn.get(1);
                boolean parameterIsMultiple = this.getParameterIsMultiple(paramName);

                if (parameterIsMultiple) {

                    boolean dontRunSurgery = (surgeryValueIn.equals("Repeated") && paramName.equals("surgery_first"))
                            || (surgeryValueIn.equals("First") && paramName.equals("surgery_extended"));

                    String multValue = "";
                    if (!dontRunSurgery) {
                        
                        if(paramName.equals("ct_location")){
                            multipleTablename = "Pheo_ImagingTests_CTLoc";
                        }else if(paramName.equals("nmr_location")){
                            multipleTablename = "Pheo_ImagingTests_NMRLoc";
                        }else if(paramName.equals("preop_blockade_agents")){
                            multipleTablename = "Pheo_Surgery_PreOp";
                        }else if(paramName.equals("intraop_bp_control_agents")){
                            multipleTablename = "Pheo_Surgery_IntraOp";
                        }/*else if(paramName.equals("surgical_procedure")){
                            multipleTablename = "Pheo_Surgery_Procedure";
                        }*/

                        String multipleSql = this.getMultipleSql(multipleTablename, pid, centerid, subTableIdName, modid);
                        //logger.debug("(checking surgery updates) multipleSql: " + multipleSql);

                        PreparedStatement multPs = connValues.prepareStatement(multipleSql);
                        multPs.setString(1, pid);
                        multPs.setString(2, centerid);
                        multPs.setString(3, modid);
                        ResultSet rsMult = multPs.executeQuery();

                        while (rsMult.next()) {
                            if (tablenames[0].equals("ACC_Biomaterial")
                                    || tablenames[0].equals("Pheo_Biomaterial")
                                    || tablenames[0].equals("NAPACA_Biomaterial")
                                    || tablenames[0].equals("APA_Biomaterial")) {

                                String normalTissueType = rsMult.getString(5);
                                if ((paramName.equals("normal_tissue_options") && normalTissueType.equals("frozen"))
                                        || (paramName.equals("normal_tissue_paraffin_options") && normalTissueType.equals("paraffin"))
                                        || (paramName.equals("normal_tissue_dna_options") && normalTissueType.equals("dna"))) {
                                    multValue += "" + rsMult.getString(6) + "|";
                                }
                            } else {
                                multValue += "" + rsMult.getString(5) + "|";
                            }
                        }
                        //Remove the final "|"
                        if (!multValue.equals("")) {
                            multValue = multValue.substring(0, multValue.length() - 1);
                        }
                    }
                    parameterIn.add(multValue);
                    parametersOut.set(i, parameterIn);
                }
            }
        } catch (Exception e) {
            logger.debug("('" + username + "') Error (getParameterValues): " + e.getMessage());
        }

        return parametersOut;
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
                || paramName.equals("metastases_location")
                || paramName.equals("nmr_location")
                || paramName.equals("ct_location")
                || paramName.equals("preop_blockade_agents")
                || paramName.equals("intraop_bp_control_agents")
                ;
    }

    private String getMultipleSql(String tablename, String pid, String centerid, String subTableIdName, String modid) {
        String sql = "";
        //sql += "SELECT * FROM " + tablename + " WHERE ensat_id=" + pid + " AND center_id='" + centerid + "' AND " + subTableIdName + "=" + modid + ";";
        sql += "SELECT * FROM " + tablename + " WHERE ensat_id=? AND center_id=? AND " + subTableIdName + "=?;";
        return sql;
    }

    public void executeParameterUpdate(int nextId, String tablename, Vector<Vector> parameters, Connection connection, HttpServletRequest request) {

        Vector<String> lastPageParamNames = new Vector<String>();
        Vector<String> lastPageParamTypes = new Vector<String>();
        Vector<String> lastPageParamValues = new Vector<String>();

        int paramSize = parameters.size();

        for (int i = 0; i < paramSize; i++) {
            Vector<String> paramIn = parameters.get(i);
            lastPageParamNames.add(paramIn.get(1));
            lastPageParamTypes.add(paramIn.get(2));
            lastPageParamValues.add(paramIn.get(10));
        }

        //String updateSql = "INSERT INTO " + tablename + " VALUES(" + nextId + ",";
        String updateSql = "INSERT INTO " + tablename + " VALUES(?,";

        int paramNum = lastPageParamNames.size();
        for (int i = 0; i < paramNum; i++) {

            String paramName = lastPageParamNames.get(i);
            String paramValue = lastPageParamValues.get(i);
            
            paramValue.replaceAll(";", "\\;");

            //Do number conversions for specific parameter names
            if (paramName.equals("gluco_serum_cortisol_napaca")
                    || paramName.equals("gluco_plasma_acth_napaca")
                    || paramName.equals("gluco_urinary_free_cortisol_napaca")
                    || paramName.equals("other_steroid_17hydroxyprogesterone_napaca")
                    || paramName.equals("other_steroid_serum_dheas_napaca")) {
                paramValue = this.napacaUnitConversion(paramName, paramValue, request);
            }
            //updateSql += "'" + paramValue + "',";
            updateSql += "?,";

        }
        updateSql = updateSql.substring(0, updateSql.length() - 1);
        updateSql += ");";

        try {
            //logger.debug("updateSql: " + updateSql);
            PreparedStatement ps = connection.prepareStatement(updateSql);
            ps.setInt(1, nextId);
            for (int i = 0; i < paramNum; i++) {

                String paramName = lastPageParamNames.get(i);
                String paramType = lastPageParamTypes.get(i);
                String paramValue = lastPageParamValues.get(i);
                
                paramValue.replaceAll(";", "\\;");

                //Do number conversions for specific parameter names
                if (paramName.equals("gluco_serum_cortisol_napaca")
                        || paramName.equals("gluco_plasma_acth_napaca")
                        || paramName.equals("gluco_urinary_free_cortisol_napaca")
                        || paramName.equals("other_steroid_17hydroxyprogesterone_napaca")
                        || paramName.equals("other_steroid_serum_dheas_napaca")) {
                    paramValue = this.napacaUnitConversion(paramName, paramValue, request);
                }
                //updateSql += "'" + paramValue + "',";
                //updateSql += "?,";
                if (paramType.equals("date")) {
                    paramValue = this.reformatDateValue(paramValue);
                }
                ps.setString(i + 2, paramValue);

            }


            logger.debug("('" + username + "') RECORD UPDATED - details to go here...");
            int update = ps.executeUpdate();
        } catch (Exception e) {
            logger.debug("('" + username + "') Error (executeParameterUpdate): " + e.getMessage());
        }

    }

    private boolean getTableIsMultiple(String tablename) {

        return tablename.equals("ACC_Chemotherapy_Regimen")
                || tablename.equals("ACC_FollowUp_Organs")
                || tablename.equals("ACC_Radiofrequency_Loc")
                || tablename.equals("ACC_Radiotherapy_Loc")
                || tablename.equals("ACC_Surgery_Extended")
                || tablename.equals("ACC_Surgery_First")
                || tablename.equals("Pheo_MetastasesLocation")
                || tablename.equals("Pheo_ImagingTests_CTLoc")
                || tablename.equals("Pheo_ImagingTests_NMRLoc")
                || tablename.equals("Pheo_Surgery_PreOp")
                || tablename.equals("Pheo_Surgery_IntraOp")
                //|| tablename.equals("Pheo_Surgery_Procedure")
                || tablename.equals("ACC_Biomaterial_Normal_Tissue")
                || tablename.equals("Pheo_Biomaterial_Normal_Tissue")
                || tablename.equals("NAPACA_Biomaterial_Normal_Tissue")
                || tablename.equals("APA_Biomaterial_Normal_Tissue");
    }

    private void executeMultipleUpdate(String tableId, String modid, String tablename, String pid, String centerid, Connection connection, HttpServletRequest request) {

        //Clear out the values that are going to be used in the multiple update
        Vector<String> lastPageParamValues = new Vector<String>();

        //Delete the current values
        String deleteSql = "";
        //deleteSql = "DELETE FROM " + tablename + " WHERE ensat_id=" + pid + " AND center_id='" + centerid + "' AND " + tableId + "=" + modid + ";";
        deleteSql = "DELETE FROM " + tablename + " WHERE ensat_id=? AND center_id=? AND " + tableId + "=?;";
        //logger.debug("Updating deleteSql: " + deleteSql);
        try {
            PreparedStatement ps = connection.prepareStatement(deleteSql);
            ps.setString(1, pid);
            ps.setString(2, centerid);
            ps.setString(3, modid);

            int update = ps.executeUpdate();
        } catch (Exception e) {
            logger.debug("('" + username + "') Error (executeMultipleUpdate): " + e.getMessage());
        }

        //Now populate with the new parameters
        String[] paramLabel = null;
        int typeNum = 0;
        Vector<String> lastPageParamTypes = new Vector<String>();
        if (tablename.equals("ACC_Chemotherapy_Regimen")) {
            typeNum = 1;
            paramLabel = new String[typeNum];
            paramLabel[0] = "chemotherapy_regimen";
        } else if (tablename.equals("ACC_FollowUp_Organs")) {
            typeNum = 1;
            paramLabel = new String[typeNum];
            paramLabel[0] = "followup_organs";
        } else if (tablename.equals("ACC_Radiofrequency_Loc")) {
            typeNum = 1;
            paramLabel = new String[typeNum];
            paramLabel[0] = "radiofrequency_location";
        } else if (tablename.equals("ACC_Radiotherapy_Loc")) {
            typeNum = 1;
            paramLabel = new String[typeNum];
            paramLabel[0] = "radiotherapy_location";
        } else if (tablename.equals("Pheo_MetastasesLocation")) {
            typeNum = 1;
            paramLabel = new String[typeNum];
            paramLabel[0] = "metastases_location";
        } else if (tablename.equals("Pheo_ImagingTests_CTLoc")) {
            typeNum = 1;
            paramLabel = new String[typeNum];
            paramLabel[0] = "ct_location";
        } else if (tablename.equals("Pheo_ImagingTests_NMRLoc")) {
            typeNum = 1;
            paramLabel = new String[typeNum];
            paramLabel[0] = "nmr_location";
        } else if (tablename.equals("Pheo_Surgery_PreOp")) {
            typeNum = 1;
            paramLabel = new String[typeNum];
            paramLabel[0] = "preop_blockade_agents";
        } else if (tablename.equals("Pheo_Surgery_IntraOp")) {
            typeNum = 1;
            paramLabel = new String[typeNum];
            paramLabel[0] = "intraop_bp_control_agents";
        }/* else if (tablename.equals("Pheo_Surgery_Procedure")) {
            typeNum = 1;
            paramLabel = new String[typeNum];
            paramLabel[0] = "surgical_procedure";
        }*/ else if (tablename.equals("ACC_Surgery_Extended")) {
            typeNum = 1;
            paramLabel = new String[typeNum];
            paramLabel[0] = "surgery_extended";
        } else if (tablename.equals("ACC_Surgery_First")) {
            typeNum = 1;
            paramLabel = new String[typeNum];
            paramLabel[0] = "surgery_first";
        } else if (tablename.equals("ACC_Biomaterial_Normal_Tissue")
                || tablename.equals("Pheo_Biomaterial_Normal_Tissue")
                || tablename.equals("NAPACA_Biomaterial_Normal_Tissue")
                || tablename.equals("APA_Biomaterial_Normal_Tissue")) {
            typeNum = 3;
            paramLabel = new String[typeNum];
            paramLabel[0] = "normal_tissue_options";
            paramLabel[1] = "normal_tissue_paraffin_options";
            paramLabel[2] = "normal_tissue_dna_options";
        }


        for (int n = 0; n < typeNum; n++) {
            String typeFlag = "";
            if (paramLabel[n].equals("normal_tissue_options")) {
                typeFlag = "frozen";
            } else if (paramLabel[n].equals("normal_tissue_paraffin_options")) {
                typeFlag = "paraffin";
            } else if (paramLabel[n].equals("normal_tissue_dna_options")) {
                typeFlag = "dna";
            }
            String paramValueNumInStr = request.getParameter(paramLabel[n] + "_num");
            int paramValueNumIn = Integer.parseInt(paramValueNumInStr);
            //System.out.println("paramValueNumIn: " + paramValueNumIn);
            for (int i = 0; i < paramValueNumIn; i++) {
                String valueIn = request.getParameter(paramLabel[n] + "_" + (i + 1));
                lastPageParamValues.add(valueIn);
                if (paramLabel[n].equals("normal_tissue_options")
                        || paramLabel[n].equals("normal_tissue_paraffin_options")
                        || paramLabel[n].equals("normal_tissue_dna_options")) {
                    lastPageParamTypes.add(typeFlag);
                }
            }
        }

        //Run an check for the last ID if the table is multiple
        int multipleNextId = 1;
        String idLabel = this.getMultipleSubTableIdName(tablename);
        //String nextIdCheck = "SELECT " + idLabel + " FROM " + tablename + " WHERE ensat_id=" + pid + " AND center_id='" + centerid + "' ORDER BY " + idLabel + " DESC";
        String nextIdCheck = "SELECT " + idLabel + " FROM " + tablename + " WHERE ensat_id=? AND center_id=? ORDER BY " + idLabel + " DESC";
        try {
            PreparedStatement ps = connection.prepareStatement(nextIdCheck);
            ps.setString(1, pid);
            ps.setString(2, centerid);

            ResultSet idCheckRs = ps.executeQuery();
            if (idCheckRs.next()) {
                String multipleNextIdStr = idCheckRs.getString(1);
                multipleNextId = Integer.parseInt(multipleNextIdStr);
                multipleNextId++;
            }
        } catch (Exception e) {
            logger.debug("('" + username + "') Error (executeMultipleUpdate): " + e.getMessage());
        }


        String overallUpdateSql = "";
        int overallIndexCount = 0;
        for (int n = 0; n < typeNum; n++) {
            String paramValueNumInStr = request.getParameter(paramLabel[n] + "_num");
            int paramValueNumIn = Integer.parseInt(paramValueNumInStr);

            for (int i = 0; i < paramValueNumIn; i++) {
                String updateSql = "INSERT INTO " + tablename + " VALUES(";
                //updateSql += "" + multipleNextId + ",";
                updateSql += "?,";
                //updateSql += "" + modid + ",";
                updateSql += "?,";
                //updateSql += "" + pid + ",'" + centerid + "',";
                updateSql += "?,?,";
                if (paramLabel[n].equals("normal_tissue_options")
                        || paramLabel[n].equals("normal_tissue_paraffin_options")
                        || paramLabel[n].equals("normal_tissue_dna_options")) {
                    //updateSql += "'" + lastPageParamTypes.get(overallIndexCount) + "',";
                    updateSql += "?,";
                }
                //updateSql += "'" + lastPageParamValues.get(overallIndexCount) + "'";
                updateSql += "?";
                updateSql += ");";
                multipleNextId++;
                overallUpdateSql = updateSql;
                try {
                    PreparedStatement ps = connection.prepareStatement(updateSql);
                    ps.setInt(1, multipleNextId);
                    ps.setString(2, modid);
                    ps.setString(3, pid);
                    ps.setString(4, centerid);
                    if (paramLabel[n].equals("normal_tissue_options")
                            || paramLabel[n].equals("normal_tissue_paraffin_options")
                            || paramLabel[n].equals("normal_tissue_dna_options")) {
                        //updateSql += "'" + lastPageParamTypes.get(overallIndexCount) + "',";
                        //updateSql += "?,";
                        ps.setString(5, lastPageParamTypes.get(overallIndexCount));
                        ps.setString(6, lastPageParamValues.get(overallIndexCount));
                    } else {
                        ps.setString(5, lastPageParamValues.get(overallIndexCount));
                    }


                    //logger.debug("overallUpdateSql: " + overallUpdateSql);
                    logger.debug("('" + username + "') MULTIPLE RECORD UPDATED - details to go here...");
                    int update = ps.executeUpdate();
                } catch (Exception e) {
                    logger.debug("('" + username + "') Error (executeMultipleUpdate): " + e.getMessage());
                }
                overallIndexCount++;
            }
        }
    }
    
    private Vector<String> getOldParamValues(String pid, String centerid, String modid, Vector<String> lastPageParamNames, String tablename, Connection conn){
        
        Vector<String> oldParamValues = new Vector<String>();
        int paramNum = lastPageParamNames.size();
        
        String tableNameId = this.getTableNameId(tablename);
        
        String sql = "";
        sql += "SELECT ";
        for(int i=0; i<paramNum; i++){
            sql += "" + lastPageParamNames.get(i) + ",";
        }
        sql = sql.trim();
        sql = sql.substring(0,sql.length()-1);
        
        sql += " FROM " + tablename + "";
        sql += " WHERE ensat_id=? AND center_id=? AND " + tableNameId + "=?;";
        
        try{
        
            PreparedStatement ps = conn.prepareStatement(sql);
            ps.setString(1,pid);
            ps.setString(2,centerid);
            ps.setString(3,modid);
            
            ResultSet rs = ps.executeQuery();
            if(rs.next()){
                for(int i=0; i<paramNum; i++){
                    String valueIn = rs.getString(i+1);
                    oldParamValues.add(valueIn);
                }
            }
            
            ps.close();
        
        }catch(Exception e){
            logger.debug("Error (getOldParamValues): " + e.getMessage());
        }
        return oldParamValues;
    }

    public void executeParameterUpdate(String tableId, String modid, String tablename, String pid, String centerid, Vector<Vector> parameters, Connection conn, HttpServletRequest request) {

        Vector<String> lastPageParamNames = new Vector<String>();
        Vector<String> lastPageParamTypes = new Vector<String>();
        Vector<String> lastPageParamValues = new Vector<String>();
        
        int paramSize = parameters.size();

        for (int i = 0; i < paramSize; i++) {
            Vector<String> paramIn = parameters.get(i);
            lastPageParamNames.add(paramIn.get(1));
            lastPageParamTypes.add(paramIn.get(2));
            lastPageParamValues.add(paramIn.get(10));
        }
        
        Vector<String> oldParamValues = this.getOldParamValues(pid,centerid,modid,lastPageParamNames,tablename,conn);

        boolean biomaterialTable =
                tablename.equals("ACC_Biomaterial")
                || tablename.equals("Pheo_Biomaterial")
                || tablename.equals("NAPACA_Biomaterial")
                || tablename.equals("APA_Biomaterial");

        //Have to case for multiples here...        
        //If tablename is multiple, need to run a delete and an insert (rather than an update)
        boolean tableIsMultiple = this.getTableIsMultiple(tablename);

        if (tableIsMultiple) {
            this.executeMultipleUpdate(tableId, modid, tablename, pid, centerid, conn, request);
        } else {

            String updateSql = "UPDATE " + tablename + " SET ";

            int paramNum = lastPageParamNames.size();

            logger.debug("=== RECORD FORM UPDATED ===");

            logger.debug("Ensat ID: " + centerid + "-" + pid);
            logger.debug("Username: " + username);
            logger.debug("Table: " + tablename + ", Form ID: " + modid);
            logger.debug(" ------ ");

            for (int i = 0; i < paramNum; i++) {
                
                String oldParamValue = "";
                if(i < oldParamValues.size()){
                    oldParamValue = oldParamValues.get(i);
                }

                String paramName = lastPageParamNames.get(i);
                boolean parameterIsNormalTissue = this.getNormalTissueParameter(paramName);
                String paramValue = lastPageParamValues.get(i);
                
                paramValue.replaceAll(";", "\\;");

                //Run an update on the aliquots table if it's a biomaterial table
                /*
                 * if (biomaterialTable) { int modidInt =
                 * Integer.parseInt(modid); this.updateAliquotTable(paramName,
                 * paramValue, tablename, modidInt, pid, centerid, request,
                 * conn); this.updateFreezerTable(paramName, paramValue,
                 * tablename, modidInt, pid, centerid, request, conn); }
                 */
                if (biomaterialTable && paramValue.equals("Yes")) {

                    //logger.debug("Updating aliquots and freezer stuff for parameter: " + paramName);
                    int modidInt = Integer.parseInt(modid);
                    this.updateAliquotTable(paramName, paramValue, tablename, modidInt, pid, centerid, request, conn);

                    this.updateFreezerTable(paramName, paramValue, tablename, modidInt, pid, centerid, request, conn);
                } else if (parameterIsNormalTissue) {
                    int modidInt = Integer.parseInt(modid);
                    this.updateAliquotTable(paramName, paramValue, tablename, modidInt, pid, centerid, request, conn);

                    //Get the number of normal tissue selections
                    String normalTissueSelectionNum = request.getParameter("" + paramName + "_num");
                    int normalTissueSelection = 0;
                    try {
                        normalTissueSelection = Integer.parseInt(normalTissueSelectionNum);
                    } catch (NumberFormatException nfe) {
                        logger.debug("Number parsing error: " + nfe.getMessage());
                    }

                    this.runFreezerNTDelete(paramName, tablename, modidInt, pid, centerid, request, conn);

                    //Figure out what the normal tissue selections are
                    String[] normalTissueSelections = new String[normalTissueSelection];
                    for (int j = 0; j < normalTissueSelection; j++) {
                        normalTissueSelections[j] = request.getParameter("" + paramName + "_" + (j + 1));
                        normalTissueSelections[j] = this.getNormalTissueParamLabel(normalTissueSelections[j]);
                        String paramNameNormalTissue = paramName + "_" + normalTissueSelections[j];
                        //System.out.println("paramNameNormalTissue: " + paramNameNormalTissue);
                        this.runFreezerNTUpdate(paramNameNormalTissue, paramValue, tablename, modidInt, pid, centerid, request, conn);
                    }

                    //this.runFreezerNTUpdate(paramName, paramValue, tablename, modidInt, pid, centerid, request, conn);                    
                }

                //Do number conversions for specific parameter names
                /*
                 * if (paramName.equals("gluco_serum_cortisol_napaca") ||
                 * paramName.equals("gluco_plasma_acth_napaca") ||
                 * paramName.equals("gluco_urinary_free_cortisol_napaca") ||
                 * paramName.equals("other_steroid_17hydroxyprogesterone_napaca")
                 * || paramName.equals("other_steroid_serum_dheas_napaca")) {
                 * paramValue = super.napacaUnitConversion(paramName,
                 * paramValue, request); }
                 */

                boolean parameterIsMultiple = this.getParameterIsMultiple(paramName);

                if (!parameterIsMultiple) {
                    //updateSql += "" + paramName + "='" + paramValue + "',";
                    updateSql += "" + paramName + "=?,";
                }

                logger.debug("'" + username + "','" + paramName + "': [old_value: " + oldParamValue + ", new_value: " + paramValue + "]");
                //logger.debug("" + paramName + ": " + paramValue);
            }
            updateSql = updateSql.substring(0, updateSql.length() - 1);

            //updateSql += " WHERE ensat_id=" + pid + " AND center_id='" + centerid + "'";
            updateSql += " WHERE ensat_id=? AND center_id=?";
            updateSql += " AND " + tableId + "=?";


            try {

                PreparedStatement ps = conn.prepareStatement(updateSql);

                int paramCommitCount = 1;
                for (int i = 0; i < paramNum; i++) {

                    String paramName = lastPageParamNames.get(i);
                    String paramType = lastPageParamTypes.get(i);
                    String paramValue = lastPageParamValues.get(i);
                    
                    paramValue.replaceAll(";", "\\;");

                    //Do number conversions for specific parameter names
                    if (paramName.equals("gluco_serum_cortisol_napaca")
                            || paramName.equals("gluco_plasma_acth_napaca")
                            || paramName.equals("gluco_urinary_free_cortisol_napaca")
                            || paramName.equals("other_steroid_17hydroxyprogesterone_napaca")
                            || paramName.equals("other_steroid_serum_dheas_napaca")) {
                        paramValue = super.napacaUnitConversion(paramName, paramValue, request);
                    }
                    boolean parameterIsMultiple = this.getParameterIsMultiple(paramName);
                    if (!parameterIsMultiple) {
                        if (paramType.equals("date")) {
                            paramValue = this.reformatDateValue(paramValue);
                        }
                        ps.setString(paramCommitCount, paramValue);
                        paramCommitCount++;
                    }
                }
                ps.setString(paramCommitCount, pid);
                ps.setString(paramCommitCount + 1, centerid);
                ps.setString(paramCommitCount + 2, modid);

                int update = ps.executeUpdate();
            } catch (Exception e) {
                logger.debug("('" + username + "') Error (executeParameterUpdate): " + e.getMessage());
            }

            logger.debug("=====");
        }

    }

    public Vector<Vector> getMainParameters(String[] _tablenames, String pid, String centerid, Connection conn, Connection paramConn) {

        //System.out.println("TEST 2...");

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

        //System.out.println("sql: " + sql);        

        Vector<Vector> parameters = new Vector<Vector>();
        try {

            PreparedStatement ps = paramConn.prepareStatement(sql);
            ps.setString(1, tablenames[0]);
            if (tablenames.length > 1) {
                for (int i = 1; i < tablenames.length; i++) {
                    //sql += " OR param_table='" + tablenames[i] + "'";
                    //sql += " OR param_table=?";
                    ps.setString(i + 1, tablenames[i]);
                }
            }

            ResultSet rs = ps.executeQuery();

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
            logger.debug("('" + username + "') Error (getMainParameters): " + e.getMessage());
        }

        //Now add the values to the parameter vector and return
        parameters = this.getMainParameterValues(tablenames, parameters, pid, centerid, conn);

        //System.out.println("parameters.size(): " + parameters.size());
        return parameters;
    }

    public Vector<Vector> getMainParameterValues(String[] tablenames, Vector<Vector> parameters, String pid, String centerid, Connection connValues) {

        int paramNum = parameters.size();
        int tableNum = tablenames.length;
        //System.out.println("tableNum (getMainParameterValues): " + tableNum);
        //System.out.println("paramNum (getMainParameterValues): " + paramNum);
        String sql = "";
        sql += "SELECT ";
        for (int i = 0; i < paramNum; i++) {
            Vector<String> parameterIn = parameters.get(i);
            //System.out.println("parameterIn.get(1): " + parameterIn.get(1));
            if (!parameterIn.get(1).equals("system_organ") && !parameterIn.get(1).equals("presentation_first_tumor") && !parameterIn.get(1).equals("imaging") && !parameterIn.get(1).equals("associated_studies")
                     && !parameterIn.get(1).equals("hormone_symptoms") && !parameterIn.get(1).equals("tumor_symptoms") && !parameterIn.get(1).equals("first_diagnosis_tnm") && !parameterIn.get(1).equals("malignant_diagnosis_tnm")) {
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

        if (tableNum > 1) {
            for (int i = 1; i < tableNum; i++) {
                sql += "Identification.ensat_id=" + tablenames[i] + ".ensat_id AND ";
                sql += "Identification.center_id=" + tablenames[i] + ".center_id AND ";
            }
        }
        sql = sql.substring(0, sql.length() - 4);
        //sql += " AND Identification.ensat_id=" + pid + " AND Identification.center_id='" + centerid + "';";
        sql += " AND Identification.ensat_id=? AND Identification.center_id=?;";

        //System.out.println("sql (getMainParameterValues): " + sql);

        Vector<Vector> parametersOut = new Vector<Vector>();
        try {
            PreparedStatement ps = connValues.prepareStatement(sql);
            ps.setString(1, pid);
            ps.setString(2, centerid);

            ResultSet rs = ps.executeQuery();

            while (rs.next()) {
                int paramCount = 0;
                for (int i = 0; i < paramNum; i++) {

                    Vector<String> parameterIn = parameters.get(i);
                    if (!parameterIn.get(1).equals("system_organ") && !parameterIn.get(1).equals("presentation_first_tumor") && !parameterIn.get(1).equals("imaging") && !parameterIn.get(1).equals("associated_studies")) {
                        String valueIn = rs.getString(paramCount + 1);
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
            logger.debug("('" + username + "') Error (getMainParameterValues): " + e.getMessage());
        }

        //System.out.println("parametersOut.size(): " + parametersOut.size());

        return parametersOut;
    }

    public String getMainParameterHtml(Vector<Vector> parameters, String lineColour, String dbn) {

        //System.out.println("TEST 3...");

        String outputStr = "";

        //Get the number of rows here        
        int paramNum = parameters.size();

        //System.out.println("paramNum: " + paramNum);

        //For each row
        for (int i = 0; i < paramNum; i++) {

            Vector<String> rowIn = parameters.get(i);

            if (rowIn.get(1).equals("eurine_act_inclusion")
                    && (dbn.equals("Pheo") || dbn.equals("APA"))) {
                //Blank this clause
            } else {

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
            } else if (modality.equals("metabolomics")) {
                pageTitle = "Steroid Metabolomics";
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
            } else if (modality.equals("complications")) {
                pageTitle = "Complications";
            } else if (modality.equals("genetics")) {
                pageTitle = "Genetics";
            }



        }
        return pageTitle;

    }

    public String getSubTablename(String modality, String dbn) {

        String tablenameOut = "";
        if (dbn.equals("ACC")) {
            if (modality.equals("biomaterial")) {
                tablenameOut = "ACC_Biomaterial";
            } else if (modality.equals("chemoembolisation")) {
                tablenameOut = "ACC_Chemoembolisation";
            } else if (modality.equals("chemotherapy")) {
                tablenameOut = "ACC_Chemotherapy";
            } else if (modality.equals("followup")) {
                tablenameOut = "ACC_FollowUp";
            } else if (modality.equals("mitotane")) {
                tablenameOut = "ACC_Mitotane";
            } else if (modality.equals("pathology")) {
                tablenameOut = "ACC_Pathology";
            } else if (modality.equals("radiofrequency")) {
                tablenameOut = "ACC_Radiofrequency";
            } else if (modality.equals("radiotherapy")) {
                tablenameOut = "ACC_Radiotherapy";
            } else if (modality.equals("surgery")) {
                tablenameOut = "ACC_Surgery";
            } else if (modality.equals("metabolomics")) {
                tablenameOut = "ACC_Metabolomics";
            }
        } else if (dbn.equals("Pheo")) {
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
                tablenameOut = "Pheo_Surgery_Procedure";
            } else if (modality.equals("morphological_progression")) {
                tablenameOut = "Pheo_Morphological_Progression";
            } else if (modality.equals("biological_assessment")) {
                tablenameOut = "Pheo_Biological_Assessment";
            }
            
        } else if (dbn.equals("NAPACA")) {
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
        } else if (dbn.equals("APA")) {
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
            } else if (modality.equals("genetics")) {
                tablenameOut = "APA_Genetics";
            }
        }

        return tablenameOut;
    }

    public String getSubTableIdName(String modality, String dbn) {

        String tableIdOut = "";
        if (dbn.equals("Pheo")) {
            if (modality.equals("clinical")
                    || modality.equals("biochemical")) {
                tableIdOut = "" + modality + "_assessment_id";
            } else if (modality.equals("imaging")) {
                tableIdOut = dbn.toLowerCase() + "_" + modality + "_tests_id";
            } else if (modality.equals("interventions")) {
                tableIdOut = dbn.toLowerCase() + "_non_surgical_" + modality + "_id";
            } else if (modality.equals("tumordetails")) {
                tableIdOut = dbn.toLowerCase() + "_tumor_details_id";
            } else if (modality.equals("surgical_procedures")) {
                tableIdOut = dbn.toLowerCase() + "_surgery_procedure_id";                
            } else if (modality.equals("morphological_progression")) {
                tableIdOut = dbn.toLowerCase() + "_morphprog_id";                
            } else if (modality.equals("biological_assessment")) {
                tableIdOut = dbn.toLowerCase() + "_biologassess_id";                
            } else {
                tableIdOut = dbn.toLowerCase() + "_" + modality + "_id";
            }
        } else if (dbn.equals("APA")) {
            if (modality.equals("clinical")
                    || modality.equals("biochemical")) {
                tableIdOut = "" + modality + "_assessment_id";
            } else {
                tableIdOut = dbn.toLowerCase() + "_" + modality + "_id";
            }
        } else {
            tableIdOut = dbn.toLowerCase() + "_" + modality + "_id";
        }
        return tableIdOut;
    }

    public String getMultipleSubTableIdName(String tablename) {

        String idLabel = "";
        if (tablename.equals("ACC_Chemotherapy_Regimen")) {
            idLabel = "acc_chemotherapy_regimen_id";
        } else if (tablename.equals("ACC_FollowUp_Organs")) {
            idLabel = "acc_followup_organs_id";
        } else if (tablename.equals("ACC_Radiofrequency_Loc")) {
            idLabel = "acc_radiofrequency_loc_id";
        } else if (tablename.equals("ACC_Radiotherapy_Loc")) {
            idLabel = "acc_radiotherapy_loc_id";
        } else if (tablename.equals("Pheo_MetastasesLocation")) {
            idLabel = "pheo_metastases_loc_id";
        } else if (tablename.equals("Pheo_ImagingTests_CTLoc")) {
            idLabel = "pheo_imaging_ctloc_id";
        } else if (tablename.equals("Pheo_ImagingTests_NMRLoc")) {
            idLabel = "pheo_imaging_ctloc_id";
        } else if (tablename.equals("Pheo_Surgery_PreOp")) {
            idLabel = "pheo_surgery_preop_id";
        } else if (tablename.equals("Pheo_Surgery_IntraOp")) {
            idLabel = "pheo_surgery_intraop_id";
        }/* else if (tablename.equals("Pheo_Surgery_Procedure")) {
            idLabel = "pheo_surgery_procedure_id";
        }*/ else if (tablename.equals("ACC_Surgery_Extended")) {
            idLabel = "acc_surgery_extended_id";
        } else if (tablename.equals("ACC_Surgery_First")) {
            idLabel = "acc_surgery_first_id";
        } else if (tablename.equals("ACC_Biomaterial_Normal_Tissue")) {
            idLabel = "acc_biomaterial_normal_tissue_id";
        } else if (tablename.equals("Pheo_Biomaterial_Normal_Tissue")) {
            idLabel = "pheo_biomaterial_normal_tissue_id";
        } else if (tablename.equals("NAPACA_Biomaterial_Normal_Tissue")) {
            idLabel = "napaca_biomaterial_normal_tissue_id";
        } else if (tablename.equals("APA_Biomaterial_Normal_Tissue")) {
            idLabel = "apa_biomaterial_normal_tissue_id";
        }
        return idLabel;
    }

    public String getAssocInfoHtml(String dbid, String dbn, String centerid, String pid, Connection conn, String modality) {

        String outputStr = "";

        outputStr += super.getAssocInfoHeaderHtml(modality);

        outputStr += this.getAssocInfoBodyHtml(dbid, dbn, centerid, pid, conn, modality, false);

        return outputStr;
    }

    public String getAssocInfoHtmlReadonly(String dbid, String dbn, String centerid, String pid, Connection conn, String modality) {

        String outputStr = "";

        outputStr += super.getAssocInfoHeaderHtml(modality);

        outputStr += this.getAssocInfoBodyHtml(dbid, dbn, centerid, pid, conn, modality, true);

        return outputStr;
    }

    private String getAssocInfoBodyHtml(String dbid, String dbn, String centerid, String pid, Connection conn, String modality, boolean readonly) {

        String outputStr = "";
        ResultSet rs = null;
        int outputCount = 0;
        int columnNum = 4;

        outputStr += "<tbody>";

        String[] listQueryTables = null;
        String[] listQueryInfoOutput = null;
        String[] listQueryModalities = null;
        String[] listQueryDatenames = null;

        if (dbn.equals("ACC")) {

            listQueryTables = new String[10];
            listQueryInfoOutput = new String[10];
            listQueryModalities = new String[10];
            listQueryDatenames = new String[10];

            listQueryTables[0] = "ACC_Biomaterial";
            listQueryTables[1] = "ACC_Chemoembolisation";
            listQueryTables[2] = "ACC_Chemotherapy";
            listQueryTables[3] = "ACC_FollowUp";
            listQueryTables[4] = "ACC_Mitotane";
            listQueryTables[5] = "ACC_Pathology";
            listQueryTables[6] = "ACC_Radiofrequency";
            listQueryTables[7] = "ACC_Radiotherapy";
            listQueryTables[8] = "ACC_Surgery";
            listQueryTables[9] = "ACC_Metabolomics";

            listQueryInfoOutput[0] = "Biomaterial";
            listQueryInfoOutput[1] = "Chemoembolisation";
            listQueryInfoOutput[2] = "Chemotherapy";
            listQueryInfoOutput[3] = "Follow-Up";
            listQueryInfoOutput[4] = "Mitotane";
            listQueryInfoOutput[5] = "Pathology";
            listQueryInfoOutput[6] = "Radiofrequency";
            listQueryInfoOutput[7] = "Radiotherapy";
            listQueryInfoOutput[8] = "Surgery";
            listQueryInfoOutput[9] = "Steroid Metabolomics";

            listQueryModalities[0] = "biomaterial";
            listQueryModalities[1] = "chemoembolisation";
            listQueryModalities[2] = "chemotherapy";
            listQueryModalities[3] = "followup";
            listQueryModalities[4] = "mitotane";
            listQueryModalities[5] = "pathology";
            listQueryModalities[6] = "radiofrequency";
            listQueryModalities[7] = "radiotherapy";
            listQueryModalities[8] = "surgery";
            listQueryModalities[9] = "metabolomics";

            listQueryDatenames[0] = "biomaterial_date";
            listQueryDatenames[1] = "chemoembolisation_date";
            listQueryDatenames[2] = "chemotherapy_initiation";
            listQueryDatenames[3] = "followup_date";
            listQueryDatenames[4] = "mitotane_initiation";
            listQueryDatenames[5] = "pathology_date";
            listQueryDatenames[6] = "radiofrequency_date";
            listQueryDatenames[7] = "radiotherapy_date";
            listQueryDatenames[8] = "surgery_date";
            listQueryDatenames[9] = "metabolomics_date";

        } else if (dbn.equals("APA")) {

            listQueryTables = new String[9];
            listQueryInfoOutput = new String[9];
            listQueryModalities = new String[9];
            listQueryDatenames = new String[9];

            listQueryTables[0] = "APA_BiochemicalAssessment";
            listQueryTables[1] = "APA_Imaging";
            listQueryTables[2] = "APA_Cardio";
            listQueryTables[3] = "APA_Complication";
            listQueryTables[4] = "APA_ClinicalAssessment";
            listQueryTables[5] = "APA_Biomaterial";
            listQueryTables[6] = "APA_Surgery";
            listQueryTables[7] = "APA_FollowUp";
            listQueryTables[8] = "APA_Genetics";

            listQueryInfoOutput[0] = "Biochemical Assessment";
            listQueryInfoOutput[1] = "Imaging";
            listQueryInfoOutput[2] = "Cardio Events";
            listQueryInfoOutput[3] = "Complications";
            listQueryInfoOutput[4] = "Clinical Assessment";
            listQueryInfoOutput[5] = "Biomaterial";
            listQueryInfoOutput[6] = "Surgery";
            listQueryInfoOutput[7] = "Follow-Up";
            listQueryInfoOutput[8] = "Genetics";

            listQueryModalities[0] = "biochemical";
            listQueryModalities[1] = "imaging";
            listQueryModalities[2] = "cardio";
            listQueryModalities[3] = "complications";
            listQueryModalities[4] = "clinical";
            listQueryModalities[5] = "biomaterial";
            listQueryModalities[6] = "surgery";
            listQueryModalities[7] = "followup";
            listQueryModalities[8] = "genetics";

            listQueryDatenames[0] = "assessment_date";
            listQueryDatenames[1] = "imaging_date";
            listQueryDatenames[2] = "event_date";
            listQueryDatenames[3] = "event_date";
            listQueryDatenames[4] = "assessment_date";
            listQueryDatenames[5] = "biomaterial_date";
            listQueryDatenames[6] = "intervention_date";
            listQueryDatenames[7] = "followup_date";
            listQueryDatenames[8] = "apa_genetics_date";

        } else if (dbn.equals("NAPACA")) {

            listQueryTables = new String[6];
            listQueryInfoOutput = new String[6];
            listQueryModalities = new String[6];
            listQueryDatenames = new String[6];

            listQueryTables[0] = "NAPACA_Imaging";
            listQueryTables[1] = "NAPACA_Pathology";
            listQueryTables[2] = "NAPACA_Biomaterial";
            listQueryTables[3] = "NAPACA_Surgery";
            listQueryTables[4] = "NAPACA_FollowUp";
            listQueryTables[5] = "NAPACA_Metabolomics";

            listQueryInfoOutput[0] = "Imaging";
            listQueryInfoOutput[1] = "Pathology";
            listQueryInfoOutput[2] = "Biomaterial";
            listQueryInfoOutput[3] = "Surgery";
            listQueryInfoOutput[4] = "Follow-Up";
            listQueryInfoOutput[5] = "Steroid Metabolomics";

            listQueryModalities[0] = "imaging";
            listQueryModalities[1] = "pathology";
            listQueryModalities[2] = "biomaterial";
            listQueryModalities[3] = "surgery";
            listQueryModalities[4] = "followup";
            listQueryModalities[5] = "metabolomics";

            listQueryDatenames[0] = "imaging_date";
            listQueryDatenames[1] = "pathology_date";
            listQueryDatenames[2] = "biomaterial_date";
            listQueryDatenames[3] = "surgery_date";
            listQueryDatenames[4] = "followup_date";
            listQueryDatenames[5] = "metabolomics_date";

        } else if (dbn.equals("Pheo")) {

            listQueryTables = new String[12];
            listQueryInfoOutput = new String[12];
            listQueryModalities = new String[12];
            listQueryDatenames = new String[9];

            listQueryTables[0] = "Pheo_BiochemicalAssessment";
            listQueryTables[1] = "Pheo_ImagingTests";
            listQueryTables[2] = "Pheo_NonSurgicalInterventions";
            listQueryTables[3] = "Pheo_ClinicalAssessment";
            listQueryTables[4] = "Pheo_Biomaterial";
            listQueryTables[5] = "Pheo_Surgery";
            listQueryTables[6] = "Pheo_FollowUp";
            listQueryTables[7] = "Pheo_Genetics";
            listQueryTables[8] = "Pheo_TumorDetails";            
            listQueryTables[9] = "Pheo_Surgery_Procedure";
            listQueryTables[10] = "Pheo_Morphological_Progression";
            listQueryTables[11] = "Pheo_Biological_Assessment";

            listQueryInfoOutput[0] = "Biochemical Assessment";
            listQueryInfoOutput[1] = "Imaging";
            listQueryInfoOutput[2] = "Non-Surgical Interventions";
            listQueryInfoOutput[3] = "Clinical Assessment";
            listQueryInfoOutput[4] = "Biomaterial";
            listQueryInfoOutput[5] = "Surgery / Pathology";
            listQueryInfoOutput[6] = "Follow-Up";
            listQueryInfoOutput[7] = "Genetics";
            listQueryInfoOutput[8] = "Tumor Details";
            listQueryInfoOutput[9] = "Surgery Procedures";
            listQueryInfoOutput[10] = "Morphological Progression";
            listQueryInfoOutput[11] = "Biological Assessment";
            
            listQueryModalities[0] = "biochemical";
            listQueryModalities[1] = "imaging";
            listQueryModalities[2] = "interventions";
            listQueryModalities[3] = "clinical";
            listQueryModalities[4] = "biomaterial";
            listQueryModalities[5] = "surgery";
            listQueryModalities[6] = "followup";
            listQueryModalities[7] = "genetics";
            listQueryModalities[8] = "tumordetails";
            listQueryModalities[9] = "surgical_procedures";
            listQueryModalities[10] = "morphological_progression";
            listQueryModalities[11] = "biological_assessment";

            listQueryDatenames[0] = "assessment_date";
            listQueryDatenames[1] = "biomaterial_date";
            listQueryDatenames[2] = "surgery_date";
            listQueryDatenames[3] = "followup_date";
            listQueryDatenames[4] = "genetics_date";
            listQueryDatenames[5] = "tumor_date";            
            listQueryDatenames[6] = "first_date";
            listQueryDatenames[7] = "baseline_imaging_date";
            listQueryDatenames[8] = "assessment_date";
        }

        int outputNum = 0;
        String[][] outputHtml = null;

        try {

            //Find the appropriate modality index
            int modalityIndex = -1;
            boolean modalityFound = false;
            int modalityCount = 0;
            while (!modalityFound && modalityCount < listQueryModalities.length) {
                if (listQueryModalities[modalityCount].equals(modality)) {
                    modalityFound = true;
                    modalityIndex = modalityCount;
                } else {
                    modalityCount++;
                }
            }


            String listQuerySql = "";
            if (dbn.equals("Pheo")) {
                if (modalityIndex < 3) {
                    //listQuerySql = "SELECT * FROM " + listQueryTables[modalityIndex] + " WHERE ensat_id=" + pid + " AND center_id='" + centerid + "';";
                    listQuerySql = "SELECT * FROM " + listQueryTables[modalityIndex] + " WHERE ensat_id=? AND center_id=?;";
                } else {
                    listQuerySql = "SELECT * FROM " + listQueryTables[modalityIndex] + " WHERE ensat_id=? AND center_id=?" + " ORDER BY " + listQueryDatenames[modalityIndex - 3] + " DESC;";
                }
            } else {
                listQuerySql = "SELECT * FROM " + listQueryTables[modalityIndex] + " WHERE ensat_id=? AND center_id=?" + " ORDER BY " + listQueryDatenames[modalityIndex] + " DESC;";
            }

            PreparedStatement ps = conn.prepareStatement(listQuerySql);
            ps.setString(1, pid);
            ps.setString(2, centerid);

            //System.out.println("listQuerySql: " + listQuerySql);

            rs = ps.executeQuery();
            while (rs.next()) {
                outputNum++;
            }
            outputHtml = new String[outputNum][2];

            rs = ps.executeQuery();

            String modid = "";
            String ensatid = "";
            while (rs.next()) {

                outputHtml[outputCount][0] = "<tr>";
                modid = rs.getString(1);
                ensatid = rs.getString(2);
                ensatid = super.formatEnsatId(ensatid);
                centerid = rs.getString(3);
                for (int i = 0; i < columnNum; i++) {

                    if (i == 0) {
                        outputHtml[outputCount][0] += "<td>" + centerid + "-" + ensatid + "</td>";
                    } else if (i == 1) {
                        outputHtml[outputCount][0] += "<td>" + modid + "</td>";
                    } else if (i == 2) {

                        String inputDate = "";

                        boolean multipleDates = modalityIndex < 3 && dbn.equals("Pheo");

                        if (multipleDates) {
                            //inputDate = "";      
                            String[] inputDates = null;
                            int[] inputIndex = null;
                            if (modalityIndex == 0) {
                                //Dates are on 5, 9, 11, 14, 17, 19 - biochemical
                                inputIndex = new int[6];
                                inputIndex[0] = 5;
                                inputIndex[1] = 9;
                                inputIndex[2] = 11;
                                inputIndex[3] = 14;
                                inputIndex[4] = 17;
                                inputIndex[5] = 19;
                            } else if (modalityIndex == 1) {
                                //Dates are on 4,8,12,16,20,24,27,32 - imaging
                                inputIndex = new int[8];
                                inputIndex[0] = 5;//4
                                inputIndex[1] = 10;//8
                                inputIndex[2] = 14;//12
                                inputIndex[3] = 18;//16
                                inputIndex[4] = 22;//20
                                inputIndex[5] = 26;//24
                                inputIndex[6] = 29;//27
                                inputIndex[7] = 34;//32
                            } else if (modalityIndex == 2) {
                                //Dates are on 4,7,10,11,14,17,18,21,22,25 - non-surgical interventions
                                inputIndex = new int[10];
                                inputIndex[0] = 4;
                                inputIndex[1] = 7;
                                inputIndex[2] = 10;
                                inputIndex[3] = 11;
                                inputIndex[4] = 14;
                                inputIndex[5] = 17;
                                inputIndex[6] = 18;
                                inputIndex[7] = 21;
                                inputIndex[8] = 22;
                                inputIndex[9] = 25;
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
                            while (!dateFound && dateCount < inputIndex.length) {
                                StringTokenizer st = new StringTokenizer(inputDates[dateCount], "-");
                                dateFound = st.countTokens() >= 3;
                                dateCount++;
                            }
                            if (dateFound) {
                                inputDate = inputDates[dateCount - 1];
                            }

                        } else { //End of multipleDates clause

                            if (listQueryModalities[modalityIndex].equals("chemotherapy") || listQueryModalities[modalityIndex].equals("mitotane")) {
                                inputDate = rs.getString(5);
                            } else {
                                inputDate = rs.getString(4);
                            }
                        }

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

                        //logger.debug("outputHtml: " + outputHtml[outputCount][0]);

                    } else {

                        //These are now the informational columns (specific to the modality)
                        if (modality.equals("biomaterial")) {
                            String study = rs.getString(20);
                            outputHtml[outputCount][0] += "<td>" + study + "</td>";
                            outputHtml[outputCount][0] += this.processBiomaterialTypes(rs);
                        } else {
                            outputHtml[outputCount][0] += "<td>" + listQueryInfoOutput[modalityIndex] + "</td>";
                        }

                        /*
                         * if (i == 3) { outputHtml[outputCount][0] += "<td>";
                         * if(modality.equals("biomaterial")){
                         * outputHtml[outputCount][0] +=
                         * this.getBiomaterialLabels(rs); }else{
                         * outputHtml[outputCount][0] +=
                         * listQueryInfoOutput[modalityIndex]; }
                         * outputHtml[outputCount][0] += "</td>"; }
                         */
                    }
                }
                String readonlyStr = "";
                if (readonly) {
                    readonlyStr = "readonly";
                } else {
                    readonlyStr = "detail";
                }
                outputHtml[outputCount][0] += "<td><a href='./jsp/modality/read/" + readonlyStr + ".jsp?dbid=" + dbid + "&dbn=" + dbn + "&pid=" + pid + "&centerid=" + centerid + "&modality=" + listQueryModalities[modalityIndex] + "&modid=" + modid + "'>Detail</a></td>";
                outputHtml[outputCount][0] += "<td>";
                if (!readonly) {
                    if (modality.equals("biomaterial")) {
                        outputHtml[outputCount][0] += "<a href='./jsp/modality/read/print_label.jsp?dbid=" + dbid + "&dbn=" + dbn + "&pid=" + pid + "&centerid=" + centerid + "&modid=" + modid + "'>Label</a>";
                        outputHtml[outputCount][0] += "</td>";
                        outputHtml[outputCount][0] += "<td>";
                    }
                    outputHtml[outputCount][0] += "<a href='./jsp/modality/delete/delete_view.jsp?dbid=" + dbid + "&dbn=" + dbn + "&pid=" + pid + "&centerid=" + centerid + "&modality=" + listQueryModalities[modalityIndex] + "&modid=" + modid + "'>Delete</a>";
                }
                outputHtml[outputCount][0] += "</td>";
                outputHtml[outputCount][0] += "</tr>";
                outputCount++;

            }
            rs.close();
        } catch (Exception e) {
            logger.debug("('" + username + "') Error (date listing): " + e.getMessage());
        }

        String[] htmlOutputStr = new String[outputNum];
        java.util.Date[] htmlDates = new java.util.Date[outputNum];
        DateFormat formatter = new SimpleDateFormat("yyyy-MM-dd");
        try {
            //logger.debug("outputNum: " + outputNum);
            for (int i = 0; i < outputNum; i++) {
                if (outputHtml[i][1] == null) {
                    outputHtml[i][1] = "";
                }
                if (!outputHtml[i][1].equals("--") && !outputHtml[i][1].equals("")) {
                    htmlDates[i] = (java.util.Date) formatter.parse(outputHtml[i][1]);
                } else {
                    htmlDates[i] = (java.util.Date) formatter.parse("1900-01-01");
                }
                htmlOutputStr[i] = outputHtml[i][0];
            }
            /*
             * for (int i = 0; i < outputNum; i++) { if (outputHtml[i][1] ==
             * null) { outputHtml[i][1] = "1900-01-01"; } htmlDates[i] =
             * (java.util.Date) formatter.parse(outputHtml[i][1]);
             * htmlOutputStr[i] = outputHtml[i][0]; }
             */
        } catch (Exception e) {
            logger.debug("('" + username + "') Error (sorting): " + e.getMessage());
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

    public String getParameterReadonlyHtml(Vector<Vector> parameters, String lineColour) {

        String outputStr = "";

        //Get the number of rows here        
        int paramNum = parameters.size();

        //For each row
        for (int i = 0; i < paramNum; i++) {

            Vector<String> rowIn = parameters.get(i);

            String paramName = rowIn.get(1);

            outputStr += this.getIndividualReadonlyParameterHtml(rowIn, lineColour, i);
        }
        return outputStr;
    }

    private String getIndividualReadonlyParameterHtml(Vector<String> rowIn, String lineColour, int index) {

        String outputStr = "";

        //Case for an empty value vector here
        String valueIn = "";
        if (rowIn.get(10) != null) {
            valueIn = rowIn.get(10);
        }

        outputStr += "<tr ";
        if (index % 2 != 0) {
            outputStr += lineColour;
        }
        outputStr += ">";
        outputStr += "<td width=\"50%\">";
        outputStr += rowIn.get(4) + ":";
        outputStr += "</td>";
        outputStr += "<td>";

        outputStr += "<strong>" + valueIn + "</strong>";

        outputStr += "</td>";
        outputStr += "</tr>";

        return outputStr;
    }

    public String getPrintLabelHtml(Vector<Vector> parameters, String lineColour, String dateValue, String formid, String dbn, Statement stmt) {

        int paramNum = parameters.size();
        String outputStr = "";
        
        dateValue = this.convertDate(dateValue);

        outputStr += "<tr>";
        outputStr += "<td colspan='2'><div align='center'>Date: ";
        outputStr += "<strong>" + dateValue + "</strong></div></td>";
        outputStr += "</tr>";

        outputStr += "<tr>";
        outputStr += "<th><div align='center'>Sample</div></th>";
        outputStr += "<th><div align='center'>Number of labels required</div></th>";
        outputStr += "</tr>";


        //System.out.println("paramNum (getPrintLabelNum): " + paramNum);

        for (int i = 0; i < paramNum; i++) {

            Vector<String> paramIn = parameters.get(i);
            logger.debug("paramIn: " + paramIn);
            String paramName = paramIn.get(0);
            String paramLabel = paramIn.get(1);
            String aliquotNum = paramIn.get(3);

            //logger.debug("paramName: " + paramName);

            if (!paramName.equals("tumor_tissue_ensat_sop")) {

                boolean paramIsMultiple = paramName.equals("normal_tissue")
                        || paramName.equals("normal_tissue_dna")
                        || paramName.equals("normal_tissue_paraffin");

                //System.out.println("paramIsMultiple: " + paramIsMultiple);

                int aliquotNumber = 0;
                try {
                    aliquotNumber = Integer.parseInt(aliquotNum);
                } catch (NumberFormatException nfe) {
                    logger.debug("('" + username + "') NumberFormatException: " + nfe.getMessage());
                }

                //Retrieve the multiple values if paramIsMultiple is true
                if (paramIsMultiple) {
                    String sql = "SELECT * FROM " + dbn + "_Biomaterial_Normal_Tissue WHERE " + dbn.toLowerCase() + "_biomaterial_id=" + formid + ";";

                    logger.debug("sql (paramIsMultiple - getPrintLabelHtml): " + sql);
                    
                    Vector<String> multValues = new Vector<String>();
                    try {
                        ResultSet rs = stmt.executeQuery(sql);

                        while (rs.next()) {
                            String materialType = rs.getString(5);
                            if ((materialType.equals("frozen") && paramName.equals("normal_tissue"))
                                    || (materialType.equals("paraffin") && paramName.equals("normal_tissue_paraffin"))
                                    || (materialType.equals("dna") && paramName.equals("normal_tissue_dna"))) {
                                multValues.add(rs.getString(6));
                            }
                        }
                    } catch (Exception e) {
                        logger.debug("('" + username + "') Error (getPrintLabelHtml): " + e.getMessage());
                    }

                    for (int m = 0; m < multValues.size(); m++) {
                        outputStr += "<tr";
                        if (i % 2 != 0) {
                            outputStr += " " + lineColour + " ";
                        }
                        outputStr += ">";
                        outputStr += "<td><div align='center'>" + paramLabel + "<br/> - " + multValues.get(m) + "</div></td>";
                        outputStr += "<td><div align='center'><select name='aliquot_" + paramName + "_" + multValues.get(m).toLowerCase() + "'>";
                        for (int k = 0; k < 10; k++) {
                            outputStr += "<option ";
                            if (aliquotNumber == k) {
                                outputStr += "selected";
                            }
                            outputStr += " value='" + k + "'>" + k + "</option>";
                        }
                        outputStr += "</select></div></td>";

                        outputStr += "</tr>";
                    }

                } else {

                    outputStr += "<tr";
                    if (i % 2 != 0) {
                        outputStr += " " + lineColour + " ";
                    }
                    outputStr += ">";
                    outputStr += "<td><div align='center'>" + paramLabel + "</div></td>";
                    outputStr += "<td><div align='center'><select name='aliquot_" + paramName + "'>";
                    for (int k = 0; k < 10; k++) {
                        outputStr += "<option ";
                        if (aliquotNumber == k) {
                            outputStr += "selected";
                        }
                        outputStr += " value='" + k + "'>" + k + "</option>";
                    }

                }
                outputStr += "</select></div></td>";

                outputStr += "</tr>";
            }
        }

        return outputStr;
    }

    public ResultSet getAliquotInfo(String pid, String centerid, String modid, String dbn, Statement stmt) {

        String aliquotSql = "";
        ResultSet rs = null;
        try {
            aliquotSql = "SELECT * FROM " + dbn + "_Biomaterial_Aliquots WHERE ensat_id=" + pid + " AND center_id='" + centerid + "' AND " + dbn.toLowerCase() + "_biomaterial_id=" + modid + ";";
            rs = stmt.executeQuery(aliquotSql);
        } catch (Exception e) {
            logger.debug("('" + username + "') Error (getAliquotInfo): " + e.getMessage());
        }

        return rs;
    }

    public Vector<Vector> getLabelInfo(ResultSet rs, Vector<Vector> parameters) {

        //Need to extract the name and value of the actual biomaterial parameters
        int paramNum = parameters.size();

        //logger.debug("paramNum (getLabelInfo): " + paramNum);

        Vector<Vector> labelParameters = new Vector<Vector>();

        for (int i = 0; i < paramNum; i++) {
            Vector<String> paramIn = parameters.get(i);

            //Get the value, if it equals "Yes", then add to the list
            String paramValue = paramIn.get(10);
            String paramName = paramIn.get(1);
            if (paramValue.equals("Yes")) {
                if(!paramName.equals("normal_tissue")
                        && !paramName.equals("normal_tissue_paraffin")
                        && !paramName.equals("normal_tissue_dna")){
                    Vector<String> labelParam = new Vector<String>();
                    labelParam.add(paramIn.get(1)); //Parameter name
                    labelParam.add(paramIn.get(4)); //Parameter label
                    labelParam.add(paramValue); //Parameter value
                    labelParameters.add(labelParam); //Add to collection
                }
            }else if((paramName.equals("normal_tissue_options") ||
              paramName.equals("normal_tissue_paraffin_options") ||
              paramName.equals("normal_tissue_dna_options"))){
                
                StringTokenizer st = new StringTokenizer(paramValue,"|");
                int tokenNum = st.countTokens();
                for(int j=0; j<tokenNum; j++){
                    String multParamValue = st.nextToken();
                    String multParamName = "";
                    if(paramName.equals("normal_tissue")){  
                        multParamName = "normal_tissue_options";
                    }else{
                        multParamName = paramName;
                    }
                    multParamName = paramName + "_" + multParamValue.toLowerCase();
                    Vector<String> labelParam = new Vector<String>(); 
                    labelParam.add(multParamName); //Parameter
                    String multParamLabel = paramIn.get(4);
                    multParamLabel = multParamLabel.replace("Specific","Normal");
                    labelParam.add(multParamLabel + " - " + multParamValue); //Parameter label
                    labelParam.add(multParamValue);
                    labelParameters.add(labelParam); //Add to collection                 
                }
            }
        }

        int labelParamNum = labelParameters.size();
        try {

            while (rs.next()) {
                //System.out.println("Test (getLabelInfo)...");
                String labelParameter = rs.getString(5);
                //logger.debug("labelParameter: " + labelParameter);

                boolean paramFound = false;
                int paramCount = 0;
                while (!paramFound && paramCount < labelParamNum) {
                    Vector<String> paramIn = labelParameters.get(paramCount);

                    //Look up the aliquot resultset and match on the parameter name
                    if (labelParameter.equals(paramIn.get(0))) {
                        //System.out.println("aliquot found...");
                        String aliquotNumber = rs.getString(6);
                        //System.out.println("aliquot number: " + aliquotNumber);
                        paramIn.add(aliquotNumber);
                        labelParameters.set(paramCount, paramIn);
                        paramFound = true;
                    } else {
                        paramCount++;
                    }
                }
            }
        } catch (Exception e) {
            logger.debug("('" + username + "') Error (getLabelInfo): " + e.getMessage());
        }

        //System.out.println("labelParameters.size(): " + labelParameters.size());

        //For each entry in labelParameters, check the size. Add "1" (string value) if the size < 4
        for (int i = 0; i < labelParameters.size(); i++) {
            Vector<String> paramIn = labelParameters.get(i);
            int paramSize = paramIn.size();
            if (paramSize < 4) {
                paramIn.add("1"); //Set the aliquot default to "1"
                labelParameters.set(i, paramIn);
            }
        }

        
        /*for (int i = 0; i < labelParameters.size(); i++) {              
            logger.debug("Parameter " + i + "..."); 
            Vector<String> paramIn = labelParameters.get(i); 
            for (int j = 0; j < paramIn.size(); j++) {
                logger.debug("value " + j + ": " + paramIn.get(j)); 
            } 
        }*/
         
        
        return labelParameters;
    }

    public String getDateValue(Vector<Vector> parameters) {

        int paramNum = parameters.size();
        String dateValue = "";

        //Get the date value
        boolean dateFound = false;
        int paramCount = 0;
        while (!dateFound && paramCount < paramNum) {
            Vector<String> paramIn = parameters.get(paramCount);
            //System.out.println("dateCheck (paramIn.get(1)): " + paramIn.get(1));
            if (paramIn.get(1).equals("biomaterial_date")) {
                dateValue = paramIn.get(10);
                dateFound = true;
            } else {
                paramCount++;
            }
        }

        return dateValue;
    }

    public Vector<String> getAliquotValues(String ensatId, String centerId, String formId, String dbn, Statement stmt, String tablename) {

        Vector<String> aliquotValues = new Vector<String>();

        if (tablename.equals("ACC_Biomaterial")
                || tablename.equals("Pheo_Biomaterial")
                || tablename.equals("NAPACA_Biomaterial")
                || tablename.equals("APA_Biomaterial")) {

            String aliquotSql = "";
            aliquotSql += "SELECT * FROM " + dbn + "_Biomaterial_Aliquots WHERE center_id='" + centerId + "' AND ensat_id=" + ensatId + " AND " + dbn.toLowerCase() + "_biomaterial_id=" + formId + ";";

            //logger.debug("aliquotSql: " + aliquotSql);
            logger.debug("('" + username + "') Checking aliquot numbers (Ensat ID: " + centerId + "-" + ensatId + ", " + dbn + " Biomaterial form ID: " + formId + ")");
            ResultSet rs = null;
            try {
                rs = stmt.executeQuery(aliquotSql);
                while (rs.next()) {
                    String paramNameIn = rs.getString(5);
                    if (paramNameIn.equals("normal_tissue_frozen")) {
                        paramNameIn = "normal_tissue";
                    }
                    String aliquotOut = paramNameIn + "|" + rs.getString(6);
                    //logger.debug("aliquotOut: " + aliquotOut);
                    aliquotValues.add(aliquotOut);
                }
            } catch (Exception e) {
                logger.debug("('" + username + "') Error (getAliquotValues): " + e.getMessage());
            }
        }

        //logger.debug("aliquotValues: " + aliquotValues);

        return aliquotValues;
    }

    public Vector<Vector> setupParameters(String[] _tablenames, String modality, HttpServletRequest request, String centerid, String pid, String modid, String subTableIdName, Connection connValues, String dbn) {

        Vector<Vector> parameters = new Vector<Vector>();

        if (modality.equals("metabolomics")) {

            Vector<String> parameter = new Vector<String>();
            parameter.add("1"); //param_id
            parameter.add("metabolomics_date"); //param_name
            parameter.add("date"); //param_type
            parameter.add("0"); //param_text_size
            parameter.add("Metabolomics date"); //param_label
            parameter.add("1"); //param_order_id
            parameter.add("0"); //menu
            parameter.add("0"); //param_sub_param
            if (dbn.equals("ACC")) {
                parameter.add("ACC_Metabolomics"); //param_table
            } else if (dbn.equals("NAPACA")) {
                parameter.add("NAPACA_Metabolomics"); //param_table
            }
            parameter.add("false"); //param_optional


            /*
             * String valueIn = request.getParameter("metabolomics_date");
             * if(valueIn == null){ valueIn =
             * (String)request.getAttribute("metabolomics_date"); }
             *
             * if(valueIn == null){ valueIn = ""; } parameter.add(valueIn);
             */
            parameters.add(parameter);
            parameter = new Vector<String>();
            parameter.add("2"); //param_id
            parameter.add("comment"); //param_name
            parameter.add("text"); //param_type
            parameter.add("50"); //param_text_size
            parameter.add("Comment"); //param_label
            parameter.add("2"); //param_order_id
            parameter.add("0"); //menu
            parameter.add("0"); //param_sub_param
            if (dbn.equals("ACC")) {
                parameter.add("ACC_Metabolomics"); //param_table
            } else if (dbn.equals("NAPACA")) {
                parameter.add("NAPACA_Metabolomics"); //param_table
            }
            parameter.add("true"); //param_optional
            /*
             * valueIn = request.getParameter("comment"); if(valueIn == null){
             * valueIn = (String)request.getAttribute("comment"); } if(valueIn
             * == null){ valueIn = ""; } parameter.add(valueIn);
             */
            parameters.add(parameter);
            parameter = new Vector<String>();
            parameter.add("3"); //param_id
            parameter.add("filename"); //param_name
            parameter.add("text"); //param_type
            parameter.add("50"); //param_text_size
            parameter.add("Filename"); //param_label
            parameter.add("3"); //param_order_id
            parameter.add("0"); //menu
            parameter.add("0"); //param_sub_param
            if (dbn.equals("ACC")) {
                parameter.add("ACC_Metabolomics"); //param_table
            } else if (dbn.equals("NAPACA")) {
                parameter.add("NAPACA_Metabolomics"); //param_table
            }
            parameter.add("false"); //param_optional
            //parameter.add("metabolomics_" + centerid + "_" + pid + "_" + modid);
            parameters.add(parameter);

        }

        //Now add the values to the parameter vector and return
        parameters = this.getParameterValues(_tablenames, parameters, pid, centerid, modid, subTableIdName, connValues);
        return parameters;
    }

    private void updateFreezerTable(String paramName, String paramValue, String tablename, int nextId, String pid, String centerid, HttpServletRequest request, Connection conn) {

        //("INTO updateFreezerTable...");

        //THIS HAS TO GO SOMEWHERE BUT THE CODE HAS RUN AGROUND...
        /*
         * String[] normalTissueSelections = new String[normalTissueSelection];
         * for(int i=0; i<normalTissueSelection; i++){ normalTissueSelections[i]
         * = request.getParameter("" + paramName + "_" + (i+1));
         * normalTissueSelections[i] =
         * this.getNormalTissueParamLabel(normalTissueSelections[i]); String
         * paramNameNormalTissue = paramName + "_" + normalTissueSelections[i];
         * this.runFreezerNTUpdate(paramNameNormalTissue, tablename, nextId,
         * pid, centerid, request, conn); }
         */

        //ALSO THIS...??
        //this.runFreezerNTDelete(paramName, tablename, nextId, pid, centerid, request, conn);


        boolean parameterHasAliquot = this.getAliquotParameter(paramName);

        if (!paramName.contains("normal_tissue")) {

            String freezerSql = "";

            if (parameterHasAliquot && paramValue.equals("Yes")) {

                //Run a delete option for this particular parameter
                String deleteSql = "DELETE FROM " + tablename + "_Freezer_Information WHERE ensat_id=? AND center_id=? AND " + tablename.toLowerCase() + "_id=? AND material=?;";

                try {
                    //ResultSet rs = statement.executeQuery(materialCheckSql);
                    //PreparedStatement ps = conn.prepareStatement(materialCheckSql);
                    PreparedStatement ps = conn.prepareStatement(deleteSql);
                    ps.setString(1, pid);
                    ps.setString(2, centerid);
                    ps.setInt(3, nextId);
                    ps.setString(4, paramName);
                    int delete = ps.executeUpdate();

                } catch (Exception e) {
                    logger.debug("Error (updateFreezerTable): " + e.getMessage());
                    //System.out.println("Error (updateFreezerTable): " + e.getMessage());
                }

                //Get the relevant aliquot number
                String aliquotNumber = request.getParameter("aliquot_" + paramName);
                int aliquotNumberInt = 1;
                try {
                    aliquotNumberInt = Integer.parseInt(aliquotNumber);
                } catch (NumberFormatException nfe) {
                    logger.debug("NumberFormatException: " + nfe.getMessage());
                    aliquotNumberInt = 1;
                }


                //Get the last ID from the freezer table
                String locationIdName = tablename.toLowerCase() + "_location_id";
                //String idCheckSql = "SELECT " + locationIdName + " FROM " + tablename + "_Freezer_Information WHERE ensat_id=? AND center_id=? AND " + tablename.toLowerCase() + "_id=? ORDER BY " + locationIdName + " DESC;";
                String idCheckSql = "SELECT " + locationIdName + " FROM " + tablename + "_Freezer_Information WHERE ensat_id=? AND center_id=? ORDER BY " + locationIdName + " DESC;";
                String formId = "0";
                try {
                    PreparedStatement ps = conn.prepareStatement(idCheckSql);
                    ps.setString(1, pid);
                    ps.setString(2, centerid);

                    //ResultSet rs = statement.executeQuery(idCheckSql);
                    ResultSet rs = ps.executeQuery();
                    if (rs.next()) {
                        formId = rs.getString(1);
                    }
                    int formIdInt = Integer.parseInt(formId);
                    formIdInt = formIdInt + 1;
                    formId = "" + formIdInt;
                } catch (Exception e) {
                    //System.out.println("Error (updateFreezerTable): " + e.getMessage());
                    logger.debug("Error (updateFreezerTable): " + e.getMessage());
                }


                //Repeat the insert for the number of aliquots            
                for (int i = 0; i < aliquotNumberInt; i++) {

                    String freezerValue = request.getParameter(paramName + "_freezer_" + (i + 1));
                    String freezerShelfValue = request.getParameter(paramName + "_freezershelf_" + (i + 1));
                    String rackValue = request.getParameter(paramName + "_rack_" + (i + 1));
                    String shelfValue = request.getParameter(paramName + "_shelf_" + (i + 1));
                    String boxValue = request.getParameter(paramName + "_box_" + (i + 1));
                    String positionValue = request.getParameter(paramName + "_position_" + (i + 1));
                    
                    String bioId = request.getParameter(paramName + "_bio_id_" + (i + 1));
                    if(bioId == null){
                        bioId = "";
                    }
                    if(bioId.equalsIgnoreCase("null")){
                        bioId = "";
                    }
                    //If the bioId is blank then generate a new one for this aliquot
                    if(bioId.equals("")){
                        bioId = this.createRandomBioId();
                    }
                    
                    String materialUsed = request.getParameter(paramName + "_material_used_" + (i + 1));
                    String materialTransferred = request.getParameter(paramName + "_material_transferred_" + (i + 1));

                    freezerSql = "INSERT INTO " + tablename + "_Freezer_Information VALUES(";
                    freezerSql += "?,?,?,?,";
                    freezerSql += "?,?,?,?,?,?,?,?,?,?,?);";

                    try {
                        conn.setAutoCommit(false);
                        //logger.debug("freezerSql: " + freezerSql);
                        //System.out.println("freezerSql: " + freezerSql);
                        PreparedStatement ps = conn.prepareStatement(freezerSql);
                        ps.setString(1, formId);
                        ps.setInt(2, nextId);
                        ps.setString(3, pid);
                        ps.setString(4, centerid);

                        ps.setInt(5, (i + 1)); //This is the aliquot_sequence_id

                        ps.setString(6, paramName);
                        ps.setString(7, freezerValue);
                        ps.setString(8, freezerShelfValue);
                        ps.setString(9, rackValue);
                        ps.setString(10, shelfValue);
                        ps.setString(11, boxValue);
                        ps.setString(12, positionValue);
                        
                        ps.setString(13, bioId);
                        ps.setString(14, materialUsed);
                        ps.setString(15, materialTransferred);

                        int updateLocation = ps.executeUpdate();

                        conn.commit();

                        int formIdInt = Integer.parseInt(formId);
                        formIdInt = formIdInt + 1;
                        formId = "" + formIdInt;

                    } catch (Exception e) {
                        logger.debug("Error (updateFreezerTable): " + e.getMessage());                        
                    }
                }
            }
        }
    }

    private String processBiomaterialTypes(ResultSet rs) throws Exception {

        String bioOutput = "<td>";

        int[] bioParamIndices = {5, 7, 8, 9, 10, 11, 12, 13, 15, 16, 17, 18, 19};

        for (int i = 0; i < bioParamIndices.length; i++) {
            int index = bioParamIndices[i];
            String bioParamIn = rs.getString(index);
            if (bioParamIn == null) {
                bioParamIn = "";
            }
            if (bioParamIn.equals("Yes")) {
                if (index == 5) {
                    bioOutput += "Tumor Tissue (Frozen)<br/>";
                } else if (index == 7) {
                    bioOutput += "Tumor Tissue (Paraffin)<br/>";
                } else if (index == 8) {
                    bioOutput += "Tumor Tissue (DNA)<br/>";
                } else if (index == 9) {
                    bioOutput += "Leukocyte DNA<br/>";
                } else if (index == 10) {
                    bioOutput += "EDTA Plasma<br/>";
                } else if (index == 11) {
                    bioOutput += "Heparin Plasma<br/>";
                } else if (index == 12) {
                    bioOutput += "Serum<br/>";
                } else if (index == 13) {
                    bioOutput += "24h Urine<br/>";
                } else if (index == 15) {
                    bioOutput += "Spot Urine<br/>";
                } else if (index == 16) {
                    bioOutput += "Normal Tissue<br/>";
                } else if (index == 17) {
                    bioOutput += "Normal Tissue (Paraffin)<br/>";
                } else if (index == 18) {
                    bioOutput += "Normal Tissue (DNA)<br/>";
                } else if (index == 19) {
                    bioOutput += "Whole Blood<br/>";
                }
            }
        }

        bioOutput += "</td>";
        return bioOutput;
    }

    public String getJavascriptValidationArray(Vector<Vector> parameters,String baseUrl) {

        String outputStr = "";
        outputStr = "<script type=\"text/JavaScript\">";

        int paramNum = parameters.size();
        //Adding the -2 because of the freezer and aliquot stuff
        for (int i = 0; i < paramNum - 2; i++) {
            Vector<String> paramIn = parameters.get(i);
            String paramName = paramIn.get(1);
            String paramOptional = paramIn.get(9);
            if (paramOptional.equals("")) {
                paramOptional = "true";
            }
            String paramType = paramIn.get(2);

            outputStr += "var paramArray" + i + " = new Array(\"" + paramName + "\"," + paramOptional + ",\"" + paramType + "\",\"" + baseUrl + "\");";
        }
        outputStr += "var paramArrays = new Array(";
        for (int i = 0; i < paramNum; i++) {
            outputStr += "paramArray" + i + ",";
        }
        outputStr = outputStr.substring(0, outputStr.length() - 1);
        outputStr += ");";
        outputStr += "</script>";

        return outputStr;
    }

    private void runFreezerNTDelete(String paramName, String tablename, int nextId, String pid, String centerid, HttpServletRequest request, Connection conn) {

        String deleteSql = "DELETE FROM " + tablename + "_Freezer_Information WHERE ensat_id=? AND center_id=? AND " + tablename.toLowerCase() + "_id=? AND material LIKE ?;";
        try {
            PreparedStatement ps = conn.prepareStatement(deleteSql);
            ps.setString(1, pid);
            ps.setString(2, centerid);
            ps.setInt(3, nextId);
            ps.setString(4, paramName + "%");
            int delete = ps.executeUpdate();
        } catch (Exception e) {
            logger.debug("Error (updateFreezerTable): " + e.getMessage());
        }
    }

    private void runFreezerNTUpdate(String paramName, String paramValue, String tablename, int nextId, String pid, String centerid, HttpServletRequest request, Connection conn) {

        //System.out.println("Into runFreezerNTUpdate...");

        /*
         * paramName = paramName + "_" + paramValue;
         * System.out.println("paramName: " + paramName);
         */

        String freezerSql = "";

        //Run a delete option for this particular parameter
            /*
         * String deleteSql = "DELETE FROM " + tablename + "_Freezer_Information
         * WHERE ensat_id=? AND center_id=? AND " + tablename.toLowerCase() +
         * "_id=? AND material=?;"; try{ PreparedStatement ps =
         * conn.prepareStatement(deleteSql); ps.setString(1,pid);
         * ps.setString(2,centerid); ps.setInt(3,nextId);
         * ps.setString(4,paramName); int delete = ps.executeUpdate();
         * }catch(Exception e){ System.out.println("Error (updateFreezerTable):
         * " + e.getMessage()); }
         */

        //Get the relevant aliquot number
        String aliquotNumber = request.getParameter("aliquot_" + paramName);
        int aliquotNumberInt = 1;
        try {
            aliquotNumberInt = Integer.parseInt(aliquotNumber);
        } catch (NumberFormatException nfe) {
            logger.debug("NumberFormatException: " + nfe.getMessage());
            aliquotNumberInt = 1;
        }

        //Get the last ID from the freezer table
        String locationIdName = tablename.toLowerCase() + "_location_id";
        //String idCheckSql = "SELECT " + locationIdName + " FROM " + tablename + "_Freezer_Information WHERE ensat_id=? AND center_id=? AND " + tablename.toLowerCase() + "_id=? ORDER BY " + locationIdName + " DESC;";
        String idCheckSql = "SELECT " + locationIdName + " FROM " + tablename + "_Freezer_Information WHERE ensat_id=? AND center_id=? ORDER BY " + locationIdName + " DESC;";
        String formId = "0";
        try {
            PreparedStatement ps = conn.prepareStatement(idCheckSql);
            ps.setString(1, pid);
            ps.setString(2, centerid);

            //ResultSet rs = statement.executeQuery(idCheckSql);
            ResultSet rs = ps.executeQuery();
            if (rs.next()) {
                formId = rs.getString(1);
            }
            int formIdInt = Integer.parseInt(formId);
            formIdInt = formIdInt + 1;
            formId = "" + formIdInt;
        } catch (Exception e) {
            logger.debug("Error (updateFreezerTable): " + e.getMessage());
        }

        //Repeat the insert for the number of aliquots            
        for (int i = 0; i < aliquotNumberInt; i++) {

            String freezerValue = request.getParameter(paramName + "_freezer_" + (i + 1));
            String freezerShelfValue = request.getParameter(paramName + "_freezershelf_" + (i + 1));
            String rackValue = request.getParameter(paramName + "_rack_" + (i + 1));
            String shelfValue = request.getParameter(paramName + "_shelf_" + (i + 1));
            String boxValue = request.getParameter(paramName + "_box_" + (i + 1));
            String positionValue = request.getParameter(paramName + "_position_" + (i + 1));

            freezerSql = "INSERT INTO " + tablename + "_Freezer_Information VALUES(";
            freezerSql += "?,?,?,?,";
            freezerSql += "?,?,?,?,?,?,?,?);";

            try {
                conn.setAutoCommit(false);
                PreparedStatement ps = conn.prepareStatement(freezerSql);
                ps.setString(1, formId);
                ps.setInt(2, nextId);
                ps.setString(3, pid);
                ps.setString(4, centerid);

                ps.setInt(5, (i + 1)); //This is the aliquot_sequence_id

                ps.setString(6, paramName);
                ps.setString(7, freezerValue);
                ps.setString(8, freezerShelfValue);
                ps.setString(9, rackValue);
                ps.setString(10, shelfValue);
                ps.setString(11, boxValue);
                ps.setString(12, positionValue);

                /*
                 * logger.debug("Form ID " + (i+1) + ": " + formId);
                 * logger.debug("Next ID " + (i+1) + ": " + nextId);
                 * logger.debug("PID " + (i+1) + ": " + pid);
                 * logger.debug("Center ID " + (i+1) + ": " + centerid);
                 * logger.debug("Aliquot sequence ID " + (i+1) + ": " + (i+1));
                 * logger.debug("paramNam" + (i+1) + ": " + paramName);
                 *
                 * logger.debug("Freezer #" + (i+1) + ": " + freezerValue);
                 * logger.debug("Rack #" + (i+1) + ": " + rackValue);
                 * logger.debug("Shelf #" + (i+1) + ": " + shelfValue);
                 * logger.debug("Box #" + (i+1) + ": " + boxValue);
                 * logger.debug("Position #" + (i+1) + ": " + positionValue);
                 */

                //logger.debug("freezerSql: " + freezerSql);
                //("freezerSql (NTUpdate): " + freezerSql);
                int updateLocation = ps.executeUpdate();
                conn.commit();

                int formIdInt = Integer.parseInt(formId);
                formIdInt = formIdInt + 1;
                formId = "" + formIdInt;

            } catch (Exception e) {
                logger.debug("Error (updateFreezerTable): " + e.getMessage());
            }
        }
    }

    public Vector<Vector> getModidNumber(String ensat_id, String centerid, String tablename, String tablenameid, Connection connection, Vector<Vector> params) {

        Vector<Vector> modidValues = new Vector<Vector>();

        String sql = "SELECT ";
        int paramNum = params.size();
        for (int i = 0; i < paramNum; i++) {
            Vector<String> paramIn = params.get(i);
            String paramNameIn = paramIn.get(1);
            sql += "" + paramNameIn + ",";

            Vector<String> paramValuesIn = new Vector<String>();
            paramValuesIn.add(paramNameIn);
            modidValues.add(paramValuesIn);
        }
        sql = sql.substring(0, sql.length() - 1);
        sql += " FROM " + tablename + " WHERE ensat_id=? AND center_id=?;";

        logger.debug("SQL " + sql + " ");

        try {

            PreparedStatement ps = connection.prepareStatement(sql);
            ps.setString(1, ensat_id);
            ps.setString(2, centerid);
            ResultSet rs = ps.executeQuery();
            while (rs.next()) {
                for (int i = 0; i < paramNum; i++) {
                    Vector<String> modidValueIn = modidValues.get(i);
                    String thisValueIn = rs.getString(i + 1);
                    modidValueIn.add(thisValueIn);
                    modidValues.set(i, modidValueIn);
                }
            }
        } catch (Exception e) {
            logger.debug("Error (getModidNumber): " + e.getMessage());
        }

        return modidValues;
    }

    //public String getTableNameId(String tablename, String dbn) {
    public String getTableNameId(String tablename) {

        String tablenameid = tablename;
        //if (dbn.equals("Pheo")) {
            if (tablename.equals("Pheo_ImagingTests")) {
                tablenameid = "Pheo_Imaging_Tests";
            } else if (tablename.equals("Pheo_TumorDetails")) {
                tablenameid = "Pheo_Tumor_Details";
            } else if (tablename.equals("Pheo_BiochemicalAssessment")) {
                tablenameid = "Biochemical_Assessment";
            } else if (tablename.equals("Pheo_ClinicalAssessment")) {
                tablenameid = "Clinical_Assessment";
            } else if (tablename.equals("Pheo_NonSurgicalInterventions")) {
                tablenameid = "Pheo_Non_Surgical_Interventions";
            }
        //} else if (dbn.equals("NAPACA")) {
            else if (tablename.equals("NAPACA_DiagnosticProcedures")) {
                tablenameid = "NAPACA_Diagnostic_Procedures";
            }
        //} else if (dbn.equals("APA")) {
            else if (tablename.equals("APA_Biomaterial_aliquots")) {
                tablenameid = "APA_Biomaterial_Aliquot";
            } else if (tablename.equals("APA_ClinicalAssessment")) {
                tablenameid = "Clinical_Assessment";
            } else if (tablename.equals("APA_BiochemicalAssessment")) {
                tablenameid = "Biochemical_Assessment";
            }
        //} else if (dbn.equals("ACC")) {
            else if (tablename.equals("ACC_Biomaterial_Aliquots")) {
                tablenameid = "ACC_Biomaterial_Aliquot";
            }
        //}
        return tablenameid;
    }

    public String[] getTimelineTables(String dbn) {

        String[] tablenames = null;

        if (dbn.equals("ACC")) {
            tablenames = new String[3];
            tablenames[0] = "Identification";
            tablenames[1] = "ACC_DiagnosticProcedures";
            tablenames[2] = "ACC_TumorStaging";
        } else if (dbn.equals("Pheo")) {
            tablenames = new String[2];
            tablenames[0] = "Identification";
            tablenames[1] = "Pheo_PatientHistory";
        } else if (dbn.equals("NAPACA")) {
            tablenames = new String[2];
            tablenames[0] = "Identification";
            tablenames[1] = "NAPACA_DiagnosticProcedures";
        } else if (dbn.equals("APA")) {
            tablenames = new String[2];
            tablenames[0] = "Identification";
            tablenames[1] = "APA_PatientHistory";
        }
        return tablenames;
    }

    public String[] getTimelineSubTables(String dbn) {

        String[] tablenames = null;

        if (dbn.equals("ACC")) {
            tablenames = new String[9];
            tablenames[0] = "ACC_Biomaterial";
            tablenames[1] = "ACC_Surgery";
            tablenames[2] = "ACC_Pathology";
            tablenames[3] = "ACC_Mitotane";
            tablenames[4] = "ACC_Chemotherapy";
            tablenames[5] = "ACC_Radiofrequency";
            tablenames[6] = "ACC_Radiotherapy";
            tablenames[7] = "ACC_Chemoembolisation";
            tablenames[8] = "ACC_FollowUp";
        } else if (dbn.equals("Pheo")) {
            tablenames = new String[8];
            tablenames[0] = "Pheo_NonSurgicalInterventions";
            tablenames[1] = "Pheo_Surgery";
            tablenames[2] = "Pheo_ClinicalAssessment";
            tablenames[3] = "Pheo_FollowUp";
            tablenames[4] = "Pheo_TumorDetails";
            tablenames[5] = "Pheo_ImagingTests";
            tablenames[6] = "Pheo_BiochemicalAssessment";
            tablenames[7] = "Pheo_Biomaterial";
        } else if (dbn.equals("NAPACA")) {
            tablenames = new String[5];
            tablenames[0] = "NAPACA_Surgery";
            tablenames[1] = "NAPACA_imaging";
            tablenames[2] = "NAPACA_Pathology";
            tablenames[3] = "NAPACA_FollowUp";
            tablenames[4] = "NAPACA_biomaterial";
        } else if (dbn.equals("APA")) {
            tablenames = new String[8];
            tablenames[0] = "APA_Biomaterial";
            tablenames[1] = "APA_ClinicalAssessment";
            tablenames[2] = "APA_Cardio";
            tablenames[3] = "APA_Complication";
            tablenames[4] = "APA_Imaging";
            tablenames[5] = "APA_Surgery";
            tablenames[6] = "APA_BiochemicalAssessment";
            tablenames[7] = "APA_FollowUp";
        }
        return tablenames;
    }

    public String getTimelineCSSColor(String dbn) {

        String cssStr = "";

        String color = "";
        String hexCode = "";
        if (dbn.equals("ACC")) {
            color = "green";
            hexCode = "#CCFFCC";
        } else if (dbn.equals("Pheo")) {
            color = "blue";
            hexCode = "#CCEEFF";
        } else if (dbn.equals("NAPACA")) {
            color = "yellow";
            hexCode = "#FFDD99";
        } else if (dbn.equals("APA")) {
            color = "grey";
            hexCode = "#DDDDDD";
        }

        cssStr += "<style type=\"text/css\">";
        cssStr += "div.timeline-event {";
        cssStr += "border-color: " + color + ";";
        cssStr += "background-color: " + hexCode + ";";
        cssStr += "}";
        cssStr += "</style>";

        return cssStr;
    }

    public String getDataRow(Vector<Vector> parameters, String tablename) {

        String jsonStr = "";
        
        //Filter out the Identification parameters
        Vector<Vector> identParams = new Vector<Vector>();
        int paramNum = parameters.size();
        for (int i = 0; i < paramNum; i++) {
            Vector<String> paramIn = parameters.get(i);
            String paramTableIn = paramIn.get(8);
            if (paramTableIn.equals(tablename)) {
                identParams.add(paramIn);
            }
        }

        int identParamNum = identParams.size();
        boolean dateIsPresent = false;

        for (int i = 0; i < identParamNum; i++) {
            String info = "";
            Vector<String> paramIn = identParams.get(i);
            String paramNameIn = paramIn.get(1);
            String paramValueIn = paramIn.get(10);
            String paramTableIn = paramIn.get(8);
            String paramLabelIn = paramIn.get(4);
            String paramTypeIn = paramIn.get(2);
            if (!paramValueIn.equals("")) {
                info += paramNameIn + "\\t => \\t" + paramValueIn + "\\n";
            } else {
                info += paramNameIn + "\\t => \\t - \\n";
            }
            if (paramTypeIn.equals("date")) {
                dateIsPresent = true;
                jsonStr += "[new Date(\"" + paramValueIn + "\"), , ' <img src=\"../images/img/" + paramTableIn + ".png\" style=\"width:32px; height:32px;\"><br>" + paramLabelIn + " ','------------ " + paramTableIn + " ------------','" + info + "'],";
            }


        }
        
        logger.debug("jsonStr: " + jsonStr);

        if (dateIsPresent) {
            return jsonStr;
        } else {
            return "";
        }
    }

    //public String getSubDataRow(Vector<Vector> parameters, String tablename, String dbn, String pid, String centerid, Connection connection) {
    public String getSubDataRow(Vector<Vector> parameters, String tablename, String pid, String centerid, Connection connection) {

        String jsonStr = "data.addRows([ ";

        //Filter out the tablename parameters
        Vector<Vector> identParams = new Vector<Vector>();
        int paramNum = parameters.size();
        for (int i = 0; i < paramNum; i++) {
            Vector<String> paramIn = parameters.get(i);
            String paramTableIn = paramIn.get(8);
            if (paramTableIn.equals(tablename)) {
                identParams.add(paramIn);
            }
        }

        int identParamNum = identParams.size();
        boolean dateIsPresent = false;

        //Need the numberModid and the corresponding parameter values (tagged to parameter name)
        //String tablenameid = this.getTableNameId(tablename, dbn);
        String tablenameid = this.getTableNameId(tablename);
        Vector<Vector> modidValues = new Vector<Vector>();
        modidValues = this.getModidNumber(pid, centerid, tablename, tablenameid, connection, identParams);

        int modidNum = modidValues.size();


        for (int i = 0; i < identParamNum; i++) {
            String info = "";
            Vector<String> paramIn = identParams.get(i);

            String paramNameIn = paramIn.get(1);
            String paramTableIn = paramIn.get(8);
            String paramLabelIn = paramIn.get(4);
            String paramTypeIn = paramIn.get(2);

            Vector<String> paramValuesIn = new Vector<String>();
            boolean paramFound = false;
            int valueCount = 0;
            while (!paramFound && valueCount < identParamNum) {
                Vector<String> paramValueIn = modidValues.get(valueCount);
                String paramValueInName = paramValueIn.get(0);
                if (paramValueInName.equals(paramNameIn)) {
                    paramFound = true;
                    paramValuesIn = paramValueIn;
                } else {
                    valueCount++;
                }
            }

            for (int j = 0; j < modidNum; j++) {

                String paramValueIn = paramValuesIn.get(j);
                if (!paramValueIn.equals("")) {
                    info += paramNameIn + "\\t => \\t" + paramValueIn + "\\n";
                } else {
                    info += paramNameIn + "\\t => \\t - \\n";
                }
                if (paramTypeIn.equals("date")) {
                    dateIsPresent = true;
                    jsonStr += "[new Date(\"" + paramValueIn + "\"), , ' <img src=\"../images/img/" + paramTableIn + ".png\" style=\"width:32px; height:32px;\"><br>" + paramLabelIn + " ','------------ " + paramTableIn + " ------------','" + info + "'],";
                }
            }
        }

        jsonStr += "]);";

        logger.debug("jsonStr: " + jsonStr);

        if (dateIsPresent) {
            return jsonStr;
        } else {
            return "";
        }
    }

    
    private String getMonthNumber(String monthName) {

        String n_month = "";

        if (monthName.equals("Jan")) {
            n_month = "01";
        } else if (monthName.equals("Feb")) {
            n_month = "02";
        } else if (monthName.equals("Mar")) {
            n_month = "03";
        } else if (monthName.equals("Apr")) {
            n_month = "04";
        } else if (monthName.equals("May")) {
            n_month = "05";
        } else if (monthName.equals("Jun")) {
            n_month = "06";
        } else if (monthName.equals("Jul")) {
            n_month = "07";
        } else if (monthName.equals("Aug")) {
            n_month = "08";
        } else if (monthName.equals("Sep")) {
            n_month = "09";
        } else if (monthName.equals("Oct")) {
            n_month = "10";
        } else if (monthName.equals("Nov")) {
            n_month = "11";
        } else if (monthName.equals("Dec")) {
            n_month = "12";
        }

        return n_month;
    }

    public boolean recordIsEditable(String doctor, String forename, String surname) {
        return doctor == forename + " " + surname;
    }
    
      public int getModidNumber(String ensat_id, String tablename, String tablenameid, Connection connection) throws SQLException{
    /*if(tablename.equals("Pheo_ImagingTests")){
        tablenameid="Pheo_Imaging_Tests";
    }else if(tablename.equals("Pheo_TumorDetails")){
        tablenameid="Pheo_Tumor_Details";
    }else if(tablename.equals("Pheo_BiochemicalAssessment")){
        tablenameid="Biochemical_Assessment";
    }else if(tablename.equals("Pheo_ClinicalAssessment")){
        tablenameid="Clinical_Assessment";
    }else if(tablename.equals("Pheo_NonSurgicalInterventions")){
        tablenameid="Pheo_Non_Surgical_Interventions";
    }*/
    
    //System.out.println("-------- "+tablenameid);
    
    String sql = "SELECT MAX("+tablenameid+"_id) FROM "+tablename+" where ensat_id="+ensat_id+";";   
    //System.out.println("SQL " +sql+" ");
    int Number =0;
    PreparedStatement ps = connection.prepareStatement(sql);
    ResultSet rs = ps.executeQuery();
    while (rs.next()) {
        if(rs.getString(1)!=null){
    Number = Integer.parseInt(rs.getString(1));
        }
       }
    return Number;
  }
  
  public String getTableNameId(String tablename, String dbn){
      String tablenameid = tablename;
    if (dbn.equals("Pheo")){
        if(tablename.equals("Pheo_ImagingTests")){
            tablenameid="Pheo_Imaging_Tests";
        }else if(tablename.equals("Pheo_TumorDetails")){
            tablenameid="Pheo_Tumor_Details";
        }else if(tablename.equals("Pheo_BiochemicalAssessment")){
            tablenameid="Biochemical_Assessment";
        }else if(tablename.equals("Pheo_ClinicalAssessment")){
            tablenameid="Clinical_Assessment";
        }else if(tablename.equals("Pheo_NonSurgicalInterventions")){
            tablenameid="Pheo_Non_Surgical_Interventions";
        }
    }else if (dbn.equals("NAPACA")){
        if(tablename.equals("NAPACA_DiagnosticProcedures")){
            tablenameid="NAPACA_Diagnostic_Procedures";
        }
    }else if (dbn.equals("APA")){
        if(tablename.equals("APA_Biomaterial_aliquots")){
            tablenameid="APA_Biomaterial_Aliquot";
        }else if (tablename.equals("APA_ClinicalAssessment")){
            tablenameid="Clinical_Assessment";
        }else if (tablename.equals("APA_BiochemicalAssessment")){
            tablenameid="Biochemical_Assessment";
        }
    }else if (dbn.equals("ACC")){
        if(tablename.equals("ACC_Biomaterial_Aliquots")){
            tablenameid="ACC_Biomaterial_Aliquot";
        }
    }   
      return tablenameid;
  }
  
  public String convertDate(String dateIn){
      
      String dateOut = dateIn;
      int firstHyphen = dateIn.indexOf("-");
      int secondHyphen = dateIn.lastIndexOf("-");
      
      String year = "";
      String month = "";
      String day = "";
      
      if(firstHyphen != -1 && secondHyphen != -1){
        year = dateIn.substring(0,firstHyphen);
        month = dateIn.substring(firstHyphen+1,secondHyphen);
        day = dateIn.substring(secondHyphen+1,dateIn.length());
      }
      
      month = this.convertMonth(month);      
      dateOut = day + " " + month + " " + year;
      return dateOut;
  }
  
  private String convertMonth(String monthIn){
      
      String monthOut = monthIn;
      if(monthIn.equals("01")){
          monthOut = "Jan";
      }else if(monthIn.equals("02")){
          monthOut = "Feb";
      }else if(monthIn.equals("03")){
          monthOut = "Mar";
      }else if(monthIn.equals("04")){
          monthOut = "Apr";
      }else if(monthIn.equals("05")){
          monthOut = "May";
      }else if(monthIn.equals("06")){
          monthOut = "Jun";
      }else if(monthIn.equals("07")){
          monthOut = "Jul";
      }else if(monthIn.equals("08")){
          monthOut = "Aug";
      }else if(monthIn.equals("09")){
          monthOut = "Sep";
      }else if(monthIn.equals("10")){
          monthOut = "Oct";
      }else if(monthIn.equals("11")){
          monthOut = "Nov";
      }else if(monthIn.equals("12")){
          monthOut = "Dec";
      }
      return monthOut;
      
  }
  
  private String createRandomBioId(){

        //This makes a random seven-char string and seven-char number code
        
        int ALPHABET_SIZE = 26;        
        int CODE_SIZE = 7;
        int INT_RANGE = 9;
        Random r = new Random();
        String bioId = "";
        for(int i=0; i<CODE_SIZE; i++){
            bioId += ("" + (char)(r.nextInt(ALPHABET_SIZE) + 'a')).toUpperCase();
        }
        
        for(int i=0; i<CODE_SIZE; i++){
            bioId += "" + r.nextInt(INT_RANGE);
        }
        
        logger.debug("Random biomaterial ID: " + bioId);        
        return bioId;        
    }

}
