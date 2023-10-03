package com.ngontro86.market.common.adf


class ADFUtils {

    static double[] diff(double[] x) {
        def diff = []
        x.eachWithIndex { currX, idx ->
            if (idx > 0) {
                diff << (currX - x[idx - 1])
            }
        }
        return diff as double[]
    }

    static double[] ones(int n) {
        def ones = []
        n.times {
            ones << 1
        }
        return ones
    }

    static double[][] laggedMatrix(double[] x, int lag) {
        double[][] laggedMatrix = new double[x.length - lag + 1][lag]

        for (int j = 0; j < lag; j++) {
            for (int i = 0; i < laggedMatrix.length; i++) {
                laggedMatrix[i][j] = x[lag - j - 1 + i]
            }
        }
        return laggedMatrix

    }

    static double[] subsetArray(double[] x, int start, int end) {
        double[] subset = new double[end - start + 1]
        System.arraycopy(x, start, subset, 0, end - start + 1)
        return subset
    }

    static double[] sequence(int start, int end) {
        double[] sequence = new double[end - start + 1]
        for (int i = start; i <= end; i++) {
            sequence[i - start] = i
        }
        return sequence
    }


}
