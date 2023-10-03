package com.ngontro86.dboe.web3j.token

interface TokenLoader<T> {

    T load(String address)

    BigInteger balanceOf(T token, String account)

    BigInteger allowance(T token, String owner, String spender)

    void approve(T token, String spender, BigInteger amount)

    BigInteger decimals(T token)
}
