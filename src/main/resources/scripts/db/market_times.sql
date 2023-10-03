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

-- Dumping structure for table ngontro86.market_times
CREATE TABLE IF NOT EXISTS `market_times` (
  `underlying` varchar(16) NOT NULL,
  `state` varchar(16) NOT NULL,
  `starting_time` int(11) DEFAULT NULL,
  `ending_time` int(11) DEFAULT NULL,
  PRIMARY KEY (`underlying`,`state`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8;

-- Dumping data for table ngontro86.market_times: ~58 rows (approximately)
/*!40000 ALTER TABLE `market_times` DISABLE KEYS */;
INSERT INTO `market_times` (`underlying`, `state`, `starting_time`, `ending_time`) VALUES
	('EUR', 'Asia', 90000, 153000),
	('EUR', 'London', 153000, 210000),
	('EUR', 'US-Afternoon', 0, 50000),
	('EUR', 'US-Morning', 210000, 235959),
	('HHI.HK', 'T+1-Close', 0, 10000),
	('HHI.HK', 'T+1-Open', 171000, 173000),
	('HHI.HK', 'T+1-UsOpen', 210000, 220000),
	('HHI.HK', 'T-Afternoon', 130000, 163000),
	('HHI.HK', 'T-Morning', 94500, 120000),
	('HHI.HK', 'T-Open', 91500, 94500),
	('HSI', 'T+1-Close', 0, 10000),
	('HSI', 'T+1-Open', 171000, 173000),
	('HSI', 'T+1-UsOpen', 210000, 220000),
	('HSI', 'T-Afternoon', 130000, 163000),
	('HSI', 'T-Morning', 94500, 120000),
	('HSI', 'T-Open', 91500, 94500),
	('MCH.HK', 'T+1-Close', 0, 10000),
	('MCH.HK', 'T+1-Open', 171000, 173000),
	('MCH.HK', 'T+1-UsOpen', 210000, 220000),
	('MCH.HK', 'T-Afternoon', 130000, 163000),
	('MCH.HK', 'T-Morning', 94500, 120000),
	('MCH.HK', 'T-Open', 91500, 94500),
	('MHI', 'T+1-Close', 0, 10000),
	('MHI', 'T+1-Open', 171000, 173000),
	('MHI', 'T+1-UsOpen', 210000, 220000),
	('MHI', 'T-Afternoon', 130000, 163000),
	('MHI', 'T-Morning', 94500, 120000),
	('MHI', 'T-Open', 91500, 94500),
	('MXID', 'T+1-Close', 40000, 44500),
	('MXID', 'T+1-Open', 174500, 181500),
	('MXID', 'T+1-UsOpen', 210000, 220000),
	('MXID', 'T-Afternoon', 120001, 161000),
	('MXID', 'T-Morning', 110000, 120000),
	('MXID', 'T-Open', 100000, 110000),
	('NIFTY', 'T+1-Close', 40000, 44500),
	('NIFTY', 'T+1-Open', 184500, 193000),
	('NIFTY', 'T+1-UsOpen', 210000, 220000),
	('NIFTY', 'T-Afternoon', 140001, 173000),
	('NIFTY', 'T-Morning', 110000, 140000),
	('NIFTY', 'T-Open', 90000, 94500),
	('SSG', 'T+1-Close', 40000, 44500),
	('SSG', 'T+1-Open', 174500, 181500),
	('SSG', 'T+1-UsOpen', 210000, 220000),
	('SSG', 'T-Afternoon', 120001, 171000),
	('SSG', 'T-Morning', 94500, 120000),
	('SSG', 'T-Open', 90000, 94500),
	('STW', 'T+1-Close', 40000, 44500),
	('STW', 'T+1-Open', 141500, 144500),
	('STW', 'T+1-UsOpen', 210000, 220000),
	('STW', 'T-Afternoon', 120000, 134500),
	('STW', 'T-Morning', 93000, 120000),
	('STW', 'T-Open', 84500, 93000),
	('XINA50', 'T+1-Close', 40000, 44500),
	('XINA50', 'T+1-Open', 170000, 173000),
	('XINA50', 'T+1-UsOpen', 210000, 220000),
	('XINA50', 'T-Afternoon', 120000, 163000),
	('XINA50', 'T-Morning', 93000, 120000),
	('XINA50', 'T-Open', 90000, 93000);
/*!40000 ALTER TABLE `market_times` ENABLE KEYS */;


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

-- Dumping data for table ngontro86._dbpub_inst_ids: ~45 rows (approximately)
/*!40000 ALTER TABLE `_dbpub_inst_ids` DISABLE KEYS */;
INSERT INTO `_dbpub_inst_ids` (`inst_id`, `inst_type`, `underlying`, `market`, `expiry_date`, `multiplier`, `currency`, `tick_size`, `date`, `timestamp`) VALUES
	('AUD/USD', 0, 'AUD', '', 0, 1, 'USD', 0.00005, 20180830, 20180830232730),
	('AUD/USD', 0, 'AUD', '', 0, 1, 'USD', 0.00005, 20180930, 20180930174713),
	('AUD/USD', 0, 'AUD', '', 0, 1, 'USD', 0.00005, 20181006, 20181006233930),
	('EUR/USD', 0, 'EUR', '', 0, 1, 'USD', 0.00005, 20180830, 20180830232730),
	('EUR/USD', 0, 'EUR', '', 0, 1, 'USD', 0.00005, 20180930, 20180930174714),
	('EUR/USD', 0, 'EUR', '', 0, 1, 'USD', 0.00005, 20181006, 20181006233930),
	('HHI.HKU18', 1, 'HHI.HK', 'HKFE', 20180927, 50, 'HKD', 1, 20180830, 20180830232731),
	('HHI.HKU18', 1, 'HHI.HK', 'HKFE', 20180927, 50, 'HKD', 1, 20180930, 20180930174715),
	('HHI.HKU18', 1, 'HHI.HK', 'HKFE', 20180927, 50, 'HKD', 1, 20181006, 20181006233931),
	('HSIU18', 1, 'HSI', 'HKFE', 20180927, 50, 'HKD', 1, 20180830, 20180830232731),
	('HSIU18', 1, 'HSI', 'HKFE', 20180927, 50, 'HKD', 1, 20180930, 20180930174715),
	('HSIU18', 1, 'HSI', 'HKFE', 20180927, 50, 'HKD', 1, 20181006, 20181006233931),
	('M3CNXU18', 1, 'M3CNX', 'SGX', 20180927, 5, 'USD', 2, 20180830, 20180830232731),
	('M3CNXU18', 1, 'M3CNX', 'SGX', 20180927, 5, 'USD', 2, 20180930, 20180930174715),
	('M3CNXU18', 1, 'M3CNX', 'SGX', 20180927, 5, 'USD', 2, 20181006, 20181006233931),
	('MCH.HKU18', 1, 'MCH.HK', 'HKFE', 20180927, 10, 'HKD', 1, 20180830, 20180830232731),
	('MCH.HKU18', 1, 'MCH.HK', 'HKFE', 20180927, 10, 'HKD', 1, 20180930, 20180930174715),
	('MCH.HKU18', 1, 'MCH.HK', 'HKFE', 20180927, 10, 'HKD', 1, 20181006, 20181006233931),
	('MHIU18', 1, 'MHI', 'HKFE', 20180927, 10, 'HKD', 1, 20180830, 20180830232731),
	('MHIU18', 1, 'MHI', 'HKFE', 20180927, 10, 'HKD', 1, 20180930, 20180930174715),
	('MHIU18', 1, 'MHI', 'HKFE', 20180927, 10, 'HKD', 1, 20181006, 20181006233931),
	('MXIDU18', 1, 'MXID', 'SGX', 20180927, 2, 'USD', 5, 20180830, 20180830232731),
	('MXIDU18', 1, 'MXID', 'SGX', 20180927, 2, 'USD', 5, 20180930, 20180930174715),
	('MXIDU18', 1, 'MXID', 'SGX', 20180927, 2, 'USD', 5, 20181006, 20181006233931),
	('NIFTYU17', 1, 'NIFTY', 'SGX', 20170928, 2, 'USD', 0.5, 20180830, 20180830232731),
	('NIFTYU17', 1, 'NIFTY', 'SGX', 20170928, 2, 'USD', 0.5, 20180930, 20180930174715),
	('NIFTYU17', 1, 'NIFTY', 'SGX', 20170928, 2, 'USD', 0.5, 20181006, 20181006233931),
	('NIFTYU18', 1, 'NIFTY', 'SGX', 20180927, 2, 'USD', 0.5, 20180830, 20180830232731),
	('NIFTYU18', 1, 'NIFTY', 'SGX', 20180927, 2, 'USD', 0.5, 20180930, 20180930174715),
	('NIFTYU18', 1, 'NIFTY', 'SGX', 20180927, 2, 'USD', 0.5, 20181006, 20181006233931),
	('SSGU18', 1, 'SSG', 'SGX', 20180927, 100, 'SGD', 0.05, 20180830, 20180830232731),
	('SSGU18', 1, 'SSG', 'SGX', 20180927, 100, 'SGD', 0.05, 20180930, 20180930174715),
	('SSGU18', 1, 'SSG', 'SGX', 20180927, 100, 'SGD', 0.05, 20181006, 20181006233931),
	('STWU18', 1, 'STW', 'SGX', 20180927, 100, 'USD', 0.1, 20180830, 20180830232731),
	('STWU18', 1, 'STW', 'SGX', 20180927, 100, 'USD', 0.1, 20180930, 20180930174715),
	('STWU18', 1, 'STW', 'SGX', 20180927, 100, 'USD', 0.1, 20181006, 20181006233931),
	('USD/CAD', 0, 'USD', '', 0, 1, 'CAD', 0.00005, 20180830, 20180830232730),
	('USD/CAD', 0, 'USD', '', 0, 1, 'CAD', 0.00005, 20180930, 20180930174714),
	('USD/CAD', 0, 'USD', '', 0, 1, 'CAD', 0.00005, 20181006, 20181006233930),
	('USD/JPY', 0, 'USD', '', 0, 1, 'JPY', 0.005, 20180830, 20180830232730),
	('USD/JPY', 0, 'USD', '', 0, 1, 'JPY', 0.005, 20180930, 20180930174714),
	('USD/JPY', 0, 'USD', '', 0, 1, 'JPY', 0.005, 20181006, 20181006233930),
	('XINA50U18', 1, 'XINA50', 'SGX', 20180927, 1, 'USD', 2.5, 20180830, 20180830232731),
	('XINA50U18', 1, 'XINA50', 'SGX', 20180927, 1, 'USD', 2.5, 20180930, 20180930174715),
	('XINA50U18', 1, 'XINA50', 'SGX', 20180927, 1, 'USD', 2.5, 20181006, 20181006233931);
/*!40000 ALTER TABLE `_dbpub_inst_ids` ENABLE KEYS */;
/*!40101 SET SQL_MODE=IFNULL(@OLD_SQL_MODE, '') */;
/*!40014 SET FOREIGN_KEY_CHECKS=IF(@OLD_FOREIGN_KEY_CHECKS IS NULL, 1, @OLD_FOREIGN_KEY_CHECKS) */;
/*!40101 SET CHARACTER_SET_CLIENT=@OLD_CHARACTER_SET_CLIENT */;
