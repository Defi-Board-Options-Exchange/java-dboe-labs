package com.ngontro86.market.web3j


import com.ngontro86.common.annotations.DBOEComponent
import com.ngontro86.common.annotations.Logging
import com.ngontro86.dboe.web3j.annotations.OtherTokenLoader
import com.ngontro86.dboe.web3j.token.TokenLoader
import org.apache.logging.log4j.Logger

import javax.inject.Inject

@DBOEComponent
class Web3TokenPortfolioManager<T> {

    @Logging
    private Logger logger

    @Inject
    @OtherTokenLoader
    private TokenLoader<T> tokenLoader

    private Map<String, T> tokens = [:]
    private Map<String, BigInteger> decimals = [:]

    void initPairs(Collection<Map> tokens) {
        tokens.each {
            loadPair(it)
        }
    }

    Map<String, Double> portfolio(Collection<String> addresses) {
        Map<String, Double> positionPortfolio = [:]
        tokens.each { underlying, token ->
            positionPortfolio[underlying] = addresses.collect { addr ->
                tokenLoader.balanceOf(token, addr)
            }.sum() / decimals[underlying]
        }
        logger.info "Spot portfolio: ${positionPortfolio}"
        return positionPortfolio
    }

    private void loadPair(Map pair) {
        try {
            tokens.putIfAbsent(pair['base_underlying'], tokenLoader.load(pair['base_token']))
            tokens.putIfAbsent(pair['quote_underlying'], tokenLoader.load(pair['quote_token']))

            decimals.putIfAbsent(pair['base_underlying'], Math.pow(10, tokenLoader.decimals(tokens.get(pair['base_underlying']))))
            decimals.putIfAbsent(pair['quote_underlying'], Math.pow(10, tokenLoader.decimals(tokens.get(pair['quote_underlying']))))
        } catch (Exception e) {
            logger.error(e)
        }
    }
}
