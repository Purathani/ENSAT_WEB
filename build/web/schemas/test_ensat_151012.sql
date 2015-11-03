-- MySQL dump 10.13  Distrib 5.1.41, for debian-linux-gnu (x86_64)
--
-- Host: localhost    Database: test_ensat
-- ------------------------------------------------------
-- Server version	5.1.41-3ubuntu12.10

/*!40101 SET @OLD_CHARACTER_SET_CLIENT=@@CHARACTER_SET_CLIENT */;
/*!40101 SET @OLD_CHARACTER_SET_RESULTS=@@CHARACTER_SET_RESULTS */;
/*!40101 SET @OLD_COLLATION_CONNECTION=@@COLLATION_CONNECTION */;
/*!40101 SET NAMES utf8 */;
/*!40103 SET @OLD_TIME_ZONE=@@TIME_ZONE */;
/*!40103 SET TIME_ZONE='+00:00' */;
/*!40014 SET @OLD_UNIQUE_CHECKS=@@UNIQUE_CHECKS, UNIQUE_CHECKS=0 */;
/*!40014 SET @OLD_FOREIGN_KEY_CHECKS=@@FOREIGN_KEY_CHECKS, FOREIGN_KEY_CHECKS=0 */;
/*!40101 SET @OLD_SQL_MODE=@@SQL_MODE, SQL_MODE='NO_AUTO_VALUE_ON_ZERO' */;
/*!40111 SET @OLD_SQL_NOTES=@@SQL_NOTES, SQL_NOTES=0 */;

--
-- Table structure for table `ACC_Biomaterial`
--

DROP TABLE IF EXISTS `ACC_Biomaterial`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!40101 SET character_set_client = utf8 */;
CREATE TABLE `ACC_Biomaterial` (
  `acc_biomaterial_id` int(11) NOT NULL AUTO_INCREMENT,
  `ensat_id` int(11) NOT NULL,
  `center_id` varchar(5) NOT NULL,
  `biomaterial_date` date DEFAULT NULL,
  `tumor_tissue_frozen` varchar(14) DEFAULT NULL,
  `tumor_tissue_ensat_sop` varchar(14) DEFAULT NULL,
  `tumor_tissue_paraffin` varchar(14) DEFAULT NULL,
  `tumor_tissue_dna` varchar(4) DEFAULT NULL,
  `leukocyte_dna` varchar(14) DEFAULT NULL,
  `plasma` varchar(14) DEFAULT NULL,
  `heparin_plasma` varchar(30) DEFAULT NULL,
  `serum` varchar(14) DEFAULT NULL,
  `24h_urine` varchar(14) DEFAULT NULL,
  `24h_urine_vol` varchar(30) DEFAULT NULL,
  `spot_urine` varchar(14) DEFAULT NULL,
  `normal_tissue` varchar(14) DEFAULT NULL,
  `normal_tissue_paraffin` varchar(4) DEFAULT NULL,
  `normal_tissue_dna` varchar(4) DEFAULT NULL,
  `associated_study` varchar(100) DEFAULT NULL,
  `associated_study_phase_visit` varchar(100) DEFAULT NULL,
  `freezer_information` varchar(100) DEFAULT NULL,
  PRIMARY KEY (`acc_biomaterial_id`,`ensat_id`,`center_id`),
  KEY `ensat_id` (`ensat_id`,`center_id`)
) ENGINE=MyISAM AUTO_INCREMENT=699 DEFAULT CHARSET=latin1;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Table structure for table `ACC_Biomaterial_Aliquots`
--

DROP TABLE IF EXISTS `ACC_Biomaterial_Aliquots`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!40101 SET character_set_client = utf8 */;
CREATE TABLE `ACC_Biomaterial_Aliquots` (
  `acc_biomaterial_aliquot_id` int(11) NOT NULL AUTO_INCREMENT,
  `acc_biomaterial_id` int(11) NOT NULL,
  `ensat_id` int(11) NOT NULL,
  `center_id` varchar(5) NOT NULL,
  `parameter_name` varchar(30) DEFAULT NULL,
  `aliquot_number` varchar(3) DEFAULT NULL,
  PRIMARY KEY (`acc_biomaterial_aliquot_id`,`acc_biomaterial_id`,`ensat_id`,`center_id`),
  KEY `ensat_id` (`ensat_id`,`center_id`)
) ENGINE=MyISAM AUTO_INCREMENT=11 DEFAULT CHARSET=latin1;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Table structure for table `ACC_Biomaterial_Normal_Tissue`
--

DROP TABLE IF EXISTS `ACC_Biomaterial_Normal_Tissue`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!40101 SET character_set_client = utf8 */;
CREATE TABLE `ACC_Biomaterial_Normal_Tissue` (
  `acc_biomaterial_normal_tissue_id` int(11) NOT NULL AUTO_INCREMENT,
  `acc_biomaterial_id` int(11) DEFAULT NULL,
  `ensat_id` int(11) NOT NULL,
  `center_id` varchar(5) NOT NULL,
  `normal_tissue_type` varchar(30) DEFAULT NULL,
  `normal_tissue_specific` varchar(100) DEFAULT NULL,
  PRIMARY KEY (`acc_biomaterial_normal_tissue_id`,`ensat_id`,`center_id`),
  KEY `ensat_id` (`ensat_id`,`center_id`)
) ENGINE=MyISAM AUTO_INCREMENT=35 DEFAULT CHARSET=latin1;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Table structure for table `ACC_Chemoembolisation`
--

DROP TABLE IF EXISTS `ACC_Chemoembolisation`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!40101 SET character_set_client = utf8 */;
CREATE TABLE `ACC_Chemoembolisation` (
  `acc_chemoembolisation_id` int(11) NOT NULL AUTO_INCREMENT,
  `ensat_id` int(11) NOT NULL,
  `center_id` varchar(5) NOT NULL,
  `chemoembolisation_date` date DEFAULT NULL,
  `chemoembolisation_type` varchar(10) DEFAULT NULL,
  `chemoembolisation_best_objective` varchar(30) DEFAULT NULL,
  `chemoembolisation_eval_date` date DEFAULT NULL,
  PRIMARY KEY (`acc_chemoembolisation_id`,`ensat_id`,`center_id`),
  KEY `ensat_id` (`ensat_id`,`center_id`)
) ENGINE=MyISAM AUTO_INCREMENT=113 DEFAULT CHARSET=latin1;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Table structure for table `ACC_Chemotherapy`
--

DROP TABLE IF EXISTS `ACC_Chemotherapy`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!40101 SET character_set_client = utf8 */;
CREATE TABLE `ACC_Chemotherapy` (
  `acc_chemotherapy_id` int(11) NOT NULL AUTO_INCREMENT,
  `ensat_id` int(11) NOT NULL,
  `center_id` varchar(5) NOT NULL,
  `chemotherapy_date` date DEFAULT NULL,
  `chemotherapy_initiation` date DEFAULT NULL,
  `chemotherapy_end` date DEFAULT NULL,
  `chemotherapy_indication` varchar(30) DEFAULT NULL,
  `chemotherapy_best_objective` varchar(30) DEFAULT NULL,
  `chemotherapy_eval_date` date DEFAULT NULL,
  PRIMARY KEY (`acc_chemotherapy_id`,`ensat_id`,`center_id`),
  KEY `ensat_id` (`ensat_id`,`center_id`)
) ENGINE=MyISAM AUTO_INCREMENT=1141 DEFAULT CHARSET=latin1;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Table structure for table `ACC_Chemotherapy_Regimen`
--

DROP TABLE IF EXISTS `ACC_Chemotherapy_Regimen`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!40101 SET character_set_client = utf8 */;
CREATE TABLE `ACC_Chemotherapy_Regimen` (
  `acc_chemotherapy_regimen_id` int(11) NOT NULL AUTO_INCREMENT,
  `acc_chemotherapy_id` int(11) NOT NULL,
  `ensat_id` int(11) NOT NULL,
  `center_id` varchar(5) NOT NULL,
  `chemotherapy_regimen` varchar(30) DEFAULT NULL,
  PRIMARY KEY (`acc_chemotherapy_regimen_id`,`acc_chemotherapy_id`,`ensat_id`,`center_id`),
  KEY `acc_chemotherapy_id` (`acc_chemotherapy_id`),
  KEY `ensat_id` (`ensat_id`,`center_id`)
) ENGINE=MyISAM AUTO_INCREMENT=103 DEFAULT CHARSET=latin1;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Table structure for table `ACC_DiagnosticProcedures`
--

DROP TABLE IF EXISTS `ACC_DiagnosticProcedures`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!40101 SET character_set_client = utf8 */;
CREATE TABLE `ACC_DiagnosticProcedures` (
  `ensat_id` int(11) NOT NULL,
  `center_id` varchar(5) NOT NULL,
  `date_of_diagnosis` varchar(11) DEFAULT NULL,
  `disease_status` varchar(30) DEFAULT NULL,
  `modality_of_diagnosis` varchar(30) DEFAULT NULL,
  `height` varchar(10) DEFAULT NULL,
  `weight` varchar(10) DEFAULT NULL,
  `bmi` varchar(10) DEFAULT NULL,
  `symptoms_diag_tumor_mass` varchar(3) DEFAULT NULL,
  `symptoms_incidental` varchar(3) DEFAULT NULL,
  `symptoms_paraneoplastic` varchar(3) DEFAULT NULL,
  `symptoms_endocrine` varchar(9) DEFAULT NULL,
  `cushings_syndrome` varchar(9) DEFAULT NULL,
  `virilisation` varchar(9) DEFAULT NULL,
  `feminization` varchar(14) DEFAULT NULL,
  `mineralocorticoid_excess` varchar(9) DEFAULT NULL,
  `hypertension` varchar(9) DEFAULT NULL,
  `hypokalemia` varchar(9) DEFAULT NULL,
  `diabetes` varchar(9) DEFAULT NULL,
  `hormonal_hypersecretion` varchar(9) DEFAULT NULL,
  `glucocorticoids` varchar(9) DEFAULT NULL,
  `androgens` varchar(9) DEFAULT NULL,
  `estrogens` varchar(9) DEFAULT NULL,
  `mineralocorticoids` varchar(9) DEFAULT NULL,
  `precursor_secretion` varchar(9) DEFAULT NULL,
  `acc_during_pregnancy` varchar(3) DEFAULT NULL,
  `other_malignancies` varchar(7) DEFAULT NULL,
  `which_malignancies` varchar(100) DEFAULT NULL,
  PRIMARY KEY (`ensat_id`,`center_id`)
) ENGINE=MyISAM DEFAULT CHARSET=latin1;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Table structure for table `ACC_FollowUp`
--

DROP TABLE IF EXISTS `ACC_FollowUp`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!40101 SET character_set_client = utf8 */;
CREATE TABLE `ACC_FollowUp` (
  `acc_followup_id` int(11) NOT NULL AUTO_INCREMENT,
  `ensat_id` int(11) NOT NULL,
  `center_id` varchar(5) NOT NULL,
  `followup_date` date DEFAULT NULL,
  `patient_status` varchar(100) DEFAULT NULL,
  `followup_comment` varchar(100) DEFAULT NULL,
  PRIMARY KEY (`acc_followup_id`,`ensat_id`,`center_id`),
  KEY `ensat_id` (`ensat_id`,`center_id`)
) ENGINE=MyISAM AUTO_INCREMENT=21450 DEFAULT CHARSET=latin1;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Table structure for table `ACC_FollowUp_Organs`
--

DROP TABLE IF EXISTS `ACC_FollowUp_Organs`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!40101 SET character_set_client = utf8 */;
CREATE TABLE `ACC_FollowUp_Organs` (
  `acc_followup_organs_id` int(11) NOT NULL AUTO_INCREMENT,
  `acc_followup_id` int(11) NOT NULL,
  `ensat_id` int(11) NOT NULL,
  `center_id` varchar(5) NOT NULL,
  `followup_organs` varchar(30) DEFAULT NULL,
  PRIMARY KEY (`acc_followup_organs_id`,`acc_followup_id`,`ensat_id`,`center_id`),
  KEY `acc_followup_id` (`acc_followup_id`),
  KEY `ensat_id` (`ensat_id`,`center_id`)
) ENGINE=MyISAM AUTO_INCREMENT=333 DEFAULT CHARSET=latin1;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Table structure for table `ACC_Mitotane`
--

DROP TABLE IF EXISTS `ACC_Mitotane`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!40101 SET character_set_client = utf8 */;
CREATE TABLE `ACC_Mitotane` (
  `acc_mitotane_id` int(11) NOT NULL AUTO_INCREMENT,
  `ensat_id` int(11) NOT NULL,
  `center_id` varchar(5) NOT NULL,
  `mitotane_date` date DEFAULT NULL,
  `mitotane_initiation` date DEFAULT NULL,
  `mitotane_end` date DEFAULT NULL,
  `mitotane_indication` varchar(30) DEFAULT NULL,
  `mitotane_best_objective` varchar(30) DEFAULT NULL,
  `mitotane_eval_date` date DEFAULT NULL,
  `mitotane_max_dosage` float DEFAULT NULL,
  `mitotane_max_level` float DEFAULT NULL,
  PRIMARY KEY (`acc_mitotane_id`,`ensat_id`,`center_id`),
  KEY `ensat_id` (`ensat_id`,`center_id`)
) ENGINE=MyISAM AUTO_INCREMENT=2265 DEFAULT CHARSET=latin1;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Table structure for table `ACC_Pathology`
--

DROP TABLE IF EXISTS `ACC_Pathology`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!40101 SET character_set_client = utf8 */;
CREATE TABLE `ACC_Pathology` (
  `acc_pathology_id` int(11) NOT NULL AUTO_INCREMENT,
  `ensat_id` int(11) NOT NULL,
  `center_id` varchar(5) NOT NULL,
  `pathology_date` date DEFAULT NULL,
  `pathology_derived_from` varchar(30) DEFAULT NULL,
  `surgery_biopsy_date` date DEFAULT NULL,
  `local_pathologist` varchar(30) DEFAULT NULL,
  `central_pathology_review` varchar(3) DEFAULT NULL,
  `central_review_pathologist` varchar(30) DEFAULT NULL,
  `number_of_mitoses_exact` varchar(14) DEFAULT NULL,
  `ki67` varchar(14) DEFAULT NULL,
  `loss_of_heterozygosity` varchar(14) DEFAULT NULL,
  `igf_overexpression` varchar(14) DEFAULT NULL,
  `weiss_score` varchar(14) DEFAULT NULL,
  `nuclear_atypia` varchar(14) DEFAULT NULL,
  `atypical_mitosis` varchar(14) DEFAULT NULL,
  `spongiocytic_tumor_cells` varchar(14) DEFAULT NULL,
  `diffuse_architecture` varchar(14) DEFAULT NULL,
  `venous_invasion` varchar(14) DEFAULT NULL,
  `sinus_invasion` varchar(14) DEFAULT NULL,
  `capsular_invasion` varchar(14) DEFAULT NULL,
  `necrosis` varchar(14) DEFAULT NULL,
  `number_of_mitoses_per5` varchar(14) DEFAULT NULL,
  PRIMARY KEY (`acc_pathology_id`,`ensat_id`,`center_id`),
  KEY `ensat_id` (`ensat_id`,`center_id`)
) ENGINE=MyISAM AUTO_INCREMENT=3268 DEFAULT CHARSET=latin1;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Table structure for table `ACC_Radiofrequency`
--

DROP TABLE IF EXISTS `ACC_Radiofrequency`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!40101 SET character_set_client = utf8 */;
CREATE TABLE `ACC_Radiofrequency` (
  `acc_radiofrequency_id` int(11) NOT NULL AUTO_INCREMENT,
  `ensat_id` int(11) NOT NULL,
  `center_id` varchar(5) NOT NULL,
  `radiofrequency_date` date DEFAULT NULL,
  `radiofrequency_type` varchar(10) DEFAULT NULL,
  `radiofrequency_best_objective` varchar(30) DEFAULT NULL,
  `radiofrequency_eval_date` date DEFAULT NULL,
  PRIMARY KEY (`acc_radiofrequency_id`,`ensat_id`,`center_id`),
  KEY `ensat_id` (`ensat_id`,`center_id`)
) ENGINE=MyISAM AUTO_INCREMENT=84 DEFAULT CHARSET=latin1;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Table structure for table `ACC_Radiofrequency_Loc`
--

DROP TABLE IF EXISTS `ACC_Radiofrequency_Loc`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!40101 SET character_set_client = utf8 */;
CREATE TABLE `ACC_Radiofrequency_Loc` (
  `acc_radiofrequency_loc_id` int(11) NOT NULL AUTO_INCREMENT,
  `acc_radiofrequency_id` int(11) NOT NULL,
  `ensat_id` int(11) NOT NULL,
  `center_id` varchar(5) NOT NULL,
  `radiofrequency_location` varchar(30) DEFAULT NULL,
  PRIMARY KEY (`acc_radiofrequency_loc_id`,`acc_radiofrequency_id`,`ensat_id`,`center_id`),
  KEY `acc_radiofrequency_id` (`acc_radiofrequency_id`),
  KEY `ensat_id` (`ensat_id`,`center_id`)
) ENGINE=MyISAM AUTO_INCREMENT=4 DEFAULT CHARSET=latin1;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Table structure for table `ACC_Radiotherapy`
--

DROP TABLE IF EXISTS `ACC_Radiotherapy`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!40101 SET character_set_client = utf8 */;
CREATE TABLE `ACC_Radiotherapy` (
  `acc_radiotherapy_id` int(11) NOT NULL AUTO_INCREMENT,
  `ensat_id` int(11) NOT NULL,
  `center_id` varchar(5) NOT NULL,
  `radiotherapy_date` date DEFAULT NULL,
  `radiotherapy_initiation` date DEFAULT NULL,
  `radiotherapy_end` date DEFAULT NULL,
  `radiotherapy_type` varchar(10) DEFAULT NULL,
  `radiotherapy_indication` varchar(30) DEFAULT NULL,
  `radiotherapy_best_objective` varchar(30) DEFAULT NULL,
  `radiotherapy_eval_date` date DEFAULT NULL,
  PRIMARY KEY (`acc_radiotherapy_id`,`ensat_id`,`center_id`),
  KEY `ensat_id` (`ensat_id`,`center_id`)
) ENGINE=MyISAM AUTO_INCREMENT=242 DEFAULT CHARSET=latin1;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Table structure for table `ACC_Radiotherapy_Loc`
--

DROP TABLE IF EXISTS `ACC_Radiotherapy_Loc`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!40101 SET character_set_client = utf8 */;
CREATE TABLE `ACC_Radiotherapy_Loc` (
  `acc_radiotherapy_loc_id` int(11) NOT NULL AUTO_INCREMENT,
  `acc_radiotherapy_id` int(11) NOT NULL,
  `ensat_id` int(11) NOT NULL,
  `center_id` varchar(5) NOT NULL,
  `radiotherapy_location` varchar(30) DEFAULT NULL,
  PRIMARY KEY (`acc_radiotherapy_loc_id`,`acc_radiotherapy_id`,`ensat_id`,`center_id`),
  KEY `acc_radiotherapy_id` (`acc_radiotherapy_id`),
  KEY `ensat_id` (`ensat_id`,`center_id`)
) ENGINE=MyISAM AUTO_INCREMENT=10 DEFAULT CHARSET=latin1;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Table structure for table `ACC_Surgery`
--

DROP TABLE IF EXISTS `ACC_Surgery`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!40101 SET character_set_client = utf8 */;
CREATE TABLE `ACC_Surgery` (
  `acc_surgery_id` int(11) NOT NULL AUTO_INCREMENT,
  `ensat_id` int(11) NOT NULL,
  `center_id` varchar(5) NOT NULL,
  `surgery_date` date DEFAULT NULL,
  `surgery_type` varchar(30) DEFAULT NULL,
  `surgery_method` varchar(30) DEFAULT NULL,
  `surgery_overall_resection_status` varchar(30) DEFAULT NULL,
  PRIMARY KEY (`acc_surgery_id`,`ensat_id`,`center_id`),
  KEY `ensat_id` (`ensat_id`,`center_id`)
) ENGINE=MyISAM AUTO_INCREMENT=1727 DEFAULT CHARSET=latin1;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Table structure for table `ACC_Surgery_Extended`
--

DROP TABLE IF EXISTS `ACC_Surgery_Extended`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!40101 SET character_set_client = utf8 */;
CREATE TABLE `ACC_Surgery_Extended` (
  `acc_surgery_extended_id` int(11) NOT NULL AUTO_INCREMENT,
  `acc_surgery_id` int(11) NOT NULL,
  `ensat_id` int(11) NOT NULL,
  `center_id` varchar(5) NOT NULL,
  `surgery_extended` varchar(30) DEFAULT NULL,
  PRIMARY KEY (`acc_surgery_extended_id`,`acc_surgery_id`,`ensat_id`,`center_id`),
  KEY `acc_surgery_id` (`acc_surgery_id`),
  KEY `ensat_id` (`ensat_id`,`center_id`)
) ENGINE=MyISAM AUTO_INCREMENT=124 DEFAULT CHARSET=latin1;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Table structure for table `ACC_Surgery_First`
--

DROP TABLE IF EXISTS `ACC_Surgery_First`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!40101 SET character_set_client = utf8 */;
CREATE TABLE `ACC_Surgery_First` (
  `acc_surgery_first_id` int(11) NOT NULL AUTO_INCREMENT,
  `acc_surgery_id` int(11) NOT NULL,
  `ensat_id` int(11) NOT NULL,
  `center_id` varchar(5) NOT NULL,
  `surgery_first` varchar(30) DEFAULT NULL,
  PRIMARY KEY (`acc_surgery_first_id`,`acc_surgery_id`,`ensat_id`,`center_id`),
  KEY `acc_surgery_id` (`acc_surgery_id`),
  KEY `ensat_id` (`ensat_id`,`center_id`)
) ENGINE=MyISAM AUTO_INCREMENT=8 DEFAULT CHARSET=latin1;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Table structure for table `ACC_TumorStaging`
--

DROP TABLE IF EXISTS `ACC_TumorStaging`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!40101 SET character_set_client = utf8 */;
CREATE TABLE `ACC_TumorStaging` (
  `ensat_id` int(11) NOT NULL,
  `center_id` varchar(5) NOT NULL,
  `site_of_adrenal_tumor` varchar(5) DEFAULT NULL,
  `information_based_on` varchar(30) DEFAULT NULL,
  `size_of_adrenal_tumor` varchar(10) DEFAULT NULL,
  `regional_lymph_nodes` varchar(14) DEFAULT NULL,
  `tumor_infiltration_adipose` varchar(14) DEFAULT NULL,
  `tumor_invasion_adjacent` varchar(14) DEFAULT NULL,
  `tumor_thrombus_vena_renal` varchar(14) DEFAULT NULL,
  `distant_metastases` varchar(9) DEFAULT NULL,
  `bone` varchar(30) DEFAULT NULL,
  `liver` varchar(30) DEFAULT NULL,
  `lung` varchar(30) DEFAULT NULL,
  `abdomen_lymph_nodes` varchar(100) DEFAULT NULL,
  `other_metastases` varchar(100) DEFAULT NULL,
  `ensat_classification` varchar(30) DEFAULT NULL,
  PRIMARY KEY (`ensat_id`,`center_id`)
) ENGINE=MyISAM DEFAULT CHARSET=latin1;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Table structure for table `APA_BiochemicalAssessment`
--

DROP TABLE IF EXISTS `APA_BiochemicalAssessment`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!40101 SET character_set_client = utf8 */;
CREATE TABLE `APA_BiochemicalAssessment` (
  `biochemical_assessment_id` int(11) NOT NULL AUTO_INCREMENT,
  `ensat_id` int(11) NOT NULL,
  `center_id` varchar(5) NOT NULL,
  `assessment_date` date DEFAULT NULL,
  `spironolactone` varchar(4) DEFAULT NULL,
  `other_diuretic` varchar(4) DEFAULT NULL,
  `beta_blocker` varchar(4) DEFAULT NULL,
  `ace_inhibitor` varchar(4) DEFAULT NULL,
  `ang_receptor` varchar(4) DEFAULT NULL,
  `central_anti_ht` varchar(4) DEFAULT NULL,
  `calcium_channel` varchar(4) DEFAULT NULL,
  `alpha_blocker` varchar(4) DEFAULT NULL,
  `potassium_salts` varchar(4) DEFAULT NULL,
  `vasodilatators` varchar(4) DEFAULT NULL,
  `renin_inhibitors` varchar(4) DEFAULT NULL,
  `serum_potassium` varchar(10) DEFAULT NULL,
  `serum_sodium` varchar(10) DEFAULT NULL,
  `plasma_creatinine` varchar(10) DEFAULT NULL,
  `supine_pra` varchar(10) DEFAULT NULL,
  `sitting_pra` varchar(10) DEFAULT NULL,
  `supine_arc` varchar(10) DEFAULT NULL,
  `sitting_arc` varchar(10) DEFAULT NULL,
  `standing_aldosterone` varchar(10) DEFAULT NULL,
  `sitting_aldosterone` varchar(10) DEFAULT NULL,
  `urinary_aldosterone` varchar(10) DEFAULT NULL,
  `urinary_tetrahydroaldosterone` varchar(10) DEFAULT NULL,
  `post_captopril_aldosterone` varchar(10) DEFAULT NULL,
  `post_oral_sodium_aldosterone` varchar(10) DEFAULT NULL,
  `post_saline_infusion_aldosterone` varchar(10) DEFAULT NULL,
  `post_fludrocorticone_aldosterone` varchar(10) DEFAULT NULL,
  `post_furosemide_aldosterone` varchar(10) DEFAULT NULL,
  `aldosterone_right` varchar(10) DEFAULT NULL,
  `corticol_right` varchar(10) DEFAULT NULL,
  `aldosterone_left` varchar(10) DEFAULT NULL,
  `corticol_left` varchar(10) DEFAULT NULL,
  `aldosterone_vena_cava` varchar(10) DEFAULT NULL,
  `corticol_vena_cava` varchar(10) DEFAULT NULL,
  `bilateral_avs` varchar(4) DEFAULT NULL,
  `stimulation_avs` varchar(4) DEFAULT NULL,
  `biochemical_comment` varchar(30) DEFAULT NULL,
  PRIMARY KEY (`biochemical_assessment_id`,`ensat_id`,`center_id`),
  KEY `ensat_id` (`ensat_id`,`center_id`)
) ENGINE=MyISAM AUTO_INCREMENT=824 DEFAULT CHARSET=latin1;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Table structure for table `APA_Biomaterial`
--

DROP TABLE IF EXISTS `APA_Biomaterial`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!40101 SET character_set_client = utf8 */;
CREATE TABLE `APA_Biomaterial` (
  `apa_biomaterial_id` int(11) NOT NULL AUTO_INCREMENT,
  `ensat_id` int(11) NOT NULL,
  `center_id` varchar(5) NOT NULL,
  `biomaterial_date` date DEFAULT NULL,
  `tumor_tissue_frozen` varchar(14) DEFAULT NULL,
  `tumor_tissue_ensat_sop` varchar(14) DEFAULT NULL,
  `tumor_tissue_paraffin` varchar(14) DEFAULT NULL,
  `tumor_tissue_dna` varchar(4) DEFAULT NULL,
  `leukocyte_dna` varchar(14) DEFAULT NULL,
  `plasma` varchar(14) DEFAULT NULL,
  `heparin_plasma` varchar(30) DEFAULT NULL,
  `serum` varchar(14) DEFAULT NULL,
  `24h_urine` varchar(14) DEFAULT NULL,
  `24h_urine_vol` varchar(30) DEFAULT NULL,
  `spot_urine` varchar(14) DEFAULT NULL,
  `normal_tissue` varchar(14) DEFAULT NULL,
  `normal_tissue_paraffin` varchar(4) DEFAULT NULL,
  `normal_tissue_dna` varchar(4) DEFAULT NULL,
  `associated_study` varchar(100) DEFAULT NULL,
  `associated_study_phase_visit` varchar(100) DEFAULT NULL,
  `freezer_information` varchar(100) DEFAULT NULL,
  PRIMARY KEY (`apa_biomaterial_id`,`ensat_id`,`center_id`)
) ENGINE=MyISAM AUTO_INCREMENT=411 DEFAULT CHARSET=latin1;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Table structure for table `APA_Biomaterial_Aliquots`
--

DROP TABLE IF EXISTS `APA_Biomaterial_Aliquots`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!40101 SET character_set_client = utf8 */;
CREATE TABLE `APA_Biomaterial_Aliquots` (
  `apa_biomaterial_aliquot_id` int(11) NOT NULL AUTO_INCREMENT,
  `apa_biomaterial_id` int(11) NOT NULL,
  `ensat_id` int(11) NOT NULL,
  `center_id` varchar(5) NOT NULL,
  `parameter_name` varchar(30) DEFAULT NULL,
  `aliquot_number` varchar(3) DEFAULT NULL,
  PRIMARY KEY (`apa_biomaterial_aliquot_id`,`apa_biomaterial_id`,`ensat_id`,`center_id`),
  KEY `ensat_id` (`ensat_id`,`center_id`)
) ENGINE=MyISAM AUTO_INCREMENT=7 DEFAULT CHARSET=latin1;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Table structure for table `APA_Biomaterial_Normal_Tissue`
--

DROP TABLE IF EXISTS `APA_Biomaterial_Normal_Tissue`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!40101 SET character_set_client = utf8 */;
CREATE TABLE `APA_Biomaterial_Normal_Tissue` (
  `apa_biomaterial_normal_tissue_id` int(11) NOT NULL AUTO_INCREMENT,
  `apa_biomaterial_id` int(11) DEFAULT NULL,
  `ensat_id` int(11) NOT NULL,
  `center_id` varchar(5) NOT NULL,
  `normal_tissue_type` varchar(30) DEFAULT NULL,
  `normal_tissue_specific` varchar(100) DEFAULT NULL,
  PRIMARY KEY (`apa_biomaterial_normal_tissue_id`,`ensat_id`,`center_id`),
  KEY `ensat_id` (`ensat_id`,`center_id`)
) ENGINE=MyISAM AUTO_INCREMENT=6 DEFAULT CHARSET=latin1;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Table structure for table `APA_Cardio`
--

DROP TABLE IF EXISTS `APA_Cardio`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!40101 SET character_set_client = utf8 */;
CREATE TABLE `APA_Cardio` (
  `apa_cardio_id` int(11) NOT NULL AUTO_INCREMENT,
  `ensat_id` int(11) NOT NULL,
  `center_id` varchar(5) NOT NULL,
  `event_date` date DEFAULT NULL,
  `echocardiography` varchar(30) DEFAULT NULL,
  `echocardiography_specific` varchar(30) DEFAULT NULL,
  `electrocardiogram` varchar(30) DEFAULT NULL,
  `electrocardiogram_specific` varchar(30) DEFAULT NULL,
  `electrocardiogram_other` varchar(100) DEFAULT NULL,
  PRIMARY KEY (`apa_cardio_id`,`ensat_id`,`center_id`),
  KEY `ensat_id` (`ensat_id`,`center_id`)
) ENGINE=MyISAM AUTO_INCREMENT=2 DEFAULT CHARSET=latin1;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Table structure for table `APA_ClinicalAssessment`
--

DROP TABLE IF EXISTS `APA_ClinicalAssessment`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!40101 SET character_set_client = utf8 */;
CREATE TABLE `APA_ClinicalAssessment` (
  `clinical_assessment_id` int(11) NOT NULL AUTO_INCREMENT,
  `ensat_id` int(11) NOT NULL,
  `center_id` varchar(5) NOT NULL,
  `assessment_date` date DEFAULT NULL,
  `height` float DEFAULT NULL,
  `weight` float DEFAULT NULL,
  `systolic_blood_pressure` float DEFAULT NULL,
  `diastolic_blood_pressure` float DEFAULT NULL,
  `heart_rate` float DEFAULT NULL,
  PRIMARY KEY (`clinical_assessment_id`,`ensat_id`,`center_id`),
  KEY `ensat_id` (`ensat_id`,`center_id`)
) ENGINE=MyISAM AUTO_INCREMENT=660 DEFAULT CHARSET=latin1;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Table structure for table `APA_Complication`
--

DROP TABLE IF EXISTS `APA_Complication`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!40101 SET character_set_client = utf8 */;
CREATE TABLE `APA_Complication` (
  `apa_complication_id` int(11) NOT NULL AUTO_INCREMENT,
  `ensat_id` int(11) NOT NULL,
  `center_id` varchar(5) NOT NULL,
  `event_date` date DEFAULT NULL,
  `complication` varchar(30) DEFAULT NULL,
  `diabetes` varchar(30) DEFAULT NULL,
  `hypercholesterenimia` varchar(30) DEFAULT NULL,
  `smoking_status` varchar(30) DEFAULT NULL,
  `comment` varchar(100) DEFAULT NULL,
  PRIMARY KEY (`apa_complication_id`,`ensat_id`,`center_id`),
  KEY `ensat_id` (`ensat_id`,`center_id`)
) ENGINE=MyISAM AUTO_INCREMENT=265 DEFAULT CHARSET=latin1;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Table structure for table `APA_FollowUp`
--

DROP TABLE IF EXISTS `APA_FollowUp`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!40101 SET character_set_client = utf8 */;
CREATE TABLE `APA_FollowUp` (
  `apa_followup_id` int(11) NOT NULL AUTO_INCREMENT,
  `ensat_id` int(11) NOT NULL,
  `center_id` varchar(5) NOT NULL,
  `followup_date` date DEFAULT NULL,
  `followup_alive` varchar(4) DEFAULT NULL,
  `cause_of_death` varchar(30) DEFAULT NULL,
  `followup_weight` float DEFAULT NULL,
  `followup_systolic_bp` float DEFAULT NULL,
  `followup_diastolic_bp` float DEFAULT NULL,
  `spironolactone` varchar(4) DEFAULT NULL,
  `other_diuretic` varchar(4) DEFAULT NULL,
  `beta_blocker` varchar(4) DEFAULT NULL,
  `ace_inhibitor` varchar(4) DEFAULT NULL,
  `ang_receptor` varchar(4) DEFAULT NULL,
  `central_anti_ht` varchar(4) DEFAULT NULL,
  `calcium_channel` varchar(4) DEFAULT NULL,
  `alpha_blocker` varchar(4) DEFAULT NULL,
  `potassium_salts` varchar(4) DEFAULT NULL,
  `eval_of_bp_outcomes` varchar(30) DEFAULT NULL,
  `serum_potassium` float DEFAULT NULL,
  `serum_sodium` float DEFAULT NULL,
  `plasma_creatinine` float DEFAULT NULL,
  `supine_pra` float DEFAULT NULL,
  `sitting_pra` float DEFAULT NULL,
  `supine_arc` float DEFAULT NULL,
  `sitting_arc` float DEFAULT NULL,
  `standing_aldosterone` float DEFAULT NULL,
  `sitting_aldosterone` float DEFAULT NULL,
  `urinary_aldosterone` float DEFAULT NULL,
  `urinary_tetrahydroaldosterone` float DEFAULT NULL,
  `followup_comment` varchar(100) DEFAULT NULL,
  `followup_complications` varchar(100) DEFAULT NULL,
  PRIMARY KEY (`apa_followup_id`,`ensat_id`,`center_id`),
  KEY `ensat_id` (`ensat_id`,`center_id`)
) ENGINE=MyISAM AUTO_INCREMENT=54 DEFAULT CHARSET=latin1;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Table structure for table `APA_Imaging`
--

DROP TABLE IF EXISTS `APA_Imaging`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!40101 SET character_set_client = utf8 */;
CREATE TABLE `APA_Imaging` (
  `apa_imaging_id` int(11) NOT NULL AUTO_INCREMENT,
  `ensat_id` int(11) NOT NULL,
  `center_id` varchar(5) NOT NULL,
  `imaging_date` date DEFAULT NULL,
  `adrenal_state` varchar(30) DEFAULT NULL,
  `tumor_sites_imaging` varchar(30) DEFAULT NULL,
  `max_tumor_by_ct_right` float DEFAULT NULL,
  `max_tumor_by_mr_right` float DEFAULT NULL,
  `max_tumor_by_ct_left` float DEFAULT NULL,
  `max_tumor_by_mr_left` float DEFAULT NULL,
  `scintigraphy` varchar(30) DEFAULT NULL,
  PRIMARY KEY (`apa_imaging_id`,`ensat_id`,`center_id`),
  KEY `ensat_id` (`ensat_id`,`center_id`)
) ENGINE=MyISAM AUTO_INCREMENT=654 DEFAULT CHARSET=latin1;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Table structure for table `APA_PatientHistory`
--

DROP TABLE IF EXISTS `APA_PatientHistory`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!40101 SET character_set_client = utf8 */;
CREATE TABLE `APA_PatientHistory` (
  `ensat_id` int(11) NOT NULL,
  `center_id` varchar(5) NOT NULL,
  `year_of_diagnosis` varchar(4) DEFAULT NULL,
  `hypertension_presentation` varchar(4) DEFAULT NULL,
  `hypertension_year` varchar(4) DEFAULT NULL,
  `first_degree_relatives_hypertension` int(11) DEFAULT NULL,
  `first_degree_relatives_pal` int(11) DEFAULT NULL,
  `doc_genetic_disease` varchar(4) DEFAULT NULL,
  `lowest_kalemia` float DEFAULT NULL,
  `apa_comment` varchar(100) DEFAULT NULL,
  PRIMARY KEY (`ensat_id`,`center_id`)
) ENGINE=MyISAM DEFAULT CHARSET=latin1;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Table structure for table `APA_Surgery`
--

DROP TABLE IF EXISTS `APA_Surgery`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!40101 SET character_set_client = utf8 */;
CREATE TABLE `APA_Surgery` (
  `apa_surgery_id` int(11) NOT NULL AUTO_INCREMENT,
  `ensat_id` int(11) NOT NULL,
  `center_id` varchar(5) NOT NULL,
  `intervention_date` date DEFAULT NULL,
  `tumor_sites_surgery` varchar(30) DEFAULT NULL,
  `surgery_procedure` varchar(30) DEFAULT NULL,
  `adrenal_sparing` varchar(4) DEFAULT NULL,
  `max_tumor_right` float DEFAULT NULL,
  `max_tumor_left` float DEFAULT NULL,
  `tumor_weight` float DEFAULT NULL,
  `comment` varchar(100) DEFAULT NULL,
  PRIMARY KEY (`apa_surgery_id`,`ensat_id`,`center_id`),
  KEY `ensat_id` (`ensat_id`,`center_id`)
) ENGINE=MyISAM AUTO_INCREMENT=320 DEFAULT CHARSET=latin1;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Table structure for table `Identification`
--

DROP TABLE IF EXISTS `Identification`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!40101 SET character_set_client = utf8 */;
CREATE TABLE `Identification` (
  `ensat_id` int(11) NOT NULL AUTO_INCREMENT,
  `center_id` varchar(5) NOT NULL,
  `local_investigator` varchar(30) DEFAULT NULL,
  `investigator_email` varchar(50) DEFAULT NULL,
  `record_date` date DEFAULT NULL,
  `date_first_reg` date DEFAULT NULL,
  `sex` varchar(1) DEFAULT NULL,
  `year_of_birth` varchar(4) DEFAULT NULL,
  `consent_obtained` varchar(30) DEFAULT NULL,
  `uploader` varchar(30) DEFAULT NULL,
  `ensat_database` varchar(10) DEFAULT NULL,
  `eurine_act_inclusion` varchar(4) DEFAULT NULL,
  PRIMARY KEY (`ensat_id`,`center_id`)
) ENGINE=MyISAM AUTO_INCREMENT=986 DEFAULT CHARSET=latin1;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Table structure for table `NAPACA_Biomaterial`
--

DROP TABLE IF EXISTS `NAPACA_Biomaterial`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!40101 SET character_set_client = utf8 */;
CREATE TABLE `NAPACA_Biomaterial` (
  `napaca_biomaterial_id` int(11) NOT NULL AUTO_INCREMENT,
  `ensat_id` int(11) NOT NULL,
  `center_id` varchar(5) NOT NULL,
  `biomaterial_date` date DEFAULT NULL,
  `tumor_tissue_frozen` varchar(4) DEFAULT NULL,
  `tumor_tissue_ensat_sop` varchar(4) DEFAULT NULL,
  `tumor_tissue_paraffin` varchar(4) DEFAULT NULL,
  `tumor_tissue_dna` varchar(4) DEFAULT NULL,
  `leukocyte_dna` varchar(14) DEFAULT NULL,
  `plasma` varchar(14) DEFAULT NULL,
  `heparin_plasma` varchar(30) DEFAULT NULL,
  `serum` varchar(14) DEFAULT NULL,
  `24h_urine` varchar(14) DEFAULT NULL,
  `24h_urine_vol` varchar(30) DEFAULT NULL,
  `spot_urine` varchar(14) DEFAULT NULL,
  `normal_tissue` varchar(14) DEFAULT NULL,
  `normal_tissue_paraffin` varchar(4) DEFAULT NULL,
  `normal_tissue_dna` varchar(4) DEFAULT NULL,
  `associated_study` varchar(100) DEFAULT NULL,
  `associated_study_phase_visit` varchar(100) DEFAULT NULL,
  `freezer_information` varchar(100) DEFAULT NULL,
  PRIMARY KEY (`napaca_biomaterial_id`,`ensat_id`,`center_id`),
  KEY `ensat_id` (`ensat_id`,`center_id`)
) ENGINE=MyISAM AUTO_INCREMENT=600 DEFAULT CHARSET=latin1;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Table structure for table `NAPACA_Biomaterial_Aliquots`
--

DROP TABLE IF EXISTS `NAPACA_Biomaterial_Aliquots`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!40101 SET character_set_client = utf8 */;
CREATE TABLE `NAPACA_Biomaterial_Aliquots` (
  `napaca_biomaterial_aliquot_id` int(11) NOT NULL AUTO_INCREMENT,
  `napaca_biomaterial_id` int(11) NOT NULL,
  `ensat_id` int(11) NOT NULL,
  `center_id` varchar(5) NOT NULL,
  `parameter_name` varchar(30) DEFAULT NULL,
  `aliquot_number` varchar(3) DEFAULT NULL,
  PRIMARY KEY (`napaca_biomaterial_aliquot_id`,`napaca_biomaterial_id`,`ensat_id`,`center_id`),
  KEY `ensat_id` (`ensat_id`,`center_id`)
) ENGINE=MyISAM AUTO_INCREMENT=311 DEFAULT CHARSET=latin1;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Table structure for table `NAPACA_Biomaterial_Normal_Tissue`
--

DROP TABLE IF EXISTS `NAPACA_Biomaterial_Normal_Tissue`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!40101 SET character_set_client = utf8 */;
CREATE TABLE `NAPACA_Biomaterial_Normal_Tissue` (
  `napaca_biomaterial_normal_tissue_id` int(11) NOT NULL AUTO_INCREMENT,
  `napaca_biomaterial_id` int(11) DEFAULT NULL,
  `ensat_id` int(11) NOT NULL,
  `center_id` varchar(5) NOT NULL,
  `normal_tissue_type` varchar(30) DEFAULT NULL,
  `normal_tissue_specific` varchar(100) DEFAULT NULL,
  PRIMARY KEY (`napaca_biomaterial_normal_tissue_id`,`ensat_id`,`center_id`),
  KEY `ensat_id` (`ensat_id`,`center_id`)
) ENGINE=MyISAM AUTO_INCREMENT=5 DEFAULT CHARSET=latin1;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Table structure for table `NAPACA_DiagnosticProcedures`
--

DROP TABLE IF EXISTS `NAPACA_DiagnosticProcedures`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!40101 SET character_set_client = utf8 */;
CREATE TABLE `NAPACA_DiagnosticProcedures` (
  `ensat_id` int(11) NOT NULL,
  `center_id` varchar(5) NOT NULL,
  `height` float DEFAULT NULL,
  `weight` float DEFAULT NULL,
  `bmi` float DEFAULT NULL,
  `year_of_diagnosis` varchar(4) DEFAULT NULL,
  `month_of_diagnosis` varchar(4) DEFAULT NULL,
  `symptoms_incidental` varchar(3) DEFAULT NULL,
  `symptoms_endocrine` varchar(9) DEFAULT NULL,
  `cushings_syndrome` varchar(9) DEFAULT NULL,
  `virilisation` varchar(9) DEFAULT NULL,
  `feminization` varchar(14) DEFAULT NULL,
  `mineralocorticoid_excess` varchar(9) DEFAULT NULL,
  `hypertension_presentation` varchar(30) DEFAULT NULL,
  `hypertension_year` varchar(4) DEFAULT NULL,
  `diabetestype2_presentation` varchar(30) DEFAULT NULL,
  `diabetestype2_year` varchar(4) DEFAULT NULL,
  `dyslipidaemia_presentation` varchar(30) DEFAULT NULL,
  `dyslipidaemia_year` varchar(4) DEFAULT NULL,
  `osteoporosis_presentation` varchar(30) DEFAULT NULL,
  `osteoporosis_year` varchar(4) DEFAULT NULL,
  `prev_cardio_events` varchar(30) DEFAULT NULL,
  `prev_cardio_year1` varchar(4) DEFAULT NULL,
  `prev_cardio_year2` varchar(4) DEFAULT NULL,
  `antidiabetic_drugs` varchar(30) DEFAULT NULL,
  `lipidlowering_drugs` varchar(30) DEFAULT NULL,
  `osteoporosis_drugs` varchar(30) DEFAULT NULL,
  `antihypertensive_drugs` varchar(30) DEFAULT NULL,
  `gluco_serum_cortisol` varchar(30) DEFAULT NULL,
  `gluco_serum_cortisol_specific` varchar(30) DEFAULT NULL,
  `gluco_plasma_acth` varchar(30) DEFAULT NULL,
  `gluco_plasma_acth_specific` varchar(30) DEFAULT NULL,
  `gluco_urinary_free_cortisol` varchar(30) DEFAULT NULL,
  `gluco_urinary_free_cortisol_specific` varchar(30) DEFAULT NULL,
  `gluco_urinary_free_method` varchar(5) DEFAULT NULL,
  `mineralo_plasma_renin_activity` varchar(30) DEFAULT NULL,
  `mineralo_plasma_renin_conc` varchar(30) DEFAULT NULL,
  `mineralo_serum_aldosterone` varchar(30) DEFAULT NULL,
  `other_steroid_17hydroxyprogesterone` varchar(30) DEFAULT NULL,
  `other_steroid_17hydroxyprogesterone_specific` varchar(30) DEFAULT NULL,
  `other_steroid_serum_dheas` varchar(30) DEFAULT NULL,
  `other_steroid_serum_dheas_specific` varchar(30) DEFAULT NULL,
  `catechol_urinary_free_excretion` varchar(30) DEFAULT NULL,
  `catechol_urinary_metanephrine_excretion` varchar(30) DEFAULT NULL,
  `catechol_plasma_metanephrines` varchar(30) DEFAULT NULL,
  `other_malignancies` varchar(9) DEFAULT NULL,
  `which_malignancies` varchar(100) DEFAULT NULL,
  `year_other_maligs` varchar(4) DEFAULT NULL,
  PRIMARY KEY (`ensat_id`,`center_id`)
) ENGINE=MyISAM DEFAULT CHARSET=latin1;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Table structure for table `NAPACA_FollowUp`
--

DROP TABLE IF EXISTS `NAPACA_FollowUp`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!40101 SET character_set_client = utf8 */;
CREATE TABLE `NAPACA_FollowUp` (
  `napaca_followup_id` int(11) NOT NULL AUTO_INCREMENT,
  `ensat_id` int(11) NOT NULL,
  `center_id` varchar(5) NOT NULL,
  `followup_date` date DEFAULT NULL,
  `followup_alive` varchar(4) DEFAULT NULL,
  `death_related_tumor` varchar(30) DEFAULT NULL,
  `date_of_death` date DEFAULT NULL,
  `followup_imaging` varchar(4) DEFAULT NULL,
  `followup_imaging_type` varchar(30) DEFAULT NULL,
  `followup_max_tumor` varchar(4) DEFAULT NULL,
  `followup_changes_hormone_secretion` varchar(4) DEFAULT NULL,
  `gluco_serum_cortisol` varchar(30) DEFAULT NULL,
  `gluco_plasma_acth` varchar(30) DEFAULT NULL,
  `gluco_urinary_free_cortisol` varchar(30) DEFAULT NULL,
  `mineralo_plasma_renin_activity` varchar(30) DEFAULT NULL,
  `mineralo_plasma_renin_conc` varchar(30) DEFAULT NULL,
  `mineralo_serum_aldosterone` varchar(30) DEFAULT NULL,
  `other_steroid_17hydroxyprogesterone` varchar(30) DEFAULT NULL,
  `other_steroid_serum_dheas` varchar(30) DEFAULT NULL,
  `catechol_urinary_free_excretion` varchar(30) DEFAULT NULL,
  `catechol_urinary_metanephrine_excretion` varchar(30) DEFAULT NULL,
  `catechol_plasma_metanephrines` varchar(30) DEFAULT NULL,
  `further_plans` varchar(30) DEFAULT NULL,
  PRIMARY KEY (`napaca_followup_id`,`ensat_id`,`center_id`),
  KEY `ensat_id` (`ensat_id`,`center_id`)
) ENGINE=MyISAM AUTO_INCREMENT=143 DEFAULT CHARSET=latin1;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Table structure for table `NAPACA_Imaging`
--

DROP TABLE IF EXISTS `NAPACA_Imaging`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!40101 SET character_set_client = utf8 */;
CREATE TABLE `NAPACA_Imaging` (
  `napaca_imaging_id` int(11) NOT NULL AUTO_INCREMENT,
  `ensat_id` int(11) NOT NULL,
  `center_id` varchar(5) NOT NULL,
  `imaging_date` date DEFAULT NULL,
  `tumor_sites` varchar(30) DEFAULT NULL,
  `right_adrenal_max_tumor` float DEFAULT NULL,
  `left_adrenal_max_tumor` float DEFAULT NULL,
  `imaging_of_tumor` varchar(3) DEFAULT NULL,
  `ct_tumor_density` varchar(10) DEFAULT NULL,
  `ct_tumor_density_specific` varchar(4) DEFAULT NULL,
  `ct_delay_contrast_washout` varchar(10) DEFAULT NULL,
  `evidence_extra_adrenal` varchar(4) DEFAULT NULL,
  `additional_imaging_performed` varchar(4) DEFAULT NULL,
  `mri_chemical_shift_analysis` varchar(4) DEFAULT NULL,
  `fdg_pet` varchar(4) DEFAULT NULL,
  `comment` varchar(100) DEFAULT NULL,
  PRIMARY KEY (`napaca_imaging_id`,`ensat_id`,`center_id`),
  KEY `ensat_id` (`ensat_id`,`center_id`)
) ENGINE=MyISAM AUTO_INCREMENT=124 DEFAULT CHARSET=latin1;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Table structure for table `NAPACA_Pathology`
--

DROP TABLE IF EXISTS `NAPACA_Pathology`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!40101 SET character_set_client = utf8 */;
CREATE TABLE `NAPACA_Pathology` (
  `napaca_pathology_id` int(11) NOT NULL AUTO_INCREMENT,
  `ensat_id` int(11) NOT NULL,
  `center_id` varchar(5) NOT NULL,
  `pathology_date` date DEFAULT NULL,
  `pathologist_name` varchar(30) DEFAULT NULL,
  `pathologist_location` varchar(30) DEFAULT NULL,
  `pathology_diagnosis` varchar(100) DEFAULT NULL,
  `number_of_mitoses_exact` varchar(30) DEFAULT NULL,
  `ki67` varchar(30) DEFAULT NULL,
  `weiss_score` varchar(14) DEFAULT NULL,
  `nuclear_atypia` varchar(14) DEFAULT NULL,
  `atypical_mitosis` varchar(14) DEFAULT NULL,
  `spongiocytic_tumor_cells` varchar(14) DEFAULT NULL,
  `diffuse_architecture` varchar(14) DEFAULT NULL,
  `venous_invasion` varchar(14) DEFAULT NULL,
  `sinus_invasion` varchar(14) DEFAULT NULL,
  `capsular_invasion` varchar(14) DEFAULT NULL,
  `necrosis` varchar(14) DEFAULT NULL,
  `number_of_mitoses_per5` varchar(14) DEFAULT NULL,
  PRIMARY KEY (`napaca_pathology_id`,`ensat_id`,`center_id`),
  KEY `ensat_id` (`ensat_id`,`center_id`)
) ENGINE=MyISAM AUTO_INCREMENT=68 DEFAULT CHARSET=latin1;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Table structure for table `NAPACA_Surgery`
--

DROP TABLE IF EXISTS `NAPACA_Surgery`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!40101 SET character_set_client = utf8 */;
CREATE TABLE `NAPACA_Surgery` (
  `napaca_surgery_id` int(11) NOT NULL AUTO_INCREMENT,
  `ensat_id` int(11) NOT NULL,
  `center_id` varchar(5) NOT NULL,
  `surgery_date` date DEFAULT NULL,
  `surgical_approach` varchar(30) DEFAULT NULL,
  PRIMARY KEY (`napaca_surgery_id`,`ensat_id`,`center_id`),
  KEY `ensat_id` (`ensat_id`,`center_id`)
) ENGINE=MyISAM AUTO_INCREMENT=70 DEFAULT CHARSET=latin1;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Table structure for table `Pheo_BiochemicalAssessment`
--

DROP TABLE IF EXISTS `Pheo_BiochemicalAssessment`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!40101 SET character_set_client = utf8 */;
CREATE TABLE `Pheo_BiochemicalAssessment` (
  `biochemical_assessment_id` int(11) NOT NULL AUTO_INCREMENT,
  `ensat_id` int(11) NOT NULL,
  `center_id` varchar(5) NOT NULL,
  `plasma_e` float DEFAULT NULL,
  `plasma_n` float DEFAULT NULL,
  `plasma_date` date DEFAULT NULL,
  `plasma_free_m` float DEFAULT NULL,
  `plasma_free_n` float DEFAULT NULL,
  `plasma_free_methox` float DEFAULT NULL,
  `plasma_free_date` date DEFAULT NULL,
  `serum_chromo_a` float DEFAULT NULL,
  `serum_chromo_a_date` date DEFAULT NULL,
  `urine_free_e` float DEFAULT NULL,
  `urine_free_n` float DEFAULT NULL,
  `urine_free_date` date DEFAULT NULL,
  `urine_m` float DEFAULT NULL,
  `urine_n` float DEFAULT NULL,
  `urine_date` date DEFAULT NULL,
  `plasma_dopamine_conc` float DEFAULT NULL,
  `plasma_dopamine_date` date DEFAULT NULL,
  PRIMARY KEY (`biochemical_assessment_id`,`ensat_id`,`center_id`),
  KEY `ensat_id` (`ensat_id`,`center_id`)
) ENGINE=MyISAM AUTO_INCREMENT=577 DEFAULT CHARSET=latin1;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Table structure for table `Pheo_Biomaterial`
--

DROP TABLE IF EXISTS `Pheo_Biomaterial`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!40101 SET character_set_client = utf8 */;
CREATE TABLE `Pheo_Biomaterial` (
  `pheo_biomaterial_id` int(11) NOT NULL AUTO_INCREMENT,
  `ensat_id` int(11) NOT NULL,
  `center_id` varchar(5) NOT NULL,
  `biomaterial_date` date DEFAULT NULL,
  `tumor_tissue_frozen` varchar(14) DEFAULT NULL,
  `tumor_tissue_ensat_sop` varchar(14) DEFAULT NULL,
  `tumor_tissue_paraffin` varchar(14) DEFAULT NULL,
  `tumor_tissue_dna` varchar(4) DEFAULT NULL,
  `leukocyte_dna` varchar(14) DEFAULT NULL,
  `plasma` varchar(14) DEFAULT NULL,
  `heparin_plasma` varchar(30) DEFAULT NULL,
  `serum` varchar(14) DEFAULT NULL,
  `24h_urine` varchar(14) DEFAULT NULL,
  `24h_urine_vol` varchar(30) DEFAULT NULL,
  `spot_urine` varchar(14) DEFAULT NULL,
  `normal_tissue` varchar(14) DEFAULT NULL,
  `normal_tissue_paraffin` varchar(4) DEFAULT NULL,
  `normal_tissue_dna` varchar(4) DEFAULT NULL,
  `whole_blood` varchar(14) DEFAULT NULL,
  `associated_study` varchar(100) DEFAULT NULL,
  `associated_study_phase_visit` varchar(100) DEFAULT NULL,
  `freezer_information` varchar(100) DEFAULT NULL,
  PRIMARY KEY (`pheo_biomaterial_id`,`ensat_id`,`center_id`),
  KEY `ensat_id` (`ensat_id`,`center_id`)
) ENGINE=MyISAM AUTO_INCREMENT=328 DEFAULT CHARSET=latin1;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Table structure for table `Pheo_Biomaterial_Aliquots`
--

DROP TABLE IF EXISTS `Pheo_Biomaterial_Aliquots`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!40101 SET character_set_client = utf8 */;
CREATE TABLE `Pheo_Biomaterial_Aliquots` (
  `pheo_biomaterial_aliquot_id` int(11) NOT NULL AUTO_INCREMENT,
  `pheo_biomaterial_id` int(11) NOT NULL,
  `ensat_id` int(11) NOT NULL,
  `center_id` varchar(5) NOT NULL,
  `parameter_name` varchar(30) DEFAULT NULL,
  `aliquot_number` varchar(3) DEFAULT NULL,
  PRIMARY KEY (`pheo_biomaterial_aliquot_id`,`pheo_biomaterial_id`,`ensat_id`,`center_id`),
  KEY `ensat_id` (`ensat_id`,`center_id`)
) ENGINE=MyISAM AUTO_INCREMENT=15 DEFAULT CHARSET=latin1;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Table structure for table `Pheo_Biomaterial_Normal_Tissue`
--

DROP TABLE IF EXISTS `Pheo_Biomaterial_Normal_Tissue`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!40101 SET character_set_client = utf8 */;
CREATE TABLE `Pheo_Biomaterial_Normal_Tissue` (
  `pheo_biomaterial_normal_tissue_id` int(11) NOT NULL AUTO_INCREMENT,
  `pheo_biomaterial_id` int(11) DEFAULT NULL,
  `ensat_id` int(11) NOT NULL,
  `center_id` varchar(5) NOT NULL,
  `normal_tissue_type` varchar(30) DEFAULT NULL,
  `normal_tissue_specific` varchar(100) DEFAULT NULL,
  PRIMARY KEY (`pheo_biomaterial_normal_tissue_id`,`ensat_id`,`center_id`),
  KEY `ensat_id` (`ensat_id`,`center_id`)
) ENGINE=MyISAM AUTO_INCREMENT=5 DEFAULT CHARSET=latin1;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Table structure for table `Pheo_ClinicalAssessment`
--

DROP TABLE IF EXISTS `Pheo_ClinicalAssessment`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!40101 SET character_set_client = utf8 */;
CREATE TABLE `Pheo_ClinicalAssessment` (
  `clinical_assessment_id` int(11) NOT NULL AUTO_INCREMENT,
  `ensat_id` int(11) NOT NULL,
  `center_id` varchar(5) NOT NULL,
  `assessment_date` date DEFAULT NULL,
  `height` float DEFAULT NULL,
  `weight` float DEFAULT NULL,
  `systolic_blood_pressure` float DEFAULT NULL,
  `diastolic_blood_pressure` float DEFAULT NULL,
  PRIMARY KEY (`clinical_assessment_id`,`ensat_id`,`center_id`),
  KEY `ensat_id` (`ensat_id`,`center_id`)
) ENGINE=MyISAM AUTO_INCREMENT=198 DEFAULT CHARSET=latin1;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Table structure for table `Pheo_FirstDiagnosisPresentation`
--

DROP TABLE IF EXISTS `Pheo_FirstDiagnosisPresentation`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!40101 SET character_set_client = utf8 */;
CREATE TABLE `Pheo_FirstDiagnosisPresentation` (
  `pheo_first_diagnosis_presentation_id` int(11) NOT NULL AUTO_INCREMENT,
  `ensat_id` int(11) NOT NULL,
  `center_id` varchar(5) NOT NULL,
  `first_diagnosis_presentation` varchar(100) DEFAULT NULL,
  PRIMARY KEY (`pheo_first_diagnosis_presentation_id`,`ensat_id`,`center_id`),
  KEY `ensat_id` (`ensat_id`,`center_id`)
) ENGINE=MyISAM AUTO_INCREMENT=315 DEFAULT CHARSET=latin1;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Table structure for table `Pheo_FollowUp`
--

DROP TABLE IF EXISTS `Pheo_FollowUp`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!40101 SET character_set_client = utf8 */;
CREATE TABLE `Pheo_FollowUp` (
  `pheo_followup_id` int(11) NOT NULL AUTO_INCREMENT,
  `ensat_id` int(11) NOT NULL,
  `center_id` varchar(5) NOT NULL,
  `followup_date` date DEFAULT NULL,
  `alive` varchar(3) DEFAULT NULL,
  `phpgl_free` varchar(3) DEFAULT NULL,
  `disease_state` varchar(30) DEFAULT NULL,
  `date_of_death` date DEFAULT NULL,
  `cause_of_death` varchar(30) DEFAULT NULL,
  `comment` varchar(100) DEFAULT NULL,
  PRIMARY KEY (`pheo_followup_id`,`ensat_id`,`center_id`),
  KEY `ensat_id` (`ensat_id`,`center_id`)
) ENGINE=MyISAM AUTO_INCREMENT=679 DEFAULT CHARSET=latin1;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Table structure for table `Pheo_Genetics`
--

DROP TABLE IF EXISTS `Pheo_Genetics`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!40101 SET character_set_client = utf8 */;
CREATE TABLE `Pheo_Genetics` (
  `pheo_genetics_id` int(11) NOT NULL AUTO_INCREMENT,
  `ensat_id` int(11) NOT NULL,
  `center_id` varchar(5) NOT NULL,
  `genetics_date` date DEFAULT NULL,
  `vhl_testing_performed` varchar(4) DEFAULT NULL,
  `vhl_testing_method` varchar(100) DEFAULT NULL,
  `vhl_mutation_detected` varchar(4) DEFAULT NULL,
  `vhl_mutation_name` varchar(30) DEFAULT NULL,
  `sdhd_testing_performed` varchar(4) DEFAULT NULL,
  `sdhd_testing_method` varchar(100) DEFAULT NULL,
  `sdhd_mutation_detected` varchar(4) DEFAULT NULL,
  `sdhd_mutation_name` varchar(30) DEFAULT NULL,
  `sdhb_testing_performed` varchar(4) DEFAULT NULL,
  `sdhb_testing_method` varchar(100) DEFAULT NULL,
  `sdhb_mutation_detected` varchar(4) DEFAULT NULL,
  `sdhb_mutation_name` varchar(30) DEFAULT NULL,
  `ret_testing_performed` varchar(4) DEFAULT NULL,
  `ret_testing_method` varchar(100) DEFAULT NULL,
  `ret_mutation_detected` varchar(4) DEFAULT NULL,
  `ret_mutation_name` varchar(30) DEFAULT NULL,
  `nf1_testing_performed` varchar(4) DEFAULT NULL,
  `nf1_testing_method` varchar(100) DEFAULT NULL,
  `nf1_mutation_detected` varchar(4) DEFAULT NULL,
  `nf1_mutation_name` varchar(30) DEFAULT NULL,
  `tmem127_testing_performed` varchar(4) DEFAULT NULL,
  `tmem127_testing_method` varchar(100) DEFAULT NULL,
  `tmem127_mutation_detected` varchar(4) DEFAULT NULL,
  `tmem127_mutation_name` varchar(30) DEFAULT NULL,
  `max_testing_performed` varchar(4) DEFAULT NULL,
  `max_testing_method` varchar(100) DEFAULT NULL,
  `max_mutation_detected` varchar(4) DEFAULT NULL,
  `max_mutation_name` varchar(30) DEFAULT NULL,
  `sdhc_testing_performed` varchar(4) DEFAULT NULL,
  `sdhc_testing_method` varchar(100) DEFAULT NULL,
  `sdhc_mutation_detected` varchar(4) DEFAULT NULL,
  `sdhc_mutation_name` varchar(30) DEFAULT NULL,
  `sdha_testing_performed` varchar(4) DEFAULT NULL,
  `sdha_testing_method` varchar(100) DEFAULT NULL,
  `sdha_mutation_detected` varchar(4) DEFAULT NULL,
  `sdha_mutation_name` varchar(30) DEFAULT NULL,
  `sdhaf2_testing_performed` varchar(4) DEFAULT NULL,
  `sdhaf2_testing_method` varchar(100) DEFAULT NULL,
  `sdhaf2_mutation_detected` varchar(4) DEFAULT NULL,
  `sdhaf2_mutation_name` varchar(30) DEFAULT NULL,
  PRIMARY KEY (`pheo_genetics_id`,`ensat_id`,`center_id`),
  KEY `ensat_id` (`ensat_id`,`center_id`)
) ENGINE=MyISAM AUTO_INCREMENT=89 DEFAULT CHARSET=latin1;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Table structure for table `Pheo_ImagingTests`
--

DROP TABLE IF EXISTS `Pheo_ImagingTests`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!40101 SET character_set_client = utf8 */;
CREATE TABLE `Pheo_ImagingTests` (
  `pheo_imaging_tests_id` int(11) NOT NULL AUTO_INCREMENT,
  `ensat_id` int(11) NOT NULL,
  `center_id` varchar(5) NOT NULL,
  `ct` varchar(30) DEFAULT NULL,
  `ct_date` date DEFAULT NULL,
  `ct_primary` int(11) DEFAULT NULL,
  `ct_metastases` int(11) DEFAULT NULL,
  `nmr` varchar(30) DEFAULT NULL,
  `nmr_date` date DEFAULT NULL,
  `nmr_primary` int(11) DEFAULT NULL,
  `nmr_metastases` int(11) DEFAULT NULL,
  `mibg` varchar(30) DEFAULT NULL,
  `mibg_date` date DEFAULT NULL,
  `mibg_primary` int(11) DEFAULT NULL,
  `mibg_metastases` int(11) DEFAULT NULL,
  `octreoscan` varchar(30) DEFAULT NULL,
  `octreoscan_date` date DEFAULT NULL,
  `octreoscan_primary` int(11) DEFAULT NULL,
  `octreoscan_metastases` int(11) DEFAULT NULL,
  `fdg_pet` varchar(30) DEFAULT NULL,
  `fdg_pet_date` date DEFAULT NULL,
  `fdg_pet_primary` int(11) DEFAULT NULL,
  `fdg_pet_metastases` int(11) DEFAULT NULL,
  `da_pet` varchar(30) DEFAULT NULL,
  `da_pet_date` date DEFAULT NULL,
  `da_pet_primary` int(11) DEFAULT NULL,
  `da_pet_metastases` int(11) DEFAULT NULL,
  `synthesis_imaging_workup` date DEFAULT NULL,
  `synthesis_imaging_workup_primary` int(11) DEFAULT NULL,
  `synthesis_imaging_workup_metastases` int(11) DEFAULT NULL,
  `imaging_comment` varchar(100) DEFAULT NULL,
  `other_imaging` varchar(30) DEFAULT NULL,
  `other_imaging_date` date DEFAULT NULL,
  `other_imaging_primary` int(11) DEFAULT NULL,
  `other_imaging_metastases` int(11) DEFAULT NULL,
  PRIMARY KEY (`pheo_imaging_tests_id`,`ensat_id`,`center_id`),
  KEY `ensat_id` (`ensat_id`,`center_id`)
) ENGINE=MyISAM AUTO_INCREMENT=296 DEFAULT CHARSET=latin1;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Table structure for table `Pheo_NonSurgicalInterventions`
--

DROP TABLE IF EXISTS `Pheo_NonSurgicalInterventions`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!40101 SET character_set_client = utf8 */;
CREATE TABLE `Pheo_NonSurgicalInterventions` (
  `pheo_non_surgical_interventions_id` int(11) NOT NULL AUTO_INCREMENT,
  `ensat_id` int(11) NOT NULL,
  `center_id` varchar(5) NOT NULL,
  `mibg_therapy` varchar(3) DEFAULT NULL,
  `mibg_therapy_date` date DEFAULT NULL,
  `mibg_therapy_comment` varchar(30) DEFAULT NULL,
  `other_targeted_radiotherapy` varchar(3) DEFAULT NULL,
  `other_targeted_radiotherapy_date` date DEFAULT NULL,
  `other_targeted_radiotherapy_comment` varchar(30) DEFAULT NULL,
  `chemotherapy` varchar(3) DEFAULT NULL,
  `chemotherapy_date` date DEFAULT NULL,
  `chemotherapy_end_date` date DEFAULT NULL,
  `chemotherapy_comment` varchar(30) DEFAULT NULL,
  `radiofrequency_ablation` varchar(3) DEFAULT NULL,
  `radiofrequency_ablation_date` date DEFAULT NULL,
  `radiofrequency_ablation_comment` varchar(30) DEFAULT NULL,
  `external_radiotherapy` varchar(3) DEFAULT NULL,
  `external_radiotherapy_date` date DEFAULT NULL,
  `external_radiotherapy_comment` varchar(30) DEFAULT NULL,
  `targeted_molecular_therapy` varchar(3) DEFAULT NULL,
  `targeted_molecular_therapy_date` date DEFAULT NULL,
  `targeted_molecular_therapy_end_date` date DEFAULT NULL,
  `targeted_molecular_therapy_comment` varchar(30) DEFAULT NULL,
  `chemoembolisation` varchar(3) DEFAULT NULL,
  `chemoembolisation_date` date DEFAULT NULL,
  `chemoembolisation_comment` varchar(30) DEFAULT NULL,
  PRIMARY KEY (`pheo_non_surgical_interventions_id`,`ensat_id`,`center_id`),
  KEY `ensat_id` (`ensat_id`,`center_id`)
) ENGINE=MyISAM AUTO_INCREMENT=85 DEFAULT CHARSET=latin1;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Table structure for table `Pheo_OtherOrgans`
--

DROP TABLE IF EXISTS `Pheo_OtherOrgans`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!40101 SET character_set_client = utf8 */;
CREATE TABLE `Pheo_OtherOrgans` (
  `pheo_other_organs_id` int(11) NOT NULL AUTO_INCREMENT,
  `ensat_id` int(11) NOT NULL,
  `center_id` varchar(5) NOT NULL,
  `other_organ` varchar(100) DEFAULT NULL,
  PRIMARY KEY (`pheo_other_organs_id`,`ensat_id`,`center_id`),
  KEY `ensat_id` (`ensat_id`,`center_id`)
) ENGINE=MyISAM AUTO_INCREMENT=150 DEFAULT CHARSET=latin1;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Table structure for table `Pheo_PatientHistory`
--

DROP TABLE IF EXISTS `Pheo_PatientHistory`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!40101 SET character_set_client = utf8 */;
CREATE TABLE `Pheo_PatientHistory` (
  `ensat_id` int(11) NOT NULL,
  `center_id` varchar(5) NOT NULL,
  `history_of_hypertension` varchar(3) DEFAULT NULL,
  `year_of_hypertension_diagnosis` varchar(4) DEFAULT NULL,
  `height_at_time_consent` varchar(10) DEFAULT NULL,
  `weight_at_time_consent` varchar(10) DEFAULT NULL,
  `systolic_bp_at_time_consent` varchar(10) DEFAULT NULL,
  `diastolic_bp_at_time_consent` varchar(10) DEFAULT NULL,
  `pheo_operation_before_consent_date` varchar(3) DEFAULT NULL,
  `residual_disease` varchar(3) DEFAULT NULL,
  `disease_metastatic` varchar(3) DEFAULT NULL,
  `multiple_tumors` varchar(3) DEFAULT NULL,
  `relatives_with_tumors` varchar(10) DEFAULT NULL,
  `doc_genetic_disease` varchar(3) DEFAULT NULL,
  `phenotypic_diagnosis` varchar(30) DEFAULT NULL,
  `history_non_ppgl_tumor` varchar(3) DEFAULT NULL,
  `pheo_comment` varchar(100) DEFAULT NULL,
  PRIMARY KEY (`ensat_id`,`center_id`)
) ENGINE=MyISAM DEFAULT CHARSET=latin1;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Table structure for table `Pheo_Surgery`
--

DROP TABLE IF EXISTS `Pheo_Surgery`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!40101 SET character_set_client = utf8 */;
CREATE TABLE `Pheo_Surgery` (
  `pheo_surgery_id` int(11) NOT NULL AUTO_INCREMENT,
  `ensat_id` int(11) NOT NULL,
  `center_id` varchar(5) NOT NULL,
  `surgery_date` date DEFAULT NULL,
  `surgery_procedure` varchar(30) DEFAULT NULL,
  `evidence_of_malignancy` varchar(30) DEFAULT NULL,
  `evidence_of_locoregional_spread` varchar(30) DEFAULT NULL,
  `comment` varchar(100) DEFAULT NULL,
  `proliferative_index` float DEFAULT NULL,
  `ki67_numbers` float DEFAULT NULL,
  `mitotic_count_numbers` float DEFAULT NULL,
  `cell_count_numbers` float DEFAULT NULL,
  `necrosis` varchar(30) DEFAULT NULL,
  `capsular_adipose_invasion` varchar(30) DEFAULT NULL,
  `vascular_invasion` varchar(30) DEFAULT NULL,
  `ingrowth_in_adjacent` varchar(30) DEFAULT NULL,
  PRIMARY KEY (`pheo_surgery_id`,`ensat_id`,`center_id`),
  KEY `ensat_id` (`ensat_id`,`center_id`)
) ENGINE=MyISAM AUTO_INCREMENT=217 DEFAULT CHARSET=latin1;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Table structure for table `Pheo_TumorDetails`
--

DROP TABLE IF EXISTS `Pheo_TumorDetails`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!40101 SET character_set_client = utf8 */;
CREATE TABLE `Pheo_TumorDetails` (
  `pheo_tumor_details_id` int(11) NOT NULL AUTO_INCREMENT,
  `ensat_id` int(11) NOT NULL,
  `center_id` varchar(5) NOT NULL,
  `tumor_date` date DEFAULT NULL,
  `largest_size_x` varchar(10) DEFAULT NULL,
  `largest_size_y` varchar(10) DEFAULT NULL,
  `largest_size_z` varchar(10) DEFAULT NULL,
  `tumor_resected` varchar(30) DEFAULT NULL,
  `tumor_a_or_e` varchar(30) DEFAULT NULL,
  `tumor_site` varchar(30) DEFAULT NULL,
  `multiple_primaries` varchar(4) DEFAULT NULL,
  `tumor_distant_metastases` varchar(4) DEFAULT NULL,
  `metastases_location` varchar(30) DEFAULT NULL,
  `diagnosis_method` varchar(30) DEFAULT NULL,
  PRIMARY KEY (`pheo_tumor_details_id`,`ensat_id`,`center_id`),
  KEY `ensat_id` (`ensat_id`,`center_id`)
) ENGINE=MyISAM AUTO_INCREMENT=214 DEFAULT CHARSET=latin1;
/*!40101 SET character_set_client = @saved_cs_client */;
/*!40103 SET TIME_ZONE=@OLD_TIME_ZONE */;

/*!40101 SET SQL_MODE=@OLD_SQL_MODE */;
/*!40014 SET FOREIGN_KEY_CHECKS=@OLD_FOREIGN_KEY_CHECKS */;
/*!40014 SET UNIQUE_CHECKS=@OLD_UNIQUE_CHECKS */;
/*!40101 SET CHARACTER_SET_CLIENT=@OLD_CHARACTER_SET_CLIENT */;
/*!40101 SET CHARACTER_SET_RESULTS=@OLD_CHARACTER_SET_RESULTS */;
/*!40101 SET COLLATION_CONNECTION=@OLD_COLLATION_CONNECTION */;
/*!40111 SET SQL_NOTES=@OLD_SQL_NOTES */;

-- Dump completed on 2012-10-16  5:15:16
