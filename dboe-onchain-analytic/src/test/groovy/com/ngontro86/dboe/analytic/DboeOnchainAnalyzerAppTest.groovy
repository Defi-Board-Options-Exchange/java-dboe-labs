package com.ngontro86.dboe.analytic

import com.ngontro86.app.common.postprocessor.ConfigValuePostProcessor
import com.ngontro86.app.common.postprocessor.LoggerPostProcessor
import com.ngontro86.component.testing.ComponentEnv
import com.ngontro86.dboe.web3j.DBOEClob
import com.ngontro86.dboe.web3j.GasProvider
import com.ngontro86.dboe.web3j.TxnManagerDbProvider
import com.ngontro86.dboe.web3j.Web3jClientProvider
import org.junit.Before
import org.junit.Test
import org.web3j.protocol.Web3j
import org.web3j.tx.RawTransactionManager
import org.web3j.tx.gas.ContractGasProvider

class DboeOnchainAnalyzerAppTest {

    ComponentEnv env

    @Before
    void "init"() {
        [
                'ethereumNodeUrl'     : 'https://api.avax.network/ext/bc/C/rpc',
                'chainId'             : '43114',
                'gasLimit'            : '7000000',
                'gasPrice'            : '25000000000',
                'dboeHost.host'       : 'xxxx',
                'dboeHost.basePath'   : 'api',
                'dboeHost.port'       : 'xxx'
        ].each { k, v -> System.setProperty(k, v) }

        env = ComponentEnv.env([Web3jClientProvider, GasProvider, TxnManagerDbProvider, ConfigValuePostProcessor, LoggerPostProcessor])
    }

    @Test
    void "should read clob info" () {
        def dboeClob = DBOEClob.load('0x6751b76eF043bf221ee129927A115e9e5a09b212', env.component(Web3j), env.component(RawTransactionManager), env.component(ContractGasProvider))

        println dboeClob.optionFactory().send()
        println dboeClob.clearingHouse().send()
    }

}
