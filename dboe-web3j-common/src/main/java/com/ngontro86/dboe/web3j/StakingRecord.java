package com.ngontro86.dboe.web3j;

import org.web3j.abi.datatypes.DynamicStruct;
import org.web3j.abi.datatypes.generated.Uint256;

import java.math.BigInteger;

public class StakingRecord extends DynamicStruct {
    public BigInteger amount;

    public BigInteger time;

    public StakingRecord(BigInteger amount, BigInteger time) {
        super(new Uint256(amount), new Uint256(time));

        this.amount = amount;
        this.time = time;
    }

    public StakingRecord(Uint256 amount, Uint256 time) {
        super(amount, time);

        this.amount = amount.getValue();
        this.time = time.getValue();
    }
}
