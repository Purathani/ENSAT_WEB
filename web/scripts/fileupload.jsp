<%@ page language="java" import="java.util.*,java.sql.*,java.text.Format,java.text.SimpleDateFormat" pageEncoding="ISO-8859-1"%>

<%
String dbid = request.getParameter("dbid");
String dbn = request.getParameter("dbn");
String pid = request.getParameter("pid");
String centerid = request.getParameter("centerid");
String modality = request.getParameter("modality");
String modid = request.getParameter("modid");
%>

    <fieldset>
        <legend>Upload Metabolomics PDF File</legend>
        <form action="/uploadservlet" method="post" enctype="multipart/form-data">
            
            <input type="hidden" name="dbid" value="<%=dbid%>"/>
            <input type="hidden" name="dbn" value="<%=dbn%>"/>
            <input type="hidden" name="pid" value="<%=pid%>"/>
            <input type="hidden" name="centerid" value="<%=centerid%>"/>
            <input type="hidden" name="modality" value="<%=modality%>"/>
            <input type="hidden" name="modid" value="<%=modid%>"/>
            
            <input id="filename" type="file" name="filename" size="50"/><br/>
            
            <table>
                <tr>
                    <td>
                        Comment:
                    </td>
                    <td>
                        <input type="text" name="comment" size="30" onfocus="inform=true;" onblur="inform=false;"/>
                    </td>
                </tr>
                <tr>
                    <td>
                        Date:
                    </td>
                    <td>
                        <input id="metabolomics_date_1" class="datepicker" type="text" name="metabolomics_date" onfocus="inform=true;" onblur="inform=false;"/>
                    </td>
                </tr>
            </table>
            <input type="submit" value="Upload"/>
            
        </form>
    </fieldset>
