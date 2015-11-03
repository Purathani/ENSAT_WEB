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
import java.util.Random;
import javax.servlet.ServletContext;

import javax.servlet.http.HttpServletRequest;

import org.apache.log4j.Level;
import org.apache.log4j.Logger;
import org.apache.log4j.PropertyConfigurator;

/**
 *
 * @author astell
 */
public class CreateSub extends Create {

    private static final Logger logger = Logger.getLogger(CreateSub.class);
    private String username = "";

    public CreateSub() {
    }

    public void setUsername(String _username) {
        username = _username;
    }

    @Override
    public String getParameterHtml(Vector<Vector> parameters, Vector<Vector> menus, String lineColour, String dbn, Connection conn, String baseUrl) {

        String outputStr = "";

        //Get the number of rows here        
        int paramNum = parameters.size();

        //logger.debug("Gets into getParameterHtml...");
        //System.out.println("paramNum: " + paramNum);

        //For each row
        for (int i = 0; i < paramNum; i++) {

            Vector<String> rowIn = parameters.get(i);
            String paramName = rowIn.get(1);
            String paramValue = rowIn.get(10);

            //System.out.println("paramName: " + paramName);
            //String tableName = rowIn.get(8);

            //Check if the parameter is a parent node
            boolean parentParam = false;
            Vector<Vector> childParameters = super.checkHiddenParams(parameters, rowIn);
            parentParam = !(childParameters.isEmpty());
            String parentJsStr = "";
            String aliquotJsStr = "";
            String studyJsStr = "";
            String parentHtmlStr = "";


            if (this.getAliquotParameter(paramName)) {
                //System.out.println("" + paramName + " is an aliquot parameter: " + this.getAliquotParameter(paramName));
                aliquotJsStr = "showHide('aliquot_" + paramName + "_options',this.value);";
                parentJsStr += aliquotJsStr;
                //String freezerJsStr = "showFreezerInfo('" + paramName + "',this.value);";
                String freezerJsStr = "showFreezerInfo('" + paramName + "',1,this.value);";
                parentJsStr += freezerJsStr;
            }

            if (paramName.equals("associated_study")) {
                studyJsStr = "study_selection(this.value,'" + dbn + "','" + baseUrl + "');getAssocStudy('" + baseUrl + "',this.value);";
                //System.out.println("studyJsStr: " + studyJsStr);
                parentJsStr += studyJsStr;
            }
            
            //logger.debug("parameter: " + paramName);

            if (parentParam) {

                //System.out.println("childParameters.size(): " + childParameters.size());
                String childParameterHtml = this.getChildParameterHtml(parameters, childParameters, menus, lineColour, i, parentJsStr, parentHtmlStr, dbn, false, false, baseUrl);
                //logger.debug("childParameterHtml: " + childParameterHtml);
                if (!paramName.equals("associated_study")) {
                    parentJsStr += "showHide('myDiv_" + paramName + "_options',this.value);";
                }
                parentHtmlStr = "</td>";
                
                /*if(paramName.equals("ct_delay_contrast_washout")){
                    logger.debug("parentJsStr: " + parentJsStr);
                }*/
                
                /*
                 * if (this.getAliquotParameter(paramName)) { parentHtmlStr +=
                 * "<td></td>"; }
                 */

                /*
                 * if (this.getMultipleHiddenMenuType(paramName)) { parentJsStr
                 * += "showHide('myDiv_" + paramName +
                 * "_2_options',this.value);";
                }
                 */

                String showHideFlag = "";
                if (paramValue.equals("Yes")) {
                    showHideFlag = "show";
                } else {
                    showHideFlag = "hide";
                }
                parentHtmlStr += "</tr><tr><td colspan='2'>" + "<div id=\"myDiv_" + paramName + "_options\" class=\"" + showHideFlag + "\">" + childParameterHtml + "</div>";
            }

            boolean subFlag = !rowIn.get(7).equals("0");
            boolean calledFromMain = true;
            outputStr += this.getIndividualParameterHtml(rowIn, menus, lineColour, i, parentJsStr, parentHtmlStr, subFlag, calledFromMain, dbn, baseUrl);

            //USING THE ALIQUOT DELIMITER TO TRIGGER FREEZER INFO AS WELL
            if (this.getAliquotParameter(paramName)) {                
                outputStr += "<tr>";
                outputStr += "<td colspan='3'>";
                outputStr += this.getFreezerInfo(paramName,false);
                outputStr += "</td>";
                outputStr += "</tr>";                
            } else if (this.getNormalTissueParameter(paramName)) {

                /*String[] normalTissueParamLabels = {"adjacentadrenal", "kidney", "liver", "lung", "lymphnode", "fatperiadrenal", "fatsubcutaneous", "others"};
                String[] normalTissueLabels = {"Adjacent Adrenal", "Kidney", "Liver", "Lung", "Lymph Node", "Fat (Periadrenal)", "Fat (Subcutaneous)", "Others"};
                int normalTissueOptionNum = normalTissueLabels.length;
                for (int k = 0; k < normalTissueOptionNum; k++) {
                    String innerParamName = paramName + "_" + normalTissueParamLabels[k];
                    String innerParamLabel = normalTissueLabels[k];
                    outputStr += "<tr>";
                    outputStr += "<th colspan='3'>";
                    outputStr += "<div id='" + innerParamName + "_showhide' class='hide'>";
                    outputStr += innerParamLabel;
                    outputStr += " ";
                    outputStr += this.getAliquotMenu(innerParamName);
                    outputStr += "</div>";
                    outputStr += "</th>";
                    outputStr += "</tr>";*/

                    //for (int j = 0; j < 9; j++) {
                        outputStr += "<tr>";
                        outputStr += "<td colspan='3'>";
                        //outputStr += this.getFreezerInfo(innerParamName, j + 1);
                        //outputStr += this.getFreezerInfo(innerParamName);
                        outputStr += this.getFreezerInfo(paramName,true);
                        outputStr += "</td>";
                        outputStr += "</tr>";
                    //}

                //}
            }
        }
        return outputStr;

    }

    private String getChildParameterHtml(Vector<Vector> parameters, Vector<Vector> childParameters, Vector<Vector> menus, String lineColour, int index, String parentJsStr, String parentHtmlStr, String dbn, boolean subFlag, boolean calledFromSub, String baseUrl) {

        String outputStr = "";

        //Get the number of rows here        
        int paramNum = childParameters.size();

        //subflag is always false, as we are rendering the child parameters in this call
        //ACTUALLY NO: if the parameter is a sub-sub parameter then we need to note that later in the process (so that it doesn't re-render one level up)
        /*
         * boolean subFlag = false;
         */

        //calledFromMain is a formatting thing: removes the table brackets for all sub and sub-sub parameters
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
                String childChildParameterHtml = this.getChildParameterHtml(parameters, childChildParameters, menus, lineColour, i, parentJsStr, parentHtmlStr, dbn, subFlag, true, baseUrl);
                //logger.debug("childChildParameterHtml: " + childChildParameterHtml);                
                
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

            //Blank this out for child parameters with aliquot menus (i.e. 24h_urine_vol)                
            if (rowIn.get(1).equals("24h_urine_vol")
                    || rowIn.get(1).equals("tumor_tissue_ensat_sop")) {
                parentJsStr = "";
            }

            //Check that the parameter isn't a child of a child
            //THIS IS WRONG...
            //System.out.println("paramName (childParameters): " + paramName);
            //System.out.println("childChildParameters.size(): " + childChildParameters.size());                
            /*
             * if (paramName.equals("disease_state")) {
             * //logger.debug("calledFromSub: " + calledFromSub); subFlag =
             * !calledFromSub;
            }
             */
            outputStr += this.getIndividualParameterHtml(rowIn, menus, lineColour, i, parentJsStr, parentHtmlStr, subFlag, calledFromMain, dbn, baseUrl);


            //MAJOR HACK FOR THE PURPOSES OF MULTIPLE TARGET HIDDEN MENUS
            /*
             * if(paramName.equals("disease_state")){ outputStr += "</div><div
             * id='myDiv_alive_2_options'>TEST"; }
             */
        }
        return outputStr;
    }

    private String getIndividualParameterHtml(Vector<String> rowIn, Vector<Vector> menus, String lineColour, int index, String parentJsStr, String parentHtmlStr, boolean subFlag, boolean calledFromMain, String dbn, String baseUrl) {

        String paramName = rowIn.get(1);
        String paramOptional = rowIn.get(9);
        String tableName = rowIn.get(8);

        //System.out.println("paramName: " + paramName);

        String outputStr = "";
        //Exceptional addition which needs to be generalised to all multiple output parent fields
        if (paramName.equals("alive")) {
            parentJsStr += "showHide('myDiv_alive_options_2',this.value);";
        } else if (paramName.equals("date_of_death")) {
            outputStr += "<tr><td colspan='2'><div id=\"myDiv_alive_options_2\" class=\"hide\">";
        }

        //Case for an empty value vector here
        String valueIn = rowIn.get(10);

        //List the parameters that should not be rendered here
        boolean exception = false;
        exception = paramName.equals("local_investigator")
                || paramName.equals("investigator_email");

        //System.out.println("exception: " + exception);

        if (!exception) {

            //Get the number of menus here
            int menuNum = menus.size();

            //String outputStr = "";



            //If the parameter is a child, don't render it
            if (subFlag) {
                //System.out.println("" + paramName + " fails to render...");
                return outputStr;
            }

            //Run another encapsulating div here for those that have multiple selections based on input from parent
            if (this.getMultipleHiddenMenuType(paramName)) {
                outputStr += "<div id='" + paramName + "_mult' class='hide'>";
            }

            if (!calledFromMain) {
                outputStr += "<table width=\"100%\">";
            }

            //Exceptional Pheo_Genetics headings in here
            if (tableName.equals("Pheo_Genetics")) {
                if (paramName.contains("_testing_performed")) {
                    outputStr += this.addGeneticHeaders(paramName);
                }
            }

            //Exceptional APA Biochemical headings in here
            if (paramName.equals("spironolactone")) {
                outputStr += "<tr><th colspan='2'>Concomitant Medication</th></tr>";
            } else if (paramName.equals("serum_potassium")) {
                outputStr += "<tr><th colspan='2'>Baseline Evaluation</th></tr>";
            } else if (paramName.equals("post_captopril_aldosterone")) {
                outputStr += "<tr><th colspan='2'>Functional Testing</th></tr>";
            } else if (paramName.equals("aldosterone_right")) {
                outputStr += "<tr><th colspan='2'>Adrenal Vein Sampling</th></tr>";
            } else if (paramName.equals("kcnj5_testing")) {
                outputStr += "<tr><th colspan='2'>KCNJ5</th></tr>";
            } else if (paramName.equals("atp1a1_testing")) {
                outputStr += "<tr><th colspan='2'>ATP1A1</th></tr>";
            } else if (paramName.equals("atp2b3_testing")) {
                outputStr += "<tr><th colspan='2'>ATP2B3</th></tr>";
            } else if (paramName.equals("cacna1d_testing")) {
                outputStr += "<tr><th colspan='2'>CACNA1D</th></tr>";
            }

            //Exceptional Pheo_Biological_Assessment headings in here
             else if (paramName.equals("hemoglobin_results")) {
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

            outputStr += "<tr ";
            if (index % 2 != 0) {
                outputStr += lineColour;
            }
            outputStr += ">";
            outputStr += "<td width=\"50%\">";


            outputStr += rowIn.get(4) + ":";


            outputStr += "</td>";
            outputStr += "<td>";


            //System.out.println("outputStr..." + outputStr);

            String othersJsTrailer = "";

            if (rowIn.get(2).equals("text") || rowIn.get(2).equals("number")) {
                outputStr += "<input name=\"" + rowIn.get(1) + "\" type=\"text\" size=\"" + rowIn.get(3) + "\" value=\"" + valueIn + "\" onfocus=\"inform=true;\" onblur=\"parameterValidate(this.value,this.name," + paramOptional + ",'" + rowIn.get(2) + "','" + baseUrl + "');inform=false;\" onchange=\"" + parentJsStr + "\" /><div id=\"" + rowIn.get(1) + "\"></div>";
            } else if (rowIn.get(2).equals("date")) {

                //Change date format back to European
                valueIn = this.reformatDateValueEuropean(valueIn);

                outputStr += "<input name=\"" + paramName + "\" type=\"text\" class=\"datepicker\" id=\"" + paramName + "_id\" size=\"30\" value=\"" + valueIn + "\" onfocus=\"inform=true;\" onblur=\"inform=false;\" onchange=\"parameterValidate(this.value,this.name," + paramOptional + ",'" + rowIn.get(2) + "');" + parentJsStr + "\" /><div id=\"" + rowIn.get(1) + "\"></div>";

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
                        parentJsStr = "getCenterInfo('" + baseUrl + "',this.value);";
                        //System.out.println("center_id menu output... HERE");
                        int lastMenuNum = menuNum - 1;
                        menuIn = menus.get(lastMenuNum);
                        //System.out.println("center_id menuIn.size(): " + menuIn.size());
                    }/*
                     * else if(rowIn.get(1).equals("associated_study")){
                     * parentJsStr = "study_selection(this.value);";
                     *
                     * }
                     */
                }



                String menuSelectStr = "";
                String menuHeaderStr = "";
                if (rowIn.get(1).equals("associated_study_phase_visit")) {

                    menuHeaderStr += "<div id=\"associated_study_menus\"></div>";
                    menuSelectStr += "";

                } else if (menuIn.get(2).equals("m")) {

                    //A quirk of the data model seems to be that only multiple select menus have "Others" as a free-text extra option... hence I'm putting this in here
                    String othersJs = "dispOthers(this.name,this.value);";
                    othersJsTrailer = "<div id=\"myDiv_" + paramName + "_others\"></div>";

                    //blanking out the parentJsStr for the aliquot problem on the biomaterial form (normal tissue)
                    //WHY DID I DO THIS? (18/10/12)
                    parentJsStr = "";

                    //Adding in the JS trigger for the normal tissue types and options                    
                    if (paramName.equals("normal_tissue_options")
                            || paramName.equals("normal_tissue_paraffin_options")
                            || paramName.equals("normal_tissue_dna_options")) {
                        parentJsStr = "normalTissueAliquotShow(" + paramName + ");";
                    }

                    /*
                     * menuHeaderStr += "<select multiple name=\"" + paramName +
                     * "\" onchange=\"" + othersJs + "\"
                     * onblur=\"parameterValidate(this.value,this.name," +
                     * paramOptional + ",'" + rowIn.get(2) + "');" + parentJsStr
                     * + "\">";
                     *
                     * //Need to tokenize valueIn to an array here (for multiple
                     * options) StringTokenizer st = new
                     * StringTokenizer(valueIn, "|"); int tokenNum =
                     * st.countTokens(); String[] valuesIn = new
                     * String[tokenNum];
                     *
                     * int tokenCount = 0; while (st.hasMoreTokens()) {
                     * valuesIn[tokenCount] = st.nextToken(); tokenCount++; }
                     *
                     * int menuSize = menuIn.size(); for (int k = 3; k <
                     * menuSize; k++) { menuSelectStr += "<option";
                     *
                     * for (int m = 0; m < tokenNum; m++) { if
                     * (valuesIn[m].equals(menuIn.get(k))) { menuSelectStr += "
                     * selected "; } } menuSelectStr += " value=\"" +
                     * menuIn.get(k) + "\">" + menuIn.get(k) + "</option>";
                    }
                     */
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
                        //menuSelectStr += "<input type=\"hidden\" name=\"" + paramName + "\" value=\"off\" />";
                        menuSelectStr += "<input name=\"" + paramName + "\" type=\"checkbox\" ";

                        for (int m = 0; m < tokenNum; m++) {
                            if (valuesIn[m].equals(menuIn.get(k))) {
                                menuSelectStr += " checked ";
                            }
                        }
                        
                        String multFreezerStr = "";
                        if(paramName.equals("normal_tissue_options")
                                || paramName.equals("normal_tissue_paraffin_options")
                                || paramName.equals("normal_tissue_dna_options")){
                            multFreezerStr = "onchange=\"showNormalTissueFreezerHeaderInfo(this.name,this.value,this.checked);\"";
                        }
                        
                        String dispOthersStr = "";
                        if(paramName.equals("chemotherapy_regimen")){
                            dispOthersStr = "onchange=\"dispOthers(this.name,this.value);\"";
                        }
                        
                        menuSelectStr += " value=\"" + menuIn.get(k) + "\" " + multFreezerStr + " " + dispOthersStr + " />" + menuIn.get(k) + "<br/>";
                    }
                } else {
                    
                    //THIS IS A HORRIBLE HACK - NEED TO FIX PROPERLY (something to do with the child parameter also being a parent...)
                    if(paramName.equals("ct_delay_contrast_washout")){
                        //logger.debug("parentJsStr (menu rendering - WRONG): " + parentJsStr);
                        parentJsStr = parentJsStr.substring(parentJsStr.indexOf(";"),parentJsStr.length());
                    }else if(paramName.equals("fdg_pet")){
                        //logger.debug("parentJsStr (menu rendering - WRONG): " + parentJsStr);
                        parentJsStr = "";
                    }

                    menuHeaderStr += "<select name=\"" + paramName + "\" onblur=\"parameterValidate(this.value,this.name," + paramOptional + ",'" + rowIn.get(2) + "');\" onchange=\"" + parentJsStr + "\">";

                    //System.out.println("paramName" + paramName);

                    if (!paramName.equals("associated_study")) {

                        menuSelectStr += "<option value=\"\">[Select...]</option>";
                        int menuSize = menuIn.size();

                        //System.out.println("menuSize: " + menuSize);

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
                            if (n == 0) {
                                menuSelectStr += "selected";
                            }
                            menuSelectStr += " value=\"" + menuItem + "\">" + menuItem + "</option>";
                        }

                    }
                }

                outputStr += menuHeaderStr;
                outputStr += menuSelectStr;

                if (!rowIn.get(1).equals("associated_study_phase_visit")) {
                    if (menuIn.get(2).equals("m")) {
                        outputStr += "</div>";
                    } else {
                        outputStr += "</select>";
                    }

                    /*
                     * if(paramName.equals("chemotherapy_regimen")){ outputStr
                     * += "<div id=\"" + paramName + "_validation\"></div>";
                     * }else{
                     */
                    outputStr += "<div id=\"" + paramName + "\"></div>";
                    //}
                    outputStr += othersJsTrailer;
                }

                if (rowIn.get(1).equals("center_id")) {
                    outputStr += "</td></tr><tr><td colspan='2'><div id=\"center_info\"></div>";
                }/*
                 * else if(rowIn.get(1).equals("associated_study")){ outputStr
                 * += "</td></tr><tr><td colspan='2'><div
                 * id=\"associated_study_menus\"></div>"; }
                 */

                outputStr += parentHtmlStr;

                //System.out.println("individual outputStr: " + outputStr);

            } else if (rowIn.get(2).equals("text_only")) {
                outputStr += "" + valueIn + "<input type='hidden' name='" + paramName + "' value='" + valueIn + "'/>";
            }
            //outputStr += "TEST 1...";
            outputStr += "</td>";

            if (this.getAliquotParameter(paramName)) {
                outputStr += "<td>";
                outputStr += "<div id='aliquot_" + paramName + "_options' class='hide'>";
                outputStr += this.getAliquotMenu(paramName);
                outputStr += "</div>";
                outputStr += "</td>";
            }
            outputStr += "</tr>";

            //This is the "alive" multiple options thing - needs generalised
            //if(paramName.equals("cause_of_death")){
            if (paramName.equals("phpgl_free")) {
                outputStr += "</div></td></tr>";
            }

            if (!calledFromMain) {
                outputStr += "</table>";
            }

            //Run another encapsulating div here for those that have multiple selections based on input from parent
            if (this.getMultipleHiddenMenuType(paramName)) {
                outputStr += "</div>";
            }

            //System.out.println("With menu...");
            return outputStr;
        } else {
            //System.out.println("Without menu...");
            return "";
        }
    }

    private String addGeneticHeaders(String paramName) {
        String outputStr = "";
        String geneName = paramName.substring(0, paramName.indexOf("_"));
        geneName = geneName.toUpperCase();
        outputStr = "<tr><th colspan='2'><div align='center'>" + geneName + "</div></th></tr>";
        return outputStr;
    }

    private String getAliquotMenu(String paramName) {

        String outputStr = "";

        outputStr += "<select name='aliquot_" + paramName + "' ";
        outputStr += "onchange='showFreezerAliquotNumber(this.name,this.value,false,\"\");' >";
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

    public void executeParameterUpdate(int nextId, String tablename, Vector<Vector> parameters, Statement statement, HttpServletRequest request) {

        Vector<String> lastPageParamNames = new Vector<String>();
        Vector<String> lastPageParamValues = new Vector<String>();
        Vector<String> lastPageParamTypes = new Vector<String>();

        int paramSize = parameters.size();

        for (int i = 0; i < paramSize; i++) {
            Vector<String> paramIn = parameters.get(i);
            lastPageParamNames.add(paramIn.get(1));
            lastPageParamTypes.add(paramIn.get(2));
            lastPageParamValues.add(paramIn.get(10));
        }

        /*
         * NEED TO RUN AN ID CHECK FIRST ON THE MULTIPLE TABLES
         *
         * String fdpNextIdCheck = "SELECT * FROM
         * Pheo_FirstDiagnosisPresentation ORDER BY
         * pheo_first_diagnosis_presentation_id DESC"; ResultSet fdpCheckRs =
         * statement.executeQuery(fdpNextIdCheck); String fdpNextId = "0";
         * if(fdpCheckRs.next()){ fdpNextId = fdpCheckRs.getString(1); }
         * fdpCheckRs.close(); int fdpNextIdInt = Integer.parseInt(fdpNextId);
         * fdpNextIdInt++;
         *
         * for(int i=0; i<firstDiagnosisPresentation.length; i++){ String
         * updateSql_ph_fdp = "INSERT INTO Pheo_FirstDiagnosisPresentation
         * VALUES(" + fdpNextIdInt + "," + nextId + ",'" + centerId + "',";
         * updateSql_ph_fdp += "'" + firstDiagnosisPresentation[i] + "');";
         * //testStrOut += updateSql_ph_fdp; int update_ph_fdp =
         * statement.executeUpdate(updateSql_ph_fdp); fdpNextIdInt++; }
         */

        String updateSql = "INSERT INTO " + tablename + " VALUES(" + nextId + ",";

        int paramNum = lastPageParamNames.size();
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
            updateSql += "'" + paramValue + "',";

        }
        updateSql = updateSql.substring(0, updateSql.length() - 1);
        updateSql += ");";


        //logger.debug("=== RECORD FORM CREATED ===");
        try {
            /*
             * logger.debug("Ensat ID: " + centerId + "-" + pid);
             * //logger.debug("Username: " + username); logger.debug("Table: " +
             * tablename); logger.debug(" ------ ");
             */



            int update = statement.executeUpdate(updateSql);
        } catch (Exception e) {
            logger.debug("('" + username + "') Error (executeParameterUpdate): " + e.getMessage());
        }

    }

    //THIS METHOD IS THE INDIVIDUAL FORM CREATION ONE... (HOLDS pid AND centerid AS REFERENCES)
    //public void executeParameterUpdate(int nextId, String tablename, String tableIdName, String pid, String centerid, Vector<Vector> parameters, Statement statement, HttpServletRequest request) {
    public void executeParameterUpdate(int nextId, String tablename, String tableIdName, String pid, String centerid, Vector<Vector> parameters, Connection conn, HttpServletRequest request) {

        Vector<String> lastPageParamNames = new Vector<String>();
        Vector<String> lastPageParamValues = new Vector<String>();
        Vector<String> lastPageParamTypes = new Vector<String>();

        int paramSize = parameters.size();

        for (int i = 0; i < paramSize; i++) {
            Vector<String> paramIn = parameters.get(i);
            lastPageParamNames.add(paramIn.get(1));
            lastPageParamTypes.add(paramIn.get(2));
            lastPageParamValues.add(paramIn.get(10));
        }

        //Check if the table is a multiple parameter one
        boolean multipleSelectTable =
                tablename.equals("ACC_Chemotherapy_Regimen")
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

        //System.out.println("multipleSelectTable: " + multipleSelectTable);

        if (multipleSelectTable) {
            //this.updateMultipleTables(nextId, pid, centerid, tablename, lastPageParamNames, lastPageParamValues, request, statement);
            this.updateMultipleTables(nextId, pid, centerid, tablename, lastPageParamNames, lastPageParamValues, request, conn);
        } else {
            //this.updatePrimaryTables(nextId, pid, centerid, tablename, tableIdName, lastPageParamNames, lastPageParamValues, request, statement);
            this.updatePrimaryTables(nextId, pid, centerid, tablename, tableIdName, lastPageParamNames, lastPageParamTypes, lastPageParamValues, request, conn);
        }
    }

    /*
     * private void updateMultipleTables(int nextId, String pid, String
     * centerid, String tablename, Vector<String> lastPageParamNames,
     * Vector<String> lastPageParamValues, HttpServletRequest request, Statement
     * statement) {
     */
    private void updateMultipleTables(int nextId, String pid, String centerid, String tablename,
            Vector<String> lastPageParamNames, Vector<String> lastPageParamValues,
            HttpServletRequest request, Connection conn) {

        //Clear out the values that are going to be used in the multiple update
        lastPageParamValues = new Vector<String>();

        //Get the parameters from the multiple select parameters
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
        } else if (tablename.equals("ACC_Surgery_Extended")) {
            typeNum = 1;
            paramLabel = new String[typeNum];
            paramLabel[0] = "surgery_extended";
        } else if (tablename.equals("ACC_Surgery_First")) {
            typeNum = 1;
            paramLabel = new String[typeNum];
            paramLabel[0] = "surgery_first";
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
        }*/ else if (tablename.equals("ACC_Biomaterial_Normal_Tissue")
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

        //System.out.println("lastPageParamValues.size(): " + lastPageParamValues.size());
        //System.out.println("lastPageParamTypes.size(): " + lastPageParamTypes.size());

        /*
         * for (int k = 0; k < lastPageParamValues.size(); k++) {
         * System.out.println("lastPageParamValues(" + k + "): " +
         * lastPageParamValues.get(k)); System.out.println("lastPageParamTypes("
         * + k + "): " + lastPageParamTypes.get(k)); }
         */

        //Run an check for the last ID if the table is multiple
        int multipleNextId = 1;
        String idLabel = "";
        if (tablename.equals("ACC_Chemotherapy_Regimen")) {
            idLabel = "acc_chemotherapy_regimen_id";
        } else if (tablename.equals("ACC_FollowUp_Organs")) {
            idLabel = "acc_followup_organs_id";
        } else if (tablename.equals("ACC_Radiofrequency_Loc")) {
            idLabel = "acc_radiofrequency_loc_id";
        } else if (tablename.equals("ACC_Radiotherapy_Loc")) {
            idLabel = "acc_radiotherapy_loc_id";
        } else if (tablename.equals("ACC_Surgery_Extended")) {
            idLabel = "acc_surgery_extended_id";
        } else if (tablename.equals("ACC_Surgery_First")) {
            idLabel = "acc_surgery_first_id";
        } else if (tablename.equals("Pheo_MetastasesLocation")) {
            idLabel = "pheo_metastases_loc_id";
        } else if (tablename.equals("Pheo_ImagingTests_CTLoc")) {
            idLabel = "pheo_imaging_ctloc_id";
        } else if (tablename.equals("Pheo_ImagingTests_NMRLoc")) {
            idLabel = "pheo_imaging_nmrloc_id";
        } else if (tablename.equals("Pheo_Surgery_PreOp")) {
            idLabel = "pheo_surgery_preop_id";
        } else if (tablename.equals("Pheo_Surgery_IntraOp")) {
            idLabel = "pheo_surgery_intraop_id";
        }/* else if (tablename.equals("Pheo_Surgery_Procedure")) {
            idLabel = "pheo_surgery_procedure_id";
        }*/ else if (tablename.equals("ACC_Biomaterial_Normal_Tissue")) {
            idLabel = "acc_biomaterial_normal_tissue_id";
        } else if (tablename.equals("Pheo_Biomaterial_Normal_Tissue")) {
            idLabel = "pheo_biomaterial_normal_tissue_id";
        } else if (tablename.equals("NAPACA_Biomaterial_Normal_Tissue")) {
            idLabel = "napaca_biomaterial_normal_tissue_id";
        } else if (tablename.equals("APA_Biomaterial_Normal_Tissue")) {
            idLabel = "apa_biomaterial_normal_tissue_id";
        }

        //String nextIdCheck = "SELECT " + idLabel + " FROM " + tablename + " WHERE ensat_id=" + pid + " AND center_id='" + centerid + "' ORDER BY " + idLabel + " DESC";
        String nextIdCheck = "SELECT " + idLabel + " FROM " + tablename + " WHERE ensat_id=? AND center_id=? ORDER BY " + idLabel + " DESC";
        try {
            PreparedStatement ps = conn.prepareStatement(nextIdCheck);
            ps.setString(1, pid);
            ps.setString(2, centerid);

            //ResultSet idCheckRs = statement.executeQuery(nextIdCheck);
            ResultSet idCheckRs = ps.executeQuery();
            if (idCheckRs.next()) {
                String multipleNextIdStr = idCheckRs.getString(1);
                multipleNextId = Integer.parseInt(multipleNextIdStr);
                multipleNextId++;
            }
        } catch (Exception e) {
            logger.debug("('" + username + "') Error (updateMultipleTables): " + e.getMessage());
        }

        String overallUpdateSql = "";
        int overallIndexCount = 0;
        for (int n = 0; n < typeNum; n++) {
            String paramValueNumInStr = request.getParameter(paramLabel[n] + "_num");
            int paramValueNumIn = Integer.parseInt(paramValueNumInStr);

            for (int i = 0; i < paramValueNumIn; i++) {
                String updateSql = "INSERT INTO " + tablename + " VALUES(";

                updateSql += "?,";
                updateSql += "?,";
                updateSql += "?,?,";
                if (paramLabel[n].equals("normal_tissue_options")
                        || paramLabel[n].equals("normal_tissue_paraffin_options")
                        || paramLabel[n].equals("normal_tissue_dna_options")) {
                    updateSql += "?,";
                }
                updateSql += "?";

                /*
                 * updateSql += "" + multipleNextId + ","; updateSql += "" +
                 * nextId + ","; updateSql += "" + pid + ",'" + centerid + "',";
                 * if (paramLabel[n].equals("normal_tissue_options") ||
                 * paramLabel[n].equals("normal_tissue_paraffin_options") ||
                 * paramLabel[n].equals("normal_tissue_dna_options")) {
                 * updateSql += "'" + lastPageParamTypes.get(overallIndexCount)
                 * + "',"; } updateSql += "'" +
                 * lastPageParamValues.get(overallIndexCount) + "'";
                 */
                updateSql += ");";

                overallUpdateSql = updateSql;
                try {
                    PreparedStatement ps = conn.prepareStatement(updateSql);
                    ps.setInt(1, multipleNextId);
                    ps.setInt(2, nextId);
                    ps.setString(3, pid);
                    ps.setString(4, centerid);
                    if (paramLabel[n].equals("normal_tissue_options")
                            || paramLabel[n].equals("normal_tissue_paraffin_options")
                            || paramLabel[n].equals("normal_tissue_dna_options")) {
                        //updateSql += "'" + lastPageParamTypes.get(overallIndexCount) + "',";
                        ps.setString(5, lastPageParamTypes.get(overallIndexCount));
                        ps.setString(6, lastPageParamValues.get(overallIndexCount));
                    } else {
                        ps.setString(5, lastPageParamValues.get(overallIndexCount));
                    }

                    //System.out.println("overallUpdateSql: " + overallUpdateSql);
                    //int update = statement.executeUpdate(updateSql);
                    int update = ps.executeUpdate();
                } catch (Exception e) {
                    logger.debug("('" + username + "') Error (updateMultipleTables): " + e.getMessage());
                }
                multipleNextId++;
                overallIndexCount++;
            }
        }
    }

    /*
     * private void updatePrimaryTables(int nextId, String pid, String centerid,
     * String tablename, String tableIdName, Vector<String> lastPageParamNames,
     * Vector<String> lastPageParamValues, HttpServletRequest request, Statement
     * statement) {
     */
    private void updatePrimaryTables(int nextId, String pid, String centerid, String tablename, String tableIdName,
            Vector<String> lastPageParamNames, Vector<String> lastPageParamTypes, Vector<String> lastPageParamValues,
            HttpServletRequest request, Connection conn) {


        //System.out.println("Updating primary table: " + tablename);

        boolean biomaterialTable =
                tablename.equals("ACC_Biomaterial")
                || tablename.equals("Pheo_Biomaterial")
                || tablename.equals("NAPACA_Biomaterial")
                || tablename.equals("APA_Biomaterial");

        //System.out.println("biomaterialTable: " + biomaterialTable);

        String updateSql = "INSERT INTO " + tablename + "(";
        String updateSqlTest = updateSql;

        updateSql += "" + tableIdName + ",ensat_id,center_id,";
        //updateSqlTest += "" + tableIdName + ",ensat_id,center_id,";

        int paramNum = lastPageParamNames.size();
        for (int i = 0; i < paramNum; i++) {

            String paramName = lastPageParamNames.get(i);
            boolean paramException = this.getParamException(tablename, paramName);

            if (!paramException) {
                updateSql += "" + paramName + ",";
                //updateSqlTest += "" + paramName + ",";
            }
        }
        updateSql = updateSql.substring(0, updateSql.length() - 1);
        //updateSqlTest = updateSql.substring(0, updateSql.length() - 1);

        //updateSqlTest += ") VALUES(" + nextId + "," + pid + ",'" + centerid + "',";
        updateSql += ") VALUES(?,?,?,";


        for (int i = 0; i < paramNum; i++) {

            String paramName = lastPageParamNames.get(i);
            String paramValue = lastPageParamValues.get(i);
            String paramType = lastPageParamTypes.get(i);

            paramValue.replaceAll(";", "\\;");
            
            //Run an update on the aliquots table if it's a biomaterial table
            if (biomaterialTable) {
                //this.updateAliquotTable(paramName, paramValue, tablename, nextId, pid, centerid, request, statement);
                this.updateAliquotTable(paramName, paramValue, tablename, nextId, pid, centerid, request, conn);
                this.updateFreezerTable(paramName, paramValue, tablename, nextId, pid, centerid, request, conn);
            }

            //Do number conversions for specific parameter names
            if (paramName.equals("gluco_serum_cortisol_napaca")
                    || paramName.equals("gluco_plasma_acth_napaca")
                    || paramName.equals("gluco_urinary_free_cortisol_napaca")
                    || paramName.equals("other_steroid_17hydroxyprogesterone_napaca")
                    || paramName.equals("other_steroid_serum_dheas_napaca")) {
                paramValue = super.napacaUnitConversion(paramName, paramValue, request);
            }

            boolean paramException = this.getParamException(tablename, paramName);

            if (!paramException) {
                //updateSqlTest += "'" + paramValue + "',";
                updateSql += "?,";
            }
        }
        updateSql = updateSql.substring(0, updateSql.length() - 1);
        //updateSqlTest = updateSql.substring(0, updateSql.length() - 1);
        updateSql += ");";
        //updateSqlTest += ");";

        //System.out.println("updateSqlTest (updatePrimaryTables): " + updateSql);
        logger.debug("=== RECORD FORM CREATED ===");
        try {
            logger.debug("Ensat ID: " + centerid + "-" + pid);
            logger.debug("Username: " + username);
            logger.debug("Table: " + tablename + ", Form ID: " + nextId);
            logger.debug(" ------ ");

            PreparedStatement ps = conn.prepareStatement(updateSql);
            ps.setInt(1, nextId);
            ps.setString(2, pid);
            ps.setString(3, centerid);

            int paramCount = 4;
            for (int i = 0; i < paramNum; i++) {

                String paramName = lastPageParamNames.get(i);
                String paramValue = lastPageParamValues.get(i);
                String paramType = lastPageParamTypes.get(i);

                //Do number conversions for specific parameter names
                if (paramName.equals("gluco_serum_cortisol_napaca")
                        || paramName.equals("gluco_plasma_acth_napaca")
                        || paramName.equals("gluco_urinary_free_cortisol_napaca")
                        || paramName.equals("other_steroid_17hydroxyprogesterone_napaca")
                        || paramName.equals("other_steroid_serum_dheas_napaca")) {
                    paramValue = super.napacaUnitConversion(paramName, paramValue, request);
                }

                boolean paramException = this.getParamException(tablename, paramName);
                if (!paramException) {
                    if (paramType.equals("date")) {
                        paramValue = this.reformatDateValue(paramValue);
                    }

                    ps.setString(paramCount, paramValue);
                    paramCount++;
                }

                logger.debug("" + paramName + ": " + paramValue);
            }

            //int update = statement.executeUpdate(updateSql);
            //System.out.println("updateSqlTest: " + updateSqlTest);
            int update = ps.executeUpdate();
        } catch (Exception e) {
            logger.debug("('" + username + "') Error (updatePrimaryTables): " + e.getMessage());            
        }

        logger.debug("=====");

    }

    private boolean getParamException(String tablename, String paramName) {
        return (tablename.equals("ACC_Chemotherapy") && paramName.equals("chemotherapy_regimen"))
                || (tablename.equals("ACC_FollowUp") && paramName.equals("followup_organs"))
                || (tablename.equals("ACC_Radiofrequency") && paramName.equals("radiofrequency_location"))
                || (tablename.equals("ACC_Radiotherapy") && paramName.equals("radiotherapy_location"))
                || (tablename.equals("ACC_Surgery") && paramName.equals("surgery_extended"))
                || (tablename.equals("ACC_Surgery") && paramName.equals("surgery_first"))
                || (tablename.equals("Pheo_ImagingLocation") && paramName.equals("imaging_location"))
                || (tablename.equals("ACC_Biomaterial") && paramName.equals("normal_tissue_options"))
                || (tablename.equals("Pheo_Biomaterial") && paramName.equals("normal_tissue_options"))
                || (tablename.equals("NAPACA_Biomaterial") && paramName.equals("normal_tissue_options"))
                || (tablename.equals("APA_Biomaterial") && paramName.equals("normal_tissue_options"))
                || (tablename.equals("ACC_Biomaterial") && paramName.equals("normal_tissue_dna_options"))
                || (tablename.equals("Pheo_Biomaterial") && paramName.equals("normal_tissue_dna_options"))
                || (tablename.equals("NAPACA_Biomaterial") && paramName.equals("normal_tissue_dna_options"))
                || (tablename.equals("APA_Biomaterial") && paramName.equals("normal_tissue_dna_options"))
                || (tablename.equals("ACC_Biomaterial") && paramName.equals("normal_tissue_paraffin_options"))
                || (tablename.equals("Pheo_Biomaterial") && paramName.equals("normal_tissue_paraffin_options"))
                || (tablename.equals("NAPACA_Biomaterial") && paramName.equals("normal_tissue_paraffin_options"))
                || (tablename.equals("APA_Biomaterial") && paramName.equals("normal_tissue_paraffin_options"));
    }

    //private void updateAliquotTable(String paramName, String paramValue, String tablename, int nextId, String pid, String centerid, HttpServletRequest request, Statement statement) {
    private void updateAliquotTable(String paramName, String paramValue, String tablename, int nextId, String pid, String centerid, HttpServletRequest request, Connection conn) {

        boolean parameterHasAliquot = this.getAliquotParameter(paramName);
        boolean parameterIsNormalTissue = this.getNormalTissueParameter(paramName);
        if (parameterHasAliquot && paramValue.equals("Yes")) {

            this.runAliquotUpdate(paramName, tablename, nextId, pid, centerid, request, conn);

        }else if(parameterIsNormalTissue){
            
            //Get the number of normal tissue selections
            String normalTissueSelectionNum = request.getParameter("" + paramName + "_num");
            int normalTissueSelection = 0;
            try{
                normalTissueSelection = Integer.parseInt(normalTissueSelectionNum);
            }catch(NumberFormatException nfe){
                logger.debug("'" + username + "' - Error (number parsing - updateAliquotTable): " + nfe.getMessage());
            }
            
            logger.debug("normalTissueSelection (updateAliquotTable): " + normalTissueSelection);
            
            //Figure out what the normal tissue selections are
            String[] normalTissueSelections = new String[normalTissueSelection];
            for(int i=0; i<normalTissueSelection; i++){
                normalTissueSelections[i] = request.getParameter("" + paramName + "_" + (i+1));
                normalTissueSelections[i] = this.getNormalTissueParamLabel(normalTissueSelections[i]);
                String paramNameNormalTissue = paramName + "_" + normalTissueSelections[i];
                logger.debug("paramNameNormalTissue (updateAliquotTable): " + paramNameNormalTissue);
                this.runAliquotUpdate(paramNameNormalTissue, tablename, nextId, pid, centerid, request, conn, i);
            }
        
        }
    }

    
    private void runAliquotUpdate(String paramName, String tablename, int nextId, String pid, String centerid, HttpServletRequest request, Connection conn){
        
        String aliquotSql = "";
        //Get the last ID from the aliquots table
        
            String aliquotIdName = tablename.toLowerCase() + "_aliquot_id";
            //String idCheckSql = "SELECT " + aliquotIdName + " FROM " + tablename + "_Aliquots WHERE ensat_id=" + pid + " AND center_id='" + centerid + "' ORDER BY " + aliquotIdName + " DESC;";
            String idCheckSql = "SELECT " + aliquotIdName + " FROM " + tablename + "_Aliquots WHERE ensat_id=? AND center_id=? ORDER BY " + aliquotIdName + " DESC;";
            String formId = "1";
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
                //System.out.println("formId: " + formId);
            } catch (Exception e) {
                logger.debug("('" + username + "') Error (updateAliquotTable): " + e.getMessage());
            }

            //System.out.println("formId (#2): " + formId);

            String aliquotValue = request.getParameter("aliquot_" + paramName);
            logger.debug("aliquotValue(" + paramName + "): " + aliquotValue);
            String aliquotSqlTest = "INSERT INTO " + tablename + "_Aliquots VALUES(" + formId + "," + nextId + "," + pid + ",'" + centerid + "',";
            aliquotSqlTest += "'" + paramName + "','" + aliquotValue + "');";
            logger.debug("aliquotSqlTest: " + aliquotSqlTest);aliquotSql = "INSERT INTO " + tablename + "_Aliquots VALUES(?,?,?,?,";
            aliquotSql += "?,?);";

            try {
                
                conn.setAutoCommit(false);
                PreparedStatement ps = conn.prepareStatement(aliquotSql);
                ps.setString(1, formId);
                ps.setInt(2, nextId);
                ps.setString(3, pid);
                ps.setString(4, centerid);
                ps.setString(5, paramName);
                ps.setString(6, aliquotValue);

                int updateAliquots = ps.executeUpdate();
                conn.commit();
                //int updateAliquots = statement.executeUpdate(aliquotSql);
                int formIdInt = Integer.parseInt(formId);
                formIdInt = formIdInt + 1;
                formId = "" + formIdInt;

            } catch (Exception e) {
                logger.debug("('" + username + "') Error (updateAliquotTable): " + e.getMessage());
            }
    }
    
    private void runAliquotUpdate(String paramName, String tablename, int nextId, String pid, String centerid, HttpServletRequest request, Connection conn, int index){
        
        logger.debug("index: " + index);
        //THIS INDEX NEEDS TO BE PROTECTED - ONLY INCREMENTS BUT DOESN'T DO IT SEQUENTIALLY
        
        String aliquotSql = "";
        //Get the last ID from the aliquots table
            String aliquotIdName = tablename.toLowerCase() + "_aliquot_id";
            //String idCheckSql = "SELECT " + aliquotIdName + " FROM " + tablename + "_Aliquots WHERE ensat_id=" + pid + " AND center_id='" + centerid + "' ORDER BY " + aliquotIdName + " DESC;";
            String idCheckSql = "SELECT " + aliquotIdName + " FROM " + tablename + "_Aliquots WHERE ensat_id=? AND center_id=? ORDER BY " + aliquotIdName + " DESC;";
            String formId = "1";
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
                formIdInt = formIdInt + 1 + index;
                formId = "" + formIdInt;
                //System.out.println("formId: " + formId);
               
            } catch (Exception e) {
                logger.debug("('" + username + "') Error (updateAliquotTable): " + e.getMessage());
            }
            
            //System.out.println("formId (#2): " + formId);

            String aliquotValue = request.getParameter("aliquot_" + paramName);
            logger.debug("aliquotValue(" + paramName + "): " + aliquotValue);
            String aliquotSqlTest = "INSERT INTO " + tablename + "_Aliquots VALUES(" + formId + "," + nextId + "," + pid + ",'" + centerid + "',";
            aliquotSqlTest += "'" + paramName + "','" + aliquotValue + "');";
            logger.debug("aliquotSqlTest: " + aliquotSqlTest);
            
            aliquotSql = "INSERT INTO " + tablename + "_Aliquots VALUES(?,?,?,?,";
            aliquotSql += "?,?);";

            try {
                
                conn.setAutoCommit(false);
                PreparedStatement ps = conn.prepareStatement(aliquotSql);
                ps.setString(1, formId);
                ps.setInt(2, nextId);
                ps.setString(3, pid);
                ps.setString(4, centerid);
                ps.setString(5, paramName);
                ps.setString(6, aliquotValue);

                int updateAliquots = ps.executeUpdate();
                conn.commit();
                //int updateAliquots = statement.executeUpdate(aliquotSql);
                
            } catch (Exception e) {
                logger.debug("('" + username + "') Error (updateAliquotTable): " + e.getMessage());
            }      
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
            ps.setString(1, tablenames[0]);
            if (tablenames.length > 1) {
                for (int i = 1; i < tablenames.length; i++) {
                    ps.setString((i + 1), tablenames[i]);
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
            logger.debug("('" + username + "') Error (getMainParameters): " + e.getMessage());
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

        //System.out.println("sql: " + sql);

        //PreparedStatement ps = conn.preparedStatement(sql);

        Vector<Vector> parametersOut = new Vector<Vector>();
        try {
            PreparedStatement ps = connValues.prepareStatement(sql);
            ps.setString(1, pid);
            ps.setString(2, centerid);

            //ResultSet rs = stmtValues.executeQuery(sql);
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

        return parametersOut;
    }

    public String getMainParameterHtml(Vector<Vector> parameters, String lineColour, String dbn) {

        String outputStr = "";

        //Get the number of rows here        
        int paramNum = parameters.size();

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
                pageTitle = "Metabolomics";
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
            } else if (modality.equals("surgical_procedures")) {
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
            } else if (modality.equals("complications")) {
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

    public int getNextId(String tablename, String tableIdName, String pid, String centerid, Connection conn) {

        int nextId = 1;

        //String idQuerySql = "SELECT " + tableIdName + " FROM " + tablename + " WHERE ensat_id=" + pid + " AND center_id='" + centerid + "' ORDER BY " + tableIdName + " DESC;";
        String idQuerySql = "SELECT " + tableIdName + " FROM " + tablename + " WHERE ensat_id=? AND center_id=? ORDER BY " + tableIdName + " DESC;";

        try {
            PreparedStatement ps = conn.prepareStatement(idQuerySql);
            ps.setString(1, pid);
            ps.setString(2, centerid);
            ResultSet rs = ps.executeQuery();

            //ResultSet rs = stmt.executeQuery(idQuerySql);
            //System.out.println("idQuerySql: " + idQuerySql);

            if (rs.next()) {
                String idResult = rs.getString(1);
                //System.out.println("idResult: " + idResult);
                int idResultNum = Integer.parseInt(idResult);
                //System.out.println("idResultNum: " + idResultNum);
                nextId = idResultNum + 1;
            }
            rs.close();
            //stmt.close();
        } catch (Exception e) {
            logger.debug("('" + username + "') Error (getNextId): " + e.getMessage());
        }
        //System.out.println("nextId (end of getNextId): " + nextId);

        return nextId;
    }

    public Vector<Vector> setupParameters(String modality, HttpServletRequest request, String centerid, String pid, String modid) {

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
            parameter.add("ACC_Metabolomics"); //param_table
            parameter.add("false"); //param_optional
            String valueIn = request.getParameter("metabolomics_date");
            if (valueIn == null) {
                valueIn = (String) request.getAttribute("metabolomics_date");
            }

            if (valueIn == null) {
                valueIn = "";
            }
            parameter.add(valueIn);
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
            parameter.add("ACC_Metabolomics"); //param_table
            parameter.add("true"); //param_optional
            valueIn = request.getParameter("comment");
            if (valueIn == null) {
                valueIn = (String) request.getAttribute("comment");
            }
            if (valueIn == null) {
                valueIn = "";
            }
            parameter.add(valueIn);
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
            parameter.add("ACC_Metabolomics"); //param_table
            parameter.add("false"); //param_optional
            parameter.add("metabolomics_" + centerid + "_" + pid + "_" + modid);
            parameters.add(parameter);

        }

        return parameters;
    }

    public String getStudyInclusion(String studyName, String ensatId, String centerId, Connection conn) {

        String sql = "SELECT study_name FROM Associated_Studies WHERE ensat_id=? AND center_id=?;";
        boolean inStudy = false;
        try {
            PreparedStatement ps = conn.prepareStatement(sql);
            ps.setString(1, ensatId);
            ps.setString(2, centerId);
            ResultSet rs = ps.executeQuery();
            while (rs.next() && !inStudy) {
                String studyNameIn = rs.getString(1);
                if (studyNameIn == null) {
                    studyNameIn = "";
                }
                inStudy = studyNameIn.equals(studyName);
            }
        } catch (Exception e) {
            logger.debug("('" + username + "') Error (getStudyInclusion): " + e.getMessage());
        }

        if (inStudy) {
            return "Yes";
        } else {
            return "No";
        }
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
    
    public String getFreezerInfo(String paramName, boolean normalTissue) {
        String outputStr = "";        
        if(normalTissue){
            outputStr += "<div id='" + paramName + "_freezer_header_info'>";
        }else{
            outputStr += "<div id='" + paramName + "_freezer_info'>";
        }
        
        outputStr += "</div>";
        return outputStr;
    }
    

    private void updateFreezerTable(String paramName, String paramValue, String tablename, int nextId, String pid, String centerid, HttpServletRequest request, Connection conn) {

        boolean parameterHasAliquot = this.getAliquotParameter(paramName);
        boolean parameterIsNormalTissue = this.getNormalTissueParameter(paramName);

        if (parameterHasAliquot && paramValue.equals("Yes")) {

            this.runFreezerUpdate(paramName, tablename, nextId, pid, centerid, request, conn);

        } else if (parameterIsNormalTissue) {

            //Get the number of normal tissue selections
            String normalTissueSelectionNum = request.getParameter("" + paramName + "_num");
            int normalTissueSelection = 0;
            try {
                normalTissueSelection = Integer.parseInt(normalTissueSelectionNum);
            } catch (NumberFormatException nfe) {
                logger.debug("'" + username + "' - Error (number parsing - updateFreezerTable): " + nfe.getMessage());
            }

            //Figure out what the normal tissue selections are
            String[] normalTissueSelections = new String[normalTissueSelection];
            for (int i = 0; i < normalTissueSelection; i++) {
                normalTissueSelections[i] = request.getParameter("" + paramName + "_" + (i + 1));
                normalTissueSelections[i] = this.getNormalTissueParamLabel(normalTissueSelections[i]);
                String paramNameNormalTissue = paramName + "_" + normalTissueSelections[i];
                this.runFreezerUpdate(paramNameNormalTissue, tablename, nextId, pid, centerid, request, conn);
            }
        }
    }

    private void runFreezerUpdate(String paramName, String tablename, int nextId, String pid, String centerid, HttpServletRequest request, Connection conn) {

        String freezerSql = "";

        //Get the relevant aliquot number
        String aliquotNumber = request.getParameter("aliquot_" + paramName);
        int aliquotNumberInt = 1;
        try {
            aliquotNumberInt = Integer.parseInt(aliquotNumber);
        } catch (NumberFormatException nfe) {
            logger.debug("'" + username + "' - Error (number parsing - runFreezerUpdate): " + nfe.getMessage());
            aliquotNumberInt = 1;
        }

        //Get the last ID from the freezer table
        String locationIdName = tablename.toLowerCase() + "_location_id";
        //String idCheckSql = "SELECT " + locationIdName + " FROM " + tablename + "_Freezer_Information WHERE ensat_id=" + pid + " AND center_id='" + centerid + "' ORDER BY " + locationIdName + " DESC;";
        String idCheckSql = "SELECT " + locationIdName + " FROM " + tablename + "_Freezer_Information WHERE ensat_id=? AND center_id=? ORDER BY " + locationIdName + " DESC;";
        String formId = "0";
        try {
            PreparedStatement ps = conn.prepareStatement(idCheckSql);
            ps.setString(1, pid);
            ps.setString(2, centerid);
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
            freezerSql += "?,?,?,?,?,?,?,?,";
            
            //For each aliquot, generate a random bio_id
            String bioId = this.createRandomBioId();
            
            freezerSql += "?,?,?);";

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

                ps.setString(13, bioId); //Random biomaterial ID                
                ps.setString(14, "No"); //Material used
                ps.setString(15, ""); //Material transferred (center code)
                /*
                 * logger.debug("Freezer #" + (i+1) + ": " + freezerValue);
                 * logger.debug("Rack #" + (i+1) + ": " + rackValue);
                 * logger.debug("Shelf #" + (i+1) + ": " + shelfValue);
                 * logger.debug("Box #" + (i+1) + ": " + boxValue);
                 * logger.debug("Position #" + (i+1) + ": " + positionValue);
                 */

                //logger.debug("freezerSql: " + freezerSql);
                int updateLocation = ps.executeUpdate();
                conn.commit();

                int formIdInt = Integer.parseInt(formId);
                formIdInt = formIdInt + 1;
                formId = "" + formIdInt;

            } catch (Exception e) {
                logger.debug("'" + username + "' - Error (updateFreezerTable): " + e.getMessage());
            }
        }
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
