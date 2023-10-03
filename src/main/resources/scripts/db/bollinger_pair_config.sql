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

-- Dumping structure for table ngontro86.bollinger_pair_config
CREATE TABLE IF NOT EXISTS `bollinger_pair_config` (
  `pairName` varchar(50) NOT NULL,
  `firstLegInstId` varchar(50) DEFAULT NULL,
  `secondLegInstId` varchar(50) DEFAULT NULL,
  `firstLegMultiplier` int(11) DEFAULT '1',
  `secondLegMultiplier` int(11) DEFAULT '1',
  `fxConversionRate` double DEFAULT '1',
  `active` int(11) DEFAULT '1',
  PRIMARY KEY (`pairName`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8 ROW_FORMAT=DYNAMIC;

-- Dumping data for table ngontro86.bollinger_pair_config: ~5 rows (approximately)
/*!40000 ALTER TABLE `bollinger_pair_config` DISABLE KEYS */;
INSERT INTO `bollinger_pair_config` (`pairName`, `firstLegInstId`, `secondLegInstId`, `firstLegMultiplier`, `secondLegMultiplier`, `fxConversionRate`, `active`) VALUES
	('HKEX HSI-HSCEI', 'HSIH18', 'HHI.HKH18', 1, 2, 1, 1),
	('SGX TW-IN', 'STWH18', 'NIFTYH18', 100, 4, 1, 1);
/*!40000 ALTER TABLE `bollinger_pair_config` ENABLE KEYS */;
/*!40101 SET SQL_MODE=IFNULL(@OLD_SQL_MODE, '') */;
/*!40014 SET FOREIGN_KEY_CHECKS=IF(@OLD_FOREIGN_KEY_CHECKS IS NULL, 1, @OLD_FOREIGN_KEY_CHECKS) */;
/*!40101 SET CHARACTER_SET_CLIENT=@OLD_CHARACTER_SET_CLIENT */;
