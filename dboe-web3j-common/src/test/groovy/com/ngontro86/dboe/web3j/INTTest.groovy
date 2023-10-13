package com.ngontro86.dboe.web3j

import com.ngontro86.app.common.postprocessor.ConfigValuePostProcessor
import com.ngontro86.app.common.postprocessor.LoggerPostProcessor
import com.ngontro86.component.testing.ComponentEnv
import org.junit.Before
import org.junit.Test
import org.web3j.protocol.Web3j
import org.web3j.tx.RawTransactionManager
import org.web3j.tx.gas.ContractGasProvider

class INTTest {


    ComponentEnv env

    @Before
    void "init"() {
        [
                'ethereumNodeUrl'  : 'https://polygon-mainnet.infura.io/v3/6942e8a7001844448ba34c7ea958f47b',
                'credential'       : 'xxxx',
                'chainId'          : '137',
                'gasLimit'         : '7000000',
                'gasPrice'         : '90000000000',
                'dboeHost.host'    : 'http://dboe.exchange',
                'dboeHost.basePath': 'api',
                'dboeHost.port'    : '8686'
        ].each { k, v -> System.setProperty(k, v) }

        env = ComponentEnv.env([Web3jClientProvider, GasProvider, TxnManagerProvider, ConfigValuePostProcessor, LoggerPostProcessor])
    }

    @Test
    void "should be able to get BTC and ETH price"() {
        def fsp = FspCalculator.load('0x731B70b86D494d6E6C3860Ad4EF6FCE04f25BA12', env.component(Web3j), env.component(RawTransactionManager), env.component(ContractGasProvider))
        println fsp.avgSpot(Utils.padding(32, 'ETH' as byte[]), 1697036400, 5).send()
        //println fsp.priceScales(Utils.padding(32, 'ETH' as byte[])).send()
    }

    @Test
    void "should be able to final settle ETH price"() {
        def optionFactory = DBOEOptionFactory.load('0x231E7ced4B68Ffd6AEF68990F7967a93720A663F', env.component(Web3j), env.component(RawTransactionManager), env.component(ContractGasProvider))
        //optionFactory.finalSettle(Utils.padding(32, 'ETH' as byte[]), 20231006).send()
        println optionFactory.underlyingPrice(Utils.padding(32, 'ETH' as byte[]), 20231006).send()
        //optionFactory.manualFinalSettle(Utils.padding(32, 'ETH' as byte[]), 20231006, 16342590).send()
    }

    @Test
    void "update ref prices for BTC"() {
        def clob = DBOEClob.load('0xeC4e8861b70Db029d687306AC131B875D6bB1e4b', env.component(Web3j), env.component(RawTransactionManager), env.component(ContractGasProvider))
        ["B27600C1013", "B27600P1013", "B27750C1013", "B27750P1013", "B27900C1013", "B27900P1013", "B28050C1013", "B28050P1013", "B28200C1013", "B28200P1013", "B28350C1013", "B28350P1013"].each {
            clob.refreshRefPx(Utils.padding(32, it as byte[])).send()
        }
    }

}
