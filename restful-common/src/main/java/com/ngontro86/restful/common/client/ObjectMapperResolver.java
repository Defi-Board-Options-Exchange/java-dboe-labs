package com.ngontro86.restful.common.client;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.ngontro86.restful.common.json.JsonUtils;

import javax.ws.rs.ext.ContextResolver;

public class ObjectMapperResolver implements ContextResolver<ObjectMapper> {
    @Override
    public ObjectMapper getContext(Class<?> type) {
        return JsonUtils.getObjectMapper();
    }
}
