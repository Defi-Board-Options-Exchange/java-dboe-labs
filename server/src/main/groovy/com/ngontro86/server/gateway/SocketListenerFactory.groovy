package com.ngontro86.server.gateway

import com.ngontro86.common.annotations.ConfigValue
import com.ngontro86.common.annotations.DBOEComponent
import com.ngontro86.common.net.SocketListener
import com.ngontro86.common.serials.ObjEvent
import com.ngontro86.utils.Utils
import org.springframework.context.annotation.Bean

import javax.inject.Inject

import static java.lang.Thread.MAX_PRIORITY


@DBOEComponent
class SocketListenerFactory {

    @Inject
    private ObjReceiver receiver

    @ConfigValue
    private Integer objPort = 7771

    @Bean
    SocketListener<Object, ObjEvent> socketListener() {
        def socketListener = new SocketListener<Object, ObjEvent>(objPort, receiver, false)
        Utils.startThread(socketListener, MAX_PRIORITY)
        return socketListener
    }
}
