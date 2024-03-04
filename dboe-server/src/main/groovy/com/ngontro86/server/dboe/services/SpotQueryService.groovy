package com.ngontro86.server.dboe.services

import com.ngontro86.cep.CepEngine
import com.ngontro86.common.annotations.DBOEComponent
import com.ngontro86.common.annotations.Logging
import org.apache.logging.log4j.Logger

import javax.inject.Inject

@DBOEComponent
class SpotQueryService {

    @Logging
    private Logger logger

    @Inject
    private CepEngine cep

    Collection<Map> allMarkets(String chain) {
        cep.queryMap("select * from DboeSpotMarketOverviewWin(chain='${chain}')")
    }

    Collection<Map> orderbook(String chain, String quoteToken, String baseToken) {
        cep.queryMap("select address, amount as size, notional, price, price_level, buy_sell " +
                "from DboeAggrSpotOrderBookWin(chain='${chain}', quote_token='${quoteToken}', base_token='${baseToken}') " +
                "order by price desc")
    }

    Collection<Map> getFixedSpreads() {
        cep.queryMap("select * from DboeSpotFixedSpreadWin")
    }

    Map onchainClobSpecs() {
        def maps = cep.queryMap("select * from DboeSpotClobSpecsWin")
        return maps.isEmpty() ? [:] : maps.first()
    }

    Collection<Map> trades(String walletId, String chain) {
        []
    }

    Collection<Map> spotDashboard(String chain) {
        cep.queryMap("select * from DboeSpotDashboardAddressWin(chain='${chain}')")
    }

}
