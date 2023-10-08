package com.ngontro86.server.dboe.rest

import com.ngontro86.common.annotations.Logging
import com.ngontro86.common.annotations.RestService
import com.ngontro86.common.times.GlobalTimeController
import com.ngontro86.server.dboe.services.CepAuthenticator
import com.ngontro86.server.dboe.services.CepService
import com.ngontro86.server.dboe.services.DefundraisingService
import com.ngontro86.server.dboe.services.ReferralService
import io.swagger.annotations.Api
import io.swagger.annotations.ApiOperation
import org.apache.logging.log4j.Logger
import org.springframework.beans.factory.annotation.Autowired

import javax.ws.rs.*
import javax.ws.rs.core.MediaType

@RestService
@Path("/coreEngine")
@Api(value = "/coreEngine", description = "To submit order")
class CepRestService {

    @Logging
    private Logger logger

    @Autowired
    private CepService cepService

    @Autowired
    private CepAuthenticator cepAuthenticator

    @Autowired
    private ReferralService referralService

    @Autowired
    private DefundraisingService defundraisingService

    @POST
    @Path('/updateEvent')
    @Consumes(MediaType.APPLICATION_JSON)
    @ApiOperation(value = "Update an event", response = Void.class)
    void updateEvent(@HeaderParam("passCode") String passCode,
                     @QueryParam("eventName") String eventName,
                     Map event) {
        logger.info("Updating event: passCode: ${passCode}, event name: ${eventName}, event: ${event}")
        if (!cepAuthenticator.auth(passCode)) {
            return
        }
        cepService.updateEvent(eventName, event)
    }

    @POST
    @Path('/projectSignup')
    @Consumes(MediaType.APPLICATION_JSON)
    @ApiOperation(value = "Update an event", response = String.class)
    String projectSignup(@QueryParam("walletAddress") String walletAddress, Map event) {
        logger.info("Signing up project ${walletAddress}, info: ${event}")
        defundraisingService.signup(walletAddress, event)
    }

    @POST
    @Path('/userUpdateEmail')
    @Consumes(MediaType.APPLICATION_JSON)
    @ApiOperation(value = "User update their email", response = Boolean.class)
    boolean userUpdateEmail(@QueryParam("email") String email,
                            @QueryParam("walletId") String walletId) {
        logger.info("User: ${walletId} self-update email: ${email} ...")
        referralService.update(walletId, email)
    }

    @POST
    @Path('/referralAck')
    @Produces(MediaType.APPLICATION_JSON)
    @Consumes(MediaType.APPLICATION_JSON)
    @ApiOperation(value = "Referee acked", response = Map.class)
    Map referralAck(@QueryParam("walletId") String walletId,
                    @QueryParam("referral_code") String referralCode) {
        logger.info("Referee: ${walletId} acked referral code: ${referralCode} ...")
        referralService.ack(walletId, referralCode)
    }


    @POST
    @Path('/updateSpot')
    @Consumes(MediaType.APPLICATION_JSON)
    @ApiOperation(value = "Update spot market", response = Boolean.class)
    boolean updateSpot(@HeaderParam("passCode") String passCode,
                       @QueryParam("underlying") String underlying,
                       @QueryParam("spot") double spot) {
        if (!cepAuthenticator.auth(passCode)) {
            return false
        }
        cepService.updateEvent('DboeSpotEvent',
                [
                        'underlying'  : underlying,
                        'spot'        : spot,
                        'in_timestamp': GlobalTimeController.getCurrentTimeMillis()
                ]
        )
        return true
    }
}
