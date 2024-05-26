package com.ngontro86.server.dboe.services.luckydraw

import org.junit.Test

class LuckyDrawStatsTest {

    @Test
    void "should add and remove the element"() {
        def stats = new LuckyDrawStats()
        25.times {
            stats.addWallet(String.valueOf(it))
        }
        assert stats.mostRecentLuckyWallets.first() == '4'
    }

}
