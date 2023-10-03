package com.ngontro86.websocket.common

import com.ngontro86.app.common.postprocessor.ConfigValuePostProcessor
import com.ngontro86.component.testing.ComponentEnv
import org.junit.Before
import org.junit.Test
import org.springframework.lang.Nullable
import org.springframework.messaging.simp.stomp.StompCommand
import org.springframework.messaging.simp.stomp.StompHeaders
import org.springframework.messaging.simp.stomp.StompSession
import org.springframework.messaging.simp.stomp.StompSessionHandler
import org.springframework.web.socket.messaging.WebSocketStompClient

import java.lang.reflect.Type

class WebSocketClientTest {

    ComponentEnv env

    @Before
    void init() {
        env = ComponentEnv.env([WebSocketStompClient])
    }

    @Test
    void "should connect to websocket server"() {
        def websocketClient = new WebSocketStompClient()

        def sessionHandler = new StompSessionHandler() {

            @Override
            void afterConnected(StompSession session, StompHeaders connectedHeaders) {

            }

            @Override
            void handleException(StompSession session, @Nullable StompCommand command, StompHeaders headers, byte[] payload, Throwable exception) {

            }

            @Override
            void handleTransportError(StompSession session, Throwable exception) {

            }

            @Override
            Type getPayloadType(StompHeaders headers) {
                return null
            }

            @Override
            void handleFrame(StompHeaders headers, @Nullable Object payload) {

            }
        }
        def session = websocketClient.connect("http://localhost:8080/broadcast", sessionHandler).get()

        sleep 5000
    }

}
