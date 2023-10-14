package com.ngontro86.server.dboe.services

import com.ngontro86.app.common.db.FlatDao
import com.ngontro86.cep.CepEngine
import com.ngontro86.common.annotations.DBOEComponent
import com.ngontro86.common.annotations.Logging
import com.ngontro86.common.annotations.NonTxTransactional
import com.ngontro86.common.times.GlobalTimeController
import com.ngontro86.utils.GlobalTimeUtils
import org.apache.logging.log4j.Logger

import javax.annotation.PostConstruct
import javax.inject.Inject
import java.util.concurrent.ConcurrentHashMap

@DBOEComponent
class ReferralService {

    @Logging
    private Logger logger

    @NonTxTransactional
    @Inject
    private FlatDao flatDao

    @Inject
    private CepEngine cep

    private Map referralCache = [:] as ConcurrentHashMap<String, String>
    private HashSet refereeWalletSet = [] as HashSet

    @PostConstruct
    private void init() {
        refereeWalletSet.addAll(flatDao.queryList("select distinct wallet_address from referral_ack"))

        referralCache = flatDao.queryList("select wallet_address, email from referral_info").collectEntries {
            [(it['wallet_address'].toString().toLowerCase()): it['email'].toString().toLowerCase()]
        }

        logger.info("Found ${referralCache.size()} referrers and ${refereeWalletSet.size()} referees")
    }

    boolean update(String walletAddr, String email) {
        if (referralCache.containsKey(walletAddr) || referralCache.values().contains(email)) {
            logger.info("Either wallet: ${walletAddr} or ${email} registered already...")
            return false
        }

        flatDao.persist("referral_info",
                [
                        [
                                'wallet_address': walletAddr,
                                'email'         : email,
                                'timestamp'     : GlobalTimeUtils.getTimeFormat(GlobalTimeController.currentTimeMillis, 'yyyyMMddHHmmss'),
                        ]
                ])

        referralCache.put(walletAddr, email)
        return true
    }

    private String referralEmail(String referralCode) {
        def ret = referralCache.findAll { it.key.toString().endsWith(referralCode.toLowerCase()) }
        return ret.isEmpty() ? "" : ret.values().first()
    }

    Map ack(String walletAddr, String referralCode) {
        if (refereeWalletSet.contains(walletAddr)) {
            logger.info("Referee ${walletAddr} acked previously, searching from cache...")
            return ['success': false, 'email': referralEmail(referralCode)]
        }

        flatDao.persist("referral_ack",
                [
                        [
                                'wallet_address': walletAddr,
                                'referral_code' : referralCode,
                                'timestamp'     : GlobalTimeUtils.getTimeFormat(GlobalTimeController.currentTimeMillis, 'yyyyMMddHHmmss'),
                        ]
                ])
        refereeWalletSet.add(walletAddr)
        return ['success': true, 'email': referralEmail(referralCode)]
    }

    boolean userUpdatedEmail(String walletAddr) {
        return referralCache.containsKey(walletAddr)
    }

    Collection<Map> airdrop(String walletId) {
        cep.queryMap("SELECT * FROM DboeWalletAirdropWin(Address = '${walletId.toLowerCase()}')")
    }

    Map referralInfo(String walletId) {
        def list = cep.queryMap("SELECT distinct referrer_wallet as wallet_address, referrer_email as email FROM DboeWalletReferStatsWin(referee_wallet = '${walletId}')")
        return list.isEmpty() ? [:] : list.first()
    }

    Map referral(String walletId) {
        def list = cep.queryMap("SELECT distinct referrer_wallet as wallet_address, referrer_email as email FROM DboeWalletReferStatsWin(referrer_wallet = '${walletId}')")
        return list.isEmpty() ? [:] : list.first()
    }

    Collection<Map> referralByEmail(String email) {
        return cep.queryMap("SELECT distinct referrer_wallet as wallet_address, referrer_email as email FROM DboeWalletReferStatsWin(referrer_email = '${email}')")
    }

    Collection<Map> allReferees(String walletId) {
        return cep.queryMap("SELECT * FROM DboeWalletReferStatsWin(referrer_wallet = '${walletId}', referee_wallet is not null)")
    }
}
