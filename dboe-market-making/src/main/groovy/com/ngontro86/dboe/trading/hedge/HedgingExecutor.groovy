package com.ngontro86.dboe.trading.hedge

import com.ngontro86.market.web3j.GreekRisk

interface HedgingExecutor {

    void hedgeIfNeeded(String underlying, GreekRisk risk)

    void setUnderlyings(Set<String> underlyings)
}