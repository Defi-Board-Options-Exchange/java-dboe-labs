package com.ngontro86.dboe.web3j.token

import com.ngontro86.app.common.postprocessor.ConfigValuePostProcessor
import com.ngontro86.app.common.postprocessor.LoggerPostProcessor
import com.ngontro86.component.testing.ComponentEnv
import org.junit.Before
import org.junit.Test

class InMemoryTokenLoaderTest {


    ComponentEnv env

    @Before
    void "init"() {
        [
                'realBlockchain'      : 'false',
                'blockchainConfigFile': 'prebuiltBlockchain'
        ].each { k, v -> System.setProperty(k, v) }

        env = ComponentEnv.env([TokenLoaderProvider, ConfigValuePostProcessor, LoggerPostProcessor])
    }

    @Test
    void "should prebuild the chain correctly per config file"() {
        def tokenLoader = env.component(TokenLoader)

        assert tokenLoader.balanceOf(tokenLoader.load('address1'), 'quantvu86') == 200
        assert tokenLoader.balanceOf(tokenLoader.load('address1'), 'savetheworld') == 51
        assert tokenLoader.balanceOf(tokenLoader.load('address2'), 'quantvu86') == 99
    }
}
