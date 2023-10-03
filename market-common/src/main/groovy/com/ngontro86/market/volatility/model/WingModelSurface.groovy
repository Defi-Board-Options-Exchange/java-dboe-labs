package com.ngontro86.market.volatility.model

class WingModelSurface {
    // ref vol and ref atm fwd
    double v, ref, atm

    // current slope
    double sr

    // put & call curvature
    double pc, cc
    // down & up cutoff
    double dc, uc

    // volatility, slope change & skew swimmingness rate
    double vcr, scr, ssr

    // down and up smoothing range
    double dsm, usm

    // Derived values
    double vc, sc

    WingModelSurface setNewAtm(double newAtm) {
        atm = newAtm
        vc = v - vcr * ssr * (newAtm - ref) / ref
        sc = sr - scr * ssr * (newAtm - ref) / ref
        this
    }

    double getVol(double k) {
        throwExceptionIfMissingAtm()

        double x = Math.log(k / atm)
        if (between(x, 0, uc)) {
            return vc + sc * x + cc * x * x
        }

        if (between(x, uc, 0)) {
            return vc + sc * x + pc * x * x
        }

        if (between(x, dc * (1 + dsm), dc)) {
            return vc - (1 + 1.0 / dsm) * pc * dc * dc - (sc * dc) / 2.0 / dsm + (1 + 1.0 / dsm) * (2 * pc * dc + sc) * x - (pc / dsm + sc / 2 / dc / dsm) * x * x
        }

        if (between(x, -1000.0, dc * (1 + dsm))) {
            return vc + dc * (2 + dsm) * (sc / 2) + (1 + dsm) * pc * dc * dc
        }

        if (between(x, uc * (1 + usm), uc)) {
            return vc - (1 + 1.0 / usm) * cc * uc * uc - (sc * uc) / 2 / usm + (1 + 1.0 / usm) * (2 * cc * uc + sc) * x - (cc / usm + sc / 2 / uc / usm) * x * x
        }

        if (between(x, uc * (1 + usm), 10000)) {
            return vc + uc * (2 + usm) / 2 / dc + (1 + usm) * cc * uc * uc
        }

        return vc
    }


    private void throwExceptionIfMissingAtm() {
        if (atm == 0 || vc * sc == 0) {
            throw new IllegalStateException("Unset either atm ref. or new atm")
        }
    }

    static boolean between(double v, double x1, double x2) {
        return v >= x1 && v <= x2
    }

}
