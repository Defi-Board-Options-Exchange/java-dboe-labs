package com.ngontro86.server.dboe.volsurface

import com.ngontro86.market.common.regression.MultiLinearReg

class VolSurfaceHelper {

    static void smoothenVols(Collection<Map> vols) {
        vols.groupBy { it['expiry'] }.each { expiry, data ->
            def linearReg = new MultiLinearReg("", 2)
            data.each { row ->
                linearReg.addData([row['moneyness'], row['moneyness'] * row['moneyness']] as double[], row['vol'])
            }
            linearReg.runRegression()
            def params = linearReg.estimateRegressionParameters()
            data.each { row ->
                row['vol'] = [[1.0, row['moneyness'], row['moneyness'] * row['moneyness']], params].transpose().collect { it[0] * it[1] }.sum()
            }
        }
    }

}
