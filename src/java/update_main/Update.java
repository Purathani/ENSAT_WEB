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
import javax.servlet.ServletContext;

import javax.servlet.http.HttpServletRequest;

import org.apache.log4j.Level;
import org.apache.log4j.Logger;
import org.apache.log4j.PropertyConfigurator;

/**
 *
 * @author astell
 */
public class Update {

    private static final Logger logger = Logger.getLogger(Update.class);
    private String username = "";

    public Update() {
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
    
    public String getBorderColor(String dbn){
        
        String borderColor = "";
        if(dbn.equals("ACC")){
            borderColor = "green";
        }else if(dbn.equals("Pheo")){
            borderColor = "blue";
        }else if(dbn.equals("NAPACA")){
            borderColor = "yellow";
        }else if(dbn.equals("APA")){
            borderColor = "grey";
        }
        return borderColor;
        
    }
    
    public String getBackgroundColor(String dbn){
        
        String borderColor = "";
        if(dbn.equals("ACC")){
            borderColor = "#CCFFCC";
        }else if(dbn.equals("Pheo")){
            borderColor = "#CCEEFF";
        }else if(dbn.equals("NAPACA")){
            borderColor = "#FFDD99";
        }else if(dbn.equals("APA")){
            borderColor = "#DDDDDD";
        }
        return borderColor;
        
    }

    //private Vector<Vector> getMenuTypes(Statement stmt) {
    private Vector<Vector> getMenuTypes(Connection conn) {

        //Do menu check here
        String menuCheckSql = "SELECT menu_id,menu_type FROM Menu";

        Vector<Vector> menuTypes = new Vector<Vector>();
        try {
            Statement stmt = conn.createStatement();
            ResultSet menuCheckRs = stmt.executeQuery(menuCheckSql);

            while (menuCheckRs.next()) {
                Vector<String> menuFeature = new Vector<String>();
                for (int i = 0; i < 2; i++) {
                    menuFeature.add(menuCheckRs.getString(i + 1));
                }
                menuTypes.add(menuFeature);
            }
        } catch (Exception e) {
            logger.debug("('" + username + "') Error (getMenuTypes): " + e.getMessage());
        }
        return menuTypes;
    }

    public Vector<Vector> getParameters(String[] _tablenames, String pid, String centerid, Connection conn, Connection paramConn, String dbn) {

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
        sql += " ORDER BY param_order_id;";

        Vector<Vector> parameters = new Vector<Vector>();
        try {
            
            PreparedStatement ps = paramConn.prepareStatement(sql);
            ps.setString(1, tablenames[0]);
            if (tablenames.length > 1) {
                for (int i = 1; i < tablenames.length; i++) {
                    ps.setString(i + 1, tablenames[i]);
                }
            }
            ResultSet rs = ps.executeQuery();

            //ResultSet rs = stmt.executeQuery(sql);

            //Get the column number
            ResultSetMetaData rsmd = rs.getMetaData();
            int columnCount = rsmd.getColumnCount();

            //rs = stmt.executeQuery(sql);
            rs = ps.executeQuery();
            int rowCount = 0;
            while (rs.next()) {
                Vector<String> rowIn = new Vector<String>();
                for (int i = 0; i < columnCount; i++) {
                    rowIn.add(rs.getString(i + 1));
                }
                //Now finally check for any values in the request object (for this parameter) and add it here              
                String paramName = rowIn.get(1);
                String tableName = rowIn.get(8);
                
                //logger.debug("paramName (getParameters): " + paramName);

                //THIS FEELS LIKE THE WRONG WAY TO DO THIS... SHOULD DO ONE CALL TO A RESULT-SET (FOR THAT PATIENT) THEN EXTRACT THE RELEVANT PARAMETER
                //Would need to do that per tableName, which I need to think about...

                boolean paramException = paramName.equals("chemotherapy_regimen")
                        || paramName.equals("system_organ")
                        || paramName.equals("presentation_first_tumor")
                        || paramName.equals("hormone_symptoms")
                        || paramName.equals("tumor_symptoms")
                        || paramName.equals("first_diagnosis_tnm")
                        || paramName.equals("malignant_diagnosis_tnm")
                        || paramName.equals("imaging")
                        || paramName.equals("associated_studies")
                        || paramName.equals("metastases_location")
                        || paramName.equals("imaging_location")
                        || paramName.equals("followup_organs")
                        || paramName.equals("radiofrequency_location")
                        || paramName.equals("radiotherapy_location")
                        || paramName.equals("surgery_first")
                        || paramName.equals("surgery_extended")
                        || paramName.equals("normal_tissue_options")
                        || paramName.equals("normal_tissue_paraffin_options")
                        || paramName.equals("normal_tissue_dna_options")
                        ;

                //logger.debug("paramName (in getParameters): " + paramName + " and paramException is: " + paramException);

                String valueIn = "";
                if (!paramException) {
                    valueIn = this.getParameterValues(tableName, pid, centerid, conn, paramName);
                } else {
                    valueIn = this.getMultipleParameterValues(paramName, conn, pid, centerid, dbn);
                }

                //logger.debug("valueIn: " + valueIn);

                if (valueIn == null || valueIn.equals("null")) {
                    valueIn = "";
                }
                rowIn.add(valueIn);

                rowCount++;
                parameters.add(rowIn);
            }


            parameters = this.addCalcFieldValues(parameters, pid, centerid, conn, tablenames);


        } catch (Exception e) {
            logger.debug("('" + username + "') Error (getParameters): " + e.getMessage());
        }

        return parameters;
    }

    public Vector<Vector> getParameters(String[] _tablenames, HttpServletRequest request, Connection paramConn) {

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
        
        //Horrible cludge - this is because I think the ordering on the main tables will screw up if I just put it straight in
        //Need to fix on main tables for this to be properly dealt with
        if(tablenames[0].equals("NAPACA_FollowUp") || tablenames.equals("Pheo_BiochemicalAssessment")){
            sql += " ORDER BY param_order_id;";
        }else{
            sql += ";";
        }

        Vector<Vector> parameters = new Vector<Vector>();
        try {
            
            //Get the menu types here (one call rather than for each parameter)
            Vector<Vector> menuTypes = this.getMenuTypes(paramConn);

            //logger.debug("sql: " + sql);          
            PreparedStatement ps = paramConn.prepareStatement(sql);
            ps.setString(1, tablenames[0]);
            if (tablenames.length > 1) {
                for (int i = 1; i < tablenames.length; i++) {
                    ps.setString(i + 1, tablenames[i]);
                }
            }

            ResultSet rs = ps.executeQuery();
            //ResultSet rs = stmt.executeQuery(sql);

            //Get the column number
            ResultSetMetaData rsmd = rs.getMetaData();
            int columnCount = rsmd.getColumnCount();

            rs = ps.executeQuery();
            int rowCount = 0;
            while (rs.next()) {
                Vector<String> rowIn = new Vector<String>();
                for (int i = 0; i < columnCount; i++) {
                    //logger.debug("rs.getString(i + 1): " + rs.getString(i + 1));
                    rowIn.add(rs.getString(i + 1));
                }
                //Now finally check for any values in the request object (for this parameter) and add it here
                rowIn = this.getIndividualValue(rowIn, request, rs, menuTypes);

                rowCount++;
                parameters.add(rowIn);
            }

            //logger.debug("TEST...");

            parameters = this.addCalcFieldValues(parameters, request, tablenames);

            //logger.debug("TEST 2...");

        } catch (Exception e) {
            logger.debug("('" + username + "') Error (getParameters #2): " + e.getMessage());
        }

        return parameters;
    }

    private Vector<Vector> addCalcFieldValues(Vector<Vector> parameters, HttpServletRequest request, String[] tablenames) {

        //Now try and add any of the calculated fields
        String[] calcParamNames = {"bmi", "ensat_classification"};
        int calcParamNum = calcParamNames.length;
        for (int i = 0; i < calcParamNum; i++) {
            String calcParamValue = request.getParameter(calcParamNames[i]);
            if (calcParamValue != null) {
                //Add a boiler-plate parameter profile
                Vector<String> rowIn = new Vector<String>();
                rowIn.add(""); //ID
                rowIn.add(calcParamNames[i]); //Param name
                rowIn.add(""); //Param type
                rowIn.add(""); //Param text size                    
                rowIn.add(""); //Param label
                rowIn.add(""); //Param order ID
                rowIn.add(""); //Menu
                rowIn.add(""); //Param sub_param
                //Param table
                if (calcParamNames[i].equals("bmi")) {
                    if (tablenames[1] != null) {
                        rowIn.add(tablenames[1]);
                    } else {
                        rowIn.add("");
                    }
                } else if (calcParamNames[i].equals("ensat_classification")) {
                    rowIn.add("ACC_TumorStaging");
                } else {
                    rowIn.add("");
                }
                rowIn.add(""); //Param optional
                rowIn.add(calcParamValue); //Param value                    

                parameters.add(rowIn);
            }
        }
        return parameters;
    }

    private Vector<Vector> addCalcFieldValues(Vector<Vector> parameters, String pid, String centerid, Connection connection, String[] tablenames) {

        boolean runCalcMethod = false;

        //Now try and add any of the calculated fields
        String[] calcParamNames = null;
        if (tablenames[1].equals("NAPACA_DiagnosticProcedures")) {
            runCalcMethod = true;
            calcParamNames = new String[1];
            calcParamNames[0] = "bmi";
        } else if (tablenames[1].equals("ACC_DiagnosticProcedures")) {
            runCalcMethod = true;
            calcParamNames = new String[1];
            calcParamNames[0] = "bmi";
        } else if (tablenames[1].equals("ACC_TumorStaging")) {
            runCalcMethod = true;
            calcParamNames = new String[1];
            calcParamNames[0] = "ensat_classification";
        }

        if (runCalcMethod) {
            int calcParamNum = calcParamNames.length;
            for (int i = 0; i < calcParamNum; i++) {
                String calcParamValue = "";
                try {
                    calcParamValue = this.getParameterValues(tablenames[1], pid, centerid, connection, calcParamNames[i]);
                } catch (Exception e) {
                    logger.debug("('" + username + "') Error (addCalcFieldValues): " + e.getMessage());
                }
                if (calcParamValue != null) {
                    //Add a boiler-plate parameter profile
                    Vector<String> rowIn = new Vector<String>();
                    rowIn.add(""); //ID
                    rowIn.add(calcParamNames[i]); //Param name
                    rowIn.add(""); //Param type
                    rowIn.add(""); //Param text size                    
                    //Param label
                    if (calcParamNames[i].equals("bmi")) {
                        rowIn.add("BMI");
                    } else if (calcParamNames[i].equals("ensat_classification")) {
                        rowIn.add("ENSAT Classification");
                    }
                    rowIn.add(""); //Param order ID
                    rowIn.add(""); //Menu
                    rowIn.add(""); //Param sub_param
                    //Param table
                    if (tablenames[1] != null) {
                        rowIn.add(tablenames[1]);
                    } else {
                        rowIn.add("");
                    }
                    rowIn.add(""); //Param optional
                    rowIn.add(calcParamValue); //Param value                    

                    parameters.add(rowIn);
                }
            }
        }
        return parameters;
    }

    private Vector<String> getIndividualValue(Vector<String> rowIn, HttpServletRequest request, ResultSet rs, Vector<Vector> menuTypes) throws Exception {

        if (request == null) {
            rowIn.add("");
            return rowIn;
        } else {

            String valueIn = "";
            if (rs.getString(3).equals("date")) {
                String dateNameIn = rs.getString(2);

                valueIn = request.getParameter(dateNameIn + "_year") + "-";
                valueIn += request.getParameter(dateNameIn + "_month") + "-";
                valueIn += request.getParameter(dateNameIn + "_day");
                if (valueIn.equals("null-null-null")) {
                    //Try grabbing the date as one component (if it's still null then it doesn't have an embedded value)
                    valueIn = request.getParameter(dateNameIn);
                    if (valueIn == null) {
                        valueIn = "";
                    }
                }
            } else if (rs.getString(3).equals("menu") || rowIn.get(1).equals("associated_studies")) {

                String menuType = "";
                //Run check to see if relevant menu is multiple or single (done at top of method call)

                String menuId = rs.getString(7);
                int menuTypeCount = 0;
                boolean menuFound = false;
                while (menuTypeCount < menuTypes.size() && !menuFound) {
                    Vector<String> menuFeaturesIn = menuTypes.get(menuTypeCount);
                    if (menuFeaturesIn.get(0).equals(menuId)) {
                        menuType =
                                menuFeaturesIn.get(1);
                        menuFound = true;
                    } else {
                        menuTypeCount++;
                    }
                }

                if (rowIn.get(1).equals("associated_studies")) {
                    menuType = "m";
                }

                //If single, then simple parameter grab
                if (menuType.equals("m")) {
                    String[] valuesIn = request.getParameterValues(rs.getString(2));
                    if (valuesIn != null) {
                        int valuesNum = valuesIn.length;
                        for (int i = 0; i < valuesNum; i++) {
                            String indValue = valuesIn[i];
                            if (indValue.equals("Others") || indValue.equals("Metastases - Others")) {
                                String otherParamName = rs.getString(2) + "_others";
                                indValue = request.getParameter(otherParamName);
                            }
                            valueIn += "|" + indValue;
                            //valueIn += "|" + valuesIn[i];
                        }
                    } else {
                        valueIn = "";
                    }
                } else {
                    valueIn = request.getParameter(rs.getString(2));
                }
            } else {
                valueIn = request.getParameter(rs.getString(2));
            }

            //logger.debug("valueIn: " + valueIn);

            if (valueIn == null || valueIn.equals("null")) {
                valueIn = "";
            }
            rowIn.add(valueIn);

            return rowIn;
        }
    }

    public Vector<Vector> getMenus(Vector<Vector> parameters, Connection paramConn) {

        Vector<Vector> menus = new Vector<Vector>();

        Vector<String> menuIDs = new Vector<String>();
        int paramCount = parameters.size();
        for (int i = 0; i < paramCount; i++) {
            Vector<String> rowIn = parameters.get(i);
            String menuIDIn = rowIn.get(6);

            //logger.debug("paramName: " + rowIn.get(1) + ", with menu: " + menuIDIn);            

            if (!menuIDIn.equals("0") && !menuIDs.contains(menuIDIn) && !menuIDIn.equals("")) {
                menuIDs.add(menuIDIn);
            }
        }

        int menuIDnum = menuIDs.size();

        try {
            
            for (int i = 0; i < menuIDnum; i++) {

                Vector<String> menu = new Vector<String>();

                //String sql = "SELECT * FROM Menu, MenuOption WHERE Menu.menu_id=MenuOption.option_menu_id AND Menu.menu_id=" + menuIDs.get(i);
                String sql = "SELECT * FROM Menu, MenuOption WHERE Menu.menu_id=MenuOption.option_menu_id AND Menu.menu_id=?;";
                PreparedStatement ps = paramConn.prepareStatement(sql);
                ps.setString(1, menuIDs.get(i));
                //logger.debug("sql (menus): " + sql);

                ResultSet rs = ps.executeQuery();
                //ResultSet rs = stmt.executeQuery(sql);

                //Grab the first three options on the first run
                if (rs.next()) {
                    for (int j = 1; j < 4; j++) {
                        menu.add(rs.getString(j));
                    }
                    menu.add(rs.getString(5));
                }

                //Only grab the option value on the subsequent runs
                while (rs.next()) {
                    menu.add(rs.getString(5));
                }

                //Finally add the menu vector to the vector of vectors
                menus.add(menu);

                //Close the resultset before next loop iteration
                rs.close();
            }
        } catch (Exception e) {
            logger.debug("('" + username + "') Error (getMenus): " + e.getMessage());
        }
        return menus;
    }

    private String getParameterValues(String tablename, String pid, String centerid, Connection conn, String paramName) throws Exception {

        //String sql = "SELECT " + paramName + " FROM " + tablename + " WHERE ensat_id=" + pid + " AND center_id='" + centerid + "';";
        String sql = "SELECT " + paramName + " FROM " + tablename + " WHERE ensat_id=? AND center_id=?;";
        String paramValue = "";

        PreparedStatement ps = conn.prepareStatement(sql);
        ps.setString(1, pid);
        ps.setString(2, centerid);

        //ResultSet rs = stmt.executeQuery(sql);
        ResultSet rs = ps.executeQuery();

        if (rs.next()) {
            paramValue = rs.getString(1);
        }
        
        //If the parameter is center_id, you may need to check for network_center_name
        if(centerid.equals("NLDAN")){
            String sqlNetwork = "SELECT center_name FROM Network_Center WHERE ensat_id=? AND network_id=?;";
            PreparedStatement psNetwork = conn.prepareStatement(sqlNetwork);
            psNetwork.setString(1, pid);
            psNetwork.setString(2, centerid);
            ResultSet rsNetwork = psNetwork.executeQuery();

            if (rsNetwork.next()) {
                String paramValueNetworkCenter = rs.getString(1);
                paramValue += "|" + paramValueNetworkCenter;
            }        
        }

        return paramValue;
    }

    private Vector<Vector> checkHiddenParams(Vector<Vector> parameters, Vector<String> rowIn) {

        Vector<Vector> childParameters = new Vector<Vector>();
        int paramNum = parameters.size();

        //Make sure and change this for aliquots and freezer info too
        for (int i = 0; i < paramNum - 2; i++) {
            Vector<String> rowCheck = parameters.get(i);
            if (rowCheck.get(7).equals(rowIn.get(0))
                    && !rowIn.get(0).equals("")) {
                childParameters.add(rowCheck);
            }
        }
        return childParameters;
    }

    public String getParameterHtml(Vector<Vector> parameters, Vector<Vector> menus, String lineColour, String dbn, String tablename, Connection conn, String centerid, String pid, String modid, String baseUrl) {

        //logger.debug("TEST 4...");

        String outputStr = "";

        //Get the number of rows here        
        int paramNum = parameters.size();

        //logger.debug("paramNum (within getParameterHtml): " + paramNum);

        //Deal with the aliquots specifically here
        //THIS NEEDS FIXED PROPERLY - MAYBE OVERRIDE THE METHOD IN UPDATESUB?
        int limitIndex = paramNum;
        if (tablename.contains("Biomaterial")) {
            limitIndex = paramNum - 2;
        }

        //logger.debug("paramNum (within getParameterHtml): " + paramNum);

        //logger.debug("aliquotValues: " + aliquotValues);
        //logger.debug("aliquotNum: " + aliquotNum);

        //logger.debug("Makes it beyond aliquot testing...");

        //For each row
        //Run to -1 for aliquot information (-2 when freezer information gets added)
        //for (int i = 0; i < paramNum - 1; i++) {
        for (int i = 0; i < limitIndex; i++) {

            //logger.debug("i: " + i);

            Vector<String> rowIn = parameters.get(i);

            String paramName = rowIn.get(1);
            String paramValue = rowIn.get(10);

            //logger.debug("Param: " + paramName + " (" + paramValue + ") ");

            //Check if the parameter is a parent node
            boolean parentParam = false;
            Vector<Vector> childParameters = this.checkHiddenParams(parameters, rowIn);
            parentParam = !(childParameters.isEmpty());
            String parentJsStr = "";
            String studyJsStr = "";
            String aliquotJsStr = "";
            String parentHtmlStr = "";

            if (this.getAliquotParameter(paramName)) {
                //logger.debug("" + paramName + " is an aliquot parameter: " + this.getAliquotParameter(paramName));
                aliquotJsStr = "showHide('aliquot_" + paramName + "_options',this.value);";
                parentJsStr += aliquotJsStr;
                //String freezerJsStr = "showFreezerInfoUpdate(this.name,1,this.value);";
                //String freezerJsStr = "showFreezerInfoUpdate(this.name,1);";
                String freezerJsStr = "showFreezerAliquotNumberUpdate(this.name,1);";
                parentJsStr += freezerJsStr;
            }

            if (parentParam) {

                String childParameterHtml = this.getChildParameterHtml(parameters, childParameters, menus, lineColour, i, parentJsStr, parentHtmlStr, dbn, conn, centerid, pid, modid, baseUrl);
                parentJsStr = "showHide('myDiv_" + paramName + "_options',this.value);";

                String showHideFlag = "";

                //Make an exception for the surgery form here (but this needs generalised...)
                if (paramName.equals("surgery_type")  
                        //|| paramName.equals("alive")
                        || paramName.equals("tumor_sites")
                        || paramName.equals("associated_study")) {
                    if (!paramValue.equals("")) {
                        showHideFlag = "show";
                    } else {
                        showHideFlag = "hide";
                    }
                }else if (paramName.equals("patient_status")) {                    
                    showHideFlag = "hide";
                    StringTokenizer st = new StringTokenizer(paramValue,"|");
                    while(st.hasMoreTokens()){
                        String valueTokenIn = st.nextToken();
                        if(valueTokenIn.equals("Alive with disease")){
                            showHideFlag = "show";
                        }
                    }
                } else if (paramName.equals("imaging")) {
                    if (paramValue.equals("CT")) {
                        showHideFlag = "show";
                    } else {
                        showHideFlag = "hide";
                    }
                } else if (paramName.equals("gluco_serum_cortisol")
                        || paramName.equals("gluco_plasma_acth")
                        || paramName.equals("gluco_urinary_free_cortisol")
                        || paramName.equals("other_steroid_17hydroxyprogesterone")
                        || paramName.equals("other_steroid_serum_dheas")) {
                    if (!paramValue.equals("Not Done") && !paramValue.equals("")) {
                        showHideFlag = "show";
                    } else {
                        showHideFlag = "hide";
                    }
                } else if(paramName.equals("followup_alive")){
                    if (paramValue.equals("Yes")) {
                        showHideFlag = "hide";
                    } else {
                        showHideFlag = "show";
                    }
                } else if(paramName.equals("ct")
                        || paramName.equals("nmr")
                        || paramName.equals("mibg")
                        || paramName.equals("octreoscan")
                        || paramName.equals("fdg_pet")
                        || paramName.equals("da_pet")
                        || paramName.equals("other_imaging")                        
                        ){
                    if(paramValue.equals("Positive")){
                        showHideFlag = "show";
                    }else{
                        showHideFlag = "hide";
                    }                    
                } else {
                    if (paramValue.equals("Yes")) {
                        showHideFlag = "show";
                    } else {
                        showHideFlag = "hide";
                    }
                }

                parentHtmlStr = "</td></tr><tr><td colspan='2'>" + "<div id=\"myDiv_" + paramName + "_options\" class=\"" + showHideFlag + "\">" + childParameterHtml + "</div>";
            }

            if (paramName.equals("associated_study")) {
                studyJsStr = "study_selection(this.value,'" + dbn + "','" + baseUrl + "');getAssocStudy('" + baseUrl + "',this.value);";
                parentJsStr += studyJsStr;
            }

            boolean subFlag = !rowIn.get(7).equals("0");
            boolean calledFromMain = true;

            //Do something really dodgy here to fix the aliquot thing...
            String aliquotVal = "0";
            if (this.getAliquotParameter(paramName)) {

                //logger.debug("Now retrieving the aliquotValues...");

                //Deal with the aliquots specifically here
                Vector<String> aliquotValues = parameters.get(paramNum - 2);
                int aliquotNum = aliquotValues.size();

                //logger.debug("aliquotValues: " + aliquotValues.toString());
                //logger.debug("aliquotNum: " + aliquotNum);

                //tag on the aliquot values here
                boolean materialFound = false;
                int materialCount = 0;
                while (!materialFound && materialCount < aliquotNum) {
                    String aliquotValueStr = aliquotValues.get(materialCount);
                    StringTokenizer st = new StringTokenizer(aliquotValueStr, "|");
                    if (st.hasMoreTokens()) {
                        String aliquotParam = st.nextToken(); //This will be the material name
                        if (aliquotParam.equals(paramName)) {
                            materialFound = true;
                            aliquotVal = st.nextToken();
                        }
                    }
                    materialCount++;
                }

                //Append the aliquot value to the value in this parameter
                //logger.debug("aliquot value retrieved: " + paramValue + "|" + aliquotVal);
                rowIn.set(10, paramValue + "|" + aliquotVal);
            }
            //logger.debug("TEST...");
            outputStr += this.getIndividualParameterHtml(rowIn, menus, lineColour, i, parentJsStr, parentHtmlStr, subFlag, calledFromMain, dbn, conn, centerid, pid, modid, baseUrl);


            if (this.getAliquotParameter(paramName)) {

                //logger.debug("into Aliquot clause (" + paramName + ")...");
                //outputStr += "<td colspan='3'>";                
                for (int j = 0; j < 9; j++) {
                    //logger.debug("aliquotVal: " + aliquotVal);
                    boolean showHide = j < Integer.parseInt(aliquotVal);
                    //logger.debug("showHide: " + showHide);
                    outputStr += "<tr>";
                    //logger.debug("BEFORE freezer information...");
                    outputStr += "<td colspan='3'>" + this.getFreezerInfo(paramName, showHide, parameters.get(paramNum - 1), j + 1) + "</td>";
                    //logger.debug("AFTER freezer information...");
                    //outputStr += this.getFreezerInfo(paramName, showHide, parameters.get(paramNum - 1), j+1) + "<br/>";                                    
                    outputStr += "</tr>";
                    //logger.debug("TESTING ALIQUOT CLAUSE...");
                }
            } else if (this.getNormalTissueParameter(paramName)) {

                //logger.debug("paramValue (within normal tissue clause): " + paramValue);
                Vector<String> freezerValues = parameters.get(paramNum - 1);
                Vector<String> aliquotValues = parameters.get(paramNum - 2);
                int aliquotNum = aliquotValues.size();
                //logger.debug("freezerValues (within normal tissue clause): " + freezerValues);
                //logger.debug("aliquotValues (within normal tissue clause): " + aliquotValues);

                //THIS NEEDS TO BE CONTROLLED USING THE VALUES (DISSECT FROM VALUE (10))


                String[] normalTissueParamLabels = {"adjacentadrenal", "kidney", "liver", "lung", "lymphnode", "fatperiadrenal", "fatsubcutaneous", "others"};
                String[] normalTissueLabels = {"Adjacent Adrenal", "Kidney", "Liver", "Lung", "Lymph Node", "Fat (Periadrenal)", "Fat (Subcutaneous)", "Others"};
                int normalTissueOptionNum = normalTissueLabels.length;
                for (int k = 0; k < normalTissueOptionNum; k++) {

                    boolean showHide = false;

                    //CHANGE THIS TO USE THE ALIQUOT NUMBER
                    String innerParamName = paramName + "_" + normalTissueParamLabels[k];
                    String innerParamLabel = normalTissueLabels[k];

                    boolean materialFound = false;
                    int materialCount = 0;

                    while (!materialFound && materialCount < aliquotNum) {
                        String aliquotValueStr = aliquotValues.get(materialCount);
                        StringTokenizer st = new StringTokenizer(aliquotValueStr, "|");
                        if (st.hasMoreTokens()) {
                            String aliquotParam = st.nextToken(); //This will be the material name
                            if (aliquotParam.equals(innerParamName)) {
                                showHide = true;
                                materialFound = true;
                                aliquotVal = st.nextToken();
                            }
                        }
                        materialCount++;
                    }
                    if (!materialFound) {
                        aliquotVal = "0";
                    }

                    outputStr += "<tr>";
                    outputStr += "<th colspan='3'>";
                    String showHideStr = "";
                    if (showHide) {
                        showHideStr = "show";
                    } else {
                        showHideStr = "hide";
                    }
                    outputStr += "<div id='" + innerParamName + "_showhide' class='" + showHideStr + "'>";
                    outputStr += innerParamLabel;
                    outputStr += " ";
                    outputStr += this.getAliquotMenu(innerParamName, aliquotVal);
                    outputStr += "</div>";
                    outputStr += "</th>";
                    outputStr += "</tr>";

                    for (int j = 0; j < 9; j++) {
                        boolean showHideFreezerLine = j < Integer.parseInt(aliquotVal);
                        outputStr += "<tr>";
                        outputStr += "<td colspan='3'>" + this.getFreezerInfo(innerParamName, showHideFreezerLine, parameters.get(paramNum - 1), j + 1) + "</td>";
                        outputStr += "</tr>";
                    }

                }
            }

        }


        return outputStr;
    }

    private String getChildParameterHtml(Vector<Vector> parameters, Vector<Vector> childParameters, Vector<Vector> menus, String lineColour, int index, String parentJsStr, String parentHtmlStr, String dbn, Connection conn, String centerid, String pid, String modid, String baseUrl) {

        String outputStr = "";

        //Get the number of rows here        
        int paramNum = childParameters.size();

        //subflag is always false, as we are rendering the child parameters in this call
        boolean subFlag = false;
        boolean calledFromMain = false;

        //For each row        
        for (int i = 0; i < paramNum; i++) {
            Vector<String> rowIn = childParameters.get(i);

            String paramName = rowIn.get(1);
            String paramValue = rowIn.get(10);

            //GOING TO TRY A RECURSIVE SOLUTION HERE FOR THE RENDERING OF SUB-MENUS OF DEPTH > 1
            //CAREFUL OF THE parentJsStr AND parentHtmlStr STRINGS...
            //METHOD NEEDS ACCESS TO THE FULL PARAMETER SET, NOT JUST THE CHILD PARAMETERS OF THE PARAMETER ABOVE
            Vector<Vector> childChildParameters = this.checkHiddenParams(parameters, rowIn);
            boolean parentParam = !(childChildParameters.isEmpty());
            if (parentParam) {
                String childChildParameterHtml = this.getChildParameterHtml(parameters, childChildParameters, menus, lineColour, i, parentJsStr, parentHtmlStr, dbn, conn, centerid, pid, modid, baseUrl);
                parentJsStr += "showHide('myDiv_" + paramName + "_options',this.value);";

                String showHideFlag = "";
                if (paramValue.equals("Yes")) {
                    showHideFlag = "show";
                } else {
                    showHideFlag = "hide";
                }
                
                if(paramValue.equals("Positive")                        
                        && (paramName.equals("ct")
                        || paramName.equals("nmr")
                        || paramName.equals("mibg")
                        || paramName.equals("octreoscan")
                        || paramName.equals("fdg_pet")
                        || paramName.equals("da_pet")
                        || paramName.equals("other_imaging")                        
                        )){
                    showHideFlag = "show";
                }
                
                parentHtmlStr = "</td></tr><tr><td colspan='2'>" + "<div id=\"myDiv_" + paramName + "_options\" class=\"" + showHideFlag + "\">" + childChildParameterHtml + "</div>";
            }
            //End of new stuff (recursive solution)

            //outputStr += this.getIndividualParameterHtml(rowIn, menus, lineColour, i, parentJsStr, parentHtmlStr, subFlag, calledFromMain, dbn);




            outputStr += this.getIndividualParameterHtml(rowIn, menus, lineColour, i, parentJsStr, parentHtmlStr, subFlag, calledFromMain, dbn, conn, centerid, pid, modid, baseUrl);

        }
        return outputStr;
    }

    private String getIndividualParameterHtml(Vector<String> rowIn, Vector<Vector> menus, String lineColour, int index, String parentJsStr, String parentHtmlStr, boolean subFlag, boolean calledFromMain, String dbn, Connection conn, String centerid, String pid, String modid, String baseUrl) {

        String paramName = rowIn.get(1);
        String paramOptional = rowIn.get(9);
        String tableName = rowIn.get(8);

        if (paramName.equals("androgens")
                || paramName.equals("estrogens")
                || paramName.equals("mineralocorticoids")
                || paramName.equals("precursor_secretion")) {
            parentJsStr = "";
            parentHtmlStr = "";
        }

        //logger.debug("paramName (getIndividualParameterHtml): " + paramName);

        //Immediately deal with the aliquot value
        String aliquotValue = "";
        if (this.getAliquotParameter(paramName)) {
            String paramValue = rowIn.get(10);
            int pipeIndex = paramValue.indexOf("|");
            if (pipeIndex != -1 && (pipeIndex != paramValue.length())) {
                aliquotValue = paramValue.substring(pipeIndex + 1, paramValue.length());
                paramValue = paramValue.substring(0, pipeIndex);
                rowIn.set(10, paramValue);
            }
        }

        //Get the number of menus here
        int menuNum = menus.size();

        String outputStr = "";

        //Case for an empty value vector here
        String valueIn = "";
        if (rowIn.get(10) != null) {
            valueIn = rowIn.get(10);
        }

        //List the parameters that should not be rendered here
        boolean exception = false;
        exception = /*
                 * paramName.equals("local_investigator") ||
                 * paramName.equals("investigator_email") ||
                 */ (paramName.equals("eurine_act_inclusion") && dbn.equals("Pheo"))
                || (paramName.equals("eurine_act_inclusion") && dbn.equals("APA"));

        //Exceptional addition which needs to be generalised to all multiple output parent fields
        if (paramName.equals("alive")) {
            //parentJsStr += "showHide('myDiv_alive_options_2',this.value);";
        } else if (paramName.equals("date_of_death") && dbn.equals("Pheo")) {
            //outputStr += "<tr><td colspan='2'><div id=\"myDiv_alive_options_2\" class=\"hide\"><table width=\"100%\">";
            //outputStr += "<tr><td colspan='2'><div id=\"myDiv_alive_options_2\" class=\"hide\">";
            //outputStr += "<tr><td colspan='2'><div id=\"myDiv_followup_alive_options_2\" class=\"hide\"><table width=\"100%\">";
        }


        //Exceptional Pheo_Genetics headings in here
        if (tableName.equals("Pheo_Genetics")) {
            if (paramName.contains("_testing_performed")) {
                outputStr += this.addGeneticHeaders(paramName);
            }
        }
        
            //Exceptional Pheo_Biological_Assessment headings in here
            if (paramName.equals("hemoglobin_results")) {
                outputStr += "<tr><th colspan='2'>Hemoglobin [Hgb] (g/dl)</th></tr>";
            }else if (paramName.equals("platelets_results")) {
                outputStr += "<tr><th colspan='2'>Platelets (10<sup>9</sup>/L)</th></tr>";
            }else if (paramName.equals("leukocytes_results")) {
                outputStr += "<tr><th colspan='2'>Leukocytes [total WBC] (10<sup>9</sup>/L)</th></tr>";
            }else if (paramName.equals("neutrophils_results")) {
                outputStr += "<tr><th colspan='2'>Neutrophils/Granulocytes [ANC/AGC] (10<sup>9</sup>/L)</th></tr>";
            }else if (paramName.equals("creatinine_results")) {
                outputStr += "<tr><th colspan='2'>Creatinine (&micro;mol/L)</th></tr>";
            }else if (paramName.equals("sgpt_results")) {
                outputStr += "<tr><th colspan='2'>SGPT [ALT] (IU/L)</th></tr>";
            }else if (paramName.equals("sgot_results")) {
                outputStr += "<tr><th colspan='2'>SGOT [AST] (IU/L)</th></tr>";
            }else if (paramName.equals("alkalinephos_results")) {
                outputStr += "<tr><th colspan='2'>Alkaline phosphatase (IU/L)</th></tr>";
            }else if (paramName.equals("bilirubin_results")) {
                outputStr += "<tr><th colspan='2'>Total bilirubin (&micro;mol/L)</th></tr>";
            }else if (paramName.equals("protein_results")) {
                outputStr += "<tr><th colspan='2'>Total protein (g/L)</th></tr>";
            }else if (paramName.equals("albuminemia_results")) {
                outputStr += "<tr><th colspan='2'>Albuminemia (g/L)</th></tr>";
            }else if (paramName.equals("glycemia_results")) {
                outputStr += "<tr><th colspan='2'>Glycemia (mmol/L)</th></tr>";
            }else if (paramName.equals("ldh_results")) {
                outputStr += "<tr><th colspan='2'>LDH (IU/L)</th></tr>";
            }else if (paramName.equals("calcium_results")) {
                outputStr += "<tr><th colspan='2'>Calcium (mmol/L)</th></tr>";
            }
            
            
            //Exceptional Pheo_Biochemical headings in here
            if (paramName.equals("plasma_e")) {
                outputStr += "<tr><th colspan='2'>Plasma</th></tr>";
            }else if (paramName.equals("plasma_free_m")) {
                outputStr += "<tr><th colspan='2'>Plasma-free</th></tr>";
            }else if (paramName.equals("serum_chromo_a")) {
                outputStr += "<tr><th colspan='2'>Serum Chromogranin A</th></tr>";
            }else if (paramName.equals("urine_free_e")) {
                outputStr += "<tr><th colspan='2'>Urinary-free</th></tr>";
            }else if (paramName.equals("urine_m")) {
                outputStr += "<tr><th colspan='2'>Urinary</th></tr>";
            }else if (paramName.equals("plasma_dopamine_conc")) {
                outputStr += "<tr><th colspan='2'>Plasma Dopamine</th></tr>";
            }

        if (!exception) {

            //If the parameter is a child, don't render it
            if (subFlag) {
                return outputStr;
            }

            //Run another encapsulating div here for those that have multiple selections based on input from parent
            if (this.getMultipleHiddenMenuType(paramName)) {

                //logger.debug("Checking multiple hidden values (" + paramName + "): " + valueIn);                

                String showHideFlag = "hide";
                if (!valueIn.equals("")) {
                    showHideFlag = "show";
                }
                outputStr += "<div id='" + paramName + "_mult' class='" + showHideFlag + "'>";
            }

            if (!calledFromMain) {
                outputStr += "<table width=\"100%\">";
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

            if (rowIn.get(2).equals("text") || rowIn.get(2).equals("number")) {
                outputStr += "<input name=\"" + rowIn.get(1) + "\" type=\"text\" size=\"" + rowIn.get(3) + "\" value=\"" + valueIn + "\" onfocus=\"inform=true;\" onblur=\"parameterValidate(this.value,this.name," + paramOptional + ",'" + rowIn.get(2) + "','" + baseUrl + "');inform=false;\" onchange=\"" + parentJsStr + "\"/><div id=\"" + rowIn.get(1) + "\"></div>";
            } else if (rowIn.get(2).equals("date")) {

                //Change date format back to European
                valueIn = this.reformatDateValueEuropean(valueIn);
                outputStr += "<input name=\"" + paramName + "\" type=\"text\"  class=\"datepicker\" id=\"" + paramName + "_id\" size=\"30\" value=\"" + valueIn + "\" onfocus=\"inform=true;\" onblur=\"inform=false;\" onchange=\"parameterValidate(this.value,this.name," + paramOptional + ",'" + rowIn.get(2) + "');" + parentJsStr + "\" /><div id=\"" + rowIn.get(1) + "\"></div>";

            } else if (rowIn.get(2).equals("menu") || rowIn.get(2).equals("dynamicmenuonload")) {

                if (paramName.equals("center_id")) {
                    
                    //Add in bracket for network center name
                    if(valueIn.startsWith("NLDAN")){
                        int pipeIndex = valueIn.indexOf("|");
                        String networkCenterName = valueIn.substring(pipeIndex+1,valueIn.length());
                        String networkName = valueIn.substring(0,pipeIndex);
                        outputStr += "<strong>" + networkName + " (" + networkCenterName + ")</strong>";
                    }else{
                        outputStr += "<strong>" + valueIn + "</strong>";
                    }
                    outputStr += "<input type='hidden' name='" + paramName + "' value='" + valueIn + "'/>";
                } else if (paramName.equals("associated_studies")) {

                    StringTokenizer st = new StringTokenizer(valueIn, "|");
                    int tokenNum = st.countTokens();
                    String[] valuesIn = new String[tokenNum];
                    int tokenCount = 0;
                    while (st.hasMoreTokens()) {
                        valuesIn[tokenCount] = st.nextToken();
                        valuesIn[tokenCount] = this.getStudyLabel(valuesIn[tokenCount]);
                        tokenCount++;
                    }

                    //Grab the last menu (which will be associated_studies on the appropriate page
                    Vector<String> menuIn = new Vector<String>();
                    menuIn = menus.get(menuNum - 1);

                    String menuSelectStr = "";
                    String menuHeaderStr = "";

                    menuHeaderStr += "<div class=\"scroll_checkboxes\">";

                    int menuSize = menuIn.size();
                    for (int k = 3; k < menuSize; k++) {

                        menuSelectStr += "<input name=\"" + paramName + "\" type=\"checkbox\" ";

                        for (int m = 0; m < tokenNum; m++) {
                            if (valuesIn[m].equals(menuIn.get(k))) {
                                menuSelectStr += " checked ";
                            }
                        }


                        menuSelectStr += " value=\"" + this.getStudyName(menuIn.get(k)) + "\" ";
                        menuSelectStr += "onclick='showAssocStudyIDs(this.value);'";
                        menuSelectStr += " />" + menuIn.get(k) + "<br/>";
                        //menuSelectStr += " value=\"" + menuIn.get(k) + "\" />" + menuIn.get(k) + "<br/>";
                    }

                    outputStr += menuHeaderStr;
                    outputStr += menuSelectStr;

                    outputStr += "</div>";
                    outputStr += "<div id=\"" + paramName + "\"></div>";

                    //Now put in the incidental ID stuff
                    outputStr += "<table width='100%'>";
                    try {
                        String idsPresentSql = "SELECT study_name,study_identifier FROM Associated_Studies WHERE center_id=? AND ensat_id=?;";
                        PreparedStatement psId = conn.prepareStatement(idsPresentSql);
                        psId.setString(1, centerid);
                        psId.setString(2, pid);
                        ResultSet rsId = psId.executeQuery();

                        Vector<Vector> ids = new Vector<Vector>();
                        while (rsId.next()) {
                            String studyNameIn = rsId.getString(1);
                            String idIn = rsId.getString(2);
                            Vector<String> nameIdIn = new Vector<String>();
                            nameIdIn.add(studyNameIn);
                            nameIdIn.add(idIn);
                            ids.add(nameIdIn);
                        }

                        String studyIdSql = "SELECT Studies.study_name,Studies.study_label FROM Studies,Study_Type WHERE Studies.study_id=Study_Type.study_id AND Study_Type.tumor_type=? AND separate_id='true';";
                        PreparedStatement ps = conn.prepareStatement(studyIdSql);
                        ps.setString(1, dbn);
                        ResultSet rs = ps.executeQuery();

                        while (rs.next()) {
                            outputStr += "<tr><td>";
                            String studyName = rs.getString(1);
                            String studyLabel = rs.getString(2);

                            boolean studyFound = false;
                            int studyCount = 0;
                            String studyIDValue = "";
                            while (!studyFound && studyCount < ids.size()) {
                                String studyNameIn = (String) ids.get(studyCount).get(0);
                                if (studyNameIn.equals(studyName)) {
                                    studyFound = true;
                                    studyIDValue = (String) ids.get(studyCount).get(1);
                                } else {
                                    studyCount++;
                                }
                            }
                            String showHideFlag = "hide";
                            if (studyFound) {
                                showHideFlag = "true";
                            }
                            outputStr += "<div class='" + showHideFlag + "' id='" + studyName + "_id_option'>";
                            outputStr += "" + studyLabel + " ID: ";

                            outputStr += "<input type='text' size='6' name='" + studyName + "_id' value='" + studyIDValue + "' onfocus=\"inform=true;\" onblur=\"inform=false;\"/>";
                            outputStr += "</div>";
                            outputStr += "</td></tr>";
                        }

                    } catch (Exception e) {
                        logger.debug("Error: " + e.getMessage());
                    }
                    outputStr += "</table>";

                    outputStr += parentHtmlStr;


                } else {

                    String menuID = rowIn.get(6);
                    boolean menuFound = false;

                    int j = 0;
                    Vector<String> menuIn = new Vector<String>();
                    while (j < menuNum && !menuFound) {
                        menuIn = menus.get(j);
                        if (menuIn.size() != 0) {
                            menuFound = menuIn.get(0).equals(menuID);
                        }
                        j++;
                    }

                    String menuSelectStr = "";
                    String menuHeaderStr = "";
                    String otherStr = "";
                    if (menuIn.size() != 0) {

                        if (rowIn.get(1).equals("associated_study_phase_visit")) {

                            menuHeaderStr += "<div id=\"associated_study_menus\">";

                            String study = this.getStudy(centerid, pid, modid, dbn, conn);
                            String phaseVisit = rowIn.get(10);

                            if (study.equals("PMT")
                                    || study.equals("FIRST-MAPPP")
                                    || study.equals("German Cushing Registry")
                                    || study.equals("German Conn Registry")) {
                                menuSelectStr = this.getAssocStudyMenu(study, phaseVisit);
                            }

                            menuSelectStr += "</div>";

                        } else if (menuIn.get(2).equals("m")) {

                            //Adding in the JS trigger for the normal tissue types and options                    
                            if (paramName.equals("normal_tissue_options")
                                    || paramName.equals("normal_tissue_paraffin_options")
                                    || paramName.equals("normal_tissue_dna_options")) {
                                parentJsStr = "normalTissueAliquotShow(" + paramName + ");";
                            }

                            //menuHeaderStr += "<select multiple name=\"" + paramName + "\" onblur=\"parameterValidate(this.value,this.name," + paramOptional + ",'" + rowIn.get(2) + "');" + parentJsStr + "\">";
                            menuHeaderStr += "<div class=\"scroll_checkboxes\">";

                            //Need to tokenize valueIn to an array here (for multiple options)
                            StringTokenizer st = new StringTokenizer(valueIn, "|");
                            int tokenNum = st.countTokens();
                            String[] valuesIn = new String[tokenNum];

                            int tokenCount = 0;
                            while (st.hasMoreTokens()) {
                                valuesIn[tokenCount] = st.nextToken();
                                tokenCount++;
                            }

                            int menuSize = menuIn.size();
                            for (int k = 3; k < menuSize; k++) {

                                //Have a separate option to pull out the "Others" options that are represented
                                if (menuIn.get(k).equals("Others")) {
                                    menuSelectStr += "<input name=\"" + paramName + "\" type=\"checkbox\" ";
                                    boolean otherFound = false;
                                    int otherCount = 0;
                                    String otherToken = "";
                                    while (otherCount < tokenNum && !otherFound) {
                                        if (!menuIn.contains(valuesIn[otherCount])) {
                                            menuSelectStr += " checked ";
                                            otherToken = valuesIn[otherCount];
                                            otherFound = true;
                                        } else {
                                            otherCount++;
                                        }
                                    }
                                    //menuSelectStr += " value=\"" + menuIn.get(k) + "\">" + menuIn.get(k) + "</option>";
                                    menuSelectStr += " value=\"" + menuIn.get(k) + "\" />" + menuIn.get(k) + "<br/>";

                                    //Now add the "other" specific text-field
                                    otherStr = "Others (please specify): <input name='" + paramName + "_others' value='" + otherToken + "' type='text' size='15' onfocus='inform=true;' onblur='inform=false;'/>";

                                } else {
                                    //menuSelectStr += "<option";
                                    menuSelectStr += "<input name=\"" + paramName + "\" type=\"checkbox\" ";

                                    for (int m = 0; m < tokenNum; m++) {
                                        if (valuesIn[m].equals(menuIn.get(k))) {
                                            menuSelectStr += " checked ";
                                        }
                                    }

                                    String multFreezerStr = "";
                                    if (paramName.equals("normal_tissue_options")
                                            || paramName.equals("normal_tissue_paraffin_options")
                                            || paramName.equals("normal_tissue_dna_options")) {
                                        multFreezerStr = "onchange=\"showNormalTissueFreezerHeaderInfoUpdate(this.name,this.value,this.checked);\"";
                                    }
                                    menuSelectStr += " value=\"" + menuIn.get(k) + "\" " + multFreezerStr + " />" + menuIn.get(k) + "<br/>";

                                    //menuSelectStr += " value=\"" + menuIn.get(k) + "\">" + menuIn.get(k) + "</option>";
                                    //menuSelectStr += " value=\"" + menuIn.get(k) + "\" />" + menuIn.get(k) + "<br/>";
                                }



                            }
                        } else {

                            menuHeaderStr += "<select name=\"" + paramName + "\" onblur=\"parameterValidate(this.value,this.name," + paramOptional + ",'" + rowIn.get(2) + "');\" onchange=\"" + parentJsStr + "\">";

                            if (!paramName.equals("associated_study")) {

                                menuSelectStr += "<option value=\"\">[Select...]</option>";
                                int menuSize = menuIn.size();
                                for (int k = 3; k < menuSize; k++) {
                                    menuSelectStr += "<option";
                                    if (valueIn.equals(menuIn.get(k))) {
                                        menuSelectStr += " selected ";
                                    }
                                    menuSelectStr += " value=\"" + menuIn.get(k) + "\">" + menuIn.get(k) + "</option>";
                                }

                            } else {
                                Vector<String> studyOptions = new Vector<String>();
                                studyOptions.add("ENS@T Biobank");
                                if (dbn.equals("ACC")) {
                                    studyOptions.add("ADIUVO");
                                    studyOptions.add("ADIUVO Observation");
                                    studyOptions.add("EURINE-ACT");
                                    studyOptions.add("Tissue Microarray");
                                } else if (dbn.equals("Pheo")) {
                                    studyOptions.add("Tissue Microarray");
                                    studyOptions.add("PMT");
                                    studyOptions.add("PMT3");
                                    studyOptions.add("PMT Candidate");
                                    studyOptions.add("Metabolomics");
                                    studyOptions.add("FIRST-MAPPP");
                                    studyOptions.add("MAPP-Prono");
                                } else if (dbn.equals("NAPACA")) {
                                    studyOptions.add("PMT");
                                    studyOptions.add("PMT3");
                                    studyOptions.add("CHIRACIC");
                                    studyOptions.add("German Cushing Registry");
                                    studyOptions.add("EURINE-ACT");
                                } else if (dbn.equals("APA")) {
                                    studyOptions.add("PMT");
                                    studyOptions.add("German Conn Registry");
                                    studyOptions.add("Metabolomics");
                                    studyOptions.add("EURINE-ACT");
                                }
                                studyOptions.add("No biomaterial available");

                                menuSelectStr += "<option value=\"\">[Select...]</option>";
                                for (int n = 0; n < studyOptions.size(); n++) {
                                    String menuItem = studyOptions.get(n);
                                    menuSelectStr += "<option ";
                                    if (valueIn.equals(menuItem)) {
                                        menuSelectStr += "selected";
                                    }
                                    menuSelectStr += " value=\"" + menuItem + "\">" + menuItem + "</option>";
                                }



                            }
                        }
                    }

                    outputStr += menuHeaderStr;
                    outputStr += menuSelectStr;

                    outputStr += "</select>";
                    outputStr += "<div id=\"" + paramName + "\"></div>";
                    outputStr += otherStr;

                    outputStr += parentHtmlStr;

                }
            } else {
                outputStr += "<strong>" + valueIn + "</strong>";
                outputStr += "<input type='hidden' name='" + paramName + "' value='" + valueIn + "'/>";
            }
            outputStr += "</td>";

            if (this.getAliquotParameter(paramName)) {
                boolean showHide = valueIn.equals("Yes");
                if (!showHide) {
                    aliquotValue = "";
                }
                outputStr += "<td>" + this.getAliquotInfo(paramName, showHide, aliquotValue) + "</td>";
            }
            outputStr += "</tr>";

            //This is the "alive" multiple options thing - needs generalised
            //if (paramName.equals("cause_of_death") && dbn.equals("Pheo")) {
            /*if (paramName.equals("disease_state") && dbn.equals("Pheo")) {
                //outputStr += "</table></div></td></tr>";
                outputStr += "</div></td></tr>";
            }*/

            if (!calledFromMain) {
                //if(!paramName.equals("death_related_tumor")){
                    outputStr += "</table>";
                //}
            }

            //Run another encapsulating div here for those that have multiple selections based on input from parent
            if (this.getMultipleHiddenMenuType(paramName)) {
                outputStr += "</div>";
            }

            return outputStr;
        } else {
            return "";
        }
    }

    public String getLastPageParamHtml(Vector<Vector> parameters, HttpServletRequest request) {

        Vector<String> lastPageParamNames = new Vector<String>();
        Vector<String> lastPageParamValues = new Vector<String>();

        int paramSize = parameters.size();

        for (int i = 0; i < paramSize; i++) {
            Vector<String> paramIn = parameters.get(i);
            lastPageParamNames.add(paramIn.get(1));
            lastPageParamValues.add(paramIn.get(10));
        }

        String outputStr = "";

        outputStr += "<table border=\"1px\" width=\"75%\">";
        outputStr += "<tr>";
        outputStr += "<td>";

        outputStr += this.getHiddenParams(lastPageParamNames, lastPageParamValues, request);

        outputStr += "</td>";
        outputStr += "</tr>";
        outputStr += "</table>";
        return outputStr;
    }

    public String getHiddenParams(Vector<String> lastPageParamNames, Vector<String> lastPageParamValues, HttpServletRequest request) {

        String outputStr = "";
        int paramNum = lastPageParamNames.size();
        for (int i = 0; i < paramNum; i++) {
            boolean parameterIsMultiple = lastPageParamNames.get(i).equals("associated_studies");

            if (parameterIsMultiple) {
                StringTokenizer st = new StringTokenizer(lastPageParamValues.get(i), "|");
                int tokenNum = st.countTokens();
                String multipleParamName = lastPageParamNames.get(i);
                int tokenCount = 1;
                boolean cdeFlag = false;
                while (st.hasMoreTokens()) {
                    String tokenValueIn = st.nextToken();
                    if (tokenValueIn.equals("EDP")) {
                        String[] tokenValuesIn = {"Cisplatin (P)", "Doxorubicin", "Etoposide"};
                        for (int m = 0; m < 3; m++) {
                            outputStr += "<input type=\"hidden\" name=\"" + multipleParamName + "_" + tokenCount + "\" value=\"" + tokenValuesIn[m] + "\"/>";
                            tokenCount++;
                        }
                        cdeFlag = true;
                    } else {
                        outputStr += "<input type=\"hidden\" name=\"" + multipleParamName + "_" + tokenCount + "\" value=\"" + tokenValueIn + "\"/>";
                        tokenCount++;
                    }
                }
                if (cdeFlag) {
                    tokenNum = tokenNum + 2;
                }
                outputStr += "<input type=\"hidden\" name=\"" + multipleParamName + "_num\" value=\"" + tokenNum + "\"/>";

                //Add the capture for the incidental IDs here
                String[] otherStudyIDs = {"adiuvo",
                    "adiuvo_observational",
                    "lysosafe",
                    "firstmappp",
                    "german_cushing",
                    "german_conn"};
                int otherStudyNum = otherStudyIDs.length;
                for (int j = 0; j < otherStudyNum; j++) {
                    String otherStudyParamName = "" + otherStudyIDs[j] + "_id";
                    String otherStudyParamValue = request.getParameter(otherStudyParamName);
                    if (otherStudyParamValue != null) {
                        outputStr += "<input type=\"hidden\" name=\"" + otherStudyParamName + "\" value=\"" + otherStudyParamValue + "\"/>";
                    }
                }
            } else {
                outputStr += "<input type=\"hidden\" name=\"" + lastPageParamNames.get(i) + "\" value=\"" + lastPageParamValues.get(i) + "\"/>";
            }
        }
        return outputStr;
    }

    public String getLastPageParamConfirmHtml(Vector<Vector> parameters, String lineColour, String dbn, HttpServletRequest request) {

        Vector<String> lastPageParamNames = new Vector<String>();
        Vector<String> lastPageParamValues = new Vector<String>();
        Vector<String> lastPageParamLabels = new Vector<String>();
        Vector<String> lastPageParamTables = new Vector<String>();

        int paramSize = parameters.size();

        for (int i = 0; i < paramSize; i++) {
            Vector<String> paramIn = parameters.get(i);

            lastPageParamNames.add(paramIn.get(1));
            lastPageParamLabels.add(paramIn.get(4));
            lastPageParamTables.add(paramIn.get(8));
            lastPageParamValues.add(paramIn.get(10));
        }

        String outputStr = "";
        outputStr += "<table border=\"1px\" width=\"75%\" cellpadding=\"5\">";

        int paramNum = lastPageParamNames.size();
        for (int i = 0; i < paramNum; i++) {

            //Test for multiple options here
            boolean parameterIsMultiple = lastPageParamNames.get(i).equals("system_organ")
                    || lastPageParamNames.get(i).equals("presentation_first_tumor")
                    || lastPageParamNames.get(i).equals("tumor_symptoms")
                    || lastPageParamNames.get(i).equals("hormone_symptoms")
                    || lastPageParamNames.get(i).equals("malignant_diagnosis_tnm")
                    || lastPageParamNames.get(i).equals("first_diagnosis_tnm")
                    || lastPageParamNames.get(i).equals("imaging")
                    || lastPageParamNames.get(i).equals("metastases_location")
                    || lastPageParamNames.get(i).equals("imaging_location")
                    || lastPageParamNames.get(i).equals("associated_studies")
                    || lastPageParamNames.get(i).equals("chemotherapy_regimen")
                    || lastPageParamNames.get(i).equals("followup_organs")
                    || lastPageParamNames.get(i).equals("radiofrequency_location")
                    || lastPageParamNames.get(i).equals("radiotherapy_location")
                    || lastPageParamNames.get(i).equals("surgery_extended")
                    || lastPageParamNames.get(i).equals("surgery_first")
                    || lastPageParamNames.get(i).equals("nmr_location")
                    || lastPageParamNames.get(i).equals("ct_location")
                    || lastPageParamNames.get(i).equals("preop_blockade_agents")
                    || lastPageParamNames.get(i).equals("intraop_bp_control_agents")                    
                    || lastPageParamNames.get(i).equals("normal_tissue_options")
                    || lastPageParamNames.get(i).equals("normal_tissue_paraffin_options")
                    || lastPageParamNames.get(i).equals("normal_tissue_dna_options");

            boolean parameterIsAldoConversion = lastPageParamNames.get(i).equals("standing_aldosterone")
                    || lastPageParamNames.get(i).equals("sitting_aldosterone")
                    || lastPageParamNames.get(i).equals("urinary_aldosterone")
                    || lastPageParamNames.get(i).equals("post_captopril_aldosterone")
                    || lastPageParamNames.get(i).equals("post_oral_sodium_aldosterone")
                    || lastPageParamNames.get(i).equals("post_saline_infusion_aldosterone")
                    || lastPageParamNames.get(i).equals("post_fludrocorticone_aldosterone")
                    || lastPageParamNames.get(i).equals("post_furosemide_aldosterone")
                    || lastPageParamNames.get(i).equals("aldosterone_right")
                    || lastPageParamNames.get(i).equals("aldosterone_left")
                    || lastPageParamNames.get(i).equals("aldosterone_vena_cava");

            boolean parameterIsCortisolConversion = lastPageParamNames.get(i).equals("corticol_right")
                    || lastPageParamNames.get(i).equals("corticol_left")
                    || lastPageParamNames.get(i).equals("corticol_vena_cava");

            boolean parameterIsPlasmaConversion = lastPageParamNames.get(i).equals("plasma_e")
                    || lastPageParamNames.get(i).equals("plasma_n")
                    || lastPageParamNames.get(i).equals("plasma_free_m")
                    || lastPageParamNames.get(i).equals("plasma_free_n")
                    || lastPageParamNames.get(i).equals("plasma_free_methox")
                    || lastPageParamNames.get(i).equals("plasma_dopamine_conc");

            boolean parameterIsUrineConversion = lastPageParamNames.get(i).equals("urine_free_e")
                    || lastPageParamNames.get(i).equals("urine_free_n")
                    || lastPageParamNames.get(i).equals("urine_m")
                    || lastPageParamNames.get(i).equals("urine_n")
                    || lastPageParamNames.get(i).equals("urinary_creatinine");

            boolean parameterHasAliquot = this.getAliquotParameter(lastPageParamNames.get(i));

            //Exceptional Pheo_Genetics headings in here
            if (lastPageParamTables.get(i).equals("Pheo_Genetics")) {
                if (lastPageParamNames.get(i).contains("_testing_performed")) {
                    outputStr += this.addGeneticHeaders(lastPageParamNames.get(i));
                }
            }
        
            outputStr += "<tr ";
            if (i % 2 != 0) {
                outputStr += lineColour;
            }
            outputStr += ">";
            outputStr += "<td width=\"50%\">" + lastPageParamLabels.get(i) + ":</td>";
            outputStr += "<td><strong>";
            if (parameterIsMultiple) {

                if (lastPageParamNames.get(i).equals("associated_studies")) {
                    StringTokenizer st = new StringTokenizer(lastPageParamValues.get(i), "|");
                    while (st.hasMoreTokens()) {
                        String tokenValueIn = st.nextToken();
                        //String tokenValueIn = request.getParameter("associated_studies_" + (j+1));
                        String studyValueIn = this.getStudyLabel(tokenValueIn);
                        outputStr += "" + studyValueIn + "";
                        String otherStudyParamName = "" + tokenValueIn + "_id";
                        String otherStudyParamValue = request.getParameter(otherStudyParamName);
                        if (otherStudyParamValue != null) {
                            outputStr += " (" + otherStudyParamValue + ")";
                        }
                        outputStr += "<br/>";
                    }
                } else {
                    StringTokenizer st = new StringTokenizer(lastPageParamValues.get(i), "|");
                    while (st.hasMoreTokens()) {
                        String tokenValueIn = st.nextToken();
                        if (tokenValueIn.equals("EDP")) {
                            String[] tokenValuesIn = {"Cisplatin (P)", "Doxorubicin", "Etoposide"};
                            for (int m = 0; m < 3; m++) {
                                outputStr += "" + tokenValuesIn[m] + "<br/>";
                            }
                        }else if(tokenValueIn.equals("Others")){                            
                            String otherValueIn = request.getParameter("" + lastPageParamNames.get(i) + "_others");
                            outputStr += "" + otherValueIn + "<br/>";                            
                        } else {
                            outputStr += "" + tokenValueIn + "<br/>";
                        }
                    }
                }
            } else if (parameterIsAldoConversion) {
                //Retrieve the units from the request object
                String aldoUnits = request.getParameter("aldosterone_units");
                if(aldoUnits == null){
                    aldoUnits = "";
                }

                if (aldoUnits.equals("ngL") || aldoUnits.equals("")) {
                    outputStr += "" + lastPageParamValues.get(i) + "";
                    if (!lastPageParamValues.get(i).equals("")) {
                        outputStr += " (ng/L)";
                    }
                    outputStr += "<input type=\"hidden\" name=\"" + lastPageParamNames.get(i) + "\" value=\"" + lastPageParamValues.get(i) + "\"/>";
                } else {
                    outputStr += "" + this.convertAldo(lastPageParamValues.get(i)) + "";
                    if (!lastPageParamValues.get(i).equals("")) {
                        outputStr += " (ng/L)<br/>";
                    }
                    outputStr += "" + lastPageParamValues.get(i) + "";
                    if (!lastPageParamValues.get(i).equals("")) {
                        outputStr += " (pmol/L)";
                    }
                    outputStr += "<input type=\"hidden\" name=\"" + lastPageParamNames.get(i) + "\" value=\"" + this.convertAldo(lastPageParamValues.get(i)) + "\"/>";
                }

            } else if (parameterIsCortisolConversion) {
                //Retrieve the units from the request object
                String cortUnits = request.getParameter("cortisol_units");
                if(cortUnits == null){
                    cortUnits = "";
                }

                if (cortUnits.equals("ugL") || cortUnits.equals("")) {
                    outputStr += "" + lastPageParamValues.get(i) + "";
                    if (!lastPageParamValues.get(i).equals("")) {
                        outputStr += " (&micro;g/L)";
                    }
                    outputStr += "<input type=\"hidden\" name=\"" + lastPageParamNames.get(i) + "\" value=\"" + lastPageParamValues.get(i) + "\"/>";
                } else {
                    outputStr += "" + this.convertCortisol(lastPageParamValues.get(i)) + "";
                    if (!lastPageParamValues.get(i).equals("")) {
                        outputStr += " (&micro;g/L)<br/>";
                    }
                    outputStr += "" + lastPageParamValues.get(i) + "";
                    if (!lastPageParamValues.get(i).equals("")) {
                        outputStr += " (nmol/L)";
                    }
                    outputStr += "<input type=\"hidden\" name=\"" + lastPageParamNames.get(i) + "\" value=\"" + this.convertCortisol(lastPageParamValues.get(i)) + "\"/>";
                }

            } else if (parameterIsPlasmaConversion) {
                //Retrieve the units from the request object
                String plasmaUnits = request.getParameter("plasma_units");
                if(plasmaUnits == null){
                    plasmaUnits = "";
                }

                if (plasmaUnits.equals("ngL") || plasmaUnits.equals("")) {
                    outputStr += "" + lastPageParamValues.get(i) + "";
                    if (!lastPageParamValues.get(i).equals("")) {
                        outputStr += " (ng/L)";
                    }
                    outputStr += "<input type=\"hidden\" name=\"" + lastPageParamNames.get(i) + "\" value=\"" + lastPageParamValues.get(i) + "\"/>";
                } else {
                    outputStr += "" + this.convertPlasmaUrine(lastPageParamValues.get(i), lastPageParamNames.get(i), plasmaUnits) + "";
                    if (!lastPageParamValues.get(i).equals("")) {
                        outputStr += " (ng/L)<br/>";
                    }
                    outputStr += "" + lastPageParamValues.get(i) + "";
                    if (!lastPageParamValues.get(i).equals("")) {
                        outputStr += " (nmol/L)";
                    }
                    outputStr += "<input type=\"hidden\" name=\"" + lastPageParamNames.get(i) + "\" value=\"" + this.convertPlasmaUrine(lastPageParamValues.get(i), lastPageParamNames.get(i), plasmaUnits) + "\"/>";
                }
            } else if (parameterIsUrineConversion) {
                //Retrieve the units from the request object
                String urineUnits = request.getParameter("urinary_units");
                if(urineUnits == null){
                    urineUnits = "";
                }
                
                if (urineUnits.equals("mgday") || urineUnits.equals("")) {
                    outputStr += "" + lastPageParamValues.get(i) + "";
                    if (!lastPageParamValues.get(i).equals("")) {
                        outputStr += " (mg/day)";
                    }
                    outputStr += "<input type=\"hidden\" name=\"" + lastPageParamNames.get(i) + "\" value=\"" + lastPageParamValues.get(i) + "\"/>";
                } else {
                    outputStr += "" + this.convertPlasmaUrine(lastPageParamValues.get(i), lastPageParamNames.get(i), urineUnits) + "";
                    if (!lastPageParamValues.get(i).equals("")) {
                        outputStr += " (mg/day)<br/>";
                    }
                    outputStr += "" + lastPageParamValues.get(i) + "";
                    if (!lastPageParamValues.get(i).equals("")) {
                        outputStr += " (&micro;mol/day)";
                    }
                    outputStr += "<input type=\"hidden\" name=\"" + lastPageParamNames.get(i) + "\" value=\"" + this.convertPlasmaUrine(lastPageParamValues.get(i), lastPageParamNames.get(i), urineUnits) + "\"/>";
                }
            } else {
                if ((dbn.equals("APA") || dbn.equals("Pheo")) && lastPageParamNames.get(i).equals("eurine_act_inclusion")) {
                    outputStr += "n/a";
                } else {
                    outputStr += "" + lastPageParamValues.get(i) + "";
                }
            }
            outputStr += "</strong></td>";
            //outputStr += "<td width=\"50%\">" + lastPageParamLabels.get(i) + ":</td><td><strong>" + lastPageParamValues.get(i) + "</strong></td>";

            //Now add the biomaterial aliquot information            
            if (lastPageParamTables.get(i).contains("Biomaterial")) {
                outputStr += "<td>";
                if (parameterHasAliquot
                        && lastPageParamValues.get(i).equals("Yes")) {
                    if (!lastPageParamNames.get(i).equals("normal_tissue")
                            && !lastPageParamNames.get(i).equals("normal_tissue_paraffin")
                            && !lastPageParamNames.get(i).equals("normal_tissue_dna")) {

                        String aliquotValue = request.getParameter("aliquot_" + lastPageParamNames.get(i));
                        outputStr += aliquotValue;
                    }
                }
                outputStr += "</td>";

                //THIS IS THE RESULT OF GIT COMMITS TRIPPING OVER EACH OTHER AS WELL...
                outputStr += "<tr>";
                outputStr += "<td colspan='3'>";
                if (parameterHasAliquot && lastPageParamValues.get(i).equals("Yes")) {
                    outputStr += this.getFreezerConfirmHtml(request, lastPageParamNames, i);
                } else if (this.getNormalTissueParameter(lastPageParamNames.get(i)) && !lastPageParamValues.get(i).equals("")) {

                    //Chop up the values string into the component parts
                    StringTokenizer st = new StringTokenizer(lastPageParamValues.get(i), "|");

                    //Render these individually in the box (label + aliquot value)
                    //Put the aliquot number next to each
                    while (st.hasMoreTokens()) {
                        String labelIn = st.nextToken();
                        outputStr += this.getFreezerConfirmHtml(request, lastPageParamNames, i, labelIn);
                    }
                }
                outputStr += "</td>";
            }


            if (parameterIsMultiple) {
                StringTokenizer st = new StringTokenizer(lastPageParamValues.get(i), "|");
                int tokenNum = st.countTokens();
                String multipleParamName = lastPageParamNames.get(i);
                int tokenCount = 1;
                boolean cdeFlag = false;
                while (st.hasMoreTokens()) {
                    String tokenValueIn = st.nextToken();
                    if (tokenValueIn.equals("EDP")) {
                        String[] tokenValuesIn = {"Cisplatin (P)", "Doxorubicin", "Etoposide"};
                        for (int m = 0; m < 3; m++) {
                            outputStr += "<input type=\"hidden\" name=\"" + multipleParamName + "_" + tokenCount + "\" value=\"" + tokenValuesIn[m] + "\"/>";
                            tokenCount++;
                        }
                        cdeFlag = true;
                    }else if(tokenValueIn.equals("Others")){                            
                        String otherValueIn = request.getParameter("" + multipleParamName + "_others");
                        outputStr += "<input type=\"hidden\" name=\"" + multipleParamName + "_" + tokenCount + "\" value=\"" + otherValueIn + "\"/>";                            
                    } else {
                        outputStr += "<input type=\"hidden\" name=\"" + multipleParamName + "_" + tokenCount + "\" value=\"" + tokenValueIn + "\"/>";
                        tokenCount++;
                    }
                }
                if (cdeFlag) {
                    tokenNum = tokenNum + 2;
                }
                outputStr += "<input type=\"hidden\" name=\"" + multipleParamName + "_num\" value=\"" + tokenNum + "\"/>";

                //Add the capture for the incidental IDs here
                String[] otherStudyIDs = {"adiuvo",
                    "adiuvo_observational",
                    "lysosafe",
                    "firstmappp",
                    "german_cushing",
                    "german_conn"};
                int otherStudyNum = otherStudyIDs.length;
                for (int j = 0; j < otherStudyNum; j++) {
                    String otherStudyParamName = "" + otherStudyIDs[j] + "_id";
                    String otherStudyParamValue = request.getParameter(otherStudyParamName);
                    if (otherStudyParamValue != null) {
                        outputStr += "<input type=\"hidden\" name=\"" + otherStudyParamName + "\" value=\"" + otherStudyParamValue + "\"/>";
                    }
                }


            } else {
                outputStr += "<input type=\"hidden\" name=\"" + lastPageParamNames.get(i) + "\" value=\"" + lastPageParamValues.get(i) + "\"/>";
            }

            //Now add the biomaterial aliquot information (hidden)
            if (lastPageParamTables.get(i).contains("Biomaterial")) {
                if (parameterHasAliquot
                        && lastPageParamValues.get(i).equals("Yes")) {
                    String aliquotValue = request.getParameter("aliquot_" + lastPageParamNames.get(i));
                    outputStr += "<input type=\"hidden\" name=\"aliquot_" + lastPageParamNames.get(i) + "\" value=\"" + aliquotValue + "\"/>";
                }
            }

            outputStr += "</tr>";

            //INSERT CALCULATED FIELDS IN HERE
            //1) BMI (after weight)
            //2) ENSAT classification (after other_metastases)
            boolean calcFieldPresent = (lastPageParamNames.get(i).equals("weight") && !lastPageParamTables.contains("Pheo_ClinicalAssessment"))
                    || lastPageParamNames.get(i).equals("other_metastases")
                    || lastPageParamNames.get(i).equals("igf_overexpression");
            if (calcFieldPresent) {

                String calcName = "";
                String calcLabel = "";

                if (lastPageParamNames.get(i).equals("weight")) {
                    calcName = "bmi";
                    calcLabel = "BMI";
                } else if (lastPageParamNames.get(i).equals("igf_overexpression")) {
                    calcName = "weiss_score";
                    calcLabel = "Weiss score";
                } else {
                    calcName = "ensat_classification";
                    calcLabel = "ENSAT classification";
                }

                String calcValue = this.calculateField(calcName, lastPageParamNames, lastPageParamValues);
                outputStr += "<tr ";
                if (i % 2 != 0) {
                    outputStr += lineColour;
                }
                outputStr += ">";
                outputStr += "<td width=\"50%\">" + calcLabel + ":</td><td><strong>" + calcValue + "</strong></td>";
                outputStr += "<input type=\"hidden\" name=\"" + calcName + "\" value=\"" + calcValue + "\"/>";
                outputStr += "</tr>";
            }
        }
        outputStr += "</table>";

        return outputStr;
    }

    protected String calculateField(String calcName, Vector<String> lastPageParamNames, Vector<String> lastPageParamValues) {

        String calcOutput = "";
        if (calcName.equals("bmi")) {

            int paramSize = lastPageParamNames.size();
            String height = "";
            String weight = "";
            for (int i = 0; i < paramSize; i++) {
                if (lastPageParamNames.get(i).equals("height")) {
                    height = lastPageParamValues.get(i);
                } else if (lastPageParamNames.get(i).equals("weight")) {
                    weight = lastPageParamValues.get(i);
                }
            }

            if (height != null && !height.equals("") && weight != null && !weight.equals("")) {
                //Replace those nasty European commas with decimal points
                height = height.replace(",", ".");
                weight = weight.replace(",", ".");
                float heightNum = Float.parseFloat(height);
                float weightNum = Float.parseFloat(weight);
                if (heightNum == 0) {
                    calcOutput = "0.0";
                } else {
                    float heightNumM = heightNum / 100;
                    float bmiNum = weightNum / (heightNumM * heightNumM);

                    //Round the bmiNum to 2 decimal places
                    float p = (float) Math.pow(10, 2);
                    bmiNum = bmiNum * p;
                    float tmp = Math.round(bmiNum);
                    calcOutput = "" + (float) tmp / p;
                }
            } else {
                calcOutput = "";
            }
        } else if (calcName.equals("weiss_score")) {

            int paramSize = lastPageParamNames.size();
            int weissScore = 0;
            boolean weissScoreNotAvailable = false;
            for (int i = 0; i < paramSize; i++) {

                boolean weissScoreContrib =
                        lastPageParamNames.get(i).equals("nuclear_atypia")
                        || lastPageParamNames.get(i).equals("atypical_mitosis")
                        || lastPageParamNames.get(i).equals("spongiocytic_tumor_cells")
                        || lastPageParamNames.get(i).equals("diffuse_architecture")
                        || lastPageParamNames.get(i).equals("venous_invasion")
                        || lastPageParamNames.get(i).equals("sinus_invasion")
                        || lastPageParamNames.get(i).equals("capsular_invasion")
                        || lastPageParamNames.get(i).equals("necrosis")
                        || lastPageParamNames.get(i).equals("number_of_mitoses_per5");

                if (weissScoreContrib) {
                    String valueIn = lastPageParamValues.get(i).trim();
                    if (valueIn.equals("Yes")) {
                        weissScore++;
                    }else if(valueIn.equals("Not Available")){
                        weissScoreNotAvailable = true;
                    }
                }
            }

            if(weissScoreNotAvailable){
                calcOutput = "Unavailable";
            }else{
                calcOutput = "" + weissScore;
            }
        } else {
            String tnmClass = "";
            String ensatClass = "";

            int paramSize = lastPageParamNames.size();
            String sizeOfAdrenalTumor = "";
            String tumorInfiltrationAdipose = "";
            String tumorInvasionAdjacent = "";
            String tumorThrombusVenaRenal = "";
            String regionalLymphNodes = "";
            String distantMetastases = "";
            for (int i = 0; i < paramSize; i++) {
                if (lastPageParamNames.get(i).equals("size_of_adrenal_tumor")) {
                    sizeOfAdrenalTumor = lastPageParamValues.get(i);
                } else if (lastPageParamNames.get(i).equals("tumor_infiltration_adipose")) {
                    tumorInfiltrationAdipose = lastPageParamValues.get(i);
                } else if (lastPageParamNames.get(i).equals("tumor_invasion_adjacent")) {
                    tumorInvasionAdjacent = lastPageParamValues.get(i);
                } else if (lastPageParamNames.get(i).equals("tumor_thrombus_vena_renal")) {
                    tumorThrombusVenaRenal = lastPageParamValues.get(i);
                } else if (lastPageParamNames.get(i).equals("regional_lymph_nodes")) {
                    regionalLymphNodes = lastPageParamValues.get(i);
                } else if (lastPageParamNames.get(i).equals("distant_metastases")) {
                    distantMetastases = lastPageParamValues.get(i);
                }
            }

            if (sizeOfAdrenalTumor != null && !sizeOfAdrenalTumor.equals("") && tumorInfiltrationAdipose != null
                    && tumorInvasionAdjacent != null && tumorThrombusVenaRenal != null
                    && regionalLymphNodes != null && distantMetastases != null) {

                //Get the size of the tumor
                sizeOfAdrenalTumor = sizeOfAdrenalTumor.replace(",", ".");
                float tumorSize = Float.parseFloat(sizeOfAdrenalTumor);
                if (tumorSize <= 50) {
                    tnmClass = "T1";
                } else {
                    tnmClass = "T2";
                }

                //Now check for infiltration or invasion (overrides previous assertion)
                if (tumorInfiltrationAdipose.equals("Yes")) {
                    tnmClass = "T3";
                }
                if (tumorInvasionAdjacent.equals("Yes") || tumorThrombusVenaRenal.equals("Yes")) {
                    tnmClass = "T4";
                }

                //Check for positive lymph nodes
                if (regionalLymphNodes.equals("Yes")) {
                    tnmClass += "N1";
                } else {
                    tnmClass += "N0";
                }

                //Check for distant metastases
                if (distantMetastases.equals("Yes")) {
                    tnmClass += "M1";
                } else {
                    tnmClass += "M0";
                }
            }

            //Now assign ENSAT classification
            if (tnmClass.equals("T1N0M0")) {
                ensatClass = "I";
            } else if (tnmClass.equals("T2N0M0")) {
                ensatClass = "II";
            } else if (tnmClass.equals("T1N1M0") || tnmClass.equals("T2N1M0")
                    || tnmClass.equals("T3N0M0") || tnmClass.equals("T3N1M0")
                    || tnmClass.equals("T4N0M0") || tnmClass.equals("T4N1M0")) {
                ensatClass = "III";
            } else if (tnmClass.equals("T1N0M1") || tnmClass.equals("T2N0M1")
                    || tnmClass.equals("T3N0M1") || tnmClass.equals("T4N0M1")
                    || tnmClass.equals("T1N1M1") || tnmClass.equals("T2N1M1")
                    || tnmClass.equals("T3N1M1") || tnmClass.equals("T4N1M1")) {
                ensatClass = "IV";
            } else {
                ensatClass = "Not Classified";
            }

            //Blank the classification if the values are ALL null
            if ((sizeOfAdrenalTumor == null || sizeOfAdrenalTumor.equals("0.0") || sizeOfAdrenalTumor.equals("0")) && (tumorInfiltrationAdipose == null || tumorInfiltrationAdipose.equals(""))
                    && (tumorInvasionAdjacent == null || tumorInvasionAdjacent.equals("")) && (tumorThrombusVenaRenal == null || tumorThrombusVenaRenal.equals(""))
                    && (regionalLymphNodes == null || regionalLymphNodes.equals("")) && (distantMetastases == null || distantMetastases.equals(""))) {
                calcOutput = "Not Classified";
            } else {
                //Add the contributing factors to the overall ENSAT class
                calcOutput = ensatClass + " (" + tnmClass + ")";
            }
        }
        return calcOutput;


    }

    public void executeParameterUpdate(String pid, String centerid, String tablename, Vector<Vector> parameters, Connection connection, HttpServletRequest request) {

        //logger.debug("tablename: " + tablename);
        //logger.debug("parameters.size(): " + parameters.size());

        boolean tableIsMultiple = this.getTableIsMultiple(tablename);

        if (tableIsMultiple) {
            this.executeMultipleUpdate(tablename, pid, centerid, connection, request);
        } else {
            //Run a check that the table actually exists (FOR PRIMARY TABLES ONLY)
            boolean tableEntryExists = this.checkTableEntry(pid, centerid, tablename, connection);
            if (tableEntryExists) {
                this.runTableUpdate(pid, centerid, tablename, parameters, connection, request);
            } else {
                this.runTableCreate(pid, centerid, tablename, parameters, connection, request);
            }
        }
    }

    private boolean checkTableEntry(String pid, String centerid, String tablename, Connection conn) {

        //String sql = "SELECT * FROM " + tablename + " WHERE center_id='" + centerid + "' AND ensat_id=" + pid + ";";
        String sql = "SELECT * FROM " + tablename + " WHERE center_id=? AND ensat_id=?;";

        boolean entryExists = false;
        try {
            PreparedStatement ps = conn.prepareStatement(sql);
            ps.setString(1, centerid);
            ps.setString(2, pid);

            //ResultSet rs = statement.executeQuery(sql);
            ResultSet rs = ps.executeQuery();
            entryExists = rs.next();
        } catch (Exception e) {
            logger.debug("('" + username + "') Error (checkTableEntry): " + e.getMessage());
        }
        return entryExists;
    }
    
    private Vector<String> getOldParamValues(String pid, String centerid, Vector<String> lastPageParamNames, String tablename, Connection conn){
        
        Vector<String> oldParamValues = new Vector<String>();
        int paramNum = lastPageParamNames.size();
        String sql = "";
        sql += "SELECT ";
        for(int i=0; i<paramNum; i++){
            sql += "" + lastPageParamNames.get(i) + ",";
        }
        sql = sql.trim();
        sql = sql.substring(0,sql.length()-1);
        
        sql += " FROM " + tablename + "";
        sql += " WHERE ensat_id=? AND center_id=?;";
        
        try{
        
            PreparedStatement ps = conn.prepareStatement(sql);
            ps.setString(1,pid);
            ps.setString(2,centerid);
            
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

    private void runTableUpdate(String pid, String centerid, String tablename, Vector<Vector> parameters, Connection conn, HttpServletRequest request) {

        Vector<String> lastPageParamNames = new Vector<String>();
        Vector<String> lastPageParamTypes = new Vector<String>();
        Vector<String> lastPageParamValues = new Vector<String>();
        
        int paramSize = parameters.size();

        for (int i = 0; i < paramSize; i++) {
            Vector<String> paramIn = parameters.get(i);
            if (paramIn.get(8).equals(tablename)) {
                lastPageParamNames.add(paramIn.get(1));
                lastPageParamTypes.add(paramIn.get(2));
                lastPageParamValues.add(paramIn.get(10));
            }
        }
        
        Vector<String> oldParamValues = this.getOldParamValues(pid,centerid,lastPageParamNames,tablename,conn);

        String updateSql = "UPDATE " + tablename + " SET ";

        logger.debug("=== RECORD UPDATED ===");
        
        logger.debug("Ensat ID: " + centerid + "-" + pid);
        logger.debug("Username: " + username);
        logger.debug("Table: " + tablename);
        logger.debug(" ------ ");

        int paramNum = lastPageParamNames.size();
        for (int i = 0; i < paramNum; i++) {
            
            String oldParamValue = "";
            if(i < oldParamValues.size()){
                oldParamValue = oldParamValues.get(i);
            }

            String paramName = lastPageParamNames.get(i);
            String paramValue = lastPageParamValues.get(i);
            
            paramValue.replaceAll(";", "\\;");

            //Do number conversions for specific parameter names
            /*
             * if (paramName.equals("gluco_serum_cortisol_napaca") ||
             * paramName.equals("gluco_plasma_acth_napaca") ||
             * paramName.equals("gluco_urinary_free_cortisol_napaca") ||
             * paramName.equals("other_steroid_17hydroxyprogesterone_napaca") ||
             * paramName.equals("other_steroid_serum_dheas_napaca")) {
             * paramValue = this.napacaUnitConversion(paramName, paramValue,
             * request); }
             */

            if (tablename.equals("Identification")) {
                if (!paramName.equals("center_id")
                        && !paramName.equals("local_investigator")
                        && !paramName.equals("investigator_email")
                        && !paramName.equals("associated_studies")) {
                    //updateSql += "" + paramName + "='" + paramValue + "',";
                    updateSql += "" + paramName + "=?,";
                    logger.debug("'" + username + "','" + paramName + "': [old_value: " + oldParamValue + ", new_value: " + paramValue + "]");
                    //logger.debug("" + paramName + ": " + paramValue);
                }
            } else {
                if (!paramName.equals("system_organ") && !paramName.equals("presentation_first_tumor") && !paramName.equals("imaging")
                        && !paramName.equals("hormone_symptoms") && !paramName.equals("tumor_symptoms") && !paramName.equals("first_diagnosis_tnm") && !paramName.equals("malignant_diagnosis_tnm")
                        
                        ) {
                    updateSql += "" + paramName + "=?,";
                    logger.debug("'" + username + "','" + paramName + "': [old_value: " + oldParamValue + ", new_value: " + paramValue + "]");
                    //logger.debug("" + paramName + ": " + paramValue);
                    //System.out.println("" + paramName + ": " + paramValue);
                }
            }
        }
        updateSql = updateSql.substring(0, updateSql.length() - 1);
        //updateSql += " WHERE ensat_id=" + pid + " AND center_id='" + centerid + "';";
        updateSql += " WHERE ensat_id=? AND center_id=?;";

        try {
            PreparedStatement ps = conn.prepareStatement(updateSql);

            int paramCommitCount = 1;
            for (int i = 0; i < paramNum; i++) {

                String paramName = lastPageParamNames.get(i);
                String paramValue = lastPageParamValues.get(i);
                String paramType = lastPageParamTypes.get(i);
                
                paramValue.replaceAll(";", "\\;");

                //Do number conversions for specific parameter names
                if (paramName.equals("gluco_serum_cortisol_napaca")
                        || paramName.equals("gluco_plasma_acth_napaca")
                        || paramName.equals("gluco_urinary_free_cortisol_napaca")
                        || paramName.equals("other_steroid_17hydroxyprogesterone_napaca")
                        || paramName.equals("other_steroid_serum_dheas_napaca")) {
                    paramValue = this.napacaUnitConversion(paramName, paramValue, request);
                }

                if (tablename.equals("Identification")) {
                    if (!paramName.equals("center_id")
                            && !paramName.equals("local_investigator")
                            && !paramName.equals("investigator_email")
                            && !paramName.equals("associated_studies")) {
                        //updateSql += "" + paramName + "='" + paramValue + "',";  
                        if (paramType.equals("date")) {
                            paramValue = this.reformatDateValue(paramValue);
                        }
                        ps.setString(paramCommitCount, paramValue);
                        paramCommitCount++;
                    }
                } else {
                    if (!paramName.equals("system_organ") && !paramName.equals("presentation_first_tumor") && !paramName.equals("imaging")
                            && !paramName.equals("hormone_symptoms") && !paramName.equals("tumor_symptoms") && !paramName.equals("first_diagnosis_tnm") && !paramName.equals("malignant_diagnosis_tnm")
                            ) {
                        //updateSql += "" + paramName + "='" + paramValue + "',";
                        //logger.debug("" + paramName + ": " + paramValue);
                        if (paramType.equals("date")) {
                            paramValue = this.reformatDateValue(paramValue);
                        }
                        ps.setString(paramCommitCount, paramValue);
                        paramCommitCount++;
                    }
                }
            }
            ps.setString(paramCommitCount, pid);
            ps.setString(paramCommitCount + 1, centerid);

            //logger.debug("updateSql (runTableUpdate): " + updateSql);
            //logger.debug("RECORD UPDATE (runTableUpdate) - details to go here...");
            if (paramNum != 0) {
                //int update = statement.executeUpdate(updateSql);
                int update = ps.executeUpdate();
            }
        } catch (Exception e) {
            logger.debug("('" + username + "') Error (runTableUpdate): " + e.getMessage());
        }

        logger.debug("=====");
    }

    private void runTableCreate(String pid, String centerid, String tablename, Vector<Vector> parameters, Connection conn, HttpServletRequest request) {

        Vector<String> lastPageParamNames = new Vector<String>();
        Vector<String> lastPageParamTypes = new Vector<String>();
        Vector<String> lastPageParamValues = new Vector<String>();

        int paramSize = parameters.size();

        for (int i = 0; i < paramSize; i++) {
            Vector<String> paramIn = parameters.get(i);
            if (paramIn.get(8).equals(tablename)) {
                lastPageParamNames.add(paramIn.get(1));
                lastPageParamTypes.add(paramIn.get(2));
                lastPageParamValues.add(paramIn.get(10));
            }
        }

        String updateSql = "INSERT INTO " + tablename + " (";

        if (tablename.equals("Identification")) {
            updateSql += "ensat_id,uploader,ensat_database,record_date,";
        } else {
            updateSql += "ensat_id,center_id,";
        }


        int paramNum = lastPageParamNames.size();
        for (int i = 0; i < paramNum; i++) {

            String paramNameIn = lastPageParamNames.get(i);

            boolean paramException = (tablename.equals("Pheo_PatientHistory")
                            && (paramNameIn.equals("system_organ") 
                            || paramNameIn.equals("presentation_first_tumor")
                            || paramNameIn.equals("hormone_symptoms")
                            || paramNameIn.equals("tumor_symptoms")
                            || paramNameIn.equals("first_diagnosis_tnm")
                            || paramNameIn.equals("malignant_diagnosis_tnm"))
                            
                    || (tablename.equals("ACC_TumorStaging")
                    && (paramNameIn.equals("imaging")))
                    
                    || (tablename.equals("NAPACA_DiagnosticProcedures")
                    && (paramNameIn.equals("bmi") || paramNameIn.equals("tumor_size")))
                    
                    || (tablename.equals("Identification")
                    && (paramNameIn.equals("associated_studies"))));

            if (!paramException) {
                updateSql += paramNameIn + ",";
            }
        }
        updateSql = updateSql.substring(0, updateSql.length() - 1);
        updateSql += ") VALUES(";

        //updateSql += "" + pid + ",'" + centerid + "',";
        updateSql += "?,?,";

        for (int i = 0; i < paramNum; i++) {

            String paramName = lastPageParamNames.get(i);
            String paramValue = lastPageParamValues.get(i);
            
            paramValue.replaceAll(";", "\\;");

            logger.debug("paramName: " + paramName);
            logger.debug("paramValue: " + paramValue);

            //Do number conversions for specific parameter names
            /*
             * if (paramName.equals("gluco_serum_cortisol_napaca") ||
             * paramName.equals("gluco_plasma_acth_napaca") ||
             * paramName.equals("gluco_urinary_free_cortisol_napaca") ||
             * paramName.equals("other_steroid_17hydroxyprogesterone_napaca") ||
             * paramName.equals("other_steroid_serum_dheas_napaca")) {
             * paramValue = this.napacaUnitConversion(paramName, paramValue,
             * request); }
             */

            boolean paramException = (tablename.equals("Pheo_PatientHistory")
                            && (paramName.equals("system_organ") 
                            || paramName.equals("presentation_first_tumor")
                            || paramName.equals("hormone_symptoms")
                            || paramName.equals("tumor_symptoms")
                            || paramName.equals("first_diagnosis_tnm")
                            || paramName.equals("malignant_diagnosis_tnm"))
                    
                    || (tablename.equals("ACC_TumorStaging")
                    && (paramName.equals("imaging")))
                    
                    || (tablename.equals("NAPACA_DiagnosticProcedures")
                    && (paramName.equals("bmi") || paramName.equals("tumor_size")))
                    
                    || (tablename.equals("Identification")
                    && (paramName.equals("associated_studies"))));

            if (!paramException) {
                //updateSql += "'" + paramValue + "',";
                updateSql += "?,";
            }
        }
        updateSql = updateSql.substring(0, updateSql.length() - 1);
        updateSql += ");";

       try {
            //logger.debug("updateSql (runTableCreate): " + updateSql);
            if (paramNum != 0) {
                PreparedStatement ps = conn.prepareStatement(updateSql);
                ps.setString(1, pid);
                ps.setString(2, centerid);

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
                        paramValue = this.napacaUnitConversion(paramName, paramValue, request);
                    }

                    boolean paramException = (tablename.equals("Pheo_PatientHistory")
                            && (paramName.equals("system_organ") 
                            || paramName.equals("presentation_first_tumor")
                            || paramName.equals("hormone_symptoms")
                            || paramName.equals("tumor_symptoms")
                            || paramName.equals("first_diagnosis_tnm")
                            || paramName.equals("malignant_diagnosis_tnm"))
                    
                    || (tablename.equals("ACC_TumorStaging") 
                            && (paramName.equals("imaging")))
                    
                    || (tablename.equals("NAPACA_DiagnosticProcedures")
                    && (paramName.equals("bmi") || paramName.equals("tumor_size")))
                    
                    || (tablename.equals("Identification")
                    && (paramName.equals("associated_studies"))));

                    if (!paramException) {
                        //updateSql += "'" + paramValue + "',";
                        if (paramType.equals("date")) {
                            paramValue = this.reformatDateValue(paramValue);
                        }
                        //logger.debug("updating parameter: " + paramName + " with: " + paramValue);
                        //logger.debug("paramCommitCount: " + paramCommitCount);
                        ps.setString(paramCommitCount + 2, paramValue);
                        //logger.debug("parameter updated...");
                        paramCommitCount++;
                    }
                }


                //int update = statement.executeUpdate(updateSql);
                int update = ps.executeUpdate();
            }
        } catch (Exception e) {
            logger.debug("('" + username + "') Error (executeParameterUpdate): " + e.getMessage());
        }
    }

    protected String napacaUnitConversion(String paramName, String paramValue, HttpServletRequest request) {

        String paramUnits = request.getParameter(paramName + "_units");
        double conversionFactor = 0.0;

        if (paramName.equals("gluco_serum_cortisol_napaca") && paramUnits.equals("nmol/L")) {
            conversionFactor = 27.59;
        } else if (paramName.equals("gluco_plasma_acth_napaca") && paramUnits.equals("pmol/L")) {
            conversionFactor = 0.22;
        } else if (paramName.equals("gluco_urinary_free_cortisol_napaca") && paramUnits.equals("umol/24h")) {
            conversionFactor = 2.759;
        } else if (paramName.equals("other_steroid_17hydroxyprogesterone_napaca") && paramUnits.equals("nmol/L")) {
            conversionFactor = 3.026;
        } else if (paramName.equals("other_steroid_serum_dheas_napaca") && paramUnits.equals("nmol/L")) {
            conversionFactor = 2.714;
        }

        try {
            double paramValueDouble = Double.parseDouble(paramValue);
            paramValueDouble = paramValueDouble / conversionFactor;
            paramValue = "" + paramValueDouble;
        } catch (NumberFormatException nfe) {
        }

        return paramValue;
    }

    public String getParameterValue(String paramName, Vector<Vector> parameters) {

        String paramValue = "";

        boolean paramFound = false;
        int count = 0;
        while (count < parameters.size() && !paramFound) {

            Vector<String> paramIn = parameters.get(count);
            String nameIn = paramIn.get(1);
            paramFound = nameIn.equals(paramName);
            if (paramFound) {
                String valueIn = paramIn.get(10);
                paramValue = valueIn;
            } else {
                count++;
            }
        }

        //Case for nulls
        if (paramValue == null) {
            paramValue = "";
        }
        return paramValue;
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
                elemCount = elemCount + ROW_SIZE;
            }

        }
        return outputStr;
    }

    private String getAssocTableRowHtml(Vector<Vector> subTables, String dbid, String dbn, String centerid, String pid, int rowSize, int elemCount) {

        String outputStr = "";
        for (int i = 0; i < rowSize; i++) {
            Vector<String> subTableIn = subTables.get(elemCount + i);
            outputStr += "<td><a href=\"./jsp/modality/home.jsp?dbid=" + dbid + "&dbn=" + dbn + "&pid=" + pid + "&centerid=" + centerid + "&modality=" + subTableIn.get(2) + "\"><strong>" + subTableIn.get(1) + "</a></strong></td>";
        }
        return outputStr;
    }

    public Vector<Vector> compileSubTableList(String dbn, String username, String centerId, String pid, Connection conn) {

        boolean showMetabolomics = false;
        
        String uploader = "";
        try{
            uploader = this.getParameterValues("Identification", pid, centerId, conn, "uploader");
        }catch(Exception e){
            logger.debug("Error (compileSubTableList): " + e.getMessage());
        }
        
        if(username.equals("w.arlt@bham.ac.uk")
                    || username.equals("bancos.irina@mayo.edu")
                    || username.equals("astell@unimelb.edu.au")
                    || username.equals("felix.beuschlein@med.uni-muenchen.de")
                    || username.equals("bchortis@hotmail.com")
                    || username.equals("annar")){
            showMetabolomics = true;
        }else if(username.equals(uploader)){
            showMetabolomics = true;
        }
        
        boolean patientInStudyPmt3 = this.getPatientInStudy(centerId, pid, "PMT3", conn);
        
        boolean patientInStudyMappProno = this.getPatientInStudy(centerId, pid, "MAPP-Prono", conn);

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
            if (showMetabolomics) {
                tableInfo = new Vector<String>();
                tableInfo.add("ACC_Metabolomics"); //Db tablename
                tableInfo.add("Steroid Metabolomics"); //Printed name
                tableInfo.add("metabolomics"); //HTML link name
                subTables.add(tableInfo);
            }

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
            tableInfo.add("Surgery / Pathology"); //Printed name
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
            if (patientInStudyPmt3) {
                tableInfo = new Vector<String>();
                tableInfo.add("Pheo_Other_Genetics"); //Db tablename
                tableInfo.add("Other Genetic Information"); //Printed name
                tableInfo.add("other_genetics"); //HTML link name
                subTables.add(tableInfo);
            }
            if (patientInStudyMappProno) {
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
            }
            
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
            if (showMetabolomics) {
                tableInfo = new Vector<String>();
                tableInfo.add("NAPACA_Metabolomics"); //Db tablename
                tableInfo.add("Steroid Metabolomics"); //Printed name
                tableInfo.add("metabolomics"); //HTML link name
                subTables.add(tableInfo);
            }

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
            subTables.add(tableInfo);
        }
        return subTables;
    }
    
    public String getAssocInfoHtml(String dbid, String dbn, String centerid, String pid, Connection conn) {

        String outputStr = "";

        outputStr += this.getAssocInfoHeaderHtml("");

        outputStr += this.getAssocInfoBodyHtml(dbid, dbn, centerid, pid, conn);

        return outputStr;
    }

    protected String getAssocInfoHeaderHtml(String modality) {

        String outputStr = "";

        outputStr += "<thead>";
        outputStr += "<tr>";

        int COLUMN_NUM = 4;
        if (modality.equals("biomaterial")) {
            COLUMN_NUM = 5;
        }
        String[] columnHeaders = new String[COLUMN_NUM];
        columnHeaders[0] = "ENSAT ID";
        columnHeaders[1] = "Form ID";
        columnHeaders[2] = "Date";
        if (modality.equals("biomaterial")) {
            columnHeaders[3] = "Study";
            columnHeaders[4] = "Types";
        } else {
            columnHeaders[3] = "Record Information";
        }

        for (int i = 0; i < COLUMN_NUM; i++) {
            outputStr += "<th>";
            outputStr += columnHeaders[i];
            outputStr += "</th>";
        }

        outputStr += "<th>";
        outputStr += "</th>";
        outputStr += "<th>";
        outputStr += "</th>";
        if (modality.equals("biomaterial")) {
            outputStr += "<th>";
            outputStr += "</th>";
        }
        outputStr += "</tr>";
        outputStr += "</thead>";

        return outputStr;
    }

    protected String formatEnsatId(String ensatId) {

        String ensatIdOut = "";
        if (ensatId.length() == 3) {
            ensatIdOut = "0" + ensatId;
        } else if (ensatId.length() == 2) {
            ensatIdOut = "00" + ensatId;
        } else if (ensatId.length() == 1) {
            ensatIdOut = "000" + ensatId;
        } else {
            ensatIdOut = ensatId;
        }
        return ensatIdOut;
    }

    private String getAssocInfoBodyHtml(String dbid, String dbn, String centerid, String pid, Connection conn) {

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
            listQueryModalities[3] = "complication";
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

            for (int j = 0; j < listQueryTables.length; j++) {

                String listQuerySql = "";

                if (dbn.equals("Pheo")) {
                    if (j < 3) {
                        //listQuerySql = "SELECT * FROM " + listQueryTables[j] + " WHERE ensat_id=" + pid + " AND center_id='" + centerid + "';";
                        listQuerySql = "SELECT * FROM " + listQueryTables[j] + " WHERE ensat_id=? AND center_id=?;";
                    } else {
                        listQuerySql = "SELECT * FROM " + listQueryTables[j] + " WHERE ensat_id=? AND center_id=?" + " ORDER BY " + listQueryDatenames[j - 3] + " DESC;";
                    }
                } else {
                    listQuerySql = "SELECT * FROM " + listQueryTables[j] + " WHERE ensat_id=? AND center_id=?" + " ORDER BY " + listQueryDatenames[j] + " DESC;";
                }

                PreparedStatement ps = conn.prepareStatement(listQuerySql);
                ps.setString(1, pid);
                ps.setString(2, centerid);

                //rs = stmt.executeQuery(listQuerySql);
                rs = ps.executeQuery();
                if (rs != null) {
                    while (rs.next()) {
                        outputNum++;
                    }
                }
            }
            outputHtml = new String[outputNum][2];

            for (int j = 0; j < listQueryTables.length; j++) {

                String listQuerySql = "";
                if (dbn.equals("Pheo")) {
                    if (j < 3) {
                        //listQuerySql = "SELECT * FROM " + listQueryTables[j] + " WHERE ensat_id=" + pid + " AND center_id='" + centerid + "';";
                        listQuerySql = "SELECT * FROM " + listQueryTables[j] + " WHERE ensat_id=? AND center_id=?;";
                    } else {
                        //listQuerySql = "SELECT * FROM " + listQueryTables[j] + " WHERE ensat_id=" + pid + " AND center_id='" + centerid + "'" + " ORDER BY " + listQueryDatenames[j - 3] + " DESC;";
                        listQuerySql = "SELECT * FROM " + listQueryTables[j] + " WHERE ensat_id=? AND center_id=?" + " ORDER BY " + listQueryDatenames[j - 3] + " DESC;";
                    }
                } else {
                    //listQuerySql = "SELECT * FROM " + listQueryTables[j] + " WHERE ensat_id=" + pid + " AND center_id='" + centerid + "'" + " ORDER BY " + listQueryDatenames[j] + " DESC;";
                    listQuerySql = "SELECT * FROM " + listQueryTables[j] + " WHERE ensat_id=? AND center_id=?" + " ORDER BY " + listQueryDatenames[j] + " DESC;";
                }

                PreparedStatement ps = conn.prepareStatement(listQuerySql);
                ps.setString(1, pid);
                ps.setString(2, centerid);

                //rs = stmt.executeQuery(listQuerySql);
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

                            //if (!dbn.equals("Pheo")) {
                            boolean multipleDates = (j < 3) && dbn.equals("Pheo");

                            //logger.debug("multipleDates: " + multipleDates);

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
                                    inputIndex[0] = 5;//4
                                    inputIndex[1] = 10;//8
                                    inputIndex[2] = 14;//12
                                    inputIndex[3] = 18;//16
                                    inputIndex[4] = 22;//20
                                    inputIndex[5] = 26;//24
                                    inputIndex[6] = 29;//27
                                    inputIndex[7] = 34;//32
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
                                while (!dateFound && dateCount < inputIndex.length) {
                                    StringTokenizer st = new StringTokenizer(inputDates[dateCount], "-");
                                    dateFound = st.countTokens() >= 3;
                                    dateCount++;
                                }
                                if (dateFound) {
                                    inputDate = inputDates[dateCount - 1];
                                }


                            } else { //End of multipleDates clause

                                if (listQueryModalities[j].equals("chemotherapy") || listQueryModalities[j].equals("mitotane")) {
                                    inputDate = rs.getString(5);
                                } else {
                                    inputDate = rs.getString(4);
                                }
                            }

                            //}

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
                            //outputHtml[outputCount][0] += "<td>TEST</td>";

                        } else {
                            if (i == 3) {
                                if (listQueryInfoOutput[j].equals("Biomaterial")) {
                                    outputHtml[outputCount][0] += "<td>";
                                    outputHtml[outputCount][0] += listQueryInfoOutput[j];
                                    outputHtml[outputCount][0] += "<br/>";
                                    outputHtml[outputCount][0] += "(" + this.getBiomaterialLabels(rs) + ")";
                                    outputHtml[outputCount][0] += "</td>";
                                } else if (listQueryInfoOutput[j].equals("Follow-Up") && dbn.equals("NAPACA")) {
                                    outputHtml[outputCount][0] += "<td>";
                                    outputHtml[outputCount][0] += listQueryInfoOutput[j];
                                    if (this.getNapacaFollowupFlag(rs)) {
                                        outputHtml[outputCount][0] += "<br/>";
                                        outputHtml[outputCount][0] += "<div class='errorLabel'>(Follow-up indicates imaging - please add the corresponding imaging form)</div>";
                                    }
                                    outputHtml[outputCount][0] += "</td>";
                                } else {
                                    outputHtml[outputCount][0] += "<td>" + listQueryInfoOutput[j] + "</td>";
                                }
                                //outputHtml[outputCount][0] += "<td>" + listQueryInfoOutput[j] + "</td>";
                            }
                        }

                    }

                    outputHtml[outputCount][0] += "<td><a href='./jsp/modality/read/detail.jsp?dbid=" + dbid + "&dbn=" + dbn + "&pid=" + pid + "&centerid=" + centerid + "&modality=" + listQueryModalities[j] + "&modid=" + modid + "'>Detail</a></td>";
                    outputHtml[outputCount][0] += "<td><a href='./jsp/modality/delete/delete_view.jsp?dbid=" + dbid + "&dbn=" + dbn + "&pid=" + pid + "&centerid=" + centerid + "&modality=" + listQueryModalities[j] + "&modid=" + modid + "'>Delete</a></td></tr>";


                    outputCount++;
                }
            }
            rs.close();
        } catch (Exception e) {
            logger.debug("('" + username + "') Error (within listQuery): " + e.getMessage());
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
        } catch (Exception e) {
            logger.debug("('" + username + "') Error (sorting dates): " + e.getMessage());
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

    public String standardisePid(String pid) {
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

    protected String addGeneticHeaders(String paramName) {
        String outputStr = "";
        String geneName = paramName.substring(0, paramName.indexOf("_"));
        geneName = geneName.toUpperCase();
        outputStr = "<tr><th colspan='2'><div align='center'>" + geneName + "</div></th></tr>";
        return outputStr;
    }

    private String getMultipleParameterValues(String paramName, Connection connValues, String pid, String centerid, String dbn) {

        String multipleValueOut = "";
        String multipleTablename = "";
        if (paramName.equals("system_organ")) {
            multipleTablename = "Pheo_OtherOrgans";
        } else if (paramName.equals("presentation_first_tumor")) {
            multipleTablename = "Pheo_FirstDiagnosisPresentation";
        } else if (paramName.equals("hormone_symptoms")) {
            multipleTablename = "Pheo_HormoneSymptoms";
        } else if (paramName.equals("tumor_symptoms")) {
            multipleTablename = "Pheo_TumorSymptoms";
        } else if (paramName.equals("first_diagnosis_tnm")) {
            multipleTablename = "Pheo_FirstDiagnosisTNM";
        } else if (paramName.equals("malignant_diagnosis_tnm")) {
            multipleTablename = "Pheo_MalignantDiagnosisTNM";
        } else if (paramName.equals("imaging")) {
            multipleTablename = "ACC_Imaging";
        } else if (paramName.equals("associated_studies")) {
            multipleTablename = "Associated_Studies";
        } else if (paramName.equals("metastases_location")) {
            multipleTablename = "Pheo_MetastasesLocation";
        } else if (paramName.equals("imaging_location")) {
            multipleTablename = "Pheo_ImagingLocation";
        } else if (paramName.equals("chemotherapy_regimen")) {
            multipleTablename = "ACC_Chemotherapy_Regimen";
        } else if (paramName.equals("followup_organs")) {
            multipleTablename = "ACC_FollowUp_Organs";
        } else if (paramName.equals("radiofrequency_location")) {
            multipleTablename = "ACC_Radiofrequency_Loc";
        } else if (paramName.equals("radiotherapy_location")) {
            multipleTablename = "ACC_Radiotherapy_Loc";
        } else if (paramName.equals("surgery_first")) {
            multipleTablename = "ACC_Surgery_First";
        } else if (paramName.equals("surgery_extended")) {
            multipleTablename = "ACC_Surgery_Extended";
        } else if (paramName.equals("nmr_location")) {
            multipleTablename = "Pheo_ImagingTests_NMRLoc";
        } else if (paramName.equals("ct_location")) {
            multipleTablename = "Pheo_ImagingTests_CTLoc";
        } else if (paramName.equals("preop_blockade_agents")) {
            multipleTablename = "Pheo_Surgery_PreOp";
        } else if (paramName.equals("intraop_bp_control_agents")) {
            multipleTablename = "Pheo_Surgery_IntraOp";
        } else if (paramName.equals("normal_tissue_options") || paramName.equals("normal_tissue_paraffin_options") || paramName.equals("normal_tissue_dna_options")) {
            multipleTablename = dbn + "_Biomaterial_Normal_Tissue";
        }


        try {
            //String sql = "SELECT * FROM " + multipleTablename + " WHERE ensat_id=" + pid + " AND center_id='" + centerid + "';";
            String sql = "SELECT * FROM " + multipleTablename + " WHERE ensat_id=? AND center_id=?;";
            PreparedStatement ps = connValues.prepareStatement(sql);
            ps.setString(1, pid);
            ps.setString(2, centerid);
            //logger.debug("sql: " + sql);
            ResultSet rs = ps.executeQuery();
            while (rs.next()) {
                
                //Try to capture the '<=' and '>' symbols
                String valueIn = rs.getString(4);
                if(valueIn.contains(" <= ")){
                    valueIn = valueIn.replaceAll(" <= ", " &lt;= ");
                }else if(valueIn.contains(" > ")){
                    valueIn = valueIn.replaceAll(" > ", " &gt; ");
                }
                
                multipleValueOut += valueIn + "|";
            }
            //Remove the trailing pipe character
            if (!multipleValueOut.equals("")) {
                multipleValueOut = multipleValueOut.substring(0, multipleValueOut.length() - 1);
            }

            //logger.debug("multipleValueOut (getMultipleParameterValues): " + multipleValueOut);

        } catch (Exception e) {
            logger.debug("('" + username + "') Error (getMultipleParameterValues): " + e.getMessage());
        }
        return multipleValueOut;
    }

    protected boolean getAliquotParameter(String paramName) {

        boolean isAliquot =
                paramName.equals("tumor_tissue_frozen")
                || paramName.equals("tumor_tissue_paraffin")
                || paramName.equals("tumor_tissue_dna")
                || paramName.equals("leukocyte_dna")
                || paramName.equals("plasma")
                || paramName.equals("heparin_plasma")
                || paramName.equals("serum")
                || paramName.equals("24h_urine")
                || paramName.equals("spot_urine")
                || paramName.equals("normal_tissue")
                || paramName.equals("normal_tissue_paraffin") || paramName.equals("normal_tissue_dna");
        return isAliquot;
    }

    public String getAliquotInfo(String paramName, boolean showHide, String aliquotValue) {

        String outputStr = "";

        if (!paramName.equals("normal_tissue")
                && !paramName.equals("normal_tissue_paraffin")
                && !paramName.equals("normal_tissue_dna")) {

            String showHideStr = "";
            if (showHide) {
                showHideStr = "show";
            } else {
                showHideStr = "hide";
            }

            outputStr += "<div id='aliquot_" + paramName + "_options' class='" + showHideStr + "'>";
            if (!aliquotValue.equals("")) {
                outputStr += this.getAliquotMenu(paramName, aliquotValue);
            } else {
                //logger.debug("aliquotValue is blank...");
                outputStr += this.getAliquotMenu(paramName);
            }
            outputStr += "</div>";

        }

        return outputStr;
    }

    private String getAliquotMenu(String paramName, String aliquotVal) {

        String outputStr = "";

        int aliquotValInt = 0;
        try {
            aliquotValInt = Integer.parseInt(aliquotVal);
        } catch (NumberFormatException nfe) {
            logger.debug("('" + username + "') Number format exception (getAliquotMenu): " + nfe.getMessage());
        }

        outputStr += "<select name='aliquot_" + paramName + "' onchange='showFreezerAliquotNumberUpdate(this.name,this.value)' >";

        boolean selectOnOne = aliquotValInt == 0;
        for (int i = 0; i < 10; i++) {
            outputStr += "<option";
            if (i == aliquotValInt && i != 0) {
                outputStr += " selected ";
            } else if (i == 1 && selectOnOne) {
                outputStr += " selected ";
            }
            outputStr += " value='" + i + "'>" + i + "</option>";
        }
        outputStr += "</select>";
        return outputStr;
    }

    protected void updateAliquotTable(String paramName, String paramValue, String tablename, int nextId, String pid, String centerid, HttpServletRequest request, Connection conn) {

        boolean parameterHasAliquot = this.getAliquotParameter(paramName);
        boolean parameterIsNormalTissue = this.getNormalTissueParameter(paramName);

        String aliquotSql = "";
        if (parameterHasAliquot && paramValue.equals("Yes")) {

            this.runAliquotUpdate(paramName, paramValue, tablename, nextId, pid, centerid, request, conn);
        } else if (parameterIsNormalTissue) {

            //Get the number of normal tissue selections
            String normalTissueSelectionNum = request.getParameter("" + paramName + "_num");
            int normalTissueSelection = 0;
            try {
                normalTissueSelection = Integer.parseInt(normalTissueSelectionNum);
            } catch (NumberFormatException nfe) {
                logger.debug("Number parsing error: " + nfe.getMessage());
            }

            this.runAliquotNTDelete(paramName, paramValue, tablename, nextId, pid, centerid, request, conn);

            //Figure out what the normal tissue selections are
            String[] normalTissueSelections = new String[normalTissueSelection];
            for (int i = 0; i < normalTissueSelection; i++) {
                normalTissueSelections[i] = request.getParameter("" + paramName + "_" + (i + 1));
                normalTissueSelections[i] = this.getNormalTissueParamLabel(normalTissueSelections[i]);
                String paramNameNormalTissue = paramName + "_" + normalTissueSelections[i];
                this.runAliquotNTUpdate(paramNameNormalTissue, paramValue, tablename, nextId, pid, centerid, request, conn);
            }
        }
    }

    protected void runAliquotNTDelete(String paramName, String paramValue, String tablename, int nextId, String pid, String centerid, HttpServletRequest request, Connection conn) {

        String aliquotSql = "";
        //Clear out the old aliquot value first
        String paramNameDelete = paramName;
        if (paramNameDelete.equals("normal_tissue")) {
            paramNameDelete = "normal_tissue_frozen";
        }
        String deleteCurrentAliquotSqlTest = "DELETE FROM " + tablename + "_Aliquots WHERE ensat_id=" + pid + " AND center_id='" + centerid + "' AND parameter_name LIKE '" + paramNameDelete + "%';";
        String deleteCurrentAliquotSql = "DELETE FROM " + tablename + "_Aliquots WHERE ensat_id=? AND center_id=? AND parameter_name LIKE ?;";
        try {
            PreparedStatement ps = conn.prepareStatement(deleteCurrentAliquotSql);
            ps.setString(1, pid);
            ps.setString(2, centerid);
            ps.setString(3, paramNameDelete + "%");

            int deleteUpdate = ps.executeUpdate();
            //int deleteUpdate = statement.executeUpdate(deleteCurrentAliquotSql);
        } catch (Exception e) {
            logger.debug("('" + username + "') Error (updateAliquotTable): " + e.getMessage());
        }
    }

    protected void runAliquotUpdate(String paramName, String paramValue, String tablename, int nextId, String pid, String centerid, HttpServletRequest request, Connection conn) {

        String aliquotSql = "";
        //Clear out the old aliquot value first


        String paramNameDelete = paramName;
        if (paramNameDelete.equals("normal_tissue")) {
            paramNameDelete = "normal_tissue_frozen";
        }
        if (!paramName.contains("normal_tissue")) {

            String deleteCurrentAliquotSqlTest = "DELETE FROM " + tablename + "_Aliquots WHERE ensat_id=" + pid + " AND center_id='" + centerid + "' AND parameter_name='" + paramNameDelete + "';";
            String deleteCurrentAliquotSql = "DELETE FROM " + tablename + "_Aliquots WHERE ensat_id=? AND center_id=? AND parameter_name=?;";
            try {
                PreparedStatement ps = conn.prepareStatement(deleteCurrentAliquotSql);
                ps.setString(1, pid);
                ps.setString(2, centerid);
                ps.setString(3, paramNameDelete);

                int deleteUpdate = ps.executeUpdate();
                //int deleteUpdate = statement.executeUpdate(deleteCurrentAliquotSql);
            } catch (Exception e) {
                logger.debug("('" + username + "') Error (updateAliquotTable): " + e.getMessage());
            }

            //Get the last ID from the aliquots table

            //String idCheckSql = "SELECT " + aliquotIdName + " FROM " + tablename + "_Aliquots WHERE ensat_id=" + pid + " AND center_id='" + centerid + "' ORDER BY " + aliquotIdName + " DESC;";
            String aliquotIdName = tablename.toLowerCase() + "_aliquot_id";
            String idCheckSql = "SELECT " + aliquotIdName + " FROM " + tablename + "_Aliquots WHERE ensat_id=? AND center_id=? ORDER BY " + aliquotIdName + " DESC;";

            String formId = "1";
            try {
                PreparedStatement ps = conn.prepareStatement(idCheckSql);
                ps.setString(1, pid);
                ps.setString(2, centerid);

                ResultSet rs = ps.executeQuery();
                //ResultSet rs = statement.executeQuery(idCheckSql);
                if (rs.next()) {
                    formId = rs.getString(1);
                }

                int formIdInt = Integer.parseInt(formId);
                formIdInt = formIdInt + 1;
                formId = "" + formIdInt;
                //logger.debug("formId: " + formId);
            } catch (Exception e) {
                logger.debug("('" + username + "') Error (updateAliquotTable): " + e.getMessage());
            }

            String aliquotValue = request.getParameter("aliquot_" + paramName);
            //aliquotSql = "INSERT INTO " + tablename + "_Aliquots VALUES(" + formId + "," + nextId + "," + pid + ",'" + centerid + "',";
            aliquotSql = "INSERT INTO " + tablename + "_Aliquots VALUES(?,?,?,?,";
            //aliquotSql += "'" + paramName + "','" + aliquotValue + "');";
            aliquotSql += "?,?);";

            try {
                PreparedStatement ps = conn.prepareStatement(aliquotSql);
                ps.setString(1, formId);
                ps.setInt(2, nextId);
                ps.setString(3, pid);
                ps.setString(4, centerid);
                ps.setString(5, paramName);
                ps.setString(6, aliquotValue);

                //int updateAliquots = statement.executeUpdate(aliquotSql);
                int updateAliquots = ps.executeUpdate();
            } catch (Exception e) {
                logger.debug("('" + username + "') Error (updateAliquotTable): " + e.getMessage());
            }
        }

    }

    protected void runAliquotNTUpdate(String paramName, String paramValue, String tablename, int nextId, String pid, String centerid, HttpServletRequest request, Connection conn) {

        String aliquotSql = "";
        //Clear out the old aliquot value first
            /*
         * String paramNameDelete = paramName;
         * if(paramNameDelete.equals("normal_tissue")){ paramNameDelete =
         * "normal_tissue_frozen"; }
         *
         * String deleteCurrentAliquotSqlTest = "DELETE FROM " + tablename +
         * "_Aliquots WHERE ensat_id=" + pid + " AND center_id='" + centerid +
         * "' AND parameter_name='" + paramNameDelete + "';";
         * System.out.println("deleteCurrentAliquotSql: " +
         * deleteCurrentAliquotSqlTest); String deleteCurrentAliquotSql =
         * "DELETE FROM " + tablename + "_Aliquots WHERE ensat_id=? AND
         * center_id=? AND parameter_name=?;"; try { PreparedStatement ps =
         * conn.prepareStatement(deleteCurrentAliquotSql); ps.setString(1,pid);
         * ps.setString(2,centerid); ps.setString(3,paramNameDelete);
         *
         * int deleteUpdate = ps.executeUpdate(); //int deleteUpdate =
         * statement.executeUpdate(deleteCurrentAliquotSql); } catch (Exception
         * e) { logger.debug("('" + username + "') Error (updateAliquotTable): "
         * + e.getMessage());
            }
         */

        //Get the last ID from the aliquots table

        //String idCheckSql = "SELECT " + aliquotIdName + " FROM " + tablename + "_Aliquots WHERE ensat_id=" + pid + " AND center_id='" + centerid + "' ORDER BY " + aliquotIdName + " DESC;";
        String aliquotIdName = tablename.toLowerCase() + "_aliquot_id";
        String idCheckSql = "SELECT " + aliquotIdName + " FROM " + tablename + "_Aliquots WHERE ensat_id=? AND center_id=? ORDER BY " + aliquotIdName + " DESC;";

        String formId = "1";
        try {
            PreparedStatement ps = conn.prepareStatement(idCheckSql);
            ps.setString(1, pid);
            ps.setString(2, centerid);

            ResultSet rs = ps.executeQuery();
            //ResultSet rs = statement.executeQuery(idCheckSql);
            if (rs.next()) {
                formId = rs.getString(1);
            }

            int formIdInt = Integer.parseInt(formId);
            formIdInt = formIdInt + 1;
            formId = "" + formIdInt;
            //logger.debug("formId: " + formId);
        } catch (Exception e) {
            logger.debug("('" + username + "') Error (updateAliquotTable): " + e.getMessage());
        }


        String aliquotValue = request.getParameter("aliquot_" + paramName);
        //aliquotSql = "INSERT INTO " + tablename + "_Aliquots VALUES(" + formId + "," + nextId + "," + pid + ",'" + centerid + "',";
        aliquotSql = "INSERT INTO " + tablename + "_Aliquots VALUES(?,?,?,?,";
        //aliquotSql += "'" + paramName + "','" + aliquotValue + "');";
        aliquotSql += "?,?);";

        try {
            PreparedStatement ps = conn.prepareStatement(aliquotSql);
            ps.setString(1, formId);
            ps.setInt(2, nextId);
            ps.setString(3, pid);
            ps.setString(4, centerid);
            ps.setString(5, paramName);
            ps.setString(6, aliquotValue);

            //int updateAliquots = statement.executeUpdate(aliquotSql);
            int updateAliquots = ps.executeUpdate();
        } catch (Exception e) {
            logger.debug("('" + username + "') Error (updateAliquotTable): " + e.getMessage());
        }
    }

    protected boolean getMultipleHiddenMenuType(String paramName) {
        return paramName.equals("surgery_first")
                || paramName.equals("surgery_extended")
                || paramName.equals("right_adrenal_max_tumor")
                || paramName.equals("left_adrenal_max_tumor")
                || paramName.equals("mitotane_best_objective")
                || paramName.equals("mitotane_best_objective_adj")
                //|| paramName.equals("phpgl_free")
                //|| paramName.equals("disease_state")
                || paramName.equals("max_tumor_by_ct_right")
                || paramName.equals("max_tumor_by_mr_right")
                || paramName.equals("max_tumor_by_ct_left")
                || paramName.equals("max_tumor_by_mr_left");
                //|| paramName.equals("date_of_death")
                //|| paramName.equals("cause_of_death");
    }

    public Vector<String> getFreezerValues(String ensatId, String centerId, String formId, String dbn, Statement stmt) {
        Vector<String> freezerValues = new Vector<String>();
        String freezerSql = "";

        freezerSql += "SELECT * FROM " + dbn + "_Biomaterial_Freezer_Information WHERE center_id='" + centerId + "' AND ensat_id=" + ensatId + " AND " + dbn.toLowerCase() + "_biomaterial_id=" + formId + " ORDER BY material,aliquot_sequence_id;";

        ResultSet rs = null;
        logger.debug("('" + username + "') Checking freezer information (Ensat ID: " + centerId + "-" + ensatId + ", " + dbn + " Biomaterial form ID: " + formId + ")");
        try {
            rs = stmt.executeQuery(freezerSql);
            while (rs.next()) {
                String freezerOut = rs.getString(6) + "|" + rs.getString(7) + "|" + rs.getString(8) + "|" + rs.getString(9) + "|" + rs.getString(10) + "|" + rs.getString(11) + "|" + rs.getString(12) + "|" + rs.getString(13) + "|" + rs.getString(14) + "|" + rs.getString(15);
                //logger.debug("freezerOut: " + freezerOut);
                freezerValues.add(freezerOut);
            }
        } catch (Exception e) {
            logger.debug("('" + username + "') Error (getFreezerValues): " + e.getMessage());
        }
        return freezerValues;
    }

    private boolean getTableIsMultiple(String tablename) {
        return tablename.equals("Pheo_FirstDiagnosisPresentation")
                || tablename.equals("Pheo_OtherOrgans")
                || tablename.equals("Pheo_HormoneSymptoms")
                || tablename.equals("Pheo_TumorSymptoms")
                || tablename.equals("Pheo_FirstDiagnosisTNM")
                || tablename.equals("Pheo_MalignantDiagnosisTNM")
                || tablename.equals("ACC_Imaging")
                || tablename.equals("Associated_Studies");
    }

    private void executeMultipleUpdate(String tablename, String pid, String centerid, Connection conn, HttpServletRequest request) {

        logger.debug("tablename (executeMultipleUpdate): " + tablename);
        
        //Clear out the values that are going to be used in the multiple update
        Vector<String> lastPageParamValues = new Vector<String>();

        //Delete the current values
        String deleteSql = "";
        //deleteSql = "DELETE FROM " + tablename + " WHERE ensat_id=" + pid + " AND center_id='" + centerid + "';";
        deleteSql = "DELETE FROM " + tablename + " WHERE ensat_id=? AND center_id=?;";
        //logger.debug("Updating deleteSql: " + deleteSql);
        try {
            PreparedStatement ps = conn.prepareStatement(deleteSql);
            ps.setString(1, pid);
            ps.setString(2, centerid);

            //int update = statement.executeUpdate(deleteSql);
            int update = ps.executeUpdate();
        } catch (Exception e) {
            logger.debug("('" + username + "') Error (executeMultipleUpdate): " + e.getMessage());
        }

        //Now populate with the new parameters
        String[] paramLabel = null;
        int typeNum = 0;
        if (tablename.equals("Pheo_FirstDiagnosisPresentation")) {
            typeNum = 1;
            paramLabel = new String[typeNum];
            paramLabel[0] = "presentation_first_tumor";
        } else if (tablename.equals("Pheo_OtherOrgans")) {
            typeNum = 1;
            paramLabel = new String[typeNum];
            paramLabel[0] = "system_organ";
        } else if (tablename.equals("Pheo_HormoneSymptoms")) {
            typeNum = 1;
            paramLabel = new String[typeNum];
            paramLabel[0] = "hormone_symptoms";
        } else if (tablename.equals("Pheo_TumorSymptoms")) {
            typeNum = 1;
            paramLabel = new String[typeNum];
            paramLabel[0] = "tumor_symptoms";
        } else if (tablename.equals("Pheo_FirstDiagnosisTNM")) {
            typeNum = 1;
            paramLabel = new String[typeNum];
            paramLabel[0] = "first_diagnosis_tnm";
        } else if (tablename.equals("Pheo_MalignantDiagnosisTNM")) {
            typeNum = 1;
            paramLabel = new String[typeNum];
            paramLabel[0] = "malignant_diagnosis_tnm";
        } else if (tablename.equals("ACC_Imaging")) {
            typeNum = 1;
            paramLabel = new String[typeNum];
            paramLabel[0] = "imaging";
        } else if (tablename.equals("Associated_Studies")) {
            typeNum = 1;
            paramLabel = new String[typeNum];
            paramLabel[0] = "associated_studies";
        }

        String paramValueNumInStr = request.getParameter(paramLabel[0] + "_num");
        int paramValueNumIn = Integer.parseInt(paramValueNumInStr);
        //logger.debug("paramValueNumIn: " + paramValueNumIn);
        for (int i = 0; i < paramValueNumIn; i++) {
            String valueIn = request.getParameter(paramLabel[0] + "_" + (i + 1));
            lastPageParamValues.add(valueIn);
        }

        //If table is Associated_Studies run a check for the incidental study IDs and add if necessary
        String[] otherStudyIDs = {"adiuvo",
            "adiuvo_observational",
            "lysosafe",
            "firstmappp",
            "german_cushing",
            "german_conn"};
        Vector<String> otherStudyIDNames = new Vector<String>();
        Vector<String> otherStudyIDValues = new Vector<String>();
        int otherStudyNum = otherStudyIDs.length;

        for (int j = 0; j < otherStudyNum; j++) {
            String otherStudyParamName = "" + otherStudyIDs[j] + "_id";
            String otherStudyParamValue = request.getParameter(otherStudyParamName);
            if (otherStudyParamValue != null) {
                otherStudyIDNames.add(otherStudyParamName);
                otherStudyIDValues.add(otherStudyParamValue);
            }
        }


        //Run an check for the last ID if the table is multiple
        int multipleNextId = 1;
        String idLabel = this.getMultipleSubTableIdName(tablename);
        //String nextIdCheck = "SELECT " + idLabel + " FROM " + tablename + " WHERE ensat_id=" + pid + " AND center_id='" + centerid + "' ORDER BY " + idLabel + " DESC";
        String nextIdCheck = "SELECT " + idLabel + " FROM " + tablename + " ORDER BY " + idLabel + " DESC";
        try {
            PreparedStatement ps = conn.prepareStatement(nextIdCheck);
            /*
             * ps.setString(1, pid); ps.setString(2, centerid);
             */

            //ResultSet idCheckRs = statement.executeQuery(nextIdCheck);
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

        for (int i = 0; i < paramValueNumIn; i++) {
            String updateSql = "INSERT INTO " + tablename + " VALUES(";
            //updateSql += "" + multipleNextId + ",";
            //updateSql += "" + pid + ",'" + centerid + "',";
            //updateSql += "'" + lastPageParamValues.get(overallIndexCount) + "'";
            updateSql += "?,";
            updateSql += "?,?,";
            updateSql += "?";

            if (tablename.equals("Associated_Studies")) {
                updateSql += ",?,?";
            }

            updateSql += ");";
            multipleNextId++;
            overallUpdateSql = updateSql;
            try {
                PreparedStatement ps = conn.prepareStatement(updateSql);
                ps.setInt(1, multipleNextId);
                ps.setString(2, pid);
                ps.setString(3, centerid);

                if (tablename.equals("Associated_Studies")) {
                    String studyLabel = this.getStudyLabel(lastPageParamValues.get(i));
                    String studyname = lastPageParamValues.get(overallIndexCount);

                    //ps.setString(4, studyName);
                    //ps.setString(5, lastPageParamValues.get(overallIndexCount));

                    ps.setString(4, studyname);
                    ps.setString(5, studyLabel);


                    //This is the particular study identifier
                    int thisStudyIndex = -1;
                    for (int k = 0; k < otherStudyIDNames.size(); k++) {
                        String studyIDNameIn = otherStudyIDNames.get(k);
                        if (studyIDNameIn.equals(studyname + "_id")) {
                            thisStudyIndex = k;
                        }
                    }
                    if (thisStudyIndex != -1) {
                        ps.setString(6, otherStudyIDValues.get(thisStudyIndex));
                    } else {
                        ps.setString(6, "");
                    }
                } else {
                    ps.setString(4, lastPageParamValues.get(overallIndexCount));
                }


                //ps.setString(4, lastPageParamValues.get(overallIndexCount));


                logger.debug("('" + username + "') MULTIPLE RECORD UPDATED - details to go here...");
                //int update = statement.executeUpdate(updateSql);
                int update = ps.executeUpdate();
            } catch (Exception e) {
                logger.debug("('" + username + "') Error (executeMultipleUpdate): " + e.getMessage());
            }
            overallIndexCount++;
        }

    }

    public String getMultipleSubTableIdName(String tablename) {

        String idLabel = "";
        if (tablename.equals("Pheo_FirstDiagnosisPresentation")) {
            idLabel = "pheo_first_diagnosis_presentation_id";
        } else if (tablename.equals("Pheo_OtherOrgans")) {
            idLabel = "pheo_other_organs_id";
        } else if (tablename.equals("Pheo_HormoneSymptoms")) {
            idLabel = "pheo_hormone_symptoms_id";
        } else if (tablename.equals("Pheo_TumorSymptoms")) {
            idLabel = "pheo_tumor_symptoms_id";
        } else if (tablename.equals("Pheo_FirstDiagnosisTNM")) {
            idLabel = "pheo_first_diagnosis_tnm_id";
        } else if (tablename.equals("Pheo_MalignantDiagnosisTNM")) {
            idLabel = "pheo_malignant_diagnosis_tnm_id";
        } else if (tablename.equals("ACC_Imaging")) {
            idLabel = "acc_imaging_id";
        } else if (tablename.equals("Associated_Studies")) {
            idLabel = "assoc_study_id";
        }
        return idLabel;
    }

    public String getJavascriptValidationArray(Vector<Vector> parameters, String baseUrl) {

        String outputStr = "";
        outputStr = "<script type=\"text/JavaScript\">";

        int paramNum = parameters.size();
        for (int i = 0; i < paramNum; i++) {
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

    protected String reformatDateValue(String dateValueIn) {

        //Change "dd-mm-yyyy" to "yyyy-mm-dd"
        int hyphenIndex1 = dateValueIn.indexOf("-");
        int hyphenIndex2 = dateValueIn.lastIndexOf("-");

        String dateValueOut = "";
        if (hyphenIndex1 == -1 || hyphenIndex2 == -1 || (hyphenIndex1 == hyphenIndex2)) {
            return dateValueOut;
        } else {
            String dayStr = dateValueIn.substring(0, hyphenIndex1);
            String yearStr = dateValueIn.substring(hyphenIndex2 + 1, dateValueIn.length());
            String monthStr = dateValueIn.substring(hyphenIndex1 + 1, hyphenIndex2);
            dateValueOut = "" + yearStr + "-" + monthStr + "-" + dayStr;
            return dateValueOut;
        }
    }

    protected String reformatDateValueEuropean(String dateValueIn) {

        //Change "yyyy-mm-dd" to "dd-mm-yyyy"
        int hyphenIndex1 = dateValueIn.indexOf("-");
        int hyphenIndex2 = dateValueIn.lastIndexOf("-");

        String dateValueOut = "";
        if (hyphenIndex1 == -1 || hyphenIndex2 == -1 || (hyphenIndex1 == hyphenIndex2)) {
            return dateValueOut;
        } else {
            String yearStr = dateValueIn.substring(0, hyphenIndex1);
            String dayStr = dateValueIn.substring(hyphenIndex2 + 1, dateValueIn.length());
            String monthStr = dateValueIn.substring(hyphenIndex1 + 1, hyphenIndex2);
            dateValueOut = "" + dayStr + "-" + monthStr + "-" + yearStr;
            return dateValueOut;
        }
    }

    public Vector<Vector> getDynamicMenus(Vector<Vector> parameters, Vector<Vector> menus, String userCenter, Connection conn, String pid, String centerid, String dbn) {

        Vector<String> menuIDs = new Vector<String>();
        int paramCount = parameters.size();
        //System.out.println("paramCount: " + paramCount);

        for (int i = 0; i < paramCount; i++) {
            Vector<String> rowIn = parameters.get(i);
            String paramType = rowIn.get(2);
            //System.out.println("paramType #" + i + ": " + paramType);
            if (paramType.equals("dynamicmenuonload")) {
                String paramName = rowIn.get(1);
                menuIDs.add(paramName);
            }
        }

        int menuIDnum = menuIDs.size();
        for (int i = 0; i < menuIDnum; i++) {

            Vector<String> dynamicMenu = new Vector<String>();
            if (menuIDs.get(0).equals("pathology_derived_from")) {

                //Run query based on this                
                try {
                    //String sql = "SELECT acc_surgery_id FROM ACC_Surgery WHERE center_id='" + centerid + "' AND ensat_id=" + pid + " ORDER BY acc_surgery_id;";
                    String sql = "SELECT acc_surgery_id FROM ACC_Surgery WHERE center_id=? AND ensat_id=? ORDER BY acc_surgery_id;";
                    PreparedStatement ps = conn.prepareStatement(sql);
                    ps.setString(1, centerid);
                    ps.setString(2, pid);
                    ResultSet rs = ps.executeQuery();

                    //ResultSet rs = stmt.executeQuery(sql);



                    dynamicMenu.add("");
                    dynamicMenu.add("");
                    dynamicMenu.add("s");
                    while (rs.next()) {
                        dynamicMenu.add("Surgery " + rs.getString(1));
                    }

                    //Add a final "no surgery" option
                    dynamicMenu.add("Biopsy");

                    rs.close();
                    //stmt.close();

                } catch (Exception e) {
                    logger.debug("('" + username + "') Error (getDynamicMenus): " + e.getMessage());
                }

            } else if (menuIDs.get(i).equals("associated_studies")) {
                try {
                    //String sql = "SELECT * FROM Studies ORDER BY study_name;";                    
                    String sql = "SELECT DISTINCT * FROM Studies,Study_Type WHERE Studies.study_id=Study_Type.study_id AND Study_Type.tumor_type=? ORDER BY study_label;";
                    PreparedStatement ps = conn.prepareStatement(sql);
                    ps.setString(1, dbn);
                    ResultSet rs = ps.executeQuery();

                    dynamicMenu.add("");
                    dynamicMenu.add("");
                    dynamicMenu.add("m");
                    while (rs.next()) {
                        String studyNameLabel = rs.getString(3);
                        dynamicMenu.add(studyNameLabel);
                    }
                    rs.close();
                    //conn.close();

                } catch (Exception e) {
                    logger.debug("('" + username + "') Error (getDynamicMenus): " + e.getMessage());
                }
                //menus.add(dynamicMenu);                    
            }
            menus.add(dynamicMenu);
        }

        return menus;
    }

    protected String getBiomaterialLabels(ResultSet rs) {

        String outputStr = "";
        try {
            //rs.beforeFirst();

            ResultSetMetaData rsmd = rs.getMetaData();
            int colCount = rsmd.getColumnCount();

            //logger.debug("colCount: " + colCount);

            String bioLabelsOut = "";
            //if(rs.next()){
            for (int i = 5; i <= colCount; i++) {
                String valueIn = rs.getString(i);
                //logger.debug("valueIn(" + i + "): " + valueIn);
                if (valueIn == null) {
                    valueIn = "";
                }
                if (valueIn.equals("Yes")) {
                    if (i == 5) {
                        bioLabelsOut += "Tumor tissue (frozen)<br/>";
                    } else if (i == 7) {
                        bioLabelsOut += "Tumor tissue (paraffin)<br/>";
                    } else if (i == 8) {
                        bioLabelsOut += "Tumor tissue (DNA)<br/>";
                    } else if (i == 9) {
                        bioLabelsOut += "Leukocyte DNA<br/>";
                    } else if (i == 10) {
                        bioLabelsOut += "Plasma<br/>";
                    } else if (i == 11) {
                        bioLabelsOut += "Heparin plasma<br/>";
                    } else if (i == 12) {
                        bioLabelsOut += "Serum<br/>";
                    } else if (i == 13) {
                        bioLabelsOut += "24h urine<br/>";
                    } else if (i == 15) {
                        bioLabelsOut += "Spot urine<br/>";
                    } else if (i == 16) {
                        bioLabelsOut += "Normal tissue (frozen)<br/>";
                    } else if (i == 17) {
                        bioLabelsOut += "Normal tissue (paraffin)<br/>";
                    } else if (i == 18) {
                        bioLabelsOut += "Normal tissue (DNA)<br/>";
                    } else if (i == 19) {
                        bioLabelsOut += "Whole blood<br/>";
                    }
                }
            }
            //}            
            outputStr = bioLabelsOut;
        } catch (Exception e) {
            logger.debug("('" + username + "') Error (getBiomaterialLabel): " + e.getMessage());
        }
        if (outputStr.length() >= 5) {
            outputStr = outputStr.substring(0, outputStr.length() - 5);
        }
        //logger.debug("outputStr: " + outputStr);

        return outputStr;
    }

    protected boolean getNapacaFollowupFlag(ResultSet rs) {

        boolean imagingFlag = false;
        try {
            String imagingValue = rs.getString("followup_imaging");
            imagingFlag = imagingValue.equals("Yes");

        } catch (Exception e) {
            logger.debug("('" + username + "') Error (getBiomaterialLabel): " + e.getMessage());
        }

        return imagingFlag;
    }

    protected String convertAldo(String valueIn) {

        if (valueIn == null) {
            valueIn = "";
        }

        if (!valueIn.equals("0") && !valueIn.equals("")) {
            double valueDouble = Double.parseDouble(valueIn);
            String valueOut = "" + (valueDouble / 2.77);

            //Truncate to three decimal places
            int pointIndex = valueOut.indexOf(".");
            if (pointIndex != -1 && (valueOut.length() - pointIndex > 3)) {
                valueOut = valueOut.substring(0, pointIndex + 3);
            }
            return "" + valueOut;
        } else {
            return valueIn;
        }
    }

    protected String convertCortisol(String valueIn) {
        if (valueIn == null) {
            valueIn = "";
        }

        if (!valueIn.equals("0") && !valueIn.equals("")) {
            double valueDouble = Double.parseDouble(valueIn);
            String valueOut = "" + (valueDouble / 2.76);

            //Truncate to three decimal places
            int pointIndex = valueOut.indexOf(".");
            if (pointIndex != -1 && (valueOut.length() - pointIndex > 4)) {
                valueOut = valueOut.substring(0, pointIndex + 4);
            }
            return "" + valueOut;
        } else {
            return valueIn;
        }
    }

    protected String convertPlasmaUrine(String valueIn, String parameterIn, String units) {
        if (valueIn == null) {
            valueIn = "";
        }
        if (parameterIn == null) {
            parameterIn = "";
        }

        if (!valueIn.equals("0") && !valueIn.equals("")) {

            double conversionFactor = 0.0;
            if (units.equals("ugday")) {
                conversionFactor = 1000.0;
            } else {
                conversionFactor = this.getPheoConversionFactor(parameterIn);
            }

            double valueDouble = Double.parseDouble(valueIn);
            String valueOut = "" + (valueDouble / conversionFactor);

            //Truncate to three decimal places
            int pointIndex = valueOut.indexOf(".");
            if (pointIndex != -1 && (valueOut.length() - pointIndex > 4)) {
                valueOut = valueOut.substring(0, pointIndex + 4);
            }
            return "" + valueOut;
        } else {
            return valueIn;
        }
    }

    protected double getPheoConversionFactor(String parameterIn) {
        double conversionFactor = 0.0;
        if (parameterIn.equals("plasma_e")) {
            conversionFactor = 5.46;
        } else if (parameterIn.equals("plasma_n")) {
            conversionFactor = 5.91;
        } else if (parameterIn.equals("plasma_free_m")) {
            conversionFactor = 5.08;
        } else if (parameterIn.equals("plasma_free_n")) {
            conversionFactor = 5.46;
        } else if (parameterIn.equals("plasma_free_methox")) {
            conversionFactor = 167.2;
        } else if (parameterIn.equals("plasma_dopamine_conc")) {
            conversionFactor = 152.9;
        } else if (parameterIn.equals("urine_free_e")) {
            conversionFactor = 5.07;
        } else if (parameterIn.equals("urine_free_n")) {
            conversionFactor = 5.46;
        } else if (parameterIn.equals("urine_m")) {
            conversionFactor = 5.07;
        } else if (parameterIn.equals("urine_n")) {
            conversionFactor = 5.46;
        }
        return conversionFactor;
    }

    public String getStudyLabel(String studyName) {

        String studyLabel = "";
        if (studyName.equals("eurineact")) {
            studyLabel = "EURINE-ACT";
        } else if (studyName.equals("ki67")) {
            studyLabel = "Ki-67";
        } else if (studyName.equals("stage_3_4_acc")) {
            studyLabel = "Stage III/IV ACC";
        } else if (studyName.equals("acc_mol_marker")) {
            studyLabel = "ACC Molecular Marker";
        } else if (studyName.equals("pmt")) {
            studyLabel = "PMT";
        } else if (studyName.equals("tma")) {
            studyLabel = "TMA";
        } else if (studyName.equals("ltphpgl")) {
            studyLabel = "Long-term PHPGL";
        } else if (studyName.equals("avis2")) {
            studyLabel = "AVIS-2";
        } else if (studyName.equals("pmt3")) {
            studyLabel = "PMT3";
        } else if (studyName.equals("adiuvo")) {
            studyLabel = "ADIUVO";
        } else if (studyName.equals("adiuvo_observational")) {
            studyLabel = "ADIUVO Observational";
        } else if (studyName.equals("hairco")) {
            studyLabel = "HairCo";
        } else if (studyName.equals("hairco2")) {
            studyLabel = "HairCo-2";
        } else if (studyName.equals("firstmappp")) {
            studyLabel = "FIRST-MAPPP";
        } else if (studyName.equals("chiracic")) {
            studyLabel = "CHIRACIC";
        } else if (studyName.equals("german_cushing")) {
            studyLabel = "German Cushing Registry";
        } else if (studyName.equals("german_conn")) {
            studyLabel = "German Conn Registry";
        } else if (studyName.equals("uk_pheo_audit")) {
            studyLabel = "UK Pheo Audit";
        } else if (studyName.equals("lysosafe")) {
            studyLabel = "Lysosafe";
        } else if (studyName.equals("firmact")) {
            studyLabel = "FIRMACT";
        } else if (studyName.equals("mapp_prono")) {
            studyLabel = "MAPP-Prono";
        } else if (studyName.equals("mibg_impact")) {
            studyLabel = "MIBG Impact";
        } else if (studyName.equals("predict_ancillary_firmact")) {
            studyLabel = "Predict Ancillary FIRM-ACT";
        }

        return studyLabel;
    }

    public String getStudyName(String studyLabel) {

        String studyName = "";
        if (studyLabel.equals("EURINE-ACT")) {
            studyName = "eurineact";
        } else if (studyLabel.equals("Ki-67")) {
            studyName = "ki67";
        } else if (studyLabel.equals("Stage III/IV ACC")) {
            studyName = "stage_3_4_acc";
        } else if (studyLabel.equals("ACC Molecular Marker")) {
            studyName = "acc_mol_marker";
        } else if (studyLabel.equals("PMT")) {
            studyName = "pmt";
        } else if (studyLabel.equals("TMA")) {
            studyName = "tma";
        } else if (studyLabel.equals("Long-term PHPGL")) {
            studyName = "ltphpgl";
        } else if (studyLabel.equals("AVIS-2")) {
            studyName = "avis2";
        } else if (studyLabel.equals("PMT3")) {
            studyName = "pmt3";
        } else if (studyLabel.equals("ADIUVO")) {
            studyName = "adiuvo";
        } else if (studyLabel.equals("ADIUVO Observational")) {
            studyName = "adiuvo_observational";
        } else if (studyLabel.equals("HairCo")) {
            studyName = "hairco";
        } else if (studyLabel.equals("HairCo-2")) {
            studyName = "hairco2";
        } else if (studyLabel.equals("FIRST-MAPPP")) {
            studyName = "firstmappp";
        } else if (studyLabel.equals("CHIRACIC")) {
            studyName = "chiracic";
        } else if (studyLabel.equals("German Cushing Registry")) {
            studyName = "german_cushing";
        } else if (studyLabel.equals("German Conn Registry")) {
            studyName = "german_conn";
        } else if (studyLabel.equals("UK Pheo Audit")) {
            studyName = "uk_pheo_audit";
        } else if (studyLabel.equals("Lysosafe")) {
            studyName = "lysosafe";
        } else if (studyLabel.equals("FIRMACT")) {
            studyName = "firmact";
        } else if (studyLabel.equals("MAPP-Prono")) {
            studyName = "mapp_prono";
        } else if (studyLabel.equals("MIBG Impact")) {
            studyName = "mibg_impact";
        } else if (studyLabel.equals("Predict Ancillary FIRM-ACT")) {
            studyName = "predict_ancillary_firmact";
        }

        return studyName;
    }

    public boolean getPatientInStudy(String centerid, String pid, String studyName, Connection conn) {

        boolean patientInStudy = false;

        try {
            String sql = "SELECT study_label FROM Associated_Studies WHERE ensat_id=? AND center_id=?;";

            PreparedStatement ps = conn.prepareStatement(sql);
            ps.setString(1, pid);
            ps.setString(2, centerid);
            ResultSet rs = ps.executeQuery();

            while (rs.next() && !patientInStudy) {
                String studyNameIn = rs.getString(1);
                if (studyNameIn.equals(studyName)) {
                    patientInStudy = true;
                }
            }
        } catch (Exception e) {
            logger.debug("Error (getPatientInStudy): " + e.getMessage());
        }
        return patientInStudy;
    }

    public String getFreezerInfo(String paramName, int index) {
        String outputStr = "";
        outputStr += "<div id='" + paramName + "_freezer_info_" + index + "' class='hide'>";
        outputStr += "<table border='2' width='100%' cellpadding='1'>";
        outputStr += "<tr>";
        outputStr += "<th>";
        outputStr += "" + index + "";
        outputStr += "</th>";
        outputStr += "<td>";
        outputStr += "<div align='center'>";
        outputStr += "Freezer: ";
        outputStr += "<input type='text' size='3' name='" + paramName + "_freezer_" + index + "' onfocus=\"inform=true;\" onblur=\"inform=false;parameterValidate(this.value,this.name,true,'number');\"/>";
        outputStr += "<div id='" + paramName + "_freezer_" + index + "'></div>";
        outputStr += "</div>";
        outputStr += "</td>";
        outputStr += "<td>";
        outputStr += "<div align='center'>";
        outputStr += "Freezer shelf: ";
        outputStr += "<input type='text' size='3' name='" + paramName + "_freezershelf_" + index + "' onfocus=\"inform=true;\" onblur=\"inform=false;parameterValidate(this.value,this.name,true,'number');\"/>";
        outputStr += "<div id='" + paramName + "_freezershelf_" + index + "'></div>";
        outputStr += "</div>";
        outputStr += "</td>";
        outputStr += "<td>";
        outputStr += "<div align='center'>";
        outputStr += "Rack: ";
        outputStr += "<input type='text' size='3' name='" + paramName + "_rack_" + index + "' onfocus=\"inform=true;\" onblur=\"inform=false;parameterValidate(this.value,this.name,true,'number');\"/>";
        outputStr += "<div id='" + paramName + "_rack_" + index + "'></div>";
        outputStr += "</div>";
        outputStr += "</td>";
        outputStr += "<td>";
        outputStr += "<div align='center'>";
        outputStr += "Rack shelf: ";
        outputStr += "<input type='text' size='3' name='" + paramName + "_shelf_" + index + "' onfocus=\"inform=true;\" onblur=\"inform=false;parameterValidate(this.value,this.name,true,'number');\"/>";
        outputStr += "<div id='" + paramName + "_shelf_" + index + "'></div>";
        outputStr += "</div>";
        outputStr += "</td>";
        outputStr += "<td>";
        outputStr += "<div align='center'>";
        outputStr += "Box: ";
        outputStr += "<input type='text' size='3' name='" + paramName + "_box_" + index + "' onfocus=\"inform=true;\" onblur=\"inform=false;parameterValidate(this.value,this.name,true,'number');\"/>";
        outputStr += "<div id='" + paramName + "_box_" + index + "'></div>";
        outputStr += "</div>";
        outputStr += "</td>";
        outputStr += "<td>";
        outputStr += "<div align='center'>";
        outputStr += "Position: ";
        outputStr += "<input type='text' size='3' name='" + paramName + "_position_" + index + "' onfocus=\"inform=true;\" onblur=\"inform=false;parameterValidate(this.value,this.name,true,'number');\"/>";
        outputStr += "<div id='" + paramName + "_position_" + index + "'></div>";
        outputStr += "</div>";
        outputStr += "</td>";
        outputStr += "</tr>";

        //Adding occupancy validation line
        outputStr += "<tr>";
        outputStr += "<td colspan='7'>";
        outputStr += "<div id='" + paramName + "_occupancy_" + index + "' class='errorLabel'>";
        outputStr += "";
        outputStr += "</div>";
        outputStr += "</td>";
        outputStr += "</tr>";

        outputStr += "</table>";
        outputStr += "</div>";
        return outputStr;
    }

    public String getFreezerInfo(String paramName, boolean showHide, Vector<String> freezerValues, int index) {
        //Dissect the freezerValues here

        String freezerVal = "";
        String freezerShelfVal = "";
        String rackVal = "";
        String shelfVal = "";
        String boxVal = "";
        String positionVal = "";
        String bioIdVal = "";
        String usedVal = "";
        String transferredVal = "";
        int freezerValueNum = freezerValues.size();

        boolean materialFound = false;
        int elemCount = 0;
        while (!materialFound && (elemCount < freezerValueNum)) {
            String freezerValueStr = freezerValues.get(elemCount);
            //logger.debug("freezerValueStr: " + freezerValueStr);            

            //StringTokenizer st = new StringTokenizer(freezerValueStr, "|");
            String[] freezerTokens = freezerValueStr.split("\\|");
            
            if(freezerTokens != null){
                int tokenNum = freezerTokens.length;
                //logger.debug("tokenNum: " + tokenNum);
                
                /*for(int i=0; i<tokenNum; i++){
                    logger.debug("freezerTokens[" + i + "]: " + freezerTokens[i]);
                }*/
                
            //if (st.hasMoreTokens()) {
                //String freezerParam = st.nextToken(); //This will be the material name, so takes cursor to beginning of that material listing
                String freezerParam = freezerTokens[0];
                if (freezerParam.equals(paramName) && showHide) {
                    materialFound = true;

                    //Move up to index-1 in the listing
                    freezerValueStr = freezerValues.get(elemCount + (index - 1));
                    //StringTokenizer st2 = new StringTokenizer(freezerValueStr, "|");
                    String[] freezerTokens2 = freezerValueStr.split("\\|");
                    int st2TokenCount = 0;
                    int tokenNum2 = freezerTokens2.length;
                    //while (st2.hasMoreTokens()) {
                    while(st2TokenCount < tokenNum2){
                        //String tokenIn = st2.nextToken();
                        String tokenIn = freezerTokens2[st2TokenCount];

                        //This is the material name again (which we don't need on the second run)
                        if (st2TokenCount == 1) {
                            freezerVal = tokenIn;
                        } else if (st2TokenCount == 2) {
                            freezerShelfVal = tokenIn;
                        } else if (st2TokenCount == 3) {
                            rackVal = tokenIn;
                        } else if (st2TokenCount == 4) {
                            shelfVal = tokenIn;
                        } else if (st2TokenCount == 5) {
                            boxVal = tokenIn;
                        } else if (st2TokenCount == 6) {
                            positionVal = tokenIn;
                        } else if (st2TokenCount == 7) {
                            bioIdVal = tokenIn;
                        } else if (st2TokenCount == 8) {
                            usedVal = tokenIn;
                        } else if (st2TokenCount == 9) {
                            transferredVal = tokenIn;
                        }

                        /*
                         * logger.debug("freezerVal: " + freezerVal);
                         * logger.debug("freezerShelfVal: " + freezerVal);
                         * logger.debug("rackVal: " + freezerVal);
                         * logger.debug("shelfVal: " + freezerVal);
                         * logger.debug("boxVal: " + freezerVal);
                         * logger.debug("positionVal: " + freezerVal);
                         */
                        st2TokenCount++;
                    }
                }
            }
            elemCount++;
        }
        
        //logger.debug("transferredVal: " + transferredVal);
        
        boolean materialUsed = usedVal.equalsIgnoreCase("Yes");
        boolean materialTransferred = !transferredVal.equals("");
        String disabledFlag = "";
        if(materialUsed || materialTransferred){
            disabledFlag = "disabled";
        }
        

        String outputStr = "";

        if (!paramName.equals("normal_tissue")
                && !paramName.equals("normal_tissue_paraffin")
                && !paramName.equals("normal_tissue_dna")) {

            String showHideStr = "";
            if (showHide) {
                showHideStr = "show";
            } else {
                showHideStr = "hide";
            }
            outputStr += "<div id='" + paramName + "_freezer_info_" + index + "' class='" + showHideStr + "'>";
            outputStr += "<table border='2' width='100%' cellpadding='1'>";
            outputStr += "<tr>";
            outputStr += "<th>";
            outputStr += "" + index + "";
            outputStr += "</th>";
            outputStr += "<td>";
            outputStr += "<div align='center'>";
            outputStr += "Freezer: ";
            outputStr += "<input type='text' size='3' name='" + paramName + "_freezer_" + index + "' value='" + freezerVal + "' onfocus=\"inform=true;\" onblur=\"inform=false;\" " + disabledFlag + " />";
            if(!disabledFlag.equals("")){
                outputStr += "<input type='hidden' name='" + paramName + "_freezer_" + index + "' value='" + freezerVal + "' />";
            }
            outputStr += "</div>";
            outputStr += "</td>";
            outputStr += "<td>";
            outputStr += "<div align='center'>";
            outputStr += "Freezer shelf: ";
            outputStr += "<input type='text' size='3' name='" + paramName + "_freezershelf_" + index + "' value='" + freezerShelfVal + "' onfocus=\"inform=true;\" onblur=\"inform=false;\" " + disabledFlag + " />";
            if(!disabledFlag.equals("")){
                outputStr += "<input type='hidden' name='" + paramName + "_freezershelf_" + index + "' value='" + freezerShelfVal + "' />";
            }            
            outputStr += "</div>";
            outputStr += "</td>";
            outputStr += "<td>";
            outputStr += "<div align='center'>";
            outputStr += "Rack: ";
            outputStr += "<input type='text' size='3' name='" + paramName + "_rack_" + index + "' value='" + rackVal + "' onfocus=\"inform=true;\" onblur=\"inform=false;\" " + disabledFlag + " />";
            if(!disabledFlag.equals("")){
                outputStr += "<input type='hidden' name='" + paramName + "_rack_" + index + "' value='" + rackVal + "' />";
            }            
            outputStr += "</div>";
            outputStr += "</td>";
            outputStr += "<td>";
            outputStr += "<div align='center'>";
            outputStr += "Rack shelf: ";
            outputStr += "<input type='text' size='3' name='" + paramName + "_shelf_" + index + "' value='" + shelfVal + "' onfocus=\"inform=true;\" onblur=\"inform=false;\" " + disabledFlag + " />";
            if(!disabledFlag.equals("")){
                outputStr += "<input type='hidden' name='" + paramName + "_shelf_" + index + "' value='" + shelfVal + "' />";
            }
            outputStr += "</div>";
            outputStr += "</td>";
            outputStr += "<td>";
            outputStr += "<div align='center'>";
            outputStr += "Box: ";
            outputStr += "<input type='text' size='3' name='" + paramName + "_box_" + index + "' value='" + boxVal + "' onfocus=\"inform=true;\" onblur=\"inform=false;\" " + disabledFlag + " />";
            if(!disabledFlag.equals("")){
                outputStr += "<input type='hidden' name='" + paramName + "_box_" + index + "' value='" + boxVal + "' />";
            }
            outputStr += "</div>";
            outputStr += "</td>";
            outputStr += "<td>";
            outputStr += "<div align='center'>";
            outputStr += "Position: ";
            outputStr += "<input type='text' size='3' name='" + paramName + "_position_" + index + "' value='" + positionVal + "' onfocus=\"inform=true;\" onblur=\"inform=false;\" " + disabledFlag + " />";
            if(!disabledFlag.equals("")){
                outputStr += "<input type='hidden' name='" + paramName + "_position_" + index + "' value='" + positionVal + "' />";
            }
            outputStr += "</div>";
            outputStr += "</td>";
            outputStr += "</tr>";
            
            //Add the bio ID beneath the freezer position
            outputStr += "<tr>";
            outputStr += "<td colspan='7'>";
            outputStr += "<strong>Bio ID:</strong> " + bioIdVal;
            outputStr += "<br/><input type='hidden' name='" + paramName + "_bio_id_" + index + "' value='" + bioIdVal + "' />";
            if(materialUsed){
                outputStr += "<div class='errorLabel'><strong>Material Used</strong></div> ";
                outputStr += "<br/><input type='hidden' name='" + paramName + "_material_used_" + index + "' value='Yes' />";
            }else{
                outputStr += "<input type='hidden' name='" + paramName + "_material_used_" + index + "' value='No' />";
            }
            if(materialTransferred){
                //String transferCenter = transferredVal.substring(0,transferredVal.indexOf(" - "));
                //outputStr += "<div class='errorLabel'><strong>Material Transferred - " + transferCenter + "</strong></div> ";
                outputStr += "<div class='errorLabel'><strong>Material Transferred - " + transferredVal + "</strong></div> ";
                outputStr += "<br/><input type='hidden' name='" + paramName + "_material_transferred_" + index + "' value='" + transferredVal + "' />";
            }else{
                outputStr += "<input type='hidden' name='" + paramName + "_material_transferred_" + index + "' value='' />";
            }                        
            outputStr += "</td>";
            outputStr += "</tr>";
            
            outputStr += "</table>";
            outputStr += "</div>";

        }
        return outputStr;

    }

    protected String getFreezerConfirmHtml(HttpServletRequest request, Vector<String> lastPageParamNames, int i) {

        String outputStr = "";

        if (!lastPageParamNames.get(i).equals("normal_tissue")
                && !lastPageParamNames.get(i).equals("normal_tissue_paraffin")
                && !lastPageParamNames.get(i).equals("normal_tissue_dna")) {

            //Get the relevant aliquot number
            String aliquotNumber = request.getParameter("aliquot_" + lastPageParamNames.get(i));
            int aliquotNumberInt = 1;
            try {
                aliquotNumberInt = Integer.parseInt(aliquotNumber);
            } catch (NumberFormatException nfe) {
                logger.debug("NumberFormatException: " + nfe.getMessage());
                aliquotNumberInt = 1;
            }

            outputStr += "<table border='2' width='100%' cellpadding='1'>";

            for (int j = 0; j < aliquotNumberInt; j++) {

                outputStr += "<tr>";

                String freezerValue = request.getParameter(lastPageParamNames.get(i) + "_freezer_" + (j + 1));
                String freezerShelfValue = request.getParameter(lastPageParamNames.get(i) + "_freezershelf_" + (j + 1));
                String shelfValue = request.getParameter(lastPageParamNames.get(i) + "_shelf_" + (j + 1));
                String rackValue = request.getParameter(lastPageParamNames.get(i) + "_rack_" + (j + 1));
                String boxValue = request.getParameter(lastPageParamNames.get(i) + "_box_" + (j + 1));
                String positionValue = request.getParameter(lastPageParamNames.get(i) + "_position_" + (j + 1));
                
                String bioIdValue = request.getParameter(lastPageParamNames.get(i) + "_bio_id_" + (j + 1));
                String materialUsedValue = request.getParameter(lastPageParamNames.get(i) + "_material_used_" + (j + 1));
                String materialTransferredValue = request.getParameter(lastPageParamNames.get(i) + "_material_transferred_" + (j + 1));

                outputStr += "<th>";
                outputStr += "<div align='center'>Aliquot #" + (j + 1) + "</div>";
                outputStr += "</th>";

                outputStr += "<td><div align='center'>Freezer: <strong>" + freezerValue + "</strong></div>";
                outputStr += "<input type='hidden' value='" + freezerValue + "' name='" + lastPageParamNames.get(i) + "_freezer_" + (j + 1) + "'>";
                outputStr += "</td>";
                outputStr += "<td><div align='center'>Freezer shelf: <strong>" + freezerShelfValue + "</strong></div>";
                outputStr += "<input type='hidden' value='" + freezerShelfValue + "' name='" + lastPageParamNames.get(i) + "_freezershelf_" + (j + 1) + "'>";
                outputStr += "</td>";
                outputStr += "<td><div align='center'>Rack: <strong>" + rackValue + "</strong></div>";
                outputStr += "<input type='hidden' value='" + rackValue + "' name='" + lastPageParamNames.get(i) + "_rack_" + (j + 1) + "'>";
                outputStr += "</td>";
                outputStr += "<td><div align='center'>Rack shelf: <strong>" + shelfValue + "</strong></div>";
                outputStr += "<input type='hidden' value='" + shelfValue + "' name='" + lastPageParamNames.get(i) + "_shelf_" + (j + 1) + "'>";
                outputStr += "</td>";
                outputStr += "<td><div align='center'>Box: <strong>" + boxValue + "</strong></div>";
                outputStr += "<input type='hidden' value='" + boxValue + "' name='" + lastPageParamNames.get(i) + "_box_" + (j + 1) + "'>";
                outputStr += "</td>";
                outputStr += "<td><div align='center'>Position: <strong>" + positionValue + "</strong></div>";
                outputStr += "<input type='hidden' value='" + positionValue + "' name='" + lastPageParamNames.get(i) + "_position_" + (j + 1) + "'>";
                outputStr += "</td>";
                
                outputStr += "<td><div align='center'>Bio ID: <strong>" + bioIdValue + "</strong></div>";
                outputStr += "<input type='hidden' value='" + bioIdValue + "' name='" + lastPageParamNames.get(i) + "_bio_id_" + (j + 1) + "'>";
                outputStr += "</td>";
                outputStr += "<td><div align='center'>Material Used: <strong>" + materialUsedValue + "</strong></div>";
                outputStr += "<input type='hidden' value='" + materialUsedValue + "' name='" + lastPageParamNames.get(i) + "_material_used_" + (j + 1) + "'>";
                outputStr += "</td>";
                outputStr += "<td><div align='center'>Material Transferred: <strong>" + materialTransferredValue + "</strong></div>";
                outputStr += "<input type='hidden' value='" + materialTransferredValue + "' name='" + lastPageParamNames.get(i) + "_material_transferred_" + (j + 1) + "'>";
                outputStr += "</td>";
                
                
                outputStr += "</tr>";
            }

            outputStr += "</table>";

        }
        return outputStr;
    }

    protected String getFreezerConfirmHtml(HttpServletRequest request, Vector<String> lastPageParamNames, int i, String labelIn) {

        String outputStr = "";

        String paramLabelIn = this.getNormalTissueParamLabel(labelIn);

        //Get the relevant aliquot number
        String aliquotNumber = request.getParameter("aliquot_" + lastPageParamNames.get(i) + "_" + paramLabelIn);
        int aliquotNumberInt = 1;
        try {
            aliquotNumberInt = Integer.parseInt(aliquotNumber);
        } catch (NumberFormatException nfe) {
            logger.debug("NumberFormatException: " + nfe.getMessage());
            aliquotNumberInt = 1;
        }

        outputStr += "<table border='2' width='100%' cellpadding='1'>";

        outputStr += "<tr><th colspan='6'><div align='center'>" + labelIn + " (" + aliquotNumber + ") ";
        outputStr += "<input type=\"hidden\" name=\"aliquot_" + lastPageParamNames.get(i) + "_" + paramLabelIn + "\" value=\"" + aliquotNumber + "\"/>";
        outputStr += "</div></th></tr>";

        for (int j = 0; j < aliquotNumberInt; j++) {

            outputStr += "<tr>";

            String freezerValue = request.getParameter(lastPageParamNames.get(i) + "_" + paramLabelIn + "_freezer_" + (j + 1));
            String freezerShelfValue = request.getParameter(lastPageParamNames.get(i) + "_" + paramLabelIn + "_freezershelf_" + (j + 1));
            String shelfValue = request.getParameter(lastPageParamNames.get(i) + "_" + paramLabelIn + "_shelf_" + (j + 1));
            String rackValue = request.getParameter(lastPageParamNames.get(i) + "_" + paramLabelIn + "_rack_" + (j + 1));
            String boxValue = request.getParameter(lastPageParamNames.get(i) + "_" + paramLabelIn + "_box_" + (j + 1));
            String positionValue = request.getParameter(lastPageParamNames.get(i) + "_" + paramLabelIn + "_position_" + (j + 1));

            outputStr += "<th>";
            outputStr += "<div align='center'>Aliquot #" + (j + 1) + "</div>";
            outputStr += "</th>";

            outputStr += "<td><div align='center'>Freezer: <strong>" + freezerValue + "</strong></div>";
            outputStr += "<input type='hidden' value='" + freezerValue + "' name='" + lastPageParamNames.get(i) + "_" + paramLabelIn + "_freezer_" + (j + 1) + "'>";
            outputStr += "</td>";
            outputStr += "<td><div align='center'>Freezer shelf: <strong>" + freezerShelfValue + "</strong></div>";
            outputStr += "<input type='hidden' value='" + freezerShelfValue + "' name='" + lastPageParamNames.get(i) + "_" + paramLabelIn + "_freezershelf_" + (j + 1) + "'>";
            outputStr += "</td>";
            outputStr += "<td><div align='center'>Rack: <strong>" + rackValue + "</strong></div>";
            outputStr += "<input type='hidden' value='" + rackValue + "' name='" + lastPageParamNames.get(i) + "_" + paramLabelIn + "_rack_" + (j + 1) + "'>";
            outputStr += "</td>";
            outputStr += "<td><div align='center'>Rack shelf: <strong>" + shelfValue + "</strong></div>";
            outputStr += "<input type='hidden' value='" + shelfValue + "' name='" + lastPageParamNames.get(i) + "_" + paramLabelIn + "_shelf_" + (j + 1) + "'>";
            outputStr += "</td>";
            outputStr += "<td><div align='center'>Box: <strong>" + boxValue + "</strong></div>";
            outputStr += "<input type='hidden' value='" + boxValue + "' name='" + lastPageParamNames.get(i) + "_" + paramLabelIn + "_box_" + (j + 1) + "'>";
            outputStr += "</td>";
            outputStr += "<td><div align='center'>Position: <strong>" + positionValue + "</strong></div>";
            outputStr += "<input type='hidden' value='" + positionValue + "' name='" + lastPageParamNames.get(i) + "_" + paramLabelIn + "_position_" + (j + 1) + "'>";
            outputStr += "</td>";
            outputStr += "</tr>";
        }

        outputStr += "</table>";
        return outputStr;
    }

    protected boolean getNormalTissueParameter(String paramName) {

        boolean isNormalTissue =
                paramName.equals("normal_tissue_options")
                || paramName.equals("normal_tissue_paraffin_options")
                || paramName.equals("normal_tissue_dna_options");
        return isNormalTissue;
    }

    protected String getNormalTissueParamLabel(String paramLabel) {

        String outputStr = "";
        if (paramLabel.equals("Adjacent Adrenal")) {
            outputStr = "adjacentadrenal";
        } else if (paramLabel.equals("Kidney")) {
            outputStr = "kidney";
        } else if (paramLabel.equals("Liver")) {
            outputStr = "liver";
        } else if (paramLabel.equals("Lung")) {
            outputStr = "lung";
        } else if (paramLabel.equals("Lymph Node")) {
            outputStr = "lymphnode";
        } else if (paramLabel.equals("Fat (Periadrenal)")) {
            outputStr = "fatperiadrenal";
        } else if (paramLabel.equals("Fat (Subcutaneous)")) {
            outputStr = "fatsubcutaneous";
        } else if (paramLabel.equals("Others")) {
            outputStr = "others";
        }


        return outputStr;
    }

    private String getAliquotMenu(String paramName) {

        String outputStr = "";

        outputStr += "<select name='aliquot_" + paramName + "' ";
        outputStr += "onchange='showFreezerAliquotNumberUpdate(this.name,this.value)";
        /*
         * if(paramName.equals("normal_tissue") ||
         * paramName.equals("normal_tissue_paraffin") ||
         * paramName.equals("normal_tissue_dna")){ outputStr +=
         * "normalTissueFreezerShow('SOMETHING','" + paramName +
         * "',this.value);"; }
         */
        outputStr += "' >";

        for (int i = 0; i < 10; i++) {
            outputStr += "<option";
            if (i == 1) {
                outputStr += " selected ";
            }
            outputStr += " value='" + i + "'>" + i + "</option>";
        }
        outputStr += "</select>";
        return outputStr;
    }
    /*
     * public String getFreezerInfo(String paramName, int index) { String
     * outputStr = ""; outputStr += "<div id='" + paramName + "_freezer_info_" +
     * index + "' class='hide'>"; outputStr += "<table border='2' width='100%'
     * cellpadding='1'>"; outputStr += "<tr>"; outputStr += "<th>"; outputStr +=
     * "" + index + ""; outputStr += "</th>"; outputStr += "<td>"; outputStr +=
     * "<div align='center'>"; outputStr += "Freezer: "; outputStr += "<input
     * type='text' size='3' name='" + paramName + "_freezer_" + index + "'
     * onfocus=\"inform=true;\"
     * onblur=\"inform=false;parameterValidate(this.value,this.name,true,'number');\"/>";
     * outputStr += "<div id='" + paramName + "_freezer_" + index + "'></div>";
     * outputStr += "</div>"; outputStr += "</td>"; outputStr += "<td>";
     * outputStr += "<div align='center'>"; outputStr += "Shelf: "; outputStr +=
     * "<input type='text' size='3' name='" + paramName + "_shelf_" + index + "'
     * onfocus=\"inform=true;\"
     * onblur=\"inform=false;parameterValidate(this.value,this.name,true,'number');\"/>";
     * outputStr += "<div id='" + paramName + "_shelf_" + index + "'></div>";
     * outputStr += "</div>"; outputStr += "</td>"; outputStr += "<td>";
     * outputStr += "<div align='center'>"; outputStr += "Rack: "; outputStr +=
     * "<input type='text' size='3' name='" + paramName + "_rack_" + index + "'
     * onfocus=\"inform=true;\"
     * onblur=\"inform=false;parameterValidate(this.value,this.name,true,'number');\"/>";
     * outputStr += "<div id='" + paramName + "_rack_" + index + "'></div>";
     * outputStr += "</div>"; outputStr += "</td>"; outputStr += "<td>";
     * outputStr += "<div align='center'>"; outputStr += "Box: "; outputStr +=
     * "<input type='text' size='3' name='" + paramName + "_box_" + index + "'
     * onfocus=\"inform=true;\"
     * onblur=\"inform=false;parameterValidate(this.value,this.name,true,'number');\"/>";
     * outputStr += "<div id='" + paramName + "_box_" + index + "'></div>";
     * outputStr += "</div>"; outputStr += "</td>"; outputStr += "<td>";
     * outputStr += "<div align='center'>"; outputStr += "Position: "; outputStr
     * += "<input type='text' size='3' name='" + paramName + "_position_" +
     * index + "' onfocus=\"inform=true;\"
     * onblur=\"inform=false;parameterValidate(this.value,this.name,true,'number');\"/>";
     * outputStr += "<div id='" + paramName + "_position_" + index + "'></div>";
     * outputStr += "</div>"; outputStr += "</td>"; outputStr += "</tr>";
     *
     * //Adding occupancy validation line outputStr += "<tr>"; outputStr +=
     * "<td colspan='6'>"; outputStr += "<div id='" + paramName + "_occupancy_"
     * + index + "' class='errorLabel'>"; outputStr += ""; outputStr +=
     * "</div>"; outputStr += "</td>"; outputStr += "</tr>";
     *
     * outputStr += "</table>"; outputStr += "</div>"; return outputStr; }
     */

    private String getAssocStudyMenu(String study, String phaseVisit) {

        String menuStr = "";

        Vector<String> studyOptions = new Vector<String>();
        if (study.equals("PMT")) {
            studyOptions.add("Phase 1");
            studyOptions.add("Phase 2");
            studyOptions.add("Phase 3");
            studyOptions.add("Phase 4 - post-op");
            studyOptions.add("Phase 4 - follow-up");
        } else if (study.equals("FIRST-MAPPP")) {
            studyOptions.add("Visit 1");
            studyOptions.add("Visit 2");
            studyOptions.add("Visit 3");
            studyOptions.add("Visit 4");
            studyOptions.add("Visit 5");
            studyOptions.add("Visit 6");
            studyOptions.add("Visit 7");
            studyOptions.add("Visit 8");
        } else if (study.equals("German Cushing Registry")) {
            studyOptions.add("Visit 1");
            studyOptions.add("Visit 1.5");
            studyOptions.add("Visit 2");
            studyOptions.add("Visit 3");
            studyOptions.add("Visit 4");
            studyOptions.add("Visit 5");
            studyOptions.add("Visit 6");
        } else if (study.equals("German Conn Registry")) {
            studyOptions.add("Visit 0A");
            studyOptions.add("Visit 0B");
            studyOptions.add("Visit 1");
            studyOptions.add("Visit 1.5");
            studyOptions.add("Visit 2");
            studyOptions.add("Visit 3");
            studyOptions.add("Visit 4");
            studyOptions.add("Visit 5");
            studyOptions.add("Visit 6");
        }
        menuStr += "<select name='associated_study_phase_visit'>";
        for (int i = 0; i < studyOptions.size(); i++) {
            String studyOptionIn = studyOptions.get(i);
            menuStr += "<option ";
            if (studyOptionIn.equals(phaseVisit)) {
                menuStr += "selected";
            }
            menuStr += " value='" + studyOptionIn + "'>" + studyOptionIn + "</option>";
        }

        menuStr += "</select>";
        return menuStr;
    }

    private String getStudy(String centerid, String pid, String modid, String dbn, Connection conn) {

        String study = "";
        String bioTable = "" + dbn + "_Biomaterial";
        String bioTableId = "" + dbn.toLowerCase() + "_biomaterial_id";

        try {
            String sql = "SELECT associated_study FROM " + bioTable + " WHERE center_id=? AND ensat_id=? AND " + bioTableId + "=?;";
            PreparedStatement ps = conn.prepareStatement(sql);
            ps.setString(1, centerid);
            ps.setString(2, pid);
            ps.setString(3, modid);
            ResultSet rs = ps.executeQuery();
            if (rs.next()) {
                study = rs.getString(1);
            }
        } catch (Exception e) {
            logger.debug("Error: " + e.getMessage());
        }

        return study;
    }

    public String[] getTablenames(String dbn) {


        String[] tablenames = new String[0];
        if (dbn.equals("ACC")) {
            tablenames = new String[12];
            tablenames[0] = "Identification";
            tablenames[1] = "ACC_DiagnosticProcedures";
            tablenames[2] = "ACC_TumorStaging";
            tablenames[3] = "ACC_Biomaterial";
            tablenames[4] = "ACC_Surgery";
            tablenames[5] = "ACC_Pathology";
            tablenames[6] = "ACC_Mitotane";
            tablenames[7] = "ACC_Chemotherapy";
            tablenames[8] = "ACC_Radiofrequency";
            tablenames[9] = "ACC_Radiotherapy";
            tablenames[10] = "ACC_Chemoembolisation";
            tablenames[11] = "ACC_FollowUp";

        } else if (dbn.equals("Pheo")) {

            tablenames = new String[13];
            tablenames[0] = "Identification";
            tablenames[1] = "Pheo_PatientHistory";
            tablenames[5] = "Pheo_NonSurgicalInterventions";
            tablenames[3] = "Pheo_Surgery";
            tablenames[4] = "Pheo_ClinicalAssessment";
            tablenames[2] = "Pheo_FollowUp";
            tablenames[6] = "Pheo_TumorDetails";
            tablenames[7] = "Pheo_ImagingTests";
            tablenames[8] = "Pheo_BiochemicalAssessment";
            tablenames[9] = "Pheo_Biomaterial";
            
            tablenames[10] = "Pheo_Surgery_Procedures";
            tablenames[11] = "Pheo_Morphological_Progression";
            tablenames[12] = "Pheo_Biological_Assessment";

        } else if (dbn.equals("NAPACA")) {
            tablenames = new String[7];
            tablenames[0] = "Identification";
            tablenames[1] = "NAPACA_DiagnosticProcedures";
            tablenames[2] = "NAPACA_Surgery";
            tablenames[3] = "NAPACA_imaging";
            tablenames[4] = "NAPACA_Pathology";
            tablenames[5] = "NAPACA_FollowUp";
            tablenames[6] = "NAPACA_biomaterial";


        } else if (dbn.equals("APA")) {
            tablenames = new String[10];
            tablenames[0] = "Identification";
            tablenames[1] = "APA_PatientHistory";
            tablenames[2] = "APA_Biomaterial";
            tablenames[3] = "APA_ClinicalAssessment";
            tablenames[4] = "APA_Cardio";
            tablenames[5] = "APA_Complication";
            tablenames[6] = "APA_Imaging";
            tablenames[7] = "APA_Surgery";
            tablenames[8] = "APA_BiochemicalAssessment";
            tablenames[9] = "APA_FollowUp";

        }
        return tablenames;
    }
    
    
    public int getDateNum(Vector<Vector> parameters){
        
        int paramNum = parameters.size();
        int dateNum = 0;
        for(int i = 0; i <paramNum;i++){
            String paramType = parameters.elementAt(i).elementAt(2).toString();
            if(paramType.equals("date")){
                dateNum++;
            }
        }
        return dateNum;
    }
    
    public String getDataRowStr(Vector<Vector> parameters, int dateNum, String pid, String centerid, Connection conn, String detailSwitch){
        
        int paramNum = parameters.size();
        String dataRowStr = "data.addRows([";                    

        for(int i = 0; i <paramNum;i++){
   
            String paramValue = "";
            String paramLabel = parameters.elementAt(i).elementAt(4).toString();
            String paramName = parameters.elementAt(i).elementAt(1).toString();
            String paramType = parameters.elementAt(i).elementAt(2).toString();
            String paramTable = parameters.elementAt(i).elementAt(8).toString();
            
            //If it is a multiple table, find the modid nums and repeat through
            boolean multipleTable = false;
            if(!paramTable.equals("Identification")
                    && !paramTable.equals("ACC_DiagnosticProcedures")
                    && !paramTable.equals("ACC_TumorStaging")
                    && !paramTable.equals("Pheo_PatientHistory")
                    && !paramTable.equals("NAPACA_DiagnosticProcedures")
                    && !paramTable.equals("APA_PatientHistory")
                    ){
                multipleTable = true;
            }
            
            //Run this for the one-off tables
            if(!multipleTable){
                paramValue = parameters.elementAt(i).elementAt(10).toString();
                if(paramValue == null){
                    paramValue = "";
                }
                paramValue = paramValue.trim();        
            }
            
            //Run the string if it's a date only
            if (paramType.equals("date")) {     
                
                //Run this for one-off table parameters that are non-empty
                if(!multipleTable && !paramValue.equals("")){                
                    dataRowStr += this.getDataRowInd(paramValue, paramLabel);                    
                }
                
                //Run this for multiple table parameters
                if(multipleTable){             
                    
                    //For initiation parameters, find the corresponding end tags
                    if(paramName.contains("initiation")){
                        String paramPrefix = paramName.substring(0,paramName.indexOf("_"));
                        
                        //Get the corresponding end values
                        Vector<String> paramEndValues = this.getDataMultipleValues(paramTable, paramPrefix + "_end", pid, centerid, conn);
                        
                        //Render the parameter with the added end value
                        Vector<String> paramValues = this.getDataMultipleValues(paramTable, paramName, pid, centerid, conn);
                        int valueNum = paramValues.size();
                        for(int j=0; j<valueNum; j++){
                            String thisValue = paramValues.get(j);
                            String thisEndValue = "";
                            if(j < paramEndValues.size()){
                                thisEndValue = paramEndValues.get(j);
                            }
                            dataRowStr += this.getDataRowInd(thisValue, paramLabel, thisEndValue);
                        }
                    }else if(paramName.contains("end")){
                        //Don't render the parameter date if it is an end tag
                        dataRowStr += "";                        
                    }else{                    
                        //All the other values are rendered here
                        //Vector<Vector> paramValues = this.getDataMultipleValues(paramTable, paramName, pid, centerid, conn, true);
                        Vector<String> paramValues = this.getDataMultipleValues(paramTable, paramName, pid, centerid, conn);
                        int valueNum = paramValues.size();
                        for(int j=0; j<valueNum; j++){
                            /*Vector<String> thisValues = paramValues.get(j);
                            for(int k=0; k<thisValues.size(); k++){
                                String thisValue = thisValues.get(j);
                                dataRowStr += this.getDataRowInd(thisValue, paramLabel);
                            }*/
                            String thisValue = paramValues.get(j);
                            if(detailSwitch.equals("on")){
                                dataRowStr += this.getDataRowInd(thisValue, paramLabel, paramTable, paramName, pid, centerid, conn, true);
                            }else{
                                dataRowStr += this.getDataRowInd(thisValue, paramLabel, paramTable, paramName, pid, centerid, conn, false);
                            }
                                                           
                        }
                    }
                }                
            }
        }    
        
        //Trim the final comma
        if(!dataRowStr.equals("")){
            dataRowStr = dataRowStr.substring(0,dataRowStr.length()-1);
        }

        dataRowStr += "]);";
        return dataRowStr;        
    }
    
    private Vector<String> getDataMultipleValues(String paramTable, String paramName, String pid, String centerid, Connection conn){
        
        Vector<String> dataMultipleValues = new Vector<String>();
        String sql = "SELECT " + paramName + " FROM " + paramTable + " WHERE center_id=? AND ensat_id=?";
        
        try{
            PreparedStatement ps = conn.prepareStatement(sql);
            ps.setString(1, centerid);
            ps.setString(2, pid);
            
            ResultSet rs = ps.executeQuery();
            while(rs.next()){
                String valueIn = rs.getString(1);
                if(valueIn == null){
                    valueIn = "";
                }
                if(!valueIn.equals("")){
                    dataMultipleValues.add(valueIn);
                }
            }
        }catch(Exception e){
            logger.debug("Error (getDataMultipleValues): " + e.getMessage());
        }        
        return dataMultipleValues;
    }
    
    private Vector<Vector> getDataMultipleValues(String paramTable, String paramName, String pid, String centerid, Connection conn, boolean vectorFlag){
        
        Vector<Vector> dataMultipleValues = new Vector<Vector>();
        String sql = "SELECT * FROM " + paramTable + " WHERE center_id=? AND ensat_id=?";
        
        try{
            PreparedStatement ps = conn.prepareStatement(sql);
            ps.setString(1, centerid);
            ps.setString(2, pid);
            
            ResultSet rs = ps.executeQuery();
            ResultSetMetaData rsmd = rs.getMetaData();
            int colNum = rsmd.getColumnCount();            
            
            while(rs.next()){
                Vector<String> paramIn = new Vector<String>();
                for(int i=0; i<colNum; i++){
                    String valueIn = rs.getString(i+1);
                    if(valueIn == null){
                        valueIn = "";
                    }
                    if(!valueIn.equals("")){
                        paramIn.add(valueIn);
                    }
                }
                dataMultipleValues.add(paramIn);
            }
        }catch(Exception e){
            logger.debug("Error (getDataMultipleValues): " + e.getMessage());
        }        
        return dataMultipleValues;
    }
    
    private String getDataRowInd(String paramValue, String paramLabel){
        
        String dataRowOut = "";
        //This is the main run for most date parameters
        String dateYear = "";
        String dateMonth = "";
        String dateDay = "";
        int firstHyphen = paramValue.indexOf("-");
        int secondHyphen = paramValue.lastIndexOf("-");
        if(firstHyphen != -1 && secondHyphen != -1){
            if(firstHyphen != secondHyphen){
                dateYear = paramValue.substring(0,firstHyphen);
                dateMonth = paramValue.substring(firstHyphen+1,secondHyphen);
                dateDay = paramValue.substring(secondHyphen+1,paramValue.length());
            }
        }
        String dateValue = "Date(" + dateYear + "," + dateMonth + "," + dateDay + ")";
        
        String contentStr = paramLabel + " (<strong>" + paramValue + "</strong>)";
        dataRowOut += "[new " + dateValue + ", , '" + contentStr + "'],";
        return dataRowOut;            
    }
    
    
    
    private String getDataRowInd(String paramValue, String paramLabel, String paramTable, String paramName, String pid, String centerid, Connection conn, boolean detailOn){
        
        String dataRowOut = "";
        //This is the main run for most date parameters
        String dateYear = "";
        String dateMonth = "";
        String dateDay = "";
        int firstHyphen = paramValue.indexOf("-");
        int secondHyphen = paramValue.lastIndexOf("-");
        if(firstHyphen != -1 && secondHyphen != -1){
            if(firstHyphen != secondHyphen){
                dateYear = paramValue.substring(0,firstHyphen);
                dateMonth = paramValue.substring(firstHyphen+1,secondHyphen);
                dateDay = paramValue.substring(secondHyphen+1,paramValue.length());
            }
        }
        String dateValue = "Date(" + dateYear + "," + dateMonth + "," + dateDay + ")";
        
        String contentStr = this.getContentStr(paramLabel, paramValue, paramTable, paramName, pid, centerid, conn, detailOn);
        dataRowOut += "[new " + dateValue + ", , '" + contentStr + "'],";
        return dataRowOut;            
    }    
    
    private String getContentStr(String paramLabel, String paramValue, String paramTable, String paramName, String pid, String centerid, Connection conn, boolean detailOn){        
        String contentStr = "";        
        
        contentStr = paramLabel + " (<strong>" + paramValue + "</strong>)<br/>";

        if(detailOn){
            int underscoreIndex = paramTable.indexOf("_");
            String modality = "";
            String dbn = "";
            if(underscoreIndex != -1){
                modality = paramTable.substring(underscoreIndex+1,paramTable.length()).toLowerCase();
                dbn = paramTable.substring(0,underscoreIndex);
            }
            
            String sql = "SELECT * FROM " + paramTable + " WHERE center_id=? AND ensat_id=? AND " + paramName + "=?;";
            try{
                PreparedStatement ps = conn.prepareStatement(sql);
                ps.setString(1, centerid);
                ps.setString(2, pid);
                ps.setString(3, paramValue);
            
                ResultSet rs = ps.executeQuery();
                ResultSetMetaData rsmd = rs.getMetaData();
                int colNum = rsmd.getColumnCount();            
            
                if(rs.next()){                
                    for(int i=0; i<colNum; i++){
                        String valueIn = rs.getString(i+1);
                        String colName = rsmd.getColumnName(i+1);
                        if(valueIn == null){
                            valueIn = "";
                        }
                        if(colName == null){
                            colName = "";
                        }                    
                        if(!valueIn.equals("")){
                            if(i == 0){
                                contentStr += "<a href=\"./jsp/modality/read/detail.jsp?dbid=1&dbn=" + dbn + "&pid=" + pid + "&centerid=" + centerid + "&modality=" + modality + "&modid=" + valueIn + "\">";
                            }
                            contentStr += "<strong>" + colName + ":</strong> " + valueIn + "<br/>";
                            if(i == 0){
                                contentStr += "</a>";
                            }
                        }
                    }                
                }
            }catch(Exception e){
                logger.debug("Error (getDataMultipleValues): " + e.getMessage());
            }            
        }
        return contentStr;
    }
    
    
    private String getDataRowInd(String paramValue, String paramLabel, String thisEndValue){
        
            String paramPrefix = paramLabel.substring(0,paramLabel.indexOf(" "));
            String courseLabel = "" + paramPrefix + " treatment";
        
                //This is the main run for most date parameters
                String dateYear = "";
                String dateMonth = "";
                String dateDay = "";
                int firstHyphen = paramValue.indexOf("-");
                int secondHyphen = paramValue.lastIndexOf("-");
                if(firstHyphen != -1 && secondHyphen != -1){
                    if(firstHyphen != secondHyphen){
                        dateYear = paramValue.substring(0,firstHyphen);
                        dateMonth = paramValue.substring(firstHyphen+1,secondHyphen);
                        dateDay = paramValue.substring(secondHyphen+1,paramValue.length());
                    }
                }
                String dateValue = "Date(" + dateYear + "," + dateMonth + "," + dateDay + ")";
                
                if(!thisEndValue.equals("")){
                    String endDateYear = "";
                    String endDateMonth = "";
                    String endDateDay = "";
                    int firstEndHyphen = thisEndValue.indexOf("-");
                    int secondEndHyphen = thisEndValue.lastIndexOf("-");
                    if(firstEndHyphen != -1 && secondEndHyphen != -1){
                        if(firstEndHyphen != secondEndHyphen){
                            endDateYear = thisEndValue.substring(0,firstEndHyphen);
                            endDateMonth = thisEndValue.substring(firstEndHyphen+1,secondEndHyphen);
                            endDateDay = thisEndValue.substring(secondEndHyphen+1,thisEndValue.length());
                        }
                    }
                    String endDateValue = "Date(" + endDateYear + "," + endDateMonth + "," + endDateDay + ")";                                
                    return "[new " + dateValue + ", new " + endDateValue + ", '" + courseLabel + " (<strong>" + paramValue + " --&gt; " + thisEndValue + "</strong>)'],";            
                }else{
                    return "[new " + dateValue + ", , '" + paramLabel + " (<strong>" + paramValue + "</strong>)'],";
                }
    }
    
    public String[] getStudyAddresses(String[] studies, Connection conn){
        
        int studyNum = studies.length;
        Vector<String> studyAddresses = new Vector<String>();
        try{            
            for(int i=0; i<studyNum; i++){
                String sql = "SELECT study_addresses FROM Studies WHERE study_name=?;";
                PreparedStatement ps = conn.prepareStatement(sql);
                ps.setString(1,studies[i]);
                ResultSet rs = ps.executeQuery();
                if(rs.next()){
                    String addressesIn = rs.getString(1);
                    if(addressesIn == null){
                        addressesIn = "";
                    }
                    studyAddresses.add(addressesIn);
                }
            }
        }catch(Exception e){
            logger.debug("Error (getStudyAddresses): " + e.getMessage());
        }
        
        String[] studyAddressesArr = new String[studyNum];
        for(int i=0; i<studyNum; i++){
            studyAddressesArr[i] = studyAddresses.get(i);
        }
        return studyAddressesArr;        
    }
    
    public String[] getStudyLabels(String[] studies, Connection conn){
        int studyNum = studies.length;
        Vector<String> studyLabels = new Vector<String>();
        try{            
            for(int i=0; i<studyNum; i++){
                String sql = "SELECT study_label FROM Studies WHERE study_name=?;";
                PreparedStatement ps = conn.prepareStatement(sql);
                ps.setString(1,studies[i]);
                ResultSet rs = ps.executeQuery();
                if(rs.next()){
                    String labelIn = rs.getString(1);
                    if(labelIn == null){
                        labelIn = "";
                    }
                    studyLabels.add(labelIn);
                }
            }
        }catch(Exception e){
            logger.debug("Error (getStudyAddresses): " + e.getMessage());
        }
        
        String[] studyLabelsArr = new String[studyNum];
        for(int i=0; i<studyNum; i++){
            studyLabelsArr[i] = studyLabels.get(i);
        }
        return studyLabelsArr;                
    }
    
    
    public boolean getPatientInStudyList(Vector<Vector> parameters, String studyName){
        
        boolean inStudy = false;
        boolean paramFound = false;
        int paramCount = 0;
        while(!paramFound && paramCount < parameters.size()){
            
            Vector<String> paramIn = parameters.get(paramCount);
            String paramName = paramIn.get(1);
            if(paramName.equals("associated_studies")){
                paramFound = true;
                String valuesIn = "";
                if(paramIn.size() >= 11){
                    valuesIn = paramIn.get(10);
                    StringTokenizer st = new StringTokenizer(valuesIn,"|");
                    while(st.hasMoreTokens()){
                        String tokenIn = st.nextToken().trim();
                        if(tokenIn.equals(studyName)){
                            inStudy = true;
                        }
                    }
                }
            }else{
                paramCount++;
            }
        }
        return inStudy;
    }

}
