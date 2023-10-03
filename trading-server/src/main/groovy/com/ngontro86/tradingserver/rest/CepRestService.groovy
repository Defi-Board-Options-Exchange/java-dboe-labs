package com.ngontro86.tradingserver.rest

import com.ngontro86.common.annotations.Logging
import com.ngontro86.common.annotations.RestService
import com.ngontro86.common.biz.entity.OrderMap
import com.ngontro86.common.biz.entity.OrderReq
import com.ngontro86.common.biz.entity.SimpleOrder
import com.ngontro86.common.times.GlobalTimeController
import com.ngontro86.common.util.BeanUtils
import com.ngontro86.utils.StringUtils
import io.swagger.annotations.Api
import io.swagger.annotations.ApiOperation
import org.apache.log4j.Logger
import org.springframework.beans.factory.annotation.Autowired

import javax.ws.rs.*
import javax.ws.rs.core.MediaType

@RestService
@Path("/cepService")
@Api(value = "/cepService", description = "To interactive with Cep")
class CepRestService {

    @Logging
    private Logger logger

    @Autowired
    private CepService cepService

    @Autowired
    private QueryService queryService

    @Autowired
    private CepAuthenticator cepAuthenticator

    @POST
    @Path('/updateEvent')
    @Consumes(MediaType.APPLICATION_JSON)
    @ApiOperation(value = "Update an event", response = Void.class)
    void updateEvent(@HeaderParam("passCode") String passCode,
                     @QueryParam("eventName") String eventName,
                     Map event) {
        logger.info("Updating event: passCode: ${passCode}, event name: ${eventName}, event: ${event}")
        if (!cepAuthenticator.auth(passCode)) {
            return
        }
        cepService.updateEvent(eventName, event)
    }

    @POST
    @Path('/submitBuy')
    @Consumes(MediaType.APPLICATION_JSON)
    @Produces(MediaType.APPLICATION_JSON)
    @ApiOperation(value = "Submit Buy order")
    SimpleOrder submitBuyOrder(SimpleOrder order) {
        order.setExchangeOrderId(UUID.randomUUID().toString())
        order.setTimestamp(GlobalTimeController.getCurrentTimeMillis())
        queryService.submitOrder(order)
        return order
    }

    @POST
    @Path('/cancelOrder')
    @Consumes(MediaType.APPLICATION_JSON)
    @ApiOperation(value = "Submit Buy order", response = Void.class)
    void cancelOrder(@QueryParam("orderReqId")  String orderReqId) {
        logger.info("Cancel order: orderId: ${orderReqId}")
    }

    @POST
    @Path('/submitSell')
    @Consumes(MediaType.APPLICATION_JSON)
    @Produces(MediaType.APPLICATION_JSON)
    @ApiOperation(value = "Submit Buy order")
    SimpleOrder submitSellOrder(SimpleOrder order) {
        logger.info("Submit SELL order: event: ${order}")
        order.setExchangeOrderId(UUID.randomUUID().toString())
        order.setTimestamp(GlobalTimeController.getCurrentTimeMillis())
        queryService.submitOrder(order)
        return order
    }

    @POST
    @Path('/updateSpot')
    @Consumes(MediaType.APPLICATION_JSON)
    @ApiOperation(value = "Update spot", response = Void.class)
    void updateSpot(@HeaderParam("passCode") String passCode,
                    @QueryParam("underlying") String underlying,
                    @QueryParam("spot") double spot) {
        logger.info("Updating event: passCode: ${passCode}, event name: ${underlying}, spot: ${spot}")
        if (!cepAuthenticator.auth(passCode)) {
            return
        }
        cepService.updateEvent('SpotEvent',
                [
                        'underlying' : underlying,
                        'spot'       : spot,
                        'open_price' : 0d,
                        'close_price': 0d,
                        'timestamp'  : GlobalTimeController.getCurrentTimeMillis()
                ])
    }

    @POST
    @Path('/updateMM')
    @Consumes(MediaType.APPLICATION_JSON)
    @ApiOperation(value = "Update market making param", response = Void.class)
    void updateMM(@HeaderParam("passCode") String passCode,
                  @QueryParam("name") String mmName,
                  @QueryParam("qAliasInstId") String qAliasInstId,
                  Map event) {
        logger.info("Updating market making param: passCode: ${passCode}, mm name: ${mmName}, q alias inst id: ${qAliasInstId}, event: ${event}")
        if (!cepAuthenticator.auth(passCode)) {
            return
        }
        cepService.updateMM(mmName, qAliasInstId, event)
    }

    @POST
    @Path('/updateMM2')
    @Consumes(MediaType.APPLICATION_JSON)
    @ApiOperation(value = "Update market making param", response = Void.class)
    void updateMM2(@HeaderParam("passCode") String passCode,
                   @QueryParam("name") String mmName,
                   @QueryParam("qAliasInstId") String qAliasInstId,
                   @QueryParam("minSize") int minSize,
                   @QueryParam("minSpreadTick") int minSpreadTick,
                   @QueryParam("incrSpreadTick") int incrSpreadTick,
                   @QueryParam("contIncrSpreadTicks") String contIncrSpreadTicks,
                   @QueryParam("jumpSensitivity") double jumpSense,
                   @QueryParam("maxHoldingPosition") int maxHoldingPosition,
                   @QueryParam("maxHoldingPeriod") int maxHoldingPeriod,
                   @QueryParam("startingTime") int startingTime,
                   @QueryParam("endingTime") int endingTime
    ) {
        logger.info("Updating market making param: passCode: ${passCode}, mm name: ${mmName}, q alias inst id: ${qAliasInstId}")
        if (!cepAuthenticator.auth(passCode)) {
            return
        }
        def map = [
                'min_size'            : minSize,
                'min_spread'          : minSpreadTick,
                'incremental_spread'  : incrSpreadTick,
                'jump_sensitivity'    : jumpSense,
                'max_holding_position': maxHoldingPosition,
                'max_holding_period'  : maxHoldingPeriod,
                'starting_time'       : startingTime,
                'ending_time'         : endingTime
        ].findAll { k, v -> v > 0 }

        if (!StringUtils.isEmpty(contIncrSpreadTicks)) {
            def contIncrSpreadTicksTok = contIncrSpreadTicks.split(",").collect { Double.valueOf(it) } as Double[]
            map << [
                    'cont_incr_st_spread' : contIncrSpreadTicksTok[0],
                    'cont_incr_mt_spread' : contIncrSpreadTicksTok.length >= 2 ? contIncrSpreadTicksTok[1] : -1.0,
                    'cont_incr_lt_spread' : contIncrSpreadTicksTok.length >= 3 ? contIncrSpreadTicksTok[2] : -1.0,
                    'cont_incr_vlt_spread': contIncrSpreadTicksTok.length >= 4 ? contIncrSpreadTicksTok[3] : -1.0
            ]
        }

        cepService.updateMM(mmName, qAliasInstId, map)
    }

    @GET
    @Path('/allMM')
    @Produces(MediaType.APPLICATION_JSON)
    @Consumes(MediaType.APPLICATION_JSON)
    @ApiOperation(value = "Query all mm params", response = Collection.class)
    Collection<Map> allMMs() {
        return cepService.allMMs()
    }

}
