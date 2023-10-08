package com.ngontro86.dboe.ref.updater

import com.ngontro86.app.common.db.FlatDao
import com.ngontro86.common.annotations.ConfigValue
import com.ngontro86.common.annotations.EntryPoint
import com.ngontro86.common.annotations.Logging
import com.ngontro86.common.annotations.NonTxTransactional
import com.ngontro86.common.times.GlobalTimeController
import com.ngontro86.dboe.web3j.DBOEOptionFactory
import com.ngontro86.dboe.web3j.TxnManagerProvider
import com.ngontro86.dboe.web3j.encryption.KeyHashUtils
import com.ngontro86.restful.common.client.RestClient
import org.apache.logging.log4j.Logger
import org.web3j.protocol.Web3j
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

    @ConfigValue(config = "underlyings")
    private Collection underlyings = ['ETH']

    @ConfigValue(config = "listAheadInDay")
    private Integer listAheadInDay = 1

    @ConfigValue(config = "asOf")
    private Integer expiry = 0

    private RestClient restClient

    @Inject
    private TxnManagerProvider txnManagerProvider

    @Inject
    private Web3j web3j

    @Inject
    private ContractGasProvider gasProvider

    @PostConstruct
    private void init() {
        restClient = build('dboeHost')
        if (expiry == 0) {
            expiry = getTimeFormat(currentTimeMillis() + listAheadInDay * 24 * 3600 * 1000, 'yyyyMMdd')
        }
    }

    @EntryPoint
    void listOptions() {
        def ops = flatDao.queryList("select signed_private_key, sign_key from dboe_key_admin.private_keys where wallet = 'Ops'").first()
        def opWallet = KeyHashUtils.unhashedKey(ops['signed_private_key'], ops['sign_key'])

        def instrIds = flatDao.queryStringList("select instr_id from dboe_listing_tmp where expiry=${expiry} and chain = '${chain}'") as Set<String>
        println "${instrIds.size()} Options with expiry = ${expiry} have been listed. ${instrIds}..."
        underlyings.each { und ->
            def spot = spot(und)
            def template = flatDao.queryList("select * from dboe_listing_template where chain='${chain}' and underlying='${und}'").first()

            def optionFactory = DBOEOptionFactory.load(template['option_factory_address'], web3j, txnManagerProvider.onDemandTxnManager(opWallet), gasProvider)
            [true, false].each { callPut ->
                def strikes = Utils.listStrikes(callPut, spot, template['strike_interval'], template['no_itm'], template['no_otm'], template['scale_up'])
                strikes.each { strike ->
                    def optionId = Utils.instrId(und, callPut, (int) (strike / template['scale_up']), expiry)

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
                                    "ltt"        : 150000,
                                    "lttUtc"     : Utils.getTimeUtc(expiry, 150000) / 1000L,
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
            throw new IllegalStateException("Spot for ${und} is not available...")
        }
        return spots.first()['spot']
    }

}
