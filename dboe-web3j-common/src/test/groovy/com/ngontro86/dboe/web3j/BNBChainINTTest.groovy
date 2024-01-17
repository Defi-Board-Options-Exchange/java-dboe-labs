package com.ngontro86.dboe.web3j

import com.ngontro86.app.common.postprocessor.ConfigValuePostProcessor
import com.ngontro86.app.common.postprocessor.LoggerPostProcessor
import com.ngontro86.component.testing.ComponentEnv
import com.ngontro86.utils.ResourcesUtils
import org.junit.Before
import org.junit.Test
import org.web3j.protocol.Web3j
import org.web3j.protocol.core.DefaultBlockParameterName
import org.web3j.tx.RawTransactionManager
import org.web3j.tx.gas.ContractGasProvider

class BNBChainINTTest {


    ComponentEnv env

    @Before
    void "init"() {
        [
                'ethereumNodeUrl': 'https://rpc.ankr.com/bsc/a2642745708278991fdd0e67fdf231ac9c2e2318de76bb0695433ec4a129812e',
                'credential'     : 'xxxx',
                'chainId'        : '56',
                'gasLimit'       : '12000000',
                'gasPrice'       : '300000000000',
                'gasEstimator'   : 'Dynamic',
                'nonceOffset'    : '16',
                'offsetNonce'    : 'false'
        ].each { k, v -> System.setProperty(k, v) }

        env = ComponentEnv.env([Web3jClientProvider, GasProvider, TxnManagerProvider, ConfigValuePostProcessor, LoggerPostProcessor])
    }

    @Test
    void "should be able to airdrop USDT to other community"() {
        def web3j = env.component(Web3j)
        def rawTxnManager = env.component(RawTransactionManager)
        def gasProvider = env.component(ContractGasProvider)

        def txCount = web3j.ethGetTransactionCount(rawTxnManager.fromAddress, DefaultBlockParameterName.LATEST).sendAsync().get()
        println "${rawTxnManager.fromAddress}, ${txCount.transactionCount}"
        sleep 2000

        def output = new File("C:\\Users\\truon\\OneDrive\\Desktop\\dboeForumRewardAirdrop.csv")

        def dboeToken = ERC20.load('0xa5ba8b84dca8ba5976ae780c852c7b8485be87c7', web3j, rawTxnManager, gasProvider)
        def dboeScale = Math.pow(10, dboeToken.decimals().send()) as BigInteger

        int numOfWallet = 0
        ResourcesUtils.lines('dboe-forum-3.csv').findAll { it.contains('0x') }.each { line ->
            def addr = line
            println "Working on ${line}..."
            try {
                def amount = 10 * dboeScale as BigInteger
                def receipt = dboeToken.transfer(addr, amount).send()
                println "Sent to ${numOfWallet} wallets..."
                sleep 500
                output << "${line},${amount}, ${receipt.getTransactionHash()}\n"
            } catch (Exception e) {
                println e
            }
            numOfWallet++
        }
    }

    @Test
    void "should be able to withdraw back DBOE token from Pre GoLive Airdrop Smart Contract"() {
        def web3j = env.component(Web3j)
        def rawTxnManager = env.component(RawTransactionManager)
        def gasProvider = env.component(ContractGasProvider)
        println "Using wallet: ${rawTxnManager.fromAddress}"

        def airdrop = DBOEGoLiveAirdrop.load('0x39D75fB1f73eFBf8159dC77eDF69cDC15266B8C2', web3j, rawTxnManager, gasProvider)
        airdrop.withdraw(Math.pow(10, 18) * 474.9999 as BigInteger).send()
    }

    @Test
    void "should provide a random number between min and max"() {
        10.times {
            println random(4, 6, Math.pow(10, 18) as BigInteger)
        }
    }

    static BigInteger random(int min, int max, BigInteger scale) {
        return (min + Math.round((max - min) * Math.random() / 0.25) * 0.25) * scale
    }

}
