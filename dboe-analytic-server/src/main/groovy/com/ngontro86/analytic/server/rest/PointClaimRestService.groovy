package com.ngontro86.analytic.server.rest

import com.ngontro86.analytic.server.services.PointClaimService
import com.ngontro86.common.annotations.RestService
import io.swagger.annotations.Api
import io.swagger.annotations.ApiOperation
import org.springframework.beans.factory.annotation.Autowired

import javax.ws.rs.*
import javax.ws.rs.core.MediaType

@RestService
@Path("/point")
@Api(value = "/point")
class PointClaimRestService {

    @Autowired
    private PointClaimService pointClaimService

    @GET
    @Path('/getEvents')
    @Produces(MediaType.APPLICATION_JSON)
    @Consumes(MediaType.APPLICATION_JSON)
    @ApiOperation(value = "Whether user placed any first option trade", response = Collection.class)
    Collection getEvents(@QueryParam("chain") String chain) {
        return pointClaimService.getEvents(chain)
    }

}
