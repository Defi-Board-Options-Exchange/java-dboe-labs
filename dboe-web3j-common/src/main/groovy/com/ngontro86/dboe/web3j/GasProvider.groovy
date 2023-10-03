package com.ngontro86.dboe.web3j

import com.ngontro86.common.annotations.ConfigValue
import com.ngontro86.common.annotations.DBOEComponent
import org.springframework.context.annotation.Bean
import org.web3j.tx.gas.ContractGasProvider

@DBOEComponent
class GasProvider {

    @ConfigValue(config = "gasPrice")
    private Long gasPrice = 25_000_000_000L

    @ConfigValue(config = "gasLimit")
    private Long gasLimit = 8_000_000L

    @ConfigValue(config = "gasEstimator")
    private String gasEstimator = "Double"

    @Bean
    ContractGasProvider provider() {
        return gasEstimator == 'Double' ?
                new DoubleStaticGasProvider(BigInteger.valueOf(gasPrice), BigInteger.valueOf(gasLimit))
                : new DynamicGasProvider()
    }
}
