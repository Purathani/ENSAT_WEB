---- ENSAT PARAMETER TABLE (updated 17/05/12)

USE ensat_parameters;

DROP TABLE Parameter;
DROP TABLE Menu;
DROP TABLE MenuOption;

-- Parameter table
CREATE TABLE Parameter (
	param_id		INTEGER AUTO_INCREMENT NOT NULL,
	param_name		VARCHAR(100),
	param_type		VARCHAR(100),
	param_text_size		INTEGER,
	param_label		VARCHAR(1000),
	param_order_id		INTEGER,
	menu			INTEGER,
	param_sub_param		INTEGER,
	param_table		VARCHAR(100),
	PRIMARY KEY(param_id),
	FOREIGN KEY(menu) REFERENCES Menu(menu_id)
);

-- Menu table
CREATE TABLE Menu (
	menu_id			INTEGER AUTO_INCREMENT NOT NULL,
	menu_name		VARCHAR(100),
	menu_type		VARCHAR(1),
	PRIMARY KEY(menu_id)
);


-- MenuOption table
CREATE TABLE MenuOption (
	option_id		INTEGER AUTO_INCREMENT NOT NULL,
	option_name		VARCHAR(100),
	option_menu_id		INTEGER,	
	PRIMARY KEY(option_id),
	FOREIGN KEY(option_menu_id) REFERENCES Menu(menu_id)	
);


INSERT INTO Parameter VALUES(1,'dob_year','text',4,'Year of Birth',1,0,0,'Identification');
INSERT INTO Parameter VALUES(2,'sex','menu',0,'Sex',2,1,0,'Identification');
INSERT INTO Parameter VALUES(3,'center_id','menu',0,'Center ID',3,2,9,'Identification');
INSERT INTO Parameter VALUES(4,'investigator_name','text',30,'Referral Doctor',4,0,0,'Identification');
INSERT INTO Parameter VALUES(5,'investigator_email','text',30,'Email',5,0,0,'Identification');
INSERT INTO Parameter VALUES(6,'date_ensat_reg','date',0,'Date of ENSAT Registration',6,0,0,'Identification');
INSERT INTO Parameter VALUES(7,'signed_consent','menu',0,'Level of consent for clinical research<br/>(<em>Local, National, ENSAT, International</em>)',7,3,0,'Identification');
INSERT INTO Parameter VALUES(8,'eurine_act_inclusion','menu',0,'Will this patient be included in the EURINE-ACT study <input type=\"button\" name=\"clarification_eurine_act\" value=\"?\" onclick=\"return clarification(this.name);\"/>',8,4,0,'Identification');

UPDATE Parameter SET param_type='dynamicmenuonload' WHERE param_name='center_id';
UPDATE Parameter SET menu=0 WHERE param_name='center_id';
UPDATE Parameter SET param_type='text_only' WHERE param_name='investigator_name' OR param_name='investigator_email';

INSERT INTO Parameter VALUES(9,'date_of_diagnosis','date',0,'Date of diagnosis',1,0,0,'ACC_DiagnosticProcedures');
INSERT INTO Parameter VALUES(10,'disease_status','menu',0,'Disease status',2,5,0,'ACC_DiagnosticProcedures');
INSERT INTO Parameter VALUES(11,'modality_of_diagnosis','menu',0,'Modality of diagnosis',3,6,0,'ACC_DiagnosticProcedures');
INSERT INTO Parameter VALUES(12,'height','number',3,'Height',4,0,0,'ACC_DiagnosticProcedures');
INSERT INTO Parameter VALUES(13,'weight','number',3,'Weight',5,0,0,'ACC_DiagnosticProcedures');
INSERT INTO Parameter VALUES(14,'symptoms_diag_tumor_mass','menu',0,'Symptoms related to tumor mass',6,4,0,'ACC_DiagnosticProcedures');
INSERT INTO Parameter VALUES(15,'symptoms_incidental','menu',0,'Incidentally detected',6,4,0,'ACC_DiagnosticProcedures');
INSERT INTO Parameter VALUES(16,'symptoms_paraneoplastic','menu',0,'Related to unspecific paraneoplastic symptoms',7,4,0,'ACC_DiagnosticProcedures');
INSERT INTO Parameter VALUES(17,'symptoms_endocrine','menu',0,'Symptoms related to hormonal secretion',8,4,0,'ACC_DiagnosticProcedures');
INSERT INTO Parameter VALUES(18,'cushings_syndrome','menu',0,'Cushing\'s syndrome',9,7,0,'ACC_DiagnosticProcedures');
INSERT INTO Parameter VALUES(19,'virilisation','menu',0,'Virilisation',10,7,0,'ACC_DiagnosticProcedures');
INSERT INTO Parameter VALUES(20,'feminization','menu',0,'Feminization',11,7,0,'ACC_DiagnosticProcedures');
INSERT INTO Parameter VALUES(21,'mineralocorticoid_excess','menu',0,'Mineralocorticoid excess',12,7,0,'ACC_DiagnosticProcedures');
INSERT INTO Parameter VALUES(22,'hypertension','menu',0,'Hypertension',13,7,0,'ACC_DiagnosticProcedures');
INSERT INTO Parameter VALUES(23,'hypokalemia','menu',0,'Hypokalemia',14,7,0,'ACC_DiagnosticProcedures');
INSERT INTO Parameter VALUES(24,'diabetes','menu',0,'Diabetes',15,7,0,'ACC_DiagnosticProcedures');
INSERT INTO Parameter VALUES(25,'hormonal_hypersecretion','menu',0,'Hormonal hypersecretion',16,7,0,'ACC_DiagnosticProcedures');
INSERT INTO Parameter VALUES(26,'glucocorticoids','menu',0,'Glucocorticoids',17,7,0,'ACC_DiagnosticProcedures');
INSERT INTO Parameter VALUES(27,'androgens','menu',0,'Androgens',18,7,0,'ACC_DiagnosticProcedures');
INSERT INTO Parameter VALUES(28,'estrogens','menu',0,'Estrogens',19,7,0,'ACC_DiagnosticProcedures');
INSERT INTO Parameter VALUES(29,'mineralocorticoids','menu',0,'Mineralocorticoids',20,7,0,'ACC_DiagnosticProcedures');
INSERT INTO Parameter VALUES(30,'precursor_secretion','menu',0,'Precursor secretion',21,7,0,'ACC_DiagnosticProcedures');
INSERT INTO Parameter VALUES(31,'acc_during_pregnancy','menu',0,'ACC during pregnancy',22,7,0,'ACC_DiagnosticProcedures');
INSERT INTO Parameter VALUES(32,'other_malignancies','menu',0,'Associated malignancy',23,7,0,'ACC_DiagnosticProcedures');
INSERT INTO Parameter VALUES(33,'which_malignancies','text',30,'Specific malignancies',24,0,0,'ACC_DiagnosticProcedures');

INSERT INTO Parameter VALUES(34,'site_of_adrenal_tumor','menu',0,'Site of adrenal tumor',25,8,0,'ACC_TumorStaging');
INSERT INTO Parameter VALUES(35,'information_based_on','menu',0,'Information based on',26,9,0,'ACC_TumorStaging');
INSERT INTO Parameter VALUES(36,'size_of_adrenal_tumor','number',3,'Size of adrenal tumor (<em>mm</em>)',27,0,0,'ACC_TumorStaging');
INSERT INTO Parameter VALUES(37,'regional_lymph_nodes','menu',0,'Regional lymph nodes positive',28,10,0,'ACC_TumorStaging');
INSERT INTO Parameter VALUES(38,'tumor_infiltration_adipose','menu',0,'Extracapsular local invasion to adipose tissues',29,10,0,'ACC_TumorStaging');
INSERT INTO Parameter VALUES(39,'tumor_invasion_adjacent','menu',0,'Extracapsular local invasion to adjacent organs',30,10,0,'ACC_TumorStaging');
INSERT INTO Parameter VALUES(40,'tumor_thrombus_vena_renal','menu',0,'Tumor in vena cava or vena renalis',31,10,0,'ACC_TumorStaging');
INSERT INTO Parameter VALUES(41,'distant_metastases','menu',0,'Distant metastases',32,4,0,'ACC_TumorStaging');
INSERT INTO Parameter VALUES(42,'bone','menu',0,'Bone',33,11,0,'ACC_TumorStaging');
INSERT INTO Parameter VALUES(43,'liver','menu',0,'Liver',34,11,0,'ACC_TumorStaging');
INSERT INTO Parameter VALUES(44,'lung','menu',0,'Lung',35,11,0,'ACC_TumorStaging');
INSERT INTO Parameter VALUES(45,'abdomen_lymph_nodes','menu',0,'Abdomen lymph nodes',36,11,0,'ACC_TumorStaging');
INSERT INTO Parameter VALUES(46,'other_metastases','text',30,'Other metastases',37,0,0,'ACC_TumorStaging');

UPDATE Parameter SET param_sub_param=17 WHERE param_id=18 OR param_id=19 OR param_id=20 OR param_id=21;
UPDATE Parameter SET param_sub_param=25 WHERE param_id=26 OR param_id=27 OR param_id=28 OR param_id=29 OR param_id=30;
UPDATE Parameter SET param_sub_param=41 WHERE param_id=42 OR param_id=43 OR param_id=44 OR param_id=45;
UPDATE Parameter SET param_sub_param=32 WHERE param_id=33;

INSERT INTO Parameter VALUES(47,'history_of_hypertension','menu',0,'History of hypertension',1,4,0,'Pheo_PatientHistory');
INSERT INTO Parameter VALUES(48,'year_of_hypertension_diagnosis','text',4,'Year of diagnosis of hypertension',2,0,0,'Pheo_PatientHistory');
INSERT INTO Parameter VALUES(49,'height_at_time_consent','number',3,'Height at the time of consent (<em>cm</em>)',3,0,0,'Pheo_PatientHistory');
INSERT INTO Parameter VALUES(50,'weight_at_time_consent','number',3,'Weight at the time of consent (<em>kg</em>)',4,0,0,'Pheo_PatientHistory');
INSERT INTO Parameter VALUES(51,'systolic_bp_at_time_consent','number',3,'Systolic BP at the time of consent (<em>mmHg</em>)',5,0,0,'Pheo_PatientHistory');
INSERT INTO Parameter VALUES(52,'diastolic_bp_at_time_consent','number',3,'Diastolic BP at the time of consent (<em>mmHg</em>)',6,0,0,'Pheo_PatientHistory');
INSERT INTO Parameter VALUES(53,'pheo_operation_before_consent_date','menu',0,'PHPGL operation before date of consent',7,4,0,'Pheo_PatientHistory');
INSERT INTO Parameter VALUES(54,'residual_disease','menu',0,'Residual disease',8,4,0,'Pheo_PatientHistory');
INSERT INTO Parameter VALUES(55,'disease_metastatic','menu',0,'Disease metastatic',9,4,0,'Pheo_PatientHistory');
INSERT INTO Parameter VALUES(56,'multiple_tumors','menu',0,'Multiple primary tumors',10,4,0,'Pheo_PatientHistory');
INSERT INTO Parameter VALUES(57,'relatives_with_tumors','number',3,'Number of first degree relatives with tumors',11,0,0,'Pheo_PatientHistory');
INSERT INTO Parameter VALUES(58,'doc_genetic_disease','menu',0,'Documented genetic disease',12,4,0,'Pheo_PatientHistory');
INSERT INTO Parameter VALUES(59,'phenotypic_diagnosis','menu',0,'Phenotypic diagnosis',13,12,0,'Pheo_PatientHistory');
INSERT INTO Parameter VALUES(60,'history_non_ppgl_tumor','menu',0,'History of non-PHPGL tumors',14,4,0,'Pheo_PatientHistory');
INSERT INTO Parameter VALUES(61,'system_organ','menu',0,'System or organ',15,13,0,'Pheo_PatientHistory');
INSERT INTO Parameter VALUES(62,'pheo_comment','text',30,'Comment',16,0,0,'Pheo_PatientHistory');
INSERT INTO Parameter VALUES(63,'presentation_first_tumor','menu',0,'Presentation at diagnosis of first tumor',17,14,0,'Pheo_PatientHistory');

UPDATE Parameter SET param_sub_param=47 WHERE param_id=48;
UPDATE Parameter SET param_sub_param=53 WHERE param_id=54 OR param_id=55 OR param_id=56;
UPDATE Parameter SET param_sub_param=58 WHERE param_id=59;
UPDATE Parameter SET param_sub_param=60 WHERE param_id=61;

INSERT INTO Parameter VALUES(64,'year_of_diagnosis','text',4,'Year of diagnosis of PAL',1,0,0,'APA_PatientHistory');
INSERT INTO Parameter VALUES(65,'hypertension_presentation','menu',0,'Hypertension at presentation',2,4,0,'APA_PatientHistory');
INSERT INTO Parameter VALUES(66,'hypertension_year','text',4,'Year of diagnosis of hypertension',3,0,0,'APA_PatientHistory');
INSERT INTO Parameter VALUES(67,'first_degree_relatives_hypertension','number',3,'Number of first degree relatives with hypertension',4,0,0,'APA_PatientHistory');
INSERT INTO Parameter VALUES(68,'first_degree_relatives_pal','number',3,'Number of first degree relatives with PAL',5,0,0,'APA_PatientHistory');
INSERT INTO Parameter VALUES(69,'doc_genetic_disease','menu',0,'Documented genetic disease',6,15,0,'APA_PatientHistory');
INSERT INTO Parameter VALUES(70,'lowest_kalemia','number',4,'Lowest kalemia (<em>mmol/l</em>)',7,0,0,'APA_PatientHistory');
INSERT INTO Parameter VALUES(71,'apa_comment','text',30,'Comment',8,0,0,'APA_PatientHistory');

UPDATE Parameter SET param_sub_param=65 WHERE param_name='hypertension_year' AND param_table='APA_PatientHistory';

INSERT INTO Parameter VALUES(72,'height','number',3,'Height (<em>cm</em>)',1,0,0,'NAPACA_DiagnosticProcedures');
INSERT INTO Parameter VALUES(73,'weight','number',3,'Weight (<em>kg</em>)',2,0,0,'NAPACA_DiagnosticProcedures');
INSERT INTO Parameter VALUES(74,'bmi','number',3,'BMI',3,0,0,'NAPACA_DiagnosticProcedures');
INSERT INTO Parameter VALUES(75,'year_of_diagnosis','number',4,'Year of diagnosis of NAPACA (<em>yyyy</em>)',4,0,0,'NAPACA_DiagnosticProcedures');
INSERT INTO Parameter VALUES(76,'month_of_diagnosis','menu',0,'Month of diagnosis of NAPACA',5,16,0,'NAPACA_DiagnosticProcedures');
INSERT INTO Parameter VALUES(77,'symptoms_incidental','menu',0,'Tumor found incidentally',6,4,0,'NAPACA_DiagnosticProcedures');
INSERT INTO Parameter VALUES(78,'symptoms_endocrine','menu',0,'Symptoms related to hormonal secretion',7,7,0,'NAPACA_DiagnosticProcedures');
INSERT INTO Parameter VALUES(79,'cushings_syndrome','menu',0,'Cushing\'s syndrome',8,10,0,'NAPACA_DiagnosticProcedures');
INSERT INTO Parameter VALUES(80,'virilisation','menu',0,'Virilisation',9,10,0,'NAPACA_DiagnosticProcedures');
INSERT INTO Parameter VALUES(81,'feminization','menu',0,'Feminization',10,10,0,'NAPACA_DiagnosticProcedures');
INSERT INTO Parameter VALUES(82,'mineralocorticoid_excess','menu',0,'Mineralocorticoid excess',11,10,0,'NAPACA_DiagnosticProcedures');
INSERT INTO Parameter VALUES(83,'hypertension_presentation','menu',0,'Hypertension at presentation',12,7,0,'NAPACA_DiagnosticProcedures');
INSERT INTO Parameter VALUES(84,'hypertension_year','number',4,'Year of diagnosis (<em>yyyy</em>)',13,0,0,'NAPACA_DiagnosticProcedures');
INSERT INTO Parameter VALUES(85,'diabetestype2_presentation','menu',0,'Diabetes type-2 at presentation',14,7,0,'NAPACA_DiagnosticProcedures');
INSERT INTO Parameter VALUES(86,'diabetestype2_year','number',4,'Year of diagnosis (<em>yyyy</em>)',15,0,0,'NAPACA_DiagnosticProcedures');
INSERT INTO Parameter VALUES(87,'dyslipidaemia_presentation','menu',0,'Dyslipidaemia at presentation',16,7,0,'NAPACA_DiagnosticProcedures');
INSERT INTO Parameter VALUES(88,'dyslipidaemia_year','number',4,'Year of diagnosis (<em>yyyy</em>)',17,0,0,'NAPACA_DiagnosticProcedures');
INSERT INTO Parameter VALUES(89,'osteoporosis_presentation','menu',0,'Osteoporosis at presentation',18,7,0,'NAPACA_DiagnosticProcedures');
INSERT INTO Parameter VALUES(90,'osteoporosis_year','number',4,'Year of diagnosis (<em>yyyy</em>)',19,0,0,'NAPACA_DiagnosticProcedures');
INSERT INTO Parameter VALUES(91,'prev_cardio_events','menu',0,'Previous cardiovascular events',20,7,0,'NAPACA_DiagnosticProcedures');
INSERT INTO Parameter VALUES(92,'prev_cardio_year1','number',4,'Year of event 1 (<em>yyyy</em>)',21,0,0,'NAPACA_DiagnosticProcedures');
INSERT INTO Parameter VALUES(93,'prev_cardio_year2','number',4,'Year of event 2 (<em>yyyy</em>)',22,0,0,'NAPACA_DiagnosticProcedures');
INSERT INTO Parameter VALUES(94,'antidiabetic_drugs','menu',0,'Currently receiving anti-diabetic drugs',23,7,0,'NAPACA_DiagnosticProcedures');
INSERT INTO Parameter VALUES(95,'lipidlowering_drugs','menu',0,'Currently receiving lipid-lowering drugs',24,7,0,'NAPACA_DiagnosticProcedures');
INSERT INTO Parameter VALUES(96,'osteoporosis_drugs','menu',0,'Currently receiving osteoporosis drugs',25,7,0,'NAPACA_DiagnosticProcedures');
INSERT INTO Parameter VALUES(97,'antihypertensive_drugs','menu',0,'Currently receiving anti-hypertensive drugs',26,7,0,'NAPACA_DiagnosticProcedures');
INSERT INTO Parameter VALUES(98,'gluco_serum_cortisol','menu',0,'Serum cortisol after 1mg Dex overnight',27,17,0,'NAPACA_DiagnosticProcedures');
INSERT INTO Parameter VALUES(99,'gluco_serum_cortisol_specific','number',3,'Specific value',28,0,0,'NAPACA_DiagnosticProcedures');
INSERT INTO Parameter VALUES(100,'gluco_plasma_acth','menu',0,'Baseline plasma ACTH',29,18,0,'NAPACA_DiagnosticProcedures');
INSERT INTO Parameter VALUES(101,'gluco_plasma_acth_specific','number',3,'Specific value',30,0,0,'NAPACA_DiagnosticProcedures');
INSERT INTO Parameter VALUES(102,'gluco_urinary_free_cortisol','menu',0,'Urinary free cortisol',31,18,0,'NAPACA_DiagnosticProcedures');
INSERT INTO Parameter VALUES(103,'gluco_urinary_free_cortisol_specific','number',3,'Specific value',32,0,0,'NAPACA_DiagnosticProcedures');
INSERT INTO Parameter VALUES(104,'gluco_urinary_free_method','menu',0,'Urinary free method',33,18,0,'NAPACA_DiagnosticProcedures');
INSERT INTO Parameter VALUES(105,'mineralo_plasma_renin_activity','menu',0,'Random plasma renin activity (<em>ng/ml/h</em>)',34,18,0,'NAPACA_DiagnosticProcedures');
INSERT INTO Parameter VALUES(106,'mineralo_plasma_renin_conc','menu',0,'Random plasma renin concentration (<em>ng/l</em>)',35,18,0,'NAPACA_DiagnosticProcedures');
INSERT INTO Parameter VALUES(107,'mineralo_serum_aldosterone','menu',0,'Random serum aldosterone (<em>ng/l</em>)',36,18,0,'NAPACA_DiagnosticProcedures');
INSERT INTO Parameter VALUES(108,'other_steroid_17hydroxyprogesterone','menu',0,'Serum 17-Hydroxyprogesterone',37,18,0,'NAPACA_DiagnosticProcedures');
INSERT INTO Parameter VALUES(109,'other_steroid_17hydroxyprogesterone_specific','number',3,'Specific value',38,0,0,'NAPACA_DiagnosticProcedures');
INSERT INTO Parameter VALUES(110,'other_steroid_serum_dheas','menu',0,'Serum DHEAS',39,18,0,'NAPACA_DiagnosticProcedures');
INSERT INTO Parameter VALUES(111,'other_steroid_serum_dheas_specific','number',3,'Specific value',40,0,0,'NAPACA_DiagnosticProcedures');
INSERT INTO Parameter VALUES(112,'catechol_urinary_free_excretion','menu',0,'Urinary free catecholamine excretion',41,18,0,'NAPACA_DiagnosticProcedures');
INSERT INTO Parameter VALUES(113,'catechol_urinary_metanephrine_excretion','menu',0,'Urinary metanephrine excretion',42,18,0,'NAPACA_DiagnosticProcedures');
INSERT INTO Parameter VALUES(114,'catechol_plasma_metanephrines','menu',0,'Plasma metanephrines',43,18,0,'NAPACA_DiagnosticProcedures');
INSERT INTO Parameter VALUES(115,'other_malignancies','menu',0,'Associated malignancy',44,7,0,'NAPACA_DiagnosticProcedures');
INSERT INTO Parameter VALUES(116,'which_malignancies','text',30,'Specific malignancies',45,0,0,'NAPACA_DiagnosticProcedures');
INSERT INTO Parameter VALUES(117,'year_other_maligs','number',4,'Year of diagnosis (<em>yyyy</em>)',46,0,0,'NAPACA_DiagnosticProcedures');

UPDATE Parameter SET param_sub_param=78 WHERE param_id=79 OR param_id=80 OR param_id=81 OR param_id=82;
UPDATE Parameter SET param_sub_param=83 WHERE param_id=84;
UPDATE Parameter SET param_sub_param=85 WHERE param_id=86;
UPDATE Parameter SET param_sub_param=87 WHERE param_id=88;
UPDATE Parameter SET param_sub_param=89 WHERE param_id=90;
UPDATE Parameter SET param_sub_param=91 WHERE param_id=92 OR param_id=93;
UPDATE Parameter SET param_sub_param=98 WHERE param_id=99;
UPDATE Parameter SET param_sub_param=100 WHERE param_id=101;
UPDATE Parameter SET param_sub_param=102 WHERE param_id=103;
UPDATE Parameter SET param_sub_param=108 WHERE param_id=109;
UPDATE Parameter SET param_sub_param=110 WHERE param_id=111;
UPDATE Parameter SET param_sub_param=115 WHERE param_id=116 OR param_id=117;

INSERT INTO Parameter VALUES(118,'biomaterial_date','date',0,'Biomaterial Date',1,0,0,'ACC_Biomaterial');
INSERT INTO Parameter VALUES(119,'associated_study','menu',0,'Associated Study',2,19,0,'ACC_Biomaterial');
INSERT INTO Parameter VALUES(120,'associated_study_phase_visit','menu',0,'Associated Study',3,20,0,'ACC_Biomaterial');
INSERT INTO Parameter VALUES(121,'tumor_tissue_frozen','menu',0,'Tumor Tissue (Frozen)',4,4,0,'ACC_Biomaterial');
INSERT INTO Parameter VALUES(122,'tumor_tissue_ensat_sop','menu',0,'Following ENSAT SOP',5,7,120,'ACC_Biomaterial');
INSERT INTO Parameter VALUES(123,'tumor_tissue_paraffin','menu',0,'Tumor Tissue (Paraffin)',6,4,0,'ACC_Biomaterial');
INSERT INTO Parameter VALUES(124,'tumor_tissue_dna','menu',0,'Tumor Tissue (DNA)',7,4,0,'ACC_Biomaterial');
INSERT INTO Parameter VALUES(125,'leukocyte_dna','menu',0,'Leukocyte DNA',8,4,0,'ACC_Biomaterial');
INSERT INTO Parameter VALUES(126,'plasma','menu',0,'EDTA Plasma',9,4,0,'ACC_Biomaterial');
INSERT INTO Parameter VALUES(127,'heparin_plasma','menu',0,'Heparin Plasma',10,4,0,'ACC_Biomaterial');
INSERT INTO Parameter VALUES(128,'serum','menu',0,'Serum',11,4,0,'ACC_Biomaterial');
INSERT INTO Parameter VALUES(129,'24h_urine','menu',0,'24h Urine',12,4,0,'ACC_Biomaterial');
INSERT INTO Parameter VALUES(130,'24h_urine_vol','text',3,'24h Urine Volume (ml)',13,0,0,'ACC_Biomaterial');
INSERT INTO Parameter VALUES(131,'spot_urine','menu',0,'Spot Urine',14,4,0,'ACC_Biomaterial');
INSERT INTO Parameter VALUES(132,'normal_tissue','menu',0,'Normal Tissue (Frozen)',15,4,0,'ACC_Biomaterial');
INSERT INTO Parameter VALUES(133,'normal_tissue_options','menu',0,'Specific Tissue (Frozen)',16,21,131,'ACC_Biomaterial');
INSERT INTO Parameter VALUES(134,'normal_tissue_paraffin','menu',0,'Normal Tissue (Paraffin)',17,4,0,'ACC_Biomaterial');
INSERT INTO Parameter VALUES(135,'normal_tissue_paraffin_options','menu',0,'Specific Tissue (Paraffin)',18,21,133,'ACC_Biomaterial');
INSERT INTO Parameter VALUES(136,'normal_tissue_dna','menu',0,'Normal Tissue (DNA)',19,4,0,'ACC_Biomaterial');
INSERT INTO Parameter VALUES(137,'normal_tissue_dna_options','menu',0,'Specific Tissue (DNA)',20,21,135,'ACC_Biomaterial');

INSERT INTO Parameter VALUES(138,'biomaterial_date','date',0,'Biomaterial Date',1,0,0,'Pheo_Biomaterial');
INSERT INTO Parameter VALUES(139,'associated_study','menu',0,'Associated Study',2,19,0,'Pheo_Biomaterial');
INSERT INTO Parameter VALUES(140,'associated_study_phase_visit','menu',0,'Associated Study',3,20,0,'Pheo_Biomaterial');
INSERT INTO Parameter VALUES(141,'tumor_tissue_frozen','menu',0,'Tumor Tissue (Frozen)',4,4,0,'Pheo_Biomaterial');
INSERT INTO Parameter VALUES(142,'tumor_tissue_ensat_sop','menu',0,'Following ENSAT SOP',5,7,120,'Pheo_Biomaterial');
INSERT INTO Parameter VALUES(143,'tumor_tissue_paraffin','menu',0,'Tumor Tissue (Paraffin)',6,4,0,'Pheo_Biomaterial');
INSERT INTO Parameter VALUES(144,'tumor_tissue_dna','menu',0,'Tumor Tissue (DNA)',7,4,0,'Pheo_Biomaterial');
INSERT INTO Parameter VALUES(145,'leukocyte_dna','menu',0,'Leukocyte DNA',8,4,0,'Pheo_Biomaterial');
INSERT INTO Parameter VALUES(146,'plasma','menu',0,'EDTA Plasma',9,4,0,'Pheo_Biomaterial');
INSERT INTO Parameter VALUES(147,'heparin_plasma','menu',0,'Heparin Plasma',10,4,0,'Pheo_Biomaterial');
INSERT INTO Parameter VALUES(148,'serum','menu',0,'Serum',11,4,0,'Pheo_Biomaterial');
INSERT INTO Parameter VALUES(149,'24h_urine','menu',0,'24h Urine',12,4,0,'Pheo_Biomaterial');
INSERT INTO Parameter VALUES(150,'24h_urine_vol','text',3,'24h Urine Volume (ml)',13,0,0,'Pheo_Biomaterial');
INSERT INTO Parameter VALUES(151,'spot_urine','menu',0,'Spot Urine',14,4,0,'Pheo_Biomaterial');
INSERT INTO Parameter VALUES(152,'normal_tissue','menu',0,'Normal Tissue (Frozen)',15,4,0,'Pheo_Biomaterial');
INSERT INTO Parameter VALUES(153,'normal_tissue_options','menu',0,'Specific Tissue (Frozen)',16,21,131,'Pheo_Biomaterial');
INSERT INTO Parameter VALUES(154,'normal_tissue_paraffin','menu',0,'Normal Tissue (Paraffin)',17,4,0,'Pheo_Biomaterial');
INSERT INTO Parameter VALUES(155,'normal_tissue_paraffin_options','menu',0,'Specific Tissue (Paraffin)',18,21,133,'Pheo_Biomaterial');
INSERT INTO Parameter VALUES(156,'normal_tissue_dna','menu',0,'Normal Tissue (DNA)',19,4,0,'Pheo_Biomaterial');
INSERT INTO Parameter VALUES(157,'normal_tissue_dna_options','menu',0,'Specific Tissue (DNA)',20,21,135,'Pheo_Biomaterial');
INSERT INTO Parameter VALUES(158,'whole_blood','menu',0,'Whole Blood',21,4,0,'Pheo_Biomaterial');

INSERT INTO Parameter VALUES(159,'biomaterial_date','date',0,'Biomaterial Date',1,0,0,'NAPACA_Biomaterial');
INSERT INTO Parameter VALUES(160,'associated_study','menu',0,'Associated Study',2,19,0,'NAPACA_Biomaterial');
INSERT INTO Parameter VALUES(161,'associated_study_phase_visit','menu',0,'Associated Study',3,20,0,'NAPACA_Biomaterial');
INSERT INTO Parameter VALUES(162,'tumor_tissue_frozen','menu',0,'Tumor Tissue (Frozen)',4,4,0,'NAPACA_Biomaterial');
INSERT INTO Parameter VALUES(163,'tumor_tissue_ensat_sop','menu',0,'Following ENSAT SOP',5,7,120,'NAPACA_Biomaterial');
INSERT INTO Parameter VALUES(164,'tumor_tissue_paraffin','menu',0,'Tumor Tissue (Paraffin)',6,4,0,'NAPACA_Biomaterial');
INSERT INTO Parameter VALUES(165,'tumor_tissue_dna','menu',0,'Tumor Tissue (DNA)',7,4,0,'NAPACA_Biomaterial');
INSERT INTO Parameter VALUES(166,'leukocyte_dna','menu',0,'Leukocyte DNA',8,4,0,'NAPACA_Biomaterial');
INSERT INTO Parameter VALUES(167,'plasma','menu',0,'EDTA Plasma',9,4,0,'NAPACA_Biomaterial');
INSERT INTO Parameter VALUES(168,'heparin_plasma','menu',0,'Heparin Plasma',10,4,0,'NAPACA_Biomaterial');
INSERT INTO Parameter VALUES(169,'serum','menu',0,'Serum',11,4,0,'NAPACA_Biomaterial');
INSERT INTO Parameter VALUES(170,'24h_urine','menu',0,'24h Urine',12,4,0,'NAPACA_Biomaterial');
INSERT INTO Parameter VALUES(171,'24h_urine_vol','text',3,'24h Urine Volume (ml)',13,0,0,'NAPACA_Biomaterial');
INSERT INTO Parameter VALUES(172,'spot_urine','menu',0,'Spot Urine',14,4,0,'NAPACA_Biomaterial');
INSERT INTO Parameter VALUES(173,'normal_tissue','menu',0,'Normal Tissue (Frozen)',15,4,0,'NAPACA_Biomaterial');
INSERT INTO Parameter VALUES(174,'normal_tissue_options','menu',0,'Specific Tissue (Frozen)',16,21,131,'NAPACA_Biomaterial');
INSERT INTO Parameter VALUES(175,'normal_tissue_paraffin','menu',0,'Normal Tissue (Paraffin)',17,4,0,'NAPACA_Biomaterial');
INSERT INTO Parameter VALUES(176,'normal_tissue_paraffin_options','menu',0,'Specific Tissue (Paraffin)',18,21,133,'NAPACA_Biomaterial');
INSERT INTO Parameter VALUES(177,'normal_tissue_dna','menu',0,'Normal Tissue (DNA)',19,4,0,'NAPACA_Biomaterial');
INSERT INTO Parameter VALUES(178,'normal_tissue_dna_options','menu',0,'Specific Tissue (DNA)',20,21,135,'NAPACA_Biomaterial');

INSERT INTO Parameter VALUES(179,'biomaterial_date','date',0,'Biomaterial Date',1,0,0,'APA_Biomaterial');
INSERT INTO Parameter VALUES(180,'associated_study','menu',0,'Associated Study',2,19,0,'APA_Biomaterial');
INSERT INTO Parameter VALUES(181,'associated_study_phase_visit','menu',0,'Associated Study',3,20,0,'APA_Biomaterial');
INSERT INTO Parameter VALUES(182,'tumor_tissue_frozen','menu',0,'Tumor Tissue (Frozen)',4,4,0,'APA_Biomaterial');
INSERT INTO Parameter VALUES(183,'tumor_tissue_ensat_sop','menu',0,'Following ENSAT SOP',5,7,120,'APA_Biomaterial');
INSERT INTO Parameter VALUES(184,'tumor_tissue_paraffin','menu',0,'Tumor Tissue (Paraffin)',6,4,0,'APA_Biomaterial');
INSERT INTO Parameter VALUES(185,'tumor_tissue_dna','menu',0,'Tumor Tissue (DNA)',7,4,0,'APA_Biomaterial');
INSERT INTO Parameter VALUES(186,'leukocyte_dna','menu',0,'Leukocyte DNA',8,4,0,'APA_Biomaterial');
INSERT INTO Parameter VALUES(187,'plasma','menu',0,'EDTA Plasma',9,4,0,'APA_Biomaterial');
INSERT INTO Parameter VALUES(188,'heparin_plasma','menu',0,'Heparin Plasma',10,4,0,'APA_Biomaterial');
INSERT INTO Parameter VALUES(189,'serum','menu',0,'Serum',11,4,0,'APA_Biomaterial');
INSERT INTO Parameter VALUES(190,'24h_urine','menu',0,'24h Urine',12,4,0,'APA_Biomaterial');
INSERT INTO Parameter VALUES(191,'24h_urine_vol','text',3,'24h Urine Volume (ml)',13,0,0,'APA_Biomaterial');
INSERT INTO Parameter VALUES(192,'spot_urine','menu',0,'Spot Urine',14,4,0,'APA_Biomaterial');
INSERT INTO Parameter VALUES(193,'normal_tissue','menu',0,'Normal Tissue (Frozen)',15,4,0,'APA_Biomaterial');
INSERT INTO Parameter VALUES(194,'normal_tissue_options','menu',0,'Specific Tissue (Frozen)',16,21,131,'APA_Biomaterial');
INSERT INTO Parameter VALUES(195,'normal_tissue_paraffin','menu',0,'Normal Tissue (Paraffin)',17,4,0,'APA_Biomaterial');
INSERT INTO Parameter VALUES(196,'normal_tissue_paraffin_options','menu',0,'Specific Tissue (Paraffin)',18,21,133,'APA_Biomaterial');
INSERT INTO Parameter VALUES(197,'normal_tissue_dna','menu',0,'Normal Tissue (DNA)',19,4,0,'APA_Biomaterial');
INSERT INTO Parameter VALUES(198,'normal_tissue_dna_options','menu',0,'Specific Tissue (DNA)',20,21,135,'APA_Biomaterial');

INSERT INTO Parameter VALUES(199,'imaging_date','date',0,'Imaging Date',1,0,0,'NAPACA_Imaging');
INSERT INTO Parameter VALUES(200,'tumor_sites','menu',0,'Tumor Sites',2,22,0,'NAPACA_Imaging');
INSERT INTO Parameter VALUES(201,'right_adrenal_max_tumor','number',3,'Right adrenal - max tumor diameter (<em>mm</em>)',3,0,200,'NAPACA_Imaging');
INSERT INTO Parameter VALUES(202,'left_adrenal_max_tumor','number',3,'Left adrenal - max tumor diameter (<em>mm</em>)',4,0,200,'NAPACA_Imaging');
INSERT INTO Parameter VALUES(203,'imaging_of_tumor','menu',0,'CT data available',5,4,0,'NAPACA_Imaging');
INSERT INTO Parameter VALUES(204,'ct_tumor_density','menu',0,'CT: pre-contrast tumor density',6,23,203,'NAPACA_Imaging');
INSERT INTO Parameter VALUES(205,'ct_tumor_density_specific','number',3,'Specific value',7,0,204,'NAPACA_Imaging');
INSERT INTO Parameter VALUES(206,'ct_delay_contrast_washout','menu',0,'CT: delay in contrast washout',8,10,203,'NAPACA_Imaging');
INSERT INTO Parameter VALUES(207,'evidence_extra_adrenal','menu',0,'Evidence of extra-adrenal tumors',9,4,0,'NAPACA_Imaging');
INSERT INTO Parameter VALUES(208,'additional_imaging_performed','menu',0,'Additional imaging performed',10,4,0,'NAPACA_Imaging');
INSERT INTO Parameter VALUES(209,'mri_chemical_shift_analysis','menu',0,'MRI: chemical shift analysis',11,4,208,'NAPACA_Imaging');
INSERT INTO Parameter VALUES(210,'fdg_pet','menu',0,'FDG-PET',12,4,208,'NAPACA_Imaging');
INSERT INTO Parameter VALUES(211,'comment','text',20,'Comments',13,0,0,'NAPACA_Imaging');

INSERT INTO Parameter VALUES(212,'surgery_date','date',0,'Surgery Date',1,0,0,'NAPACA_Surgery');
INSERT INTO Parameter VALUES(213,'surgical_approach','menu',0,'Surgical Approach',2,24,0,'NAPACA_Surgery');

INSERT INTO Parameter VALUES(214,'pathology_date','date',0,'Pathology Date',1,0,0,'NAPACA_Pathology');
INSERT INTO Parameter VALUES(215,'pathologist_name','text',20,'Pathologist Name',2,0,0,'NAPACA_Pathology');
INSERT INTO Parameter VALUES(216,'pathologist_location','text',0,'Pathologist Location',3,0,0,'NAPACA_Pathology');
INSERT INTO Parameter VALUES(217,'pathology_diagnosis','menu',0,'Pathology Diagnosis',4,25,0,'NAPACA_Pathology');
INSERT INTO Parameter VALUES(218,'number_of_mitoses_exact','number',0,'Number of mitoses (<em>exact count per 50 HPF</em>)',5,0,0,'NAPACA_Pathology');
INSERT INTO Parameter VALUES(219,'ki67','number',0,'ki67 (<em>in %/50 HPF</em>)',6,0,0,'NAPACA_Pathology');
INSERT INTO Parameter VALUES(220,'weiss_score','text_only',0,'Weiss Score',7,0,0,'NAPACA_Pathology');
INSERT INTO Parameter VALUES(221,'nuclear_atypia','menu',0,'Nuclear Atypia',8,26,0,'NAPACA_Pathology');
INSERT INTO Parameter VALUES(222,'atypical_mitosis','menu',0,'Atypical Mitosis',9,26,0,'NAPACA_Pathology');
INSERT INTO Parameter VALUES(223,'spongiocytic_tumor_cells','menu',0,'Spongiocytic Tumor Cells',10,26,0,'NAPACA_Pathology');
INSERT INTO Parameter VALUES(224,'diffuse_architecture','menu',0,'Diffuse Architecture',11,26,0,'NAPACA_Pathology');
INSERT INTO Parameter VALUES(225,'venous_invasion','menu',0,'Venous Invasion',12,26,0,'NAPACA_Pathology');
INSERT INTO Parameter VALUES(226,'sinus_invasion','menu',0,'Sinus Invasion',13,26,0,'NAPACA_Pathology');
INSERT INTO Parameter VALUES(227,'capsular_invasion','menu',0,'Capsular Invasion',14,26,0,'NAPACA_Pathology');
INSERT INTO Parameter VALUES(228,'necrosis','menu',0,'Necrosis',15,26,0,'NAPACA_Pathology');
INSERT INTO Parameter VALUES(229,'number_of_mitoses_per5','menu',0,'Number of Mitoses (<em>> 5/50 HPF</em>)',16,26,0,'NAPACA_Pathology');

INSERT INTO Parameter VALUES(230,'followup_date','date',0,'Follow-Up Date',1,0,0,'NAPACA_FollowUp');
INSERT INTO Parameter VALUES(231,'followup_alive','menu',0,'Alive',2,4,0,'NAPACA_FollowUp');
INSERT INTO Parameter VALUES(232,'death_related_tumor','menu',0,'Death related to adrenal tumor',3,7,231,'NAPACA_FollowUp');
INSERT INTO Parameter VALUES(233,'date_of_death','date',0,'Date of death',4,0,231,'NAPACA_FollowUp');
INSERT INTO Parameter VALUES(234,'followup_imaging','menu',0,'Imaging',5,4,0,'NAPACA_FollowUp');
INSERT INTO Parameter VALUES(235,'followup_imaging_type','menu',0,'Imaging type',6,27,234,'NAPACA_FollowUp');
INSERT INTO Parameter VALUES(236,'followup_max_tumor','number',3,'Max. tumor (<em>mm</em>)',7,0,234,'NAPACA_FollowUp');
INSERT INTO Parameter VALUES(237,'followup_changes_hormone_secretion','menu',0,'Changes in hormone secretion',8,4,0,'NAPACA_FollowUp');
INSERT INTO Parameter VALUES(238,'gluco_serum_cortisol','menu',0,'Serum cortisol after 1mg Dex overnight',9,17,237,'NAPACA_FollowUp');
INSERT INTO Parameter VALUES(239,'gluco_plasma_acth','menu',0,'Baseline plasma ACTH',10,18,237,'NAPACA_FollowUp');
INSERT INTO Parameter VALUES(240,'gluco_urinary_free_cortisol','menu',0,'Urinary free cortisol',11,18,237,'NAPACA_FollowUp');
INSERT INTO Parameter VALUES(241,'mineralo_plasma_renin_activity','menu',0,'Random plasma renin activity (<em>ng/ml/h</em>)',12,18,237,'NAPACA_FollowUp');
INSERT INTO Parameter VALUES(242,'mineralo_plasma_renin_conc','menu',0,'Random plasma renin concentration (<em>ng/l</em>)',13,18,237,'NAPACA_FollowUp');
INSERT INTO Parameter VALUES(243,'mineralo_serum_aldosterone','menu',0,'Random serum aldosterone (<em>ng/l</em>)',14,18,237,'NAPACA_FollowUp');
INSERT INTO Parameter VALUES(244,'other_steroid_17hydroxyprogesterone','menu',0,'Serum 17-Hydroxyprogesterone',15,18,237,'NAPACA_FollowUp');
INSERT INTO Parameter VALUES(245,'other_steroid_serum_dheas','menu',0,'Serum DHEAS',16,18,237,'NAPACA_FollowUp');
INSERT INTO Parameter VALUES(246,'catechol_urinary_free_excretion','menu',0,'Urinary free catecholamine excretion',17,18,237,'NAPACA_FollowUp');
INSERT INTO Parameter VALUES(247,'catechol_urinary_metanephrine_excretion','menu',0,'Urinary metanephrine excretion',18,18,237,'NAPACA_FollowUp');
INSERT INTO Parameter VALUES(248,'catechol_plasma_metanephrines','menu',0,'Plasma metanephrines',19,18,237,'NAPACA_FollowUp');
INSERT INTO Parameter VALUES(249,'further_plans','menu',0,'Further plans for follow-up',20,28,0,'NAPACA_FollowUp');

INSERT INTO Parameter VALUES(250,'assessment_date','date',0,'Assessment Date',1,0,0,'Pheo_ClinicalAssessment');
INSERT INTO Parameter VALUES(251,'height','number',3,'Height',2,0,0,'Pheo_ClinicalAssessment');
INSERT INTO Parameter VALUES(252,'weight','number',3,'Weight',3,0,0,'Pheo_ClinicalAssessment');
INSERT INTO Parameter VALUES(253,'systolic_blood_pressure','number',3,'Systolic blood pressure',4,0,0,'Pheo_ClinicalAssessment');
INSERT INTO Parameter VALUES(254,'diastolic_blood_pressure','number',3,'Diastolic blood pressure',5,0,0,'Pheo_ClinicalAssessment');

INSERT INTO Parameter VALUES(255,'plasma_e','number',3,'Plasma epinephrine concentration',1,0,0,'Pheo_BiochemicalAssessment');
INSERT INTO Parameter VALUES(256,'plasma_n','number',3,'Plasma norepinephrine concentration',2,0,0,'Pheo_BiochemicalAssessment');
INSERT INTO Parameter VALUES(257,'plasma_date','date',0,'Plasma measurement date',3,0,255,'Pheo_BiochemicalAssessment');
INSERT INTO Parameter VALUES(258,'plasma_free_m','number',3,'Plasma-free metanephrine concentration',4,0,0,'Pheo_BiochemicalAssessment');
INSERT INTO Parameter VALUES(259,'plasma_free_n','number',3,'Plasma-free normetanephrine concentration',5,0,0,'Pheo_BiochemicalAssessment');
INSERT INTO Parameter VALUES(260,'plasma_free_methox','number',3,'Plasma-free methoxytyramine concentration',6,0,0,'Pheo_BiochemicalAssessment');
INSERT INTO Parameter VALUES(261,'plasma_free_date','date',0,'Plasma-free measurement date',7,0,258,'Pheo_BiochemicalAssessment');
INSERT INTO Parameter VALUES(262,'serum_chromo_a','number',3,'Serum chromogranin A concentration',8,0,0,'Pheo_BiochemicalAssessment');
INSERT INTO Parameter VALUES(263,'serum_chromo_a_date','date',0,'Serum chromogranin A measurement date',9,0,262,'Pheo_BiochemicalAssessment');
INSERT INTO Parameter VALUES(264,'urine_free_e','number',3,'Urinary-free epinephrine excretion',10,0,0,'Pheo_BiochemicalAssessment');
INSERT INTO Parameter VALUES(265,'urine_free_n','number',3,'Urinary-free norepinephrine excretion',11,0,0,'Pheo_BiochemicalAssessment');
INSERT INTO Parameter VALUES(266,'urine_free_date','date',0,'Urinary-free measurement date',12,0,264,'Pheo_BiochemicalAssessment');
INSERT INTO Parameter VALUES(267,'urine_m','number',3,'Urinary metanephrine excretion',13,0,0,'Pheo_BiochemicalAssessment');
INSERT INTO Parameter VALUES(268,'urine_n','number',3,'Urinary normetanephrine excretion',14,0,0,'Pheo_BiochemicalAssessment');
INSERT INTO Parameter VALUES(269,'urine_date','date',0,'Urinary measurement date',15,0,267,'Pheo_BiochemicalAssessment');
INSERT INTO Parameter VALUES(270,'plasma_dopamine_conc','number',3,'Plasma dopamine concentration',16,0,0,'Pheo_BiochemicalAssessment');
INSERT INTO Parameter VALUES(271,'plasma_dopamine_date','date',0,'Plasma dopamine measurement date',17,0,270,'Pheo_BiochemicalAssessment');

INSERT INTO Parameter VALUES(272,'surgery_date','date',0,'Surgery date',1,0,0,'Pheo_Surgery');
INSERT INTO Parameter VALUES(273,'surgery_procedure','menu',0,'Procedure',2,29,0,'Pheo_Surgery');
INSERT INTO Parameter VALUES(274,'evidence_of_malignancy','menu',0,'Evidence of malignancy',3,7,0,'Pheo_Surgery');
INSERT INTO Parameter VALUES(275,'evidence_of_locoregional_spread','menu',0,'Evidence of loco-regional spread',4,7,0,'Pheo_Surgery');
INSERT INTO Parameter VALUES(276,'comment','text',0,'Comment',5,0,0,'Pheo_Surgery');
INSERT INTO Parameter VALUES(277,'proliferative_index','number',4,'Proliferative index',6,0,0,'Pheo_Surgery');
INSERT INTO Parameter VALUES(278,'ki67_numbers','number',4,'ki67 (%)',7,0,0,'Pheo_Surgery');
INSERT INTO Parameter VALUES(279,'mitotic_count_numbers','number',4,'Mitotic number per 10 HPF',8,0,0,'Pheo_Surgery');
INSERT INTO Parameter VALUES(280,'cell_count_numbers','number',4,'Number of cells counted',9,0,0,'Pheo_Surgery');
INSERT INTO Parameter VALUES(281,'necrosis','menu',0,'Necrosis',10,7,0,'Pheo_Surgery');
INSERT INTO Parameter VALUES(282,'capsular_adipose_invasion','menu',0,'Capsular/adipose tissue invasion',11,7,0,'Pheo_Surgery');
INSERT INTO Parameter VALUES(283,'vascular_invasion','menu',0,'Extra-tumors vascular invasion',12,7,0,'Pheo_Surgery');
INSERT INTO Parameter VALUES(284,'ingrowth_in_adjacent','menu',0,'In-growth in adjacent organs',13,7,0,'Pheo_Surgery');

INSERT INTO Parameter VALUES(285,'tumor_date','date',0,'Tumor date',1,0,0,'Pheo_TumorDetails');
INSERT INTO Parameter VALUES(286,'largest_size_x','number',3,'Size of largest tumor - x dim (<em>mm</em>)',2,0,0,'Pheo_TumorDetails');
INSERT INTO Parameter VALUES(287,'largest_size_y','number',3,'Size of largest tumor - y dim (<em>mm</em>)',3,0,0,'Pheo_TumorDetails');
INSERT INTO Parameter VALUES(288,'largest_size_z','number',3,'Size of largest tumor - z dim (<em>mm</em>)',4,0,0,'Pheo_TumorDetails');
INSERT INTO Parameter VALUES(289,'tumor_resected','menu',0,'Tumor resected',5,30,0,'Pheo_TumorDetails');
INSERT INTO Parameter VALUES(290,'tumor_a_or_e','menu',0,'Adrenal or extra-adrenal',6,31,0,'Pheo_TumorDetails');
INSERT INTO Parameter VALUES(291,'tumor_site','menu',0,'Tumor site',7,32,0,'Pheo_TumorDetails');
INSERT INTO Parameter VALUES(292,'multiple_primaries','menu',0,'Multiple primary tumors',8,4,0,'Pheo_TumorDetails');
INSERT INTO Parameter VALUES(293,'tumor_distant_metastases','menu',0,'Distant metastases',9,4,0,'Pheo_TumorDetails');
INSERT INTO Parameter VALUES(294,'metastases_location','menu',0,'Location',10,33,293,'Pheo_TumorDetails');
INSERT INTO Parameter VALUES(295,'diagnosis_method','menu',0,'Source of information',11,34,0,'Pheo_TumorDetails');

INSERT INTO Parameter VALUES(296,'followup_date','date',0,'Follow-up date',1,0,0,'Pheo_FollowUp');
INSERT INTO Parameter VALUES(297,'alive','menu',0,'Alive',2,4,0,'Pheo_FollowUp');
INSERT INTO Parameter VALUES(298,'phpgl_free','menu',0,'PHPGL free',3,4,297,'Pheo_FollowUp');
INSERT INTO Parameter VALUES(299,'disease_state','menu',0,'Disease state',4,35,298,'Pheo_FollowUp');
INSERT INTO Parameter VALUES(300,'date_of_death','date',0,'Date of death',5,0,297,'Pheo_FollowUp');
INSERT INTO Parameter VALUES(301,'cause_of_death','menu',0,'Cause of death',6,36,297,'Pheo_FollowUp');
INSERT INTO Parameter VALUES(302,'comment','text',30,'Comment',7,0,0,'Pheo_FollowUp');

INSERT INTO Parameter VALUES(303,'ct','menu',0,'CT',1,37,0,'Pheo_ImagingTests');
INSERT INTO Parameter VALUES(304,'ct_date','date',0,'CT date',2,0,0,'Pheo_ImagingTests');
INSERT INTO Parameter VALUES(305,'ct_primary','number',3,'Primary (number)',3,0,303,'Pheo_ImagingTests');
INSERT INTO Parameter VALUES(306,'ct_metastases','number',3,'Metastases - liver/lung/bone (number)',4,0,303,'Pheo_ImagingTests');
INSERT INTO Parameter VALUES(307,'nmr','menu',0,'NMR',5,37,0,'Pheo_ImagingTests');
INSERT INTO Parameter VALUES(308,'nmr_date','date',0,'NMR date',6,0,0,'Pheo_ImagingTests');
INSERT INTO Parameter VALUES(309,'nmr_primary','number',3,'Primary (number)',7,0,307,'Pheo_ImagingTests');
INSERT INTO Parameter VALUES(310,'nmr_metastases','number',3,'Metastases - liver/lung/bone (number)',8,0,307,'Pheo_ImagingTests');
INSERT INTO Parameter VALUES(311,'mibg','menu',0,'MIBG',9,37,0,'Pheo_ImagingTests');
INSERT INTO Parameter VALUES(312,'mibg_date','date',0,'MIBG date',10,0,0,'Pheo_ImagingTests');
INSERT INTO Parameter VALUES(313,'mibg_primary','number',3,'Primary (number)',11,0,311,'Pheo_ImagingTests');
INSERT INTO Parameter VALUES(314,'mibg_metastases','number',3,'Metastases - liver/lung/bone (number)',12,0,311,'Pheo_ImagingTests');
INSERT INTO Parameter VALUES(315,'octreoscan','menu',0,'Octreoscan',13,37,0,'Pheo_ImagingTests');
INSERT INTO Parameter VALUES(316,'octreoscan_date','date',0,'Octreoscan date',14,0,0,'Pheo_ImagingTests');
INSERT INTO Parameter VALUES(317,'octreoscan_primary','number',3,'Primary (number)',15,0,315,'Pheo_ImagingTests');
INSERT INTO Parameter VALUES(318,'octreoscan_metastases','number',3,'Metastases - liver/lung/bone (number)',16,0,315,'Pheo_ImagingTests');
INSERT INTO Parameter VALUES(319,'fdg_pet','menu',0,'FDG-PET',17,37,0,'Pheo_ImagingTests');
INSERT INTO Parameter VALUES(320,'fdg_pet_date','date',0,'FDG-PET date',18,0,0,'Pheo_ImagingTests');
INSERT INTO Parameter VALUES(321,'fdg_pet_primary','number',3,'Primary (number)',19,0,319,'Pheo_ImagingTests');
INSERT INTO Parameter VALUES(322,'fdg_pet_metastases','number',3,'Metastases - liver/lung/bone (number)',20,0,319,'Pheo_ImagingTests');
INSERT INTO Parameter VALUES(323,'da_pet','menu',0,'F-DOPA PET',21,37,0,'Pheo_ImagingTests');
INSERT INTO Parameter VALUES(324,'da_pet_date','date',0,'F-DOPA PET date',22,0,0,'Pheo_ImagingTests');
INSERT INTO Parameter VALUES(325,'da_pet_primary','number',3,'Primary (number)',23,0,323,'Pheo_ImagingTests');
INSERT INTO Parameter VALUES(326,'da_pet_metastases','number',3,'Metastases - liver/lung/bone (number)',24,0,323,'Pheo_ImagingTests');
INSERT INTO Parameter VALUES(327,'synthesis_imaging_workup','date',0,'Synthesis of imaging work-up',25,0,0,'Pheo_ImagingTests');
INSERT INTO Parameter VALUES(328,'synthesis_imaging_workup_primary','number',3,'Primary PHPGL (number)',26,0,0,'Pheo_ImagingTests');
INSERT INTO Parameter VALUES(329,'synthesis_imaging_workup_metastases','number',3,'Metastases - liver/lung/bone (number)',27,0,0,'Pheo_ImagingTests');
INSERT INTO Parameter VALUES(330,'imaging_comment','text',30,'Comment/other imaging name',28,0,0,'Pheo_ImagingTests');
INSERT INTO Parameter VALUES(331,'other','menu',0,'Other imaging tool',29,37,0,'Pheo_ImagingTests');
INSERT INTO Parameter VALUES(332,'other_date','date',0,'Other date',30,0,0,'Pheo_ImagingTests');
INSERT INTO Parameter VALUES(333,'other_primary','number',3,'Primary (number)',31,0,331,'Pheo_ImagingTests');
INSERT INTO Parameter VALUES(334,'other_metastases','number',3,'Metastases - liver/lung/bone (number)',32,0,331,'Pheo_ImagingTests');

INSERT INTO Parameter VALUES(335,'mibg_therapy','menu',0,'MIBG therapy',1,4,0,'Pheo_NonSurgicalInterventions');
INSERT INTO Parameter VALUES(336,'mibg_therapy_date','date',0,'Therapy date',2,0,335,'Pheo_NonSurgicalInterventions');
INSERT INTO Parameter VALUES(337,'mibg_therapy_comment','text',30,'Comment',3,0,335,'Pheo_NonSurgicalInterventions');
INSERT INTO Parameter VALUES(338,'other_targeted_radiotherapy','menu',0,'Other targeted radiotherapy',4,4,0,'Pheo_NonSurgicalInterventions');
INSERT INTO Parameter VALUES(339,'other_targeted_radiotherapy_date','date',0,'Therapy date',5,0,338,'Pheo_NonSurgicalInterventions');
INSERT INTO Parameter VALUES(340,'other_targeted_radiotherapy_comment','text',30,'Comment',6,0,338,'Pheo_NonSurgicalInterventions');
INSERT INTO Parameter VALUES(341,'chemotherapy','menu',0,'Chemotherapy',7,4,0,'Pheo_NonSurgicalInterventions');
INSERT INTO Parameter VALUES(342,'chemotherapy_date','date',0,'Therapy date',8,0,341,'Pheo_NonSurgicalInterventions');
INSERT INTO Parameter VALUES(343,'chemotherapy_end_date','date',0,'Therapy end date',9,0,341,'Pheo_NonSurgicalInterventions');
INSERT INTO Parameter VALUES(344,'chemotherapy_comment','text',30,'Comment',10,0,341,'Pheo_NonSurgicalInterventions');
INSERT INTO Parameter VALUES(345,'radiofrequency_ablation','menu',0,'Radiofrequency ablation (lungs/liver/bones)',11,4,0,'Pheo_NonSurgicalInterventions');
INSERT INTO Parameter VALUES(346,'radiofrequency_ablation_date','date',0,'Therapy date',12,0,345,'Pheo_NonSurgicalInterventions');
INSERT INTO Parameter VALUES(347,'radiofrequency_ablation_comment','text',30,'Comment',13,0,345,'Pheo_NonSurgicalInterventions');
INSERT INTO Parameter VALUES(348,'external_radiotherapy','menu',0,'External radiotherapy',14,4,0,'Pheo_NonSurgicalInterventions');
INSERT INTO Parameter VALUES(349,'external_radiotherapy_date','date',0,'Therapy date',15,0,348,'Pheo_NonSurgicalInterventions');
INSERT INTO Parameter VALUES(350,'external_radiotherapy_comment','text',30,'Comment',16,0,348,'Pheo_NonSurgicalInterventions');
INSERT INTO Parameter VALUES(351,'targeted_molecular_therapy','menu',0,'Targeted molecular therapy',17,4,0,'Pheo_NonSurgicalInterventions');
INSERT INTO Parameter VALUES(352,'targeted_molecular_therapy_date','date',0,'Therapy date',18,0,351,'Pheo_NonSurgicalInterventions');
INSERT INTO Parameter VALUES(353,'targeted_molecular_therapy_end_date','date',0,'Therapy date',19,0,351,'Pheo_NonSurgicalInterventions');
INSERT INTO Parameter VALUES(354,'targeted_molecular_therapy_comment','text',30,'Comment',20,0,351,'Pheo_NonSurgicalInterventions');
INSERT INTO Parameter VALUES(355,'chemoembolisation','menu',0,'Chemoembolisation',21,4,0,'Pheo_NonSurgicalInterventions');
INSERT INTO Parameter VALUES(356,'chemoembolisation_date','date',0,'Therapy date',22,0,356,'Pheo_NonSurgicalInterventions');
INSERT INTO Parameter VALUES(357,'chemoembolisation_comment','text',0,'Comment',23,0,356,'Pheo_NonSurgicalInterventions');

INSERT INTO Parameter VALUES(358,'surgery_date','date',0,'Surgery date',1,0,0,'ACC_Surgery');
INSERT INTO Parameter VALUES(359,'surgery_type','menu',0,'Type',2,38,0,'ACC_Surgery');
INSERT INTO Parameter VALUES(360,'surgery_method','menu',0,'Method',3,39,0,'ACC_Surgery');
INSERT INTO Parameter VALUES(361,'surgery_overall_resection_status','menu',0,'Overall resection status',4,40,0,'ACC_Surgery');

INSERT INTO Parameter VALUES(362,'pathology_date','date',0,'Pathology date',1,0,0,'ACC_Pathology');
INSERT INTO Parameter VALUES(363,'pathology_derived_from','dynamicmenuonload',0,'Pathology derived from',2,0,0,'ACC_Pathology');
INSERT INTO Parameter VALUES(364,'surgery_biopsy_date','date',0,'Surgery biopsy date',3,0,363,'ACC_Pathology');
INSERT INTO Parameter VALUES(365,'local_pathologist','text',20,'Local pathologist',4,0,0,'ACC_Pathology');
INSERT INTO Parameter VALUES(366,'central_pathology_review','menu',0,'Central pathology review',5,4,0,'ACC_Pathology');
INSERT INTO Parameter VALUES(367,'central_review_pathologist','text',20,'Central review pathologist',6,0,366,'ACC_Pathology');
INSERT INTO Parameter VALUES(368,'number_of_mitoses_exact','number',3,'Number of mitoses (<em>Exact count per 50 HPF</em>)',7,0,0,'ACC_Pathology');
INSERT INTO Parameter VALUES(369,'ki67','number',3,'Ki67 (<em>in %/50 HPF</em>)',8,0,0,'ACC_Pathology');
INSERT INTO Parameter VALUES(370,'loss_of_heterozygosity','menu',0,'17p13 loss of heterozygosity',9,26,0,'ACC_Pathology');
INSERT INTO Parameter VALUES(371,'igf_overexpression','menu',0,'IGF overexpression',10,26,0,'ACC_Pathology');
INSERT INTO Parameter VALUES(372,'weiss_score','text_only',0,'Weiss score',11,0,0,'ACC_Pathology');
INSERT INTO Parameter VALUES(373,'nuclear_atypia','menu',0,'Nuclear atypia',12,26,0,'ACC_Pathology');
INSERT INTO Parameter VALUES(374,'atypical_mitosis','menu',0,'Atypical mitosis',13,26,0,'ACC_Pathology');
INSERT INTO Parameter VALUES(375,'spongiocytic_tumor_cells','menu',0,'Spongiocytic tumor cells',14,26,0,'ACC_Pathology');
INSERT INTO Parameter VALUES(376,'diffuse_architecture','menu',0,'Diffuse architecture',15,26,0,'ACC_Pathology');
INSERT INTO Parameter VALUES(377,'venous_invasion','menu',0,'Venous invasion',16,26,0,'ACC_Pathology');
INSERT INTO Parameter VALUES(378,'sinus_invasion','menu',0,'Sinus invasion',17,26,0,'ACC_Pathology');
INSERT INTO Parameter VALUES(379,'capsular_invasion','menu',0,'Capsular invasion',18,26,0,'ACC_Pathology');
INSERT INTO Parameter VALUES(380,'necrosis','menu',0,'Necrosis',19,26,0,'ACC_Pathology');
INSERT INTO Parameter VALUES(381,'number_of_mitoses_per5','menu',0,'Number of mitoses (<em>> 5/50 HPF</em>)',20,0,0,'ACC_Pathology');

INSERT INTO Parameter VALUES(382,'mitotane_date','date',0,'Mitotane date',1,0,0,'ACC_Mitotane');
INSERT INTO Parameter VALUES(383,'mitotane_initiation','date',0,'Mitotane initiation',2,0,0,'ACC_Mitotane');
INSERT INTO Parameter VALUES(384,'mitotane_end','date',0,'Mitotane end',3,0,0,'ACC_Mitotane');
INSERT INTO Parameter VALUES(385,'mitotane_indication','menu',0,'Indication',4,41,0,'ACC_Mitotane');
INSERT INTO Parameter VALUES(386,'mitotane_best_objective','menu',0,'Best objective evaluation',5,42,0,'ACC_Mitotane');
INSERT INTO Parameter VALUES(387,'mitotane_eval_date','date',0,'Evaluation date',6,0,0,'ACC_Mitotane');
INSERT INTO Parameter VALUES(388,'mitotane_max_dosage','number',3,'Maximum dosage (<em>g/d</em>)',7,0,0,'ACC_Mitotane');
INSERT INTO Parameter VALUES(389,'mitotane_max_level','number',3,'Maximum level (<em>mg/l</em>)',8,0,0,'ACC_Mitotane');

INSERT INTO Parameter VALUES(390,'chemotherapy_date','date',0,'Chemotherapy date',1,0,0,'ACC_Chemotherapy');
INSERT INTO Parameter VALUES(391,'chemotherapy_initiation','date',0,'Chemotherapy initiation',2,0,0,'ACC_Chemotherapy');
INSERT INTO Parameter VALUES(392,'chemotherapy_end','date',0,'Chemotherapy end',3,0,0,'ACC_Chemotherapy');
INSERT INTO Parameter VALUES(393,'chemotherapy_indication','menu',0,'Indication',4,41,0,'ACC_Chemotherapy');
INSERT INTO Parameter VALUES(394,'chemotherapy_best_objective','menu',0,'Best objective evaluation',5,42,0,'ACC_Chemotherapy');
INSERT INTO Parameter VALUES(395,'chemotherapy_eval_date','date',0,'Evaluation date',6,0,0,'ACC_Chemotherapy');

INSERT INTO Parameter VALUES(396,'radiofrequency_date','date',0,'Radiofrequency date',1,0,0,'ACC_Radiofrequency');
INSERT INTO Parameter VALUES(397,'radiofrequency_type','menu',0,'Type',2,38,0,'ACC_Radiofrequency');
INSERT INTO Parameter VALUES(398,'radiofrequency_best_objective','date',0,'Best objective evaluation',3,42,0,'ACC_Radiofrequency');
INSERT INTO Parameter VALUES(399,'radiofrequency_eval_date','date',0,'Evaluation date',4,0,0,'ACC_Radiofrequency');

INSERT INTO Parameter VALUES(400,'radiotherapy_date','date',0,'Radiotherapy date',1,0,0,'ACC_Radiotherapy');
INSERT INTO Parameter VALUES(401,'radiotherapy_initiation','date',0,'Radiotherapy initiation',2,0,0,'ACC_Radiotherapy');
INSERT INTO Parameter VALUES(402,'radiotherapy_end','date',0,'Radiotherapy end',3,0,0,'ACC_Radiotherapy');
INSERT INTO Parameter VALUES(403,'radiotherapy_type','menu',0,'Radiotherapy type',4,38,0,'ACC_Radiotherapy');
INSERT INTO Parameter VALUES(404,'radiotherapy_indication','menu',0,'Indication',5,41,0,'ACC_Radiotherapy');
INSERT INTO Parameter VALUES(405,'radiotherapy_best_objective','menu',0,'Best objective evaluation',6,42,0,'ACC_Radiotherapy');
INSERT INTO Parameter VALUES(406,'radiotherapy_eval_date','date',0,'Evaluation date',7,0,0,'ACC_Radiotherapy');

INSERT INTO Parameter VALUES(407,'chemoembolisation_date','date',0,'Chemoembolisation date',1,0,0,'ACC_Chemoembolisation');
INSERT INTO Parameter VALUES(408,'chemoembolisation_type','menu',0,'Type',2,38,0,'ACC_Chemoembolisation');
INSERT INTO Parameter VALUES(409,'chemoembolisation_best_objective','menu',0,'Best objective evaluation',3,42,0,'ACC_Chemoembolisation');
INSERT INTO Parameter VALUES(410,'chemoembolisation_eval_date','date',0,'Evaluation date',4,0,0,'ACC_Chemoembolisation');

INSERT INTO Parameter VALUES(411,'followup_date','date',0,'Follow-up date',1,0,0,'ACC_FollowUp');
INSERT INTO Parameter VALUES(412,'patient_status','menu',20,'Patient status',2,43,0,'ACC_FollowUp');

INSERT INTO Parameter VALUES(413,'assessment_date','date',0,'Assessment date',1,0,0,'APA_ClinicalAssessment');
INSERT INTO Parameter VALUES(414,'height','number',3,'Height (<em>cm</em>)',2,0,0,'APA_ClinicalAssessment');
INSERT INTO Parameter VALUES(415,'weight','number',3,'Weight (<em>kg</em>)',3,0,0,'APA_ClinicalAssessment');
INSERT INTO Parameter VALUES(416,'systolic_blood_pressure','number',0,'Systolic blood pressure (<em>mmHg</em>)',4,0,0,'APA_ClinicalAssessment');
INSERT INTO Parameter VALUES(417,'diastolic_blood_pressure','number',0,'Diastolic blood pressure (<em>mmHg</em>)',5,0,0,'APA_ClinicalAssessment');
INSERT INTO Parameter VALUES(418,'heart_rate','date',0,'Heart rate (<em>bpm</em>)',6,0,0,'APA_ClinicalAssessment');

INSERT INTO Parameter VALUES(419,'event_date','date',0,'Event date',1,0,0,'APA_Cardio');
INSERT INTO Parameter VALUES(420,'echocardiography','menu',0,'Echocardiography',2,4,0,'APA_Cardio');
INSERT INTO Parameter VALUES(421,'echocardiography_specific','menu',0,'Echocardiography (specific)',3,44,420,'APA_Cardio');
INSERT INTO Parameter VALUES(422,'electrocardiogram','menu',0,'Electrocardiogram',4,4,0,'APA_Cardio');
INSERT INTO Parameter VALUES(423,'electrocardiogram_specific','menu',0,'Electrocardiogram (specific)',5,45,422,'APA_Cardio');
INSERT INTO Parameter VALUES(424,'electrocardiogram_other','text',20,'Electrocardiogram (other)',6,0,422,'APA_Cardio');

INSERT INTO Parameter VALUES(425,'event_date','date',0,'Event date',1,0,0,'APA_Complication');
INSERT INTO Parameter VALUES(426,'complication','menu',0,'Complication',2,46,0,'APA_Complication');
INSERT INTO Parameter VALUES(427,'diabetes','menu',0,'Diabetes',3,4,0,'APA_Complication');
INSERT INTO Parameter VALUES(428,'hypercholesterenimia','menu',0,'Hypercholesterenimia',4,4,0,'APA_Complication');
INSERT INTO Parameter VALUES(429,'smoking_status','menu',0,'Smoking status',5,47,0,'APA_Complication');
INSERT INTO Parameter VALUES(430,'comment','text',20,'Comment',6,0,0,'APA_Complication');

INSERT INTO Parameter VALUES(431,'imaging_date','date',0,'Imaging date',1,0,0,'APA_Imaging');
INSERT INTO Parameter VALUES(432,'adrenal_state','menu',0,'Adrenal morphology',2,48,0,'APA_Imaging');
INSERT INTO Parameter VALUES(433,'tumor_sites_imaging','menu',0,'Tumor sites',3,49,0,'APA_Imaging');
INSERT INTO Parameter VALUES(434,'max_tumor_by_ct_right','number',3,'Max tumor diameter by CT - right (<em>mm</em>)',4,0,433,'APA_Imaging');
INSERT INTO Parameter VALUES(435,'max_tumor_by_mr_right','number',3,'Max tumor diameter by MR - right (<em>mm</em>)',5,0,433,'APA_Imaging');
INSERT INTO Parameter VALUES(436,'max_tumor_by_ct_left','number',3,'Max tumor diameter by CT - left (<em>mm</em>)',6,0,433,'APA_Imaging');
INSERT INTO Parameter VALUES(437,'max_tumor_by_mr_left','number',3,'Max tumor diameter by MR - left (<em>mm</em>)',7,0,433,'APA_Imaging');
INSERT INTO Parameter VALUES(438,'scintigraphy','menu',0,'Scintigraphy',8,50,0,'APA_Imaging');

INSERT INTO Parameter VALUES(439,'intervention_date','date',0,'Intervention date',1,0,0,'APA_Surgery');
INSERT INTO Parameter VALUES(440,'tumor_sites_surgery','menu',0,'Operated tumor sites',2,22,0,'APA_Surgery');
INSERT INTO Parameter VALUES(441,'surgery_procedure','menu',0,'Procedure',3,24,0,'APA_Surgery');
INSERT INTO Parameter VALUES(442,'adrenal_sparing','menu',0,'Adrenal sparing',4,7,0,'APA_Surgery');
INSERT INTO Parameter VALUES(443,'max_tumor_right','number',3,'Max tumor diameter - right (<em>mm</em>)',5,0,442,'APA_Surgery');
INSERT INTO Parameter VALUES(444,'max_tumor_left','number',3,'Max tumor diameter - left (<em>mm</em>)',6,0,442,'APA_Surgery');
INSERT INTO Parameter VALUES(445,'tumor_weight','number',3,'Tumor weight (<em>g</em>)',7,0,0,'APA_Surgery');
INSERT INTO Parameter VALUES(446,'comment','text',20,'Comment',8,0,0,'APA_Surgery');

INSERT INTO Parameter VALUES(447,'assessment_date','date',0,'Assessment date',1,0,0,'APA_BiochemicalAssessment');
INSERT INTO Parameter VALUES(448,'spironolactone','menu',0,'Spironolactone or eplerenone in last 4 wks',2,7,0,'APA_BiochemicalAssessment');
INSERT INTO Parameter VALUES(449,'other_diuretic','menu',0,'Other diuretic in the last week',3,7,0,'APA_BiochemicalAssessment');
INSERT INTO Parameter VALUES(450,'beta_blocker','menu',0,'Beta-blocker in the last week',4,7,0,'APA_BiochemicalAssessment');
INSERT INTO Parameter VALUES(451,'ace_inhibitor','menu',0,'ACE inhibitor in the last week',5,7,0,'APA_BiochemicalAssessment');
INSERT INTO Parameter VALUES(452,'ang_receptor','menu',0,'Ang II receptor antagonist in the last week',6,7,0,'APA_BiochemicalAssessment');
INSERT INTO Parameter VALUES(453,'central_anti_ht','menu',0,'Centrally acting anti-HT in the last week',7,7,0,'APA_BiochemicalAssessment');
INSERT INTO Parameter VALUES(454,'calcium_channel','menu',0,'Calcium channel antagonist in the last week',8,7,0,'APA_BiochemicalAssessment');
INSERT INTO Parameter VALUES(455,'alpha_blocker','menu',0,'Alpha blocker in the last week',9,7,0,'APA_BiochemicalAssessment');
INSERT INTO Parameter VALUES(456,'potassium_salts','menu',0,'Potassium supplementation in the last week',10,7,0,'APA_BiochemicalAssessment');
INSERT INTO Parameter VALUES(457,'serum_potassium','number',3,'Serum potassium (mmol/l)',11,0,0,'APA_BiochemicalAssessment');
INSERT INTO Parameter VALUES(458,'serum_sodium','number',3,'Serum sodium (mmol/l)',12,0,0,'APA_BiochemicalAssessment');
INSERT INTO Parameter VALUES(459,'plasma_creatinine','number',3,'Plasma creatinine (µmol/l)',13,0,0,'APA_BiochemicalAssessment');
INSERT INTO Parameter VALUES(460,'supine_pra','number',3,'Supine plasma renin activity (PRA) (ng/ml.h)',14,0,0,'APA_BiochemicalAssessment');
INSERT INTO Parameter VALUES(461,'sitting_pra','number',3,'Sitting or standing PRA (ng/ml.h)',15,0,0,'APA_BiochemicalAssessment');
INSERT INTO Parameter VALUES(462,'supine_arc','number',3,'Supine active renin concentration (ARC) (ng/l)',16,0,0,'APA_BiochemicalAssessment');
INSERT INTO Parameter VALUES(463,'sitting_arc','number',3,'Sitting or standing ARC (ng/l)',17,0,0,'APA_BiochemicalAssessment');
INSERT INTO Parameter VALUES(464,'standing_aldosterone','number',3,'Standing aldosterone concentration (ng/L)',18,0,0,'APA_BiochemicalAssessment');
INSERT INTO Parameter VALUES(465,'sitting_aldosterone','number',3,'Sitting or supine aldosterone concentration (ng/L)',19,0,0,'APA_BiochemicalAssessment');
INSERT INTO Parameter VALUES(466,'urinary_aldosterone','number',3,'Urinary aldosterone (µg/day)',20,0,0,'APA_BiochemicalAssessment');
INSERT INTO Parameter VALUES(467,'urinary_tetrahydroaldosterone','number',3,'Urinary tetrahydroaldosterone (µg/day)',21,0,0,'APA_BiochemicalAssessment');
INSERT INTO Parameter VALUES(468,'post_captopril_aldosterone','number',3,'Post captopril aldosterone concentration (ng/L)',22,0,0,'APA_BiochemicalAssessment');
INSERT INTO Parameter VALUES(469,'post_oral_sodium_aldosterone','number',3,'Post oral sodium load aldosterone concentration (ng/L)	',23,0,0,'APA_BiochemicalAssessment');
INSERT INTO Parameter VALUES(470,'post_saline_infusion_aldosterone','number',3,'Post saline infusion aldosterone concentration (ng/L)',24,0,0,'APA_BiochemicalAssessment');
INSERT INTO Parameter VALUES(471,'post_fludrocorticone_aldosterone','number',3,'Post fludrocorticone aldosterone concentration (ng/L)',25,0,0,'APA_BiochemicalAssessment');
INSERT INTO Parameter VALUES(472,'post_furosemide_aldosterone','number',3,'Post furosemide aldosterone concentration (ng/L)',26,0,0,'APA_BiochemicalAssessment');
INSERT INTO Parameter VALUES(473,'aldosterone_right','number',3,'Aldosterone concentration, right side (ng/L)',27,0,0,'APA_BiochemicalAssessment');
INSERT INTO Parameter VALUES(474,'corticol_right','number',3,'Cortisol concentration, right side (µg/L)',28,0,0,'APA_BiochemicalAssessment');
INSERT INTO Parameter VALUES(475,'aldosterone_left','number',3,'Aldosterone concentration, left side (ng/L)',29,0,0,'APA_BiochemicalAssessment');
INSERT INTO Parameter VALUES(476,'corticol_left','number',3,'Cortisol concentration, left side (µg/L)',30,0,0,'APA_BiochemicalAssessment');
INSERT INTO Parameter VALUES(477,'aldosterone_vena_cava','number',3,'Aldosterone concentration, inferior vena cava (ng/L)',31,0,0,'APA_BiochemicalAssessment');
INSERT INTO Parameter VALUES(478,'corticol_vena_cava','number',3,'Cortisol concentration, inferior vena cava (µg/L)',32,0,0,'APA_BiochemicalAssessment');
INSERT INTO Parameter VALUES(479,'bilateral_avs','menu',0,'Successful bilateral adrenal vein sampling',33,0,0,'APA_BiochemicalAssessment');
INSERT INTO Parameter VALUES(480,'stimulation_avs','menu',0,'Stimulation during AVS',34,0,0,'APA_BiochemicalAssessment');
INSERT INTO Parameter VALUES(481,'biochemical_comment','text',20,'Comment',35,0,0,'APA_BiochemicalAssessment');

INSERT INTO Parameter VALUES(482,'followup_date','date',0,'Follow-up date',1,0,0,'APA_FollowUp');
INSERT INTO Parameter VALUES(483,'followup_alive','menu',0,'Alive',2,7,0,'APA_FollowUp');
INSERT INTO Parameter VALUES(484,'cause_of_death','text',20,'Cause of death',3,0,483,'APA_FollowUp');
INSERT INTO Parameter VALUES(485,'followup_weight','number',3,'Weight',4,0,0,'APA_FollowUp');
INSERT INTO Parameter VALUES(486,'followup_systolic_bp','number',3,'Systolic BP',5,0,0,'APA_FollowUp');
INSERT INTO Parameter VALUES(487,'followup_diastolic_bp','number',3,'Diastolic BP',6,0,0,'APA_FollowUp');
INSERT INTO Parameter VALUES(488,'spironolactone','menu',0,'Spironolactone or eplerenone in last 4 weeks',7,7,0,'APA_FollowUp');
INSERT INTO Parameter VALUES(489,'other_diuretic','menu',0,'Other diuretic in the last week',8,7,0,'APA_FollowUp');
INSERT INTO Parameter VALUES(490,'beta_blocker','menu',0,'Beta-blocker in the last week',9,7,0,'APA_FollowUp');
INSERT INTO Parameter VALUES(491,'ace_inhibitor','menu',0,'ACE inhibitor in the last week',10,7,0,'APA_FollowUp');
INSERT INTO Parameter VALUES(492,'ang_receptor','menu',0,'Ang II receptor antagonist in the last week',11,7,0,'APA_FollowUp');
INSERT INTO Parameter VALUES(493,'central_anti_ht','menu',0,'Centrally acting anti-HT in the last week',12,7,0,'APA_FollowUp');
INSERT INTO Parameter VALUES(494,'calcium_channel','menu',0,'Calcium channel antagonist in the last week',13,7,0,'APA_FollowUp');
INSERT INTO Parameter VALUES(495,'alpha_blocker','menu',0,'Alpha blocker in the last week',14,7,0,'APA_FollowUp');
INSERT INTO Parameter VALUES(496,'potassium_salts','menu',0,'Potassium supplementation in the last week',15,7,0,'APA_FollowUp');
INSERT INTO Parameter VALUES(497,'eval_of_bp_outcomes','menu',0,'Evaluation of BP outcomes',16,7,0,'APA_FollowUp');
INSERT INTO Parameter VALUES(498,'serum_potassium','number',3,'Serum potassium (mmol/l)',17,0,0,'APA_FollowUp');
INSERT INTO Parameter VALUES(499,'serum_sodium','number',3,'Serum sodium (mmol/l)',18,0,0,'APA_FollowUp');
INSERT INTO Parameter VALUES(500,'plasma_creatinine','number',3,'Plasma creatinine (µmol/l)',19,0,0,'APA_FollowUp');
INSERT INTO Parameter VALUES(501,'supine_pra','number',3,'Standing plasma renin activity (PRA) (ng/ml.h)	',20,0,0,'APA_FollowUp');
INSERT INTO Parameter VALUES(502,'sitting_pra','number',3,'Sitting or supine PRA (ng/ml.h)',21,0,0,'APA_FollowUp');
INSERT INTO Parameter VALUES(503,'supine_arc','number',3,'Standing active renin concentration (ARC) (ng/l)',22,0,0,'APA_FollowUp');
INSERT INTO Parameter VALUES(504,'sitting_arc','number',3,'Sitting or supine ARC (ng/l)',23,0,0,'APA_FollowUp');
INSERT INTO Parameter VALUES(505,'standing_aldosterone','number',3,'Standing aldosterone concentration (ng/L)',24,0,0,'APA_FollowUp');
INSERT INTO Parameter VALUES(506,'sitting_aldosterone','number',3,'Sitting or supine aldosterone concentration (ng/L)',25,0,0,'APA_FollowUp');
INSERT INTO Parameter VALUES(507,'urinary_aldosterone','number',3,'Urinary aldosterone (µg/day)',26,0,0,'APA_FollowUp');
INSERT INTO Parameter VALUES(508,'urinary_tetrahydroaldosterone','number',3,'Urinary tetrahydroaldosterone (µg/day)',27,0,0,'APA_FollowUp');
INSERT INTO Parameter VALUES(509,'followup_comment','text',20,'Comment',28,0,0,'APA_FollowUp');
INSERT INTO Parameter VALUES(510,'followup_complications','menu',0,'Complications',29,0,0,'APA_FollowUp');


INSERT INTO Menu VALUES(1,'gender','s');
INSERT INTO Menu VALUES(2,'center','s');
INSERT INTO Menu VALUES(3,'sharing_levels','s');
INSERT INTO Menu VALUES(4,'yes_no','s');

INSERT INTO Menu VALUES(5,'disease_status','s');
INSERT INTO Menu VALUES(6,'modality','s');
INSERT INTO Menu VALUES(7,'yes_no_unknown','s');

INSERT INTO Menu VALUES(8,'site_adrenal_tumor','s');
INSERT INTO Menu VALUES(9,'information_based_on','s');
INSERT INTO Menu VALUES(10,'yes_no_nd','s');
INSERT INTO Menu VALUES(11,'yes_no_nd_dbom','s');

INSERT INTO Menu VALUES(12,'phenotypic_diag_pheo','s');
INSERT INTO Menu VALUES(13,'system_organ','m');
INSERT INTO Menu VALUES(14,'presentation_first_tumor','m');

INSERT INTO Menu VALUES(15,'doc_genetic_apa','s');

INSERT INTO Menu VALUES(16,'months','s');
INSERT INTO Menu VALUES(17,'serum_cortisol','s');
INSERT INTO Menu VALUES(18,'napaca_levels','s');

INSERT INTO Menu VALUES(19,'associated_study','s');
INSERT INTO Menu VALUES(20,'associated_study_phase_visit','s');
INSERT INTO Menu VALUES(21,'normal_tissue_options','m');

INSERT INTO Menu VALUES(22,'tumor_sites','s');
INSERT INTO Menu VALUES(23,'ct_hounsfield_units','s');

INSERT INTO Menu VALUES(24,'surgical_approach','s');

INSERT INTO Menu VALUES(25,'pathology_diagnosis','s');
INSERT INTO Menu VALUES(26,'yes_no_na','s');

INSERT INTO Menu VALUES(27,'imaging_type','s');
INSERT INTO Menu VALUES(28,'followup_plans','s');

INSERT INTO Menu VALUES(29,'pheo_surgery_procedure','s');

INSERT INTO Menu VALUES(30,'pheo_resection','s');
INSERT INTO Menu VALUES(31,'a_or_e','s');
INSERT INTO Menu VALUES(32,'tumor_site','s');
INSERT INTO Menu VALUES(33,'metastases_location','s');
INSERT INTO Menu VALUES(34,'pheo_information_source','s');

INSERT INTO Menu VALUES(35,'disease_state','s');
INSERT INTO Menu VALUES(36,'cause_of_death','s');

INSERT INTO Menu VALUES(37,'positive_negative_imaging','s');

INSERT INTO Menu VALUES(38,'surgery_type','s');
INSERT INTO Menu VALUES(39,'surgery_method','s');
INSERT INTO Menu VALUES(40,'resection_status_acc','s');

INSERT INTO Menu VALUES(41,'indication','s');
INSERT INTO Menu VALUES(42,'best_objective_evaluation','s');

INSERT INTO Menu VALUES(43,'patient_status','s');

INSERT INTO Menu VALUES(44,'echocardiography','s');
INSERT INTO Menu VALUES(45,'electrocardiogram','s');

INSERT INTO Menu VALUES(46,'complications','s');
INSERT INTO Menu VALUES(47,'smoking_status','s');

INSERT INTO Menu VALUES(48,'adrenal_state','s');
INSERT INTO Menu VALUES(49,'tumor_sites','s');
INSERT INTO Menu VALUES(50,'scintigraphy','s');



INSERT INTO MenuOption VALUES(1,'M',1);
INSERT INTO MenuOption VALUES(2,'F',1);

INSERT INTO MenuOption VALUES(3,'FRPA2',2);

INSERT INTO MenuOption VALUES(4,'Local',3);
INSERT INTO MenuOption VALUES(5,'National',3);
INSERT INTO MenuOption VALUES(6,'European ENSAT Partners',3);
INSERT INTO MenuOption VALUES(7,'International Collaborators',3);

INSERT INTO MenuOption VALUES(8,'Yes',4);
INSERT INTO MenuOption VALUES(9,'No',4);

INSERT INTO MenuOption VALUES(10,'Free of disease',5);
INSERT INTO MenuOption VALUES(11,'Not free of disease',5);

INSERT INTO MenuOption VALUES(12,'Pathology',6);
INSERT INTO MenuOption VALUES(13,'Hormonal + Imaging Work-Up',6);

INSERT INTO MenuOption VALUES(14,'Yes',7);
INSERT INTO MenuOption VALUES(15,'No',7);
INSERT INTO MenuOption VALUES(16,'Unknown',7);

INSERT INTO MenuOption VALUES(17,'Right',8);
INSERT INTO MenuOption VALUES(18,'Left',8);
INSERT INTO MenuOption VALUES(19,'Both',8);

INSERT INTO MenuOption VALUES(20,'Pathology',9);
INSERT INTO MenuOption VALUES(21,'Imaging',9);
INSERT INTO MenuOption VALUES(22,'Both (Pathology and Radiology)',9);

INSERT INTO MenuOption VALUES(23,'Yes',10);
INSERT INTO MenuOption VALUES(24,'No',10);
INSERT INTO MenuOption VALUES(25,'Not Determined',10);

INSERT INTO MenuOption VALUES(26,'Yes',11);
INSERT INTO MenuOption VALUES(27,'No',11);
INSERT INTO MenuOption VALUES(28,'Not Determined',11);
INSERT INTO MenuOption VALUES(29,'Determined By Other Means',11);

INSERT INTO MenuOption VALUES(30,'None',15);
INSERT INTO MenuOption VALUES(31,'FH1',15);
INSERT INTO MenuOption VALUES(32,'FH2',15);
INSERT INTO MenuOption VALUES(33,'FH3',15);

INSERT INTO MenuOption VALUES(34,'Suggestive hormone and/or tumor-related symptoms',14);
INSERT INTO MenuOption VALUES(35,'Therapy resistant hypertension',14);
INSERT INTO MenuOption VALUES(36,'Incidentaloma',14);
INSERT INTO MenuOption VALUES(37,'Presymptomatic screening',14);
INSERT INTO MenuOption VALUES(38,'Other',14);

INSERT INTO MenuOption VALUES(39,'MEN2',12);
INSERT INTO MenuOption VALUES(40,'VHL',12);
INSERT INTO MenuOption VALUES(41,'NF1',12);
INSERT INTO MenuOption VALUES(42,'SDHA',12);
INSERT INTO MenuOption VALUES(43,'SDHAF2',12);
INSERT INTO MenuOption VALUES(44,'SDHB',12);
INSERT INTO MenuOption VALUES(45,'SDHC',12);
INSERT INTO MenuOption VALUES(46,'SDHD',12);
INSERT INTO MenuOption VALUES(47,'TMEM127',12);

INSERT INTO MenuOption VALUES(48,'Bone and connective tissue',13);
INSERT INTO MenuOption VALUES(49,'Breast',13);
INSERT INTO MenuOption VALUES(50,'CNS',13);
INSERT INTO MenuOption VALUES(51,'GI track',13);
INSERT INTO MenuOption VALUES(52,'Leukemia/Lymphoma',13);
INSERT INTO MenuOption VALUES(53,'Lung',13);
INSERT INTO MenuOption VALUES(54,'Pancreas',13);
INSERT INTO MenuOption VALUES(55,'Skin',13);
INSERT INTO MenuOption VALUES(56,'Thyroid',13);
INSERT INTO MenuOption VALUES(57,'Urogenital',13);
INSERT INTO MenuOption VALUES(58,'Other',13);

INSERT INTO MenuOption VALUES(59,'Jan',16);
INSERT INTO MenuOption VALUES(60,'Feb',16);
INSERT INTO MenuOption VALUES(61,'Mar',16);
INSERT INTO MenuOption VALUES(62,'Apr',16);
INSERT INTO MenuOption VALUES(63,'May',16);
INSERT INTO MenuOption VALUES(64,'Jun',16);
INSERT INTO MenuOption VALUES(65,'Jul',16);
INSERT INTO MenuOption VALUES(66,'Aug',16);
INSERT INTO MenuOption VALUES(67,'Sep',16);
INSERT INTO MenuOption VALUES(68,'Oct',16);
INSERT INTO MenuOption VALUES(69,'Nov',16);
INSERT INTO MenuOption VALUES(70,'Dec',16);

INSERT INTO MenuOption VALUES(71,'Suppressed',17);
INSERT INTO MenuOption VALUES(72,'Not Suppressed',17);
INSERT INTO MenuOption VALUES(73,'Not Done',17);

INSERT INTO MenuOption VALUES(74,'Elevated',18);
INSERT INTO MenuOption VALUES(75,'Suppressed',18);
INSERT INTO MenuOption VALUES(76,'Normal',18);
INSERT INTO MenuOption VALUES(77,'Not Done',18);

INSERT INTO MenuOption VALUES(78,'ENS@T Biobank',19);
INSERT INTO MenuOption VALUES(79,'PMT',19);
INSERT INTO MenuOption VALUES(80,'ADIUVO',19);
INSERT INTO MenuOption VALUES(81,'EURINE-ACT',19);
INSERT INTO MenuOption VALUES(82,'CHIRACIC',19);
INSERT INTO MenuOption VALUES(83,'German Conn Registry',19);
INSERT INTO MenuOption VALUES(84,'German Cushing Registry',19);

---- Menu 20 (Associated Study Phase/Visit) will be dynamically generated

INSERT INTO MenuOption VALUES(85,'Adjacent Adrenal',21);
INSERT INTO MenuOption VALUES(86,'Kidney',21);
INSERT INTO MenuOption VALUES(87,'Liver',21);
INSERT INTO MenuOption VALUES(88,'Lung',21);
INSERT INTO MenuOption VALUES(89,'Lymph Node',21);
INSERT INTO MenuOption VALUES(90,'Fat (Periadrenal)',21);
INSERT INTO MenuOption VALUES(91,'Fat (Subcutaneous)',21);
INSERT INTO MenuOption VALUES(92,'Others',21);

INSERT INTO MenuOption VALUES(93,'Right Adrenal',22);
INSERT INTO MenuOption VALUES(94,'Left Adrenal',22);
INSERT INTO MenuOption VALUES(95,'Both Adrenals',22);

INSERT INTO MenuOption VALUES(96,'< 10',23);
INSERT INTO MenuOption VALUES(97,'10-20',23);
INSERT INTO MenuOption VALUES(98,'> 20',23);
INSERT INTO MenuOption VALUES(99,'Not done',23);

INSERT INTO MenuOption VALUES(100,'Open Surgery',24);
INSERT INTO MenuOption VALUES(101,'Minimal Invasive',24);

INSERT INTO MenuOption VALUES(102,'Adrenocortical Adenoma',25);
INSERT INTO MenuOption VALUES(103,'Adrenomyelolipoma',25);
INSERT INTO MenuOption VALUES(104,'Adrenal Ganglioneuroma',25);
INSERT INTO MenuOption VALUES(105,'Adrenal Haemangioma',25);
INSERT INTO MenuOption VALUES(106,'Adrenal Cyst',25);
INSERT INTO MenuOption VALUES(107,'Adrenocortical Oncocytoma',25);
INSERT INTO MenuOption VALUES(108,'Indeterminate',25);
INSERT INTO MenuOption VALUES(109,'Metastasis',25);
INSERT INTO MenuOption VALUES(110,'Lymphoma',25);
INSERT INTO MenuOption VALUES(111,'Phaeochromocytoma',25);
INSERT INTO MenuOption VALUES(112,'Adrenocortical Carcinoma',25);
INSERT INTO MenuOption VALUES(113,'Other',25);

INSERT INTO MenuOption VALUES(114,'Yes',26);
INSERT INTO MenuOption VALUES(115,'No',26);
INSERT INTO MenuOption VALUES(116,'Not Available',26);

INSERT INTO MenuOption VALUES(117,'CT',27);
INSERT INTO MenuOption VALUES(118,'MRI',27);
INSERT INTO MenuOption VALUES(119,'Ultrasound',27);
INSERT INTO MenuOption VALUES(120,'FDG-PET',27);

INSERT INTO MenuOption VALUES(121,'Discharged',28);
INSERT INTO MenuOption VALUES(122,'Surgery',28);
INSERT INTO MenuOption VALUES(123,'Further Monitoring',28);

INSERT INTO MenuOption VALUES(124,'Biopsy',29);
INSERT INTO MenuOption VALUES(125,'Open',29);
INSERT INTO MenuOption VALUES(126,'Laparoscopic',29);

INSERT INTO MenuOption VALUES(127,'R0',30);
INSERT INTO MenuOption VALUES(128,'R1',30);
INSERT INTO MenuOption VALUES(129,'R2',30);
INSERT INTO MenuOption VALUES(130,'RX',30);
INSERT INTO MenuOption VALUES(131,'No resection',30);

INSERT INTO MenuOption VALUES(132,'Adrenal',31);
INSERT INTO MenuOption VALUES(133,'Extra-adrenal',31);

INSERT INTO MenuOption VALUES(134,'Right adrenal',32);
INSERT INTO MenuOption VALUES(135,'Left adrenal',32);
INSERT INTO MenuOption VALUES(136,'Both adrenals',32);
INSERT INTO MenuOption VALUES(137,'Abdominal PGL',32);
INSERT INTO MenuOption VALUES(138,'Thoracic PGL',32);
INSERT INTO MenuOption VALUES(139,'Cervical PGL',32);
INSERT INTO MenuOption VALUES(140,'HN-PGL',32);

INSERT INTO MenuOption VALUES(141,'Lung',33);
INSERT INTO MenuOption VALUES(142,'Liver',33);
INSERT INTO MenuOption VALUES(143,'Bone',33);
INSERT INTO MenuOption VALUES(144,'Others',33);

INSERT INTO MenuOption VALUES(145,'Imaging',34);
INSERT INTO MenuOption VALUES(146,'Pathology',34);
INSERT INTO MenuOption VALUES(147,'PatientHistory',34);

INSERT INTO MenuOption VALUES(148,'Stable disease',35);
INSERT INTO MenuOption VALUES(149,'Progressive disease',35);
INSERT INTO MenuOption VALUES(150,'Partial remission',35);
INSERT INTO MenuOption VALUES(151,'Not applicable',35);

INSERT INTO MenuOption VALUES(152,'PHPGL-related',36);
INSERT INTO MenuOption VALUES(153,'Non-PHPGL-related',36);
INSERT INTO MenuOption VALUES(154,'Unknown',36);

INSERT INTO MenuOption VALUES(155,'Positive',37);
INSERT INTO MenuOption VALUES(156,'Negative',37);

INSERT INTO MenuOption VALUES(157,'First',38);
INSERT INTO MenuOption VALUES(158,'Repeated',38);

INSERT INTO MenuOption VALUES(159,'Open',39);
INSERT INTO MenuOption VALUES(160,'Laparoscopic',39);
INSERT INTO MenuOption VALUES(161,'Laparoscopic and Open',39);

INSERT INTO MenuOption VALUES(162,'R0',40);
INSERT INTO MenuOption VALUES(163,'R1',40);
INSERT INTO MenuOption VALUES(164,'R2',40);
INSERT INTO MenuOption VALUES(165,'RX',40);
INSERT INTO MenuOption VALUES(166,'Not known',40);

INSERT INTO MenuOption VALUES(167,'Adjuvant',41);
INSERT INTO MenuOption VALUES(168,'Palliative',41);

INSERT INTO MenuOption VALUES(169,'Progressive',42);
INSERT INTO MenuOption VALUES(170,'Stable',42);
INSERT INTO MenuOption VALUES(171,'Objective Response',42);
INSERT INTO MenuOption VALUES(172,'Unknown',42);

INSERT INTO MenuOption VALUES(173,'Alive with disease',43);
INSERT INTO MenuOption VALUES(174,'Alive without evidence disease',43);
INSERT INTO MenuOption VALUES(175,'Death related to ACC or treatment toxicity',43);
INSERT INTO MenuOption VALUES(176,'Death not related to ACC',43);
INSERT INTO MenuOption VALUES(177,'Other',43);

INSERT INTO MenuOption VALUES(178,'Normal',44);
INSERT INTO MenuOption VALUES(179,'Left ventricular hypertrophy',44);

INSERT INTO MenuOption VALUES(180,'Atrial fibrillation',45);
INSERT INTO MenuOption VALUES(181,'Left ventricular hypertrophy',45);
INSERT INTO MenuOption VALUES(182,'Normal',45);
INSERT INTO MenuOption VALUES(183,'Other',45);

INSERT INTO MenuOption VALUES(184,'Stroke',46);
INSERT INTO MenuOption VALUES(185,'Cerebrovascular stenosis',46);
INSERT INTO MenuOption VALUES(186,'Angina pectoris',46);
INSERT INTO MenuOption VALUES(187,'Myocardial infarction',46);
INSERT INTO MenuOption VALUES(188,'Chronic heart failure',46);
INSERT INTO MenuOption VALUES(189,'Atrial fibrillation',46);
INSERT INTO MenuOption VALUES(190,'Coronary angioplasty',46);
INSERT INTO MenuOption VALUES(191,'Hypertensive emergency',46);
INSERT INTO MenuOption VALUES(192,'Other atrial dysrhythmia',46);
INSERT INTO MenuOption VALUES(193,'Other ventrical dysrhythmia',46);
INSERT INTO MenuOption VALUES(194,'Coronary artery bypass',46);
INSERT INTO MenuOption VALUES(195,'Retinopathy',46);
INSERT INTO MenuOption VALUES(196,'Peripheral arterial occlusive disease',46);
INSERT INTO MenuOption VALUES(197,'Diabetic foot syndrome',46);
INSERT INTO MenuOption VALUES(198,'Chronic renal failure',46);
INSERT INTO MenuOption VALUES(199,'Deep vein thrombosis',46);
INSERT INTO MenuOption VALUES(200,'Osteoporosis',46);
INSERT INTO MenuOption VALUES(201,'Neoplasma',46);
INSERT INTO MenuOption VALUES(202,'Sleep apnea',46);
INSERT INTO MenuOption VALUES(203,'Hypokalemia',46);
INSERT INTO MenuOption VALUES(204,'Hyperkalemia',46);
INSERT INTO MenuOption VALUES(205,'Spine fracture',46);
INSERT INTO MenuOption VALUES(206,'Radial fracture',46);
INSERT INTO MenuOption VALUES(207,'Hip fracture',46);

INSERT INTO MenuOption VALUES(208,'Yes',47);
INSERT INTO MenuOption VALUES(209,'No',47);
INSERT INTO MenuOption VALUES(210,'Former smoker',47);

INSERT INTO MenuOption VALUES(211,'Normal',48);
INSERT INTO MenuOption VALUES(212,'Hyperplasia',48);
INSERT INTO MenuOption VALUES(213,'Tumor',48);

INSERT INTO MenuOption VALUES(214,'Right adrenal',49);
INSERT INTO MenuOption VALUES(215,'Left adrenal',49);
INSERT INTO MenuOption VALUES(216,'Both adrenals',49);
INSERT INTO MenuOption VALUES(217,'No tumor',49);

INSERT INTO MenuOption VALUES(218,'Not available',50);
INSERT INTO MenuOption VALUES(219,'Tumor seen',50);
INSERT INTO MenuOption VALUES(220,'Tumor not seen',50);








---- Parameter re-naming (mainly in the Identification table)

UPDATE Parameter SET param_name='year_of_birth' WHERE param_id=1;
UPDATE Parameter SET param_name='local_investigator' WHERE param_id=4;
UPDATE Parameter SET param_name='date_first_reg' WHERE param_id=6;
UPDATE Parameter SET param_name='consent_obtained' WHERE param_id=7;

UPDATE Parameter SET param_type='number' WHERE param_name='year_of_birth';

---- Adding the validation field ("optional")

ALTER TABLE Parameter ADD COLUMN param_optional VARCHAR(10) AFTER param_table;
UPDATE Parameter SET param_optional='false';


---- These are now random bug-fixes throughout the schema

UPDATE Parameter SET param_sub_param=0 WHERE param_name='center_id';


---- Have to set parent menus as "Yes/No" (menu 4) only - else it gets all mixed up with the CSS selector

UPDATE Parameter SET menu=4 WHERE param_name='other_malignancies';
UPDATE Parameter SET menu=4 WHERE param_name='hormonal_hypersecretion';
UPDATE Parameter SET menu=4 WHERE param_id=78 OR param_id=83 OR param_id=85 OR param_id=87 OR param_id=89 OR param_id=91;

UPDATE Parameter SET param_label='Associated Study (Phase/Visit)' WHERE param_id=120;
UPDATE Parameter SET param_type='dynamicmenu' WHERE param_id=120;
UPDATE Parameter SET param_sub_param=129 WHERE param_id=130;
UPDATE Parameter SET param_sub_param=132 WHERE param_id=133;
UPDATE Parameter SET param_sub_param=134 WHERE param_id=135;
UPDATE Parameter SET param_sub_param=136 WHERE param_id=137;




