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

-- Dumping structure for table ngontro86.config
DROP TABLE IF EXISTS `config`;
CREATE TABLE IF NOT EXISTS `config` (
  `config` varchar(256) NOT NULL DEFAULT '',
  `value` varchar(1024) DEFAULT NULL,
  PRIMARY KEY (`config`)
) ENGINE=InnoDB DEFAULT CHARSET=latin1;

-- Dumping data for table ngontro86.config: ~57 rows (approximately)
/*!40000 ALTER TABLE `config` DISABLE KEYS */;
INSERT INTO `config` (`config`, `value`) VALUES
	('Algo.accounts', 'U8819390'),
	('Algo.portfolios', 'Global FX,Global lt EUR,Global mt EUR'),
	('Algo.strategies', 'EUR-lt,EUR-mt'),
	('Algo.subscription', 'localhost:7771'),
	('Crawler.VnStocks.Download.Path', 'C:\\Users\\TruongVinh\\workspace\\releases\\datafeed'),
	('Crawler.VnStocks.Stats.Args', 'minAnnualRet:0.12,maxAnnualRet:0.45,maxDailyRetShock:0.5,minTotalAsset:100000,maxTotalAsset:750000,minPE:5,maxPE:12'),
	('Crawler.VnStocks.Stats.Path', 'C:\\Users\\TruongVinh\\Dropbox\\QuantVu86\\vn stats'),
	('DBPub.PROD.queries', 'DB:_dbpub_trades|@snapshot,subscribe|@Name(\'DBTrades\') select * from TradeWin;\r\nDB:_dbpub_inst|@snapshot,subscribe|@Name(\'DBInst\') select * from InstWin;\r\nCSV:/home/ngontro86/subs/sigs|@subscribe|@Name(\'Sig\') select * from BollingerPriceWin;'),
	('DBPub.PROD.subscriptionObj', 'localhost:7771'),
	('Emailer.props', 'smtp.gmail.com;587;quantvu86@gmail.com;VuKhanhLam)%)(2013'),
	('Execution.PROD.InstWatcher.queries', '@Name(\'Verifier_Inst\') select * from InstWin;select * from InstWin '),
	('Execution.PROD.InstWatcher.subscriptionObj', 'localhost:7771'),
	('Execution.PROD.OrderMapWatcher.queries', '@Name(\'ETOMap\') select * from OrderMapWin'),
	('Execution.PROD.OrderMapWatcher.subscriptionObj', 'localhost:7771'),
	('Execution.PROD.PriceMAWatcher.queries', '@Name(\'ETPriceMA\') select * from PriceMAWin'),
	('Execution.PROD.PriceMAWatcher.subscriptionObj', 'localhost:7771'),
	('Execution.PROD.PriceWatcher.queries', '@Name(\'ETPrice\') select * from PriceWin'),
	('Execution.PROD.PriceWatcher.subscriptionObj', 'localhost:7771'),
	('Execution.PROD.SignalWatcher.queries', '@Name(\'ETSignal\') select * from SignalWin'),
	('Execution.PROD.SignalWatcher.subscriptionObj', 'localhost:7771'),
	('Execution.PROD.subscriptionObj', 'localhost:7771'),
	('Execution.PROD.TradeWatcher.queries', '@Name(\'ETTrades\') select * from TradeWin'),
	('Execution.PROD.TradeWatcher.subscriptionObj', 'localhost:7771'),
	('IB.PROD.host', 'localhost'),
	('IB.PROD.port', '4001'),
	('IB.PROD.query', '@Name(\'IBOrder\') select * from OrderReqVerifiedEvent'),
	('IB.PROD.subscription', 'localhost:7771'),
	('IB.SIM.host', 'localhost'),
	('IB.SIM.port', '4001'),
	('IB.SIM.query', ''),
	('IB.SIM.subscription', 'localhost:7771'),
	('PL.emails', 'info@connectingdots.vn,quantvu86@gmail.com'),
	('Research.CopyQ', '/home/ngontro86/copyq'),
	('Research.HLs', 'EUR-st,EUR-mt,EUR-lt'),
	('Research.MA.Destination', '/home/ngontro86/Documents/mavg/mavg_'),
	('Research.MA.WriteFile', '0'),
	('Research.Radar.D', '-50,-40,-30,-20,-10,0,10,20,30,40,50'),
	('Research.Radar.Imbalance', '-25,0,25'),
	('Research.Radar.MaxLoss', '0'),
	('Research.Radar.MinD', '0'),
	('Research.Result.Act', '1'),
	('Research.Result.Email', 'info@connectingdots.vn'),
	('Research.Stats.Destination', '/home/ngontro86/Documents/stats/stats_'),
	('Research.Stats.WriteFile', '1'),
	('Research.TimeSurfaces', '0'),
	('Server.BACKTEST.restPort', '0'),
	('Server.BACKTEST.restURL', 'http://localhost/'),
	('Server.BACKTEST.staticTablesLoad', 'config;'),
	('Server.PROD.copyq', '/home/ngontro86/copyq'),
	('Server.PROD.copyQObjects', 'IBPriceEvent;IBSizeEvent;'),
	('Server.PROD.copyqPrefix', 'copyq'),
	('Server.PROD.copyqTime', '900'),
	('Server.PROD.objPort', '7771'),
	('Server.PROD.restPort', '7777'),
	('Server.PROD.restURL', 'http://localhost/'),
	('Server.PROD.staticTablesLoad', 'config;serv_fx_inst;serv_fut_inst;'),
	('Verifier.PROD.InstWatcher.queries', '@Name(\'Verifier_Inst\') select * from InstWin;select * from InstWin '),
	('Verifier.PROD.InstWatcher.subscriptionObj', 'localhost:7771'),
	('Verifier.PROD.PriceWatcher.queries', '@Name(\'Verifier_Price\') select * from PriceWin;select * from PriceWin'),
	('Verifier.PROD.PriceWatcher.subscriptionObj', 'localhost:7771'),
	('Verifier.PROD.subscriptionObj', 'localhost:7771');
/*!40000 ALTER TABLE `config` ENABLE KEYS */;
/*!40101 SET SQL_MODE=IFNULL(@OLD_SQL_MODE, '') */;
/*!40014 SET FOREIGN_KEY_CHECKS=IF(@OLD_FOREIGN_KEY_CHECKS IS NULL, 1, @OLD_FOREIGN_KEY_CHECKS) */;
/*!40101 SET CHARACTER_SET_CLIENT=@OLD_CHARACTER_SET_CLIENT */;
