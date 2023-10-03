package com.ngontro86.market.common;

import java.util.ArrayList;
import java.util.Collection;

/**
 * Two ladders correspond to price and size
 */
public class BidOfferBook {

    private Collection<Double> priceLadder;
    private Collection<Integer> sizeLadder;

    protected BidOfferBook() {
        priceLadder = new ArrayList<>();
        sizeLadder = new ArrayList<>();
    }

    public final static BidOfferBook with(double price, int size) {
        final BidOfferBook bidOfferBook = new BidOfferBook();
        bidOfferBook.getPriceLadder().add(price);
        bidOfferBook.getSizeLadder().add(size);
        return bidOfferBook;
    }

    public final static BidOfferBook with(double price1, int size1, double price2, int size2) {
        final BidOfferBook bidOfferBook = new BidOfferBook();
        bidOfferBook.getPriceLadder().add(price1);
        bidOfferBook.getSizeLadder().add(size1);

        bidOfferBook.getPriceLadder().add(price2);
        bidOfferBook.getSizeLadder().add(size2);

        return bidOfferBook;
    }

    public final static BidOfferBook with(Collection<Double> prices, Collection<Integer> sizes) {
        final BidOfferBook bidOfferBook = new BidOfferBook();
        bidOfferBook.getPriceLadder().addAll(prices);
        bidOfferBook.getSizeLadder().addAll(sizes);
        return bidOfferBook;
    }

    public Collection<Double> getPriceLadder() {
        return priceLadder;
    }

    public void setPriceLadder(Collection<Double> priceLadder) {
        this.priceLadder = priceLadder;
    }

    public Collection<Integer> getSizeLadder() {
        return sizeLadder;
    }

    public void setSizeLadder(Collection<Integer> sizeLadder) {
        this.sizeLadder = sizeLadder;
    }

    public BidOffer side() { return BidOffer.BID; };

}
