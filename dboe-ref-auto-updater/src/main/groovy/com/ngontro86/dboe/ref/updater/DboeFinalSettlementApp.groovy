package com.ngontro86.dboe.ref.updater

import com.ngontro86.app.common.db.FlatDao
import com.ngontro86.common.annotations.ConfigValue
import com.ngontro86.common.annotations.EntryPoint
import com.ngontro86.common.annotations.Logging
import com.ngontro86.common.annotations.NonTxTransactional
import com.ngontro86.common.annotations.Web3jReadWrite
import com.ngontro86.dboe.web3j.DBOEOptionFactory
import com.ngontro86.dboe.web3j.Utils
import com.ngontro86.utils.GlobalTimeUtils
import com.ngontro86.utils.ResourcesUtils
import org.apache.logging.log4j.Logger
import org.web3j.protocol.Web3j
import org.web3j.tx.RawTransactionManager
import org.web3j.tx.gas.ContractGasProvider

import javax.inject.Inject

import static com.ngontro86.dboe.ref.updater.Utils.getTimeUtc
import static com.ngontro86.utils.GlobalTimeUtils.getTimeFormat
import static java.lang.System.currentTimeMillis

class DboeFinalSettlementApp {

    @Logging
    private Logger logger

    @Inject
    @NonTxTransactional
    private FlatDao flatDao

    @ConfigValue(config = "chain")
    private String chain

    @ConfigValue(config = "asOf")
    private Integer asOf = getTimeFormat(currentTimeMillis(), 'yyyyMMdd')

    @Inject
    @Web3jReadWrite
    private Web3j web3j

    @Inject
    private ContractGasProvider gasProvider

    @Inject
    @Web3jReadWrite
    private RawTransactionManager txnManager

    private String query = ResourcesUtils.content("queries/expiring.sql")

    @EntryPoint
    void run() {
        try {
            def records = flatDao.queryList(String.format(query, chain, asOf))
            records.findAll { getTimeUtc(it['expiry'], it['ltt']) <= System.currentTimeMillis() }.each { rec ->
                def optionFactory = DBOEOptionFactory.load(rec['option_factory_address'], web3j, txnManager, gasProvider)
                println "Final settling for underlying: ${rec['underlying']}, expiry: ${rec['expiry']}"
                optionFactory.finalSettle(Utils.padding(32, rec['underlying'] as byte[]), rec['expiry'] as BigInteger).send()

                def fsp = optionFactory.underlyingPrice(Utils.padding(32, rec['underlying'] as byte[]), rec['expiry'] as BigInteger).send()
                flatDao.persist('dboe_fsp', [
                        [
                                'chain'                 : chain,
                                'underlying'            : rec['underlying'],
                                'expiry'                : rec['expiry'],
                                'option_factory_address': rec['option_factory_address'],
                                'fsp'                   : fsp,
                                'in_timestamp'          : getTimeFormat(System.currentTimeMillis(), 'yyyyMMddHHmmss')
                        ]
                ])
            }
            println "Done"
            System.exit(0)
        } catch (Exception e) {
            logger.error(e)
        }
    }

}
