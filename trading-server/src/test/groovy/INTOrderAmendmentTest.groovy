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
class INTOrderAmendmentTest {

    SocketPublisher<ObjEvent, Object> publisher

    @Before
    void init() {
        publisher = new SocketPublisher<ObjEvent, Object>("", "localhost", 7771, new Handler(), false)
    }

    @Test
    void "should not have Order with the same px or quantity"() {
        IOUtils.requestObjectSubscription("select * from OrderReqWin", "Order", publisher)

        publisher.handle(new ObjMap('OrderReqEvent', [
                'portfolio'        : "T 14",
                'inst_id'          : 'FIDV20',
                'broker'           : 'IB',
                'account'          : 'U9361398',
                'qty'              : 1,
                'price'            : 2700,
                'order_req_id'     : 'Test',
                'exchange_order_id': null,
                'timestamp'        : GlobalTimeController.currentTimeMillis,
        ]))

        Double prevPx = 2700
        100.times {
            prevPx += (int) ((Math.random() - 0.5) * 4)
            publisher.handle(new ObjMap('OrderReqEvent', [
                    'portfolio'        : "T 14",
                    'inst_id'          : 'FIDV20',
                    'broker'           : 'IB',
                    'account'          : 'U9361398',
                    'qty'              : 1,
                    'price'            : prevPx,
                    'order_req_id'     : 'Test',
                    'exchange_order_id': 'X1',
                    'timestamp'        : GlobalTimeController.currentTimeMillis,
            ]))
            println "${prevPx}"
            Utils.pause(100)
        }

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
