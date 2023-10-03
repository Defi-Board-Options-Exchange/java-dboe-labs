package com.ngontro86.common.serials;

import java.util.HashMap;
import java.util.Map;

public class ObjMap extends HashMap<String, Object> {
    private static final long serialVersionUID = -4009495359136904661L;
    public String name;

    public ObjMap(String name) {
        super();
        this.name = name;
    }

    public ObjMap(String name, Map<String, Object> data) {
        super();
        this.name = name;
        this.putAll(data);
    }
}
