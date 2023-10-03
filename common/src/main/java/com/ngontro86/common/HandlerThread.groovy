package com.ngontro86.common


import org.apache.logging.log4j.LogManager
import org.apache.logging.log4j.Logger

import java.util.concurrent.BlockingQueue
import java.util.concurrent.LinkedBlockingQueue

class HandlerThread<T> implements Runnable, Handler<T> {
    protected Logger logger = LogManager.getLogger(HandlerThread)

    private Handler<T> handler
    private BlockingQueue<T> q = new LinkedBlockingQueue<T>()

    @Override
    void run() {
        while (true) {
            try {
                this.handler.handle(q.take())
            } catch (Exception e) {
                logger.error("run got exception", e)
            }
        }
    }

    @Override
    boolean handle(T obj) {
        try {
            q.put(obj)
        } catch (Exception e) {
            logger.error(e)
            return false
        }
        return true
    }

    Handler<T> getHandler() {
        return handler
    }

    void setHandler(Handler<T> handler) {
        this.handler = handler
    }
}
