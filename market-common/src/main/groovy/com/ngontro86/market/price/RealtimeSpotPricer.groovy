package com.ngontro86.market.price

import com.ngontro86.restful.common.client.RestClient

import javax.annotation.PostConstruct
import java.util.concurrent.ConcurrentHashMap
import java.util.concurrent.Executors
import java.util.concurrent.ScheduledExecutorService
import java.util.concurrent.TimeUnit

import static com.ngontro86.restful.common.client.RestClientBuilder.build

class RealtimeSpotPricer implements SpotPricer {

    Map<String, Double> latest = [:] as ConcurrentHashMap
    Map<String, Double> prevs = [:] as ConcurrentHashMap

    private RestClient restClient
    private final ScheduledExecutorService scheduler = Executors.newSingleThreadScheduledExecutor()

    @PostConstruct
    private void init() {
        restClient = build('dboeHost')
        scheduler.scheduleAtFixedRate(new Runnable() {
            @Override
            void run() {
                reload()
            }
        }, 30, 300, TimeUnit.SECONDS)
    }

    private void reload() {
        def spots = restClient.withQueryParams('query/query', ['query': 'select spot, underlying from DboeSpotWin'], Collection)
        spots.each {
            update(it['underlying'], it['spot'])
        }
    }

    @Override
    Double spot(String underlying) {
        return latest.get(underlying)
    }

    @Override
    void update(String underlying, double spot) {
        def prev = latest.put(underlying, spot)
        if (prev != null) {
            prevs.put(underlying, prev)
        }
    }
}
