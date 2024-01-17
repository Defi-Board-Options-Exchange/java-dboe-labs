package com.ngontro86.dboe.web3j

import com.ngontro86.app.common.postprocessor.ConfigValuePostProcessor
import com.ngontro86.app.common.postprocessor.LoggerPostProcessor
import com.ngontro86.common.config.MaskedConfig
import com.ngontro86.component.testing.ComponentEnv
import com.ngontro86.dboe.web3j.encryption.KeyHashUtils
import com.ngontro86.utils.ResourcesUtils
import org.junit.Before
import org.junit.Test
import org.web3j.protocol.Web3j
import org.web3j.protocol.core.DefaultBlockParameterName
import org.web3j.tx.RawTransactionManager
import org.web3j.tx.gas.ContractGasProvider

class ArbitrumINTTest {


    ComponentEnv env

    @Before
    void "init"() {
        [
                'ethereumNodeUrl'  : 'https://rpc.ankr.com/polygon/yyyy',
                'credential'       : 'xxxx',
                'chainId'          : '137',
                'gasLimit'         : '7000000',
                'gasPrice'         : '90000000000',
                'dboeHost.host'    : 'http://dboe.exchange',
                'dboeHost.basePath': 'api',
                'dboeHost.port'    : '8686'
        ].each { k, v -> System.setProperty(k, v) }

        env = ComponentEnv.env([Web3jClientProvider, GasProvider, TxnManagerProvider, ConfigValuePostProcessor, LoggerPostProcessor])
    }

    @Test
    void "should be able to get BTC and ETH price"() {
        def fsp = FspCalculator.load('0x731B70b86D494d6E6C3860Ad4EF6FCE04f25BA12', env.component(Web3j), env.component(RawTransactionManager), env.component(ContractGasProvider))
        println fsp.avgSpot(Utils.padding(32, 'ETH' as byte[]), 1699369200, 5).send()
        //println fsp.priceScales(Utils.padding(32, 'ETH' as byte[])).send()
    }


    @Test
    void "pull MATIC and USDT balances for KYT wallets"() {

        def output = new File("C:\\Users\\truon\\OneDrive\\Desktop\\kytAddresses2.csv")
        def web3j = env.component(Web3j)
        def usdt = ERC20.load('0xc2132D05D31c914a87C6611C10748AEb04B58e8F', web3j, env.component(RawTransactionManager), env.component(ContractGasProvider))
        def scale = Math.pow(10, 18)
        def usdtScale = Math.pow(10, usdt.decimals().send())
        int numOfWallet = 0
        ResourcesUtils.lines('kyt-addresses').findAll { it.startsWith('0x') }.each { line ->
            def addr = line.split(",")[0]
            try {
                output << line + ",${usdt.balanceOf(addr).send() / usdtScale},${web3j.ethGetBalance(addr, DefaultBlockParameterName.LATEST).send().balance / scale}\n"
            } catch (Exception e) {
            }
            numOfWallet++
            if (Math.random() < 0.1) {
                println "Queried: ${numOfWallet} already..."
            }
        }
    }

    @Test
    void "should work out number of deposit and avg time"() {
        def output = new File("C:\\Users\\truon\\OneDrive\\Desktop\\aevoDepositsOutput.csv")

        ResourcesUtils.lines('aevo-deposits.csv').groupBy { it.split(",")[2] }.collect { wallet, lines ->
            def sortedTime = lines.collect { Long.valueOf(it.split(",")[1]) }.sort()
            def avgDepositTime = 0d
            if (!sortedTime.isEmpty() && sortedTime.size() > 1) {
                def prev = sortedTime.first()
                sortedTime.each {
                    avgDepositTime += it - prev
                    prev = it
                }
                avgDepositTime /= (sortedTime.size() - 1)
            }
            println "${wallet}, ${lines.size()}, ${avgDepositTime}"
        }
    }

}
