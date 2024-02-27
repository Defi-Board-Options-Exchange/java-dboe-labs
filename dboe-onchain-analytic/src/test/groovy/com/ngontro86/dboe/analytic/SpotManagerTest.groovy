package com.ngontro86.dboe.analytic

import com.ngontro86.app.common.postprocessor.ConfigValuePostProcessor
import com.ngontro86.app.common.postprocessor.LoggerPostProcessor
import com.ngontro86.component.testing.ComponentEnv
import com.ngontro86.dboe.web3j.GasProvider
import com.ngontro86.dboe.web3j.TxnManagerDbProvider
import com.ngontro86.dboe.web3j.Web3jClientProvider
import org.junit.Before
import org.junit.Test

import javax.ws.rs.client.Entity

import static com.ngontro86.restful.common.client.RestClientBuilder.build

class SpotManagerTest {


    ComponentEnv env

    @Before
    void "init"() {
        [
                'ethereumNodeUrl'     : 'https://api.avax.network/ext/bc/C/rpc',
                'fspCalculatorAddress': '0x9c3596A9e7c2847580B0Da5598c258c492D95f4b',
                'chainId'             : '43114',
                'gasLimit'            : '7000000',
                'gasPrice'            : '25000000000',
                'dboeHost.host'       : 'http://dboe.exchange',
                'dboeHost.basePath'   : 'api',
                'dboeHost.port'       : '8686'

        ].each { k, v -> System.setProperty(k, v) }

        env = ComponentEnv.env([SpotManager, Web3jClientProvider, GasProvider, TxnManagerDbProvider, ConfigValuePostProcessor, LoggerPostProcessor])
    }

    @Test
    void "should read spot info"() {
        def spotManager = env.component(SpotManager)

        println spotManager.getSpot('ETH', (long) ((System.currentTimeMillis() - 5000) / 1000), 5)
    }

    @Test
    void "should update spot for BTC"() {
        def restClient = build('dboeHost')
        restClient.postWithQParamsAndHeader('coreEngine/updateSpot',
                ['underlying': 'BTC', 'spot': 28343d],
                'passCode', '2511', Entity.json(null), Boolean)
    }
}
