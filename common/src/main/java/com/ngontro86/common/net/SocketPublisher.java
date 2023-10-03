package com.ngontro86.common.net;

import com.ngontro86.common.Handler;
import com.ngontro86.utils.Utils;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.net.Socket;

public class SocketPublisher<RECV, SEND> implements PublisherHandler<SEND> {

    protected Logger logger = LogManager.getLogger(SocketPublisher.class);

    public String host;
    public int port;

    protected SocketConn<RECV, SEND> conn;
    protected Handler<SocketData<RECV>> handler;
    protected boolean initialized;

    public SocketPublisher(String publisherID, String host, int port, Handler<SocketData<RECV>> handler, boolean isRaw) {
        this.handler = handler;
        this.host = host;
        this.port = port;
        this.initialized = init(publisherID, host, port, handler, isRaw);
    }

    private boolean init(String publisherID, String host, int port, Handler<SocketData<RECV>> handler, boolean isRaw) {
        try {
            this.conn = new SocketConn<>(publisherID, new Socket(host, port), handler, isRaw);
            Utils.startThread(conn, Thread.NORM_PRIORITY);
        } catch (Exception e) {
            logger.error("Could not establish a socket connection to: " + host + ", port: " + port + ". Exception: ", e);
            return false;
        }
        return true;
    }

    @Override
    public boolean handle(SEND obj) {
        if (this.conn != null) {
            this.conn.handle(obj);
        }
        return true;
    }

    @Override
    public void stop() {
        try {
            conn.stop();
        } catch (Exception e) {
        }
    }
}
