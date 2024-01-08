package com.ngontro86.server.dboe.volsurface

import com.ngontro86.cep.esper.utils.OptionUtils
import com.ngontro86.market.common.regression.MultiLinearReg
import com.ngontro86.market.pricing.Black76

import static com.ngontro86.market.pricing.OptionKind.Call
import static com.ngontro86.market.pricing.OptionKind.Put

class VolSurfaceHelper {

    static void implyVols(Collection<Map> marketOps) {
        marketOps.each { map ->
            map['bid_iv'] = map['bid'] == null ? null : 100d * OptionUtils.iv(map['bid'], map['kind'], map['spot'], map['strike'], map['cond_strike'], map['time_to_expiry'])
            map['ask_iv'] = map['ask'] == null ? null : 100d * OptionUtils.iv(map['ask'], map['kind'], map['spot'], map['strike'], map['cond_strike'], map['time_to_expiry'])
            map['ref_iv'] = map['ref'] == null ? null : 100d * OptionUtils.iv(map['ref'], map['kind'], map['spot'], map['strike'], map['cond_strike'], map['time_to_expiry'])
        }
    }

    static void smoothenVols(Collection<Map> vols) {
        vols.groupBy { it['expiry'] }.each { expiry, options ->
            smoothenOneCurve(options)
        }
    }

    private static List smoothenOneCurve(options) {
        try {
            ['bid_iv', 'ask_iv', 'ref_iv'].each { volType ->
                def linearReg = new MultiLinearReg("", 2)
                options.each { row ->
                    if (row[volType] != null) {
                        linearReg.addData([row['moneyness'], row['moneyness'] * row['moneyness']] as double[], row[volType])
                    }
                }
                if (linearReg.getObCount() > 3) {
                    linearReg.runRegression()
                    def params = linearReg.estimateRegressionParameters()
                    options.each { row ->
                        row[volType] = [[1.0, row['moneyness'], row['moneyness'] * row['moneyness']], params].transpose().collect {
                            it[0] * it[1]
                        }.sum()
                    }
                }
            }
        } catch (Exception e) {
            e.printStackTrace()
        }
    }

    static void updateGreek(Collection<Map> options) {
        options.findAll { it['ref_iv'] > 0d }.each { option ->
            option['greek'] = Black76.greekDboe(option: [kind: option['kind'] == 'Call' ? Call : Put, atm: option['spot'], strike: option['strike'], condStrike: option['cond_strike'], r: 0.0, t: option['time_to_expiry'], vol: option['ref_iv'] / 100d])
        }
    }
}
