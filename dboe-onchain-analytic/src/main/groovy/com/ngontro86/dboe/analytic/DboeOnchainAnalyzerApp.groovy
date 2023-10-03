package com.ngontro86.dboe.analytic


import com.ngontro86.common.annotations.ConfigValue
import com.ngontro86.common.annotations.EntryPoint
import com.ngontro86.common.annotations.Logging
import com.ngontro86.common.net.SocketPublisher
import com.ngontro86.common.serials.ObjEvent
import com.ngontro86.common.serials.ObjMap
import com.ngontro86.dboe.web3j.DBOEBaseOption
import com.ngontro86.dboe.web3j.DBOEClob
import com.ngontro86.dboe.web3j.DBOEOptionFactory
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

import static com.ngontro86.common.times.GlobalTimeController.currentTimeMillis
import static com.ngontro86.dboe.web3j.Utils.padding
import static com.ngontro86.restful.common.client.RestClientBuilder.build

class DboeOnchainAnalyzerApp {
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

    @Inject
    private SpotManager spotManager

    @ConfigValue(config = "chain")
    private String chain = "AVAX"

    @ConfigValue(config = "spotRefreshFreqSec")
    private Integer spotRefreshFreqSec = 5

    @ConfigValue(config = "priceOracleEnabled")
    private Boolean priceOracleEnabled = true

    @ConfigValue(config = "oiRefreshFreqSec")
    private Integer oiRefreshFreqSec = 15

    @ConfigValue(config = "obRefreshFreqSec")
    private Integer obRefreshFreqSec = 2

    private Map<String, DBOEClob> dboeClobs = [:]

    private Map<String, DBOEOptionFactory> optionFactories = [:]
    private Map<String, String> optionFactoryFspAddrMap = [:]

    private final ScheduledExecutorService scheduler = Executors.newScheduledThreadPool(5)

    @EntryPoint
    void go() {
        logger.info("~~~~~~~ DboeOnchainAnalyzerApp starting ~~~~~~~")
        servPub = new SocketPublisher<>("DBOEOnchainAnalyzerApp", subscriptionUrl.split(":")[0], Utils.toInt(subscriptionUrl.split(":")[1], 7771), null, false)
        init()

        updateOnchainClobSpecs()
        pullOptionInfo()
        pullObs()

        scheduler.scheduleAtFixedRate(new Runnable() {
            @Override
            void run() {
                init()
                pullOptionInfo()
            }
        }, 10, 10, TimeUnit.MINUTES)

        scheduler.scheduleAtFixedRate(new Runnable() {
            @Override
            void run() {
                pullObs()
            }
        }, 5, obRefreshFreqSec, TimeUnit.SECONDS)

        scheduler.scheduleAtFixedRate(new Runnable() {
            @Override
            void run() {
                updateOpenInterest()
            }
        }, 5, oiRefreshFreqSec, TimeUnit.SECONDS)

        scheduler.scheduleAtFixedRate(new Runnable() {
            @Override
            void run() {
                refreshSpots()
            }

        }, 5, spotRefreshFreqSec, TimeUnit.SECONDS)

        while (true) {
            logger.info("Pausing for 100 ms ...")
            Utils.pause(100)
        }
    }

    void init() {
        def clobs = getClobInfo()

        clobs.each {
            def clobAddress = it['ob_sc_address'] as String
            if (!dboeClobs.containsKey(clobAddress)) {
                dboeClobs.put(clobAddress, DBOEClob.load(clobAddress, web3j, txnManager, gasProvider))
            }
            def optionFactoryAddress = dboeClobs.get(clobAddress).optionFactory().send()
            def clearingHouseAddress = dboeClobs.get(clobAddress).clearingHouse().send()

            if (!optionFactories.containsKey(clobAddress)) {
                def optionFactory = DBOEOptionFactory.load(optionFactoryAddress, web3j, txnManager, gasProvider)
                optionFactories.put(clobAddress, optionFactory)
                optionFactoryFspAddrMap[optionFactoryAddress] = optionFactory.fspCalculator().send()
            }

            logger.info("On-chain CLOB at ${clobAddress}, Option factory: ${optionFactoryAddress}, clearing house: ${clearingHouseAddress}...")
            println("On-chain CLOB at ${clobAddress}, Option factory: ${optionFactoryAddress}, clearing house: ${clearingHouseAddress}...")

            servPub.handle(new ObjMap('DboeAddressDividerEvent',
                    [
                            'chain'                 : it['chain'],
                            'underlying'            : it['underlying'],
                            'expiry'                : it['expiry'],
                            'ob_address'            : clobAddress,
                            'clearing_address'      : clearingHouseAddress,
                            'fsp_address'           : optionFactoryFspAddrMap[optionFactoryAddress],
                            'option_factory_address': optionFactoryAddress
                    ]))
        }
    }

    private DBOEClob getClob(String clobAddress) {
        return dboeClobs.get(clobAddress)
    }

    private DBOEOptionFactory getOptionFactory(String clobAddress) {
        return optionFactories.get(clobAddress)
    }

    Collection<Map> getClobInfo() {
        build('dboeHost').withQueryParams('query/clobInfo', [:], Collection).findAll {
            it['chain'] == chain
        } as Collection<Map>
    }

    Collection<Map> getOptions() {
        build('dboeHost').withQueryParams('query/listInstrument', ['chain': chain], Collection) as Collection<Map>
    }

    void updateOnchainClobSpecs() {
        getClobInfo().each {
            def obAddress = it['ob_sc_address']

            def obClob = getClob(obAddress)
            [true, false].each { buySell ->
                def fixedSpreads = obClob.getFixedSpreads(buySell).send() as int[]
                fixedSpreads.eachWithIndex { spread, idx ->
                    servPub.handle(new ObjMap('DboeFixedSpreadEvent',
                            [
                                    'chain'           : chain,
                                    'ob_address'      : obAddress,
                                    'buy_sell'        : buySell ? 1 : 2,
                                    'price_level'     : idx,
                                    'fixed_spread_bps': spread
                            ]
                    ))
                }
            }

            def specs = obClob.getObSpecs().send()

            servPub.handle(new ObjMap('DboeOnchainClobSpecsEvent',
                    [
                            'chain'                      : chain,
                            'ob_address'                 : obAddress,
                            'num_px_level'               : specs.component1(),
                            'maker_fee_bps'              : specs.component2(),
                            'taker_fee_bps'              : specs.component3(),
                            'max_order_validity_sec'     : specs.component4(),
                            'min_ref_px_waiting_sec'     : specs.component5(),
                            'max_ref_px_waiting_sec'     : specs.component6(),
                            'min_lmt_order_notional'     : specs.component7(),
                            'ref_px_update_random_chance': specs.component8()
                    ]))
        }
    }

    void pullOptionInfo() {
        getClobInfo().each {
            def underlying = it['underlying']
            def expiry = it['expiry']
            def obAddress = it['ob_sc_address']
            def optionAddresses = getOptionFactory(obAddress).getOptions(padding(32, underlying as byte[]), expiry as BigInteger).send()
            def options = optionAddresses.collectEntries {
                [(it): DBOEBaseOption.load(it, web3j, txnManager, gasProvider)]
            } as Map<String, DBOEBaseOption>

            options.each { addr, option ->
                def specs = option.getOptionSpecs().send()

                servPub.handle(new ObjMap('DboeOnchainOptionEvent',
                        [
                                'chain'              : chain,
                                'instr_id'           : (option.name().send()),
                                'call_put'           : (specs.isCall ? 1 : 2),
                                'long_short'         : (specs.isLong ? 1 : 2),
                                'underlying'         : new String(specs.underlying).trim(),
                                'currency'           : new String(specs.currency).trim(),
                                'expiry'             : specs.expiry.toInteger(),
                                'ltt'                : specs.ltt.toInteger(),
                                'lttUtc'             : specs.lttUtc.toLong(),
                                'strike'             : specs.strike.toInteger(),
                                'cond_strike'        : specs.conditionalStrike.toInteger(),
                                'underlying_px_scale': specs.underlyingPxScale.toInteger(),
                                'strike'             : specs.strike.toInteger(),
                                'address'            : option.contractAddress
                        ]
                ))
            }
        }
    }

    void pullObs() {
        try {
            def options = getOptions()
            println "Found: ${options.size()} options On ${chain} ..."

            options.each { option ->
                def clobAddress = option['ob_address']
                def dboeClob = getClob(clobAddress)
                def twoSidedOb = [true, false].collectEntries { bs ->
                    [(bs): dboeClob.obDepth(padding(32, option['instr_id'] as byte[]), bs).send()]
                }
                def refPx = dboeClob.refPrices(padding(32, option['instr_id'] as byte[])).send()

                println "${option['instr_id']}, ref: ${refPx}..."

                twoSidedOb.each { bs, amounts ->
                    amounts.eachWithIndex { amt, idx ->
                        servPub.handle(new ObjMap('DboeOnchainOrderbookEvent',
                                [
                                        'chain'       : option['chain'],
                                        'instr_id'    : option['instr_id'],
                                        'currency'    : option['currency'],
                                        'ob_address'  : clobAddress,
                                        'underlying'  : option['underlying'],
                                        'expiry'      : option['expiry'],
                                        'buy_sell'    : (bs ? 1 : 2),
                                        'price_level' : idx,
                                        'ref_price'   : refPx,
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

    void refreshSpots() {
        if (!priceOracleEnabled) {
            return
        }
        try {
            def underlyings = getOptions().collect { it['underlying'] } as Set<String>
            def fspAddresses = getOptions().collectEntries { [(it['underlying']): it['fsp_address']] }
            println "Found ${underlyings.size()} distinct Underlyings, ${fspAddresses.size()} FSP Addresses on ${chain}"
            spotManager.setFspAddresses(fspAddresses)

            underlyings.each { underlying ->
                def spot = spotManager.getSpot(underlying, getTime(), 5)
                println "${underlying}, spot: ${spot}"
                servPub.handle(new ObjMap('DboeSpotEvent', [
                        'underlying'  : underlying,
                        'spot'        : spot,
                        'in_timestamp': getCurrentTimeMillis()
                ]))
            }
        } catch (Exception e) {
            logger.error(e)
        }
    }

    private long getTime() {
        return (long) (System.currentTimeMillis() / 1000 - 5)
    }

    void updateOpenInterest() {
        try {
            def options = getOptions()
            options.each {
                def longBalance = optionToken(it['long_contract_address']).totalSupply().send() / Math.pow(10, 18)
                def shortBalance = optionToken(it['short_contract_address']).totalSupply().send() / Math.pow(10, 18)

                if (longBalance != shortBalance) {
                    logger.error("Long and Short balance of Option ${it['instr_id']} are out of sync...")
                }

                servPub.handle(new ObjMap('DboeOpenInterestEvent', [
                        'instr_id'     : it['instr_id'],
                        'chain'        : chain,
                        'currency'     : it['currency'],
                        'open_interest': longBalance.toDouble()
                ]))
            }
        } catch (Exception e) {
            logger.error(e)
        }
    }

    private ERC20 optionToken(String address) {
        return ERC20.load(address, web3j, txnManager, gasProvider)
    }
}
