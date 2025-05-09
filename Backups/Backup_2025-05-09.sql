-- MySQL dump 10.13  Distrib 8.0.41, for Win64 (x86_64)
--
-- Host: localhost    Database: hotelmanagementsystem
-- ------------------------------------------------------
-- Server version	8.0.41

/*!40101 SET @OLD_CHARACTER_SET_CLIENT=@@CHARACTER_SET_CLIENT */;
/*!40101 SET @OLD_CHARACTER_SET_RESULTS=@@CHARACTER_SET_RESULTS */;
/*!40101 SET @OLD_COLLATION_CONNECTION=@@COLLATION_CONNECTION */;
/*!50503 SET NAMES utf8mb4 */;
/*!40103 SET @OLD_TIME_ZONE=@@TIME_ZONE */;
/*!40103 SET TIME_ZONE='+00:00' */;
/*!40014 SET @OLD_UNIQUE_CHECKS=@@UNIQUE_CHECKS, UNIQUE_CHECKS=0 */;
/*!40014 SET @OLD_FOREIGN_KEY_CHECKS=@@FOREIGN_KEY_CHECKS, FOREIGN_KEY_CHECKS=0 */;
/*!40101 SET @OLD_SQL_MODE=@@SQL_MODE, SQL_MODE='NO_AUTO_VALUE_ON_ZERO' */;
/*!40111 SET @OLD_SQL_NOTES=@@SQL_NOTES, SQL_NOTES=0 */;

--
-- Table structure for table `activitylog`
--

DROP TABLE IF EXISTS `activitylog`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `activitylog` (
  `id` int NOT NULL AUTO_INCREMENT,
  `nic` varchar(12) NOT NULL,
  `activity` varchar(255) NOT NULL,
  `result` varchar(255) DEFAULT NULL,
  `activity_time` timestamp NULL DEFAULT CURRENT_TIMESTAMP,
  `name` varchar(100) DEFAULT NULL,
  `jobrole` varchar(50) DEFAULT NULL,
  PRIMARY KEY (`id`)
) ENGINE=InnoDB AUTO_INCREMENT=184 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `activitylog`
--

LOCK TABLES `activitylog` WRITE;
/*!40000 ALTER TABLE `activitylog` DISABLE KEYS */;
INSERT INTO `activitylog` VALUES (1,'200229004669','Login','Success','2025-05-08 17:29:28','modiwixrezrcre','Admin'),(2,'200229004669','Logged out','Success','2025-05-08 17:30:43','modiwixrezrcre','Admin'),(3,'200229004669','Login','Success','2025-05-08 17:37:02','modiwixrezrcre','Admin'),(4,'200229004669','Auto-backup database','Failed','2025-05-08 17:37:37','modiwixrezrcre','Admin'),(5,'200229004669','Auto-backup database','Failed','2025-05-08 17:38:06','modiwixrezrcre','Admin'),(6,'200229004669','Auto-backup database','Failed','2025-05-08 17:38:36','modiwixrezrcre','Admin'),(7,'200229004669','Logged out','Success','2025-05-08 17:38:51','modiwixrezrcre','Admin'),(8,'200229004669','Login','Success','2025-05-08 18:00:40','modiwixrezrcre','Admin'),(9,'200229004669','Auto-backup database','Success','2025-05-08 18:01:14','modiwixrezrcre','Admin'),(10,'200229004669','Attempted to backup database','Success','2025-05-08 18:01:27','modiwixrezrcre','Admin'),(11,'200229004669','Logged out','Success','2025-05-08 18:01:31','modiwixrezrcre','Admin'),(12,'200229004669','Login','Success','2025-05-08 18:11:52','modiwixrezrcre','Admin'),(13,'200229004669','Auto-backup database','Success','2025-05-08 18:12:26','modiwixrezrcre','Admin'),(14,'200229004669','Attempted to add new employee: xfefr (200229005467)','Success','2025-05-08 18:12:52','modiwixrezrcre','Admin'),(15,'200229004669','Auto-backup database','Success','2025-05-08 18:12:56','modiwixrezrcre','Admin'),(16,'200229004669','Auto-backup database','Success','2025-05-08 18:13:26','modiwixrezrcre','Admin'),(17,'200229004669','Auto-backup database','Success','2025-05-08 18:13:56','modiwixrezrcre','Admin'),(18,'200229004669','Auto-backup database','Success','2025-05-08 18:14:26','modiwixrezrcre','Admin'),(19,'200229004669','Attempted to add or update customer moirmxpmxoijr( 200229009086)','Failed','2025-05-08 18:14:45','modiwixrezrcre','Admin'),(20,'200229004669','Auto-backup database','Success','2025-05-08 18:14:56','modiwixrezrcre','Admin'),(21,'200229004669','Auto-backup database','Success','2025-05-08 18:15:26','modiwixrezrcre','Admin'),(22,'200229004669','Auto-backup database','Success','2025-05-08 18:15:56','modiwixrezrcre','Admin'),(23,'200229004669','Auto-backup database','Success','2025-05-08 18:16:26','modiwixrezrcre','Admin'),(24,'200229004669','Login','Success','2025-05-08 18:29:19','modiwixrezrcre','Admin'),(25,'200229004669','Auto-backup database','Success','2025-05-08 18:29:53','modiwixrezrcre','Admin'),(26,'200229004669','Auto-backup database','Success','2025-05-08 18:30:23','modiwixrezrcre','Admin'),(27,'200229004669','Auto-backup database','Success','2025-05-08 18:30:53','modiwixrezrcre','Admin'),(28,'200229004669','Suggested price for room 001','Success','2025-05-08 18:30:55','modiwixrezrcre','Admin'),(29,'200229004669','Auto-backup database','Success','2025-05-08 18:31:23','modiwixrezrcre','Admin'),(30,'200229004669','Attempted to add room ( Room no: 021)','Success','2025-05-08 18:31:50','modiwixrezrcre','Admin'),(31,'200229004669','Login','Success','2025-05-08 19:01:34','modiwixrezrcre','Admin'),(32,'200229004669','Auto-backup database','Success','2025-05-08 19:02:08','modiwixrezrcre','Admin'),(33,'200229004669','Attempted to search room ( Room no: 018)','Success','2025-05-08 19:02:29','modiwixrezrcre','Admin'),(34,'200229004669','Auto-backup database','Success','2025-05-08 19:02:38','modiwixrezrcre','Admin'),(35,'200229004669','Auto-backup database','Success','2025-05-08 19:03:08','modiwixrezrcre','Admin'),(36,'200229004669','Auto-backup database','Success','2025-05-08 19:03:38','modiwixrezrcre','Admin'),(37,'200229004669','Attempted to add customer fcrfmofiec( 200229004563)','Success','2025-05-08 19:03:53','modiwixrezrcre','Admin'),(38,'200229004669','Auto-backup database','Success','2025-05-08 19:04:08','modiwixrezrcre','Admin'),(39,'200229004669','Auto-backup database','Success','2025-05-08 19:04:38','modiwixrezrcre','Admin'),(40,'200229004669','Attempted to genarate customer report','Success','2025-05-08 19:05:02','modiwixrezrcre','Admin'),(41,'200229004669','Auto-backup database','Success','2025-05-08 19:05:08','modiwixrezrcre','Admin'),(42,'200229004669','Attempted to generate order details report','Success','2025-05-08 19:05:17','modiwixrezrcre','Admin'),(43,'200229004669','Attempted to generate inventory report','Success','2025-05-08 19:05:30','modiwixrezrcre','Admin'),(44,'200229004669','Auto-backup database','Success','2025-05-08 19:05:38','modiwixrezrcre','Admin'),(45,'200229004669','Auto-backup database','Success','2025-05-08 19:06:08','modiwixrezrcre','Admin'),(46,'200229004669','Auto-backup database','Success','2025-05-08 19:06:38','modiwixrezrcre','Admin'),(47,'200229004669','Auto-backup database','Success','2025-05-08 19:07:08','modiwixrezrcre','Admin'),(48,'200229004669','Auto-backup database','Success','2025-05-08 19:07:38','modiwixrezrcre','Admin'),(49,'200229004669','Auto-backup database','Success','2025-05-08 19:08:08','modiwixrezrcre','Admin'),(50,'200229004669','Auto-backup database','Success','2025-05-08 19:08:38','modiwixrezrcre','Admin'),(51,'200229004669','Auto-backup database','Success','2025-05-08 19:09:08','modiwixrezrcre','Admin'),(52,'200229004669','Auto-backup database','Success','2025-05-08 19:09:38','modiwixrezrcre','Admin'),(53,'200229004669','Auto-backup database','Success','2025-05-08 19:10:08','modiwixrezrcre','Admin'),(54,'200229004669','Attempted to generate inventory report','Success','2025-05-08 19:10:31','modiwixrezrcre','Admin'),(55,'200229004669','Auto-backup database','Success','2025-05-08 19:10:38','modiwixrezrcre','Admin'),(56,'200229004669','Auto-backup database','Success','2025-05-08 19:11:08','modiwixrezrcre','Admin'),(57,'200229004669','Auto-backup database','Success','2025-05-08 19:11:38','modiwixrezrcre','Admin'),(58,'200229004669','Auto-backup database','Success','2025-05-08 19:12:08','modiwixrezrcre','Admin'),(59,'200229004669','Auto-backup database','Success','2025-05-08 19:12:39','modiwixrezrcre','Admin'),(60,'200229004669','Auto-backup database','Success','2025-05-08 19:13:08','modiwixrezrcre','Admin'),(61,'200229004669','Auto-backup database','Success','2025-05-08 19:13:38','modiwixrezrcre','Admin'),(62,'200229004669','Auto-backup database','Success','2025-05-08 19:14:08','modiwixrezrcre','Admin'),(63,'200229004669','Auto-backup database','Success','2025-05-08 19:14:38','modiwixrezrcre','Admin'),(64,'200229004669','Auto-backup database','Success','2025-05-08 19:15:08','modiwixrezrcre','Admin'),(65,'200229004669','Auto-backup database','Success','2025-05-08 19:15:38','modiwixrezrcre','Admin'),(66,'200229004669','Auto-backup database','Success','2025-05-08 19:16:09','modiwixrezrcre','Admin'),(67,'200229004669','Auto-backup database','Success','2025-05-08 19:16:39','modiwixrezrcre','Admin'),(68,'200229004669','Auto-backup database','Success','2025-05-08 19:17:08','modiwixrezrcre','Admin'),(69,'200229004669','Logged out','Success','2025-05-08 19:17:19','modiwixrezrcre','Admin'),(70,'200229004669','Login','Success','2025-05-08 19:53:53','modiwixrezrcre','Admin'),(71,'200229004669','Auto-backup database','Success','2025-05-08 19:54:27','modiwixrezrcre','Admin'),(72,'200229004669','Attempted to backup database','Success','2025-05-08 19:54:35','modiwixrezrcre','Admin'),(73,'200229004669','Attempted to generate activity log report','Success','2025-05-08 19:54:57','modiwixrezrcre','Admin'),(74,'200229004669','Auto-backup database','Success','2025-05-08 19:54:58','modiwixrezrcre','Admin'),(75,'200229004669','Auto-backup database','Success','2025-05-08 19:55:27','modiwixrezrcre','Admin'),(76,'200229004669','Attempted to order item from inventory','Success','2025-05-08 19:55:34','modiwixrezrcre','Admin'),(77,'200229004669','Attempted to generate order details report','Success','2025-05-08 19:55:38','modiwixrezrcre','Admin'),(78,'200229004669','Auto-backup database','Success','2025-05-08 19:55:57','modiwixrezrcre','Admin'),(79,'200229004669','Auto-backup database','Success','2025-05-08 19:56:27','modiwixrezrcre','Admin'),(80,'200229004669','Attempted to add checkin: fcrfmofiec ( Customer ID:1)','Success','2025-05-08 19:56:55','modiwixrezrcre','Admin'),(81,'200229004669','Auto-backup database','Success','2025-05-08 19:56:57','modiwixrezrcre','Admin'),(82,'200229004669','Attempted to generate checkin report','Success','2025-05-08 19:57:12','modiwixrezrcre','Admin'),(83,'200229004669','Auto-backup database','Success','2025-05-08 19:57:27','modiwixrezrcre','Admin'),(84,'200229004669','Attempted to add checkout:( Customer ID:1)','Success','2025-05-08 19:57:32','modiwixrezrcre','Admin'),(85,'200229004669','Attempted to generate checkout report','Success','2025-05-08 19:57:35','modiwixrezrcre','Admin'),(86,'200229004669','Auto-backup database','Success','2025-05-08 19:57:57','modiwixrezrcre','Admin'),(87,'200229004669','Attempted to order item using batch form from inventory','Success','2025-05-08 19:58:22','modiwixrezrcre','Admin'),(88,'200229004669','Auto-backup database','Success','2025-05-08 19:58:27','modiwixrezrcre','Admin'),(89,'200229004669','Attempted to generate order details report','Success','2025-05-08 19:58:30','modiwixrezrcre','Admin'),(90,'200229004669','Auto-backup database','Success','2025-05-08 19:58:57','modiwixrezrcre','Admin'),(91,'200229004669','Auto-backup database','Success','2025-05-08 19:59:27','modiwixrezrcre','Admin'),(92,'200229004669','Attempted to add new item through batch insert.','Failed','2025-05-08 19:59:37','modiwixrezrcre','Admin'),(93,'200229004669','Auto-backup database','Success','2025-05-08 19:59:57','modiwixrezrcre','Admin'),(94,'200229004669','Auto-backup database','Success','2025-05-08 20:00:27','modiwixrezrcre','Admin'),(95,'200229004669','Login','Success','2025-05-08 20:00:36','modiwixrezrcre','Admin'),(96,'200229004669','Attempted to add new item through batch insert.','Success','2025-05-08 20:00:53','modiwixrezrcre','Admin'),(97,'200229004669','Auto-backup database','Success','2025-05-08 20:00:57','modiwixrezrcre','Admin'),(98,'200229004669','Auto-backup database','Success','2025-05-08 20:01:11','modiwixrezrcre','Admin'),(99,'200229004669','Attempted to delete item: T56 (Accessories)','Success','2025-05-08 20:01:17','modiwixrezrcre','Admin'),(100,'200229004669','Auto-backup database','Success','2025-05-08 20:01:27','modiwixrezrcre','Admin'),(101,'200229004669','Auto-backup database','Success','2025-05-08 20:01:41','modiwixrezrcre','Admin'),(102,'200229004669','Auto-backup database','Success','2025-05-08 20:01:57','modiwixrezrcre','Admin'),(103,'200229004669','Auto-backup database','Success','2025-05-08 20:02:11','modiwixrezrcre','Admin'),(104,'200229004669','Auto-backup database','Success','2025-05-08 20:02:27','modiwixrezrcre','Admin'),(105,'200229004669','Auto-backup database','Success','2025-05-08 20:02:41','modiwixrezrcre','Admin'),(106,'200229004669','Auto-backup database','Success','2025-05-08 20:02:57','modiwixrezrcre','Admin'),(107,'200229004669','Attempted to generate inventory report','Success','2025-05-08 20:03:04','modiwixrezrcre','Admin'),(108,'200229004669','Auto-backup database','Success','2025-05-08 20:03:11','modiwixrezrcre','Admin'),(109,'200229004669','Auto-backup database','Success','2025-05-08 20:03:27','modiwixrezrcre','Admin'),(110,'200229004669','Attempted to search room type','Success','2025-05-08 20:03:39','modiwixrezrcre','Admin'),(111,'200229004669','Attempted to search room type','Success','2025-05-08 20:03:39','modiwixrezrcre','Admin'),(112,'200229004669','Auto-backup database','Success','2025-05-08 20:03:41','modiwixrezrcre','Admin'),(113,'200229004669','Attempted to search room type','Success','2025-05-08 20:03:56','modiwixrezrcre','Admin'),(114,'200229004669','Attempted to search room type','Success','2025-05-08 20:03:57','modiwixrezrcre','Admin'),(115,'200229004669','Attempted to search room type','Success','2025-05-08 20:03:57','modiwixrezrcre','Admin'),(116,'200229004669','Auto-backup database','Success','2025-05-08 20:03:57','modiwixrezrcre','Admin'),(117,'200229004669','Attempted to search room type','Success','2025-05-08 20:03:57','modiwixrezrcre','Admin'),(118,'200229004669','Attempted to search room type','Success','2025-05-08 20:03:58','modiwixrezrcre','Admin'),(119,'200229004669','Attempted to search room type','Success','2025-05-08 20:04:03','modiwixrezrcre','Admin'),(120,'200229004669','Auto-backup database','Success','2025-05-08 20:04:11','modiwixrezrcre','Admin'),(121,'200229004669','Auto-backup database','Success','2025-05-08 20:04:27','modiwixrezrcre','Admin'),(122,'200229004669','Auto-backup database','Success','2025-05-08 20:04:41','modiwixrezrcre','Admin'),(123,'200229004669','Attempted to delete room ( Room no: 021)','Success','2025-05-08 20:04:41','modiwixrezrcre','Admin'),(124,'200229004669','Attempted to generate Room availability report','Success','2025-05-08 20:04:48','modiwixrezrcre','Admin'),(125,'200229004669','Auto-backup database','Success','2025-05-08 20:04:57','modiwixrezrcre','Admin'),(126,'200229004669','Auto-backup database','Success','2025-05-08 20:05:11','modiwixrezrcre','Admin'),(127,'200229004669','Auto-backup database','Success','2025-05-08 20:05:27','modiwixrezrcre','Admin'),(128,'200229004669','Auto-backup database','Success','2025-05-08 20:05:41','modiwixrezrcre','Admin'),(129,'200229004669','Attempted to add customer iumhochricue( 200229032149)','Success','2025-05-08 20:05:48','modiwixrezrcre','Admin'),(130,'200229004669','Auto-backup database','Success','2025-05-08 20:05:57','modiwixrezrcre','Admin'),(131,'200229004669','Attempted to genarate customer report','Success','2025-05-08 20:05:59','modiwixrezrcre','Admin'),(132,'200229004669','Auto-backup database','Success','2025-05-08 20:06:11','modiwixrezrcre','Admin'),(133,'200229004669','Auto-backup database','Success','2025-05-08 20:06:27','modiwixrezrcre','Admin'),(134,'200229004669','Attempted to add room ( Room no: 021)','Success','2025-05-08 20:06:30','modiwixrezrcre','Admin'),(135,'200229004669','Auto-backup database','Success','2025-05-08 20:06:41','modiwixrezrcre','Admin'),(136,'200229004669','Attempted to delete room ( Room no: 021)','Success','2025-05-08 20:06:45','modiwixrezrcre','Admin'),(137,'200229004669','Auto-backup database','Success','2025-05-08 20:06:57','modiwixrezrcre','Admin'),(138,'200229004669','Auto-backup database','Success','2025-05-08 20:07:11','modiwixrezrcre','Admin'),(139,'200229004669','Auto-backup database','Success','2025-05-08 20:07:27','modiwixrezrcre','Admin'),(140,'200229004669','Attempted to add event ( Event no: 1)','Failed','2025-05-08 20:07:40','modiwixrezrcre','Admin'),(141,'200229004669','Auto-backup database','Success','2025-05-08 20:07:41','modiwixrezrcre','Admin'),(142,'200229004669','Attempted to add event ( Event no: 1)','Success','2025-05-08 20:07:45','modiwixrezrcre','Admin'),(143,'200229004669','Auto-backup database','Success','2025-05-08 20:07:57','modiwixrezrcre','Admin'),(144,'200229004669','Auto-backup database','Success','2025-05-08 20:08:11','modiwixrezrcre','Admin'),(145,'200229004669','Attempted to genarate event report','Success','2025-05-08 20:08:14','modiwixrezrcre','Admin'),(146,'200229004669','Auto-backup database','Success','2025-05-08 20:08:27','modiwixrezrcre','Admin'),(147,'200229004669','Auto-backup database','Success','2025-05-08 20:08:41','modiwixrezrcre','Admin'),(148,'200229004669','Auto-backup database','Success','2025-05-08 20:08:57','modiwixrezrcre','Admin'),(149,'200229004669','Attempted to add new employee: meifcoxjirexfe (200229678969)','Success','2025-05-08 20:09:02','modiwixrezrcre','Admin'),(150,'200229004669','Auto-backup database','Success','2025-05-08 20:09:11','modiwixrezrcre','Admin'),(151,'200229004669','Attempted to generate employee report','Success','2025-05-08 20:09:19','modiwixrezrcre','Admin'),(152,'200229004669','Auto-backup database','Success','2025-05-08 20:09:27','modiwixrezrcre','Admin'),(153,'200229004669','Auto-backup database','Success','2025-05-08 20:09:41','modiwixrezrcre','Admin'),(154,'200229004669','Attempted to search employee ( NIC: 200229678969)','Success','2025-05-08 20:09:42','modiwixrezrcre','Admin'),(155,'200229004669','Attempt to update employee: meifcoxjirexfe (200229678969)','Success','2025-05-08 20:09:56','modiwixrezrcre','Admin'),(156,'200229004669','Auto-backup database','Success','2025-05-08 20:09:57','modiwixrezrcre','Admin'),(157,'200229004669','Attempted to search employee ( NIC: 200229678969)','Success','2025-05-08 20:10:05','modiwixrezrcre','Admin'),(158,'200229004669','Auto-backup database','Success','2025-05-08 20:10:11','modiwixrezrcre','Admin'),(159,'200229004669','Attempt to delete employee: meifcoxjirexfe (200229678969)','Success','2025-05-08 20:10:12','modiwixrezrcre','Admin'),(160,'200229004669','Auto-backup database','Success','2025-05-08 20:10:27','modiwixrezrcre','Admin'),(161,'200229004669','Auto-backup database','Success','2025-05-08 20:10:41','modiwixrezrcre','Admin'),(162,'200229004669','Auto-backup database','Success','2025-05-08 20:10:57','modiwixrezrcre','Admin'),(163,'200229004669','Auto-backup database','Success','2025-05-08 20:11:11','modiwixrezrcre','Admin'),(164,'200229004669','Logged out','Success','2025-05-08 20:11:26','modiwixrezrcre','Admin'),(165,'200229004669','Auto-backup database','Success','2025-05-08 20:11:27','modiwixrezrcre','Admin'),(166,'200229004669','Auto-backup database','Success','2025-05-08 20:11:57','modiwixrezrcre','Admin'),(167,'200229004669','Auto-backup database','Success','2025-05-08 20:12:27','modiwixrezrcre','Admin'),(168,'200229004669','Auto-backup database','Success','2025-05-08 20:12:57','modiwixrezrcre','Admin'),(169,'200229004669','Auto-backup database','Success','2025-05-08 20:13:27','modiwixrezrcre','Admin'),(170,'200229004669','Auto-backup database','Success','2025-05-08 20:13:57','modiwixrezrcre','Admin'),(171,'200229004669','Auto-backup database','Success','2025-05-08 20:14:27','modiwixrezrcre','Admin'),(172,'200229004669','Auto-backup database','Success','2025-05-08 20:14:57','modiwixrezrcre','Admin'),(173,'200229004669','Auto-backup database','Success','2025-05-08 20:15:27','modiwixrezrcre','Admin'),(174,'200229004669','Auto-backup database','Success','2025-05-08 20:15:57','modiwixrezrcre','Admin'),(175,'200229004669','Auto-backup database','Success','2025-05-08 20:16:27','modiwixrezrcre','Admin'),(176,'200229004669','Auto-backup database','Success','2025-05-08 20:16:57','modiwixrezrcre','Admin'),(177,'200229004669','Login','Success','2025-05-09 02:57:48','modiwixrezrcre','Admin'),(178,'200229004669','Attempted to generate inventory report','Success','2025-05-09 02:58:05','modiwixrezrcre','Admin'),(179,'200229004669','Auto-backup database','Success','2025-05-09 02:58:23','modiwixrezrcre','Admin'),(180,'200229004669','Auto-backup database','Success','2025-05-09 02:58:53','modiwixrezrcre','Admin'),(181,'200229004669','Auto-backup database','Success','2025-05-09 02:59:23','modiwixrezrcre','Admin'),(182,'200229004669','Auto-backup database','Success','2025-05-09 02:59:53','modiwixrezrcre','Admin'),(183,'200229004669','Auto-backup database','Success','2025-05-09 03:00:23','modiwixrezrcre','Admin');
/*!40000 ALTER TABLE `activitylog` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `checkin_details`
--

DROP TABLE IF EXISTS `checkin_details`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `checkin_details` (
  `id` int NOT NULL AUTO_INCREMENT,
  `customer_id` varchar(20) DEFAULT NULL,
  `room_no` varchar(20) DEFAULT NULL,
  `name` varchar(100) DEFAULT NULL,
  `checkin_time` datetime DEFAULT NULL,
  `price` varchar(20) DEFAULT NULL,
  `nights` varchar(10) DEFAULT NULL,
  `total` varchar(20) DEFAULT NULL,
  `paid_amount` varchar(20) DEFAULT NULL,
  `pending_amount` varchar(20) DEFAULT NULL,
  PRIMARY KEY (`id`)
) ENGINE=InnoDB AUTO_INCREMENT=2 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `checkin_details`
--

LOCK TABLES `checkin_details` WRITE;
/*!40000 ALTER TABLE `checkin_details` DISABLE KEYS */;
/*!40000 ALTER TABLE `checkin_details` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `checkout`
--

DROP TABLE IF EXISTS `checkout`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `checkout` (
  `id` int NOT NULL AUTO_INCREMENT,
  `cus_id` varchar(20) DEFAULT NULL,
  `room_no` varchar(20) DEFAULT NULL,
  `price` varchar(20) DEFAULT NULL,
  `pending` varchar(100) DEFAULT NULL,
  `paid` varchar(10) DEFAULT NULL,
  `checkin_time` datetime DEFAULT NULL,
  `checkout_time` datetime DEFAULT NULL,
  PRIMARY KEY (`id`)
) ENGINE=InnoDB AUTO_INCREMENT=2 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `checkout`
--

LOCK TABLES `checkout` WRITE;
/*!40000 ALTER TABLE `checkout` DISABLE KEYS */;
INSERT INTO `checkout` VALUES (1,'1','11','50000.0','49000','Yes','2025-05-09 01:25:47','2025-05-09 01:27:30');
/*!40000 ALTER TABLE `checkout` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `customers`
--

DROP TABLE IF EXISTS `customers`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `customers` (
  `customer_id` int NOT NULL AUTO_INCREMENT,
  `name` varchar(255) NOT NULL,
  `phone` varchar(20) NOT NULL,
  `NIC` varchar(20) NOT NULL,
  `room_type` varchar(15) DEFAULT NULL,
  `room_number` varchar(10) NOT NULL,
  `room_price` varchar(15) NOT NULL,
  PRIMARY KEY (`customer_id`),
  UNIQUE KEY `NIC` (`NIC`)
) ENGINE=InnoDB AUTO_INCREMENT=3 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `customers`
--

LOCK TABLES `customers` WRITE;
/*!40000 ALTER TABLE `customers` DISABLE KEYS */;
INSERT INTO `customers` VALUES (1,'fcrfmofiec','0741040292','200229004563','Single Bed','011','25000'),(2,'iumhochricue','0741040292','200229032149','Single Bed','017','25000');
/*!40000 ALTER TABLE `customers` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `employee`
--

DROP TABLE IF EXISTS `employee`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `employee` (
  `nic` varchar(12) NOT NULL,
  `name` varchar(100) DEFAULT NULL,
  `age` varchar(10) DEFAULT NULL,
  `gender` varchar(10) DEFAULT NULL,
  `job` varchar(50) DEFAULT NULL,
  `address` varchar(500) DEFAULT NULL,
  `phone` varchar(15) DEFAULT NULL,
  `email` varchar(50) DEFAULT NULL,
  `password` varchar(255) NOT NULL,
  `datejoined` varchar(20) DEFAULT NULL,
  `salary` int DEFAULT NULL,
  PRIMARY KEY (`nic`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `employee`
--

LOCK TABLES `employee` WRITE;
/*!40000 ALTER TABLE `employee` DISABLE KEYS */;
INSERT INTO `employee` VALUES ('200019201672','Rithu Balasooriya','33','Female','Chef/Cook','23 Gangaramaya','0783450983','Rithu@Gmail.com','-','2025-05-02',65000),('200129301829','Rajesh Rahul','43','Male','Waiter/Waitress','54 Kilinochchi','0781028392','Raju@Gmail.com','-','2025-05-02',45000),('200200000000','Ashfaq Rifath','25','Male','Receptionist','34 Dehiwala','0781028303','Rifa@Gmail.com','123456','2025-05-01',60000),('200229004669','modiwixrezrcre','12','Male','Admin','mxidmoizmpoeimrzc','0741040292','ashfaaq.rifath2@gmail.com','123456','2025-05-08',200000),('200229005467','xfefr','23','Male','Waiter/Waitress','ferxrvrvtrcvy','0741040292','ashfaaq@gmail.com','-','2025-05-08',45000),('200320192093','Tharidu Mahabage','22','Male','Waiter/Waitress','43 Panadura','0719202830','Tharidu@Gmail.com','-','2025-05-01',45000),('200400800675','Kaveesha Dilanjan','21','Male','Admin','59/3 Moratuwa','0719322884','Kdilanjan@Gmail.com','123456','2025-05-01',200000),('200510293012','Oveen Bandara','21','Male','Waiter/Waitress','34 Linton state','0781209223','Oveen@Gmail.com','-','2025-05-02',45000),('200610298351','Dinusha Dewinda','21','Male','Maintenance Worker','54 Sranankara','0761092016','Dewinda@Gmail.com','-','2025-05-02',55000),('200610920196','Nethul Nimsara','23','Male','Waiter/Waitress','23 Gamunu Sreet','0781092345','Nethul@Gmail.com','-','2025-05-02',45000),('751029402V','Kasuni Kalhari','65','Female','Cleaning Staff','54 Lunawa','0781029301','kasu@Gmail.com','-','2025-05-04',40000),('752901927V','Gunapara Fernando','66','Female','Maintenance Worker','34 Kalutara','0781092639','Gune@Gmail.com','-','2025-05-02',55000),('760293871V','Kingsley Peiris','65','Female','Security Officer','65 Moratuwa','0781029384','CP@Gmail.com','-','2025-05-01',50000);
/*!40000 ALTER TABLE `employee` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `event`
--

DROP TABLE IF EXISTS `event`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `event` (
  `eventnumber` int NOT NULL,
  `description` varchar(50) DEFAULT NULL,
  `date` varchar(20) DEFAULT NULL,
  `time` varchar(20) DEFAULT NULL,
  `duration` varchar(20) DEFAULT NULL,
  PRIMARY KEY (`eventnumber`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `event`
--

LOCK TABLES `event` WRITE;
/*!40000 ALTER TABLE `event` DISABLE KEYS */;
INSERT INTO `event` VALUES (1,'Party','2025-05-09','06:00 AM','3 hr 00 min');
/*!40000 ALTER TABLE `event` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `inventory`
--

DROP TABLE IF EXISTS `inventory`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `inventory` (
  `item_id` int NOT NULL AUTO_INCREMENT,
  `item_name` varchar(255) NOT NULL,
  `category` varchar(100) DEFAULT NULL,
  `quantity` varchar(20) DEFAULT NULL,
  `price` decimal(10,2) NOT NULL,
  `supplier_name` varchar(20) NOT NULL,
  `supplier_phone` varchar(10) NOT NULL,
  `date_added` datetime DEFAULT CURRENT_TIMESTAMP,
  `orders` int DEFAULT '0',
  PRIMARY KEY (`item_id`)
) ENGINE=InnoDB AUTO_INCREMENT=47 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `inventory`
--

LOCK TABLES `inventory` WRITE;
/*!40000 ALTER TABLE `inventory` DISABLE KEYS */;
INSERT INTO `inventory` VALUES (5,'Rice','Food','194g',350.00,'Jonny','0743678292','2025-05-02 00:38:08',3),(6,'Light bulbs','Supplies','72',500.00,'Shaarl','0776354789','2025-05-02 00:38:08',4),(9,'Bottles','Accessories','314',100.00,'Rifath','0741040292','2025-05-02 00:38:08',3),(10,'Pumpkin','Food','292',500.00,'Saman','0741035692','2025-05-02 00:38:08',2),(15,'Titanium','Supplies','42',1500.00,'Stark','0789758292','2025-05-02 00:38:08',3),(17,'Potatoes','Food','411',150.00,'John','0746754292','2025-05-02 00:38:08',2),(18,'Salt','Food','180g',250.00,'Amaran','0741046789','2025-05-02 00:38:08',1),(20,'Chicken','Food','80g',890.00,'Thirandu','0741040897','2025-05-02 00:38:08',0),(21,'Sugar','Food','24g',678.00,'John','0741040292','2025-05-02 00:38:08',4),(23,'Beef','Food','350g',1500.00,'jcmoimejpoice','0744567292','2025-05-02 19:30:22',0),(28,'Tomatoes','Foods','250g',240.00,'OilDepot','5557777777','2025-05-06 15:33:37',85),(29,'Chili Powder','Foods','95g',150.00,'SpiceCo','5555555555','2025-05-06 15:33:37',61),(30,'Garlic','Foods','145g',90.00,'HarvestHub','5554444444','2025-05-06 15:33:37',76),(34,'Onions','Foods','500g',120.00,'FarmFresh','5553333333','2025-05-09 01:30:51',100),(35,'Turmeric Powder','Foods','100g',160.00,'NatureSpice','5552222222','2025-05-09 01:30:51',50),(36,'Ginger','Foods','200g',110.00,'GreenLeaf','5551111111','2025-05-09 01:30:51',70),(37,'Cumin Seeds','Foods','100g',180.00,'SpiceWorld','5556666666','2025-05-09 01:30:51',40),(38,'Green Chilies','Foods','250g',130.00,'ChiliFarm','5558888888','2025-05-09 01:30:51',90),(39,'Potatoes','Foods','1kg',90.00,'RootHarvest','5559999999','2025-05-09 01:30:51',120),(40,'Carrots','Foods','500g',100.00,'FreshFields','5551212121','2025-05-09 01:30:51',80),(41,'Coriander Leaves','Foods','100g',40.00,'HerbHeaven','5553434343','2025-05-09 01:30:51',60),(42,'Mustard Seeds','Foods','200g',170.00,'SpiceCart','5557878787','2025-05-09 01:30:51',45),(44,'Beans','Foods','500g',95.00,'FarmDirect','5554747474','2025-05-09 01:30:51',95),(45,'Tamarind','Foods','250g',150.00,'TangyTreats','5552323232','2025-05-09 01:30:51',55),(46,'Black Pepper','Foods','100g',200.00,'PepperKing','5556767676','2025-05-09 01:30:51',35);
/*!40000 ALTER TABLE `inventory` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `inventory_orders`
--

DROP TABLE IF EXISTS `inventory_orders`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `inventory_orders` (
  `order_id` int NOT NULL AUTO_INCREMENT,
  `item_name` varchar(255) NOT NULL,
  `order_quantity` varchar(50) NOT NULL,
  `order_date` datetime DEFAULT CURRENT_TIMESTAMP,
  `cost` double DEFAULT NULL,
  PRIMARY KEY (`order_id`)
) ENGINE=InnoDB AUTO_INCREMENT=33 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `inventory_orders`
--

LOCK TABLES `inventory_orders` WRITE;
/*!40000 ALTER TABLE `inventory_orders` DISABLE KEYS */;
INSERT INTO `inventory_orders` VALUES (1,'Sugar','7','2025-05-03 22:37:31',NULL),(2,'Sugar','12g','2025-05-03 22:37:48',NULL),(3,'Beef','150g','2025-05-03 22:44:18',NULL),(4,'Guns','5','2025-05-04 12:11:35',NULL),(5,'Potatoes','35','2025-05-05 21:17:35',NULL),(6,'Sugar','5g','2025-05-06 10:00:42',3390),(7,'Titanium','3','2025-05-06 12:36:53',4500),(8,'Bottles','20','2025-05-06 12:47:00',2000),(9,'Potatoes','4','2025-05-06 12:48:55',600),(10,'Bottles','3','2025-05-06 12:54:18',300),(11,'Pumpkin','5','2025-05-06 12:57:30',2500),(12,'Light bulbs','10','2025-05-06 13:06:44',5000),(13,'Titanium','4','2025-05-06 13:07:43',6000),(14,'Light bulbs','9','2025-05-06 13:08:40',4500),(15,'Light bulbs','5','2025-05-06 13:12:30',2500),(16,'Light bulbs','4','2025-05-06 13:13:24',2000),(17,'Salt','320g','2025-05-06 13:16:52',80000),(18,'Titanium','1','2025-05-06 13:19:00',1500),(19,'Pumpkin','3','2025-05-06 13:20:37',1500),(20,'Sugar','10g','2025-05-06 17:24:09',6780),(21,'Rice','2g','2025-05-06 17:24:09',700),(22,'T56','2','2025-05-06 17:24:10',10000),(23,'T56','2','2025-05-06 17:25:35',10000),(24,'Chili Powder','5g','2025-05-07 10:12:42',750),(25,'Sugar','10g','2025-05-07 10:14:45',6780),(26,'Rice','2g','2025-05-07 10:14:45',700),(27,'T56','3','2025-05-07 10:14:45',15000),(28,'Garlic','5g','2025-05-07 10:28:38',450),(29,'Bottles','3','2025-05-09 01:25:29',300),(30,'Sugar','10g','2025-05-09 01:28:20',6780),(31,'Rice','2g','2025-05-09 01:28:20',700),(32,'T56','3','2025-05-09 01:28:20',15000);
/*!40000 ALTER TABLE `inventory_orders` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `room`
--

DROP TABLE IF EXISTS `room`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `room` (
  `roomnumber` varchar(10) DEFAULT NULL,
  `availability` varchar(20) DEFAULT NULL,
  `cleaning_status` varchar(20) DEFAULT NULL,
  `price` varchar(20) DEFAULT NULL,
  `bed_type` varchar(20) DEFAULT NULL
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `room`
--

LOCK TABLES `room` WRITE;
/*!40000 ALTER TABLE `room` DISABLE KEYS */;
INSERT INTO `room` VALUES ('001','Available','Cleaned','35000','Double Bed'),('002','Available','Cleaned','35000','Double Bed'),('003','Available','Cleaned','35000','Double Bed'),('004','Not yet','Dirty','35000','Double Bed'),('005','Not yet','Dirty','35000','Double Bed'),('006','Not yet','Cleaned','35000','Double Bed'),('007','Available','Cleaned','35000','Double Bed'),('008','Available','Cleaned','35000','Double Bed'),('009','Not yet','Cleaned','35000','Double Bed'),('010','Available','Cleaned','35000','Double Bed'),('011','Not yet','Cleaned','25000','Single Bed'),('012','Available','Cleaned','25000','Single Bed'),('013','Available','Cleaned','25000','Single Bed'),('014','Not yet','Dirty','25000','Single Bed'),('015','Not yet','Dirty','25000','Single Bed'),('016','Not yet','Cleaned','25000','Single Bed'),('017','Not yet','Cleaned','25000','Single Bed'),('018','Available','Cleaned','25000','Single Bed'),('019','Not yet','Cleaned','25000','Single Bed'),('020','Available','Cleaned','25000','Single Bed');
/*!40000 ALTER TABLE `room` ENABLE KEYS */;
UNLOCK TABLES;
/*!40103 SET TIME_ZONE=@OLD_TIME_ZONE */;

/*!40101 SET SQL_MODE=@OLD_SQL_MODE */;
/*!40014 SET FOREIGN_KEY_CHECKS=@OLD_FOREIGN_KEY_CHECKS */;
/*!40014 SET UNIQUE_CHECKS=@OLD_UNIQUE_CHECKS */;
/*!40101 SET CHARACTER_SET_CLIENT=@OLD_CHARACTER_SET_CLIENT */;
/*!40101 SET CHARACTER_SET_RESULTS=@OLD_CHARACTER_SET_RESULTS */;
/*!40101 SET COLLATION_CONNECTION=@OLD_COLLATION_CONNECTION */;
/*!40111 SET SQL_NOTES=@OLD_SQL_NOTES */;

-- Dump completed on 2025-05-09  8:30:53
