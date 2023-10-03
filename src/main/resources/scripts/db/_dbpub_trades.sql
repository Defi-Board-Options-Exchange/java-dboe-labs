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

-- Dumping structure for table ngontro86._dbpub_trades
CREATE TABLE IF NOT EXISTS `_dbpub_trades` (
  `inst_id` varchar(50) NOT NULL,
  `order_req_id` varchar(128) NOT NULL,
  `exchange_order_id` varchar(128) DEFAULT NULL,
  `broker` varchar(128) NOT NULL,
  `account` varchar(128) DEFAULT NULL,
  `portfolio` varchar(128) DEFAULT NULL,
  `size` int(11) DEFAULT NULL,
  `avg_price` double DEFAULT NULL,
  `status` varchar(50) DEFAULT NULL,
  `timestamp` bigint(20) DEFAULT NULL,
  `date` int(11) NOT NULL,
  PRIMARY KEY (`date`,`order_req_id`,`inst_id`,`broker`),
  KEY `inst_id` (`inst_id`),
  KEY `broker` (`broker`),
  KEY `account` (`account`),
  KEY `portfolio` (`portfolio`),
  KEY `timestamp` (`timestamp`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8;

-- Data exporting was unselected.
/*!40101 SET SQL_MODE=IFNULL(@OLD_SQL_MODE, '') */;
/*!40014 SET FOREIGN_KEY_CHECKS=IF(@OLD_FOREIGN_KEY_CHECKS IS NULL, 1, @OLD_FOREIGN_KEY_CHECKS) */;
/*!40101 SET CHARACTER_SET_CLIENT=@OLD_CHARACTER_SET_CLIENT */;
