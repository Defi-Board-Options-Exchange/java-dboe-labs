-- --------------------------------------------------------
-- Host:                         127.0.0.1
-- Server version:               5.7.9-log - MySQL Community Server (GPL)
-- Server OS:                    Win64
-- HeidiSQL Version:             9.3.0.4984
-- --------------------------------------------------------

/*!40101 SET @OLD_CHARACTER_SET_CLIENT=@@CHARACTER_SET_CLIENT */;
/*!40101 SET NAMES utf8mb4 */;
/*!40014 SET @OLD_FOREIGN_KEY_CHECKS=@@FOREIGN_KEY_CHECKS, FOREIGN_KEY_CHECKS=0 */;
/*!40101 SET @OLD_SQL_MODE=@@SQL_MODE, SQL_MODE='NO_AUTO_VALUE_ON_ZERO' */;

-- Dumping database structure for ngontro86
CREATE DATABASE IF NOT EXISTS `ngontro86` /*!40100 DEFAULT CHARACTER SET utf8 */;
USE `ngontro86`;


-- Dumping structure for table ngontro86.pair_signal_setup
CREATE TABLE IF NOT EXISTS `pair_signal_setup` (
  `pairName` varchar(50) NOT NULL,
  `firstInstId` varchar(50) DEFAULT NULL,
  `secondInstId` varchar(50) DEFAULT NULL,
  `firstSize` int(11) DEFAULT '1',
  `secondSize` int(11) DEFAULT '1',
  `startingSignalTime` bigint(20) DEFAULT NULL,
  `endingSignalTime` bigint(20) DEFAULT NULL,
  `sampling` int(11) DEFAULT NULL,
  `active` int(11) DEFAULT '1',
  PRIMARY KEY (`pairName`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8;

-- Dumping data for table ngontro86.pair_signal_setup: ~1 rows (approximately)
/*!40000 ALTER TABLE `pair_signal_setup` DISABLE KEYS */;
REPLACE INTO `pair_signal_setup` (`pairName`, `firstInstId`, `secondInstId`, `firstSize`, `secondSize`, `startingSignalTime`, `endingSignalTime`, `sampling`, `active`) VALUES
	('Hang Seng T', 'HSIM17', 'HHI.HKM17', 1, 1, 80000, 120000, 1, 1),
	('Mini HS T', 'MHIM17', 'HHI.HKM17', 1, 1, 80000, 120000, 1, 1);
/*!40000 ALTER TABLE `pair_signal_setup` ENABLE KEYS */;
/*!40101 SET SQL_MODE=IFNULL(@OLD_SQL_MODE, '') */;
/*!40014 SET FOREIGN_KEY_CHECKS=IF(@OLD_FOREIGN_KEY_CHECKS IS NULL, 1, @OLD_FOREIGN_KEY_CHECKS) */;
/*!40101 SET CHARACTER_SET_CLIENT=@OLD_CHARACTER_SET_CLIENT */;
