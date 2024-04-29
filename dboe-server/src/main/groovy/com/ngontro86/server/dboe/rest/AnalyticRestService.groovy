package com.ngontro86.server.dboe.rest

import com.ngontro86.common.annotations.RestService
import com.ngontro86.server.dboe.services.AnalyticService
import com.ngontro86.server.dboe.services.analytic.PortfolioRisk
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
    @Path('/greek/{underlying}/{walletId}')
    @Produces(MediaType.APPLICATION_JSON)
    @Consumes(MediaType.APPLICATION_JSON)
    @ApiOperation(value = "Compute Greeks for this particular wallet", response = PortfolioRisk.class)
    PortfolioRisk singleWallet(@PathParam('underlying') String underlying, @PathParam('walletId') String walletId) {
        return analyticService.greeks([underlying], [walletId])
    }

    @GET
    @Path('/greeks')
    @Produces(MediaType.APPLICATION_JSON)
    @Consumes(MediaType.APPLICATION_JSON)
    @ApiOperation(value = "Compute Greeks for multiple wallets. Addresses are separated by comma", response = PortfolioRisk.class)
    PortfolioRisk multiWallets(@QueryParam('underlying') String underlying, @QueryParam('wallets') String wallets) {
        return analyticService.greeks([underlying], wallets.split(",") as Collection)
    }

    @GET
    @Path('/allMarketGreeks')
    @Produces(MediaType.APPLICATION_JSON)
    @Consumes(MediaType.APPLICATION_JSON)
    @ApiOperation(value = "Compute Greeks for multiple wallets. Addresses are separated by comma", response = PortfolioRisk.class)
    PortfolioRisk allMarketGreeks(@QueryParam('wallets') String wallets) {
        return analyticService.greeks([], wallets.split(",") as Collection)
    }

    @GET
    @Path('/dmmQuote/{dmmAddr}/${instrId}')
    @Produces(MediaType.APPLICATION_JSON)
    @Consumes(MediaType.APPLICATION_JSON)
    @ApiOperation(value = "Return the quotes from this DMM to a particular Option", response = Collection.class)
    Collection<Map> dmmQuote(@PathParam("dmmAddr") String dmmAddr, @PathParam('instrId') String instrId) {
        return analyticService.dmmQuote(dmmAddr, instrId)
    }

    @GET
    @Path('/dmmQuote/{dmmAddr}')
    @Produces(MediaType.APPLICATION_JSON)
    @Consumes(MediaType.APPLICATION_JSON)
    @ApiOperation(value = "Return all the quotes from this DMM", response = Collection.class)
    Collection<Map> dmmQuotes(@PathParam("dmmAddr") String dmmAddr) {
        return analyticService.dmmQuotes(dmmAddr)
    }
}
