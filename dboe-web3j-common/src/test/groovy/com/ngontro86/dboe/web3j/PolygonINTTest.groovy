package com.ngontro86.dboe.web3j

import com.ngontro86.app.common.postprocessor.ConfigValuePostProcessor
import com.ngontro86.app.common.postprocessor.LoggerPostProcessor
import com.ngontro86.common.config.MaskedConfig
import com.ngontro86.component.testing.ComponentEnv
import com.ngontro86.dboe.web3j.encryption.KeyHashUtils
import com.ngontro86.utils.ResourcesUtils
import org.junit.Before
import org.junit.Test
import org.web3j.crypto.RawTransaction
import org.web3j.protocol.Web3j
import org.web3j.protocol.core.DefaultBlockParameterName
import org.web3j.tx.RawTransactionManager
import org.web3j.tx.gas.ContractGasProvider
import org.web3j.utils.Convert

class PolygonINTTest {


    ComponentEnv env

    @Before
    void "init"() {
        [
                //'ethereumNodeUrl'  : 'https://rpc.ankr.com/polygon/a2642745708278991fdd0e67fdf231ac9c2e2318de76bb0695433ec4a129812e',
                //'ethereumNodeUrl'  : 'https://polygon-mainnet.g.alchemy.com/v2/6DFTYfJcjb7L4xLcG_hT6VwqHpavk_yq',
                'ethereumNodeUrl'  : 'https://json-rpc.2aaynfw8c92s0hl9a79u1vdwh.blockchainnodeengine.com',
                //'ethereumNodeUrl'  : 'https://json-rpc.6umvljcbsy6suny4y4m5vf13n.blockchainnodeengine.com',
                //'credential'       : 'xxxx',
                'credential'       : 'xxx',
                'chainId'          : '137',
                'gasLimit'         : '12000000',
                'gasPrice'         : '500000000000',
                'web3jApiKey'      : 'xxxx-bXTP6XjUTsfzEZcU',
                //'gasEstimator'     : 'Dynamic',
                'nonceOffset'      : '16',
                'offsetNonce'      : 'false',
                'dboeHost.host'    : 'http://dboe.exchange',
                'dboeHost.basePath': 'api',
                'dboeHost.port'    : '8686'
        ].each { k, v -> System.setProperty(k, v) }

        env = ComponentEnv.env([Web3jClientProvider, GasProvider, TxnManagerProvider, ConfigValuePostProcessor, LoggerPostProcessor])
    }

    @Test
    void "should be able to unhash"() {
        //println KeyHashUtils.('xxx', 'DBOEToTheMoon')).build().hashedValue
        //println MaskedConfig.newInstance().setRawValue(KeyHashUtils.unhashedKey('xxxx', 'DBOEToTheMoon')).build().hashedValue
    }

    @Test
    void "should be able to get BTC and ETH price"() {
        def fsp = FspCalculator.load('0x731B70b86D494d6E6C3860Ad4EF6FCE04f25BA12', env.component(Web3j), env.component(RawTransactionManager), env.component(ContractGasProvider))
        println fsp.avgSpot(Utils.padding(32, 'ETH' as byte[]), 1699542000, 5).send()
        //println fsp.priceScales(Utils.padding(32, 'ETH' as byte[])).send()
    }

    @Test
    void "should be able to get option allowance"() {
        def option = DBOEBaseOption.load('0x36438da7313cac3c6e5b45f2346e8e17c9474e0b', env.component(Web3j), env.component(RawTransactionManager), env.component(ContractGasProvider))
        println option.allowance('0x01D3B6725103280DF855F8d8480CB098851bf3F6', '0x8a73d3f064d82baf38edb0f6d9a5d15a59b9dd6f').send()
    }

    @Test
    void "should be able to get user quotes"() {
        def web3j = env.component(Web3j)

        println web3j.ethBlockNumber().send().blockNumber
        System.exit(0)

        def dboeClob = DBOEClob.load('0xBfd5cB04D7719Eae3F8FFf79E23332b647D08797', env.component(Web3j), env.component(RawTransactionManager), env.component(ContractGasProvider))
        def bids = dboeClob.userQuotes('0x01d3b6725103280df855f8d8480cb098851bf3f6', Utils.padding(32, 'E2320C105' as byte[]), true).send()

        println bids.component1()
        println bids.component2()
        println bids.component3()
    }

    @Test
    void "should be able to find out nonce"() {
        def web3j = env.component(Web3j)
        def rawTxnManager = env.component(RawTransactionManager)
        def gasProvider = env.component(ContractGasProvider)

        def txCount = web3j.ethGetTransactionCount(rawTxnManager.fromAddress, DefaultBlockParameterName.PENDING).sendAsync().get()
        println "${rawTxnManager.fromAddress}, ${txCount.transactionCount}"
        sleep 2000

        txCount = web3j.ethGetTransactionCount(rawTxnManager.fromAddress, DefaultBlockParameterName.LATEST).sendAsync().get()
        println "${rawTxnManager.fromAddress}, ${txCount.transactionCount}"

        3.times {
            txCount = web3j.ethGetTransactionCount(rawTxnManager.fromAddress, DefaultBlockParameterName.LATEST).sendAsync().get()
            println "${rawTxnManager.fromAddress}, ${txCount.transactionCount}"
            sleep 2000

            def amt = Convert.toWei("1.0", Convert.Unit.ETHER).toBigInteger()

            def rawTxn = RawTransaction.createEtherTransaction(
                    txCount.transactionCount,
                    gasProvider.gasPrice,
                    gasProvider.gasLimit,
                    rawTxnManager.fromAddress,
                    amt
            )

            def sendTxn = web3j.ethSendRawTransaction(rawTxnManager.sign(rawTxn)).send()
            println "Sending transaction... now waiting for 30s"
            sleep 30000
            println "txn hash: ${sendTxn.getTransactionHash()}"

            println "Latest nonce: ${web3j.ethGetTransactionCount(rawTxnManager.fromAddress, DefaultBlockParameterName.LATEST).sendAsync().get().transactionCount}"
            println "Pending nonce: ${web3j.ethGetTransactionCount(rawTxnManager.fromAddress, DefaultBlockParameterName.PENDING).sendAsync().get().transactionCount}"
        }
    }

    @Test
    void "should be able to send USDT to other account"() {
        def web3j = env.component(Web3j)
        def rawTxnManager = env.component(RawTransactionManager)
        def gasProvider = env.component(ContractGasProvider)

        def txCount = web3j.ethGetTransactionCount(rawTxnManager.fromAddress, DefaultBlockParameterName.LATEST).sendAsync().get()
        println "${rawTxnManager.fromAddress}, ${txCount.transactionCount}"
        sleep 2000

        def usdt = ERC20.load('0xc2132D05D31c914a87C6611C10748AEb04B58e8F', web3j, rawTxnManager, gasProvider)
        def usdtScale = Math.pow(10, usdt.decimals().send())
        usdt.transfer('0x649Fb2a8eBd926FaF4375C7eD7259E74D1d7851d', 8340 * usdtScale as BigInteger).send()
        println "Sending transaction... no waiting for 30s"
        sleep 30000
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
    void "pull MATIC and USDT balances for AEVO wallets"() {
        def output = new File("C:\\Users\\truon\\OneDrive\\Desktop\\aevoAddresses.csv")
        def web3j = env.component(Web3j)
        def usdt = ERC20.load('0xc2132D05D31c914a87C6611C10748AEb04B58e8F', web3j, env.component(RawTransactionManager), env.component(ContractGasProvider))
        def scale = Math.pow(10, 18)
        def usdtScale = Math.pow(10, usdt.decimals().send())
        int numOfWallet = 0
        ResourcesUtils.lines('aevo-addresses').findAll { it.startsWith('0x') }.each { line ->
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
    void "pull MATIC and USDT balances for DBOE Forum Signup wallets"() {
        def output = new File("C:\\Users\\truon\\OneDrive\\Desktop\\dboeForumAddresses.csv")
        def web3j = env.component(Web3j)
        def usdt = ERC20.load('0xc2132D05D31c914a87C6611C10748AEb04B58e8F', web3j, env.component(RawTransactionManager), env.component(ContractGasProvider))
        def scale = Math.pow(10, 18)
        def usdtScale = Math.pow(10, usdt.decimals().send())
        int numOfWallet = 0
        ResourcesUtils.lines('forum-signup-addresses.csv').findAll { it.startsWith('0x') }.each { line ->
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
    void "should be able to load dashboard"() {
        //def spotDashboard = DBOESpotDashboard.load('0x0B84054d12350c2195dcDFBF3f71f291db4C9f00', env.component(Web3j), env.component(RawTransactionManager), env.component(ContractGasProvider))
        //println spotDashboard.dashboard().send()
        def spot = DBOESpotMarket.load('0x4fc9b357a48b2e9f09db5ac7d9d717e6cb9d6915', env.component(Web3j), env.component(RawTransactionManager), env.component(ContractGasProvider))
        println "Treasury: ${spot.treasuryAddress().send()}"
        println spot.getFixedSpreads(true).send()
        println spot.getFixedSpreads(false).send()

        100.times {
            def tups = spot.refInfo().send()
            println "current fair price: ${tups.component2() / tups.component3()}, as of:${tups.component4()}"
            sleep 5000
        }
    }

}
