package com.ngontro86.server.dboe.rest

import com.ngontro86.common.annotations.RestService
import com.ngontro86.server.dboe.services.CepAuthenticator
import com.ngontro86.server.dboe.services.CopyTradeService
import com.ngontro86.server.dboe.services.copytrade.CopyTradeReconciliationResult
import io.swagger.annotations.Api
import io.swagger.annotations.ApiOperation
import org.springframework.beans.factory.annotation.Autowired

import javax.ws.rs.*
import javax.ws.rs.core.MediaType

@RestService
@Path("/copyTrade")
@Api(value = "/copyTrade")
class CopyTradeRestService {

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
    @Produces(MediaType.TEXT_PLAIN)
    @Consumes(MediaType.APPLICATION_JSON)
    @ApiOperation(value = "Query the current leader the user subscribe to", response = String.class)
    String leader(@QueryParam('chain') String chain, @QueryParam('walletId') String walletId) {
        return copyTradeService.leader(chain, walletId).leaderWallet
    }

    @POST
    @Path('/signup')
    @Produces(MediaType.TEXT_PLAIN)
    @Consumes(MediaType.APPLICATION_JSON)
    @ApiOperation(value = "Query the current leader the user subscribe to", response = Boolean.class)
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

    @GET
    @Path('/reconcile')
    @Produces(MediaType.APPLICATION_JSON)
    @Consumes(MediaType.APPLICATION_JSON)
    @ApiOperation(value = "Reconcile user wallet and his/her subscribed leader positions", response = CopyTradeReconciliationResult.class)
    CopyTradeReconciliationResult reconcile(@QueryParam('chain') String chain, @QueryParam('walletId') String walletId) {
        return copyTradeService.reconcile(chain, walletId)
    }

}
