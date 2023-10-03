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

-- Dumping structure for table ngontro86.algo_strategies
DROP TABLE IF EXISTS `algo_strategies`;
CREATE TABLE IF NOT EXISTS `algo_strategies` (
  `strategy_name` varchar(128) NOT NULL DEFAULT '',
  `variable` varchar(128) NOT NULL DEFAULT '',
  `value` varchar(128) DEFAULT NULL,
  `strategy_type` int(10) DEFAULT NULL,
  PRIMARY KEY (`strategy_name`,`variable`)
) ENGINE=InnoDB DEFAULT CHARSET=latin1;

-- Dumping data for table ngontro86.algo_strategies: ~100 rows (approximately)
/*!40000 ALTER TABLE `algo_strategies` DISABLE KEYS */;
INSERT INTO `algo_strategies` (`strategy_name`, `variable`, `value`, `strategy_type`) VALUES
	('CAD-st', 'abs_max_loss', '75.0', 1),
	('CAD-st', 'account', 'DU145284', 1),
	('CAD-st', 'aggressive', '1', 1),
	('CAD-st', 'broker', 'IB', 1),
	('CAD-st', 'closeout_time', '153000', 1),
	('CAD-st', 'd', '1.54', 1),
	('CAD-st', 'delete_after', '5000', 1),
	('CAD-st', 'hl', 'st', 1),
	('CAD-st', 'inst_id', 'USD/CAD', 1),
	('CAD-st', 'max_loss', '15.0', 1),
	('CAD-st', 'min_d', '4.0', 1),
	('CAD-st', 'portfolio', 'Global FX', 1),
	('CAD-st', 'qty', '100000', 1),
	('CAD-st', 'running_time', '83000', 1),
	('EUR-lt', 'abs_max_loss', '75.0', 1),
	('EUR-lt', 'account', 'U8819390', 1),
	('EUR-lt', 'aggressive', '0', 1),
	('EUR-lt', 'broker', 'IB', 1),
	('EUR-lt', 'closeout_time', '235500', 1),
	('EUR-lt', 'd', '1.4', 1),
	('EUR-lt', 'delete_after', '5000', 1),
	('EUR-lt', 'hl', 'lt', 1),
	('EUR-lt', 'imbalance', '2.0', 1),
	('EUR-lt', 'inst_id', 'EUR/USD', 1),
	('EUR-lt', 'max_loss', '7.0', 1),
	('EUR-lt', 'min_d', '6.0', 1),
	('EUR-lt', 'portfolio', 'Global lt EUR', 1),
	('EUR-lt', 'qty', '100000', 1),
	('EUR-lt', 'running_time', '161500', 1),
	('EUR-mt', 'abs_max_loss', '75.0', 1),
	('EUR-mt', 'account', 'U8819390', 1),
	('EUR-mt', 'aggressive', '0', 1),
	('EUR-mt', 'broker', 'IB', 1),
	('EUR-mt', 'closeout_time', '235500', 1),
	('EUR-mt', 'd', '2.0', 1),
	('EUR-mt', 'delete_after', '5000', 1),
	('EUR-mt', 'hl', 'mt', 1),
	('EUR-mt', 'imbalance', '3.0', 1),
	('EUR-mt', 'inst_id', 'EUR/USD', 1),
	('EUR-mt', 'max_loss', '7.0', 1),
	('EUR-mt', 'min_d', '4.5', 1),
	('EUR-mt', 'portfolio', 'Global mt EUR', 1),
	('EUR-mt', 'qty', '100000', 1),
	('EUR-mt', 'running_time', '161000', 1),
	('EUR-st', 'abs_max_loss', '75.0', 1),
	('EUR-st', 'account', 'U1109880', 1),
	('EUR-st', 'aggressive', '1', 1),
	('EUR-st', 'broker', 'IB', 1),
	('EUR-st', 'closeout_time', '235500', 1),
	('EUR-st', 'd', '1.54', 1),
	('EUR-st', 'delete_after', '5000', 1),
	('EUR-st', 'hl', 'st', 1),
	('EUR-st', 'inst_id', 'EUR/USD', 1),
	('EUR-st', 'max_loss', '15.0', 1),
	('EUR-st', 'min_d', '6.0', 1),
	('EUR-st', 'portfolio', 'Global FX', 1),
	('EUR-st', 'qty', '100000', 1),
	('EUR-st', 'running_time', '161000', 1),
	('EUR-vlt', 'abs_max_loss', '75.0', 1),
	('EUR-vlt', 'account', 'U1109880', 1),
	('EUR-vlt', 'aggressive', '1', 1),
	('EUR-vlt', 'broker', 'IB', 1),
	('EUR-vlt', 'closeout_time', '235500', 1),
	('EUR-vlt', 'd', '1.92', 1),
	('EUR-vlt', 'delete_after', '5000', 1),
	('EUR-vlt', 'hl', 'vlt', 1),
	('EUR-vlt', 'inst_id', 'EUR/USD', 1),
	('EUR-vlt', 'max_loss', '15.0', 1),
	('EUR-vlt', 'min_d', '7.0', 1),
	('EUR-vlt', 'portfolio', 'Global FX', 1),
	('EUR-vlt', 'qty', '100000', 1),
	('EUR-vlt', 'running_time', '163000', 1),
	('JPY-mt', 'abs_max_loss', '200.0', 1),
	('JPY-mt', 'account', 'DU145284', 1),
	('JPY-mt', 'aggressive', '0', 1),
	('JPY-mt', 'broker', 'IB', 1),
	('JPY-mt', 'closeout_time', '153000', 1),
	('JPY-mt', 'd', '2.5', 1),
	('JPY-mt', 'delete_after', '5000', 1),
	('JPY-mt', 'hl', 'st', 1),
	('JPY-mt', 'inst_id', 'USD/JPY', 1),
	('JPY-mt', 'max_loss', '10.0', 1),
	('JPY-mt', 'min_d', '4.0', 1),
	('JPY-mt', 'portfolio', 'Global FX', 1),
	('JPY-mt', 'qty', '100000', 1),
	('JPY-mt', 'running_time', '83000', 1),
	('JPY-st', 'abs_max_loss', '75.0', 1),
	('JPY-st', 'account', 'U1109880', 1),
	('JPY-st', 'aggressive', '1', 1),
	('JPY-st', 'broker', 'IB', 1),
	('JPY-st', 'closeout_time', '155500', 1),
	('JPY-st', 'd', '1.54', 1),
	('JPY-st', 'delete_after', '5000', 1),
	('JPY-st', 'hl', 'st', 1),
	('JPY-st', 'inst_id', 'USD/JPY', 1),
	('JPY-st', 'max_loss', '15.0', 1),
	('JPY-st', 'min_d', '6.0', 1),
	('JPY-st', 'portfolio', 'Global FX', 1),
	('JPY-st', 'qty', '100000', 1),
	('JPY-st', 'running_time', '81500', 1);
/*!40000 ALTER TABLE `algo_strategies` ENABLE KEYS */;
/*!40101 SET SQL_MODE=IFNULL(@OLD_SQL_MODE, '') */;
/*!40014 SET FOREIGN_KEY_CHECKS=IF(@OLD_FOREIGN_KEY_CHECKS IS NULL, 1, @OLD_FOREIGN_KEY_CHECKS) */;
/*!40101 SET CHARACTER_SET_CLIENT=@OLD_CHARACTER_SET_CLIENT */;
