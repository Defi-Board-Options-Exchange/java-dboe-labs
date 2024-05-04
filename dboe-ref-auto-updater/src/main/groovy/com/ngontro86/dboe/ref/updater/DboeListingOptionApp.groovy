package com.ngontro86.dboe.ref.updater

import com.ngontro86.app.common.db.FlatDao
import com.ngontro86.common.annotations.*
import com.ngontro86.common.times.GlobalTimeController
import com.ngontro86.dboe.web3j.DBOEOptionFactory
import com.ngontro86.restful.common.client.RestClient
import org.apache.logging.log4j.Logger
import org.web3j.protocol.Web3j
import org.web3j.tx.RawTransactionManager
import org.web3j.tx.gas.ContractGasProvider

import javax.annotation.PostConstruct
import javax.inject.Inject

import static com.ngontro86.restful.common.client.RestClientBuilder.build
import static com.ngontro86.utils.GlobalTimeUtils.getTimeFormat
import static java.lang.System.currentTimeMillis

class DboeListingOptionApp {

    @Logging
    private Logger logger

    @Inject
    @NonTxTransactional
    private FlatDao flatDao

    @ConfigValue(config = "chain")
    private String chain

    @ConfigValue(config = "template")
    private String template = 'Daily'

    @ConfigValue(config = "underlyings")
    private Collection underlyings = ['ETH']

    @ConfigValue(config = "listAheadInDay")
    private Integer listAheadInDay = 1

    @ConfigValue(config = "asOf")
    private Integer expiry = 0

    @ConfigValue(config = "underlyingSpots")
    private Collection underlyingSpots = ['ETH:2100.0']

    private RestClient restClient

    @Inject
    @Web3jReadWrite
    private RawTransactionManager rawTransactionManager

    @Inject
    @Web3jReadWrite
    private Web3j web3j

    @Inject
    private ContractGasProvider gasProvider

    private Map<String, Double> hardcodedSpots = [:]

    @PostConstruct
    private void init() {
        restClient = build('dboeHost')
        if (expiry == 0) {
            expiry = getTimeFormat(currentTimeMillis() + listAheadInDay * 24 * 3600 * 1000, 'yyyyMMdd')
        }

        hardcodedSpots = underlyingSpots.collectEntries {
            [(it.split(":")[0]): Double.valueOf(it.split(":")[1])]
        }
    }

    @EntryPoint
    void listOptions() {
        def instrIds = flatDao.queryStringList("select instr_id from dboe_listing_tmp where expiry=${expiry} and chain = '${chain}'") as Set<String>
        println "${instrIds.size()} Options with expiry = ${expiry} have been listed. ${instrIds}..."
        underlyings.each { und ->
            def spot = spot(und)
            def template = flatDao.queryList("select * from dboe_listing_template where template='${template}' and chain='${chain}' and underlying='${und}'").first()

            def optionFactory = DBOEOptionFactory.load(template['option_factory_address'], web3j, rawTransactionManager, gasProvider)
            [true, false].each { callPut ->
                def strikes = Utils.listStrikes(callPut, spot, template['strike_interval'], template['no_itm'], template['no_otm'], template['scale_up'])
                strikes.each { strike ->
                    def optionId = Utils.instrId(template['prefix'], template['strike_fmt'], callPut, (double) (strike / template['scale_up']), expiry)

                    if (!instrIds.contains(optionId)) {
                        listOneOption(optionId, strike, optionFactory, und, callPut, template)
                    } else {
                        println "Listed option: ${optionId} on ${chain}"
                    }
                }
            }

            println "Updating OB Divider so new Options will be picked up by back-end..."
            flatDao.persist('dboe_ob_address_divider', [
                    [
                            'underlying'   : und,
                            'expiry'       : expiry,
                            'ob_sc_address': template['ob_address'],
                            'active'       : 1,
                            'chain'        : chain
                    ]
            ])

        }

        println "Exiting now..."
        System.exit(0)
    }

    private void listOneOption(optionId, strike, DBOEOptionFactory optionFactory, String und, boolean callPut, template) {
        try {
            println "Listing ${optionId}, ${strike}, ${expiry}"
            optionFactory.createDBOEOption(
                    optionId, optionId,
                    com.ngontro86.dboe.web3j.Utils.optionSpecs(
                            [
                                    "instr_id"   : optionId,
                                    "underlying" : und,
                                    "expiry"     : expiry,
                                    "isCall"     : callPut,
                                    "isLong"     : true,
                                    "multiplier" : 1,
                                    "ltt"        : template['ltt_gmt'],
                                    "lttUtc"     : Utils.getTimeUtc(expiry, template['ltt_gmt']) / 1000L,
                                    "cond_strike": strike + (callPut ? 1 : -1) * template['collateral'] * template['scale_up'],
                                    "strike"     : strike,
                                    "currency"   : template['currency'],
                                    "price_scale": template['scale_up']
                            ]
                    )
            ).send()
            println "Done listing ${optionId}..."
            flatDao.persist('dboe_listing_tmp', [
                    [
                            'expiry'   : expiry,
                            'instr_id' : optionId,
                            'chain'    : chain,
                            'timestamp': GlobalTimeController.getCurrentTimeMillis()
                    ]
            ])
        } catch (Exception e) {
            println e
        }
    }

    private double spot(String und) {
        def spots = restClient.withQueryParams("query/query", ['query': "select * from DboeSpotWin(underlying='${und}')"], Collection) as Collection<Map>
        if (spots.isEmpty()) {
            logger.error("Spot for ${und} is not available in CEP. Taking from config? Pausing for 10sec...")
            sleep(10000)
            return hardcodedSpots[und]
        }
        return spots.first()['spot']
    }

}
