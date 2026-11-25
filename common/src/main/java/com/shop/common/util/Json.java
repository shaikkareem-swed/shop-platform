package com.shop.common.util;

import tools.jackson.databind.json.JsonMapper;

/** Tiny JSON helper (Jackson 3) used for Kafka payloads. */
public final class Json {
    private static final JsonMapper MAPPER = JsonMapper.builder().build();

    private Json() {}

    public static String write(Object value) {
        return MAPPER.writeValueAsString(value);
    }

    public static <T> T read(String json, Class<T> type) {
        return MAPPER.readValue(json, type);
    }
}
