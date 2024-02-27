package com.ngontro86.dboe.web3j.token


import com.ngontro86.common.annotations.Web3jReadWrite
import com.ngontro86.dboe.web3j.ERC20
import com.ngontro86.dboe.web3j.TxnManagerDbProvider
import org.web3j.protocol.Web3j
import org.web3j.protocol.core.DefaultBlockParameterName
import org.web3j.tx.RawTransactionManager
import org.web3j.tx.gas.ContractGasProvider

import javax.inject.Inject

abstract class Web3jAbstractTokenLoader implements TokenLoader<ERC20> {

    @Inject
    @Web3jReadWrite
    protected Web3j web3j

    @Inject
    protected TxnManagerDbProvider txnManagerProvider

    @Inject
    protected ContractGasProvider gasProvider

    protected RawTransactionManager txnManager

    @Override
    String getOwnerAddress() {
        return txnManager.fromAddress
    }

    @Override
    ERC20 load(String address) {
        return ERC20.load(address, web3j, txnManager, gasProvider)
    }

    @Override
    BigInteger balanceOf(ERC20 token, String account) {
        return token.balanceOf(account).send()
    }

    @Override
    BigInteger allowance(ERC20 token, String owner, String spender) {
        return token.allowance(owner, spender).send()
    }

    @Override
    void approve(ERC20 token, String spender, BigInteger amount) {
        token.approve(spender, amount).send()
    }

    @Override
    BigInteger decimals(ERC20 token) {
        return token.decimals().send()
    }

    @Override
    BigInteger nativeTokenBalance(String addr) {
        web3j.ethGetBalance(addr, DefaultBlockParameterName.LATEST).send().balance
    }
}
