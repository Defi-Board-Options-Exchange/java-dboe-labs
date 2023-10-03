package com.ngontro86.common.biz.entity

import com.ngontro86.common.util.BeanUtils
import com.ngontro86.utils.Utils
import org.apache.commons.lang3.builder.ToStringBuilder


public class BollingerReq extends AbstractStratReq implements Comparable<BollingerReq> {
    private final static String SEPERATOR = ";";
    public final static int ENTRY = 1, EXIT = -1;

    public String hl;
    public double d, minD;
    public double imbalance;
    public int aggressive;
    public double maxLossBps;
    public int deleteAfter;
    public long timestamp;

    private BollingerReq() {}

    static BollingerReq build(Map map) {
        def bollingerReq = new BollingerReq()
        BeanUtils.copyProperties(bollingerReq, map, [
                'min_d'        : 'minD',
                'max_loss'     : 'maxLossBps',
                'delete_after' : 'deleteAfter',
                'strategy_name': 'strategyName',
                'ref_signal'   : 'refSignal',
                'req_id'       : 'reqId',
                'inst_id'      : 'instId'
        ])
        bollingerReq.entryOrder = bollingerReq.getOrder(true)
        bollingerReq.exitOrder = bollingerReq.getOrder(false)
        return bollingerReq
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
        if (tokens.length < 3) {
            return false;
        }
        return "B".equals(tokens[1]);
    }

    static String getReqId(String orderReqId) {
        String[] tokens = orderReqId.split(SEPERATOR);
        if (tokens.length < 3) {
            return null;
        }
        return Utils.toString(tokens[0], null);
    }

    static Integer getSide(String orderReqId) {
        String[] tokens = orderReqId.split(SEPERATOR);
        if (tokens.length < 3) {
            return null;
        }
        return Utils.toInt(tokens[2], 0);
    }

    static String getORI(String reqId, boolean isBuy, boolean isEntry) {
        return String.format("%s%s%s%s%d", reqId, SEPERATOR, isBuy ? "B" : "S", SEPERATOR, isEntry ? ENTRY : EXIT);
    }

    String getHl() {
        return hl
    }

    void setHl(String hl) {
        this.hl = hl
    }

    double getD() {
        return d
    }

    void setD(double d) {
        this.d = d
    }

    double getMinD() {
        return minD
    }

    void setMinD(double minD) {
        this.minD = minD
    }

    double getImbalance() {
        return imbalance
    }

    void setImbalance(double imbalance) {
        this.imbalance = imbalance
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

    long getTimestamp() {
        return timestamp
    }

    void setTimestamp(long timestamp) {
        this.timestamp = timestamp
    }

    @Override
    int compareTo(BollingerReq arg0) { return 1; }

    @Override
    String toString() {
        return new ToStringBuilder(this)
                .append("hl", hl)
                .append("d", d)
                .append("minD", minD)
                .append("imbalance", imbalance)
                .append("aggressive", aggressive)
                .append("maxLossBps", maxLossBps)
                .append("deleteAfter", deleteAfter)
                .append("timestamp", timestamp)
                .toString();
    }
}
