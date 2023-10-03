package com.ngontro86.server.dboe.services

import com.ngontro86.app.common.db.FlatDao
import com.ngontro86.common.annotations.ConfigValue
import com.ngontro86.common.annotations.Logging
import com.ngontro86.common.annotations.NonTxTransactional
import com.ngontro86.common.annotations.DBOEComponent
import com.ngontro86.restful.common.client.HttpsRestClient
import com.ngontro86.restful.common.json.JsonUtils
import com.ngontro86.server.dboe.services.kyt.RiskAssessment
import org.apache.logging.log4j.Logger

import javax.annotation.PostConstruct
import javax.inject.Inject
import javax.ws.rs.client.Entity

import static com.ngontro86.restful.common.client.RestClientBuilder.buildHttpsClient
import static com.ngontro86.utils.GlobalTimeUtils.getTimeFormat
import static java.lang.System.currentTimeMillis

@DBOEComponent
class KYTService {

    @Logging
    private Logger logger

    @ConfigValue(config = "apiToken")
    private String apiToken

    @ConfigValue(config = "whitelistMode")
    private Boolean whitelistMode = true

    @ConfigValue(config = "kytRisks")
    private Collection kytRisks = ['High', 'Severe']

    @Inject
    @NonTxTransactional
    private FlatDao flatDao

    private Set<String> whitelisted = [] as Set

    private Map<String, String> kytRecords = [:]

    private HttpsRestClient client

    @PostConstruct
    private void init() {
        client = buildHttpsClient('chainalysis')
        whitelisted.addAll(flatDao.queryStringList("select distinct wallet_id from dboe_whitelist_wallets"))

        def kytWallets = flatDao.queryList("select * from dboe_kyt_addresses")
        kytWallets.each { kytRecords.put(it['wallet_id'], it['risk'].toString().toLowerCase()) }
    }

    boolean kytRisk(String walletId) {
        return whitelistMode ? !whitelisted.contains(walletId.toLowerCase()) : kyt(walletId)
    }

    private boolean kyt(String walletId) {
        if (kytRecords.containsKey(walletId)) {
            return kytRisks.contains(kytRecords.get(walletId))
        }

        def risk = registerThenQueryRisk(walletId)

        kytRecords.put(risk.address, risk.risk)
        flatDao.persist('dboe_kyt_addresses', [['wallet_id': walletId, 'risk': risk.risk, 'timestamp': getTimeFormat(currentTimeMillis(), "yyyyMMddHHmmss")]])
        return kytRisks.contains(risk.risk)
    }

    private RiskAssessment registerThenQueryRisk(String walletId) {
        client.postWithHeaderToken('', [:], Entity.json(JsonUtils.toJson(['address': walletId])), 'Token', apiToken, Map)

        return client.withQueryParams("${walletId}", [:], 'Token', apiToken, RiskAssessment)
    }

    boolean whitelist(String email, String wallet, String country) {
        def riskFlagged = whitelistMode ? kyt(wallet) : false
        if (riskFlagged) {
            return false
        }
        whitelisted.add(wallet.toLowerCase())
        flatDao.persist('dboe_whitelist_wallets', [['email': email, 'wallet_id': wallet, 'country_residence': country]])
        return true
    }
}
