package com.ngontro86.server.dboe.services.analytic

import com.fasterxml.jackson.annotation.JsonProperty
import com.ngontro86.market.web3j.GreekRisk

class PortfolioRisk {
    Map<String, Double> vals
    Map<String, GreekRisk> greeks

    @JsonProperty('TOTAL VAL')
    double totalVal() {
        return vals.collect { it.value }.sum()
    }
}
