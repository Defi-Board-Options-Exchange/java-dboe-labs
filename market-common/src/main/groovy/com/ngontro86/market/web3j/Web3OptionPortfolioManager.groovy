package com.ngontro86.market.web3j


import com.ngontro86.common.annotations.DBOEComponent
import com.ngontro86.common.annotations.Logging
import com.ngontro86.dboe.web3j.annotations.OptionLoader
import com.ngontro86.dboe.web3j.token.TokenLoader
import org.apache.logging.log4j.Logger

import javax.inject.Inject

@DBOEComponent
class Web3OptionPortfolioManager<T> {

    @Logging
    private Logger logger

    @Inject
    @OptionLoader
    private TokenLoader<T> tokenLoader

    private Map<String, T> longTokens = [:]
    private Map<String, T> shortTokens = [:]

    Map<String, Collection> optionByInstrId = [:]

    private final double DBOE_TOKEN_SCALE = Math.pow(10, 18)

    void initOptions(Collection<Map> options) {
        options.each { option ->
            loadLongShortOption(option)
        }
        optionByInstrId = options.groupBy { it['instr_id'] }
    }

    Map<String, Double> portfolio(Set<String> underlyings, Collection<String> addresses) {
        Map<String, Double> positionPortfolio = [:]
        longTokens.findAll {
            underlyings.isEmpty() || underlyings.contains(optionByInstrId.get(it.key).first()['underlying'])
        }.each { instrId, longToken ->
            positionPortfolio[instrId] = addresses.collect { addr ->
                tokenLoader.balanceOf(longToken, addr) - tokenLoader.balanceOf(shortTokens[instrId], addr)
            }.sum() / DBOE_TOKEN_SCALE
        }
        logger.info "Portfolio: ${positionPortfolio}"
        return positionPortfolio
    }

    Map<String, Double> portfolio(Collection<String> addresses) {
        return portfolio([] as Set, addresses)
    }

    private void loadLongShortOption(Map option) {
        try {
            longTokens.putIfAbsent(option['instr_id'], tokenLoader.load(option['long_contract_address']))
            shortTokens.putIfAbsent(option['instr_id'], tokenLoader.load(option['short_contract_address']))
        } catch (Exception e) {
            logger.error(e)
        }
    }
}
