package com.ngontro86.market.time

class SystemTimeSource implements TimeSource {

    @Override
    long currentTimeMilliSec() {
        return System.currentTimeMillis()
    }
}
