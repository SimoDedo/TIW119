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
) ENGINE=InnoDB AUTO_INCREMENT=19 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `account`
--

LOCK TABLES `account` WRITE;
/*!40000 ALTER TABLE `account` DISABLE KEYS */;
INSERT INTO `account` VALUES (7,'contodisimo',11990647.15,1),(8,'contodivale',12794.74,2),(9,'contodiuser',3000.50,3),(10,'contodisimo2',696.00,1),(11,'contodisimocreato',100.00,1),(12,'test lmfaoooo',8989906989.00,4),(13,'conto per fare casino',10000.00,1),(14,'conto nuovo',8722834.00,2),(15,'contodecimale',0.01,1),(16,'wow',82.00,1),(17,'è.è.è.è',8899911.00,1),(18,'asdasd',8988.00,1);
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
INSERT INTO `contact` VALUES (2,1),(1,2),(1,3);
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
INSERT INTO `movement` VALUES (31,'2022-07-04 08:52:59',3.00,'òòòàààùùùù',8,7),(32,'2022-07-04 08:52:59',5.00,'òòòàààùùùù',8,7),(33,'2022-07-04 10:22:19',1000.50,'così tanto perchè mi va',10,7),(34,'2022-07-04 10:23:02',0.00,'niente',10,7),(35,'2022-07-04 16:22:15',50.50,'niosdniopsdniopnopasdnopasdnopasnopasdnopnoasdopnasdnopasdnopasdnopnopasdnopasdnopasdnopasdnopnopasdnopasdnopsdanopasdnopasdnopnopasd',7,10),(36,'2022-07-04 16:22:29',50.50,'niosdniopsdniopnopasdnopasdnopasnopasdnopnoasdopnasdnopasdnopasdnopnopasdnopasdnopasdnopasdnopnopasdnopasdnopsdanopasdnopasdnopnopasd',7,10),(37,'2022-07-04 16:22:35',50.50,'niosdniopsdniopnopasdnopasdnopasnopasdnopnoasdopnasdnopasdnopasdnopnopasdnopasdnopasdnopasdnopnopasdnopasdnopsdanopasdnopasdnopnopasd',7,10),(38,'2022-07-04 16:22:36',50.50,'niosdniopsdniopnopasdnopasdnopasnopasdnopnoasdopnasdnopasdnopasdnopnopasdnopasdnopasdnopasdnopnopasdnopasdnopsdanopasdnopasdnopnopasd',7,10),(39,'2022-07-04 16:22:38',50.50,'niosdniopsdniopnopasdnopasdnopasnopasdnopnoasdopnasdnopasdnopasdnopnopasdnopasdnopasdnopasdnopnopasdnopasdnopsdanopasdnopasdnopnopasd',7,10),(40,'2022-07-04 16:30:28',9.00,'9',10,7),(41,'2022-07-04 18:06:44',10.00,'120',10,7),(42,'2022-07-04 18:08:55',10.00,'10',10,7),(43,'2022-07-04 18:10:07',10.00,'10',10,7),(44,'2022-07-04 18:26:34',10.00,'10',10,7),(45,'2022-07-04 18:37:12',7.00,'è l\'unico di cui ricordo l\'id',7,16),(46,'2022-07-04 18:45:32',11.00,'11',11,10),(47,'2022-07-04 18:46:14',0.00,'0',11,13),(48,'2022-07-04 18:47:08',10000.00,'fai casino',13,7),(49,'2022-07-04 18:52:23',10.00,'poo',7,10),(50,'2022-07-04 18:56:57',10.00,'10',10,7),(51,'2022-07-05 05:15:49',15.74,'Sed ut perspiciatis unde omnis iste natus ',8,7),(52,'2022-07-05 06:46:31',2.00,'frase lunga lunga lunga lunga lunga lunga lunga lunga lunga lunga lunga lunga lunga lunga',8,7),(53,'2022-07-05 08:31:27',89.00,'89 lmfaopopopopop',7,17),(54,'2022-07-05 19:04:45',89.00,'a valerio con affetto',8,7),(55,'2022-07-05 19:08:28',89.00,'perchè siamo totalemente piu fortunati di voi',8,10),(56,'2022-07-06 06:01:33',89.00,'89',8,7),(57,'2022-07-06 07:32:48',1.00,'per capire una cosin',7,18),(58,'2022-07-06 08:02:24',0.50,'0',8,10),(59,'2022-07-06 08:02:31',0.50,'0',8,10),(60,'2022-07-06 08:22:11',0.79,'decimale',15,7),(61,'2022-07-06 08:22:53',0.80,'non voglio',7,15);
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
) ENGINE=InnoDB AUTO_INCREMENT=11 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `user`
--

LOCK TABLES `user` WRITE;
/*!40000 ALTER TABLE `user` DISABLE KEYS */;
INSERT INTO `user` VALUES (1,'Simo','simo@mail.it','simo','Simone','de Donato'),(2,'Vale','vale@mail.it','vale','Valerio','Donno'),(3,'User','user@mail.it','user','User','User'),(4,'test1','test1@mail.com','test1','test1name','test1surname'),(5,'asdasd','asdasd@asdasd.a','asdasd','asdasd','asdsda'),(6,'asdasdq','asdasd@asdasd.aq','asdasd','asdasd','asdsda'),(7,'lol','lol.lol@mail.com','lol','lol','lol'),(8,'lol2','lol2.lol2.lol@mail.mailmailit','lol','lol','lol'),(9,'test2','simone.dedonato@mail.polimi.it','test2','test2','test2'),(10,'test3','username@yahoo.co.co.co.co','test3','test3','test3');
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

-- Dump completed on 2022-07-10 12:25:27
