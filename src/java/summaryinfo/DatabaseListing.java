/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package summaryinfo;

import java.util.List;
import java.util.ArrayList;
import java.util.StringTokenizer;
import java.util.Vector;
import java.sql.*;

import security.Authz;

import javax.servlet.ServletContext;

import org.apache.log4j.Level;
import org.apache.log4j.Logger;
import org.apache.log4j.PropertyConfigurator;

/**
 *
 * @author astell
 */
public class DatabaseListing {

    private static final Logger logger = Logger.getLogger(DatabaseListing.class);

    public DatabaseListing() {
    }

    public String getListingHeaderHtml(int extraColumnNum, String dbn, String dbid, String pageNum) {

        String outputStr = "";
        outputStr += "<tr>";
        outputStr += "<th>";
        outputStr += "<a href=\"./jsp/dbhome.jsp?dbn=" + dbn + "&dbid=" + dbid + "&page=" + pageNum + "&ensatidorder=1\">ENSAT ID</a>";
        outputStr += "</th>";
        outputStr += "<th>";
        outputStr += "Referral Doctor";
        outputStr += "</th>";
        outputStr += "<th>";
        outputStr += "<a href=\"./jsp/dbhome.jsp?dbn=" + dbn + "&dbid=" + dbid + "&page=" + pageNum + "&ensatidorder=2\">Record Date</a>";
        outputStr += "</th>";
        outputStr += "<th>";
        outputStr += "<div align=\"left\"><a href=\"./jsp/dbhome.jsp?dbn=" + dbn + "&dbid=" + dbid + "&page=" + pageNum + "&ensatidorder=3\">Date of First Registration</a></div>";
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

        for (int i = 0; i < extraColumnNum; i++) {
            outputStr += "<th>";
            if(dbn.equals("NAPACA") && i == 4){
                outputStr += "Transfer to";
            }
            outputStr += "</th>";
        }
        outputStr += "</tr>";
        return outputStr;
    }

    //Do a connection here for the national set - obtain username/country resultset then iterate over this throughout the loop
    public List<String>[] countryCheck(Connection secConn) {

        //Initialise the main array
        List<String>[] countryCheck = new ArrayList[2];

        String sql = "SELECT email_address, country FROM User";
        try {
            PreparedStatement ps = secConn.prepareStatement(sql);

            ResultSet rs_country_check = ps.executeQuery();
            List<String> countries = new ArrayList<String>();
            List<String> usernames = new ArrayList<String>();
            while (rs_country_check.next()) {
                String usernameIn = rs_country_check.getString(1);
                String countryIn = rs_country_check.getString(2);
                usernames.add(usernameIn);
                countries.add(countryIn);
            }

            countryCheck[0] = usernames;
            countryCheck[1] = countries;

            rs_country_check.close();
            ps.close();
        } catch (Exception e) {
            logger.debug("Error (countryCheck): " + e.getMessage());
        }

        return countryCheck;
    }

    public Vector<Vector> patientConflictCheck(Connection conn) {

        Vector<Vector> patientConflictIDs = new Vector<Vector>();

        String sql = "SELECT A.center_id,A.ensat_id,B.local_investigator,B.investigator_email "
                + "FROM Identification A, Identification B "
                + "WHERE A.sex=B.sex "
                + "AND A.year_of_birth=B.year_of_birth "
                + "AND A.date_first_reg=B.date_first_reg "
                + "AND A.ensat_id!=B.ensat_id "
                + "AND A.center_id!=B.center_id;";

        try {
            PreparedStatement ps = conn.prepareStatement(sql);
            ResultSet pccRs = ps.executeQuery();
            while (pccRs.next()) {
                Vector<String> conflictIDin = new Vector<String>();
                for (int i = 0; i < 4; i++) {
                    conflictIDin.add(pccRs.getString(i + 1));
                }
                patientConflictIDs.add(conflictIDin);
            }
            pccRs.close();
            ps.close();

        } catch (Exception e) {
            logger.debug("Error (patientConflictCheck): " + e.getMessage());
        }

        return patientConflictIDs;
    }

    private List<String> listFollowUpRequired(Connection conn, String dbn) {

        List<String> followup_str = new ArrayList<String>();
        List<String> older6mths_str = new ArrayList<String>();
        List<String> finalList_str = new ArrayList<String>();

        try {
            //Get date six months previously from SQL ResultSet
            String timeSql = "SELECT DATE_SUB(NOW(), INTERVAL 12 MONTH);";
            PreparedStatement ps = conn.prepareStatement(timeSql);
            
            ResultSet rs_12mth = ps.executeQuery();
            rs_12mth.next();
            String date12mth = rs_12mth.getString(1);
            
            ps.close();
            
            //Compile list of patient records that are older than 6 months
            String recordDateSql = "SELECT ensat_id,center_id FROM Identification WHERE record_date < ?;";
            PreparedStatement ps2 = conn.prepareStatement(recordDateSql);
            ps2.setString(1,date12mth);
            
            ResultSet rs_recordDate = ps2.executeQuery();
            while(rs_recordDate.next()){
                String pidIn = rs_recordDate.getString(1);
                pidIn = this.formatID(pidIn);
                String ensatIdIn = rs_recordDate.getString(2) + "-" + pidIn;
                if (!older6mths_str.contains(ensatIdIn)) {
                    older6mths_str.add(ensatIdIn);
                }
            }
            
            ps2.close();

            //Compile sub-table list for each tumor type
            String[] subTables = new String[0];
            //Manually compile sub-table names and date-names
            subTables = this.getSubTables(dbn);
            int subTableNum = subTables.length;            
            String[] subTableDateNames = new String[subTableNum];
            for(int i=0; i < subTableNum; i++){
                subTableDateNames[i] = this.getSubTableDateName(subTables[i]);
            }
            
            //Run the query check for each sub-table
            for (int i = 0; i < subTableNum; i++) {

                //Get all followup results that are younger than date retrieved above
                String infoCheckSql = "SELECT DISTINCT ensat_id,center_id FROM " + subTables[i] + " WHERE " + subTableDateNames[i] + " > ?;";
                PreparedStatement ps3 = conn.prepareStatement(infoCheckSql);
                ps3.setString(1,date12mth);
                
                ResultSet rs_info = ps3.executeQuery();
                while (rs_info.next()) {
                    String pidIn = rs_info.getString(1);
                    pidIn = this.formatID(pidIn);
                    String ensatIdIn = rs_info.getString(2) + "-" + pidIn;
                    if (!followup_str.contains(ensatIdIn)) {
                        followup_str.add(ensatIdIn);
                    }
                }
                ps3.close();
            }
            
        } catch (Exception e) {
            logger.debug("Error (listFollowUpRequired): " + e.getMessage());
        }
        
        //Now go through both lists and compile a third which is a disjoint of the two:
        //i.e. A list of IDs that are older than 6 months, but with no sub-forms within 6 months (in list 1 but not in list 2)
        int older6mthNum = older6mths_str.size();
        for(int i=0; i< older6mthNum; i++){
            String ensatIdIn = older6mths_str.get(i);
            if(!followup_str.contains(ensatIdIn)){
                finalList_str.add(ensatIdIn);
            }
        }
        
        /*for(int i=0; i < finalList_str.size(); i++){
            logger.debug("finalList(" + i + "): " + finalList_str.get(i));
        }*/
        
        return finalList_str;
    }
    
    private List<String> listDeceasedPatients(Connection conn, String dbn) {

        List<String> finalList_str = new ArrayList<String>();

        try {
            
            String deceasedCheckSql = "";
            if(dbn.equals("ACC")){
                deceasedCheckSql = "SELECT DISTINCT ensat_id,center_id FROM ACC_FollowUp WHERE patient_status LIKE '%eath%';";
            }else if(dbn.equals("Pheo")){
                deceasedCheckSql = "SELECT DISTINCT ensat_id,center_id FROM Pheo_FollowUp WHERE alive = 'No';";
            }else if(dbn.equals("NAPACA")){
                deceasedCheckSql = "SELECT DISTINCT ensat_id,center_id FROM NAPACA_FollowUp WHERE followup_alive = 'No';";
            }else if(dbn.equals("APA")){
                deceasedCheckSql = "SELECT DISTINCT ensat_id,center_id FROM APA_FollowUp WHERE followup_alive = 'No';";
            }
            
            //Compile list of patient records that are marked as deceased            
            PreparedStatement ps = conn.prepareStatement(deceasedCheckSql);                        
            ResultSet rs_deceased = ps.executeQuery();
            while(rs_deceased.next()){
                String pidIn = rs_deceased.getString(1);
                pidIn = this.formatID(pidIn);
                String ensatIdIn = rs_deceased.getString(2) + "-" + pidIn;
                if (!finalList_str.contains(ensatIdIn)) {
                    finalList_str.add(ensatIdIn);
                }
            }            
            ps.close();
            
        } catch (Exception e) {
            logger.debug("Error (listDeceasedPatients): " + e.getMessage());
        }
        
        /*for(int i=0; i < finalList_str.size(); i++){
            logger.debug("finalList(" + i + "): " + finalList_str.get(i));
        }*/
        
        return finalList_str;
    }

    public int getRowCount(String _searchFilter, String _ensatIdOrdering, String _ensatDatabase, String _userCenter, ResultSet rs) {

        int rowCount = 0;
        try {
            while (rs.next()) {
                rowCount++;
            }
        } catch (Exception e) {
            logger.debug("Error (getRowCount): " + e.getMessage());
        }
        return rowCount;
    }

    public ResultSet compilePatientList(Connection conn, String _searchFilter, String _ensatIdOrdering, String _ensatDatabase, String _userCenter) {

        String searchFilter = _searchFilter;
        String ensatIdOrdering = _ensatIdOrdering;
        String userCenter = _userCenter;
        String dbn = _ensatDatabase;

        String selectionQuery = "SELECT ensat_id, center_id, local_investigator, investigator_email, record_date, date_first_reg, sex, year_of_birth, consent_obtained, uploader, ensat_database FROM Identification";
        if (!dbn.equals("")) {
            selectionQuery += " WHERE ensat_database=?";
        }
        
        int extraCondCount = 0;
        Vector<String> extraConds = new Vector<String>();
        
        if (!searchFilter.equals("all")) {
            if (userCenter == null) {
                userCenter = "";
            }
            if (userCenter.length() != 0) {

                int centerNum = 1;
                String[] centerCodes = null;
                if (userCenter.indexOf("|") != -1) {
                    StringTokenizer st = new StringTokenizer(userCenter, "|");
                    centerNum = st.countTokens();
                    centerCodes = new String[centerNum];
                    int codeCount = 0;
                    while (st.hasMoreTokens()) {
                        centerCodes[codeCount] = st.nextToken();
                        codeCount++;
                    }
                } else {
                    centerCodes = new String[centerNum];
                    centerCodes[0] = userCenter;
                }

                if (searchFilter.equals("local")) {
                    extraCondCount++;
                    extraConds.add(centerCodes[0]);
                    if (!dbn.equals("")) {
                        selectionQuery += " AND center_id=? ";
                    } else {
                        selectionQuery += " WHERE center_id=? ";
                    }
                    for (int j = 1; j < centerNum; j++) {
                        extraCondCount++;
                        extraConds.add(centerCodes[j]);
                        selectionQuery += "OR center_id=? ";
                    }

                } else {
                    String[] userCenterCountryLetters = new String[centerNum];
                    for (int j = 0; j < centerNum; j++) {
                        userCenterCountryLetters[j] = centerCodes[j].substring(0, 2);
                    }
                    extraCondCount++;
                    extraConds.add(userCenterCountryLetters[0] + "%");
                    if (!dbn.equals("")) {
                        selectionQuery += " AND center_id LIKE ? ";
                    } else {
                        selectionQuery += " WHERE center_id LIKE ? ";
                    }
                    for (int j = 1; j < centerNum; j++) {
                        extraCondCount++;
                        extraConds.add(userCenterCountryLetters[j]);
                        selectionQuery += "OR center_id LIKE ? ";
                    }
                }
            }
        }
        
        

        if (ensatIdOrdering.equals("1")) {
            selectionQuery += " ORDER BY center_id, ensat_id ";
        } else if (ensatIdOrdering.equals("2")) {
            selectionQuery += " ORDER BY record_date ";
        } else if (ensatIdOrdering.equals("3")) {
            selectionQuery += " ORDER BY date_first_reg ";
        } else {
            selectionQuery += " ORDER BY record_date, center_id, ensat_id ";
        }
        selectionQuery += ";";
        
        ResultSet rs = null;
        try {
            PreparedStatement ps = conn.prepareStatement(selectionQuery);            
            if (!dbn.equals("")) {
                ps.setString(1,dbn);                
            }
            for(int i=0; i<extraCondCount; i++){
                ps.setString(i+2,extraConds.get(i));
            }            
            rs = ps.executeQuery();
        } catch (Exception e) {
            logger.debug("Error (compilePatientList): " + e.getMessage());
        }

        return rs;
    }

    /*
     * public ResultSet getPatientList() { return patientList; }
     *
     * public void setPatientList(ResultSet _patientList) { patientList =
     * _patientList; }
     */
    private int getUpperBound(int pageNumInt) {
        return (pageNumInt * 100);
    }

    private int getLowerBound(int upperBound) {
        if (upperBound > 100) {
            return (upperBound - 99);
        } else {
            return 0;
        }
    }

    public String getPagingHtml(int rowCount, String pageNum, String searchFilter, String dbn, String dbid, String ensatIdOrdering) {

        //Method calculates the upper and lower bounds
        int pageNumInt = 0;
        try {
            pageNumInt = Integer.parseInt(pageNum);
        } catch (NumberFormatException nfe) {
            logger.debug("NumberFormatException (getPagingHtml): " + nfe.getMessage());
        }

        //Calculate number of pages required in total
        int pageNumTotal = rowCount / 100;
        int pageNumDivider = rowCount % 100;
        if (pageNumDivider != 0) {
            pageNumTotal++;
        }

        int upperBound = this.getUpperBound(pageNumInt);
        int lowerBound = this.getLowerBound(upperBound);

        //Have a clause here to avoid inputting page numbers higher than records returned
        if (pageNumInt > pageNumTotal) {
            pageNumInt = 1;
            upperBound = (pageNumInt * 100);
            lowerBound = upperBound - 99;
        }

        int upperBoundDisp = 0;
        if (rowCount < upperBound) {
            upperBoundDisp = rowCount;
        } else {
            upperBoundDisp = upperBound;
        }

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
        outputStr += " in this database (showing records <strong>" + lowerBound + " - " + upperBoundDisp + "</strong>). Filter: <strong>" + searchFilter + "</strong></p>";

        outputStr += "<p>";

        if (pageNumInt > 1) {
            outputStr += "<a href=\"./jsp/dbhome.jsp?&dbn=" + dbn + "&dbid=" + dbid + "";
            if (!ensatIdOrdering.equals("0")) {
                outputStr += "&ensatidorder=" + ensatIdOrdering + "";
            }
            outputStr += "&page=" + (pageNumInt - 1) + "\">Previous Page</a>";
            outputStr += "&nbsp;";
        }

        for (int i = 0; i < pageNumTotal; i++) {
            if ((i + 1) == pageNumInt) {
                outputStr += "" + (i + 1) + "";
                outputStr += "&nbsp;";
            } else {
                outputStr += "<a href=\"./jsp/dbhome.jsp?&dbn=" + dbn + "&dbid=" + dbid + "";
                if (!ensatIdOrdering.equals("0")) {
                    outputStr += "&ensatidorder=" + ensatIdOrdering + "";
                }
                outputStr += "&page=" + (i + 1) + "\">" + (i + 1) + "</a>";
                outputStr += "&nbsp;";
            }
        }

        if (pageNumInt < pageNumTotal) {
            outputStr += "<a href=\"./jsp/dbhome.jsp?&dbn=" + dbn + "&dbid=" + dbid + "";
            if (!ensatIdOrdering.equals("0")) {
                outputStr += "&ensatidorder=" + ensatIdOrdering + "";
            }
            outputStr += "&page=" + (pageNumInt + 1) + "\">Next Page</a>";
            outputStr += "&nbsp;";
        }
        outputStr += "</p>";

        return outputStr;
    }

    public String getTableHtml(ServletContext context, Connection secConn, Connection conn, String pageNum, String dbn, String dbid, String mainDb, String username, String country, ResultSet rs) throws Exception {

        String dbUsername = context.getInitParameter("username");
        String password = context.getInitParameter("password");
        String host = context.getInitParameter("server_name");
        
        String outputStr = "";

        //Move the cursor to the beginning of the resultset here (preparing for re-use)
        rs.beforeFirst();

        //=======
        //Work out the line colour for the table (assigned once here rather than for each line)
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
        //=======

        //=======
        //This is a 2-D string array holding the IDs and investigator information (name/email) on IDs that are matched on sex, yob and date_first_reg
        //If these parameters match ==> likely to be conflicts.
        //This is worked out here so that the database connection isn't run for each individual record
        Vector<Vector> patientConflictIDs = this.patientConflictCheck(conn);

        //Find the conflict number
        int patientConflictCheckNum = patientConflictIDs.size();
        //=======

        //=======
        //Retrieve a list of countries and usernames from the security database to set the appropriate security view on each record
        List<String>[] countryCheck = this.countryCheck(secConn);
        //=======

        //=======
        //Run a check of the follow-up required elements
        //List<String> followupStr = this.listFollowUpRequired(stmt);
        List<String> followupStr = this.listFollowUpRequired(conn, dbn);
        //=======        
        
        //=======
        //Check list for deceased patients
        List<String> deceasedStr = this.listDeceasedPatients(conn, dbn);
        //=======

        //=======
        //Run a check of any incomplete data records
        List<String> incompleteDataStr = this.listIncompleteData(conn, mainDb, dbn);
        //=======        

        //=======        
        //For NAPACA: run a check to suggest any potential section transfers        
        Vector<Vector> potentialTransferStr = new Vector<Vector>();
        int potentialTransferCount = 0;
        if (dbn.equals("NAPACA")) {
            potentialTransferStr = this.listPotentialTransfers(mainDb, conn);

            //Find the potential transfer number            
            potentialTransferCount = potentialTransferStr.size();
        }
        //=======        

        //=======
        //Now get the current page number (to evaluate lower and upper bounds)
        int pageNumInt = 0;
        try {
            pageNumInt = Integer.parseInt(pageNum);
        } catch (NumberFormatException nfe) {
            logger.debug("NumberFormatException (getTableHtml): " + nfe.getMessage());
        }
        int upperBound = this.getUpperBound(pageNumInt);
        int lowerBound = this.getLowerBound(upperBound);
        //=======

        //=======
        //Move the resultset cursor up to the lower bound
        int rsCount = 0;
        if (rs != null) {
            while (rsCount < lowerBound) {
                rs.next();
                rsCount++;
            }
        }

        //Now iterate through each result and construct the records
        int recordCount = lowerBound;
        List<PatientRecord> records = new ArrayList<PatientRecord>();
        boolean rsReady = true;
        if (rsCount == 0) {
            if (rs != null && !rs.isClosed()) {
                rsReady = rs.next();
            }
        } else {
            rsReady = true;
        }

        try {
            while ((recordCount <= upperBound) && rsReady) {

                recordCount++;

                //Now create a PatientRecord object for each line 
                PatientRecord pr = new PatientRecord();

                //Set the ENSAT ID                           
                String pid = rs.getString(1);
                String pidDisp = this.formatID(pid);
                pr.setEnsatID(pidDisp);

                //Set the center ID 
                String centerid = rs.getString(2);
                pr.setCenterID(centerid);

                //Set the investigator name 
                String investigatorName = rs.getString(3);
                pr.setInvestigatorName(investigatorName);

                //Set the investigator email
                String investigatorEmail = rs.getString(4);
                pr.setInvestigatorEmail(investigatorEmail);

                //Set the record date and date of first registration 
                for (int j = 0; j < 2; j++) {
                    String recordDate = rs.getString(j + 5);
                    if (recordDate
                            == null) {
                        recordDate = "";
                    }
                    StringTokenizer st = new StringTokenizer(recordDate, "-");
                    String recordDateYear = "";
                    String recordDateMonth = "";
                    String recordDateDay = "";
                    if (st.hasMoreTokens()) {
                        recordDateYear = st.nextToken();
                        recordDateMonth = st.nextToken();
                        recordDateDay = st.nextToken();
                    }
                    recordDate = recordDateDay + " " + this.formatMonth(recordDateMonth)
                            + " " + recordDateYear;

                    if (j == 0) {
                        pr.setRecordDate(recordDate);
                    } else {
                        pr.setDateFirstReg(recordDate);
                    }
                }

                //Set the sex 
                String sex = rs.getString(7);
                pr.setSex(sex);

                //Set the year of birth 
                String yearOfBirth = rs.getString(8);
                pr.setYearOfBirth(yearOfBirth);

                //Set the consent level 
                String consentLevel = rs.getString(9);
                pr.setConsentLevel(consentLevel);

                //Set the uploader 
                String uploader = rs.getString(10);
                pr.setUploader(uploader);

                //Now add the PatientRecord object to the list 
                records.add(pr);

                //Get the next record in the resultset                 
                rsReady = rs.next();
            }
        } catch (Exception e) {
            logger.debug("Error (getTableHtml): " + e.getMessage());
        }
        //=======

        outputStr = this.getIndividualHtml(records, lineColour,
                patientConflictIDs, patientConflictCheckNum, countryCheck,
                followupStr, deceasedStr, incompleteDataStr, potentialTransferStr,
                potentialTransferCount, dbn, dbid, username, country,
                dbUsername, password, host, conn, secConn);

        return outputStr;
    }

    public String getIndividualHtml(List<PatientRecord> records,
            String lineColour,
            Vector<Vector> patientConflictIDs,
            int patientConflictCheckNum,
            List<String>[] countryCheck,
            List<String> followupStr,
            List<String> deceasedStr,
            List<String> incompleteDataStr,
            Vector<Vector> potentialTransferStr,
            int potentialTransferCount,
            String dbn,
            String dbid,
            String username,
            String country,
            
            String dbUsername,
            String password,
            String host,
            Connection conn,
            Connection secConn
    ) {

        String outputStr = "";
        int recordNum = 0;
        if (records != null) {
            recordNum = records.size();
        }

        for (int i = 0; i < recordNum; i++) {

            PatientRecord pr = records.get(i);

            String pid = pr.getEnsatID();
            String centerid = pr.getCenterID();
            String investigatorName = pr.getInvestigatorName();
            String investigatorEmail = pr.getInvestigatorEmail();
            String recordDate = pr.getRecordDate();
            String dateFirstReg = pr.getDateFirstReg();
            String sex = pr.getSex();
            String yearOfBirth = pr.getYearOfBirth();
            String consentLevel = pr.getConsentLevel();
            String uploader = pr.getUploader();

            //Now construct the HTML string for this record
            outputStr += "<tr";
            if (i % 2 == 0) {
                outputStr += " " + lineColour + " ";
            }
            outputStr += ">";
            outputStr += "<td><strong>" + centerid + "-" + pid + "</strong></td>";
            outputStr += "<td><a href='mailto:"  + investigatorEmail + "'>"+ investigatorName + "</a></td>";
            outputStr += "<td><div align='left'>" + recordDate + "</div></td>";
            outputStr += "<td><div align='left'>" + dateFirstReg + "</div></td>";
            outputStr += "<td>" + sex + "</div></td>";
            outputStr += "<td>" + yearOfBirth + "</div></td>";

            //===========
            //Split the countryCheck elements into one list of usernames and another corresponding list of countries (indexes should match)            
            List<String> usernames = new ArrayList();
            List<String> countries = new ArrayList();

            if (countryCheck.length != 0) {
                usernames = countryCheck[0];
                countries = countryCheck[1];
            }

            outputStr += "<td><div align='left'>";
            String uploaderCountry = "";
            if (consentLevel.equals("National")) {
                //Check the country of the uploader
                int uploaderIndex = -1;
                for (int j = 0; j < usernames.size(); j++) {
                    if (usernames.get(j).equals(uploader)) {
                        uploaderIndex = j;
                    }
                }
                if(uploaderIndex != -1){
                    uploaderCountry = countries.get(uploaderIndex);
                }else{
                    logger.debug("blank uploader is: " + uploader);
                    uploaderCountry = "";
                }
                
                outputStr += consentLevel + " (" + uploaderCountry + ")</div></td>";
            } else {
                outputStr += consentLevel + "</td>";
            }
            //===========

            /**
             * ESTABLISH AT THIS POINT WHETHER THE RECORD IS EDITABLE OR NOT AND
             * MODIFY FINAL COLUMNS AS APPROPRIATE
             */
            Authz security = new Authz();
            //security.modifyRecordEditable(username, uploader, pid, centerid, dbUsername, password, host);
            security.modifyRecordEditable(username, uploader, pid, centerid, conn, secConn);
            boolean recordEditable = security.getRecordEditable();
            /*if(centerid.equals("GBBI") && pid.equals("0136")){
                logger.debug("recordEditable: " + recordEditable);
            }*/

            outputStr += this.secureOutput(dbn, dbid, pid, centerid, consentLevel, uploaderCountry, country, recordEditable);

            outputStr += this.actionRequired(dbn, followupStr, deceasedStr,
                    patientConflictIDs, patientConflictCheckNum,
                    incompleteDataStr,
                    potentialTransferStr, potentialTransferCount,
                    centerid, pid);

            //Now add the section to transfer the patient into another registry
            if (dbn.equals("NAPACA")) {
                outputStr += "<td>";
                outputStr += "<div align='center'>";
                outputStr += "<a href='./jsp/read/transfer_registry.jsp?pid=" + pid + "&centerid=" + centerid + "&study=ACC'>ACC</a>";
                outputStr += "<br/>";
                outputStr += "<a href='./jsp/read/transfer_registry.jsp?pid=" + pid + "&centerid=" + centerid + "&study=Pheo'>Pheo</a>";
                outputStr += "<br/>";
                outputStr += "<a href='./jsp/read/transfer_registry.jsp?pid=" + pid + "&centerid=" + centerid + "&study=APA'>APA</a>";
                outputStr += "</div>";
                outputStr += "</td>";
            }
            outputStr += "</tr>";
        }

        return outputStr;
    }

    private String secureOutput(String dbn, String dbid, String pid, String centerid, String consentLevel, String uploaderCountry, String country, boolean recordEditable) {

        String outputStr = "";
        if (recordEditable) {

            //Extra column 1
            outputStr += "<td><a href='./jsp/read/detail.jsp?dbid=" + dbid + "&dbn=" + dbn + "&pid=" + pid + "&centerid=" + centerid + "'>Detail</a></td>";

            //Extra column 2
            outputStr += "<td><a href='./jsp/delete/delete_view.jsp?dbid=" + dbid + "&dbn=" + dbn + "&pid=" + pid + "&centerid=" + centerid + "'>Delete</a></td>";

            //Extra column 3
            outputStr += "<td><a href='./jsp/read/timeline.jsp?dbid=" + dbid + "&dbn=" + dbn + "&pid=" + pid + "&centerid=" + centerid + "'>Timeline</a></td>";
            
            //Extra column 4
            if (dbn.equals("ACC")) {
                outputStr += "<td><a href='./jsp/read/status_report.jsp?dbid=" + dbid + "&dbn=" + dbn + "&pid=" + pid + "&centerid=" + centerid + "'>Status Report</a></td>";
            } else if (dbn.equals("Pheo") || dbn.equals("NAPACA")) {
                outputStr += "<td><div align='left'><a href='./jsp/read/transfer_study.jsp?dbid=" + dbid + "&dbn=" + dbn + "&pid=" + pid + "&centerid=" + centerid + "&study=pmt'>Transfer to PMT</a></div></td>";
            } else {
                outputStr += "<td></td>";
            }
        } else if (consentLevel.equals("Local")) {

            //Extra column 1
            outputStr += "<td></td>";

            //Extra column 2
            outputStr += "<td></td>";

            //Extra column 3                
            outputStr += "<td></td>";
            
            //Extra column 4
            outputStr += "<td></td>";
            
            /*if (dbn.equals("ACC") || dbn.equals("Pheo") || dbn.equals("NAPACA")) {
             outputStr += "<td></td>";
             } else {
             outputStr += "<td></td>";
             }*/
        } else if (consentLevel.equals("National")) {
            if (uploaderCountry.equals(country)) {

                //Extra column 1
                outputStr += "<td><a href='./jsp/read/readonly.jsp?dbid=" + dbid + "&dbn=" + dbn + "&pid=" + pid + "&centerid=" + centerid + "'>Detail</a></td>";

                //Extra column 2
                outputStr += "<td></td>";

                //Extra column 3
                outputStr += "<td></td>";
                
                //Extra column 4
                outputStr += "<td></td>";
                
                
                /*if (dbn.equals("ACC") || dbn.equals("Pheo") || dbn.equals("NAPACA")) {
                 outputStr += "<td></td>";
                 } else {
                 outputStr += "<td></td>";
                 }*/
            } else {
                //Extra column 1                    
                outputStr += "<td></td>";

                //Extra column 2                    
                outputStr += "<td></td>";

                //Extra column 3                    
                outputStr += "<td></td>";
                
                //Extra column 4
                outputStr += "<td></td>";
                
                
                /*if (dbn.equals("ACC") || dbn.equals("Pheo") || dbn.equals("NAPACA")) {
                 outputStr += "<td></td>";
                 } else {
                 outputStr += "<td></td>";
                 }*/
            }
        } else {
            
            //Extra column 1
            if(!country.equals("US") && !country.equals("Brazil")){
                outputStr += "<td><a href='./jsp/read/readonly.jsp?dbid=" + dbid + "&dbn=" + dbn + "&pid=" + pid + "&centerid=" + centerid + "'>Detail</a></td>";
            }else{
                outputStr += "<td></td>";
            }

            //Extra column 2
            outputStr += "<td></td>";

            //Extra column 3
            outputStr += "<td></td>";
            
            //Extra column 4
            outputStr += "<td></td>";
            
            /*if (dbn.equals("ACC") || dbn.equals("Pheo") || dbn.equals("NAPACA")) {
             outputStr += "<td></td>";
             } else {
             outputStr += "";
             }*/
        }
        return outputStr;
    }

    public String actionRequired(String dbn,
            List<String> followupStr,
            List<String> deceasedStr,
            Vector<Vector> patientConflictIDs,
            int patientConflictCheckNum,
            List<String> incompleteDataStr,
            Vector<Vector> potentialTransferStr,
            int potentialTransferCount,
            String centerid,
            String pid) {

        String outputStr = "";

        //ALL FOUR REQUIRE: INCOMPLETE CHECK, FOLLOW-UP CHECK, PATIENT CONFLICT CHECK        
        //NAPACA ADDITIONALLY REQUIRES: SPECIFIC CHECK TO RECOMMEND WHETHER PATIENT GOES INTO ONE OF THE OTHER THREE
        outputStr += "<td><div id='action_required'>";

        String actionStr = "";

        //=================
        //INCOMPLETE DATA SET
        //Check rest of result set for incomplete data sets
        int incompleteDataSize = 0;
        boolean incompleteFound = false;
        if (incompleteDataStr != null) {
            incompleteDataSize = incompleteDataStr.size();
        }
        //actionStr += "incompleteDataSize: " + incompleteDataSize +"<br/>";                
        for (int i = 0; i < incompleteDataSize; i++) {
            int pidInt = Integer.parseInt(pid);
            String pidStr = "" + pidInt;
            pidStr = this.formatID(pidStr);
            if (incompleteDataStr.get(i).equals(centerid + "-" + pidStr)) {
                incompleteFound = true;
                break;
            }
        }
        if (incompleteFound) {
            //actionStr += "Record incomplete<br/>";
            //AJS (06/02/14): I'm going to stub this out until there's agreement on what constitutes "incomplete"
            actionStr += "";
        }

        //=================
        //=================
        //DECEASED PATIENT FLAG
        //Check follow-up flags for deceased flag
        int deceasedStrSize = 0;
        if (deceasedStr != null) {
            deceasedStrSize = deceasedStr.size();
        }
        int deceasedCount = 0;
        boolean deceasedRecordFound = false;
        while(deceasedCount < deceasedStrSize && !deceasedRecordFound){        
            if (deceasedStr.get(deceasedCount).equals(centerid + "-" + pid)) {                
                deceasedRecordFound = true;
            }else{
                deceasedCount++;
            }
        }
        if (deceasedRecordFound) {            
            actionStr += "Patient marked as deceased<br/>";            
        }
        
        
        
        if(!deceasedRecordFound){
        //=================
            //FOLLOW-UP REQUIRED
            //Check follow-up flags for action required (if patient not flagged as deceased)
            int followupStrSize = 0;
            if (followupStr != null) {
                followupStrSize = followupStr.size();
            }
            int followupCount = 0;
            boolean flaggedRecordFound = false;
            while(followupCount < followupStrSize && !flaggedRecordFound){        
                if (followupStr.get(followupCount).equals(centerid + "-" + pid)) {                
                    flaggedRecordFound = true;
                }else{
                    followupCount++;
                }
            }
            if (flaggedRecordFound) {            
                actionStr += "No follow-up for 12 months<br/>";            
            }
        }

        //================
        //Patient conflict mark check
        boolean patientConflict = false;
        String patientConflictContact = "";
        int k = 0;
        while (k < patientConflictCheckNum && !patientConflict) {
            Vector<String> conflictIn = patientConflictIDs.get(k);
            int pidInt = Integer.parseInt(pid);
            int conflictPidInt = Integer.parseInt(conflictIn.get(1));

            patientConflict = centerid.equals(conflictIn.get(0)) && (pidInt == conflictPidInt);
            if (patientConflict) {
                patientConflictContact = conflictIn.get(2) + " (" + conflictIn.get(3) + ")";
            }
            k++;
        }
        //================
        if (patientConflict) {
            int conflictNum = k;
            actionStr += "Patient is a potential double entry<br/> - to verify please contact " + patientConflictContact + " ";
            //actionStr += "<a href=\"javascript:clarification('double_entry');\">Show criteria</a><br/>";
            actionStr += "<button id=\"opener" + conflictNum + "\" class=\"opener1\" onclick=\"return false;\">Criteria</button><br/>";
        }

        outputStr += actionStr;
        outputStr += "</div></td>";
        if (dbn.equals("NAPACA")) {
            outputStr += "<td><div id='action_required'>";

            String actionStrNapaca = "";
            boolean apaFlag = false;
            boolean pheoFlag = false;

            for (int i = 0; i < potentialTransferCount; i++) {
                Vector<String> potentialTransferIn = potentialTransferStr.get(i);
                String ensatId = centerid + "-" + pid;
                if (potentialTransferIn.get(0).equals(ensatId)) {
                    apaFlag = potentialTransferIn.get(1).equals("APA");
                    pheoFlag = potentialTransferIn.get(1).equals("Pheo");
                    break;
                }
            }
            if (apaFlag) {
                actionStrNapaca = "Patient should be considered for APA<br/><hr/>";
            }
            if (pheoFlag) {
                actionStrNapaca += "Patient should be considered for Pheo<br/><hr/>";
            }

            outputStr += actionStrNapaca;
            outputStr += "</div></td>";

        }
        return outputStr;
    }

    public String formatMonth(String recordDateMonth) {
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
        return recordDateMonthDisp;
    }

    public String formatID(String pid) {
        //Add leading zero's
        String pidDisp = "";
        if (pid.length() == 1) {
            pidDisp = "000" + pid;
        } else if (pid.length() == 2) {
            pidDisp = "00" + pid;
        } else if (pid.length() == 3) {
            pidDisp = "0" + pid;
        } else {
            pidDisp = pid;
        }
        return pidDisp;
    }

    public List<String> listIncompleteData(Connection conn, String mainDb, String dbn) {

        List<String> incompleteData = new ArrayList<String>();

        try { //Compile query and columnCount based on which database is being queried
            String sql = "";
            int columnCount = 0;
            if (dbn.equals("ACC")) {
                sql = " SELECT DISTINCT * FROM Identification, ACC_DiagnosticProcedures, ACC_TumorStaging WHERE Identification.ensat_id=ACC_DiagnosticProcedures.ensat_id AND Identification.center_id=ACC_DiagnosticProcedures.center_id AND Identification.ensat_id=ACC_TumorStaging.ensat_id AND Identification.center_id=ACC_TumorStaging.center_id;";
            } else if (dbn.equals("Pheo")) {
                sql = "SELECT DISTINCT * FROM Identification, Pheo_PatientHistory WHERE Identification.ensat_id=Pheo_PatientHistory.ensat_id AND Identification.center_id=Pheo_PatientHistory.center_id;";
            } else if (dbn.equals("NAPACA")) {
                sql = "SELECT DISTINCT * FROM Identification, NAPACA_DiagnosticProcedures WHERE Identification.ensat_id=NAPACA_DiagnosticProcedures.ensat_id AND Identification.center_id=NAPACA_DiagnosticProcedures.center_id;";
            } else if (dbn.equals("APA")) {
                sql = "SELECT DISTINCT * FROM Identification, APA_PatientHistory WHERE Identification.ensat_id=APA_PatientHistory.ensat_id AND Identification.center_id=APA_PatientHistory.center_id;";
            }

            PreparedStatement ps = conn.prepareStatement(sql);
            ResultSet rsDataCheck = ps.executeQuery();

            ResultSetMetaData rsmd = rsDataCheck.getMetaData();
            columnCount = rsmd.getColumnCount();

            while (rsDataCheck.next()) {
                boolean incompletePointFound = false;

                //Set the ENSAT ID for this record
                String ensatIdIn = rsDataCheck.getString(2) + "-" + rsDataCheck.getString(1);

                int pointCount = 0;
                //for (int i = 0; i < columnCount; i++) {
                while (pointCount < columnCount && !incompletePointFound) {
                    //Retrieve the individual value and check it for nulls or blanks
                    String valueIn = rsDataCheck.getString(pointCount + 1);
                    if (valueIn != null) {
                        valueIn = valueIn.trim();
                    }
                    if (valueIn == null || valueIn.equals("null") || valueIn.equals("")) {                        
                        incompletePointFound = true;
                    } else {
                        pointCount++;
                    }
                }
                //If the overall number of points that are not null or blank are less than the column count, add the ENSAT ID
                if (incompletePointFound) {
                    incompleteData.add(ensatIdIn);
                }
            }
        } catch (Exception e) {
            logger.debug("Error (listIncompleteData): " + e.getMessage());
        }

        return incompleteData;
    }

    public Vector<Vector> listPotentialTransfers(String mainDb, Connection conn) {

        Vector<Vector> potentialTransfers = new Vector<Vector>();

        String sql = "SELECT ensat_id, center_id, ";
        sql += "mineralo_plasma_renin_conc, mineralo_serum_aldosterone, "; //APA test parameters
        sql += "catechol_urinary_free_excretion, catechol_urinary_metanephrine_excretion, catechol_plasma_metanephrines "; //Pheo test parameters
        sql += " FROM NAPACA_DiagnosticProcedures;";

        try {
            PreparedStatement ps = conn.prepareStatement(sql);
            ResultSet ptRs = ps.executeQuery();

            while (ptRs.next()) {
                //Set the ENSAT ID for this record
                String ensatIdIn = ptRs.getString(2) + "-" + ptRs.getString(1);

                //Use this boolean variable to make sure there is only one suggestion per patient record (APA is checked first)
                boolean suggestionAssigned = false;

                //Check the values to be evaluated
                //Check for APA
                String reninValueIn = ptRs.getString(3);
                String aldoValueIn = ptRs.getString(4);
                if (reninValueIn != null && aldoValueIn != null) {
                    if (reninValueIn.equals("Suppressed") && aldoValueIn.equals("Elevated")) {
                        Vector<String> transferSuggested = new Vector<String>();
                        transferSuggested.add(ensatIdIn);
                        transferSuggested.add("APA");
                        potentialTransfers.add(transferSuggested);
                        suggestionAssigned = true;
                    }
                }

                if (!suggestionAssigned) {
                    //Check for Pheo
                    String catecholValue1In = ptRs.getString(5);
                    String catecholValue2In = ptRs.getString(6);
                    String catecholValue3In = ptRs.getString(7);
                    if (catecholValue1In != null && catecholValue2In != null && catecholValue3In != null) {
                        if (catecholValue1In.equals("Elevated") || catecholValue2In.equals("Elevated") || catecholValue3In.equals("Elevated")) {
                            Vector<String> transferSuggested = new Vector<String>();
                            transferSuggested.add(ensatIdIn);
                            transferSuggested.add("Pheo");
                            potentialTransfers.add(transferSuggested);
                        }
                    }
                }
            }
            ptRs.close();
        } catch (Exception e) {
            logger.debug("Error (listPotentialTransfers): " + e.getMessage());
        }
        return potentialTransfers;
    }
    
    private String[] getSubTables(String dbn){
        
        String[] subTables = new String[0];
        if(dbn.equals("ACC")){
            subTables = new String[10];
            subTables[0] = "ACC_FollowUp";
            subTables[1] = "ACC_Biomaterial";
            subTables[2] = "ACC_Chemoembolisation";
            subTables[3] = "ACC_Chemotherapy";
            subTables[4] = "ACC_Metabolomics";
            subTables[5] = "ACC_Mitotane";
            subTables[6] = "ACC_Pathology";
            subTables[7] = "ACC_Radiofrequency";
            subTables[8] = "ACC_Radiotherapy";
            subTables[9] = "ACC_Surgery";            
        } else if(dbn.equals("Pheo")){
            subTables = new String[8];
            //subTables[0] = "Pheo_BiochemicalAssessment";
            subTables[0] = "Pheo_Biomaterial";
            subTables[1] = "Pheo_ClinicalAssessment";
            subTables[2] = "Pheo_FollowUp";
            subTables[3] = "Pheo_Genetics";
            subTables[4] = "Pheo_ImagingTests";
            subTables[5] = "Pheo_NonSurgicalInterventions";
            subTables[6] = "Pheo_Surgery";
            subTables[7] = "Pheo_TumorDetails";            
        } else if(dbn.equals("NAPACA")){
            subTables = new String[6];
            subTables[0] = "NAPACA_Biomaterial";
            subTables[1] = "NAPACA_FollowUp";
            subTables[2] = "NAPACA_Imaging";
            subTables[3] = "NAPACA_Metabolomics";
            subTables[4] = "NAPACA_Pathology";
            subTables[5] = "NAPACA_Surgery";            
        } else if(dbn.equals("APA")){
            subTables = new String[8];
            subTables[0] = "APA_BiochemicalAssessment";
            subTables[1] = "APA_Biomaterial";
            subTables[2] = "APA_Cardio";
            subTables[3] = "APA_ClinicalAssessment";
            subTables[4] = "APA_Complication";
            subTables[5] = "APA_FollowUp";            
            subTables[6] = "APA_Imaging";            
            subTables[7] = "APA_Surgery";            
        }
        
        return subTables;
    }
    
    private String getSubTableDateName(String tablename){
        
        String subTableDateName = "";
        if(tablename.equals("ACC_FollowUp")){
            subTableDateName = "followup_date";
        } else if(tablename.equals("ACC_Biomaterial")){
            subTableDateName = "biomaterial_date";
        } else if(tablename.equals("ACC_Chemoembolisation")){
            subTableDateName = "chemoembolisation_date";
        } else if(tablename.equals("ACC_Chemotherapy")){
            subTableDateName = "chemotherapy_date";
        } else if(tablename.equals("ACC_Metabolomics")){
            subTableDateName = "metabolomics_date";
        } else if(tablename.equals("ACC_Mitotane")){
            subTableDateName = "mitotane_date";
        } else if(tablename.equals("ACC_Pathology")){
            subTableDateName = "pathology_date";
        } else if(tablename.equals("ACC_Radiofrequency")){
            subTableDateName = "radiofrequency_date";
        } else if(tablename.equals("ACC_Radiotherapy")){
            subTableDateName = "radiotherapy_date";
        } else if(tablename.equals("ACC_Surgery")){
            subTableDateName = "surgery_date";
        } else if(tablename.equals("Pheo_BiochemicalAssessment")){
            subTableDateName = "plasma_date";
        } else if(tablename.equals("Pheo_Biomaterial")){
            subTableDateName = "biomaterial_date";
        } else if(tablename.equals("Pheo_ClinicalAssessment")){
            subTableDateName = "assessment_date";
        } else if(tablename.equals("Pheo_FollowUp")){
            subTableDateName = "followup_date";
        } else if(tablename.equals("Pheo_Genetics")){
            subTableDateName = "genetics_date";
        } else if(tablename.equals("Pheo_ImagingTests")){
            subTableDateName = "ct_date";
        } else if(tablename.equals("Pheo_NonSurgicalInterventions")){
            subTableDateName = "mibg_therapy_date";
        } else if(tablename.equals("Pheo_Surgery")){
            subTableDateName = "surgery_date";
        } else if(tablename.equals("Pheo_TumorDetails")){
            subTableDateName = "tumor_date";
        } else if(tablename.equals("NAPACA_Biomaterial")){
            subTableDateName = "biomaterial_date";
        } else if(tablename.equals("NAPACA_FollowUp")){
            subTableDateName = "followup_date";
        } else if(tablename.equals("NAPACA_Imaging")){
            subTableDateName = "imaging_date";
        } else if(tablename.equals("NAPACA_Metabolomics")){
            subTableDateName = "metabolomics_date";
        } else if(tablename.equals("NAPACA_Pathology")){
            subTableDateName = "pathology_date";
        } else if(tablename.equals("NAPACA_Surgery")){
            subTableDateName = "surgery_date";
        } else if(tablename.equals("APA_BiochemicalAssessment")){
            subTableDateName = "assessment_date";
        } else if(tablename.equals("APA_Biomaterial")){
            subTableDateName = "biomaterial_date";
        } else if(tablename.equals("APA_Cardio")){
            subTableDateName = "event_date";
        } else if(tablename.equals("APA_ClinicalAssessment")){
            subTableDateName = "assessment_date";
        } else if(tablename.equals("APA_Complication")){
            subTableDateName = "event_date";
        } else if(tablename.equals("APA_FollowUp")){
            subTableDateName = "followup_date";
        } else if(tablename.equals("APA_Imaging")){
            subTableDateName = "imaging_date";
        } else if(tablename.equals("APA_Surgery")){
            subTableDateName = "intervention_date";
        }
                
        return subTableDateName;
    }
}
