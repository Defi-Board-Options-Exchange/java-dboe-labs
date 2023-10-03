package com.ngontro86.common.biz.entity

import com.ngontro86.common.util.BeanUtils


public class StratReqStatus implements Comparable<StratReqStatus> {

    public String strategyName, strategyReqId, status
    public int leg
    public long timestamp

    private StratReqStatus() {}

    public static StratReqStatus build(Map map) {
        def req = new StratReqStatus()
        BeanUtils.copyProperties(req, map,
                [
                        'strategy_name'  : 'strategyName',
                        'strategy_req_id': 'strategyReqId'
                ])
        return req
    }

    @Override
    public int compareTo(StratReqStatus o) {
        return 1;
    }


    @Override
    public String toString() {
        return "StratReqStatus{" +
                "strategyName='" + strategyName + '\'' +
                ", strategyReqId='" + strategyReqId + '\'' +
                ", status='" + status + '\'' +
                ", leg=" + leg +
                ", timestamp=" + timestamp +
                '}';
    }

    String getStrategyName() {
        return strategyName
    }

    void setStrategyName(String strategyName) {
        this.strategyName = strategyName
    }

    String getStrategyReqId() {
        return strategyReqId
    }

    void setStrategyReqId(String strategyReqId) {
        this.strategyReqId = strategyReqId
    }

    String getStatus() {
        return status
    }

    void setStatus(String status) {
        this.status = status
    }

    int getLeg() {
        return leg
    }

    void setLeg(int leg) {
        this.leg = leg
    }

    long getTimestamp() {
        return timestamp
    }

    void setTimestamp(long timestamp) {
        this.timestamp = timestamp
    }
}

