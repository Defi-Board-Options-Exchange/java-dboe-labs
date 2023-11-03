package com.ngontro86.dboe.analytic

import com.ngontro86.app.common.db.FlatDao
import com.ngontro86.common.annotations.ConfigValue
import com.ngontro86.common.annotations.EntryPoint
import com.ngontro86.common.annotations.Logging
import com.ngontro86.common.annotations.NonTxTransactional
import com.ngontro86.dboe.analytic.rest.ChainbaseCloudQueryService
import com.ngontro86.dboe.analytic.rest.ChainbaseRpcApiQueryService
import com.ngontro86.utils.Utils
import org.apache.logging.log4j.Logger

import javax.inject.Inject
import java.util.concurrent.Executors
import java.util.concurrent.ScheduledExecutorService
import java.util.concurrent.TimeUnit

import static com.ngontro86.utils.ResourcesUtils.content

class DboeTransferExtractorApp {

    @Logging
    private Logger logger

    @ConfigValue(config = "loadTransferFreqSec")
    private Integer loadTransferFreqSec = 65

    @ConfigValue(config = "sqlQueryFile")
    private String sqlQueryFile = "query-txn-hash-1h.sql"

    @ConfigValue(config = "insertToTable")
    private String insertToTable = "staging_dboe_transfers"

    @NonTxTransactional
    @Inject
    private FlatDao flatDao

    @Inject
    private ChainbaseCloudQueryService chainbaseCQS

    @Inject
    private ChainbaseRpcApiQueryService chainbaseRpcQS

    @ConfigValue(config = "useRPC")
    private Boolean useRPC = true

    private final ScheduledExecutorService scheduler = Executors.newSingleThreadScheduledExecutor()

    @EntryPoint
    void start() {
        def query = content("queries/${sqlQueryFile}")
        println query
        scheduler.scheduleAtFixedRate(new Runnable() {
            @Override
            void run() {
                loadTransfers(query)
            }
        }, 10, loadTransferFreqSec, TimeUnit.SECONDS)

        while (true) {
            logger.info("Pausing for 100 ms ...")
            Utils.pause(100)
        }
    }

    private void loadTransfers(String query) {
        try {
            def txnHashes = flatDao.queryStringList(query)
            if (!txnHashes.isEmpty()) {
                println("${new Date()}: Found ${txnHashes.size()} transaction hashes without transfers...")
                def transfers = useRPC ? chainbaseRpcQS.query(txnHashes) : chainbaseCQS.query(txnHashes)
                println("${new Date()}: Persisting ${transfers.size()} into DB ...")
                flatDao.persist(insertToTable, transfers)
            }
        } catch (Exception e) {
            logger.error(e)
        }
    }
}
