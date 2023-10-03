package com.ngontro86.common.biz.entity

import com.ngontro86.common.util.BeanUtils


class Instrument implements Comparable<Instrument> {

    public String instId
    public String underlying
    public double tickSize
    public String currency
    public long timestamp
    public int multiplier

    public String market
    public int expiryDate

    private Instrument() {}

    static Instrument build(Map map) {
        def inst = new Instrument()
        BeanUtils.copyProperties(inst, map,
                [
                        'inst_id'    : 'instId',
                        'tick_size'  : 'tickSize',
                        'expiry_date': 'expiryDate'
                ]
        )
        return inst
    }

    String getInstId() {
        return instId
    }

    void setInstId(String instId) {
        this.instId = instId
    }

    String getUnderlying() {
        return underlying
    }

    void setUnderlying(String underlying) {
        this.underlying = underlying
    }

    double getTickSize() {
        return tickSize
    }

    void setTickSize(double tickSize) {
        this.tickSize = tickSize
    }

    String getCurrency() {
        return currency
    }

    void setCurrency(String currency) {
        this.currency = currency
    }

    long getTimestamp() {
        return timestamp
    }

    void setTimestamp(long timestamp) {
        this.timestamp = timestamp
    }

    int getMultiplier() {
        return multiplier
    }

    void setMultiplier(int multiplier) {
        this.multiplier = multiplier
    }

    String getMarket() {
        return market
    }

    void setMarket(String market) {
        this.market = market
    }

    int getExpiryDate() {
        return expiryDate
    }

    void setExpiryDate(int expiryDate) {
        this.expiryDate = expiryDate
    }

    @Override
    int compareTo(Instrument o) {
        if (o == null) {
            return 1;
        }
        return this.instId.compareTo(o.instId);
    }

    @Override
    String toString() {
        return "Instrument{" +
                "instId='" + instId + '\'' +
                ", underlying='" + underlying + '\'' +
                ", tickSize=" + tickSize +
                ", currency='" + currency + '\'' +
                ", timestamp=" + timestamp +
                ", multiplier=" + multiplier +
                ", market='" + market + '\'' +
                ", expiryDate=" + expiryDate +
                '}';
    }
}

