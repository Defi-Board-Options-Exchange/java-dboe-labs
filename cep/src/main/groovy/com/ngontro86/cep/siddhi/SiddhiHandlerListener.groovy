package com.ngontro86.cep.siddhi

import com.ngontro86.cep.CepEngine
import com.ngontro86.common.Handler
import io.siddhi.core.event.Event
import io.siddhi.core.query.output.callback.QueryCallback
import io.siddhi.query.api.definition.Attribute
import org.apache.log4j.Logger

import java.util.concurrent.BlockingQueue
import java.util.concurrent.LinkedBlockingQueue

class SiddhiHandlerListener<T> extends QueryCallback implements Runnable {

    private Logger logger = Logger.getLogger(SiddhiHandlerListener.class)

    public final Handler<T> handler

    public BlockingQueue<Map> outputQ = new LinkedBlockingQueue<>()

    public volatile boolean queueEmpty = true

    public int processed = 0
    private volatile boolean stopped = false
    private Object objectKey = null

    private CepEngine cep
    private Attribute[] attributes
    private String query

    SiddhiHandlerListener(CepEngine cep, Handler<T> handler, Attribute[] attributes, String query) {
        this.handler = handler
        this.cep = cep
        this.attributes = attributes
        this.query = query
    }


    @Override
    void receive(long timestamp, Event[] inEvents, Event[] removeEvents) {
        if (!stopped) {
            if (inEvents != null) {
                this.queueEmpty = false
                if (inEvents != null) {
                    inEvents.each { outputQ.offer(SiddhiUtils.toMap(attributes, it)) }
                }
            }
        } else {
            logger.warn("Stopped SiddhiHandlerListener still received arg1 an event!")
        }
    }

    @Override
    void run() {
        while (!stopped) {
            try {
                Object obj = outputQ.take()
                if (!handler.handle((T) obj)) {
                    logger.warn("CepStore subscriber failed to handle update. Statement: ${query}; Removing listeners and destroying the statement.")
                    this.stopped = true
                }
                this.processed++
                this.queueEmpty = outputQ.isEmpty()
            } catch (Exception e) {
                logger.warn("CepStore subscriber failed to handle update. Statement: ${query}; Removing listeners and destroying the statement.", e)
                this.stopped = true
            }
        }
    }

    void stop() {
        this.stopped = true
    }

    protected boolean getStopped() {
        return stopped
    }
}
