package com.ngontro86.analytic.server.rest

import com.ngontro86.analytic.server.services.AnalyticService
import com.ngontro86.common.annotations.RestService
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
    @Path('/firstOptionTrade')
    @Produces(MediaType.APPLICATION_JSON)
    @Consumes(MediaType.APPLICATION_JSON)
    @ApiOperation(value = "Return the quotes from this DMM to a particular Option", response = Map.class)
    Map firstOptionTrade(@QueryParam("address") String addr) {
        return analyticService.firstOptionTrade(addr)
    }

    @GET
    @Path('/firstSpotTrade')
    @Produces(MediaType.APPLICATION_JSON)
    @Consumes(MediaType.APPLICATION_JSON)
    @ApiOperation(value = "Return the quotes from this DMM to a particular Option", response = Map.class)
    Map firstSpotTrade(@QueryParam("address") String addr) {
        return analyticService.firstSpotTrade(addr)
    }

    @GET
    @Path('/invited')
    @Produces(MediaType.APPLICATION_JSON)
    @Consumes(MediaType.APPLICATION_JSON)
    @ApiOperation(value = "Return the quotes from this DMM to a particular Option", response = Map.class)
    Map invited(@QueryParam("address") String addr, @QueryParam("noOfInvites") Integer noOfInvites) {
        return analyticService.invited(addr, noOfInvites)
    }

}
