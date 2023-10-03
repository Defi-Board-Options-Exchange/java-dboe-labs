package com.ngontro86.websocket.common

import com.ngontro86.common.annotations.ConfigValue
import com.ngontro86.common.annotations.Logging
import com.ngontro86.common.annotations.DBOEComponent
import org.apache.logging.log4j.Logger
import org.springframework.context.annotation.Configuration
import org.springframework.messaging.simp.config.MessageBrokerRegistry
import org.springframework.web.socket.config.annotation.EnableWebSocketMessageBroker
import org.springframework.web.socket.config.annotation.StompEndpointRegistry
import org.springframework.web.socket.config.annotation.WebSocketMessageBrokerConfigurer

import javax.annotation.PostConstruct

@DBOEComponent
@Configuration
@EnableWebSocketMessageBroker
class WebSocketServer implements WebSocketMessageBrokerConfigurer {

    @Logging
    private Logger logger

    @ConfigValue(config = "webSocketDestinationPrefix")
    private String webSocketDestinationPrefix = '/websocket'

    @ConfigValue(config = "webSocketBrokers")
    private Collection webSocketBrokers = ['/queue/orders/', '/queue/trades/', '/topic/orders/', '/topic/trades/']

    @PostConstruct
    private void init() {
        //logger.info("WebSocket Server Initialising...")
    }

    @Override
    void registerStompEndpoints(StompEndpointRegistry registry) {
        registry.addEndpoint("/broadcast").withSockJS()
                .setStreamBytesLimit(512 * 1024)
                .setHttpMessageCacheSize(1000)
                .setDisconnectDelay(30 * 1000)

    }

    @Override
    void configureMessageBroker(MessageBrokerRegistry registry) {
        registry.setApplicationDestinationPrefixes(webSocketDestinationPrefix)
        registry.enableSimpleBroker(webSocketBrokers as String[])
    }
}
