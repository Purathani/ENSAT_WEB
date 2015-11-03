<%@ page language="java" import="java.util.*,java.sql.*,java.io.*,java.text.Format,java.text.SimpleDateFormat, org.apache.log4j.*" pageEncoding="ISO-8859-1"%>

<jsp:useBean id='connect' class='ConnectBean.ConnectBean'  scope='session'/>
<jsp:useBean id='user' class='user.UserBean'  scope='session'/>
<jsp:useBean id='pdf' class='PDFBean.PDFBean'  scope='session'/>
<jsp:useBean id='printlabel' class='update_main.PrintLabels'  scope='session'/>

<%
ServletContext context = this.getServletContext();
//Logging configuration
String log4jConfigFile = context.getInitParameter("log4j_property_file");
Logger logger = Logger.getLogger("rootLogger");
logger.setLevel(Level.DEBUG);
PropertyConfigurator.configure(log4jConfigFile);
    
String username = user.getUsername();
String country = user.getCountry();

String a4request = request.getParameter("print_labels_a4");
String a4str = "";
if(a4request == null){
    a4request = "";    
}
boolean a4labels = !a4request.equals("");

if(a4labels){
    a4str = "A4";
}else{
    a4str = "Individual";
}

String dbn = request.getParameter("dbn");
String pid = request.getParameter("pid");
String centerid = request.getParameter("centerid");
String modid = request.getParameter("modid");

boolean isWindows = false;

String filepath = printlabel.getFilepath(country, isWindows);
String filename = printlabel.getFilename(filepath, request, session, username, centerid, modid, pid);

logger.debug("('" + username + "') - Labels printed (" + a4str + ") (Ensat ID = '" + centerid + "-" + pid + "')");

String exportStorageRoot = context.getInitParameter("export_storage_root");

Connection connection = connect.getConnection();
Statement statement = connection.createStatement();

//Need to find out the multiple "Yes" items here and divide the file names appropriately

//Pull out the appropriate information
Vector<String> bioInfo = printlabel.getBioTablenames(dbn);
Vector<String> bioOutput = printlabel.getBiomaterialInfo(dbn, bioInfo, pid, centerid, modid, request, statement);
Vector<String> bioNames = printlabel.setNames(dbn);
Vector<String> aliquotNumbers = printlabel.getAliquotNumbers(request,bioOutput,bioNames);
Vector<String> bioLabels = printlabel.setLabels(dbn);

//Calculate total label numbers (yesCount multiplied by the relevant aliquot numbers)
int totalLabelNum = printlabel.getTotalLabelNum(bioOutput,aliquotNumbers);

//MULTIPLE CODE STARTS

Vector<Vector> bioMultOutput = printlabel.getBiomaterialMultipleInfo(dbn, bioInfo, pid, centerid, modid, request, statement);
Vector<Vector> aliquotMultNumbers = printlabel.getMultipleAliquotCount(bioMultOutput,request);    
int totalLabelMultNum = printlabel.getTotalMultLabelNum(bioMultOutput,aliquotMultNumbers);
    
//MULTIPLE CODE ENDS    
    
Vector<String> labelOutputStr = printlabel.getLabelOutputStr(totalLabelNum, bioOutput,aliquotNumbers, centerid, pid, modid, bioLabels, context);

//MULTIPLE CODE STARTS

Vector<String> labelOutputMultStr = printlabel.getLabelOutputMultStr(totalLabelMultNum, bioMultOutput, aliquotMultNumbers, centerid, pid, modid, bioOutput);

//Concatenate the arrays here
labelOutputStr.addAll(labelOutputMultStr);
//totalLabelNum += totalLabelMultNum;
totalLabelNum = labelOutputStr.size();

//Turn to array for PDF creation
String[] labelOutputArray = new String[totalLabelNum];
for(int i=0; i<totalLabelNum; i++){
    labelOutputArray[i] = labelOutputStr.get(i);
}

//MULTIPLE CODE ENDS

String filenameSuffix = printlabel.getFilenameSuffix(filename);
String pdfFilename = printlabel.getPdfFilename(filenameSuffix, filepath, exportStorageRoot);
pdf.createPdf(pdfFilename,labelOutputArray,a4labels);
String pageForwardName = printlabel.getPageForwardName(filepath, filenameSuffix, isWindows);


int labelCount = labelOutputStr.size();
%>

<%--labelOutputMultStr.size(): <%=labelOutputMultStr.size()%><br/>
totalLabelNum: <%=totalLabelNum%><br/>--%>

<%--<%
for(int i=0; i<labelOutputStr.size(); i++){
%>

<%=labelOutputStr.get(i)%><br/>

<%
}
%>--%>


<%--biomaterialQuery: <%=biomaterialQuery%>
totalLabelNum: <%=totalLabelNum%><br/>
yesCount: <%=yesCount%><br/>

labelCount: <%= labelCount%><br/>
<%
for(int i=0; i<labelCount; i++){
%>
<%=i%>: <%=labelOutputStr[i]%><br/>
<%
}
%>

====<br/>
bioOutput.size():  <%=bioOutput.size()%><br/>
<%
for(int i=0; i<bioOutput.size(); i++){
%>
<%=i%>: <%=bioOutput.get(i)%><br/>
<%
}
%>

====<br/>
bioLabels.size():  <%=bioLabels.size()%><br/>
<%
for(int i=0; i<bioLabels.size(); i++){
%>
<%=i%>: <%=bioLabels.get(i)%><br/>
<%
}
%>

====<br/>
aliquotNumbers.size():  <%=aliquotNumbers.size()%><br/>
<%
for(int i=0; i<aliquotNumbers.size(); i++){
%>
<%=i%>: <%=aliquotNumbers.get(i)%><br/>
<%
}
%>

<%--<p>/<%=filepath%>/<%=filenameSuffix%></p>--%>

<%--<p>File successfully exported to: <a target="_blank" href="/<%=filepath%>/<%=filenameSuffix%>"><%=filenameSuffix%></a></p>--%>

<%--<%=pageForwardName%>--%>
<jsp:forward page="<%=pageForwardName%>"/>
<%--<p><%=labelOutputMultStr.size()%></p>
<p>frozenCount: <%=frozenCount%></p>
<p>paraffinCount: <%=paraffinCount%></p>
<p>dnaCount: <%=dnaCount%></p>--%>