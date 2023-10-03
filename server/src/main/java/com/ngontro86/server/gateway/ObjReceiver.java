package com.ngontro86.server.gateway;

import com.ngontro86.common.Handler;
import com.ngontro86.common.annotations.DBOEComponent;
import com.ngontro86.common.net.SocketData;
import com.ngontro86.common.serials.ObjAdhoc;
import com.ngontro86.common.serials.ObjEvent;
import com.ngontro86.common.serials.ObjSubscribe;
import com.ngontro86.server.processor.ObjAdhocProcessor;
import com.ngontro86.server.processor.ObjEventProcessor;
import com.ngontro86.server.processor.ObjSubscribeProcessor;
import com.ngontro86.server.processor.SocketDataProcessor;

import javax.annotation.PostConstruct;
import javax.inject.Inject;
import java.util.HashMap;
import java.util.Map;

@DBOEComponent
public class ObjReceiver implements Handler<SocketData<Object>> {

    @Inject
    private Handler<SocketData<ObjEvent>> outHandler;

    private Map<Class<?>, SocketDataProcessor<Object, ObjEvent>> processors = new HashMap<>();

    @Inject
    private ObjAdhocProcessor objAdhocProcessor;

    @Inject
    private ObjSubscribeProcessor objSubscribeProcessor;

    @Inject
    private ObjEventProcessor defaultProcessor;

    @PostConstruct
    private void init() {
        processors.put(ObjAdhoc.class, objAdhocProcessor);
        processors.put(ObjSubscribe.class, objSubscribeProcessor);
    }

    @Override
    public boolean handle(SocketData<Object> obj) {
        SocketDataProcessor<Object, ObjEvent> proc = processors.get(obj.data.getClass());
        if (proc != null) {
            proc.process(obj, this.outHandler);
        } else if (defaultProcessor != null) {
            defaultProcessor.process(obj, this.outHandler);
        }
        return true;
    }

}
