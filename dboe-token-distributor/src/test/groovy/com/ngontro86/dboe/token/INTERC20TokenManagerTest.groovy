package com.ngontro86.dboe.token

import com.ngontro86.app.common.postprocessor.ConfigValuePostProcessor
import com.ngontro86.app.common.postprocessor.LoggerPostProcessor
import com.ngontro86.component.testing.ComponentEnv
import com.ngontro86.dboe.web3j.GasProvider
import com.ngontro86.dboe.web3j.TxnManagerProvider
import com.ngontro86.dboe.web3j.Web3jClientProvider
import org.junit.Before
import org.junit.Test

class INTERC20TokenManagerTest {


    ComponentEnv env

    @Before
    void "init"() {
        [
                'ethereumNodeUrl': 'https://api.nautilus.nautchain.xyz',
                'credential'     : 'xxxx',
                'chainId'        : '22222',
                'tokenAmounts'   : 'ZBC:1',
                'gasLimit'       : '300000000',
                'gasPrice'       : '1000000000'
        ].each { k, v -> System.setProperty(k, v) }

        env = ComponentEnv.env([Web3jClientProvider, GasProvider, TxnManagerProvider, ERC20TokenManager, ConfigValuePostProcessor, LoggerPostProcessor])
    }

    @Test
    void "should transfer native token"() {
        def tokenManager = env.component(ERC20TokenManager)
        tokenManager.transfer('ZBC', '0xEC85c29E2d8Fe910714F98a02c30dc7C9effcfb2')
    }
}
