package com.ngontro86.dboe.token

import com.ngontro86.app.common.db.FlatDao
import com.ngontro86.common.annotations.ConfigValue
import com.ngontro86.common.annotations.EntryPoint
import com.ngontro86.common.annotations.Logging
import com.ngontro86.common.annotations.NonTxTransactional
import org.apache.logging.log4j.Logger

import javax.inject.Inject

class TokenDistributorApp {

    @Logging
    private Logger logger

    @Inject
    @NonTxTransactional
    private FlatDao flatDao

    @ConfigValue(config = "unprocessedReqQuery")
    private String unprocessedReqQuery

    @ConfigValue(config = "tokenAddresses")
    private Collection tokenAddresses

    @Inject
    private ERC20TokenManager tokenManager

    @ConfigValue(config = "nativeToken")
    private String nativeToken = 'ZBC'

    @EntryPoint
    void go() {
        println("~~~~~~~ DboeTokenDistributorApp starting ~~~~~~~")
        def tokens = tokenAddresses.collect { it.split(":")[0] } as Set
        tokens.add(nativeToken)
        def requests = flatDao.queryList(unprocessedReqQuery)
        println "Found: ${requests.size()} pending requests..."
        requests.each { req ->
            processOneRequest(tokens, req)
            flatDao.updateQuery("update dboe_token_request set processed=1 where wallet_id='${req['wallet_id']}' and req_id='${req['req_id']}'")
        }
        Thread.sleep(1000)
        println "Exit now"
        System.exit(0)
    }

    private void processOneRequest(Set<String> tokens, Map req) {
        try {
            tokens.each { token ->
                tokenManager.transfer(token, req['wallet_id'])
            }
        } catch (Exception e) {
            logger.error(e)
        }
    }
}
