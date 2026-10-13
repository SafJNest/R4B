package com.R4B.client.core;

import java.net.URI;
import java.time.Duration;
import java.util.List;

public record R4BConfig(URI baseUri, Duration connectTimeout, Duration requestTimeout, List<RequestPolicy> policies) {
    public static final Duration DEFAULT_CONNECT_TIMEOUT = Duration.ofSeconds(10);
    public static final Duration DEFAULT_REQUEST_TIMEOUT = Duration.ofSeconds(30);

    public R4BConfig {
        if (baseUri == null || !("http".equalsIgnoreCase(baseUri.getScheme()) || "https".equalsIgnoreCase(baseUri.getScheme()))
                || baseUri.getHost() == null || baseUri.getRawQuery() != null || baseUri.getRawFragment() != null) {
            throw new IllegalArgumentException("Base URL must be an absolute HTTP(S) URI without query or fragment");
        }
        if (connectTimeout == null || connectTimeout.isZero() || connectTimeout.isNegative()) {
            throw new IllegalArgumentException("Connect timeout must be positive");
        }
        if (requestTimeout == null || requestTimeout.isZero() || requestTimeout.isNegative()) {
            throw new IllegalArgumentException("Request timeout must be positive");
        }
        policies = policies == null ? List.of() : List.copyOf(policies);
        String path = baseUri.getPath();
        if (path != null && !path.isEmpty() && !"/".equals(path)) {
            throw new IllegalArgumentException("Base URL must not include a path");
        }
        baseUri = URI.create(baseUri.getScheme() + "://" + baseUri.getRawAuthority());
    }

    public static R4BConfig of(String baseUrl) {
        if (baseUrl == null || baseUrl.isBlank()) throw new IllegalArgumentException("Base URL is required");
        return of(URI.create(baseUrl.trim()));
    }

    public static R4BConfig of(URI baseUri) {
        return new R4BConfig(baseUri, DEFAULT_CONNECT_TIMEOUT, DEFAULT_REQUEST_TIMEOUT, List.of());
    }

    public R4BConfig withTimeouts(Duration connect, Duration request) {
        return new R4BConfig(baseUri, connect, request, policies);
    }

    public R4BConfig withPolicies(List<RequestPolicy> values) {
        return new R4BConfig(baseUri, connectTimeout, requestTimeout, values);
    }
}
