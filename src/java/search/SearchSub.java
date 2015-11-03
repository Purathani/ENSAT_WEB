package search;

import ConnectBean.ConnectionAuxiliary;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.Statement;
import java.sql.ResultSet;
import java.sql.ResultSetMetaData;
import java.util.ArrayList;
import java.util.List;

import java.util.Vector;
import java.util.StringTokenizer;
import javax.servlet.ServletContext;

import javax.servlet.http.HttpServletRequest;

/**
 *
 * @author astell
 */
public class SearchSub extends Search {

    public SearchSub() {
    }

    /**
     * Note that this method has different arguments from that inherited from Search
     * @param parametersOrig
     * @param conditionsOrig
     * @param comparatorsOrig
     * @param tablesOrig
     * @param request
     * @param parameters
     * @return 
     */
    public SearchResult compileSearchResults(List<String> parametersOrig, List<String> conditionsOrig, List<String> comparatorsOrig,
            List<String> tablesOrig, HttpServletRequest request, Vector<Vector> parameters) {

        SearchResult sr = new SearchResult();

        List<String> parameterInputs = new ArrayList<String>();
        List<String> tables = new ArrayList<String>();
        List<String> conditions = new ArrayList<String>();
        List<String> comparators = new ArrayList<String>();

        int paramNum = parameters.size();

        if (parametersOrig != null) {
            int paramOrigSize = parametersOrig.size();
            for (int i = 0; i < paramOrigSize; i++) {
                String paramInName = parametersOrig.get(i);
                String condInName = conditionsOrig.get(i);
                String comparatorInName = comparatorsOrig.get(i);

                parameterInputs.add(paramInName);
                conditions.add(condInName);
                comparators.add(comparatorInName);
            }

            int tableOrigSize = 0;
            if (tablesOrig != null) {
                tableOrigSize = tablesOrig.size();
            }
            for (int i = 0; i < tableOrigSize; i++) {
                String tableInName = tablesOrig.get(i);
                tables.add(tableInName);
            }
        }

        //System.out.println("paramNum: " + paramNum);
        
        int firstConditionCount = 0;
        for (int i = 0; i < paramNum; i++) {
            Vector<String> paramIn = parameters.get(i);

            //Get the parameter
            String paramName = paramIn.get(1);
            String paramType = paramIn.get(2);
            //System.out.println("paramType: " + paramType);

            //Identify what tables are associated with the parameters that have information and add to the table list
            String tableIn = paramIn.get(8);
            //System.out.println("tableIn (SEARCHSUB): " + tableIn);

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
                if(condInReq != null && condInReq.equals("Others")){
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
                    if(request != null){
                        condIn1 = request.getParameter(paramName + "_1");
                        condIn2 = request.getParameter(paramName + "_2");
                    }

                    if (condIn1 == null) {
                        condIn1 = "";
                    }
                    if (condIn2 == null) {
                        condIn2 = "";
                    }

                } else {
                    String condInMonth1 = "";
                    String condInMonth2 = "";
                    String condInDay1 = "";
                    String condInDay2 = "";
                    String condInYear1 = "";
                    String condInYear2 = "";
                    
                    if(request != null){
                        condInMonth1 = request.getParameter(paramName + "_month_1");
                        condInMonth2 = request.getParameter(paramName + "_month_2");
                        condInDay1 = request.getParameter(paramName + "_day_1");
                        condInDay2 = request.getParameter(paramName + "_day_2");
                        condInYear1 = request.getParameter(paramName + "_year_1");
                        condInYear2 = request.getParameter(paramName + "_year_2");
                    }

                    condIn1 = "" + condInYear1 + "-" + condInMonth1 + "-" + condInDay1;
                    condIn2 = "" + condInYear2 + "-" + condInMonth2 + "-" + condInDay2;

                    if (condIn1 == null || condIn1.equals("--") || condIn1.equals("null-null-null")) {
                        condIn1 = "";
                    }
                    if (condIn2 == null || condIn2.equals("--") || condIn2.equals("null-null-null")) {
                        condIn2 = "";
                    }

                    //System.out.println("condIn1: " + condIn1 + ", condIn2: " + condIn2);
                }

                if (!condIn1.equals("") && !condIn2.equals("")) {
                    conditionIn = tableIn + "." + paramName + " >= " + condIn1 + " AND " + tableIn + "." + paramName + " <= " + condIn2 + "";
                } else if (condIn1.equals("") && !condIn2.equals("")) {
                    conditionIn = tableIn + "." + paramName + " <= " + condIn2;
                } else if (!condIn1.equals("") && condIn2.equals("")) {
                    conditionIn = tableIn + "." + paramName + " >= " + condIn1;
                } else {
                    conditionIn = "";
                }
            }

            //System.out.println("conditionIn (" + i + "): " + conditionIn);

            if (!conditionIn.equals("")) {
                if (!tables.contains(tableIn)) {
                    tables.add(tableIn);
                    //System.out.println("Adding " + tableIn + " to table list...");
                }
            }

            //If condition is not null or blank, add the data to the lists
            if (conditionIn != null && !conditionIn.trim().equals("")) {
                
                //Count the first condition (for the first NOT comparator)
                firstConditionCount++;

                //Qualify the paramName with the table name
                paramName = tableIn + "." + paramName;

                if (!parameterInputs.contains(paramName)) {
                    parameterInputs.add(paramName);
                    conditions.add(conditionIn);
                    comparators.add(comparatorIn);
                }
            }

        }

        sr.setParameters(parameterInputs);
        sr.setConditions(conditions);
        sr.setComparators(comparators);
        sr.setTables(tables);

        return sr;

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
            }
        }else if (dbn.equals("Pheo")) {
            if (modality.equals("biomaterial")) {
                tablenameOut = "Pheo_Biomaterial";
            } else if (modality.equals("clinical")) {
                tablenameOut = "Pheo_ClinicalAssessment";
            } else if (modality.equals("biochemical")) {
                tablenameOut = "Pheo_BiochemicalAssessment";
            } else if (modality.equals("imaging")) {
                tablenameOut = "Pheo_ImagingTests";
            } else if (modality.equals("tumordetails")) {
                tablenameOut = "Pheo_TumorDetails";
            } else if (modality.equals("surgery")) {
                tablenameOut = "Pheo_Surgery";
            } else if (modality.equals("interventions")) {
                tablenameOut = "Pheo_NonSurgicalInterventions";
            } else if (modality.equals("followup")) {
                tablenameOut = "Pheo_FollowUp";
            } else if (modality.equals("genetics")) {
                tablenameOut = "Pheo_Genetics";
            }
        }else if (dbn.equals("NAPACA")) {
            if (modality.equals("biomaterial")) {
                tablenameOut = "NAPACA_Biomaterial";
            } else if (modality.equals("imaging")) {
                tablenameOut = "NAPACA_Imaging";
            } else if (modality.equals("surgery")) {
                tablenameOut = "NAPACA_Surgery";
            } else if (modality.equals("pathology")) {
                tablenameOut = "NAPACA_Pathology";
            } else if (modality.equals("followup")) {
                tablenameOut = "NAPACA_FollowUp";
            }
        }else if (dbn.equals("APA")) {
            if (modality.equals("biomaterial")) {
                tablenameOut = "APA_Biomaterial";
            } else if (modality.equals("imaging")) {
                tablenameOut = "APA_Imaging";
            } else if (modality.equals("surgery")) {
                tablenameOut = "APA_Surgery";
            } else if (modality.equals("clinical")) {
                tablenameOut = "APA_ClinicalAssessment";
            } else if (modality.equals("biochemical")) {
                tablenameOut = "APA_BiochemicalAssessment";
            } else if (modality.equals("cardio")) {
                tablenameOut = "APA_Cardio";
            } else if (modality.equals("complications")) {
                tablenameOut = "APA_Complication";
            } else if (modality.equals("followup")) {
                tablenameOut = "APA_FollowUp";
            }
        }

        return tablenameOut;
    }

    public String getSubTableIdName(String modality, String dbn) {

        String tableIdOut = "";
        if (dbn.equals("ACC")) {
            tableIdOut = dbn.toLowerCase() + "_" + modality + "_id";

        }
        return tableIdOut;
    }
}
