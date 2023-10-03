package com.ngontro86.server.integration

import com.ngontro86.common.net.SocketData
import com.ngontro86.common.net.SocketPublisher
import com.ngontro86.common.serials.ObjEvent
import com.ngontro86.common.serials.ObjMap
import com.ngontro86.common.times.GlobalTimeController
import com.ngontro86.common.util.IOUtils
import com.ngontro86.utils.Utils
import org.junit.Before
import org.junit.Ignore
import org.junit.Test

@Ignore
class TestServer {
    SocketPublisher<ObjEvent, Object> publisher

    @Before
    void init() {
        publisher = new SocketPublisher<ObjEvent, Object>("", "localhost", 7771, new Handler(), false)
    }

    @Test
    void "should validate Order req"() {
        IOUtils.requestObjectSubscription("select * from MMSpreadAdjWin", "SpreadAdj", publisher)
        IOUtils.requestObjectSubscription("select * from TradeEvent(status='Filled')", "Trade", publisher)
        IOUtils.requestObjectSubscription("select * from PortfolioWin", "Portfolio", publisher)

        /*
        publisher.handle(new ObjMap('PortfolioEvent', [
                'component'   : 'trading',
                'portfolio'   : "T 14",
                'inst_id'     : 'NIFTYX18',
                'broker'      : 'IB',
                'account'     : 'U9361398',
                'size'        : -1,
                'abs_size'    : -2,
                'abs_notional': -1050000.0,
                'notional'    : 60000.0,
                'timestamp'   : GlobalTimeController.currentTimeMillis,
        ]))

        publisher.handle(new ObjMap('OrderMapEvent', [
                'portfolio'        : "T 14",
                'inst_id'          : 'NIFTYX18',
                'broker'           : 'IB',
                'account'          : 'U9361398',
                'side'             : 1,
                'order_req_id'     : 'E-1-MM 14-T 14-NIFTYX18-1',
                'exchange_order_id': '101',
                'timestamp'        : GlobalTimeController.currentTimeMillis,
        ]))
        */

        while (true) {
            Utils.pause(1000)
        }
    }

    class Handler implements com.ngontro86.common.Handler<SocketData<ObjEvent>> {
        boolean handle(SocketData<ObjEvent> obj) {
            println "${new Date().toString()}, ${obj.data.eventID} - ${System.currentTimeMillis() - obj.data.event['timestamp']} - ${obj.data.event}"
            return true
        }
    }
}
