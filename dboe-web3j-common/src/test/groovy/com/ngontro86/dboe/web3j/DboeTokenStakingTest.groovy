package com.ngontro86.dboe.web3j

import com.ngontro86.app.common.postprocessor.ConfigValuePostProcessor
import com.ngontro86.app.common.postprocessor.LoggerPostProcessor
import com.ngontro86.component.testing.ComponentEnv
import org.junit.Before
import org.junit.Test
import org.web3j.protocol.Web3j
import org.web3j.tx.RawTransactionManager
import org.web3j.tx.gas.ContractGasProvider

class DboeTokenStakingTest {


    ComponentEnv env

    @Before
    void "init"() {
        [
                'ethereumNodeUrl': 'https://api.nautilus.nautchain.xyz',
                'credential'     : 'xxxx',
                'chainId'        : '22222',
                'gasLimit'       : '100000000',
                'gasPrice'       : '30000000000'
        ].each { k, v -> System.setProperty(k, v) }

        env = ComponentEnv.env([Web3jClientProvider, GasProvider, TxnManagerDbProvider, ConfigValuePostProcessor, LoggerPostProcessor])
    }

    @Test
    void "should be able to stake"() {
        def nusd = ERC20.load('0x8bbB0fC25CC557DDE93ebB93Fc4a7B7321787659', env.component(Web3j), env.component(RawTransactionManager), env.component(ContractGasProvider))
        nusd.approve('0xb97a784Cad1736dF91C4e2293ACaCb939a0dC012', Math.pow(10, 18) * 50000 as BigInteger).send()

        def dboeStake = DBOETokenStaking.load('0xb97a784Cad1736dF91C4e2293ACaCb939a0dC012', env.component(Web3j), env.component(RawTransactionManager), env.component(ContractGasProvider))

        dboeStake.stake(Math.pow(10, 18) * 8000 as BigInteger, 0 as BigInteger).send()
        dboeStake.stake(Math.pow(10, 18) * 10000 as BigInteger, 1 as BigInteger).send()
        dboeStake.stake(Math.pow(10, 18) * 10000 as BigInteger, 2 as BigInteger).send()
    }

    @Test
    void "should be able to unstake"() {
        def dboeStake = DBOETokenStaking.load('0xb97a784Cad1736dF91C4e2293ACaCb939a0dC012', env.component(Web3j), env.component(RawTransactionManager), env.component(ContractGasProvider))

        dboeStake.unstake(1, 0).send()
    }

    @Test
    void "should be able to withdraw"() {
        def dboeStake = DBOETokenStaking.load('0x833E23d7f7aD9C32d49B6e89C6a0Da4fE69EDbb9', env.component(Web3j), env.component(RawTransactionManager), env.component(ContractGasProvider))
        dboeStake.withdraw(Math.pow(10, 18) * 500 as BigInteger).send()
    }

    @Test
    void "should be able to pull staking info for a given wallet"() {
        def dboeStake = DBOETokenStaking.load('0x833E23d7f7aD9C32d49B6e89C6a0Da4fE69EDbb9', env.component(Web3j), env.component(RawTransactionManager), env.component(ContractGasProvider))
        def stakingInfo = dboeStake.userStakingInfo("0x4acd5Cc057c1b8c771E2E3cD3e30780Ca257dEC0", 0 as BigInteger).send()

        stakingInfo.each { println it }
    }

}
