package com.ngontro86.dboe.ref.updater

import com.ngontro86.common.annotations.ConfigValue
import com.ngontro86.common.annotations.EntryPoint
import com.ngontro86.common.annotations.Logging
import com.ngontro86.dboe.web3j.DBOEClob
import org.apache.logging.log4j.Logger
import org.web3j.protocol.Web3j
import org.web3j.tx.RawTransactionManager
import org.web3j.tx.gas.ContractGasProvider

import javax.inject.Inject

import static com.ngontro86.dboe.ref.updater.Utils.getTimeUtc
import static com.ngontro86.restful.common.client.RestClientBuilder.build

class DboeRefUpdaterApp {

    @Logging
    private Logger logger

    @ConfigValue(config = "chain")
    private String chain = 'AVAX'

    @ConfigValue(config = "oldExpiry")
    private Integer oldExpiry = 20230901

    @Inject
    private Web3j web3j

    @Inject
    private ContractGasProvider gasProvider

    @Inject
    private RawTransactionManager txnManager

    private Map<String, DBOEClob> dboeClobs = [:]

    @EntryPoint
    void start() {
        initClob()

        reCalcRefs()

        println "DONE, exiting ..."
        System.exit(0)
    }

    void reCalcRefs() {
        try {
            def options = getOptions()

            options.findAll { it['expiry'] == oldExpiry }
                    .collectEntries { [(it['instr_id']): it['ob_address']] }
                    .each { instrId, clobAddr ->
                        if (dboeClobs.containsKey(clobAddr)) {
                            def clob = dboeClobs.get(clobAddr)
                            println "Refreshing: ${instrId}..."
                            clob.refreshRefPx(com.ngontro86.dboe.web3j.Utils.padding(32, instrId as byte[])).send()
                            println "${instrId} got new ref: ${clob.refPrices(com.ngontro86.dboe.web3j.Utils.padding(32, instrId as byte[])).send()}"
                        }
                    }

            def underlyingExpiryClobMap = [:]
            options.findAll { it['expiry'] != oldExpiry }.each {
                underlyingExpiryClobMap.putIfAbsent(it['underlying'], [:])
                underlyingExpiryClobMap.get(it['underlying']).putIfAbsent(it['expiry'], it['ob_address'])
            }
            underlyingExpiryClobMap.each { und, map ->
                map.each { exp, clobAddress ->
                    if (dboeClobs.containsKey(clobAddress)) {
                        def clob = dboeClobs.get(clobAddress)
                        println "Refreshing: Und: ${und}, Expiry: ${exp}..."
                        clob.refreshRefs(com.ngontro86.dboe.web3j.Utils.padding(32, und as byte[]), exp as BigInteger).send()
                        println "${und}, ${exp} got new refs..."
                    }
                }
            }
        } catch (Exception e) {
            logger.error(e)
        }
    }

    private void initClob() {
        getClobInfo().each {
            def clobAddress = it['ob_sc_address'] as String
            println "Initialising CLOB at address: ${clobAddress}"
            if (!dboeClobs.containsKey(clobAddress)) {
                dboeClobs.put(clobAddress, DBOEClob.load(clobAddress, web3j, txnManager, gasProvider))
            }
        }
    }

    private Collection<Map> getClobInfo() {
        build('dboeHost').withQueryParams('query/clobInfo', [:], Collection).findAll {
            it['chain'] == chain
        } as Collection<Map>
    }

    private Collection<Map> getOptions() {
        build('dboeHost')
                .withQueryParams('query/listInstrument', ['chain': chain], Collection)
                .findAll {
                    getTimeUtc(it['expiry'], it['ltt']) > System.currentTimeMillis()
                } as Collection<Map>
    }

}
