package com.ngontro86.common.biz.entity

import com.ngontro86.common.util.BeanUtils
import com.ngontro86.utils.Utils
import org.apache.commons.lang3.builder.ToStringBuilder


class TrendReq extends AbstractStratReq implements Comparable<TrendReq> {
    private final static String SEPERATOR = ";";

    public double imbVlt, imbLt, imbMt, imbSt;

    public int aggressive;

    public double maxLossBps;

    public int deleteAfter;

    public double exitCoeff;

    public long timestamp;

    private TrendReq() {}

    static TrendReq build(Map<String, ? extends Object> map) {
        def rq = new TrendReq()
        BeanUtils.copyProperties(rq, map,
                [
                        'imb_vlt'      : 'imbVlt',
                        'imb_lt'       : 'imbLt',
                        'imb_mt'       : 'imbMt',
                        'imb_st'       : 'imbSt',
                        'max_loss'     : 'maxLossBps',
                        'delete_after' : 'deleteAfter',
                        'exit_coeff'   : 'exitCoeff',
                        'strategy_name': 'strategyName',
                        'ref_signal'   : 'refSignal',
                        'req_id'       : 'reqId',
                        'inst_id'      : 'instId'
                ])
        rq.entryOrder = rq.getOrder(true)
        rq.exitOrder = rq.getOrder(false)
        return rq
    }

    void setEntrySide(boolean isBuy) {
        this.isEntryBuy = isBuy;
        this.entryOrder.put("order_req_id", getORI(reqId, isBuy, true));
        this.entryOrder.put("qty", isBuy ? qty : -qty);

        this.exitOrder.put("order_req_id", getORI(reqId, !isBuy, false));
        this.exitOrder.put("qty", isBuy ? -qty : qty);
    }

    static boolean isBuy(String orderReqId) {
        String[] tokens = orderReqId.split(SEPERATOR);
        if (tokens.length < 3) return false;
        return "B".equals(tokens[1]);
    }

    static String getReqId(String orderReqId) {
        String[] tokens = orderReqId.split(SEPERATOR);
        if (tokens.length < 3) return null;
        return Utils.toString(tokens[0], null);
    }

    static Integer getSide(String orderReqId) {
        String[] tokens = orderReqId.split(SEPERATOR);
        if (tokens.length < 3) return null;
        return Utils.toInt(tokens[2], 0);
    }

    double getImbVlt() {
        return imbVlt
    }

    void setImbVlt(double imbVlt) {
        this.imbVlt = imbVlt
    }

    double getImbLt() {
        return imbLt
    }

    void setImbLt(double imbLt) {
        this.imbLt = imbLt
    }

    double getImbMt() {
        return imbMt
    }

    void setImbMt(double imbMt) {
        this.imbMt = imbMt
    }

    double getImbSt() {
        return imbSt
    }

    void setImbSt(double imbSt) {
        this.imbSt = imbSt
    }

    int getAggressive() {
        return aggressive
    }

    void setAggressive(int aggressive) {
        this.aggressive = aggressive
    }

    double getMaxLossBps() {
        return maxLossBps
    }

    void setMaxLossBps(double maxLossBps) {
        this.maxLossBps = maxLossBps
    }

    int getDeleteAfter() {
        return deleteAfter
    }

    void setDeleteAfter(int deleteAfter) {
        this.deleteAfter = deleteAfter
    }

    double getExitCoeff() {
        return exitCoeff
    }

    void setExitCoeff(double exitCoeff) {
        this.exitCoeff = exitCoeff
    }

    long getTimestamp() {
        return timestamp
    }

    void setTimestamp(long timestamp) {
        this.timestamp = timestamp
    }

    static String getORI(String reqId, boolean isBuy, boolean isEntry) {
        return String.format("%s%s%s%s%d", reqId, SEPERATOR, isBuy ? "B" : "S", SEPERATOR, isEntry ? ENTRY : EXIT);
    }

    @Override
    int compareTo(TrendReq o) { return 1; }


    @Override
    String toString() {
        return new ToStringBuilder(this)
                .append("imbVlt", imbVlt)
                .append("imbLt", imbLt)
                .append("imbMt", imbMt)
                .append("imbSt", imbSt)
                .append("aggressive", aggressive)
                .append("maxLossBps", maxLossBps)
                .append("deleteAfter", deleteAfter)
                .append("exitCoeff", exitCoeff)
                .append("timestamp", timestamp)
                .toString();
    }
}
