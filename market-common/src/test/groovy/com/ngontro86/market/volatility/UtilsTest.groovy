package com.ngontro86.market.volatility

import org.junit.Test

import static com.ngontro86.market.volatility.Utils.twoNearest

class UtilsTest {

    @Test
    void "should throw exception when set is empty or null"() {
        try {
            twoNearest(null, 100)
        } catch (Exception e) {
            assert true
        }

        try {
            twoNearest([], 100)
        } catch (Exception e) {
            assert true
        }
    }

    @Test
    void "should find two nearest expiries"() {
        def sets = [101L, 103L, 104L, 105L, 106L] as Set
        assert twoNearest(sets, 102L) == [101L, 103L]
        assert twoNearest(sets, 99L) == [101L, 101L]
        assert twoNearest(sets, 200L) == [106L, 106L]
    }
}
