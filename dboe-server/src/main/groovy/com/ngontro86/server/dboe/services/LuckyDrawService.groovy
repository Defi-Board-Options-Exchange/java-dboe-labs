package com.ngontro86.server.dboe.services

import com.ngontro86.app.common.db.FlatDao
import com.ngontro86.cep.CepEngine
import com.ngontro86.common.annotations.ConfigValue
import com.ngontro86.common.annotations.DBOEComponent
import com.ngontro86.common.annotations.Logging
import com.ngontro86.common.annotations.NonTxTransactional
import com.ngontro86.server.dboe.services.luckydraw.LuckyDrawStats
import org.apache.logging.log4j.Logger

import javax.annotation.PostConstruct
import javax.inject.Inject
import java.util.concurrent.Executors
import java.util.concurrent.ScheduledExecutorService
import java.util.concurrent.TimeUnit

import static com.ngontro86.common.times.GlobalTimeController.currentTimeMillis
import static com.ngontro86.utils.GlobalTimeUtils.getTimeFormat
import static com.ngontro86.utils.GlobalTimeUtils.getTimeUtc

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

    @ConfigValue(config = "dailyLuckyDrawQuota")
    private Integer dailyLuckyDrawQuota = 100

    @ConfigValue(config = "minExpiry")
    private Integer minExpiry = 20240531

    @ConfigValue
    private Integer reloadFreqMin = 15

    private Integer selectedExpiry = 0
    private Map<Boolean, Map> selectedOptions = [:]
    private Map<Integer, Integer> dailyLuckyDrawCounts = [:]

    private Collection<Map> pendingLuckyReqs = []
    private Map<String, Long> lastReqTimes = [:]

    private LuckyDrawStats stats = new LuckyDrawStats()

    private final ScheduledExecutorService scheduler = Executors.newSingleThreadScheduledExecutor()

    @PostConstruct
    private void init() {
        def dbStats = flatDao.queryList("select * from dboe_academy.dboe_luckydraw_stats where underlying = '${fixedUnderlying}'")
        if (!dbStats.isEmpty()) {
            stats.setTotalRequest(dbStats.first()['total_requests'])
            stats.setTotalLuckyDraw(dbStats.first()['total_lucky_wallets'])
        }

        lastReqTimes = flatDao.queryList("select * from dboe_academy.dboe_luckydraw_wallets")
                .collectEntries {
                    [(it['wallet']): getTimeUtc(String.valueOf(it['timestamp']), 'GMT', 'yyyyMMddHHmmss')]
                }

        dailyLuckyDrawCounts = flatDao.queryList("select floor(timestamp/1000000) as date, count(*) as numOfDraw from dboe_academy.dboe_luckydraw_wallets group by 1")
                .collectEntries { [(it['date']): it['numOfDraw']] }

        scheduler.scheduleAtFixedRate(
                new Runnable() {
                    @Override
                    void run() {
                        updateOptionAvailability()
                    }
                }, 0, reloadFreqMin, TimeUnit.MINUTES
        )
    }

    private void updateOptionAvailability() {
        try {
            def options = cep.queryMap("select i.* " +
                    "from DboeOptionInstrWin(expiry>=${minExpiry},chain='${chain}',underlying='${fixedUnderlying}') i " +
                    "inner join DboeOptionTimeToExpiryWin(time_to_expiry>0) t on i.instr_id = t.instr_id " +
                    "inner join DboeOptionChainMarketWin(bid>0,ask>0,Math.abs(greek[1])<${absDelta}) m on i.instr_id = m.instr_id and i.chain = m.chain")

            selectedExpiry = options.groupBy { it['expiry'] }.keySet().min()
            options = options.findAll { it['expiry'] == selectedExpiry }

            selectedOptions.clear()
            if (!options.findAll { it['kind'] == 'Call' }.isEmpty()) {
                selectedOptions[true] = Utils.minMax(options.findAll { it['kind'] == 'Call' }, 'strike', true)
            }

            if (!options.findAll { it['kind'] == 'Put' }.isEmpty()) {
                selectedOptions[false] = Utils.minMax(options.findAll { it['kind'] == 'Put' }, 'strike', false)
            }
        } catch (Exception e) {
            logger.error(e)
        }
    }

    Map luckyOrNot(String wallet) {
        try {
            stats.incrementReq()
            def tooEarly = lastReqTimes.getOrDefault(wallet, 0) + coolingOffMin * 60000 > currentTimeMillis
            if (tooEarly) {
                return [
                        'lucky': false,
                        'bonus': "Try again"
                ]
            }
            lastReqTimes[wallet] = currentTimeMillis

            boolean lucky = Math.random() <= luckyChance
            if (lucky) {
                pendingLuckyReqs << [
                        'wallet'   : wallet,
                        'timestamp': getTimeFormat(currentTimeMillis, 'yyyyMMddHHmmss')
                ]
                persistIfNeeded()
                stats.incrementLuckyReq()
                stats.addWallet(wallet)
                dailyLuckyDrawCounts[getTimeFormat(currentTimeMillis, 'yyyyMMdd')] += 1
            }

            return [
                    'lucky': lucky,
                    'bonus': ''
            ]
        } catch (Exception e) {
            logger.error(e)
            return [
                    'lucky': false,
                    'bonus': 'Try again'
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
                                    'timestamp'          : getTimeFormat(currentTimeMillis, 'yyyyMMddHHmmss')
                            ]
                    ]
            )
            pendingLuckyReqs.clear()
        }
    }

    LuckyDrawStats pastLuckyWallets() {
        stats
    }

    Map gimme(boolean upDown) {
        selectedOptions[upDown]
    }

    Map status() {
        [
                'slotAvailable' : dailyLuckyDrawQuota >= dailyLuckyDrawCounts[getTimeFormat(currentTimeMillis, 'yyyyMMdd')],
                'upcomingExpiry': selectedExpiry,
                'luckySlotToday': dailyLuckyDrawCounts[getTimeFormat(currentTimeMillis, 'yyyyMMdd')]
        ]
    }

    Map optionAvail() {
        selectedOptions
    }

    Map lastSpinTime(String wallet) {
        [
                'lastSpinTimeUtc' : lastReqTimes[wallet],
                'waitingPeriodMin': coolingOffMin
        ]
    }
}
