package com.ngontro86.tradingserver.rest

import com.ngontro86.cep.CepEngine
import com.ngontro86.common.annotations.DBOEComponent
import com.ngontro86.common.serials.ObjMap
import com.ngontro86.common.times.GlobalTimeController

import javax.inject.Inject
import java.util.concurrent.atomic.AtomicInteger

@DBOEComponent
class BookingService {

    @Inject
    private CepEngine cepEngine

    private AtomicInteger manualBookingOrder = new AtomicInteger(0)

    void book(String broker, String portfolio, String account, String instId, int qty, double avgPrice) {
        cepEngine.accept(new ObjMap('TradeEvent',
                [
                        'inst_id'          : instId,
                        'order_req_id'     : "Manual-${manualBookingOrder.get()}".toString(),
                        'exchange_order_id': "Ex-Manual-${manualBookingOrder.getAndIncrement()}".toString(),
                        'broker'           : broker,
                        'account'          : account,
                        'portfolio'        : portfolio,
                        'size'             : qty,
                        'avg_price'        : avgPrice,
                        'status'           : 'Filled',
                        'timestamp'        : GlobalTimeController.currentTimeMillis
                ]))
    }
}
