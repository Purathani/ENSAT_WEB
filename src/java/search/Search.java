package search;

import ConnectBean.ConnectionAuxiliary;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.Statement;
import java.sql.ResultSet;
import java.sql.ResultSetMetaData;
import java.sql.PreparedStatement;

import java.util.Vector;
import java.util.StringTokenizer;
import java.util.List;
import java.util.ArrayList;
import java.util.Enumeration;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.ServletContext;
import security.Authz;

import org.apache.log4j.Level;
import org.apache.log4j.Logger;
import org.apache.log4j.PropertyConfigurator;

/**
 *
 * @author astell
 */
public class Search {

    private static final Logger logger = Logger.getLogger(Search.class);
    private String username = "";
    private ResultSet searchResults = null;
    private String searchQuery = null;
    private Vector<String> conditions = null;
    private Vector<String> viewParams = null;

    public Search() {
    }
    
    public void setUsername(String _username){
        username = _username;
    }

    public String getLineColour() {
        return "class=\"parameter-line-double-search\"";
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
            logger.debug("Error (getMenuTypes): " + e.getMessage());
            System.out.println("Error (getMenuTypes): " + e.getMessage());
        }
        return menuTypes;
    }

    public Vector<Vector> getParameters(String[] _tablenames, HttpServletRequest request, ServletContext context) {

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
            ConnectionAuxiliary aux = new ConnectionAuxiliary();
            //Statement stmt = null;
            Connection conn = null;
            if (context != null) {
                //stmt = aux.getAuxiliaryConnection(context, "parameters").createStatement();
                conn = aux.getAuxiliaryConnection(context, "parameters");
            }

            //Get the menu types here (one call rather than for each parameter)
            Vector<Vector> menuTypes = this.getMenuTypes(conn);

            PreparedStatement ps = conn.prepareStatement(sql);
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

            rs = ps.executeQuery();
            int rowCount = 0;
            while (rs.next()) {
                Vector<String> rowIn = new Vector<String>();
                for (int i = 0; i < columnCount; i++) {
                    rowIn.add(rs.getString(i + 1));
                }
                rowCount++;
                parameters.add(rowIn);
            }

            parameters = this.addCalcFieldValues(parameters, request, tablenames);

        } catch (Exception e) {
            logger.debug("Error (getParameters): " + e.getMessage());
            System.out.println("Error (getParameters): " + e.getMessage());
        }

        return parameters;
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
        } catch (Exception e) {
            logger.debug("Error (getMenus): " + e.getMessage());
            System.out.println("Error (getMenus): " + e.getMessage());
        }
        return menus;
    }

    public String getParameterHtml(Vector<Vector> parameters, Vector<Vector> menus, String lineColour) {

        String outputStr = "";

        //Get the number of rows here        
        int paramNum = parameters.size();

        //For each row
        for (int i = 0; i < paramNum; i++) {

            Vector<String> rowIn = parameters.get(i);
            String addedJsStr = "";
            outputStr += this.getIndividualParameterHtml(rowIn, menus, lineColour, i, addedJsStr);
        }
        return outputStr;
    }

    private String getIndividualParameterHtml(Vector<String> rowIn, Vector<Vector> menus, String lineColour, int index, String addedJsStr) {

        String paramName = rowIn.get(1);

        //Get the number of menus here
        int menuNum = menus.size();

        String outputStr = "";

        //Case for an empty value vector here
        String valueIn = "";

        outputStr += "<tr ";
        if (index % 2 != 0) {
            outputStr += lineColour;
        }
        outputStr += ">";

        //Add the search comparator here
        outputStr += "<td>";
        outputStr += "<select name=\"comparator_" + paramName + "\">";
        outputStr += "<option value=\"\">[Select...]</option>";
        outputStr += "<option value=\"NOT\">NOT</option>";
        outputStr += "<option value=\"AND\">AND</option>";
        outputStr += "<option value=\"OR\">OR</option>";
        outputStr += "</select>";
        outputStr += "</td>";

        outputStr += "<td width=\"50%\">";
        outputStr += rowIn.get(4) + ":";
        outputStr += "</td>";
        outputStr += "<td>";

        String othersJsTrailer = "";
        if (rowIn.get(2).equals("text") || rowIn.get(2).equals("text_only")) {
            outputStr += "<input name=\"" + rowIn.get(1) + "\" type=\"text\" size=\"" + rowIn.get(3) + "\" onfocus=\"inform=true;\" onblur=\"" + addedJsStr + "inform=false;\"/>";

            //Add the additional button for showing extra options here
            outputStr += "<input type=\"button\" name=\"add_button_" + paramName + "\" value=\"+\" onclick=\"return showHideSearch(this.name);\"/>";

            //Add additional options in hidden CSS here
            outputStr += "<table>";
            for (int j = 1; j < 5; j++) {
                outputStr += "<tr><td><div id=\"" + paramName + "_option_" + j + "\" class=\"hide\">";
                outputStr += "<input name=\"" + rowIn.get(1) + "_additional_option_" + j + "\" type=\"text\" size=\"" + rowIn.get(3) + "\" onfocus=\"inform=true;\" onblur=\"" + addedJsStr + "inform=false;\"/>";
                outputStr += "</div></td></tr>";
            }
            outputStr += "</table>";

        } else if (rowIn.get(2).equals("number")) {
            outputStr += "<input name=\"" + rowIn.get(1) + "_1\" type=\"text\" size=\"" + rowIn.get(3) + "\" onfocus=\"inform=true;\" onblur=\"" + addedJsStr + "inform=false;\"/>";
            outputStr += " to ";
            outputStr += "<input name=\"" + rowIn.get(1) + "_2\" type=\"text\" size=\"" + rowIn.get(3) + "\" onfocus=\"inform=true;\" onblur=\"" + addedJsStr + "inform=false;\"/>";
        } else if (rowIn.get(2).equals("date")) {

            //Dissect up the valueIn for the date here
            String valueYear = "";
            String valueMonth = "";
            String valueDay = "";

            if (valueIn != null && !valueIn.trim().equals("")) {
                valueYear = valueIn.substring(0, valueIn.indexOf("-"));
                valueMonth = valueIn.substring(valueIn.indexOf("-") + 1, valueIn.lastIndexOf("-"));
                valueDay = valueIn.substring(valueIn.lastIndexOf("-") + 1, valueIn.length());
            }

            outputStr += "<table width=\"100%\" cellpadding=\"5\">";
            for (int j = 1; j < 3; j++) {

                outputStr += "<tr>";
                outputStr += "<td>";
                outputStr += "Day:";
                outputStr += "</td>";
                outputStr += "<td>";
                outputStr += "<input name=\"" + paramName + "_day_" + j + "\" type=\"text\" size=\"3\"  value=\"" + valueDay + "\" onfocus=\"inform=true;\" onblur=\"inform=false;\"/>";
                outputStr += "</td>";
                outputStr += "<td>";
                outputStr += "Month:";
                outputStr += "</td>";
                outputStr += "<td>";
                outputStr += "<select name=\"" + paramName + "_month_" + j + "\" onchange=\"\">"
                        + "<option value=\"\">[Select...]</option>";
                outputStr += "<option";
                if (valueMonth.equals("01")) {
                    outputStr += " selected ";
                }
                outputStr += " value=\"01\">Jan</option>";
                outputStr += "<option";
                if (valueMonth.equals("02")) {
                    outputStr += " selected ";
                }
                outputStr += " value=\"02\">Feb</option>";
                outputStr += "<option";
                if (valueMonth.equals("03")) {
                    outputStr += " selected ";
                }
                outputStr += " value=\"03\">Mar</option>";
                outputStr += "<option";
                if (valueMonth.equals("04")) {
                    outputStr += " selected ";
                }
                outputStr += " value=\"04\">Apr</option>";
                outputStr += "<option";
                if (valueMonth.equals("05")) {
                    outputStr += " selected ";
                }
                outputStr += " value=\"05\">May</option>";
                outputStr += "<option";
                if (valueMonth.equals("06")) {
                    outputStr += " selected ";
                }
                outputStr += " value=\"06\">Jun</option>";
                outputStr += "<option";
                if (valueMonth.equals("07")) {
                    outputStr += " selected ";
                }
                outputStr += " value=\"07\">Jul</option>";
                outputStr += "<option";
                if (valueMonth.equals("08")) {
                    outputStr += " selected ";
                }
                outputStr += " value=\"08\">Aug</option>";
                outputStr += "<option";
                if (valueMonth.equals("09")) {
                    outputStr += " selected ";
                }
                outputStr += " value=\"09\">Sep</option>";
                outputStr += "<option";
                if (valueMonth.equals("10")) {
                    outputStr += " selected ";
                }
                outputStr += " value=\"10\">Oct</option>";
                outputStr += "<option";
                if (valueMonth.equals("11")) {
                    outputStr += " selected ";
                }
                outputStr += " value=\"11\">Nov</option>";
                outputStr += "<option";
                if (valueMonth.equals("12")) {
                    outputStr += " selected ";
                }
                outputStr += " value=\"12\">Dec</option>";

                outputStr += "</select>";
                outputStr += "</td>";
                outputStr += "<td>";
                outputStr += "Year:";
                outputStr += "</td>";
                outputStr += "<td>";
                outputStr += "<input name=\"" + paramName + "_year_" + j + "\" type=\"text\" size=\"4\"  value=\"" + valueYear + "\" onfocus=\"inform=true;\" onblur=\"inform=false;\"/>";
                outputStr += "</td>";
                outputStr += "</tr>";
            }
            outputStr += "</table>";
        } else if (rowIn.get(2).equals("menu")) {

            String othersJs = "dispOthers(this.name,this.value);";
            othersJsTrailer = "<div id=\"myDiv_" + paramName + "_others\"></div>";

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

            /*
             * if (menuIn.get(2).equals("m")) { menuHeaderStr += "<select
             * multiple name=\"" + paramName + "\" onchange=\"" + addedJsStr +
             * "\">"; } else {
             */
            menuHeaderStr += "<select name=\"" + paramName + "\" onchange=\"" + addedJsStr + "" + othersJs + "\">";
            //}

            /*
             * if (menuIn.get(2).equals("m")) {
             *
             * //Need to tokenize valueIn to an array here (for multiple
             * options) StringTokenizer st = new StringTokenizer(valueIn, "|");
             * int tokenNum = st.countTokens(); String[] valuesIn = new
             * String[tokenNum];
             *
             * int tokenCount = 0; while (st.hasMoreTokens()) {
             * valuesIn[tokenCount] = st.nextToken(); tokenCount++; }
             *
             * int menuSize = menuIn.size(); for (int k = 3; k < menuSize; k++)
             * { menuSelectStr += "<option";
             *
             * for (int m = 0; m < tokenNum; m++) { if
             * (valuesIn[m].equals(menuIn.get(k))) { menuSelectStr += " selected
             * "; } } menuSelectStr += " value=\"" + menuIn.get(k) + "\">" +
             * menuIn.get(k) + "</option>"; } } else {
             */
            menuSelectStr += "<option value=\"\">[Select...]</option>";
            int menuSize = menuIn.size();
            for (int k = 3; k < menuSize; k++) {
                menuSelectStr += "<option";
                if (valueIn.equals(menuIn.get(k))) {
                    menuSelectStr += " selected ";
                }
                menuSelectStr += " value=\"" + menuIn.get(k) + "\">" + menuIn.get(k) + "</option>";
            }
            //}

            outputStr += menuHeaderStr;
            outputStr += menuSelectStr;

            outputStr += "</select>";

            //Add the additional button for showing extra options here
            outputStr += "<input type=\"button\" name=\"add_button_" + paramName + "\" value=\"+\" onclick=\"return showHideSearch(this.name);\"/>";

            //Add additional options in hidden CSS here
            outputStr += "<table>";

            for (int k = 1; k < 5; k++) {
                outputStr += "<tr><td><div id=\"" + paramName + "_option_" + k + "\" class=\"hide\">";
                String innerMenuHeaderStr = "";
                /*
                 * if (menuIn.get(2).equals("m")) { innerMenuHeaderStr +=
                 * "<select multiple name=\"" + paramName +
                 * "_additional_option_" + k + "\" onchange=\"" + addedJsStr +
                 * "\">"; } else {
                 */
                innerMenuHeaderStr += "<select name=\"" + paramName + "_additional_option_" + k + "\" onchange=\"" + addedJsStr + "\">";
                //}
                outputStr += innerMenuHeaderStr;
                outputStr += menuSelectStr;
                outputStr += "</select>";
                outputStr += "</div></td></tr>";
            }
            outputStr += "</table>";
        } else {

            String menuSelectStr = "";
            //Use this point to catch the parameters that are unusual (e.g. center_id)
            if (paramName.equals("center_id")) {

                //NEED TO MAKE THIS DYNAMIC (use AJAX to get it from center_callout table)
                String menuHeaderStr = "<select name=\"" + paramName + "\">";

                Vector<String> centerIDs = this.getCenterIDs();

                menuSelectStr += "<option value=\"\">[Select...]</option>";

                for (int i = 0; i < centerIDs.size(); i++) {
                    String centerIDin = centerIDs.get(i);
                    menuSelectStr += "<option value=\"" + centerIDin + "\">" + centerIDin + "</option>";
                }
                outputStr += menuHeaderStr;
                outputStr += menuSelectStr;
                outputStr += "</select>";

                //Add the additional button for showing extra options here
                outputStr += "<input type=\"button\" name=\"add_button_" + paramName + "\" value=\"+\" onclick=\"return showHideSearch(this.name);\"/>";

                //Add additional options in hidden CSS here
                outputStr += "<table>";

                for (int k = 1; k < 5; k++) {
                    outputStr += "<tr><td><div id=\"" + paramName + "_option_" + k + "\" class=\"hide\">";
                    String innerMenuHeaderStr = "";

                    innerMenuHeaderStr += "<select name=\"" + paramName + "_additional_option_" + k + "\" onchange=\"" + addedJsStr + "\">";

                    outputStr += innerMenuHeaderStr;
                    outputStr += menuSelectStr;
                    outputStr += "</select>";
                    outputStr += "</div></td></tr>";
                }
                outputStr += "</table>";
            }
        }

        outputStr += othersJsTrailer;

        outputStr += "</td>";
        outputStr += "</tr>";

        //Add some detail for the calculated fields here (BMI and ENSAT classification)
        if (paramName.equals("weight")) {
            outputStr += "<tr>";
            //Add the search comparator here
            outputStr += "<td>";
            outputStr += "<select name=\"comparator_bmi\">";
            outputStr += "<option value=\"\">[Select...]</option>";
            outputStr += "<option value=\"NOT\">NOT</option>";
            outputStr += "<option value=\"AND\">AND</option>";
            outputStr += "<option value=\"OR\">OR</option>";
            outputStr += "</select>";
            outputStr += "</td>";

            outputStr += "<td width=\"50%\">";
            outputStr += "BMI:";
            outputStr += "</td>";
            outputStr += "<td>";

            outputStr += "<input name=\"bmi_1\" type=\"text\" size=\"3\" onfocus=\"inform=true;\" onblur=\"" + addedJsStr + "inform=false;\"/>";
            outputStr += " to ";
            outputStr += "<input name=\"bmi_2\" type=\"text\" size=\"3\" onfocus=\"inform=true;\" onblur=\"" + addedJsStr + "inform=false;\"/>";

            outputStr += "</td>";
            outputStr += "</tr>";

        } else if (paramName.equals("other_metastases")) {
            outputStr += "<tr>";
            //Add the search comparator here
            outputStr += "<td>";
            outputStr += "<select name=\"comparator_bmi\">";
            outputStr += "<option value=\"\">[Select...]</option>";
            outputStr += "<option value=\"NOT\">NOT</option>";
            outputStr += "<option value=\"AND\">AND</option>";
            outputStr += "<option value=\"OR\">OR</option>";
            outputStr += "</select>";
            outputStr += "</td>";

            outputStr += "<td width=\"50%\">";
            outputStr += "ENSAT Classification:";
            outputStr += "</td>";
            outputStr += "<td>";

            outputStr += "<input name=\"ensat_classification\" type=\"text\" size=\"4\" onfocus=\"inform=true;\" onblur=\"" + addedJsStr + "inform=false;\"/>";

            //Add the additional button for showing extra options here
            outputStr += "<input type=\"button\" name=\"add_button_ensat_classification\" value=\"+\" onclick=\"return showHideSearch(this.name);\"/>";

            //Add additional options in hidden CSS here
            outputStr += "<table>";
            for (int j = 1; j < 5; j++) {
                outputStr += "<tr><td><div id=\"ensat_classification_option_" + j + "\" class=\"hide\">";
                outputStr += "<input name=\"ensat_classification_additional_option_" + j + "\" type=\"text\" size=\"4\" onfocus=\"inform=true;\" onblur=\"" + addedJsStr + "inform=false;\"/>";
                outputStr += "</div></td></tr>";
            }
            outputStr += "</table>";
            outputStr += "</td>";
            outputStr += "</tr>";
        }

        return outputStr;
    }

    private Vector<String> getCenterIDs() {

        Vector<String> centerIDs = new Vector<String>();
        try {
            ConnectionAuxiliary aux = new ConnectionAuxiliary();
            Statement stmt = null;
            String driverName = "com.mysql.jdbc.Driver";
            String serverName = "stell-2.rc.melbourne.nectar.org.au";
            //String serverName = "localhost";
            String port = "3306";
            String username = "ensat";
            String password = "ensat_melb)";
            stmt = aux.getAuxiliaryConnection("center_callout", driverName, serverName, port, username, password).createStatement();

            String sql = "SELECT DISTINCT center_id FROM Center_Callout;";
            ResultSet rs = stmt.executeQuery(sql);
            rs = stmt.executeQuery(sql);
            while (rs.next()) {
                String valueIn = rs.getString(1);
                centerIDs.add(valueIn);
            }

        } catch (Exception e) {
            logger.debug("Error (getCenterIDs): " + e.getMessage());
            System.out.println("Error (getCenterIDs): " + e.getMessage());
        }

        return centerIDs;
    }

    public SearchResult compileSearchResults(Vector<Vector> parameters, HttpServletRequest request) {

        SearchResult sr = new SearchResult();

        List<String> parameterInputs = new ArrayList<String>();
        List<String> tables = new ArrayList<String>();
        List<String> conditions = new ArrayList<String>();
        List<String> comparators = new ArrayList<String>();

        int paramNum = parameters.size();

        //System.out.println("paramNum (SEARCH OUTPUT): " + paramNum);
        int firstConditionCount = 0;
        for (int i = 0; i < paramNum; i++) {

            Vector<String> paramIn = parameters.get(i);

            //Get the parameter
            String paramName = paramIn.get(1);
            String paramType = paramIn.get(2);
            //System.out.println("paramType: " + paramType);

            //Identify what tables are associated with the parameters that have information and add to the table list
            String tableIn = paramIn.get(8);

            //Get the condition (if date or number range, then grab 1 and 2)           
            //Test for null-ness
            String conditionIn = "";

            //Check the relevant comparator
            String comparatorIn = "";
            if (request != null) {
                comparatorIn = request.getParameter("comparator_" + paramName);
            }
            if (comparatorIn == null) {
                comparatorIn = "";
            }
            if (comparatorIn.equals("")) {
                //Default to "AND", but only add to list if condition exists
                comparatorIn = "AND";
            }
            boolean firstComparatorNot = (firstConditionCount < 1) && (comparatorIn.equals("NOT"));

            //System.out.println("comparatorIn: " + comparatorIn);
            //System.out.println("firstConditionCount: " + firstConditionCount);
            //System.out.println("firstComparatorNot: " + firstComparatorNot);
            if (!paramType.equals("date") && !paramType.equals("number")) {
                String condInReq = "";
                if (request != null) {
                    condInReq = request.getParameter(paramName);
                }

                //MODIFY THIS TO ACCOUNT FOR VALUE "OTHERS"
                //System.out.println("condInReq: " + condInReq);
                if (condInReq.equals("Others")) {
                    condInReq = request.getParameter(paramName + "_others");
                }

                //System.out.println("condInReq (from request): " + condInReq);
                if (condInReq == null || condInReq.equals("") || condInReq.equals("null")) {
                    conditionIn = "";
                } else {
                    conditionIn = tableIn + "." + paramName;

                    if (firstComparatorNot) {
                        conditionIn += " NOT ";
                    }

                    conditionIn += " LIKE '" + condInReq + "'";
                }

                //Grab possible extra options if the parameter is menu or text
                String paramOptionLeadStr = "" + paramName + "_additional_option_";
                for (int j = 1; j < 5; j++) {
                    String thisParamOptionStr = paramOptionLeadStr + j;
                    String additionalOption = "";
                    if (request != null) {
                        additionalOption = request.getParameter(thisParamOptionStr);
                    }
                    if (additionalOption != null) {
                        additionalOption = additionalOption.trim();
                    }
                    if (additionalOption != null && !additionalOption.equals("null") && !additionalOption.equals("")) {
                        conditionIn += " OR " + tableIn + "." + paramName;
                        conditionIn += " LIKE '" + additionalOption + "'";
                    }
                }
            } else {
                String condIn1 = "";
                String condIn2 = "";

                if (paramType.equals("number")) {
                    if (request != null) {
                        condIn1 = request.getParameter(paramName + "_1");
                        condIn2 = request.getParameter(paramName + "_2");
                    }
                } else {
                    String condInMonth1 = "";
                    String condInMonth2 = "";
                    String condInDay1 = "";
                    String condInDay2 = "";
                    String condInYear1 = "";
                    String condInYear2 = "";

                    if (request != null) {
                        condInMonth1 = request.getParameter(paramName + "_month_1");
                        condInMonth2 = request.getParameter(paramName + "_month_2");
                        condInDay1 = request.getParameter(paramName + "_day_1");
                        condInDay2 = request.getParameter(paramName + "_day_2");
                        condInYear1 = request.getParameter(paramName + "_year_1");
                        condInYear2 = request.getParameter(paramName + "_year_2");
                    }

                    condIn1 = "" + condInYear1 + "-" + condInMonth1 + "-" + condInDay1;
                    condIn2 = "" + condInYear2 + "-" + condInMonth2 + "-" + condInDay2;

                    if (condIn1.equals("--")) {
                        condIn1 = "";
                    }
                    if (condIn2.equals("--")) {
                        condIn2 = "";
                    }

                    //System.out.println("condIn1: " + condIn1 + ", condIn2: " + condIn2);
                }

                if (!condIn1.equals("") && !condIn2.equals("")) {
                    conditionIn = tableIn + "." + paramName + " ";
                    if (firstComparatorNot) {
                        conditionIn += "!";
                    }
                    conditionIn += ">= " + condIn1 + " AND " + tableIn + "." + paramName + " ";
                    if (firstComparatorNot) {
                        conditionIn += "!";
                    }
                    conditionIn += "<= " + condIn2 + "";
                } else if (condIn1.equals("") && !condIn2.equals("")) {
                    conditionIn = tableIn + "." + paramName + " ";
                    if (firstComparatorNot) {
                        conditionIn += "!";
                    }
                    conditionIn += "<= " + condIn2;
                } else if (!condIn1.equals("") && condIn2.equals("")) {
                    conditionIn = tableIn + "." + paramName + " ";
                    if (firstComparatorNot) {
                        conditionIn += "!";
                    }
                    conditionIn += ">= " + condIn1;
                } else {
                    conditionIn = "";
                }
            }

            //System.out.println("conditionIn (" + i + "): " + conditionIn);
            if (!conditionIn.trim().equals("")) {
                if (!tables.contains(tableIn)) {
                    tables.add(tableIn);
                }
            }

            //If condition is not null or blank, add the data to the lists
            if (conditionIn != null && !conditionIn.trim().equals("")) {

                //Count the first condition (for the first NOT comparator)
                firstConditionCount++;

                //Qualify the paramName with the table name
                paramName = tableIn + "." + paramName;

                parameterInputs.add(paramName);
                conditions.add(conditionIn);
                comparators.add(comparatorIn);
            }

        }

        sr.setParameters(parameterInputs);
        sr.setConditions(conditions);
        sr.setComparators(comparators);
        sr.setTables(tables);

        return sr;
    }

    public SearchResult getRepeatSearchResults(List<String> parametersOrig, List<String> conditionsOrig, List<String> comparatorsOrig,
            List<String> tablesOrig, HttpServletRequest request, Vector<Vector> parameters) {

        SearchResult sr = new SearchResult();

        List<String> parameterInputs = new ArrayList<String>();
        List<String> tables = new ArrayList<String>();
        List<String> conditions = new ArrayList<String>();
        List<String> comparators = new ArrayList<String>();

        if (parametersOrig != null) {
            int paramOrigSize = parametersOrig.size();
            for (int i = 0; i < paramOrigSize; i++) {
                String paramInName = parametersOrig.get(i);
                String condInName = conditionsOrig.get(i);
                String comparatorInName = comparatorsOrig.get(i);
                String origSelection = request.getParameter("query_selection_" + paramInName);

                if (origSelection != null) {
                    if (origSelection.equals("on")) {

                        logger.debug("----- #" + i);
                        logger.debug("Parameter: " + paramInName);
                        logger.debug("SELECTED FOR REPEAT QUERYING...");
                        logger.debug("Condition: " + condInName);
                        logger.debug("Comparator: " + comparatorInName);

                        parameterInputs.add(paramInName);
                        conditions.add(condInName);
                        comparators.add(comparatorInName);

                        //Run through the available parameter look-up and retrieve the associated table
                        boolean paramFound = false;
                        int paramCount = 0;
                        while (paramCount < parameters.size() && !paramFound) {
                            Vector<String> paramIn = parameters.get(paramCount);
                            if (paramIn.get(1).equals(paramInName)) {
                                String tableValue = paramIn.get(8);
                                if (!tables.contains(tableValue)) {
                                    tables.add(tableValue);
                                }
                                paramFound = true;
                            } else {
                                paramCount++;
                            }
                        }
                    }

                }
            }

            int tableOrigSize = tablesOrig.size();
            for (int i = 0; i < tableOrigSize; i++) {
                String tableInName = tablesOrig.get(i);
                tables.add(tableInName);
            }

        }

        sr.setParameters(parameterInputs);
        sr.setConditions(conditions);
        sr.setComparators(comparators);
        sr.setTables(tables);

        return sr;

    }

    public SearchResult getShowFormSearchResults(List<String> parametersOrig, List<String> conditionsOrig, List<String> comparatorsOrig,
            List<String> tablesOrig, Vector<Vector> parameters) {

        SearchResult sr = new SearchResult();

        List<String> parameterInputs = new ArrayList<String>();
        List<String> tables = new ArrayList<String>();
        List<String> conditions = new ArrayList<String>();
        List<String> comparators = new ArrayList<String>();

        if (parametersOrig != null) {
            int paramOrigSize = parametersOrig.size();
            for (int i = 0; i < paramOrigSize; i++) {
                String paramInName = parametersOrig.get(i);
                String condInName = conditionsOrig.get(i);
                String comparatorInName = comparatorsOrig.get(i);

                logger.debug("----- #" + i);
                logger.debug("Parameter: " + paramInName);
                logger.debug("Condition: " + condInName);
                logger.debug("Comparator: " + comparatorInName);

                parameterInputs.add(paramInName);
                conditions.add(condInName);
                comparators.add(comparatorInName);

                //Run through the available parameter look-up and retrieve the associated table
                boolean paramFound = false;
                int paramCount = 0;
                while (paramCount < parameters.size() && !paramFound) {
                    Vector<String> paramIn = parameters.get(paramCount);
                    if (paramIn.get(1).equals(paramInName)) {
                        String tableValue = paramIn.get(8);
                        if (!tables.contains(tableValue)) {
                            tables.add(tableValue);
                        }
                        paramFound = true;
                    } else {
                        paramCount++;
                    }
                }
            }

            int tableOrigSize = tablesOrig.size();
            for (int i = 0; i < tableOrigSize; i++) {
                String tableInName = tablesOrig.get(i);
                tables.add(tableInName);
            }
        }

        sr.setParameters(parameterInputs);
        sr.setConditions(conditions);
        sr.setComparators(comparators);
        sr.setTables(tables);

        return sr;

    }

    public String compileSearchQuery(SearchResult sr, String dbn) {

        String searchQuerySql = "";

        List<String> parameterInputs = sr.getParameters();
        List<String> conditions = sr.getConditions();
        List<String> comparators = sr.getComparators();
        List<String> tables = sr.getTables();

        searchQuerySql = "SELECT DISTINCT Identification.ensat_id, Identification.center_id, Identification.local_investigator, Identification.investigator_email, Identification.record_date, Identification.date_first_reg, Identification.sex, Identification.year_of_birth, Identification.consent_obtained, Identification.uploader,";

        //GOING TO TRY LIMITING THIS TO 10 PARAMETERS
        int paramNumLimit = 10;
        if (parameterInputs.size() < 10) {
            paramNumLimit = parameterInputs.size();
        }

        for (int i = 0; i < paramNumLimit; i++) {
            String paramIn = parameterInputs.get(i);

            if (!(paramIn.equals("ensat_id") || paramIn.equals("year_of_birth") || paramIn.equals("sex") || paramIn.equals("center_id")
                    || paramIn.equals("local_investigator") || paramIn.equals("investigator_email") || paramIn.equals("date_first_reg") || paramIn.equals("consent_obtained"))) {
                searchQuerySql += paramIn + ",";
            }
        }

        searchQuerySql = searchQuerySql.substring(0, searchQuerySql.length() - 1);
        searchQuerySql += " FROM Identification, ";

        boolean diag_present = false;
        boolean tumor_present = false;

        for (int i = 0; i < tables.size(); i++) {
            String tableIn = tables.get(i);
            if (!tableIn.equals("Identification")) {
                searchQuerySql += tableIn + ", ";
                if (tableIn.equals("ACC_DiagnosticProcedures")) {
                    diag_present = true;
                } else if (tableIn.equals("ACC_TumorStaging")) {
                    tumor_present = true;
                }
            }
        }

        searchQuerySql = searchQuerySql.substring(0, searchQuerySql.length() - 2);
        if (parameterInputs.size() != 0) {
            searchQuerySql += " WHERE ";
        }

        if (diag_present && tumor_present) {
            searchQuerySql += " Identification.ensat_id=ACC_DiagnosticProcedures.ensat_id AND "
                    + " Identification.ensat_id=ACC_TumorStaging.ensat_id AND ";
            searchQuerySql += " AND Identification.center_id=ACC_DiagnosticProcedures.center_id AND "
                    + " Identification.center_id=ACC_TumorStaging.center_id AND ";
        } else if (diag_present && !tumor_present) {
            searchQuerySql += " Identification.ensat_id=ACC_DiagnosticProcedures.ensat_id ";
            searchQuerySql += " AND Identification.center_id=ACC_DiagnosticProcedures.center_id AND ";
        } else if (!diag_present && tumor_present) {
            searchQuerySql += " Identification.ensat_id=ACC_TumorStaging.ensat_id ";
            searchQuerySql += " AND Identification.center_id=ACC_TumorStaging.center_id AND ";
        }

        for (int i = 0; i < paramNumLimit; i++) {
            if (i == 0 && !diag_present && !tumor_present) {
                searchQuerySql += "";
            } else {
                if (comparators.size() != 0) {
                    String comparatorIn = comparators.get(i);
                    if (comparatorIn.equals("NOT")) {
                        if (i != 0) {
                            comparatorIn = "AND NOT";
                        } else {
                            //Taking account of the firstConditionCount for the NOT comparator
                            comparatorIn = "AND";
                        }
                    } else if (comparatorIn.equals("")) {
                        comparatorIn = "AND";
                    }
                    if (i == 0) {
                        //Remove the last "AND"
                        searchQuerySql = searchQuerySql.substring(0, searchQuerySql.length() - 5);
                    }
                    searchQuerySql += " " + comparatorIn + " ";
                }
            }
            String paramIn = parameterInputs.get(i);
            String condIn = conditions.get(i);

            searchQuerySql += condIn;

        }

        //Now add the database clause
        searchQuerySql += " AND Identification.ensat_database='" + dbn + "';";

        //System.out.println("searchQuerySql: " + searchQuerySql);
        return searchQuerySql;
    }

    public String compileSearchSQL(Vector<String> conditions, Vector<String> viewParams, String dbn) {

        int viewParamNum = viewParams.size();
        int conditionNum = conditions.size();

        logger.debug("conditionNum: " + conditionNum);

        //Compile unique table list (from both parameters and conditions)
        Vector<String> tables = new Vector<String>();
        for (int i = 0; i < viewParamNum; i++) {
            String paramIn = viewParams.get(i);
            String tableIn = paramIn.substring(0, paramIn.indexOf("."));
            if (!tables.contains(tableIn)) {
                tables.add(tableIn);
            }
        }
        for (int i = 0; i < conditionNum; i++) {
            String conditionIn = conditions.get(i);
            logger.debug("conditionIn: " + conditionIn);

            if (!conditionIn.equals("")) {
                boolean condIsMultiple = this.getCondIsMultiple(conditionIn);
                if (condIsMultiple) {
                    String condTable = this.getCondMultipleTable(conditionIn);
                    if (!tables.contains(condTable)) {
                        tables.add(condTable);
                    }
                } else {
                    String tableIn = conditionIn.substring(0, conditionIn.indexOf("."));
                    if (!tables.contains(tableIn)) {
                        tables.add(tableIn);
                    }
                }
            }
        }
        int tableNum = tables.size();

        String sql = "";
        sql += "SELECT DISTINCT ";
        for (int i = 0; i < viewParamNum; i++) {
            sql += "" + viewParams.get(i);
            if (i == viewParamNum - 1) {
                sql += " ";
            } else {
                sql += ", ";
            }
        }
        sql += "FROM ";

        //Compile the table list here
        for (int i = 0; i < tableNum; i++) {
            sql += "" + (String) tables.get(i);
            if (i == tableNum - 1) {
                sql += " ";
            } else {
                sql += ", ";
            }
        }

        //If tables need joined or conditions are present add the "WHERE"
        if (tableNum > 1 || conditionNum != 0) {
            sql += "WHERE ";
        }

        //Add the conditions here
        for (int i = 0; i < conditionNum; i++) {

            String conditionIn = conditions.get(i);

            //Check here if condition is multiple table
            //If so, then add the table name inner join, and the table descriptor
            boolean condIsMultiple = this.getCondIsMultiple(conditionIn);
            if (condIsMultiple) {

                String condMultipleValue = this.getCondMultipleValue(conditionIn);
                String condTable = this.getCondMultipleTable(conditionIn);
                String condDescriptor = this.getCondMultipleDescriptor(conditionIn);
                //String tableInnerJoin = (String) tables.get(i);

                /*sql += "" + tableInnerJoin + ".ensat_id=" + condTable + ".ensat_id AND ";
                 sql += "" + tableInnerJoin + ".center_id=" + condTable + ".center_id AND ";*/
                sql += "" + condTable + "." + condDescriptor + "='" + condMultipleValue + "'";

            } else {
                sql += "" + conditionIn;
            }

            if (i == conditionNum - 1) {
                sql += " ";
            } else {
                sql += " AND ";
            }
        }

        //And add the inner join condition here (on ensat_id and center_id)  
        //Running some kind of chain-linking algorithm here        
        if (tableNum > 1 && conditionNum != 0) {
            sql += " AND ";
        }
        String lastTableIn = "";
        for (int i = 0; i < tableNum; i++) {
            String tableIn = tables.get(i);
            if (!lastTableIn.equals("")) {
                sql += tableIn + ".ensat_id=" + lastTableIn + ".ensat_id";
                sql += " AND ";
                sql += tableIn + ".center_id=" + lastTableIn + ".center_id ";
                if (i == tableNum - 1) {
                    sql += " ";
                } else {
                    sql += "AND ";
                }
            }
            lastTableIn = tableIn;
        }

        //Add the restriction of the database (add if Identification is present, otherwise table contents will guide it contextually... I think)
        if (tables.contains("Identification")) {
            
            if(!sql.contains(" WHERE ")){
                sql += "WHERE Identification.ensat_database='" + dbn + "' ";
            }else{
                sql += "AND Identification.ensat_database='" + dbn + "' ";
            }
            
        }

        //Finally tail it with the standard ordering clause
        //Assume that we'll have at least one table
        String orderingTable = tables.get(0);
        sql += "ORDER BY " + orderingTable + ".center_id," + orderingTable + ".ensat_id;";

        System.out.println("sql: " + sql);

        return sql;
    }

    public ResultSet runSearchQuery(String searchQuerySql, Connection conn) {

        logger.debug("('" + username + "') - EXECUTING QUERY...");
        logger.debug("searchQuerySql: " + searchQuerySql);

        ResultSet rs = null;
        try {
            PreparedStatement ps = conn.prepareStatement(searchQuerySql);
            //STILL NEED TO SET PARAMETERS HERE...
            rs = ps.executeQuery();

        } catch (Exception e) {
            logger.debug("Error (runSearchQuery): " + e.getMessage());
            System.out.println("Error (runSearchQuery): " + e.getMessage());
        }
        return rs;
    }

    public int getRowCount(ResultSet rs) {
        boolean noQuery = (rs == null);
        int rowCount = 0;
        if (!noQuery) {
            try {
                rs.beforeFirst();
                while (rs.next()) {
                    rowCount++;
                }
            } catch (Exception e) {
                logger.debug("Error (getRowCount): " + e.getMessage());
                System.out.println("Error (getRowCount): " + e.getMessage());
            }
        }

        logger.debug("('" + username + "') - " + rowCount + " records returned from query...");

        return rowCount;
    }

    public String getSearchHeaderInfo(ResultSet rs, int rowCount, SearchResult sr) {

        List<String> parameterInputs = sr.getParameters();
        List<String> conditions = sr.getConditions();
        List<String> comparators = sr.getComparators();

        String outputStr = "";

        outputStr += "<p>";
        outputStr += "There ";
        if (rowCount != 1) {
            outputStr += "are ";
        } else {
            outputStr += "is ";
        }
        outputStr += "<strong>" + rowCount + "</strong> record";
        if (rowCount != 1) {
            outputStr += "s";
        }
        outputStr += " matching the following query:<br/><br/>";
        outputStr += "<table cellpadding=\"5\" border=\"1\">";
        outputStr += "<tr>";
        outputStr += "<th><div align='center'>Parameter</div></th><th><div align='center'>Condition</div></th>";
        outputStr += "<th></th>";
        outputStr += "</tr>";

        for (int i = 0; i < parameterInputs.size(); i++) {

            //Put the comparator separator in here...
            if (i != 0) {
                String comparatorIn = comparators.get(i);
                if (comparatorIn.equals("NOT")) {
                    comparatorIn = "AND NOT";
                } else if (comparatorIn.equals("")) {
                    comparatorIn = "AND";
                }
                outputStr += "<tr>";
                outputStr += "<td colspan='3'><div align='center'>" + comparatorIn + "</div></td>";
                outputStr += "</tr>";
            }

            String paramName = parameterInputs.get(i);
            outputStr += "<tr>";
            outputStr += "<td><div align='center'>" + paramName + "</div></td>";
            outputStr += "<td><div align='center'>" + conditions.get(i) + "</div></td>";
            outputStr += "<td><input checked type='checkbox' name='query_selection_" + paramName + "' /></td>";
            outputStr += "</tr>";
        }
        //}
        outputStr += "</table>";
        outputStr += "</p>";
        return outputStr;
    }

    public String compileAllSearchQuery(String dbn, ServletContext context) {

        String sql = "SELECT * FROM Identification WHERE Identification.ensat_database='" + dbn + "';";
        return sql;
    }

    public String getSummaryInfo(int rowCount) {

        logger.debug("('" + username + "') - Displaying summary information...");

        String outputStr = "";
        outputStr += "<p>";
        outputStr += "There ";
        if (rowCount != 1) {
            outputStr += "are ";
        } else {
            outputStr += "is ";
        }
        outputStr += "<strong>" + rowCount + "</strong> record";
        if (rowCount != 1) {
            outputStr += "s";
        }
        outputStr += " records in the database";
        outputStr += "</p>";
        return outputStr;
    }

    public String getAssocTableRowHtml(Vector<Vector> subTables, String dbid, String dbn) {

        String outputStr = "";
        int subTableNum = subTables.size();

        for (int i = 0; i < subTableNum; i++) {
            Vector<String> subTableIn = subTables.get(i);
            outputStr += "<p><a href=\"./jsp/search/search_result.jsp?dbid=" + dbid + "&dbn=" + dbn + "&modality=" + subTableIn.get(2) + "&mainsearch=listall&showformsearch=1\"><strong>" + subTableIn.get(1) + "</strong></a></p>";
        }
        return outputStr;
    }

    public String getTableHeaderInfo() {

        String outputStr = "";
        outputStr += "<thead>";
        outputStr += "<tr>";
        outputStr += "<th>";
        outputStr += "ENSAT ID";
        outputStr += "</th>";
        outputStr += "<th>";
        outputStr += "Referral Doctor";
        outputStr += "</th>";
        outputStr += "<th>";
        outputStr += "Record Date";
        outputStr += "</th>";
        outputStr += "<th>";
        outputStr += "Date of First Registration";
        outputStr += "</th>";
        outputStr += "<th>";
        outputStr += "Sex";
        outputStr += "</th>";
        outputStr += "<th>";
        outputStr += "Year of Birth";
        outputStr += "</th>";
        outputStr += "<th>";
        outputStr += "Consent Level Obtained";
        outputStr += "</th>";
        outputStr += "<th>";
        outputStr += "</th>";
        outputStr += "<th>";
        outputStr += "</th>";
        outputStr += "</tr>";
        outputStr += "</thead>";
        return outputStr;
    }

    public String getTableBodyInfo(ResultSet rs, String dbid, String dbn, String username, String country, String host, String dbUsername, String password, Connection conn, Connection secConn) {

        String outputStr = "";
        boolean noQuery = (rs == null);

        outputStr += "<tbody>";

        try {
            if (!noQuery) {

                //Reset the resultset (already used to count rows)
                rs.beforeFirst();

                int rowIndexMain = 0;
                while (rs.next()) {
                    String pid = rs.getString(1);
                    String centerid = rs.getString(2);

                    outputStr += "<tr";
                    if (rowIndexMain % 2 == 0) {
                        outputStr += " class=\"parameter-line-double-search\" ";
                    }
                    outputStr += "><td><strong>" + centerid + "-" + pid + "</strong></td><td>"
                            + rs.getString(3) + "<br/>(" + rs.getString(4) + ")</td><td>";

                    for (int j = 0; j < 2; j++) {
                        String recordDate = rs.getString(j + 5);
                        StringTokenizer st = new StringTokenizer(recordDate, "-");
                        String recordDateYear = "";
                        String recordDateMonth = "";
                        String recordDateDay = "";
                        if (st.hasMoreTokens()) {
                            recordDateYear = st.nextToken();
                            recordDateMonth = st.nextToken();
                            recordDateDay = st.nextToken();
                        }
                        String recordDateMonthDisp = "";
                        if (recordDateMonth.equals("01")) {
                            recordDateMonthDisp = "Jan";
                        } else if (recordDateMonth.equals("02")) {
                            recordDateMonthDisp = "Feb";
                        } else if (recordDateMonth.equals("03")) {
                            recordDateMonthDisp = "Mar";
                        } else if (recordDateMonth.equals("04")) {
                            recordDateMonthDisp = "Apr";
                        } else if (recordDateMonth.equals("05")) {
                            recordDateMonthDisp = "May";
                        } else if (recordDateMonth.equals("06")) {
                            recordDateMonthDisp = "Jun";
                        } else if (recordDateMonth.equals("07")) {
                            recordDateMonthDisp = "Jul";
                        } else if (recordDateMonth.equals("08")) {
                            recordDateMonthDisp = "Aug";
                        } else if (recordDateMonth.equals("09")) {
                            recordDateMonthDisp = "Sep";
                        } else if (recordDateMonth.equals("10")) {
                            recordDateMonthDisp = "Oct";
                        } else if (recordDateMonth.equals("11")) {
                            recordDateMonthDisp = "Nov";
                        } else if (recordDateMonth.equals("12")) {
                            recordDateMonthDisp = "Dec";
                        }
                        recordDate = recordDateDay + " " + recordDateMonthDisp + " " + recordDateYear;

                        outputStr += recordDate + "</td><td>";
                    }

                    outputStr += rs.getString(7) + "</td><td>"
                            + rs.getString(8) + "</td><td>";

                    String consentLevel = rs.getString(9);
                    String uploader = rs.getString(10);
                    String uploaderCountry = "";

                    //--- START OF COUNTRY-CHECK CONNECTION LOOP
                    //Do one connection here for the national set - obtain username/country resultset then iterate over this throughout the loop
                    String countryCheckSql = "SELECT username, country FROM User";
                    Class.forName("com.mysql.jdbc.Driver").newInstance();
                    //String connectionURL = "jdbc:mysql://localhost:3306/ensat_security";
                    String connectionURL = "jdbc:mysql://stell-2.rc.melbourne.nectar.org.au:3306/ensat_security";
                    Connection connection_cc = DriverManager.getConnection(connectionURL, "ensat", "ensat_melb)");
                    Statement statement_cc = connection_cc.createStatement();

                    ResultSet rs_country_check = statement_cc.executeQuery(countryCheckSql);
                    List<String> countries = new ArrayList<String>();
                    List<String> usernames = new ArrayList<String>();
                    while (rs_country_check.next()) {
                        String usernameIn = rs_country_check.getString(1);
                        String countryIn = rs_country_check.getString(2);
                        usernames.add(usernameIn);
                        countries.add(countryIn);
                    }

                    rs_country_check.close();
                    statement_cc.close();
                    connection_cc.close();
                    //--- END OF COUNTRY-CHECK CONNECTION LOOP

                    if (consentLevel.equals("National")) {
                        //Check the country of the uploader
                        int uploaderIndex = -1;
                        for (int j = 0; j < usernames.size(); j++) {
                            if (usernames.get(j).equals(uploader)) {
                                uploaderIndex = j;
                            }
                        }
                        uploaderCountry = countries.get(uploaderIndex);
                        outputStr += consentLevel + " (" + uploaderCountry + ")</td>";
                    } else {
                        outputStr += consentLevel + "</td>";
                    }

                    Authz security = new Authz();
                    //security.modifyRecordEditable(username, uploader, pid, centerid, host, dbUsername, password);
                    security.modifyRecordEditable(username, uploader, pid, centerid, conn, secConn);
                    boolean recordEditable = security.getRecordEditable();

                    outputStr += this.secureOutput(dbn, dbid, pid, centerid, consentLevel, uploaderCountry, country, recordEditable);


                    /*
                     * if (recordEditable) { outputStr += "<td><a
                     * href='./jsp/read/detail.jsp?dbid=" + dbid + "&dbn=" + dbn
                     * + "&pid=" + pid + "&centerid=" + centerid +
                     * "'>Detail</a></td>"; outputStr += "<td><a
                     * href='./jsp/delete/delete_view.jsp?dbid=" + dbid +
                     * "&dbn=" + dbn + "&pid=" + pid + "&centerid=" + centerid +
                     * "'>Delete</a></td>"; } else if
                     * (consentLevel.equals("Local")) { outputStr +=
                     * "<td></td>"; outputStr += "<td></td>"; } else if
                     * (consentLevel.equals("National")) { outputStr +=
                     * "<td></td>"; outputStr += "<td></td>"; //} } else {
                     * outputStr += "<td><a href='./jsp/read/readonly.jsp?dbid="
                     * + dbid + "&dbn=" + dbn + "&pid=" + pid + "&centerid=" +
                     * centerid + "'>Detail</a></td>"; outputStr += "<td></td>";
                     }
                     */
                    outputStr += "</tr>";
                }
                rs.close();
            }
        } catch (Exception e) {
            logger.debug("Error (getTableBodyInfo): " + e.getMessage());
            System.out.println("Error (getTableBodyInfo): " + e.getMessage());
        }

        outputStr += "</tbody>";
        return outputStr;
    }

    private String secureOutput(String dbn, String dbid, String pid, String centerid, String consentLevel, String uploaderCountry, String country, boolean recordEditable) {

        String outputStr = "";
        if (recordEditable) {

            //Extra column 1
            outputStr += "<td><a href='./jsp/read/detail.jsp?dbid=" + dbid + "&dbn=" + dbn + "&pid=" + pid + "&centerid=" + centerid + "'>Detail</a></td>";

            //Extra column 2
            outputStr += "<td><a href='./jsp/delete/delete_view.jsp?dbid=" + dbid + "&dbn=" + dbn + "&pid=" + pid + "&centerid=" + centerid + "'>Delete</a></td>";

        } else if (consentLevel.equals("Local")) {

            //Extra column 1
            outputStr += "<td></td>";

            //Extra column 2
            outputStr += "<td></td>";

        } else if (consentLevel.equals("National")) {
            if (uploaderCountry.equals(country)) {

                //Extra column 1
                outputStr += "<td><a href='./jsp/read/readonly.jsp?dbid=" + dbid + "&dbn=" + dbn + "&pid=" + pid + "&centerid=" + centerid + "'>Detail</a></td>";

                //Extra column 2
                outputStr += "<td></td>";

            } else {
                //Extra column 1                    
                outputStr += "<td></td>";

                //Extra column 2                    
                outputStr += "<td></td>";
            }
        } else {
            //Extra column 1
            outputStr += "<td><a href='./jsp/read/readonly.jsp?dbid=" + dbid + "&dbn=" + dbn + "&pid=" + pid + "&centerid=" + centerid + "'>Detail</a></td>";

            //Extra column 2
            outputStr += "<td></td>";
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
            tableInfo.add("Pheo_Biomaterial"); //Db tablename
            tableInfo.add("Biomaterial"); //Printed name
            tableInfo.add("biomaterial"); //HTML link name
            subTables.add(tableInfo);
            tableInfo = new Vector<String>();
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
            tableInfo.add("NAPACA_Imaging"); //Db tablename
            tableInfo.add("Imaging Tests"); //Printed name
            tableInfo.add("imaging"); //HTML link name
            subTables.add(tableInfo);
            tableInfo = new Vector<String>();
            tableInfo.add("NAPACA_Surgery"); //Db tablename
            tableInfo.add("Surgery"); //Printed name
            tableInfo.add("surgery"); //HTML link name
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
            tableInfo.add("Imaging Tests"); //Printed name
            tableInfo.add("imaging"); //HTML link name
            subTables.add(tableInfo);
            tableInfo = new Vector<String>();
            tableInfo.add("APA_Cardio"); //Db tablename
            tableInfo.add("Cardiovascular"); //Printed name
            tableInfo.add("cardio"); //HTML link name
            subTables.add(tableInfo);
            tableInfo = new Vector<String>();
            tableInfo.add("APA_Complication"); //Db tablename
            tableInfo.add("Complications"); //Printed name
            tableInfo.add("complications"); //HTML link name
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
        }
        return subTables;
    }

    public String getAssocTableHtml(Vector<Vector> subTables, String dbid, String dbn) {

        String outputStr = "";
        int ROW_SIZE = 1;

        int subTableNum = subTables.size();
        int rowNum = subTableNum / ROW_SIZE;
        int rowNumRemainder = subTableNum % ROW_SIZE;
        if (rowNumRemainder != 0) {
            rowNum++;
        }

        int elemCount = 0;
        for (int i = 0; i < rowNum; i++) {
            if (rowNumRemainder != 0 && i == (rowNum - 1)) {
                outputStr += "<tr>" + this.getAssocTableRowHtml(subTables, dbid, dbn, rowNumRemainder, elemCount) + "</tr>";
                elemCount = elemCount + rowNumRemainder;
            } else {
                outputStr += "<tr>" + this.getAssocTableRowHtml(subTables, dbid, dbn, ROW_SIZE, elemCount) + "</tr>";
                //System.out.println("elemCount: " + elemCount);
                elemCount = elemCount + ROW_SIZE;
            }

        }
        //System.out.println("outputStr (getAssocTableHtml): " + outputStr);

        return outputStr;
    }

    private String getAssocTableRowHtml(Vector<Vector> subTables, String dbid, String dbn, int rowSize, int elemCount) {

        String outputStr = "";
        for (int i = 0; i < rowSize; i++) {
            Vector<String> subTableIn = subTables.get(elemCount + i);
            outputStr += "<td><a href=\"./jsp/search/search_result.jsp?dbid=" + dbid + "&dbn=" + dbn + "&modality=" + subTableIn.get(2) + "&mainsearch=custom&showformsearch=1\"><strong>" + subTableIn.get(1) + "</a></strong></td>";
        }

        //System.out.println("outputStr (getAssocTableRowHtml): " + outputStr);
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

    private Vector<Vector> addCalcFieldValues(Vector<Vector> parameters, HttpServletRequest request, String[] tablenames) {

        //Now try and add any of the calculated fields
        String[] calcParamNames = {"bmi", "ensat_classification"};
        int calcParamNum = calcParamNames.length;
        for (int i = 0; i < calcParamNum; i++) {
            String calcParamValue = request.getParameter(calcParamNames[i]);
            //System.out.println("calcParamNames: " + calcParamNames[i]);
            //System.out.println("calcParamValue: " + calcParamValue);
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

    public Vector<Vector> getSearchParameters(HttpServletRequest request, Connection paramConn) {

        String sql = "";
        sql = "SELECT * FROM Parameter ORDER BY param_order_id;";

        Vector<Vector> parameters = new Vector<Vector>();
        try {
            PreparedStatement ps = paramConn.prepareStatement(sql);
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
                rowCount++;
                parameters.add(rowIn);
            }
        } catch (Exception e) {
            logger.debug("Error (getSearchParameters): " + e.getMessage());
            System.out.println("Error (getSearchParameters): " + e.getMessage());
        }
        return parameters;
    }

    public Vector<String> getSearchParameterTables(Vector<Vector> searchParameters, String dbn) {

        Vector<String> paramTables = new Vector<String>();
        int paramNum = searchParameters.size();

        for (int i = 0; i < paramNum; i++) {
            Vector<String> paramIn = searchParameters.get(i);
            String tableIn = paramIn.get(8);
            if (tableIn.startsWith(dbn) || tableIn.equals("Identification")) {
                if (!paramTables.contains(tableIn)) {
                    paramTables.add(tableIn);
                }
            }
        }
        return paramTables;
    }

    public String getSearchConditionHtml(Vector<Vector> parameters, Vector<Vector> menus, Vector<String> paramTables, String lineColour, Connection conn, Connection ccConn) {

        String outputStr = "";

        int paramTableNum = paramTables.size();

        for (int i = 0; i < paramTableNum; i++) {
            String tablename = paramTables.get(i);
            outputStr += this.getSearchConditionIndividualHtml(tablename, parameters, menus, conn, ccConn);
        }

        return outputStr;
    }

    private String getSearchConditionIndividualHtml(String tablename, Vector<Vector> parameters, Vector<Vector> menus, Connection conn, Connection ccConn) {

        String outputStr = "";

        outputStr += "<p><a href=\"javascript:showHideSearchCondition('" + tablename + "');\">" + this.getSectionTitle(tablename) + "</a><br/></p>";
        outputStr += "<div id=\"search_condition_" + tablename + "\" class=\"hide\">";
        outputStr += "<table border=\"1\" cellpadding=\"5\" width=\"100%\">";

        //Get the number of rows here        
        int paramNum = parameters.size();

        outputStr += "<tr><th colspan='4'>" + this.getSectionTitle(tablename) + "</th></tr>";

        for (int i = 0; i < paramNum; i++) {
            Vector<String> rowIn = parameters.get(i);
            String tableIn = rowIn.get(8);
            String paramType = rowIn.get(2);
            String paramName = rowIn.get(1);
            String menuID = rowIn.get(6);
            if (tableIn.equals(tablename)) {
                outputStr += "<tr>";
                outputStr += "<td>" + rowIn.get(4) + "</td>";
                String boolParamName = "searchboolean_" + tableIn + "." + paramName;
                paramName = "searchcondition_" + tableIn + "." + paramName;                
                outputStr += this.getBooleanMenu(boolParamName);
                outputStr += this.getParameterTypeConditionHtml(paramType, paramName, menuID, menus, conn, ccConn);
                //outputStr += this.getAdditionalParamButton(boolParamName);
                outputStr += "</tr>";
                
                //outputStr += "<tr><td colspan='4'><div id='" + boolParamName + "_extraline'>&nbsp;</div></td></tr>";
            }
        }

        outputStr += "</table>";
        outputStr += "</div>";
        outputStr += "<hr/>";
        return outputStr;
    }
    
    private String getBooleanMenu(String boolParamName){
        
        String outputStr = "";        
        outputStr += "<td><select name='" + boolParamName + "'>";
        //outputStr += "<option value=''>[Select...]</option><option value='NOT'>NOT</option><option value='AND'>AND</option><option value='OR'>OR</option>";
        outputStr += "<option value=''>[Select...]</option><option value='NOT'>NOT</option>";
        outputStr += "</select></td>";        
        return outputStr;
    }
    
    private String getAdditionalParamButton(String boolParamName){
        
        String outputStr = "";        
        outputStr += "<td><a href=\"javascript:showAdditionalParamButton('" + boolParamName + "',1);\">+</a></td>";
        return outputStr;
    }

    private String getSearchParameterIndividualHtml(String tablename, Vector<Vector> parameters) {

        String outputStr = "";

        outputStr += "<p><a href=\"javascript:showHideSearchParameter('" + tablename + "');\">" + this.getSectionTitle(tablename) + "</a><br/></p>";
        outputStr += "<div id=\"search_parameter_" + tablename + "\" class=\"hide\">";
        outputStr += "<table border=\"1\" cellpadding=\"5\" width=\"100%\">";

        //Get the number of rows here        
        int paramNum = parameters.size();

        outputStr += "<tr><th colspan='2'>" + this.getSectionTitle(tablename) + "</th></tr>";

        for (int i = 0; i < paramNum; i++) {
            Vector<String> rowIn = parameters.get(i);
            String tableIn = rowIn.get(8);
            String paramType = rowIn.get(2);
            String paramName = rowIn.get(1);
            if (tableIn.equals(tablename)) {
                outputStr += "<tr>";
                outputStr += "<td>" + rowIn.get(4) + "</td>";
                outputStr += "<td>";
                outputStr += "<input type='checkbox' name='viewparam_" + tablename + "." + paramName + "'/>";
                outputStr += "</td>";
                outputStr += "</tr>";

                if (paramName.equals("weight")) {
                    outputStr += "<tr>";
                    outputStr += "<td>BMI:</td>";
                    outputStr += "<td>";
                    outputStr += "<input type='checkbox' name='viewparam_" + tablename + ".bmi'/>";
                    outputStr += "</td>";
                    outputStr += "</tr>";
                } else if (paramName.equals("other_metastases")) {
                    outputStr += "<tr>";
                    outputStr += "<td>ENSAT Classification:</td>";
                    outputStr += "<td>";
                    outputStr += "<input type='checkbox' name='viewparam_" + tablename + ".ensat_classification'/>";
                    outputStr += "</td>";
                    outputStr += "</tr>";
                }
            }
        }

        outputStr += "</table>";
        outputStr += "</div>";
        outputStr += "<hr/>";
        return outputStr;
    }

    private String getParameterTypeConditionHtml(String paramType, String paramName, String menuID, Vector<Vector> menus, Connection conn, Connection ccConn) {

        //int INPUT_OPTION_NUM = 1;
        String outputStr = "";
        outputStr += "<td>";
        if (paramType.equals("text") || paramType.equals("text_only")) {

            //for(int i=0; i<INPUT_OPTION_NUM; i++){
            String showHideFlag = "show";
            /*if(i == 0){
             showHideFlag = "show";
             }*/
            outputStr += "<div id='" + paramName /*+ "_" + (i + 1)*/ + "_option' class='" + showHideFlag + "'>";
            outputStr += "<input type='text' name='" + paramName /*+ "_" + (i + 1)*/ + "' size='15' onblur='inform=false;' onfocus='inform=true;'/>";
            /*if(i == 0){
             outputStr += "<input type='button' value='+' onclick='showSearchOptions('" + paramName + "')'/>";
             }*/
            outputStr += "</div>";
            //}

        } else if (paramType.equals("menu")) {

            //String menuID = rowIn.get(6);
            boolean menuFound = false;
            int menuNum = menus.size();

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
                /*if (!menuFound) {
             if (rowIn.get(1).equals("center_id")) {
             parentJsStr = "getCenterInfo(this.value);";
             //System.out.println("center_id menu output... HERE");
             int lastMenuNum = menuNum - 2;
             menuIn = menus.get(lastMenuNum);
             //System.out.println("center_id menuIn.size(): " + menuIn.size());
             }else if(rowIn.get(1).equals("associated_studies")){
             int lastMenuNum = menuNum - 1;
             menuIn = menus.get(lastMenuNum);                        
             }
                    
             }*/
                //for(int i=0; i<INPUT_OPTION_NUM; i++){
            String menuSelectStr = "";
            String menuHeaderStr = "";
            /*if (menuIn.get(2).equals("m")) {
                    
                    
                    
             menuHeaderStr += "<div class=\"scroll_checkboxes\">";
             int menuSize = menuIn.size();
             for (int k = 3; k < menuSize; k++) {
             menuSelectStr += "<input name=\"" + paramName + "\" type=\"checkbox\" ";

             menuSelectStr += " value=\"" + menuIn.get(k) + "\" ";
             //}
             menuSelectStr += " />" + menuIn.get(k) + "<br/>";
             }
                    
             } else {*/
            menuHeaderStr += "<select name=\"" + paramName /*+ "_" + (i + 1)*/ + "\">";
            menuSelectStr += "<option value=\"\">[Select...]</option>";
            int menuSize = menuIn.size();
            for (int k = 3; k < menuSize; k++) {
                menuSelectStr += "<option value=\"" + menuIn.get(k) + "\">" + menuIn.get(k) + "</option>";
            }
            //}
            outputStr += menuHeaderStr;
            outputStr += menuSelectStr;

            /*if (menuIn.get(2).equals("m")) {
             outputStr += "</div>";
             }else{*/
            outputStr += "</select>";
                //}

            outputStr += "<br/>";

        } else if (paramType.equals("date")) {
            outputStr += "<input name=\"" + paramName + "_1\" type=\"text\" class=\"datepicker\" id=\"" + paramName + "_id1\" size=\"10\" onfocus=\"inform=true;\" onblur=\"inform=false;\"/>";
            outputStr += " to: ";
            outputStr += "<input name=\"" + paramName + "_2\" type=\"text\" class=\"datepicker\" id=\"" + paramName + "_id2\" size=\"10\" onfocus=\"inform=true;\" onblur=\"inform=false;\"/>";
        } else if (paramType.equals("number")) {
            outputStr += "<input type='text' name='" + paramName + "_1' size='3' onblur='inform=false;' onfocus='inform=true;'/>";
            outputStr += " to: ";
            outputStr += "<input type='text' name='" + paramName + "_2' size='3' onblur='inform=false;' onfocus='inform=true;'/>";
        }

        //Add the dynamic fields here
        if (paramName.equals("searchcondition_Identification.center_id")) {
            outputStr += "" + this.getCenterIds(ccConn);
        } else if (paramName.equals("searchcondition_Identification.associated_studies")) {
            outputStr += "" + this.getAssocStudies(conn);
        }

        outputStr += "</td>";

        //Add the calculated fields here
        if (paramName.equals("searchcondition_ACC_DiagnosticProcedures.weight")) {
            outputStr += "</tr><tr><td>BMI:</td>";
            outputStr += "<td><input type='text' name='searchcondition_ACC_DiagnosticProcedures.bmi_1' size='3' onblur='inform=false;' onfocus='inform=true;'/>";
            outputStr += " to: ";
            outputStr += "<input type='text' name='searchcondition_ACC_DiagnosticProcedures.bmi_2' size='3' onblur='inform=false;' onfocus='inform=true;'/></td>";
        } else if (paramName.equals("searchcondition_ACC_TumorStaging.other_metastases")) {
            outputStr += "</tr><tr><td>ENSAT Classification:</td>";
            outputStr += "<td>";
            outputStr += "<select name=\"searchcondition_ACC_TumorStaging.ensat_classification\">";
            outputStr += "<option value=\"\">[Select...]</option>";
            outputStr += "<option value=\"I\">I</option>";
            outputStr += "<option value=\"II\">II</option>";
            outputStr += "<option value=\"III\">III</option>";
            outputStr += "<option value=\"IV\">IV</option>";
            outputStr += "<option value=\"Not Classified\">Not Classified</option>";
            outputStr += "</select>";
            outputStr += "</td>";
        }

        return outputStr;
    }

    private String getCenterIds(Connection conn) {

        String centerIdMenuOut = "<select name=\"searchcondition_Identification.center_id\">";
        centerIdMenuOut += "<option value=\"\">[Select...]</option>";
        try {
            String sql = "SELECT DISTINCT center_id FROM Center_Callout ORDER BY center_id;";

            PreparedStatement ps = conn.prepareStatement(sql);
            ResultSet rs = ps.executeQuery();
            while (rs.next()) {
                String codeIn = rs.getString(1);
                if (codeIn == null) {
                    codeIn = "";
                }
                centerIdMenuOut += "<option value=\"" + codeIn + "\">" + codeIn + "</option>";
            }
            rs.close();            

        } catch (Exception e) {
            logger.debug("('" + username + "') Error (getDynamicMenus): " + e.getMessage());
        }
        centerIdMenuOut += "</select>";
        return centerIdMenuOut;
    }

    private String getAssocStudies(Connection conn) {

        String centerIdMenuOut = "<select name=\"searchcondition_Identification.associated_studies\">";
        centerIdMenuOut += "<option value=\"\">[Select...]</option>";
        try {            
            String sql = "SELECT DISTINCT study_label FROM Associated_Studies ORDER BY study_label;";

            PreparedStatement ps = conn.prepareStatement(sql);
            ResultSet rs = ps.executeQuery();
            while (rs.next()) {
                String labelIn = rs.getString(1);
                if (labelIn == null) {
                    labelIn = "";
                }
                centerIdMenuOut += "<option value=\"" + labelIn + "\">" + labelIn + "</option>";
            }
            rs.close();            

        } catch (Exception e) {
            logger.debug("('" + username + "') Error (getDynamicMenus): " + e.getMessage());
        }
        centerIdMenuOut += "</select>";
        return centerIdMenuOut;
    }

    public String getSearchParameterHtml(Vector<Vector> parameters, Vector<String> paramTables, String lineColour) {

        String outputStr = "";
        int paramTableNum = paramTables.size();
        for (int i = 0; i < paramTableNum; i++) {
            String tablename = paramTables.get(i);
            outputStr += this.getSearchParameterIndividualHtml(tablename, parameters);
        }
        return outputStr;
    }

    public String getSectionTitle(String tablename) {

        String pageTitle = "";

        if (tablename.equals("Identification")) {
            pageTitle = "Identification";
        } else if (tablename.equals("ACC_Biomaterial")) {
            pageTitle = "Biomaterial";
        } else if (tablename.equals("ACC_FollowUp")) {
            pageTitle = "Follow Up";
        } else if (tablename.equals("ACC_Radiofrequency")) {
            pageTitle = "Radiofrequency";
        } else if (tablename.equals("ACC_Surgery")) {
            pageTitle = "Surgery";
        } else if (tablename.equals("ACC_Mitotane")) {
            pageTitle = "Mitotane";
        } else if (tablename.equals("ACC_Chemotherapy")) {
            pageTitle = "Chemotherapy";
        } else if (tablename.equals("ACC_Radiotherapy")) {
            pageTitle = "Radiotherapy";
        } else if (tablename.equals("ACC_Chemoembolisation")) {
            pageTitle = "Chemoembolisation";
        } else if (tablename.equals("ACC_Pathology")) {
            pageTitle = "Pathology";
        } else if (tablename.equals("ACC_Metabolomics")) {
            pageTitle = "Steroid Metabolomics";
        } else if (tablename.equals("ACC_DiagnosticProcedures")) {
            pageTitle = "Diagnostic Procedures";
        } else if (tablename.equals("ACC_TumorStaging")) {
            pageTitle = "Tumor Staging";
        } else if (tablename.equals("Pheo_Biomaterial")) {
            pageTitle = "Biomaterial";
        } else if (tablename.equals("Pheo_FollowUp")) {
            pageTitle = "Follow Up";
        } else if (tablename.equals("Pheo_ClinicalAssessment")) {
            pageTitle = "Clinical Assessment";
        } else if (tablename.equals("Pheo_BiochemicalAssessment")) {
            pageTitle = "Biochemical Assessment";
        } else if (tablename.equals("Pheo_ImagingTests")) {
            pageTitle = "Imaging Tests";
        } else if (tablename.equals("Pheo_Surgery")) {
            pageTitle = "Surgical Interventions";
        } else if (tablename.equals("Pheo_TumorDetails")) {
            pageTitle = "Tumor Details";
        } else if (tablename.equals("Pheo_NonSurgicalInterventions")) {
            pageTitle = "Non-Surgical Interventions";
        } else if (tablename.equals("Pheo_Genetics")) {
            pageTitle = "Genetics";
        } else if (tablename.equals("Pheo_Other_Genetics")) {
            pageTitle = "Other Genetics";
        } else if (tablename.equals("Pheo_PatientHistory")) {
            pageTitle = "Patient History";
        } else if (tablename.equals("NAPACA_DiagnosticProcedures")) {
            pageTitle = "Diagnostic Procedures";
        } else if (tablename.equals("NAPACA_FollowUp")) {
            pageTitle = "Follow-Up";
        } else if (tablename.equals("NAPACA_Biomaterial")) {
            pageTitle = "Biomaterial";
        } else if (tablename.equals("NAPACA_Imaging")) {
            pageTitle = "Imaging";
        } else if (tablename.equals("NAPACA_Surgery")) {
            pageTitle = "Surgery";
        } else if (tablename.equals("NAPACA_Pathology")) {
            pageTitle = "Pathology";
        } else if (tablename.equals("NAPACA_Metabolomics")) {
            pageTitle = "Metabolomics";
        } else if (tablename.equals("APA_PatientHistory")) {
            pageTitle = "Patient History";
        } else if (tablename.equals("APA_FollowUp")) {
            pageTitle = "Follow-Up";
        } else if (tablename.equals("APA_Complication")) {
            pageTitle = "Complications";
        } else if (tablename.equals("APA_Biomaterial")) {
            pageTitle = "Biomaterial";
        } else if (tablename.equals("APA_Imaging")) {
            pageTitle = "Imaging";
        } else if (tablename.equals("APA_Surgery")) {
            pageTitle = "Surgery";
        } else if (tablename.equals("APA_Cardio")) {
            pageTitle = "Cardiovascular Events";
        } else if (tablename.equals("APA_ClinicalAssessment")) {
            pageTitle = "Clinical Assessment";
        } else if (tablename.equals("APA_BiochemicalAssessment")) {
            pageTitle = "Biochemical Assessment";
        } else if (tablename.equals("APA_Genetics")) {
            pageTitle = "Genetics";
        }
        return pageTitle;
    }

    public void setSearchResults(ResultSet rs) {
        searchResults = rs;
    }

    public ResultSet getSearchResults() {
        return searchResults;
    }

    public void setSearchQuery(String query) {
        searchQuery = query;
    }

    public String getSearchQuery() {
        return searchQuery;
    }

    public Vector<Vector> getCenterDistribution(ResultSet rs) {

        Vector<Vector> centerDistribution = new Vector<Vector>();
        Vector<String> centerIDs = new Vector<String>();
        Vector<Integer> centerCounts = new Vector<Integer>();

        boolean noQuery = (rs == null);
        if (!noQuery) {
            try {

                //Find the column corresponding to the first center_id in a record
                ResultSetMetaData rsmd = rs.getMetaData();
                int columnNum = rsmd.getColumnCount();
                System.out.println("columnNum: " + columnNum);
                boolean centerIdFound = false;
                int colCount = 0;
                while (!centerIdFound && colCount < columnNum) {
                    String labelIn = rsmd.getColumnLabel(colCount + 1);
                    System.out.println("labelIn: " + labelIn);
                    if (labelIn.equals("center_id")) {
                        centerIdFound = true;
                    } else {
                        colCount++;
                    }
                }
                int centerIdIndex = -1;
                if (centerIdFound) {
                    centerIdIndex = colCount + 1;
                }
                System.out.println("centerIdIndex: " + centerIdIndex);

                if (centerIdIndex != -1) {
                    rs.beforeFirst();
                    while (rs.next()) {
                        String centerIdIn = rs.getString(centerIdIndex);
                        if (!centerIDs.contains(centerIdIn)) {
                            centerIDs.add(centerIdIn);
                            centerCounts.add(new Integer(1));
                        } else {
                            //Find the index of the centerID
                            int centerCount = 0;
                            boolean centerFound = false;
                            while (!centerFound && centerCount < centerIDs.size()) {
                                String centerIn = centerIDs.get(centerCount);
                                if (centerIn.equals(centerIdIn)) {
                                    centerFound = true;
                                } else {
                                    centerCount++;
                                }
                            }
                            int centerIndex = -1;
                            if (centerFound) {
                                centerIndex = centerCount;
                            }
                            //Add one to the count of the centerCounts
                            int centerCountInt = centerCounts.get(centerIndex).intValue();
                            centerCountInt++;
                            centerCounts.set(centerIndex, centerCountInt);
                        }
                    }
                }
            } catch (Exception e) {
                logger.debug("Error (getCenterDistribution): " + e.getMessage());
                System.out.println("Error (getCenterDistribution): " + e.getMessage());
            }
        }

        //Feed the IDs and counts into the overall distribution
        for (int i = 0; i < centerIDs.size(); i++) {
            Vector<String> centerInfoIn = new Vector<String>();
            centerInfoIn.add((String) centerIDs.get(i));
            centerInfoIn.add("" + (Integer) centerCounts.get(i));
            if (!centerIDs.get(i).trim().equals("")) {
                centerDistribution.add(centerInfoIn);
            }
        }
        return centerDistribution;
    }

    public Vector<Vector> getCountryDistribution(ResultSet rs) {

        Vector<Vector> countryDistribution = new Vector<Vector>();
        Vector<String> countries = new Vector<String>();
        Vector<Integer> countryCounts = new Vector<Integer>();

        boolean noQuery = (rs == null);
        if (!noQuery) {
            try {

                //Find the column corresponding to the first center_id in a record
                ResultSetMetaData rsmd = rs.getMetaData();
                int columnNum = rsmd.getColumnCount();
                System.out.println("columnNum: " + columnNum);
                boolean centerIdFound = false;
                int colCount = 0;
                while (!centerIdFound && colCount < columnNum) {
                    String labelIn = rsmd.getColumnLabel(colCount + 1);
                    System.out.println("labelIn: " + labelIn);
                    if (labelIn.equals("center_id")) {
                        centerIdFound = true;
                    } else {
                        colCount++;
                    }
                }
                int centerIdIndex = -1;
                if (centerIdFound) {
                    centerIdIndex = colCount + 1;
                }
                System.out.println("centerIdIndex: " + centerIdIndex);

                if (centerIdIndex != -1) {
                    rs.beforeFirst();
                    while (rs.next()) {
                        String centerIdIn = rs.getString(centerIdIndex);
                        if (centerIdIn == null) {
                            centerIdIn = "";
                        }
                        String countryIn = "";
                        if (centerIdIn.length() >= 2) {
                            countryIn = centerIdIn.substring(0, 2);
                        }
                        if (!countries.contains(countryIn)) {
                            countries.add(countryIn);
                            countryCounts.add(new Integer(1));
                        } else {
                            //Find the index of the country
                            int countryCount = 0;
                            boolean countryFound = false;
                            while (!countryFound && countryCount < countries.size()) {
                                String countryInList = countries.get(countryCount);
                                if (countryInList.equals(countryIn)) {
                                    countryFound = true;
                                } else {
                                    countryCount++;
                                }
                            }
                            int countryIndex = -1;
                            if (countryFound) {
                                countryIndex = countryCount;
                            }
                            //Add one to the count of the centerCounts
                            int countryCountInt = countryCounts.get(countryIndex).intValue();
                            countryCountInt++;
                            countryCounts.set(countryIndex, countryCountInt);
                        }
                    }
                }
            } catch (Exception e) {
                logger.debug("Error (getCountryDistribution): " + e.getMessage());
                System.out.println("Error (getCountryDistribution): " + e.getMessage());
            }
        }

        //Feed the IDs and counts into the overall distribution
        for (int i = 0; i < countries.size(); i++) {
            Vector<String> countryInfoIn = new Vector<String>();
            countryInfoIn.add((String) countries.get(i));
            countryInfoIn.add("" + (Integer) countryCounts.get(i));
            if (!countries.get(i).trim().equals("")) {
                countryDistribution.add(countryInfoIn);
            }
        }
        return countryDistribution;
    }
    
    private Vector<Vector> loadStudyAssociations(Connection conn){
        
        Vector<Vector> studyAssociationIDs = new Vector<Vector>();
        
        try{
            String sql = "SELECT center_id,ensat_id,study_name FROM Associated_Studies;";
            PreparedStatement ps = conn.prepareStatement(sql);
            ResultSet rs = ps.executeQuery();
            while(rs.next()){
                Vector<String> studyAssoc = new Vector<String>();
                studyAssoc.add(rs.getString(1));
                studyAssoc.add(rs.getString(2));
                studyAssoc.add(rs.getString(3));
                studyAssociationIDs.add(studyAssoc);
            }
        }catch(Exception e){
            logger.debug("Error (loadStudyAssociations): " + e.getMessage());
        }
        return studyAssociationIDs;
    }

    public String getResultMatrixDetail(ResultSet rs, String pageNum, String dbn, Connection conn) {

        //Load the study associations here (once) first
        Vector<Vector> studyAssociationIds = this.loadStudyAssociations(conn);
        
        int COLUMN_LIMIT = 8;
        int RECORD_DISPLAY_LIMIT = 100;
        int pageNumInt = 1;
        try {
            pageNumInt = Integer.parseInt(pageNum);
        } catch (NumberFormatException nfe) {
            pageNumInt = 1;
        }

        Vector<String> headers = new Vector<String>();
        Vector<Vector> dataIn = new Vector<Vector>();

        Vector<String> idsIn = new Vector<String>();

        logger.debug("username: " + username);
        boolean noQuery = (rs == null);
        if (!noQuery) {
            try {

                //Add the resultset headers in first
                ResultSetMetaData rsmd = rs.getMetaData();
                int columnNum = rsmd.getColumnCount();
                int columnLimit = 1;
                if (columnNum < COLUMN_LIMIT) {
                    columnLimit = columnNum;
                } else {
                    columnLimit = COLUMN_LIMIT;
                }
                for (int i = 0; i < columnLimit; i++) {
                    String headerIn = rsmd.getColumnLabel(i + 1);
                    headers.add(headerIn);
                }

                //Then get the data
                rs.beforeFirst();
                while (rs.next()) {
                    Vector<String> rowIn = new Vector<String>();
                    String idIn = "";
                    for (int i = 0; i < columnNum; i++) {
                        String valueIn = rs.getString(i + 1);
                        if (i == 0) {
                            idIn = valueIn;
                        } else if (i == 1) {
                            idIn += "-" + valueIn;
                        }
                        String dataPointIn = valueIn;
                        if (dataPointIn == null) {
                            dataPointIn = "";
                        }
                        rowIn.add(dataPointIn);
                    }

                    //Use this clause to strip out double-counting and restrict according to username/uploader information
                    if (!idsIn.contains(idIn)) {
                        int uploaderIndex = columnNum - 1;
                        String uploader = rowIn.get(uploaderIndex);
                        String pid = rowIn.get(0);
                        String centerid = rowIn.get(1);
                        boolean addId = this.checkUserPermission(uploader,centerid,pid,studyAssociationIds);
                        if (addId) {                            
                            idsIn.add(idIn);
                            dataIn.add(rowIn);
                        }
                    }
                }
            } catch (Exception e) {
                logger.debug("Error (getResultMatrixDetail): " + e.getMessage());
                //System.out.println("Error (getResultMatrixDetail): " + e.getMessage());
            }
        }

        //Now turn all this into HTML
        String matrixStr = "";

        //Fix up the paging facility first
        int totalRecordNum = dataIn.size();

        if (totalRecordNum != 0) {

            int numberOfPages = totalRecordNum / RECORD_DISPLAY_LIMIT;
            if (totalRecordNum % RECORD_DISPLAY_LIMIT != 0) {
                numberOfPages++;
            }
            matrixStr += "<p>";
            for (int i = 0; i < numberOfPages; i++) {
                if ((i + 1) == pageNumInt) {
                    matrixStr += "" + (i + 1) + " ";
                } else {
                    matrixStr += "<a href=\"./jsp/search/search_view.jsp?dbn=" + dbn + "&page=" + (i + 1) + "\"> " + (i + 1) + "</a> ";
                }
            }
            matrixStr += "</p>";

            matrixStr += "<p><strong>Page " + (pageNumInt) + "</strong></p>";

            matrixStr += "<table border=\"1px\" cellpadding=\"10\">";
            matrixStr += "<tr>";
            for (int i = 0; i < headers.size(); i++) {
                matrixStr += "<th>";
                matrixStr += (String) headers.get(i);
                matrixStr += "</th>";
            }
            matrixStr += "</tr>";

            logger.debug("totalRecordNum: " + totalRecordNum);

            //Modifying the displayed number of records
            int recordNumToDisp = 0;
            if (totalRecordNum < RECORD_DISPLAY_LIMIT) {
                //ONLY ONE PAGE
                recordNumToDisp = totalRecordNum;
            } else if (totalRecordNum % RECORD_DISPLAY_LIMIT != 0) {
                //REMAINDER ON LAST PAGE (NON WHOLE-NUMBER)
                if (pageNumInt == numberOfPages) {
                    recordNumToDisp = totalRecordNum % RECORD_DISPLAY_LIMIT;
                } else {
                    recordNumToDisp = RECORD_DISPLAY_LIMIT;
                }
            } else {
                //ALL INTERMEDIATE OR WHOLE-NUMBER RECORDS
                recordNumToDisp = RECORD_DISPLAY_LIMIT;
            }
            logger.debug("recordNumToDisp: " + recordNumToDisp);

            int dispOffset = (pageNumInt - 1) * RECORD_DISPLAY_LIMIT;
            for (int i = 0; i < recordNumToDisp; i++) {
                int indexToRetrieve = i + dispOffset;
                Vector<String> dataRowIn = dataIn.get(indexToRetrieve);
                int columnLimit = 1;
                if (dataRowIn.size() < COLUMN_LIMIT) {
                    columnLimit = dataRowIn.size();
                } else {
                    columnLimit = COLUMN_LIMIT;
                }
                matrixStr += "<tr>";
                for (int j = 0; j < columnLimit; j++) {
                    matrixStr += "<td>";
                    matrixStr += (String) dataRowIn.get(j);
                    matrixStr += "</td>";
                }
                matrixStr += "</tr>";
            }
            matrixStr += "</table>";

            matrixStr += "<p>";
            for (int i = 0; i < numberOfPages; i++) {
                if ((i + 1) == pageNumInt) {
                    matrixStr += "" + (i + 1) + " ";
                } else {
                    matrixStr += "<a href=\"./jsp/search/search_view.jsp?dbn=" + dbn + "&page=" + (i + 1) + "\"> " + (i + 1) + "</a> ";
                }
            }
            matrixStr += "</p>";

        }else{
            matrixStr += "<strong>No records to show</strong>";
        }

        return matrixStr;
    }
    
    private boolean checkUsernamePermission(String usernameIn){
        
        boolean usernameIsGywu = false;
        usernameIsGywu = usernameIn.equals("haaf_m@ukw.de")
                || usernameIn.equalsIgnoreCase("haaf_m@ukw.de")
                || usernameIn.equalsIgnoreCase("weissmann_d@medizin.uni-wuerzburg.de")
                || usernameIn.equalsIgnoreCase("lang_k@medizin.uni-wuerzburg.de")
                || usernameIn.equalsIgnoreCase("kroiss_m@medizin.uni-wuerzburg.de")
                || usernameIn.equalsIgnoreCase("Deutschbein_T@ukw.de")
                || usernameIn.equalsIgnoreCase("E_Guterman_L@medizin.uni-wuerzburg.de")
                || usernameIn.equalsIgnoreCase("Schirpenba_C@medizin.uni-wuerzburg.de")
                || usernameIn.equalsIgnoreCase("Sbiera_S@medizin.uni-wuerzburg.de")
                || usernameIn.equalsIgnoreCase("Hahner_S@medizin.uni-wuerzburg.de")
                || usernameIn.equalsIgnoreCase("Zink_M@medizin.uni-wuerzburg.de")
                || usernameIn.equalsIgnoreCase("Bala_M@medizin.uni-wuerzburg.de")
                || usernameIn.equalsIgnoreCase("ronchi_c@ukw.de");
        return usernameIsGywu;
    }
    
    private boolean checkUserPermission(String uploader, String centerid, String pid, Vector<Vector> studyAssociationIds){
        
        logger.debug("uploader: " + uploader);
        logger.debug("centerid: " + centerid);
        logger.debug("pid: " + pid);
        
        boolean permitView = false;
        if(uploader.equals(username)){
            permitView = true;
        }else if(centerid.equals("GYWU")){
            boolean usernameIsGywu = this.checkUsernamePermission(username);
            if(usernameIsGywu){
                permitView = true;
            }
        }else{
            int studyAssocNum = studyAssociationIds.size();
            int studyAssocCount = 0;
            while(!permitView && studyAssocCount < studyAssocNum){
                Vector<String> studyAssocIn = studyAssociationIds.get(studyAssocCount);
                
                /*logger.debug("studyAssocIn.get(0): " + studyAssocIn.get(0));
                logger.debug("studyAssocIn.get(1): " + studyAssocIn.get(1));
                logger.debug("studyAssocIn.get(2): " + studyAssocIn.get(2));*/
                                
                if(studyAssocIn.get(0).equals(centerid) && studyAssocIn.get(1).equals(pid)){
                    logger.debug("studyAssocIn.get(2): " + studyAssocIn.get(2));
                    if(studyAssocIn.get(2).equals("adiuvo") && username.equals("oncotrial.sanluigi@gmail.com")){
                        permitView = true;
                    }else if(studyAssocIn.get(2).equals("adiuvo_observational") && username.equals("oncotrial.sanluigi@gmail.com")){
                        permitView = true;
                    }else if(studyAssocIn.get(2).equals("mapp_prono") && username.equals("segolene.hescot@u-psud.fr")){
                        permitView = true;
                    }else if(centerid.equals("GYMU") && username.equals("felix.beuschlein@med.uni-muenchen.de")){
                        permitView = true;
                    }else if(centerid.equals("NLNI") && username.equals("dipti.rao@radboudumc.nl")){
                        permitView = true;
                    }/*else if(centerid.equals("GYWU") && usernameIsGywu){
                        permitView = true;
                    }*/
                }
                studyAssocCount++;                
            }
        }                        
        return permitView;        
    }

    public Vector<String> getConditions(HttpServletRequest request, Connection paramConn) {

        Enumeration inputs = request.getParameterNames();
        Vector<String> conditionsIn = new Vector<String>();

        Vector<String> rangeInputsPresent = new Vector<String>();
        Vector<String> rangeValuesPresent = new Vector<String>();

        while (inputs.hasMoreElements()) {
            String input = (String) inputs.nextElement();
            if (input == null) {
                input = "";
            }

            if (input.startsWith("searchcondition_")) {
                String valueIn = request.getParameter(input);
                if (valueIn == null) {
                    valueIn = "";
                }
                valueIn = valueIn.trim();
                if (!valueIn.equals("")) {
                    
                    //If the condition has an input value
                    
                    //Now remove the prefix from the input
                    input = input.substring(16, input.length());
                    logger.debug("input: " + input);
                    
                    //Check the boolean prefix (if it's 'NOT' then add it)
                    boolean notParam = false;
                    String boolValIn = request.getParameter("searchboolean_" + input);
                    if(boolValIn == null){
                        boolValIn = "";
                    }
                    if(!boolValIn.equals("")){
                        notParam = true;
                    }                    

                    //Strip the 1 or 2 if it belongs to a range condition (use this to identify the type only)
                    String tempInput = "";
                    if (input.endsWith("1") || input.endsWith("2")) {
                        tempInput = input.substring(0, input.length() - 2);
                    } else {
                        tempInput = input;
                    }
                    String inputType = this.getInputType(tempInput, paramConn);
                    System.out.println("inputType: " + inputType);
                    String conditionIn = "";
                    if (inputType.equals("date") || inputType.equals("number")) {
                        String rangeConditionIn = this.getSpecificConditionRange(input, valueIn, inputType);
                        rangeInputsPresent.add(input);
                        rangeValuesPresent.add(rangeConditionIn);
                    } else {
                        conditionIn = this.getSpecificConditionSingle(input, valueIn, inputType, notParam);
                        System.out.println("conditionIn (getConditions): " + conditionIn);
                        conditionsIn.add(conditionIn);
                    }
                }
            }
        }

        //Add the range conditions here (once loop is finished and all inputs are accounted for)
        for (int i = 0; i < rangeInputsPresent.size(); i++) {
            String rangeInputIn = rangeInputsPresent.get(i);
            String rangeInputNameStem = rangeInputIn.substring(0, rangeInputIn.length() - 2);
            if (rangeInputIn.endsWith("1")) {
                if (rangeInputsPresent.contains(rangeInputNameStem + "2")) {
                    //Pull out this first condition
                    String rangeConditionIn1 = rangeValuesPresent.get(i);

                    //Search for the other corresponding condition
                    boolean conditionFound = false;
                    int conditionCount = 0;
                    while (!conditionFound && conditionCount < rangeInputsPresent.size()) {
                        String rangeIn = rangeInputsPresent.get(conditionCount);
                        if (rangeIn.equals(rangeInputNameStem + "2")) {
                            conditionFound = true;
                        } else {
                            conditionCount++;
                        }
                    }
                    //Extract the value and add to the first condition
                    String rangeConditionIn2 = rangeValuesPresent.get(conditionCount);
                    String finalRangeCondition = rangeConditionIn1 + " AND " + rangeConditionIn2;
                    conditionsIn.add(finalRangeCondition);
                } else {
                    //Add the singular condition
                    String rangeConditionIn = rangeValuesPresent.get(i);
                    conditionsIn.add(rangeConditionIn);
                }
            } else {
                if (rangeInputsPresent.contains(rangeInputNameStem + "1")) {
                    //Pull out this first condition
                    String rangeConditionIn1 = rangeValuesPresent.get(i);

                    //Search for the other corresponding condition
                    boolean conditionFound = false;
                    int conditionCount = 0;
                    while (!conditionFound && conditionCount < rangeInputsPresent.size()) {
                        String rangeIn = rangeInputsPresent.get(conditionCount);
                        if (rangeIn.equals(rangeInputNameStem + "1")) {
                            conditionFound = true;
                        } else {
                            conditionCount++;
                        }
                    }
                    //Extract the value and add to the first condition
                    String rangeConditionIn2 = rangeValuesPresent.get(conditionCount);
                    String finalRangeCondition = rangeConditionIn1 + " AND " + rangeConditionIn2;
                    conditionsIn.add(finalRangeCondition);
                } else {
                    //Add the singular condition
                    String rangeConditionIn = rangeValuesPresent.get(i);
                    conditionsIn.add(rangeConditionIn);
                }
            }
        }
        return conditionsIn;
    }

    private String getInputType(String input, Connection paramConn) {

        String inputType = "";

        System.out.println("input (getInputType): " + input);
        //Remove the preceding table name
        String tableName = input.substring(0, input.indexOf("."));
        input = input.substring(input.indexOf(".") + 1, input.length());

        //Connect to the parameter database and just get this information here
        try {
            String sql = "SELECT param_type FROM Parameter WHERE param_name=? AND param_table=?;";
            PreparedStatement ps = paramConn.prepareStatement(sql);
            ps.setString(1, input);
            ps.setString(2, tableName);

            ResultSet rs = ps.executeQuery();
            if (rs.next()) {
                inputType = rs.getString(1);
            }
        } catch (Exception e) {
            logger.debug("Error: " + e.getMessage());
            System.out.println("Error: " + e.getMessage());
        }

        if (input.equals("bmi")) {
            return "number";
        } else if (input.equals("ensat_classification")) {
            return "text";
        }

        return inputType;
    }

    private String getSpecificConditionRange(String input, String valueIn, String inputType) {

        if (inputType.equals("date")) {
            valueIn = this.reformatDateValue(valueIn);
        }

        String conditionOut = "";
        if (input.endsWith("1")) {
            //Then we're on run number 1
            input = input.substring(0, input.length() - 2);
            conditionOut = "" + input + " >= '" + valueIn + "'";
        } else {
            input = input.substring(0, input.length() - 2);
            conditionOut = "" + input + " <= '" + valueIn + "'";
        }
        return conditionOut;
    }

    private String getSpecificConditionSingle(String input, String valueIn, String inputType, boolean notParam) {

        String conditionOut = "";
        if (inputType.equals("text")) {
            if(notParam){
                conditionOut = "" + input + " NOT LIKE '%" + valueIn + "%'";
            }else{
                conditionOut = "" + input + " LIKE '%" + valueIn + "%'";
            }            
        } else if (inputType.equals("menu") || inputType.equals("dynamicmenuonload")) {
            if(notParam){
                conditionOut = "" + input + "!='" + valueIn + "'";
            }else{
                conditionOut = "" + input + "='" + valueIn + "'";
            }            
        } else {
            conditionOut = "";
        }
        return conditionOut;
    }

    public void setConditions(Vector<String> _conditions) {
        conditions = _conditions;
    }

    public Vector<String> getViewParameters(HttpServletRequest request) {

        Enumeration inputs = request.getParameterNames();
        Vector<String> viewParamsIn = new Vector<String>();

        while (inputs.hasMoreElements()) {
            String input = (String) inputs.nextElement();
            if (input == null) {
                input = "";
            }
            if (input.startsWith("viewparam_")) {
                String valueIn = request.getParameter(input);
                if (valueIn == null) {
                    valueIn = "";
                }
                valueIn = valueIn.trim();
                if (valueIn.equals("on")) {
                    //Trim the prefix off the input
                    input = input.substring(10, input.length());
                    viewParamsIn.add(input);
                }
            }
        }

        //Going to manually add the center_id and ensat_id here (for whatever table is present)
        //Extract the first table found
        String paramIn = viewParamsIn.get(0);
        String tableIn = paramIn.substring(0, paramIn.indexOf("."));

        Vector<String> viewParamsOut = new Vector<String>();
        viewParamsOut.add(tableIn + ".ensat_id");
        viewParamsOut.add(tableIn + ".center_id");
        for (int i = 0; i < viewParamsIn.size(); i++) {
            viewParamsOut.add(viewParamsIn.get(i));
        }

        //Adding the uploader restriction manually here - hopefully this will automatically add the Identification table into everything
        String uploaderParam = "Identification.uploader";
        viewParamsOut.add(uploaderParam);

        return viewParamsOut;
    }

    public void setViewParameters(Vector<String> _viewParams) {
        viewParams = _viewParams;
    }

    public String getSearchQuerySummary() {

        String querySummaryStr = "";

        querySummaryStr += "<table cellpadding=\"10\">";
        querySummaryStr += "<tr>";
        querySummaryStr += "<td valign=\"top\">";

        querySummaryStr += "<table border=\"1px\" cellpadding=\"10\">";
        querySummaryStr += "<tr>";
        querySummaryStr += "<th>";
        querySummaryStr += "Parameters to view";
        querySummaryStr += "</th>";
        querySummaryStr += "</tr>";
        for (int i = 0; i < viewParams.size(); i++) {
            querySummaryStr += "<tr>";
            querySummaryStr += "<td>";
            querySummaryStr += "" + (String) viewParams.get(i);
            querySummaryStr += "</td>";
            querySummaryStr += "</tr>";
        }
        querySummaryStr += "</table>";

        querySummaryStr += "</td>";
        querySummaryStr += "<td valign=\"top\">";

        querySummaryStr += "<table border=\"1px\" cellpadding=\"10\">";
        querySummaryStr += "<tr>";
        querySummaryStr += "<th>";
        querySummaryStr += "Conditions";
        querySummaryStr += "</th>";
        querySummaryStr += "</tr>";
        for (int i = 0; i < conditions.size(); i++) {
            querySummaryStr += "<tr>";
            querySummaryStr += "<td>";
            querySummaryStr += "" + (String) conditions.get(i);
            querySummaryStr += "</td>";
            querySummaryStr += "</tr>";
        }
        querySummaryStr += "</table>";

        querySummaryStr += "</td>";
        querySummaryStr += "</tr>";
        querySummaryStr += "</table>";

        return querySummaryStr;
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

    private boolean getCondIsMultiple(String conditionIn) {

        boolean condIsMultiple = false;
        int dotHyphen = conditionIn.indexOf(".");
        int equalsHyphen = conditionIn.indexOf("=");
        if(equalsHyphen == -1){
            equalsHyphen = conditionIn.indexOf(" LIKE");
        }                
        String condStr = conditionIn.substring(dotHyphen + 1, equalsHyphen);

        if (condStr.equals("associated_studies")
                || condStr.equals("chemotherapy_regimen")
                || condStr.equals("followup_organs")
                || condStr.equals("radiofrequency_location")
                || condStr.equals("radiotherapy_location")
                || condStr.equals("surgery_extended")
                || condStr.equals("surgery_first")
                || condStr.equals("biomaterial_normal_tissue")
                || /*condStr.equals("pheo_biomaterial_normal_tissue") ||                
                 condStr.equals("napaca_biomaterial_normal_tissue") ||
                 condStr.equals("apa_biomaterial_normal_tissue") ||*/ condStr.equals("location")
                || /*condStr.equals("imaging_ctloc") ||
                 condStr.equals("imaging_nmrloc") ||*/ condStr.equals("preop")
                || condStr.equals("intraop")) {
            condIsMultiple = true;
        }

        return condIsMultiple;
    }

    private String getCondMultipleValue(String conditionIn) {

        if (conditionIn == null) {
            conditionIn = "";
        }
        conditionIn = conditionIn.trim();
        int equalsHyphen = conditionIn.indexOf("=");
        String condStrVal = conditionIn.substring(equalsHyphen + 2, conditionIn.length() - 1);
        return condStrVal;
    }

    private String getCondMultipleTable(String conditionIn) {

        String condMultipleTable = "";
        int dotHyphen = conditionIn.indexOf(".");
        int equalsHyphen = conditionIn.indexOf("=");
        String condStr = conditionIn.substring(dotHyphen + 1, equalsHyphen);

        if (condStr.equals("associated_studies")) {
            condMultipleTable = "Associated_Studies";
        } else if (condStr.equals("chemotherapy_regimen")) {
            condMultipleTable = "ACC_Chemotherapy_Regimen";
        } else if (condStr.equals("followup_organs")) {
            condMultipleTable = "ACC_Followup_Organs";
        } else if (condStr.equals("radiofrequency_location")) {
            condMultipleTable = "ACC_Radiofrequency_Loc";
        } else if (condStr.equals("radiotherapy_location")) {
            condMultipleTable = "ACC_Radiotherapy_Loc";
        } else if (condStr.equals("surgery_extended")) {
            condMultipleTable = "ACC_Surgery_Extended";
        } else if (condStr.equals("surgery_first")) {
            condMultipleTable = "ACC_Surgery_First";
        } else if (condStr.equals("acc_biomaterial_normal_tissue")) {
            condMultipleTable = "ACC_Biomaterial_Normal_Tissue";
        } else if (condStr.equals("pheo_biomaterial_normal_tissue")) {
            condMultipleTable = "Pheo_Biomaterial_Normal_Tissue";
        } else if (condStr.equals("napaca_biomaterial_normal_tissue")) {
            condMultipleTable = "NAPACA_Biomaterial_Normal_Tissue";
        } else if (condStr.equals("apa_biomaterial_normal_tissue")) {
            condMultipleTable = "APA_Biomaterial_Normal_Tissue";
        } else if (condStr.equals("metastases_loc")) {
            condMultipleTable = "Pheo_MetastasesLocation";
        } else if (condStr.equals("imaging_ctloc")) {
            condMultipleTable = "Pheo_ImagingTests_CTLoc";
        } else if (condStr.equals("imaging_nmrloc")) {
            condMultipleTable = "Pheo_ImagingTests_NMRLoc";
        } else if (condStr.equals("surgery_preop")) {
            condMultipleTable = "Pheo_Surgery_PreOp";
        } else if (condStr.equals("surgery_intraop")) {
            condMultipleTable = "Pheo_Surgery_IntraOp";
        }

        return condMultipleTable;
    }

    private String getCondMultipleDescriptor(String conditionIn) {

        String condMultipleDesc = "";
        int dotHyphen = conditionIn.indexOf(".");
        int equalsHyphen = conditionIn.indexOf("=");
        String condStr = conditionIn.substring(dotHyphen + 1, equalsHyphen);

        if (condStr.equals("associated_studies")) {
            condMultipleDesc = "study_label";
        } else if (condStr.equals("chemotherapy_regimen")) {
            condMultipleDesc = "chemotherapy_regimen";
        } else if (condStr.equals("followup_organs")) {
            condMultipleDesc = "followup_organs";
        } else if (condStr.equals("radiofrequency_location")) {
            condMultipleDesc = "radiofrequency_location";
        } else if (condStr.equals("radiotherapy_location")) {
            condMultipleDesc = "radiotherapy_location";
        } else if (condStr.equals("surgery_extended")) {
            condMultipleDesc = "surgery_extended";
        } else if (condStr.equals("surgery_first")) {
            condMultipleDesc = "surgery_first";
        } else if (condStr.equals("acc_biomaterial_normal_tissue")) {
            condMultipleDesc = "normal_tissue_specific";
        } else if (condStr.equals("pheo_biomaterial_normal_tissue")) {
            condMultipleDesc = "normal_tissue_specific";
        } else if (condStr.equals("napaca_biomaterial_normal_tissue")) {
            condMultipleDesc = "normal_tissue_specific";
        } else if (condStr.equals("apa_biomaterial_normal_tissue")) {
            condMultipleDesc = "normal_tissue_specific";
        } else if (condStr.equals("metastases_loc")) {
            condMultipleDesc = "location";
        } else if (condStr.equals("imaging_ctloc")) {
            condMultipleDesc = "location";
        } else if (condStr.equals("imaging_nmrloc")) {
            condMultipleDesc = "location";
        } else if (condStr.equals("surgery_preop")) {
            condMultipleDesc = "preop";
        } else if (condStr.equals("surgery_intraop")) {
            condMultipleDesc = "intraop";
        }

        return condMultipleDesc;

    }

}
