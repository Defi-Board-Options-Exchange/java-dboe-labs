package com.ngontro86.cep.esper.aggregation;


import com.espertech.esper.epl.agg.aggregator.AggregationMethod;
import com.ngontro86.cep.CepTimeController;

import static java.lang.Double.isNaN;
import static java.lang.Math.sqrt;

public class MAvg implements AggregationMethod {

    final static int AVG = 0, STDEV = 1;
    final static int MIN_PTS = 7;

    final double LN2 = Math.log(2);

    Double[] vals = new Double[]{Double.NaN, Double.NaN};
    Double[] tmpVals = new Double[]{Double.NaN, Double.NaN};

    long lastTimestamp;

    double halfLife;

    int countEvt;

    @Override
    public void clear() {
        vals[AVG] = Double.NaN;
        vals[STDEV] = Double.NaN;

        tmpVals[AVG] = Double.NaN;
        tmpVals[STDEV] = Double.NaN;

        lastTimestamp = 0;
        halfLife = 0d;
        countEvt = 0;
    }

    @Override
    public void enter(Object arg0) {
        long timeNow = CepTimeController.getCurrentTimeMillis();

        Object[] ins = (Object[]) arg0;
        double x = (Double) ins[0],
                hl = (Double) ins[1];

        if (halfLife == 0d) {
            halfLife = hl;
        }

        if (isNaN(x) || isNaN(hl)) {
            return;
        }

        // First time called this func
        if (lastTimestamp == 0) {
            vals[AVG] = x;
            vals[STDEV] = 0.0;
            tmpVals[AVG] = x;
            tmpVals[STDEV] = 0.0;

        } else {
            final double prevPointWeight = Math.exp(LN2 * (lastTimestamp - timeNow) / halfLife);
            final double newPointMavgWeight = 1.0 - prevPointWeight;

            final double newAvg = prevPointWeight * tmpVals[AVG] + newPointMavgWeight * x,
                    newStdev = sqrt(prevPointWeight * (times2(tmpVals[STDEV]) + newPointMavgWeight * times2(x - tmpVals[AVG])));

            if (countEvt >= MIN_PTS) {
                vals[AVG] = newAvg;
                vals[STDEV] = newStdev;
            }
            tmpVals[AVG] = newAvg;
            tmpVals[STDEV] = newStdev;
        }
        lastTimestamp = timeNow;
        countEvt++;
    }

    private final double times2(double val) {
        return val * val;
    }

    @Override
    public Object getValue() {
        return vals;
    }

    @SuppressWarnings("rawtypes")
    @Override
    public Class getValueType() {
        return Double[].class;
    }

    @Override
    public void leave(Object arg0) {
    }
}
