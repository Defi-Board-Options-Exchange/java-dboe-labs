package com.ngontro86.dboe.web3j

import com.ngontro86.app.common.db.DataSourceProvider
import com.ngontro86.app.common.db.FlatDao
import com.ngontro86.app.common.db.FlatDaoProvider
import com.ngontro86.app.common.postprocessor.ConfigValuePostProcessor
import com.ngontro86.app.common.postprocessor.LoggerPostProcessor
import com.ngontro86.common.config.MaskedConfig
import com.ngontro86.component.testing.ComponentEnv
import com.ngontro86.dboe.web3j.token.Web3jTokenLoader
import com.ngontro86.utils.ResourcesUtils
import org.junit.Before
import org.junit.Test
import org.web3j.crypto.RawTransaction
import org.web3j.protocol.Web3j
import org.web3j.protocol.core.DefaultBlockParameterName
import org.web3j.tx.RawTransactionManager
import org.web3j.tx.gas.ContractGasProvider
import org.web3j.utils.Convert

class VariousINTTest {

    ComponentEnv env

    @Before
    void "init"() {
        [
                //'ethereumNodeUrl'    : 'https://rpc.ankr.com/polygon/a2642745708278991fdd0e67fdf231ac9c2e2318de76bb0695433ec4a129812e',
                //'ethereumRONodeUrl'  : 'https://rpc.ankr.com/polygon/a2642745708278991fdd0e67fdf231ac9c2e2318de76bb0695433ec4a129812e',
                'ethereumNodeUrl'    : 'https://polygon-mainnet.g.alchemy.com/v2/6DFTYfJcjb7L4xLcG_hT6VwqHpavk_yq',
                'ethereumRONodeUrl'  : 'https://polygon-mainnet.g.alchemy.com/v2/6DFTYfJcjb7L4xLcG_hT6VwqHpavk_yq',
                //'ethereumNodeUrl'    : 'https://bsc-dataseed.binance.org/',
                //'ethereumRONodeUrl'  : 'https://bsc-dataseed.binance.org/',
                //'ethereumNodeUrl'    : 'https://json-rpc.2aaynfw8c92s0hl9a79u1vdwh.blockchainnodeengine.com',
                //'ethereumRONodeUrl'  : 'https://json-rpc.2aaynfw8c92s0hl9a79u1vdwh.blockchainnodeengine.com',
                //'ethereumNodeUrl'  : 'https://json-rpc.6umvljcbsy6suny4y4m5vf13n.blockchainnodeengine.com',
                'spotWallet'         : 'NewDeployment',
                'rwWallet'           : 'NewDeployment',
                'chainId'            : '137',
                //'chainId'            : '56',
                'gasLimit'           : '7000000',
                'gasPrice'           : '150000000000',
                //'web3jApiKey'        : 'xxx-bXTP6XjUTsfzEZcU',
                'gasEstimator'       : 'Dynamic',
                'offsetNonce'        : 'false',
                'nonceOffset'        : '16',
                'datasource.host'    : 'localhost',
                'datasource.name'    : 'dboe_key_admin',
                'datasource.username': 'root',
                'datasource.password': 'root'
        ].each { k, v -> System.setProperty(k, v) }

        env = ComponentEnv.env([Web3jClientProvider, Web3jTokenLoader, GasProvider, TxnManagerDbProvider, FlatDao, FlatDaoProvider, DataSourceProvider, ConfigValuePostProcessor, LoggerPostProcessor])
    }

    @Test
    void "should be able to unmask"() {
        println MaskedConfig.newInstance().setHashedValue('').build().unmaskedValue
    }

    @Test
    void "should be able to withdraw from Spot DMM V1"() {
        def web3jClientProvider = env.component(Web3jClientProvider)
        def txnDbProvider = env.component(TxnManagerDbProvider)
        println "Wallet: ${txnDbProvider.rwTxnManager().fromAddress}"
        def dmm = DedicatedSpotMarketMaker.load('0x7869365da3747de9E615aa413Ac948CC62866349', web3jClientProvider.web3jRWClient(), txnDbProvider.rwTxnManager(), env.component(ContractGasProvider))
        println "Owner: ${dmm.owner().send()}"
        [
                '0x0d500b1d8e8ef31e21c99d1db9a6444d3adf1270',
                '0x1c954e8fe737f99f68fa1ccda3e51ebdb291948c',
                '0x53e0bca35ec356bd5dddfebbd1fc0fd03fabad39',
                '0x172370d5cd63279efa6d502dab29171933a610af',
                '0x9c2c5fd7b07e95ee044ddeba0e97a665f142394f',
                '0xa1c57f48f0deb89f569dfbe6e2b7f46d33606fd4',
                '0xd6df932a45c0f255f85145f286ea0b292b21c90b',
                '0x8f3cf7ad23cd3cadbd9735aff958023239c6a063',
                '0x5fe2b58c013d7601147dcdd68c143a77499f5531',
                '0xc2132d05d31c914a87c6611c10748aeb04b58e8f'
        ].each { addr ->
            def erc20 = ERC20.load(addr, web3jClientProvider.web3jROClient(), txnDbProvider.roTxnManager(), env.component(ContractGasProvider))
            def balance = erc20.balanceOf('0x7869365da3747de9E615aa413Ac948CC62866349').send()
            try {
                if (balance > 0) {
                    println "Withdrawing ${erc20.name().send()}, ${balance}..."
                    dmm.withdraw(balance, addr).send()
                }
            } catch (Exception e) {
                e.printStackTrace()
            }
        }
    }

    @Test
    void "should be able to get FSP prices"() {
        println "${Utils.getTimeUtc(20240229, 152000) / 1000L}"
        def web3jClientProvider = env.component(Web3jClientProvider)
        def txnDbProvider = env.component(TxnManagerDbProvider)
        def fsp = FspCalculator.load('0x7691840D55355Cf2CeA32Ec113235B9a928a369A', web3jClientProvider.web3jROClient(), txnDbProvider.roTxnManager(), env.component(ContractGasProvider))

        ['MATIC', 'BNB', 'SOL', 'LINK', 'XRP', 'DOGE'].each {
            def underlyingFsp = fsp.avgSpot(Utils.padding(32, it as byte[]), 1709221270 as BigInteger, 5 as BigInteger).send()
            println "${it}, ${underlyingFsp}"
        }
    }

    @Test
    void "should be able to final settle prices"() {
        def web3jClientProvider = env.component(Web3jClientProvider)
        def txnDbProvider = env.component(TxnManagerDbProvider)
        def optionFactory = DBOEOptionFactory.load('0xE8D5BE77d69c074E9df81f8D2D2fdBb931095F7F', web3jClientProvider.web3jRWClient(), txnDbProvider.rwTxnManager(), env.component(ContractGasProvider))
        println "OptionFactory loaded..."
        [
                //'LINK': 179748,
                //'SOL' : 992900,
                //'XRP' : 4996,
                //'DOGE': 78820,
                //'ETH': 22912188
        ].each { und, val ->
            println "Final settling ${und}, underlying: ${val}..."
            optionFactory.manualFinalSettle(Utils.padding(32, und as byte[]), 20240202, val as BigInteger).send()
        }
    }


    @Test
    void "should be able to pull OB"() {
        def web3jClientProvider = env.component(Web3jClientProvider)
        def txnDbProvider = env.component(TxnManagerDbProvider)
        def dboeClob = DBOEClob.load('0x1af79ce441EdF8c3dcc9d3f2628486aeF667cEF9', web3jClientProvider.web3jRWClient(), txnDbProvider.rwTxnManager(), env.component(ContractGasProvider))
        println "DBOE CLOB loaded..."

        def bids = dboeClob.obDepth(Utils.padding(32, "E3050C301" as byte[]), true).send()
        bids.each {
            println it
        }

        def asks = dboeClob.obDepth(Utils.padding(32, "E3050C301" as byte[]), false).send()
        asks.each {
            println it
        }
    }

    @Test
    void "should be able to stake into DBOE Staking Service"() {
        def web3jClientProvider = env.component(Web3jClientProvider)
        def txnDbProvider = env.component(TxnManagerDbProvider)

        def nusd = env.component(Web3jTokenLoader).load('0x706566d265f756a05D81bA502B86D6FEa572092f')
        def scale = Math.pow(10, nusd.decimals().send())
        println "NUSD scale: ${scale}"

        println "Approve Spending Limit..."
        //nusd.approve('0xfA0de58633d1F33D9B1d4b58d9188FA87f2503b5', 100000 * scale as BigInteger).send()

        def dboeTokenStaking = DBOETokenStaking.load('0xfA0de58633d1F33D9B1d4b58d9188FA87f2503b5', web3jClientProvider.web3jRWClient(), txnDbProvider.rwTxnManager(), env.component(ContractGasProvider))
        println "DBOE Token Staking loaded..."

        println "Staking into the 1M pool..."
        dboeTokenStaking.stake(1000 * scale as BigInteger, 0).send()

        println "Staking into the 3M pool..."
        dboeTokenStaking.stake(5000 * scale as BigInteger, 1).send()

        println "Staking into the 6M pool..."
        dboeTokenStaking.stake(2000 * scale as BigInteger, 2).send()

        println "Staking into the 12M pool..."
        dboeTokenStaking.stake(2000 * scale as BigInteger, 3).send()
    }

    @Test
    void "should be able to withdraw from Dedicated Options Market Maker Smart Contract"() {
        def web3jClientProvider = env.component(Web3jClientProvider)
        def txnDbProvider = env.component(TxnManagerDbProvider)

        def marketMaker = DedicatedOptionsMarketMaker.load('0xE25Ee7801149da83005Dd388c788bf276E7F92f8', web3jClientProvider.web3jRWClient(), txnDbProvider.rwTxnManager(), env.component(ContractGasProvider))
        println "Dedicated Options Market Maker loaded..."

        println "Withdrawing from the Smart Contract..."
        marketMaker.withdraw(5 * Math.pow(10, 6) as BigInteger, '0xc2132D05D31c914a87C6611C10748AEb04B58e8F').send()
    }

    @Test
    void "should be able to get allowance"() {
        def web3jClientProvider = env.component(Web3jClientProvider)
        def txnDbProvider = env.component(TxnManagerDbProvider)

        def longOption = ERC20.load('0xa3490cfec7bd75aba4ea5b158ad758b5af479046', web3jClientProvider.web3jRWClient(), txnDbProvider.rwTxnManager(), env.component(ContractGasProvider))
        println longOption.allowance('0xab2e8abd855d35036526dd843dc39e6d36d53c98', '0x0D138984951b2D452E22fAa0B63Cefb55098E0B2').send()

        def shortOption = ERC20.load('0x854b49c9d979733ec09eabff49c4a748a4dfd621', web3jClientProvider.web3jRWClient(), txnDbProvider.rwTxnManager(), env.component(ContractGasProvider))
        println shortOption.allowance('0xab2e8abd855d35036526dd843dc39e6d36d53c98', '0x0D138984951b2D452E22fAa0B63Cefb55098E0B2').send()

    }

    @Test
    void "should be able to get Option ref price"() {
        def web3jClientProvider = env.component(Web3jClientProvider)
        def txnDbProvider = env.component(TxnManagerDbProvider)

        def gps = DBOEGlobalPricingSystem.load('0x5268751296015Fb5EB3743CAcdd24695fcd47a55', web3jClientProvider.web3jRWClient(), txnDbProvider.rwTxnManager(), env.component(ContractGasProvider))
        println gps.estVol(Utils.padding(32, 'BTC' as byte[]), 626400, -0.2135d * Math.pow(10, 18) as BigInteger).send()

        def optionFactory = DBOEOptionFactory.load('0xE8D5BE77d69c074E9df81f8D2D2fdBb931095F7F', web3jClientProvider.web3jRWClient(), txnDbProvider.rwTxnManager(), env.component(ContractGasProvider))
        println optionFactory.spot(Utils.padding(32, 'BTC' as byte[])).send()

        def dboeClob = DBOEClob.load('0x1af79ce441EdF8c3dcc9d3f2628486aeF667cEF9', web3jClientProvider.web3jRWClient(), txnDbProvider.rwTxnManager(), env.component(ContractGasProvider))
        println "DBOE Onchain CLOB loaded..."
        println dboeClob.refInfo(Utils.padding(32, 'B42000P223' as byte[])).send()
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
    void "pull MATIC and USDT balances for KYT wallets"() {
        def output = new File("C:\\Users\\truon\\OneDrive\\Desktop\\kytAddresses.csv")
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
        def output = new File("C:\\Users\\truon\\OneDrive\\Desktop\\aevoAddresses2.csv")
        def web3j = env.component(Web3jTokenLoader)
        def usdt = web3j.load('0xc2132D05D31c914a87C6611C10748AEb04B58e8F')
        // ERC20.load('0xc2132D05D31c914a87C6611C10748AEb04B58e8F', web3j, env.component(RawTransactionManager), env.component(ContractGasProvider))
        def scale = Math.pow(10, 18)
        def usdtScale = Math.pow(10, usdt.decimals().send())
        int numOfWallet = 0
        ResourcesUtils.lines('aevo-addresses').findAll { it.startsWith('0x') }.each { line ->
            def addr = line.split(",")[0]
            try {
                output << line + ",${usdt.balanceOf(addr).send() / usdtScale},${web3j.nativeTokenBalance(addr) / scale}\n"
            } catch (Exception e) {
            }
            numOfWallet++
            if (Math.random() < 0.1) {
                println "Queried: ${numOfWallet} already..."
            }
        }
    }

    @Test
    void "airdrop DBOE to AEVO wallets"() {
        def output = new File("C:\\Users\\truon\\OneDrive\\Desktop\\aevoAddressesSent.csv")
        def usdt = env.component(Web3jTokenLoader).load('0xa5Ba8B84DcA8bA5976AE780C852C7B8485BE87C7')

        def usdtScale = Math.pow(10, usdt.decimals().send())
        def wallets = ResourcesUtils.lines('aevo-address-airdrop-dboe.csv').findAll { it.startsWith('0x') } as Set
        println "Num of wallets: ${wallets.size()}"
        wallets.each { wallet ->
            try {
                double amt = Math.round((1.0 + Math.random()) / 0.25) * 0.25d
                usdt.transfer(wallet, usdtScale * amt as BigInteger).send()
                output << "${wallet},${amt}\n"
                println "Sent:${wallet},${amt}"
            } catch (Exception e) {
                e.printStackTrace()
            }
        }
    }

    @Test
    void "airdrop DBOE to DBOE Forum # 2 users"() {
        def output = new File("C:\\Users\\truon\\OneDrive\\Desktop\\dboeForum2AddressesSent.csv")
        def dboe = env.component(Web3jTokenLoader).load('0xa5Ba8B84DcA8bA5976AE780C852C7B8485BE87C7')

        def dboeScale = Math.pow(10, dboe.decimals().send())
        def wallets = ResourcesUtils.lines('dboe-forum-2-users.csv').findAll { it.startsWith('0x') }.collectEntries {
            [(it.split(",")[0]): Double.valueOf(it.split(",")[1])]
        }
        println "Num of wallets: ${wallets.size()}"
        wallets.each { println "Wallet: ${it.key}, amt: ${it.value}" }

        wallets.each { wallet, amt ->
            try {
                dboe.transfer(wallet, dboeScale * amt as BigInteger).send()
                output << "${wallet},${amt}\n"
                println "Sent:${wallet},${amt}"
            } catch (Exception e) {
                e.printStackTrace()
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

}
