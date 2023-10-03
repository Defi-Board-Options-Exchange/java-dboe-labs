package com.ngontro86.dboe.trading.test

import com.ngontro86.common.annotations.DBOEComponent
import com.ngontro86.dboe.trading.hedge.HedgingExecutor
import com.ngontro86.market.web3j.GreekRisk

@DBOEComponent
class NoOpHedger implements HedgingExecutor {

    @Override
    void hedgeIfNeeded(String underlying, GreekRisk risk) {
        println "Need to hedge out: ${risk}"
    }

    @Override
    void setUnderlyings(Set<String> underlyings) {
        println "Underlyings: ${underlyings}"
    }
}