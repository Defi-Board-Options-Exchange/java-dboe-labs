package com.ngontro86.common.biz.entity;

import com.ngontro86.common.serials.ObjMap;

import java.util.Map;

import static com.ngontro86.common.times.GlobalTimeController.getCurrentTimeMillis;

public abstract class AbstractStratReq {

    public final static int ENTRY = 1, EXIT = -1;

    public String strategyName;

    public String refSignal;

    public String instId, reqId;

    public String broker, account, portfolio;

    public double qty;

    //\_/\_/\_/\_/ Transient properties\_/\_/\_/\_/\_/\_/\_/\_/\_/
    public transient boolean isEntryBuy;
    public transient boolean mkObs;
    public transient boolean entryOrderSent, entryOrderAcked, entryOrderFilled, exitOrderSent, exitOrderAcked, exitOrderFilled;

    public transient double slidingExit;

    public transient ObjMap entryOrder, exitOrder;
    //\_/\_/\_/\_/\_/\_/\_/\_/\_/\_/\_/\_/\_/\_/\_/\_/\_/\_/\_/\_/

    protected ObjMap getOrder(boolean isEntry) {
        ObjMap ret = new ObjMap("OrderReqEvent");

        ret.put("inst_id", this.instId);
        ret.put("exchange_order_id", null);

        ret.put("broker", this.broker);
        ret.put("account", this.account);
        ret.put("portfolio", this.portfolio);

        ret.put("timestamp", getCurrentTimeMillis());

        return ret;
    }

    public synchronized void setPrice(double price, int side) {
        if (side == ENTRY) {
            entryOrder.put("price", price);
        } else if (side == EXIT) {
            exitOrder.put("price", price);
        }
    }

    public void copyTransient(AbstractStratReq prevReq) {

        this.isEntryBuy = prevReq.isEntryBuy;
        this.mkObs = prevReq.mkObs;
        this.entryOrderSent = prevReq.entryOrderSent;
        this.entryOrderAcked = prevReq.entryOrderAcked;
        this.entryOrderFilled = prevReq.entryOrderFilled;
        this.exitOrderSent = prevReq.exitOrderSent;
        this.exitOrderAcked = prevReq.exitOrderAcked;
        this.exitOrderFilled = prevReq.exitOrderFilled;

        this.slidingExit = prevReq.slidingExit;

        for (String key : new String[]{"exchange_order_id"}) {
            this.entryOrder.put(key, prevReq.entryOrder.get(key));
            this.exitOrder.put(key, prevReq.exitOrder.get(key));
        }
    }

    public boolean isCancelOrder() {
        return qty == 0.0;
    }

    public synchronized void setExchangeOrderId(String exchangeOrderId, int side) {
        if (side == ENTRY) {
            this.entryOrder.put("exchange_order_id", exchangeOrderId);
            entryOrderAcked = true;
        } else if (side == EXIT) {
            this.exitOrder.put("exchange_order_id", exchangeOrderId);
            exitOrderAcked = true;
        }
    }

    public String getStrategyName() {
        return strategyName;
    }

    public void setStrategyName(String strategyName) {
        this.strategyName = strategyName;
    }

    public String getRefSignal() {
        return refSignal;
    }

    public void setRefSignal(String refSignal) {
        this.refSignal = refSignal;
    }

    public String getInstId() {
        return instId;
    }

    public void setInstId(String instId) {
        this.instId = instId;
    }

    public String getReqId() {
        return reqId;
    }

    public void setReqId(String reqId) {
        this.reqId = reqId;
    }

    public String getBroker() {
        return broker;
    }

    public void setBroker(String broker) {
        this.broker = broker;
    }

    public String getAccount() {
        return account;
    }

    public void setAccount(String account) {
        this.account = account;
    }

    public String getPortfolio() {
        return portfolio;
    }

    public void setPortfolio(String portfolio) {
        this.portfolio = portfolio;
    }

    public double getQty() {
        return qty;
    }

    public void setQty(double qty) {
        this.qty = qty;
    }
}
