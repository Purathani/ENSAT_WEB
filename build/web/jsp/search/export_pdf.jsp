<%@ page language="java" import="java.util.*,java.sql.*,java.io.*,java.text.Format,java.text.SimpleDateFormat" pageEncoding="ISO-8859-1"%>

<jsp:include page="/jsp/page/check_credentials.jsp" />
<jsp:include page="/jsp/page/check_input.jsp" />
<jsp:include page="/jsp/page/page_head.jsp" />

<td colspan="2" width="81%" align="left" valign="top" background="/images/possbk.jpg"><table width="1200" border="0" cellspacing="0" cellpadding="10"> 
      <tr> 
        <td width="15" align="left" valign="top">&nbsp;</td> 
        <td align="left" valign="top"><!-- #BeginEditable "MainText" --> 

<jsp:include page="/jsp/page/page_nav.jsp" />

<jsp:useBean id='connect' class='ConnectBean.ConnectBean'  scope='session'/>
<jsp:useBean id='user' class='user.UserBean'  scope='session'/>
<jsp:useBean id='pdf' class='PDFBean.PDFBean'  scope='session'/>

<%
String username = user.getUsername();
String country = user.getCountry();

String a4request = request.getParameter("print_labels_a4");
if(a4request == null){
    a4request = "";
}
boolean a4labels = !a4request.equals("");

String dbn = request.getParameter("dbn");
String dbid = request.getParameter("dbid");

String pid = request.getParameter("pid");
String centerid = request.getParameter("centerid");
String modality = request.getParameter("modality");
String modid = request.getParameter("modid");

    //Add leading zero's
    String pidDisp = pid;
    if(pid.length() == 1){
        pidDisp = "000" + pid;
    }else if(pid.length() == 2){
        pidDisp = "00" + pid;
    }else if(pid.length() == 3){
        pidDisp = "0" + pid;
    }
    
    String idtoprint = centerid + pidDisp;

%>

<h2>Patient <%=centerid%>-<%=pidDisp%> Labels</h2>


    <%
Connection connection = null;
ResultSet rs = null;
ResultSet rs2 = null;

connection = connect.getConnection();

%>


<%

String filename = "";
String filepath = "exported_files/" + country;

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

StringTokenizer st2 = new StringTokenizer(formTime,":");
String formHour = st2.nextToken();
String formMin = st2.nextToken();

filename += formDate + "_" + formHour + formMin;


//Get username from security
//String username = "test";

filename += "_" + username;

filename += "_" + centerid + pidDisp + "_" + modid;



//Need to find out the multiple "Yes" items here and divide the file names appropriately

//Pull out the appropriate information
String bioTable = "";
String bioMultTable = "";
String bioId = "";
if(dbn.equals("ACC")){
    bioTable = "ACC_Biomaterial";
    bioMultTable = "ACC_Biomaterial_Normal_Tissue";
    bioId = "acc_biomaterial_id";
}else if(dbn.equals("Pheo")){
    bioTable = "Pheo_Biomaterial";
    bioMultTable = "Pheo_Biomaterial_Normal_Tissue";
    bioId = "pheo_biomaterial_id";
}else if(dbn.equals("NAPACA")){
    bioTable = "NAPACA_Biomaterial";
    bioMultTable = "NAPACA_Biomaterial_Normal_Tissue";
    bioId = "napaca_biomaterial_id";
}else if(dbn.equals("APA")){
    bioTable = "APA_Biomaterial";
    bioMultTable = "APA_Biomaterial_Normal_Tissue";
    bioId = "apa_biomaterial_id";
}

//Collect all the data about the biomaterial form (non-multiple)
String biomaterialQuery = "SELECT * FROM " + bioTable + " WHERE ensat_id=" + pid + " AND center_id='" + centerid + "' AND " + bioId + "=" + modid + ";";
ResultSet bio_rs = statement.executeQuery(biomaterialQuery);

String [][] bioOutput = null;
int bioOutputNum = 0;

if(dbn.equals("Pheo")){
    bioOutputNum = 18;    
}else{
    bioOutputNum = 17;    
}
bioOutput = new String[2][bioOutputNum];

String [] aliquotNumbers = new String[bioOutputNum];
for(int i=0; i<bioOutputNum; i++){
    String aliquotNumberIn = request.getParameter("aliquot_" + i);
    if(aliquotNumberIn == null || aliquotNumberIn.equals("null")){
        aliquotNumbers[i] = "0";
    }else{
        aliquotNumbers[i] = aliquotNumberIn;        
    }
}

while(bio_rs.next()){    
    for(int i=0; i<bioOutputNum; i++){
       if(i != 15 && i != 16){
            bioOutput[1][i] = "";
            bioOutput[1][i] = bio_rs.getString(i+1);
       }else if(i == 15){
            bioOutput[1][i] = "";
            if(dbn.equals("Pheo")){
                bioOutput[1][i] = bio_rs.getString(20);            
            }else{
                bioOutput[1][i] = bio_rs.getString(19);            
            }
       }else if(i == 16){
           bioOutput[1][i] = "";
            if(dbn.equals("Pheo")){
                bioOutput[1][i] = bio_rs.getString(21);            
            }else{
                bioOutput[1][i] = bio_rs.getString(20);            
            }
       }
    }
}
bio_rs.close();

int bioMultNum = 0;
String [][] bioMultOutput = null;

String biomaterialQueryMult = "SELECT * FROM " + bioMultTable + " WHERE ensat_id=" + pid + " AND center_id='" + centerid + "' AND " + bioId + "=" + modid + ";";
ResultSet bio_mult_rs = statement.executeQuery(biomaterialQueryMult);

while(bio_mult_rs.next()){    
    bioMultNum++;
}

bioMultOutput = new String[2][bioMultNum];
bio_mult_rs = statement.executeQuery(biomaterialQueryMult);

int bioMultCount = 0;
while(bio_mult_rs.next()){    
    bioMultOutput[0][bioMultCount] = bio_mult_rs.getString(5);
    bioMultOutput[1][bioMultCount] = bio_mult_rs.getString(6);       
    bioMultCount++;
}
bio_mult_rs.close();



/**
* bioOutput array:

4 = tumor_tissue_frozen
5 = tumor_tissue_ensat_sop
6 = tumor_tissue_paraffin
7 = tumor_tissue_dna
8 = leukocyte_dna
9 = plasma
10 = serum
11 = 24h_urine
12 = 24h_urine_vol
13 = spot_urine
14 = normal_tissue
15 = normal_tissue_specify
16 = normal_tissue_paraffin
17 = normal_tissue_paraffin_specify
18 = normal_tissue_dna
19 = normal_tissue_dna_specify
20 = associated_study
21 = associated_study_phase_visit

*/
bioOutput[0][4] = "Tumor Tissue (Frozen";
bioOutput[0][5] = "Tumor Tissue (ENSAT SOP)";
bioOutput[0][6] = "Tumor Tissue (Paraffin)";
bioOutput[0][7] = "Tumor Tissue (DNA)";
bioOutput[0][8] = "Leukocyte DNA";
bioOutput[0][9] = "EDTA Plasma";
bioOutput[0][10] = "Heparin Plasma";
bioOutput[0][11] = "Serum";
bioOutput[0][12] = "24h Urine";
bioOutput[0][13] = "24h Urine (Volume)";
bioOutput[0][14] = "Spot Urine";
bioOutput[0][15] = "Associated Study";
bioOutput[0][16] = "Associated Study Phase/Visit";
if(dbn.equals("Pheo")){
    bioOutput[0][17] = "Whole Blood";
}

//Calculate total label numbers (yesCount multiplied by the relevant aliquot numbers)
int totalLabelNum = 0;

int yesCount = 0;
for(int i=0; i<bioOutputNum; i++){
    if(bioOutput[1][i].equals("Yes")){
        if(i != 5 && i != 13){
            yesCount++;
        }
    }
}

for(int i=0; i<bioOutputNum; i++){
    if(bioOutput[1][i].equals("Yes")){
        if(i != 5 && i != 13){
            int aliquotNumInt = Integer.parseInt(aliquotNumbers[i]);        
            totalLabelNum += aliquotNumInt;       
        }
    }
}

    
    int frozenCount = 0;
    int frozenAliquotNum = 0;
    int paraffinCount = 0;
    int paraffinAliquotNum = 0;
    int dnaCount = 0;
    int dnaAliquotNum = 0;
    
    for(int i=0; i<bioMultNum; i++){        
        if(bioMultOutput[0][i].equals("frozen")){
            frozenCount++;
        }else if(bioMultOutput[0][i].equals("paraffin")){
            paraffinCount++;
        }else if(bioMultOutput[0][i].equals("dna")){
            dnaCount++;
        }
    }    
    
    if(frozenCount > 0){
        try{
            String aliquotNumberIn = request.getParameter("aliquot_16");
            frozenAliquotNum = Integer.parseInt(aliquotNumberIn);        
        }catch(NumberFormatException nfe){
            frozenAliquotNum = 0;
        }
        totalLabelNum += frozenCount*frozenAliquotNum;
    }

    if(paraffinCount > 0){
        try{
            String aliquotNumberIn = request.getParameter("aliquot_18");
            paraffinAliquotNum = Integer.parseInt(aliquotNumberIn);        
        }catch(NumberFormatException nfe){
            paraffinAliquotNum = 0;
        }        
        totalLabelNum += paraffinCount*paraffinAliquotNum;
    }

    if(dnaCount > 0){
        try{
            String aliquotNumberIn = request.getParameter("aliquot_20");
            dnaAliquotNum = Integer.parseInt(aliquotNumberIn);        
        }catch(NumberFormatException nfe){
            dnaAliquotNum = 0;
        }        
        totalLabelNum += dnaCount*dnaAliquotNum;
    }

String [] labelOutputStr = new String[totalLabelNum];
String [] imageFilenameSuffix = new String[totalLabelNum];
String pdfFilename = "";

int labelCount = 0;
for(int i=0; i<bioOutputNum; i++){
    if(bioOutput[1][i].equals("Yes")){
        
        if(i != 5){
        
        int thisAliquotNum = Integer.parseInt(aliquotNumbers[i]);        

        for(int j=0; j<thisAliquotNum; j++){
        
            if(i != 12 && i != 13 && i != 4){
                labelOutputStr[labelCount] = centerid + "-" + pidDisp + "\r\nbio-ID " + modid + "\r\nStudy: " + bioOutput[1][15];
                if(bioOutput[1][15].equals("PMT") || bioOutput[1][15].equals("German Conn Registry") || bioOutput[1][15].equals("German Cushing Registry")){
                    labelOutputStr[labelCount] += " (" + bioOutput[1][16] + ")";
                }
                labelOutputStr[labelCount] += "\r\nDate: " + bioOutput[1][3] + "\r\n" + bioOutput[0][i] + /*"\r\nAliquot: " + (j+1) + */"";
            }else if(i == 12){
                labelOutputStr[labelCount] = centerid + "-" + pidDisp + "\r\nbio-ID " + modid + "\r\nStudy: " + bioOutput[1][15];
                if(bioOutput[1][15].equals("PMT") || bioOutput[1][15].equals("German Conn Registry") || bioOutput[1][15].equals("German Cushing Registry")){
                    labelOutputStr[labelCount] += " (" + bioOutput[1][16] + ")";
                }                
                labelOutputStr[labelCount] += "\r\nDate: " + bioOutput[1][3] + "\r\n" + bioOutput[0][12] + " (" + bioOutput[1][13] + " ml)"/*\r\nAliquot: " + (j+1)*/ + "";
            }else if(i == 4){
                boolean ensatSopLabel = bioOutput[1][5].equals("Yes");                
                labelOutputStr[labelCount] = centerid + "-" + pidDisp + "\r\nbio-ID " + modid + "\r\nStudy: " + bioOutput[1][15];
                if(bioOutput[1][15].equals("PMT") || bioOutput[1][15].equals("German Conn Registry") || bioOutput[1][15].equals("German Cushing Registry")){
                    labelOutputStr[labelCount] += " (" + bioOutput[1][16] + ")";
                }                                                
                labelOutputStr[labelCount] += "\r\nDate: " + bioOutput[1][3] + "\r\n" + bioOutput[0][i];
                /*if(ensatSopLabel){
                    labelOutputStr[labelCount] += " - Follows ENSAT SOP";
                }*/                
                //labelOutputStr[labelCount] += "\r\nAliquot: " + (j+1) + "";
            }

            //Add trailer               
            imageFilenameSuffix[labelCount] = "/opt/apache-tomcat-7.0.19/webapps/ROOT/" + filepath + "/" + filename + "_" + (labelCount+1) + "_" + (j+1) + "";        
        
            //Create QR image
            //pdf.createQR(imageFilenameSuffix[labelCount], labelOutputStr[labelCount]);
            pdf.createQR(imageFilenameSuffix[labelCount], modid,a4labels);
                    
            labelCount++;            
        }        
    }
       }
}

for(int i=0; i<bioMultNum; i++){    
    
    int thisAliquotNum = 0;
    String outputLabel = "";
    if(bioMultOutput[0][i].equals("frozen")){
        thisAliquotNum = frozenAliquotNum;
        outputLabel = "Normal Tissue - Frozen";
    }else if(bioMultOutput[0][i].equals("paraffin")){
        thisAliquotNum = paraffinAliquotNum;
        outputLabel = "Normal Tissue - Paraffin";
    }else if(bioMultOutput[0][i].equals("dna")){
        thisAliquotNum = dnaAliquotNum;
        outputLabel = "Normal Tissue - DNA";
    }
    
    

        for(int j=0; j<thisAliquotNum; j++){        

            labelOutputStr[labelCount] = centerid + "-" + pidDisp + "\r\nbio-ID " + modid + "\r\nStudy: " + bioOutput[1][15];
            if(bioOutput[1][15].equals("PMT") || bioOutput[1][15].equals("German Conn Registry") || bioOutput[1][15].equals("German Cushing Registry")){
                    labelOutputStr[labelCount] += " (" + bioOutput[1][16] + ")";
                }                                            
            labelOutputStr[labelCount] += "\r\nDate: " + bioOutput[1][3] + "\r\n" + outputLabel + " \r\n(" + bioMultOutput[1][i] + ")"/*\r\nAliquot: " + (j+1)*/ + "";
        
        //Add trailer               
        imageFilenameSuffix[labelCount] = "/opt/apache-tomcat-7.0.19/webapps/ROOT/" + filepath + "/" + filename + "_" + (labelCount+1) + "_" + (j+1) + "";        
        
        //Create QR image
        //pdf.createQR(imageFilenameSuffix[labelCount], labelOutputStr[labelCount]);
        pdf.createQR(imageFilenameSuffix[labelCount], modid,a4labels);
                    
        labelCount++;            
        }            
}

String filenameSuffix = filename + "_export.pdf";
pdfFilename = "/opt/apache-tomcat-7.0.19/webapps/ROOT/" + filepath + "/" + filenameSuffix;
pdf.createPdf(pdfFilename,labelOutputStr,imageFilenameSuffix,a4labels);

%>

<%--bioMultNum: <%=bioMultNum%><br/>
frozenCount: <%=frozenCount%><br/>
paraffinCount: <%=paraffinCount%><br/>
dnaCount: <%=dnaCount%><br/>
frozenAliquotNum: <%=frozenAliquotNum%><br/>--%>
<%--<%
for(int i=0; i<bioOutputNum; i++){
%>
bioOutput[<%=i%>]: <%=bioOutput[0][i]%> - <%=bioOutput[1][i]%><br/>

<%
}
%>--%>


<p><%=a4request%></p>

<p>File successfully exported to: <a target="_blank" href="/<%=filepath%>/<%=filenameSuffix%>"><%=filenameSuffix%></a></p>

<p><hr/></p>

<p><strong>Return to <a href="/jsp/dbhome.jsp?dbn=<%=dbn%>&dbid=<%=dbid%>&page=1"><%=dbn%> Home</a></strong></p>

<p><strong>Return to <a href="/jsp/read/detail.jsp?dbid=<%=dbid%>&dbn=<%=dbn%>&pid=<%=pid%>&centerid=<%=centerid%>">Patient <%=centerid%>-<%=pid%> Detail</a></strong></p>

<p><strong>Return to <a href="/jsp/modality/home.jsp?dbid=<%=dbid%>&dbn=<%=dbn%>&pid=<%=pid%>&centerid=<%=centerid%>&modality=biomaterial">Patient <%=centerid%>-<%=pid%> Biomaterial Page</a></strong></p>



<jsp:include page="/jsp/page/page_foot.jsp" />