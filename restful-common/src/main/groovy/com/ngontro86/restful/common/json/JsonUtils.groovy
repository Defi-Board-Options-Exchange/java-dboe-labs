package com.ngontro86.restful.common.json

import com.fasterxml.jackson.core.JsonProcessingException
import com.fasterxml.jackson.databind.DeserializationFeature
import com.fasterxml.jackson.databind.ObjectMapper

import java.text.SimpleDateFormat

class JsonUtils {

    private static class MapperHolder {
        private static ObjectMapper mapper = new ObjectMapper()
        static {
            mapper.setTimeZone(TimeZone.getTimeZone("Asia/Singapore"))
                    .setDateFormat(new SimpleDateFormat("yyyyMMdd HH:mm:ss"))
                    .configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false)
                    .configure(DeserializationFeature.ACCEPT_SINGLE_VALUE_AS_ARRAY, true)
        }
    }

    static ObjectMapper getObjectMapper() {
        return MapperHolder.mapper
    }

    static String toJson(Object obj) {
        try {
            return getObjectMapper().writeValueAsString(obj)
        } catch (JsonProcessingException e) {
            e.printStackTrace()
            return ""
        }
    }

    static <T> T fromJson(String json, Class<T> toClass) {
        try {
            return getObjectMapper().readValue(json, toClass)
        } catch (IOException e) {
            e.printStackTrace()
            return null
        }
    }

}
