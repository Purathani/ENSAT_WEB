-- ENSAT DATABASE SCHEMA FILE (ACC V2 - AJS, 03/05/10)----

USE ensat_v3;

-- Removes tables if previously created
DROP TABLE Identification;
DROP TABLE ACC_DiagnosticProcedures;
DROP TABLE ACC_TumorStaging;

DROP TABLE ACC_Biomaterial;

DROP TABLE ACC_Radiofrequency;
DROP TABLE ACC_Radiofrequency_Loc;
DROP TABLE ACC_Surgery;
DROP TABLE ACC_Surgery_First;
DROP TABLE ACC_Surgery_Extended;
DROP TABLE ACC_Pathology;
DROP TABLE ACC_Chemotherapy;
DROP TABLE ACC_Chemotherapy_Regimen;
DROP TABLE ACC_Radiotherapy;
DROP TABLE ACC_Radiotherapy_Loc;
DROP TABLE ACC_FollowUp;
DROP TABLE ACC_FollowUp_Organs;

DROP TABLE ACC_Chemoembolisation;
DROP TABLE ACC_Mitotane;

DROP TABLE Pheo_PatientHistory;

DROP TABLE Pheo_FirstDiagnosisPresentation;
DROP TABLE Pheo_OtherOrgans;
DROP TABLE Pheo_TumorDetails;

DROP TABLE Pheo_ClinicalAssessment;
DROP TABLE Pheo_BiochemicalAssessment;
DROP TABLE Pheo_ImagingTests;
DROP TABLE Pheo_Surgery;
DROP TABLE Pheo_Biomaterial;
DROP TABLE Pheo_NonSurgicalInterventions;
DROP TABLE Pheo_FollowUp;

DROP TABLE NAPACA_DiagnosticProcedures;

DROP TABLE NAPACA_FollowUp;
DROP TABLE NAPACA_Biomaterial;
DROP TABLE NAPACA_Imaging;
DROP TABLE NAPACA_Surgery;
DROP TABLE NAPACA_Pathology;

DROP TABLE APA_PatientHistory;

DROP TABLE APA_ClinicalAssessment;
DROP TABLE APA_BiochemicalAssessment;
DROP TABLE APA_Imaging;
DROP TABLE APA_Cardio;
DROP TABLE APA_Complications;
DROP TABLE APA_Surgery;
DROP TABLE APA_FollowUp;
DROP TABLE APA_Biomaterial;


-- Now create the tables ----

-- Identification table
CREATE TABLE Identification (
	ensat_id		INTEGER		AUTO_INCREMENT	 	NOT NULL,
	center_id			VARCHAR(5) NOT NULL,
	local_investigator	VARCHAR(30),
	investigator_email	VARCHAR(50),
	record_date			DATE,
	date_first_reg		DATE,
	sex				VARCHAR(1),
	year_of_birth		VARCHAR(4),
	consent_obtained		VARCHAR(30),
	uploader			VARCHAR(30),
	ensat_database		VARCHAR(10),
	PRIMARY KEY(ensat_id,center_id)
);

-- ACC_DiagnosticProcedures table
CREATE TABLE ACC_DiagnosticProcedures (
	ensat_id			INTEGER	NOT NULL,
	center_id			VARCHAR(5)	NOT NULL,                                                                                                                                                                                                                                                                                                                                                       
	date_of_diagnosis		DATE,
	disease_status		VARCHAR(30),
	modality_of_diagnosis	VARCHAR(30),
	height		FLOAT,
	weight		FLOAT,
	bmi			FLOAT,
	symptoms_diag_tumor_mass	VARCHAR(3),
	symptoms_incidental		VARCHAR(3),
	symptoms_paraneoplastic		VARCHAR(3),
	symptoms_endocrine		VARCHAR(9),
	cushings_syndrome		VARCHAR(9),
	virilisation		VARCHAR(9),
	feminization		VARCHAR(14),
	mineralocorticoid_excess	VARCHAR(9),
	hypertension		VARCHAR(9),
	hypokalemia			VARCHAR(9),
	diabetes			VARCHAR(9),
	hormonal_hypersecretion	VARCHAR(9),
	glucocorticoids		VARCHAR(9),
	androgens			VARCHAR(9),
	estrogens			VARCHAR(9),
	mineralocorticoids	VARCHAR(9),
	precursor_secretion	VARCHAR(9),
	acc_during_pregnancy	VARCHAR(3),
	other_malignancies	VARCHAR(7),
	which_malignancies	VARCHAR(100),
	PRIMARY KEY(ensat_id,center_id),
	FOREIGN KEY(ensat_id,center_id) REFERENCES Identification(ensat_id,center_id)
);

-- ACC_TumorStaging table
CREATE TABLE ACC_TumorStaging (
	ensat_id			INTEGER	NOT NULL,
	center_id			VARCHAR(5)	NOT NULL,                                                                                                                                                                                                                                                                                                                                                       
	site_of_adrenal_tumor	VARCHAR(5),
	information_based_on		VARCHAR(30),
	size_of_adrenal_tumor	FLOAT,
	regional_lymph_nodes	VARCHAR(14),
	tumor_infiltration_adipose	VARCHAR(14),
	tumor_invasion_adjacent		VARCHAR(14),
	tumor_thrombus_vena_renal	VARCHAR(14),
	distant_metastases	VARCHAR(9),
	bone				VARCHAR(30),
	liver				VARCHAR(30),
	lung				VARCHAR(30),
	abdomen_lymph_nodes	VARCHAR(100),
	other_metastases		VARCHAR(100),
	ensat_classification	VARCHAR(30),
	PRIMARY KEY(ensat_id,center_id),
	FOREIGN KEY(ensat_id,center_id) REFERENCES Identification(ensat_id,center_id)
);

-- ACC_Biomaterial table
CREATE TABLE ACC_Biomaterial (
	acc_biomaterial_id		INTEGER 	AUTO_INCREMENT	NOT NULL,
	ensat_id			INTEGER	NOT NULL,
	center_id			VARCHAR(5)	NOT NULL,
	biomaterial_date		DATE,
	tumor_tissue_frozen	VARCHAR(14),
	tumor_tissue_ensat_sop	VARCHAR(14),
	tumor_tissue_paraffin	VARCHAR(14),
	leukocyte_dna		VARCHAR(14),
	plasma			VARCHAR(14),
	serum				VARCHAR(14),
	24h_urine			VARCHAR(14),
	24h_urine_vol		FLOAT,
	spot_urine			VARCHAR(14),
	normal_tissue		VARCHAR(14),
	normal_tissue_specify	VARCHAR(100),	
	PRIMARY KEY(acc_biomaterial_id, ensat_id,center_id),
	FOREIGN KEY(ensat_id,center_id) REFERENCES Identification(ensat_id,center_id)
);


-- ACC_Radiofrequency table
CREATE TABLE ACC_Radiofrequency (
	acc_radiofrequency_id	INTEGER		AUTO_INCREMENT	NOT NULL,
	ensat_id	INTEGER		NOT NULL,
	center_id			VARCHAR(5)	NOT NULL,
	radiofrequency_date		DATE,
	radiofrequency_type	VARCHAR(10),
	radiofrequency_best_objective		VARCHAR(30),
	radiofrequency_eval_date	DATE,
	PRIMARY KEY(acc_radiofrequency_id, ensat_id, center_id),
	FOREIGN KEY(ensat_id,center_id) REFERENCES Identification(ensat_id,center_id)
);

-- ACC_Radiofrequency_Loc table
CREATE TABLE ACC_Radiofrequency_Loc (
	acc_radiofrequency_loc_id 	INTEGER	AUTO_INCREMENT	NOT NULL,
	acc_radiofrequency_id		INTEGER	NOT NULL,
	ensat_id				INTEGER	NOT NULL,
	center_id			VARCHAR(5)	NOT NULL,
	radiofrequency_location		VARCHAR(30),
	PRIMARY KEY(acc_radiofrequency_loc_id, acc_radiofrequency_id, ensat_id, center_id),
	FOREIGN KEY(acc_radiofrequency_id) REFERENCES ACC_Radiofrequency(acc_radiofrequency_id),
	FOREIGN KEY(ensat_id,center_id) REFERENCES Identification(ensat_id,center_id)
);

-- ACC_Surgery table
CREATE TABLE ACC_Surgery (
	acc_surgery_id	INTEGER		AUTO_INCREMENT	NOT NULL,
	ensat_id	INTEGER		NOT NULL,
	center_id			VARCHAR(5)	NOT NULL,
	surgery_date		DATE,
	surgery_type	VARCHAR(30),
	surgery_method	VARCHAR(30),
	surgery_overall_resection_status	VARCHAR(30),
	PRIMARY KEY(acc_surgery_id, ensat_id, center_id),
	FOREIGN KEY(ensat_id,center_id) REFERENCES Identification(ensat_id,center_id)
	
);

-- ACC_Surgery_First table
CREATE TABLE ACC_Surgery_First (
	acc_surgery_first_id 	INTEGER	AUTO_INCREMENT	NOT NULL,
	acc_surgery_id		INTEGER	NOT NULL,
	ensat_id				INTEGER	NOT NULL,
	center_id			VARCHAR(5)	NOT NULL,
	surgery_first		VARCHAR(30),
	PRIMARY KEY(acc_surgery_first_id, acc_surgery_id, ensat_id, center_id),
	FOREIGN KEY(acc_surgery_id) REFERENCES ACC_Surgery(acc_surgery_id),
	FOREIGN KEY(ensat_id,center_id) REFERENCES Identification(ensat_id,center_id)
);

-- ACC_Surgery_Extended table
CREATE TABLE ACC_Surgery_Extended (
	acc_surgery_extended_id 	INTEGER	AUTO_INCREMENT	NOT NULL,
	acc_surgery_id		INTEGER	NOT NULL,
	ensat_id				INTEGER	NOT NULL,
	center_id			VARCHAR(5)	NOT NULL,
	surgery_extended		VARCHAR(30),
	PRIMARY KEY(acc_surgery_extended_id, acc_surgery_id, ensat_id, center_id),
	FOREIGN KEY(acc_surgery_id) REFERENCES ACC_Surgery(acc_surgery_id),
	FOREIGN KEY(ensat_id,center_id) REFERENCES Identification(ensat_id,center_id)
);


-- ACC_Mitotane table
CREATE TABLE ACC_Mitotane (
	acc_mitotane_id	INTEGER		AUTO_INCREMENT	NOT NULL,
	ensat_id	INTEGER		NOT NULL,
	center_id			VARCHAR(5)	NOT NULL,
	mitotane_date		DATE,
	mitotane_initiation		DATE,
	mitotane_end		DATE,
	mitotane_indication	VARCHAR(30),
	mitotane_best_objective		VARCHAR(30),
	mitotane_eval_date		DATE,
	mitotane_max_dosage	FLOAT,
	mitotane_max_level	FLOAT,
	PRIMARY KEY(acc_mitotane_id, ensat_id, center_id),
	FOREIGN KEY(ensat_id,center_id) REFERENCES Identification(ensat_id,center_id)	
);

-- ACC_Chemotherapy table
CREATE TABLE ACC_Chemotherapy (
	acc_chemotherapy_id	INTEGER		AUTO_INCREMENT	NOT NULL,
	ensat_id	INTEGER		NOT NULL,
	center_id		VARCHAR(5)	NOT NULL,
	chemotherapy_date		DATE,
	chemotherapy_initiation		DATE,
	chemotherapy_end			DATE,
	chemotherapy_indication		VARCHAR(30),
	chemotherapy_best_objective	VARCHAR(30),
	chemotherapy_eval_date		DATE,
	PRIMARY KEY(acc_chemotherapy_id, ensat_id, center_id),
	FOREIGN KEY(ensat_id,center_id) REFERENCES Identification(ensat_id,center_id)	
);

-- ACC_Chemotherapy_Regimen table
CREATE TABLE ACC_Chemotherapy_Regimen (
	acc_chemotherapy_regimen_id 	INTEGER	AUTO_INCREMENT	NOT NULL,
	acc_chemotherapy_id		INTEGER	NOT NULL,
	ensat_id				INTEGER	NOT NULL,
	center_id			VARCHAR(5)	NOT NULL,
	chemotherapy_regimen		VARCHAR(30),
	PRIMARY KEY(acc_chemotherapy_regimen_id, acc_chemotherapy_id, ensat_id, center_id),
	FOREIGN KEY(acc_chemotherapy_id) REFERENCES ACC_Chemotherapy(acc_chemotherapy_id),
	FOREIGN KEY(ensat_id,center_id) REFERENCES Identification(ensat_id,center_id)
);


-- ACC_Radiotherapy table
CREATE TABLE ACC_Radiotherapy (
	acc_radiotherapy_id	INTEGER		AUTO_INCREMENT	NOT NULL,
	ensat_id	INTEGER		NOT NULL,
	center_id			VARCHAR(5)	NOT NULL,
	radiotherapy_date		DATE,
	radiotherapy_initiation		DATE,
	radiotherapy_end			DATE,
	radiotherapy_type		VARCHAR(10),
	radiotherapy_indication		VARCHAR(30),
	radiotherapy_best_objective	VARCHAR(30),
	radiotherapy_eval_date	DATE,
	PRIMARY KEY(acc_radiotherapy_id, ensat_id, center_id),
	FOREIGN KEY(ensat_id,center_id) REFERENCES Identification(ensat_id,center_id)	
);

-- ACC_Radiotherapy_Loc table
CREATE TABLE ACC_Radiotherapy_Loc (
	acc_radiotherapy_loc_id 	INTEGER	AUTO_INCREMENT	NOT NULL,
	acc_radiotherapy_id		INTEGER	NOT NULL,
	ensat_id				INTEGER	NOT NULL,
	center_id			VARCHAR(5)	NOT NULL,
	radiotherapy_location		VARCHAR(30),
	PRIMARY KEY(acc_radiotherapy_loc_id, acc_radiotherapy_id, ensat_id, center_id),
	FOREIGN KEY(acc_radiotherapy_id) REFERENCES ACC_Radiotherapy(acc_radiotherapy_id),
	FOREIGN KEY(ensat_id,center_id) REFERENCES Identification(ensat_id,center_id)
);


-- ACC_Chemoembolisation table
CREATE TABLE ACC_Chemoembolisation (
	acc_chemoembolisation_id	INTEGER		AUTO_INCREMENT	NOT NULL,
	ensat_id	INTEGER		NOT NULL,
	center_id			VARCHAR(5)		NOT NULL,
	chemoembolisation_date	DATE,
	chemoembolisation_type		VARCHAR(10),
	chemoembolisation_best_objective	VARCHAR(30),
	chemoembolisation_eval_date	DATE,
	PRIMARY KEY(acc_chemoembolisation_id, ensat_id, center_id),
	FOREIGN KEY(ensat_id,center_id) REFERENCES Identification(ensat_id,center_id)	
);

-- ACC_FollowUp table
CREATE TABLE ACC_FollowUp (
	acc_followup_id		INTEGER 	AUTO_INCREMENT	NOT NULL,
	ensat_id		INTEGER	NOT NULL,
	center_id			VARCHAR(5)	NOT NULL,
	followup_date		DATE,
	patient_status		VARCHAR(100),
	followup_comment		VARCHAR(100),
	PRIMARY KEY(acc_followup_id, ensat_id, center_id),
	FOREIGN KEY(ensat_id,center_id) REFERENCES Identification(ensat_id,center_id)
);

-- ACC_FollowUp_Organs table
CREATE TABLE ACC_FollowUp_Organs (
	acc_followup_organs_id 	INTEGER	AUTO_INCREMENT	NOT NULL,
	acc_followup_id		INTEGER	NOT NULL,
	ensat_id				INTEGER	NOT NULL,
	center_id			VARCHAR(5)	NOT NULL,
	followup_organs		VARCHAR(30),
	PRIMARY KEY(acc_followup_organs_id, acc_followup_id, ensat_id, center_id),
	FOREIGN KEY(acc_followup_id) REFERENCES ACC_FollowUp(acc_followup_id),
	FOREIGN KEY(ensat_id,center_id) REFERENCES Identification(ensat_id,center_id)
);

-- ACC_Pathology table
CREATE TABLE ACC_Pathology (
	acc_pathology_id 		INTEGER	AUTO_INCREMENT	NOT NULL,
	ensat_id			INTEGER	NOT NULL,
	center_id			VARCHAR(5)	NOT NULL,
	pathology_date	DATE,
	pathology_derived_from	VARCHAR(10),
	surgery_biopsy_date		DATE,
	local_pathologist		VARCHAR(30),
	central_pathology_review	VARCHAR(3),
	central_review_pathologist	VARCHAR(30),
	number_of_mitoses_exact		VARCHAR(14),
	ki67				VARCHAR(14),
	loss_of_heterozygosity	VARCHAR(14),
	igf_overexpression	VARCHAR(14),
	weiss_score			VARCHAR(14),
	nuclear_atypia		VARCHAR(14),
	atypical_mitosis		VARCHAR(14),
	spongiocytic_tumor_cells	VARCHAR(14),
	diffuse_architecture	VARCHAR(14),
	venous_invasion		VARCHAR(14),
	sinus_invasion		VARCHAR(14),
	capsular_invasion		VARCHAR(14),
	necrosis			VARCHAR(14),
	number_of_mitoses_per5	VARCHAR(14),
	PRIMARY KEY(acc_pathology_id, ensat_id, center_id),
	FOREIGN KEY(ensat_id,center_id) REFERENCES Identification(ensat_id,center_id)
);

-- Pheo_PatientHistory table
CREATE TABLE Pheo_PatientHistory (
	ensat_id			INTEGER	NOT NULL,
	center_id			VARCHAR(5)	NOT NULL,                                                                                                                                                                                                                                                                                                                                                       
	history_of_hypertension			VARCHAR(3),
	year_of_hypertension_diagnosis	VARCHAR(4),
	height_at_time_consent	FLOAT,
	weight_at_time_consent	FLOAT,
	systolic_bp_at_time_consent	FLOAT,
	diastolic_bp_at_time_consent	FLOAT,
	pheo_operation_before_consent_date	VARCHAR(3),
	residual_disease		VARCHAR(3),
	disease_metastatic		VARCHAR(3),
	multiple_tumors		VARCHAR(3),
	relatives_with_tumors	INTEGER,
	doc_genetic_disease	VARCHAR(3),
	phenotypic_diagnosis	VARCHAR(30),
	history_non_ppgl_tumor	VARCHAR(3),
	further_detail		VARCHAR(100),
	pheo_comment		VARCHAR(100),
	PRIMARY KEY(ensat_id,center_id),
	FOREIGN KEY(ensat_id,center_id) REFERENCES Identification(ensat_id,center_id)
);

-- Pheo_FirstDiagnosisPresentation table
CREATE TABLE Pheo_FirstDiagnosisPresentation (
	pheo_first_diagnosis_presentation_id 	INTEGER	AUTO_INCREMENT	NOT NULL,
	ensat_id				INTEGER	NOT NULL,
	center_id				VARCHAR(5)	NOT NULL,
	first_diagnosis_presentation	VARCHAR(100),
	PRIMARY KEY(pheo_first_diagnosis_presentation_id,ensat_id,center_id),
	FOREIGN KEY(ensat_id,center_id) REFERENCES Identification(ensat_id,center_id)
);

-- Pheo_TumorDetails table
CREATE TABLE Pheo_TumorDetails (
	pheo_tumor_details_id 	INTEGER	AUTO_INCREMENT	NOT NULL,
	ensat_id				INTEGER	NOT NULL,
	center_id			VARCHAR(5)	NOT NULL,
	tumor_date			DATE,
	largest_size_x		VARCHAR(10),
	largest_size_y		VARCHAR(10),
	largest_size_z		VARCHAR(10),
	tumor_resected		VARCHAR(4),
	tumor_a_or_e		VARCHAR(30),
	tumor_site			VARCHAR(30),
	multiple_primaries	VARCHAR(4),
	tumor_distant_metastases	VARCHAR(4),
	metastases_location	VARCHAR(30),
	diagnosis_method		VARCHAR(30),
	PRIMARY KEY(pheo_tumor_details_id,ensat_id,center_id),
	FOREIGN KEY(ensat_id,center_id) REFERENCES Identification(ensat_id,center_id)
);

-- Pheo_OtherOrgans table
CREATE TABLE Pheo_OtherOrgans (
	pheo_other_organs_id 	INTEGER	AUTO_INCREMENT	NOT NULL,
	ensat_id				INTEGER	NOT NULL,
	center_id			VARCHAR(5)	NOT NULL,
	other_organ			VARCHAR(100),
	PRIMARY KEY(pheo_other_organs_id,ensat_id,center_id),
	FOREIGN KEY(ensat_id,center_id) REFERENCES Identification(ensat_id,center_id)
);


-- Pheo_ClinicalAssessment table
CREATE TABLE Pheo_ClinicalAssessment (
	clinical_assessment_id	INTEGER	AUTO_INCREMENT	NOT NULL,
	ensat_id			INTEGER	NOT NULL,
	center_id			VARCHAR(5)	NOT NULL,
	assessment_date		DATE,
	height			FLOAT,
	weight			FLOAT,
	systolic_blood_pressure			FLOAT,
	diastolic_blood_pressure		FLOAT,
	PRIMARY KEY(clinical_assessment_id,ensat_id,center_id),
	FOREIGN KEY(ensat_id,center_id) REFERENCES Identification(ensat_id,center_id)
);

-- Pheo_BiochemicalAssessment table
CREATE TABLE Pheo_BiochemicalAssessment (
	biochemical_assessment_id	INTEGER	AUTO_INCREMENT	NOT NULL,
	ensat_id			INTEGER	NOT NULL,
	center_id			VARCHAR(5)	NOT NULL,
	plasma_e	FLOAT,
	plasma_n	FLOAT,
	plasma_date		DATE,
	plasma_free_m	FLOAT,
	plasma_free_n	FLOAT,
	plasma_free_methox	FLOAT,
	plasma_free_date	DATE,
	serum_chromo_a	FLOAT,
	serum_chromo_a_date		DATE,
	urine_free_e	FLOAT,
	urine_free_n	FLOAT,
	urine_free_date	DATE,
	urine_m		FLOAT,
	urine_n		FLOAT,
	urine_date		DATE,
	plasma_dopamine_conc	FLOAT,
	plasma_dopamine_date	DATE,
	PRIMARY KEY(biochemical_assessment_id,ensat_id,center_id),
	FOREIGN KEY(ensat_id,center_id) REFERENCES Identification(ensat_id,center_id)
);

-- Pheo_ImagingTests table
CREATE TABLE Pheo_ImagingTests (
	pheo_imaging_tests_id	INTEGER	AUTO_INCREMENT	NOT NULL,
	ensat_id			INTEGER	NOT NULL,
	center_id			VARCHAR(5)	NOT NULL,
	ct				VARCHAR(30),
	ct_date			DATE,
	ct_primary			INTEGER,
	ct_metastases		INTEGER,
	nmr				VARCHAR(30),
	nmr_date			DATE,
	nmr_primary			INTEGER,
	nmr_metastases		INTEGER,
	mibg				VARCHAR(30),
	mibg_date			DATE,
	mibg_primary		INTEGER,
	mibg_metastases		INTEGER,
	octreoscan			VARCHAR(30),
	octreoscan_date			DATE,
	octreoscan_primary		INTEGER,
	octreoscan_metastases		INTEGER,
	fdg_pet				VARCHAR(30),
	fdg_pet_date			DATE,
	fdg_pet_primary			INTEGER,
	fdg_pet_metastases		INTEGER,
	da_pet				VARCHAR(30),
	da_pet_date				DATE,
	da_pet_primary			INTEGER,
	da_pet_metastases			INTEGER,
	synthesis_imaging_workup	DATE,
	imaging_comment			VARCHAR(100),
	other_imaging_pet				VARCHAR(30),
	other_imaging_date				DATE,
	other_imaging_primary			INTEGER,
	other_imaging_metastases			INTEGER,
	PRIMARY KEY(pheo_imaging_tests_id, ensat_id,center_id),
	FOREIGN KEY(ensat_id,center_id) REFERENCES Identification(ensat_id,center_id)
);




-- Pheo_Biomaterial table
CREATE TABLE Pheo_Biomaterial (
	pheo_biomaterial_id		INTEGER 	AUTO_INCREMENT	NOT NULL,
	ensat_id			INTEGER	NOT NULL,
	center_id			VARCHAR(5)	NOT NULL,
	serum				VARCHAR(3),
	serum_sampling_date		DATE,
	plasma				VARCHAR(3),
	plasma_sampling_date		DATE,
	lymphocytes				VARCHAR(3),
	lymphocytes_sampling_date		DATE,
	urine				VARCHAR(3),
	urine_sampling_date		DATE,
	tumor_specimen				VARCHAR(30),
	tumor_specimen_sampling_date		DATE,
	PRIMARY KEY(pheo_biomaterial_id, ensat_id,center_id),
	FOREIGN KEY(ensat_id,center_id) REFERENCES Identification(ensat_id,center_id)
);


-- Pheo_FollowUp table
CREATE TABLE Pheo_FollowUp (
	pheo_followup_id		INTEGER 	AUTO_INCREMENT	NOT NULL,
	ensat_id		INTEGER	NOT NULL,
	center_id			VARCHAR(5)	NOT NULL,
	followup_date		DATE,
	alive				VARCHAR(3),
	phpgl_free			VARCHAR(3),
	date_of_recurrence	DATE,
	date_of_death		DATE,
	cause_of_death	VARCHAR(30),
	comment		VARCHAR(100),
	PRIMARY KEY(pheo_followup_id, ensat_id,center_id),
	FOREIGN KEY(ensat_id,center_id) REFERENCES Identification(ensat_id,center_id)
);

-- Pheo_NonSurgicalInterventions table
CREATE TABLE Pheo_NonSurgicalInterventions (
	pheo_non_surgical_interventions_id	INTEGER	AUTO_INCREMENT	NOT NULL,
	ensat_id			INTEGER	NOT NULL,
	center_id			VARCHAR(5)	NOT NULL,
	mibg_therapy				VARCHAR(3),
	mibg_therapy_date			DATE,
	mibg_therapy_comment			VARCHAR(30),
	other_targeted_radiotherapy				VARCHAR(3),
	other_targeted_radiotherapy_date			DATE,
	other_targeted_radiotherapy_comment			VARCHAR(30),
	chemotherapy				VARCHAR(3),
	chemotherapy_date			DATE,
	chemotherapy_comment			VARCHAR(30),
	radiofrequency_ablation				VARCHAR(3),
	radiofrequency_ablation_date			DATE,
	radiofrequency_ablation_comment			VARCHAR(30),
	external_radiotherapy				VARCHAR(3),
	external_radiotherapy_date			DATE,
	external_radiotherapy_comment			VARCHAR(30),
	targeted_molecular_therapy				VARCHAR(3),
	targeted_molecular_therapy_date			DATE,
	targeted_molecular_therapy_comment			VARCHAR(30),
	chemoembolisation						VARCHAR(3),
	chemoembolisation_date					DATE,
	chemoembolisation_comment				VARCHAR(30),
	PRIMARY KEY(pheo_non_surgical_interventions_id, ensat_id,center_id),
	FOREIGN KEY(ensat_id,center_id) REFERENCES Identification(ensat_id,center_id)
);




-- Pheo_Surgery table
CREATE TABLE Pheo_Surgery (
	pheo_surgery_id 		INTEGER	AUTO_INCREMENT	NOT NULL,
	ensat_id			INTEGER	NOT NULL,
	center_id			VARCHAR(5)	NOT NULL,
	surgery_date		DATE,
	surgery_procedure				VARCHAR(30),
	evidence_of_malignancy		VARCHAR(3),
	evidence_of_locoregional_spread	VARCHAR(30),
	comment		VARCHAR(100),
	proliferative_index		FLOAT,
	ki67_numbers			FLOAT,
	mitotic_count_numbers		FLOAT,
	cell_count_numbers		FLOAT,
	necrosis				VARCHAR(30),
	capsular_adipose_invasion	VARCHAR(30),
	vascular_invasion			VARCHAR(30),
	ingrowth_in_adjacent			VARCHAR(30),
	PRIMARY KEY(pheo_surgery_id, ensat_id,center_id),
	FOREIGN KEY(ensat_id,center_id) REFERENCES Identification(ensat_id,center_id)
);

-- NAPACA_DiagnosticProcedures table
CREATE TABLE NAPACA_DiagnosticProcedures (
	ensat_id			INTEGER	NOT NULL,
	center_id			VARCHAR(5)	NOT NULL,                                                                                                                                                                                                                                                                                                                                                       
	height		FLOAT,
	weight		FLOAT,
	bmi			FLOAT,
	year_of_diagnosis		VARCHAR(4),
	month_of_diagnosis		VARCHAR(4),
	symptoms_incidental		VARCHAR(3),
	symptoms_endocrine		VARCHAR(9),
	cushings_syndrome		VARCHAR(9),
	virilisation		VARCHAR(9),
	feminization		VARCHAR(14),
	mineralocorticoid_excess	VARCHAR(9),
	hypertension_presentation	VARCHAR(3),
	hypertension_year			VARCHAR(4),
	diabetestype2_presentation	VARCHAR(3),
	diabetestype2_year			VARCHAR(4),
	dyslipidaemia_presentation	VARCHAR(3),
	dyslipidaemia_year			VARCHAR(4),
	osteoporosis_presentation	VARCHAR(3),
	osteoporosis_year			VARCHAR(4),
	prev_cardio_events		VARCHAR(3),
	prev_cardio_year1			VARCHAR(4),
	prev_cardio_year2			VARCHAR(4),
	antidiabetic_drugs		VARCHAR(3),
	lipidlowering_drugs		VARCHAR(3),
	osteoporosis_drugs		VARCHAR(3),
	antihypertensive_drugs		VARCHAR(3),
	gluco_serum_cortisol				VARCHAR(30),
	gluco_serum_cortisol_specific			VARCHAR(30),
	gluco_plasma_acth					VARCHAR(30),
	gluco_plasma_acth_specific			VARCHAR(30),
	gluco_urinary_free_cortisol			VARCHAR(30),
	gluco_urinary_free_cortisol_specific	VARCHAR(30),
	gluco_urinary_free_method			VARCHAR(5),
	mineralo_plasma_renin_activity		VARCHAR(30),
	mineralo_plasma_renin_conc			VARCHAR(30),
	mineralo_serum_aldosterone			VARCHAR(30),
	other_steroid_17hydroxyprogesterone		VARCHAR(30),
	other_steroid_17hydroxyprogesterone_specific	VARCHAR(30),
	other_steroid_serum_dheas			VARCHAR(30),
	other_steroid_serum_dheas_specific		VARCHAR(30),
	catechol_urinary_free_excretion		VARCHAR(30),
	catechol_urinary_metanephrine_excretion	VARCHAR(30),
	catechol_plasma_metanephrines			VARCHAR(30),
	other_malignancies	VARCHAR(9),
	which_malignancies	VARCHAR(100),
	year_other_maligs		VARCHAR(4),
	PRIMARY KEY(ensat_id,center_id),
	FOREIGN KEY(ensat_id,center_id) REFERENCES Identification(ensat_id,center_id)
);

-- NAPACA_FollowUp table
CREATE TABLE NAPACA_FollowUp (
	napaca_followup_id		INTEGER 	AUTO_INCREMENT	NOT NULL,
	ensat_id		INTEGER	NOT NULL,
	center_id			VARCHAR(5)	NOT NULL,
	followup_date		DATE,
	followup_alive		VARCHAR(4),
	followup_imaging		VARCHAR(4),
	followup_imaging_type	VARCHAR(3),
	followup_max_tumor	FLOAT,
	followup_changes_hormone_secretion	VARCHAR(4),
	gluco_serum_cortisol				VARCHAR(30),
	gluco_plasma_acth					VARCHAR(30),
	gluco_urinary_free_cortisol			VARCHAR(30),
	mineralo_plasma_renin_activity		VARCHAR(30),
	mineralo_plasma_renin_conc			VARCHAR(30),
	mineralo_serum_aldosterone			VARCHAR(30),
	other_steroid_17hydroxyprogesterone		VARCHAR(30),
	other_steroid_serum_dheas			VARCHAR(30),
	catechol_urinary_free_excretion		VARCHAR(30),
	catechol_urinary_metanephrine_excretion	VARCHAR(30),
	catechol_plasma_metanephrines			VARCHAR(30),
	further_plans		VARCHAR(30),
	PRIMARY KEY(napaca_followup_id, ensat_id, center_id),
	FOREIGN KEY(ensat_id,center_id) REFERENCES Identification(ensat_id,center_id)
);

-- NAPACA_Biomaterial table
CREATE TABLE NAPACA_Biomaterial (
	napaca_biomaterial_id		INTEGER 	AUTO_INCREMENT	NOT NULL,
	ensat_id			INTEGER	NOT NULL,
	center_id			VARCHAR(5)	NOT NULL,
	biomaterial_date		DATE,
	leukocyte_dna		VARCHAR(14),
	plasma			VARCHAR(14),
	serum				VARCHAR(14),
	24h_urine			VARCHAR(14),
	24h_urine_vol		FLOAT,
	spot_urine			VARCHAR(14),
	tumor_tissue_frozen	VARCHAR(14),
	tumor_tissue_ensat_sop	VARCHAR(14),
	tumor_tissue_paraffin	VARCHAR(14),
	normal_tissue		VARCHAR(14),
	normal_tissue_specify	VARCHAR(100),
	PRIMARY KEY(napaca_biomaterial_id, ensat_id,center_id),
	FOREIGN KEY(ensat_id,center_id) REFERENCES Identification(ensat_id,center_id)
);

-- NAPACA_Imaging table
CREATE TABLE NAPACA_Imaging (
	napaca_imaging_id		INTEGER 	AUTO_INCREMENT	NOT NULL,
	ensat_id			INTEGER	NOT NULL,
	center_id			VARCHAR(5)	NOT NULL,
	imaging_date		DATE,
	tumor_sites			VARCHAR(30),
	right_adrenal_max_tumor		FLOAT,
	left_adrenal_max_tumor		FLOAT,
	imaging_of_tumor		VARCHAR(3),
	ct_tumor_density		VARCHAR(10),
	ct_tumor_density_specific	VARCHAR(4),
	ct_delay_contrast_washout		VARCHAR(10),
	evidence_extra_adrenal		VARCHAR(4),
	additional_imaging_performed		VARCHAR(4),
	mri_chemical_shift_analysis		VARCHAR(4),
	fdg_pet			VARCHAR(4),
	comment			VARCHAR(100),	
	PRIMARY KEY(napaca_imaging_id, ensat_id,center_id),
	FOREIGN KEY(ensat_id,center_id) REFERENCES Identification(ensat_id,center_id)
);

-- NAPACA_Surgery table
CREATE TABLE NAPACA_Surgery (
	napaca_surgery_id		INTEGER 	AUTO_INCREMENT	NOT NULL,
	ensat_id			INTEGER	NOT NULL,
	center_id			VARCHAR(5)	NOT NULL,
	surgery_date		DATE,
	surgical_approach		VARCHAR(30),
	PRIMARY KEY(napaca_surgery_id, ensat_id,center_id),
	FOREIGN KEY(ensat_id,center_id) REFERENCES Identification(ensat_id,center_id)
);

-- NAPACA_Pathology table
CREATE TABLE NAPACA_Pathology (
	napaca_pathology_id 		INTEGER	AUTO_INCREMENT	NOT NULL,
	ensat_id			INTEGER	NOT NULL,
	center_id			VARCHAR(5)	NOT NULL,
	pathology_date		DATE,
	pathologist_name		VARCHAR(30),
	pathologist_location	VARCHAR(30),
	pathology_diagnosis	VARCHAR(100),
	number_of_mitoses_exact		FLOAT,
	ki67				FLOAT,
	weiss_score			VARCHAR(14),
	nuclear_atypia		VARCHAR(14),
	atypical_mitosis		VARCHAR(14),
	spongiocytic_tumor_cells	VARCHAR(14),
	diffuse_architecture	VARCHAR(14),
	venous_invasion		VARCHAR(14),
	sinus_invasion		VARCHAR(14),
	capsular_invasion		VARCHAR(14),
	necrosis			VARCHAR(14),
	number_of_mitoses_per5	VARCHAR(14),
	PRIMARY KEY(napaca_pathology_id, ensat_id, center_id),
	FOREIGN KEY(ensat_id,center_id) REFERENCES Identification(ensat_id,center_id)
);

-- APA_PatientHistory table
CREATE TABLE APA_PatientHistory (
	ensat_id			INTEGER	NOT NULL,
	center_id			VARCHAR(5)	NOT NULL,                                                                                                                                                                                                                                                                                                                                                       
	year_of_diagnosis		VARCHAR(4),
	hypertension_presentation	VARCHAR(4),
	hypertension_year			VARCHAR(4),
	first_degree_relatives_hypertension		INTEGER,
	first_degree_relatives_pal		INTEGER,
	doc_genetic_disease		VARCHAR(4),
	lowest_kalemia		FLOAT,
	apa_comment			VARCHAR(100),
	PRIMARY KEY(ensat_id,center_id),
	FOREIGN KEY(ensat_id,center_id) REFERENCES Identification(ensat_id,center_id)
);

-- APA_ClinicalAssessment table
CREATE TABLE APA_ClinicalAssessment (
	clinical_assessment_id	INTEGER	AUTO_INCREMENT	NOT NULL,
	ensat_id			INTEGER	NOT NULL,
	center_id			VARCHAR(5)	NOT NULL,
	assessment_date		DATE,
	height			FLOAT,
	weight			FLOAT,
	systolic_blood_pressure			FLOAT,
	diastolic_blood_pressure		FLOAT,
	heart_rate			FLOAT,
	PRIMARY KEY(clinical_assessment_id,ensat_id,center_id),
	FOREIGN KEY(ensat_id,center_id) REFERENCES Identification(ensat_id,center_id)
);

-- APA_BiochemicalAssessment table
CREATE TABLE APA_BiochemicalAssessment (
	biochemical_assessment_id		INTEGER 	AUTO_INCREMENT	NOT NULL,
	ensat_id		INTEGER	NOT NULL,
	center_id			VARCHAR(5)	NOT NULL,
	assessment_date		DATE,
	spironolactone		VARCHAR(4),
	other_diuretic		VARCHAR(4),
	beta_blocker		VARCHAR(4),
	ace_inhibitor		VARCHAR(4),
	ang_receptor		VARCHAR(4),
	central_anti_ht		VARCHAR(4),
	calcium_channel		VARCHAR(4),
	alpha_blocker		VARCHAR(4),
	potassium_salts		VARCHAR(4),
	serum_potassium		FLOAT,
	serum_sodium		FLOAT,
	plasma_creatinine		FLOAT,
	supine_pra			FLOAT,
	sitting_pra			FLOAT,
	supine_arc			FLOAT,
	sitting_arc			FLOAT,
	standing_aldosterone	FLOAT,
	sitting_aldosterone	FLOAT,
	urinary_aldosterone	FLOAT,
	urinary_tetrahydroaldosterone		FLOAT,
	post_captopril_aldosterone	FLOAT,
	post_oral_sodium_aldosterone	FLOAT,
	post_saline_infusion_aldosterone	FLOAT,
	post_fludrocorticone_aldosterone	FLOAT,
	post_furosemide_aldosterone	FLOAT,
	aldosterone_right		FLOAT,
	corticol_right		FLOAT,
	aldosterone_left		FLOAT,
	corticol_left		FLOAT,
	aldosterone_vena_cava	FLOAT,
	corticol_vena_cava	FLOAT,
	bilateral_avs		VARCHAR(4),
	stimulation_avs		VARCHAR(4),
	biochemical_comment	VARCHAR(30),
	PRIMARY KEY(biochemical_assessment_id, ensat_id, center_id),
	FOREIGN KEY(ensat_id,center_id) REFERENCES Identification(ensat_id,center_id)
);

-- APA_FollowUp table
CREATE TABLE APA_FollowUp (
	apa_followup_id		INTEGER 	AUTO_INCREMENT	NOT NULL,
	ensat_id		INTEGER	NOT NULL,
	center_id			VARCHAR(5)	NOT NULL,
	followup_date		DATE,
	followup_alive		VARCHAR(4),
	cause_of_death		VARCHAR(30),
	followup_weight		FLOAT,
	followup_systolic_bp	FLOAT,
	followup_diastolic_bp	FLOAT,
	spironolactone		VARCHAR(4),
	other_diuretic		VARCHAR(4),
	beta_blocker		VARCHAR(4),
	ace_inhibitor		VARCHAR(4),
	ang_receptor		VARCHAR(4),
	central_anti_ht		VARCHAR(4),
	calcium_channel		VARCHAR(4),
	alpha_blocker		VARCHAR(4),
	potassium_salts		VARCHAR(4),
	eval_of_bp_outcomes		VARCHAR(30),
	serum_potassium		FLOAT,
	serum_sodium		FLOAT,
	plasma_creatinine		FLOAT,
	supine_pra			FLOAT,
	sitting_pra			FLOAT,
	supine_arc			FLOAT,
	sitting_arc			FLOAT,
	standing_aldosterone	FLOAT,
	sitting_aldosterone	FLOAT,
	urinary_aldosterone	FLOAT,
	urinary_tetrahydroaldosterone		FLOAT,
	followup_comment		VARCHAR(100),
	followup_complications		VARCHAR(100),
	PRIMARY KEY(apa_followup_id, ensat_id, center_id),
	FOREIGN KEY(ensat_id,center_id) REFERENCES Identification(ensat_id,center_id)
);

-- APA_Biomaterial table
CREATE TABLE APA_Biomaterial (
	apa_biomaterial_id		INTEGER 	AUTO_INCREMENT	NOT NULL,
	ensat_id			INTEGER	NOT NULL,
	center_id			VARCHAR(5)	NOT NULL,
	serum_apa				VARCHAR(3),
	serum_apa_sampling_date		DATE,
	plasma_apa				VARCHAR(3),
	plasma_apa_sampling_date		DATE,
	lymphocytes_apa				VARCHAR(3),
	lymphocytes_apa_sampling_date		DATE,
	urine_apa				VARCHAR(3),
	urine_apa_sampling_date		DATE,
	tumor_specimen_apa				VARCHAR(30),
	tumor_specimen_apa_sampling_date		DATE,
	PRIMARY KEY(apa_biomaterial_id, ensat_id,center_id),
	FOREIGN KEY(ensat_id,center_id) REFERENCES Identification(ensat_id,center_id)
);

-- APA_Surgery table
CREATE TABLE APA_Surgery (
	apa_surgery_id 	INTEGER	AUTO_INCREMENT	NOT NULL,
	ensat_id				INTEGER	NOT NULL,
	center_id			VARCHAR(5)	NOT NULL,
	intervention_date		DATE,
	tumor_sites_surgery		VARCHAR(30),
	surgery_procedure			VARCHAR(30),
	adrenal_sparing		VARCHAR(4),
	max_tumor_right	FLOAT,
	max_tumor_left	FLOAT,
	tumor_weight	FLOAT,
	comment			VARCHAR(100),
	PRIMARY KEY(apa_surgery_id,ensat_id,center_id),
	FOREIGN KEY(ensat_id,center_id) REFERENCES Identification(ensat_id,center_id)
);

-- APA_Cardio table
CREATE TABLE APA_Cardio (
	apa_cardio_id 	INTEGER	AUTO_INCREMENT	NOT NULL,
	ensat_id		INTEGER	NOT NULL,
	center_id		VARCHAR(5)	NOT NULL,
	event_date		DATE,
	echocardiography	VARCHAR(30),
	echocardiography_specific	VARCHAR(30),
	electrocardiogram	VARCHAR(30),
	electrocardiogram_specific	VARCHAR(30),
	electrocardiogram_other		VARCHAR(100),
	PRIMARY KEY(apa_cardio_id,ensat_id,center_id),
	FOREIGN KEY(ensat_id,center_id) REFERENCES Identification(ensat_id,center_id)
);

-- APA_Complication table
CREATE TABLE APA_Complication (
	apa_complication_id 	INTEGER	AUTO_INCREMENT	NOT NULL,
	ensat_id		INTEGER	NOT NULL,
	center_id		VARCHAR(5)	NOT NULL,
	event_date		DATE,
	complication	VARCHAR(30),
	diabetes		VARCHAR(30),
	hypercholesterenimia	VARCHAR(30),
	smoking_status	VARCHAR(30),
	comment		VARCHAR(100),
	PRIMARY KEY(apa_complication_id,ensat_id,center_id),
	FOREIGN KEY(ensat_id,center_id) REFERENCES Identification(ensat_id,center_id)
);



-- APA_Imaging table
CREATE TABLE APA_Imaging (
	apa_imaging_id 	INTEGER	AUTO_INCREMENT	NOT NULL,
	ensat_id		INTEGER	NOT NULL,
	center_id		VARCHAR(5)	NOT NULL,
	imaging_date		DATE,
	adrenal_state	VARCHAR(30),
	tumor_sites_imaging		VARCHAR(30),
	max_tumor_by_ct_right	FLOAT,
	max_tumor_by_mr_right	FLOAT,
	max_tumor_by_ct_left	FLOAT,
	max_tumor_by_mr_left	FLOAT,
	scintigraphy	VARCHAR(30),
	PRIMARY KEY(apa_imaging_id,ensat_id,center_id),
	FOREIGN KEY(ensat_id,center_id) REFERENCES Identification(ensat_id,center_id)
);

-----

ALTER TABLE Identification ADD COLUMN eurine_act_inclusion VARCHAR(4) AFTER ensat_database;
ALTER TABLE Pheo_FollowUp ADD COLUMN disease_state VARCHAR(30) AFTER phpgl_free;
ALTER TABLE Pheo_TumorDetails MODIFY COLUMN tumor_resected VARCHAR(30);
ALTER TABLE Pheo_Biomaterial ADD COLUMN lymphocytes_dna VARCHAR(30) AFTER lymphocytes_sampling_date;
ALTER TABLE Pheo_Biomaterial ADD COLUMN lymphocytes_dna_sampling_date DATE AFTER lymphocytes_dna;
ALTER TABLE Pheo_ImagingTests ADD COLUMN synthesis_imaging_workup_primary INTEGER AFTER synthesis_imaging_workup;
ALTER TABLE Pheo_ImagingTests ADD COLUMN synthesis_imaging_workup_metastases INTEGER AFTER synthesis_imaging_workup_primary;

ALTER TABLE NAPACA_DiagnosticProcedures MODIFY COLUMN hypertension_presentation VARCHAR(30);
ALTER TABLE NAPACA_DiagnosticProcedures MODIFY COLUMN diabetestype2_presentation VARCHAR(30);
ALTER TABLE NAPACA_DiagnosticProcedures MODIFY COLUMN dyslipidaemia_presentation VARCHAR(30);
ALTER TABLE NAPACA_DiagnosticProcedures MODIFY COLUMN osteoporosis_presentation VARCHAR(30);
ALTER TABLE NAPACA_DiagnosticProcedures MODIFY COLUMN prev_cardio_events VARCHAR(30);
ALTER TABLE NAPACA_DiagnosticProcedures MODIFY COLUMN antidiabetic_drugs VARCHAR(30);
ALTER TABLE NAPACA_DiagnosticProcedures MODIFY COLUMN lipidlowering_drugs VARCHAR(30);
ALTER TABLE NAPACA_DiagnosticProcedures MODIFY COLUMN osteoporosis_drugs VARCHAR(30);
ALTER TABLE NAPACA_DiagnosticProcedures MODIFY COLUMN antihypertensive_drugs VARCHAR(30);

ALTER TABLE Pheo_Biomaterial ADD COLUMN normal_tissue_pheo VARCHAR(30) AFTER tumor_specimen_sampling_date;
ALTER TABLE Pheo_Biomaterial ADD COLUMN normal_tissue_pheo_sampling_date DATE AFTER normal_tissue_pheo;
ALTER TABLE Pheo_Biomaterial ADD COLUMN normal_tissue_pheo_specific VARCHAR(30) AFTER normal_tissue_pheo_sampling_date;

ALTER TABLE Pheo_NonSurgicalInterventions ADD COLUMN chemotherapy_end_date DATE AFTER chemotherapy_date;
ALTER TABLE Pheo_NonSurgicalInterventions ADD COLUMN targeted_molecular_therapy_end_date DATE AFTER targeted_molecular_therapy_date;

ALTER TABLE Pheo_FollowUp DROP COLUMN date_of_recurrence;
ALTER TABLE Pheo_ImagingTests CHANGE COLUMN other_imaging_pet other_imaging VARCHAR(30);

ALTER TABLE NAPACA_Pathology MODIFY COLUMN number_of_mitoses_exact VARCHAR(30);
ALTER TABLE NAPACA_Pathology MODIFY COLUMN ki67 VARCHAR(30);

ALTER TABLE ACC_Biomaterial ADD COLUMN normal_tissue_paraffin VARCHAR(4) AFTER normal_tissue_specify;
ALTER TABLE ACC_Biomaterial ADD COLUMN normal_tissue_paraffin_specify VARCHAR(30) AFTER normal_tissue_paraffin;

ALTER TABLE NAPACA_Biomaterial MODIFY COLUMN tumor_tissue_frozen VARCHAR(4) AFTER biomaterial_date;
ALTER TABLE NAPACA_Biomaterial MODIFY COLUMN tumor_tissue_ensat_sop VARCHAR(4) AFTER tumor_tissue_frozen;
ALTER TABLE NAPACA_Biomaterial MODIFY COLUMN tumor_tissue_paraffin VARCHAR(4) AFTER tumor_tissue_ensat_sop;

ALTER TABLE NAPACA_Biomaterial ADD COLUMN normal_tissue_paraffin VARCHAR(4) AFTER normal_tissue_specify;
ALTER TABLE NAPACA_Biomaterial ADD COLUMN normal_tissue_paraffin_specify VARCHAR(30) AFTER normal_tissue_paraffin;

UPDATE ACC_Biomaterial SET normal_tissue_paraffin='';
UPDATE ACC_Biomaterial SET normal_tissue_paraffin_specify='';
UPDATE NAPACA_Biomaterial SET normal_tissue_paraffin='';
UPDATE NAPACA_Biomaterial SET normal_tissue_paraffin_specify='';

DROP TABLE APA_Biomaterial;

-- APA_Biomaterial table
CREATE TABLE APA_Biomaterial (
	apa_biomaterial_id		INTEGER 	AUTO_INCREMENT	NOT NULL,
	ensat_id			INTEGER	NOT NULL,
	center_id			VARCHAR(5)	NOT NULL,
	biomaterial_date		DATE,
	tumor_tissue_frozen	VARCHAR(14),
	tumor_tissue_ensat_sop	VARCHAR(14),
	tumor_tissue_paraffin	VARCHAR(14),
	leukocyte_dna		VARCHAR(14),
	plasma			VARCHAR(14),
	serum				VARCHAR(14),
	24h_urine			VARCHAR(14),
	24h_urine_vol		FLOAT,
	spot_urine			VARCHAR(14),
	normal_tissue		VARCHAR(14),
	normal_tissue_specify	VARCHAR(100),	
	normal_tissue_paraffin		VARCHAR(4),
	normal_tissue_paraffin_specify	VARCHAR(30),	
	PRIMARY KEY(apa_biomaterial_id, ensat_id,center_id)
);

DROP TABLE Pheo_Biomaterial;

-- Pheo_Biomaterial table
CREATE TABLE Pheo_Biomaterial (
	pheo_biomaterial_id		INTEGER 	AUTO_INCREMENT	NOT NULL,
	ensat_id			INTEGER	NOT NULL,
	center_id			VARCHAR(5)	NOT NULL,
	biomaterial_date		DATE,
	tumor_tissue_frozen	VARCHAR(14),
	tumor_tissue_ensat_sop	VARCHAR(14),
	tumor_tissue_paraffin	VARCHAR(14),
	leukocyte_dna		VARCHAR(14),
	plasma			VARCHAR(14),
	serum				VARCHAR(14),
	24h_urine			VARCHAR(14),
	24h_urine_vol		FLOAT,
	spot_urine			VARCHAR(14),
	normal_tissue		VARCHAR(14),
	normal_tissue_specify	VARCHAR(100),	
	normal_tissue_paraffin		VARCHAR(4),
	normal_tissue_paraffin_specify	VARCHAR(30),	
	whole_blood		VARCHAR(14),
	PRIMARY KEY(pheo_biomaterial_id, ensat_id,center_id),
	FOREIGN KEY(ensat_id,center_id) REFERENCES Identification(ensat_id,center_id)
);


ALTER TABLE ACC_Biomaterial ADD COLUMN tumor_tissue_dna VARCHAR(4) AFTER tumor_tissue_paraffin;
ALTER TABLE ACC_Biomaterial ADD COLUMN normal_tissue_dna VARCHAR(4) AFTER normal_tissue_paraffin_specify;
ALTER TABLE ACC_Biomaterial ADD COLUMN normal_tissue_dna_specify VARCHAR(30) AFTER normal_tissue_dna;
ALTER TABLE Pheo_Biomaterial ADD COLUMN tumor_tissue_dna VARCHAR(4) AFTER tumor_tissue_paraffin;
ALTER TABLE Pheo_Biomaterial ADD COLUMN normal_tissue_dna VARCHAR(4) AFTER normal_tissue_paraffin_specify;
ALTER TABLE Pheo_Biomaterial ADD COLUMN normal_tissue_dna_specify VARCHAR(30) AFTER normal_tissue_dna;
ALTER TABLE NAPACA_Biomaterial ADD COLUMN tumor_tissue_dna VARCHAR(4) AFTER tumor_tissue_paraffin;
ALTER TABLE NAPACA_Biomaterial ADD COLUMN normal_tissue_dna VARCHAR(4) AFTER normal_tissue_paraffin_specify;
ALTER TABLE NAPACA_Biomaterial ADD COLUMN normal_tissue_dna_specify VARCHAR(30) AFTER normal_tissue_dna;
ALTER TABLE APA_Biomaterial ADD COLUMN tumor_tissue_dna VARCHAR(4) AFTER tumor_tissue_paraffin;
ALTER TABLE APA_Biomaterial ADD COLUMN normal_tissue_dna VARCHAR(4) AFTER normal_tissue_paraffin_specify;
ALTER TABLE APA_Biomaterial ADD COLUMN normal_tissue_dna_specify VARCHAR(30) AFTER normal_tissue_dna;

UPDATE ACC_Biomaterial SET tumor_tissue_dna='';
UPDATE ACC_Biomaterial SET normal_tissue_dna='';
UPDATE ACC_Biomaterial SET normal_tissue_dna_specify='';
UPDATE Pheo_Biomaterial SET tumor_tissue_dna='';
UPDATE Pheo_Biomaterial SET normal_tissue_dna='';
UPDATE Pheo_Biomaterial SET normal_tissue_dna_specify='';
UPDATE NAPACA_Biomaterial SET tumor_tissue_dna='';
UPDATE NAPACA_Biomaterial SET normal_tissue_dna='';
UPDATE NAPACA_Biomaterial SET normal_tissue_dna_specify='';
UPDATE APA_Biomaterial SET tumor_tissue_dna='';
UPDATE APA_Biomaterial SET normal_tissue_dna='';
UPDATE APA_Biomaterial SET normal_tissue_dna_specify='';

ALTER TABLE NAPACA_FollowUp MODIFY COLUMN followup_max_tumor VARCHAR(4);
ALTER TABLE NAPACA_FollowUp ADD COLUMN death_related_tumor VARCHAR(30) AFTER followup_alive;
ALTER TABLE NAPACA_FollowUp ADD COLUMN date_of_death DATE AFTER death_related_tumor;
UPDATE NAPACA_FollowUp SET death_related_tumor='';

ALTER TABLE Pheo_Surgery MODIFY COLUMN evidence_of_malignancy VARCHAR(30);

ALTER TABLE ACC_Biomaterial MODIFY COLUMN 24h_urine_vol VARCHAR(30);
ALTER TABLE Pheo_Biomaterial MODIFY COLUMN 24h_urine_vol VARCHAR(30);
ALTER TABLE NAPACA_Biomaterial MODIFY COLUMN 24h_urine_vol VARCHAR(30);
ALTER TABLE APA_Biomaterial MODIFY COLUMN 24h_urine_vol VARCHAR(30);

-- Pheo_Genetics table
CREATE TABLE Pheo_Genetics (
	pheo_genetics_id		INTEGER 	AUTO_INCREMENT	NOT NULL,
	ensat_id			INTEGER	NOT NULL,
	center_id			VARCHAR(5)	NOT NULL,
	genetics_date		DATE,
	vhl_testing_performed	VARCHAR(4),
	vhl_testing_method		VARCHAR(100),
	vhl_mutation_detected	VARCHAR(4),
	vhl_mutation_name		VARCHAR(30),
	sdhd_testing_performed	VARCHAR(4),
	sdhd_testing_method		VARCHAR(100),
	sdhd_mutation_detected	VARCHAR(4),
	sdhd_mutation_name		VARCHAR(30),
	sdhb_testing_performed	VARCHAR(4),
	sdhb_testing_method		VARCHAR(100),
	sdhb_mutation_detected	VARCHAR(4),
	sdhb_mutation_name		VARCHAR(30),
	ret_testing_performed	VARCHAR(4),
	ret_testing_method		VARCHAR(100),
	ret_mutation_detected	VARCHAR(4),
	ret_mutation_name		VARCHAR(30),
	nf1_testing_performed	VARCHAR(4),
	nf1_testing_method		VARCHAR(100),
	nf1_mutation_detected	VARCHAR(4),
	nf1_mutation_name		VARCHAR(30),
	tmem127_testing_performed	VARCHAR(4),
	tmem127_testing_method		VARCHAR(100),
	tmem127_mutation_detected	VARCHAR(4),
	tmem127_mutation_name		VARCHAR(30),
	max_testing_performed	VARCHAR(4),
	max_testing_method		VARCHAR(100),
	max_mutation_detected	VARCHAR(4),
	max_mutation_name		VARCHAR(30),
	sdhc_testing_performed	VARCHAR(4),
	sdhc_testing_method		VARCHAR(100),
	sdhc_mutation_detected	VARCHAR(4),
	sdhc_mutation_name		VARCHAR(30),
	sdha_testing_performed	VARCHAR(4),
	sdha_testing_method		VARCHAR(100),
	sdha_mutation_detected	VARCHAR(4),
	sdha_mutation_name		VARCHAR(30),
	sdhaf2_testing_performed	VARCHAR(4),
	sdhaf2_testing_method		VARCHAR(100),
	sdhaf2_mutation_detected	VARCHAR(4),
	sdhaf2_mutation_name		VARCHAR(30),
	PRIMARY KEY(pheo_genetics_id, ensat_id,center_id),
	FOREIGN KEY(ensat_id,center_id) REFERENCES Identification(ensat_id,center_id)
);


-- ACC_Biomaterial_Normal_Tissue table
CREATE TABLE ACC_Biomaterial_Normal_Tissue (
	acc_biomaterial_normal_tissue_id	INTEGER 	AUTO_INCREMENT	NOT NULL,
	ensat_id			INTEGER	NOT NULL,
	center_id			VARCHAR(5)	NOT NULL,
	normal_tissue_type		VARCHAR(30),
	normal_tissue_specific		VARCHAR(100),
	PRIMARY KEY(acc_biomaterial_normal_tissue_id, ensat_id,center_id),
	FOREIGN KEY(ensat_id,center_id) REFERENCES Identification(ensat_id,center_id)
);

-- Pheo_Biomaterial_Normal_Tissue table
CREATE TABLE Pheo_Biomaterial_Normal_Tissue (
	pheo_biomaterial_normal_tissue_id	INTEGER 	AUTO_INCREMENT	NOT NULL,
	ensat_id			INTEGER	NOT NULL,
	center_id			VARCHAR(5)	NOT NULL,
	normal_tissue_type		VARCHAR(30),
	normal_tissue_specific		VARCHAR(100),
	PRIMARY KEY(pheo_biomaterial_normal_tissue_id, ensat_id,center_id),
	FOREIGN KEY(ensat_id,center_id) REFERENCES Identification(ensat_id,center_id)
);

-- NAPACA_Biomaterial_Normal_Tissue table
CREATE TABLE NAPACA_Biomaterial_Normal_Tissue (
	napaca_biomaterial_normal_tissue_id	INTEGER 	AUTO_INCREMENT	NOT NULL,
	ensat_id			INTEGER	NOT NULL,
	center_id			VARCHAR(5)	NOT NULL,
	normal_tissue_type		VARCHAR(30),
	normal_tissue_specific		VARCHAR(100),
	PRIMARY KEY(napaca_biomaterial_normal_tissue_id, ensat_id,center_id),
	FOREIGN KEY(ensat_id,center_id) REFERENCES Identification(ensat_id,center_id)
);

-- APA_Biomaterial_Normal_Tissue table
CREATE TABLE APA_Biomaterial_Normal_Tissue (
	apa_biomaterial_normal_tissue_id	INTEGER 	AUTO_INCREMENT	NOT NULL,
	ensat_id			INTEGER	NOT NULL,
	center_id			VARCHAR(5)	NOT NULL,
	normal_tissue_type		VARCHAR(30),
	normal_tissue_specific		VARCHAR(100),
	PRIMARY KEY(apa_biomaterial_normal_tissue_id, ensat_id,center_id),
	FOREIGN KEY(ensat_id,center_id) REFERENCES Identification(ensat_id,center_id)
);

ALTER TABLE ACC_Biomaterial DROP COLUMN normal_tissue_specify;
ALTER TABLE ACC_Biomaterial DROP COLUMN normal_tissue_paraffin_specify;
ALTER TABLE ACC_Biomaterial DROP COLUMN normal_tissue_dna_specify;

ALTER TABLE Pheo_Biomaterial DROP COLUMN normal_tissue_specify;
ALTER TABLE Pheo_Biomaterial DROP COLUMN normal_tissue_paraffin_specify;
ALTER TABLE Pheo_Biomaterial DROP COLUMN normal_tissue_dna_specify;

ALTER TABLE NAPACA_Biomaterial DROP COLUMN normal_tissue_specify;
ALTER TABLE NAPACA_Biomaterial DROP COLUMN normal_tissue_paraffin_specify;
ALTER TABLE NAPACA_Biomaterial DROP COLUMN normal_tissue_dna_specify;

ALTER TABLE APA_Biomaterial DROP COLUMN normal_tissue_specify;
ALTER TABLE APA_Biomaterial DROP COLUMN normal_tissue_paraffin_specify;
ALTER TABLE APA_Biomaterial DROP COLUMN normal_tissue_dna_specify;

ALTER TABLE ACC_Pathology MODIFY COLUMN pathology_derived_from VARCHAR(30);


CREATE TABLE ACC_Biomaterial_Aliquots (
	acc_biomaterial_aliquot_id	INTEGER		AUTO_INCREMENT	NOT NULL,
	acc_biomaterial_id		INTEGER		NOT NULL,
	ensat_id			INTEGER		NOT NULL,
	center_id			VARCHAR(5)	NOT NULL,
	parameter_name			VARCHAR(30),
	aliquot_number			VARCHAR(3),
	PRIMARY KEY(acc_biomaterial_aliquot_id, ensat_id, center_id),
	FOREIGN KEY(ensat_id,center_id) REFERENCES Identification(ensat_id,center_id)
);

CREATE TABLE Pheo_Biomaterial_Aliquots (
	pheo_biomaterial_aliquot_id	INTEGER		AUTO_INCREMENT	NOT NULL,
	pheo_biomaterial_id		INTEGER		NOT NULL,
	ensat_id			INTEGER		NOT NULL,
	center_id			VARCHAR(5)	NOT NULL,
	parameter_name			VARCHAR(30),
	aliquot_number			VARCHAR(3),
	PRIMARY KEY(pheo_biomaterial_aliquot_id, ensat_id, center_id),
	FOREIGN KEY(ensat_id,center_id) REFERENCES Identification(ensat_id,center_id)
);

CREATE TABLE NAPACA_Biomaterial_Aliquots (
	napaca_biomaterial_aliquot_id	INTEGER		AUTO_INCREMENT	NOT NULL,
	napaca_biomaterial_id		INTEGER		NOT NULL,
	ensat_id			INTEGER		NOT NULL,
	center_id			VARCHAR(5)	NOT NULL,
	parameter_name			VARCHAR(30),
	aliquot_number			VARCHAR(3),
	PRIMARY KEY(napaca_biomaterial_aliquot_id, ensat_id, center_id),
	FOREIGN KEY(ensat_id,center_id) REFERENCES Identification(ensat_id,center_id)
);

CREATE TABLE APA_Biomaterial_Aliquots (
	apa_biomaterial_aliquot_id	INTEGER		AUTO_INCREMENT	NOT NULL,
	apa_biomaterial_id		INTEGER		NOT NULL,
	ensat_id			INTEGER		NOT NULL,
	center_id			VARCHAR(5)	NOT NULL,
	parameter_name			VARCHAR(30),
	aliquot_number			VARCHAR(3),
	PRIMARY KEY(apa_biomaterial_aliquot_id, ensat_id, center_id),
	FOREIGN KEY(ensat_id,center_id) REFERENCES Identification(ensat_id,center_id)
);

ALTER TABLE NAPACA_FollowUp MODIFY COLUMN followup_imaging_type VARCHAR(30);

ALTER TABLE APA_BiochemicalAssessment MODIFY COLUMN serum_potassium VARCHAR(10);
ALTER TABLE APA_BiochemicalAssessment MODIFY COLUMN serum_sodium VARCHAR(10);
ALTER TABLE APA_BiochemicalAssessment MODIFY COLUMN plasma_creatinine VARCHAR(10);
ALTER TABLE APA_BiochemicalAssessment MODIFY COLUMN supine_pra VARCHAR(10);
ALTER TABLE APA_BiochemicalAssessment MODIFY COLUMN sitting_pra VARCHAR(10);
ALTER TABLE APA_BiochemicalAssessment MODIFY COLUMN supine_arc VARCHAR(10);
ALTER TABLE APA_BiochemicalAssessment MODIFY COLUMN sitting_arc VARCHAR(10);
ALTER TABLE APA_BiochemicalAssessment MODIFY COLUMN standing_aldosterone VARCHAR(10);
ALTER TABLE APA_BiochemicalAssessment MODIFY COLUMN sitting_aldosterone VARCHAR(10);
ALTER TABLE APA_BiochemicalAssessment MODIFY COLUMN urinary_aldosterone VARCHAR(10);
ALTER TABLE APA_BiochemicalAssessment MODIFY COLUMN urinary_tetrahydroaldosterone VARCHAR(10);
ALTER TABLE APA_BiochemicalAssessment MODIFY COLUMN post_captopril_aldosterone VARCHAR(10);
ALTER TABLE APA_BiochemicalAssessment MODIFY COLUMN post_oral_sodium_aldosterone VARCHAR(10);
ALTER TABLE APA_BiochemicalAssessment MODIFY COLUMN post_saline_infusion_aldosterone VARCHAR(10);
ALTER TABLE APA_BiochemicalAssessment MODIFY COLUMN post_fludrocorticone_aldosterone VARCHAR(10);
ALTER TABLE APA_BiochemicalAssessment MODIFY COLUMN post_furosemide_aldosterone VARCHAR(10);
ALTER TABLE APA_BiochemicalAssessment MODIFY COLUMN aldosterone_right VARCHAR(10);
ALTER TABLE APA_BiochemicalAssessment MODIFY COLUMN corticol_right VARCHAR(10);
ALTER TABLE APA_BiochemicalAssessment MODIFY COLUMN aldosterone_left VARCHAR(10);
ALTER TABLE APA_BiochemicalAssessment MODIFY COLUMN corticol_left VARCHAR(10);
ALTER TABLE APA_BiochemicalAssessment MODIFY COLUMN aldosterone_vena_cava VARCHAR(10);
ALTER TABLE APA_BiochemicalAssessment MODIFY COLUMN corticol_vena_cava VARCHAR(10);

---- Mods after code re-consolidation

ALTER TABLE ACC_DiagnosticProcedures MODIFY COLUMN height VARCHAR(10);
ALTER TABLE ACC_DiagnosticProcedures MODIFY COLUMN weight VARCHAR(10);
ALTER TABLE ACC_DiagnosticProcedures MODIFY COLUMN bmi VARCHAR(10);
ALTER TABLE ACC_DiagnosticProcedures MODIFY COLUMN date_of_diagnosis VARCHAR(11);

ALTER TABLE ACC_TumorStaging MODIFY COLUMN size_of_adrenal_tumor VARCHAR(10);

ALTER TABLE Pheo_PatientHistory MODIFY COLUMN height_at_time_consent VARCHAR(10);
ALTER TABLE Pheo_PatientHistory MODIFY COLUMN weight_at_time_consent VARCHAR(10);
ALTER TABLE Pheo_PatientHistory MODIFY COLUMN systolic_bp_at_time_consent VARCHAR(10);
ALTER TABLE Pheo_PatientHistory MODIFY COLUMN diastolic_bp_at_time_consent VARCHAR(10);
ALTER TABLE Pheo_PatientHistory MODIFY COLUMN relatives_with_tumors VARCHAR(10);
ALTER TABLE Pheo_PatientHistory DROP COLUMN further_detail;

ALTER TABLE APA_BiochemicalAssessment MODIFY COLUMN bilateral_avs VARCHAR(10);
