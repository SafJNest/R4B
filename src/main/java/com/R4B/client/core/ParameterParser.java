package com.R4B.client.core;

import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import java.util.Locale;

public final class ParameterParser {
    private ParameterParser() {}

    public static String path(String value, String parameter) {
        if (value == null || value.isBlank()) throw new IllegalArgumentException(parameter + " is required");
        return encode(value.trim());
    }

    public static String query(Object value) {
        if (value == null) return null;
        if (value instanceof Enum<?> enumValue) return enumValue.name();
        if (value instanceof String text) return text.trim();
        return String.valueOf(value);
    }

    public static String encode(String value) {
        return URLEncoder.encode(value, StandardCharsets.UTF_8).replace("+", "%20");
    }

    public static String normalized(String value) {
        return value == null ? null : value.trim().toLowerCase(Locale.ROOT);
    }
}
