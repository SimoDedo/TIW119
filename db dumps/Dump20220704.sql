-- MySQL dump 10.13  Distrib 8.0.28, for Win64 (x86_64)
--
-- Host: 127.0.0.1    Database: tiw119
-- ------------------------------------------------------
-- Server version	8.0.28

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
-- Table structure for table `account`
--

DROP TABLE IF EXISTS `account`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `account` (
  `id` int NOT NULL AUTO_INCREMENT,
  `name` varchar(45) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NOT NULL,
  `balance` decimal(12,2) NOT NULL,
  `userid` int NOT NULL,
  PRIMARY KEY (`id`),
  UNIQUE KEY `name_UNIQUE` (`name`),
  KEY `userid_idx` (`userid`),
  CONSTRAINT `userid` FOREIGN KEY (`userid`) REFERENCES `user` (`id`) ON DELETE CASCADE ON UPDATE CASCADE
) ENGINE=InnoDB AUTO_INCREMENT=16 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `account`
--

LOCK TABLES `account` WRITE;
/*!40000 ALTER TABLE `account` DISABLE KEYS */;
INSERT INTO `account` VALUES (7,'contodisimo',12001553.88,1),(8,'contodivale',12498.00,2),(9,'contodiuser',3000.50,3),(10,'contodisimo2',0.00,1),(11,'contodisimocreato',89.00,1),(12,'test lmfaoooo',8989906989.00,4),(13,'conto per fare casino',0.00,1),(14,'conto nuovo',8722834.00,2),(15,'contodecimale',0.02,1);
/*!40000 ALTER TABLE `account` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `contact`
--

DROP TABLE IF EXISTS `contact`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `contact` (
  `userid` int NOT NULL,
  `accountid` int NOT NULL,
  PRIMARY KEY (`userid`,`accountid`),
  KEY `contactid_idx` (`accountid`),
  CONSTRAINT `contactid` FOREIGN KEY (`accountid`) REFERENCES `account` (`id`),
  CONSTRAINT `ownerid` FOREIGN KEY (`userid`) REFERENCES `user` (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `contact`
--

LOCK TABLES `contact` WRITE;
/*!40000 ALTER TABLE `contact` DISABLE KEYS */;
INSERT INTO `contact` VALUES (1,7),(1,8),(3,8),(1,9),(2,9);
/*!40000 ALTER TABLE `contact` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `movement`
--

DROP TABLE IF EXISTS `movement`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `movement` (
  `id` int NOT NULL AUTO_INCREMENT,
  `date` timestamp NOT NULL,
  `amount` decimal(12,2) NOT NULL,
  `motive` varchar(200) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NOT NULL,
  `inAccountID` int NOT NULL,
  `outAccountID` int NOT NULL,
  PRIMARY KEY (`id`),
  KEY `inAccountid_idx` (`inAccountID`),
  KEY `outAccountid_idx` (`outAccountID`),
  CONSTRAINT `inAccountid` FOREIGN KEY (`inAccountID`) REFERENCES `account` (`id`),
  CONSTRAINT `outAccountid` FOREIGN KEY (`outAccountID`) REFERENCES `account` (`id`)
) ENGINE=InnoDB AUTO_INCREMENT=30 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `movement`
--

LOCK TABLES `movement` WRITE;
/*!40000 ALTER TABLE `movement` DISABLE KEYS */;
INSERT INTO `movement` VALUES (2,'2020-07-02 07:10:32',50.00,'per provare',7,8),(3,'2020-08-08 05:12:34',760.00,'test',8,7),(4,'2000-11-12 20:34:45',8.00,'vecchio',7,8),(5,'1990-09-30 22:00:00',600000000.00,'preciso',7,8),(6,'2022-07-03 07:42:55',50.00,'creo movimento',8,7),(7,'2022-07-03 07:46:52',100.00,'PROVIAMO',8,7),(8,'2022-07-03 07:47:57',100.00,'riprenditeli non li voglio ugh',7,8),(9,'2022-07-03 07:55:10',8000.00,'motivo',12,13),(10,'2022-07-03 10:37:12',10000.00,'tnato per',8,14),(11,'2022-07-03 11:19:18',50.00,'vediamo',8,7),(12,'2022-07-03 11:26:10',40.00,'asodinosdna',8,7),(13,'2022-07-03 11:29:49',50.00,'aosdnd',8,7),(15,'2022-07-03 11:47:58',10.00,'wow',8,7),(16,'2022-07-03 11:51:43',10.00,'aoidsdani',8,7),(17,'2022-07-03 11:53:08',10.00,'daiii',8,7),(18,'2022-07-03 11:56:57',10.00,'NSDANISDA',8,7),(19,'2022-07-03 12:10:16',10.00,'ciaoooo',8,7),(20,'2022-07-03 12:28:07',1.88,'asnasdp',7,15),(21,'2022-07-03 18:02:41',12000000.00,'tieni tutto quello che ho',7,10),(22,'2022-07-04 05:42:31',10.00,'vediamo se back funziona',8,7),(24,'2022-07-04 05:42:32',10.00,'èèòàùàì',8,7),(25,'2022-07-04 06:18:13',70.00,'\\u00C3\\u00A8\\u00C3\\u00B2\\u00C3\\u00A0\\u00C3\\u00B9\\u00C3\\u00AC',8,7),(26,'2022-07-04 06:28:24',8.00,'\\u00C3\\u00A8\\u00C3\\u00B2\\u00C3\\u00A0\\u00C3\\u00AC\\u00C3\\u00B9',8,7),(27,'2022-07-04 06:33:57',78.00,'\\u00C3\\u00A8\\u00C3\\u00B9\\u00C3\\u00B2\\u00C3\\u00A0',8,7),(28,'2022-07-04 06:42:25',67.00,'Ã¨Ã²Ã Ã¹Ã¬',8,7),(29,'2022-07-04 06:50:24',8.00,'asdbbsd\\n',8,7);
/*!40000 ALTER TABLE `movement` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `user`
--

DROP TABLE IF EXISTS `user`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `user` (
  `id` int NOT NULL AUTO_INCREMENT,
  `username` varchar(45) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NOT NULL,
  `email` varchar(45) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NOT NULL,
  `password` varchar(45) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NOT NULL,
  `name` varchar(45) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NOT NULL,
  `surname` varchar(45) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NOT NULL,
  PRIMARY KEY (`id`),
  UNIQUE KEY `email_UNIQUE` (`email`),
  UNIQUE KEY `username_UNIQUE` (`username`)
) ENGINE=InnoDB AUTO_INCREMENT=5 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `user`
--

LOCK TABLES `user` WRITE;
/*!40000 ALTER TABLE `user` DISABLE KEYS */;
INSERT INTO `user` VALUES (1,'Simo','simo@mail.it','simo','Simone','de Donato'),(2,'Vale','vale@mail.it','vale','Valerio','Donno'),(3,'User','user@mail.it','user','User','User'),(4,'test1','test1@mail.com','test1','test1name','test1surname');
/*!40000 ALTER TABLE `user` ENABLE KEYS */;
UNLOCK TABLES;
/*!40103 SET TIME_ZONE=@OLD_TIME_ZONE */;

/*!40101 SET SQL_MODE=@OLD_SQL_MODE */;
/*!40014 SET FOREIGN_KEY_CHECKS=@OLD_FOREIGN_KEY_CHECKS */;
/*!40014 SET UNIQUE_CHECKS=@OLD_UNIQUE_CHECKS */;
/*!40101 SET CHARACTER_SET_CLIENT=@OLD_CHARACTER_SET_CLIENT */;
/*!40101 SET CHARACTER_SET_RESULTS=@OLD_CHARACTER_SET_RESULTS */;
/*!40101 SET COLLATION_CONNECTION=@OLD_COLLATION_CONNECTION */;
/*!40111 SET SQL_NOTES=@OLD_SQL_NOTES */;

-- Dump completed on 2022-07-04 10:55:55
