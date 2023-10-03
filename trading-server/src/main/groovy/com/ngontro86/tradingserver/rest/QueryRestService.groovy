package com.ngontro86.tradingserver.rest

import com.ngontro86.common.annotations.RestService
import io.swagger.annotations.Api
import io.swagger.annotations.ApiOperation
import io.swagger.annotations.ApiParam
import org.springframework.beans.factory.annotation.Autowired

import javax.ws.rs.*
import javax.ws.rs.core.MediaType

@RestService
@Path("/queryService")
@Api(value = "/queryService")
class QueryRestService {

    @Autowired
    private QueryService queryService

    @GET
    @Path('/query')
    @Produces(MediaType.APPLICATION_JSON)
    @Consumes(MediaType.APPLICATION_JSON)
    @ApiOperation(value = "Query Core", response = Collection.class)
    Collection<Map> query(@ApiParam(value = "query", required = true) @QueryParam('query') String query) {
        return queryService.query(query)
    }

    @GET
    @Path('/currentPnL')
    @Produces(MediaType.APPLICATION_JSON)
    @Consumes(MediaType.APPLICATION_JSON)
    @ApiOperation(value = "Query Current PnL", response = Collection.class)
    Collection<Map> queryPnL() {
        return queryService.pnl()
    }

    @GET
    @Path('/getSignals')
    @Produces(MediaType.APPLICATION_JSON)
    @Consumes(MediaType.APPLICATION_JSON)
    @ApiOperation(value = "Query Signals", response = Collection.class)
    Collection<Map> querySignals() {
        queryService.signals()
    }

    @GET
    @Path('/getMarketSnapshot')
    @Produces(MediaType.APPLICATION_JSON)
    @Consumes(MediaType.APPLICATION_JSON)
    @ApiOperation(value = "Query Current Market", response = Collection.class)
    Collection<Map> queryMarket() {
        queryService.marketSnapshot()
    }

    @GET
    @Path('/getOneMarketSnapshot')
    @Produces(MediaType.APPLICATION_JSON)
    @Consumes(MediaType.APPLICATION_JSON)
    @ApiOperation(value = "Query One Market", response = Collection.class)
    Collection<Map> queryOneMarket(@ApiParam(value = "instId", required = true) @QueryParam('instId') String instId) {
        return queryService.oneMarketSnapshot(instId)
    }

    @GET
    @Path('/getPendingOrders')
    @Produces(MediaType.APPLICATION_JSON)
    @Consumes(MediaType.APPLICATION_JSON)
    @ApiOperation(value = "Query Pending Orders", response = Collection.class)
    Collection<Map> queryPendingOrders() {
        return queryService.pendingOrders()
    }

    @GET
    @Path('/allOrders')
    @Produces(MediaType.APPLICATION_JSON)
    @Consumes(MediaType.APPLICATION_JSON)
    @ApiOperation(value = "Query all Orders", response = Collection.class)
    Collection<Map> allOrders() {
        return queryService.pendingOrders()
    }

    @GET
    @Path('/currentPosition')
    @Produces(MediaType.APPLICATION_JSON)
    @Consumes(MediaType.APPLICATION_JSON)
    @ApiOperation(value = "Query Current Position", response = Collection.class)
    Collection<Map> queryPosition() {
        return queryService.position()
    }

    @GET
    @Path('/allInstruments')
    @Produces(MediaType.APPLICATION_JSON)
    @Consumes(MediaType.APPLICATION_JSON)
    @ApiOperation(value = "Query All Instruments", response = Map.class)
    Map<String, Collection> allInstruments() {
        return queryService.allInstruments()
    }

    @GET
    @Path('/strategies')
    @Produces(MediaType.APPLICATION_JSON)
    @Consumes(MediaType.APPLICATION_JSON)
    @ApiOperation(value = "Query All Strategies", response = Collection.class)
    Collection<Map> queryStrategies() {
        return queryService.strategy()
    }

    @POST
    @Path('/updateStrategies')
    @Produces(MediaType.APPLICATION_JSON)
    @Consumes(MediaType.APPLICATION_JSON)
    @ApiOperation(value = "Update Strategies", response = Collection.class)
    void updateStrategies(Collection<Map> strategies) {
        queryService.updateStrategies(strategies)
    }

    @GET
    @Path('/pastPerf')
    @Produces(MediaType.APPLICATION_JSON)
    @Consumes(MediaType.APPLICATION_JSON)
    @ApiOperation(value = "Query past PnL", response = Collection.class)
    Collection<Map> queryPastPerf() {
        return queryService.pastPerf()
    }

    @GET
    @Path('/getSettings')
    @Produces(MediaType.APPLICATION_JSON)
    @Consumes(MediaType.APPLICATION_JSON)
    @ApiOperation(value = "Get Settings", response = Map.class)
    Map getSettings() {
        return queryService.getSettings()
    }

    @POST
    @Path('/updateSettings')
    @Consumes(MediaType.APPLICATION_JSON)
    @ApiOperation(value = "Update Settings", response = Void.class)
    void updateSetting(Map<String, String> newSettings) {
        queryService.updateSetting(newSettings)
    }

    @POST
    @Path('/subscribeInstruments')
    @Consumes(MediaType.APPLICATION_JSON)
    @ApiOperation(value = "Instrument To subscribe ", response = Void.class)
    void subscribeInstrument(Map<String, Collection> newSettings) {
        queryService.updateInstruments(newSettings)
    }

    @GET
    @Path('/log')
    @Produces(MediaType.APPLICATION_JSON)
    @Consumes(MediaType.APPLICATION_JSON)
    @ApiOperation(value = "Read Log File", response = Collection.class)
    Collection<String> readLog(
            @ApiParam(value = "folder", required = false, defaultValue = "/home/ngontro86/logs") @QueryParam('folder') String folder,
            @ApiParam(value = "file", required = true, defaultValue = "ib_20190107.txt") @QueryParam('file') String file,
            @ApiParam(value = "numOfLine", required = true, defaultValue = "100") @QueryParam('numOfLine') int numOfLine,
            @ApiParam(value = "status", required = true, defaultValue = "Filled") @QueryParam('status') String status) {
        return queryService.readFile(folder, file, numOfLine, status)
    }

    @GET
    @Path('/backendError')
    @Produces(MediaType.APPLICATION_JSON)
    @ApiOperation(value = "Read Backend Error", response = Collection.class)
    Collection<String> readBackendError() {
        return queryService.readServerErrors()
    }

}
