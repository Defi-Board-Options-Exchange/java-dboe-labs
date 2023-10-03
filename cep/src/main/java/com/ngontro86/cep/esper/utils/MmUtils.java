package com.ngontro86.cep.esper.utils;

public class MmUtils {

    protected static double contSpread(double quotingSize, int level, Double[] contSpreads, Double[] contSizes) {
        int minLvl = Math.min(Math.min(level, contSizes.length), contSpreads.length);
        for (int idx = 0; idx < minLvl; idx++) {
            if (contSizes[idx] > 0) {
                return contSpreads[idx] * contSizes[idx] / quotingSize;
            }
        }
        return 0d;
    }

    /**
     * @param bidAsk
     * @param alpha
     * @param halfSpread
     * @param jumpBps
     * @return spread in absolute value
     */
    protected static double jumpSpread(int bidAsk, double alpha, double halfSpread, double jumpBps, double theo) {
        if (bidAsk == 1) {
            if (jumpBps >= 0) {
                return 0d;
            }
            return Math.max(theo / 10000.0 * alpha * Math.abs(jumpBps) - halfSpread, 0d);
        }
        if (bidAsk == 2) {
            if (jumpBps <= 0) {
                return 0d;
            }
            return Math.max(theo / 10000.0 * alpha * jumpBps - halfSpread, 0d);
        }
        return 0d;
    }

    public static double spread(
            int bidAsk,
            double quotingSize,
            double initialSpread,
            double incrementalSpread,
            double currentPosition,
            double jumpSenstivity,
            double jumpBps,
            double theo,
            Double[] contIncrSpreads,
            Double[] totalContFillSizes) {
        double contSpreadForRepetitiveQuote = contSpread(quotingSize, 3, contIncrSpreads, totalContFillSizes);
        double contSpreadForIncrementalQuote = contSpread(quotingSize, contIncrSpreads.length, contIncrSpreads, totalContFillSizes);
        double jumpSpread = jumpSpread(bidAsk, jumpSenstivity, initialSpread, jumpBps, theo);
        if (bidAsk == 1) {
            if (currentPosition <= 0) {
                return initialSpread + contSpreadForRepetitiveQuote;
            }
            return initialSpread + incrementalSpread * currentPosition / quotingSize + contSpreadForIncrementalQuote + jumpSpread;
        }
        if (bidAsk == 2) {
            if (currentPosition >= 0) {
                return initialSpread + contSpreadForRepetitiveQuote;
            }
            return initialSpread + incrementalSpread * Math.abs(currentPosition) / quotingSize + contSpreadForIncrementalQuote + jumpSpread;
        }
        return initialSpread;
    }


    public static double spread2(
            int bidAsk,
            double quotingSize,
            double currentPosition,
            double incrementalSpread,
            double minSpread,
            double maxSpread,
            double reducingSpreadStep,
            int reducingTimeStepMs,
            long timeDiffMs) {
        final double timeAdjSpread = Math.max(minSpread, maxSpread - reducingSpreadStep * Math.abs(timeDiffMs) / reducingTimeStepMs);
        if (bidAsk == 1) {
            if (currentPosition <= 0) {
                return timeAdjSpread;
            }
            return timeAdjSpread + incrementalSpread * currentPosition / quotingSize;
        }
        if (bidAsk == 2) {
            if (currentPosition >= 0) {
                return timeAdjSpread;
            }
            return timeAdjSpread + incrementalSpread * Math.abs(currentPosition) / quotingSize;
        }
        return timeAdjSpread;
    }

    public static String mmQuoteOrderReqId(String name, String portfolio, String instId, int bidAsk, int runningNum) {
        return String.format("E-%d-%s-%s-%s-%d", bidAsk, name, portfolio, instId, runningNum);
    }

    public static String mmExitOrderReqId(String name, String portfolio, String instId, int runningNum) {
        return String.format("X-%s-%s-%s-%d", name, portfolio, instId, runningNum);
    }

}
