package com.ngontro86.server.dboe.services

import com.ngontro86.app.common.db.FlatDao
import com.ngontro86.cep.CepEngine
import com.ngontro86.common.annotations.Logging
import com.ngontro86.common.annotations.NonTxTransactional
import com.ngontro86.common.annotations.DBOEComponent
import com.ngontro86.common.times.GlobalTimeController
import com.ngontro86.utils.GlobalTimeUtils
import org.apache.logging.log4j.Logger

import javax.annotation.PostConstruct
import javax.inject.Inject

@DBOEComponent
class ReferralService {

    @Logging
    private Logger logger

    @NonTxTransactional
    @Inject
    private FlatDao flatDao

    @Inject
    private CepEngine cep

    private Map referralCache = [:] as HashMap
    private HashSet emailCollectedSet = [] as HashSet

    private HashSet refereeSet = [] as HashSet

    @PostConstruct
    private void init() {
        emailCollectedSet.addAll(flatDao.queryList("select distinct email from referral_info"))

        refereeSet.addAll(flatDao.queryList("select distinct wallet_address from referral_ack"))

        referralCache = flatDao.queryList("select wallet_address, email from referral_info").collectEntries {
            [(it['wallet_address']): it['email']]
        }

        logger.info("Found ${referralCache.size()} users with email and ${refereeSet.size()} referees")
    }

    boolean update(String walletAddr, String email) {
        if (emailCollectedSet.contains(email)) {
            logger.info("Email ${email} has been used...")
            return false
        }
        if (referralCache.containsKey(walletAddr)) {
            logger.info("Wallet: ${walletAddr} updated already")
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

        emailCollectedSet.add(email)
        referralCache.put(walletAddr, email)
        return true
    }

    Map ack(String walletAddr, String referralCode) {
        if (refereeSet.contains(walletAddr)) {
            logger.info("Referee ${walletAddr} acked previously")

            def emails = flatDao.queryList("select distinct email from referral_info where right(wallet_address, 8) = '${referralCode}'", String.class)
            return ['success': false, 'email': emails.isEmpty() ? "" : emails.first()]
        }

        flatDao.persist("referral_ack",
                [
                        [
                                'wallet_address': walletAddr,
                                'referral_code' : referralCode,
                                'timestamp'     : GlobalTimeUtils.getTimeFormat(GlobalTimeController.currentTimeMillis, 'yyyyMMddHHmmss'),
                        ]
                ])
        refereeSet.add(walletAddr)
        def emails = flatDao.queryList("select distinct email from referral_info where right(wallet_address, 8) = '${referralCode}'", String.class)
        return ['success': true, 'email': emails.isEmpty() ? "" : emails.first()]
    }

    boolean userUpdatedEmail(String walletAddr) {
        return referralCache.containsKey(walletAddr)
    }

    Collection<Map> airdrop(String walletId) {
        cep.queryMap("SELECT * FROM DboeWalletAirdropWin WHERE Address = '${walletId}'")
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
