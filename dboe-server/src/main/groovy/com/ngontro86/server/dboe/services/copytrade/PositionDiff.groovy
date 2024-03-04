package com.ngontro86.server.dboe.services.copytrade

class PositionDiff {
    String token
    boolean isOption
    boolean isCash
    double leaderPos
    double userPos
    double idealUserPos
    boolean needAdjustment
}
