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

-- Dumping structure for table ngontro86.serv_fx_inst
DROP TABLE IF EXISTS `serv_fx_inst`;
CREATE TABLE IF NOT EXISTS `serv_fx_inst` (
  `underlying` varchar(50) NOT NULL DEFAULT '',
  `tick_size` double DEFAULT NULL,
  `currency` varchar(50) NOT NULL DEFAULT '',
  PRIMARY KEY (`underlying`,`currency`)
) ENGINE=InnoDB DEFAULT CHARSET=latin1;

-- Dumping data for table ngontro86.serv_fx_inst: ~4 rows (approximately)
/*!40000 ALTER TABLE `serv_fx_inst` DISABLE KEYS */;
INSERT INTO `serv_fx_inst` (`underlying`, `tick_size`, `currency`) VALUES
	('AUD', 0.00005, 'USD'),
	('EUR', 0.00005, 'USD'),
	('USD', 0.00005, 'CAD'),
	('USD', 0.005, 'JPY');
/*!40000 ALTER TABLE `serv_fx_inst` ENABLE KEYS */;
/*!40101 SET SQL_MODE=IFNULL(@OLD_SQL_MODE, '') */;
/*!40014 SET FOREIGN_KEY_CHECKS=IF(@OLD_FOREIGN_KEY_CHECKS IS NULL, 1, @OLD_FOREIGN_KEY_CHECKS) */;
/*!40101 SET CHARACTER_SET_CLIENT=@OLD_CHARACTER_SET_CLIENT */;
