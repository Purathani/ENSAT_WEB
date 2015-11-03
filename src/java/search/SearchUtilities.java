package search;

import ConnectBean.ConnectionAuxiliary;
import com.google.common.collect.ArrayListMultimap;
import com.google.common.collect.LinkedHashMultimap;
import com.google.common.collect.LinkedListMultimap;
import com.google.common.collect.Multimap;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.sql.Connection;
import java.sql.Statement;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.ResultSetMetaData;
import java.sql.SQLException;
import java.util.*;
import org.apache.log4j.Level;
import org.apache.log4j.Logger;
import org.apache.log4j.PropertyConfigurator;
import org.apache.poi.ss.usermodel.*;
import org.apache.poi.ss.util.WorkbookUtil;
import org.apache.poi.xssf.usermodel.XSSFCellStyle;
import org.apache.poi.xssf.usermodel.XSSFSheet;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;

import update_main.Utilities;
import javax.servlet.ServletContext;

/**
 * Holds information relevant to the search query to be passed between pages
 *
 * @author Anthony Stell @copy University of Melbourne, 2012
 */
public class SearchUtilities {

    private static final Logger logger = Logger.getLogger(SearchUtilities.class);
    private String username = "";
    private String context;
    //private ServletContext servContext;
    private String base;
    private String filepath;
    private String filename;
    private String center_id;
    private List<String> tables;
    private List<String> parameters;
    private List<String> calcColumns;
    private List<String> idList;
    private final Set<String> singleEntryTables;
    private final Multimap<String, String> subTableMap;
    private Connection connection;
    private String searchQuerySql;
    private List<String> orderedExportedUsers;
    private final Map<String, Integer> tableMaxEntryCount;
    private List<String> aliquotParameterList = new ArrayList<String>();

    public SearchUtilities() {

        parameters = new ArrayList<String>();

        tables = new ArrayList<String>();
        singleEntryTables = new HashSet<String>();
        singleEntryTables.add("Identification");
        singleEntryTables.add("ACC_DiagnosticProcedures");
        singleEntryTables.add("ACC_TumorStaging");
        singleEntryTables.add("Pheo_PatientHistory");
        singleEntryTables.add("NAPACA_DiagnosticProcedures");
        singleEntryTables.add("APA_PatientHistory");
        singleEntryTables.add("Pheo_OtherOrgans");
        singleEntryTables.add("Pheo_FirstDiagnosisPresentation");

        subTableMap = LinkedHashMultimap.create();

        /*subTableMap.put("ACC_Biomaterial", "ACC_Biomaterial_Aliquots");
        subTableMap.put("ACC_Biomaterial", "ACC_Biomaterial_Normal_Tissue");
        subTableMap.put("ACC_Chemotherapy", "ACC_Chemotherapy_Regimen");
        subTableMap.put("ACC_FollowUp", "ACC_FollowUp_Organs");
        subTableMap.put("ACC_Radiofrequency", "ACC_Radiofrequency_Loc");
        subTableMap.put("ACC_Radiotherapy", "ACC_Radiotherapy_Loc");
        subTableMap.put("ACC_Surgery", "ACC_Surgery_First");
        subTableMap.put("ACC_Surgery", "ACC_Surgery_Extended");

        subTableMap.put("APA_Biomaterial", "APA_Biomaterial_Aliquots");
        subTableMap.put("APA_Biomaterial", "APA_Biomaterial_Normal_Tissue");

        subTableMap.put("NAPACA_Biomaterial", "NAPACA_Biomaterial_Aliquots");
        subTableMap.put("NAPACA_Biomaterial", "NAPACA_Biomaterial_Normal_Tissue");

        subTableMap.put("Pheo_Biomaterial", "Pheo_Biomaterial_Aliquots");
        subTableMap.put("Pheo_Biomaterial", "Pheo_Biomaterial_Normal_Tissue");*/

        tableMaxEntryCount = new HashMap<String, Integer>();

    }
    
    /*public void setServContext(ServletContext _servContext){
        servContext = _servContext;        
    }*/

    public String getSearchQuerySql() {
        return searchQuerySql;
    }

    public void setSearchQuerySql(String _searchQuerySql) {
        searchQuerySql = _searchQuerySql;
    }

    public List<String> getParameters() {
        return parameters;
    }

    public void setParameters(List<String> _parameters) {
        parameters = _parameters;
    }

    public List<String> getTables() {
        return tables;
    }

    public void setTables(List<String> _tables) {
        tables = _tables;
    }

    public void setExportLocation(String base, String context, String filepath, String filename) {
        this.context = context;
        this.base = base;
        this.filepath = filepath;
        this.filename = filename;
    }

    public void setUserDetails(String username, String center_id) {
        this.username = username;
        this.center_id = center_id;
    }

    public void setParametersToExport(String mod, String query, String study, String center, String country, String dbn) {
        parameters = new ArrayList<String>();
        calcColumns = new ArrayList<String>();

        String exportLogMsge = "";
        exportLogMsge += "Setting parameters for ";
        if(mod.equals("1")){
            exportLogMsge += " all patients belonging to user " + username;
        }else if(mod.equals("2")){
            exportLogMsge += " all patients belonging to center " + center;
        }else if(mod.equals("3")){
            exportLogMsge += " all patients belonging to country " + country;
        }else if(mod.equals("4")){
            exportLogMsge += " all patients belonging to type " + dbn;
        }else if(mod.equals("5")){
            exportLogMsge += " all patients subject to the " + query + " query";
        }else if(mod.equals("6")){
            exportLogMsge += " all patients in the " + study + " study";
        }
        
        logger.debug(exportLogMsge + "...");

        //Making this universal so that all exports get the associated studies parameter
        this.setCalcColumnsToExport(mod, query, study);
        if (mod.equals("1")
                || mod.equals("2")
                || mod.equals("3")
                || mod.equals("4")) {
            parameters.add("*");
        } else if (mod.equals("5")) {
            if (query.equals("laterality")) {
                parameters.add("ACC_TumorStaging.site_of_adrenal_tumor");
                parameters.add("Pheo_TumorDetails.tumor_site");
                parameters.add("NAPACA_Imaging.tumor_sites");
                parameters.add("APA_Imaging.tumor_sites_imaging");
            } else if (query.equals("summary_only")) {
                //this.setCalcColumnsToExport(mod, query, study);
            } else if (query.equals("summary_all")) {
                //this.setCalcColumnsToExport(mod, query, study);
                parameters.add("ACC_DiagnosticProcedures.*");
                parameters.add("ACC_TumorStaging.*");
                parameters.add("ACC_Biomaterial.*");
                parameters.add("ACC_Radiofrequency.*");
                parameters.add("ACC_Surgery.*");
                parameters.add("ACC_Pathology.*");
                parameters.add("ACC_Chemotherapy.*");
                parameters.add("ACC_Radiotherapy.*");
                //parameters.add("ACC_FollowUp.*");
                parameters.add("ACC_Chemoembolisation.*");
                parameters.add("ACC_Mitotane.*");
            }else if(query.equals("acc_quickcheck")){
                
                //this.setCalcColumnsToExport(mod, query, study);
                
                parameters.add("ACC_DiagnosticProcedures.symptoms_diag_tumor_mass");
                parameters.add("ACC_DiagnosticProcedures.symptoms_incidental");
                parameters.add("ACC_DiagnosticProcedures.symptoms_paraneoplastic");
                parameters.add("ACC_DiagnosticProcedures.symptoms_endocrine");
                parameters.add("ACC_DiagnosticProcedures.cushings_syndrome");
                parameters.add("ACC_DiagnosticProcedures.virilisation");
                parameters.add("ACC_DiagnosticProcedures.feminization");
                parameters.add("ACC_DiagnosticProcedures.mineralocorticoid_excess");
                
                parameters.add("ACC_TumorStaging.size_of_adrenal_tumor");
                parameters.add("ACC_TumorStaging.regional_lymph_nodes");
                parameters.add("ACC_TumorStaging.tumor_infiltration_adipose");
                parameters.add("ACC_TumorStaging.tumor_invasion_adjacent");
                parameters.add("ACC_TumorStaging.tumor_thrombus_vena_renal");
                parameters.add("ACC_TumorStaging.distant_metastases");
                parameters.add("ACC_TumorStaging.bone");
                parameters.add("ACC_TumorStaging.liver");
                parameters.add("ACC_TumorStaging.lung");
                parameters.add("ACC_TumorStaging.abdomen_lymph_nodes");
                parameters.add("ACC_TumorStaging.other_metastases");
                
                parameters.add("ACC_Surgery.surgery_date");
                parameters.add("ACC_Surgery.surgery_method");
                parameters.add("ACC_Surgery.surgery_overall_resection_status");
                
                parameters.add("ACC_Pathology.ki67");
                parameters.add("ACC_Pathology.weiss_score");
                parameters.add("ACC_Pathology.number_of_mitoses");
                
                parameters.add("ACC_Mitotane.mitotane_indication");
                parameters.add("ACC_Radiotherapy.radiotherapy_indication");
                
            }
        }else if(mod.equals("6")){
            if(study.equals("stage3_4_acc")){
                parameters.add("*");
            }else if(study.equals("acc_pregnancy")){
                parameters.add("*");
            }else if(study.equals("pmt")){
                parameters.add("*");
            }else if(study.equals("pmt3")){
                parameters.add("*");
            }else if(study.equals("tma")){
                parameters.add("*");
            }else if(study.equals("ltphpgl")){
                parameters.add("*");
            }else if(study.equals("mibg_impact")){
                parameters.add("*");
            }
            else if(study.equals("ki67")
                    || study.equals("ltphpgl")
                    || study.equals("avis2")
                    || study.equals("adiuvo")
                    || study.equals("adiuvo_observational")                    
                    || study.equals("hairco")
                    || study.equals("firstmappp")
                    || study.equals("chiracic")
                    || study.equals("german_cushing")
                    || study.equals("german_conn")
                    || study.equals("uk_pheo_audit")
                    || study.equals("lysosafe")
                    || study.equals("firmact")
                    || study.equals("mapp_prono")
                    ){
                parameters.add("*");
            }
            
            
            
            else if(study.equals("acc_mol_marker")){
                //this.setCalcColumnsToExport(mod, query, study);
            }else if(study.equals("eurineact")){
                //this.setCalcColumnsToExport(mod, query, study);
                parameters.add("Identification.*");                
                
                parameters.add("NAPACA_DiagnosticProcedures.height");
                parameters.add("NAPACA_DiagnosticProcedures.weight");
                
                parameters.add("NAPACA_DiagnosticProcedures.gluco_plasma_acth_specific");
                parameters.add("NAPACA_DiagnosticProcedures.gluco_urinary_free_cortisol_specific");
                parameters.add("NAPACA_DiagnosticProcedures.other_steroid_17hydroxyprogesterone_specific");
                parameters.add("NAPACA_DiagnosticProcedures.other_steroid_serum_dheas_specific");
                
                parameters.add("NAPACA_DiagnosticProcedures.hypertension_presentation");
                parameters.add("NAPACA_DiagnosticProcedures.diabetestype2_presentation");
                parameters.add("NAPACA_DiagnosticProcedures.dyslipidaemia_presentation");
                parameters.add("NAPACA_DiagnosticProcedures.osteoporosis_presentation");                
                
                parameters.add("NAPACA_DiagnosticProcedures.symptoms_incidental");
                
                parameters.add("NAPACA_DiagnosticProcedures.symptoms_endocrine");
                parameters.add("NAPACA_DiagnosticProcedures.cushings_syndrome");
                parameters.add("NAPACA_DiagnosticProcedures.virilisation");
                parameters.add("NAPACA_DiagnosticProcedures.feminization");
                parameters.add("NAPACA_DiagnosticProcedures.mineralocorticoid_excess");
                
                parameters.add("NAPACA_DiagnosticProcedures.gluco_serum_cortisol");
                parameters.add("NAPACA_DiagnosticProcedures.gluco_serum_cortisol_specific");
                parameters.add("NAPACA_DiagnosticProcedures.gluco_serum_cortisol_units");
                parameters.add("NAPACA_DiagnosticProcedures.gluco_plasma_acth");
                parameters.add("NAPACA_DiagnosticProcedures.gluco_urinary_free_cortisol");
                parameters.add("NAPACA_DiagnosticProcedures.mineralo_plasma_renin_activity");
                parameters.add("NAPACA_DiagnosticProcedures.mineralo_plasma_renin_conc");
                parameters.add("NAPACA_DiagnosticProcedures.mineralo_serum_aldosterone");
                parameters.add("NAPACA_DiagnosticProcedures.other_steroid_17hydroxyprogesterone");
                parameters.add("NAPACA_DiagnosticProcedures.other_steroid_serum_dheas");
                parameters.add("NAPACA_DiagnosticProcedures.catechol_urinary_metanephrine_excretion");
                parameters.add("NAPACA_DiagnosticProcedures.catechol_plasma_metanephrines");
                parameters.add("NAPACA_DiagnosticProcedures.other_malignancies");
                parameters.add("NAPACA_DiagnosticProcedures.tumor_size");
                parameters.add("NAPACA_DiagnosticProcedures.midnight_salivary_cortisol");
                parameters.add("NAPACA_DiagnosticProcedures.midnight_serum_cortisol");
                
                parameters.add("ACC_DiagnosticProcedures.date_of_diagnosis");
                parameters.add("ACC_DiagnosticProcedures.height");
                parameters.add("ACC_DiagnosticProcedures.weight");
                parameters.add("ACC_DiagnosticProcedures.symptoms_incidental");
                parameters.add("ACC_DiagnosticProcedures.cushings_syndrome");
                parameters.add("ACC_DiagnosticProcedures.virilisation");
                parameters.add("ACC_DiagnosticProcedures.feminization");
                parameters.add("ACC_DiagnosticProcedures.mineralocorticoid_excess");
                parameters.add("ACC_DiagnosticProcedures.hypertension");
                parameters.add("ACC_DiagnosticProcedures.hypokalemia");
                parameters.add("ACC_DiagnosticProcedures.diabetes");
                parameters.add("ACC_DiagnosticProcedures.hormonal_hypersecretion");
                parameters.add("ACC_DiagnosticProcedures.glucocorticoids");
                parameters.add("ACC_DiagnosticProcedures.cortisol_after_dex");
                parameters.add("ACC_DiagnosticProcedures.cortisol_after_dex_units");
                parameters.add("ACC_DiagnosticProcedures.androgens");
                parameters.add("ACC_DiagnosticProcedures.estrogens");
                parameters.add("ACC_DiagnosticProcedures.mineralocorticoids");
                parameters.add("ACC_DiagnosticProcedures.precursor_secretion");
                parameters.add("ACC_TumorStaging.site_of_adrenal_tumor");
                parameters.add("ACC_TumorStaging.imaging");
                parameters.add("ACC_TumorStaging.hounsfield_units");
                parameters.add("ACC_TumorStaging.size_of_adrenal_tumor");
                parameters.add("ACC_TumorStaging.distant_metastases");
                parameters.add("ACC_TumorStaging.bone");
                parameters.add("ACC_TumorStaging.liver");
                parameters.add("ACC_TumorStaging.lung");
                parameters.add("ACC_TumorStaging.abdomen_lymph_nodes");
                parameters.add("ACC_TumorStaging.other_metastases");
                
                parameters.add("ACC_Biomaterial.biomaterial_date");
                parameters.add("ACC_Biomaterial.24h_urine");
                parameters.add("ACC_Biomaterial.24h_urine_vol");
                parameters.add("ACC_Biomaterial.spot_urine");
                
                parameters.add("ACC_Surgery.surgery_date");
                parameters.add("ACC_Surgery.surgery_overall_resection_status");
                
                parameters.add("ACC_Pathology.pathology_date");
                parameters.add("ACC_Pathology.ki67");
                parameters.add("ACC_Pathology.nuclear_atypia");
                parameters.add("ACC_Pathology.atypical_mitosis");
                parameters.add("ACC_Pathology.spongiocytic_tumor_cells");
                parameters.add("ACC_Pathology.diffuse_architecture");
                parameters.add("ACC_Pathology.venous_invasion");
                parameters.add("ACC_Pathology.sinus_invasion");
                parameters.add("ACC_Pathology.capsular_invasion");
                parameters.add("ACC_Pathology.necrosis");
                parameters.add("ACC_Pathology.number_of_mitoses_per5");
                
                parameters.add("ACC_Mitotane.mitotane_date");
                parameters.add("ACC_Mitotane.mitotane_initiation");
                parameters.add("ACC_Mitotane.mitotane_end");
                
                parameters.add("NAPACA_Biomaterial.biomaterial_date");
                parameters.add("NAPACA_Biomaterial.24h_urine");
                parameters.add("NAPACA_Biomaterial.24h_urine_vol");
                parameters.add("NAPACA_Biomaterial.spot_urine");
                
                parameters.add("NAPACA_Surgery.surgery_date");
                parameters.add("NAPACA_Surgery.surgical_approach");
                
                parameters.add("NAPACA_Pathology.pathology_date");
                parameters.add("NAPACA_Pathology.pathologist_name");
                parameters.add("NAPACA_Pathology.pathologist_location");
                parameters.add("NAPACA_Pathology.pathology_diagnosis");
                parameters.add("NAPACA_Pathology.number_of_mitoses_exact");
                parameters.add("NAPACA_Pathology.ki67");
                parameters.add("NAPACA_Pathology.weiss_score");
                parameters.add("NAPACA_Pathology.nuclear_atypia");
                parameters.add("NAPACA_Pathology.atypical_mitosis");
                parameters.add("NAPACA_Pathology.spongiocytic_tumor_cells");
                parameters.add("NAPACA_Pathology.diffuse_architecture");
                parameters.add("NAPACA_Pathology.venous_invasion");
                parameters.add("NAPACA_Pathology.sinus_invasion");
                parameters.add("NAPACA_Pathology.capsular_invasion");
                parameters.add("NAPACA_Pathology.necrosis");
                parameters.add("NAPACA_Pathology.number_of_mitoses_per5");
                
                parameters.add("NAPACA_Imaging.imaging_date");
                parameters.add("NAPACA_Imaging.tumor_sites");
                parameters.add("NAPACA_Imaging.right_adrenal_max_tumor");
                parameters.add("NAPACA_Imaging.left_adrenal_max_tumor");
                parameters.add("NAPACA_Imaging.imaging_of_tumor");
                parameters.add("NAPACA_Imaging.ct_tumor_density");
                parameters.add("NAPACA_Imaging.ct_delay_contrast_washout");
                parameters.add("NAPACA_Imaging.evidence_extra_adrenal");
                
                parameters.add("NAPACA_Imaging.additional_imaging_performed");
                parameters.add("NAPACA_Imaging.mri_chemical_shift_analysis");
                parameters.add("NAPACA_Imaging.fdg_pet");
                parameters.add("NAPACA_Imaging.comment");
                parameters.add("NAPACA_Imaging.absolute_contrast_washout");
                parameters.add("NAPACA_Imaging.relative_contrast_washout");
                parameters.add("NAPACA_Imaging.drop_intensity_signal");
                
                parameters.add("NAPACA_FollowUp.*");
                /*parameters.add("NAPACA_FollowUp.followup_date");
                parameters.add("NAPACA_FollowUp.followup_alive");
                parameters.add("NAPACA_FollowUp.followup_imaging");
                parameters.add("NAPACA_FollowUp.followup_imaging_type");
                parameters.add("NAPACA_FollowUp.followup_max_tumor");
                parameters.add("NAPACA_FollowUp.followup_changes_hormone_secretion");*/
                
                parameters.add("ACC_FollowUp.followup_date");
                parameters.add("ACC_FollowUp.patient_status");
                parameters.add("ACC_FollowUp.followup_comment");
                parameters.add("ACC_FollowUp.lost_to_followup");
                parameters.add("ACC_FollowUp.imaging_date");
                parameters.add("ACC_FollowUp.imaging_method");
                parameters.add("ACC_FollowUp.mitotane_ongoing");
                parameters.add("ACC_FollowUp.mitotane_recent_level");
                parameters.add("ACC_FollowUp.mitotane_recent_dose");
                parameters.add("ACC_FollowUp.current_steroid_substitution");
                parameters.add("ACC_FollowUp.current_steroid_dose");
                parameters.add("ACC_FollowUp.current_fludrocortisone_replacement");
                parameters.add("ACC_FollowUp.testosterone_replacement");
                parameters.add("ACC_FollowUp.replacement_choice");
                
                parameters.add("APA_PatientHistory.year_of_diagnosis");
                parameters.add("APA_ClinicalAssessment.weight");
                parameters.add("APA_ClinicalAssessment.height");
                parameters.add("APA_Imaging.adrenal_state");
                parameters.add("APA_Imaging.tumor_sites_imaging");
                parameters.add("APA_Imaging.max_tumor_by_ct_right");
                parameters.add("APA_Imaging.max_tumor_by_mr_right");
                parameters.add("APA_Imaging.max_tumor_by_ct_left");
                parameters.add("APA_Imaging.max_tumor_by_mr_left");
                parameters.add("APA_Surgery.intervention_date");
                parameters.add("APA_Surgery.tumor_sites_surgery");
                
            }
        }

        /*
         * for(int i=0; i<parameters.size(); i++){ logger.debug("parameters(" +
         * i + "): " + parameters.get(i));
        }
         */

    }

    private void setCalcColumnsToExport(String mod, String query, String study) {
        
        String exportLogMsge = "";
        exportLogMsge += "Setting calculated columns for ";
        if(mod.equals("5")){
            exportLogMsge += " all patients subject to the " + query + " query";
        }else if(mod.equals("6")){
            exportLogMsge += " all patients in the " + study + " study";
        }else{
            exportLogMsge += " all patients in this export";
        }        
        logger.debug(exportLogMsge + "...");

        if (mod.equals("5")) {
            if (query.equals("summary_only")
                    || query.equals("summary_all")
                    || query.equals("acc_quickcheck")) {
                calcColumns.add("ENSAT stage");
                calcColumns.add("Age at first diagnosis");
                calcColumns.add("Last follow-up");
                calcColumns.add("Overall survival");
                calcColumns.add("Lost to follow-up");
                calcColumns.add("Patient alive");
            }
        }else if(mod.equals("6")){
            if (study.equals("eurineact")) {
                calcColumns.add("Number of 24h urines");
                calcColumns.add("Number of spot urines");                
                calcColumns.add("Number of serums");
                calcColumns.add("Number of hep plasmas");
                calcColumns.add("ADIUVO (Y/N)");
                calcColumns.add("ENSAT stage");
                calcColumns.add("Age at first diagnosis");
                calcColumns.add("Last follow-up");
                calcColumns.add("Overall survival");
                calcColumns.add("Lost to follow-up");
                calcColumns.add("Patient alive");
                calcColumns.add("From NAPACA");
            }else if(study.equals("acc_mol_marker")){
                calcColumns.add("Mitotane (Y/N)");
            }
        }else{
            calcColumns.add("Associated Studies");
        }
    }

    public void setTablesToExport(String dbn, String mod, String query, String study, String center, String country) {

        String exportLogMsge = "";
        exportLogMsge += "Setting parameters for ";
        if(mod.equals("1")){
            exportLogMsge += " all patients belonging to user " + username;
        }else if(mod.equals("2")){
            exportLogMsge += " all patients belonging to center " + center;
        }else if(mod.equals("3")){
            exportLogMsge += " all patients belonging to country " + country;
        }else if(mod.equals("4")){
            exportLogMsge += " all patients belonging to type " + dbn;
        }else if(mod.equals("5")){
            exportLogMsge += " all patients subject to the " + query + " query";
        }else if(mod.equals("6")){
            exportLogMsge += " all patients in the " + study + " study";
        }        
        logger.debug(exportLogMsge + "...");

        tables = new ArrayList<String>();
        if (mod.equals("1")
                || mod.equals("2")
                || mod.equals("3")) {
            tables.add("Identification");
            tables.add("ACC_DiagnosticProcedures");
            tables.add("ACC_TumorStaging");
            tables.add("ACC_Biomaterial");

            //tables.add("ACC_Biomaterial_Normal_Tissue");
            tables.add("ACC_Radiofrequency");
            //tables.add("ACC_Radiofrequency_Loc");
            tables.add("ACC_Surgery");
            //tables.add("ACC_Surgery_First");
            //tables.add("ACC_Surgery_Extended");
            tables.add("ACC_Pathology");
            tables.add("ACC_Chemotherapy");
            //tables.add("ACC_Chemotherapy_Regimen");
            tables.add("ACC_Radiotherapy");
            //tables.add("ACC_Radiotherapy_Loc");
            tables.add("ACC_FollowUp");
            //tables.add("ACC_FollowUp_Organs");
            tables.add("ACC_Chemoembolisation");
            tables.add("ACC_Mitotane");
            tables.add("Pheo_BiochemicalAssessment");
            tables.add("Pheo_Biomaterial");
            tables.add("Pheo_Genetics");
            tables.add("Pheo_ClinicalAssessment");
            //tables.add("Pheo_FirstDiagnosisPresentation");
            tables.add("Pheo_FollowUp");
            tables.add("Pheo_ImagingTests");
            tables.add("Pheo_NonSurgicalInterventions");
            //tables.add("Pheo_OtherOrgans");
            tables.add("Pheo_PatientHistory");
            tables.add("Pheo_Surgery");
            tables.add("Pheo_TumorDetails");
            tables.add("NAPACA_Biomaterial");
            //tables.add("NAPACA_Biomaterial_Normal_Tissue");
            tables.add("NAPACA_DiagnosticProcedures");
            tables.add("NAPACA_FollowUp");
            tables.add("NAPACA_Imaging");
            tables.add("NAPACA_Pathology");
            tables.add("NAPACA_Surgery");
            tables.add("APA_BiochemicalAssessment");
            tables.add("APA_Biomaterial");
            //tables.add("APA_Biomaterial_Normal_Tissue");
            tables.add("APA_Cardio");
            tables.add("APA_ClinicalAssessment");
            tables.add("APA_Complication");
            tables.add("APA_FollowUp");
            tables.add("APA_Imaging");
            tables.add("APA_PatientHistory");
            tables.add("APA_Surgery");
        }else if (mod.equals("4")) {
            if (dbn.equals("ACC")) {
                tables.add("Identification");
                tables.add("ACC_DiagnosticProcedures");
                tables.add("ACC_TumorStaging");
                tables.add("ACC_Biomaterial");

                //tables.add("ACC_Biomaterial_Normal_Tissue");
                tables.add("ACC_Radiofrequency");
                //tables.add("ACC_Radiofrequency_Loc");
                tables.add("ACC_Surgery");
                //tables.add("ACC_Surgery_First");
                //tables.add("ACC_Surgery_Extended");
                tables.add("ACC_Pathology");
                tables.add("ACC_Chemotherapy");
                //tables.add("ACC_Chemotherapy_Regimen");
                tables.add("ACC_Radiotherapy");
                //tables.add("ACC_Radiotherapy_Loc");
                
                //TAKING FOLLOW-UP OUT RIGHT NOW AS THERE ARE JUST TOO MANY RECORDS - EVERYTHING STALLING...                
                //tables.add("ACC_FollowUp");
                
                
                
                //tables.add("ACC_FollowUp_Organs");
                tables.add("ACC_Chemoembolisation");
                tables.add("ACC_Mitotane");

            } else if (dbn.equals("Pheo")) {
                tables.add("Identification");
                tables.add("Pheo_BiochemicalAssessment");
                tables.add("Pheo_Biomaterial");
                tables.add("Pheo_Genetics");
                tables.add("Pheo_ClinicalAssessment");
                //tables.add("Pheo_FirstDiagnosisPresentation");
                tables.add("Pheo_FollowUp");
                tables.add("Pheo_ImagingTests");
                tables.add("Pheo_NonSurgicalInterventions");
                //tables.add("Pheo_OtherOrgans");
                tables.add("Pheo_PatientHistory");
                tables.add("Pheo_Surgery");
                tables.add("Pheo_TumorDetails");
                tables.add("Pheo_Surgery_Procedure");
                tables.add("Pheo_Morphological_Progression");
                tables.add("Pheo_Biological_Assessment");
            } else if (dbn.equals("NAPACA")) {
                tables.add("Identification");
                tables.add("NAPACA_Biomaterial");
                //tables.add("NAPACA_Biomaterial_Normal_Tissue");
                tables.add("NAPACA_DiagnosticProcedures");
                tables.add("NAPACA_FollowUp");
                tables.add("NAPACA_Imaging");
                tables.add("NAPACA_Pathology");
                tables.add("NAPACA_Surgery");
            } else if (dbn.equals("APA")) {
                tables.add("Identification");
                tables.add("APA_BiochemicalAssessment");
                tables.add("APA_Biomaterial");
                //tables.add("APA_Biomaterial_Normal_Tissue");
                tables.add("APA_Cardio");
                tables.add("APA_ClinicalAssessment");
                tables.add("APA_Complication");
                tables.add("APA_FollowUp");
                tables.add("APA_Imaging");
                tables.add("APA_PatientHistory");
                tables.add("APA_Surgery");
            }
        } else if (mod.equals("5")) {            
            if(query.equals("laterality")){
                tables.add("Identification");
                tables.add("ACC_TumorStaging");
                tables.add("Pheo_TumorDetails");
                tables.add("NAPACA_Imaging");
                tables.add("APA_Imaging");
            }else if(query.equals("summary_only")
                    || query.equals("summary_all")){
                tables.add("Identification");
                tables.add("ACC_DiagnosticProcedures");
                tables.add("ACC_TumorStaging");
                tables.add("ACC_Biomaterial");

                //tables.add("ACC_Biomaterial_Normal_Tissue");
                tables.add("ACC_Radiofrequency");
                //tables.add("ACC_Radiofrequency_Loc");
                tables.add("ACC_Surgery");
                //tables.add("ACC_Surgery_First");
                //tables.add("ACC_Surgery_Extended");
                tables.add("ACC_Pathology");
                tables.add("ACC_Chemotherapy");
                //tables.add("ACC_Chemotherapy_Regimen");
                tables.add("ACC_Radiotherapy");
                //tables.add("ACC_Radiotherapy_Loc");
                //tables.add("ACC_FollowUp");
                //tables.add("ACC_FollowUp_Organs");
                tables.add("ACC_Chemoembolisation");
                tables.add("ACC_Mitotane");                
            }else if(query.equals("acc_quickcheck")){
                tables.add("Identification");
                tables.add("ACC_DiagnosticProcedures");
                tables.add("ACC_TumorStaging");
                tables.add("ACC_Surgery");
                tables.add("ACC_Pathology");
                tables.add("ACC_Mitotane");
                tables.add("ACC_Radiotherapy");                
            }
        } else if (mod.equals("6")) { 
            if(study.equals("stage3_4")){
                tables.add("Identification");
                tables.add("ACC_DiagnosticProcedures");
                tables.add("ACC_TumorStaging");
                tables.add("ACC_Biomaterial");

                //tables.add("ACC_Biomaterial_Normal_Tissue");
                tables.add("ACC_Radiofrequency");
                //tables.add("ACC_Radiofrequency_Loc");
                tables.add("ACC_Surgery");
                //tables.add("ACC_Surgery_First");
                //tables.add("ACC_Surgery_Extended");
                tables.add("ACC_Pathology");
                tables.add("ACC_Chemotherapy");
                //tables.add("ACC_Chemotherapy_Regimen");
                tables.add("ACC_Radiotherapy");
                //tables.add("ACC_Radiotherapy_Loc");
                tables.add("ACC_FollowUp");
                //tables.add("ACC_FollowUp_Organs");
                tables.add("ACC_Chemoembolisation");
                tables.add("ACC_Mitotane");                               
            }else if(study.equals("acc_pregnancy")){
                tables.add("Identification");
                tables.add("ACC_DiagnosticProcedures");
                tables.add("ACC_TumorStaging");
                tables.add("ACC_Biomaterial");

                //tables.add("ACC_Biomaterial_Normal_Tissue");
                tables.add("ACC_Radiofrequency");
                //tables.add("ACC_Radiofrequency_Loc");
                tables.add("ACC_Surgery");
                //tables.add("ACC_Surgery_First");
                //tables.add("ACC_Surgery_Extended");
                tables.add("ACC_Pathology");
                tables.add("ACC_Chemotherapy");
                //tables.add("ACC_Chemotherapy_Regimen");
                tables.add("ACC_Radiotherapy");
                //tables.add("ACC_Radiotherapy_Loc");
                //tables.add("ACC_FollowUp");
                //tables.add("ACC_FollowUp_Organs");
                tables.add("ACC_Chemoembolisation");
                tables.add("ACC_Mitotane");
            }else if(study.equals("pmt3") || study.equals("tma") || study.equals("mibg_impact")){
                tables.add("Identification");
                tables.add("Pheo_BiochemicalAssessment");
                tables.add("Pheo_Biomaterial");
                tables.add("Pheo_Genetics");
                tables.add("Pheo_ClinicalAssessment");
                //tables.add("Pheo_FirstDiagnosisPresentation");
                tables.add("Pheo_FollowUp");
                tables.add("Pheo_ImagingTests");
                tables.add("Pheo_NonSurgicalInterventions");
                //tables.add("Pheo_OtherOrgans");
                tables.add("Pheo_PatientHistory");
                tables.add("Pheo_Surgery");
                tables.add("Pheo_TumorDetails");
            }else if(study.equals("mapp-prono")){
                tables.add("Identification");
                tables.add("Pheo_BiochemicalAssessment");
                tables.add("Pheo_Biomaterial");
                tables.add("Pheo_Genetics");
                tables.add("Pheo_ClinicalAssessment");
                //tables.add("Pheo_FirstDiagnosisPresentation");
                tables.add("Pheo_FollowUp");
                tables.add("Pheo_ImagingTests");
                tables.add("Pheo_NonSurgicalInterventions");
                //tables.add("Pheo_OtherOrgans");
                tables.add("Pheo_PatientHistory");
                tables.add("Pheo_Surgery");
                tables.add("Pheo_TumorDetails");
                tables.add("Pheo_Surgery_Procedure");
                tables.add("Pheo_Morphological_Progression");
                tables.add("Pheo_Biological_Assessment");
            }else if(study.equals("eurineact")){
                tables.add("Identification");
                tables.add("ACC_DiagnosticProcedures");
                tables.add("ACC_TumorStaging");
                tables.add("ACC_Biomaterial");                
                //tables.add("ACC_Radiofrequency");                
                tables.add("ACC_Surgery");
                tables.add("ACC_Pathology");
                tables.add("ACC_FollowUp");
                /*tables.add("ACC_Chemotherapy");
                tables.add("ACC_Radiotherapy");
                tables.add("ACC_Chemoembolisation");*/
                tables.add("ACC_Mitotane");
                tables.add("NAPACA_Biomaterial");                
                tables.add("NAPACA_DiagnosticProcedures");
                tables.add("NAPACA_FollowUp");
                tables.add("NAPACA_Imaging");
                tables.add("NAPACA_Pathology");
                tables.add("NAPACA_Surgery");
                
                tables.add("APA_PatientHistory");
                tables.add("APA_ClinicalAssessment");
                tables.add("APA_Imaging");
                tables.add("APA_Surgery");
                
            }else if(study.equals("pmt")){
                if(dbn.equals("Pheo")){
                    tables.add("Identification");
                    tables.add("Pheo_BiochemicalAssessment");
                    tables.add("Pheo_Biomaterial");
                    tables.add("Pheo_Genetics");
                    tables.add("Pheo_ClinicalAssessment");
                    //tables.add("Pheo_FirstDiagnosisPresentation");
                    tables.add("Pheo_FollowUp");
                    tables.add("Pheo_ImagingTests");
                    tables.add("Pheo_NonSurgicalInterventions");
                    //tables.add("Pheo_OtherOrgans");
                    tables.add("Pheo_PatientHistory");
                    tables.add("Pheo_Surgery");
                    tables.add("Pheo_TumorDetails");
                }else if(dbn.equals("NAPACA")){
                    tables.add("Identification");
                    tables.add("NAPACA_Biomaterial");                
                    tables.add("NAPACA_DiagnosticProcedures");
                    tables.add("NAPACA_FollowUp");
                    tables.add("NAPACA_Imaging");
                    tables.add("NAPACA_Pathology");
                    tables.add("NAPACA_Surgery");
                }
            }else if(study.equals("ltphpgl")){                
                tables.add("Identification");
                tables.add("Pheo_BiochemicalAssessment");
                //tables.add("Pheo_Biomaterial");
                tables.add("Pheo_Genetics");
                //tables.add("Pheo_ClinicalAssessment");                
                tables.add("Pheo_FollowUp");
                tables.add("Pheo_ImagingTests");
                //tables.add("Pheo_NonSurgicalInterventions");                
                tables.add("Pheo_PatientHistory");
                tables.add("Pheo_Surgery");
                tables.add("Pheo_TumorDetails");                
            }
        }
        
        /*for(int i=0; i<tables.size(); i++){
            logger.debug("table(" + i + "): " + tables.get(i));
        }*/
        
    }

    public void setExportUserList(String[] idListIn) {
        if (idListIn != null) {
            idList = Arrays.asList(idListIn);            
        } else {
            idList = new ArrayList<String>();
        }
    }

    public void setConnection(Connection conn) {
        connection = conn;
    }

    public String processExportTest() {


        String table = "TestTable";
        String[] currentOutputHeader = {"Test 1", "Test 2"};
        String[] currentOutputDetail = {"Row 1", "Row 2"};

        try {
            CsvExporter exporter = new CsvExporter(base + filepath + "/" + table + "_" + filename + ".csv", currentOutputHeader);
            exporter.writeLine(currentOutputDetail);
            exporter.close();

        } catch (Exception e) {
            logger.debug("Error exporting: " + e.getMessage());
        }

        return "Test export done...";
    }

    public String processExport(Connection conn) throws SQLException {

        String output = "";
        orderedExportedUsers = new LinkedList<String>();

        XSSFWorkbook excelWorkbook = new XSSFWorkbook();
        Map<Integer, XSSFCellStyle> styleMap = new HashMap<Integer, XSSFCellStyle>();

        //process headers            
        for (int m = 0; m < tables.size(); m++) {
            String table = tables.get(m);

            logger.debug("Exporting table: " + table);

            GenericExporter[] exporters = new GenericExporter[2];
            //AS - Creates the exporters - and headers - for each table involved (compiled from the choice of databases earlier)
            try {

                String[] currentOutputHeader = this.generateOutputHeader(table);
                
                //logger.debug("Columns output (" + table + ")...");
                if (currentOutputHeader.length != 0) {
                    exporters[0] = new CsvExporter(base + filepath + "/" + table + "_" + filename + ".csv", currentOutputHeader);
                    exporters[1] = new ExcelExporter(base + filepath + "/" + table + "_" + filename + ".xlsx", currentOutputHeader);
                }
                //logger.debug("Exporters set (" + table + ")...");
            } catch (IOException ioe) {
                logger.debug("Unable to write export files to " + base + filepath + " (" + context + "/" + filepath + "): " + ioe.getMessage());
                return "Unable to write export files to " + base + filepath + " (" + context + "/" + filepath + "): " + ioe.getMessage();
            }

            //logger.debug("Moving from headers to content (" + table + ")...");

            try {

                if (singleEntryTables.contains(table)) {
                    //logger.debug("Filling single entry content (" + table + ")...");
                    this.fillExportersWithSingleEntryContent(exporters, table, conn);
                    //logger.debug("Finished filling single-entry content (" + table + ")...");
                } else {
                    //logger.debug("Filling multiple entry content (" + table + ")...");
                    this.fillExportersWithMultipleEntryContent(exporters, table);
                    //logger.debug("Finished filling multiple-entry content (" + table + ")...");
                }

                //adds created XLSX workbook as sheet in combined form, only executes if the
                //workbook has valid data (due to try block).
                String sheetName = WorkbookUtil.createSafeSheetName(table);
                XSSFSheet newSheet = excelWorkbook.createSheet(sheetName);
                Workbook originalWorkbook = ((ExcelExporter) exporters[1]).getWorkbook();

                if (table.equals("Identification")) {
                    Sheet originalSheet = originalWorkbook.getSheetAt(0);

                    for (int n = 0; n <= originalSheet.getLastRowNum(); n++) {
                        Row row = originalSheet.getRow(n);
                        String exportedUserId = row.getCell(0).getStringCellValue();
                        orderedExportedUsers.add(exportedUserId);
                    }                                        
                }

                
                //logger.debug("Now setting up original sheet(" + table + ")...");                
                Sheet originalSheet = originalWorkbook.getSheetAt(0);
                Row row = null;
                Row newRow = null;
                Cell newCell = null;
                int newRowPosn = 0;

                //for (String patient: orderedExportedUsers) {
                //logger.debug("User number: " + orderedExportedUsers.size());
                for (int n = 0; n < orderedExportedUsers.size(); n++) {
                    String patient = orderedExportedUsers.get(n);
                    row = null;
                    boolean rowFound = false;
                    int rowCount = 0;
                    while (!rowFound && rowCount <= originalSheet.getLastRowNum()) {
                        row = originalSheet.getRow(rowCount);
                        if (row != null) {
                            if (row.getCell(0) != null) {
                                String rowIdIn = row.getCell(0).getStringCellValue();
                                if (rowIdIn.equals(patient)) {
                                    rowFound = true;
                                }
                            }
                        }
                        rowCount++;
                    }
                    newRow = newSheet.createRow(newRowPosn);

                    if (row != null && row.getCell(0) != null && patient.equals(row.getCell(0).getStringCellValue())) {
                        for (int c = 0; c < row.getLastCellNum(); c++) {
                            Cell cell = row.getCell(c);
                            newCell = newRow.createCell(cell.getColumnIndex(), cell.getCellType());
                            switch (cell.getCellType()) {
                                case Cell.CELL_TYPE_NUMERIC:
                                    newCell.setCellValue(cell.getNumericCellValue());
                                    break;
                                case Cell.CELL_TYPE_STRING:
                                    newCell.setCellValue(cell.getStringCellValue());
                                    break;                                    
                            }
                            int stHashCode = cell.getCellStyle().hashCode();
                            XSSFCellStyle newCellStyle = styleMap.get(stHashCode);
                            if (newCellStyle == null) {
                                newCellStyle = excelWorkbook.createCellStyle();
                                newCellStyle.cloneStyleFrom(cell.getCellStyle());
                                styleMap.put(stHashCode, newCellStyle);
                            }
                            newCell.setCellStyle(newCellStyle);
                        }
                    }
                    newRowPosn++;
                }
                
                //logger.debug("Now add the padding (" + table + ")...");

                //AS - something to do with the padding for the combined sheet?
                Row excelRow = newSheet.getRow(0);
                int rowLength = excelRow.getLastCellNum();
                for (int i = 0; i < rowLength; i++) {
                    newSheet.autoSizeColumn(i);
                }

                //logger.debug("Now commit the updates to the file (" + table + ")...");
                
                //AS - I *think* this is so that the writing to the exporter commits (closure of file-stream)
                for (GenericExporter exporter : exporters) {
                    exporter.close();
                }
                logger.debug(table + "_" + filename + " written to disk...");

                output += "<p>"
                        + "<b>" + table + " form export:</b>"
                        + "<ul>";
                output += "<li>";
                output += "File successfully exported to: <a target='_blank' href='";
                output += context + "/" + filepath + "/" + table + "_" + filename + ".csv'>";
                output += table + "_" + filename + ".csv</a></li>";
                output += "<li>";
                output += "File successfully exported to: <a target='_blank' href='";
                output += context + "/" + filepath + "/" + table + "_" + filename + ".xlsx'>";
                output += table + "_" + filename + ".xlsx</a></li>";
                output += "</ul></p>";
            } catch (SearchResultsException sre) {

                //If there are no results, close exporter and delete empty export file.                
                output += "<p><em>No matching entries for " + table + "</em></p>";

                //for (GenericExporter exporter : exporters) {
                for (int ge = 0; ge < exporters.length; ge++) {
                    GenericExporter exporter = exporters[ge];
                    File file = null;
                    if (exporter.getClass().isAssignableFrom(ExcelExporter.class)) {
                        file = new File(base + filepath + "/" + table + "_" + filename + ".xlsx");
                    } else {
                        file = new File(base + filepath + "/" + table + "_" + filename + ".csv");
                    }
                    exporter.close();
                    file.delete();
                }

            }

            logger.debug("====");

        }

        //This writes all the workbook stuff to the combined file
        try {
            FileOutputStream fos = new FileOutputStream(base + filepath + "/Combined_" + filename + ".xlsx");
            excelWorkbook.write(fos);
            fos.close();
        } catch (IOException ioe) {
            logger.debug("Error closing off file (" + username + "): " + ioe.getMessage());
        }

        output = "<p><b>Combined form export:</b></p>"
                + "<p><ul><li>File successfully exported to: <a target='_blank' href='"
                + context + "/" + filepath + "/" + "Combined" + "_" + filename + ".xlsx'>"
                + "Combined_" + filename + ".xlsx</a></li></ul></p>" + output;

        //logger.debug(output);
        return output;
    }

    public String getUserListQueryFragment(String prefix) {

        String pre = "";
        if (prefix == null) {
            pre = "";
        } else {
            pre = prefix;
        }

        String subFragment = "";
        if (prefix == null) {
            subFragment += "WHERE (";
        } else {
            subFragment += "AND (";
        }

        //logger.debug("idList.size() [within getUserListQueryFragment]: " + idList.size());
        for (int i = 0; i < idList.size(); i++) {
            
            String subFragmentIn = "";
            String user = idList.get(i);

            String centerid = user.substring(0, user.indexOf("-"));
            String pid = user.substring(user.indexOf("-") + 1, user.length());

            subFragmentIn += "(";
            if (pre.length() < 1) {
                subFragmentIn += "";
            } else {
                subFragmentIn += pre + ".";
            }

            subFragmentIn += "ensat_id = " + pid;
            subFragmentIn += " AND ";
            if (pre.length() < 1) {
                subFragmentIn += "";
            } else {
                subFragmentIn += pre + ".";
            }

            subFragmentIn += "center_id = '" + centerid + "'";
            subFragmentIn += ")";
            subFragmentIn += " OR ";
            
            //logger.debug("subFragmentIn #" + i + ": " + subFragmentIn);            
            subFragment += subFragmentIn;
        }

        subFragment = subFragment.substring(0, subFragment.length() - 4);
        subFragment += ") ";
        return subFragment;
    }

    public void fillExportersWithSingleEntryContent(GenericExporter[] exporters, String table, Connection conn) throws SQLException, SearchResultsException {
        
        //NEED TO REFERENCE THE PARAMETER ARRAY HERE
        String tableQuery = this.getTableQuery(table);
        
        logger.debug("tableQuery: " + tableQuery);

        boolean hasData = false;
        if (!tableQuery.equals("")) {
            ResultSet rs = connection.createStatement().executeQuery(tableQuery);
            ResultSetMetaData rsmd = rs.getMetaData();
            int columnNum = rsmd.getColumnCount();

            //int rsRowCount = 1;
            Vector<String> idListReturned = new Vector<String>();
            while (rs.next()) {
                /*if(table.equals("Identification")){
                    logger.debug("patient number: " + rsRowCount);
                }*/
                
                hasData = true;

                int dataInSize = columnNum - 1;
                String[] row = new String[dataInSize];

                String thisEnsatId = "";
                if (rsmd.getColumnLabel(1).equals("ensat_id")) {
                    row[0] = formatPatientId(rs.getString(2), rs.getString(1));
                    thisEnsatId = row[0];
                    idListReturned.add(thisEnsatId);
                    for (int i = 1; i < dataInSize; i++) {
                        row[i] = rs.getString(i + 2);
                    }
                } else {
                    row[0] = rs.getString(1);
                    row[1] = formatPatientId(rs.getString(3), rs.getString(2));
                    thisEnsatId = row[1];
                    for (int i = 2; i < dataInSize; i++) {
                        row[i] = rs.getString(i + 2);
                    }
                }
                
                //Add calculated columns to the row here (put them in after on Identification)
                int calcColumnNum = 0;
                String[] rowFinal = null;
                if(table.equals("Identification")){                    
                    calcColumnNum = calcColumns.size();        
                    int totalRowSize = dataInSize + calcColumnNum;
                    rowFinal = new String[totalRowSize];
                    for(int i=0; i<dataInSize; i++){
                        rowFinal[i] = row[i];
                    }
                    
                    if(calcColumnNum > 0){
                        //Calculate the values for this ID
                        //logger.debug("Calculating columns for " + thisEnsatId + "...");
                        String[] calcColumnValues = this.getCalcColumnValues(thisEnsatId,conn);
                        for(int i=dataInSize; i<totalRowSize; i++){
                            //String calcColumnIn = calcColumns.get(i-dataInSize);
                            rowFinal[i] = calcColumnValues[i-dataInSize];
                        }                        
                        //logger.debug("Columns calculated...");
                        //logger.debug("-----");
                    }                                               
                }else{
                    rowFinal = new String[dataInSize];
                    for(int i=0; i<dataInSize; i++){
                        rowFinal[i] = row[i];
                    }
                }
                
                /*if(table.equals("NAPACA_DiagnosticProcedures")){                
                    for(int i=0; i < rowFinal.length; i++){
                        logger.debug("rowFinal[" + i + "]: " + rowFinal[i]);
                    }
                }*/

                for (int i = 0; i < exporters.length; i++) {
                    GenericExporter exporter = exporters[i];
                    exporter.writeLine(rowFinal);
                }
                //rsRowCount++;
            }
            
            /*if(table.equals("Identification")){
                logger.debug("idListReturned.size(): " + idListReturned.size());
                logger.debug("idList.size(): " + idList.size());
                int notContainedCount = 0;
            //logger.debug("idList.get(0): " + idList.get(0));
            //logger.debug("idListReturned.get(0): " + idListReturned.get(0));
                for(int i=0; i<idList.size(); i++){
                    String idIn = idList.get(i);    
                    logger.debug("#" + (i+1) + ": " + idIn);
                    boolean foundId = false;
                    int mainIdCount = 0;
                    while(mainIdCount < idListReturned.size() && !foundId){
                        String mainIdIn = idListReturned.get(mainIdCount);
                        if(mainIdIn.equalsIgnoreCase(idIn)){
                            foundId = true;
                            logger.debug("Found match: " + mainIdIn);
                        }else{
                            mainIdCount++;
                        }
                    }
                    if(!foundId){
                        notContainedCount++;
                        logger.debug("idListReturned does not contain: " + idIn);
                    }                
                }
                logger.debug("notContainedCount: " + notContainedCount);
            }*/
        }

        if (!hasData) {
            throw new SearchResultsException();
        }
    }
    
    private String[] getCalcColumnValues(String ensatId, Connection conn){
        
        int calcColumnNum = calcColumns.size();
        
        int hyphenIndex = ensatId.indexOf("-");
        if(hyphenIndex != -1){
            
            String[] calcColumnValues = new String[calcColumnNum];
            String centerid = ensatId.substring(0,hyphenIndex);
            String pid = ensatId.substring(hyphenIndex+1,ensatId.length());            
            
            try{
                for(int i=0; i<calcColumnNum; i++){
                    calcColumnValues[i] = this.getIndividualCalcValue(centerid, pid, conn, calcColumns.get(i));
                }
                
            }catch(Exception e){
                logger.debug("Error (getCalcColumnValues): " + e.getMessage());
            }
            
            return calcColumnValues;
        }else{        
            return new String[0];
        }
    }    
    
    private String getIndividualCalcValue(String centerid, String pid, Connection conn, String calcColumnName){
        
        update_main.Utilities util = new update_main.Utilities();
        
        if(calcColumnName.equals("ENSAT stage")){
            String ensatStage = util.getEnsatStage(centerid,pid,conn);
            return ensatStage;
        }else if(calcColumnName.equals("Age at first diagnosis")){            
            String dateDiagnosis = util.getDateDiagnosis(centerid,pid,conn);
            String dateDiagnosisYear = util.getDateDiagnosisYear(dateDiagnosis);
            int diagnosisAge = util.getDiagnosisAge(centerid, pid, conn, dateDiagnosisYear);
            String diagnosisAgeStr = "";
            if(diagnosisAge == -1){
                diagnosisAgeStr = "";
            }else{
                diagnosisAgeStr = "" + diagnosisAge;
            }            
            return "" + diagnosisAgeStr;            
        }else if(calcColumnName.equals("Last follow-up")){
            ResultSet statusCheck = util.getPatientStatusCheck(centerid, pid, conn);
            String followupDate = util.getFollowupDate(statusCheck);
            return followupDate;                
        }else if(calcColumnName.equals("Overall survival")){            
            ResultSet statusCheck = util.getPatientStatusCheck(centerid, pid, conn);
            String patientAliveStr = util.getPatientAliveStr(statusCheck);
            String dateDiagnosis = util.getDateDiagnosis(centerid,pid,conn);
            String followupDate = util.getFollowupDate(statusCheck);
            ResultSet resectionSet = util.getResectionSet(centerid, pid, conn);
            boolean r0 = util.getResectionStatus(resectionSet);
            String dateResectionStr = util.getResectionDateStr(resectionSet);
            long dateInterval = util.getDateInterval(followupDate,patientAliveStr,dateDiagnosis,dateResectionStr);
            long dateIntervalDays = util.getDateInDays(dateInterval);
            long dateIntervalYears = util.getDateInYears(dateIntervalDays);
            String dateIntervalStr = "" + dateIntervalDays + " days (" + dateIntervalYears + " years)";
            return "" + dateIntervalStr;
        }else if(calcColumnName.equals("Lost to follow-up")){
            ResultSet statusCheck = util.getPatientStatusCheck(centerid, pid, conn);
            String lostToFollowUp = util.getLostToFollowUp(statusCheck);
            return lostToFollowUp;                                
        }else if(calcColumnName.equals("Patient alive")){
            ResultSet statusCheck = util.getPatientStatusCheck(centerid, pid, conn);
            String patientAliveStr = util.getPatientAliveStr(statusCheck);
            return patientAliveStr;                
        }else if(calcColumnName.equals("Number of 24h urines")){
            String urine24hNum = util.getUrineNum(centerid,pid,conn,"24h_urine");
            return urine24hNum;
        }else if(calcColumnName.equals("Number of spot urines")){
            String urineSpotNum = util.getUrineNum(centerid,pid,conn,"spot_urine");
            return urineSpotNum;
        }else if(calcColumnName.equals("Number of serums")){
            String serumNum = util.getUrineNum(centerid,pid,conn,"serum");
            return serumNum;
        }else if(calcColumnName.equals("Number of hep plasmas")){
            String hepPlasmaNum = util.getUrineNum(centerid,pid,conn,"heparin_plasma");
            return hepPlasmaNum;
        }else if(calcColumnName.equals("Mitotane (Y/N)")){
            String mitotanePresence = util.getMitotanePresence(centerid,pid,conn);
            return mitotanePresence;
        }else if(calcColumnName.equals("ADIUVO (Y/N)")){
            String adiuvoPresence = util.getAdiuvoPresence(centerid,pid,conn);
            return adiuvoPresence;
        }else if(calcColumnName.equals("From NAPACA")){
            String transferFromNapaca = util.getTransferFromNapaca(centerid,pid,conn);
            return transferFromNapaca;
        }else if(calcColumnName.equals("Associated Studies")){
            String assocStudies = util.getAssociatedStudies(centerid,pid,conn);
            return assocStudies;
        }
        return "";
    }
    

    private String getTableQuery(String table_name) {

        //THIS IS WHERE THE PARAMETERS NEED TO BE REFERENCED

        if (table_name.equals("Identification")) {
            String uploaderString = "";
            String query = "SELECT * FROM %s "
                    + uploaderString
                    + this.getUserListQueryFragment(null)
                    + "ORDER BY center_id, ensat_id";
            return String.format(query, table_name);
        } else {
            String uploaderString = "";
            String parameterString = this.getParameterString(table_name);

            if (parameterString.equals("")) {
                return "";
            } else {
                //String query = "SELECT A.* FROM %s AS A, Identification AS B "
                String query = "SELECT " + parameterString + " FROM %s AS A, Identification AS B "
                        + "WHERE A.ensat_id = B.ensat_id "
                        + "AND A.center_id = B.center_id "
                        + uploaderString
                        + this.getUserListQueryFragment("A")
                        + "ORDER BY A.center_id, A.ensat_id";

                //logger.debug("query: " + query);
                return String.format(query, table_name);
            }
        }

    }

    private String getParameterString(String table_name) {

        String paramStr = "";
        
        if(singleEntryTables.contains(table_name)){
            paramStr = "A.ensat_id, A.center_id, ";
        }else{
            if(table_name.equals("Pheo_TumorDetails")){
                paramStr += "A.pheo_tumor_details_id, A.ensat_id, A.center_id, ";
            }else{
                String formParamName = this.getSubTableIdName(table_name);                
                paramStr += "A." + formParamName + ", A.ensat_id, A.center_id, ";
            }
        }
        
        for (int i = 0; i < parameters.size(); i++) {
            String paramIn = parameters.get(i);            
            int dotIndex = paramIn.indexOf(".");
            if (dotIndex != -1) {
                String tableQualifierIn = paramIn.substring(0, dotIndex);
                if (tableQualifierIn.equals(table_name)) {
                    paramIn = paramIn.substring(dotIndex + 1, paramIn.length());                    
                    paramStr += "A." + paramIn + ", ";
                }
            } else {
                paramStr += "A." + paramIn + ", ";
            }
        }
        //Chop the final comma plus space off
        if (paramStr.length() >= 2) {
            paramStr = paramStr.substring(0, paramStr.length() - 2);
        }        
        if (paramStr == null) {
            paramStr = "";
        }
        paramStr = paramStr.trim();
        return paramStr;
    }

    private String getSubTableQuery(String table_name, String sub_table_name) {
        //String uploaderString = "AND A.center_id = \"%s\" ";
        String uploaderString = "";
        String query = "SELECT A.* FROM %s AS A, Identification AS B "
                + "WHERE A.ensat_id = B.ensat_id "
                + "AND A.center_id = B.center_id "
                + uploaderString
                + getUserListQueryFragment("A")
                + "ORDER BY A.%s_id";
        //return String.format(query, sub_table_name, center_id, table_name.toLowerCase());
        return String.format(query, sub_table_name, table_name.toLowerCase());
    }

    private String[] generateSingleEntryHeader(String table_name) throws SQLException {

        //NEED TO REFERENCE THE CALCULATED COLUMNS HERE (AND ADD TO IDENTIFICATION TABLE)
        

        //String tableQuery = this.getTableQuery(table_name, username)+" LIMIT 1";
        String tableQuery = this.getTableQuery(table_name);

        String[] output = new String[0];
        String[] outputFinal = null;
        if (!tableQuery.equals("")) {

            tableQuery += " LIMIT 1";
            
            //logger.debug("tableQuery (generateSingleEntryHeader): " + tableQuery);

            ResultSet resultSet = connection.createStatement().executeQuery(tableQuery);
            ResultSetMetaData rsmd = resultSet.getMetaData();
            int headerInSize = rsmd.getColumnCount() - 1;
            output = new String[headerInSize];

            if (rsmd.getColumnLabel(1).equals("ensat_id")) {
                output[0] = "patient_id";
                for (int i = 1; i < output.length; i++) {
                    output[i] = rsmd.getColumnName(i + 2);
                }
            } else {
                output[0] = rsmd.getColumnName(1);
                output[1] = "patient_id";
                for (int i = 2; i < output.length; i++) {
                    output[i] = rsmd.getColumnName(i + 2);
                }
            }
            
            
            int calcColumnNum = 0;            
            if(table_name.equals("Identification")){                    
                calcColumnNum = calcColumns.size();        
                int totalRowSize = headerInSize + calcColumnNum;
                outputFinal = new String[totalRowSize];
                for(int i=0; i<headerInSize; i++){
                    outputFinal[i] = output[i];
                }
                for(int i=headerInSize; i<totalRowSize; i++){
                    String calcColumnIn = calcColumns.get(i-headerInSize);
                    outputFinal[i] = calcColumnIn;
                }                                                    
            }else{
                outputFinal = new String[headerInSize];
                for(int i=0; i<headerInSize; i++){
                    outputFinal[i] = output[i];
                }
            }
        }
        return outputFinal;
    }

    private String[] generateOutputHeader(String table_name) {

        if (singleEntryTables.contains(table_name)) {
            try {
                return this.generateSingleEntryHeader(table_name);
            } catch (SQLException sqle) {
                logger.debug("Error (" + username + "): " + sqle.getMessage());
            }
        }
        //String uploaderString = "AND A.center_id = \"%s\" ";
        String uploaderString = "";

        String unformattedColumnLengthQuery = "SELECT count(*) AS counter FROM %s AS A, Identification AS B "
                + "WHERE A.ensat_id = B.ensat_id "
                + "AND A.center_id = B.center_id "
                + uploaderString
                + this.getUserListQueryFragment("A")
                + "GROUP BY A.ensat_id "
                + "ORDER BY counter DESC";

        //logger.debug("unformattedColumnLengthQuery (" + username + "): " + unformattedColumnLengthQuery);
        String columnLengthQuery = "";
        //columnLengthQuery = String.format(unformattedColumnLengthQuery, table_name, center_id);
        columnLengthQuery = String.format(unformattedColumnLengthQuery, table_name);
        //logger.debug("columnLengthQuery (" + username + "): " + columnLengthQuery);
        
        int maxNumberOfEntries = this.getMaxNumberOfEntries(columnLengthQuery);
        
        //logger.debug("maxNumberOfEntries: " + maxNumberOfEntries);
        
        String[] rowHeaders = this.getRowHeaders(table_name);
        TableHeader tableHeader = new TableHeader(rowHeaders, maxNumberOfEntries);
        tableMaxEntryCount.put(table_name, maxNumberOfEntries);

        String subTableColumnLengthQueryTemplate =
                "SELECT %s_id, count(*) AS counter FROM %s AS A, Identification AS B "
                + "WHERE A.ensat_id = B.ensat_id "
                + "AND A.center_id = B.center_id "
                + uploaderString
                + this.getUserListQueryFragment("A")
                + "GROUP BY %s_id "
                + "ORDER BY counter DESC";
        
        //logger.debug("subTableColumnLengthQueryTemplate: " + subTableColumnLengthQueryTemplate);
        

        //WHY DOES HE RESET THIS MAX-ENTRY COUNTER HERE?
        maxNumberOfEntries = 1;

        if (subTableMap.containsKey(table_name)) {

            Collection<String> coll = subTableMap.get(table_name);
            Iterator iter = coll.iterator();
            
            while (iter.hasNext()) {

                //I NEED TO UNDERSTAND THIS BETTER (GETTING RID OF THE FOR-EACH LOOP... THINK THAT'S IT...)
                String entry = (String) iter.next();
                logger.debug("entry: " + entry);
                String subTableColumnLengthQuery = "";
                //subTableColumnLengthQuery = String.format(subTableColumnLengthQueryTemplate, table_name.toLowerCase(), entry, center_id, table_name.toLowerCase());                
                subTableColumnLengthQuery = String.format(subTableColumnLengthQueryTemplate, table_name.toLowerCase(), entry, table_name.toLowerCase());                
                logger.debug("subTableColumnLengthQuery: " + subTableColumnLengthQuery);
                
                ResultSet count = null;
                String[] subRowHeaders = null;
                if (!entry.endsWith("Biomaterial_Aliquots") && !entry.endsWith("Normal_Tissue")) {
                    try {
                        count = connection.createStatement().executeQuery(subTableColumnLengthQuery);
                    } catch (SQLException se) {
                        logger.debug("Error (subtable column length query) (" + username + "): " + se.getMessage());
                        //throw new SQLException(se.getMessage()+"; "+table_name+","+entry+"; "+subTableColumnLengthQuery);
                    }
                    
                    logger.debug("TEST #1");

                    try {
                        if (count.next()) {
                            maxNumberOfEntries = count.getInt(2);
                        }
                        count.close();
                    } catch (SQLException sqle) {
                        logger.debug("Error (max number of entries) (" + username + "): " + sqle.getMessage());
                    }
                    
                    logger.debug("TEST #2");

                    try {
                        //ResultSet subQuery = connection.createStatement().executeQuery(this.getSubTableQuery(table_name, entry, username) + " LIMIT 1");
                        ResultSet subQuery = connection.createStatement().executeQuery(this.getSubTableQuery(table_name, entry) + " LIMIT 1");
                        //DEBUG
                        //System.out.println(getSubTableQuery(table_name, entry, username));
                        ResultSetMetaData rowMetadata = subQuery.getMetaData();
                        subRowHeaders = new String[rowMetadata.getColumnCount()];
                        for (int j = 1; j <= rowMetadata.getColumnCount(); j++) {
                            subRowHeaders[j - 1] = rowMetadata.getColumnName(j);
                        }
                        subQuery.close();
                    } catch (SQLException sqle) {
                        logger.debug("Error (getting header names) (" + username + "): " + sqle.getMessage());
                    }
                }/* else {
                    aliquotParameterList = new ArrayList<String>();
                    String aliquotParameterListQuery = "SELECT DISTINCT parameter_name FROM " + entry + " ORDER BY parameter_name";

                    try {
                        ResultSet subQuery = connection.createStatement().executeQuery(aliquotParameterListQuery);
                        String[] falseHeaders = {"XXXXXXXXXXXXXXXXXX", "XXXXXXXXXXXXXXXXXX", "XXXXXXXXXXXXXXXXXX", "XXXXXXXXXXXXXXXXXX"};
                        for (int j = 0; j < falseHeaders.length; j++) {
                            String falseHeader = falseHeaders[j];
                            aliquotParameterList.add(falseHeader);
                        }
                        while (subQuery.next()) {
                            aliquotParameterList.add(subQuery.getString(1));
                        }
                    } catch (SQLException sqle) {
                        logger.debug("Error (getting aliquot header names) (" + username + "): " + sqle.getMessage());
                    }

                    subRowHeaders = new String[aliquotParameterList.size()];
                    for (int i = 0; i < subRowHeaders.length; i++) {
                        subRowHeaders[i] = aliquotParameterList.get(i) + "_aliquots";
                    }
                }*/

                logger.debug("TEST #3");
                
                tableHeader.addSubTable(subRowHeaders, maxNumberOfEntries);
                logger.debug("TEST #4");
                tableMaxEntryCount.put(entry, maxNumberOfEntries);
                
                logger.debug("TEST #5");
            }
            logger.debug("TEST #6");
        }
        
        //logger.debug("TEST #7");

        if(tableHeader != null){
            //logger.debug("TEST #8");
            return tableHeader.getHeaders();
        }else{
            //logger.debug("TEST #8 - null");
            return new String[0];
        }

    }

    private int getMaxNumberOfEntries(String columnLengthQuery) {

        //This gets the maximum number of entries        
        int maxNumberOfEntries = 1;
        try {
            Statement stmtPatientCount = connection.createStatement();
            ResultSet patientCount = stmtPatientCount.executeQuery(columnLengthQuery);
            if (patientCount.next()) {
                maxNumberOfEntries = patientCount.getInt(1);
            }
            //logger.debug("maxNumberOfEntries (" + username + "): " + maxNumberOfEntries);
            patientCount.close();
        } catch (SQLException sqle) {
            logger.debug("Error (calculating maxNumberOfEntries) (" + username + "): " + sqle.getMessage());
        }
        return maxNumberOfEntries;
    }

    private String[] getRowHeaders(String table_name) {

        //NEED TO REFERENCE THE PARAMETER ARRAY HERE

        //This gets the string array of row headers
        String[] rowHeaders = null;
        try {
            Statement stmtRows = connection.createStatement();

            //String tableQuery = this.getTableQuery(table_name, username) + " LIMIT 1";
            String tableQuery = this.getTableQuery(table_name) + " LIMIT 1";
            ResultSet rows = stmtRows.executeQuery(tableQuery);
            ResultSetMetaData rowMetadata = rows.getMetaData();
            int rowColCount = rowMetadata.getColumnCount();
            rowHeaders = new String[rowColCount];
            for (int i = 1; i <= rowMetadata.getColumnCount(); i++) {
                rowHeaders[i - 1] = rowMetadata.getColumnName(i);
            }
            rows.close();
        } catch (SQLException sqle) {
            logger.debug("Error (" + username + "): " + sqle.getMessage());
        }
        return rowHeaders;
    }

    private void fillExportersWithMultipleEntryContent(GenericExporter[] exporters, String table) throws SQLException, SearchResultsException {

        //NEED TO REFERENCE THE PARAMETER ARRAY HERE (Probably - still not really sure what's going on here...)


        //if (table.endsWith("Biomaterial")) return;
        /*
         * table_multimap: ensat_id -> [table_id, ensat_id, center_id, content]
         * -> [table_id, ensat_id, center_id, content] ensat_id -> [tab ...
         */
        //String tableQuery = this.getTableQuery(table, username);
        String tableQuery = this.getTableQuery(table);

        //logger.debug("tableQuery (fillExportersWithMultipleEntryContent): " + tableQuery);
        
        ResultSet mainTableResultSet = connection.createStatement().executeQuery(tableQuery);
        int mainTableResultSetColumnCount = mainTableResultSet.getMetaData().getColumnCount();
        ResultSetMetaData rsmd = mainTableResultSet.getMetaData();

        int ensatIdIndex = -1;
        if (singleEntryTables.contains(table)) {
            ensatIdIndex = 1;
        } else {
            ensatIdIndex = 2;
        }

        //Multimap<Integer, String[]> tableResultsMultimap = ArrayListMultimap.create();
        Multimap<String, String[]> tableResultsMultimap = ArrayListMultimap.create();
        while (mainTableResultSet.next()) {
            String[] row = new String[mainTableResultSetColumnCount];
                        
            for (int i = 0; i < mainTableResultSetColumnCount; i++) {
                row[i] = mainTableResultSet.getString(i + 1);
                if(table.equals("Pheo_Genetics")){
                    logger.debug("row[" + i + "]: " + row[i]);
                }            
            }

            String fullEnsatId = mainTableResultSet.getString(ensatIdIndex+1) + "-" + mainTableResultSet.getString(ensatIdIndex);
            //logger.debug("mainTableResultSet.getInt(" + ensatIdIndex + "): " + mainTableResultSet.getInt(ensatIdIndex));
            //tableResultsMultimap.put(mainTableResultSet.getInt(ensatIdIndex), row);
            tableResultsMultimap.put(fullEnsatId, row);
        }

        /*
         * subtable_multimaps_map { subtable_name -> multimap table_id ->
         * [subtable_id, table_id, ensat_id, center_id, content] ->
         * [subtable_id, table_id, ensat_id, center_id, content] table_id ->
         * [sub ...] , subtable_name -> multimap table_id -> [subtable_id,
         * table_id, ensat_id, center_id, content] -> [subtable_id, table_id,
         * ensat_id, center_id, content] table_id -> [sub ...] }
         */

        Map<String, Multimap<Integer, String[]>> subTableResultsMultimaps = new HashMap<String, Multimap<Integer, String[]>>();

        //for (String subtable : subTableMap.get(table)) {
        Collection<String> coll = subTableMap.get(table);
        Iterator iter = coll.iterator();
        while (iter.hasNext()) {
            String subtable = (String) iter.next();

            //ResultSet subTableResultSet = connection.createStatement().executeQuery(getSubTableQuery(table, subtable, username));
            ResultSet subTableResultSet = connection.createStatement().executeQuery(getSubTableQuery(table, subtable));
            int subTableResultSetColumnCount = subTableResultSetColumnCount = subTableResultSet.getMetaData().getColumnCount();

            Multimap<Integer, String[]> subTableResultsMultimap = LinkedListMultimap.create();
            while (subTableResultSet.next()) {
                String[] row = new String[subTableResultSetColumnCount];
                for (int i = 0; i < subTableResultSetColumnCount; i++) {
                    row[i] = subTableResultSet.getString(i + 1);
                }
                subTableResultsMultimap.put(subTableResultSet.getInt(2), row);
            }
            subTableResultsMultimaps.put(subtable, subTableResultsMultimap);
        }

        //SortedSet<Integer> sortedEnsatId = new TreeSet<Integer>();
        SortedSet<String> sortedEnsatId = new TreeSet<String>();
        sortedEnsatId.addAll(tableResultsMultimap.keySet());

        if (sortedEnsatId.isEmpty()) {
            throw new SearchResultsException();
        }

        //AM LEAVING THE GODDAM FOR-EACH LOOPS IN THIS LITTLE SEGMENT - CAN'T BE BOTHERED RE-ARRANGING THEM ALL (TOO MANY ITERATORS)
        List<String> rowBuilder = null;
        //for (Integer ensat_id : sortedEnsatId) {
        for (String ensat_id : sortedEnsatId) {

            rowBuilder = new ArrayList<String>();
            for (String[] patientRow : tableResultsMultimap.get(ensat_id)) {
                int table_id = Integer.parseInt(patientRow[0]);
                //first time, add identifiers
                if (rowBuilder.isEmpty()) {
                    rowBuilder.add(formatPatientId(patientRow[2], patientRow[1]));
                }
                rowBuilder.add(patientRow[0]);
                //fill out thingies
                for (int i = 3; i < patientRow.length; i++) {
                    rowBuilder.add(patientRow[i]);
                }

                //do the subtables, ensure padding is done
                for (String subtable : subTableMap.get(table)) {
                    Multimap<Integer, String[]> subTableResultsMultimap = subTableResultsMultimaps.get(subtable);
                    int maxEntries = tableMaxEntryCount.get(subtable);
                    int entryCount = 0;

                    if (!subtable.endsWith("Biomaterial_Aliquots")) {

                        ResultSet columnCountRS = connection.createStatement().executeQuery(String.format("SELECT * FROM %s LIMIT 0", subtable));
                        int columnCount = columnCountRS.getMetaData().getColumnCount() - 4;
                        for (String[] subRow : subTableResultsMultimap.get(table_id)) {
                            for (int i = 4; i < subRow.length; i++) {
                                rowBuilder.add(subRow[i]);
                            }
                            entryCount++;
                        }
                        int totalEntries = entryCount * columnCount;
                        while (totalEntries < maxEntries * columnCount) {
                            rowBuilder.add("");
                            totalEntries++;
                        }

                        //This is for Aliquots only
                    } else {
                        //for each aliquot parameter
                        /*for (int i = 4; i < aliquotParameterList.size(); i++) {
                            String aliquots = "";
                            //check entries for existence of that parameter
                            for (String[] subRow : subTableResultsMultimap.get(table_id)) {
                                int thisRowEnsatId = Integer.parseInt(subRow[2]);
                                if (ensat_id == thisRowEnsatId && subRow[4].equals(aliquotParameterList.get(i))) {
                                    aliquots = subRow[5];
                                }
                            }
                            rowBuilder.add(aliquots);
                        }*/
                    }


                    //for (int i = 0; i < (columnCount-4)*(maxEntries-entryCount); i++ ) {
                    //    rowBuilder.add("");
                    //}
                }

            }

            for (GenericExporter exporter : exporters) {
                exporter.writeLine(rowBuilder.toArray(new String[0]));
            }

        }
    }

    public String formatPatientId(String centre_id, String ensat_id) {
        String ensatPadding = "";
        for (int i = ensat_id.length(); i < 4; i++) {
            ensatPadding += '0';
        }
        return centre_id + "-" + ensatPadding + ensat_id;
    }

    public String compileExportQuerySql(String mod, String dbn, String study, String query, String username, String userCountry, String userCenter) {

        //THIS WILL NEED TO BE MODIFIED BY THE SECURITY POLICY SOMEHOW 

        String queryStr = "";
        if (mod.equals("1")) {
            queryStr = "SELECT ensat_id,center_id FROM Identification WHERE uploader='" + username + "';";
        } else if (mod.equals("2")) {
            queryStr = "SELECT ensat_id,center_id FROM Identification WHERE center_id='" + userCenter + "';";
        } else if (mod.equals("3")) {
            //Grab the country code from the center_id
            String countryCode = userCenter.substring(0, 2);
            queryStr = "SELECT ensat_id,center_id FROM Identification WHERE center_id LIKE '" + countryCode + "%';";
        } else if (mod.equals("4")) {
            String countryCode = userCenter.substring(0, 2);
            queryStr = "SELECT ensat_id,center_id FROM Identification WHERE ensat_database='" + dbn + "' AND center_id LIKE '" + countryCode + "%';";
        } else if (mod.equals("5")) {
            //Now select the particular query from a list of stored queries
            queryStr = this.returnStoredQuery(query, userCenter);
        } else if (mod.equals("6")) {
            //Now select the particular query from a list of studies
            queryStr = this.returnStudyQuery(study);
        }

        return queryStr;
    }

    public String getExportText(String mod, String dbn, String study, String query, String username, String userCountry, String userCenter) {

        String exportText = "";
        if (mod.equals("1")) {
            exportText = "You have chosen to export all of your records (username: <strong>" + username + "</strong>).";
        } else if (mod.equals("2")) {
            exportText = "You have chosen to export all of the records from your center (<strong>" + userCenter + "</strong>).";
        } else if (mod.equals("3")) {
            exportText = "You have chosen to export all of the records from your country (<strong>" + userCountry + "</strong>).";
        } else if (mod.equals("4")) {
            exportText = "You have chosen to export all of your records from the <strong>" + dbn + "</strong> tumor section.";
        } else if (mod.equals("5")) {
            //Now select the particular query from a list of stored queries
            exportText = "You have chosen to export all of your records from the <strong>" + query + "</strong> stored query.";
        } else if (mod.equals("6")) {
            //Now select the particular query from a list of studies
            exportText = "You have chosen to export all of your records from the <strong>" + study + "</strong> study.";
        }

        return exportText;
    }

    private String returnStoredQuery(String query, String userCenter) {

        String storedQuery = "";
        if (query.equals("summary_only")) {
            storedQuery = "SELECT ensat_id,center_id FROM Identification WHERE ensat_database='ACC' ORDER BY center_id,ensat_id;";
        } else if (query.equals("summary_all") || query.equals("acc_quickcheck")) {
            String countryCode = userCenter.substring(0, 2);
            storedQuery = "SELECT ensat_id,center_id FROM Identification WHERE ensat_database='ACC' AND center_id LIKE '" + countryCode + "%' ORDER BY center_id,ensat_id;";
        } else if (query.equals("laterality")) {
            storedQuery = "SELECT ensat_id,center_id FROM Identification ORDER BY center_id,ensat_id;";
        }
        return storedQuery;
    }

    private String returnStudyQuery(String study) {

        String storedStudy = "";
        storedStudy = "SELECT DISTINCT ensat_id,center_id FROM Associated_Studies WHERE study_name='" + study + "' ORDER BY center_id,ensat_id;";
        /*if (study.equals("eurineact")) {
            storedStudy = "SELECT ensat_id,center_id FROM Associated_Studies WHERE study_name='eurineact' ORDER BY center_id,ensat_id;";
        } else if (study.equals("ki67")) {
            storedStudy = "";
        } else if (study.equals("stage3_4")) {
            storedStudy = "SELECT ensat_id,center_id FROM Associated_Studies WHERE study_name='stage_3_4_acc' ORDER BY center_id,ensat_id;";
        } else if (study.equals("pmt")) {
            storedStudy = "";
        } else if (study.equals("pmt3")) {
            storedStudy = "SELECT ensat_id,center_id FROM Associated_Studies WHERE study_name='pmt3' ORDER BY center_id,ensat_id;";
        } else if (study.equals("tma")) {
            storedStudy = "SELECT ensat_id,center_id FROM Associated_Studies WHERE study_name='tma' ORDER BY center_id,ensat_id;";
        } else if (study.equals("ltphpgl")) {
            storedStudy = "";
        } else if (study.equals("avis2")) {
            storedStudy = "";
        } else if (study.equals("acc_pregnancy")) {
            //storedStudy = "SELECT Identification.ensat_id,Identification.center_id FROM Identification, ACC_DiagnosticProcedures, ACC_TumorStaging WHERE Identification.sex='F' AND (ACC_TumorStaging.ensat_classification LIKE 'I %' OR ACC_TumorStaging.ensat_classification LIKE 'II %' OR ACC_TumorStaging.ensat_classification LIKE 'III %') AND (ACC_DiagnosticProcedures.date_of_diagnosis > '1998' AND ACC_DiagnosticProcedures.date_of_diagnosis < '2012') AND (Identification.center_id LIKE 'FR%' OR Identification.center_id='GYWU' OR Identification.center_id='GYBN') AND Identification.ensat_id=ACC_DiagnosticProcedures.ensat_id AND Identification.center_id=ACC_DiagnosticProcedures.center_id AND Identification.ensat_id=ACC_TumorStaging.ensat_id AND Identification.center_id=ACC_TumorStaging.center_id ORDER BY Identification.center_id,Identification.ensat_id;";
            storedStudy = "SELECT Identification.ensat_id,Identification.center_id FROM Identification, ACC_DiagnosticProcedures, ACC_TumorStaging WHERE Identification.sex='F' AND (ACC_TumorStaging.ensat_classification LIKE 'IV %') AND (ACC_DiagnosticProcedures.date_of_diagnosis > '1998' AND ACC_DiagnosticProcedures.date_of_diagnosis < '2013') AND (Identification.center_id LIKE 'FR%' OR Identification.center_id='GYWU' OR Identification.center_id='GYBN') AND Identification.ensat_id=ACC_DiagnosticProcedures.ensat_id AND Identification.center_id=ACC_DiagnosticProcedures.center_id AND Identification.ensat_id=ACC_TumorStaging.ensat_id AND Identification.center_id=ACC_TumorStaging.center_id ORDER BY Identification.center_id,Identification.ensat_id;";
        }*/
        return storedStudy;
    }

    public ArrayList<String> getPatientIdList(String query, Connection conn, String username) {

        ArrayList<String> patientIdList = new ArrayList<String>();
        try {
            PreparedStatement ps = conn.prepareStatement(query);
            
            //THE PARAMETERS NEED TO BE SET HERE...
            
            ResultSet rs = ps.executeQuery();
            
            ResultSetMetaData rsmd = rs.getMetaData();
            int columnNum = rsmd.getColumnCount();
            
            while (rs.next()) {

                String centerIdIn = rs.getString(2);
                String pidIn = "" + rs.getInt(1);
                if (centerIdIn == null) {
                    centerIdIn = "";
                }
                if (pidIn == null) {
                    pidIn = "";
                }
                if (!centerIdIn.equals("") && !pidIn.equals("")) {                    
                    String uploader = rs.getString(columnNum);
                    String ensatId = this.formatPatientId(centerIdIn, pidIn);
                    boolean addId = false;                    
                    if(uploader.equals(username)){                    
                        addId = true;
                    }else if(centerIdIn.equals("FRPA3") && username.equals("segolene.hescot@u-psud.fr")){
                        addId = true;
                    }
                    
                    if(addId){
                        patientIdList.add(ensatId);
                    }
                }
            }
            rs.close();
        } catch (Exception e) {            
            logger.debug("Error compiling patient ID list (" + username + "): " + e.getMessage());
        }
        return patientIdList;
    }
    
    public ArrayList<String> getPatientIdList(String query, Connection conn) {

        ArrayList<String> patientIdList = new ArrayList<String>();
        try {
            PreparedStatement ps = conn.prepareStatement(query);
            
            //THE PARAMETERS NEED TO BE SET HERE...
            
            ResultSet rs = ps.executeQuery();
            
            ResultSetMetaData rsmd = rs.getMetaData();
            int columnNum = rsmd.getColumnCount();
            
            while (rs.next()) {

                String centerIdIn = rs.getString(2);
                String pidIn = "" + rs.getInt(1);
                if (centerIdIn == null) {
                    centerIdIn = "";
                }
                if (pidIn == null) {
                    pidIn = "";
                }
                if (!centerIdIn.equals("") && !pidIn.equals("")) {                    
                    String ensatId = this.formatPatientId(centerIdIn, pidIn);
                    patientIdList.add(ensatId);
                }
            }
            rs.close();
        } catch (Exception e) {            
            logger.debug("Error compiling patient ID list (" + username + "): " + e.getMessage());
        }
        return patientIdList;
    }

    public String getTimeEstimate(ArrayList patientIdList, String mod, String dbn, String study, String querySql, Connection conn) {

        int patientNum = patientIdList.size();
        int paramNum = 1;

        //Analyse the query for the number of parameters

        //If mod=1 --> 4 then the parameter number is the total for the whole database
        //Else if mod=5 or mod=6 then it depends on the query
        if (mod.equals("1") || mod.equals("2") || mod.equals("3") || mod.equals("4")) {
            paramNum = this.getTotalParamNum(dbn, conn);
        } else {
            paramNum = this.analyseQueryParams(querySql);
        }

        //Render time is how long it takes to render one parameter in Apache POI
        //First estimate is 0.1s
        double renderTimeMins = 0.00167;
        double timeMins = patientNum * paramNum * renderTimeMins;

        //Round up to the nearest minute
        String timeMinsStr = "" + timeMins;
        if (timeMinsStr.indexOf(".") != -1) {
            timeMinsStr = timeMinsStr.substring(0, timeMinsStr.indexOf("."));
        }
        return "" + timeMinsStr;
    }

    public int getTotalParamNum(String dbn, Connection conn) {

        int paramCount = 1;
        //Load in all the tables
        Vector<String> allTables = new Vector<String>();
        try {
            String sql = "SHOW TABLES;";
            PreparedStatement ps = conn.prepareStatement(sql);
            ResultSet rs = ps.executeQuery();
            while (rs.next()) {
                String tableIn = rs.getString(1);
                allTables.add(tableIn);
            }
        } catch (Exception e) {
            logger.debug("Error in table-loading (" + username + "): " + e.getMessage());
        }
        
        //Add only the ones that are relevant to this query
        Vector<String> tableNames = new Vector<String>();
        if (dbn.equals("")) {
            for (int i = 0; i < allTables.size(); i++) {
                tableNames.add(allTables.get(i));
            }
        } else {
            tableNames.add("Identification");
            for (int i = 0; i < allTables.size(); i++) {
                String tableIn = allTables.get(i);
                int underscoreIndex = tableIn.indexOf("_");
                if(underscoreIndex != -1){
                    String prefix = tableIn.substring(0, underscoreIndex);
                    if (prefix.equals(dbn)) {
                        tableNames.add(tableIn);
                    }
                }
            }
        }
        
        //Run queries on all of them
        int tableNum = tableNames.size();
        try {
            for (int i = 0; i < tableNum; i++) {
                String sql = "SELECT * FROM " + tableNames.get(i);
                PreparedStatement ps = conn.prepareStatement(sql);
                ResultSet rs = ps.executeQuery();
                ResultSetMetaData rsmd = rs.getMetaData();
                int colNum = rsmd.getColumnCount();
                paramCount += colNum;
            }
        } catch (Exception e) {
            logger.debug("Error (" + username + "): " + e.getMessage());
        }
        
        return paramCount;
    }

    public int analyseQueryParams(String querySql) {

        int paramCount = 0;

        //Find the "FROM" tag
        int fromIndex = querySql.indexOf("FROM");
        int selectIndex = querySql.indexOf("SELECT");

        String paramStr = "";
        if (fromIndex != -1 && selectIndex != -1) {
            paramStr = querySql.substring(selectIndex + 7, fromIndex);
        }

        StringTokenizer st = new StringTokenizer(paramStr, ",");
        paramCount = st.countTokens();
        return paramCount;
    }

    public String getFilepath(String userCountry) {
        String filepath = "";
        filepath += "exported_files/" + userCountry;
        return filepath;
    }

    public String getFilename(String username, String formDate) {

        String filename = "";

        StringTokenizer st = new StringTokenizer(formDate);
        String formDateDay = st.nextToken();
        String formDateMonth = st.nextToken();
        String formDateYear = st.nextToken();
        String formTime = st.nextToken();
        formDate = formDateYear + "" + formDateMonth + "" + formDateDay;

        StringTokenizer st2 = new StringTokenizer(formTime, ":");
        String formHour = st2.nextToken();
        String formMin = st2.nextToken();

        filename += formDate + "_" + formHour + formMin;

        filename += "_" + username;
        filename += "_export";
        //filename += ".xlsx";
        return filename;
    }
    
    public String getPatientSelectionFormHtml(ArrayList<String> patientIdList, String centerid){
        
        int ID_ROW_NUM = 100;
        
        String outputStr = "";
        int idNum = patientIdList.size();
        
        int columnNum = idNum / ID_ROW_NUM;
        int leftoverNum = idNum % ID_ROW_NUM;
        if(leftoverNum != 0){
            columnNum = columnNum + 1;
        }
        
        /*logger.debug("idNum: " + idNum);
        logger.debug("columnNum: " + columnNum);
        logger.debug("leftoverNum: " + leftoverNum);*/
        
        outputStr += "<table border=\"1\" cellpadding=\"5\">";
        outputStr += "<tr><th colspan=\"" + (columnNum * 2) + "\">Select all <input type=\"checkbox\" name=\"selectall\" onchange=\"select_all();\" /> " + this.getCenterSelectionList(patientIdList) + "</th></tr>";
        
        for(int i=0; i<ID_ROW_NUM; i++){            
            outputStr += "<tr>";
            for(int j=0; j<columnNum; j++){                
                int retrievalIndex = i + (ID_ROW_NUM * j);
                if(retrievalIndex < idNum){
                    String ensatId = patientIdList.get(retrievalIndex);
                    outputStr += "<td>" + ensatId + "</td><td><input type=\"checkbox\" name=\"patient_selection\" value=\"" + ensatId + "\"/></td>";
                }
            }
            outputStr += "</tr>";
        }
        outputStr += "</table>";
        return outputStr;
    }
    
    private String getCenterSelectionList(ArrayList<String> patientIdList){
                
        String centerSelectionText = "Select center ";
        centerSelectionText += "<select name=\"selectallcenter\" onchange=\"select_all_centerid(this.value);\">";
        centerSelectionText += "<option value=\"\">[Select...]</option>";
        
        int patientNum = patientIdList.size();
        Vector<String> centerIds = new Vector<String>();
        for(int i=0; i<patientNum; i++){            
            String patientIdIn = patientIdList.get(i);
            if(patientIdIn.indexOf("-") != -1){
                String centerIdIn = patientIdIn.substring(0,patientIdIn.indexOf("-"));
                if(!centerIds.contains(centerIdIn)){
                    centerIds.add(centerIdIn);
                }
            }
        }
        
        int uniqueCenterIdNum = centerIds.size();
        for(int i=0; i<uniqueCenterIdNum; i++){
            String centerId = centerIds.get(i);
            centerSelectionText += "<option value=\"" + centerId + "\">" + centerId + "</option>";
        }
        centerSelectionText += "</select>";
        
        //centerSelectionText += "<input type=\"checkbox\" name=\"selectallcenter\" onchange=\"select_all_centerid('" + centerid + "');\"";
        
        return centerSelectionText;
    }
    
    
    private String getSubTableIdName(String tablename){
        
        String tableIdOut = "";
        tableIdOut = tablename.toLowerCase() + "_id";
        if(tablename.equals("Pheo_BiochemicalAssessment") || tablename.equals("APA_BiochemicalAssessment")){
            tableIdOut = "biochemical_assessment_id";
        }else if(tablename.equals("Pheo_ClinicalAssessment") || tablename.equals("APA_ClinicalAssessment")){
            tableIdOut = "clinical_assessment_id";
        }else if(tablename.equals("Pheo_ImagingTests")){
            tableIdOut = "pheo_imaging_tests_id";
        }else if(tablename.equals("Pheo_NonSurgicalInterventions")){
            tableIdOut = "pheo_non_surgical_interventions_id";
        }else if(tablename.equals("Pheo_TumorDetails")){
            tableIdOut = "pheo_tumor_details_id";
        }else if(tablename.equals("Pheo_Morphological_Progression")){
            tableIdOut = "pheo_morphprog_id";
        }else if(tablename.equals("Pheo_Biological_Assessment")){
            tableIdOut = "pheo_biologassess_id";
        }        
        return tableIdOut;           
    }
    
    public ArrayList<String> restrictList(ArrayList<String> patientIdList, String userCenter){
        
        //Case for multiples with the userCenter
        StringTokenizer st = new StringTokenizer(userCenter,"|");
        int tokenNum = st.countTokens();
        System.out.println("tokenNum: " + tokenNum);
        String[] userCenters = new String[tokenNum];
        for(int i=0; i<tokenNum; i++){
            userCenters[i] = st.nextToken();
        }
        
        ArrayList<String> restrictedList = new ArrayList<String>();
        int patientNum = patientIdList.size();
        for(int i=0; i<patientNum; i++){
            
            String idIn = patientIdList.get(i);
            int hyphenIndex = idIn.indexOf("-");
            if(hyphenIndex != -1){
                String centerIdIn = idIn.substring(0,hyphenIndex);
                boolean centerIdFound = false;
                int centerIdCount = 0;
                while(!centerIdFound && centerIdCount < tokenNum){
                    if(centerIdIn.equals(userCenters[centerIdCount])){
                        if(!restrictedList.contains(idIn)){
                            restrictedList.add(idIn);
                        }                    
                        centerIdFound = true;
                    }else{
                        centerIdCount++;
                    }
                }
            }
        }
        return restrictedList;
    }
    
    
}
