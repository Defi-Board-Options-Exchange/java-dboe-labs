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

-- Dumping structure for table ngontro86._dbpub_inst_ids
CREATE TABLE IF NOT EXISTS `_dbpub_inst_ids` (
  `inst_id` varchar(32) NOT NULL,
  `inst_type` int(11) DEFAULT NULL,
  `underlying` varchar(16) NOT NULL,
  `market` varchar(16) NOT NULL,
  `expiry_date` int(11) DEFAULT NULL,
  `multiplier` int(11) DEFAULT NULL,
  `currency` varchar(8) DEFAULT NULL,
  `tick_size` double DEFAULT NULL,
  `date` int(11) NOT NULL,
  `timestamp` bigint(20) NOT NULL,
  PRIMARY KEY (`inst_id`,`date`),
  KEY `underlying` (`underlying`),
  KEY `market` (`market`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8;

-- Data exporting was unselected.
/*!40101 SET SQL_MODE=IFNULL(@OLD_SQL_MODE, '') */;
/*!40014 SET FOREIGN_KEY_CHECKS=IF(@OLD_FOREIGN_KEY_CHECKS IS NULL, 1, @OLD_FOREIGN_KEY_CHECKS) */;
/*!40101 SET CHARACTER_SET_CLIENT=@OLD_CHARACTER_SET_CLIENT */;
