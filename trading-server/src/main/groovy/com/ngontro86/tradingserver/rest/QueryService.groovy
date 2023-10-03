package com.ngontro86.tradingserver.rest

import com.ngontro86.app.common.db.FlatDao
import com.ngontro86.cep.CepEngine
import com.ngontro86.common.annotations.ConfigValue
import com.ngontro86.common.annotations.Logging
import com.ngontro86.common.annotations.NonTxTransactional
import com.ngontro86.common.annotations.DBOEComponent
import com.ngontro86.common.biz.entity.SimpleOrder
import com.ngontro86.utils.ResourcesUtils
import org.apache.log4j.Logger

import javax.inject.Inject

import static com.ngontro86.utils.GlobalTimeUtils.getTimeFormat
import static com.ngontro86.utils.RoundUtils.roundToNearestStep
import static com.ngontro86.utils.StringUtils.isEmpty
import static java.lang.System.currentTimeMillis

@DBOEComponent
class QueryService {

    @Logging
    private Logger logger

    @ConfigValue
    private Integer cutOffDate = 20181115

    @Inject
    private CepEngine cep

    @Inject
    @NonTxTransactional
    private FlatDao flatDao

    @ConfigValue
    private String logFolder = "/home/ngontro86/logs"

    private String queryStrategies = ResourcesUtils.content("queries/loadStrat.sql")

    @ConfigValue
    private Integer backendErrorCount = 15

    Collection<Map> query(String query) {
        logger.info("${query}")
        return cep.queryMap(query)
    }

    Collection<Map> pnl() {
        cep.queryMap("select portfolio, slice, sum(val) as value from PLWin group by portfolio, slice")
    }

    Collection<Map> position() {
        cep.queryMap("select portfolio, inst_id, sum(size) as size from PortfolioWin group by portfolio, inst_id")
    }

    Collection<Map> strategy() {
        cep.queryMap("select * from PairConfigWin")
    }

    Collection<Map> pastPerf() {
        def perfs = flatDao.queryList("select date, concat(inst_id,'-', portfolio) as strategy, sum(pnl - fee) as pnl from daily_pnl where date >= ${cutOffDate} group by date, concat(inst_id,'-', portfolio)")
        logger.info("Num of past perf records: ${perfs.size()}")
        return perfs
    }

    void updateSetting(Map newSettings) {
        logger.info("Settings: ${newSettings}")
        println("Settings: ${newSettings}")
    }

    Map<String, Collection> allInstruments() {
        [
                'FX'         : ['EUR/USD': false, 'JPY/USD': true, 'CAD/USD': true],
                'Securities' : ['VNM': false, 'FB': true, 'GOOG': true, 'BABA': true],
                'Derivatives': ['SIMSCI Index': false, 'HSI Index': true, 'HSCEI Index': true, 'NKY Index': false]
        ]
    }

    Map getSettings() {
        [
                'fxRate'                  : [
                        'SGD': 1.35,
                        'JPY': 101.0,
                        'EUR': 0.82,
                        'CAD': 1.35,
                        'GBP': 0.67
                ],
                'simulation'              : true,
                'PnL Email'               : 'truongvinh.vu@gmail.com',
                'PnL Freq sec'            : 300,
                'rolling future ahead LTD': 3
        ]
    }

    Collection<Map> signals() {
        [
                [
                        'ref_signal'  : 'Pair MAVG',
                        'inst_id'     : 'HI1 Index',
                        'order_req_id': 'Pair_HI1_MAVG_1',
                        'ref_price'   : 0.5d,
                        'timestamp'   : 2018060514302500
                ],
                [
                        'ref_signal'  : 'Pair MAVG',
                        'inst_id'     : 'QZ1 Index',
                        'order_req_id': 'Pair_QZ1_MAVG_1',
                        'ref_price'   : 0.25d,
                        'timestamp'   : 2018060514302500
                ],
                [
                        'ref_signal'  : 'Pair MAVG',
                        'inst_id'     : 'VIX1 Index',
                        'order_req_id': 'Pair_VIX1_MAVG_1',
                        'ref_price'   : 0.75d,
                        'timestamp'   : 2018060514302500
                ],
        ]
    }

    void updateInstruments(Map<String, Collection> instruments) {
        logger.info("Instrument to subscribe: ${instruments}")
        println("Instrument to subscribe: ${instruments}")
    }

    void updateStrategies(Collection<Map> maps) {
        logger.info(maps)
    }

    Collection<String> readFile(String folder, String file, int numOfLine, String containStr) {
        def ret = [] as Queue
        new File("${isEmpty(folder)? logFolder : folder}/${file}").withReader { r ->
            r.eachLine { line ->
                if (line.contains(containStr)) {
                    ret.offer(line)
                    if (ret.size() > numOfLine) {
                        ret.poll()
                    }
                }
            }
        }
        return ret
    }

    Collection<String> readServerErrors() {
        def ret = []
        [
                "ib_${getTimeFormat(currentTimeMillis(), 'yyyyMMdd')}.txt",
                "server_${getTimeFormat(currentTimeMillis(), 'yyyyMMdd')}.txt"
        ].each { file ->
            ret << file.startsWith("ib") ? "IB ERROR:" : "SERVER ERROR:"
            ret << readFile('', file, backendErrorCount, "ERROR")
        }
        return ret
    }

    Collection<Map> marketSnapshot() {
        cep.queryMap("select * from PriceWin")
    }

    Collection<Map> oneMarketSnapshot(String instId) {
        def obs = cep.queryMap("select p.inst_id as inst_id, p.bid as bid, p.ask as ask, p.bid_size as bid_size, p.ask_size as ask_size, i.tick_size as tick_size from PriceWin(inst_id='${instId}') p inner join InstWin i on p.inst_id = i.inst_id")
        if (!obs.isEmpty()) {
            def ret = []
            def obSnapshot = obs.first()
            def bid = obSnapshot['bid'], ask = obSnapshot['ask'], tickSize = obSnapshot['tick_size']
            def bidSize = obSnapshot['bid_size'], askSize = obSnapshot['ask_size']
            def midpoint = roundToNearestStep(0.5 * (bid + ask), tickSize)
            50.times {
                ret << [
                        'price'  : midpoint + tickSize * it,
                        'size'   : (midpoint + tickSize * it) == ask ? askSize : 0,
                        'bid_ask': 'ask'
                ]
                ret << [
                        'price'  : midpoint - tickSize * it,
                        'size'   : (midpoint - tickSize * it) == bid ? bidSize : 0,
                        'bid_ask': 'bid'
                ]
            }
            return ret
        }
        return Collections.emptyList()
    }

    static Collection<Map> pendingOrders = [
            [
                    'portfolio'      : 'Global Portfolio',
                    'inst_id'        : 'Cash Basket',
                    'qty'            : 1,
                    'price'          : 355.55,
                    'exchangeOrderId': 'Order1'
            ],
            [
                    'portfolio'      : 'Global Portfolio',
                    'inst_id'        : 'SGPF21',
                    'qty'            : -1,
                    'price'          : 356.50,
                    'exchangeOrderId': 'Order2'
            ],
    ]

    void submitOrder(SimpleOrder order) {
        pendingOrders << [
                'portfolio': 'Global Portfolio',
                'inst_id'  : order.instId,
                'qty'      : order.qty,
                'price'    : order.price,
                'exchangeOrderId' : order.exchangeOrderId
        ]
    }

    Collection<Map> pendingOrders() {
        return pendingOrders
    }
}
