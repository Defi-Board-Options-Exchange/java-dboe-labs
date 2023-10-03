package com.ngontro86.server.dboe.rest

import com.ngontro86.common.annotations.RestService
import com.ngontro86.server.dboe.services.KYTService
import io.swagger.annotations.Api
import io.swagger.annotations.ApiOperation
import io.swagger.annotations.ApiParam
import org.springframework.beans.factory.annotation.Autowired

import javax.ws.rs.*
import javax.ws.rs.core.MediaType

@RestService
@Path("/kyt")
@Api(value = "/kyt")
class KYTRestService {

    @Autowired
    private KYTService kytService

    @GET
    @Path('/query')
    @Consumes(MediaType.APPLICATION_JSON)
    @ApiOperation(value = "Risk check for an address", response = Boolean.class)
    boolean riskScreen(@ApiParam(value = "query", required = true) @QueryParam('wallet_id') String walletId) {
        return kytService.kytRisk(walletId)
    }

    @POST
    @Path('/whitelist')
    @Consumes(MediaType.APPLICATION_JSON)
    @ApiOperation(value = "Straight-through whitelist", response = Boolean.class)
    boolean whitelist(@QueryParam("email") String email,
                      @QueryParam("walletId") String walletId,
                      @QueryParam("country") String country) {
        return kytService.whitelist(email, walletId, country)
    }
}
