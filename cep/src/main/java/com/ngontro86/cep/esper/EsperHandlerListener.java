package com.ngontro86.cep.esper;

import com.espertech.esper.client.EPStatement;
import com.ngontro86.cep.CepEngine;
import com.ngontro86.common.Handler;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.util.Map;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.LinkedBlockingQueue;

public class EsperHandlerListener<T> implements Runnable{

    private Logger logger = LogManager.getLogger(EsperHandlerListener.class);

    public final Handler<T> handler;
    public final EPStatement statement;
    public BlockingQueue<Map> outputQ = new LinkedBlockingQueue<>();
    public volatile boolean queueEmpty = true;
    public int processed = 0;
    private boolean isMap;
    private volatile boolean stopped = false;
    private Object objectKey = null;

    private CepEngine cep;

    public EsperHandlerListener(CepEngine cep, Handler<T> handler, EPStatement statement, boolean isMap) {
        this.handler = handler;
        this.statement = statement;
        this.isMap = isMap;
        this.cep = cep;
    }

    public void update(Map events) {
        if (!stopped) {
            if (events != null) {
                this.queueEmpty = false;
                if (events != null) {
                    outputQ.offer(events);
                }
            }
        } else {
            logger.warn("Stopped CepHandlerListener still received arg1 an event!");
        }
    }

    @Override
    public void run() {
        while (!stopped) {
            try {
                Object obj = outputQ.take();
                if (!handler.handle((T) obj)) {
                    logger.warn("CepStore subscriber failed to handle update. Statement: " + statement.getText() + "; Removing listeners and destroying the statement.");
                    this.stopped = true;
                }
                this.processed++;
                this.queueEmpty = outputQ.isEmpty();
            } catch (Exception e) {
                logger.warn("CepStore subscriber failed to handle update. Statement: " + statement.getText() + "; Removing listeners and destroying the statement.", e);
                this.stopped = true;
            }
        }
        remove(true);
    }

    public void stop() {
        this.stopped = true;
        remove(false);
    }

    private void remove(boolean clean) {
        logger.info("CepHandlerListener thread stopping: " + this.statement.getText());
        try {
            statement.setSubscriber(null);
            if (!statement.isDestroyed()) {
                statement.destroy();
            }
        } catch (Exception e) {
        }
        if (clean) {
            cep.cleanListeners();
        }
    }

    protected boolean getStopped() {
        return stopped;
    }

}
