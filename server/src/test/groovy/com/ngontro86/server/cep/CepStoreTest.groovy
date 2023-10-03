package com.ngontro86.server.cep

import com.ngontro86.common.net.SocketData
import com.ngontro86.common.net.SocketPublisher
import com.ngontro86.common.serials.ObjEvent
import com.ngontro86.common.serials.ObjMap
import com.ngontro86.common.util.IOUtils
import com.ngontro86.utils.Utils
import org.junit.Ignore
import org.junit.Test

@Ignore
class CepStoreTest {

    @Test
    void "should subscribe"() {
        SocketPublisher<ObjEvent, Object> publisher = new SocketPublisher<ObjEvent, Object>("", "35.231.65.181", 7771, new Handler(), false)
        /*
        publisher.handle(new ObjMap('PairOrderEvent',
                [
                        'strategyName'              : 'Test',
                        'pairName'                  : 'TestPair',
                        'broker'                    : 'IB',
                        'account'                   : 'U09',
                        'portfolio'                 : 'Global',
                        'firstLegInstId'            : 'I1',
                        'secondLegPrimaryInstId'    : 'I1',
                        'firstLegMultiplier'        : 10d,
                        'secondLegPrimaryMultiplier': 50d,
                        'refBasis'                  : 10d,
                        'basis'                     : 11d,
                        'maxLoss'                   : 11d,
                        'qty'                       : 1,
                        'secondLegQty'              : 1.6d,
                        'pairReqId'                 : 'req-3',
                        'timestamp'                 : 10l
                ]))
        sleep 1000
        */
        def query = "select * from SignalWin"
        IOUtils.requestObjectSnapshot(query, "Signal", publisher)
        while (true) {
            Utils.pause(1000)
        }
    }

    class Handler implements com.ngontro86.common.Handler<SocketData<ObjEvent>> {
        boolean handle(SocketData<ObjEvent> obj) {
            def event = obj.data.event
            println event
            //println "${event.timestamp}, ${event.hl},${event.micro_price},${event.imbavg},${event.mpavg},${event.stdev}"
            return true
        }
    }

}
