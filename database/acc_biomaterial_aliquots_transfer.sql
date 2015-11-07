CREATE DATABASE  IF NOT EXISTS `ensat_v3` /*!40100 DEFAULT CHARACTER SET utf8 */;
USE `ensat_v3`;
-- MySQL dump 10.13  Distrib 5.6.24, for Win64 (x86_64)
--
-- Host: 127.0.0.1    Database: ensat_v3
-- ------------------------------------------------------
-- Server version	5.6.26-log

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
-- Table structure for table `acc_biomaterial_aliquots_transfer`
--

DROP TABLE IF EXISTS `acc_biomaterial_aliquots_transfer`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!40101 SET character_set_client = utf8 */;
CREATE TABLE `acc_biomaterial_aliquots_transfer` (
  `acc_biomaterial_transfer_id` int(11) NOT NULL AUTO_INCREMENT,
  `acc_biomaterial_id` int(11) NOT NULL,
  `ensat_id` int(11) NOT NULL,
  `center_id` varchar(100) NOT NULL,
  `acc_biomaterial_location_id` int(11) NOT NULL,
  `acc_biomaterial_transfer_group_id` int(11) NOT NULL,
  `destination_center_id` varchar(100) NOT NULL,
  `transfered_date` datetime DEFAULT NULL,
  `status` varchar(45) NOT NULL,
  PRIMARY KEY (`acc_biomaterial_transfer_id`)
) ENGINE=InnoDB AUTO_INCREMENT=77 DEFAULT CHARSET=utf8;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `acc_biomaterial_aliquots_transfer`
--

LOCK TABLES `acc_biomaterial_aliquots_transfer` WRITE;
/*!40000 ALTER TABLE `acc_biomaterial_aliquots_transfer` DISABLE KEYS */;
INSERT INTO `acc_biomaterial_aliquots_transfer` VALUES (73,5,715,'GYMU',10,27,'AUME','2015-11-03 00:00:00','DAMAGED'),(74,16,623,'GYMU',16,27,'AUME','2015-11-03 00:00:00','RECEIVED'),(75,682,312,'GYMU',18,27,'AUME','2015-11-03 00:00:00','DAMAGED'),(76,1,783,'GYMU',1,27,'AUME','2015-11-03 00:00:00','RECEIVED');
/*!40000 ALTER TABLE `acc_biomaterial_aliquots_transfer` ENABLE KEYS */;
UNLOCK TABLES;
/*!40103 SET TIME_ZONE=@OLD_TIME_ZONE */;

/*!40101 SET SQL_MODE=@OLD_SQL_MODE */;
/*!40014 SET FOREIGN_KEY_CHECKS=@OLD_FOREIGN_KEY_CHECKS */;
/*!40014 SET UNIQUE_CHECKS=@OLD_UNIQUE_CHECKS */;
/*!40101 SET CHARACTER_SET_CLIENT=@OLD_CHARACTER_SET_CLIENT */;
/*!40101 SET CHARACTER_SET_RESULTS=@OLD_CHARACTER_SET_RESULTS */;
/*!40101 SET COLLATION_CONNECTION=@OLD_COLLATION_CONNECTION */;
/*!40111 SET SQL_NOTES=@OLD_SQL_NOTES */;

-- Dump completed on 2015-11-04  9:43:46
