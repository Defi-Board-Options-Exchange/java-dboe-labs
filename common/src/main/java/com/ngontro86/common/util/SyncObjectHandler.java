package com.ngontro86.common.util;

import com.ngontro86.common.Handler;
import com.ngontro86.common.net.SocketData;
import com.ngontro86.common.net.SocketPublisher;
import com.ngontro86.common.serials.ObjEvent;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.util.LinkedList;
import java.util.List;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.LinkedBlockingQueue;

public class SyncObjectHandler implements Runnable, Handler<SocketData<ObjEvent>> {
    private Logger logger = LogManager.getLogger(SyncObjectHandler.class);

    private BlockingQueue<ObjEvent> q;

    public List<Object> result;

    public SocketPublisher<ObjEvent, Object> pub;

    private String query, eventId;

    public SyncObjectHandler(String query, String eventId, SocketPublisher<ObjEvent, Object> pub) {

        this.result = new LinkedList<>();
        this.q = new LinkedBlockingQueue<>();

        this.pub = new SocketPublisher<>("SyncObject_" + System.currentTimeMillis(), pub.host, pub.port, this, false);
        this.query = query;
        this.eventId = eventId;
    }

    @Override
    public boolean handle(SocketData<ObjEvent> obj) {
        try {
            q.put(obj.data);
        } catch (Exception e) {
            logger.error("handle exception: ", e);
            return false;
        }
        return true;
    }

    @SuppressWarnings("unchecked")
    @Override
    public void run() {
        IOUtils.requestObjectSnapshot(query, eventId, pub);
        while (true) {
            try {
                List<Object> list = (List<Object>) (q.take().event);
                if (list != null && !list.isEmpty()) {
                    result.addAll(list);
                }
                break;
            } catch (Exception e) {
                logger.error("exception: ", e);
            }
        }
        pub.stop();
    }

}
