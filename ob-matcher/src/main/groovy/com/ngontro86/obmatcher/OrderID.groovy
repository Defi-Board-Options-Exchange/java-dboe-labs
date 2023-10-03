package com.ngontro86.obmatcher

class OrderID {
    String walletAddress
    String userOrderId
    String exOrderId
    String orderHash
    String txnSig

    static OrderID fromMap(Map map) {
        return new OrderID(
                walletAddress: map['wallet_id'],
                userOrderId: map['user_order_id'],
                exOrderId: map['ex_order_id'],
                orderHash: map['order_hash'],
                txnSig: map['txn_sig']
        )
    }

    @Override
    boolean equals(Object oi) {
        if (oi instanceof OrderID) {
            def oi2 = (OrderID) oi
            return walletAddress == oi2.walletAddress &&
                    exOrderId == oi2.exOrderId &&
                    userOrderId == oi2.userOrderId &&
                    orderHash == oi2.orderHash &&
                    txnSig == oi2.txnSig
        }
        return false
    }

    @Override
    int hashCode() {
        final int prime = 31
        int result = 1
        result = prime * result + ((walletAddress == null) ? 0 : walletAddress.hashCode())
        result = prime * result + ((exOrderId == null) ? 0 : exOrderId.hashCode())
        result = prime * result + ((userOrderId == null) ? 0 : userOrderId.hashCode())
        result = prime * result + ((orderHash == null) ? 0 : orderHash.hashCode())
        result = prime * result + ((txnSig == null) ? 0 : txnSig.hashCode())
        return result
    }

    @Override
    String toString() {
        return "wallet: ${walletAddress}, ex order id: ${exOrderId}".toString()
    }

}
