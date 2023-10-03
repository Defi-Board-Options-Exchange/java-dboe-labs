package com.ngontro86.websocket.common

import com.ngontro86.app.common.postprocessor.ConfigValuePostProcessor
import com.ngontro86.app.common.postprocessor.LoggerPostProcessor
import com.ngontro86.component.testing.ComponentEnv
import com.ngontro86.websocket.common.web.WebSocketController
import org.junit.Before
import org.junit.Test

class WebSocketServerTest {

    ComponentEnv env

    @Before
    void init() {
        [
                'server.port': '8080'
        ].each { k, v -> System.setProperty(k, v) }
        env = ComponentEnv.env([WebSocketServer, WebSocketController, LoggerPostProcessor, ConfigValuePostProcessor])
    }

    @Test
    void "should bring up websocket"() {
        def websocket = env.component(WebSocketServer)

        1000.times {sleep 200}
    }

}
