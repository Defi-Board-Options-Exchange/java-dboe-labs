package com.ngontro86.websocket.common

import com.ngontro86.common.annotations.ConfigValue
import com.ngontro86.common.annotations.Logging
import com.ngontro86.common.annotations.DBOEComponent
import org.apache.logging.log4j.Logger
import org.springframework.lang.Nullable
import org.springframework.messaging.simp.stomp.StompCommand
import org.springframework.messaging.simp.stomp.StompHeaders
import org.springframework.messaging.simp.stomp.StompSession
import org.springframework.messaging.simp.stomp.StompSessionHandler
import org.springframework.web.socket.messaging.WebSocketStompClient

import javax.annotation.PostConstruct
import javax.inject.Inject
import java.lang.reflect.Type

@DBOEComponent
class WsMessageProducer {

    @Logging
    private Logger logger

    @Inject
    private WebSocketStompClient stompClient

    private StompSession session

    @ConfigValue(config = "queueUrl")
    private String queueUrl

    @PostConstruct
    private void init() {
        logger.info("WsMessage Producer Initialising...")

        session = stompClient.connect(queueUrl,  new StompSessionHandler() {
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
        }).get()
    }

}
