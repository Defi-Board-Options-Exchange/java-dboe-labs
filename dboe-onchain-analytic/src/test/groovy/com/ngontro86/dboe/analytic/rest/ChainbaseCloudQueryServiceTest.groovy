package com.ngontro86.dboe.analytic.rest

import com.ngontro86.app.common.postprocessor.ConfigValuePostProcessor
import com.ngontro86.app.common.postprocessor.LoggerPostProcessor
import com.ngontro86.component.testing.ComponentEnv
import org.junit.Before
import org.junit.Test

import static com.ngontro86.utils.ResourcesUtils.lines

class ChainbaseCloudQueryServiceTest {

    ComponentEnv env

    @Before
    void "init"() {
        [
                'chainbase.host'    : 'https://api.chainbase.online',
                'chainbase.basePath': 'v1/dw',
                'chainbase.version' : 'TLSv1.2',
                'chainbase.port'    : '80',
                'https.protocols'   : 'TLSv1,TLSv1.1,TLSv1.2',
                'chainbaseApiKeys'  : 'xxx'
        ].each { k, v -> System.setProperty(k, v) }

        env = ComponentEnv.env([ChainbaseCloudQueryService, ConfigValuePostProcessor, LoggerPostProcessor])
    }

    @Test
    void "should be able to pull all the transfers"() {
        def queryService = env.component(ChainbaseCloudQueryService)

        def transfers = queryService.query(lines('txn-hashes'))
        println "Found: ${transfers.size()} records..."
        transfers.each { println it }
    }
}
