package com.ngontro86.tradingserver.rest

import com.ngontro86.common.annotations.Logging
import com.ngontro86.common.annotations.RestService
import io.swagger.annotations.Api
import io.swagger.annotations.ApiOperation
import org.apache.log4j.Logger
import org.springframework.beans.factory.annotation.Autowired

import javax.ws.rs.*
import javax.ws.rs.core.MediaType

@RestService
@Path("/bookingService")
@Api(value = "/bookingService", description = "To book manual trades")
class BookingRestService {

    @Logging
    private Logger logger

    @Autowired
    private BookingService bookingService

    @Autowired
    private CepAuthenticator cepAuthenticator

    @POST
    @Path('/manualBook')
    @Consumes(MediaType.APPLICATION_JSON)
    @ApiOperation(value = "Manually book", response = Void.class)
    void manualBook(@HeaderParam("passCode") String passCode,
                    @QueryParam("broker") String broker,
                    @QueryParam("portfolio") String portfolio,
                    @QueryParam("account") String account,
                    @QueryParam("instId") String instId,
                    @QueryParam("qty") int qty,
                    @QueryParam("avgPrice") double avgPrice) {
        if(!cepAuthenticator.auth(passCode)) {
            return
        }
        logger.info("Booking manual entry, instId: ${instId}, qty: ${qty}, avg price: ${avgPrice}")
        bookingService.book(broker, portfolio, account, instId, qty, avgPrice)
    }

}
