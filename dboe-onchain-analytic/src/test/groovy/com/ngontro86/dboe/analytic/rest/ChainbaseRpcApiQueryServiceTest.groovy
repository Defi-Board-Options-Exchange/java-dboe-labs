package com.ngontro86.dboe.analytic.rest

import com.ngontro86.app.common.postprocessor.ConfigValuePostProcessor
import com.ngontro86.app.common.postprocessor.LoggerPostProcessor
import com.ngontro86.component.testing.ComponentEnv
import org.junit.Before
import org.junit.Test

import static com.ngontro86.utils.ResourcesUtils.lines

class ChainbaseRpcApiQueryServiceTest {


    ComponentEnv env

    @Before
    void "init"() {
        [
                'chainbaserpc.host'    : 'https://polygon-mainnet.s.chainbase.online',
                'chainbaserpc.basePath': 'v1',
                'chainbaserpc.version' : 'TLSv1.2',
                'chainbaserpc.port'    : '80',
                'https.protocols'      : 'TLSv1,TLSv1.1,TLSv1.2',
                'chainbaseApiKeys'     : 'xxxx'
        ].each { k, v -> System.setProperty(k, v) }

        env = ComponentEnv.env([ChainbaseRpcApiQueryService, ConfigValuePostProcessor, LoggerPostProcessor])
    }

    @Test
    void "should be able to pull all the transfers"() {
        def queryService = env.component(ChainbaseRpcApiQueryService)

        def transfers = queryService.query(lines('txn-hashes-rpc-api'))
        transfers.each { println it }
    }
}
