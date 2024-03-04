package com.ngontro86.server.dboe.services.copytrade

class CopyTradeReconciliationResult {
    double leaderPortfolioValue
    double userPortfolioValue
    Collection<PositionDiff> positionDiffs
}
