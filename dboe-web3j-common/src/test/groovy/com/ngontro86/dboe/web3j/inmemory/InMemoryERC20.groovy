package com.ngontro86.dboe.web3j.inmemory

class InMemoryERC20 {

    private String address
    private Map<String, Long> balanceMap = [:]

    protected InMemoryERC20(String contractAddress) {
        this.address = contractAddress
    }

    void setBalance(String addr, Long balance) {
        balanceMap.put(addr, balance)
    }

    BigInteger balanceOf(String account) {
        return balanceMap.getOrDefault(account, 0) as BigInteger
    }
}
