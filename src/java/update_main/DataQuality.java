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

import java.util.Vector;
import java.util.StringTokenizer;

import java.text.SimpleDateFormat;
import java.text.DateFormat;
import java.text.DecimalFormat;
import javax.servlet.ServletContext;

import javax.servlet.http.HttpServletRequest;

import org.apache.log4j.Level;
import org.apache.log4j.Logger;
import org.apache.log4j.PropertyConfigurator;

/**
 *
 * @author astell
 */
public class DataQuality {

    private static final Logger logger = Logger.getLogger(DataQuality.class);
    private String username = "";

    public DataQuality() {
    }

    public void setUsername(String _username) {
        username = _username;
    }
    
    
    public Vector<Vector> getTablesPercentage(Vector<String> patientList, Connection conn, String type, String study){
        
        Vector<Vector> tablesOut = new Vector<Vector>();        
        Vector<String> tableList = this.getTableList(conn, type, study);
        int patientNum = patientList.size();
        //int patientNum = 10;
        
        logger.debug("tableList.size(): " + tableList.size());
        
        try{
            int tableNum = tableList.size();
            if(patientList.size() > 0){
                for(int i=0; i<tableNum; i++){
                    String tableName = tableList.get(i);
                    String sql = "SELECT * FROM " + tableName + " WHERE ";            
                    for(int j=0; j<patientNum; j++){                    
                        sql += "(center_id=? AND ensat_id=?) OR ";                        
                    }
                    sql = sql.substring(0,sql.length()-4);
                    sql += ";";
                    //logger.debug("sql: " + sql);
                    
                    PreparedStatement ps = conn.prepareStatement(sql);
                    
                    //logger.debug("patientList.size(): " + patientList.size());
                    
                    for(int j=0; j<patientNum; j++){                    
                        String patientId = patientList.get(j);
                        String centerId = patientId.substring(0,patientId.indexOf("-"));
                        String ensatId = patientId.substring(patientId.indexOf("-")+1,patientId.length()); 
                        //logger.debug("patientId (" + j + "): " + patientId);
                        //logger.debug("ensatId: " + ensatId);
                        //logger.debug("===== before setting");                        
                        ps.setString(((2*j)+1),centerId);
                        ps.setString(((2*j)+2),ensatId);
                        //logger.debug("===== after setting");
                    }
                    
                    //Run the query
                    ResultSet rs = ps.executeQuery();
                    
                    //Get the total table column number
                    ResultSetMetaData rsmd = rs.getMetaData();
                    int colCount = rsmd.getColumnCount();
                    
                    logger.debug("colCount (" + tableName + "): " + colCount);
                    
                    //Get the total number of blanks (over all patients)
                    int blankCount = 0;
                    boolean noTable = !(rs.next());
                    rs.beforeFirst();           
                    //int formNum = 0;
                    /*while(rs.next()){
                        formNum++;
                    }
                    rs.beforeFirst();*/
                    int totalColCount = 0;
                    while(rs.next()){
                        for(int k=0; k<colCount; k++){                            
                            String valueIn = rs.getString(k+1);
                            if(valueIn == null){
                                valueIn = "";
                            }
                            if(valueIn.trim().equals("")){
                                blankCount++;
                            }             
                            totalColCount++;
                        }  
                        //formNum++;
                    }
                    logger.debug("blankCount(" + tableName + "): " + blankCount);
                    logger.debug("totalColCount (" + tableName + "): " + totalColCount);
                    
                    //Now calculate the overall percentage of blanks                    
                    float tablePercentBlank = (float)(((float) blankCount / (float)totalColCount) * 100);
                    if(noTable){
                        tablePercentBlank = (float) 100.0;
                    }
                    logger.debug("tablePercentBlank (" + tableName + "): " + tablePercentBlank);
                    float tablePercentNotBlank = 100 - tablePercentBlank;
                    //Round this to two significant figures
                    //tablePercentNotBlank = Math.round
                    logger.debug("tablePercentVal (" + tableName + "): " + tablePercentNotBlank);
                    
                    Vector<String> tablePercent = new Vector<String>();
                    tablePercent.add(tableName);
                    DecimalFormat df = new DecimalFormat("#.00");
                    String tablePercentNotBlankStr = df.format(tablePercentNotBlank);                    
                    tablePercent.add("" + tablePercentNotBlankStr);
                    tablesOut.add(tablePercent);
                }
            }        
        }catch(Exception e){
            logger.debug("" + username + " - error (getTablesPercentage): " + e.getMessage());
        }
        
        logger.debug("tablesOut.size(): " + tablesOut.size());
        
        return tablesOut;
    }
    
    private Vector<String> getTableList(Connection conn, String typeSelected, String study){
        
        Vector<String> tableList = new Vector<String>();        
        Vector<String> typeList = new Vector<String>();
        typeList.add("acc");
        typeList.add("pheo");
        typeList.add("napaca");
        typeList.add("apa");
                
        try{
            //Run the study table-check here and put into a vector
            Vector<String> studyTypes = new Vector<String>();
            if(!study.equals("")){
                String studySql = "SELECT DISTINCT tumor_type FROM Study_Type,Studies WHERE Study_Type.study_id=Studies.study_id AND Studies.study_label=?;";
                PreparedStatement studyPs = conn.prepareStatement(studySql);
                studyPs.setString(1, study);
                ResultSet studyRs = studyPs.executeQuery();
                while(studyRs.next()){
                    String typeIn = studyRs.getString(1);
                    studyTypes.add(typeIn.toLowerCase());
                }
                studyRs.close();
                studyPs.close();
            }            
            
            for(int i=0; i<studyTypes.size(); i++){
                logger.debug("studyTypes(" + i + "): " + studyTypes.get(i));
            }
            
            String sql = "SHOW TABLES;";
            
            PreparedStatement ps = conn.prepareStatement(sql);
            ResultSet rs = ps.executeQuery();
            
            while(rs.next()){
                String tableIn = rs.getString(1);
                String tablePrefix = "";
                if(tableIn.indexOf("_") != -1){
                    tablePrefix = tableIn.substring(0,tableIn.indexOf("_"));
                }                
                boolean dataTable = false;
                if(!typeSelected.equals("")){
                    //Get all tables of that type
                    String typeIn = typeSelected.toLowerCase();                    
                    if(tablePrefix.equalsIgnoreCase(typeIn) || tableIn.equalsIgnoreCase("Identification")){                    
                        dataTable = true;
                    }
                    //dataTable = tableIn.equalsIgnoreCase("Identification");
                }else if(!study.equals("")){
                    //Get the study-relevant table types
                    for(int i=0; i<studyTypes.size(); i++){
                        String studyTypeIn = studyTypes.get(i);
                        if(tablePrefix.equalsIgnoreCase(studyTypeIn) || tableIn.equalsIgnoreCase("Identification")){
                            dataTable = true;
                        }
                    }
                }else{
                    //Get all (data) tables
                    for(int i=0; i<typeList.size(); i++){
                        String typeListIn = typeList.get(i);
                        if(tablePrefix.equalsIgnoreCase(typeListIn) || tableIn.equalsIgnoreCase("Identification")){
                            dataTable = true;
                        }
                    }
                }
                if(dataTable){ 
                    if(!this.isExceptionTable(tableIn)){
                        tableList.add(tableIn);                    
                    }
                }                
            }
        }catch(Exception e){
            logger.debug("" + username + " - error(getTableList): " + e.getMessage());
        }        
        return tableList;        
    }
    
    public boolean isExceptionTable(String tableIn){
        boolean isException = 
                tableIn.contains("biomaterial_aliquots") ||
                tableIn.contains("freezer_information") ||
                tableIn.contains("biomaterial_normal_tissue") ||
                tableIn.equals("acc_chemotherapy_regimen") ||
                tableIn.equals("acc_followup_organs") ||
                tableIn.equals("acc_radiotherapy_loc") ||
                tableIn.equals("acc_surgery_extended") ||
                tableIn.equals("acc_surgery_first") ||
                
                tableIn.equals("pheo_clinical_stigmata") ||
                tableIn.equals("pheo_firstdiagnosispresentation") ||
                tableIn.equals("pheo_hormonesymptoms") ||
                tableIn.equals("pheo_tumorsymptoms") ||
                tableIn.equals("pheo_imagingtests_ctloc") ||
                tableIn.equals("pheo_imagingtests_nmrloc") ||
                tableIn.equals("pheo_malignantdiagnosistnm") ||
                tableIn.equals("pheo_metastaseslocation") ||
                tableIn.equals("pheo_other_genetics") ||
                tableIn.equals("pheo_otherorgans") ||
                tableIn.equals("pheo_surgery_intraop") ||
                tableIn.equals("pheo_surgery_preop") ||
                tableIn.equals("pheo_surgery_procedure")
                ;
        return isException;
    }
    
    
    public String getCenterMenu(Connection ccConn){
        
        String centerMenuHtml = "";
        centerMenuHtml += "<option value=''>[Select...]</option>";
        try{
            String sql = "SELECT center_id FROM Center_Callout ORDER BY center_id;";
            PreparedStatement ps = ccConn.prepareStatement(sql);
            ResultSet rs = ps.executeQuery();
            while(rs.next()){
                String centerIn = rs.getString(1);
                if(centerIn == null){
                    centerIn = "";
                }
                if(!centerIn.equals("")){
                    centerMenuHtml += "<option value='" + centerIn + "'>" + centerIn + "</option>";
                }
            }
        }catch(Exception e){
            logger.debug("I/O error (getCenterMenu): " + e.getMessage());
        }
        //Top and tail the HTML element
        centerMenuHtml = "<select name='centerid'>" + centerMenuHtml + "</select>";
        
        return centerMenuHtml;
    }
    
    public String getStudyMenu(Connection conn){
        String studyMenuHtml = "";
        studyMenuHtml += "<option value=''>[Select...]</option>";
        try{
            String sql = "SELECT study_label FROM Studies ORDER BY study_label;";
            PreparedStatement ps = conn.prepareStatement(sql);
            ResultSet rs = ps.executeQuery();
            while(rs.next()){
                String studyIn = rs.getString(1);
                if(studyIn == null){
                    studyIn = "";
                }
                if(!studyIn.equals("")){
                    studyMenuHtml += "<option value='" + studyIn + "'>" + studyIn + "</option>";
                }
            }
        }catch(Exception e){
            logger.debug("I/O error (getStudyMenu): " + e.getMessage());
        }
        //Top and tail the HTML element
        studyMenuHtml = "<select name='study'>" + studyMenuHtml + "</select>";
        
        return studyMenuHtml;
    }
    
    public String getTypeMenu(){
        
        String typeMenuHtml = "";
        typeMenuHtml += "<option value=''>[Select...]</option>";
        typeMenuHtml += "<option value='ACC'>ACC</option>";
        typeMenuHtml += "<option value='Pheo'>Pheo</option>";
        typeMenuHtml += "<option value='NAPACA'>NAPACA</option>";
        typeMenuHtml += "<option value='APA'>APA</option>";
        //Top and tail the HTML element
        typeMenuHtml = "<select name='type'>" + typeMenuHtml + "</select>";
        return typeMenuHtml;
    }
    
    public Vector<String> getPatientList(String filter, String tag, Connection conn){
        
        Vector<String> patientIds = new Vector<String>();
        try{
            String sql = "";
        
            if(filter.equals("centerid")){
                sql = "SELECT DISTINCT center_id,ensat_id FROM Identification WHERE center_id=? ORDER BY center_id,ensat_id;";
            }else if(filter.equals("study")){
                sql = "SELECT DISTINCT center_id,ensat_id FROM Associated_Studies WHERE study_label=? ORDER BY center_id,ensat_id;";
            }else if(filter.equals("type")){
                sql = "SELECT DISTINCT center_id,ensat_id FROM Identification WHERE ensat_database=? ORDER BY center_id,ensat_id;";
            }
            PreparedStatement ps = conn.prepareStatement(sql);
            
            ps.setString(1,tag);
            ResultSet rs = ps.executeQuery();
            int count = 0;
            while(rs.next()){
                String fullEnsatIdIn = "";
                //logger.debug("record number: " + count);
                String centerIdIn = rs.getString(1);
                String ensatIdIn = rs.getString(2);
                
                if(centerIdIn == null){
                    centerIdIn = "";
                }
                if(ensatIdIn == null){
                    ensatIdIn = "";
                }                
                if(!centerIdIn.equals("") && !ensatIdIn.equals("")){
                    fullEnsatIdIn = centerIdIn + "-" + ensatIdIn;
                    //logger.debug("fullEnsatIdIn: " + fullEnsatIdIn);
                    patientIds.add(fullEnsatIdIn);
                }
                count++;
            }            
        }catch(Exception e){
            logger.debug("I/O error (getPatientList): " + e.getMessage());
        }
        
        return patientIds;
    }
    
    public String getXAxisNamesData(Vector<Vector> tablesPercent, boolean retrieveData){
        
        int indexToRetrieve = 0;
        if(retrieveData){
            indexToRetrieve = 1;
        }        
        String xAxisCatNames = "";
        xAxisCatNames = "[";
        for(int i=0; i<tablesPercent.size(); i++){
            Vector<String> tableIn = tablesPercent.get(i);
            String tableName = tableIn.get(indexToRetrieve);
            if(retrieveData){
                xAxisCatNames += tableName + ",";
            }else{
                xAxisCatNames += "'" + tableName + "',";
            }
        }
        //Remove trailing comma
        if(tablesPercent.size() > 0){
            xAxisCatNames = xAxisCatNames.substring(0,xAxisCatNames.length()-1);
        }
        xAxisCatNames += "]";
        return xAxisCatNames;
    }
    
    
    public Vector<String> consolidatePatientList(Vector<String> patientList1, Vector<String> patientList2, Vector<String> patientList3){
        
        Vector<String> patientList = new Vector<String>();
        for(int i=0; i<patientList1.size(); i++){
            String patientIn = patientList1.get(i);
            patientList.add(patientIn);
        }
        
        for(int i=0; i<patientList2.size(); i++){
            String patientIn = patientList2.get(i);
            if(!patientList.contains(patientIn)){
                patientList.add(patientIn);
            }            
        }
        
        for(int i=0; i<patientList3.size(); i++){
            String patientIn = patientList3.get(i);
            if(!patientList.contains(patientIn)){
                patientList.add(patientIn);
            }            
        }
        return patientList;        
    }
}
