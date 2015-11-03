<%@ page language="java" 
	import="java.util.*,java.text.Format,java.text.SimpleDateFormat"
	pageEncoding="ISO-8859-1"%>

<div id="page-foot">
	<br />
	<hr />

	<div class="page-foot-left">
		<%
			Format formatter = new SimpleDateFormat(
					"EEEE, dd MMM yyyy HH:mm:ss Z");
			Date date = new Date(session.getLastAccessedTime());
			String s = formatter.format(date);
		%>
		<%=s%>
	</div>

	<div class="page-foot-right">
		<span class="page-foot-text">Powered By: </span>
		<a href="http://www.eresearch.unimelb.edu.au"> <img
				src="./images/unimelb_logo_small.bmp" /> </a>
	</div>

<!-- Start of StatCounter tracking code
<script type="text/javascript">
var sc_project=6165214;
var sc_invisible=1;
var sc_security="2a8f2b8a";
</script>

<script type="text/javascript"
src="http://www.statcounter.com/counter/counter.js"></script><noscript><div
class="statcounter"><a title="joomla stats"
href="http://www.statcounter.com/joomla/"
target="_blank"><img class="statcounter"
src="http://c.statcounter.com/6165214/0/2a8f2b8a/1/"
alt="joomla stats" ></a></div></noscript>-->
<!-- End of StatCounter Code -->


</div>

</body>
</html>

