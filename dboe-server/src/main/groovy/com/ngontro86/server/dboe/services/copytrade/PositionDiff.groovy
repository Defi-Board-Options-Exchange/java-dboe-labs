package com.ngontro86.server.dboe.services.copytrade

class PositionDiff {
    String token
    String underlying
    int expiry
    double strike
    double condStrike
    String longContractAddr
    String shortContractAddr
    String currencyAddr
    String obAddr
    String optionFactoryAddr
    String clearingHouseAddr
    boolean isOption
    boolean isCash
    double leaderPos
    double userPos
    double idealUserPos
    boolean needAdjustment
}
