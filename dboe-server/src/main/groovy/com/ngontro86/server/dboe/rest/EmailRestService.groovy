package com.ngontro86.server.dboe.rest

import com.ngontro86.common.annotations.Logging
import com.ngontro86.common.annotations.RestService
import com.ngontro86.server.dboe.services.CepAuthenticator
import com.ngontro86.server.dboe.services.EmailProcessor
import io.swagger.annotations.Api
import io.swagger.annotations.ApiOperation
import org.apache.logging.log4j.Logger
import org.springframework.beans.factory.annotation.Autowired

import javax.ws.rs.*
import javax.ws.rs.core.MediaType

@RestService
@Path("/email")
@Api(value = "/email")
class EmailRestService {

    @Logging
    private Logger logger

    @Autowired
    private EmailProcessor emailService

    @Autowired
    private CepAuthenticator cepAuthenticator

    @POST
    @Path('/schedule')
    @Consumes(MediaType.APPLICATION_JSON)
    @ApiOperation(value = "Trigger emailing", response = String.class)
    String scheduleEmail(@HeaderParam("Authorization") String passCode,
                         @QueryParam("template") String template,
                         @QueryParam("to") String to,
                         Map params) {

        if (!cepAuthenticator.auth(passCode)) {
            logger.error("Unauthorised triggering this method...")
            throw new IllegalAccessException("Unauthorised")
        }
        emailService.email(template, to, params)
        return "Email is scheduled to be processed"
    }
}
