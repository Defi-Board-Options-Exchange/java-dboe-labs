package com.ngontro86.dboe.web3j

import com.ngontro86.common.annotations.ConfigValue
import com.ngontro86.common.annotations.Web3jReadOnly
import org.web3j.protocol.Web3j
import org.web3j.tx.gas.ContractGasProvider

import javax.inject.Inject

class DynamicGasProvider implements ContractGasProvider {

    @ConfigValue(config = "gasLimit")
    private Long gasLimit = 8_000_000L

    @Inject
    @Web3jReadOnly
    private Web3j web3j

    @Override
    BigInteger getGasPrice(String contractFunc) {
        web3j.ethGasPrice().send().gasPrice
    }

    @Override
    BigInteger getGasPrice() {
        web3j.ethGasPrice().send().gasPrice
    }

    @Override
    BigInteger getGasLimit(String contractFunc) {
        BigInteger.valueOf(gasLimit)
    }

    @Override
    BigInteger getGasLimit() {
        BigInteger.valueOf(gasLimit)
    }
}
