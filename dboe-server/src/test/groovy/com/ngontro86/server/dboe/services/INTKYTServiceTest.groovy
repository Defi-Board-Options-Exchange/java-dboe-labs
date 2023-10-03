package com.ngontro86.server.dboe.services


import com.ngontro86.app.common.postprocessor.ConfigValuePostProcessor
import com.ngontro86.app.common.postprocessor.LoggerPostProcessor
import com.ngontro86.component.testing.ComponentEnv
import com.ngontro86.component.testing.defaultEnvs.InMemDatabaseProvider
import org.junit.Before
import org.junit.Ignore
import org.junit.Test

@Ignore
class INTKYTServiceTest {


    ComponentEnv env

    @Before
    void "init"() {
        [
                'chainalysis.host'    : 'https://api.chainalysis.com',
                'chainalysis.version' : 'TLSv1.2',
                'apiToken'            : 'xxx',
                'chainalysis.basePath': 'api/risk/v2/entities',
                'chainalysis.port'    : '80',
                'https.protocols'     : 'TLSv1,TLSv1.1,TLSv1.2'
        ].each { k, v -> System.setProperty(k, v) }

        env = ComponentEnv.env([KYTService, InMemDatabaseProvider, ConfigValuePostProcessor, LoggerPostProcessor])
    }

    @Test
    void "should register a wallet"() {
        def kytService = env.component(KYTService)
        println kytService.registerThenQueryRisk('0xEC85c29E2d8Fe910714F98a02c30dc7C9effcfb2')
    }

}
