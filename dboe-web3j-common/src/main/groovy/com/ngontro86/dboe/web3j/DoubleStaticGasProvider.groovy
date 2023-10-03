package com.ngontro86.dboe.web3j

import org.web3j.tx.gas.StaticGasProvider

class DoubleStaticGasProvider extends StaticGasProvider {

    DoubleStaticGasProvider(BigInteger gasPrice, BigInteger gasLimit) {
        super(gasPrice, gasLimit)
    }
}
