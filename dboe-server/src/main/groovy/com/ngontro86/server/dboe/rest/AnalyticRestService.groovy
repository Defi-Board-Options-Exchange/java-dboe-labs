package com.ngontro86.server.dboe.rest

import com.ngontro86.common.annotations.RestService
import com.ngontro86.market.web3j.GreekRisk
import com.ngontro86.server.dboe.services.AnalyticService
import io.swagger.annotations.Api
import io.swagger.annotations.ApiOperation
import org.springframework.beans.factory.annotation.Autowired

import javax.ws.rs.*
import javax.ws.rs.core.MediaType

@RestService
@Path("/analytic")
@Api(value = "/analytic")
class AnalyticRestService {

    @Autowired
    private AnalyticService analyticService

    @GET
    @Path('/greek/{walletId}')
    @Produces(MediaType.APPLICATION_JSON)
    @Consumes(MediaType.APPLICATION_JSON)
    @ApiOperation(value = "Compute Greeks for this particular wallet", response = Map.class)
    Map<String, GreekRisk> singleWallet(@PathParam('walletId') String walletId) {
        return analyticService.greeks([walletId])
    }

    @GET
    @Path('/greeks')
    @Produces(MediaType.APPLICATION_JSON)
    @Consumes(MediaType.APPLICATION_JSON)
    @ApiOperation(value = "Compute Greeks for multiple wallets. Addresses are separated by comma", response = Map.class)
    Map<String, GreekRisk> multiWallets(@QueryParam('wallets') String wallets) {
        return analyticService.greeks(wallets.split(",") as Collection)
    }
}
