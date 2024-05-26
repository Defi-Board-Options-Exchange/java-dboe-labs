package com.ngontro86.server.dboe.services.luckydraw

class LuckyDrawStats {
    Collection<String> mostRecentLuckyWallets = new ArrayList(20)
    int totalLuckyDraw
    int totalRequest

    void incrementReq() {
        totalRequest++
    }

    void incrementLuckyReq() {
        totalLuckyDraw++
    }

    void addWallet(String wallet) {
        if (mostRecentLuckyWallets.size() > 20) {
            mostRecentLuckyWallets.remove(mostRecentLuckyWallets.first())
        }
        mostRecentLuckyWallets.add(wallet)
    }
}
