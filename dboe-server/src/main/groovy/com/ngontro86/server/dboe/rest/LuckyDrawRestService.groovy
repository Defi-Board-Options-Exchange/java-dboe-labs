package com.ngontro86.server.dboe.rest

import com.ngontro86.common.annotations.Logging
import com.ngontro86.common.annotations.RestService
import com.ngontro86.server.dboe.services.LuckyDrawService
import com.ngontro86.server.dboe.services.luckydraw.LuckyDrawStats
import io.swagger.annotations.Api
import io.swagger.annotations.ApiOperation
import org.apache.logging.log4j.Logger
import org.springframework.beans.factory.annotation.Autowired

import javax.ws.rs.*
import javax.ws.rs.core.MediaType

@RestService
@Path("/luckydraw")
@Api(value = "/luckydraw")
class LuckyDrawRestService {

    @Logging
    private Logger logger

    @Autowired
    private LuckyDrawService luckyDrawService

    @GET
    @Path('/luckyOrNot/{walletId}')
    @Produces(MediaType.APPLICATION_JSON)
    @Consumes(MediaType.APPLICATION_JSON)
    @ApiOperation(value = "Find out whether this wallet got luck or not", response = Map.class)
    Map luckyOrNot(@PathParam('walletId') String walletId) {
        return luckyDrawService.luckyOrNot(walletId)
    }

    @GET
    @Path('/luckyStats')
    @Produces(MediaType.APPLICATION_JSON)
    @Consumes(MediaType.APPLICATION_JSON)
    @ApiOperation(value = "Get past lucky stats", response = LuckyDrawStats.class)
    LuckyDrawStats luckyStats() {
        return luckyDrawService.pastLuckyWallets()
    }

    @GET
    @Path('/gimme')
    @Produces(MediaType.APPLICATION_JSON)
    @Consumes(MediaType.APPLICATION_JSON)
    @ApiOperation(value = "Return the Option depending on the market trend", response = Map.class)
    Map gimme(@QueryParam('trend') String upDown) {
        logger.info("Gimme: ${upDown}")
        return luckyDrawService.gimme(upDown == 'Up')
    }
}
