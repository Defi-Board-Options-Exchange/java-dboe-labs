package com.ngontro86.common.net;

public class SocketData<T> {
    public String connectionID;

    public T data;

    public SocketData(String id, T data) {
        this.connectionID = id;
        this.data = data;
    }

    public String getConnectionID() {
        return connectionID;
    }

    public void setConnectionID(String connectionID) {
        this.connectionID = connectionID;
    }

    public T getData() {
        return data;
    }

    public void setData(T data) {
        this.data = data;
    }
}
