package com.ngontro86.dboe.web3j

import com.ngontro86.app.common.postprocessor.ConfigValuePostProcessor
import com.ngontro86.app.common.postprocessor.LoggerPostProcessor
import com.ngontro86.component.testing.ComponentEnv
import org.junit.Before
import org.junit.Test
import org.web3j.protocol.Web3j

class Web3jClientProviderTest {

    ComponentEnv env

    @Before
    void "init"() {
        [
                'ethereumNodeUrl': 'https://data-seed-prebsc-1-s1.binance.org:8545'

        ].each { k, v -> System.setProperty(k, v) }

        env = ComponentEnv.env([Web3jClientProvider, ConfigValuePostProcessor, LoggerPostProcessor])

    }

    @Test
    void "should be able to initiate web3j"() {
        env.component(Web3j)
    }
}
