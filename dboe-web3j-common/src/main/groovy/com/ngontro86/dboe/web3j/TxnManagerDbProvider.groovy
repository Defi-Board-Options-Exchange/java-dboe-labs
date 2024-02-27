package com.ngontro86.dboe.web3j

import com.ngontro86.app.common.db.FlatDao
import com.ngontro86.common.annotations.*
import com.ngontro86.dboe.web3j.encryption.KeyHashUtils
import org.springframework.context.annotation.Bean
import org.web3j.crypto.Credentials
import org.web3j.protocol.Web3j
import org.web3j.tx.RawTransactionManager

import javax.inject.Inject

import static org.web3j.tx.TransactionManager.DEFAULT_POLLING_ATTEMPTS_PER_TX_HASH
import static org.web3j.tx.TransactionManager.DEFAULT_POLLING_FREQUENCY

@DBOEComponent
class TxnManagerDbProvider {

    @ConfigValue(config = "chainId")
    private Integer chainId = 56

    @ConfigValue(config = "offsetNonce")
    private Boolean offsetNonce = false

    @Inject
    @Web3jReadWrite
    private Web3j web3j

    @Inject
    @Web3jReadOnly
    private Web3j web3jRo

    @ConfigValue(config = "web3jAttempts")
    private Integer web3jAttempts = DEFAULT_POLLING_ATTEMPTS_PER_TX_HASH

    @ConfigValue(config = "web3jTimeout")
    private Integer web3jTimeout = DEFAULT_POLLING_FREQUENCY

    @ConfigValue(config = "rwWallet")
    private String rwWallet

    @ConfigValue(config = "roWallet")
    private String roWallet = 'ro'

    @Inject
    @NonTxTransactional
    private FlatDao flatDao

    private Credentials rwCred, roCred

    @Bean
    @Web3jReadWrite
    RawTransactionManager rwTxnManager() {
        if (rwCred == null) {
            rwCred = getCreds(rwWallet)
        }
        return offsetNonce ? new CustomNonceTxnManager(web3j, rwCred, chainId) : new RawTransactionManager(web3j, rwCred, chainId, web3jAttempts, web3jTimeout)
    }

    @Bean
    @Web3jReadOnly
    RawTransactionManager roTxnManager() {
        if (roCred == null) {
            roCred = getCreds(roWallet)
        }
        return offsetNonce ? new CustomNonceTxnManager(web3jRo, roCred, chainId) : new RawTransactionManager(web3jRo, roCred, chainId, web3jAttempts, web3jTimeout)
    }

    RawTransactionManager roOnDemandTxnManager(String wallet) {
        return offsetNonce ? new CustomNonceTxnManager(web3jRo, getCreds(wallet), chainId) : new RawTransactionManager(web3jRo, getCreds(wallet), chainId, web3jAttempts, web3jTimeout)
    }

    RawTransactionManager rwOnDemandTxnManager(String wallet) {
        return offsetNonce ? new CustomNonceTxnManager(web3j, getCreds(wallet), chainId) : new RawTransactionManager(web3j, getCreds(wallet), chainId, web3jAttempts, web3jTimeout)
    }

    private Credentials getCreds(String wallet) {
        def users = flatDao.queryList("select wallet, signed_private_key, sign_key from dboe_key_admin.private_keys where wallet = '${wallet}'")
        if (users.isEmpty()) {
            throw new IllegalStateException("No key configuration for wallet: ${wallet}")
        }
        return Credentials.create(KeyHashUtils.unhashedKey(users.first()['signed_private_key'], users.first()['sign_key']))
    }

}
