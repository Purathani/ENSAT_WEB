/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package security;

/**
 *
 * @author anthony
 */
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.Statement;
import java.sql.PreparedStatement;
import java.sql.ResultSet;

import java.util.List;
import java.util.ArrayList;
import org.apache.log4j.Level;
import org.apache.log4j.Logger;
import org.apache.log4j.PropertyConfigurator;

public class Authz {

    private boolean recordEditable;
    
    private static final Logger logger = Logger.getLogger(Authz.class);

    public Authz() {        
        recordEditable = false;
    }

    private Statement connect(String host, String username, String password) throws Exception {
        
        String connectionURL = "jdbc:mysql://" + host + ":3306/ensat_security";
        Class.forName("com.mysql.jdbc.Driver").newInstance();
        Connection connection = DriverManager.getConnection(connectionURL, username, password);
        Statement statement = connection.createStatement();
        return statement;
    }

    public void setRecordEditable(boolean _recordEditable) {
        recordEditable = _recordEditable;
    }

    public String getRecordUploader(String pid, String centerid, Statement stmt) {
        ResultSet rs = null;
        String uploader = "";
        String sql = "SELECT uploader FROM Identification WHERE ensat_id=" + pid + " AND center_id='" + centerid + "';";
        try {
            rs = stmt.executeQuery(sql);
            if (rs != null) {
                while (rs.next()) {
                    uploader = rs.getString(1);
                }
            }
        } catch (Exception e) {
            System.out.println("Error (getRecordUploader): " + e.getMessage());
        }
        return uploader;
    }

    public boolean getRecordEditable() {
        return recordEditable;
    }

    //public void modifyRecordEditable(String username, String uploader, String pid, String centerid, String dbUsername, String password, String host) {
    public void modifyRecordEditable(String username, String uploader, String pid, String centerid, Connection conn, Connection secConn) {

        int pidInt = 0;
        boolean formatCorrect = true;
        try {
            pidInt = Integer.parseInt(pid);
            pid = "" + pidInt;
        } catch (NumberFormatException nfe) {
            System.out.println("NumberFormatException (modifyRecordEditable): " + nfe.getMessage());
            formatCorrect = false;
        }


        if (formatCorrect) {
            
            boolean usernameGerman = false;
            boolean uploaderGerman = false;
            
            //Find the German details here
            try {
                    String sql = "SELECT country FROM User WHERE email_address=?;";
                    PreparedStatement ps = secConn.prepareStatement(sql);
                    ps.setString(1, username);
                    ResultSet rs = ps.executeQuery();
                    while (rs.next()) {
                        String countryIn = rs.getString(1);
                        if(countryIn.equalsIgnoreCase("Germany")){
                            usernameGerman = true;
                        }
                    }
                    
                    PreparedStatement ps2 = secConn.prepareStatement(sql);
                    ps2.setString(1, uploader);
                    ResultSet rs2 = ps.executeQuery();
                    while (rs2.next()) {
                        String countryIn = rs2.getString(1);
                        if(countryIn.equalsIgnoreCase("Germany")){
                            uploaderGerman = true;
                        }
                    }
                } catch (Exception e) {
                    logger.info("Database connection error: " + e.getMessage());
                    //System.out.println("Database connection error: " + e.getMessage());
                }
            
            if (username.equals(uploader)) {
                recordEditable = true;
            } else if(centerid.equals("FRPA3")){
                if(username.equals("olivier")){
                    recordEditable = true;
                }
            }else if (usernameGerman){
                if(uploaderGerman){
                    recordEditable = true;
                }
            }/*
//THIS CLAUSE ALLOWS GERMAN USERS TO VIEW ALL GERMAN RECORDS (IRRESPECTIVE OF OWNER)
                    //Munich users
                    username.equalsIgnoreCase("felix.beuschlein@med.uni-muenchen.de")
                    || username.equalsIgnoreCase("urs.lichtenauer@med.uni-muenchen.de")
                    || username.equalsIgnoreCase("nicole.reisch@med.uni-muenchen.de")
                    || username.equalsIgnoreCase("brigitte.mauracher@med.uni-muenchen.de")
                    || username.equalsIgnoreCase("igor.shapiro@med.uni-muenchen.de")
                    || username.equalsIgnoreCase("susanne_schmid@med.uni-muenchen.de")
                    || username.equalsIgnoreCase("anna.pallauf@med.uni-muenchen.de")
                    || username.equalsIgnoreCase("andrea.osswald@med.uni-muenchen.de")
                    //|| username.equals("marc")
                    //|| username.equals("matthiasb")
                    || username.equalsIgnoreCase("friederike.dahm@med.uni-muenchen.de")
                    || username.equalsIgnoreCase("gabriele.breu@med.uni-muenchen.de")
                    //|| username.equals("christiane")
                    || username.equalsIgnoreCase("anna.riester@med.uni-muenchen.de")
                    //|| username.equals("sabrina")
                    || username.equalsIgnoreCase("Christina.Berr@med.uni-muenchen.de")
                    || username.equalsIgnoreCase("nina")
                    || username.equalsIgnoreCase("petrar")
                    || username.equalsIgnoreCase("annad")
                    || username.equalsIgnoreCase("nicolem")
                    || username.equalsIgnoreCase("christinabr")
                    
                    || //Wurzburg users
                    username.equalsIgnoreCase("martin")
                    || username.equalsIgnoreCase("michaela")
                    || username.equalsIgnoreCase("dirk")
                    || username.equalsIgnoreCase("kathi")
                    || username.equalsIgnoreCase("matthias")
                    || username.equalsIgnoreCase("timo")
                    || username.equalsIgnoreCase("caroline")
                    || username.equalsIgnoreCase("silviu")
                    || username.equalsIgnoreCase("stefanie")
                    || username.equalsIgnoreCase("martina")
                    || username.equalsIgnoreCase("margarita")
                    //|| username.equals("katharina")
                    || //Berlin users
                    username.equalsIgnoreCase("marcus")
                    || username.equalsIgnoreCase("tina")
                    || //Dusseldorf users
                    username.equalsIgnoreCase("ivo") ||
                    username.equalsIgnoreCase("xing") ||
                    username.equalsIgnoreCase("katharinale") ||
                    username.equalsIgnoreCase("claudia")
                    
                    || //Dresden users
                    username.equalsIgnoreCase("graeme") ||                    
                    username.equalsIgnoreCase("roland") ||
                    username.equalsIgnoreCase("christina") ||
                    username.equalsIgnoreCase("julia") ||
                    username.equalsIgnoreCase("stephang") ||
                    username.equalsIgnoreCase("mariko") ||
                    username.equalsIgnoreCase("susan") ||
                    username.equalsIgnoreCase("christinas") ||
                    username.equalsIgnoreCase("katharinal")
                    ) {
                
                
                
                if ( //Munich users
                        uploader.equalsIgnoreCase("felix.beuschlein@med.uni-muenchen.de")
                        || uploader.equalsIgnoreCase("urs.lichtenauer@med.uni-muenchen.de")
                        || uploader.equalsIgnoreCase("nicole.reisch@med.uni-muenchen.de")
                        || uploader.equalsIgnoreCase("brigitte.mauracher@med.uni-muenchen.de")
                        || uploader.equalsIgnoreCase("igor.shapiro@med.uni-muenchen.de")
                        || uploader.equalsIgnoreCase("susanne_schmid@med.uni-muenchen.de")
                        || uploader.equalsIgnoreCase("anna.pallauf@med.uni-muenchen.de")
                        || uploader.equalsIgnoreCase("andrea.osswald@med.uni-muenchen.de")
                        //|| uploader.equals("marc")
                        //|| uploader.equals("matthiasb")
                        || uploader.equalsIgnoreCase("friederike.dahm@med.uni-muenchen.de")
                        || uploader.equalsIgnoreCase("gabriele.breu@med.uni-muenchen.de")
                        //|| uploader.equals("christiane")
                        || uploader.equalsIgnoreCase("anna.riester@med.uni-muenchen.de")
                        //|| uploader.equals("sabrina")
                        || uploader.equalsIgnoreCase("christinab")
                        || uploader.equalsIgnoreCase("nina")
                        || uploader.equalsIgnoreCase("petrar")
                        || uploader.equalsIgnoreCase("annad")
                        || uploader.equalsIgnoreCase("nicolem")
                        || uploader.equalsIgnoreCase("christinabr")
                    
                        || //uploader.equals("anthony") ||
                        //Wurzburg users
                        uploader.equals("martin")
                        || uploader.equals("michaela")
                        || uploader.equals("dirk")
                        || uploader.equals("kathi")
                        || uploader.equals("matthias")
                        || uploader.equals("timo")
                        || uploader.equals("caroline")
                        || uploader.equals("silviu")
                        || uploader.equals("stefanie")
                        || uploader.equals("martina")
                        || uploader.equals("margarita")
                        || uploader.equals("katharina")
                        || //Berlin users
                        uploader.equals("marcus")
                        || uploader.equals("tina")
                        || //Dusseldorf users
                        uploader.equals("ivo") ||
                        uploader.equals("xing") ||
                        uploader.equals("katharinale") ||
                        uploader.equals("claudia")
                    
                        || //Dresden users
                        uploader.equals("graeme") ||
                        (uploader.equals("stephang") && centerid.startsWith("GY")) ||
                        uploader.equals("roland") ||
                        uploader.equals("christina") ||
                        uploader.equals("julia") ||
                        uploader.equals("mariko") ||
                        uploader.equals("susan") ||
                        uploader.equals("christinas") ||
                        uploader.equals("katharinal")
                        ) {
                    recordEditable = true;
                }
            }*/ else if ( //THIS CLAUSE ALLOWS THE EURINE-ACT USERS IN BIRMINGHAM TO ACCESS RELEVANT RECORDS                
                    username.equalsIgnoreCase("d.m.oneil@bham.ac.uk")
                    || username.equalsIgnoreCase("wiebke")
                    || username.equalsIgnoreCase("tracymclean6@gmail.com")                    
                    || (username.equalsIgnoreCase("stephan.gloeckner@uniklinikum-dresden.de") && centerid.equalsIgnoreCase("GBBI"))
                    || username.equalsIgnoreCase("bancos.irina@mayo.edu")                    
                    || username.equalsIgnoreCase("a.taylor.5@bham.ac.uk")
                    || username.equalsIgnoreCase("petra")
                    || username.equalsIgnoreCase("bchortis@hotmail.com")
                    || username.equalsIgnoreCase("d.l.mccartney@bham.ac.uk")
                    || username.equalsIgnoreCase("j.idkowiak@bham.ac.uk")
                    || username.equalsIgnoreCase("priyanka")
                    || username.equalsIgnoreCase("z.hassansmith@bham.ac.uk")
                    || username.equalsIgnoreCase("a.i.hughes@bham.ac.uk ")
                    || username.equalsIgnoreCase("k.lang@bham.ac.uk")
                    
                    ) {
                
                if(uploader.equalsIgnoreCase("d.m.oneil@bham.ac.uk")
                    || uploader.equalsIgnoreCase("wiebke")
                    || uploader.equalsIgnoreCase("tracymclean6@gmail.com")                    
                    || (uploader.equalsIgnoreCase("stephan.gloeckner@uniklinikum-dresden.de") && centerid.equalsIgnoreCase("GBBI"))
                    || uploader.equalsIgnoreCase("bancos.irina@mayo.edu")                    
                    || uploader.equalsIgnoreCase("a.taylor.5@bham.ac.uk")
                    || uploader.equalsIgnoreCase("petra")
                    || uploader.equalsIgnoreCase("bchortis@hotmail.com")
                    || uploader.equalsIgnoreCase("d.l.mccartney@bham.ac.uk")
                    || uploader.equalsIgnoreCase("j.idkowiak@bham.ac.uk")
                    || uploader.equalsIgnoreCase("priyanka")
                    || uploader.equalsIgnoreCase("z.hassansmith@bham.ac.uk")
                    || uploader.equalsIgnoreCase("k.lang@bham.ac.uk")
                    || uploader.equalsIgnoreCase("a.i.hughes@bham.ac.uk ")){
                    recordEditable = true;
                }else {
               
                //Get the EURINE-ACT designation for this particular patient
                //String connectionURL = "jdbc:mysql://192.168.101.63:3306/ensat_v3";
                //String connectionURL = "jdbc:mysql://" + host + ":3306/ensat_v3";
                
                String eurineActOutput = "No";
                try {
                    //Class.forName("com.mysql.jdbc.Driver").newInstance();                    
                    //Connection connection = DriverManager.getConnection(connectionURL, "root", "ps4Xy2a");                    
                    //Connection connection = DriverManager.getConnection(connectionURL, dbUsername, password);

                    String sql = "SELECT study_name FROM Associated_Studies WHERE center_id=? AND ensat_id=?;";
                    PreparedStatement ps = conn.prepareStatement(sql);
                    ps.setString(1, centerid);
                    ps.setString(2, pid);
                    ResultSet rs = ps.executeQuery();
                    while (rs.next()) {
                        String studyNameIn = rs.getString(1);
                        if(studyNameIn.equals("eurineact")){
                            eurineActOutput = "Yes";
                        }                        
                    }
                } catch (Exception e) {
                    logger.info("Database connection error: " + e.getMessage());
                    //System.out.println("Database connection error: " + e.getMessage());
                }

                recordEditable = eurineActOutput.equals("Yes");
                
                /*if(username.equals("stephang") || username.equals("irina") || username.equals("wiebke")){
                    if(centerid.equals("GBBI")){
                        recordEditable = true;
                    }                    
                }*/
                }
            }/* else if (uploader.equals("irina")) {

                //THIS CLAUSE ALLOWS SHARING OF EDITING RIGHTS WITHIN THE BIRMINGHAM CENTER
                if (username.equals("stephang")) {
                    recordEditable = true;
                }else if(username.equals("vasilis")){
                    recordEditable = true;
                }
            }else if(uploader.equals("stephang") && centerid.equals("GBBI")){
                
                //THIS CLAUSE ALLOWS SHARING OF EDITING RIGHTS WITHIN THE BIRMINGHAM CENTER
                if (username.equals("irina")) {
                    recordEditable = true;
                }else if(username.equals("vasilis")){
                    recordEditable = true;
                }
            }else if (uploader.equals("henri")) {

                //THIS CLAUSE ALLOWS SHARING OF EDITING RIGHTS WITHIN THE NIJMEGEN CENTER
                
            }*/else if(uploader.equalsIgnoreCase("i.piscaer@aig.umcn.nl") || uploader.equalsIgnoreCase("h.timmers@endo.umcn.nl") || uploader.equalsIgnoreCase("a.vanberkel@aig.umcn.nl") || uploader.equalsIgnoreCase("c.vogel@endo.umcn.nl") || uploader.equalsIgnoreCase("dipti.rao@radboudumc.nl")){
                if (username.equalsIgnoreCase("i.piscaer@aig.umcn.nl")) {
                    recordEditable = true;
                }else if(username.equalsIgnoreCase("a.vanberkel@aig.umcn.nl")){
                    recordEditable = true;
                }else if(username.equalsIgnoreCase("h.timmers@endo.umcn.nl")){
                    recordEditable = true;
                }else if(username.equalsIgnoreCase("c.vogel@endo.umcn.nl")){
                    recordEditable = true;
                }else if(username.equalsIgnoreCase("dipti.rao@radboudumc.nl")){
                    recordEditable = true;
                }
            }else if (uploader.equalsIgnoreCase("sarah.cazenave@chu-bordeaux.fr")) {

                //THIS CLAUSE ALLOWS SHARING OF EDITING RIGHTS WITHIN THE BORDEAUX CENTER (FRBO)
                if (username.equalsIgnoreCase("antoine.tabarin@chu-bordeaux.fr")) {
                    recordEditable = true;
                }
            }else if (uploader.equalsIgnoreCase("antoine.tabarin@chu-bordeaux.fr")) {

                //THIS CLAUSE ALLOWS SHARING OF EDITING RIGHTS WITHIN THE BORDEAUX CENTER (FRBO)
                if (username.equalsIgnoreCase("sarah.cazenave@chu-bordeaux.fr")) {
                    recordEditable = true;
                }
            }else if (uploader.equalsIgnoreCase("alfredo.berruti@gmail.com")) {

                //THIS CLAUSE ALLOWS SHARING OF EDITING RIGHTS WITHIN THE BRESCIA2 CENTER (ITBR2)
                if (username.equalsIgnoreCase("ester.oneda@hotmail.it")) {
                    recordEditable = true;
                }
            }else if (uploader.equalsIgnoreCase("ester.oneda@hotmail.it")) {

                //THIS CLAUSE ALLOWS SHARING OF EDITING RIGHTS WITHIN THE BRESCIA CENTER (ITBR2)
                if (username.equalsIgnoreCase("alfredo.berruti@gmail.com")) {
                    recordEditable = true;
                }
            }else if (uploader.equalsIgnoreCase("tomyg_24@hotmail.com")) {

                //THIS CLAUSE ALLOWS SHARING OF EDITING RIGHTS WITHIN THE GALWAY CENTER (IRGA)
                if (username.equalsIgnoreCase("cdennedy@me.com")) {
                    recordEditable = true;
                }
            }else if (uploader.equalsIgnoreCase("laurence.amar@egp.aphp.fr")) {

                //THIS CLAUSE ALLOWS SHARING OF EDITING RIGHTS WITHIN THE HEGP PARIS CENTER (FRPA1)
                if (username.equalsIgnoreCase("adela_delcea@yahoo.com") || username.equalsIgnoreCase("annepaule") || username.equalsIgnoreCase("mariachristina")) {
                    recordEditable = true;
                }
            } else if (uploader.equalsIgnoreCase("adela_delcea@yahoo.com")) {

                //THIS CLAUSE ALLOWS SHARING OF EDITING RIGHTS WITHIN THE HEGP PARIS CENTER (FRPA1)
                if (username.equalsIgnoreCase("laurence.amar@egp.aphp.fr") || username.equalsIgnoreCase("annepaule") || username.equalsIgnoreCase("mariachristina")) {
                    recordEditable = true;
                }
            } else if (uploader.equalsIgnoreCase("rossella.libe@cch.aphp.fr")) {

                if (centerid.equalsIgnoreCase("FRPA1") && (username.equalsIgnoreCase("laurence.amar@egp.aphp.fr") || username.equalsIgnoreCase("adela_delcea@yahoo.com") || username.equalsIgnoreCase("annepaule") || username.equalsIgnoreCase("mariachristina"))) {
                    recordEditable = true;
                }
                
                //Allowing Delphine in Toulouse access to select records
                if(username.equals("delphine")){
                    if(centerid.equals("FRPA3")){
                        if(pid.equals("48")
                                || pid.equals("48")
                                || pid.equals("53")
                                || pid.equals("55")
                                || pid.equals("59")
                                || pid.equals("75")
                                || pid.equals("76")
                                || pid.equals("150")
                                || pid.equals("155")
                                || pid.equals("157")
                                || pid.equals("158")
                                || pid.equals("159")
                                || pid.equals("160")
                                || pid.equals("116")
                                || pid.equals("168")
                                || pid.equals("169")
                                || pid.equals("181")
                                || pid.equals("191")
                                || pid.equals("192")
                                ){
                            recordEditable = true;
                        }else{
                            recordEditable = false;
                        }
                    }else if(centerid.equals("FRTO")){
                        if(pid.equals("21")
                                ){
                            recordEditable = true;
                        }else{
                            recordEditable = false;
                        }
                    }else if(centerid.equals("FRPA2")){
                        if(pid.equals("132")
                                || pid.equals("137")
                                ){
                            recordEditable = true;
                        }else{
                            recordEditable = false;
                        }
                    }else{
                        recordEditable = false;
                    }
                }   
                
                if (username.equalsIgnoreCase("delphined")) {
                    if(centerid.equals("FRNA")){
                        recordEditable = true;
                    } 
                }

                //THIS CLAUSE ALLOWS SHARING OF EDITING RIGHTS WITHIN THE MAJORITY OF FRENCH CENTERS (UPLOADED USING ROSSELLA'S LOGIN, BUT ACTUALLY DONE BY DENIS)
                if (username.equalsIgnoreCase("denis")) {
                    recordEditable = true;
                }
            }else if(username.equalsIgnoreCase("stephan.gloeckner@uniklinikum-dresden.de")){                
                if(centerid.equals("GBBI") || centerid.equals("GYDR")){
                    recordEditable = true;
                }                
            }else if (uploader.equalsIgnoreCase("terzolo@usa.net")) {

                //THIS CLAUSE ALLOWS SHARING OF EDITING RIGHTS WITHIN THE TURIN CENTER (ITTU)
                if (username.equalsIgnoreCase("basile_vittoria@libero.it") || username.equalsIgnoreCase("oncotrial.sanluigi@gmail.com")) {
                    recordEditable = true;
                }
            }else if (uploader.equalsIgnoreCase("basile_vittoria@libero.it")) {

                //THIS CLAUSE ALLOWS SHARING OF EDITING RIGHTS WITHIN THE TURIN CENTER (ITTU)
                if (username.equalsIgnoreCase("terzolo@usa.net") || username.equalsIgnoreCase("oncotrial.sanluigi@gmail.com")) {
                    recordEditable = true;
                }
            }else if (uploader.equalsIgnoreCase("oncotrial.sanluigi@gmail.com")) {

                //THIS CLAUSE ALLOWS SHARING OF EDITING RIGHTS WITHIN THE TURIN CENTER (ITTU)
                if (username.equalsIgnoreCase("terzolo@usa.net") || username.equalsIgnoreCase("basile_vittoria@libero.it")) {
                    recordEditable = true;
                }
            }else if(username.equalsIgnoreCase("terzolo@usa.net") || username.equalsIgnoreCase("oncotrial.sanluigi@gmail.com")){
                String adiuvoOutput = "No";
                    try {
                    
                        String sql = "SELECT study_name FROM Associated_Studies WHERE center_id=? AND ensat_id=?;";
                        PreparedStatement ps = conn.prepareStatement(sql);
                        ps.setString(1, centerid);
                        ps.setString(2, pid);
                        ResultSet rs = ps.executeQuery();
                        while (rs.next()) {
                            String studyNameIn = rs.getString(1);
                            if(studyNameIn.equals("adiuvo") || studyNameIn.equals("adiuvo_observational")){
                                adiuvoOutput = "Yes";
                            }                        
                        }
                    } catch (Exception e) {
                        logger.info("Database connection error: " + e.getMessage());                        
                    }

                    recordEditable = adiuvoOutput.equals("Yes");                    
            }else if(centerid.equals("GYWU") && (pid.equals("1283") || pid.equals("1257") || pid.equals("1292") || pid.equals("1310"))){
                if(username.equalsIgnoreCase("mrobledo@cnio.es")){
                    recordEditable = true;
                }
            }else if (uploader.equalsIgnoreCase("m.mannelli@dfc.unifi.it") || uploader.equalsIgnoreCase("benedettazampetti@yahoo.it") || uploader.equalsIgnoreCase("letiziacanu@yahoo.it")) {

                //THIS CLAUSE ALLOWS SHARING OF EDITING RIGHTS WITHIN THE FLORENCE CENTER (ITFL)
                if (username.equalsIgnoreCase("m.mannelli@dfc.unifi.it") || username.equalsIgnoreCase("benedettazampetti@yahoo.it") || username.equalsIgnoreCase("letiziacanu@yahoo.it")) {
                    recordEditable = true;
                }
            }else if (uploader.equalsIgnoreCase("s.g.ball@newcastle.ac.uk") || uploader.equalsIgnoreCase("yasmin.clark@nhs.net") || uploader.equalsIgnoreCase("paul.burn3@nuth.nhs.net") || uploader.equalsIgnoreCase("dianne.wake@nuth.nhs.net") || uploader.equalsIgnoreCase("victoria.richardson@nuth.nhs.uk")) {

                //THIS CLAUSE ALLOWS SHARING OF EDITING RIGHTS WITHIN THE NEWCASTLE CENTER (GBNE)
                if (username.equalsIgnoreCase("s.g.ball@newcastle.ac.uk") || username.equalsIgnoreCase("yasmin.clark@nhs.net") || username.equalsIgnoreCase("paul.burn3@nuth.nhs.net") || username.equalsIgnoreCase("dianne.wake@nuth.nhs.net") || username.equalsIgnoreCase("victoria.richardson@nuth.nhs.uk")) {
                    recordEditable = true;
                }
            }else if (uploader.equalsIgnoreCase("bernard.goichot@chru-strasbourg.fr") || uploader.equalsIgnoreCase("luz.siegel@chru-strasbourg.fr")) {

                //THIS CLAUSE ALLOWS SHARING OF EDITING RIGHTS WITHIN THE STRASBOURG CENTER (FRST)
                if (username.equalsIgnoreCase("bernard.goichot@chru-strasbourg.fr") || username.equalsIgnoreCase("luz.siegel@chru-strasbourg.fr")) {
                    recordEditable = true;
                }
            }else if (username.equalsIgnoreCase("segolene.hescot@u-psud.fr")) {
                String mappPronoOutput = "No";
                try {
                    
                    String sql = "SELECT study_name FROM Associated_Studies WHERE center_id=? AND ensat_id=?;";
                    PreparedStatement ps = conn.prepareStatement(sql);
                    ps.setString(1, centerid);
                    ps.setString(2, pid);
                    ResultSet rs = ps.executeQuery();
                    while (rs.next()) {
                        String studyNameIn = rs.getString(1);
                        if(studyNameIn.equals("mapp_prono")){
                            mappPronoOutput = "Yes";
                        }                        
                    }
                } catch (Exception e) {
                    logger.info("Database connection error: " + e.getMessage());                        
                }

                recordEditable = mappPronoOutput.equals("Yes");                
                
            }else {
                recordEditable = false;
            }

        }
    }

    public boolean getSecurityShow(String consent, String uploader, String country, String host, String username, String password) {

        List<String> countries = this.getCountries(host,username,password);
        List<String> usernames = this.getUsernames(host,username,password);
        
        //logger.debug("countries (getSecurityShow): " + countries);
        //logger.debug("usernames (getSecurityShow): " + usernames);        
        //logger.debug("uploader (getSecurityShow): " + uploader);

        boolean securityShow = false;
        if (recordEditable) {
            securityShow = true;
        } else {
            if (consent.equals("Local")) {
                securityShow = false;
            } else if (consent.equals("National")) {
                int uploaderIndex = -1;
                for (int j = 0; j < usernames.size(); j++) {
                    if (usernames.get(j).equals(uploader)) {
                        uploaderIndex = j;
                    }
                }
                String uploaderCountry = countries.get(uploaderIndex);
                logger.debug("uploaderCountry (getSecurityShow): " + uploaderCountry);
                securityShow = uploaderCountry.equals(country);
            } else {
                securityShow = true;
            }
        }
        return securityShow;
    }

    public List<String> getCountries(String host, String username, String password) {

        List<String> countries = new ArrayList<String>();
        String sql = "SELECT country FROM User";
        try {
            Statement stmt = this.connect(host, username, password);
            ResultSet rs = stmt.executeQuery(sql);

            while (rs.next()) {
                String countryIn = rs.getString(1);
                countries.add(countryIn);
            }

            rs.close();
            stmt.close();
        } catch (Exception e) {
            System.out.println("Error (getCountries): " + e.getMessage());
        }

        return countries;
    }

    public List<String> getUsernames(String host, String username, String password) {

        List<String> usernames = new ArrayList<String>();
        //String sql = "SELECT username FROM User";
        String sql = "SELECT email_address FROM User";
        try {
            Statement stmt = this.connect(host, username, password);
            ResultSet rs = stmt.executeQuery(sql);
            while (rs.next()) {
                String usernameIn = rs.getString(1);
                usernames.add(usernameIn);
            }
            rs.close();
            stmt.close();
        } catch (Exception e) {
            System.out.println("Error (getUsernames): " + e.getMessage());
        }

        return usernames;
    }

    private String standardisePid(String pid) {
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
}
