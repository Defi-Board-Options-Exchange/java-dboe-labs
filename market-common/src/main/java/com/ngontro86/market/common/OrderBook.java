package com.ngontro86.market.common;

public class OrderBook {

    private BidBook bidBook;

    private OfferBook offerBook;

    private long time;

    public BidBook getBidBook() {
        return bidBook;
    }

    public void setBidBook(BidBook bidBook) {
        this.bidBook = bidBook;
    }

    public OfferBook getOfferBook() {
        return offerBook;
    }

    public void setOfferBook(OfferBook offerBook) {
        this.offerBook = offerBook;
    }

    public long getTime() {
        return time;
    }

    public void setTime(long time) {
        this.time = time;
    }
}
