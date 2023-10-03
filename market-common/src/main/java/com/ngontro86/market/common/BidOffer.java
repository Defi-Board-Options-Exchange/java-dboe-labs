package com.ngontro86.market.common;

public enum BidOffer {
    BID,
    OFFER;

    public static BidOffer side(int signed) {
        return signed < 0 ? BID : OFFER;
    }
}
