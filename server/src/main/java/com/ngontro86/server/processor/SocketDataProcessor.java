package com.ngontro86.server.processor;

import com.ngontro86.common.Handler;
import com.ngontro86.common.net.SocketData;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.util.concurrent.BlockingQueue;
import java.util.concurrent.LinkedBlockingQueue;

public abstract class SocketDataProcessor<RECV, SEND> implements Runnable {
    private Logger logger = LogManager.getLogger(SocketDataProcessor.class);

    protected String name;
    private BlockingQueue<OneEventHandler> q = new LinkedBlockingQueue<>();

    public abstract void processOneEvent(SocketData<RECV> obj, Handler<SocketData<SEND>> outHandler);

    public void process(SocketData<RECV> obj, Handler<SocketData<SEND>> outHandler) {
        try {
            q.put(new OneEventHandler(obj, outHandler));
        } catch (Exception e) {
        }
    }

    @Override
    public void run() {
        while (true) {
            try {
                OneEventHandler oneEvent = q.take();
                if (oneEvent.event != null) {
                    processOneEvent(oneEvent.event, oneEvent.handler);
                }
            } catch (Exception e) {
                logger.error(e);
            }
        }
    }

    private class OneEventHandler {
        public SocketData<RECV> event;
        public Handler<SocketData<SEND>> handler;

        public OneEventHandler(SocketData<RECV> ev, Handler<SocketData<SEND>> handler) {
            this.event = ev;
            this.handler = handler;
        }
    }
}
