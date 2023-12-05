package com.ngontro86.dboe.analytic

import com.ngontro86.common.annotations.ConfigValue
import com.ngontro86.common.annotations.EntryPoint
import com.ngontro86.common.annotations.Logging
import com.ngontro86.common.net.SocketPublisher
import com.ngontro86.common.serials.ObjEvent
import com.ngontro86.common.serials.ObjMap
import com.ngontro86.dboe.web3j.DBOESpotDashboard
import com.ngontro86.dboe.web3j.DBOESpotMarket
import com.ngontro86.dboe.web3j.ERC20
import com.ngontro86.utils.Utils
import org.apache.logging.log4j.Logger
import org.web3j.protocol.Web3j
import org.web3j.tx.RawTransactionManager
import org.web3j.tx.gas.ContractGasProvider

import javax.inject.Inject
import java.util.concurrent.Executors
import java.util.concurrent.ScheduledExecutorService
import java.util.concurrent.TimeUnit

import static com.ngontro86.common.times.GlobalTimeController.getCurrentTimeMillis
import static com.ngontro86.restful.common.client.RestClientBuilder.build

class DboeSpotOnchainAnalyzerApp {

    @Logging
    private Logger logger

    @ConfigValue
    private String subscriptionUrl

    private SocketPublisher<ObjEvent, Object> servPub

    @Inject
    private Web3j web3j

    @Inject
    private ContractGasProvider gasProvider

    @Inject
    private RawTransactionManager txnManager

    @ConfigValue(config = "chain")
    private String chain = "AVAX"

    @ConfigValue(config = "obRefreshFreqSec")
    private Integer obRefreshFreqSec = 2

    private Map<String, DBOESpotDashboard> dboeSpotDashboards = [:]
    private Map<String, DBOESpotMarket> dboeSpotMarkets = [:]

    private final ScheduledExecutorService scheduler = Executors.newScheduledThreadPool(2)

    @EntryPoint
    void go() {
        logger.info("~~~~~~~ DboeSpotOnchainAnalyzerApp starting ~~~~~~~")
        servPub = new SocketPublisher<>("DBOESpotOnchainAnalyzerApp", subscriptionUrl.split(":")[0], Utils.toInt(subscriptionUrl.split(":")[1], 7771), null, false)
        init()
        pullObs()

        scheduler.scheduleAtFixedRate(new Runnable() {
            @Override
            void run() {
                init()
            }
        }, 10, 10, TimeUnit.MINUTES)

        scheduler.scheduleAtFixedRate(new Runnable() {
            @Override
            void run() {
                pullObs()
                updateMarketRefs()
            }
        }, 5, obRefreshFreqSec, TimeUnit.SECONDS)

        while (true) {
            logger.info("Pausing for 100 ms ...")
            Utils.pause(100)
        }
    }

    void init() {
        def dashboards = getSpotDashboard()

        dashboards.each {
            def dashboardAddress = it['dashboard_address'] as String
            if (!dboeSpotDashboards.containsKey(dashboardAddress)) {
                dboeSpotDashboards.put(dashboardAddress, DBOESpotDashboard.load(dashboardAddress, web3j, txnManager, gasProvider))
            }
            def spots = dboeSpotDashboards.get(dashboardAddress).dashboard().send()
            println "Dashboard:${dashboardAddress} got: ${spots.size()} Spot Market..."

            spots.each { spotMarketAddr ->
                if (!dboeSpotMarkets.containsKey(spotMarketAddr)) {
                    dboeSpotMarkets.put(spotMarketAddr, DBOESpotMarket.load(spotMarketAddr, web3j, txnManager, gasProvider))
                }
                def spotMarket = dboeSpotMarkets.get(spotMarketAddr)
                def quoteToken = spotMarket.quoteToken().send()
                def baseToken = spotMarket.baseToken().send()
                println "Found a pair: ${baseToken}/${quoteToken}"
                servPub.handle(new ObjMap('DboeSpotMarketEvent',
                        [
                                'chain'      : chain,
                                'quote_token': quoteToken,
                                'base_token' : baseToken,
                                'address'    : spotMarketAddr
                        ]))

                [quoteToken, baseToken].each { addr ->
                    def erc20 = ERC20.load(addr, web3j, txnManager, gasProvider)
                    servPub.handle(new ObjMap('DboeTokenDecimalEvent',
                            [
                                    'chain'         : chain,
                                    'token_address' : addr,
                                    'decimal_factor': (long) Math.pow(10, erc20.decimals().send())
                            ]))
                }

                [true, false].each { buySell ->
                    def fixedSpreads = spotMarket.getFixedSpreads(buySell).send() as int[]
                    fixedSpreads.eachWithIndex { spread, idx ->
                        servPub.handle(new ObjMap('DboeSpotFixedSpreadEvent',
                                [
                                        'chain'           : chain,
                                        'address'         : spotMarketAddr,
                                        'buy_sell'        : buySell ? 1 : 2,
                                        'price_level'     : idx,
                                        'fixed_spread_bps': spread
                                ]
                        ))
                    }
                }

                def specs = spotMarket.getObSpecs().send()

                servPub.handle(new ObjMap('DboeSpotClobSpecsEvent',
                        [
                                'chain'                 : chain,
                                'address'               : spotMarketAddr,
                                'num_px_level'          : specs.component1(),
                                'maker_fee_bps'         : specs.component2(),
                                'taker_fee_bps'         : specs.component3(),
                                'max_order_validity_sec': specs.component4(),
                                'min_lmt_order_notional': specs.component5()
                        ]))

            }

        }
    }

    Collection<Map> getSpotMarkets() {
        build('dboeHost').withQueryParams('spot/query/listMarkets', ['chain': chain], Collection) as Collection<Map>
    }

    Collection<Map> getSpotDashboard() {
        build('dboeHost').withQueryParams('spot/query/dashboard', ['chain': chain], Collection) as Collection<Map>
    }

    void updateMarketRefs() {
        try {
            def spotMarkets = getSpotMarkets()
            println "Found: ${spotMarkets.size()} Spot Markets On ${chain} ..."

            spotMarkets.each { market ->
                def spotMarket = dboeSpotMarkets.get(market['address'])

                def refPx = spotMarket.refInfo().send()
                println "${market['base_token']}/${market['quote_token']}, ref: ${refPx}..."

                servPub.handle(new ObjMap('DboeSpotRefEvent',
                        [
                                'chain'       : chain,
                                'address'     : market['address'],
                                'status'      : refPx.component1() ? 1 : 0,
                                'ref_price'   : refPx.component2(),
                                'px_scale'    : refPx.component3(),
                                'ref_time'    : refPx.component4(),
                                'in_timestamp': getCurrentTimeMillis()
                        ]))

            }
        } catch (Exception e) {
            logger.error(e)
        }
    }

    void pullObs() {
        try {
            def spotMarkets = getSpotMarkets()
            println "Found: ${spotMarkets.size()} Spot Markets On ${chain} ..."

            spotMarkets.each { market ->
                def spotMarket = dboeSpotMarkets.get(market['address'])
                def twoSidedOb = [true, false].collectEntries { bs ->
                    [(bs): spotMarket.obDepth(bs).send()]
                }

                twoSidedOb.each { bs, amounts ->
                    amounts.eachWithIndex { amt, idx ->
                        servPub.handle(new ObjMap('DboeSpotQuoteEvent',
                                [
                                        'chain'       : chain,
                                        'address'     : market['address'],
                                        'buy_sell'    : (bs ? 1 : 2),
                                        'price_level' : idx,
                                        'amount'      : amt,
                                        'in_timestamp': getCurrentTimeMillis()
                                ]))
                    }
                }
            }
        } catch (Exception e) {
            logger.error(e)
        }
    }
}
