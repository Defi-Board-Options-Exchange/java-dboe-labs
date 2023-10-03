package com.ngontro86.server.processor;

import com.ngontro86.cep.CepEngine;
import com.ngontro86.common.Handler;
import com.ngontro86.common.annotations.Logging;
import com.ngontro86.common.annotations.DBOEComponent;
import com.ngontro86.common.net.SocketData;
import com.ngontro86.common.serials.ObjAdhoc;
import com.ngontro86.common.serials.ObjEvent;
import com.ngontro86.utils.Utils;
import org.apache.logging.log4j.Logger;

import javax.annotation.PostConstruct;
import javax.inject.Inject;
import java.util.List;

@DBOEComponent
public class ObjAdhocProcessor extends SocketDataProcessor<Object, ObjEvent> {

    @Logging
    private Logger logger;

    @Inject
    private CepEngine cep;

    protected volatile long ok, err;

    @PostConstruct
    private void init() {
        Utils.startThread(this, Thread.MIN_PRIORITY);
    }

    @Override
    public void processOneEvent(SocketData<Object> event, Handler<SocketData<ObjEvent>> outgoingHandler) {
        try {
            String query = ((ObjAdhoc) event.data).query;
            String eventID = ((ObjAdhoc) event.data).eventID;
            if (eventID == null) {
                eventID = "";
            }
            try {
                List<Object> results = this.cep.queryObj(query);
                outgoingHandler.handle(new SocketData<>(event.connectionID, new ObjEvent(eventID, results)));
            } catch (Exception e) {
                logger.error("Failed to execute query:" + event.data, e);
            }
            this.ok++;
        } catch (Exception e) {
            logger.error("Failed to deal with adhoc request: " + event.data, e);
        }
        this.err++;
    }
}
