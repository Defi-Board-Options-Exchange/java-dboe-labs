package com.ngontro86.dboe.web3j

import com.ngontro86.common.annotations.ConfigValue
import org.web3j.crypto.Credentials
import org.web3j.protocol.Web3j
import org.web3j.tx.RawTransactionManager

class CustomNonceTxnManager extends RawTransactionManager {

    @ConfigValue(config = "nonceOffset")
    private Integer nonceOffset = 16

    CustomNonceTxnManager(Web3j web3j, Credentials credentials, long chainId) {
        super(web3j, credentials, chainId)
    }

    @Override
    protected BigInteger getNonce() throws IOException {
        return super.getNonce() - nonceOffset
    }
}
