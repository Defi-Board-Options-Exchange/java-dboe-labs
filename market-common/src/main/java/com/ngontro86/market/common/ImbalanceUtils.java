package com.ngontro86.market.common;

import static java.lang.Math.sqrt;

public class ImbalanceUtils {

    public static double imbalance(int bidSize, int askSize) {
        if (bidSize + askSize == 0) {
            return 0d;
        }
        return 1.0 * (bidSize - askSize) / (bidSize + askSize);
    }

    /**
     * <br>Refer to Order Imbalance Based Strategy in HFC written by Darryl Shen
     *
     * @param bidSize1
     * @param bidSize2
     * @param bid1
     * @param bid2
     * @param askSize1
     * @param askSize2
     * @param ask1
     * @param ask2
     * @return
     */
    public static double imbalance(int bidSize1, int bidSize2,
                                   double bid1, double bid2,
                                   int askSize1, int askSize2,
                                   double ask1, double ask2) {
        int deltaBidSize = getDeltaBidSize(bidSize1, bidSize2, bid1, bid2);
        int deltaAskSize = getDeltaAskSize(askSize1, askSize2, ask1, ask2);
        if (deltaBidSize + deltaAskSize == 0) {
            return 0d;
        }

        return (deltaBidSize - deltaAskSize) / (deltaBidSize + deltaAskSize);
    }

    public static double imbalanceVol(int bidSize1, int bidSize2,
                                      double bid1, double bid2,
                                      int askSize1, int askSize2,
                                      double ask1, double ask2) {
        return getDeltaBidSize(bidSize1, bidSize2, bid1, bid2) - getDeltaAskSize(askSize1, askSize2, ask1, ask2);
    }

    public static double imbalance(int bidSize1, int bidSize2,
                                   double bid1, double bid2,
                                   int askSize1, int askSize2,
                                   double ask1, double ask2,
                                   int deltaTime) {
        return imbalanceTimeDiscount
                (
                        imbalance(bidSize1, bidSize2, bid1, bid2, askSize1, askSize2, ask1, ask2),
                        deltaTime
                );
    }

    public static double imbalanceVol(int bidSize1, int bidSize2,
                                      double bid1, double bid2,
                                      int askSize1, int askSize2,
                                      double ask1, double ask2,
                                      int deltaTime) {
        return imbalanceTimeDiscount
                (
                        imbalanceVol(bidSize1, bidSize2, bid1, bid2, askSize1, askSize2, ask1, ask2),
                        deltaTime
                );
    }

    /**
     * @param imb
     * @param deltaTime in ms
     * @return
     */
    public static double imbalanceTimeDiscount(double imb, int deltaTime) {
        return imb / sqrt(deltaTime / 1000.0);
    }

    static int getDeltaAskSize(int askSize1, int askSize2, double ask1, double ask2) {
        int deltaAskSize = 0;
        if (ask1 == ask2) {
            deltaAskSize = askSize1 - askSize2;
        } else if (ask1 < ask2) {
            deltaAskSize = askSize1;
        }
        return deltaAskSize;
    }

    static int getDeltaBidSize(int bidSize1, int bidSize2, double bid1, double bid2) {
        int deltaBidSize = 0;
        if (bid1 == bid2) {
            deltaBidSize = bidSize1 - bidSize2;
        } else if (bid1 > bid2) {
            deltaBidSize = bidSize1;
        }
        return deltaBidSize;
    }


}
