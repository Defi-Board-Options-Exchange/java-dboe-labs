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
                'ethereumNodeUrl'  : 'https://rpc.ankr.com/polygon/a2642745708278991fdd0e67fdf231ac9c2e2318de76bb0695433ec4a129812e',
                'credential'       : 'MDViMzI0ZmQ2MGM0ZTU5MzM5YjY0MmQ0Mzc2MTViNjFhM2Q5NGVmODFiMzBkZjk1NDVlZDhlM2VmOGQ0YjY5Yw==',
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
    void "should be able to unhash"() {
        println MaskedConfig.newInstance().setRawValue(KeyHashUtils.unhashedKey('eyJhbGciOiJIUzI1NiJ9.eyJpc3MiOiJEQk9FIiwic3ViIjoiUHJpdmF0ZSBLZXkgU2lnbmluZyIsImV4cCI6MTcwMzk1MjAwMCwiY29tbW9uUGhyYXNlIjoiREJPRSIsInByaXZhdGVLZXkiOiIwNWIzMjRmZDYwYzRlNTkzMzliNjQyZDQzNzYxNWI2MWEzZDk0ZWY4MWIzMGRmOTU0NWVkOGUzZWY4ZDRiNjljIn0.7GhuDrGPMS9GB9BVtBt9pDYqKz_JiQ9nTYwMZGEzPl4', 'DBOEToTheMoon')).build().hashedValue
    }

    @Test
    void "should be able to get BTC and ETH price"() {
        def fsp = FspCalculator.load('0x731B70b86D494d6E6C3860Ad4EF6FCE04f25BA12', env.component(Web3j), env.component(RawTransactionManager), env.component(ContractGasProvider))
        println fsp.avgSpot(Utils.padding(32, 'ETH' as byte[]), 1699369200, 5).send()
        //println fsp.priceScales(Utils.padding(32, 'ETH' as byte[])).send()
    }

    @Test
    void "should be able to final settle ETH price"() {
        def optionFactory = DBOEOptionFactory.load('0x231E7ced4B68Ffd6AEF68990F7967a93720A663F', env.component(Web3j), env.component(RawTransactionManager), env.component(ContractGasProvider))
        //optionFactory.finalSettle(Utils.padding(32, 'ETH' as byte[]), 20231016).send()
        //println optionFactory.underlyingPrice(Utils.padding(32, 'ETH' as byte[]), 20231006).send()
        //optionFactory.manualFinalSettle(Utils.padding(32, 'ETH' as byte[]), 20231006, 16342590).send()
    }

    @Test
    void "update ref prices for BTC"() {
        def clob = DBOEClob.load('0xeC4e8861b70Db029d687306AC131B875D6bB1e4b', env.component(Web3j), env.component(RawTransactionManager), env.component(ContractGasProvider))
        ["B27600C1013", "B27600P1013", "B27750C1013", "B27750P1013", "B27900C1013", "B27900P1013", "B28050C1013", "B28050P1013", "B28200C1013", "B28200P1013", "B28350C1013", "B28350P1013"].each {
            clob.refreshRefPx(Utils.padding(32, it as byte[])).send()
        }
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
