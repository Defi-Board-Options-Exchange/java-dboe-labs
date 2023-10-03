package com.ngontro86.server.integration

import com.ngontro86.app.common.db.DataSourceProvider
import com.ngontro86.app.common.db.FlatDao
import com.ngontro86.app.common.db.FlatDaoProvider
import com.ngontro86.app.common.postprocessor.ConfigValuePostProcessor
import com.ngontro86.app.common.postprocessor.LoggerPostProcessor
import com.ngontro86.common.biz.sim.MatchingEngineSimulator
import com.ngontro86.common.biz.watcher.PriceWatcher
import com.ngontro86.common.net.SocketPublisher
import com.ngontro86.common.serials.ObjEvent
import com.ngontro86.common.serials.ObjMap
import com.ngontro86.component.testing.ComponentEnv
import com.ngontro86.utils.Utils
import org.junit.Before
import org.junit.Ignore
import org.junit.Test

import static com.ngontro86.common.times.GlobalTimeController.getCurrentTimeMillis

@Ignore
class IbSimulator {

    ComponentEnv env

    SocketPublisher<ObjEvent, Object> publisher

    MatchingEngineSimulator sim
    FlatDao flatDao

    @Before
    void init() {
        System.setProperty("org.apache.commons.logging.LogFactory", "org.apache.commons.logging.impl.LogFactoryImpl")
        System.setProperty("org.apache.commons.logging.Log", "org.apache.commons.logging.impl.NoOpLog")
        System.setProperty("user.timezone", "Asia/Singapore")

        [
                "PriceWatcher.queries"        : "@Name('MatchingSim_Price') select * from PriceWin",
                "PriceWatcher.subscriptionObj": "localhost:7771",
                "Sim.subscriptionObj"         : "localhost:7771",
                'datasource.host'             : 'localhost',
                'datasource.name'             : 'ngontro86',
                'datasource.username'         : 'user_rw',
                'datasource.password'         : '251120Vi'
        ].each { k, v -> System.setProperty(k, v) }

        publisher = new SocketPublisher<ObjEvent, Object>("", "localhost", 7771, null, false)
        env = ComponentEnv.env([DataSourceProvider, FlatDaoProvider, ConfigValuePostProcessor, PriceWatcher, MatchingEngineSimulator, LoggerPostProcessor])
        sim = env.component(MatchingEngineSimulator)
        //flatDao = env.component(FlatDao)
    }

    @Test
    void "publish prices"() {
        def simMkt = { mp ->
            def diff = ((int) (Math.random() * 2)) * 5
            def ask = mp + diff
            def bid = ask - diff
            def micro = (bid + ask) / 2
            def spot = micro - 50
            [
                    new ObjMap('PriceEvent', [
                            'inst_id'    : 'NKYH20',
                            'bid'        : bid,
                            'ask'        : ask,
                            'last_price' : micro,
                            'micro_price': micro,
                            'last_size'  : 1,
                            'bid_size'   : 1,
                            'ask_size'   : 1,
                            'open_price' : 21500,
                            'close_price': 21250,
                            'timestamp'  : getCurrentTimeMillis()
                    ]),
                    new ObjMap('SpotEvent', [
                            'underlying' : 'NKY',
                            'spot'       : spot,
                            'open_price' : 21500.0,
                            'close_price': 21250.0,
                            'timestamp'  : getCurrentTimeMillis()
                    ])
            ]
        }
        def mps = [21505.0, 21510.0, 21510.0, 21505.0, 21510.0, 21505.0, 21510.0, 21515.0, 21510.0, 21505.0, 21500.0, 21495.0]
        100.times { loop ->
            println "Running ${loop}..."
            mps.each { mp ->
                simMkt(mp).each { mkt ->
                    publisher.handle(mkt)
                    sleep 1500
                }
            }
        }

        while (true) {
            Utils.pause(1000)
        }
    }
}
