package com.ngontro86.algo.utils

import com.ngontro86.algo.strategy.PairStrategy
import org.junit.Test

import static com.ngontro86.algo.utils.StrategyUtils.load
import static com.ngontro86.algo.utils.StrategyUtils.validatePairName


class StrategyUtilsTest {

    @Test
    void "should load strategy 1"() {
        def strat = load(
                [
                        ['variable': 'account', 'value': 'x'],
                        ['variable': 'endingTradingTime', 'value': 110000],
                        ['variable': 'firstLegInstId', 'value': 'STWQ17'],
                        ['variable': 'secondLegPrimaryInstId', 'value': 'STWQ17'],
                        ['variable': 'max2ncPcWeightPct', 'value': '18']
                ], PairStrategy
        )

        assert strat.account == 'x'
        assert strat.firstLegInstId == 'STWQ17'
        assert strat.max2ncPcWeightPct == 18d
        assert strat.endingTradingTime == 110000
    }

    @Test
    void "should validate pair name 1"() {
        try {
            validatePairName('Hang-Seng')
            assert false
        } catch (Exception e){
            assert  true
        }
    }

}
