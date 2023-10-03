package com.ngontro86.server.dboe.engine


import com.ngontro86.common.annotations.Logging
import com.ngontro86.common.annotations.DBOEComponent
import org.apache.logging.log4j.Logger

import javax.annotation.PostConstruct
import java.util.concurrent.ConcurrentHashMap
import java.util.concurrent.atomic.AtomicInteger

import static com.ngontro86.common.times.GlobalTimeController.currentTimeMillis

@DBOEComponent
class OrderSequencer {

    @Logging
    private Logger logger

    private Map<String, AtomicInteger> orderSeqCache = [:] as ConcurrentHashMap<String, AtomicInteger>

    String getOrderSequence(String walletId) {
        orderSeqCache.putIfAbsent(walletId, new AtomicInteger(currentTimeMillis.toInteger()))
        def nextOrderSeq = orderSeqCache.get(walletId).incrementAndGet()
        return "${nextOrderSeq}"
    }

    @PostConstruct
    private void init() {
        logger.info("Order Sequencer initiated...")
    }
}
