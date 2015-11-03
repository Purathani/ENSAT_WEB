/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package update_main;

import ConnectBean.ConnectionAuxiliary;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.util.Vector;
import java.util.StringTokenizer;
import java.text.SimpleDateFormat;
import java.text.Format;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;
import javax.servlet.ServletContext;

import java.sql.ResultSet;
import java.sql.Statement;

import org.apache.log4j.Level;
import org.apache.log4j.Logger;
import org.apache.log4j.PropertyConfigurator;


/**
 *
 * @author astell
 */
public class PrintLabels {
    
    private static final Logger logger = Logger.getLogger(PrintLabels.class);

    public PrintLabels() {
    }

    public String getFilepath(String country, boolean isWindows) {
        if(isWindows){
            return "exported_files\\" + country;
        }else{
            return "exported_files/" + country;
        }
    }

    public String getFilename(String filepath, HttpServletRequest request, HttpSession session, String username, String centerid, String modid, String pid) {

        String filename = "";

        //Create filename of format "[date]_[username]_[idtoprint]_export.csv"

        //Today's date
        Format formatter = new SimpleDateFormat("dd MM yyyy HH:mm:ss");
        java.util.Date date = new java.util.Date(session.getLastAccessedTime());
        String formDate = formatter.format(date);

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

        String pidDisp = "";
        if (pid.length() == 1) {
            pidDisp = "000" + pid;
        } else if (pid.length() == 2) {
            pidDisp = "00" + pid;
        } else if (pid.length() == 2) {
            pidDisp = "0" + pid;
        } else {
            pidDisp = "" + pid;
        }

        filename += "_" + centerid + pidDisp + "_" + modid;

        return filename;
    }

    public Vector<String> getBioTablenames(String dbn) {

        Vector<String> bioInfo = new Vector<String>();
        if (dbn.equals("ACC")) {
            bioInfo.add("ACC_Biomaterial");
            bioInfo.add("ACC_Biomaterial_Normal_Tissue");
            bioInfo.add("acc_biomaterial_id");
        } else if (dbn.equals("Pheo")) {
            bioInfo.add("Pheo_Biomaterial");
            bioInfo.add("Pheo_Biomaterial_Normal_Tissue");
            bioInfo.add("pheo_biomaterial_id");
        } else if (dbn.equals("NAPACA")) {
            bioInfo.add("NAPACA_Biomaterial");
            bioInfo.add("NAPACA_Biomaterial_Normal_Tissue");
            bioInfo.add("napaca_biomaterial_id");
        } else if (dbn.equals("APA")) {
            bioInfo.add("APA_Biomaterial");
            bioInfo.add("APA_Biomaterial_Normal_Tissue");
            bioInfo.add("apa_biomaterial_id");
        }
        return bioInfo;
    }

    public Vector<String> getBiomaterialInfo(String dbn, Vector<String> bioInfo, String pid, String centerid, String modid, HttpServletRequest request, Statement statement) {

        String bioTable = bioInfo.get(0);        
        String bioId = bioInfo.get(2);

        //Collect all the data about the biomaterial form (non-multiple)

        //String[][] bioOutput = null;
        Vector<String> bioOutput = new Vector<String>();
        int bioOutputNum = 0;

        /*if (dbn.equals("Pheo")) {
            bioOutputNum = 18;
        } else {
            bioOutputNum = 17;
        }*/
        bioOutputNum = 19;
        //bioOutput = new String[2][bioOutputNum];

        UpdateSub us = new UpdateSub();
        String biomaterialQuery = "SELECT * FROM " + bioTable + " WHERE ensat_id=" + pid + " AND center_id='" + centerid + "' AND " + bioId + "=" + modid + ";";
        try {
            ResultSet bio_rs = statement.executeQuery(biomaterialQuery);

            while (bio_rs.next()) {
                for (int i = 0; i < bioOutputNum; i++) {
                    String bioInfoIn = "";
                    if (i != 16 && i != 17) {
                        bioInfoIn = bio_rs.getString(i + 1);
                        
                        //Convert the date if necessary
                        if(i == 3){                            
                            bioInfoIn = us.convertDate(bioInfoIn);
                        }

                    } else if (i == 16) {
                        /*if (dbn.equals("Pheo")) {
                            bioInfoIn = bio_rs.getString(20);
                        } else {
                            bioInfoIn = bio_rs.getString(19);
                        }*/
                        bioInfoIn = bio_rs.getString(21);
                    } else if (i == 17) {
                        /*if (dbn.equals("Pheo")) {
                            bioInfoIn = bio_rs.getString(21);
                        } else {
                            bioInfoIn = bio_rs.getString(20);
                        }*/
                        bioInfoIn = bio_rs.getString(22);
                    }
                    bioOutput.add(bioInfoIn);
                }
            }
            bio_rs.close();
        } catch (Exception e) {
            logger.debug("Error (getBiomaterialInfo): " + e.getMessage());
        }
        return bioOutput;
    }

    public Vector<Vector> getBiomaterialMultipleInfo(String dbn, Vector<String> bioInfo, String pid, String centerid, String modid, HttpServletRequest request, Statement statement) {

        String bioMultTable = bioInfo.get(1);
        String bioId = bioInfo.get(2);

        int bioMultNum = 0;
        //String[][] bioMultOutput = null;
        Vector<Vector> bioMultOutput = new Vector<Vector>();

        String biomaterialQueryMult = "SELECT * FROM " + bioMultTable + " WHERE ensat_id=" + pid + " AND center_id='" + centerid + "' AND " + bioId + "=" + modid + ";";
        
        //System.out.println("biomaterialQueryMult: " + biomaterialQueryMult);
        try {
            ResultSet bio_mult_rs = statement.executeQuery(biomaterialQueryMult);

            while (bio_mult_rs.next()) {
                bioMultNum++;
            }

            //bioMultOutput = new String[2][bioMultNum];
            bio_mult_rs = statement.executeQuery(biomaterialQueryMult);

            int bioMultCount = 0;
            while (bio_mult_rs.next()) {
                String bioInfoType = bio_mult_rs.getString(5);
                String bioInfoDetail = bio_mult_rs.getString(6);
                Vector<String> bioInfoIn = new Vector<String>();
                bioInfoIn.add(bioInfoType);
                bioInfoIn.add(bioInfoDetail);
                bioMultOutput.add(bioInfoIn);
                bioMultCount++;
            }
            bio_mult_rs.close();

        } catch (Exception e) {
            logger.debug("Error (getBiomaterialMultipleInfo): " + e.getMessage());
        }
        
        /*for(int i=0; i<bioMultOutput.size(); i++){
            Vector<String> bioMultIn = bioMultOutput.get(i);
            for(int j=0; j<bioMultIn.size(); j++){
                logger.debug("bioMultOutput(" + i + ")(" + j + "): " + bioMultIn.get(j));
            }
            
        }*/
        
        
        return bioMultOutput;
    }

    public Vector<String> setLabels(String dbn) {

        Vector<String> bioLabels = new Vector<String>();
        /**
         * bioOutput array:
         *
         * 4 = tumor_tissue_frozen 5 = tumor_tissue_ensat_sop 6 =
         * tumor_tissue_paraffin 7 = tumor_tissue_dna 8 = leukocyte_dna 9 =
         * plasma 10 = serum 11 = 24h_urine 12 = 24h_urine_vol 13 = spot_urine
         * 14 = normal_tissue 15 = normal_tissue_specify 16 =
         * normal_tissue_paraffin 17 = normal_tissue_paraffin_specify 18 =
         * normal_tissue_dna 19 = normal_tissue_dna_specify 20 =
         * associated_study 21 = associated_study_phase_visit
         *
         */
        //Add in the blanks covering IDs and dates
        for (int i = 0; i < 4; i++) {
            bioLabels.add("");
        }

        bioLabels.add("Tumor Tissue (Frozen)");
        bioLabels.add("Tumor Tissue (ENSAT SOP)");
        bioLabels.add("Tumor Tissue (Paraffin)");
        bioLabels.add("Tumor Tissue (DNA)");
        bioLabels.add("Leukocyte DNA");
        bioLabels.add("EDTA Plasma");
        bioLabels.add("Heparin Plasma");
        bioLabels.add("Serum");
        bioLabels.add("24h Urine");
        bioLabels.add("24h Urine (Volume)");
        bioLabels.add("Spot Urine");
        bioLabels.add("Associated Study");
        bioLabels.add("Associated Study Phase/Visit");
        //if (dbn.equals("Pheo")) {
            bioLabels.add("Whole Blood");
        //}
        return bioLabels;
    }
    
    public Vector<String> setNames(String dbn) {

        Vector<String> bioNames = new Vector<String>();
        /**
         * bioOutput array:
         *
         * 4 = tumor_tissue_frozen 5 = tumor_tissue_ensat_sop 6 =
         * tumor_tissue_paraffin 7 = tumor_tissue_dna 8 = leukocyte_dna 9 =
         * plasma 10 = serum 11 = 24h_urine 12 = 24h_urine_vol 13 = spot_urine
         * 14 = normal_tissue 15 = normal_tissue_specify 16 =
         * normal_tissue_paraffin 17 = normal_tissue_paraffin_specify 18 =
         * normal_tissue_dna 19 = normal_tissue_dna_specify 20 =
         * associated_study 21 = associated_study_phase_visit
         *
         */
        //Add in the blanks covering IDs and dates
        for (int i = 0; i < 4; i++) {
            bioNames.add("");
        }

        bioNames.add("tumor_tissue_frozen");
        bioNames.add("tumor_tissue_ensat_sop");
        bioNames.add("tumor_tissue_paraffin");
        bioNames.add("tumor_tissue_dna");
        bioNames.add("leukocyte_dna");
        bioNames.add("plasma");
        bioNames.add("heparin_plasma");
        bioNames.add("serum");
        bioNames.add("24h_urine");
        bioNames.add("24h_urine_vol");
        bioNames.add("spot_urine");
        bioNames.add("associated_study");
        bioNames.add("associated_study_phase_visit");
        //if (dbn.equals("Pheo")) {
            bioNames.add("whole_blood");
            bioNames.add("blood_clot");
        //}
        return bioNames;
    }

    public int getTotalLabelNum(Vector<String> bioOutput, Vector<String> aliquotNumbers) {

        int bioOutputNum = bioOutput.size();

        int totalLabelNum = 0;
        for (int i = 0; i < bioOutputNum; i++) {
            //if(bioOutput.get(i).equals("Yes")){
            if (i != 5 && i != 13) {
                String aliquotNumStr = aliquotNumbers.get(i);
                //System.out.println("aliquotNumStr: " + aliquotNumStr);
                try {
                    int aliquotNumInt = Integer.parseInt(aliquotNumStr);
                    totalLabelNum += aliquotNumInt;
                    //System.out.println("totalLabelNum: " + totalLabelNum);
                } catch (NumberFormatException nfe) {
                    logger.debug("NumberFormatException (getTotalLabelNum): " + nfe.getMessage());
                }
            }
            //}
        }
        return totalLabelNum;
    }
    
    public int getTotalMultLabelNum(Vector<Vector> bioMultOutput, Vector<Vector> aliquotMultNumbers) {

        int bioMultOutputNum = bioMultOutput.size();

        int totalLabelNum = 0;
        for (int i = 0; i < bioMultOutputNum; i++) {
            Vector<String> aliquotNumIn = aliquotMultNumbers.get(i);
            String aliquotNumStr = aliquotNumIn.get(1);                
            try {
                int aliquotNumInt = Integer.parseInt(aliquotNumStr);
                totalLabelNum += aliquotNumInt;                
            } catch (NumberFormatException nfe) {
                logger.debug("NumberFormatException (getTotalLabelNum): " + nfe.getMessage());
            }
        }
        return totalLabelNum;
    }

    public int getYesCount(Vector<String> bioOutput) {

        int bioOutputNum = bioOutput.size();

        int yesCount = 0;
        for (int i = 0; i < bioOutputNum; i++) {
            if (bioOutput.get(i).equals("Yes")) {
                if (i != 5 && i != 13) {
                    yesCount++;
                }
            }
        }
        return yesCount;
    }

    public Vector<String> getAliquotNumbers(HttpServletRequest request, Vector<String> bioOutput, Vector<String> bioNames) {

        int bioOutputNum = bioOutput.size();

        //String[] aliquotNumbers = new String[bioOutputNum];
        Vector<String> aliquotNumbers = new Vector<String>();
        for (int i = 0; i < bioOutputNum; i++) {            
            String bioName = bioNames.get(i);
            String aliquotNumberIn = request.getParameter("aliquot_" + bioName);
            if (aliquotNumberIn == null || aliquotNumberIn.equals("null")) {
                aliquotNumbers.add("0");
            } else {
                aliquotNumbers.add(aliquotNumberIn);
            }
        }

        return aliquotNumbers;
    }

    public String getPdfFilename(String filenameSuffix, String filepath, String exportStorageRoot) {
        String pdfFilename = "" + exportStorageRoot + filepath + "/" + filenameSuffix;
        return pdfFilename;
    }

    public String getFilenameSuffix(String filename) {
        return filename + "_export.pdf";
    }

    public String getPageForwardName(String filepath, String filenameSuffix, boolean isWindows) {
        if(isWindows){
            logger.debug("pageForwardName: " + "\\" + filepath + "\\" + filenameSuffix);
            return "..\\..\\" + filepath + "\\" + filenameSuffix;
        }else{
            return "../../" + filepath + "/" + filenameSuffix;
        }
    }

    public String getImageFilenameSuffix(String filepath, String filename, int labelCount, int index, String exportStorageRoot, boolean isWindows) {
        if(isWindows){        
            return "" + exportStorageRoot + filepath + "/" + filename + "_" + (labelCount + 1) + "_" + (index + 1) + "";
        }else{
            return "" + exportStorageRoot + filepath + "\\" + filename + "_" + (labelCount + 1) + "_" + (index + 1) + "";
        }
    }

    public Vector<String> getLabelOutputStr(int totalLabelNum, Vector<String> bioOutput, Vector<String> aliquotNumbers, String centerid, String pid, String modid, Vector<String> bioLabels, ServletContext context) {

        //String[] labelOutputStr = new String[totalLabelNum];
        Vector<String> labelOutputStr = new Vector<String>();
        int bioOutputNum = bioOutput.size();
        int labelCount = 0;
        
        String study = bioOutput.get(16);        
        String phasevisit = bioOutput.get(17);
        if(study == null){
            study = "";
        }
        if(phasevisit == null){
            phasevisit = "";
        }
        //logger.debug("study: " + study);
        //logger.debug("phasevisit: " + phasevisit);
        
        for (int i = 0; i < bioOutputNum; i++) {
            //System.out.println("" + i + ": " + bioOutput.get(i));
            if (bioOutput.get(i).equals("Yes")) {
                //Elem 5 is "Following ENSAT SOP"
                if (i != 5) {
                    int thisAliquotNum = Integer.parseInt(aliquotNumbers.get(i));

                    //System.out.println("thisAliquotNum: " + thisAliquotNum);
                    for (int j = 0; j < thisAliquotNum; j++) {

                        if (i != 12 && i != 13 && i != 4) {
                            String thisLabelStr = "";
                            thisLabelStr = centerid + "-" + pid + "\r\nbio-ID " + modid + "\r\nStudy: " + study;                            
                            thisLabelStr += this.getStudyString(study, phasevisit);
                            thisLabelStr += "\r\nDate: " + bioOutput.get(3) + "\r\n" + bioLabels.get(i) + "\r\nAliquot: " + (j + 1) + "";
                            labelOutputStr.add(thisLabelStr);
                        } else if (i == 12) {
                            String thisLabelStr = "";
                            //Elem 12 is "24h urine" which relates to the volume (element 13)
                            thisLabelStr = centerid + "-" + pid + "\r\nbio-ID " + modid + "\r\nStudy: " + study;
                            thisLabelStr += this.getStudyString(study, phasevisit);
                            thisLabelStr += "\r\nDate: " + bioOutput.get(3) + "\r\n" + bioLabels.get(12) + " (" + bioOutput.get(13) + " ml)\r\nAliquot: " + (j + 1) + "";
                            labelOutputStr.add(thisLabelStr);
                        } else if (i == 4) {
                            String thisLabelStr = "";
                            //Elem 4 is "Tumor Tissue (Frozen)" - which relates to element 5
                            boolean ensatSopLabel = bioOutput.get(5).equals("Yes");
                            thisLabelStr = centerid + "-" + pid + "\r\nbio-ID " + modid + "\r\nStudy: " + study;
                            thisLabelStr += this.getStudyString(study, phasevisit);
                            thisLabelStr += "\r\nDate: " + bioOutput.get(3) + "\r\n" + bioLabels.get(i) + "\r\nAliquot: " + (j + 1) + "";
                            labelOutputStr.add(thisLabelStr);
                        }                        
                        labelCount++;
                    }
                }
            }            
            //System.out.println("labelCount: " + labelCount + "");
        }
        return labelOutputStr;
    }
        
    public Vector<String> getLabelOutputMultStr(int totalMultLabelNum, Vector<Vector> bioMultOutput, Vector<Vector> aliquotMultNumbers, String centerid, String pid, String modid, Vector<String> bioOutput) {

        
        Vector<String> labelOutputStr = new Vector<String>();
        int bioMultNum = bioMultOutput.size();
        int labelCount = 0;
        
        //System.out.println("bioMultNum: " + bioMultNum);

        for(int i=0; i<bioMultNum; i++){    
    
            Vector<String> bioMultIn = bioMultOutput.get(i);
            
            int thisAliquotNum = 0;
            Vector<String> aliquotMultIn = aliquotMultNumbers.get(i);
            thisAliquotNum = Integer.parseInt(aliquotMultIn.get(1));            
            String outputLabel = "";
            if(bioMultIn.get(0).equals("frozen")){                
                outputLabel = "Normal - Frozen";
            }else if(bioMultIn.get(0).equals("paraffin")){                
                outputLabel = "Normal - Paraffin";
            }else if(bioMultIn.get(0).equals("dna")){                
                outputLabel = "Normal - DNA";
            }

            for(int j=0; j<thisAliquotNum; j++){        
                
                String material = bioMultIn.get(1);
                
                String study = bioOutput.get(15);
                String phasevisit = bioOutput.get(16);
                String dateValue = bioOutput.get(3);
                
                String thisLabelStr = "";
                thisLabelStr = centerid + "-" + pid + "\r\nbio-ID " + modid + "\r\nStudy: " + study;
                thisLabelStr += this.getStudyString(study, phasevisit);  
                thisLabelStr += "\r\nDate: " + dateValue + "\r\n" + outputLabel + " (" + material + ")\r\nAliquot: " + (j + 1) + "";
                labelOutputStr.add(thisLabelStr);
                labelCount++;            
            }            
        }

        return labelOutputStr;
    }    

    private String getStudyString(String studyname, String phasevisit) {
        String studyStr = "";
        if (studyname.equals("PMT") || studyname.equals("German Conn Registry") || studyname.equals("German Cushing Registry") || studyname.equals("FIRST-MAPPP")) {
            studyStr += " (" + phasevisit + ")";
        }
        //System.out.println("studyStr: " + studyStr);
        return studyStr;
    }
    
    /*public int getMultipleCount(String multipleType, Vector<Vector> bioMultOutput){
        
        int bioMultNum = bioMultOutput.size();
        int multCount = 0;
        
        for(int i=0; i<bioMultNum; i++){    
            Vector<String> bioMultIn = bioMultOutput.get(i);
            if(bioMultIn.get(0).equals(multipleType)){
                multCount++;
            }
        }
        return multCount;
    }*/
    
    public Vector<Vector> getMultipleAliquotCount(Vector<Vector> bioMultOutput, HttpServletRequest request){
        
        int bioMultNum = bioMultOutput.size();
        Vector<Vector> multAliquotNums = new Vector<Vector>();
        
        //logger.debug("bioMultNum: " + bioMultNum);
        
        for(int i=0; i<bioMultNum; i++){    
            Vector<String> bioMultIn = bioMultOutput.get(i);
            String multipleType = bioMultIn.get(0);
            String requestTag = "";
            if(multipleType.equals("frozen")){
                requestTag = "aliquot_normal_tissue_";
            }else if(multipleType.equals("paraffin")){
                requestTag = "aliquot_normal_tissue_paraffin_";
            }else if(multipleType.equals("dna")){
                requestTag = "aliquot_normal_tissue_dna_";
            }
                
            String material = bioMultIn.get(1);
            String paramName = requestTag + material.toLowerCase();
            int aliquotNum = 0;
            try{
                String aliquotNumberIn = request.getParameter(paramName);
                aliquotNum = Integer.parseInt(aliquotNumberIn);        
            }catch(NumberFormatException nfe){
                aliquotNum = 0;
            }
            Vector<String> multAliquotOut = new Vector<String>();
            multAliquotOut.add(paramName);
            multAliquotOut.add("" + aliquotNum);
            multAliquotNums.add(multAliquotOut);
        }
        
        /*for(int i=0; i<multAliquotNums.size(); i++){
            Vector<String> multAliquotIn = multAliquotNums.get(i);
            for(int j=0; j<multAliquotIn.size(); j++){
                logger.debug("multAliquotNums(" + i + ")(" + j + "): " + multAliquotIn.get(j));
            }
        }*/
        
        return multAliquotNums;        
    }
    
    
    /*public int getMultipleLabelCount(int frozenCount, int frozenAliquotNum, int paraffinCount, int paraffinAliquotNum, int dnaCount, int dnaAliquotNum){        
        int multLabelCount = 0;
        multLabelCount += frozenCount*frozenAliquotNum;
        multLabelCount += paraffinCount*paraffinAliquotNum;
        multLabelCount += dnaCount*dnaAliquotNum;
        
        System.out.println("multLabelCount: " + multLabelCount);
        
        return multLabelCount;
    }*/
    
}
