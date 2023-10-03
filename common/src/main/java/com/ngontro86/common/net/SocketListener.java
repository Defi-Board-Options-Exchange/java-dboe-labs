package com.ngontro86.common.net;

import com.ngontro86.common.Handler;
import com.ngontro86.utils.Utils;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.net.ServerSocket;
import java.net.Socket;
import java.util.Iterator;
import java.util.Map.Entry;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.atomic.AtomicInteger;

public class SocketListener<RECV, SEND> implements Runnable, Handler<SocketData<SEND>> {
    private Logger logger = LogManager.getLogger(SocketListener.class);

    private Handler<SocketData<RECV>> handler;

    private ServerSocket socket;

    private int port;
    private boolean isRaw;

    private ConcurrentHashMap<String, SocketConn<RECV, SEND>> allSockets;

    private volatile boolean stopped;
    private AtomicInteger socketCount = new AtomicInteger(0);

    public SocketListener(int port, Handler<SocketData<RECV>> handler, boolean isRaw) {
        this.handler = handler;
        this.port = port;
        this.isRaw = isRaw;
        this.allSockets = new ConcurrentHashMap<>();
    }

    @Override
    public boolean handle(SocketData<SEND> obj) {
        SocketConn<RECV, SEND> conn = allSockets.get(obj.connectionID);
        if (conn != null) conn.handle(obj.data);
        return true;
    }

    @Override
    public void run() {
        this.stopped = !initSock();
        while (!this.stopped) {
            try {
                Socket clSocket = socket.accept();
                logger.info(String.format("Got incoming connection. socket =%s", clSocket.toString()));
                cleanDeadConn();
                String clSocketID = getSocketID(socketCount.incrementAndGet());
                SocketConn<RECV, SEND> clConn = new SocketConn<RECV, SEND>(clSocketID, clSocket, this.handler, this.isRaw);
                allSockets.put(clSocketID, clConn);
                Utils.startThread(clConn, Thread.MIN_PRIORITY);
            } catch (Exception e) {
                logger.error("Fail to accept a new incoming connection: ", e);
                close();
            }
        }
    }

    private String getSocketID(int id) {
        return String.format("SocketConn_%d", id);
    }

    private void cleanDeadConn() {
        Iterator<Entry<String, SocketConn<RECV, SEND>>> it = allSockets.entrySet().iterator();
        while (it.hasNext()) {
            SocketConn<RECV, SEND> conn = it.next().getValue();
            if (conn.stop) {
                conn.stop();
                it.remove();
            }
        }
    }

    private void close() {
        try {
            if (socket != null && socket.isClosed()) socket.close();
            for (Entry<String, SocketConn<RECV, SEND>> it : allSockets.entrySet()) {
                it.getValue().stop();
            }
            allSockets.clear();
            this.stopped = true;
        } catch (Exception e) {
            logger.error("Couldnt close socket exception: ", e);
        }
    }

    private boolean initSock() {
        try {
            this.socket = new ServerSocket(this.port);
            logger.info("Open a socket listening on port: " + port);
            this.stopped = false;
            return true;
        } catch (Exception e) {
            logger.error("initSock fail: ", e);
            close();
            return false;
        }
    }

}
