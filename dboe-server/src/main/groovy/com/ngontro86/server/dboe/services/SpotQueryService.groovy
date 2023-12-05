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
        cep.queryMap("select * from DboeSpotMarketWin(chain='${chain}')")
    }

    Collection<Map> orderbook(String chain, String quoteToken, String baseToken) {
        cep.queryMap("select address, sum(amount) as size, price, price_level, buy_sell " +
                "from DboeSpotOrderBookWin(chain='${chain}', quote_token='${quoteToken}', base_token='${baseToken}') " +
                "group by buy_sell, price, address, price_level " +
                "order by price desc")
    }

    Collection<Map> getFixedSpreads() {
        cep.queryMap("select * from DboeSpotFixedSpreadWin")
    }

    Map onchainClobSpecs() {
        def maps = cep.queryMap("select * from DboeSpotClobSpecsWin")
        return maps.isEmpty() ? [:] : maps.first()
    }

    Collection<Map> trades(String walletId) {
        []
    }

    Collection<Map> spotDashboard(String chain) {
        cep.queryMap("select * from DboeSpotDashboardAddressWin(chain='${chain}')")
    }
}
