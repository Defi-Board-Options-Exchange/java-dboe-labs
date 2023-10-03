package com.ngontro86.server.dboe.rest

import com.ngontro86.common.annotations.ConfigValue
import com.ngontro86.common.annotations.RestService
import com.ngontro86.server.dboe.services.QueryService
import com.ngontro86.server.dboe.services.ReferralService
import io.swagger.annotations.Api
import io.swagger.annotations.ApiOperation
import io.swagger.annotations.ApiParam
import org.springframework.beans.factory.annotation.Autowired

import javax.ws.rs.*
import javax.ws.rs.core.MediaType

@RestService
@Path("/query")
@Api(value = "/query")
class QueryRestService {

    @Autowired
    private QueryService queryService

    @Autowired
    private ReferralService referralService

    @ConfigValue(config = "fundingSmartContractChain")
    private String fundingSmartContractChain = 'AVAX'

    @ConfigValue(config = "fundingSmartContract")
    private String fundingSmartContract = '0xe265a12e99a19249c31d4d2a323c0740f36f855e'

    @ConfigValue(config = "idoSmartContract")
    private String idoSmartContract = '0x2BC035F1e460b3656A85e516673b48fFC5381F5B'

    @GET
    @Path('/query')
    @Produces(MediaType.APPLICATION_JSON)
    @Consumes(MediaType.APPLICATION_JSON)
    @ApiOperation(value = "Query Core", response = Collection.class)
    Collection<Map> query(@ApiParam(value = "query", required = true) @QueryParam('query') String query) {
        return queryService.query(query)
    }

    @GET
    @Path('/allUserOrders/{walletId}')
    @Produces(MediaType.APPLICATION_JSON)
    @Consumes(MediaType.APPLICATION_JSON)
    @ApiOperation(value = "Query all User Orders", response = Collection.class)
    Collection<Map> allUserOrders(@PathParam('walletId') String walletId) {
        return []
    }

    @GET
    @Path('/allUserTrades/{walletId}')
    @Produces(MediaType.APPLICATION_JSON)
    @Consumes(MediaType.APPLICATION_JSON)
    @ApiOperation(value = "Query all User Trades", response = Collection.class)
    Collection<Map> allUserTrades(@PathParam('walletId') String walletId) {
        return queryService.allUserTrades(walletId)
    }

    @GET
    @Path('/listReferees/{walletId}')
    @Produces(MediaType.APPLICATION_JSON)
    @Consumes(MediaType.APPLICATION_JSON)
    @ApiOperation(value = "Query all referees", response = Collection.class)
    Collection<Map> listReferees(@PathParam('walletId') String walletId) {
        return referralService.allReferees(walletId)
    }

    @GET
    @Path('/referralInfo/{walletId}')
    @Produces(MediaType.APPLICATION_JSON)
    @Consumes(MediaType.APPLICATION_JSON)
    @ApiOperation(value = "Pull referral info", response = Map.class)
    Map referralInfo(@PathParam('walletId') String walletId) {
        return referralService.referralInfo(walletId)
    }

    @GET
    @Path('/referral/{walletId}')
    @Produces(MediaType.APPLICATION_JSON)
    @Consumes(MediaType.APPLICATION_JSON)
    @ApiOperation(value = "Pull full info", response = Map.class)
    Map referral(@PathParam('walletId') String walletId) {
        return referralService.referral(walletId)
    }

    @GET
    @Path('/referralByEmail/{email}')
    @Produces(MediaType.APPLICATION_JSON)
    @Consumes(MediaType.APPLICATION_JSON)
    @ApiOperation(value = "Pull all referral info given email", response = Collection.class)
    Collection<Map> referralByEmail(@PathParam('email') String email) {
        return referralService.referralByEmail(email)
    }

    @GET
    @Path('/airdrop/{walletId}')
    @Produces(MediaType.APPLICATION_JSON)
    @Consumes(MediaType.APPLICATION_JSON)
    @ApiOperation(value = "All airdrops that the wallet is eligible for", response = Collection.class)
    Collection<Map> airdrop(@PathParam('walletId') String walletId) {
        return referralService.airdrop(walletId)
    }

    @GET
    @Path('/markPrices')
    @Produces(MediaType.APPLICATION_JSON)
    @Consumes(MediaType.APPLICATION_JSON)
    @ApiOperation(value = "Query mark prices", response = Collection.class)
    Collection<Map> queryMarkPrices(@QueryParam('chain') String chain) {
        return queryService.markPrices(chain)
    }

    @GET
    @Path('/lastPrice')
    @Produces(MediaType.TEXT_PLAIN)
    @Consumes(MediaType.APPLICATION_JSON)
    @ApiOperation(value = "Query last price", response = Collection.class)
    double queryLastPrice(@QueryParam('chain') String chain, @QueryParam('instrId') String instrId) {
        return queryService.lastPrice(chain, instrId)
    }

    @GET
    @Path('/listInstrument')
    @Produces(MediaType.APPLICATION_JSON)
    @Consumes(MediaType.APPLICATION_JSON)
    @ApiOperation(value = "Query all tradeable", response = Collection.class)
    Collection<Map> listInstrument(@QueryParam('chain') String chain) {
        return queryService.allTradable(chain)
    }

    @GET
    @Path('/listAllOptions')
    @Produces(MediaType.APPLICATION_JSON)
    @Consumes(MediaType.APPLICATION_JSON)
    @ApiOperation(value = "Query options including expired", response = Collection.class)
    Collection<Map> listAllOptions(@QueryParam('chain') String chain) {
        return queryService.allOptions(chain)
    }

    @GET
    @Path('/listToken')
    @Produces(MediaType.APPLICATION_JSON)
    @Consumes(MediaType.APPLICATION_JSON)
    @ApiOperation(value = "Query all tokens", response = Collection.class)
    Collection<Map> listToken() {
        return queryService.allTokens()
    }

    @GET
    @Path('/optionChain')
    @Produces(MediaType.APPLICATION_JSON)
    @Consumes(MediaType.APPLICATION_JSON)
    @ApiOperation(value = "Query all option chain", response = Collection.class)
    Collection<Map> optionChain(@QueryParam("chain") String chain, @QueryParam("underlying") String underlying, @QueryParam("expiryDate") int expiryDate, @QueryParam("collateralGroup") String collateralGroup) {
        return queryService.optionChain(chain, underlying, expiryDate, collateralGroup)
    }

    @GET
    @Path('/optionChainMarket')
    @Produces(MediaType.APPLICATION_JSON)
    @Consumes(MediaType.APPLICATION_JSON)
    @ApiOperation(value = "Query all option chain", response = Collection.class)
    Collection<Map> optionChainMarket(@QueryParam("chain") String chain, @QueryParam("underlying") String underlying, @QueryParam("expiryDate") int expiryDate, @QueryParam("collateralGroup") String collateralGroup) {
        return queryService.optionChainMarket(chain, underlying, expiryDate, collateralGroup)
    }

    @GET
    @Path('/orderbook')
    @Produces(MediaType.APPLICATION_JSON)
    @Consumes(MediaType.APPLICATION_JSON)
    @ApiOperation(value = "Query orderbook", response = Collection.class)
    Collection<Map> orderbook(@QueryParam("chain") String chain, @QueryParam("instrId") String instrId) {
        return queryService.orderbook(chain, instrId)
    }

    @GET
    @Path('/walletAvgPx')
    @Produces(MediaType.APPLICATION_JSON)
    @Consumes(MediaType.APPLICATION_JSON)
    @ApiOperation(value = "Query wallet Avg Px", response = Collection.class)
    Collection<Map> walletAvgPx(@QueryParam("walletId") String walletId) {
        return queryService.walletAvgPx(walletId)
    }


    @GET
    @Path('/volSurface')
    @Produces(MediaType.APPLICATION_JSON)
    @Consumes(MediaType.APPLICATION_JSON)
    @ApiOperation(value = "Query 3D volatility surface", response = Collection.class)
    Collection<Map> volSurface(@QueryParam("underlying") String underlying) {
        return queryService.volSurface(underlying)
    }

    @GET
    @Path('/priceOptionWithShocks')
    @Produces(MediaType.APPLICATION_JSON)
    @Consumes(MediaType.APPLICATION_JSON)
    @ApiOperation(value = "Price Option with Shocks", response = Map.class)
    Map priceOptionWithShocks(@QueryParam("isCall") boolean isCall,
                              @QueryParam("expiryDate") int expiryDate,
                              @QueryParam("spot") double spot,
                              @QueryParam("strike") double strike, @QueryParam("condStrike") double condStrike,
                              @QueryParam("volatility") double volatility) {
        return queryService.priceOptionWithShocks(isCall, expiryDate, spot, strike, condStrike, volatility, [-0.25, -0.1, 0, 0.1, 0.25] as double[], [-0.25, -0.1, 0, 0.1, 0.25] as double[])
    }


    @GET
    @Path('/userUpdatedEmail')
    @Produces(MediaType.APPLICATION_JSON)
    @Consumes(MediaType.APPLICATION_JSON)
    @ApiOperation(value = "Query to see whether user updated email", response = Boolean.class)
    boolean userUpdatedEmail(@QueryParam("walletId") String walletId) {
        return referralService.userUpdatedEmail(walletId)
    }

    @GET
    @Path('/chainInfo')
    @Produces(MediaType.APPLICATION_JSON)
    @Consumes(MediaType.APPLICATION_JSON)
    @ApiOperation(value = "Get Chain Info", response = Collection.class)
    Collection<Map> getChainInfo() {
        return queryService.getChainInfo()
    }

    @GET
    @Path('/allChainInfo')
    @Produces(MediaType.APPLICATION_JSON)
    @Consumes(MediaType.APPLICATION_JSON)
    @ApiOperation(value = "Get All Chain Info", response = Map.class)
    Map allChainInfo() {
        return queryService.allChainInfo()
    }

    @GET
    @Path('/fundingSmartContract')
    @Produces(MediaType.APPLICATION_JSON)
    @Consumes(MediaType.APPLICATION_JSON)
    @ApiOperation(value = "Get funding smart contract information", response = Map.class)
    Map fundingSmartContract() {
        return [
                'Chain'                 : fundingSmartContractChain,
                'Funding Smart Contract': fundingSmartContract
        ]
    }

    @GET
    @Path('/idoSmartContract')
    @Produces(MediaType.APPLICATION_JSON)
    @Consumes(MediaType.APPLICATION_JSON)
    @ApiOperation(value = "Get IDO smart contract information", response = Map.class)
    Map idoSmartContract() {
        return [
                'Chain'             : fundingSmartContractChain,
                'IDO Smart Contract': idoSmartContract
        ]
    }

    @GET
    @Path('/clobSpecs')
    @Produces(MediaType.APPLICATION_JSON)
    @Consumes(MediaType.APPLICATION_JSON)
    @ApiOperation(value = "Get number of price level", response = Map.class)
    Map getClobSpecs() {
        return queryService.onchainClobSpecs()
    }

    @GET
    @Path('/clobInfo')
    @Produces(MediaType.APPLICATION_JSON)
    @Consumes(MediaType.APPLICATION_JSON)
    @ApiOperation(value = "Get Clob Info", response = Collection.class)
    Collection<Map> getClobInfo() {
        return queryService.getClobInfo()
    }

    @GET
    @Path('/addressDivider')
    @Produces(MediaType.APPLICATION_JSON)
    @Consumes(MediaType.APPLICATION_JSON)
    @ApiOperation(value = "Get Address Divider", response = Collection.class)
    Collection<Map> getAddressDivider() {
        return queryService.getAddressDivider()
    }

    @GET
    @Path('/fixedSpreads')
    @Produces(MediaType.APPLICATION_JSON)
    @Consumes(MediaType.APPLICATION_JSON)
    @ApiOperation(value = "Get fixed spreads Info", response = Collection.class)
    Collection<Map> getFixedSpreads() {
        return queryService.getFixedSpreads()
    }

    @POST
    @Path('/faucets')
    @Consumes(MediaType.APPLICATION_JSON)
    @ApiOperation(value = "Request For Token", response = Boolean.class)
    boolean requestToken(@QueryParam("email") String email,
                         @QueryParam("reqId") String reqId,
                         @QueryParam("walletId") String walletId) {
        queryService.faucet(email, reqId, walletId)
    }

    @GET
    @Path('/newWalletCount')
    @Produces(MediaType.APPLICATION_JSON)
    @Consumes(MediaType.APPLICATION_JSON)
    @ApiOperation(value = "Query new wallet count for past 14 days", response = Collection.class)
    Collection<Map> newWalletCount() {
        return queryService.newWalletCount()
    }

    @GET
    @Path('/tradedValue/{chain}')
    @Produces(MediaType.TEXT_PLAIN)
    @Consumes(MediaType.APPLICATION_JSON)
    @ApiOperation(value = "Traded Value on all available Options", response = Double.class)
    Double tradedValue(@PathParam('chain') String chain) {
        return queryService.tradedValue(chain)
    }

    @GET
    @Path('/tradedValue/{chain}/{underlying}/{expiry}')
    @Produces(MediaType.APPLICATION_JSON)
    @Consumes(MediaType.APPLICATION_JSON)
    @ApiOperation(value = "Traded Value on all available Options", response = Collection.class)
    Collection<Map> tradedValueForOneExpiry(@PathParam('chain') String chain, @PathParam('underlying') String underlying, @PathParam('expiry') Integer expiry) {
        return queryService.tradedValueForOneExpiry(chain, underlying, expiry)
    }
}
