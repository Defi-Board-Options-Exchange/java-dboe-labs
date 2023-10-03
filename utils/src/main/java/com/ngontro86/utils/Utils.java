package com.ngontro86.utils;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.math.BigDecimal;
import java.math.RoundingMode;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Map;

import static java.lang.Math.abs;
import static java.lang.Math.exp;

public class Utils {
    public static final int EntryTimeBuffer = 30 * 60 * 1000; // 30 minutes prior any economical events
    private final static int BUY = 1, SELL = 2, NO = 0;
    public static double BPS = 10000;
    public static double BPS_INV = 0.0001;
    private static Logger logger = LogManager.getLogger(Utils.class);
    private static String EURO_RELATED_ENTITIES = "United States,European,Spain,France,Germany,Italy,Portugal,Greece";
    private static SimpleDateFormat yyyymmddhhmmss = new SimpleDateFormat("yyyyMMddHHmmssSSS");

    public static String getEuroFilterEntityStatement() {
        final StringBuilder sb = new StringBuilder();
        for (String eventPattern : Utils.EURO_RELATED_ENTITIES.split(",")) {
            sb.append(String.format("event_entity like '%%%s%%' or ", eventPattern));
        }
        sb.append("false");
        return sb.toString();
    }

    public static boolean isBollingerSignal(Double minD, Double maxD, Double minImbalance, Double mpAvg, Double imbalance, Double stdev) {
        if (minD == null || maxD == null || minImbalance == null || mpAvg == null || imbalance == null || stdev == null) {
            return false;
        }
        double stdevBps = stdev / mpAvg * BPS;
        boolean ret = Math.abs(imbalance) >= minImbalance;
        ret &= stdevBps >= minD;
        ret &= stdevBps <= maxD;
        return ret;
    }

    public static int trendSignal(Double minImbSt, Double minImbMt, Double minImbLt, Double minImbVlt, Double imbSt, Double imbMt, Double imbLt, Double imbVlt) {
        if (imbSt == null || imbMt == null || imbLt == null || imbVlt == null) {
            return NO;
        }
        boolean isBuy = isBuy(minImbSt, imbSt) && isBuy(minImbMt, imbMt) && isBuy(minImbLt, imbLt) && isBuy(minImbVlt, imbVlt);
        boolean isSell = isSell(minImbSt, imbSt) && isSell(minImbMt, imbMt) && isSell(minImbLt, imbLt) && isSell(minImbVlt, imbVlt);
        if (isBuy && isSell) {
            return NO;
        }
        return isBuy ? BUY : (isSell ? SELL : NO);
    }

    private static boolean isBuy(Double minImb, Double imb) {
        if (minImb < 0) {
            return true;
        }
        return imb >= minImb;
    }

    private static boolean isSell(Double minImb, Double imb) {
        if (minImb < 0) {
            return true;
        }
        return imb <= 0 && abs(imb) > minImb;
    }

    public static double getPrice(boolean isBuy, double wantedPrice, double tickSize) {
        try {
            BigDecimal wP = BigDecimal.valueOf(wantedPrice), ts = BigDecimal.valueOf(tickSize);
            return isBuy ? Math.floor(wP.divide(ts, RoundingMode.HALF_UP).doubleValue()) * tickSize : Math.ceil(wP.divide(ts, RoundingMode.HALF_UP).doubleValue()) * tickSize;
        } catch (Exception e) {
            return isBuy ? Math.floor(wantedPrice / tickSize) * tickSize : Math.ceil(wantedPrice / tickSize) * tickSize;
        }
    }

    /*
     * @Params:
     * maxLoss is in bps
     */
    public static double getHSLExitPrice(boolean isBuy, double entry, double maxLoss) {
        double y = -maxLoss;
        return entry * (1.0 + y * (isBuy ? 1.0 : -1.0) * BPS_INV);
    }

    public static double getSlidingHSLExitPrice(boolean isBuy, double entry, double mktPrice, double maxLoss, double alpha) {
        double y = -maxLoss;
        double pl = abs(mktPrice - entry);
        double plBps = pl / entry * BPS;
        int buySellMultiplier = isBuy ? 1 : -1;

        boolean positivePL = (isBuy && mktPrice > entry) || (!isBuy && mktPrice < entry);
        double slidingPart = (positivePL ? pl : 0.0) * buySellMultiplier;
        double hslPart = y * BPS_INV * exp(-alpha * (positivePL ? plBps : 0.0) / 100.0) * entry * buySellMultiplier;

        return entry + hslPart + slidingPart; // y = x + hsl * e^ (-alpha * x /100) when x is positive else y = hsl
    }

    public static double getSlidingHSLExitPrice(boolean isBuy, double entry, double mktPrice, double maxLoss) {
        return getSlidingHSLExitPrice(isBuy, entry, mktPrice, maxLoss, 0.75);
    }

    public static String getBaseUnderlying(String instId) {
        String[] idArr = instId.split("/");
        return "USD".equals(idArr[0]) ? idArr[1] : idArr[0];
    }

    public static double normalized(String instId, double val, int idx, final Map<String, double[][]> normalizedStats) {
        if (!normalizedStats.containsKey(instId)) {
            return Double.NaN;
        }
        double[][] stats = normalizedStats.get(instId);
        double min = stats[0][idx], range = stats[1][idx];
        return (val - min) / range;
    }

    public static double deNormalized(String instId, double val, int idx, final Map<String, double[][]> normalizedStats) {
        if (!normalizedStats.containsKey(instId)) {
            return Double.NaN;
        }
        double[][] stats = normalizedStats.get(instId);
        double min = stats[0][idx], range = stats[1][idx];
        return range * val + min;
    }

    public static void pause(long millis) {
        try {
            Thread.sleep(millis);
        } catch (Exception e) {
        }
    }

    public static Thread startThread(Runnable obj, int priority) {
        Thread thread = new Thread(obj);
        thread.setPriority(priority);
        thread.start();
        return thread;
    }

    public static double toDouble(Object obj, double defaultVal) {
        try {
            if (obj instanceof Double) {
                return (Double) obj;
            }
            if (obj == null) {
                return defaultVal;
            }
            return Double.parseDouble(obj.toString());
        } catch (Exception e) {
            return defaultVal;
        }
    }

    public static int toInt(Object obj, int defaultVal) {
        try {
            if (obj instanceof Integer) {
                return (Integer) obj;
            }
            if (obj == null) {
                return defaultVal;
            }
            return Integer.parseInt(obj.toString());
        } catch (Exception e) {
            return defaultVal;
        }
    }

    public static long toLong(Object obj, long defaultVal) {
        try {
            if (obj instanceof Long) {
                return (Long) obj;
            }
            if (obj == null) {
                return defaultVal;
            }
            return Long.parseLong(obj.toString());
        } catch (Exception e) {
            return defaultVal;
        }
    }

    public static String toString(Object obj, String defaultVal) {
        try {
            if (obj instanceof String) {
                return (String) obj;
            }
            if (obj == null) {
                return defaultVal;
            }
            return obj.toString();
        } catch (Exception e) {
            return defaultVal;
        }
    }

    public static String readInputStream(InputStream is) {
        try {
            String line;
            StringBuilder sb = new StringBuilder();
            BufferedReader br = new BufferedReader(new InputStreamReader(is));
            while ((line = br.readLine()) != null) {
                sb.append(line).append("\r\n");
            }
            return sb.toString();
        } catch (Exception e) {
            logger.error("Failed to read input stream!");
            return null;
        }
    }

    public static String loadResourceAsString(String path) {
        return readInputStream(Utils.class.getClassLoader().getResourceAsStream(path));
    }

    public static String getInstID(String underlying, String currency) {
        return String.format("%s/%s", underlying, currency);
    }

    public static String getInstID(String underlying, String market, Integer expiry_date, Integer multiplier, String currency) {
        if (expiry_date == null || multiplier == null || currency == null) {
            return null;
        }
        return String.format("F%s%s%d%s%d", underlying, market, multiplier, currency, expiry_date / 100);
    }

    public static long getHumanTimestamp(long timestampUtc) {
        try {
            return Utils.toLong(yyyymmddhhmmss.format(new Date(timestampUtc)), 0);
        } catch (Exception e) {
            return 0;
        }
    }

    public static double getFxRate(String instId, double rate) {
        return instId.contains("USD/") ? 1.0 / rate : 1.0;
    }

    public static String getNeuralIds(String instId, String horizon) {
        String baseUnderlying = getBaseUnderlying(instId);
        return String.format("%s_%s", baseUnderlying, horizon);
    }

}
