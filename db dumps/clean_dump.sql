CREATE DATABASE  IF NOT EXISTS `tiw119` /*!40100 DEFAULT CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci */ /*!80016 DEFAULT ENCRYPTION='N' */;
USE `tiw119`;
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
) ENGINE=InnoDB AUTO_INCREMENT=67892 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `account`
--

LOCK TABLES `account` WRITE;
/*!40000 ALTER TABLE `account` DISABLE KEYS */;
INSERT INTO `account` VALUES (1,'contodisimo',10000.00,1),(2,'contodivale',500.00,2),(3,'contodiuser',100.00,3),(4,'contodisimo out',2222.22,1),(5,'contodisimo no movimenti',0.00,1),(6,'contodivale no movimenti',600.60,2),(7,'contodinomovimenti',150.00,5),(8,'contodinocontatti',300.00,6),(9,'contodisimo in',4000.00,1),(10,'contodivale out',5000.00,2),(11,'contodivale in',6000.00,2),(578,'578',578.00,67890),(12345,'12345',12345.00,12345),(12367,'12367',12367.00,12367),(12368,'12368',12368.00,12367),(12369,'12369',12369.00,12367),(23456,'23456',23456.00,12345),(67890,'67890',67890.00,67890),(67891,'67891',67891.00,67890);
/*!40000 ALTER TABLE `account` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `contact`
--

DROP TABLE IF EXISTS `contact`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `contact` (
  `ownerUserid` int NOT NULL,
  `contactUserid` int NOT NULL,
  PRIMARY KEY (`ownerUserid`,`contactUserid`),
  KEY `contactUserid_idx` (`contactUserid`),
  CONSTRAINT `contactUserid` FOREIGN KEY (`contactUserid`) REFERENCES `user` (`id`) ON DELETE CASCADE ON UPDATE CASCADE,
  CONSTRAINT `ownerUserid` FOREIGN KEY (`ownerUserid`) REFERENCES `user` (`id`) ON DELETE CASCADE ON UPDATE CASCADE
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `contact`
--

LOCK TABLES `contact` WRITE;
/*!40000 ALTER TABLE `contact` DISABLE KEYS */;
INSERT INTO `contact` VALUES (2,1),(1,2),(1,3),(2,3),(1,12345),(2,12345),(1,12367),(2,12367),(1,67890),(2,67890);
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
  CONSTRAINT `inAccountid` FOREIGN KEY (`inAccountID`) REFERENCES `account` (`id`) ON DELETE CASCADE ON UPDATE CASCADE,
  CONSTRAINT `outAccountid` FOREIGN KEY (`outAccountID`) REFERENCES `account` (`id`) ON DELETE CASCADE ON UPDATE CASCADE
) ENGINE=InnoDB AUTO_INCREMENT=62 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `movement`
--

LOCK TABLES `movement` WRITE;
/*!40000 ALTER TABLE `movement` DISABLE KEYS */;
INSERT INTO `movement` VALUES (1,'1982-07-04 08:52:59',50.50,'movimento vecchio',2,1),(2,'1982-07-04 08:53:00',500.00,'movimento meno vecchio',2,1),(3,'1990-01-22 21:27:12',600.56,'movimento più recente',2,1),(4,'2022-02-18 13:45:40',0.10,'movimento ancora più recente',2,1),(5,'2021-02-12 16:15:40',123.00,'regalo',1,2),(6,'2022-05-01 00:20:20',456.00,'risarcimento',1,3),(7,'2022-06-18 13:45:42',789.00,'Lorem ipsum dolor sit amet, consectetur adipiscing elit. Proin sed consectetur eros. Maecenas ornare facilisis tempus. Fusce vitae consequat diam, sed lobortis elit.',1,2),(8,'2022-07-14 01:33:33',33.00,'Lorem ipsum dolor sit amet, consectetur adipiscing elit. Proin sed consectetur eros. Maecenas ornare facilisis tempus. Fusce vitae consequat diam, sed lobortis elit.',9,4),(9,'2022-07-14 01:33:33',33.00,'Lorem ipsum dolor sit amet, consectetur adipiscing elit. Proin sed consectetur eros. Maecenas ornare facilisis tempus. Fusce vitae consequat diam, sed lobortis elit.',11,10);
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
) ENGINE=InnoDB AUTO_INCREMENT=67891 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `user`
--

LOCK TABLES `user` WRITE;
/*!40000 ALTER TABLE `user` DISABLE KEYS */;
INSERT INTO `user` VALUES (1,'Simo','simo@mail.it','simo','Simone','de Donato'),(2,'Vale','vale@mail.it','vale','Valerio','Donno'),(3,'User','user@mail.it','user','User','User'),(4,'no conti','noconti@mail.it','noconti','User','No Conti'),(5,'no movimenti','nomovimenti@mail.it','nomovimenti','User','No Movimenti'),(6,'no contatti','nocontatti@mail.it','nocontatti','User','No Contatti'),(12345,'12345','12345@mail.it','12345','12345','12345'),(12367,'12367','12367@mail.it','12367','12367','12367'),(67890,'67890','67890@mail.it','67890','67890','67890');
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

-- Dump completed on 2022-07-14 20:38:26
