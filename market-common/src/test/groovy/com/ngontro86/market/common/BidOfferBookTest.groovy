package com.ngontro86.market.common

import org.junit.Test

import static com.ngontro86.market.common.BidOfferBook.with


class BidOfferBookTest {

    @Test
    void "should create book"() {
        def book = with(10.0, 1)
        assert book.priceLadder == [10.0]
        assert book.sizeLadder == [1]

        book = with(10.0, 1, 11.0, 6)
        assert book.priceLadder == [10.0, 11.0]
        assert book.sizeLadder == [1, 6]

        def identicalBook = with([10.0, 11.0], [1, 6])
        assert book.priceLadder == identicalBook.priceLadder
        assert book.sizeLadder == identicalBook.sizeLadder
    }

}
