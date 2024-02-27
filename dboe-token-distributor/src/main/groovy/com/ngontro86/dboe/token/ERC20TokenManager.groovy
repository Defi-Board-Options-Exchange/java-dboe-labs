package com.ngontro86.dboe.token

import com.ngontro86.common.annotations.ConfigValue
import com.ngontro86.common.annotations.DBOEComponent
import com.ngontro86.common.annotations.Web3jReadWrite
import com.ngontro86.dboe.web3j.ERC20
import org.web3j.protocol.Web3j
import org.web3j.protocol.core.RemoteCall
import org.web3j.protocol.core.methods.response.TransactionReceipt
import org.web3j.tx.RawTransactionManager
import org.web3j.tx.Transfer
import org.web3j.tx.gas.ContractGasProvider
import org.web3j.utils.Convert

import javax.annotation.PostConstruct
import javax.inject.Inject
import java.util.concurrent.Callable

@DBOEComponent
class ERC20TokenManager {

    @ConfigValue(config = "tokenAddresses")
    private Collection tokenAddresses = []

    @ConfigValue(config = "tokenAmounts")
    private Collection amounts

    @ConfigValue(config = "nativeTokenGasLimit")
    private Long nativeTokenGasLimit = 21000L

    @Inject
    @Web3jReadWrite
    private Web3j web3j

    @Inject
    private ContractGasProvider gasProvider

    @Inject
    @Web3jReadWrite
    private RawTransactionManager txnManager

    @ConfigValue(config = "nativeToken")
    private String nativeToken = 'ZBC'

    private Map<String, ERC20> tokens = [:]
    private Map<String, Double> tokenAmounts = [:]
    private Map<String, BigInteger> tokenDecimals = [:]

    @PostConstruct
    private void init() {
        tokenAddresses.each { pair ->
            def name = pair.split(":")[0]
            def address = pair.split(":")[1]
            println "Init: ${name}, at: ${address}"
            tokens[name] = ERC20.load(address, web3j, txnManager, gasProvider)
            tokenDecimals[name] = (tokens[name] as ERC20).decimals().send()
        }

        amounts.each { pair ->
            println "Amount: ${pair}"
            tokenAmounts[pair.split(":")[0]] = Double.valueOf(pair.split(":")[1])
        }
    }

    void transfer(String token, String walletAddress) {
        if (nativeToken == token) {
            println "Txn: ${tokenAmounts[token]} of native ${token} to ${walletAddress}..."
            transferNativeToken(token, walletAddress)
        } else {
            println "Txn: ${tokenAmounts[token]} of ${token} to ${walletAddress}..."
            def receipt = tokens[token].transfer(walletAddress, Math.pow(10, tokenDecimals[token]) * tokenAmounts[token] as BigInteger).send()
            println("BlockNum: ${receipt.blockNumber}, tx hash: ${receipt.transactionHash}, blockHash: ${receipt.blockHash} ...")
        }
    }

    private void transferNativeToken(String token, String walletAddress) {
        new RemoteCall<>(new Callable<TransactionReceipt>() {
            @Override
            TransactionReceipt call() throws Exception {
                return new Transfer(web3j, txnManager).send(walletAddress, BigDecimal.valueOf(tokenAmounts[token]), Convert.Unit.ETHER, gasProvider.getGasPrice(), nativeTokenGasLimit as BigInteger)
            }
        }).send()
    }

}
