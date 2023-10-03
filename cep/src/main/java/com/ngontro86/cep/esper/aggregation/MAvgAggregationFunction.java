package com.ngontro86.cep.esper.aggregation;

import com.espertech.esper.client.hook.AggregationFunctionFactory;
import com.espertech.esper.epl.agg.aggregator.AggregationMethod;
import com.espertech.esper.epl.agg.service.AggregationValidationContext;


public class MAvgAggregationFunction implements AggregationFunctionFactory {

    @SuppressWarnings("rawtypes")
    @Override
    public Class getValueType() {
        return Double[].class;
    }

    @Override
    public AggregationMethod newAggregator() {
        return new MAvg();
    }

    @Override
    public void setFunctionName(String arg0) {
    }

    @Override
    public void validate(AggregationValidationContext arg0) {
        if (arg0.getParameterTypes().length != 2
                || !Double.class.isAssignableFrom(arg0.getParameterTypes()[0])
                || !Double.class.isAssignableFrom(arg0.getParameterTypes()[1])) {
            throw new IllegalArgumentException("MAvg expect two Double values");
        }
    }

}
