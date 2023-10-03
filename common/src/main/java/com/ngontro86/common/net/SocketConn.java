package com.ngontro86.common.net;

import com.ngontro86.common.Handler;
import com.ngontro86.utils.Utils;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;


import java.io.*;
import java.net.Socket;

public class SocketConn<RECV, SEND> implements Runnable, Handler<SEND> {

    private Logger logger = LogManager.getLogger(SocketConn.class);

    public String socketID;

    public Handler<SocketData<RECV>> handler;

    public Socket socket;

    public boolean isRaw;

    public volatile boolean stop;

    private static final int MAX_TRIES = 10;

    private ObjectInputStream objIn;
    private ObjectOutputStream objOut;

    private InputStream rawIn;
    private OutputStream rawOut;

    private int resetCnt = 0;

    private byte[] buff = new byte[8 * 1024];

    public SocketConn(String connectionID, Socket sock, Handler<SocketData<RECV>> handler, boolean isRaw) {
        this.socketID = connectionID;
        this.socket = sock;
        this.handler = handler;
        this.isRaw = isRaw;
        this.stop = !connect();
    }

    @SuppressWarnings({"unchecked", "rawtypes"})
    @Override
    public void run() {
        int tried = 0;
        while (!stop && tried < MAX_TRIES) {
            if (socket == null) {
                continue;
            }
            RECV data = null;
            try {
                if (isRaw) {
                    int len = rawIn.read(buff);
                    if (len < 0) {
                        logger.error("read size < 0: reaching the end of socket stream. Terminating now");
                        stop();
                    }
                    byte[] msg = new byte[len];
                    System.arraycopy(buff, 0, msg, 0, len);
                    data = (RECV) msg;
                } else {
                    data = (RECV) objIn.readUnshared();
                }
            } catch (Exception e) {
                Utils.pause(100);
                tried++;
            }
            if (data != null) {
                this.handler.handle(new SocketData(socketID, data));
                tried = 0;
            }
        }
        logger.info(String.format("%s, exceeding maximum tries: %d (%d), stop the listener now!", socketID, MAX_TRIES, tried));
        stop();
    }

    public void stop() {
        try {
            if (socket != null && !socket.isClosed()) {
                socket.close();
            }
        } catch (Exception e) {
            logger.error("SocketConn exception: ", e);
        }
        this.stop = true;
    }

    @Override
    public synchronized boolean handle(SEND obj) {
        if (stop) {
            return true;
        }

        try {
            if (isRaw) {
                Object objSend = obj;
                this.rawOut.write((byte[]) objSend);
                this.rawOut.flush();
            } else {
                this.objOut.writeUnshared(obj);
                if (resetCnt++ > 200) {
                    objOut.reset();
                    resetCnt = 0;
                }
                this.objOut.flush();
            }
        } catch (IOException e) {
            logger.error("IOException when write to socket: ", e);
            stop();
            logger.error("Stop socket conn: " + socketID);
        }
        return true;
    }

    private boolean connect() {
        try {
            if (isRaw) {
                this.rawOut = socket.getOutputStream();
                this.rawOut.flush();
                this.rawIn = socket.getInputStream();
            } else {
                this.objOut = new ObjectOutputStream(new BufferedOutputStream(socket.getOutputStream()));
                this.objOut.flush();
                this.objIn = new ObjectInputStream(new BufferedInputStream(socket.getInputStream()));
            }
        } catch (Exception e) {
            logger.error(String.format("SocketID: %s couldn't create in/out stream. Terminate now... ", this.socketID), e);
            stop();
            return false;
        }
        return true;
    }
}
