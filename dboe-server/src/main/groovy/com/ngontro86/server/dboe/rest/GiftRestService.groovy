package com.ngontro86.server.dboe.rest

import com.ngontro86.common.annotations.Logging
import com.ngontro86.common.annotations.RestService
import com.ngontro86.server.dboe.services.CepAuthenticator
import com.ngontro86.server.dboe.services.gift.GiftService
import io.swagger.annotations.Api
import io.swagger.annotations.ApiOperation
import org.apache.logging.log4j.Logger
import org.springframework.beans.factory.annotation.Autowired

import javax.ws.rs.*
import javax.ws.rs.core.MediaType

@RestService
@Path("/gift")
@Api(value = "/gift")
class GiftRestService {

    @Logging
    private Logger logger

    @Autowired
    private GiftService giftService

    @Autowired
    private CepAuthenticator cepAuthenticator

    @GET
    @Path('/listGifts')
    @Produces(MediaType.APPLICATION_JSON)
    @Consumes(MediaType.APPLICATION_JSON)
    @ApiOperation(value = "Given wallet, return different gift name and how many opens", response = Map.class)
    Map<String, Integer> listGifts(@QueryParam("walletId") String walletId) {
        return giftService.numOfGifts(walletId)
    }

    @POST
    @Path('/open')
    @Consumes(MediaType.APPLICATION_JSON)
    @ApiOperation(value = "Open a gift box", response = Double.class)
    double openGift(@HeaderParam("passCode") String passCode,
                    @QueryParam("name") String name,
                    @QueryParam("openKey") String openKey,
                    @QueryParam("wallet") String wallet) {
        logger.info("Open A Gift: passCode: ${passCode},  name: ${name}, wallet: ${wallet}, openkey: ${openKey}")
        if (!cepAuthenticator.auth(passCode)) {
            return
        }
        return giftService.open(openKey, wallet, name)
    }
}
