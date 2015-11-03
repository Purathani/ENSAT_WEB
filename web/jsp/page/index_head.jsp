<%@ page language="java" import="java.util.*" pageEncoding="ISO-8859-1"%>

<%
String path = request.getContextPath();
String basePath = request.getScheme()+"://"+request.getServerName()+":"+request.getServerPort()+path+"/";
%>

<!DOCTYPE html PUBLIC "-//W3C//DTD XHTML 1.0 Transitional//EN" "http://www.w3.org/TR/xhtml1/DTD/xhtml1-transitional.dtd"> 
<html xmlns="http://www.w3.org/1999/xhtml">
<head> 
<meta http-equiv="Content-Type" content="text/html; charset=iso-8859-1" /> 
<title>ENS@T - European Network for the Study of Adrenal Tumors</title> 
<base href="<%=basePath%>" />
<meta name="keywords" content="ENS@T, Adrenal Tumors, cortex, medulla, adrenal glands, cancer, rare diseases, Aldosterone producing adenomas, Pheochromocytomas, paragangliomas, Non-aldosterone cortical adrenal adenomas, Adrenocortical carcinomas, COMETE, GANIMED, NISGAT" /> 
<meta name="description" content="ENS@T is the acronym for European Network for the Study of Adrenal Tumors. ENS@T aims to improve the understanding of the genetics, tumorigenesis and hypersecretion in patients with adrenal tumors and syndromes." />                 
<link REL="SHORTCUT ICON" HREF="/Ensat/images/ensat.ico" />                


        <script type="text/JavaScript"> 
<!--
function MM_swapImgRestore() { //v3.0
  var i,x,a=document.MM_sr; for(i=0;a&&i<a.length&&(x=a[i])&&x.oSrc;i++) x.src=x.oSrc;
}
 
function MM_preloadImages() { //v3.0
  var d=document; if(d.images){ if(!d.MM_p) d.MM_p=new Array();
    var i,j=d.MM_p.length,a=MM_preloadImages.arguments; for(i=0; i<a.length; i++)
    if (a[i].indexOf("#")!=0){ d.MM_p[j]=new Image; d.MM_p[j++].src=a[i];}}
}
 
function MM_findObj(n, d) { //v4.01
  var p,i,x;  if(!d) d=document; if((p=n.indexOf("?"))>0&&parent.frames.length) {
    d=parent.frames[n.substring(p+1)].document; n=n.substring(0,p);}
  if(!(x=d[n])&&d.all) x=d.all[n]; for (i=0;!x&&i<d.forms.length;i++) x=d.forms[i][n];
  for(i=0;!x&&d.layers&&i<d.layers.length;i++) x=MM_findObj(n,d.layers[i].document);
  if(!x && d.getElementById) x=d.getElementById(n); return x;
}
 
function MM_swapImage() { //v3.0
  var i,j=0,x,a=MM_swapImage.arguments; document.MM_sr=new Array; for(i=0;i<(a.length-2);i+=3)
   if ((x=MM_findObj(a[i]))!=null){document.MM_sr[j++]=x; if(!x.oSrc) x.oSrc=x.src; x.src=a[i+2];}
}
//-->
</script> 
<style type="text/css"> 
<!--
body {
	margin-left: 0px;
	margin-top: 0px;
	margin-right: 0px;
	margin-bottom: 0px;
}
.style1 {
	color: #535473;
	font-family: Arial, Helvetica, sans-serif;
	font-size: small;
	text-decoration: none;
}
.style2 {
	font-size: x-small;
	text-decoration: none;
	color: #535473;
}
a:hover {
	/*color: #FFFFFF;*/
        color: #AAAAAA;
}
a:active {
	/*color: #FFFFFF;*/
        color: #AAAAAA;
}
.style4 {
	font-family: Arial, Helvetica, sans-serif;
	color: #535473;
	font-size: small;
}
-->
</style> 


<!-- THE TWO LINES BELOW RELATE TO THE PREVIOUS NESC STYLING -->
		<link href="./css/ensat.css" rel="stylesheet" type="text/css" />
        
<!--todo: something about this-->
                <link rel="stylesheet" href="EnsatStyle.css" type="text/css" />        


</head>
	<body onload="MM_preloadImages('/images/TabsMembers2.gif','/images/TabsContacts2.gif')">

<table width="100%" border="0" cellspacing="0" cellpadding="0" height="100%"> 
  <tr> 
    <td width="175" height="100"><div align="center"><img src="./images/ensat_registry_logo.jpg" width="186" height="114" /></div></td> 
    <td width="100%" height="70"  valign="bottom" background="./images/homebk.jpg"><table width="100%" border="0" cellspacing="0" cellpadding="0"> 
      <tr> 
        <th align="left" valign="bottom" scope="col"><table width="100%" border="0" cellspacing="0" cellpadding="0"> 
          <tr> 
            <th align="left" valign="bottom" scope="col"><!-- #BeginEditable "Title" --><img src="./images/TitleEnsatReg.gif" width="564" height="38" /><!-- #EndEditable --></th> 
            </tr> 
        </table></th> 
      </tr> 
    </table></td> 
  </tr> 

              <tr> 
