package com.ngontro86.cep.esper.aggregation;

import com.espertech.esper.client.hook.AggregationFunctionFactory;
import com.espertech.esper.epl.agg.aggregator.AggregationMethod;
import com.espertech.esper.epl.agg.service.AggregationValidationContext;


import java.math.BigDecimal;

public class PrevDoubleAggregationFunction implements AggregationFunctionFactory {

    @SuppressWarnings("rawtypes")
    @Override
    public Class getValueType() {
        return Double.class;
    }

    @Override
    public AggregationMethod newAggregator() {
        return new PrevDouble();
    }

    @Override
    public void setFunctionName(String arg0) {
    }

    @Override
    public void validate(AggregationValidationContext arg0) {
        if (arg0.getParameterTypes().length != 1 || !(
                Double.class.isAssignableFrom(arg0.getParameterTypes()[0]) ||
                        BigDecimal.class.isAssignableFrom(arg0.getParameterTypes()[0]))) {
            throw new IllegalArgumentException("PrevDouble expect one Double value");
        }
    }

}
