-- MySQL dump 10.13  Distrib 8.0.36, for Win64 (x86_64)
--
-- Host: localhost    Database: hotelmanagementsystem
-- ------------------------------------------------------
-- Server version	8.0.36

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
) ENGINE=InnoDB AUTO_INCREMENT=27 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `activitylog`
--

LOCK TABLES `activitylog` WRITE;
/*!40000 ALTER TABLE `activitylog` DISABLE KEYS */;
INSERT INTO `activitylog` VALUES (1,'200200000000','Login','Success','2025-05-08 14:49:15','Ashfaq Rifath','Receptionist'),(2,'200200000000','Attempted to add event ( Event no: 14)','Success','2025-05-08 14:50:51','Ashfaq Rifath','Receptionist'),(3,'200200000000','Attempted to search event ( Event no: 14)','Success','2025-05-08 14:51:37','Ashfaq Rifath','Receptionist'),(4,'200200000000','Attempted to update event ( Event no: 14)','Success','2025-05-08 14:51:51','Ashfaq Rifath','Receptionist'),(5,'200200000000','Attempted to search event ( Event no: 14)','Success','2025-05-08 14:51:54','Ashfaq Rifath','Receptionist'),(6,'200200000000','Attempted to search event ( Event no: 14)','Success','2025-05-08 14:52:01','Ashfaq Rifath','Receptionist'),(7,'200200000000','Attempted to search event ( Event no: 14)','Success','2025-05-08 14:52:03','Ashfaq Rifath','Receptionist'),(8,'200200000000','Attempted to search event ( Event no: )','Not Found','2025-05-08 14:52:15','Ashfaq Rifath','Receptionist'),(9,'200200000000','Logged out','Success','2025-05-08 14:52:41','Ashfaq Rifath','Receptionist'),(10,'200400800675','Login','Success','2025-05-08 14:52:56','Kaveesha Dilanjan','Admin'),(11,'200400800675','Attempted to backup database','Success','2025-05-08 14:54:01','Kaveesha Dilanjan','Admin'),(12,'200400800675','Logged out','Success','2025-05-08 14:54:49','Kaveesha Dilanjan','Admin'),(13,'200400800675','Login','Success','2025-05-08 15:09:17','Kaveesha Dilanjan','Admin'),(14,'200400800675','Attempted to add room ( Room no: 001)','Success','2025-05-08 15:10:14','Kaveesha Dilanjan','Admin'),(15,'200400800675','Attempted to generate employee report','Success','2025-05-08 15:13:20','Kaveesha Dilanjan','Admin'),(16,'200400800675','Logged out','Success','2025-05-08 15:18:56','Kaveesha Dilanjan','Admin'),(17,'200200000000','Login','Success','2025-05-08 15:37:38','Ashfaq Rifath','Receptionist'),(18,'200200000000','Attempted to generate Room availability report','Success','2025-05-08 15:38:30','Ashfaq Rifath','Receptionist'),(19,'200200000000','Suggested price for room 001','Success','2025-05-08 15:39:20','Ashfaq Rifath','Receptionist'),(20,'200200000000','Suggested price for room 020','Success','2025-05-08 15:39:42','Ashfaq Rifath','Receptionist'),(21,'200200000000','Logged out','Success','2025-05-08 15:39:57','Ashfaq Rifath','Receptionist'),(22,'200200000000','Login','Success','2025-05-08 15:47:15','Ashfaq Rifath','Receptionist'),(23,'200200000000','Attempted to search room ( Room no: 012)','Success','2025-05-08 15:48:13','Ashfaq Rifath','Receptionist'),(24,'200200000000','Suggested price for room 012','Success','2025-05-08 15:48:34','Ashfaq Rifath','Receptionist'),(25,'200200000000','Attempted to generate Room availability report','Success','2025-05-08 15:48:51','Ashfaq Rifath','Receptionist'),(26,'200200000000','Logged out','Success','2025-05-08 15:49:41','Ashfaq Rifath','Receptionist');
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
) ENGINE=InnoDB AUTO_INCREMENT=3 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `checkin_details`
--

LOCK TABLES `checkin_details` WRITE;
/*!40000 ALTER TABLE `checkin_details` DISABLE KEYS */;
INSERT INTO `checkin_details` VALUES (2,'8','6','Ravija','2025-05-07 13:17:16','89000','1','89000.0','50000','39000');
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
) ENGINE=InnoDB AUTO_INCREMENT=3 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `checkout`
--

LOCK TABLES `checkout` WRITE;
/*!40000 ALTER TABLE `checkout` DISABLE KEYS */;
INSERT INTO `checkout` VALUES (1,'1','1','20000','10000','Yes','2025-04-29 11:48:22','2025-04-29 11:48:48'),(2,'1','1','20000','8000','Yes','2025-05-01 14:25:14','2025-05-01 14:25:52');
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
) ENGINE=InnoDB AUTO_INCREMENT=22 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `customers`
--

LOCK TABLES `customers` WRITE;
/*!40000 ALTER TABLE `customers` DISABLE KEYS */;
INSERT INTO `customers` VALUES (6,'senuth','1231234568','980380937291','Double Bed','002','87000'),(8,'Ravija','0761029374','200301920374','Double Bed','006','89000');
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
INSERT INTO `employee` VALUES ('200019201672','Rithu Balasooriya','33','Female','Chef/Cook','23 Gangaramaya','0783450983','Rithu@Gmail.com','-','2025-05-02',65000),('200129301829','Rajesh Rahul','43','Male','Waiter/Waitress','54 Kilinochchi','0781028392','Raju@Gmail.com','-','2025-05-02',45000),('200200000000','Ashfaq Rifath','25','Male','Receptionist','34 Dehiwala','0781028303','Rifa@Gmail.com','123456','2025-05-01',60000),('200320192093','Tharidu Mahabage','22','Male','Waiter/Waitress','43 Panadura','0719202830','Tharidu@Gmail.com','-','2025-05-01',45000),('200400800675','Kaveesha Dilanjan','21','Male','Admin','59/3 Moratuwa','0719322884','Kdilanjan@Gmail.com','123456','2025-05-01',200000),('200510293012','Oveen Bandara','21','Male','Waiter/Waitress','34 Linton state','0781209223','Oveen@Gmail.com','-','2025-05-02',45000),('200610920196','Nethul Nimsara','23','Male','Waiter/Waitress','23 Gamunu Sreet','0781092345','Nethul@Gmail.com','-','2025-05-02',45000),('751029402V','Kasuni Kalhari','65','Female','Cleaning Staff','54 Lunawa','0781029301','kasu@Gmail.com','-','2025-05-04',40000),('752901927V','Gunapara Fernando','66','Female','Maintenance Worker','34 Kalutara','0781092639','Gune@Gmail.com','-','2025-05-02',55000),('760293871V','Kingsley Peiris','65','Female','Security Officer','65 Moratuwa','0781029384','CP@Gmail.com','-','2025-05-01',50000);
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
INSERT INTO `event` VALUES (1,'ttt','2025-04-30','12:05 PM','2 hour'),(2,'yyyyffff','2025-04-26','12:00 AM','3 hour'),(3,'des','2025-07-10','12:00 AM','2 hour'),(6,'Wedding','2025-05-07','12:00 AM','02 hours'),(7,'Batch Party','2025-05-11','12:00 AM','0 hr 45 min'),(8,'gg','2025-05-15','12:00 AM','3 hr 00 min'),(9,'hh','2025-05-17','12:00 AM','0 hr 15 min'),(10,'BTB','2025-05-05','11:00 PM','2 hr 00 min'),(11,'BYD','2025-05-16','12:00 AM','2 hr 00 min'),(12,'Wedding','2025-05-06','09:00 AM','6 hr 00 min'),(13,'r4','2025-08-08','12:00 AM','2 hr 00 min'),(14,'Batch Party','2025-05-08','11:00 PM','1 hr 00 min');
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
) ENGINE=InnoDB AUTO_INCREMENT=54 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `inventory`
--

LOCK TABLES `inventory` WRITE;
/*!40000 ALTER TABLE `inventory` DISABLE KEYS */;
INSERT INTO `inventory` VALUES (1,'Onions','Foods','500g',120.00,'FarmFresh','5553333333','2025-05-07 11:18:31',100),(2,'Turmeric Powder','Foods','100g',160.00,'NatureSpice','5552222222','2025-05-07 11:18:32',50),(3,'Ginger','Foods','200g',110.00,'GreenLeaf','5551111111','2025-05-07 11:18:32',70),(4,'Cumin Seeds','Foods','100g',180.00,'SpiceWorld','5556666666','2025-05-07 11:18:32',40),(5,'Green Chilies','Foods','250g',130.00,'ChiliFarm','5558888888','2025-05-07 11:18:32',90),(6,'Potatoes','Foods','0.90kg',90.00,'RootHarvest','5559999999','2025-05-07 11:18:32',121),(7,'Carrots','Foods','400g',100.00,'FreshFields','5551212121','2025-05-07 11:18:32',81),(8,'Coriander Leaves','Foods','100g',40.00,'HerbHeaven','5553434343','2025-05-07 11:18:32',60),(9,'Mustard Seeds','Foods','200g',170.00,'SpiceCart','5557878787','2025-05-07 11:18:32',45),(10,'Curry Leaves','Foods','50g',30.00,'GreenBasket','5556565656','2025-05-07 11:18:32',75),(11,'Beans','Foods','500g',95.00,'FarmDirect','5554747474','2025-05-07 11:18:32',95),(12,'Tamarind','Foods','250g',150.00,'TangyTreats','5552323232','2025-05-07 11:18:32',55),(13,'Black Pepper','Foods','100g',200.00,'PepperKing','5556767676','2025-05-07 11:18:32',35),(15,'Onions','Foods','500g',120.00,'FarmFresh','5553333333','2025-05-07 12:48:11',100),(16,'Turmeric Powder','Foods','100g',160.00,'NatureSpice','5552222222','2025-05-07 12:48:11',50),(17,'Ginger','Foods','200g',110.00,'GreenLeaf','5551111111','2025-05-07 12:48:11',70),(18,'Cumin Seeds','Foods','100g',180.00,'SpiceWorld','5556666666','2025-05-07 12:48:11',40),(19,'Green Chilies','Foods','250g',130.00,'ChiliFarm','5558888888','2025-05-07 12:48:11',90),(20,'Potatoes','Foods','0.90kg',90.00,'RootHarvest','5559999999','2025-05-07 12:48:11',121),(21,'Carrots','Foods','400g',100.00,'FreshFields','5551212121','2025-05-07 12:48:11',81),(22,'Coriander Leaves','Foods','100g',40.00,'HerbHeaven','5553434343','2025-05-07 12:48:11',60),(23,'Mustard Seeds','Foods','200g',170.00,'SpiceCart','5557878787','2025-05-07 12:48:11',45),(24,'Curry Leaves','Foods','50g',30.00,'GreenBasket','5556565656','2025-05-07 12:48:11',75),(25,'Beans','Foods','500g',95.00,'FarmDirect','5554747474','2025-05-07 12:48:11',95),(26,'Tamarind','Foods','250g',150.00,'TangyTreats','5552323232','2025-05-07 12:48:11',55),(27,'Black Pepper','Foods','100g',200.00,'PepperKing','5556767676','2025-05-07 12:48:11',35),(28,'Onions','Foods','500g',120.00,'FarmFresh','5553333333','2025-05-07 12:49:04',100),(29,'Turmeric Powder','Foods','100g',160.00,'NatureSpice','5552222222','2025-05-07 12:49:04',50),(30,'Ginger','Foods','200g',110.00,'GreenLeaf','5551111111','2025-05-07 12:49:04',70),(31,'Cumin Seeds','Foods','100g',180.00,'SpiceWorld','5556666666','2025-05-07 12:49:04',40),(32,'Green Chilies','Foods','250g',130.00,'ChiliFarm','5558888888','2025-05-07 12:49:04',90),(33,'Potatoes','Foods','0.90kg',90.00,'RootHarvest','5559999999','2025-05-07 12:49:04',121),(34,'Carrots','Foods','400g',100.00,'FreshFields','5551212121','2025-05-07 12:49:04',81),(35,'Coriander Leaves','Foods','100g',40.00,'HerbHeaven','5553434343','2025-05-07 12:49:04',60),(36,'Mustard Seeds','Foods','200g',170.00,'SpiceCart','5557878787','2025-05-07 12:49:04',45),(37,'Curry Leaves','Foods','50g',30.00,'GreenBasket','5556565656','2025-05-07 12:49:04',75),(38,'Beans','Foods','500g',95.00,'FarmDirect','5554747474','2025-05-07 12:49:04',95),(39,'Tamarind','Foods','250g',150.00,'TangyTreats','5552323232','2025-05-07 12:49:04',55),(40,'Black Pepper','Foods','100g',200.00,'PepperKing','5556767676','2025-05-07 12:49:04',35),(41,'Onions','Foods','500g',120.00,'FarmFresh','5553333333','2025-05-07 12:51:21',100),(42,'Turmeric Powder','Foods','100g',160.00,'NatureSpice','5552222222','2025-05-07 12:51:21',50),(43,'Ginger','Foods','200g',110.00,'GreenLeaf','5551111111','2025-05-07 12:51:21',70),(44,'Cumin Seeds','Foods','100g',180.00,'SpiceWorld','5556666666','2025-05-07 12:51:21',40),(45,'Green Chilies','Foods','250g',130.00,'ChiliFarm','5558888888','2025-05-07 12:51:21',90),(46,'Potatoes','Foods','0.90kg',90.00,'RootHarvest','5559999999','2025-05-07 12:51:21',121),(47,'Carrots','Foods','400g',100.00,'FreshFields','5551212121','2025-05-07 12:51:21',81),(48,'Coriander Leaves','Foods','100g',40.00,'HerbHeaven','5553434343','2025-05-07 12:51:21',60),(49,'Mustard Seeds','Foods','200g',170.00,'SpiceCart','5557878787','2025-05-07 12:51:21',45),(50,'Curry Leaves','Foods','50g',30.00,'GreenBasket','5556565656','2025-05-07 12:51:21',75),(51,'Beans','Foods','500g',95.00,'FarmDirect','5554747474','2025-05-07 12:51:21',95),(52,'Tamarind','Foods','250g',150.00,'TangyTreats','5552323232','2025-05-07 12:51:21',55),(53,'Black Pepper','Foods','100g',200.00,'PepperKing','5556767676','2025-05-07 12:51:21',35);
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
) ENGINE=InnoDB AUTO_INCREMENT=10 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `inventory_orders`
--

LOCK TABLES `inventory_orders` WRITE;
/*!40000 ALTER TABLE `inventory_orders` DISABLE KEYS */;
INSERT INTO `inventory_orders` VALUES (1,'Carrot','1kg','2025-05-03 23:39:24',NULL),(2,'Carrot','1000g','2025-05-03 23:39:43',NULL),(3,'Carrot','100g','2025-05-03 23:39:53',NULL),(4,'Carrot','5','2025-05-03 23:40:04',NULL),(5,'Carrot','7','2025-05-03 23:40:19',NULL),(6,'Chairs','20','2025-05-04 00:13:56',NULL),(7,'Tables','4','2025-05-04 00:14:19',NULL),(8,'Carrots','100g','2025-05-07 13:06:39',10000),(9,'Potatoes','100g','2025-05-08 17:08:03',9000);
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
INSERT INTO `room` VALUES ('001','Available','Cleaned','35000','Double Bed'),('002','Available','Cleaned','35000','Double Bed'),('003','Available','Cleaned','35000','Double Bed'),('004','Available','Dirty','35000','Double Bed'),('005','Not yet','Dirty','35000','Double Bed'),('006','Not yet','Cleaned','35000','Double Bed'),('007','Available','Cleaned','35000','Double Bed'),('008','Available','Cleaned','35000','Double Bed'),('009','Not yet','Cleaned','35000','Double Bed'),('010','Available','Cleaned','35000','Double Bed'),('011','Available','Cleaned','25000','Single Bed'),('012','Available','Cleaned','25000','Single Bed'),('013','Available','Cleaned','25000','Single Bed'),('014','Not yet','Dirty','25000','Single Bed'),('015','Not yet','Dirty','25000','Single Bed'),('016','Not yet','Cleaned','25000','Single Bed'),('017','Available','Cleaned','25000','Single Bed'),('018','Available','Cleaned','25000','Single Bed'),('019','Not yet','Cleaned','25000','Single Bed'),('020','Available','Cleaned','25000','Single Bed');
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

-- Dump completed on 2025-05-08 23:35:23
