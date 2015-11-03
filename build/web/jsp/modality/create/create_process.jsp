<%@ page language="java" import="java.util.*,java.sql.*" pageEncoding="ISO-8859-1"%>

<jsp:useBean id='connect' class='ConnectBean.ConnectBean'  scope='session'/>
<jsp:useBean id='createsub' class='create_main.CreateSub'  scope='session'/>
<jsp:useBean id='mail' class='MailBean.MailBean'  scope='session'/>

<%
//ServletContext context = this.getServletContext();

Connection connection = connect.getConnection();
Connection paramConn = connect.getParamConnection();

String dbn = request.getParameter("dbn");
String dbid = request.getParameter("dbid");
String pid = request.getParameter("pid");
pid = createsub.standardisePid(pid);
String centerid = request.getParameter("centerid");
String modality = request.getParameter("modality");

String[] subTablenames = null;
Vector<Vector> parameters = null;

//Retrieve data parameter information
subTablenames = new String[1];
subTablenames[0] = createsub.getSubTablename(modality,dbn);

//Retrieve data parameter information
if(!modality.equals("metabolomics")){
parameters = createsub.getParameters(subTablenames,request,paramConn);
}else{    
    String modid = request.getParameter("modid");
parameters = createsub.setupParameters(modality, request,centerid,pid,modid);  

    //Add in module here to inform PI that their patient has received a metabolomics form update
    String piEmail = createsub.getParameterValue("investigator_email", "Identification", connection, centerid, pid);    
    String mailId = centerid + "-" + pid;
    mail.sendMetabolomicsEmail(request, response, mailId, piEmail);    


}

int nextId = 0;
String tableIdName = createsub.getSubTableIdName(modality, dbn);
nextId = createsub.getNextId(subTablenames[0],tableIdName,pid, centerid, connection);

//Now execute update
//createsub.executeParameterUpdate(nextId, subTablenames[0], tableIdName, pid, centerid, parameters, statement, request);
createsub.executeParameterUpdate(nextId, subTablenames[0], tableIdName, pid, centerid, parameters, connection, request);
if(subTablenames[0].equals("ACC_Chemotherapy")){    
    //createsub.executeParameterUpdate(nextId, "ACC_Chemotherapy_Regimen", "acc_chemotherapy_regimen_id",pid, centerid, parameters, statement, request);    
    createsub.executeParameterUpdate(nextId, "ACC_Chemotherapy_Regimen", "acc_chemotherapy_regimen_id",pid, centerid, parameters, connection, request);    
}else if(subTablenames[0].equals("ACC_FollowUp")){
       //createsub.executeParameterUpdate(nextId, "ACC_FollowUp_Organs", "acc_followup_organs_id", pid, centerid, parameters, statement, request);    
    createsub.executeParameterUpdate(nextId, "ACC_FollowUp_Organs", "acc_followup_organs_id", pid, centerid, parameters, connection, request);    
}else if(subTablenames[0].equals("ACC_Radiofrequency")){    
    //createsub.executeParameterUpdate(nextId, "ACC_Radiofrequency_Loc", "acc_radiofrequency_loc_id", pid, centerid, parameters, statement, request);    
    createsub.executeParameterUpdate(nextId, "ACC_Radiofrequency_Loc", "acc_radiofrequency_loc_id", pid, centerid, parameters, connection, request);    
}else if(subTablenames[0].equals("ACC_Radiotherapy")){    
    //createsub.executeParameterUpdate(nextId, "ACC_Radiotherapy_Loc", "acc_radiotherapy_loc_id", pid, centerid, parameters, statement, request);    
    createsub.executeParameterUpdate(nextId, "ACC_Radiotherapy_Loc", "acc_radiotherapy_loc_id", pid, centerid, parameters, connection, request);    
}else if(subTablenames[0].equals("ACC_Surgery")){    
    //createsub.executeParameterUpdate(nextId, "ACC_Surgery_Extended", "acc_surgery_extended_id", pid, centerid, parameters, statement, request);    
    createsub.executeParameterUpdate(nextId, "ACC_Surgery_Extended", "acc_surgery_extended_id", pid, centerid, parameters, connection, request);
    //createsub.executeParameterUpdate(nextId, "ACC_Surgery_First", "acc_surgery_first_id", pid, centerid, parameters, statement, request);    
    createsub.executeParameterUpdate(nextId, "ACC_Surgery_First", "acc_surgery_first_id", pid, centerid, parameters, connection, request);
}else if(subTablenames[0].equals("ACC_Biomaterial")){    
    //createsub.executeParameterUpdate(nextId, "ACC_Biomaterial_Normal_Tissue", "acc_biomaterial_normal_tissue_id", pid, centerid, parameters, statement, request);        
    createsub.executeParameterUpdate(nextId, "ACC_Biomaterial_Normal_Tissue", "acc_biomaterial_normal_tissue_id", pid, centerid, parameters, connection, request);
}else if(subTablenames[0].equals("Pheo_Biomaterial")){    
    //createsub.executeParameterUpdate(nextId, "Pheo_Biomaterial_Normal_Tissue", "pheo_biomaterial_normal_tissue_id", pid, centerid, parameters, statement, request);        
    createsub.executeParameterUpdate(nextId, "Pheo_Biomaterial_Normal_Tissue", "pheo_biomaterial_normal_tissue_id", pid, centerid, parameters, connection, request);
}else if(subTablenames[0].equals("NAPACA_Biomaterial")){    
    //createsub.executeParameterUpdate(nextId, "NAPACA_Biomaterial_Normal_Tissue", "napaca_biomaterial_normal_tissue_id", pid, centerid, parameters, statement, request);        
    createsub.executeParameterUpdate(nextId, "NAPACA_Biomaterial_Normal_Tissue", "napaca_biomaterial_normal_tissue_id", pid, centerid, parameters, connection, request);
}else if(subTablenames[0].equals("APA_Biomaterial")){    
    //createsub.executeParameterUpdate(nextId, "APA_Biomaterial_Normal_Tissue", "apa_biomaterial_normal_tissue_id", pid, centerid, parameters, statement, request);        
    createsub.executeParameterUpdate(nextId, "APA_Biomaterial_Normal_Tissue", "apa_biomaterial_normal_tissue_id", pid, centerid, parameters, connection, request);
}else if(subTablenames[0].equals("Pheo_TumorDetails")){    
    //createsub.executeParameterUpdate(nextId, "Pheo_MetastasesLocation", "pheo_metastases_loc_id", pid, centerid, parameters, statement, request);        
    createsub.executeParameterUpdate(nextId, "Pheo_MetastasesLocation", "pheo_metastases_loc_id", pid, centerid, parameters, connection, request);
}else if(subTablenames[0].equals("Pheo_ImagingTests")){        
    createsub.executeParameterUpdate(nextId, "Pheo_ImagingTests_CTLoc", "pheo_imaging_ctloc_id", pid, centerid, parameters, connection, request);    
    createsub.executeParameterUpdate(nextId, "Pheo_ImagingTests_NMRLoc", "pheo_imaging_nmrloc_id", pid, centerid, parameters, connection, request);
}else if(subTablenames[0].equals("Pheo_Surgery")){        
    createsub.executeParameterUpdate(nextId, "Pheo_Surgery_PreOp", "pheo_surgery_preop_id", pid, centerid, parameters, connection, request);    
    createsub.executeParameterUpdate(nextId, "Pheo_Surgery_IntraOp", "pheo_surgery_intraop_id", pid, centerid, parameters, connection, request);
    //createsub.executeParameterUpdate(nextId, "Pheo_Surgery_Procedure", "pheo_surgery_procedure_id", pid, centerid, parameters, connection, request);
}
%>

<jsp:forward page="/jsp/modality/create/create_result.jsp">
<jsp:param name="dbid" value="<%=dbid%>"/>
<jsp:param name="dbn" value="<%=dbn%>"/>
<jsp:param name="nextid" value="<%=nextId%>"/>
</jsp:forward>
