package com.ngontro86.dboe.web3j;

import org.web3j.abi.datatypes.Address;
import org.web3j.abi.datatypes.DynamicBytes;
import org.web3j.abi.datatypes.DynamicStruct;
import org.web3j.abi.datatypes.generated.Bytes32;

public class DBOEOrderData extends DynamicStruct {
    public byte[] orderId;

    public Boolean isMarketOrder;

    public String addr;

    public byte[] signature;

    public DBOEOrderData(byte[] orderId, String addr, byte[] signature) {
        super(new Bytes32(orderId),new Address(addr),new DynamicBytes(signature));
        this.orderId = orderId;
        this.addr = addr;
        this.signature = signature;
    }

    public DBOEOrderData(Bytes32 orderId, Address addr, DynamicBytes signature) {
        super(orderId,addr,signature);
        this.orderId = orderId.getValue();
        this.addr = addr.getValue();
        this.signature = signature.getValue();
    }
}
