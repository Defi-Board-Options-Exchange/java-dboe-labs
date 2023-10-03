package com.ngontro86.websocket.common.web

import com.ngontro86.common.annotations.Logging
import com.ngontro86.common.annotations.DBOEComponent
import com.ngontro86.websocket.bean.ObjPayload
import org.apache.logging.log4j.Logger
import org.springframework.messaging.handler.annotation.MessageExceptionHandler
import org.springframework.messaging.handler.annotation.MessageMapping
import org.springframework.messaging.handler.annotation.Payload
import org.springframework.messaging.simp.SimpMessagingTemplate
import org.springframework.messaging.simp.annotation.SendToUser
import org.springframework.messaging.simp.annotation.SubscribeMapping
import org.springframework.stereotype.Controller

import javax.annotation.PostConstruct
import javax.inject.Inject

@DBOEComponent
@Controller
class WebSocketController {

    @Logging
    private Logger logger

    @Inject
    private SimpMessagingTemplate messagingTemplate

    @PostConstruct
    private void init() {
        logger.info("WebSocket Controller Initialising...")
    }

    @MessageMapping("/broadcast")
    void broadcastObject(@Payload ObjPayload obj) {
        messagingTemplate.convertAndSend(obj.topic, obj.payload)
    }

    @SubscribeMapping("/subscribe")
    void subscribe(@Payload ObjPayload obj) {

    }

    @MessageExceptionHandler
    @SendToUser("/queue/errors")
    String handleException(Throwable exception) {
        return exception.getMessage()
    }

}
