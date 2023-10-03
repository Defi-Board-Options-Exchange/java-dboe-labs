package com.ngontro86.common.biz.entity

import com.ngontro86.common.serials.ObjMap
import com.ngontro86.common.util.BeanUtils
import org.apache.commons.lang3.builder.ToStringBuilder

import static com.ngontro86.common.biz.entity.OrderStatus.FILLED
import static com.ngontro86.common.times.GlobalTimeController.getCurrentTimeMillis
import static com.ngontro86.utils.PairUtils.*
import static com.ngontro86.utils.StringUtils.isNotEmpty

class PairReq {
    String strategyName
    String pairName
    String broker
    String account
    String portfolio

    String pairOrderId

    String firstLegInstId
    double firstLegMultiplier
    String secondLegPrimaryInstId
    double secondLegPrimaryMultiplier
    String secondLegSecondaryInstId
    double secondLegSecondaryMultiplier

    double qty
    double secondLegQty
    double basis
    double refBasis
    double exitBasis
    double maxLoss
    long timestamp

    private transient boolean startedExit
    private transient Map<Boolean, Map<String, OrderStatus>> orderStatuses = [:]
    private transient Map<Boolean, Map<String, ObjMap>> prebuiltOrderMap = [:]
    private transient ObjMap pairReqDelete

    private static boolean ENTRY = true, EXIT = false

    private PairReq() {}

    static PairReq build(Map map) {
        def req = new PairReq()
        BeanUtils.copyProperties(req, map)
        return req.constructOrders()
    }

    private PairReq constructOrders() {
        def secondLegQties = optimalLots(secondLegQty,[secondLegPrimaryMultiplier, secondLegSecondaryMultiplier].findAll {it > 0})
        def qties = [qty, secondLegQties].flatten()

        [firstLegInstId,secondLegPrimaryInstId,secondLegSecondaryInstId].findAll {isNotEmpty(it)}.eachWithIndex { instId, idx ->
            def qty = qties.get(idx)
            if (qty != 0) {
                [ENTRY, EXIT].each { entry ->
                    def orderReqId = getOrderReqId(pairOrderId, entry, idx)
                    prebuiltOrderMap.get(entry, [:])[orderReqId] = new ObjMap('OrderReqEvent',
                            [
                                    'inst_id'     : instId,
                                    'order_req_id': orderReqId,
                                    'qty'         : (entry ? 1 : -1) * qty,
                                    'broker'      : broker,
                                    'account'     : account,
                                    'portfolio'   : portfolio,
                                    'timestamp'   : getCurrentTimeMillis()
                            ])
                }
            }
        }

        orderStatuses[ENTRY] = [:]
        orderStatuses[EXIT] = [:]
        pairReqDelete = new ObjMap('PairReqDelete', ['pairOrderId': pairOrderId])
        return this
    }


    boolean entryComplete() {
        return !orderStatuses[ENTRY].isEmpty() &&
                orderStatuses[ENTRY].values().findAll { it != FILLED }.isEmpty()
    }

    boolean exitComplete() {
        return !orderStatuses[EXIT].isEmpty() &&
                orderStatuses[EXIT].values().findAll { it != FILLED }.isEmpty()
    }

    OrderStatus getOrderStatus(String orderReqId) {
        return orderStatuses[isEntry(orderReqId)].get(orderReqId)
    }

    OrderStatus setOrderStatus(String orderReqId, OrderStatus stt) {
        return orderStatuses[isEntry(orderReqId)].put(orderReqId, stt)
    }

    void setExchangeOrderId(String exchangeOrderId, String orderReqId) {
        prebuiltOrderMap[isEntry(orderReqId)][orderReqId]['exchange_order_id'] = exchangeOrderId
    }

    Map<Boolean, Map<String, ObjMap>> getPrebuiltOrderMap() {
        return prebuiltOrderMap
    }

    ObjMap getPairReqDelete() {
        return pairReqDelete
    }

    double getExitBasis() {
        return exitBasis
    }

    void setExitBasis(double exitBasis) {
        this.exitBasis = exitBasis
    }

    double getFirstLegMultiplier() {
        return firstLegMultiplier
    }

    void setFirstLegMultiplier(double firstLegMultiplier) {
        this.firstLegMultiplier = firstLegMultiplier
    }

    double getSecondLegPrimaryMultiplier() {
        return secondLegPrimaryMultiplier
    }

    void setSecondLegPrimaryMultiplier(double secondLegPrimaryMultiplier) {
        this.secondLegPrimaryMultiplier = secondLegPrimaryMultiplier
    }

    double getSecondLegSecondaryMultiplier() {
        return secondLegSecondaryMultiplier
    }

    void setSecondLegSecondaryMultiplier(double secondLegSecondaryMultiplier) {
        this.secondLegSecondaryMultiplier = secondLegSecondaryMultiplier
    }

    boolean getStartedExit() {
        return startedExit
    }

    void setStartedExit(boolean startedExit) {
        this.startedExit = startedExit
    }

    double getMaxLoss() {
        return maxLoss
    }

    void setMaxLoss(double maxLoss) {
        this.maxLoss = maxLoss
    }

    @Override
    String toString() {
        return new ToStringBuilder(this)
                .append("strategyName", strategyName)
                .append("pairName", pairName)
                .append("broker", broker)
                .append("account", account)
                .append("portfolio", portfolio)
                .append("pairOrderId", pairOrderId)
                .append("firstLegInstId", firstLegInstId)
                .append("firstLegMultiplier", firstLegMultiplier)
                .append("secondLegPrimaryInstId", secondLegPrimaryInstId)
                .append("secondLegPrimaryMultiplier", secondLegPrimaryMultiplier)
                .append("secondLegSecondaryInstId", secondLegSecondaryInstId)
                .append("secondLegSecondaryMultiplier", secondLegSecondaryMultiplier)
                .append("qty", qty)
                .append("secondLegQty", secondLegQty)
                .append("basis", basis)
                .append("refBasis", refBasis)
                .append("exitBasis", exitBasis)
                .append("maxLoss", maxLoss)
                .append("timestamp", timestamp)
                .toString()
    }
}
