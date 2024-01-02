package com.ngontro86.market.pricing

import org.apache.commons.math3.distribution.NormalDistribution
import org.junit.Test

import static com.ngontro86.market.pricing.Black76.priceOption
import static com.ngontro86.market.pricing.OptionKind.Call
import static com.ngontro86.market.pricing.OptionKind.Put

class Black76Test {

    @Test
    void "should price Option accordingly"() {
        assert priceOption(Call, 4400, 4500, 0.0, 0.08, 0.9) == 402.4442502087061

        println priceOption(Call, 180000, 185000, 0.0, 0.08, 0.75)
        println priceOption(Call, 180000, 215000, 0.0, 0.08, 0.75)
    }

    @Test
    void "should price put call options"() {
        def c = Black76.price option: [kind: Call, atm: 4400, strike: 4500, r: 0.0, t: 0.08, vol: 0.95]
        assert c == 427.24344198146105

        def p = Black76.price option: [kind: Put, atm: 4400, strike: 4500, r: 0.0, t: 0.08, vol: 0.95]
        assert p == 527.2434419814608
    }

    @Test
    void "should price a shock"() {
        def c1 = Black76.price option: [kind: Call, atm: 100d, strike: 100d, r: 0.0, t: 7d / 365d, vol: 0.95]
        [5, 10, 15, 20].each { spotMove ->
            def c2 = Black76.price option: [kind: Call, atm: 100d * (1d + spotMove / 100d), strike: 100d, r: 0.0, t: 7d / 365d, vol: 0.95 * 1.50]
            println "${c1}, ${spotMove}, ${c2}"
        }
    }


    @Test
    void "should price option spread"() {
        [10, 5].each { leverage ->
            def strikeSpread = Math.round(4400d / leverage / 100d) * 100d
            [3d / 12d, 1d / 12d, 7d / 360d, 4d / 360d, 2d / 360d, 1d / 360d].each { time ->
                def c1 = Black76.price option: [kind: Call, atm: 4400, strike: 4500, r: 0.0, t: time, vol: 0.95]
                def c2 = Black76.price option: [kind: Call, atm: 4400, strike: 4500 + strikeSpread, r: 0.0, t: time, vol: 0.95]
                def p1 = Black76.price option: [kind: Put, atm: 4400, strike: 4300, r: 0.0, t: time, vol: 0.95]
                def p2 = Black76.price option: [kind: Put, atm: 4400, strike: 4300 - strikeSpread, r: 0.0, t: time, vol: 0.95]
                println "${time * 360}, ${leverage}, ${c1}, ${c2}, ${p1}, ${p2}"
            }
        }
    }

    @Test
    void "should price DBOE option range"() {
        def leverage = 10d
        def time = 7d / 360d
        def strikeSpread = Math.round(4400d * leverage / 100d / 100d) * 100d
        30.times { i ->
            def strike = 4400d + 100d * (i - 15)
            def c1 = Black76.priceDboe option: [kind: Call, atm: 4400, strike: strike, condStrike: strike + strikeSpread, r: 0.0, t: time, vol: 0.95]
            def p1 = Black76.priceDboe option: [kind: Put, atm: 4400, strike: strike, condStrike: strike - strikeSpread, r: 0.0, t: time, vol: 0.95]
            println "${strike}, ${strikeSpread}, ${strike + strikeSpread}, ${strike - strikeSpread} ${time * 360}, ${leverage}, ${c1}, ${p1}"
        }
    }

    @Test
    void "should work out greeks for DBOE options"() {
        def leverage = 10d
        def time = 7d / 360d
        def strikeSpread = Math.round(4400d * leverage / 100d / 100d) * 100d
        5.times { i ->
            def strike = 4400d + 100d * (i - 15)
            def c = Black76.greekDboe option: [kind: Call, atm: 4400, strike: strike, condStrike: strike + strikeSpread, r: 0.0, t: time, vol: 0.95]
            def rc1 = Black76.greek option: [kind: Put, atm: 4400, strike: strike, r: 0.0, t: time, vol: 0.95]
            def rc2 = Black76.greek option: [kind: Put, atm: 4400, strike: strike + strikeSpread, r: 0.0, t: time, vol: 0.95]
            println "${strike}, ${strikeSpread}, ${strike + strikeSpread},${c['delta']},${c['gamma']},${c['vega']},${c['vomma']}, ${rc1['delta']},${rc1['gamma']},${rc1['vega']},${rc1['vomma']},${rc2['delta']},${rc2['gamma']},${rc2['vega']},${rc2['vomma']}"
        }


        def g = Black76.greekDboe option: [kind: Put, atm: 1789.5d, strike: 1850d, condStrike: 1750d, r: 0.0, t: 5.0 / 24.0 / 365d, vol: 4.0]
        println g
    }

    @Test
    void "should work out greek"() {
        println Black76.greekDboe(option: [kind: Call, atm: 104.5d, strike: 97.5d, condStrike: 112.5d, r: 0.0, t: 12.0 / 365d, vol: 1.5])
    }


    @Test
    void "should price DBOE option with variable spot"() {
        def leverage = 10d
        def time = 3d / 360d
        30.times { i ->
            def atm = 4400d + 100d * (i - 15)
            def c1 = Black76.priceDboe option: [kind: Call, atm: atm, strike: 4400d, condStrike: 4840d, r: 0.0, t: time, vol: 0.95]
            def p1 = Black76.priceDboe option: [kind: Put, atm: atm, strike: 4400d, condStrike: 3960d, r: 0.0, t: time, vol: 0.95]
            println "${atm}, ${leverage}, ${c1}, ${p1}"
        }
    }

    @Test
    void "should find out iv"() {
        assert Black76.iv(53.2, [kind: Call, atm: 1800d, strike: 1850d, condStrike: 2250d, r: 0.0, t: 7d / 365d]) == 0.7653222656249998

        println Black76.iv(116.5, [kind: Put, atm: 1691.5d, strike: 1830d, condStrike: 1680d, r: 0.0, t: 0.7d / 365d])


        println Black76.iv(60.5, [kind: Put, atm: 1789.5d, strike: 1850d, condStrike: 1750d, r: 0.0, t: 5.0 / 24.0 / 365d])

    }

    @Test
    void "should work out d1 and d2 for Blackscholes"() {
        def normal = new NormalDistribution()
        def atm = 1900d

        [0.25d, 0.50d, 0.75d].each { vol ->
            [1d, 5d, 7d].collect { it / 365d }.each { daysToExpiry ->
                [1700d, 1800d, 1900d, 2000d, 2100d, 2200d].each { k ->
                    def d1 = (Math.log(atm / k) + vol * vol * daysToExpiry / 2) / vol / Math.sqrt(daysToExpiry)
                    println "${vol}, ${daysToExpiry}, ${d1}, ${normal.cumulativeProbability(d1)}"
                }
            }
        }
    }

    @Test
    void "return floor log2"() {
        println floorLog2(20000)
    }

    int floorLog2(int n) {
        int res = 0
        if (n < 256) {
            while (n > 1) {
                n >>= 1
                res += 1
            }
            return res
        }

        for (int s = 128; s > 0; s >>= 1) {
            if (n >= 1 << s) {
                n >>= s
                res |= s
            }
        }
        return res
    }

}
