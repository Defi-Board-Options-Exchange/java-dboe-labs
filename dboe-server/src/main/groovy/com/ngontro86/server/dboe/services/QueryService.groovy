package com.ngontro86.server.dboe.services

import com.ngontro86.app.common.db.FlatDao
import com.ngontro86.cep.CepEngine
import com.ngontro86.common.annotations.ConfigValue
import com.ngontro86.common.annotations.DBOEComponent
import com.ngontro86.common.annotations.Logging
import com.ngontro86.common.annotations.NonTxTransactional
import com.ngontro86.server.dboe.services.onchain.OnchainPositionLoader
import org.apache.logging.log4j.Logger

import javax.inject.Inject

import static com.ngontro86.common.times.GlobalTimeController.currentTimeMillis
import static com.ngontro86.utils.GlobalTimeUtils.getTimeFormat

@DBOEComponent
class QueryService {

    @Logging
    private Logger logger

    @Inject
    private CepEngine cep

    @Inject
    @NonTxTransactional
    private FlatDao flatDao

    @Inject
    private OnchainPositionLoader onchainPositionLoader

    @ConfigValue(config = "displayTrades")
    private Boolean displayTrades

    Collection<Map> query(String query) {
        logger.info("${query}")
        return cep.queryMap(query)
    }

    Collection<Map> optionChain(String chain, String underlying, int expiryDate, String collateralGroup) {
        cep.queryMap("select * from DboeOptionInstrWin(chain='${chain}', underlying='${underlying}', expiry = ${expiryDate}, collateral_group='${collateralGroup}')")
    }

    Collection<Map> volSurface(String underlying) {
        cep.queryMap("select * from DboeVolSurfaceWin(underlying='${underlying}')")
    }

    Collection<Map> optionChainMarket(String chain, String underlying, int expiryDate, String collateralGroup) {
        cep.queryMap("select ob.* from DboeOptionChainMarketWin(chain='${chain}') ob inner join DboeOptionInstrWin(chain='${chain}',underlying='${underlying}',expiry=${expiryDate}, collateral_group='${collateralGroup}') i on i.instr_id = ob.instr_id")
    }

    Collection<Map> orderbook(String chain, String instrId) {
        cep.queryMap("select instr_id, sum(amount) as size, price, price_level, buy_sell " +
                "from DboeOrderBookWin(chain='${chain}', instr_id='${instrId}') " +
                "group by buy_sell, price, instr_id, price_level " +
                "order by price desc")
    }

    Collection<Map> allTradable(String chain) {
        cep.queryMap("select i.* " +
                "from DboeOptionInstrWin(chain='${chain}') i " +
                "inner join DboeOptionTimeToExpiryWin(time_to_expiry>0) t on i.instr_id = t.instr_id")
    }

    Collection<Map> allTokens() {
        cep.queryMap("select chain, token, address from DboeCollateralWin")
    }

    protected static Map exposure(Collection<Map> greeks, Map position) {
        if (greeks.isEmpty()) return [:]
        def greeksMap = greeks.groupBy { it['instr_id'] }
        return ['delta', 'vega', 'theta', 'gamma'].collectEntries { oneGreek ->
            [(oneGreek): position.collect {
                it.value * (greeksMap[it.key] == null ? 0d : greeksMap[it.key][oneGreek])
            }.sum()]
        }
    }

    Collection<Map> allUserTrades(String walletId) {
        return displayTrades ? flatDao.queryList("select * from analytics.dboe_wallet_txn where Address='${walletId}'") : []
    }

    Map priceOptionWithShocks(boolean isCall,
                              int expiryDate,
                              double spot,
                              double strike, double condStrike,
                              double volatility,
                              double[] spotShocks,
                              double[] volShocks
    ) {
        return Utils.priceOptionWithShocks(isCall, expiryDate, getCurrentTimeMillis(), spot, strike, condStrike, volatility, spotShocks, volShocks)
    }

    Collection<Map> getChainInfo() {
        cep.queryMap("select * from DboeChainInfoWin")
    }

    Collection<Map> allOptions(String chain) {
        cep.queryMap("select * from DboeAllOptionsWin(chain='${chain}')")
    }

    Collection<Map> walletAvgPx(String walletId) {
        def ret = cep.queryMap("select p.instr_id as instr_id, pos_val, avg_px, p.chain as chain, wallet_id, pos, long_contract_address, short_contract_address, bid, ask " +
                "from DboeWalletPositionWin(wallet_id='${walletId}') p " +
                "left outer join DboeOptionInstrWin i on p.instr_id = i.instr_id and p.chain = i.chain " +
                "left outer join DboePriceWin m on p.instr_id = m.instr_id and p.chain = m.chain")
        ret.findAll { it['ask'] != null && it['bid'] != null }.each { m ->
            try {
                def currPos = onchainPositionLoader.loadPosition(m['chain'], m['long_contract_address'], m['long_contract_address'], walletId)
                if (currPos != Double.NaN) {
                    m['avg_px'] = Utils.estAvgPx(currPos, m['pos'], m['avg_px'], m['bid'], m['ask'])
                    m['estimated'] = true
                }
            } catch (Exception e) {
                logger.error(e)
            }
        }
        return ret
    }

    Collection<Map> walletAvgPx(String walletId, String instrId) {
        def ret = cep.queryMap("select i.instr_id as instr_id, pos_val, avg_px, i.chain as chain, wallet_id, pos, long_contract_address, short_contract_address, bid, ask " +
                "from DboeOptionInstrWin(instr_id='${instrId}') i " +
                "left outer join DboeWalletPositionWin(wallet_id='${walletId}') p on p.instr_id = i.instr_id and p.chain = i.chain " +
                "inner join DboePriceWin(instr_id='${instrId}') m on i.instr_id = m.instr_id and i.chain = m.chain")
        ret.findAll { it['ask'] != null && it['bid'] != null }.each { m ->
            try {
                def currPos = onchainPositionLoader.loadPosition(m['chain'], m['long_contract_address'], m['long_contract_address'], walletId)
                if (currPos != Double.NaN && m['pos'] != null && m['avg_px'] != null) {
                    m['avg_px'] = Utils.estAvgPx(currPos, m['pos'], m['avg_px'], m['bid'], m['ask'])
                    m['estimated'] = true
                }
            } catch (Exception e) {
                logger.error(e)
            }
        }
        return ret
    }

    Map allChainInfo() {
        return cep.queryMap("select * from DboeChainInfoWin").groupBy {
            it['chain']
        }
    }

    Map onchainClobSpecs() {
        def maps = cep.queryMap("select * from DboeOnchainClobSpecsWin")
        return maps.isEmpty() ? [:] : maps.first()
    }

    Collection<Map> getClobInfo() {
        cep.queryMap("select * from DboeObAddressDividerWin")
    }

    Collection<Map> getAddressDivider() {
        cep.queryMap("select * from DboeAddressDividerWin")
    }

    Collection<Map> getFixedSpreads() {
        cep.queryMap("select * from DboeFixedSpreadWin")
    }

    Collection<Map> markPrices(String chain) {
        return cep.queryMap("select * from DboeRefPriceWin(chain='${chain}')")
    }

    double lastPrice(String chain, String instrId) {
        def r = cep.queryMap("select coalesce(f.fsp, r.ref_price) as lastPx from DboeRefPriceWin(chain='${chain}',instr_id='${instrId}') r full outer join DboeOptionFspWin(chain='${chain}',instr_id='${instrId}') f on r.instr_id = f.instr_id")
        if (r.isEmpty()) {
            return 0d
        }
        return r.first()['lastPx'] as Double
    }

    boolean faucet(String email, String reqId, String walletId) {
        try {
            def exists = flatDao.queryList("select * from dboe_token_request where req_id='${reqId}' and wallet_id = '${walletId}'")

            if (!exists.isEmpty()) {
                return false
            }

            flatDao.persist('dboe_token_request',
                    [[
                             'email'    : email,
                             'req_id'   : reqId,
                             'wallet_id': walletId,
                             'processed': 0,
                             'timestamp': getTimeFormat(getCurrentTimeMillis(), 'yyyyMMddHHmmss')
                     ]]
            )
            return true
        } catch (Exception e) {
            logger.error(e)
            return false
        }
    }

    Collection<Map> newWalletCount() {
        flatDao.queryList("select * from dboe_new_wallet_count")
    }

    double tradedValue(String chain) {
        def r = cep.queryMap("select chain, sum(tradedValue) as tradedValue from DboeActiveOptionsTradedValueWin(chain='${chain}') r inner join DboeOptionTimeToExpiryWin(time_to_expiry > 0d) t on r.instr_id = t.instr_id group by chain")
        if (r.isEmpty()) {
            return 0d
        }
        return r.first()['tradedValue'] as Double
    }

    Collection<Map> tradedValueForOneExpiry(String chain, String und, int expiry) {
        cep.queryMap("select i.kind, sum(r.tradedValue) as tradedValue from DboeActiveOptionsTradedValueWin(chain='${chain}') r inner join DboeOptionTimeToExpiryWin(time_to_expiry > 0d) t on r.instr_id = t.instr_id inner join DboeOptionInstrWin(chain='${chain}',underlying='${und}',expiry=${expiry}) i on i.instr_id = r.instr_id group by i.kind")
    }

    Map dashboard() {
        def lqDashboard = cep.queryMap("select * from DboeTotalLiquidityDashboardWin")
        def airdrop = cep.queryMap("select sum(token_reward) as airdrop, count(distinct Address) as num_wallet from DboeWalletAirdropWin")

        def ret = lqDashboard.isEmpty() ? [:] : lqDashboard.first()
        ret.putAll(airdrop.isEmpty() ? [:] : airdrop.first())
        return ret
    }
}
