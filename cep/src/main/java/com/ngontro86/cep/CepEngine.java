package com.ngontro86.cep;

import com.ngontro86.common.Handler;

import java.util.List;
import java.util.Map;

public interface CepEngine {

    boolean accept(Object obj);

    List<Map<String, Object>> queryMap(String query);

    List<Object> queryObj(String query);

    boolean registerMapHandler(String query, Handler<Map<String, Object>> handler);

    boolean registerObjectHandler(String query, Handler<Object> handler);

    boolean registerSubscriber(String query, Object subscriber);

    boolean unregisterHandler(Handler<? extends Object> handler);

    void unregisterMapHandler(Handler<Map<String, Object>> handler);

    long getCurrentTimeMillis();

    void cleanListeners();

    void terminate();
}
