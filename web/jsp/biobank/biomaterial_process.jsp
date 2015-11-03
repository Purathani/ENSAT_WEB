<%@ page language="java" import="java.util.*,java.sql.*,org.apache.log4j.*,java.io.*,org.apache.poi.xssf.usermodel.*" pageEncoding="ISO-8859-1"%>

<jsp:include page="/jsp/page/check_credentials.jsp" />
<jsp:include page="/jsp/page/check_input.jsp" />
<jsp:include page="/jsp/page/page_head.jsp" />

<td colspan="2" width="81%" align="left" valign="top" background="/images/possbk.jpg"><table width="1200" border="0" cellspacing="0" cellpadding="10"> 
      <tr> 
        <td width="15" align="left" valign="top">&nbsp;</td> 
        <td align="left" valign="top"><!-- #BeginEditable "MainText" --> 

<jsp:include page="/jsp/page/page_nav.jsp" />

<jsp:useBean id='connect' class='ConnectBean.ConnectBean' scope='session'/>
<jsp:useBean id='presentation' class='summaryinfo.SummaryInfo' scope='session'/>
<jsp:useBean id='utilities' class='update_main.Utilities' scope='session'/>
<jsp:useBean id='user' class='user.UserBean'  scope='session'/>

<%    
ServletContext context = this.getServletContext();    
Connection conn = connect.getConnection();
    
//Logging configuration
String log4jConfigFile = context.getInitParameter("log4j_property_file");
Logger logger = Logger.getLogger("rootLogger");
logger.setLevel(Level.DEBUG);
PropertyConfigurator.configure(log4jConfigFile);
        
String username = user.getUsername();
logger.debug("('" + username + "') - biomaterial_process.jsp");

String centerid = request.getParameter("centerid");
if(centerid == null){
    centerid = "";
}

//Get all the freezer positions for this centerid

//summaryinfo.SummaryInfo presentation = new summaryinfo.SummaryInfo();
Vector<Vector> freezerData = presentation.getAllFreezerData(centerid, conn, true); //boolean flag to use biomaterial date
String dbn = "";
String htmlOut = "";

int[] capacities = utilities.getFreezerCapacities(centerid, conn);

%>

<%

//Get the content type information from JSP Request Header
String contentType = request.getContentType();

//Checking the content type is not equal to null and as well as the passed data from mulitpart/form-data is greater than or equal to 0 (i.e. not empty)
if ((contentType != null) && (contentType.indexOf("multipart/form-data") >= 0)) {

    logger.debug("Within file analysis...");
    
    //Retrieve the file from upload form and convert into a byte array
    DataInputStream in = new DataInputStream(request.getInputStream());

    //Take the length of Content type data
    int formDataLength = request.getContentLength();
    byte[] dataBytes = utilities.convertDataStream(formDataLength, in);
    String file = new String(dataBytes);
    
    logger.debug("Conversion to data bytes...");
    
    //Get the dbn parameter
    dbn = utilities.getDbnParameter(file);
    
%>
<p>Details of the localization information to be uploaded to <strong><%=dbn%></strong> are shown in the table below</p>

<p>Only positions that are unoccupied will be uploaded</p>

<p><a href="./jsp/biobank/biomaterial_upload.jsp?centerid=<%=centerid%>">Upload new localization file</a></p>

<%
    boolean fileValid = false;    
    //Save the file name and check validity
    String saveFile = file.substring(file.indexOf("filename=\"") + 10);    
    if(saveFile.contains(".xls")){
        fileValid = true;
    }
    
    logger.debug("fileValid: " + fileValid);
    
    //Find the relevant positions of the actual file content
    saveFile = saveFile.substring(0, saveFile.indexOf("\n"));
    saveFile = saveFile.substring(saveFile.lastIndexOf("\\") + 1,saveFile.indexOf("\""));
    int lastIndex = contentType.lastIndexOf("=");
    String boundary = contentType.substring(lastIndex + 1,contentType.length());
                
    int pos;
    //Extract the index of file 
    pos = file.indexOf("filename=\"");
    pos = file.indexOf("\n", pos) + 1;
    pos = file.indexOf("\n", pos) + 1;
    pos = file.indexOf("\n", pos) + 1;
    int boundaryLocation = file.indexOf(boundary, pos) - 4;
    int startPos = ((file.substring(0, pos)).getBytes()).length;
    int endPos = ((file.substring(0, boundaryLocation)).getBytes()).length;
                
    //Convert byte array to input stream for POI interface
    InputStream excelIn = new ByteArrayInputStream(dataBytes, startPos, (endPos - startPos));                
    XSSFWorkbook wb = new XSSFWorkbook(excelIn);                
    int sheetNum = wb.getNumberOfSheets();
    int rows = 0;
    
    logger.debug("sheetNum: " + sheetNum);
    
    //Get the headers of interest and initialise the corresponding index array
    Vector<String> headerIndexes = utilities.getHeaderIndexes();    
    Vector<Integer> headerIndexInts = new Vector<Integer>();
    for(int n=0; n<headerIndexes.size(); n++){
        headerIndexInts.add(new Integer(-1));
    }
    
    if(!fileValid){
        htmlOut += "<strong>File is an invalid type (.xls or .xlsx only)</strong>";
    }else{
        
    //int[] headerCounts = new int[sheetNum];
    for (int k = 0; k < sheetNum; k++) {
        
        //boolean sheetValid = false;        
        boolean sheetValid = true;        
        Vector<Vector> excelOut = new Vector<Vector>();
        
        XSSFSheet sheet = wb.getSheetAt(k);        
        rows = sheet.getPhysicalNumberOfRows();
        for (int r = 0; r < rows; r++) {
            Vector<String> rowIn = new Vector<String>();
            rowIn = utilities.getRowData(sheet, r, centerid, headerIndexes, headerIndexInts);
            for(int rc = 0; rc < rowIn.size(); rc++){
                //System.out.println("rowIn.get(" + rc + "): " + rowIn.get(rc));
            }
            excelOut.add(rowIn);
            //System.out.println("====");
        }
        
        //Clean out all the empty rows        
        Vector<Vector> excelOutDisp = utilities.cleanFreezerRows(excelOut);
        
        //Make sure the sheet is valid by counting the relevant rows
        /*if(headerCounts[k] == headerIndexes.size()){
            sheetValid = true;
        }*/
 
        logger.debug("File gets as far as trying to print output (" + k + ")...");
        
        //Now get the string output for the HTML
        if(sheetValid){
            String sheetOutput = presentation.getFreezerSheetOutput(excelOutDisp, k, freezerData, capacities);
            logger.debug("Prints one sheet...");
            htmlOut += sheetOutput;
        }else{
            htmlOut += "<p>Sheet is invalid (wrong column number)</p>";
        }
    }        
}
    
}
%>

<%=htmlOut%>

<%
Vector<Vector> freezerPosToUpload = presentation.getFreezerPosToUpload();
boolean noAvailablePositions = (freezerPosToUpload.size() == 0);

if(!noAvailablePositions){
%>

<form action="./jsp/biobank/biomaterial_commit.jsp?centerid=<%=centerid%>" method="POST">
    <input type="hidden" name="dbn" value="<%=dbn%>"/>
    <input type="submit" name="add_positions" value="Add Positions"/>    
</form>

<%
}else{    
%>
<p>All positions already occupied in freezer</p>
<%
}    
%>

<p><a href="./jsp/biobank/biomaterial_upload.jsp?centerid=<%=centerid%>">Upload new localization file</a></p>

<jsp:include page="/jsp/page/page_foot.jsp" />

</td> 
      </tr> 
    </table> 
    </td> 
    
</tr>

</table>


