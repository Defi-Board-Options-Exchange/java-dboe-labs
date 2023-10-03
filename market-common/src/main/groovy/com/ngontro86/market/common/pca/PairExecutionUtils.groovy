package com.ngontro86.market.common.pca

import com.ngontro86.market.common.NormalizeMethod


public class PairExecutionUtils {

    public static double[] executeInPairConcept(NormalizeMethod method,
                                                double[] pcWeights,
                                                double[] avgs,
                                                double[] stdevs,
                                                int[] minSizes,
                                                double[] multipliers) {
        if (method == NormalizeMethod.LOG_RET) {
            return getExecuteBasingLnRet(pcWeights, avgs, minSizes, multipliers)
        }
        if (method == NormalizeMethod.Z_SCORE) {
            return getExecuteBasingZScore(pcWeights, avgs, stdevs, minSizes, multipliers)
        }
        throw new IllegalAccessException("Have not implemented normalize method: ${method}")
    }

    static double[] getExecuteBasingZScore(double[] pcWeights,
                                           double[] avgs,
                                           double[] stdevs,
                                           int[] minSizes,
                                           double[] multipliers) {
        final double w1 = pcWeights[0] / stdevs[0], w2 = pcWeights[1] / stdevs[1]
        double scale = minSizes[0] * multipliers[0] / w1
        double size2 = scale * w2 / multipliers[1]
        double basis = w1 * avgs[0] + w2 * avgs[1]
        return [minSizes[0], size2, basis * scale]
    }

    static double[] getExecuteBasingLnRet(double[] pcWeights,
                                          double[] avgs,
                                          int[] minSizes,
                                          double[] multipliers) {
        double w1 = pcWeights[0] / avgs[0], w2 = pcWeights[1] / avgs[1]
        double scale = minSizes[0] * multipliers[0] / w1
        double size2 = scale * w2 / multipliers[1]
        double basis = pcWeights.sum() * scale
        return [minSizes[0], size2, basis]
    }

    static long gcd(long a, long b) {
        while (b > 0) {
            long temp = b
            b = a % b
            a = temp
        }
        return a
    }

    static long lcm(long a, long b) {
        return a * (b / gcd(a, b))
    }

}
