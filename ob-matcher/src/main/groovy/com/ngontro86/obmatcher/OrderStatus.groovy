package com.ngontro86.obmatcher

enum OrderStatus {
    NEW,
    PARTIAL_FILLED,
    FILLED,
    AWAITING_BLOCKCHAIN_CONFIRMATION,
    BLOCKCHAIN_REJECTION
}