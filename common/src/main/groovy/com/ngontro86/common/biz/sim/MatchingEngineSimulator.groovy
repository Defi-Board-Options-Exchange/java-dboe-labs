package com.ngontro86.common.biz.sim

import com.ngontro86.common.Handler
import com.ngontro86.common.Observer
import com.ngontro86.common.annotations.ConfigValue
import com.ngontro86.common.annotations.Logging
import com.ngontro86.common.annotations.DBOEComponent
import com.ngontro86.common.biz.entity.Price
import com.ngontro86.common.biz.watcher.InstrumentWatcher
import com.ngontro86.common.biz.watcher.PriceWatcher
import com.ngontro86.common.net.SocketData
import com.ngontro86.common.net.SocketPublisher
import com.ngontro86.common.serials.ObjEvent
import com.ngontro86.common.serials.ObjMap
import com.ngontro86.common.util.IOUtils
import com.ngontro86.utils.Utils
import org.apache.logging.log4j.Logger

import javax.annotation.PostConstruct
import javax.inject.Inject

import static com.ngontro86.common.config.Configuration.config
import static com.ngontro86.common.times.GlobalTimeController.getCurrentTimeMillis

@DBOEComponent
class MatchingEngineSimulator implements Handler<SocketData<ObjEvent>>, Observer<String, Price> {

    @Logging
    private Logger logger

    @Inject
    private PriceWatcher priceWatcher

    @Inject
    private InstrumentWatcher instWatcher

    @ConfigValue(config = "probGetLifted")
    private Double probabilityGetLifted = 0.05

    @ConfigValue(config = "probGetSwept")
    private Double probabilityGetSwept = 0.25

    @ConfigValue(config = "probPartialFilled")
    private Double probabilityPartialFilled = 0.5

    private SocketPublisher pub

    private Map<String, Map<String, Map>> orderCache = [:]
    private Map<String, String> orderStatusCache = [:]

    @PostConstruct
    private void init() {
        def subscriptionObj = config().getConfig("Sim.subscriptionObj")
        def subTokens = subscriptionObj.split(":")
        pub = new SocketPublisher<>("Sim_Publisher", subTokens[0], Utils.toInt(subTokens[1], 7771), this, false)
        IOUtils.requestObjectSubscription("select * from OrderReqVerifiedEvent", "OrderReq", pub)
        priceWatcher.setGlobalObserver(this)
    }

    @Override
    synchronized boolean handle(SocketData<ObjEvent> obj) {
        def order = obj.data.event
        def newOrder = order['exchange_order_id'] == null
        def cancelOrder = order['qty'] == 0 && order['exchange_order_id'] != null
        logger.info("${newOrder ? 'NEW' : 'AMEND'}: ${order}")
        def exchangeOrderId = newOrder ? UUID.randomUUID().toString() : order['exchange_order_id']
        if (!newOrder) {
            if (!orderCache.getOrDefault(order['inst_id'], [:]).containsKey(exchangeOrderId)) {
                logger.warn("Order got filled or purged. Order: ${order['order_req_id']}")
                return true
            }
            if (cancelOrder) {
                logger.info("CANCELLING Order: ${order['order_req_id']}, order: ${order}")
                orderCache.remove(order['exchange_order_id'])
            }
        }
        if (newOrder && orderStatusCache.containsKey(order['order_req_id'])) {
            logger.warn("DUPE? Order: ${order['order_req_id']}")
        }
        if (newOrder) {
            def ordMap = new ObjMap("IBOrderMapEvent")
            ["inst_id", "order_req_id", "broker", "account", "portfolio", "timestamp"].each {
                ordMap.put(it, order[it])
            }
            ordMap.put("exchange_order_id", exchangeOrderId)
            ordMap.put("side", order['qty'] > 0 ? 1 : -1)
            order['exchange_order_id'] = exchangeOrderId
            pub.handle(ordMap)
            orderStatusCache[order['order_req_id']] = 'Received'
        }
        orderCache.putIfAbsent(order['inst_id'], [:])
        orderCache.get(order['inst_id']).put(exchangeOrderId, order)
        def currPrice = priceWatcher.get(order['inst_id'])
        if (currPrice != null) {
            def matched = match(currPrice, null, exchangeOrderId, order)
            if (matched) {
                orderCache.get(order['inst_id']).remove(exchangeOrderId)
            }
        }
        return true
    }

    private boolean match(Price price, Price prevPrice, String exchangeOrderId, Map order) {
        def tickSize = instWatcher.get(price.instId).tickSize
        def sweepingBidDown = prevPrice == null ? false : (price.bid < prevPrice.bid)
        def sweepingOfferUp = prevPrice == null ? false : (price.ask > prevPrice.ask)
        def matched = order['qty'] > 0 ? (order['price'] >= price.ask) : (order['price'] <= price.bid)
        def swept = ((Math.random() < probabilityGetSwept) && (order['qty'] > 0 ? (order['price'] >= (price.ask - tickSize) && sweepingBidDown) : (order['price'] <= (price.bid + tickSize) && sweepingOfferUp)))
        def getHit = ((Math.random() < probabilityGetLifted) && (order['qty'] > 0 ? (order['price'] >= (price.ask - tickSize)) : (order['price'] <= (price.bid + tickSize))))

        if (matched || getHit || swept) {
            def partialFilled = orderStatusCache[order['order_req_id']] != 'Submitted' && Math.random() < probabilityPartialFilled && Math.abs(order['qty']) > 1
            def fillMap = [
                    'order_id' : exchangeOrderId,
                    'status'   : partialFilled ? 'Submitted' : 'Filled',
                    'filled'   : (int) ((partialFilled ? 0.5 : 1.0) * Math.abs(order['qty'])),
                    'remaining': Math.abs(order['qty']) - (int) ((partialFilled ? 0.5 : 1.0) * Math.abs(order['qty'])),
                    'avg_price': order['price'],
                    'timestamp': getCurrentTimeMillis()
            ]
            logger.info("One Fill: {status:${fillMap['status']},orderReqId:${order['order_req_id']},qty:${fillMap['filled']},ask:${price.ask},price:${order['price']},bid:${price.bid},instId:${order['inst_id']},${matched},${swept},${getHit}} published...")
            pub.handle(new ObjMap('IBTradeEvent', fillMap))
            if (partialFilled) {
                orderStatusCache[order['order_req_id']] = 'Submitted'
                return false
            }
            orderStatusCache.remove(order['order_req_id'])
            return true
        }
        return false
    }

    @Override
    synchronized void update(String instId, Object newObj, Object oldObj) {
        def price = (Price) newObj
        def prevPrice = (Price) oldObj
        if (orderCache.containsKey(instId)) {
            def it = orderCache.get(instId).entrySet().iterator()
            while (it.hasNext()) {
                def entry = (Map.Entry<String, Map>) it.next()
                def matched = match(price, prevPrice, entry.key, entry.value)
                if (matched) {
                    logger.info("Purged order: ${entry.value['order_req_id']}")
                    it.remove()
                }
            }
        }
    }
}
