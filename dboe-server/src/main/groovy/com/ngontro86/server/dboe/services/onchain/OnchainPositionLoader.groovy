package com.ngontro86.server.dboe.services.onchain


import com.ngontro86.common.annotations.ConfigValue
import com.ngontro86.common.annotations.DBOEComponent
import com.ngontro86.common.annotations.Logging
import com.ngontro86.server.dboe.ERC20
import org.apache.logging.log4j.Logger
import org.web3j.crypto.Credentials
import org.web3j.crypto.Keys
import org.web3j.protocol.Web3j
import org.web3j.protocol.http.HttpService
import org.web3j.tx.gas.StaticGasProvider

import javax.annotation.PostConstruct
import java.util.concurrent.ConcurrentHashMap

@DBOEComponent
class OnchainPositionLoader {

    @Logging
    private Logger logger

    @ConfigValue(config = "ethereumNodeUrl")
    private String ethereumNodeUrl

    @ConfigValue(config = "chain")
    private String chain = 'Polygon'

    @ConfigValue(config = "gasPrice")
    private Long gasPrice = 25_000_000_000L

    @ConfigValue(config = "gasLimit")
    private Long gasLimit = 8_000_000L

    private Web3j web3j
    private Credentials dummyCredentials = Credentials.create(Keys.createEcKeyPair())
    private StaticGasProvider gasProvider

    private Map<String, ERC20> erc20s = [:] as ConcurrentHashMap

    private static final double DBOE_OPTION_PRECISION = Math.pow(10, 18)
    @PostConstruct
    private void init() {
        web3j = Web3j.build(new HttpService(ethereumNodeUrl))
        gasProvider = new StaticGasProvider(new BigInteger(gasPrice), new BigInteger(gasLimit))
    }

    double loadPosition(String c, String longAddr, String shortAddr, String walletAddr) {
        if (c != chain) {
            return Double.NaN
        }
        return (loadIfNeeded(longAddr).balanceOf(walletAddr).send() - loadIfNeeded(shortAddr).balanceOf(walletAddr).send()) / DBOE_OPTION_PRECISION
    }

    private ERC20 loadIfNeeded(String tokenAddr) {
        if (erc20s.containsKey(tokenAddr)) {
            return erc20s.get(tokenAddr)
        }
        def erc20 = ERC20.load(tokenAddr, web3j, dummyCredentials, gasProvider)
        erc20s.put(tokenAddr, erc20)
        return erc20
    }

}
