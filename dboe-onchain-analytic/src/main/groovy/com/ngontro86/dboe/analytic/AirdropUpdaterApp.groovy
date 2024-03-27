package com.ngontro86.dboe.analytic

import com.ngontro86.app.common.db.FlatDao
import com.ngontro86.common.annotations.ConfigValue
import com.ngontro86.common.annotations.EntryPoint
import com.ngontro86.common.annotations.Logging
import com.ngontro86.common.annotations.NonTxTransactional
import com.ngontro86.common.net.SocketPublisher
import com.ngontro86.common.serials.ObjEvent
import com.ngontro86.common.serials.ObjMap
import com.ngontro86.utils.Utils
import org.apache.logging.log4j.Logger

import javax.inject.Inject

class AirdropUpdaterApp {

    @Logging
    private Logger logger

    @ConfigValue
    private String subscriptionUrl

    private SocketPublisher<ObjEvent, Object> servPub

    @Inject
    @NonTxTransactional
    private FlatDao flatDao

    @ConfigValue(config = "tables")
    private Collection<String> tables = ['dboe_wallet_airdrop', 'dboe_wallet_refer_stats']

    @EntryPoint
    void go() {
        logger.info("~~~~~~~ AirdropUpdaterApp starting ~~~~~~~")
        servPub = new SocketPublisher<>("AirdropUpdaterApp", subscriptionUrl.split(":")[0], Utils.toInt(subscriptionUrl.split(":")[1], 7771), null, false)
        tables.each { update(it) }
        println "Done..."
    }

    private void update(String table) {
        def records = flatDao.queryList("select * from ${table}")
        println "${table} got ${records.size()}"
        records.each {
            servPub.handle(new ObjMap("${table}_event", it))
        }
    }
}
