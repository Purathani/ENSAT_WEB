
menu_status = new Array();

function showHide(theid,thevalue){
    
    if (document.getElementById) {
        var switch_id = document.getElementById(theid);
                
        var subMenuTrigger = this.getSubMenuTrigger(theid);
        
        //This clause opens the sub-menus if there is any input at all
        if(subMenuTrigger==="ALL"){            
            if(thevalue!=='') {
                
                if((theid==="myDiv_gluco_serum_cortisol_options"
                || theid==="myDiv_gluco_plasma_acth_options"
                || theid==="myDiv_gluco_urinary_free_cortisol_options"
                || theid==="myDiv_other_steroid_17hydroxyprogesterone_options"
                || theid==="myDiv_other_steroid_serum_dheas_options") && thevalue==="Not Done"){
                    switch_id.className = 'hide';
                    menu_status[theid] = 'hide';
                }else{
                    switch_id.className = 'show';
                    menu_status[theid] = 'show';
                }
            }else{
                switch_id.className = 'hide';
                menu_status[theid] = 'hide';
            }
        }else if(subMenuTrigger==="MULTIPLE"){

            //This one toggles between two alternative sub-menus depending on the input
            //Input varies according to parameter so need to encapsulate that here (returned value shows value 1 and hides 2, vice versa does the opposite)
            
            //Show the parent parameter first
            var switch_id_show = document.getElementById(theid);
            switch_id_show.className = 'show';
            menu_status[theidShow] = 'show';
            
            //Do the mapping of which parameters beneath to show            
            var paramSubParams = this.getParamSubParams(theid);
            var paramsToShow = this.mapOptionsToSubParams(thevalue,theid);
             
            //Show all the parameters in the array that is mapped
            var m = 0;
            while(m<paramsToShow.length){
                var theidShow = paramsToShow[m];
                //window.alert("theidShow: " + theidShow);
                var switch_id_show_sub = document.getElementById(theidShow);
                switch_id_show_sub.className = 'show';
                menu_status[theidShow] = 'show';            
                m++;
            }
            
            //Now run through all the sub-params and if they aren't in paramsToShow then hide
            var n = 0;
            while(n<paramSubParams.length){
                var theidHide = paramSubParams[n];
                
                var p = 0;
                var showParam = false;
                while(p<paramsToShow.length && !showParam){
                    if(paramsToShow[p]===theidHide){
                        showParam = true;
                    }else{
                        p++;
                    }
                }
                
                if(!showParam){
                    var switch_id_hide = document.getElementById(theidHide);
                    switch_id_hide.className = 'hide';
                    menu_status[theidHide] = 'hide';
                }
                n++;
            }
            
        }else if(subMenuTrigger==="Biopsy"){
            
            //This trigger fires if it depends on the input being "Biopsy"                        
            if(menu_status[theid] != 'show' && thevalue==='Biopsy') {
                switch_id.className = 'show';
                menu_status[theid] = 'show';
            }else{
                switch_id.className = 'hide';
                menu_status[theid] = 'hide';
            }            
        }else if(subMenuTrigger==="Imaging"){
            
            //This trigger fires if it depends on the input being "Biopsy"                        
            if(menu_status[theid] != 'show' && thevalue!=='Not Done') {
                switch_id.className = 'show';
                menu_status[theid] = 'show';
            }else{
                switch_id.className = 'hide';
                menu_status[theid] = 'hide';
            }            
        }else if(subMenuTrigger==="CT"){
            
            //This trigger fires if it depends on the input being "Biopsy"                        
            if(menu_status[theid] != 'show' && thevalue==='CT') {
                switch_id.className = 'show';
                menu_status[theid] = 'show';
            }else{
                switch_id.className = 'hide';
                menu_status[theid] = 'hide';
            }            
        }else if(subMenuTrigger==="Alive with disease"){
            
            //This trigger fires if it depends on the input being "Biopsy"                        
            if(menu_status[theid] != 'show' && thevalue==='Alive with disease') {
                switch_id.className = 'show';
                menu_status[theid] = 'show';
            }else{
                switch_id.className = 'hide';
                menu_status[theid] = 'hide';
            }            
        }else if(subMenuTrigger==="midnight_cortisol"){
            
            //This trigger fires if it depends on the input being "Biopsy"                        
            if(menu_status[theid] != 'show' && thevalue!=='Not Done') {
                switch_id.className = 'show';
                menu_status[theid] = 'show';
            }else{
                switch_id.className = 'hide';
                menu_status[theid] = 'hide';
            }            
        }else if(subMenuTrigger==="Suppressed"){
                                    
            if(thevalue!=='Not Done') {
                switch_id.className = 'show';
                menu_status[theid] = 'show';
            }else{
                switch_id.className = 'hide';
                menu_status[theid] = 'hide';
            }            
        }else if(subMenuTrigger==="Yes"){
            
            //This trigger fires if it depends on the input being "Yes"
            //But has the caveat that some will depend on it inversely (if alive="No", show the death parameters)
            var inverseYes = false;
            if((theid==="myDiv_followup_alive_options")
                || (theid==="myDiv_mitotane_ongoing_options")
                || (theid==="myDiv_phpgl_free_options")){
                inverseYes = true;
            }
            
            if(!inverseYes){
                
                //special case for the alive options in Pheo_FollowUp
                if(theid==="myDiv_alive_options"){                    
                    var theid2 = "myDiv_alive_options_2";
                    var switch_id2 = document.getElementById(theid2);
                    //window.alert("theid2: " + theid2 + ", theid: " + theid + ", thevalue: " + thevalue);
                    if(thevalue==='Yes') {
                        //window.alert('Goes into the Yes clause...');
                        switch_id.className = 'show';
                        menu_status[theid] = 'show';
                        switch_id2.className = 'hide';
                        menu_status[theid2] = 'hide';
                        //window.alert('Something going on here? #1');
                    }else{
                        //window.alert('Goes into the No clause...');
                        switch_id.className = 'hide';
                        menu_status[theid] = 'hide';
                        switch_id2.className = 'show';
                        menu_status[theid2] = 'show';
                        //window.alert('Something going on here? #2');
                    }
                    //window.alert('Something going on here? #3');
                }else{                    
                    if(theid!=="myDiv_alive_options_2"){                    
                        if(menu_status[theid] != 'show' && thevalue==='Yes') {
                            switch_id.className = 'show';
                            menu_status[theid] = 'show';
                        }else{
                            switch_id.className = 'hide';
                            menu_status[theid] = 'hide';
                        }
                    }
                }
                
                //window.alert('Something going on here? #4: ' + theid);
            }else{
                if(menu_status[theid] != 'show' && thevalue==='No') {
                    switch_id.className = 'show';
                    menu_status[theid] = 'show';
                }else{
                    switch_id.className = 'hide';
                    menu_status[theid] = 'hide';
                }                
            }
            //window.alert('Something going on here? #5');
        }
    }
}

/*function getParamOptions(theid){
    var paramOptions = new Array();
    if(theid==="myDiv_alive_options"){
        paramOptions.push("Yes");
        paramOptions.push("No");
    }else if(theid==="myDiv_surgery_type_options"){
        paramOptions.push("First");
        paramOptions.push("Repeated");
    }else if(theid==="myDiv_tumor_sites_options"){
        paramOptions.push("Right Adrenal");
        paramOptions.push("Left Adrenal");
        paramOptions.push("Both Adrenals");
    }
    return paramOptions;            
}*/

function getParamSubParams(theid){
    var paramSubParams = new Array();
    /*if(theid==="myDiv_alive_options"){
        paramSubParams.push("phpgl_free" + "_mult");
        paramSubParams.push("disease_state" + "_mult");
        paramSubParams.push("date_of_death" + "_mult");
        paramSubParams.push("cause_of_death" + "_mult");
    }else */if(theid==="myDiv_surgery_type_options"){
        paramSubParams.push("surgery_first" + "_mult");
        paramSubParams.push("surgery_extended" + "_mult");
    }else if(theid==="myDiv_tumor_sites_options"){
        paramSubParams.push("right_adrenal_max_tumor" + "_mult");
        paramSubParams.push("left_adrenal_max_tumor" + "_mult");        
    }else if(theid==="myDiv_tumor_sites_imaging_options"){
        paramSubParams.push("max_tumor_by_ct_right" + "_mult");
        paramSubParams.push("max_tumor_by_mr_right" + "_mult");        
        paramSubParams.push("max_tumor_by_ct_left" + "_mult");
        paramSubParams.push("max_tumor_by_mr_left" + "_mult");
    }else if(theid==="myDiv_mitotane_indication_options"){
        paramSubParams.push("mitotane_best_objective" + "_mult");
        paramSubParams.push("mitotane_best_objective_adj" + "_mult");        
    }
    return paramSubParams;
}

function mapOptionsToSubParams(thevalue,theid){
    var paramsToShow = new Array();
    /*if(theid==="myDiv_alive_options"){
        if(thevalue==="Yes"){
            paramsToShow.push("phpgl_free" + "_mult");
        //paramsToShow.push("disease_state");
        }else{
            paramsToShow.push("date_of_death" + "_mult");
            paramsToShow.push("cause_of_death" + "_mult");
        }
    }else */if(theid==="myDiv_surgery_type_options"){
        if(thevalue==="First"){
            paramsToShow.push("surgery_first" + "_mult");
        }else{
            paramsToShow.push("surgery_extended" + "_mult");
        }
    }else if(theid==="myDiv_tumor_sites_options"){
        if(thevalue==="Right Adrenal"){
            paramsToShow.push("right_adrenal_max_tumor" + "_mult");
        }else if(thevalue==="Left Adrenal"){
            paramsToShow.push("left_adrenal_max_tumor" + "_mult");
        }else{
            paramsToShow.push("right_adrenal_max_tumor" + "_mult");
            paramsToShow.push("left_adrenal_max_tumor" + "_mult");
        }
    }else if(theid==="myDiv_tumor_sites_imaging_options"){
        if(thevalue==="Right adrenal"){
            paramsToShow.push("max_tumor_by_ct_right" + "_mult");
            paramsToShow.push("max_tumor_by_mr_right" + "_mult");
        }else if(thevalue==="Left adrenal"){
            paramsToShow.push("max_tumor_by_ct_left" + "_mult");
            paramsToShow.push("max_tumor_by_mr_left" + "_mult");
        }else if(thevalue==="Both adrenals"){
            paramsToShow.push("max_tumor_by_ct_right" + "_mult");
            paramsToShow.push("max_tumor_by_mr_right" + "_mult");
            paramsToShow.push("max_tumor_by_ct_left" + "_mult");
            paramsToShow.push("max_tumor_by_mr_left" + "_mult");
        }
    }else if(theid==="myDiv_mitotane_indication_options"){
        if(thevalue==="Adjuvant"){
            paramsToShow.push("mitotane_best_objective_adj" + "_mult");
        }else{
            paramsToShow.push("mitotane_best_objective" + "_mult");            
        }
    }
    return paramsToShow;
}


function getSubMenuTrigger(theid){
    if(theid==="myDiv_gluco_plasma_acth_options"
        || theid==="myDiv_gluco_urinary_free_cortisol_options"
        || theid==="myDiv_other_steroid_17hydroxyprogesterone_options"
        || theid==="myDiv_other_steroid_serum_dheas_options"        
        || theid==="myDiv_ct_tumor_density_options" 
        || theid==="myDiv_adrenal_sparing_options"){
        return "ALL";
    }else if(/*theid==="myDiv_alive_options"                
        || */theid==="myDiv_surgery_type_options"
        || theid==="myDiv_tumor_sites_options"
        || theid==="myDiv_tumor_sites_imaging_options"
        || theid==="myDiv_mitotane_indication_options"){
        return "MULTIPLE";
    }else if(theid==="myDiv_pathology_derived_from_options"){
        return "Biopsy";
    }else if(theid==="myDiv_imaging_options"){
        return "CT";
    }else if(theid==="myDiv_patient_status_options"){
        return "Alive with disease";
    }else if(theid==="myDiv_gluco_serum_cortisol_options"){
        return "Suppressed";
    }else if(theid==="myDiv_midnight_salivary_cortisol_options" 
        || theid==="myDiv_midnight_serum_cortisol_options" ){
        return "midnight_cortisol";
    }else if(theid==="myDiv_ct_options"
        || theid==="myDiv_nmr_options"
        || theid==="myDiv_mibg_options"
        || theid==="myDiv_octreoscan_options"
        || theid==="myDiv_fdg_pet_options"
        || theid==="myDiv_da_pet_options"        
        || theid==="myDiv_other_imaging_options"){
        return "Imaging";
    }else{
        return "Yes";
    }        
}

/*function getSubMenuMultipleTrigger(theid){
    if(theid==="myDiv_alive_options"){
        return "Yes";
    }else if(theid==="myDiv_surgery_type_options"){
        return "First";
    }else if(theid==="myDiv_tumor_sites_options"){
        return "Right Adrenal";
    }else{
        return "";
    }
}*/

search_menu_status = new Array();

function showHideSearch(theid){
    
    //Remove the leading "add_button_" string
    theid = theid.substring(11,theid.length);
    
    if (document.getElementById) {        
        var open_number = search_menu_status[theid];
        if(open_number == null){
            open_number = 0;
        }
        if(open_number < 4){
            open_number = open_number+1;
        }
        var option_id = theid + "_option_" + open_number;            
        var open_id_option = document.getElementById(option_id);        
        open_id_option.className = 'show';        
        search_menu_status[theid] = open_number;        
    }
}

function study_selection_withphase(studyname, phase){
    
    /*window.alert("in study_selection_withphase()...");
    var assocStudy_id = document.getElementById('associated_study_menus');
    if(assocStudy_id.className!== null && studyname===""){
        assocStudy_id.innerHTML="";
    }*/    
    
    //If the study is any of the following, then leave it until the phase/visit is selected
    if(studyname!=="PMT" && studyname!=="FIRST-MAPPP" && studyname!=="German Cushing Registry" && studyname!=="German Conn Registry"){        
        return;
    }else{
    
        //Compile the parameter array based on the study selected
        var paramArray = new Array();
    
        if(studyname==="German Conn Registry"){
            if(phase==="Visit 1" || phase==="Visit 2"){
                paramArray.push(new Array("leukocyte_dna",1));
            }
            paramArray.push(new Array("plasma",1));
            paramArray.push(new Array("serum",1));        
            paramArray.push(new Array("24h_urine",1));        
        }else if(studyname==="FIRST-MAPPP"){               
            if(phase==="Baseline"){
                paramArray.push(new Array("tumor_tissue_frozen",2));
                paramArray.push(new Array("tumor_tissue_paraffin",1));
            }
            paramArray.push(new Array("plasma",2));
            paramArray.push(new Array("heparin_plasma",4));  //ORIGINAL      
            //paramArray.push(new Array("heparin_plasma",1));
            //paramArray.push(new Array("serum",1));
            //paramArray.push(new Array("24h_urine",1));
            paramArray.push(new Array("24h_urine",4)); //ORIGINAL
        }else if(studyname==="PMT"){
            if(phase==="Phase 4 - post-op"){
                paramArray.push(new Array("leukocyte_dna",1));
            }
            if(phase==="Phase 4 - post-op" || phase==="Phase 2" || phase==="Phase 3" || phase==="Phase 4 - follow-up"){
                paramArray.push(new Array("plasma",1));
            }
            if(phase==="Phase 4 - follow-up" || phase==="Phase 1"){
                paramArray.push(new Array("serum",1));        
            }            
            paramArray.push(new Array("heparin_plasma",1));
            paramArray.push(new Array("24h_urine",1));        
        }else{
            paramArray.push(new Array("",1));            
        }
            
        //Close the 24h_urine by default first
        this.showHide("myDiv_24h_urine_options","");
    
        //Now loop through the parameters and set the options to false-true-false for each named in the array
        var elemCount = 0;    
        var totalElems = document.f1.elements.length;
    
        //Reset the aliquot/freezer options first
        while(elemCount < totalElems){
            var thisAliquotParamName = document.f1.elements[elemCount].name;
            var aliquotName = "aliquot_" + thisAliquotParamName + "_options";
            
            //for(var freezerCount = 1; freezerCount < 10; freezerCount++){
                //var freezerName = thisAliquotParamName + "_freezer_info_" + freezerCount;
                var freezerName = thisAliquotParamName + "_freezer_info";
                //window.alert("freezerName: " + freezerName);
                var freezerId = document.getElementById(freezerName);
                //window.alert("freezerId: " + freezerId);
                if(freezerId!==null){
                    freezerId.innerHTML="";
                    /*if(freezerId.className==='show'){
                        this.showHide(freezerName,"");
                    }*/
                }
        
            var aliquotId = document.getElementById(aliquotName);
            
            if(aliquotId!==null){
                if(aliquotId.className==='show'){
                    this.showHide(aliquotName,"");
                }        
            }
            elemCount = elemCount+1;
        }
        elemCount = 0;    

        //Add the new freezer, aliquot and validation element number count
        totalElems = totalElems + (paramArray.length * 8);

        while(elemCount < totalElems){        
            var thisParamName = document.f1.elements[elemCount];
        
            //Avoid the text fields (alternative is "SELECT" for the menus - "INPUT" is for the text-fields)
            //window.alert("nodeName: " + thisParamName.nodeName);
        
            if(thisParamName.nodeName!=="INPUT"){
        
                //Except elemCount (3), which is the associated_study field itself (and elemCount (4) which is the phase/visit)
            
                if(elemCount != 1 && elemCount != 2){
                //if(elemCount != 1){
                
                    //THIS EXTRA BIT SOLVES THE ANNOYING ALIQUOT RESET BUG THAT I HAD
                    var aliquotStr = /aliquot/mi;
                    if(aliquotStr.test(thisParamName.name)
                        || thisParamName.name==="biomaterial_date_month"){
                    
                    }else{
                        thisParamName.options[0].selected = false; //[Select...]
                        thisParamName.options[1].selected = false; //"Yes"
                        thisParamName.options[2].selected = false; //"No"
                    }

                    var elemFound = false;
                    var paramCount = 0;
                    while(paramCount < paramArray.length && !elemFound){                   
                        var paramNameObj = paramArray[paramCount];                
                        var paramName = paramNameObj[0];
                        //window.alert("paramName: " + paramName);
                        //window.alert("paramName: " + paramName);
                        //window.alert("thisParamName.name: " + thisParamName.name);                            
                        if(paramName===thisParamName.name){
                            thisParamName.options[1].selected = true; //"Yes"
                            //Show the associated aliquot/freezer with the parameter name
                            var aliquotParamName = "aliquot_" + paramName;  
                            //window.alert("aliquotParamName: " + aliquotParamName);
                            var aliquotParamElems = document.getElementsByName(aliquotParamName);
                            var aliquotParamElem = aliquotParamElems[0];
                            this.showHide(aliquotParamName + "_options","Yes");
                            
                            //Set the number in the aliquot menu
                            var paramAliquotNumber = paramNameObj[1];                            
                            var aliqFound = false;
                            var aliqCount = 0;
                            while(aliqCount < 10 && !aliqFound){
                                if(aliquotParamElem.options[aliqCount].value==paramAliquotNumber){                                    
                                    aliqFound = true;                                    
                                }else{
                                    aliqCount++;
                                }
                            }
                            if(aliqFound){
                                aliquotParamElem.options[aliqCount].selected = true;
                            }
                            this.showFreezerInfo(paramName,paramAliquotNumber,"Yes");
                        
                            if(paramName==="24h_urine"){
                                //window.alert("within 24h_urine");
                                this.showHide("myDiv_" + paramName + "_options","Yes");
                                //this.showFreezerInfo(paramName,paramAliquotNumber,"Yes");
                                elemCount = elemCount + 1;
                            }
                            elemFound = true;
                            //This skips beyond the newly inputted aliquot selection menu as an option...
                            elemCount = elemCount + 1;
                            paramCount = paramCount + 1;
                        }else{                                           
                            if(thisParamName.name!=="associated_study" && !aliquotStr.test(thisParamName.name)
                                && thisParamName.name!=="normal_tissue_options" && thisParamName.name!=="normal_tissue_paraffin_options"
                                && thisParamName.name!=="normal_tissue_dna_options"
                                ){
                                thisParamName.options[2].selected = true; //"No" (parameter is not part of study)
                            }
                            paramCount = paramCount + 1;
                        }
                    }
                }
            }
            elemCount = elemCount + 1;
        }
        return;
    }
}

function study_selection(studyname, ensatsection, server_root){
    
    //window.alert("in study_selection()...");
    var assocStudy_id = document.getElementById('associated_study_menus');
    if(assocStudy_id.className!== null && studyname===""){
        assocStudy_id.innerHTML="";
    }    
    
    //If the study is any of the following, then leave it until the phase/visit is selected
    if(studyname==="PMT" || studyname==="FIRST-MAPPP" || studyname==="German Cushing Registry" || studyname==="German Conn Registry"){        
        return;
    }else{
    
        //Compile the parameter array based on the study selected
        var paramArray = new Array();
    
        if(studyname==="EURINE-ACT"){        
            //Add "plasma" if called from NAPACA        
            if(ensatsection==="NAPACA"){
                paramArray.push("plasma");
            }
            paramArray.push("heparin_plasma");
            paramArray.push("serum");
            paramArray.push("24h_urine");
            paramArray.push("spot_urine");        
        }else if(studyname==="ADIUVO"){
            paramArray.push("serum");        
            paramArray.push("24h_urine");
            paramArray.push("spot_urine");
        }else{
            paramArray.push("");        
        }
    
        //Close the 24h_urine by default first
        this.showHide("myDiv_24h_urine_options","");
    
        //Now loop through the parameters and set the options to false-true-false for each named in the array
        var elemCount = 0;    
        var totalElems = document.f1.elements.length;

        //Reset the aliquot/freezer options first
        //window.alert("elemCount: " + elemCount);
        //window.alert("totalElems: " + totalElems);
        while(elemCount < totalElems){
            var thisAliquotParamName = document.f1.elements[elemCount].name;
            var aliquotName = "aliquot_" + thisAliquotParamName + "_options";

            var freezerName = thisAliquotParamName + "_freezer_info";
            var freezerId = document.getElementById(freezerName);
            if(freezerId!==null){
                freezerId.innerHTML="";
            }
        
            var aliquotId = document.getElementById(aliquotName);
            if(aliquotId!==null){
                if(aliquotId.className==='show'){
                    this.showHide(aliquotName,"");
                }
            }        
            elemCount = elemCount+1;
        }
        elemCount = 0;    
    
        while(elemCount < totalElems){        
            var thisParamName = document.f1.elements[elemCount];

            //Avoid the text fields (alternative is "SELECT" for the menus - "INPUT" is for the text-fields)
            //window.alert("nodeName: " + thisParamName.nodeName);
        
            if(thisParamName.nodeName!=="INPUT"){

                //Except elemCount (3), which is the associated_study field itself (and elemCount (4) which is the phase/visit)
                //if(elemCount != 1 && elemCount != 2){
                if(elemCount != 1){

                    //THIS EXTRA BIT SOLVES THE ANNOYING ALIQUOT RESET BUG THAT I HAD
                    var aliquotStr = /aliquot/mi;
                    if(aliquotStr.test(thisParamName.name)
                        || thisParamName.name==="biomaterial_date_month"){

                    }else{
                        thisParamName.options[0].selected = false; //[Select...]
                        thisParamName.options[1].selected = false; //"Yes"
                        thisParamName.options[2].selected = false; //"No"
                    }

                    var elemFound = false;
                    var paramCount = 0;
                    while(paramCount < paramArray.length && !elemFound){                   
                        var paramName = paramArray[paramCount];                
                        if(paramName===thisParamName.name){
                            thisParamName.options[1].selected = true; //"Yes"
                            //Show the associated aliquot/freezer with the parameter name
                            var aliquotParamName = "aliquot_" + paramName;                                                
                            this.showHide(aliquotParamName + "_options","Yes");
                            //window.alert("about to run showFreezerInfo...");
                            this.showFreezerInfo(paramName,1,"Yes",server_root);

                            if(paramName==="24h_urine"){
                                this.showHide("myDiv_" + paramName + "_options","Yes");
                                elemCount = elemCount + 1;
                            }
                            elemFound = true;
                            //This skips beyond the newly inputted aliquot selection menu as an option...
                             elemCount = elemCount + 1;
                        }else{                                           
                            if(thisParamName.name!=="associated_study" && !aliquotStr.test(thisParamName.name)
                                && thisParamName.name!=="normal_tissue_options" && thisParamName.name!=="normal_tissue_paraffin_options"
                                && thisParamName.name!=="normal_tissue_dna_options"
                                ){
                                //window.alert("thisParamName.name: " + thisParamName.name);
                                thisParamName.options[2].selected = true; //"No" (parameter is not part of study)
                            }
                            paramCount = paramCount + 1;
                         }
                    }
                 }
             }
            elemCount = elemCount + 1;
         }
        return;
     }
}

function getCenterInfo(server_root,str)
{    
    var xmlhttp;
    if (str==""){
        document.getElementById("center_info").innerHTML="";
        return;
    }
  
    if (window.XMLHttpRequest){
        // code for IE7+, Firefox, Chrome, Opera, Safari
        xmlhttp=new XMLHttpRequest();
    }else{
        // code for IE6, IE5
        xmlhttp=new ActiveXObject("Microsoft.XMLHTTP");
    }
  
    xmlhttp.onreadystatechange=function(){
        if (xmlhttp.readyState==4 && xmlhttp.status==200){            
            document.getElementById("center_info").innerHTML=xmlhttp.responseText;
        }        
    }
    
    var script_url = server_root + "/scripts/getcenterinfo.jsp?centerid="+str;
    //xmlhttp.open("POST","https://registry.ensat.org/scripts/getcenterinfo.jsp?centerid="+str,true);
    xmlhttp.open("POST",script_url,true);
    xmlhttp.send();
}

function getAssocStudy(server_root,str)
{    
    //Make sure this is only fired for the studies that actually have extra information available
    if(str!=="PMT" && str!=="FIRST-MAPPP" && str!=="German Cushing Registry" && str!=="German Conn Registry"){    
        str = "";
    }
    this.runAssocStudy(server_root,str);    
}

function runAssocStudy(server_root,str){
    
    //Run the showHide function for myDiv_associated_study_options first...
    //BINARY BUG: check if the element is open first
    var switch_id = document.getElementById('myDiv_associated_study_options');
    if(switch_id.className!=='show'){
        this.showHide('myDiv_associated_study_options','Yes');
    }
        
    var xmlhttp;
    if (str==""){
        document.getElementById("associated_study_menus").innerHTML="";
        return;
    }
  
    if (window.XMLHttpRequest){
        // code for IE7+, Firefox, Chrome, Opera, Safari
        xmlhttp=new XMLHttpRequest();
    }else{
        // code for IE6, IE5
        xmlhttp=new ActiveXObject("Microsoft.XMLHTTP");
    }
  
    xmlhttp.onreadystatechange=function(){
        if (xmlhttp.readyState==4 && xmlhttp.status==200){            
            document.getElementById("associated_study_menus").innerHTML=xmlhttp.responseText;
        }        
    }
    
    //xmlhttp.open("POST","https://registry.ensat.org/scripts/getassocstudy.jsp?study="+str,true);
    //xmlhttp.open("POST","https://stell-3.rc.melbourne.nectar.org.au/test_ensat/scripts/getassocstudy.jsp?study="+str,true);
    //xmlhttp.open("POST","http://localhost:18080/scripts/getassocstudy.jsp?study="+str,true);
    var script_url = "" + server_root + "scripts/getassocstudy.jsp?study="+str;
    xmlhttp.open("POST",script_url,true);
    xmlhttp.send();    
}


function parameterValidate(paramValue, paramName, optional, paramType, server_root){
    
    var paramElement = document.getElementById("" + paramName);
    //var paramElement = document.getElementById("myDiv_" + paramName);
    
    paramValue = paramValue.trim();
    //window.alert("paramValue: " + paramValue + ", paramName: " + paramName + ", optional: " + optional + ", paramType: " + paramType);
    
    //Check #1 - if the parameter is not optional is it blank
    if(!optional && paramValue===""){        
        //document.getElementById("" + paramName).innerHTML="<div class='errorLabel'>*Required</div>";
        if(paramElement){
            paramElement.innerHTML="<div class='errorLabel'>*Required</div>";
        }
        return false;        
    }else{
        
        //Reset the element from any previous warnings
        //window.alert("paramName: " + paramName);
        this.resetParameter(paramElement);
        
        /** Check #2 
          * - if number, check it's numeric
          * - if date, check it's a date          
          */
        var validityErrorMessage = this.checkValidity(paramValue,paramType);
         
        if(validityErrorMessage!==""){
            if(paramElement){
                //document.getElementById("" + paramName).innerHTML="" + validityErrorMessage;
                paramElement.innerHTML="" + validityErrorMessage;
            }
            return false;
        }else{
             
            //Reset the element from any previous warnings
            this.resetParameter(paramElement);
        
            /** Check #3 
               * - if number or date, check range with lower and upper bounds
               * - if text, check length of allowed string (based on database field size)
               */
            var rangeTestErrorMessage = this.checkRange(paramName, paramType, paramValue,server_root);
             
            //DEBUGGING STUB
            //rangeTestErrorMessage = "";
             
            if(rangeTestErrorMessage!==""){
                /*if(paramName==="height"){
                    window.alert("rangeTestErrorMessage: " + rangeTestErrorMessage);
                }*/
                if(paramElement){
                    //document.getElementById("" + paramName).innerHTML="" + rangeTestErrorMessage;                    
                    paramElement.innerHTML="" + rangeTestErrorMessage;
                }
                return false;
            }else{
                
                var mmParameter = this.getMmParameter(paramName);
                if(mmParameter && paramValue!==""){                    
                    var mmValidErrorMessage = this.checkMmValidity(paramValue);                    
                    if(mmValidErrorMessage!==""){                    
                        if(paramElement){
                            paramElement.innerHTML="" + mmValidErrorMessage;
                        }                    
                        return false;                
                    }else{
                        return true;
                    }
                }else{
                    return true;
                }
            }             
        }
    }
}


function checkMmValidity(paramValue){
    
    //Test every character for a decimal point, if it's present then return as invalid (mm with decimal points is unlikely)
    var valueLength = paramValue.length;
    var m = 0;
    var dpFound = false;
    while(!dpFound && m < valueLength){
        var charIn = paramValue.charAt(m);
        if(charIn==='.'){
            dpFound = true;
        }else{
            m++;
        }
    }    
    if(dpFound){
        return "<div class='errorLabel'>*Decimal point in mm measurement</div>";
    }else{
        return "";
    }    
}

function getMmParameter(paramName){
    
    if(paramName==="size_of_adrenal_tumor"
        || paramName==="right_adrenal_max_tumor"
        || paramName==="left_adrenal_max_tumor"
        || paramName==="followup_max_tumor"
        || paramName==="tumor_size"
        || paramName==="largest_size_x"
        || paramName==="largest_size_y"
        || paramName==="largest_size_z"
        || paramName==="max_tumor_by_ct_right"
        || paramName==="max_tumor_by_mr_right"            
        || paramName==="max_tumor_by_ct_left"
        || paramName==="max_tumor_by_mr_left"
        || paramName==="max_tumor_right"            
        || paramName==="max_tumor_left"
        ){
            return true;
        }else{
            return false;
        }
}

function resetParameter(paramElement){        
    if(paramElement){
        paramElement.innerHTML="";
    }
    return;    
}


function checkValidity(paramValue, paramType){
    
    var validityErrorMessage = "";
    
    if(paramType==="number"){        
        if(!(this.isNumeric(paramValue)) && !(paramValue==="")){
            validityErrorMessage = "<div class='errorLabel'>*Value is not numeric</div>";            
        }
    }else if(paramType==="date"){
        if(!(this.checkDateValidity(paramValue)) && !(paramValue==="")){
            validityErrorMessage = "<div class='errorLabel'>*Value is not in the correct date format (dd-mm-yyyy)</div>";
        }
    }

    //Message will return blank if the type is "text" or "menu"
    return validityErrorMessage;
}

function checkRange(paramName, paramType, paramValue,server_root){
    
    //Run AJAX functions here to retrieve the range bounding values and text size from database
    //Though note that the AJAX functions are actually synchronous... so not strictly AJAX        
    var rangeTestErrorMessage = "";    
    if(paramType==="number"){
        var boundArray = this.checkParameterRange(server_root,paramName,paramType);    
        var lowerBound = boundArray[0];
        var upperBound = boundArray[1];                        
        if(!this.checkNumberRange(paramValue,lowerBound,upperBound) && !(paramValue==="")){
            rangeTestErrorMessage = "<div class='errorLabel'>*Value is not within the specified number range</div>";
        }
    }else if(paramType==="date"){        
        boundArray = this.checkParameterRange(server_root,paramName,paramType);    
        lowerBound = boundArray[0];
        upperBound = boundArray[1];

        if(!this.checkDateRange(paramValue,lowerBound,upperBound) && !(paramValue==="")){
            rangeTestErrorMessage = "<div class='errorLabel'>*Value is not within the specified date range</div>";
        }
    }else if(paramType==="text"){        
        var textSizeBound = this.checkTextSize(server_root,paramName);    
        if(paramValue.length > textSizeBound){
            rangeTestErrorMessage = "<div class='errorLabel'>*Text value is too large</div>";
        }
    }    
    return rangeTestErrorMessage;
}

function checkParameterRange(server_root, paramName, paramType){
    
    var paramRangeArray = new Array();
    
    var xmlhttp;
    if (paramName===""){
        paramRangeArray.push("");
        paramRangeArray.push("");
        return paramRangeArray;
    }else{
  
        if (window.XMLHttpRequest){
            // code for IE7+, Firefox, Chrome, Opera, Safari
            xmlhttp=new XMLHttpRequest();
        }else{
            // code for IE6, IE5
            xmlhttp=new ActiveXObject("Microsoft.XMLHTTP");
        }
  
        xmlhttp.onreadystatechange=function(){
            if (xmlhttp.readyState==4 && xmlhttp.status==200){            
                var rangeResponse = xmlhttp.responseText;                                        
                //Remove all the white-space from the response
                rangeResponse = rangeResponse.trim();
                
                //Put in non-null values if the type is date and the values are empty
                if(rangeResponse==="|"){
                    if(paramType==="date"){
                        rangeResponse = "1900-01-01|2100-01-01";
                    }
                }
                
                //Now divide into upper and lower bound
                var pipeIndex = rangeResponse.indexOf("|");
                var lowerBound = rangeResponse.substr(0,pipeIndex);
                var upperBound = rangeResponse.substr(pipeIndex+1,rangeResponse.length);
                paramRangeArray.push(lowerBound);
                paramRangeArray.push(upperBound);                
            }        
        }
    
        //Note the "false" flag. This is because the Javascript has to wait on the response before proceeding, so it must be synchronous
        
        var script_url = server_root + "/scripts/getrange.jsp?param="+paramName;        
        //xmlhttp.open("POST","https://stell-3.rc.melbourne.nectar.org.au/test_ensat/scripts/getrange.jsp?param="+paramName,false);
        //xmlhttp.open("POST","http://localhost:18080/scripts/getrange.jsp?param="+paramName,false);
        xmlhttp.open("POST",script_url,false);
        xmlhttp.send();    
    }
    return paramRangeArray;
}

function checkTextSize(server_root, paramName){
    
    var textSize = "";
    
    var xmlhttp;
    if (paramName===""){            
        return textSize;
    }else{
  
        if (window.XMLHttpRequest){
            // code for IE7+, Firefox, Chrome, Opera, Safari
            xmlhttp=new XMLHttpRequest();
        }else{
            // code for IE6, IE5
            xmlhttp=new ActiveXObject("Microsoft.XMLHTTP");
        }
  
        xmlhttp.onreadystatechange=function(){
            if (xmlhttp.readyState==4 && xmlhttp.status==200){            
                textSize = xmlhttp.responseText;                                        
                //Remove all the white-space from the response
                textSize = textSize.trim();
            }        
        }
    
        //Note the "false" flag. This is because the Javascript has to wait on the response before proceeding, so it must be synchronous
        var script_url = server_root + "/scripts/gettextsize.jsp?param="+paramName;
        //xmlhttp.open("POST","https://stell-3.rc.melbourne.nectar.org.au/test_ensat/scripts/gettextsize.jsp?param="+paramName,false);
        //xmlhttp.open("POST","http://localhost:18080/scripts/gettextsize.jsp?param="+paramName,false);
        xmlhttp.open("POST",script_url,false);
        xmlhttp.send();    
    }
    return textSize;
}

function checkDateValidity(dateValue){
    
    var dateValid = false;            
    //Test that all three components are present
    dateValid = this.testDateFormat(dateValue);    
    return dateValid;
}

function checkDateRange(dateValue,lowBound,highBound){

    //Might need to run some conversions into dates here (NEEDS A FORMATTER MAYBE?)
    dateValue = this.reformatDateEuropean(dateValue);
    lowBound = this.reformatDateEuropean(lowBound);
    highBound = this.reformatDateEuropean(highBound);
    
    //window.alert("dateValue: " + dateValue);
    //window.alert("lowBound: " + lowBound);
    //window.alert("highBound: " + highBound); 
    
    //NOTE: dates can't be instantiated like this (the below works in Chrome but not Firefox)
    var thisDate = new Date(dateValue);
    var lowDate = new Date(lowBound);
    var highDate = new Date(highBound);
    
    var validDate = (thisDate > lowDate) && (thisDate < highDate);    
    //window.alert("validDate: " + validDate);
    return validDate;    
}

function reformatDateEuropean(dateValue){
    var cptDay = dateValue.substr(0,2);    
    var cptMonth = dateValue.substr(dateValue.indexOf("-")+1,2);
    var cptYear = dateValue.substr(dateValue.lastIndexOf("-")+1,4);        
    //return cptMonth + "-" + cptDay + "-" + cptYear;    
    return cptYear + "/" + cptMonth + "/" + cptDay + " 12:00:00 AM";
}

function checkNumberRange(numberValue,lowBound,highBound){    
    return (parseFloat(numberValue) > lowBound) && (parseFloat(numberValue) < highBound);
}

function validateForm(inputForm, formName, dbn, centerid,server_root){
    
    var formValid = true;        
    
    //Do a pre-processing of the form parameters to strip off the confirm button and any "clarification" elements
    //Also strip off the freezer parameters
    var formParams = new Array();
    var freezerParams = new Array();
    var i = 0;
    while(i<(inputForm.elements.length-1)){
        
        var paramName = inputForm.elements[i].name;
        var paramValue = inputForm.elements[i].value;
        var paramOptional = "";
        var paramType = "";

        //Remove clarification elements from form evaluation (and freezer elements)
        var clarStr = /clarification/mi;
        var freezerStr = /freezer/mi;
        var freezerShelfStr = /freezershelf/mi;
        var rackStr = /rack/mi;
        var shelfStr = /shelf/mi;
        var boxStr = /box/mi;
        var positionStr = /position/mi;    
        if(!clarStr.test(paramName)
            && !freezerStr.test(paramName)
            && !freezerShelfStr.test(paramName)
            && !rackStr.test(paramName)
            && !shelfStr.test(paramName)
            && !boxStr.test(paramName)
            && !positionStr.test(paramName)
            ){
        
            //Check whether parameter is optional
            var paramCheckNum = paramArrays.length;                        
            var paramTypeFound = false;
            var paramCheckCount = 0;
            while(paramCheckCount < paramCheckNum && !paramTypeFound){
                var paramArrayIn = paramArrays[paramCheckCount];
                if(paramName===paramArrayIn[0]){
                    paramOptional = paramArrayIn[1];
                    paramType = paramArrayIn[2];
                    paramTypeFound = true;
                }else{
                    paramCheckCount++;
                }
            }

            var formParam = new Array(paramName,paramValue,paramOptional,paramType);
            formParams.push(formParam);        
        }else{
            if(!clarStr.test(paramName)){
                var freezerParam = new Array(paramName,paramValue,true,'number');
                freezerParams.push(freezerParam);
            }            
        }
        i++;
    }
    
    //The loop below is for display-checking only
    /*var k = 0;
    while(k<formParams.length){
        var thisParamOut = formParams[k];
        var thisParamName = thisParamOut[0];
        var thisParamValue = thisParamOut[1];
        var thisParamOptional = thisParamOut[2];
        var thisParamType = thisParamOut[3];
        //window.alert(k + " | " + thisParamName + " | " + thisParamValue + " | " + thisParamOptional + " | " + thisParamType);        
        k++;
    }*/

    //Now run through the consolidated set of parameters and check their validity
    var j = 0;
    while(j<formParams.length){
        
        var paramValid = true;
        
        var thisParam = formParams[j];
        paramType = thisParam[3];
        paramName = thisParam[0];
        paramOptional = thisParam[2];
        paramValue = thisParam[1];

        //BUG: NEED SOME WAY TO (CONSISTENTLY) SKIP THE VALIDATION OF HIDDEN PARAMETERS - IN THIS CASE IT'S THE ADDITIONAL "OTHERS" OPTIONS
        if(paramName==="surgery_extended_others"
            || paramName==="surgery_first_others"
            || paramName==="hormone_symptoms_others"
            || paramName==="tumor_symptoms_others"
            || paramName==="firstmappp_id"
            || paramName==="adiuvo_id"
            || paramName==="adiuvo_observational_id"
            || paramName==="lysosafe_id"
            || paramName==="german_cushing_id"
            || paramName==="german_conn_id"){
            paramValid = true;
        }else{            
            //window.alert("paramName: " + paramName + ", paramValid: " + paramValid + ", paramOptional: " + paramOptional);                
            //Set optional to true if the parameter has any of the aliquot name elements (in the string)
            if(!paramOptional){
                paramOptional = this.matchAliquotNames(paramName);
            }
            
            //Identify that the parameter is one that has a freezer/aliquot association        
            var isFreezerLine = this.matchAliquotFreezerParameters(paramName);
            
            //If it is a parameter with a freezer line, then pull all of them out of the document and validate all those that are visible (className==="show")
            if(isFreezerLine){ 
                var hiddenLineFound = false;
                var k = 1;
                while(k<10 && !hiddenLineFound){
                    var freezerInfoStr = paramName + "_freezer_info_" + k + "";                    
                    var param_element = document.getElementById(freezerInfoStr);
                    if(param_element.className==="show"){    
                    
                        //Now validate the individual elements of the freezer line
                        var indFreezerName = "";
                        var indFreezerValue = "";
                        var freezerinfo = "";
                        for(var m=0; m<6; m++){                        
                            if(m===0){
                                indFreezerName = paramName + "_freezer_" + k;                            
                            }else if(m===1){
                                indFreezerName = paramName + "_freezershelf_" + k;
                            }else if(m===2){
                                indFreezerName = paramName + "_shelf_" + k;
                            }else if(m===3){
                                indFreezerName = paramName + "_rack_" + k;
                            }else if(m===4){
                                indFreezerName = paramName + "_box_" + k;
                            }else if(m===5){
                                indFreezerName = paramName + "_position_" + k;
                            }

                            var n = 0;
                            var freezerParamFound = false;
                            while(n<freezerParams.length && !freezerParamFound){
                                var thisParamOut = freezerParams[n];
                                var thisFreezerParamName = thisParamOut[0];
                                if(thisFreezerParamName===indFreezerName){
                                    indFreezerValue = thisParamOut[1];
                                    freezerParamFound = true;
                                }else{
                                    n++;
                                }
                            }
                            //window.alert("indFreezerValue (" + m + "): " + indFreezerValue);
                            freezerinfo = freezerinfo + indFreezerValue + "|";                        
                            //window.alert("freezerinfo: " + freezerinfo);
                            paramValid = this.parameterValidate(indFreezerValue, indFreezerName,'true','number',server_root);
                            if(!paramValid){
                                //window.alert("paramName: " + paramName);
                                formValid = false;
                            }                            
                        } 
                        
                        //window.alert("freezerInfoStr: " + freezerInfoStr);
                        //window.alert("paramValid: " + paramValid);
                        
                        //Run an occupancy check for each freezer line (that is open)                    
                        //Cut the last pipe symbol from freezerinfo
                        freezerinfo = freezerinfo.substr(0,freezerinfo.length-1);
                        //window.alert("freezerinfo: " + freezerinfo);                    
                        if(paramValid){
                            var positionOccupied = this.checkFreezerOccupancy(dbn, centerid, freezerinfo);
                            //window.alert("positionOccupied (in validateForm): " + positionOccupied);
                            if(positionOccupied==='true'){
                                //window.alert("POINT IS REACHED IF positionOccupied IS TRUE... (" + positionOccupied + ")");
                                document.getElementById("" + paramName + "_occupancy_" + k).innerHTML="*Position in freezer is already occupied";
                                paramValid = false;
                            }
                        }                        
                        if(!paramValid){
                            //window.alert("paramName: " + paramName);
                            formValid = false;
                        }                    
                        k++;                    
                    }else{
                        hiddenLineFound = true;                
                    }                
                }
            }else{
                paramValid = this.parameterValidate(paramValue, paramName, paramOptional, paramType, server_root);
                if(!paramValid && paramName!==""){
                    //window.alert("paramName: " + paramName);
                    formValid = false;
                }
            }
        }                        
        j++;
    }
    
    if(!formValid){
        document.getElementById("" + formName).innerHTML="<div class='errorLabel'>*Please address the validation issues below.</div>";
    }    
    return formValid;
    //return false;
}

function parameterDateCheck(paramName){    
    var finalTag = paramName.substr(paramName.lastIndexOf("_")+1,paramName.length);    
    return (finalTag==="day" || finalTag==="month" || finalTag==="year");
}

function parameterDateName(paramName){    
    return paramName.substr(0,paramName.lastIndexOf("_"));    
}

function testDateFormat(paramValue){    
    //Check that all three components have length > 0
    var cpt1 = paramValue.substr(0,paramValue.indexOf("-"));
    var cpt2 = paramValue.substr(paramValue.indexOf("-")+1,paramValue.lastIndexOf("-"));
    var cpt3 = paramValue.substr(paramValue.lastIndexOf("-")+1,paramValue.length);
    
    return (cpt1.length > 0) && (cpt2.length > 0) && (cpt3.length > 0)
    && (paramValue.length > paramValue.lastIndexOf("-")) && (paramValue.lastIndexOf("-") > paramValue.indexOf("-") && (paramValue.indexOf("-") > 0));    
}

function testDayRange(paramValue){
    
    //Grab the day and month components
    var month = paramValue.substr(paramValue.indexOf("-")+1,paramValue.lastIndexOf("-"));
    var day = paramValue.substr(paramValue.lastIndexOf("-")+1,paramValue.length);
    
    if(month==="01" || month==="03" || month==="05" || month==="07" || month==="08" || month==="10" || month==="12"){
        if(parseFloat(day) > 31){
            return false;
        }else{
            return true;
        }
    }else if(month==="04" || month==="06" || month==="09" || month==="11"){
        if(parseFloat(day) > 30){
            return false;
        }else{
            return true;
        }
    }else{
        if(parseFloat(day) > 29){
            return false;
        }else{
            return true;
        }
    }
}


function isNumeric(sText){
    var validChars = "0123456789.-";
    var isNumber=true;
    var character;
    
    for (var i = 0; i < sText.length && isNumber == true; i++){
        character = sText.charAt(i);
        if (validChars.indexOf(character) == -1){
            isNumber = false;
        }
    }
    return isNumber;
}

function dispOthers(fieldName,value)
{   
    //Fix this so that other options as well as "Others" will trigger the open text-field
    var otherPresent = false;
    if(fieldName==="chemotherapy_regimen"){
        var inputOptions = document.forms.f1.chemotherapy_regimen;
    }else if(fieldName==="followup_organs"){
        inputOptions = document.forms.f1.followup_organs;
    }else if(fieldName==="surgery_extended"){
        inputOptions = document.forms.f1.surgery_extended;
    }else if(fieldName==="surgery_first"){
        inputOptions = document.forms.f1.surgery_first;
    }else if(fieldName==="radiotherapy_location"){
        inputOptions = document.forms.f1.radiotherapy_location;
    }else if(fieldName==="radiofrequency_location"){
        inputOptions = document.forms.f1.radiofrequency_location;
    }else if(fieldName==="metastases_location"){
        inputOptions = document.forms.f1.metastases_location;
    }else if(fieldName==="normal_tissue_options"){
        inputOptions = document.forms.f1.normal_tissue_options;
    }else if(fieldName==="normal_tissue_paraffin_options"){
        inputOptions = document.forms.f1.normal_tissue_paraffin_options;
    }else if(fieldName==="normal_tissue_dna_options"){
        inputOptions = document.forms.f1.normal_tissue_dna_options;
    }
    
    /*for (var i=0; i < inputOptions.length; i++){
        
        //window.alert("inputOptions[" + i +"].selected: " + inputOptions[i].selected + " and value is: " + inputOptions[i].value);
        if (inputOptions[i].selected !== false){            
            var inputOptionsVal = "" + inputOptions[i].value;
            if(inputOptionsVal==="Others" || inputOptionsVal==="Metastases - Others"){
                otherPresent = true;
            }            
        }
    }*/
    otherPresent = (value==="Others");
        
    if(otherPresent){
        document.getElementById("myDiv_" + fieldName + "_others").innerHTML="Others (please specify): <input name='" + fieldName + "_others' type='text' size='15' onfocus='inform=true;' onblur='inform=false;'/>";
    }else{
        document.getElementById("myDiv_" + fieldName + "_others").innerHTML="";
    }
   
    
    
/*if(value==="Others" || (fieldName==="surgery_extended" && value==="Metastases - Others")){
        document.getElementById("myDiv_" + fieldName + "_others").innerHTML="Others (please specify): <input name='" + fieldName + "_others' type='text' size='15' onfocus='inform=true;' onblur='inform=false;'/>";
    }else{
        document.getElementById("myDiv_" + fieldName + "_others").innerHTML="";
    }*/
}

function IsInteger(s) {
    return (s.toString().search(/^-?[0-9]+$/) == 0);
}

function clarification(fieldName){

    var message = "";
    if(fieldName==="clarification_ensat_reg"){
        message = "Description of ENSAT registration";
    }else if(fieldName==="clarification_random"){
        message = "Plasma renin and aldosterone should be measured from samples taken at the same time";
    }else if(fieldName==="clarification_eurine_act"){
        message = "See http://www.ensat.org/eurineact.htm for further information on the EURINE-ACT study";
    }else if(fieldName==="tumor_details"){
        message = "To edit list, go to Tumor Details form list";
    }else if(fieldName==="clarification_absolute_contrast_washout"){
        message = "Absolute washout = 100 * (HU_max - HU_10min)/(HU_max - HU_nativ)";
    }else if(fieldName==="clarification_relative_contrast_washout"){
        message = "Relative washout = 100 * (HU_max - HU_10min)/(HU_max)";
    }else if(fieldName==="double_entry"){
        message = "Matching patient criteria: sex, year of birth and date of ENSAT registration";
    }else{
        message = "Not set";
    }
    alert(message);
    return false;
}

function search_field_addition(fieldName){
                
    var search_field_output = "";                
    search_field_output = "<table><tr>";
    search_field_output += "<td><select name='comparator_6a'><option value=''>[Select...]</option><option value='NOT'>NOT</option><option value='AND'>AND</option><option value='OR'>OR</option></select></td>";
    search_field_output += "<td><input type='text' name='" + fieldName + "' size='30' /></td>";
    search_field_output += "</tr></table>";                                
    document.getElementById("myDiv_" + fieldName).innerHTML="" + search_field_output;
    return;
                
}

function startTime(m, s){
    
    if(s==0){
        m = m-1;
        s = 59;
    }else{
        s = s-1;
    }
    
    // add a zero in front of numbers<10
    s=checkTime(s);
    
    document.getElementById('session_clock').innerHTML="("+m+":"+s+")";
    t=setTimeout(function(){
        startTime(m,s)
    },1000);
}

function checkTime(i){
    if (i<10)
    {
        i="0" + i;
    }
    return i;
}

/*function getExportTimeEstimateTest(dbn, mod){
    return;
}*/

function getExportTimeEstimate(dbn, mod){
    
    //window.alert("into getExportTimeEstimate...");
    
    //Extract the patient count by going through all the select elements of the form
    var patientNum = 0;
    var checkboxes = document.getElementsByName('patient_selection');
    var checkboxnum = checkboxes.length;
    for(var i=0; i < checkboxnum; i++) {
        if(checkboxes[i].checked){
            patientNum++;
        }        
    }
    //window.alert("patientNum: " + patientNum);

    /*var xmlhttp;
      
    if (window.XMLHttpRequest){
        // code for IE7+, Firefox, Chrome, Opera, Safari
        xmlhttp=new XMLHttpRequest();
    }else{
        // code for IE6, IE5
        xmlhttp=new ActiveXObject("Microsoft.XMLHTTP");
    }
  
    xmlhttp.onreadystatechange=function(){
        if (xmlhttp.readyState==4 && xmlhttp.status==200){            
            document.getElementById("timeEstimate").innerHTML = xmlhttp.responseText;                                                    
        }        
    }    
        
    xmlhttp.open("POST","https://registry.ensat.org/scripts/getexporttimeestimate.jsp?patientnumber="+patientNum+"&dbn="+dbn+"&mod="+mod+"",true);
    //xmlhttp.open("POST","https://stell-3.rc.melbourne.nectar.org.au/test_ensat/scripts/getexporttimeestimate.jsp?patientnumber="+patientNum+"&dbn="+dbn+"&mod="+mod+"",true);
    //xmlhttp.open("POST","http://localhost:18080/scripts/getexporttimeestimate.jsp?patientnumber="+patientNum+"&dbn="+dbn+"&mod="+mod+"",true);
    xmlhttp.send();*/
    
    return;
}

function select_all() {
    
  var checkboxes = document.getElementsByName('patient_selection');
  var checkboxnum = checkboxes.length;  
  
  var switchBoolean = true;
  //Test using the first check-box - if it's selected then wipe all of them
  if(checkboxes[0].checked){
      switchBoolean = false;
  }  
  
  for(var i=0; i < checkboxnum; i++) {    
    checkboxes[i].checked = switchBoolean;
  }
  return;
}

function select_all_centerid(centerid) {
    
  var checkboxes = document.getElementsByName('patient_selection');
  var checkboxnum = checkboxes.length;  
  
  //Clear everything
  var codeCount = 0;
  while(codeCount < checkboxnum){
      checkboxes[codeCount].checked = false;
      codeCount++;
  }
  
  //then apply selection
  for(var i=0; i < checkboxnum; i++) {    
    var valueIn = checkboxes[i].value;    
    var hyphenIndex = valueIn.indexOf('-');
    var valueCenterCode = valueIn.substr(0,hyphenIndex);
    if(valueCenterCode===centerid){
        checkboxes[i].checked = true;
    }    
  }
  
  return;
}


function listenerTest(){
    //window.alert("Change fired #1...");
}

function showAssocStudyIDs(valueIn){    
    if(this.checkAssocStudyFlags(valueIn)){
        var elementId = valueIn + "_id_option";        
        var optionIdSwitch = document.getElementById(elementId);
        var showHideFlag = 'show';
        if(optionIdSwitch.className==='hide'){
            showHideFlag = 'show';
        }else{
            showHideFlag = 'hide';
        }
        optionIdSwitch.className = showHideFlag;
    }    
}

function checkAssocStudyFlags(studyIn){    
    return studyIn==="adiuvo"
            || studyIn==="adiuvo_observational"
            || studyIn==="firstmappp"
            || studyIn==="german_cushing"
            || studyIn==="german_conn"
            || studyIn==="lysosafe";
}

function matchAliquotNames(inStr){
    var aliquotStr = /aliquot/mi;    
    return aliquotStr.test(inStr);
}

/*function showFreezerInfo(theid,thevalue){    
    window.alert("test...");
    if (document.getElementById) {
        var switch_id = document.getElementById(theid + "_freezer_info");
        if(thevalue==="Yes"){
            switch_id.className = 'show';
        }else{        
            switch_id.className = 'hide';
        }
        var freezerStrParam = "";
        //Run through the elements and show as many as specified by the aliquots
        var freezerNum = 1;
        if (document.getElementById) {        
            for(var i=0; i<freezerNum; i++){
                freezerStrParam = freezerStrParam + this.getFreezerHtml(theid,"",(i+1),false) + "<br/>";            
            }            
            switch_id.innerHTML=freezerStrParam;
        }
    }
}*/

function showFreezerInfo(theid,aliquotNum,thevalue, server_root){
    
    //window.alert("theid: " + theid);
    
    if (document.getElementById) {
        var switch_id = document.getElementById(theid + "_freezer_info");
        if(thevalue==="Yes"){
            switch_id.className = 'show';
        }else{        
            switch_id.className = 'hide';
        }
        var freezerStrParam = "";
        //Run through the elements and show as many as specified by the aliquots
        var freezerNum = aliquotNum;
        if (document.getElementById) {        
            //window.alert("freezerNum: " + freezerNum);
            for(var i=0; i<freezerNum; i++){
                freezerStrParam = freezerStrParam + this.getFreezerHtml(theid,"",(i+1),false,server_root) + "<br/>";            
                //window.alert("freezerStrParam: " + freezerStrParam);
            }            
            
            switch_id.innerHTML=freezerStrParam;
        }
    }
}



function showFreezerInfoUpdate(theid,thevalue){    
    if (document.getElementById) {
        var switch_id = document.getElementById(theid + "_freezer_info_1");
        if(thevalue==="Yes"){
            switch_id.className = 'show';
        }else{        
            switch_id.className = 'hide';
        }
        var freezerStrParam = "";
        //Run through the elements and show as many as specified by the aliquots
        var freezerNum = 1;
        if (document.getElementById) {        
            for(var i=0; i<freezerNum; i++){
                freezerStrParam = freezerStrParam + this.getFreezerHtml(theid,"",(i+1),false) + "<br/>";            
            }            
            switch_id.innerHTML=freezerStrParam;
        }
    }
}

function showFreezerAliquotNumber(thename,thevalue,normalTissue,ntValue){    
    
    //Case for the zero possibility (should still be 1)
    if(thevalue===0){
        thevalue = 1;
    }
    
    //Remove leading "aliquot_" string
    var freezerName = thename.substr(8, thename.length-8);
    
    var freezerStrParam = "";    
    //Run through the elements and show as many as specified by the aliquots
    var freezerNum = thevalue;
    if (document.getElementById) {        
        for(var i=0; i<freezerNum; i++){
            if(normalTissue){
                //freezerStrParam = freezerStrParam + this.getFreezerHtml(freezerName,ntValue,(i+1),true) + "<br/>";
                freezerStrParam = freezerStrParam + this.getFreezerHtml(freezerName,"",(i+1),true) + "<br/>";
            }else{
                freezerStrParam = freezerStrParam + this.getFreezerHtml(freezerName,"",(i+1),false) + "<br/>";
            }
            
        }
        var switch_id = document.getElementById(freezerName + "_freezer_info");
        switch_id.innerHTML=freezerStrParam;
    }
}

function showFreezerAliquotNumberUpdate(thename,thevalue){    
    
    //Case for the zero possibility (should still be 1)
    if(thevalue===0){
        thevalue = 1;
    }
    
    //Remove leading "aliquot_" string (if it's there)
    var freezerName = thename;
    if(thename.substr(0,8)==="aliquot_"){
        freezerName = thename.substr(8, thename.length-8);
    }    
    
    //var freezerStrParam = "";    
    //Run through the elements and show as many as specified by the aliquots
    var freezerNum = thevalue;
    if (document.getElementById) {        
        for(var i=0; i<freezerNum; i++){
            var freezerLabel = freezerName + "_freezer_info_" + (i+1);
            var freezerElem = document.getElementById(freezerLabel);
            freezerElem.className='show';
        }
        
        //Hide any that are open beyond freezerNum
        for(var j=parseInt(freezerNum); j<9; j++){
            freezerLabel = freezerName + "_freezer_info_" + (j+1);
            freezerElem = document.getElementById(freezerLabel);
            if(freezerElem != null){
                freezerElem.className='hide';            
            }
        }
    }
}



function checkFreezerOccupancy(server_root, dbn,centerid,freezerinfo){
    
    var positionOccupied = true;
    
    var xmlhttp;
    if (dbn==="" || freezerinfo===""){
        return false;
    }else{
  
        if (window.XMLHttpRequest){
            // code for IE7+, Firefox, Chrome, Opera, Safari
            xmlhttp=new XMLHttpRequest();
        }else{
            // code for IE6, IE5
            xmlhttp=new ActiveXObject("Microsoft.XMLHTTP");
        }
  
        xmlhttp.onreadystatechange=function(){
            if (xmlhttp.readyState==4 && xmlhttp.status==200){            
                positionOccupied = xmlhttp.responseText;                                        
                //Remove all the white-space from the response
                positionOccupied = positionOccupied.trim();                
            }        
        }
    
        //Note the "false" flag. This is because the Javascript has to wait on the response before proceeding, so it must be synchronous
        var script_url = server_root + "/scripts/checkfreezerposition.jsp?dbn="+dbn+"&centerid="+centerid+"&freezerinfo="+freezerinfo;
        //xmlhttp.open("POST","https://registry.ensat.org/scripts/checkfreezerposition.jsp?dbn="+dbn+"&centerid="+centerid+"&freezerinfo="+freezerinfo,false);
        //xmlhttp.open("POST","https://stell-3.rc.melbourne.nectar.org.au/test_ensat/scripts/checkfreezerposition.jsp?dbn="+dbn+"&centerid="+centerid+"&freezerinfo="+freezerinfo,false);
        //xmlhttp.open("POST","http://localhost:18080/scripts/checkfreezerposition.jsp?dbn="+dbn+"&centerid="+centerid+"&freezerinfo="+freezerinfo,false);
        xmlhttp.open("POST",script_url,false);
        xmlhttp.send();    
    }
    return positionOccupied;
}

function matchAliquotFreezerParameters(paramIn){
    
    return paramIn==="tumor_tissue_frozen" 
    || paramIn==="tumor_tissue_paraffin"
    || paramIn==="tumor_tissue_dna"
    || paramIn==="leukocyte_dna"
    || paramIn==="plasma"
    || paramIn==="heparin_plasma"
    || paramIn==="serum"
    || paramIn==="24h_urine"
    || paramIn==="spot_urine"
    || paramIn==="normal_tissue_options_adjacentadrenal"
    || paramIn==="normal_tissue_options_kidney"
    || paramIn==="normal_tissue_options_liver"
    || paramIn==="normal_tissue_options_lung"
    || paramIn==="normal_tissue_options_lymphnode"
    || paramIn==="normal_tissue_options_fatperiadrenal"
    || paramIn==="normal_tissue_options_fatsubcutaneous"
    || paramIn==="normal_tissue_options_others"
    || paramIn==="normal_tissue_paraffin_options_adjacentadrenal"
    || paramIn==="normal_tissue_paraffin_options_kidney"
    || paramIn==="normal_tissue_paraffin_options_liver"
    || paramIn==="normal_tissue_paraffin_options_lung"
    || paramIn==="normal_tissue_paraffin_options_lymphnode"
    || paramIn==="normal_tissue_paraffin_options_fatperiadrenal"
    || paramIn==="normal_tissue_paraffin_options_fatsubcutaneous"
    || paramIn==="normal_tissue_paraffin_options_others"
    || paramIn==="normal_tissue_dna_options_adjacentadrenal"
    || paramIn==="normal_tissue_dna_options_kidney"
    || paramIn==="normal_tissue_dna_options_liver"
    || paramIn==="normal_tissue_dna_options_lung"
    || paramIn==="normal_tissue_dna_options_lymphnode"
    || paramIn==="normal_tissue_dna_options_fatperiadrenal"
    || paramIn==="normal_tissue_dna_options_fatsubcutaneous"
    || paramIn==="normal_tissue_dna_options_others";
}

function normalTissueAliquotShow(field){

    var fieldName = field.name;
        
    var inputOptions = "";
    var normalTissueType = "";
    if(fieldName==="normal_tissue_options"){
        inputOptions = document.forms.f1.normal_tissue_options;
        normalTissueType = "";
    }else if(fieldName==="normal_tissue_paraffin_options"){
        inputOptions = document.forms.f1.normal_tissue_paraffin_options;
        normalTissueType = "paraffin_";
    }else if(fieldName==="normal_tissue_dna_options"){
        inputOptions = document.forms.f1.normal_tissue_dna_options;
        normalTissueType = "dna_";
    }

    for (var i=0; i < inputOptions.length; i++){
        
        //Depending on the option selected and the type, show the relevant aliquot option
        var optionIn = "" + inputOptions[i].value;    
        optionIn = this.valueToParam(optionIn);
        var normalTissueParamToShow = "normal_tissue_" + normalTissueType + "options_" + optionIn + "";        
        var showHideStr = "";
        if (inputOptions[i].selected !== false){
            showHideStr = "show";
        }else{
            //this.hideNormalTissueFreezerInfo("" + normalTissueParamToShow + "");   
            showHideStr = "hide";
        }
        this.showNormalTissueFreezerInfo("" + normalTissueParamToShow + "",showHideStr);
    }         
    return;
}

function showNormalTissueFreezerHeaderInfo(paramName, value, checked){    
    
    if (document.getElementById) {
        var freezerHeaderStr = this.getFreezerHeaderHtml(paramName,value);
        value = this.valueToParam(value);
        var switch_id = document.getElementById(paramName + "_freezer_header_info");        
        
        var freezerHeaderOrig = switch_id.innerHTML;        
        var freezerHeaderNew = "";
        
        //window.alert("freezerHeaderOrig: " + freezerHeaderOrig);
        
        if(checked){
            //Check parameter isn't already on the list
            var thisSwitch_id_checked = document.getElementById(paramName + "_options_" + value + "_freezer_info");
            if(thisSwitch_id_checked == null){
            
                if(freezerHeaderOrig!==""){
                    freezerHeaderNew += "<table>";
                    freezerHeaderNew += freezerHeaderOrig;
            
                    //Remove the trailing '</table>' tag
                    freezerHeaderNew = freezerHeaderNew.substr(0,freezerHeaderNew.length-8);
            
                    //Add the header and aliquot menu
                    freezerHeaderNew += "<tr><td>" + freezerHeaderStr + "</td></tr>";
                
                    //Add in the space for the actual freezer menus
                    var freezerTags = "<div id=\"" + paramName + "_" + value + "_freezer_info\">" + this.getFreezerHtml(paramName,value,1,true) + "<br/></div>";
                    freezerHeaderNew += "<tr>";
                    freezerHeaderNew += "<td>";
                    freezerHeaderNew += freezerTags;
                    freezerHeaderNew += "</td>";
                    freezerHeaderNew += "</tr>";
                
                    freezerHeaderNew += "</table>";
                }else{
                    freezerHeaderNew += "<table>";
                
                    //Add the header and aliquot menu
                    freezerHeaderNew += "<tr><td>" + freezerHeaderStr + "</td></tr>";
                
                    //Add in the space for the actual freezer menus                
                    freezerTags = "<div id=\"" + paramName + "_" + value + "_freezer_info\">" + this.getFreezerHtml(paramName,value,1,true) + "<br/></div>";
                    freezerHeaderNew += "<tr>";
                    freezerHeaderNew += "<td>";
                    freezerHeaderNew += freezerTags;
                    freezerHeaderNew += "</td>";
                    freezerHeaderNew += "</tr>";                    
                
                    freezerHeaderNew += "</table>";
                }     
            }
        }else{
            //window.alert("unchecked...");
            //window.alert(paramName + "_options_" + value + "_freezer_info");
            //Blank out the contents of this div if it is unchecked...
            /*var thisSwitch_id_unchecked = document.getElementById(paramName + "_options_" + value + "_freezer_info");
            window.alert("thisSwitch_id_unchecked: " + thisSwitch_id_unchecked);
            thisSwitch_id_unchecked.innerHTML="";*/
            freezerHeaderNew = freezerHeaderOrig;
        }
        
        if(value!==""){
            switch_id.innerHTML = freezerHeaderNew;
        }
    }
    return;
}

function showNormalTissueFreezerHeaderInfoUpdate(paramName, value, checked){    
    
    if (document.getElementById) {
        var freezerHeaderStr = this.getFreezerHeaderHtml(paramName,value);
        value = this.valueToParam(value);
        /*window.alert("paramName: " + paramName);
        window.alert("value: " + value);*/
        var showhide_id = document.getElementById(paramName + "_" + value + "_showhide");        
        showhide_id.className='show';
        var freezer_info_id = document.getElementById(paramName + "_" + value + "_freezer_info_1");        
        freezer_info_id.className='show';
        
        /*var freezerHeaderOrig = switch_id.innerHTML;        
        var freezerHeaderNew = "";
        
        if(checked){
            if(freezerHeaderOrig!==""){
                freezerHeaderNew += "<table>";
                freezerHeaderNew += freezerHeaderOrig;
            
                //Remove the trailing '</table>' tag
                freezerHeaderNew = freezerHeaderNew.substr(0,freezerHeaderNew.length-8);
            
                freezerHeaderNew += "<tr>";
                freezerHeaderNew += "<td>";
                freezerHeaderNew += freezerHeaderStr;
                freezerHeaderNew += "</td>";
                freezerHeaderNew += "</tr>";
                
                //Add in the space for the actual freezer menus
                var freezerTags = "<div id=\"" + paramName + "_" + value + "_freezer_info\">" + this.getFreezerHtml(paramName,value,1,true) + "<br/></div>";
                freezerHeaderNew += "<tr>";
                freezerHeaderNew += "<td>";
                freezerHeaderNew += freezerTags;
                freezerHeaderNew += "</td>";
                freezerHeaderNew += "</tr>";
                
                freezerHeaderNew += "</table>";
            }else{
                freezerHeaderNew += "<table><tr><td>" + freezerHeaderStr + "</td></tr>";
                
                //Add in the space for the actual freezer menus                
                freezerTags = "<div id=\"" + paramName + "_" + value + "_freezer_info\">" + this.getFreezerHtml(paramName,value,1,true) + "<br/></div>";
                freezerHeaderNew += "<tr>";
                freezerHeaderNew += "<td>";
                freezerHeaderNew += freezerTags;
                freezerHeaderNew += "</td>";
                freezerHeaderNew += "</tr>";                    
                
                freezerHeaderNew += "</table>";
            }     
        }else{
            //Find the corresponding value and remove it from the table list
            
            
        }
        
        if(value!==""){
            switch_id.innerHTML = freezerHeaderNew;
        }*/
    }
    return;
}

function getFreezerHeaderHtml(paramName,value){
    
    var outputStr = "";
    outputStr += "<tr>";
    outputStr += "<th colspan='3'>";    
    outputStr += value;
    outputStr += " ";    
    var innerParamName = this.valueToParam(value);
    outputStr += this.getAliquotMenu(paramName,innerParamName,true);    
    outputStr += "</th>";
    outputStr += "</tr>";
    return outputStr;
}

function getAliquotMenu(paramName, innerParamName, normalTissue) {

        var outputStr = "";

        if(normalTissue){
            outputStr += "<select name='aliquot_" + paramName + "_" + innerParamName + "' ";
            //outputStr += "<select name='aliquot_" + paramName + "' ";
            outputStr += "onchange='showFreezerAliquotNumber(this.name,this.value,true,\"" + innerParamName + "\");' >";
        }else{
            outputStr += "<select name='aliquot_" + paramName + "' ";
            outputStr += "onchange='showFreezerAliquotNumber(this.name,this.value,false,\"\");' >";
        }
        
        
        for (var i = 0; i < 10; i++) {
            outputStr += "<option";
            if (i === 1) {
                outputStr += " selected ";
            }
            outputStr += " value='" + i + "'>" + i + "</option>";
        }
        outputStr += "</select>";
        return outputStr;
    }

function valueToParam(valueIn){
    
    var valueOut = "";
    if(valueIn==="Adjacent Adrenal"){
        valueOut = "adjacentadrenal";
    }else if(valueIn==="Kidney"){
        valueOut = "kidney";
    }else if(valueIn==="Liver"){
        valueOut = "liver";
    }else if(valueIn==="Lung"){
        valueOut = "lung";
    }else if(valueIn==="Lymph Node"){
        valueOut = "lymphnode";
    }else if(valueIn==="Fat (Periadrenal)"){
        valueOut = "fatperiadrenal";
    }else if(valueIn==="Fat (Subcutaneous)"){
        valueOut = "fatsubcutaneous";
    }else if(valueIn==="Others"){
        valueOut = "others";
    }
    return valueOut;
}

function getSuggestedFreezerPositions(server_root){
    
    var xmlhttp;
    var suggestedPositions = "1_1_1_1_1_1";
  
    if (window.XMLHttpRequest){
        // code for IE7+, Firefox, Chrome, Opera, Safari
        xmlhttp=new XMLHttpRequest();
    }else{
        // code for IE6, IE5
        xmlhttp=new ActiveXObject("Microsoft.XMLHTTP");
    }
  
    xmlhttp.onreadystatechange=function(){
        if (xmlhttp.readyState==4 && xmlhttp.status==200){            
            suggestedPositions=xmlhttp.responseText;
        }        
    }
    
    //window.alert("server_root: " + server_root);
    var script_url = server_root + "/scripts/getfreezerpos.jsp";
    //window.alert("script_url: " + script_url);
    //xmlhttp.open("POST","https://registry.ensat.org/scripts/getfreezerpos.jsp",false);
    //xmlhttp.open("POST","http://localhost:18080/scripts/getfreezerpos.jsp",false);
    xmlhttp.open("POST",script_url,false);
    xmlhttp.send();
    
    return suggestedPositions;
}


function getFreezerHtml(paramName, value, index, normalTissue, server_root){
    
    if(value!==""){
        value = "_" + value;
    }
    
    //Find the next unoccupied position in the freezer
    //var suggestedPositions = this.getSuggestedFreezerPositions(server_root);    
    //window.alert("suggestedPositions: " + suggestedPositions);
    var suggestedPositions = "1_1_1_1_1_1";
    //var suggestedPositions = "";
    
    var freezerPositions = new Array();
    freezerPositions[0] = "";
    freezerPositions[1] = "";
    freezerPositions[2] = "";
    freezerPositions[3] = "";
    freezerPositions[4] = "";
    freezerPositions[5] = "";    
    
    /*freezerPositions = suggestedPositions.split("_");
    for(var i = 0; i < 6; i++){
        if(freezerPositions[i]===null){
            freezerPositions[i] = "";
        }
    }*/
    
    var outputStr = "";
    outputStr += "<table border='2' width='100%' cellpadding='1'>";
    outputStr += "<tr>";
    outputStr += "<th>";
    outputStr += "#" + index + "";
    outputStr += "</th>";
    outputStr += "<td>";
    outputStr += "<div align='center'>";
    outputStr += "Freezer: ";
    
    if(normalTissue){
        outputStr += "<input type='text' size='3' name='" + paramName + "" + value + "_freezer_" + index + "' value='" + freezerPositions[0] + "' onfocus='inform=true;' onblur='inform=false;'/>";
    }else{
        outputStr += "<input type='text' size='3' name='" + paramName + "_freezer_" + index + "' value='" + freezerPositions[0] + "' onfocus='inform=true;' onblur='inform=false;'/>";
    }
    outputStr += "<div id='" + paramName + "_freezer_" + index + "'></div>";
    outputStr += "</div>";
    outputStr += "</td>";
    
    outputStr += "<td>";
    outputStr += "<div align='center'>";
    outputStr += "Freezer shelf: ";
    if(normalTissue){
        outputStr += "<input type='text' size='3' name='" + paramName + "" + value + "_freezershelf_" + index + "' value='" + freezerPositions[1] + "' onfocus=\"inform=true;\" onblur=\"inform=false;\"/>";
    }else{
        outputStr += "<input type='text' size='3' name='" + paramName + "_freezershelf_" + index + "' value='" + freezerPositions[1] + "' onfocus=\"inform=true;\" onblur=\"inform=false;\"/>";
    }
    outputStr += "<div id='" + paramName + "_freezershelf_" + index + "'></div>";
    outputStr += "</div>";
    outputStr += "</td>";

    outputStr += "<td>";
    outputStr += "<div align='center'>";
    outputStr += "Rack: ";
    
    if(normalTissue){
        outputStr += "<input type='text' size='3' name='" + paramName + "" + value + "_rack_" + index + "' value='" + freezerPositions[2] + "' onfocus=\"inform=true;\" onblur=\"inform=false;\"/>";
    }else{
        outputStr += "<input type='text' size='3' name='" + paramName + "_rack_" + index + "' value='" + freezerPositions[2] + "' onfocus=\"inform=true;\" onblur=\"inform=false;\"/>";
    }
    outputStr += "<div id='" + paramName + "_rack_" + index + "'></div>";
    outputStr += "</div>";
    outputStr += "</td>";
    
    outputStr += "<td>";
    outputStr += "<div align='center'>";
    outputStr += "Rack shelf: ";
    
    if(normalTissue){
        outputStr += "<input type='text' size='3' name='" + paramName + "" + value + "_shelf_" + index + "' value='" + freezerPositions[3] + "' onfocus=\"inform=true;\" onblur=\"inform=false;\"/>";
    }else{
        outputStr += "<input type='text' size='3' name='" + paramName + "_shelf_" + index + "' value='" + freezerPositions[3] + "' onfocus=\"inform=true;\" onblur=\"inform=false;\"/>";
    }
    outputStr += "<div id='" + paramName + "_shelf_" + index + "'></div>";
    outputStr += "</div>";
    outputStr += "</td>";
    
    outputStr += "<td>";
    outputStr += "<div align='center'>";
    outputStr += "Box: ";
    
    if(normalTissue){
        outputStr += "<input type='text' size='3' name='" + paramName + "" + value + "_box_" + index + "' value='" + freezerPositions[4] + "' onfocus=\"inform=true;\" onblur=\"inform=false;\"/>";
    }else{
        outputStr += "<input type='text' size='3' name='" + paramName + "_box_" + index + "' value='" + freezerPositions[4] + "' onfocus=\"inform=true;\" onblur=\"inform=false;\"/>";
    }            
    outputStr += "<div id='" + paramName + "_box_" + index + "'></div>";
    outputStr += "</div>";
    outputStr += "</td>";
    outputStr += "<td>";
    outputStr += "<div align='center'>";
    outputStr += "Position: ";
    
    if(normalTissue){
        outputStr += "<input type='text' size='3' name='" + paramName + "" + value + "_position_" + index + "' value='" + freezerPositions[5] + "' onfocus=\"inform=true;\" onblur=\"inform=false;\"/>";
    }else{
        outputStr += "<input type='text' size='3' name='" + paramName + "_position_" + index + "' value='" + freezerPositions[5] + "' onfocus=\"inform=true;\" onblur=\"inform=false;\"/>";
    }            
    outputStr += "<div id='" + paramName + "_position_" + index + "'></div>";
    outputStr += "</div>";
    outputStr += "</td>";
    outputStr += "</tr>";
    
    //Adding occupancy validation line
    outputStr += "<tr>";
    outputStr += "<td colspan='7'>";
    outputStr += "<div id='" + paramName + "_occupancy_" + index + "' class='errorLabel'>";
    outputStr += "";
    outputStr += "</div>";
    outputStr += "</td>";
    outputStr += "</tr>";

    outputStr += "</table>";
    return outputStr;
}


function showHideSearchCondition(tablename){
  
     if (document.getElementById) {
         var searchConditionName = "search_condition_" + tablename + "";
         var switch_id = document.getElementById(searchConditionName);
         
         if(switch_id.className==='show'){
             switch_id.className = 'hide';
             menu_status[searchConditionName] = 'hide';
         }else{        
             switch_id.className = 'show';
             menu_status[searchConditionName] = 'show';
         }
     }
     return; 
 }
 
 function showHideSearchParameter(tablename){
  
     if (document.getElementById) {
         var searchParameterName = "search_parameter_" + tablename + "";
         var switch_id = document.getElementById(searchParameterName);
         
         if(switch_id.className==='show'){
             switch_id.className = 'hide';
             menu_status[searchParameterName] = 'hide';
         }else{        
             switch_id.className = 'show';
             menu_status[searchParameterName] = 'show';
         }
     }
     return; 
 }
 
 function showSearchOptions(paramname){
     
     //Use this function to expose the hidden five extras (for text and menu only)
     
 }
 
 function showCenterDistribution(){
     
     if (document.getElementById) {
         var idName = "center_distribution_summary";
         var switch_id = document.getElementById(idName);
         
         if(switch_id.className==='show'){
             switch_id.className = 'hide';
             menu_status[idName] = 'hide';
         }else{        
             switch_id.className = 'show';
             menu_status[idName] = 'show';
         }
     }
     return; 
 }
 
 function showCountryDistribution(){
     
     if (document.getElementById) {
         var idName = "country_distribution_summary";
         var switch_id = document.getElementById(idName);
         
         if(switch_id.className==='show'){
             switch_id.className = 'hide';
             menu_status[idName] = 'hide';
         }else{        
             switch_id.className = 'show';
             menu_status[idName] = 'show';
         }
     }
     return; 
 }
 
 function showDetailedRecordOutput(){
     
     if (document.getElementById) {
         var idName = "detailed_record_output";
         var switch_id = document.getElementById(idName);
         
         if(switch_id.className==='show'){
             switch_id.className = 'hide';
             menu_status[idName] = 'hide';
         }else{        
             switch_id.className = 'show';
             menu_status[idName] = 'show';
         }
     }
     return; 
 }
 
 function showQuerySummary(){
     
     if (document.getElementById) {
         var idName = "query_summary";
         var switch_id = document.getElementById(idName);
         
         if(switch_id.className==='show'){
             switch_id.className = 'hide';
             menu_status[idName] = 'hide';
         }else{        
             switch_id.className = 'show';
             menu_status[idName] = 'show';
         }
     }
     return; 
     
}
 
 function validateSearchForm(inputForm, formName){
     //window.alert("validateSearchForm");
     
     var searchFormValid = false;
     var i = 0;
     while(i<(inputForm.elements.length)){
         
         var paramName = inputForm.elements[i].name;        
         
         //Test to see if the input is a viewparameter input control
         var searchParamStr = /viewparam_/mi;
         if(searchParamStr.test(paramName)){
 
             var paramValue = document.getElementsByName(paramName);
             for (var j = 0; j < paramValue.length; j++) {
                 var item = paramValue[j];  
                 if(item.checked){
                     searchFormValid = true;
                 }
             }            
         }
         i++;
     }            
     
     if(!searchFormValid){
         document.getElementById("" + formName).innerHTML="<div class='errorLabel'>*Please select at least one parameter to view</div>";
     }    
     return searchFormValid         
 }
 
 function showAdditionalParamButton(boolParamName, index){
     
     var paramLineId = document.getElementById(boolParamName + "_extraline");
     index = index + 1;
     
     var outputHtml = "<table><tr>";     
     var oldHtml = paramLineId.innerHTML;
     outputHtml += oldHtml;
     
     if(index < 5){
        outputHtml += "<td>TEST " + index + " <a href=\"javascript:showAdditionalParamButton('" + boolParamName + "'," + index + ");\">+</a></td>";
     }else if(index == 5){
        outputHtml += "<td>TEST " + index + "</td>";
     }else{
        outputHtml += "<td></td>";
     }
     
     outputHtml += "</tr></table>";
     paramLineId.innerHTML=outputHtml;
     
     return true;
 }
 
