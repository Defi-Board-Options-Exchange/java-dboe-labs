package com.ngontro86.market.instruments


import com.ngontro86.dboe.web3j.Utils
import com.ngontro86.market.time.TimeSource
import com.ngontro86.restful.common.client.RestClient

import javax.annotation.PostConstruct
import javax.inject.Inject

import static com.ngontro86.restful.common.client.RestClientBuilder.build

class RestBasedExchangeSpecsLoader implements ExchangeSpecsLoader {

    private RestClient restClient

    @Inject
    private TimeSource timeSource

    @PostConstruct
    private void init() {
        restClient = build('dboeHost')
    }

    @Override
    Collection loadChains() {
        return restClient.withQueryParams('query/chainInfo', [:], Collection)
    }

    @Override
    Collection loadOptions(String chain) {
        return restClient.withQueryParams('query/listInstrument', ['chain': chain], Collection).findAll {
            it['chain'] == chain && Utils.getTimeUtc(it['expiry'], it['ltt']) >= timeSource.currentTimeMilliSec()
        } as Collection<Map>
    }

    @Override
    Collection loadClobs(String chain) {
        return restClient.withQueryParams('query/clobInfo', [:], Collection).findAll {
            it['chain'] == chain
        } as Collection<Map>
    }

    @Override
    Collection loadOptionChainMarket(String chain, String und, int expiry) {
        return restClient.withQueryParams('query/optionChainMarket', [
                'chain'          : chain,
                'underlying'     : und,
                'expiryDate'     : expiry,
                'collateralGroup': '20%'
        ], Collection) as Collection<Map>
    }

    @Override
    Collection orderbook(String chain, String instrId) {
        return restClient.withQueryParams('query/orderbook', [
                'chain'  : chain,
                'instrId': instrId
        ], Collection) as Collection<Map>
    }
}
