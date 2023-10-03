package com.ngontro86.server.processor;

import com.ngontro86.cep.CepEngine;
import com.ngontro86.common.Handler;
import com.ngontro86.common.annotations.Logging;
import com.ngontro86.common.annotations.DBOEComponent;
import com.ngontro86.common.net.SocketData;
import com.ngontro86.common.serials.ObjEvent;
import com.ngontro86.common.serials.ObjSubscribe;
import com.ngontro86.utils.Utils;
import org.apache.logging.log4j.Logger;

import javax.annotation.PostConstruct;
import javax.inject.Inject;
import java.util.HashMap;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.Map;

@DBOEComponent
public class ObjSubscribeProcessor extends SocketDataProcessor<Object, ObjEvent> {

    @Logging
    private Logger logger;

    @Inject
    private CepEngine cep;

    private Map<String, LinkedList<CepObjProcessor>> processors = new HashMap<String, LinkedList<CepObjProcessor>>();

    @PostConstruct
    private void init() {
        Utils.startThread(this, Thread.MAX_PRIORITY);
    }

    @Override
    public void processOneEvent(SocketData<Object> obj, Handler<SocketData<ObjEvent>> outHandler) {
        ObjSubscribe objSub = (ObjSubscribe) obj.data;
        if (objSub.query == null) {
            // Stop the statement
            removeHandler(obj.connectionID, objSub.eventID, objSub.query);
        } else {
            CepObjProcessor handler = new CepObjProcessor(objSub.eventID, obj.connectionID, objSub.query, outHandler);
            this.cep.registerObjectHandler(objSub.query, handler);
            LinkedList<CepObjProcessor> procList = this.processors.get(obj.connectionID);
            if (procList == null) {
                procList = new LinkedList<>();
                this.processors.put(obj.connectionID, procList);
            }
            procList.add(handler);
            logger.info("Started new obj subscription thread for connID=" + obj.connectionID + "; eventId=" + objSub.eventID + "; query=" + objSub.query);
        }
    }

    private synchronized void removeHandler(String connectionID, String eventID, String query) {
        if (eventID == null) {
            eventID = "";
        }
        LinkedList<CepObjProcessor> procList = this.processors.get(connectionID);
        if (procList != null) {
            Iterator<CepObjProcessor> it = procList.iterator();
            while (it.hasNext()) {
                CepObjProcessor processor = it.next();
                if (eventID.equals(processor.getEventId()) && query.equals(processor.getQuery())) {
                    cep.unregisterHandler(processor);
                    logger.info(String.format("Removing handler, connID: %s, eventId: %s, query: %s", connectionID, eventID, query));
                    it.remove();
                }
            }
            if (procList.isEmpty()) {
                this.processors.remove(connectionID);
            }
        }
    }
}
