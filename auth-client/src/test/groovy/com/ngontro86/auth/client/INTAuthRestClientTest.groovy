package com.ngontro86.auth.client

import com.ngontro86.app.common.postprocessor.ConfigValuePostProcessor
import com.ngontro86.app.common.postprocessor.LoggerPostProcessor
import com.ngontro86.component.testing.ComponentEnv
import org.junit.Before
import org.junit.Ignore
import org.junit.Test

@Ignore
class INTAuthRestClientTest {

    ComponentEnv env

    @Before
    void init() {
        env = ComponentEnv.env([AuthRestClient, ConfigValuePostProcessor, LoggerPostProcessor])
        [
                'useremail'           : 'truongvinh.vu@gmail.com',
                'userpassword'        : 'xxxxx',
                'authRestClientName'  : 'localServer',
                'localServer.host'    : 'http://localhost',
                'localServer.basePath': 'api',
                'localServer.port'    : '7779'
        ].each {k, v -> System.setProperty(k, v)}
    }

    @Test
    void "should get server signed token"() {
        def client = env.component(AuthRestClient)
        println  client.withQueryParams("user/userGroup", [:], String)
    }

}
