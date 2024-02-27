package com.ngontro86.dboe.web3j.token

import com.ngontro86.common.annotations.ConfigValue
import com.ngontro86.dboe.web3j.inmemory.InMemoryERC20
import com.ngontro86.utils.ResourcesUtils

import javax.annotation.PostConstruct

class InMemoryTokenLoader implements TokenLoader<InMemoryERC20> {

    @ConfigValue(config = "blockchainConfigFile")
    private String blockchainConfigFile

    private Map<String, InMemoryERC20> prebuiltTokens = [:]

    @PostConstruct
    private void init() {
        InMemoryERC20 token
        ResourcesUtils.lines(blockchainConfigFile).each { line ->
            if (line.trim().startsWith('#')) {
                def addr = line.trim().replace('#', '')
                prebuiltTokens[addr] = new InMemoryERC20(addr)
                token = prebuiltTokens[addr]
            } else {
                def toks = line.trim().split(':')
                token.setBalance(toks[0], Long.valueOf(toks[1]))
            }
        }
    }

    @Override
    String getOwnerAddress() {
        return null
    }

    @Override
    InMemoryERC20 load(String address) {
        return prebuiltTokens.getOrDefault(address, new InMemoryERC20(address))
    }

    @Override
    BigInteger balanceOf(InMemoryERC20 token, String account) {
        return token.balanceOf(account)
    }

    @Override
    BigInteger allowance(InMemoryERC20 token, String owner, String spender) {
        return 0
    }

    @Override
    void approve(InMemoryERC20 token, String spender, BigInteger amount) {
        // Nothing
    }

    @Override
    BigInteger decimals(InMemoryERC20 token) {
        return 18
    }

    @Override
    BigInteger nativeTokenBalance(String addr) {
        return 0
    }
}
