package com.ngontro86.cep.esper.aggregation;

import com.espertech.esper.epl.agg.aggregator.AggregationMethod;

import java.math.BigDecimal;

public class PrevDouble implements AggregationMethod {

    private double prev, curr;

    @Override
    public void clear() {
        prev = Double.NaN;
        curr = Double.NaN;
    }

    @Override
    public void enter(Object arg0) {
        if (!(arg0 instanceof Double || arg0 instanceof BigDecimal)) {
            System.out.println(String.format("Obj: %s is not a double or big decimal instance", arg0));
            return;
        }
        prev = curr;
        curr = arg0 instanceof Double ? (Double) arg0 : ((BigDecimal) arg0).doubleValue();
    }

    @Override
    public Object getValue() {
        return prev;
    }

    @SuppressWarnings("rawtypes")
    @Override
    public Class getValueType() {
        return Double.class;
    }

    @Override
    public void leave(Object arg0) {
    }

}
