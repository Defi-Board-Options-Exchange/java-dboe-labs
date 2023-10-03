package com.ngontro86.cep.esper;

import com.espertech.esper.client.EventBean;
import com.espertech.esper.event.bean.BeanEventBean;

import java.util.HashMap;
import java.util.Map;

public class EsperUtils {

    @SuppressWarnings("unchecked")
    public static  Map<String, Object> parseEventBeanUnderlyingAsMap(EventBean eb) {
        if (eb instanceof BeanEventBean) {
            Map<String, Object> map = new HashMap<String, Object>();
            for (String k : eb.getEventType().getPropertyNames()) {
                map.put(k, eb.get(k));
            }
            return map;
        }
        return (Map<String, Object>) eb.getUnderlying();
    }

}
