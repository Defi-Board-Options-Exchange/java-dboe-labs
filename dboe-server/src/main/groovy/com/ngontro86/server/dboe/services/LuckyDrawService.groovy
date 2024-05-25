package com.ngontro86.server.dboe.services

import com.ngontro86.app.common.db.FlatDao
import com.ngontro86.cep.CepEngine
import com.ngontro86.common.annotations.ConfigValue
import com.ngontro86.common.annotations.DBOEComponent
import com.ngontro86.common.annotations.Logging
import com.ngontro86.common.annotations.NonTxTransactional
import com.ngontro86.common.times.GlobalTimeController
import com.ngontro86.server.dboe.services.luckydraw.LuckyDrawStats
import com.ngontro86.utils.GlobalTimeUtils
import org.apache.logging.log4j.Logger

import javax.annotation.PostConstruct
import javax.inject.Inject

@DBOEComponent
class LuckyDrawService {

    @Logging
    private Logger logger

    @Inject
    private CepEngine cep

    @Inject
    @NonTxTransactional
    private FlatDao flatDao

    @ConfigValue(config = "fixedUnderlying")
    private String fixedUnderlying = 'ETH'

    @ConfigValue(config = "chain")
    private String chain = 'Polygon'

    @ConfigValue(config = "absDelta")
    private Double absDelta = 0.1d

    @ConfigValue(config = "luckyChance")
    private Double luckyChance = 0.25d

    @ConfigValue(config = "coolingOffMin")
    private Integer coolingOffMin = 5

    @ConfigValue(config = "reqBatchSize")
    private Integer reqBatchSize = 5

    private Collection<Map> pendingLuckyReqs = []
    private Map<String, Long> lastReqTimes = [:]

    private LuckyDrawStats stats = new LuckyDrawStats()

    @PostConstruct
    private void init() {
        def dbStats = flatDao.queryList("select * from dboe_academy.dboe_luckydraw_stats where underlying = '${fixedUnderlying}'")
        if (!dbStats.isEmpty()) {
            stats.setTotalRequest(dbStats.first()['total_requests'])
            stats.setTotalLuckyDraw(dbStats.first()['total_lucky_wallets'])
        }
    }

    Map luckyOrNot(String wallet) {
        try {
            stats.setTotalLuckyDraw(stats.getTotalRequest() + 1)
            def tooEarly = lastReqTimes.getOrDefault(wallet, 0) + coolingOffMin * 60000 > GlobalTimeController.currentTimeMillis
            if (tooEarly) {
                return [
                        'lucky' : false,
                        'reason': "Retry after ${coolingOffMin} minutes"
                ]
            }
            lastReqTimes[wallet] = GlobalTimeController.currentTimeMillis

            boolean lucky = Math.random() <= luckyChance
            if (lucky) {
                pendingLuckyReqs << [
                        'wallet'   : wallet,
                        'timestamp': GlobalTimeUtils.getTimeFormat(GlobalTimeController.currentTimeMillis, 'yyyyMMddHHmmss')
                ]
                persistIfNeeded()
                stats.setTotalLuckyDraw(stats.getTotalLuckyDraw() + 1)
            }
            return [
                    'lucky' : lucky,
                    'reason': 'Random basis'
            ]
        } catch (Exception e) {
            logger.error(e)
            return [
                    'lucky' : false,
                    'reason': 'Error happened'
            ]
        }
    }

    void persistIfNeeded() {
        if (pendingLuckyReqs.size() >= reqBatchSize) {
            flatDao.persist('dboe_luckydraw_wallets', pendingLuckyReqs)
            flatDao.persist('dboe_luckdraw_stats',
                    [
                            [
                                    'underlying'         : fixedUnderlying,
                                    'total_requests'     : stats.totalRequest,
                                    'total_lucky_wallets': stats.totalLuckyDraw,
                                    'timestamp'          : GlobalTimeUtils.getTimeFormat(GlobalTimeController.currentTimeMillis, 'yyyyMMddHHmmss')
                            ]
                    ]
            )
            stats.setMostRecentLuckyWallets(pendingLuckyReqs.collect { it['wallet'] })
            pendingLuckyReqs.clear()
        }
    }

    LuckyDrawStats pastLuckyWallets() {
        stats
    }

    Map gimme(boolean upDown) {
        def options = cep.queryMap("select i.* " +
                "from DboeOptionInstrWin(chain='${chain}',underlying='${fixedUnderlying}',kind='${upDown ? 'Call' : 'Put'}') i " +
                "inner join DboeOptionTimeToExpiryWin(time_to_expiry>0) t on i.instr_id = t.instr_id " +
                "inner join DboeOptionChainMarketWin(bid>0,ask>0,Math.abs(greek[1])<${absDelta}) m on i.instr_id = m.instr_id and i.chain = m.chain")

        return Utils.minMax(options, 'strike', upDown)
    }
}
