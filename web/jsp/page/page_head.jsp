<%@ page language="java" import="java.util.*" pageEncoding="ISO-8859-1"%>

<jsp:useBean id='user' class='user.UserBean'  scope='session'/>

<%
String path = request.getContextPath();
String basePath = request.getScheme()+"://"+request.getServerName()+":"+request.getServerPort()+path+"/";
%>

<!-- Detecting browser here (IE handles the base/href combo differently -->
<script type="text/javascript">
var ieversion = "";
if (/MSIE (\d+\.\d+);/.test(navigator.userAgent)){ //test for MSIE x.x;
 ieversion=new Number(RegExp.$1) // capture x.x portion and store as a number
}

var ffversion = "";
if (/Firefox[\/\s](\d+\.\d+)/.test(navigator.userAgent)){ //test for Firefox/x.x or Firefox x.x (ignoring remaining digits);
 ffversion=new Number(RegExp.$1) // capture x.x portion and store as a number
}

var timeoutsec = 900
var expirytag = "<meta http-equiv=\"refresh\" content=\"" + timeoutsec + "; url=/jsp/sessionexpired.jsp\">";
</script>

<!DOCTYPE html PUBLIC "-//W3C//DTD XHTML 1.0 Transitional//EN" "http://www.w3.org/TR/xhtml1/DTD/xhtml1-transitional.dtd"> 
<html xmlns="http://www.w3.org/1999/xhtml">
<head> 
<meta http-equiv="Content-Type" content="text/html; charset=iso-8859-1" /> 
<meta http-equiv="pragma" content="no-cache" />
<meta http-equiv="cache-control" content="no-cache" />
<meta http-equiv="expires" content="0" />
<base href="<%=basePath%>" />
<!-- The line below is a session expiry control - set for 15 mins (variables set in Javascript above) -->        
<script type="text/javascript">
    document.write(expirytag);    
</script>


<title>ENS@T - European Network for the Study of Adrenal Tumors</title> 
<meta name="keywords" content="ENS@T, Adrenal Tumors, cortex, medulla, adrenal glands, cancer, rare diseases, Aldosterone producing adenomas, Pheochromocytomas, paragangliomas, Non-aldosterone cortical adrenal adenomas, Adrenocortical carcinomas, COMETE, GANIMED, NISGAT" /> 
<meta name="description" content="ENS@T is the acronym for European Network for the Study of Adrenal Tumors. ENS@T aims to improve the understanding of the genetics, tumorigenesis and hypersecretion in patients with adrenal tumors and syndromes." />                 
<link REL="SHORTCUT ICON" HREF="./images/ensat.ico" />                

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

.hide{
display: none;
}

.show{
display: block;
}

-->
</style> 


<!-- THE TWO LINES BELOW RELATE TO THE PREVIOUS NESC STYLING -->
		<link href="./css/ensat.css" rel="stylesheet" type="text/css" />
        <script src="./scripts/ensat.js" type="text/javascript"></script>
        
<!--<link rel="stylesheet" href="EnsatStyle.css" type="text/css" />-->

        <script type="text/javascript">
            var inform=false;
            function mykeyhandler() {
                if (inform)
                    return true;
                if (window.event && window.event.keyCode == 8) {
                    //Cancel the backspace
                    window.event.cancelBubble = true;
                    window.event.returnValue = false;
                    return false;
                }
            }
            document.onkeydown = mykeyhandler;
      </script>

<!-- Google Analytics code -->
<script type="text/javascript">
  (function(i,s,o,g,r,a,m){i['GoogleAnalyticsObject']=r;i[r]=i[r]||function(){
  (i[r].q=i[r].q||[]).push(arguments)},i[r].l=1*new Date();a=s.createElement(o),
  m=s.getElementsByTagName(o)[0];a.async=1;a.src=g;m.parentNode.insertBefore(a,m)
  })(window,document,'script','//www.google-analytics.com/analytics.js','ga');

  ga('create', 'UA-46868950-1', 'ensat.org');
  ga('send', 'pageview');

</script>
<!-- End Google Analytics code -->


<!-- Adding JQuery headers -->
<link rel="stylesheet" href="./css/jquery-ui.css" />
<script src="./scripts/jquery-1.8.3.js"></script>
<script src="./scripts/jquery-ui.js"></script>
  
<!-- NOTE: the onSelect:function(){} bit is a patch for IE and the JQuery datepicker -->  
<script>
  $(function() {      
      $('input').filter('.datepicker').datepicker({ dateFormat: "dd-mm-yy", onSelect:function(){} });             
  });
</script>
  
<script>
  $(function() {
    $( "#dialog1" ).dialog({
      open: function(){
        var closeBtn = $('.ui-dialog-titlebar-close');
        closeBtn.append('<span class="ui-button-icon-primary ui-icon ui-icon-closethick"></span><span class="ui-button-text">close</span>');
      },
      width: 750,
      autoOpen: false,
      show: {
        effect: "blind",
        duration: 100
      },
      hide: {
        effect: "blind",
        duration: 100
      }
    });
    
    $( "#dialog2" ).dialog({
      open: function(){
        var closeBtn = $('.ui-dialog-titlebar-close');
        closeBtn.append('<span class="ui-button-icon-primary ui-icon ui-icon-closethick"></span><span class="ui-button-text">close</span>');
      },
      width: 750,
      autoOpen: false,
      show: {
        effect: "blind",
        duration: 100
      },
      hide: {
        effect: "blind",
        duration: 100
      }
    });
 
    $( ".opener1" ).click(function() {
      $( "#dialog1" ).dialog( "open" );
    });    
    
    $( ".opener2" ).click(function() {
      $( "#dialog2" ).dialog( "open" );
    });    
  });  
</script>
  
<!-- End of JQuery headers -->

<style type="text/css"> 
.scroll_checkboxes {
    background-color: #ffffff;
    border:2px solid #ccc;
    width:300px;
    height: 100px;
    overflow-y: scroll; 
}
</style>

<!-- Adding in the timeline stuff here -->
<script type="text/javascript" src="./scripts/timeline.js"></script>                
<link href="./css/timeline.css" media="all" rel="stylesheet" type="text/css" >
    <script type="text/javascript" src="https://www.google.com/jsapi"></script>
    
    
<!-- Adding in HighCharts stuff here -->
<!--<script src="http://ajax.googleapis.com/ajax/libs/jquery/1.8.2/jquery.min.js"></script>-->
<script src="https://code.highcharts.com/highcharts.js"></script>
    
</head>
    <body onload="startTime(15,0);">      
        
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
