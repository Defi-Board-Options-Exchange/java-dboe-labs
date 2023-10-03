package com.ngontro86.dboe.trading.test

import com.ngontro86.common.annotations.DBOEComponent
import com.ngontro86.market.instruments.ExchangeSpecsLoader

@DBOEComponent
class InMemoryExchangeSpecsLoader implements ExchangeSpecsLoader {

    @Override
    Collection loadOptions(String chain) {
        return []
    }

    @Override
    Collection loadClobs(String chain) {
        return []
    }
}