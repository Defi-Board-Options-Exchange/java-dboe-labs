package com.ngontro86.market.common

import org.junit.Test


class BidOfferTest {

    @Test
    void "should get side"() {
        assert BidOffer.side(-1) == BidOffer.BID

        assert BidOffer.side(1) == BidOffer.OFFER
    }

}
