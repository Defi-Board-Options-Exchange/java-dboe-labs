package com.ngontro86.algo.research

import com.ngontro86.market.common.pca.PCA
import org.junit.Ignore
import org.junit.Test

import static com.ngontro86.algo.research.ReadCsvUtils.readCsv
import static com.ngontro86.market.common.NormalizeMethod.Z_SCORE
import static com.ngontro86.market.common.pca.PairExecutionUtils.executeInPairConcept
import static com.ngontro86.utils.GlobalTimeUtils.dates
import static com.ngontro86.utils.GlobalTimeUtils.samplingTimes
import static com.ngontro86.utils.TransformUtils.samplingAsOfTimestamp

@Ignore
class PairBreakoutResearcher {

    File researchOutput
    Map<Double, Map<Double, Map<Double, Collection<OneTrade>>>> trades = [:]

    void init(Map config) {
        researchOutput = new File(config['researchOutput'])
        researchOutput << "Date, Time, Max2Pc, MaxLoss, EntrySignal, Side, Qty, Price, Comm, Reason, Notional,Month\n"
        config['maxLosses'].each { maxLoss ->
            config['max2Pcs'].each { max2Pc ->
                config['entrySignals'].each { eb ->
                    trades.get(maxLoss, [:]).get(max2Pc, [:]).put(eb, [] as Collection<OneTrade>)
                }
            }
        }
    }

    @Test
    void "experiment big HSI and HSCEI"() {
        def config = [
                'startResearchTime': 92000,
                'endResearchTime'  : 120000,
                'startTradingTime' : 130000,
                'endTradingTime'   : 160000,
                'max2Pcs'          : [20d, 25d, 30d, 35d],
                'startingDate'     : 20170103,
                'endingDate'       : 20170609,
                'maxLosses'        : [1000d, 1500d, 2000d, 2500d],
                'minSizes'         : [1, 1],
                'multipliers'      : [50d, 50d],
                'comm'             : 112d,
                'entrySignals'     : [1500d, 2000d, 2500d, 3000d],
                'researchOutput'   : 'C:\\Users\\TruongVinh\\Dropbox\\QuantVu86\\prod\\researches\\HSI_HSCEI_breakout_112_ML.csv'
        ]
        init(config)
        def lines = readCsv('pair/hscei-hsi.csv')
        dates(config['startingDate'], config['endingDate'], 1).each { date ->
            oneDay(config, date, lines, ['HSI', 'HSCEI'])
        }
        printOutTrades()
    }

    PCA researchOneDay(Map config, int date, Collection<Map> lines, Collection factors) {
        def researchTimeSnapshots = samplingTimes(date, config['startResearchTime'], config['endResearchTime'], 1)
        def pca = new PCA(factors: factors, normalizeMethod: Z_SCORE, window: 1000).init()
        def selectedLines = lines.findAll {
            it['datetime'] >= researchTimeSnapshots.first() && it['datetime'] <= researchTimeSnapshots.last()
        }
        def samplingData = samplingAsOfTimestamp(selectedLines, 'id', 'datetime', 'price', researchTimeSnapshots)
        if (samplingData.size() == 0) {
            return null
        }

        samplingData.each { timestamp, dataMap ->
            pca.addData(dataMap)
        }
        pca.computeBasis(2)
        return pca
    }

    void tradingOneDay(Map config, PCA pca, int date, Collection<Map> lines, Collection factors) {
        double[] multipliers = config['multipliers'] as double[]

        def tradingTimeSnapshots = samplingTimes(date, config['startTradingTime'], config['endTradingTime'], 1)
        double[] tradingSizeAndBasis = executeInPairConcept(
                pca.normalizeMethod,
                pca.getBasisVector(1),
                pca.mean,
                pca.stdevs,
                config['minSizes'] as int[],
                multipliers
        )
        println "$date - ${tradingSizeAndBasis}"
        def selectedLines = lines.findAll {
            it['datetime'] >= tradingTimeSnapshots.first() && it['datetime'] <= tradingTimeSnapshots.last()
        }
        def tradingData = samplingAsOfTimestamp(selectedLines, 'id', 'datetime', 'price', tradingTimeSnapshots)

        long prevTimestamp = 0l
        double prevB = 0d
        tradingData.each { timestamp, tradingDataMap ->
            def b = tradingSizeAndBasis[0] * multipliers[0] * tradingDataMap[factors.first()] + tradingSizeAndBasis[1] * multipliers[1] * tradingDataMap[factors.last()] - tradingSizeAndBasis[2]
            config['maxLosses'].each { maxLoss ->
                config['max2Pcs'].each { max2Pc ->
                    if (pca.pcaWeights[1] > max2Pc) {
                        tradingOneTimestamp(config, maxLoss, max2Pc, date, timestamp, prevTimestamp, trades, b, prevB)
                    }
                }
            }

            prevTimestamp = timestamp
            prevB = b
        }

        config['maxLosses'].each { maxLoss ->
            config['max2Pcs'].each { max2Pc ->
                if (pca.pcaWeights[1] <= max2Pc) {
                    tradingOneTimestamp(config, maxLoss, max2Pc, date + 1, prevTimestamp, prevTimestamp, trades, prevB, prevB, true)
                }
            }
        }
    }


    void printOutTrades() {
        trades.each { maxLoss, traderMaxLossMap ->
            traderMaxLossMap.each { max2Pc, traderMap ->
                traderMap.each { eb, exTrades ->
                    researchOutput << toCsv(max2Pc, maxLoss, eb, exTrades)
                }
            }
        }
    }

    String toCsv(double max2Pc, double maxLoss, double eb, Collection<OneTrade> tradeList) {
        def sb = new StringBuilder()
        tradeList.each { trade ->
            sb.append("${trade.date}, ${trade.timestamp}, ${max2Pc}, ${maxLoss}, ${eb}, ${trade.side}, ${trade.qty}, ${trade.price}, ${trade.comm}, ${trade.reason}, ${-trade.qty * trade.price - trade.comm}, ${(int) (trade.date / 100)}\n".toString())
        }
        return sb.toString()
    }

    void tradingOneTimestamp(Map config, double maxLoss, double max2Pc, int date, long timestamp, long prevTimestamp, Map<Double, Map<Double, Collection>> trades, double b, double prevB, boolean closeAll = false) {
        trades.get(maxLoss).get(max2Pc).each { eb, tradePerStrategy ->
            OneTrade lastTrade = tradePerStrategy.isEmpty() ? null : tradePerStrategy.last()
            if (lastTrade != null && lastTrade.side == SIDE.ENTRY && lastTrade.date < date) {
                tradePerStrategy << new OneTrade(
                        date: lastTrade.date,
                        timestamp: prevTimestamp,
                        side: SIDE.EXIT,
                        qty: -lastTrade.qty,
                        price: prevB,
                        comm: config['comm'],
                        reason: 'EOD Exit'
                )
                lastTrade = tradePerStrategy.last()
            }
            if (!closeAll) {
                if (lastTrade == null || (lastTrade.side == SIDE.EXIT && (lastTrade.date != date || !lastTrade.reason.equals('StopLoss')))) {
                    if (b >= eb) {
                        tradePerStrategy << new OneTrade(
                                date: date,
                                timestamp: timestamp,
                                side: SIDE.ENTRY,
                                qty: 1,
                                price: b,
                                comm: config['comm'],
                                reason: 'Buy'
                        )
                    } else if (b <= -eb) {
                        tradePerStrategy << new OneTrade(
                                date: date,
                                timestamp: timestamp,
                                side: SIDE.ENTRY,
                                qty: -1,
                                price: b,
                                comm: config['comm'],
                                reason: 'Sell'
                        )
                    }
                }

                if (lastTrade != null && lastTrade.side == SIDE.ENTRY) {
                    double loss = lastTrade.qty * (lastTrade.price - b)
                    if (loss >= maxLoss) {
                        tradePerStrategy << new OneTrade(
                                date: date,
                                timestamp: timestamp,
                                side: SIDE.EXIT,
                                qty: -lastTrade.qty,
                                price: b,
                                comm: config['comm'],
                                reason: 'StopLoss'
                        )
                    }
                }
            }
        }

    }


    void oneDay(Map config, int date, Collection<Map> lines, Collection factors) {
        def pca = researchOneDay(config, date, lines, factors)
        if (pca == null) {
            return
        }
        tradingOneDay(config, pca, date, lines, factors)
    }

    @Test
    void "should read csv properly"() {
        def lines = readCsv('pair/hscei-hsi.csv')
        assert lines.size() == 74860
        assert lines[0] == ['datetime': 20170103092100, 'id': 'HSCEI', 'price': 9382.26]
    }

}
