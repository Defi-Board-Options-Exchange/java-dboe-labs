package com.ngontro86.common.biz.watcher;

import com.ngontro86.common.Handler;
import com.ngontro86.common.Observer;
import com.ngontro86.common.net.SocketData;
import com.ngontro86.common.net.SocketPublisher;
import com.ngontro86.common.serials.ObjEvent;
import com.ngontro86.common.util.IOUtils;
import com.ngontro86.utils.Utils;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.util.LinkedList;
import java.util.List;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.LinkedBlockingQueue;

import static com.ngontro86.common.config.Configuration.config;

public abstract class AbstractWatcher<T, O> implements Runnable, Handler<SocketData<ObjEvent>> {
    protected Logger logger = LogManager.getLogger(AbstractWatcher.class);
    protected volatile long total, ok, err;
    private SocketPublisher<ObjEvent, Object> pub;
    private String watcherName, subQ, snapQ;
    private BlockingQueue<O> q = new LinkedBlockingQueue<>();
    private ConcurrentHashMap<T, O> cache = new ConcurrentHashMap<>();
    private Observer<T, O> globalObserver;
    private ConcurrentHashMap<T, LinkedList<Observer<T, ? extends Object>>> observers = new ConcurrentHashMap<T, LinkedList<Observer<T, ? extends Object>>>();
    private volatile boolean ready;

    void init(String watcherId) {
        this.watcherName = watcherId;

        String subscriptionObj = config().getConfig(watcherId + ".subscriptionObj");
        String[] queries = config().getConfig(watcherId + ".queries").split(";");

        String[] subTokens = subscriptionObj.split(":");
        this.pub = new SocketPublisher<>(watcherName + "_Watcher", subTokens[0], Utils.toInt(subTokens[1], 7771), this, false);
        subQ = queries[0];
        snapQ = queries.length > 1 ? queries[1] : null;

        Utils.startThread(this, Thread.MIN_PRIORITY);
    }

    public abstract T getKey(O obj);

    public abstract O getValue(Object obj);

    public abstract boolean isEqual(O value1, O value2);

    public O get(T key) {
        return cache.get(key);
    }

    public synchronized String getStatus() {
        return String.format("Watcher:%s;total:%d;ok:%d;error:%d;observers cnt:%d;cache size:%d", watcherName, total, ok, err, observers.size(), cache.size());
    }

    public void setGlobalObserver(Observer<T, O> observer) {
        this.globalObserver = observer;
    }

    public ConcurrentHashMap<T, O> getCache() {
        return cache;
    }

    public void addObserver(T type, Observer<T, ? extends Object> observer) {
        if (!observers.containsKey(type)) {
            observers.put(type, new LinkedList<>());
        }
        List<Observer<T, ? extends Object>> listObserver = observers.get(type);
        synchronized (listObserver) {
            listObserver.add(observer);
        }
    }

    public void removeObserver(T type, Observer<T, ? extends Object> observer) {
        if (!observers.containsKey(type)) {
            return;
        }
        List<Observer<T, ? extends Object>> listObserver = observers.get(type);
        synchronized (listObserver) {
            listObserver.remove(observer);
        }
    }

    @SuppressWarnings("unchecked")
    private void process(O obj) {
        T key = getKey(obj);
        O oldObj = cache.put(key, obj);
        if (needUpdate(obj, oldObj)) {
            updateGlobalObserver(obj, key, oldObj);
            updateAdhocObservers(obj, key, oldObj);
        }
    }

    private void updateGlobalObserver(O obj, T key, O oldObj) {
        if (this.globalObserver != null) {
            this.globalObserver.update(key, obj, oldObj);
        }
    }

    private void updateAdhocObservers(O obj, T key, O oldObj) {
        if (!observers.containsKey(key)) {
            return;
        }
        LinkedList<Observer<T, ? extends Object>> listObserver = observers.get(key);

        LinkedList<Observer<T, ? extends Object>> toProcess;
        synchronized (this.observers) {
            toProcess = (LinkedList<Observer<T, ? extends Object>>) listObserver.clone();
        }

        for (Observer<T, ? extends Object> observer : toProcess) {
            observer.update(key, obj, oldObj);
        }
    }

    private boolean needUpdate(O obj, O oldObj) {
        return oldObj == null || !isEqual(obj, oldObj);
    }

    @Override
    public boolean handle(SocketData<ObjEvent> event) {
        return processOneEvent(event.data.event);
    }

    private boolean processOneEvent(Object event) {
        try {
            q.offer(getValue(event));
            return true;
        } catch (Exception e) {
            logger.error("processOneEvent: ", e);
            return false;
        }
    }

    public boolean waitReady(int timeOut) {
        long startingTime = System.currentTimeMillis();
        while (!this.ready && (System.currentTimeMillis() - startingTime < timeOut)) {
            Utils.pause(50);
        }
        return this.ready;
    }

    @Override
    public void run() {
        snapshotIfSet();
        this.ready = true;
        subscribeIfSet();

        constantPolling();
    }

    private void constantPolling() {
        while (true) {
            try {
                O obj = q.take();
                process(obj);
                total++;
            } catch (Exception e) {
                logger.error("failed to take one element from the queue. Exception: ", e);
            }
        }
    }

    private void subscribeIfSet() {
        if (subQ != null) {
            IOUtils.requestObjectSubscription(subQ, this.watcherName, pub);
        }
    }

    private void snapshotIfSet() {
        if (this.snapQ != null) {
            List<Object> l = IOUtils.syncObject(snapQ, watcherName, pub, 20000);
            for (Object oneObj : l) {
                if (!processOneEvent(oneObj)) {
                    continue;
                }
            }
            logger.info(String.format("Watcher: %s; SYNCED O QUERY: %s", watcherName, snapQ));
        }
    }
}
