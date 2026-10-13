package com.R4B.client.core;

import java.net.URI;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.concurrent.CompletableFuture;

public final class R4BStreamRequestBuilder {
    private final EndpointDefinition endpoint;
    private final String path;
    private final Map<String, String> query = new LinkedHashMap<>();

    public R4BStreamRequestBuilder(EndpointDefinition endpoint, String path) {
        this.endpoint = endpoint;
        this.path = path;
    }

    public R4BStreamRequestBuilder withPatch(String patch) {
        if (!endpoint.queryParameters().contains("patch")) throw new IllegalStateException("patch is not supported by " + endpoint.id());
        String value = ParameterParser.query(patch);
        if (value != null && !value.isBlank()) query.put("patch", value);
        return this;
    }

    public java.io.InputStream sync() {
        return R4BRuntime.current().executeStreamSync(this);
    }

    public CompletableFuture<java.io.InputStream> async() {
        return R4BRuntime.current().executeStreamAsync(this);
    }

    public EndpointDefinition endpoint() { return endpoint; }

    public URI uri(URI baseUri) {
        StringBuilder value = new StringBuilder(baseUri.toString()).append(path);
        boolean first = true;
        for (Map.Entry<String, String> parameter : query.entrySet()) {
            value.append(first ? '?' : '&').append(ParameterParser.encode(parameter.getKey()))
                .append('=').append(ParameterParser.encode(parameter.getValue()));
            first = false;
        }
        return URI.create(value.toString());
    }
}
