/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package create_main;

import ConnectBean.ConnectionAuxiliary;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.Statement;
import java.sql.ResultSet;
import java.sql.ResultSetMetaData;
import java.sql.PreparedStatement;

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
public class Create {

    private static final Logger logger = Logger.getLogger(Create.class);
    private String username = "";

    public Create() {
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

    private Vector<Vector> getMenuTypes(Connection conn) {

        //Do menu check here
        String menuCheckSql = "SELECT menu_id,menu_type FROM Menu";

        Vector<Vector> menuTypes = new Vector<Vector>();
        try {
            //PreparedStatement menuStmt = null;            
            PreparedStatement ps = conn.prepareStatement(menuCheckSql);
            //ResultSet menuCheckRs = stmt.executeQuery(menuCheckSql);
            ResultSet menuCheckRs = ps.executeQuery();

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

    public Vector<Vector> getParameters(String[] _tablenames, HttpServletRequest request, Connection paramConn) {

        String[] tablenames = _tablenames;
        String sql = "";
        sql = "SELECT * FROM Parameter WHERE param_table=?";

        if (tablenames.length > 1) {
            for (int i = 1; i < tablenames.length; i++) {
                sql += " OR param_table=?";
            }
        }
        sql += " ORDER BY param_order_id ASC;";

        Vector<Vector> parameters = new Vector<Vector>();
        try {
            //Get the menu types here (one call rather than for each parameter)
            Vector<Vector> menuTypes = this.getMenuTypes(paramConn);

            PreparedStatement ps = paramConn.prepareStatement(sql);
            ps.setString(1, tablenames[0]);
            if (tablenames.length > 1) {
                for (int i = 1; i < tablenames.length; i++) {
                    ps.setString(i + 1, tablenames[i]);
                }
            }
            ResultSet rs = ps.executeQuery();
            //Get the column number
            ResultSetMetaData rsmd = rs.getMetaData();
            int columnCount = rsmd.getColumnCount();

            rs = ps.executeQuery();
            int rowCount = 0;
            while (rs.next()) {
                Vector<String> rowIn = new Vector<String>();
                for (int i = 0; i < columnCount; i++) {
                    rowIn.add(rs.getString(i + 1));
                }
                //Now finally check for any values in the request object (for this parameter) and add it here
                rowIn = this.getIndividualValue(rowIn, request, rs, menuTypes);

                rowCount++;
                parameters.add(rowIn);
            }

            parameters = this.addCalcFieldValues(parameters, request, tablenames);

            //paramConn.close();

        } catch (Exception e) {
            logger.debug("('" + username + "')  Error (getParameters): " + e.getMessage());
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
                if (i == 0) {
                    if (tablenames[1] != null) {
                        if (tablenames[1].equals("ACC_DiagnosticProcedures")) {
                            rowIn.add(tablenames[1]);
                        }
                    }
                } else if (i == 1) {
                    if (tablenames[2] != null) {
                        if (tablenames[2].equals("ACC_TumorStaging") && i == 1) {
                            rowIn.add(tablenames[2]);
                        }
                    }
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
                
                if(rowIn.get(1).equals("associated_studies")){
                    menuType = "m";
                }

                //If single, then simple parameter grab, but not so simple if it's a multiple menu
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
            if (valueIn == null || valueIn.equals("null")) {
                valueIn = "";
            }
            //logger.debug("valueIn: " + valueIn);
            
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
            if (!menuIDIn.equals("0") && !menuIDs.contains(menuIDIn)) {
                menuIDs.add(menuIDIn);
            }
        }

        int menuIDnum = menuIDs.size();

        try {

            for (int i = 0; i < menuIDnum; i++) {

                Vector<String> menu = new Vector<String>();

                String sql = "SELECT * FROM Menu, MenuOption WHERE Menu.menu_id=MenuOption.option_menu_id AND Menu.menu_id=?";
                PreparedStatement ps = paramConn.prepareStatement(sql);
                ps.setString(1, menuIDs.get(i));
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

            //paramConn.close();

        } catch (Exception e) {
            logger.debug("('" + username + "') Error (getMenus): " + e.getMessage());
        }
        return menus;
    }

    public Vector<Vector> getDynamicMenus(Vector<Vector> parameters, Vector<Vector> menus, String userCenter, Connection ccConn, Connection conn, String pid, String centerid, String dbn) {

        Vector<String> menuIDs = new Vector<String>();
        int paramCount = parameters.size();
        
        for (int i = 0; i < paramCount; i++) {
            Vector<String> rowIn = parameters.get(i);
            String paramType = rowIn.get(2);
            if (paramType.equals("dynamicmenuonload")) {
                String paramName = rowIn.get(1);
                menuIDs.add(paramName);
            }
        }
        
        int menuIDnum = menuIDs.size();
        for (int i = 0; i < menuIDnum; i++) {
            
            Vector<String> dynamicMenu = new Vector<String>();
            
            if (menuIDs.get(i).equals("center_id")) {

                //Need to know the user country code (first two letters of their center code)        
                //Dissect the center code for the country code
                String centercode = userCenter;
                
                //Account for multiples here
                int centerNum = 1;
                String [] countrycode = null;
                if(centercode.indexOf("|") != -1){
                    StringTokenizer st = new StringTokenizer(centercode,"|");
                    centerNum = st.countTokens();
                    countrycode = new String[centerNum];
                    int codeCount = 0;
                    while(st.hasMoreTokens()){
                        countrycode[codeCount] = st.nextToken();
                        codeCount++;
                    }                    
                }else{
                    countrycode = new String[centerNum];
                    countrycode[0] = centercode;
                }
                
                //String countrycode = "";                                
                if (centercode != null && !centercode.equals("")) {
                    for(int j=0; j<centerNum; j++){
                        countrycode[j] = countrycode[j].substring(0, 2);
                    }                    
                }

                //Run query based on this                
                try {
                    String sql = "SELECT DISTINCT center_id FROM Center_Callout WHERE center_id LIKE ? ";                    
                    for(int j=1; j<centerNum; j++){
                        sql += "OR center_id LIKE ? ";
                    }                    
                    sql += ";";
                    
                    PreparedStatement ps = ccConn.prepareStatement(sql);
                    ps.setString(1, countrycode[0] + "%");                    
                    for(int j=1; j<centerNum; j++){
                        ps.setString((j+1), countrycode[j] + "%");
                    }
                    ResultSet rs = ps.executeQuery();
                    dynamicMenu.add("");
                    dynamicMenu.add("");
                    dynamicMenu.add("s");
                    while (rs.next()) {
                        dynamicMenu.add(rs.getString(1));
                    }
                    rs.close();
                    //ccConn.close();

                } catch (Exception e) {
                    logger.debug("('" + username + "') Error (getDynamicMenus): " + e.getMessage());
                }
            } else if (menuIDs.get(i).equals("pathology_derived_from")) {

                //Run query based on this                
                try {
                    String sql = "SELECT acc_surgery_id FROM ACC_Surgery WHERE center_id=? AND ensat_id=? ORDER BY acc_surgery_id;";
                    PreparedStatement ps = conn.prepareStatement(sql);
                    ps.setString(1, centerid);
                    ps.setString(2, pid);
                    ResultSet rs = ps.executeQuery();

                    dynamicMenu.add("");
                    dynamicMenu.add("");
                    dynamicMenu.add("s");
                    while (rs.next()) {
                        dynamicMenu.add("Surgery " + rs.getString(1));
                    }

                    //Add a final "no surgery" option
                    dynamicMenu.add("Biopsy");
                    rs.close();
                    //conn.close();

                } catch (Exception e) {
                    logger.debug("('" + username + "') Error (getDynamicMenus): " + e.getMessage());
                }
                //menus.add(dynamicMenu);
                //return menus;
            }else if(menuIDs.get(i).equals("associated_studies")){                
                    try {
                        String sql = "SELECT DISTINCT * FROM Studies,Study_Type WHERE Studies.study_id=Study_Type.study_id AND Study_Type.tumor_type=? ORDER BY study_label;";
                        PreparedStatement ps = conn.prepareStatement(sql);
                        ps.setString(1,dbn);
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
             }
            menus.add(dynamicMenu);
        }
        return menus;
    }

    protected Vector<Vector> checkHiddenParams(Vector<Vector> parameters, Vector<String> rowIn) {

        Vector<Vector> childParameters = new Vector<Vector>();
        int paramNum = parameters.size();

        //Make sure and change this for aliquots and freezer info too
        for (int i = 0; i < paramNum; i++) {
            Vector<String> rowCheck = parameters.get(i);
            if (rowCheck.get(7).equals(rowIn.get(0))) {
                childParameters.add(rowCheck);
            }
        }
        return childParameters;
    }

    protected Vector<Vector> checkParentParams(Vector<Vector> parameters, Vector<String> rowIn) {

        Vector<Vector> parentParameters = new Vector<Vector>();
        int paramNum = parameters.size();
        for (int i = 0; i < paramNum; i++) {
            Vector<String> rowCheck = parameters.get(i);
            if (rowCheck.get(7).equals(rowIn.get(0))) {
                parentParameters.add(rowCheck);
            }
        }
        return parentParameters;
    }

    public String getParameterHtml(Vector<Vector> parameters, Vector<Vector> menus, String lineColour, String dbn, Connection conn, String baseUrl) {

        String outputStr = "";

        //Get the number of rows here        
        int paramNum = parameters.size();

        //For each row
        for (int i = 0; i < paramNum; i++) {

            Vector<String> rowIn = parameters.get(i);
            String paramName = rowIn.get(1);
            String paramValue = rowIn.get(10);

            //Check if the parameter is a parent node
            boolean parentParam = false;
            Vector<Vector> childParameters = this.checkHiddenParams(parameters, rowIn);
            parentParam = !(childParameters.isEmpty());
            String parentJsStr = "";
            String parentHtmlStr = "";
            if (parentParam) {

                //Distinguish between normal sets of child parameters and those with multiple distinct options
                String childParameterHtml = this.getChildParameterHtml(parameters, childParameters, menus, lineColour, i, parentJsStr, parentHtmlStr, dbn, conn, baseUrl);
                parentJsStr = "showHide('myDiv_" + paramName + "_options',this.value);";
                
                String showHideFlag = "";
                if (paramValue.equals("Yes")) {
                    showHideFlag = "show";
                } else {
                    showHideFlag = "hide";
                }
                parentHtmlStr = "</td></tr><tr><td colspan='2'>" + "<div id=\"myDiv_" + paramName + "_options\" class=\"" + showHideFlag + "\">" + childParameterHtml + "</div>";
            }

            boolean subFlag = !rowIn.get(7).equals("0");
            boolean calledFromMain = true;
            outputStr += this.getIndividualParameterHtml(rowIn, menus, lineColour, i, parentJsStr, parentHtmlStr, subFlag, calledFromMain, dbn, conn, baseUrl);
        }
        return outputStr;
    }

    protected boolean getMultipleHiddenMenuType(String paramName) {
        return paramName.equals("surgery_first")
                || paramName.equals("surgery_extended")
                || paramName.equals("right_adrenal_max_tumor")
                || paramName.equals("left_adrenal_max_tumor")
                || paramName.equals("mitotane_best_objective")
                || paramName.equals("mitotane_best_objective_adj")
                || paramName.equals("max_tumor_by_ct_right")
                || paramName.equals("max_tumor_by_mr_right")
                || paramName.equals("max_tumor_by_ct_left")
                || paramName.equals("max_tumor_by_mr_left")
                
                /*|| paramName.equals("phpgl_free")
                || paramName.equals("disease_state")
                || paramName.equals("date_of_death")
                || paramName.equals("cause_of_death")*/;
    }

    private String getChildParameterHtml(Vector<Vector> parameters, Vector<Vector> childParameters, Vector<Vector> menus, String lineColour, int index, String parentJsStr, String parentHtmlStr, String dbn, Connection conn, String baseUrl) {

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
                String childChildParameterHtml = this.getChildParameterHtml(parameters, childChildParameters, menus, lineColour, i, parentJsStr, parentHtmlStr, dbn, conn, baseUrl);
                parentJsStr += "showHide('myDiv_" + paramName + "_options',this.value);";

                String showHideFlag = "";
                if (paramValue.equals("Yes")) {
                    showHideFlag = "show";
                } else {
                    showHideFlag = "hide";
                }
                parentHtmlStr = "</td></tr><tr><td colspan='2'>" + "<div id=\"myDiv_" + paramName + "_options\" class=\"" + showHideFlag + "\">" + childChildParameterHtml + "</div>";
            }
            //End of new stuff (recursive solution)

            outputStr += this.getIndividualParameterHtml(rowIn, menus, lineColour, i, parentJsStr, parentHtmlStr, subFlag, calledFromMain, dbn, conn, baseUrl);

        }
        return outputStr;
    }

    private String getIndividualParameterHtml(Vector<String> rowIn, Vector<Vector> menus, String lineColour, int index, String parentJsStr, String parentHtmlStr, boolean subFlag, boolean calledFromMain, String dbn, Connection conn, String baseUrl) {

        String paramName = rowIn.get(1);
        String paramOptional = rowIn.get(9);

        //Case for an empty value vector here
        String valueIn = rowIn.get(10);
        
        if(paramName.equals("androgens")
                || paramName.equals("estrogens")
                || paramName.equals("mineralocorticoids")
                || paramName.equals("precursor_secretion")){
            parentJsStr = "";
        }        

        //List the parameters that should not be rendered here
        boolean exception = false;
        exception = paramName.equals("local_investigator")
                || paramName.equals("investigator_email")
                || (paramName.equals("eurine_act_inclusion") && dbn.equals("Pheo"))
                || (paramName.equals("eurine_act_inclusion") && dbn.equals("APA"));

        if (!exception) {

            //Get the number of menus here
            int menuNum = menus.size();

            String outputStr = "";

            //If the parameter is a child, don't render it
            if (subFlag) {
                return outputStr;
            }

            //Run another encapsulating div here for those that have multiple selections based on input from parent
            if (this.getMultipleHiddenMenuType(paramName)) {
                outputStr += "<div id='" + paramName + "_mult' class='hide'>";
            }

            if (!calledFromMain) {
                outputStr += "<table width=\"100%\">";
            }

            
            if (paramName.equals("date_of_diagnosis")) {
                outputStr += "<tr><th colspan='2'>All data below is required to be at time of primary diagnosis</th></tr>";
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
                outputStr += "<input name=\"" + rowIn.get(1) + "\" type=\"text\" size=\"" + rowIn.get(3) + "\" value=\"" + valueIn + "\" onfocus=\"inform=true;\" onblur=\"parameterValidate(this.value,this.name," + paramOptional + ",'" + rowIn.get(2) + "','" + baseUrl + "');inform=false;\" onchange=\"" + parentJsStr + "\" /><div id=\"" + rowIn.get(1) + "\"></div>";
            } else if (rowIn.get(2).equals("date")) {

                //Change date format back to European
                valueIn = this.reformatDateValueEuropean(valueIn);

                outputStr += "<input name=\"" + paramName + "\" type=\"text\" class=\"datepicker\" id=\"" + paramName + "_id\" size=\"30\" value=\"" + valueIn + "\" onfocus=\"inform=true;\" onblur=\"inform=false;\" onchange=\"parameterValidate(this.value,this.name," + paramOptional + ",'" + rowIn.get(2) + "');" + parentJsStr + "\" /><div id=\"" + rowIn.get(1) + "\"></div>";

                //outputStr += "<input type=\"hidden\" id=\"format\" value=\"yy-mm-dd\" />";
                //outputStr += "Format options:<br /><select id=\"format\"><option value=\"mm/dd/yy\">Default - mm/dd/yy</option><option value=\"yy-mm-dd\">ISO 8601 - yy-mm-dd</option><option value=\"d M, y\">Short - d M, y</option><option value=\"d MM, y\">Medium - d MM, y</option><option value=\"DD, d MM, yy\">Full - DD, d MM, yy</option><option value=\"'day' d 'of' MM 'in the year' yy\">With text - 'day' d 'of' MM 'in the year' yy</option></select>";
                //Dissect up the valueIn for the date here
                /*
                 * String valueYear = ""; String valueMonth = ""; String
                 * valueDay = "";
                 *
                 * if (valueIn != null && !valueIn.trim().equals("")) {
                 * valueYear = valueIn.substring(0, valueIn.indexOf("-"));
                 * valueMonth = valueIn.substring(valueIn.indexOf("-") + 1,
                 * valueIn.lastIndexOf("-")); valueDay =
                 * valueIn.substring(valueIn.lastIndexOf("-") + 1,
                 * valueIn.length()); }
                 *
                 * outputStr += "<table width=\"100%\" cellpadding=\"5\">";
                 * outputStr += "<tr>"; outputStr += "<td>"; outputStr +=
                 * "Day:"; outputStr += "</td>"; outputStr += "<td>"; outputStr
                 * += "<input name=\"" + paramName + "_day\" type=\"text\"
                 * size=\"3\" value=\"" + valueDay + "\"
                 * onfocus=\"inform=true;\"
                 * onblur=\"parameterValidate(this.value,this.name," +
                 * paramOptional + ",'" + rowIn.get(2) +
                 * "');inform=false;\"/><div id=\"" + rowIn.get(1) +
                 * "_day\"></div>"; outputStr += "</td>"; outputStr += "<td>";
                 * outputStr += "Month:"; outputStr += "</td>"; outputStr +=
                 * "<td>"; outputStr += "<select name=\"" + paramName +
                 * "_month\" onblur=\"parameterValidate(this.value,this.name," +
                 * paramOptional + ",'" + rowIn.get(2) + "');\">" + "<option
                 * value=\"\">[Select...]</option>"; outputStr += "<option"; if
                 * (valueMonth.equals("01")) { outputStr += " selected "; }
                 * outputStr += " value=\"01\">Jan</option>"; outputStr +=
                 * "<option"; if (valueMonth.equals("02")) { outputStr += "
                 * selected "; } outputStr += " value=\"02\">Feb</option>";
                 * outputStr += "<option"; if (valueMonth.equals("03")) {
                 * outputStr += " selected "; } outputStr += "
                 * value=\"03\">Mar</option>"; outputStr += "<option"; if
                 * (valueMonth.equals("04")) { outputStr += " selected "; }
                 * outputStr += " value=\"04\">Apr</option>"; outputStr +=
                 * "<option"; if (valueMonth.equals("05")) { outputStr += "
                 * selected "; } outputStr += " value=\"05\">May</option>";
                 * outputStr += "<option"; if (valueMonth.equals("06")) {
                 * outputStr += " selected "; } outputStr += "
                 * value=\"06\">Jun</option>"; outputStr += "<option"; if
                 * (valueMonth.equals("07")) { outputStr += " selected "; }
                 * outputStr += " value=\"07\">Jul</option>"; outputStr +=
                 * "<option"; if (valueMonth.equals("08")) { outputStr += "
                 * selected "; } outputStr += " value=\"08\">Aug</option>";
                 * outputStr += "<option"; if (valueMonth.equals("09")) {
                 * outputStr += " selected "; } outputStr += "
                 * value=\"09\">Sep</option>"; outputStr += "<option"; if
                 * (valueMonth.equals("10")) { outputStr += " selected "; }
                 * outputStr += " value=\"10\">Oct</option>"; outputStr +=
                 * "<option"; if (valueMonth.equals("11")) { outputStr += "
                 * selected "; } outputStr += " value=\"11\">Nov</option>";
                 * outputStr += "<option"; if (valueMonth.equals("12")) {
                 * outputStr += " selected "; } outputStr += "
                 * value=\"12\">Dec</option>";
                 *
                 * outputStr += "</select><div id=\"" + rowIn.get(1) +
                 * "_month\"></div>"; outputStr += "</td>"; outputStr += "<td>";
                 * outputStr += "Year:"; outputStr += "</td>"; outputStr +=
                 * "<td>"; outputStr += "<input name=\"" + paramName + "_year\"
                 * type=\"text\" size=\"4\" value=\"" + valueYear + "\"
                 * onfocus=\"inform=true;\"
                 * onblur=\"parameterValidate(this.value,this.name," +
                 * paramOptional + ",'" + rowIn.get(2) +
                 * "');inform=false;\"/><div id=\"" + rowIn.get(1) +
                 * "_year\"></div>"; outputStr += "</td>"; outputStr += "</tr>";
                 * outputStr += "</table>";
                 */
            } else if (rowIn.get(2).equals("menu") || rowIn.get(2).equals("dynamicmenuonload")) {

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

                //If the menu referred to is a dynamic one, then it shouldn't be found in the clause above
                //Run the search based on the parameter name
                //If parameter name = 'center_id', grab the last one on the vector list
                if (!menuFound) {
                    if (rowIn.get(1).equals("center_id")) {
                        if(!username.equals("isabel")){
                            parentJsStr = "getCenterInfo('" + baseUrl + "',this.value);";
                            int lastMenuNum = menuNum - 2;
                            menuIn = menus.get(lastMenuNum);                        
                        }else{
                            menuIn = new Vector<String>();
                            menuIn.add("");
                            menuIn.add("");
                            menuIn.add("");
                            menuIn.add("PTCO");
                        }
                        
                        
                    }else if(rowIn.get(1).equals("associated_studies")){
                        int lastMenuNum = menuNum - 1;
                        menuIn = menus.get(lastMenuNum);                        
                    }
                    
                    /* else if (rowIn.get(1).equals("center_id")) {
                        int lastMenuNum = menuNum - 1;
                        menuIn = menus.get(lastMenuNum);
                    }*/
                }



                String menuSelectStr = "";
                String menuHeaderStr = "";
                if (menuIn.get(2).equals("m")) {

                    /*menuHeaderStr += "<select multiple name=\"" + paramName + "\" onblur=\"parameterValidate(this.value,this.name," + paramOptional + ",'" + rowIn.get(2) + "');" + parentJsStr + "\">";

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
                        menuSelectStr += "<option";

                        for (int m = 0; m < tokenNum; m++) {
                            if (valuesIn[m].equals(menuIn.get(k))) {
                                menuSelectStr += " selected ";
                            }
                        }
                        menuSelectStr += " value=\"" + menuIn.get(k) + "\">" + menuIn.get(k) + "</option>";
                    }*/
                    
                    //menuHeaderStr += "<select multiple  onblur=\"parameterValidate(this.value,this.name," + paramOptional + ",'" + rowIn.get(2) + "');" + parentJsStr + "\">";
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
                        menuSelectStr += "<input name=\"" + paramName + "\" type=\"checkbox\" ";

                        for (int m = 0; m < tokenNum; m++) {
                            if (valuesIn[m].equals(menuIn.get(k))) {
                                menuSelectStr += " checked ";
                            }
                        }
                        
                        if(paramName.equals("associated_studies")){
                            menuSelectStr += " value=\"" + this.getStudyName(menuIn.get(k)) + "\" ";
                            menuSelectStr += "onclick='showAssocStudyIDs(this.value);'";
                        }else{
                            menuSelectStr += " value=\"" + menuIn.get(k) + "\" ";
                        }                        
                        
                        menuSelectStr += " />" + menuIn.get(k) + "<br/>";
                    }                   
                    
                    
                } else {

                    menuHeaderStr += "<select name=\"" + paramName + "\" onblur=\"parameterValidate(this.value,this.name," + paramOptional + ",'" + rowIn.get(2) + "');\" onchange=\"" + parentJsStr + "\">";


                    menuSelectStr += "<option value=\"\">[Select...]</option>";
                    int menuSize = menuIn.size();

                    for (int k = 3; k < menuSize; k++) {
                        menuSelectStr += "<option";
                        if (valueIn.equals(menuIn.get(k))) {
                            menuSelectStr += " selected ";
                        }
                        menuSelectStr += " value=\"" + menuIn.get(k) + "\">" + menuIn.get(k) + "</option>";
                    }

                }

                outputStr += menuHeaderStr;
                outputStr += menuSelectStr;

                if (menuIn.get(2).equals("m")) {
                    outputStr += "</div>";
                }else{
                    outputStr += "</select>";
                }                
                outputStr += "<div id=\"" + paramName + "\"></div>";

                if (rowIn.get(1).equals("center_id")) {
                    outputStr += "</td></tr><tr><td colspan='2'><div id=\"center_info\">";

                    //Use the username - BIG HACK, needs generalized
                    if(username.equals("isabel")){
                        outputStr += "<table width='100%' cellpadding='5'><tr><td width='50%'>Referral doctor:</td><td><strong>Isabel Paiva</strong><input type='hidden' name='local_investigator' value='Isabel Paiva'/></td></tr>";
                        outputStr += "<tr><td width='50%'>Email:</td><td><strong>ipaiva@netcabo.pt</strong><input type='hidden' name='investigator_email' value='ipaiva@netcabo.pt'/></td></tr></table>";                        
                    }
                    outputStr += "</div>";
                    
                    
                } else if (rowIn.get(1).equals("associated_study")) {
                    outputStr += "</td></tr><tr><td colspan='2'><div id=\"associated_study\"></div>";
                }

                outputStr += parentHtmlStr;

            } else if (rowIn.get(2).equals("text_only")) {
                //outputStr += "<div id='" + rowIn.get(1) + "_options' class='hide'></div>";
                outputStr += "" + valueIn + "<input type='hidden' name='" + paramName + "' value='" + valueIn + "'/>";
            }

            //Add the separate (hidden) IDs for different studies
            if(paramName.equals("associated_studies")){
                
                outputStr += "<table width='100%'>";
                try{
                    String studyIdSql = "SELECT Studies.study_name,Studies.study_label FROM Studies,Study_Type WHERE Studies.study_id=Study_Type.study_id AND Study_Type.tumor_type=? AND separate_id='true';";
                    PreparedStatement ps = conn.prepareStatement(studyIdSql);
                    ps.setString(1,dbn);
                    ResultSet rs = ps.executeQuery();
                
                    while(rs.next()){
                        outputStr += "<tr><td>";                        
                        String studyName = rs.getString(1);
                        String studyLabel = rs.getString(2);
                        outputStr += "<div class='hide' id='" + studyName + "_id_option'>";
                        outputStr += "" + studyLabel + " ID: ";
                        outputStr += "<input type='text' size='6' name='" + studyName + "_id' value='' onfocus=\"inform=true;\" onblur=\"inform=false;\"/>";
                        outputStr += "</div>";                        
                        outputStr += "</td></tr>";                        
                    }                                       
                    
                }catch(Exception e){
                    logger.debug("Error: " + e.getMessage());
                }   
                outputStr += "</table>";                
            }

            outputStr += "</td>";
            outputStr += "</tr>";


            if (!calledFromMain) {
                outputStr += "</table>";
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

    public String getLastPageParamHtml(Vector<Vector> parameters, String dbn, HttpServletRequest request) {

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

        outputStr += this.getHiddenParams(lastPageParamNames, lastPageParamValues, dbn, request);

        outputStr += "</td>";
        outputStr += "</tr>";
        outputStr += "</table>";
        return outputStr;
    }

    public String getHiddenParams(Vector<String> lastPageParamNames, Vector<String> lastPageParamValues, String dbn, HttpServletRequest request) {

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
                for(int j=0; j<otherStudyNum; j++){
                    String otherStudyParamName = "" + otherStudyIDs[j] + "_id";                        
                    String otherStudyParamValue = request.getParameter(otherStudyParamName);
                    if(otherStudyParamValue != null){
                        outputStr += "<input type=\"hidden\" name=\"" + otherStudyParamName + "\" value=\"" + otherStudyParamValue + "\"/>";
                    }                        
                }                    
            }else{
                
                if(lastPageParamValues.get(i).equals("NLDAN")){
                    String networkCenterName = request.getParameter("network_center_name");                    
                    outputStr += "<input type=\"hidden\" name=\"" + lastPageParamNames.get(i) + "\" value=\"" + lastPageParamValues.get(i) + "|" + networkCenterName + "\"/>";
                }else{
                    outputStr += "<input type=\"hidden\" name=\"" + lastPageParamNames.get(i) + "\" value=\"" + lastPageParamValues.get(i) + "\"/>";
                }
            }
        }
        return outputStr;
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
                /*|| paramName.equals("normal_tissue")
                || paramName.equals("normal_tissue_paraffin")
                || paramName.equals("normal_tissue_dna")*/;
        return isAliquot;
    }
    
    protected boolean getNormalTissueParameter(String paramName) {

        boolean isNormalTissue =
                paramName.equals("normal_tissue_options")
                || paramName.equals("normal_tissue_paraffin_options")
                || paramName.equals("normal_tissue_dna_options");
        return isNormalTissue;
    }

    public String getLastPageParamConfirmHtml(Vector<Vector> parameters, String lineColour, HttpServletRequest request, String dbn) {

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
                    || lastPageParamNames.get(i).equals("first_diagnosis_tnm")
                    || lastPageParamNames.get(i).equals("malignant_diagnosis_tnm")
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

            //Display the value here
            outputStr += "<tr ";
            if (i % 2 != 0) {
                outputStr += lineColour;
            }
            outputStr += ">";
            outputStr += "<td width=\"50%\">" + lastPageParamLabels.get(i) + ":</td>";
            outputStr += "<td><strong>";
            if (parameterIsMultiple) {
                
                if(lastPageParamNames.get(i).equals("associated_studies")){                    
                    int tokenNum = Integer.parseInt(request.getParameter("associated_studies_num"));                    
                    for(int j=0; j<tokenNum; j++){
                        String tokenValueIn = request.getParameter("associated_studies_" + (j+1));
                        String studyValueIn = this.getStudyLabel(tokenValueIn);
                        outputStr += "" + studyValueIn + "";
                        String otherStudyParamName = "" + tokenValueIn + "_id";
                        String otherStudyParamValue = request.getParameter(otherStudyParamName);
                        if(otherStudyParamValue != null){
                            outputStr += " (" + otherStudyParamValue + ")";
                        }                        
                        outputStr += "<br/>";
                    }
                }else{
                    StringTokenizer st = new StringTokenizer(lastPageParamValues.get(i), "|");
                    while (st.hasMoreTokens()) {
                        String tokenValueIn = st.nextToken();
                        if (tokenValueIn.equals("EDP")) {
                            String[] tokenValuesIn = {"Cisplatin (P)", "Doxorubicin", "Etoposide"};
                            for (int m = 0; m < 3; m++) {
                                outputStr += "" + tokenValuesIn[m] + "<br/>";
                            }
                        }else if(tokenValueIn.equals("Others")){                            
                            String otherValueIn = request.getParameter("" + lastPageParamValues.get(i) + "_others");
                            outputStr += "" + otherValueIn + "<br/>";                            
                        } else {
                            outputStr += "" + tokenValueIn + "<br/>";
                        }
                    }
                }
            } else if (parameterIsAldoConversion) {
                //Retrieve the units from the request object
                String aldoUnits = request.getParameter("aldosterone_units");

                if (aldoUnits.equals("ngL") || aldoUnits.equals("")) {
                    outputStr += "" + lastPageParamValues.get(i) + "";
                    if(!lastPageParamValues.get(i).equals("")){
                        outputStr += " (ng/L)";
                    }
                    outputStr += "<input type=\"hidden\" name=\"" + lastPageParamNames.get(i) + "\" value=\"" + lastPageParamValues.get(i) + "\"/>";
                } else {
                    outputStr += "" + this.convertAldo(lastPageParamValues.get(i)) + "";
                    if(!lastPageParamValues.get(i).equals("")){
                        outputStr += " (ng/L)<br/>";
                    }
                    outputStr += "" + lastPageParamValues.get(i) + "";
                    if(!lastPageParamValues.get(i).equals("")){
                        outputStr += " (pmol/L)";
                    }
                    outputStr += "<input type=\"hidden\" name=\"" + lastPageParamNames.get(i) + "\" value=\"" + this.convertAldo(lastPageParamValues.get(i)) + "\"/>";
                }

            } else if (parameterIsCortisolConversion) {
                //Retrieve the units from the request object
                String cortUnits = request.getParameter("cortisol_units");

                if (cortUnits.equals("ugL") || cortUnits.equals("")) {
                    outputStr += "" + lastPageParamValues.get(i) + "";
                    if(!lastPageParamValues.get(i).equals("")){
                        outputStr += " (&micro;g/L)";
                    }                    
                    outputStr += "<input type=\"hidden\" name=\"" + lastPageParamNames.get(i) + "\" value=\"" + lastPageParamValues.get(i) + "\"/>";
                } else {
                    outputStr += "" + this.convertCortisol(lastPageParamValues.get(i)) + "";
                    if(!lastPageParamValues.get(i).equals("")){
                        outputStr += " (&micro;g/L)<br/>";
                    }
                    outputStr += "" + lastPageParamValues.get(i) + "";
                    if(!lastPageParamValues.get(i).equals("")){
                        outputStr += " (nmol/L)";
                    }
                    outputStr += "<input type=\"hidden\" name=\"" + lastPageParamNames.get(i) + "\" value=\"" + this.convertCortisol(lastPageParamValues.get(i)) + "\"/>";
                }

            } else if (parameterIsPlasmaConversion) {
                //Retrieve the units from the request object
                String plasmaUnits = request.getParameter("plasma_units");
                
                if (plasmaUnits.equals("ngL") || plasmaUnits.equals("")) {
                    outputStr += "" + lastPageParamValues.get(i) + "";
                    if(!lastPageParamValues.get(i).equals("")){
                        outputStr += " (ng/L)";
                    }                    
                    outputStr += "<input type=\"hidden\" name=\"" + lastPageParamNames.get(i) + "\" value=\"" + lastPageParamValues.get(i) + "\"/>";
                } else {
                    outputStr += "" + this.convertPlasmaUrine(lastPageParamValues.get(i),lastPageParamNames.get(i),plasmaUnits) + "";
                    if(!lastPageParamValues.get(i).equals("")){
                        outputStr += " (ng/L)<br/>";
                    }
                    outputStr += "" + lastPageParamValues.get(i) + "";
                    if(!lastPageParamValues.get(i).equals("")){
                        outputStr += " (nmol/L)";
                    }
                    outputStr += "<input type=\"hidden\" name=\"" + lastPageParamNames.get(i) + "\" value=\"" + this.convertPlasmaUrine(lastPageParamValues.get(i),lastPageParamNames.get(i),plasmaUnits) + "\"/>";
                }                
            } else if (parameterIsUrineConversion) {
                //Retrieve the units from the request object
                String urineUnits = request.getParameter("urinary_units");
                
                if (urineUnits.equals("mgday") || urineUnits.equals("")) {
                    outputStr += "" + lastPageParamValues.get(i) + "";
                    if(!lastPageParamValues.get(i).equals("")){
                        outputStr += " (mg/day)";
                    }                    
                    outputStr += "<input type=\"hidden\" name=\"" + lastPageParamNames.get(i) + "\" value=\"" + lastPageParamValues.get(i) + "\"/>";
                } else {
                    outputStr += "" + this.convertPlasmaUrine(lastPageParamValues.get(i),lastPageParamNames.get(i),urineUnits) + "";
                    if(!lastPageParamValues.get(i).equals("")){
                        outputStr += " (mg/day)<br/>";
                    }
                    outputStr += "" + lastPageParamValues.get(i) + "";
                    if(!lastPageParamValues.get(i).equals("")){
                        outputStr += " (&micro;mol/day)";
                    }
                    outputStr += "<input type=\"hidden\" name=\"" + lastPageParamNames.get(i) + "\" value=\"" + this.convertPlasmaUrine(lastPageParamValues.get(i),lastPageParamNames.get(i),urineUnits) + "\"/>";                
                }               
            } else {
                //if(lastPageParamValues.get(i).equals("NLDAN")){
                if(lastPageParamValues.get(i).startsWith("NLDAN")){
                    //String networkCenterName = request.getParameter("network_center_name");
                    String networkCenterValueIn = lastPageParamValues.get(i);
                    String networkCenterName = networkCenterValueIn.substring(networkCenterValueIn.indexOf("|")+1,networkCenterValueIn.length());
                    if(networkCenterName == null){
                        networkCenterName = "";
                    }                    
                    outputStr += "NLDAN (" + networkCenterName + ")";
                }else{
                    outputStr += "" + lastPageParamValues.get(i) + "";
                }
                
            }
            outputStr += "</strong></td>";

            //Now add the biomaterial aliquot information     
            //Also use this condition to retrieve the freezer information
            if (lastPageParamTables.get(i).contains("Biomaterial")) {
                outputStr += "<td>";
                if (parameterHasAliquot
                        && lastPageParamValues.get(i).equals("Yes")) {
                    String aliquotValue = request.getParameter("aliquot_" + lastPageParamNames.get(i));
                    outputStr += aliquotValue;
                }
                outputStr += "</td>";
                
                //Adding the freezer information
                outputStr += "</tr>";
                outputStr += "<tr>";
                outputStr += "<td colspan='3'>";                
                if (parameterHasAliquot && lastPageParamValues.get(i).equals("Yes")) {                                        
                    outputStr += this.getFreezerConfirmHtml(request, lastPageParamNames, i);                    
                }else if(this.getNormalTissueParameter(lastPageParamNames.get(i)) && !lastPageParamValues.get(i).equals("")){
                    
                    //Chop up the values string into the component parts
                    StringTokenizer st = new StringTokenizer(lastPageParamValues.get(i),"|");
                    
                    //Render these individually in the box (label + aliquot value)
                    //Put the aliquot number next to each
                    while(st.hasMoreTokens()){
                        String labelIn = st.nextToken();                                                                        
                        outputStr += this.getFreezerConfirmHtml(request, lastPageParamNames, i, labelIn);                        
                    }                 
                }
                outputStr += "</td>";
                
                
            }

            //Now add the hidden parameter containing the values here
            if (parameterIsMultiple) {
                
                if(lastPageParamNames.get(i).equals("associated_studies")){                    
                    int tokenNum = Integer.parseInt(request.getParameter("associated_studies_num"));
                    for(int j=0; j<tokenNum; j++){
                        String tokenValueIn = request.getParameter("associated_studies_" + (j+1));
                        outputStr += "<input type=\"hidden\" name=\"associated_studies_" + (j+1) + "\" value=\"" + tokenValueIn + "\"/>";
                    }
                    outputStr += "<input type=\"hidden\" name=\"associated_studies_num\" value=\"" + tokenNum + "\"/>";
                    
                    //Add the capture for the incidental IDs here
                    String[] otherStudyIDs = {"adiuvo",
                                                "adiuvo_observational",
                                                "lysosafe",
                                                "firstmappp",
                                                "german_cushing",
                                                "german_conn"};
                    int otherStudyNum = otherStudyIDs.length;                    
                    for(int j=0; j<otherStudyNum; j++){
                        String otherStudyParamName = "" + otherStudyIDs[j] + "_id";
                        String otherStudyParamValue = request.getParameter(otherStudyParamName);
                        if(otherStudyParamValue != null){
                            outputStr += "<input type=\"hidden\" name=\"" + otherStudyParamName + "\" value=\"" + otherStudyParamValue + "\"/>";
                        }                        
                    }                    
                }else{
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
                            
                        }else {
                            outputStr += "<input type=\"hidden\" name=\"" + multipleParamName + "_" + tokenCount + "\" value=\"" + tokenValueIn + "\"/>";
                            tokenCount++;
                        }
                    }
                    if (cdeFlag) {
                        tokenNum = tokenNum + 2;
                    }
                    outputStr += "<input type=\"hidden\" name=\"" + multipleParamName + "_num\" value=\"" + tokenNum + "\"/>";
                }
            } else {
                //logger.debug("lastPageParamValues.get(" + i + "): " + lastPageParamValues.get(i));
                //if(lastPageParamValues.get(i).equals("NLDAN")){
                if(lastPageParamValues.get(i).startsWith("NLDAN")){
                    //String networkCenterName = request.getParameter("network_center_name");
                    String networkCenterValueIn = lastPageParamValues.get(i);
                    String networkCenterName = networkCenterValueIn.substring(networkCenterValueIn.indexOf("|")+1,networkCenterValueIn.length());                    
                    //logger.debug("networkCenterName: " + networkCenterName);
                    if(networkCenterName == null){
                        networkCenterName = "";
                    }                    
                    outputStr += "<input type=\"hidden\" name=\"" + lastPageParamNames.get(i) + "\" value=\"NLDAN|" + networkCenterName + "\"/>";                    
                }else{
                    outputStr += "<input type=\"hidden\" name=\"" + lastPageParamNames.get(i) + "\" value=\"" + lastPageParamValues.get(i) + "\"/>";
                }
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
            boolean calcFieldPresent = lastPageParamNames.get(i).equals("weight")
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

    private String convertAldo(String valueIn) {
        
        if(valueIn == null){
            valueIn = "";
        }

        if (!valueIn.equals("0") && !valueIn.equals("")) {
            double valueDouble = Double.parseDouble(valueIn);
            String valueOut = "" + (valueDouble / 2.77);
            
            //Truncate to three decimal places
            int pointIndex = valueOut.indexOf(".");            
            if(pointIndex != -1 && (valueOut.length() - pointIndex > 3)){
                valueOut = valueOut.substring(0,pointIndex+3);
            }            
            return "" + valueOut;
        } else {
            return valueIn;
        }
    }

    private String convertCortisol(String valueIn) {
        
        if(valueIn == null){
            valueIn = "";
        }
        
        if (!valueIn.equals("0") && !valueIn.equals("")) {
            double valueDouble = Double.parseDouble(valueIn);
            String valueOut = "" + (valueDouble / 2.76);
            
            //Truncate to three decimal places
            int pointIndex = valueOut.indexOf(".");            
            if(pointIndex != -1 && (valueOut.length() - pointIndex > 4)){
                valueOut = valueOut.substring(0,pointIndex+4);
            }
            return "" + valueOut;
        } else {
            return valueIn;
        }
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

            //Default parameter is ensat_classification
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

                //Replace those nasty European commas with decimal points
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

    public int getNextId(String centerId, Connection ccConn) {

        int nextId = 1;
        String idQuerySql = "SELECT ensat_id FROM Center_Callout WHERE center_id=?";
        
        try {
            PreparedStatement ps = ccConn.prepareStatement(idQuerySql);
            ps.setString(1, centerId);
            ResultSet rs = ps.executeQuery();

            //ResultSet rs = stmt.executeQuery(idQuerySql);
            if (rs.next()) {
                String idResult = rs.getString(1);
                int idResultNum = Integer.parseInt(idResult);
                nextId = idResultNum + 1;
            }
            rs.close();
            //stmt.close();

            //ccConn.close();

        } catch (Exception e) {
            logger.debug("('" + username + "') Error (getNextId): " + e.getMessage());
        }
        return nextId;
    }

    public void setNextId(String centerId, int nextId, Connection ccConn) {

        try {
            String sql = "UPDATE Center_Callout SET ensat_id=? WHERE center_id=?;";

            PreparedStatement ps = ccConn.prepareStatement(sql);
            ps.setInt(1, nextId);
            ps.setString(2, centerId);
            int updateId = ps.executeUpdate();
            //ccConn.close();

        } catch (Exception e) {
            logger.debug("('" + username + "') Error (setNextId): " + e.getMessage());
        }
    }

    //public String executeParameterUpdate(String username, String dbn, int nextId, String centerId, String tablename, Vector<Vector> parameters, Statement statement, HttpServletRequest request) {
    public String executeParameterUpdate(String username, String dbn, int nextId, String centerId, String tablename, Vector<Vector> parameters, Connection conn, HttpServletRequest request) {

        //Split up the network value (if required)
        String networkCenterName = "";
        if(centerId.startsWith("NLDAN")){            
            int pipeIndex = centerId.indexOf("|");
            networkCenterName = centerId.substring(pipeIndex+1,centerId.length());
            centerId = "NLDAN";
        }
        
        
        Vector<String> lastPageParamNames = new Vector<String>();
        Vector<String> lastPageParamValues = new Vector<String>();
        Vector<String> lastPageParamTypes = new Vector<String>();

        int paramSize = parameters.size();

        //Check if the table is a multiple parameter one
        boolean multipleSelectTable =
                tablename.equals("Pheo_FirstDiagnosisPresentation")
                || tablename.equals("Pheo_OtherOrgans")                
                || tablename.equals("Pheo_HormoneSymptoms")
                || tablename.equals("Pheo_TumorSymptoms")
                || tablename.equals("Pheo_FirstDiagnosisTNM")
                || tablename.equals("Pheo_MalignantDiagnosisTNM")                
                || tablename.equals("ACC_Imaging")
                || tablename.equals("Associated_Studies");

        if (multipleSelectTable) {
            //Get the parameters from the multiple select parameters
            String paramLabel = "";
            if (tablename.equals("Pheo_FirstDiagnosisPresentation")) {
                paramLabel = "presentation_first_tumor";
            } else if (tablename.equals("Pheo_OtherOrgans")) {
                paramLabel = "system_organ";
            } else if (tablename.equals("Pheo_HormoneSymptoms")) {
                paramLabel = "hormone_symptoms";
            } else if (tablename.equals("Pheo_TumorSymptoms")) {
                paramLabel = "tumor_symptoms";
            } else if (tablename.equals("Pheo_FirstDiagnosisTNM")) {
                paramLabel = "first_diagnosis_tnm";
            } else if (tablename.equals("Pheo_MalignantDiagnosisTNM")) {
                paramLabel = "malignant_diagnosis_tnm";
            } else if (tablename.equals("ACC_Imaging")) {
                paramLabel = "imaging";
            } else if (tablename.equals("Associated_Studies")) {
                paramLabel = "associated_studies";
            }
            String paramValueNumInStr = request.getParameter(paramLabel + "_num");
            int paramValueNumIn = Integer.parseInt(paramValueNumInStr);
            for (int i = 0; i < paramValueNumIn; i++) {
                String valueIn = request.getParameter(paramLabel + "_" + (i + 1));
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
            //logger.debug("otherStudyNum: " + otherStudyNum);            
            
            for(int j=0; j<otherStudyNum; j++){
                String otherStudyParamName = "" + otherStudyIDs[j] + "_id";                        
                String otherStudyParamValue = request.getParameter(otherStudyParamName);
                //logger.debug("otherStudyParamName (" + j + "): " + otherStudyParamName);
                //logger.debug("otherStudyParamValue (" + j + "): " + otherStudyParamValue);
                if(otherStudyParamValue != null){
                    otherStudyIDNames.add(otherStudyParamName);
                    otherStudyIDValues.add(otherStudyParamValue);                    
                }                        
            }                                

            //Run an check for the last ID if the table is multiple
            int multipleNextId = 1;
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

            String nextIdCheck = "SELECT " + idLabel + " FROM " + tablename + " ORDER BY " + idLabel + " DESC";
            try {
                PreparedStatement ps = conn.prepareStatement(nextIdCheck);
                ResultSet idCheckRs = ps.executeQuery();

                //ResultSet idCheckRs = statement.executeQuery(nextIdCheck);
                if (idCheckRs.next()) {
                    String multipleNextIdStr = idCheckRs.getString(1);
                    multipleNextId = Integer.parseInt(multipleNextIdStr);
                    multipleNextId++;
                }
            } catch (Exception e) {
                logger.debug("('" + username + "') Error (executeParameterUpdate): " + e.getMessage());
            }

            String overallUpdateSql = "";
            for (int i = 0; i < paramValueNumIn; i++) {
                String updateSql = "INSERT INTO " + tablename + " VALUES(";
                updateSql += "?,";
                updateSql += "?,?,";
                updateSql += "?";

                if(tablename.equals("Associated_Studies")){
                    updateSql += ",?,?";
                }
                
                /*
                 * updateSql += "" + multipleNextId + ","; updateSql += "" +
                 * nextId + ",'" + centerId + "',"; updateSql += "'" +
                 * lastPageParamValues.get(i) + "'";
                 */
                updateSql += ");";
                multipleNextId++;
                overallUpdateSql += updateSql;
                try {
                    PreparedStatement ps = conn.prepareStatement(updateSql);
                    ps.setInt(1, multipleNextId);
                    ps.setInt(2, nextId);
                    ps.setString(3, centerId);                    
                    
                    if(tablename.equals("Associated_Studies")){
                        String studyLabel = this.getStudyLabel(lastPageParamValues.get(i));
                        String studyname = lastPageParamValues.get(i);
                        //logger.debug("studyname: " + studyname);
                        //logger.debug("studyLabel: " + studyLabel);
                        ps.setString(4, studyname);
                        ps.setString(5, studyLabel);
                        
                        //This is the particular study identifier
                        int thisStudyIndex = -1;                        
                        for(int k=0; k<otherStudyIDNames.size(); k++){
                            String studyIDNameIn = otherStudyIDNames.get(k);
                            //logger.debug("studyIDNameIn: " + studyIDNameIn);
                            //logger.debug("studyname: " + studyname);
                            if(studyIDNameIn.equals(studyname + "_id")){
                                thisStudyIndex = k;
                            }
                        }
                        //logger.debug("thisStudyIndex: " + thisStudyIndex);
                        
                        if(thisStudyIndex != -1){
                            ps.setString(6, otherStudyIDValues.get(thisStudyIndex)); 
                        }else{
                            ps.setString(6, ""); 
                        }
                    }else{
                        ps.setString(4, lastPageParamValues.get(i));
                    }

                    int update = ps.executeUpdate();
                } catch (Exception e) {
                    logger.debug("('" + username + "') Error (executeParameterUpdate): " + e.getMessage());
                }
            }
            return overallUpdateSql;
        } else {
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
                        && (
                        paramNameIn.equals("system_organ") 
                        || paramNameIn.equals("presentation_first_tumor")
                        || paramNameIn.equals("hormone_symptoms")
                        || paramNameIn.equals("tumor_symptoms")
                        || paramNameIn.equals("first_diagnosis_tnm")
                        || paramNameIn.equals("malignant_diagnosis_tnm")                        
                        )
                        ||
                        (tablename.equals("ACC_TumorStaging")
                        && (paramNameIn.equals("imaging")))
                        ||
                        (tablename.equals("Identification")
                        && (paramNameIn.equals("associated_studies"))))
                        ;

                if (!paramException) {
                    updateSql += paramNameIn + ",";
                }
            }
            updateSql = updateSql.substring(0, updateSql.length() - 1);
            updateSql += ") VALUES(";

            String recordDate = this.getRecordDate();

            if (tablename.equals("Identification")) {
                //updateSql += "" + nextId + ",'" + username + "','" + dbn + "','" + recordDate + "',";
                updateSql += "?,?,?,?,";
            } else {
                updateSql += "?,?,";
                //updateSql += "" + nextId + ",'" + centerId + "',";
            }

            for (int i = 0; i < paramNum; i++) {

                String paramName = lastPageParamNames.get(i);
                String paramType = lastPageParamTypes.get(i);
                String paramValue = lastPageParamValues.get(i);
                
                //Need to check for semi-colon characters and replace
                paramValue.replaceAll(";", "\\;");

                //System.out.println("paramName: " + paramName);
                //System.out.println("paramValue: " + paramValue);

                //Do number conversions for specific parameter names
                if (paramName.equals("gluco_serum_cortisol_napaca")
                        || paramName.equals("gluco_plasma_acth_napaca")
                        || paramName.equals("gluco_urinary_free_cortisol_napaca")
                        || paramName.equals("other_steroid_17hydroxyprogesterone_napaca")
                        || paramName.equals("other_steroid_serum_dheas_napaca")) {
                    paramValue = this.napacaUnitConversion(paramName, paramValue, request);
                }

                boolean paramException = (tablename.equals("Pheo_PatientHistory")
                        && (
                        paramName.equals("system_organ") 
                        || paramName.equals("presentation_first_tumor")
                        || paramName.equals("hormone_symptoms")
                        || paramName.equals("tumor_symptoms")
                        || paramName.equals("first_diagnosis_tnm")
                        || paramName.equals("malignant_diagnosis_tnm")                        
                        )
                        ||
                        (tablename.equals("ACC_TumorStaging")
                        && (paramName.equals("imaging")))
                        ||
                        (tablename.equals("Identification")
                        && (paramName.equals("associated_studies"))))                        
                        ;

                if (!paramException) {
                    //updateSql += "'" + paramValue + "',";
                    updateSql += "?,";
                }
            }
            updateSql = updateSql.substring(0, updateSql.length() - 1);
            updateSql += ");";

            //logger.debug("updateSql: " + updateSql);
            logger.debug("=== RECORD CREATED ===");
            try {
                logger.debug("Ensat ID: " + centerId + "-" + nextId);
                logger.debug("Username: " + username);
                logger.debug("Table: " + tablename);
                logger.debug(" ------ ");

                PreparedStatement ps = conn.prepareStatement(updateSql);
                if (tablename.equals("Identification")) {
                    //updateSql += "" + nextId + ",'" + username + "','" + dbn + "','" + recordDate + "',";
                    ps.setInt(1, nextId);
                    ps.setString(2, username);
                    ps.setString(3, dbn);
                    //recordDate = this.reformatDateValue(recordDate);
                    ps.setString(4, recordDate);
                } else {
                    //updateSql += "" + nextId + ",'" + centerId + "',";
                    ps.setInt(1, nextId);
                    ps.setString(2, centerId);
                }

                int paramCount = 0;
                if (tablename.equals("Identification")) {
                    paramCount = 5;
                } else {
                    paramCount = 3;
                }

                for (int i = 0; i < paramNum; i++) {

                    String paramName = lastPageParamNames.get(i);
                    String paramValue = lastPageParamValues.get(i);
                    
                    //Need to check for semi-colon characters and replace
                    paramValue.replaceAll(";", "\\;");
                    
                    String paramType = lastPageParamTypes.get(i);
                    
                    if(paramValue.startsWith("NLDAN")){
                        paramValue = "NLDAN";
                    }

                    boolean paramException = (tablename.equals("Pheo_PatientHistory")
                        && (
                        paramName.equals("system_organ") 
                        || paramName.equals("presentation_first_tumor")
                        || paramName.equals("hormone_symptoms")
                        || paramName.equals("tumor_symptoms")
                        || paramName.equals("first_diagnosis_tnm")
                        || paramName.equals("malignant_diagnosis_tnm")                        
                        )
                        ||
                            (tablename.equals("ACC_TumorStaging")
                            && (paramName.equals("imaging")))                        
                            ||
                            (tablename.equals("Identification")
                            && (paramName.equals("associated_studies"))))
                            ;

                    if (!paramException) {
                        //updateSql += "'" + paramValue + "',";
                        if (paramType.equals("date")) {
                            paramValue = this.reformatDateValue(paramValue);
                        }
                        ps.setString(paramCount, paramValue);
                        paramCount++;
                    }                    
                    logger.debug("" + paramName + ": " + paramValue);
                }


                //int update = statement.executeUpdate(updateSql);
                int update = ps.executeUpdate();
            } catch (Exception e) {
                logger.debug("('" + username + "') Error (executeParameterUpdate): " + e.getMessage());
            }
            
            //Update the network center table here (if required)
            if(!networkCenterName.equals("")){
                
                
                String updateNetworkSql = "INSERT INTO Network_Center(ensat_id,network_id,center_name) VALUES(?,?,?);";
                try{
                    PreparedStatement psNetwork = conn.prepareStatement(updateNetworkSql);
                    psNetwork.setInt(1,nextId);
                    psNetwork.setString(2,centerId);
                    psNetwork.setString(3,networkCenterName);
                    int updateNetwork = psNetwork.executeUpdate();
                } catch (Exception e) {
                    logger.debug("('" + username + "') Error (executeParameterUpdate - networkCenterName): " + e.getMessage());
                }            
            }

            logger.debug("=====");

            return updateSql;
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
    
    private String getRecordDate() {
        java.util.Date now = new java.util.Date();

        //Tue May 29 16:42:31 EST 2012
        String nowStr = now.toString();
        StringTokenizer st = new StringTokenizer(nowStr);
        st.nextToken();
        String month = st.nextToken();

        if (month.equals("Jan")) {
            month = "01";
        } else if (month.equals("Feb")) {
            month = "02";
        } else if (month.equals("Mar")) {
            month = "03";
        } else if (month.equals("Apr")) {
            month = "04";
        } else if (month.equals("May")) {
            month = "05";
        } else if (month.equals("Jun")) {
            month = "06";
        } else if (month.equals("Jul")) {
            month = "07";
        } else if (month.equals("Aug")) {
            month = "08";
        } else if (month.equals("Sep")) {
            month = "09";
        } else if (month.equals("Oct")) {
            month = "10";
        } else if (month.equals("Nov")) {
            month = "11";
        } else if (month.equals("Dec")) {
            month = "12";
        }

        String day = st.nextToken();
        st.nextToken();
        st.nextToken();
        String year = st.nextToken();

        return "" + year + "-" + month + "-" + day;
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
        
        int freezerInfoCount = paramNum;
        String[] freezerInfoLabels = {"freezer", "freezershelf", "shelf", "rack", "box", "position"};
        for (int i = 0; i < paramNum; i++) {
            Vector<String> paramIn = parameters.get(i);
            String paramName = paramIn.get(1);

            if (this.getAliquotParameter(paramName)) {
                //j acts as the freezer line index
                for (int j = 1; j < 10; j++) {
                    for (int k = 0; k < freezerInfoLabels.length; k++) {
                        outputStr += "var paramArray" + freezerInfoCount + " = new Array(\"" + paramName + "_" + freezerInfoLabels[k] + "_" + j + "\",true,\"number\");";
                        freezerInfoCount++;
                    }
                }
            }
        }
        
        outputStr += "var paramArrays = new Array(";
        for (int i = 0; i < paramNum; i++) {
            outputStr += "paramArray" + i + ",";
        }
        
        freezerInfoCount = paramNum;
        for (int i = 0; i < paramNum; i++) {
            Vector<String> paramIn = parameters.get(i);
            String paramName = paramIn.get(1);

            if (this.getAliquotParameter(paramName)) {                
                //j acts as the freezer line index
                for (int j = 1; j < 10; j++) {
                    for (int k = 0; k < freezerInfoLabels.length; k++) {
                        outputStr += "paramArray" + freezerInfoCount + ",";
                        freezerInfoCount++;
                    }
                }
            }
        }        
        
        outputStr = outputStr.substring(0, outputStr.length() - 1);
        outputStr += ");";
        outputStr += "</script>";

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
    
    
    protected String convertPlasmaUrine(String valueIn, String parameterIn, String units){
        if(valueIn == null){
            valueIn = "";
        }
        if(parameterIn == null){
            parameterIn = "";
        }
        
        if (!valueIn.equals("0") && !valueIn.equals("")) {
            
            double conversionFactor = 0.0;
            if(units.equals("ugday")){
                conversionFactor = 1000.0;
            }else{
                conversionFactor = this.getPheoConversionFactor(parameterIn);
            }            
            
            double valueDouble = Double.parseDouble(valueIn);
            String valueOut = "" + (valueDouble / conversionFactor);
            
            //Truncate to three decimal places
            int pointIndex = valueOut.indexOf(".");            
            if(pointIndex != -1 && (valueOut.length() - pointIndex > 4)){
                valueOut = valueOut.substring(0,pointIndex+4);
            }
            return "" + valueOut;
        } else {
            return valueIn;
        }        
    }
    
    protected double getPheoConversionFactor(String parameterIn){
        double conversionFactor = 0.0;
        if(parameterIn.equals("plasma_e")){
            conversionFactor = 5.46;
        }else if(parameterIn.equals("plasma_n")){
            conversionFactor = 5.91;
        }else if(parameterIn.equals("plasma_free_m")){
            conversionFactor = 5.08;
        }else if(parameterIn.equals("plasma_free_n")){
            conversionFactor = 5.46;
        }else if(parameterIn.equals("plasma_free_methox")){
            conversionFactor = 167.2;
        }else if(parameterIn.equals("plasma_dopamine_conc")){
            conversionFactor = 152.9;
        }else if(parameterIn.equals("urine_free_e")){
            conversionFactor = 5.07;
        }else if(parameterIn.equals("urine_free_n")){
            conversionFactor = 5.46;
        }else if(parameterIn.equals("urine_m")){
            conversionFactor = 5.07;
        }else if(parameterIn.equals("urine_n")){
            conversionFactor = 5.46;
        }

        return conversionFactor;
    }
    
    public String getStudyInclusion(String studyName, HttpServletRequest request){
        
        boolean inStudy = false;
        String paramValueNumInStr = request.getParameter("associated_studies_num");
        //logger.debug("paramValueNumInStr: " + paramValueNumInStr);
        int paramValueNumIn = Integer.parseInt(paramValueNumInStr);
        int paramCount = 0;
        while((paramCount < paramValueNumIn) && !inStudy){
            String valueIn = request.getParameter("associated_studies_" + (paramCount + 1));
            if(valueIn == null){
                valueIn = "";
            }
            valueIn = valueIn.trim();            
            //logger.debug("valueIn: " + valueIn);
            inStudy = valueIn.equals(studyName);
            paramCount++;
        }
        
        if(inStudy){
            return "Yes";
        }else{
            return "No";
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
    
    public String[] getStudiesIncluded(HttpServletRequest request){
        
        String paramValueNumInStr = request.getParameter("associated_studies_num");        
        int paramValueNumIn = 0;
        try{
            paramValueNumIn = Integer.parseInt(paramValueNumInStr);
        }catch(NumberFormatException nfe){
            paramValueNumIn = 0;
        }        
        String[] studies = new String[paramValueNumIn];
        for(int i=0; i<paramValueNumIn; i++){
            String valueIn = request.getParameter("associated_studies_" + (i + 1));
            if(valueIn == null){
                valueIn = "";
            }
            valueIn = valueIn.trim();
            studies[i] = valueIn;
        }
        return studies;
    }    
    
    public String getStudyName(String studyLabel){
        
        String studyName = "";
        if(studyLabel.equals("EURINE-ACT")){
            studyName = "eurineact";
        }else if(studyLabel.equals("Ki-67")){
            studyName = "ki67";
        }else if(studyLabel.equals("Stage III/IV ACC")){
            studyName = "stage_3_4_acc";
        }else if(studyLabel.equals("ACC Molecular Marker")){
            studyName = "acc_mol_marker";
        }else if(studyLabel.equals("PMT")){
            studyName = "pmt";
        }else if(studyLabel.equals("TMA")){
            studyName = "tma";
        }else if(studyLabel.equals("Long-term PHPGL")){
            studyName = "ltphpgl";
        }else if(studyLabel.equals("AVIS-2")){
            studyName = "avis2";
        }else if(studyLabel.equals("PMT3")){
            studyName = "pmt3";
        }else if(studyLabel.equals("ADIUVO")){
            studyName = "adiuvo";
        }else if(studyLabel.equals("ADIUVO Observational")){
            studyName = "adiuvo_observational";
        }else if(studyLabel.equals("HairCo")){
            studyName = "hairco";
        }else if(studyLabel.equals("HairCo-2")){
            studyName = "hairco2";
        }else if(studyLabel.equals("FIRST-MAPPP")){
            studyName = "firstmappp";
        }else if(studyLabel.equals("CHIRACIC")){
            studyName = "chiracic";
        }else if(studyLabel.equals("German Cushing Registry")){
            studyName = "german_cushing";
        }else if(studyLabel.equals("German Conn Registry")){
            studyName = "german_conn";
        }else if(studyLabel.equals("UK Pheo Audit")){
            studyName = "uk_pheo_audit";
        }else if(studyLabel.equals("Lysosafe")){
            studyName = "lysosafe";
        }else if(studyLabel.equals("FIRMACT")){
            studyName = "firmact";
        } else if (studyLabel.equals("MAPP-Prono")) {
            studyName = "mapp_prono";
        }
        
        return studyName;
    }
    
        public String getStudyLabel(String studyName){
        
        String studyLabel = "";
        if(studyName.equals("eurineact")){
            studyLabel = "EURINE-ACT";
        }else if(studyName.equals("ki67")){
            studyLabel = "Ki-67";
        }else if(studyName.equals("stage_3_4_acc")){
            studyLabel = "Stage III/IV ACC";
        }else if(studyName.equals("acc_mol_marker")){
            studyLabel = "ACC Molecular Marker";
        }else if(studyName.equals("pmt")){
            studyLabel = "PMT";
        }else if(studyName.equals("tma")){
            studyLabel = "TMA";
        }else if(studyName.equals("ltphpgl")){
            studyLabel = "Long-term PHPGL";
        }else if(studyName.equals("avis2")){
            studyLabel = "AVIS-2";
        }else if(studyName.equals("pmt3")){
            studyLabel = "PMT3";
        }else if(studyName.equals("adiuvo")){
            studyLabel = "ADIUVO";
        }else if(studyName.equals("adiuvo_observational")){
            studyLabel = "ADIUVO Observational";
        }else if(studyName.equals("hairco")){
            studyLabel = "HairCo";
        }else if(studyName.equals("hairco2")){
            studyLabel = "HairCo-2";
        }else if(studyName.equals("firstmappp")){
            studyLabel = "FIRST-MAPPP";
        }else if(studyName.equals("chiracic")){
            studyLabel = "CHIRACIC";
        }else if(studyName.equals("german_cushing")){
            studyLabel = "German Cushing Registry";
        }else if(studyName.equals("german_conn")){
            studyLabel = "German Conn Registry";
        }else if(studyName.equals("uk_pheo_audit")){
            studyLabel = "UK Pheo Audit";
        }else if(studyName.equals("lysosafe")){
            studyLabel = "Lysosafe";
        }else if(studyName.equals("firmact")){
            studyLabel = "FIRMACT";
        } else if (studyName.equals("mapp_prono")) {
            studyLabel = "MAPP-Prono";
        }
        
        return studyLabel;
    }
        
        
    protected String getFreezerConfirmHtml(HttpServletRequest request, Vector<String> lastPageParamNames, int i) {
        
        String outputStr = "";
        
        //Get the relevant aliquot number
        String aliquotNumber = request.getParameter("aliquot_" + lastPageParamNames.get(i));
        int aliquotNumberInt = 1;
        try{
            aliquotNumberInt = Integer.parseInt(aliquotNumber);
        }catch(NumberFormatException nfe){
            logger.debug("NumberFormatException: " + nfe.getMessage());
            aliquotNumberInt = 1;
        }
        
        outputStr += "<table border='2' width='100%' cellpadding='1'>";
        for(int j=0; j<aliquotNumberInt; j++){
                
            outputStr += "<tr>";
        
            String freezerValue = request.getParameter(lastPageParamNames.get(i) + "_freezer_" + (j+1));
            String freezerShelfValue = request.getParameter(lastPageParamNames.get(i) + "_freezershelf_" + (j+1));
            String shelfValue = request.getParameter(lastPageParamNames.get(i) + "_shelf_" + (j+1));
            String rackValue = request.getParameter(lastPageParamNames.get(i) + "_rack_" + (j+1));
            String boxValue = request.getParameter(lastPageParamNames.get(i) + "_box_" + (j+1));
            String positionValue = request.getParameter(lastPageParamNames.get(i) + "_position_" + (j+1));
            
            outputStr += "<th>";
            outputStr += "<div align='center'>Aliquot #" + (j+1) + "</div>";
            outputStr += "</th>"; 

            outputStr += "<td><div align='center'>Freezer: <strong>" + freezerValue + "</strong></div>";
            outputStr += "<input type='hidden' value='" + freezerValue + "' name='" + lastPageParamNames.get(i) + "_freezer_" + (j+1) + "'>";
            outputStr += "</td>";
            outputStr += "<td><div align='center'>Freezer shelf: <strong>" + freezerShelfValue + "</strong></div>";
            outputStr += "<input type='hidden' value='" + freezerShelfValue + "' name='" + lastPageParamNames.get(i) + "_freezershelf_" + (j+1) + "'>";
            outputStr += "</td>";
            outputStr += "<td><div align='center'>Rack: <strong>" + rackValue + "</strong></div>";
            outputStr += "<input type='hidden' value='" + rackValue + "' name='" + lastPageParamNames.get(i) + "_rack_" + (j+1) + "'>";
            outputStr += "</td>";
            outputStr += "<td><div align='center'>Rack shelf: <strong>" + shelfValue + "</strong></div>";
            outputStr += "<input type='hidden' value='" + shelfValue + "' name='" + lastPageParamNames.get(i) + "_shelf_" + (j+1) + "'>";
            outputStr += "</td>";
            outputStr += "<td><div align='center'>Box: <strong>" + boxValue + "</strong></div>";
            outputStr += "<input type='hidden' value='" + boxValue + "' name='" + lastPageParamNames.get(i) + "_box_" + (j+1) + "'>";
            outputStr += "</td>";
            outputStr += "<td><div align='center'>Position: <strong>" + positionValue + "</strong></div>";
            outputStr += "<input type='hidden' value='" + positionValue + "' name='" + lastPageParamNames.get(i) + "_position_" + (j+1) + "'>";
            outputStr += "</td>";
            outputStr += "</tr>";        
        }
        outputStr += "</table>";
        return outputStr;
    }        
    
    protected String getNormalTissueParamLabel(String paramLabel){
        
        String outputStr = "";
        if(paramLabel.equals("Adjacent Adrenal")){
            outputStr = "adjacentadrenal";
        }else if(paramLabel.equals("Kidney")){
            outputStr = "kidney";
        }else if(paramLabel.equals("Liver")){
            outputStr = "liver";
        }else if(paramLabel.equals("Lung")){
            outputStr = "lung";
        }else if(paramLabel.equals("Lymph Node")){
            outputStr = "lymphnode";
        }else if(paramLabel.equals("Fat (Periadrenal)")){
            outputStr = "fatperiadrenal";
        }else if(paramLabel.equals("Fat (Subcutaneous)")){
            outputStr = "fatsubcutaneous";
        }else if(paramLabel.equals("Others")){
            outputStr = "others";
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
        
        outputStr += "<tr><th colspan='7'><div align='center'>" + labelIn + " (" + aliquotNumber + ") ";
        outputStr += "<input type=\"hidden\" name=\"aliquot_" + lastPageParamNames.get(i) + "_" + paramLabelIn + "\" value=\"" + aliquotNumber + "\"/>";
        outputStr += "</div></th></tr>";        

        for (int j = 0; j < aliquotNumberInt; j++) {

            outputStr += "<tr>";

            String freezerValue = request.getParameter(lastPageParamNames.get(i) +  "_" + paramLabelIn + "_freezer_" + (j + 1));
            String freezerShelfValue = request.getParameter(lastPageParamNames.get(i) +  "_" + paramLabelIn + "_freezershelf_" + (j + 1));
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
}
