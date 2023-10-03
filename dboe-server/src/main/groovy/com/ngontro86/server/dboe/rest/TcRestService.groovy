package com.ngontro86.server.dboe.rest

import com.ngontro86.common.annotations.RestService
import com.ngontro86.server.dboe.services.TcService
import io.swagger.annotations.Api
import io.swagger.annotations.ApiOperation
import org.springframework.beans.factory.annotation.Autowired

import javax.ws.rs.*
import javax.ws.rs.core.MediaType

@RestService
@Path("/tc")
@Api(value = "/tc")
class TcRestService {

    @Autowired
    private TcService tcService

    @GET
    @Path('/exists')
    @Consumes(MediaType.APPLICATION_JSON)
    @ApiOperation(value = "Check whether the address had accepted our T&C", response = Boolean.class)
    boolean exists(@QueryParam('wallet_id') String walletId) {
        return tcService.exists(walletId)
    }

    @POST
    @Path('/agreed')
    @Consumes(MediaType.APPLICATION_JSON)
    @ApiOperation(value = "User agreed", response = Boolean.class)
    boolean agreed(@QueryParam("walletId") String walletId) {
        return tcService.agreed(walletId)
    }
}
