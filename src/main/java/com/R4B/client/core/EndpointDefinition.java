package com.R4B.client.core;

import java.util.Set;

public record EndpointDefinition(
    String id,
    HttpMethod method,
    Set<String> queryParameters,
    AccessScope accessScope,
    String rateLimitBucket,
    boolean allowsNoContent
) {
    public EndpointDefinition {
        if (id == null || id.isBlank()) throw new IllegalArgumentException("Endpoint id is required");
        if (method == null) throw new IllegalArgumentException("HTTP method is required");
        queryParameters = Set.copyOf(queryParameters);
        if (accessScope == null) throw new IllegalArgumentException("Access scope is required");
    }

    public enum HttpMethod {
        GET,
        POST
    }
}
