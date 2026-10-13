package com.R4B.client.core;

import java.net.URI;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.concurrent.CompletableFuture;

import com.fasterxml.jackson.databind.JavaType;
import com.fasterxml.jackson.databind.type.TypeFactory;

public abstract class R4BRequestBuilder<T> {
    private final EndpointDefinition endpoint;
    private final String path;
    private final JavaType responseType;
    private final Map<String, String> query = new LinkedHashMap<>();

    protected R4BRequestBuilder(EndpointDefinition endpoint, String path, JavaType responseType) {
        this.endpoint = endpoint;
        this.path = path;
        this.responseType = responseType;
    }

    protected final void query(String name, Object value) {
        if (!endpoint.queryParameters().contains(name)) {
            throw new IllegalStateException("Parameter " + name + " is not supported by endpoint " + endpoint.id());
        }
        String parsed = ParameterParser.query(value);
        if (parsed == null || parsed.isEmpty()) query.remove(name);
        else query.put(name, parsed);
    }

    protected final void requiredQuery(String name, Object value) {
        String parsed = ParameterParser.query(value);
        if (parsed == null || parsed.isBlank()) throw new IllegalArgumentException(name + " is required");
        query(name, parsed);
    }

    public final T sync() {
        return R4BRuntime.current().executeSync(this);
    }

    public final CompletableFuture<T> async() {
        return R4BRuntime.current().executeAsync(this);
    }

    public final EndpointDefinition endpoint() {
        return endpoint;
    }

    public final JavaType responseType() {
        return responseType;
    }

    public final URI uri(URI baseUri) {
        StringBuilder value = new StringBuilder(baseUri.toString()).append(path);
        boolean first = true;
        for (Map.Entry<String, String> parameter : query.entrySet()) {
            value.append(first ? '?' : '&').append(ParameterParser.encode(parameter.getKey()))
                .append('=').append(ParameterParser.encode(parameter.getValue()));
            first = false;
        }
        return URI.create(value.toString());
    }

    public static JavaType type(Class<?> type) {
        return TypeFactory.defaultInstance().constructType(type);
    }

    public static JavaType type(java.lang.reflect.Type type) {
        return TypeFactory.defaultInstance().constructType(type);
    }
}
