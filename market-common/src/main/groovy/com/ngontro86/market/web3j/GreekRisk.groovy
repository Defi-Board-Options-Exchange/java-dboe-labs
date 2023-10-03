package com.ngontro86.market.web3j

import com.ngontro86.utils.EqualUtils

class GreekRisk {
    double delta
    double vega
    double gamma
    double theta

    GreekRisk() {}

    GreekRisk(double d, double v, double g, double t) {
        delta = d
        vega = v
        gamma = g
        theta = t
    }

    @Override
    String toString() {
        "Delta:, ${delta}, Vega:, ${vega},Gamma:, ${gamma},Theta:, ${theta}"
    }

    @Override
    boolean equals(Object ob) {
        if (!(ob instanceof GreekRisk)) {
            return false
        }
        GreekRisk gr = (GreekRisk) ob
        return EqualUtils.equals(delta, gr.delta, 0.0001) && EqualUtils.equals(vega, gr.vega, 0.0001) && EqualUtils.equals(gamma, gr.gamma, 0.0001) && EqualUtils.equals(theta, gr.theta, 0.0001)
    }
}
