package com.ngontro86.analytic.server.rest

import com.ngontro86.analytic.server.services.AnalyticService
import com.ngontro86.analytic.server.services.FixingPriceService
import com.ngontro86.common.annotations.RestService
import io.swagger.annotations.Api
import io.swagger.annotations.ApiOperation
import org.springframework.beans.factory.annotation.Autowired

import javax.ws.rs.*
import javax.ws.rs.core.MediaType

@RestService
@Path("/fsp")
@Api(value = "/fsp")
class FixingPriceRestService {

    @Autowired
    private FixingPriceService fixingPriceService

    @GET
    @Path('/fixings')
    @Produces(MediaType.APPLICATION_JSON)
    @Consumes(MediaType.APPLICATION_JSON)
    @ApiOperation(value = "Whether user placed any first option trade", response = Collection.class)
    Collection fixing(@QueryParam("expiry") int expiry) {
        return fixingPriceService.fixing(expiry)
    }

}
