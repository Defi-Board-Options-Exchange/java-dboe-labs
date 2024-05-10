package com.ngontro86.server.dboe.rest

import com.ngontro86.common.annotations.RestService
import com.ngontro86.server.dboe.services.SpotQueryService
import io.swagger.annotations.Api
import io.swagger.annotations.ApiOperation
import org.springframework.beans.factory.annotation.Autowired

import javax.ws.rs.*
import javax.ws.rs.core.MediaType

@RestService
@Path("/spot/query")
@Api(value = "/spot/query")
class SpotQueryRestService {

    @Autowired
    private SpotQueryService queryService

    @GET
    @Path('/listMarkets')
    @Produces(MediaType.APPLICATION_JSON)
    @Consumes(MediaType.APPLICATION_JSON)
    @ApiOperation(value = "Query all spot markets", response = Collection.class)
    Collection<Map> listMarkets(@QueryParam('chain') String chain) {
        return queryService.allMarkets(chain)
    }

    @GET
    @Path('/orderbook')
    @Produces(MediaType.APPLICATION_JSON)
    @Consumes(MediaType.APPLICATION_JSON)
    @ApiOperation(value = "Query orderbook of a spot market", response = Collection.class)
    Collection<Map> orderbook(@QueryParam("chain") String chain, @QueryParam("quoteToken") String quoteToken, @QueryParam("baseToken") String baseToken) {
        return queryService.orderbook(chain, quoteToken, baseToken)
    }

    @GET
    @Path('/fixedSpreads')
    @Produces(MediaType.APPLICATION_JSON)
    @Consumes(MediaType.APPLICATION_JSON)
    @ApiOperation(value = "Get fixed spreads Info", response = Collection.class)
    Collection<Map> getFixedSpreads() {
        return queryService.getFixedSpreads()
    }

    @GET
    @Path('/clobSpecs')
    @Produces(MediaType.APPLICATION_JSON)
    @Consumes(MediaType.APPLICATION_JSON)
    @ApiOperation(value = "Get number of price level", response = Map.class)
    Map getClobSpecs() {
        return queryService.onchainClobSpecs()
    }

    @GET
    @Path('/trades')
    @Produces(MediaType.APPLICATION_JSON)
    @Consumes(MediaType.APPLICATION_JSON)
    @ApiOperation(value = "Query historical trades", response = Collection.class)
    Collection<Map> trades(@QueryParam("walletId") String walletId, @QueryParam("chain") String chain) {
        return queryService.trades(walletId, chain)
    }

    @GET
    @Path('/dashboard')
    @Produces(MediaType.APPLICATION_JSON)
    @Consumes(MediaType.APPLICATION_JSON)
    @ApiOperation(value = "Get Spot Dashboard", response = Collection.class)
    Collection<Map> spotDashboard(@QueryParam("chain") String chain) {
        return queryService.spotDashboard(chain)
    }

    @GET
    @Path('/stats')
    @Produces(MediaType.APPLICATION_JSON)
    @Consumes(MediaType.APPLICATION_JSON)
    @ApiOperation(value = "Get Spot Stats", response = Collection.class)
    Collection<Map> stats(@QueryParam("chain") String chain) {
        return queryService.stats(chain)
    }
}
