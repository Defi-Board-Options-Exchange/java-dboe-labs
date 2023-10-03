package com.ngontro86.common.biz.entity

import com.ngontro86.common.util.BeanUtils
import org.apache.commons.lang3.builder.ToStringBuilder


class PriceMA implements Comparable<PriceMA> {

    public String instId, hl;

    public double bid, ask, microPrice, openPrice, mpAvg, stdev;

    public double imbalance, imbAvg;

    public long timestamp;

    private PriceMA() {}

    static PriceMA build(Map<String, Object> map) {
        def priceMa = new PriceMA()
        BeanUtils.copyProperties(priceMa, map,
                [
                        'inst_id'    : 'instId',
                        'imbavg'     : 'imbAvg',
                        'micro_price': 'microPrice',
                        'open_price' : 'openPrice',
                        'mpavg'      : 'mpAvg'
                ])
        return priceMa
    }

    static String getKey(String instId, String hl) {
        return String.format("%s%s", instId, hl);
    }

    @Override
    int compareTo(PriceMA arg0) {
        return 1;
    }



    String getInstId() {
        return instId
    }

    void setInstId(String instId) {
        this.instId = instId
    }

    String getHl() {
        return hl
    }

    void setHl(String hl) {
        this.hl = hl
    }

    double getBid() {
        return bid
    }

    void setBid(double bid) {
        this.bid = bid
    }

    double getAsk() {
        return ask
    }

    void setAsk(double ask) {
        this.ask = ask
    }

    double getMicroPrice() {
        return microPrice
    }

    void setMicroPrice(double microPrice) {
        this.microPrice = microPrice
    }

    double getOpenPrice() {
        return openPrice
    }

    void setOpenPrice(double openPrice) {
        this.openPrice = openPrice
    }

    double getMpAvg() {
        return mpAvg
    }

    void setMpAvg(double mpAvg) {
        this.mpAvg = mpAvg
    }

    double getStdev() {
        return stdev
    }

    void setStdev(double stdev) {
        this.stdev = stdev
    }

    double getImbalance() {
        return imbalance
    }

    void setImbalance(double imbalance) {
        this.imbalance = imbalance
    }

    double getImbAvg() {
        return imbAvg
    }

    void setImbAvg(double imbAvg) {
        this.imbAvg = imbAvg
    }

    long getTimestamp() {
        return timestamp
    }

    void setTimestamp(long timestamp) {
        this.timestamp = timestamp
    }

    @Override
    String toString() {
        return new ToStringBuilder(this)
                .append("instId", instId)
                .append("hl", hl)
                .append("bid", bid)
                .append("ask", ask)
                .append("microPrice", microPrice)
                .append("openPrice", openPrice)
                .append("mpAvg", mpAvg)
                .append("stdev", stdev)
                .append("imbalance", imbalance)
                .append("imbAvg", imbAvg)
                .append("timestamp", timestamp)
                .toString();
    }
}
