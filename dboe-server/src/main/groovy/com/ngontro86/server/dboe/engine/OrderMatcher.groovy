package com.ngontro86.server.dboe.engine

import com.ngontro86.cep.CepEngine
import com.ngontro86.common.Handler
import com.ngontro86.common.annotations.Logging
import com.ngontro86.common.annotations.DBOEComponent
import com.ngontro86.common.serials.ObjMap
import com.ngontro86.obmatcher.Order
import com.ngontro86.obmatcher.OrderBook
import com.ngontro86.utils.Utils
import org.apache.logging.log4j.Logger

import javax.annotation.PostConstruct
import javax.inject.Inject
import java.util.concurrent.BlockingQueue
import java.util.concurrent.ConcurrentHashMap
import java.util.concurrent.LinkedBlockingQueue

import static com.ngontro86.utils.ResourcesUtils.content

@DBOEComponent
class OrderMatcher implements Runnable {
    @Logging
    private Logger logger

    @Inject
    private CepEngine cep

    private String orderToMatchQ = content("epl-query/order-to-match.sql")
    private String orderToExpireQ = content("epl-query/order-to-expire.sql")

    private Map<String, OrderBook> obByInstrId = [:] as ConcurrentHashMap<String, OrderBook>

    private BlockingQueue q = new LinkedBlockingQueue()

    private volatile int numOfOrders = 0, numOfCxl = 0, numberOfMatch = 0

    @PostConstruct
    private void init() {
        logger.info("Order Matcher initiated...")
        Utils.startThread(this, Thread.MAX_PRIORITY)
        logger.info("Start query to match order...")
        cep.registerMapHandler(orderToMatchQ, new Handler<Map<String, Object>>() {
            @Override
            boolean handle(Map<String, Object> obj) {
                q.put(obj)
                return true
            }
        })
    }

    boolean matching(Map<String, Object> orderMap) {
        logger.info("NumOfOrder: ${numOfOrders}, ${numOfCxl}, order: ${orderMap}")
        def order = Order.fromMap(orderMap)
        if (order.qty == 0) {
            numOfCxl++
        }
        def instrId = orderMap['instr_id']
        obByInstrId.putIfAbsent(instrId, new OrderBook(instrId))
        def matches = obByInstrId.get(instrId).matchThenAdd(order)
        matches.collect { new ObjMap('DboeMatchEvent', it.toMap()) }.each {
            cep.accept(it)
            logger.info("NumOfMatch: ${numberOfMatch}, Match: ${it}")
            numberOfMatch++
        }
        numOfOrders++
        return true
    }

    @Override
    void run() {
        while (true) {
            try {
                matching(q.take())
            } catch (Exception e) {
                throw e
            }
        }
    }
}
