package com.ngontro86.server.dboe.rest

import com.ngontro86.common.annotations.Logging
import com.ngontro86.common.annotations.RestService
import com.ngontro86.server.dboe.services.CepAuthenticator
import com.ngontro86.server.dboe.services.CopyTradeService
import com.ngontro86.server.dboe.services.copytrade.CopyTradeReconciliationResult
import com.ngontro86.server.dboe.services.copytrade.LeaderSubscription
import io.swagger.annotations.Api
import io.swagger.annotations.ApiOperation
import org.apache.logging.log4j.Logger
import org.springframework.beans.factory.annotation.Autowired

import javax.ws.rs.*
import javax.ws.rs.core.MediaType

@RestService
@Path("/copyTrade")
@Api(value = "/copyTrade")
class CopyTradeRestService {

    @Logging
    private Logger logger

    @Autowired
    private CopyTradeService copyTradeService

    @Autowired
    private CepAuthenticator cepAuthenticator

    @GET
    @Path('/leaderboard')
    @Produces(MediaType.APPLICATION_JSON)
    @Consumes(MediaType.APPLICATION_JSON)
    @ApiOperation(value = "Query all leaders", response = Collection.class)
    Collection<Map> leaderBoard(@QueryParam('chain') String chain) {
        return copyTradeService.leaderBoard(chain)
    }

    @GET
    @Path('/leaderposition')
    @Produces(MediaType.APPLICATION_JSON)
    @Consumes(MediaType.APPLICATION_JSON)
    @ApiOperation(value = "Query all leaders", response = Collection.class)
    Collection<Map> leaderPosition(@QueryParam('chain') String chain, @QueryParam('walletId') String walletId) {
        return copyTradeService.latestPositions(chain, walletId)
    }

    @GET
    @Path('/leader')
    @Produces(MediaType.APPLICATION_JSON)
    @Consumes(MediaType.APPLICATION_JSON)
    @ApiOperation(value = "Query the current leader the user subscribe to", response = LeaderSubscription.class)
    LeaderSubscription leader(@QueryParam('chain') String chain, @QueryParam('walletId') String walletId) {
        return copyTradeService.leader(chain, walletId)
    }

    @POST
    @Path('/signup')
    @Produces(MediaType.TEXT_PLAIN)
    @Consumes(MediaType.APPLICATION_JSON)
    @ApiOperation(value = "Subscribe to this leader", response = Boolean.class)
    boolean signup(@HeaderParam("passCode") String passCode,
                   @QueryParam('chain') String chain,
                   @QueryParam('walletId') String walletId,
                   @QueryParam('leaderWalletId') String leaderWalletId,
                   @QueryParam('errorPct') double errorPct) {
        if (!cepAuthenticator.auth(passCode)) {
            return false
        }
        return copyTradeService.signup(chain, walletId, leaderWalletId, errorPct)
    }

    @POST
    @Path('/unsubscribe')
    @Produces(MediaType.TEXT_PLAIN)
    @Consumes(MediaType.APPLICATION_JSON)
    @ApiOperation(value = "Unsubscribe", response = Boolean.class)
    boolean unsubscribe(@HeaderParam("passCode") String passCode,
                   @QueryParam('chain') String chain,
                   @QueryParam('walletId') String walletId,
                   @QueryParam('leaderWalletId') String leaderWalletId) {
        if (!cepAuthenticator.auth(passCode)) {
            return false
        }
        return copyTradeService.unsubscribe(chain, walletId, leaderWalletId)
    }

    @GET
    @Path('/reconcile')
    @Produces(MediaType.APPLICATION_JSON)
    @Consumes(MediaType.APPLICATION_JSON)
    @ApiOperation(value = "Reconcile user wallet and his/her subscribed leader positions", response = CopyTradeReconciliationResult.class)
    CopyTradeReconciliationResult reconcile(@QueryParam('chain') String chain, @QueryParam('walletId') String walletId) {
        logger.info("reconcile: user wallet:${walletId} on ${chain}")
        return copyTradeService.reconcile(chain, walletId)
    }

}
