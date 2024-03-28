package com.ngontro86.server.dboe.services

import com.ngontro86.app.common.db.FlatDao
import com.ngontro86.cep.CepEngine
import com.ngontro86.common.annotations.DBOEComponent
import com.ngontro86.common.annotations.Logging
import com.ngontro86.common.annotations.NonTxTransactional
import com.ngontro86.server.dboe.services.copytrade.CopyTradeReconciliationResult
import com.ngontro86.server.dboe.services.copytrade.LeaderSubscription
import com.ngontro86.server.dboe.services.copytrade.PositionDiff
import com.ngontro86.utils.ResourcesUtils
import org.apache.logging.log4j.Logger

import javax.inject.Inject

import static com.ngontro86.utils.GlobalTimeUtils.getTimeFormat
import static java.lang.System.currentTimeMillis

@DBOEComponent
class CopyTradeService {

    @Logging
    private Logger logger

    @Inject
    @NonTxTransactional
    private FlatDao flatDao

    @Inject
    private CepEngine cep

    private String copyTradeLatestPositionQuery = ResourcesUtils.content("queries/copytrade-latest-positions.sql")
    private String copyTradeLatestPortfolioValQuery = ResourcesUtils.content("queries/copytrade-latest-portfolio.sql")

    Collection<Map> leaderBoard(String chain) {
        cep.queryMap("select * from DboeCopyTradeLeaderPerformancesWin(chain = '${chain}')")
    }

    LeaderSubscription leader(String chain, String walletId) {
        def leader = cep.queryMap("select * from DboeCopyTradeSubscriptionsWin(chain = '${chain}', user_wallet = '${walletId}')")
        return leader.isEmpty() ? null : new LeaderSubscription(leaderWallet: leader.first()['leader_wallet'], errorPct: leader.first()['error_pct'])
    }

    boolean signup(String chain, String walletId, String leaderWalletId, double errorPct) {
        try {
            flatDao.persist('dboe_copytrade_subscriptions', [
                    [
                            'chain'        : chain,
                            'user_wallet'  : walletId,
                            'leader_wallet': leaderWalletId,
                            'error_pct'    : errorPct,
                            'timestamp'    : getTimeFormat(currentTimeMillis(), "yyyyMMddHHmmss")
                    ]
            ])
            return true
        } catch (Exception e) {
            logger.error(e)
            return false
        }
    }

    CopyTradeReconciliationResult reconcile(String chain, String userWalletId) {
        CopyTradeReconciliationResult res = new CopyTradeReconciliationResult()
        try {
            def subscription = leader(chain, userWalletId)
            if (subscription == null) {
                return res
            }
            def leaderPv = flatDao.queryList(String.format(copyTradeLatestPortfolioValQuery, chain, subscription.leaderWallet))
            def userPv = flatDao.queryList(String.format(copyTradeLatestPortfolioValQuery, chain, userWalletId))

            if (leaderPv.isEmpty() || userPv.isEmpty()) {
                return res
            }

            res.setLeaderPortfolioValue(leaderPv.first()['portfolio_value'])
            res.setUserPortfolioValue(userPv.first()['portfolio_value'])

            def leaderPos = latestPositions(chain, subscription.leaderWallet).collectEntries { [(it['token']): it] }
            def userPos = latestPositions(chain, userWalletId).collectEntries { [(it['token']): it] }

            def positionDiffs = []
            positionDiffs << leaderPos.collect { token, map ->

                def userPosition = userPos.containsKey(token) ? userPos.get(token)['pos'] : 0d
                def idealPosition = estUserPos(res.leaderPortfolioValue, res.userPortfolioValue, map['pos'])

                new PositionDiff(
                        token: token,
                        underlying: map['underlying'],
                        expiry: map['expiry'],
                        strike: map['strike'],
                        condStrike: map['cond_strike'],
                        longContractAddr: map['long_contract_address'],
                        shortContractAddr: map['long_contract_address'],
                        currencyAddr: map['currency_address'],
                        obAddr: map['ob_address'],
                        optionFactoryAddr: map['option_factory_address'],
                        clearingHouseAddr: map['clearing_address'],
                        isOption: 'Options' == map['category'],
                        isCash: ['USDT', 'USDt', 'USDC'].contains(token),
                        leaderPos: map['pos'],
                        userPos: userPosition,
                        idealUserPos: idealPosition,
                        needAdjustment: needAdj(userPosition, idealPosition, subscription.errorPct)
                )
            }
            res.positionDiffs = positionDiffs
        } catch (Exception e) {
            logger.error(e)
        }

        return res
    }

    private boolean needAdj(double userPos, double idealUserPos, double errorPct) {
        if (userPos == 0d && idealUserPos != 0d) {
            return true
        }
        return Math.abs(idealUserPos / userPos - 1d) >= errorPct
    }

    private double estUserPos(double leaderPv, double userPv, double leaderPos) {
        return userPv / leaderPv * leaderPos
    }

    Collection<Map> latestPositions(String chain, String walletId) {
        flatDao.queryList(String.format(copyTradeLatestPositionQuery, chain, walletId))
    }
}
