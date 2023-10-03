package com.ngontro86.server.processor;

import com.ngontro86.cep.CepEngine;
import com.ngontro86.common.Handler;
import com.ngontro86.common.annotations.DBOEComponent;
import com.ngontro86.common.net.SocketData;
import com.ngontro86.common.serials.ObjEvent;
import com.ngontro86.utils.Utils;

import javax.annotation.PostConstruct;
import javax.inject.Inject;

@DBOEComponent
public class ObjEventProcessor extends SocketDataProcessor<Object, ObjEvent> {

    @Inject
    private CepEngine cep;

    public volatile long total, error, ok;

    @PostConstruct
    private void init() {
        Utils.startThread(this, Thread.MAX_PRIORITY);
    }

    @Override
    public void processOneEvent(SocketData<Object> obj, Handler<SocketData<ObjEvent>> outHandler) {
        if (!this.cep.accept(obj.data)) {
            this.error++;
        } else {
            this.ok++;
        }
        this.total++;
    }
}
