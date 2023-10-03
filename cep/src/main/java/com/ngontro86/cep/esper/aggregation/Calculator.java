package com.ngontro86.cep.esper.aggregation;

import com.ngontro86.common.biz.entity.BollingerReq;
import com.ngontro86.common.biz.entity.TrendReq;
import com.ngontro86.common.util.InstIdUtils;
import com.ngontro86.market.common.MidPriceUtils;
import com.ngontro86.utils.AssetType;
import com.ngontro86.utils.Utils;
import org.apache.commons.math3.stat.correlation.PearsonsCorrelation;
import org.apache.commons.math3.stat.descriptive.DescriptiveStatistics;
import org.apache.commons.math3.stat.regression.SimpleRegression;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

import static java.lang.Double.NaN;
import static java.lang.Math.abs;

public class Calculator {

    public static Logger logger = LogManager.getLogger(Calculator.class);
    private static ConcurrentHashMap<String, ConcurrentHashMap<String, Double>> thresholdMem = new ConcurrentHashMap<String, ConcurrentHashMap<String, Double>>();

    public static int getFXType() {
        return AssetType.FX.ordinal();
    }

    public static int getFutureType() {
        return AssetType.FUTURE.ordinal();
    }

    public static int getSecurityType() {
        return AssetType.SEC.ordinal();
    }

    public static int getDynType() {
        return AssetType.DYN.ordinal();
    }

    public static String getInstID(String underlying, String currency) {
        return Utils.getInstID(underlying, currency);
    }

    public static String getInstID(String underlying, String market, Integer expiry_date, Integer multiplier, String currency) {
        return InstIdUtils.getBbgTicker(underlying, expiry_date);
    }

    public static int gcd(int m1, int m2) {
        if (m2 == 0) {
            return m1;
        }
        return gcd(m2, m1 % m2);
    }

    public static Double imbalance(Integer bidSize, Integer askSize) {
        if (bidSize == null || askSize == null) {
            return NaN;
        }
        return 50.0 * (bidSize - askSize) / (bidSize + askSize);
    }

    public static Double microPrice(Double bid, Double lastPrice, Double ask, Double bidSize, Double lastSize, Double askSize) {
        return MidPriceUtils.microPrice(bid, lastPrice, ask, bidSize, lastSize, askSize);
    }

    public static String getBollingerORI(String reqId, Boolean isBuy, Boolean isEntry) {
        if (reqId == null || isBuy == null || isEntry == null) {
            return null;
        }
        return BollingerReq.getORI(reqId, isBuy, isEntry);
    }

    public static String getTrendORI(String reqId, Boolean isBuy, Boolean isEntry) {
        if (reqId == null || isBuy == null || isEntry == null) {
            return null;
        }
        return TrendReq.getORI(reqId, isBuy, isEntry);
    }

    public static Double getBollingerQuotingRef(Boolean isBuy, Double quotingRef, Double mpAvg, Double stdev) {
        if (isBuy == null || quotingRef == null || mpAvg == null || stdev == null) {
            return Double.NaN;
        }
        return isBuy ? (mpAvg - quotingRef * stdev) : (mpAvg + quotingRef * stdev);
    }

    public static boolean isBollingerSignal(Double minD, Double maxD, Double minImbalance, Double mpAvg, Double imbalance, Double stdev,
                                            Double bid, Double ask, Double buyRef, Double sellRef) {
        if (!Utils.isBollingerSignal(minD, maxD, minImbalance, mpAvg, imbalance, stdev)) {
            return false;
        }
        return (ask <= buyRef && (minImbalance < 0 || imbalance > 0)) || (bid >= sellRef && (minImbalance < 0 || imbalance < 0));
    }

    public static int trendSignal(Double minImbSt, Double minImbMt, Double minImbLt, Double minImbVlt, Double imbSt, Double imbMt, Double imbLt, Double imbVlt) {
        return Utils.trendSignal(minImbSt, minImbMt, minImbLt, minImbVlt, imbSt, imbMt, imbLt, imbVlt);
    }

    public static boolean threshold(String element, String funcGroup, Double val, Double threshold) {
        if (!thresholdMem.containsKey(funcGroup)) {
            thresholdMem.put(funcGroup, new ConcurrentHashMap<>());
        }
        if (!thresholdMem.get(funcGroup).containsKey(element)) {
            thresholdMem.get(funcGroup).put(element, val);
            return true;
        }
        Double prevVal = thresholdMem.get(funcGroup).get(element);
        if (prevVal == 0.0) {
            return true;
        }
        if (abs((val - prevVal) / prevVal) > threshold) {
            thresholdMem.get(funcGroup).put(element, val);
            return true;
        }
        return false;
    }

    public static Double[] stats(Double[] rets) {
        SimpleRegression regression = new SimpleRegression();
        for (int idx = 0; idx < rets.length; idx++) {
            regression.addData(idx + 1, rets[idx]);
        }
        return new Double[]{stdev(rets), regression.getSlope(), regression.getRSquare()};
    }

    public static double stdev(Double[] rets) {
        DescriptiveStatistics descriptiveStatistics = new DescriptiveStatistics();
        for (int idx = 0; idx < rets.length; idx++) {
            descriptiveStatistics.addValue(rets[idx]);
        }
        return descriptiveStatistics.getStandardDeviation();
    }

    public static double corr(Map[] rets) {
        double[] firstRets = new double[rets.length];
        double[] secondRets = new double[rets.length];
        for (int idx = 0; idx < rets.length; idx++) {
            firstRets[idx] = (Double) rets[idx].get("firstRet");
            secondRets[idx] = (Double) rets[idx].get("secondRet");
        }
        final PearsonsCorrelation pearsonsCorrelation = new PearsonsCorrelation();
        return pearsonsCorrelation.correlation(firstRets, secondRets);
    }
}
