package com.ngontro86.dboe.analytic

import com.ngontro86.app.common.postprocessor.ConfigValuePostProcessor
import com.ngontro86.app.common.postprocessor.LoggerPostProcessor
import com.ngontro86.component.testing.ComponentEnv
import com.ngontro86.dboe.web3j.GasProvider
import com.ngontro86.dboe.web3j.TxnManagerProvider
import com.ngontro86.dboe.web3j.Web3jClientProvider
import org.junit.Before
import org.junit.Ignore
import org.junit.Test

@Ignore
class SpotManagerTest {


    ComponentEnv env

    @Before
    void "init"() {
        [
                'ethereumNodeUrl'     : 'https://api.avax.network/ext/bc/C/rpc',
                'fspCalculatorAddress': '0x9c3596A9e7c2847580B0Da5598c258c492D95f4b',
                'credential'          : PrivateKey.DEPLOYER_PK,
                'chainId'             : '43114',
                'gasLimit'            : '7000000',
                'gasPrice'            : '25000000000'

        ].each { k, v -> System.setProperty(k, v) }

        env = ComponentEnv.env([SpotManager, Web3jClientProvider, GasProvider, TxnManagerProvider, ConfigValuePostProcessor, LoggerPostProcessor])
    }

    @Test
    void "should read spot info"() {
        def spotManager = env.component(SpotManager)

        println spotManager.getSpot('ETH', (long)((System.currentTimeMillis() - 5000)/1000), 5)
    }
}
