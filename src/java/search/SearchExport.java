package search;

/**
 * Method tools for displaying the exported files
 *
 * @author Anthony Stell @copy University of Melbourne, 2012
 */
import ConnectBean.ConnectionAuxiliary;
import java.io.File;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.ResultSetMetaData;

import java.util.StringTokenizer;
import java.util.Vector;
import java.util.Arrays;

import java.text.SimpleDateFormat;
import java.text.Format;
import org.apache.log4j.Level;
import org.apache.log4j.Logger;
import org.apache.log4j.PropertyConfigurator;

import javax.servlet.ServletContext;

public class SearchExport {

    private static final Logger logger = Logger.getLogger(SearchExport.class);
    private String username = "";

    public SearchExport() {
    }

    public String getFileListHtml(String username, String userCountry, String exportRoot) {

        String outputStr = "";

        String foldername = "";
        String filepath = "";

        //Put into role-specific folder
        filepath += "exported_files/" + userCountry;

        //Get a listing of all the files under the folder of that specific user's country
        foldername = exportRoot + filepath + "/";

        File folderName = new File(foldername);

        Vector<String> orderedFilenames = new Vector<String>();
        boolean errorInFolder = false;
        if (folderName.exists() && folderName.isDirectory()) {
            File[] files = folderName.listFiles();
            int fileNum = files.length;
            String[] filenames = new String[fileNum];

            if (fileNum == 0) {
                outputStr = "<p>There are no exported files currently available.</p>";
            } else {
                for (int i = 0; i < fileNum; i++) {
                    filenames[i] = files[i].getName();

                    //Check that the username is contained in the retrieved filename
                    boolean userFile = this.filenameUserCheck(filenames[i], username);

                    if (userFile) {

                        boolean formatCheck = this.filenameFormatCheck(filenames[i]);
                        if (formatCheck) {
                            orderedFilenames.add(filenames[i]);
                        }
                    }
                }
            }
        } else {
            errorInFolder = true;
        }

        orderedFilenames = this.orderFilenames(orderedFilenames);

        int filesToDisplayNum = orderedFilenames.size();
        if (filesToDisplayNum > 0) {

            String lastSectionFlag = "";
            int sectionIndex = 0;
            for (int i = 0; i < filesToDisplayNum; i++) {
                String filenameIn = orderedFilenames.get(i);

                StringTokenizer st = new StringTokenizer(filenameIn, "_");
                String fileDateStr = "";
                if (st.countTokens() > 2) {
                    st.nextToken();
                    fileDateStr = st.nextToken();
                }

                String thisSectionFlag = "";
                long timeDiff = this.getTimeDiff(fileDateStr);

                if (timeDiff < 86400000) {
                    thisSectionFlag = "Today";
                } else if (timeDiff >= 86400000 && timeDiff < 604800000) {
                    thisSectionFlag = "Less than a week ago";
                } else if (timeDiff >= 604800000 && timeDiff < 1209600000) {
                    thisSectionFlag = "Over a week ago";
                } else {
                    thisSectionFlag = "Over two weeks ago";
                }

                if (i == 0) {
                    outputStr += "<h3>" + thisSectionFlag + "</h3>";
                    sectionIndex++;
                } else if (thisSectionFlag.equals(lastSectionFlag)) {
                    sectionIndex++;
                } else {
                    sectionIndex = 0;
                    outputStr += "<h3>" + thisSectionFlag + "</h3>";
                }
                lastSectionFlag = thisSectionFlag;

                outputStr += "<p><a target=\"_blank\" href=\"/" + filepath + "/" + filenameIn + "\">" + filenameIn + "</a></p>";
            }
        } else {
            if (errorInFolder) {
                outputStr = "<p>An error occurred, please try again or contact the system administrator.</p>";
            } else {
                outputStr = "<p>There are no export files to display</p>";
            }
        }

        return outputStr;
    }

    private boolean filenameUserCheck(String filenameIn, String username) {

        boolean userFile = false;

        StringTokenizer st = new StringTokenizer(filenameIn, "_");
        while (st.hasMoreTokens() && !userFile) {
            userFile = st.nextToken().trim().equals(username);
        }
        return userFile;
    }

    private boolean filenameFormatCheck(String filenameIn) {

        StringTokenizer st = new StringTokenizer(filenameIn, "_");
        int tokenCount = st.countTokens();
        if (tokenCount < 5) {
            return false;
        } else {
            String combinedText = st.nextToken();
            String dateToken = st.nextToken();
            String timeToken = st.nextToken();

            if (!combinedText.equals("Combined")) {
                return false;
            } else if (dateToken.length() != 8) {
                return false;
            } else if (timeToken.length() != 4) {
                return false;
            } else {
                try {
                    int dateInt = Integer.parseInt(dateToken);
                    int timeInt = Integer.parseInt(timeToken);
                    return true;
                } catch (NumberFormatException nfe) {
                    return false;
                }
            }
        }
    }

    private Vector<String> orderFilenames(Vector<String> filenames) {

        int fileNum = filenames.size();
        String[] filenamesArray = new String[fileNum];
        for (int i = 0; i < fileNum; i++) {
            filenamesArray[i] = filenames.get(i);
        }

        //Sorts into ascending order...
        Arrays.sort(filenamesArray);

        //But we want descending...
        Vector<String> filenamesOut = new Vector<String>();
        for (int i = fileNum - 1; i >= 0; i--) {
            filenamesOut.add(filenamesArray[i]);
        }
        return filenamesOut;
    }

    private long getTimeDiff(String filenameIn) {

        SimpleDateFormat formatter = new SimpleDateFormat("yyyyMMdd");

        long diffMilli = 0;
        try {
            //File date
            java.util.Date fileDate = formatter.parse(filenameIn);

            //Today's date
            java.util.Date todayDate = new java.util.Date();

            diffMilli = todayDate.getTime() - fileDate.getTime();

        } catch (Exception e) {
            logger.debug("Parsing error: " + e.getMessage());
        }
        return diffMilli;
    }

    public String getBioTable(ServletContext context) {

        String outputTable = "";

        try {
            ConnectionAuxiliary aux = new ConnectionAuxiliary();
            Connection conn = null;
            if (context != null) {
                conn = aux.getAuxiliaryConnection(context, "main");
            }

            String[] dbs = {"ACC", "Pheo", "NAPACA", "APA"};
            String[] labels = {"serum","24h_urine","spot_urine","tumor_tissue_frozen"};//tumor_tissue_paraffin, tumor_tissue_dna
            int sectionCount = dbs.length;
            int labelCount = labels.length;
            int[][] bioCounts = new int[labelCount][sectionCount];
            for (int i = 0; i < labelCount; i++) {
                for (int j = 0; j < sectionCount; j++) {
                    bioCounts[i][j] = 0;
                }
            }

            for (int i = 0; i < labelCount; i++) {

                for(int j=0; j < sectionCount; j++){
                
                    String sql = "SELECT * FROM " + dbs[j] + "_Biomaterial WHERE " + labels[i] + "='Yes';";
                    PreparedStatement ps = conn.prepareStatement(sql);
                    ResultSet rs = ps.executeQuery();

                    while (rs.next()) {                    
                        bioCounts[i][j]++;
                    }
                    logger.debug("sql: " + sql);
                    logger.debug("Count for dbs=" + dbs[i] + " and labels=" + labels[j] + " is: " + bioCounts[i][j]);
                    
                    ps.close();                    
                }
            }

            outputTable = "<table cellpadding='10' border='1'>";
            outputTable += "<tr><th></th><th>ACC</th><th>Pheo</th><th>NAPACA</th><th>APA</th></tr>";
            for (int i = 0; i < labelCount; i++) {
                outputTable += "<tr>";
                String rowLabel = "";
                if (i == 0) {
                    rowLabel = "Serum";
                } else if (i == 1) {
                    rowLabel = "24h urine";
                } else if (i == 2) {
                    rowLabel = "Spot urine";
                } else if (i == 3) {
                    rowLabel = "Tumor tissue";
                }
                outputTable += "<td>" + rowLabel + "</td>";

                for (int j = 0; j < sectionCount; j++) {
                    outputTable += "<td>" + bioCounts[i][j] + "</td>";
                }
                outputTable += "</tr>";
            }
            outputTable += "</table>";

        } catch (Exception e) {
            logger.debug("Error: " + e.getMessage());
        }

        return outputTable;
    }

    private Vector<Vector> addToCenterDistn(Vector<Vector> centerDistn, String centerIn) {

        //Check list of center IDs
        int centerDistNum = centerDistn.size();
        //Bootstrap the list if it's the first
        if (centerDistNum == 0 && !centerIn.equals("")) {
            Vector<String> centerDist = new Vector<String>();
            centerDist.add(centerIn);
            centerDist.add("1");
            centerDistn.add(centerDist);
        }
        String centerDistNumberIn = "";
        int centerDistCount = 0;
        boolean centerFound = false;
        while (!centerFound && centerDistCount < centerDistNum) {
            String centerDistNameIn = (String) centerDistn.get(centerDistCount).get(0);
            if (centerIn.equals(centerDistNameIn)) {
                centerFound = true;
                centerDistNumberIn = (String) centerDistn.get(centerDistCount).get(1);
                try {
                    int centerDistNumberInt = Integer.parseInt(centerDistNumberIn);
                    centerDistNumberInt++;
                    centerDistNumberIn = "" + centerDistNumberInt;
                } catch (Exception e) {
                    centerDistNumberIn = "1";
                }
            } else {
                centerDistCount++;
            }
        }

        if (centerFound) {
            Vector<String> centerDist = new Vector<String>();
            centerDist.add(centerIn);
            centerDist.add(centerDistNumberIn);
            centerDistn.set(centerDistCount, centerDist);
        } else {
            if(!centerIn.equals("")){
                Vector<String> centerDist = new Vector<String>();
                centerDist.add(centerIn);
                centerDist.add("1");
                centerDistn.add(centerDist);
            }
        }
        return centerDistn;
    }
    
    public Vector<Vector> getLimitedStudyNumbers(String study, ServletContext context){
        
        try {
            ConnectionAuxiliary aux = new ConnectionAuxiliary();
            Connection conn = null;
            if (context != null) {
                conn = aux.getAuxiliaryConnection(context, "main");
            }

            String sql = "SELECT DISTINCT center_id,ensat_id FROM Identification WHERE ensat_database='Pheo';";
            PreparedStatement ps = conn.prepareStatement(sql);
            ResultSet rs = ps.executeQuery();

            int totalPheoNum = 0;
            Vector<Vector> centerDistn = new Vector<Vector>();
            while (rs.next()) {
                String centerIn = rs.getString(1);
                centerDistn = this.addToCenterDistn(centerDistn, centerIn);
                totalPheoNum++;
            }
            
            int centerDistNum = centerDistn.size();
            logger.debug("centerDistNum: " + centerDistNum);
            for (int i = 0; i < centerDistNum; i++) {
                Vector<String> centerIn = centerDistn.get(i);
                logger.debug("" + centerIn.get(0) + ": " + centerIn.get(1));
            }
            logger.debug("Total number of pheo patients: " + totalPheoNum);

        } catch (Exception e) {
            logger.debug("Error: " + e.getMessage());
        }

        int studyNumber = 0;
        String sql = "";

        String[] tables = {"Pheo_FollowUp", "Pheo_ImagingTests", "Pheo_BiochemicalAssessment"};

        Vector<Vector> ltphpglIds = new Vector<Vector>();
        Vector<Vector> centerDistn = new Vector<Vector>();
        try {
            ConnectionAuxiliary aux = new ConnectionAuxiliary();
            Connection conn = null;
            if (context != null) {
                conn = aux.getAuxiliaryConnection(context, "main");
            }

            for (int i = 0; i < 3; i++) {

                sql = "SELECT DISTINCT Pheo_PatientHistory.center_id,Pheo_PatientHistory.ensat_id ";
                sql += "FROM Pheo_PatientHistory, Pheo_Surgery, " + tables[i] + " ";
                sql += "WHERE Pheo_PatientHistory.center_id=Pheo_Surgery.center_id ";
                sql += "AND Pheo_PatientHistory.ensat_id=Pheo_Surgery.ensat_id ";
                sql += "AND Pheo_Surgery.center_id=" + tables[i] + ".center_id AND Pheo_Surgery.ensat_id=" + tables[i] + ".ensat_id ";
                sql += ";";

                //logger.debug("sql: " + sql);
                PreparedStatement ps = conn.prepareStatement(sql);
                ResultSet rs = ps.executeQuery();

                while (rs.next()) {
                    String centerid = rs.getString(1);
                    String pid = rs.getString(2);
                    Vector<String> ltphpglId = new Vector<String>();
                    ltphpglId.add(centerid);
                    ltphpglId.add(pid);

                    boolean listContainsId = false;
                    int idCount = 0;
                    while (!listContainsId && (idCount < ltphpglIds.size())) {
                        Vector<String> idIn = ltphpglIds.get(idCount);
                        String centerIdIn = idIn.get(0);
                        String ensatIdIn = idIn.get(1);
                        if (centerIdIn.equals(centerid) && ensatIdIn.equals(pid)) {
                            listContainsId = true;
                        } else {
                            idCount++;
                        }
                    }
                    if (!listContainsId) {
                        ltphpglIds.add(ltphpglId);
                        centerDistn = this.addToCenterDistn(centerDistn, centerid);
                    }
                }
            }
        } catch (Exception e) {
            logger.debug("Error: " + e.getMessage());
        }
        
        int centerDistNum = centerDistn.size();
        logger.debug("centerDistNum: " + centerDistNum);
        for (int i = 0; i < centerDistNum; i++) {
            Vector<String> centerIn = centerDistn.get(i);
            logger.debug("" + centerIn.get(0) + ": " + centerIn.get(1));
        }

        //Number of pheo patients that have a surgery form and one of biochem, follow-up or imaging
        studyNumber = ltphpglIds.size();
        logger.debug("#1) Number of pheo patients with surgery and one of biochemical, follow-up or imaging: " + studyNumber);
        return ltphpglIds;        
    }

    public int getStudyNumbers(String study, ServletContext context) {

        try {
            ConnectionAuxiliary aux = new ConnectionAuxiliary();
            Connection conn = null;
            if (context != null) {
                conn = aux.getAuxiliaryConnection(context, "main");
            }

            String sql = "SELECT DISTINCT center_id,ensat_id FROM Identification WHERE ensat_database='Pheo';";
            PreparedStatement ps = conn.prepareStatement(sql);
            ResultSet rs = ps.executeQuery();

            int totalPheoNum = 0;
            Vector<Vector> centerDistn = new Vector<Vector>();
            while (rs.next()) {
                String centerIn = rs.getString(1);
                centerDistn = this.addToCenterDistn(centerDistn, centerIn);
                totalPheoNum++;
            }
            
            int centerDistNum = centerDistn.size();
            logger.debug("centerDistNum: " + centerDistNum);
            for (int i = 0; i < centerDistNum; i++) {
                Vector<String> centerIn = centerDistn.get(i);
                logger.debug("" + centerIn.get(0) + ": " + centerIn.get(1));
            }
            logger.debug("Total number of pheo patients: " + totalPheoNum);

        } catch (Exception e) {
            logger.debug("Error: " + e.getMessage());
        }

        int studyNumber = 0;
        String sql = "";

        String[] tables = {"Pheo_FollowUp", "Pheo_ImagingTests", "Pheo_BiochemicalAssessment"};

        Vector<Vector> ltphpglIds = new Vector<Vector>();
        Vector<Vector> centerDistn = new Vector<Vector>();
        try {
            ConnectionAuxiliary aux = new ConnectionAuxiliary();
            Connection conn = null;
            if (context != null) {
                conn = aux.getAuxiliaryConnection(context, "main");
            }

            for (int i = 0; i < 3; i++) {

                sql = "SELECT DISTINCT Pheo_PatientHistory.center_id,Pheo_PatientHistory.ensat_id ";
                sql += "FROM Pheo_PatientHistory, Pheo_Surgery, " + tables[i] + " ";
                sql += "WHERE Pheo_PatientHistory.center_id=Pheo_Surgery.center_id ";
                sql += "AND Pheo_PatientHistory.ensat_id=Pheo_Surgery.ensat_id ";
                sql += "AND Pheo_Surgery.center_id=" + tables[i] + ".center_id AND Pheo_Surgery.ensat_id=" + tables[i] + ".ensat_id ";
                sql += ";";

                //logger.debug("sql: " + sql);
                PreparedStatement ps = conn.prepareStatement(sql);
                ResultSet rs = ps.executeQuery();

                while (rs.next()) {
                    String centerid = rs.getString(1);
                    String pid = rs.getString(2);
                    Vector<String> ltphpglId = new Vector<String>();
                    ltphpglId.add(centerid);
                    ltphpglId.add(pid);

                    boolean listContainsId = false;
                    int idCount = 0;
                    while (!listContainsId && (idCount < ltphpglIds.size())) {
                        Vector<String> idIn = ltphpglIds.get(idCount);
                        String centerIdIn = idIn.get(0);
                        String ensatIdIn = idIn.get(1);
                        if (centerIdIn.equals(centerid) && ensatIdIn.equals(pid)) {
                            listContainsId = true;
                        } else {
                            idCount++;
                        }
                    }
                    if (!listContainsId) {
                        ltphpglIds.add(ltphpglId);
                        centerDistn = this.addToCenterDistn(centerDistn, centerid);
                    }
                }
            }
        } catch (Exception e) {
            logger.debug("Error: " + e.getMessage());
        }
        
        int centerDistNum = centerDistn.size();
        logger.debug("centerDistNum: " + centerDistNum);
        for (int i = 0; i < centerDistNum; i++) {
            Vector<String> centerIn = centerDistn.get(i);
            logger.debug("" + centerIn.get(0) + ": " + centerIn.get(1));
        }

        

        //Number of pheo patients that have a surgery form and one of biochem, follow-up or imaging
        studyNumber = ltphpglIds.size();
        logger.debug("#1) Number of pheo patients with surgery and one of biochemical, follow-up or imaging: " + studyNumber);

        //From the list above, pull out the first surgery date for each ID        
        String surgeryDateSql = "SELECT center_id,ensat_id,surgery_date FROM Pheo_Surgery WHERE ";

        for (int i = 0; i < studyNumber; i++) {
            Vector<String> idIn = ltphpglIds.get(i);
            String centerid = idIn.get(0);
            String pid = idIn.get(1);
            surgeryDateSql += "(center_id='" + centerid + "' AND ensat_id=" + pid + ") OR ";
        }
        surgeryDateSql = surgeryDateSql.substring(0, surgeryDateSql.length() - 4);
        surgeryDateSql += " ORDER BY center_id,ensat_id,surgery_date;";

        //logger.debug("surgeryDateSql: " + surgeryDateSql);
        Vector<Vector> ltphpglIdsSurgery = new Vector<Vector>();
        try {
            ConnectionAuxiliary aux = new ConnectionAuxiliary();
            Connection conn = null;
            if (context != null) {
                conn = aux.getAuxiliaryConnection(context, "main");
            }

            PreparedStatement ps = conn.prepareStatement(surgeryDateSql);
            ResultSet rs = ps.executeQuery();

            int idCount = 0;
            while (rs.next()) {
                Vector<String> surgeryInfoIn = new Vector<String>();
                for (int j = 0; j < 3; j++) {
                    surgeryInfoIn.add(rs.getString(j + 1));
                }
                ltphpglIdsSurgery.add(surgeryInfoIn);
                idCount++;
            }
        } catch (Exception e) {
            logger.debug("Error: " + e.getMessage());
        }

        logger.debug("#2) Drawn from (1), the number of surgery forms with distinct dates: " + ltphpglIdsSurgery.size());

        //Now strip this surgery info list down to only the first surgery for a given patient
        //Final number should be 673 again
        Vector<Vector> ltphpglSurgeryFinal = new Vector<Vector>();
        for (int i = 0; i < ltphpglIdsSurgery.size(); i++) {

            Vector<String> surgeryInfoIn = ltphpglIdsSurgery.get(i);
            String centerid = surgeryInfoIn.get(0);
            String pid = surgeryInfoIn.get(1);
            String surgeryDate = surgeryInfoIn.get(2);

            //logger.debug("ID: " + centerid + "-" + pid);
            boolean listContainsId = false;
            int idCount = 0;
            while (!listContainsId && (idCount < ltphpglSurgeryFinal.size())) {
                Vector<String> idIn = ltphpglSurgeryFinal.get(idCount);
                String centerIdIn = idIn.get(0);
                String ensatIdIn = idIn.get(1);

                if (centerIdIn.equals(centerid) && ensatIdIn.equals(pid)) {
                    listContainsId = true;
                    //logger.debug("ID found: " + centerIdIn + "-" + ensatIdIn);
                } else {
                    idCount++;
                }
            }
            if (!listContainsId) {
                ltphpglSurgeryFinal.add(surgeryInfoIn);
            }
        }
        logger.debug("#3) Drawn from (2), with only unique IDs presented (i.e. only the first surgery): " + ltphpglSurgeryFinal.size());

        //Pull out the dates of the corresponding form (imaging, biochem or follow-up)                
        Vector<Vector> ltphpglOtherFinal = new Vector<Vector>();
        for (int j = 0; j < tables.length; j++) {
            Vector<Vector> ltphpglIdsOther = new Vector<Vector>();

            String dateStr = "";
            if (tables[j].equals("Pheo_FollowUp")) {
                dateStr = "followup_date";
            } else if (tables[j].equals("Pheo_BiochemicalAssessment")) {
                dateStr = "plasma_date,plasma_free_date,serum_chromo_a_date,urine_free_date,urine_date,plasma_dopamine_date";
            } else if (tables[j].equals("Pheo_ImagingTests")) {
                dateStr = "ct_date,nmr_date,mibg_date,octreoscan_date,fdg_pet_date,da_pet_date,synthesis_imaging_workup,other_imaging_date";
            }

            String otherDateSql = "SELECT center_id,ensat_id," + dateStr + " FROM " + tables[j] + " WHERE ";
            //String otherDateSqlHeader = otherDateSql;

            for (int i = 0; i < studyNumber; i++) {
                Vector<String> idIn = ltphpglIds.get(i);
                String centerid = idIn.get(0);
                String pid = idIn.get(1);
                otherDateSql += "(center_id='" + centerid + "' AND ensat_id=" + pid + ") OR ";
            }
            otherDateSql = otherDateSql.substring(0, otherDateSql.length() - 4);
            otherDateSql += " ORDER BY center_id,ensat_id," + dateStr + ";";

            //logger.debug("otherDateSqlHeader: " + otherDateSqlHeader);
            try {
                ConnectionAuxiliary aux = new ConnectionAuxiliary();
                Connection conn = null;
                if (context != null) {
                    conn = aux.getAuxiliaryConnection(context, "main");
                }

                PreparedStatement ps = conn.prepareStatement(otherDateSql);
                ResultSet rs = ps.executeQuery();

                ResultSetMetaData rsmd = rs.getMetaData();
                int columnNum = rsmd.getColumnCount();

                int idCount = 0;
                while (rs.next()) {
                    Vector<String> otherInfoIn = new Vector<String>();

                    //USE THIS BIT TO ANALYSE THE DATE AND ADD THE FIRST ONE THAT MEETS THE REQUIREMENTS                        
                    for (int k = 0; k < 2; k++) {
                        otherInfoIn.add(rs.getString(k + 1));
                    }
                    boolean dateFound = false;
                    int dateCount = 2;
                    while (!dateFound && (dateCount < columnNum)) {
                        String dateIn = rs.getString(dateCount + 1);
                        if (dateIn == null) {
                            dateIn = "";
                        }
                        //logger.debug("dateIn: " + dateIn);
                        if (this.checkDateFormat(dateIn)) {
                            otherInfoIn.add(dateIn);
                            dateFound = true;
                        } else {
                            dateCount++;
                        }
                    }
                    //TESTING
                    if (!dateFound) {
                        otherInfoIn.add("");
                    }

                    ltphpglIdsOther.add(otherInfoIn);
                    idCount++;
                }
            } catch (Exception e) {
                logger.debug("Error (otherDateSql): " + e.getMessage());
            }

            logger.debug("#4." + j + ") Total number of " + tables[j] + " forms belonging to the IDs listed in (1): " + ltphpglIdsOther.size());
            //Now strip this other info list down to only the first other follow-up for a given patient

            for (int i = 0; i < ltphpglIdsOther.size(); i++) {

                Vector<String> otherInfoIn = ltphpglIdsOther.get(i);
                String centerid = otherInfoIn.get(0);
                String pid = otherInfoIn.get(1);
                //String otherDate = otherInfoIn.get(2);

                //logger.debug("ID: " + centerid + "-" + pid);
                boolean listContainsId = false;
                int idCount = 0;
                while (!listContainsId && (idCount < ltphpglOtherFinal.size())) {
                    Vector<String> idIn = ltphpglOtherFinal.get(idCount);
                    String centerIdIn = idIn.get(0);
                    String ensatIdIn = idIn.get(1);

                    if (centerIdIn.equals(centerid) && ensatIdIn.equals(pid)) {
                        listContainsId = true;
                        //logger.debug("ID found: " + centerIdIn + "-" + ensatIdIn);
                    } else {
                        idCount++;
                    }
                }
                if (!listContainsId) {
                    ltphpglOtherFinal.add(otherInfoIn);
                }
            }
            logger.debug("#5) Running total of the distinct IDs belonging to those listed in (1): " + ltphpglOtherFinal.size());
        }

        //Add the other date to the list of IDs with surgery date
        for (int i = 0; i < ltphpglSurgeryFinal.size(); i++) {

            Vector<String> surgeryRecordIn = ltphpglSurgeryFinal.get(i);
            String centerid = surgeryRecordIn.get(0);
            String pid = surgeryRecordIn.get(1);

            boolean idFound = false;
            int idCount = 0;            
            while (!idFound && (idCount < ltphpglOtherFinal.size())) {

                Vector<String> otherRecordIn = ltphpglOtherFinal.get(idCount);
                String centeridIn = otherRecordIn.get(0);                
                String pidIn = otherRecordIn.get(1);

                if (centerid.equals(centeridIn) && pid.equals(pidIn)) {
                    idFound = true;
                    String otherDate = otherRecordIn.get(2);
                    surgeryRecordIn.add(otherDate);
                } else {
                    idCount++;
                }
            }
            if (idFound) {
                ltphpglSurgeryFinal.set(i, surgeryRecordIn);
            } else {
                surgeryRecordIn.add("");
                ltphpglSurgeryFinal.set(i, surgeryRecordIn);
            }            
        }
        
        for (int i = 0; i < ltphpglSurgeryFinal.size(); i++) {

            Vector<String> recordIn = ltphpglSurgeryFinal.get(i);
            String outputStr = "";
            for (int j = 0; j < 4; j++) {
                outputStr += "" + recordIn.get(j) + " ";
            }
        }

        //Compare and add to list if interval is 6 mths or less
        Vector<Vector> intervalList = new Vector<Vector>();
        int exceptionCount = 0;
        centerDistn = new Vector<Vector>();
        for (int i = 0; i < ltphpglSurgeryFinal.size(); i++) {

            Vector<String> recordIn = ltphpglSurgeryFinal.get(i);
            try {
                boolean intervalCorrect = this.compareTimes(recordIn);
                if (intervalCorrect) {
                    intervalList.add(recordIn);
                    String centeridIn = recordIn.get(0);
                    centerDistn = this.addToCenterDistn(centerDistn, centeridIn);
                }
            } catch (Exception e) {
                //logger.debug("Parsing exception: " + e.getMessage());
                exceptionCount++;
            }
        }
        
        logger.debug("Parsing exception count: " + exceptionCount);
        centerDistNum = centerDistn.size();
        logger.debug("centerDistNum: " + centerDistNum);
        for (int i = 0; i < centerDistNum; i++) {
            Vector<String> centerIn = centerDistn.get(i);
            logger.debug("" + centerIn.get(0) + ": " + centerIn.get(1));
        }
        
        logger.debug("#6) Number of (1) that have the biochemical, imaging or follow-up within 6 months of the first surgery: " + intervalList.size());

        //Of the remaining list, find those that have a second follow-up, biochem or imaging form, add to list
        //Of that remaining list, print out the time interval between the surgery and the second form
        return studyNumber;

    }

    private boolean compareTimes(Vector<String> recordIn) throws Exception {

        boolean intervalCorrect = false;
        String date1Str = recordIn.get(2);
        String date2Str = recordIn.get(3);

        //Convert to dates and compare
        SimpleDateFormat formatter = new SimpleDateFormat("yyyy-MM-dd");

        java.util.Date date1 = formatter.parse(date1Str);
        java.util.Date date2 = formatter.parse(date2Str);

        //Now calculate 6 months in milliseconds
        long intervalCheck = 6 * 28 * 24 * 60 * 60 * 1000;

        //intervalCheck = intervalCheck * 2; //1 year
        //intervalCheck = intervalCheck * 4; //2 years
        //intervalCheck = intervalCheck * 6; //3 years
        long interval = date2.getTime() - date1.getTime();
        //if(interval <= intervalCheck && !(interval < 0)){
        if (interval <= intervalCheck) {
            intervalCorrect = true;
        }

        /*if(interval < 0){
         logger.debug("" + recordIn.get(0) + "-" + recordIn.get(1) + ": " + date1Str + " | " + date2Str + " | " + intervalCorrect);
         }*/
        //If interval is 6 months or less return true
        //Else return false
        return intervalCorrect;
    }

    private boolean checkDateFormat(String dateIn) {

        if (dateIn == null) {
            dateIn = "";
        }
        boolean isDate = false;
        if (!dateIn.equals("")) {
            isDate = true;
        }
        return isDate;
    }

    public String getStudyLinks(Connection conn) {
        String studyLinksStr = "<ul>";
        String[] dbns = {"ACC", "Pheo", "NAPACA", "APA"};
        for (int i = 0; i < dbns.length; i++) {
            studyLinksStr += this.getStudyLinksByType(dbns[i], conn);
        }
        studyLinksStr += "</ul>";
        return studyLinksStr;
    }

    private String getStudyLinksByType(String dbn, Connection conn) {

        String typeStudyLinkStr = "<li>" + dbn + "<ul>";

        String studyLinkSql = "SELECT Studies.study_name,Studies.study_label FROM Studies,Study_Type WHERE Studies.study_id=Study_Type.study_id AND Study_Type.tumor_type=?;";

        try {
            PreparedStatement ps = conn.prepareStatement(studyLinkSql);
            ps.setString(1, dbn);

            ResultSet rs = ps.executeQuery();
            while (rs.next()) {
                String studyName = rs.getString(1);
                String studyLabel = rs.getString(2);
                typeStudyLinkStr += "<li><a href=\"/jsp/search/search_export.jsp?mod=6&dbn=" + dbn + "&study=" + studyName + "\">" + studyLabel + "</a></li>";
            }
        } catch (Exception e) {
            logger.debug("Error (getStudyLinks): " + e.getMessage());
        }

        typeStudyLinkStr += "</ul></li>";
        return typeStudyLinkStr;
    }
    
    
    public String checkFrenchNumbers(ServletContext context){
        
        //1) Check for forms entered after Dec 2013
        String[] tablenames1 = this.getSubTablenames("ACC");
        String[] tablenames2 = this.getSubTablenames("Pheo");
        
        int totalTableNum = tablenames1.length + tablenames2.length;
        logger.debug("totalTableNum: " + totalTableNum);
        String[] tablenames = new String[totalTableNum];
        for(int i=0; i<tablenames1.length; i++){
            tablenames[i] = tablenames1[i];
        }
        for(int i=tablenames1.length; i<(tablenames1.length + tablenames2.length); i++){
            tablenames[i] = tablenames2[i-tablenames1.length];
        }
        
        Vector<String> patientIds = new Vector<String>();
        ConnectionAuxiliary aux = new ConnectionAuxiliary();
            Connection conn = null;
            if (context != null) {
                conn = aux.getAuxiliaryConnection(context, "main");
            }
        try{
            
            for(int i=0; i<tablenames.length; i++){
        
                String tablenameDate = this.getSubTablenameDate(tablenames[i]);
                String sql = "SELECT DISTINCT center_id,ensat_id FROM " + tablenames[i] + " WHERE center_id LIKE 'FR%' AND " + tablenameDate + " > '2013-12-31' ORDER BY " + tablenameDate + ";";
                //logger.debug("sql: " + sql);
                PreparedStatement ps = conn.prepareStatement(sql);
                ResultSet rs = ps.executeQuery();
                while(rs.next()){
                    String centerIdIn = rs.getString(1);
                    String ensatIdIn = rs.getString(2);
                    if(centerIdIn == null){
                        centerIdIn = "";
                    }
                    if(ensatIdIn == null){
                        ensatIdIn = "";
                    }
                    String idIn = centerIdIn + "-" + ensatIdIn;
                    if(!patientIds.contains(idIn)){
                        patientIds.add(idIn);
                    }
                }
            }
        }catch(Exception e){
            logger.debug("Error (checkFrenchNumbers): " + e.getMessage());
        }
        
        logger.debug("Number of patients updated since Dec 2013: " + patientIds.size());
        logger.debug("====");
        /*for(int i=0; i<patientIds.size(); i++){
            logger.debug("" + patientIds.get(i));
        }
        logger.debug("====");*/
        
        
        //2) Baseline check of completeness
        int countOver50 = 0;
        try{
            
            String[] baselineTables = {"ACC","Pheo"};            
            for(int i=0; i<baselineTables.length; i++){
        
                String sql = "";
                if(i == 0){
                    sql = "SELECT * FROM ACC_DiagnosticProcedures, ACC_TumorStaging WHERE ACC_DiagnosticProcedures.center_id=ACC_TumorStaging.center_id AND ACC_DiagnosticProcedures.ensat_id=ACC_TumorStaging.ensat_id AND ACC_DiagnosticProcedures.center_id LIKE 'FR%';";
                }else{
                    sql = "SELECT * FROM Pheo_PatientHistory WHERE center_id LIKE 'FR%';";
                }
                
                PreparedStatement ps = conn.prepareStatement(sql);
                ResultSet rs = ps.executeQuery();
                
                ResultSetMetaData rsmd = rs.getMetaData();
                int colNum = rsmd.getColumnCount();
                
                while(rs.next()){
                    int colCount = 0;
                    int positiveCount = 0;
                    boolean over50 = false;
                    while(!over50 && colCount < colNum){
                        String valueIn = rs.getString(colCount+1);
                        if(valueIn == null){
                            valueIn = "";
                        }
                        if(!valueIn.equals("")){
                            positiveCount++;
                        }
                        if(positiveCount > (colCount/2)){
                            over50 = true;
                        }else{
                            colCount++;
                        }
                    }
                    
                    if(over50){
                        countOver50++;
                    }
                }
            }
        }catch(Exception e){
            logger.debug("Error (checkFrenchNumbers): " + e.getMessage());
        }
        
        logger.debug("Number of patients with baseline completeness > 50%: " + countOver50);
        logger.debug("====");
        
        
        //3) Check for dates of registration in all years from 2010 -> 2013
        Vector<Vector> dateRegLists = new Vector<Vector>();        
        String[] yearTag = {"2010","2011","2012","2013"};
        try{
            for(int i=0; i<yearTag.length; i++){
                Vector<String> dateReg = new Vector<String>();
                String sql = "";
                sql = "SELECT DISTINCT center_id,ensat_id FROM Identification WHERE (ensat_database='ACC' OR ensat_database='Pheo') AND center_id LIKE 'FR%' AND date_first_reg LIKE '" + yearTag[i] + "%';";
                /*if(i != yearTag.length-1){
                    //sql = "SELECT DISTINCT center_id,ensat_id FROM Identification WHERE (ensat_database='ACC' OR ensat_database='Pheo') AND center_id LIKE 'FR%' AND (date_first_reg > " + yearTag[i] + " AND date_first_reg < " + yearTag[i+1] + ");";                    
                }else{
                    sql = "SELECT DISTINCT center_id,ensat_id FROM Identification WHERE (ensat_database='ACC' OR ensat_database='Pheo') AND center_id LIKE 'FR%' AND (date_first_reg > " + yearTag[i] + ");";
                }*/
                PreparedStatement ps = conn.prepareStatement(sql);
                ResultSet rs = ps.executeQuery();
                while(rs.next()){
                    String centerIdIn = rs.getString(1);
                    String ensatIdIn = rs.getString(2);
                    if(centerIdIn == null){
                        centerIdIn = "";
                    }
                    if(ensatIdIn == null){
                        ensatIdIn = "";
                    }
                    String idIn = centerIdIn + "-" + ensatIdIn;
                    if(!dateReg.contains(idIn)){
                        dateReg.add(idIn);
                    }
                }
                dateRegLists.add(dateReg);
            }            
        }catch(Exception e){
            logger.debug("Error (checkFrenchNumbers): " + e.getMessage());
        }
        
        for(int i=0; i<yearTag.length; i++){
            Vector<String> yearTagsIn = dateRegLists.get(i);            
            logger.debug("Number of patients registered in " + yearTag[i] + ": " + yearTagsIn.size());
            logger.debug("=====");
            /*for(int j=0; j<yearTagsIn.size(); j++){
                logger.debug("" + yearTagsIn.get(j));
            }
            logger.debug("=====");*/
        }
        
        
        //4) Check for forms entered in all years from 2010 -> 2013
        Vector<Vector> formsEntered = new Vector<Vector>();
        try{
            
            for(int i=0; i<tablenames.length; i++){
                
                for(int j=0; j<yearTag.length; j++){
        
                    Vector<String> formsEnteredYear = new Vector<String>();
                    String tablenameDate = this.getSubTablenameDate(tablenames[i]);;
                    String sql = "";
                    if(j != yearTag.length-1){
                        sql = "SELECT DISTINCT center_id,ensat_id FROM " + tablenames[i] + " WHERE center_id LIKE 'FR%' AND " + tablenameDate + " > '" + yearTag[j] + "' AND " + tablenameDate + " < '" + yearTag[j+1] + "' ORDER BY " + tablenameDate + ";";
                    }else{
                        sql = "SELECT DISTINCT center_id,ensat_id FROM " + tablenames[i] + " WHERE center_id LIKE 'FR%' AND " + tablenameDate + " > '" + yearTag[j] + "' ORDER BY " + tablenameDate + ";";
                    }
                    PreparedStatement ps = conn.prepareStatement(sql);
                    ResultSet rs = ps.executeQuery();
                    while(rs.next()){
                        String centerIdIn = rs.getString(1);
                        String ensatIdIn = rs.getString(2);
                        if(centerIdIn == null){
                            centerIdIn = "";
                        }
                        if(ensatIdIn == null){
                            ensatIdIn = "";
                        }
                        String idIn = centerIdIn + "-" + ensatIdIn;
                        if(!formsEnteredYear.contains(idIn)){
                            formsEnteredYear.add(idIn);
                        }
                    }
                    formsEntered.add(formsEnteredYear);
                }
                
            }
        }catch(Exception e){
            logger.debug("Error (checkFrenchNumbers): " + e.getMessage());
        }
        
        for(int i=0; i<yearTag.length; i++){
            Vector<String> yearTagsIn = formsEntered.get(i);            
            logger.debug("Number of patients updated in " + yearTag[i] + ": " + yearTagsIn.size());
            logger.debug("=====");
            /*for(int j=0; j<yearTagsIn.size(); j++){
                logger.debug("" + yearTagsIn.get(j));
            }
            logger.debug("=====");*/
        }
        
        
        //5) Check ACC/Pheo bio-sample counts for French records
        Vector<String> bioSamplesCount = new Vector<String>();
        String[] bioSamples = {"tumor_tissue_frozen","serum","24h_urine","plasma"};
        String[] types = {"ACC","Pheo"};
        try{
            
            for(int i=0; i<bioSamples.length; i++){
                
                int bioCount = 0;
                for(int j=0; j<types.length; j++){
                    String sql = "SELECT DISTINCT " + types[j].toLowerCase() + "_biomaterial_id FROM " + types[j] + "_Biomaterial WHERE " + bioSamples[i] + "='Yes' AND center_id LIKE 'FR%';";
        
                    PreparedStatement ps = conn.prepareStatement(sql);
                    ResultSet rs = ps.executeQuery();
                    while(rs.next()){
                        bioCount++;
                    }
                }
                bioSamplesCount.add("" + bioCount);                
            }
        }catch(Exception e){
            logger.debug("Error (checkFrenchNumbers): " + e.getMessage());
        }
        
        logger.debug("====");
        for(int i=0; i<bioSamplesCount.size(); i++){
            logger.debug("" + bioSamples[i] + ": " + bioSamplesCount.get(i));
        }
        logger.debug("====");
        
        
        return "";
    }
    
    private String[] getSubTablenames(String dbn){
        
        String[] tablenames = null;
        if(dbn.equals("ACC")){
            tablenames = new String[9];
            tablenames[0] = "ACC_Biomaterial";
            tablenames[1] = "ACC_Chemoembolisation";
            tablenames[2] = "ACC_FollowUp";
            tablenames[3] = "ACC_Metabolomics";
            tablenames[4] = "ACC_Mitotane";
            tablenames[5] = "ACC_Pathology";
            tablenames[6] = "ACC_Radiofrequency";
            tablenames[7] = "ACC_Radiotherapy";
            tablenames[8] = "ACC_Surgery";
        }else if(dbn.equals("Pheo")){
            tablenames = new String[9];
            tablenames[0] = "Pheo_BiochemicalAssessment";
            tablenames[1] = "Pheo_Biomaterial";
            tablenames[2] = "Pheo_ClinicalAssessment";
            tablenames[3] = "Pheo_FollowUp";
            tablenames[4] = "Pheo_Genetics";
            tablenames[5] = "Pheo_ImagingTests";
            tablenames[6] = "Pheo_NonSurgicalInterventions";
            tablenames[7] = "Pheo_Surgery";
            tablenames[8] = "Pheo_TumorDetails";
        }
        
        return tablenames;
    }
    
    private String getSubTablenameDate(String tablename){
        
        String tablenameDate = "";
        if(tablename.equals("ACC_Biomaterial")){
            tablenameDate = "biomaterial_date";
        }else if(tablename.equals("ACC_Chemoembolisation")){
            tablenameDate = "chemoembolisation_date";
        }else if(tablename.equals("ACC_FollowUp")){
            tablenameDate = "followup_date";
        }else if(tablename.equals("ACC_Metabolomics")){
            tablenameDate = "metabolomics_date";
        }else if(tablename.equals("ACC_Mitotane")){
            tablenameDate = "mitotane_date";
        }else if(tablename.equals("ACC_Pathology")){
            tablenameDate = "pathology_date";
        }else if(tablename.equals("ACC_Radiofrequency")){
            tablenameDate = "radiofrequency_date";
        }else if(tablename.equals("ACC_Radiotherapy")){
            tablenameDate = "radiotherapy_date";
        }else if(tablename.equals("ACC_Surgery")){
            tablenameDate = "surgery_date";
        }else if(tablename.equals("Pheo_BiochemicalAssessment")){
            tablenameDate = "plasma_date";
        }else if(tablename.equals("Pheo_Biomaterial")){
            tablenameDate = "biomaterial_date";
        }else if(tablename.equals("Pheo_ClinicalAssessment")){
            tablenameDate = "assessment_date";
        }else if(tablename.equals("Pheo_FollowUp")){
            tablenameDate = "followup_date";
        }else if(tablename.equals("Pheo_Genetics")){
            tablenameDate = "genetics_date";
        }else if(tablename.equals("Pheo_ImagingTests")){
            tablenameDate = "ct_date";
        }else if(tablename.equals("Pheo_NonSurgicalInterventions")){
            tablenameDate = "mibg_therapy_date";
        }else if(tablename.equals("Pheo_Surgery")){
            tablenameDate = "surgery_date";
        }else if(tablename.equals("Pheo_TumorDetails")){
            tablenameDate = "tumor_date";
        }
        return tablenameDate;
    }

}
