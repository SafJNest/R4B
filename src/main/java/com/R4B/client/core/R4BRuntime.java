package com.R4B.client.core;

import java.io.IOException;
import java.io.InputStream;
import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.time.Duration;
import java.util.List;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.CompletionException;

import com.R4B.api.dto.LolApiError;
import com.R4B.api.dto.TransportJson;
import com.R4B.client.exception.R4BException;
import com.R4B.client.exception.R4BPendingException;
import com.fasterxml.jackson.databind.JavaType;
import com.fasterxml.jackson.databind.ObjectMapper;

public final class R4BRuntime {
    private static volatile R4BRuntime current;
    private final R4BConfig config;
    private final HttpClient httpClient;
    private final ObjectMapper mapper;

    private R4BRuntime(R4BConfig config) {
        this.config = config;
        this.httpClient = HttpClient.newBuilder().connectTimeout(config.connectTimeout()).build();
        this.mapper = TransportJson.mapper();
    }

    public static synchronized void configure(R4BConfig config) {
        if (config == null) throw new IllegalArgumentException("R4B config is required");
        if (current != null) {
            if (!current.config.baseUri().equals(config.baseUri())) throw new IllegalStateException("R4B is already configured with another base URL");
            return;
        }
        current = new R4BRuntime(config);
    }

    public static R4BRuntime current() {
        R4BRuntime runtime = current;
        if (runtime == null) throw new IllegalStateException("Call R4B.configure(baseUrl) before making requests");
        return runtime;
    }

    public <T> T executeSync(R4BRequestBuilder<T> request) {
        HttpRequest httpRequest = build(request.endpoint(), request.uri(config.baseUri()));
        try {
            HttpResponse<String> response = httpClient.send(httpRequest, HttpResponse.BodyHandlers.ofString());
            return decode(request.endpoint(), request.responseType(), response.statusCode(), response.uri(), response.body());
        } catch (InterruptedException exception) {
            Thread.currentThread().interrupt();
            throw new R4BException("REQUEST_INTERRUPTED", "HTTP request was interrupted", 0, httpRequest.uri(), "", exception);
        } catch (IOException exception) {
            throw new R4BException("NETWORK_ERROR", exception.getMessage(), 0, httpRequest.uri(), "", exception);
        }
    }

    public <T> CompletableFuture<T> executeAsync(R4BRequestBuilder<T> request) {
        HttpRequest httpRequest = build(request.endpoint(), request.uri(config.baseUri()));
        return httpClient.sendAsync(httpRequest, HttpResponse.BodyHandlers.ofString())
            .<T>thenApply(response -> decode(request.endpoint(), request.responseType(), response.statusCode(), response.uri(), response.body()))
            .exceptionallyCompose(exception -> CompletableFuture.failedFuture(unwrap(exception)));
    }

    public InputStream executeStreamSync(R4BStreamRequestBuilder request) {
        HttpRequest httpRequest = build(request.endpoint(), request.uri(config.baseUri()));
        try {
            HttpResponse<InputStream> response = httpClient.send(httpRequest, HttpResponse.BodyHandlers.ofInputStream());
            return streamResult(request.endpoint(), response.statusCode(), response.uri(), response.body());
        } catch (InterruptedException exception) {
            Thread.currentThread().interrupt();
            throw new R4BException("REQUEST_INTERRUPTED", "HTTP request was interrupted", 0, httpRequest.uri(), "", exception);
        } catch (IOException exception) {
            throw new R4BException("NETWORK_ERROR", exception.getMessage(), 0, httpRequest.uri(), "", exception);
        }
    }

    public CompletableFuture<InputStream> executeStreamAsync(R4BStreamRequestBuilder request) {
        HttpRequest httpRequest = build(request.endpoint(), request.uri(config.baseUri()));
        return httpClient.sendAsync(httpRequest, HttpResponse.BodyHandlers.ofInputStream())
            .thenApply(response -> streamResult(request.endpoint(), response.statusCode(), response.uri(), response.body()))
            .exceptionallyCompose(exception -> CompletableFuture.failedFuture(unwrap(exception)));
    }

    private HttpRequest build(EndpointDefinition endpoint, URI uri) {
        HttpRequest.Builder builder = HttpRequest.newBuilder(uri)
            .timeout(config.requestTimeout())
            .header("Accept", "application/json");
        for (RequestPolicy policy : config.policies()) policy.apply(endpoint, builder);
        if (endpoint.method() == EndpointDefinition.HttpMethod.POST) builder.POST(HttpRequest.BodyPublishers.noBody());
        else builder.GET();
        return builder.build();
    }

    private <T> T decode(EndpointDefinition endpoint, JavaType responseType, int status, URI uri, String body) {
        if (status == 202) throw pending(status, uri, body);
        if (status == 204 && endpoint.allowsNoContent()) return null;
        if (status != 200 && status != 201) throw httpError(status, uri, body);
        if (responseType.getRawClass() == Void.class || body == null || body.isBlank()) return null;
        try {
            return mapper.readValue(body, responseType);
        } catch (IOException exception) {
            throw new R4BException("INVALID_RESPONSE", "Could not decode endpoint response", status, uri, body, exception);
        }
    }

    private InputStream streamResult(EndpointDefinition endpoint, int status, URI uri, InputStream body) {
        if (status == 200 || status == 201) return body;
        String content = readBody(body, uri);
        if (status == 202) throw pending(status, uri, content);
        throw httpError(status, uri, content);
    }

    private R4BPendingException pending(int status, URI uri, String body) {
        LolApiError error = parseError(body);
        return new R4BPendingException(error == null ? "PENDING" : error.code(),
            error == null ? "The requested data is not ready" : error.message(), status, uri, body, error);
    }

    private R4BException httpError(int status, URI uri, String body) {
        LolApiError error = parseError(body);
        String code = error == null || error.code() == null || error.code().isBlank() ? "HTTP_" + status : error.code();
        String message = error == null || error.message() == null || error.message().isBlank()
            ? "HTTP request failed with status " + status : error.message();
        return new R4BException(code, message, status, uri, body);
    }

    private LolApiError parseError(String body) {
        if (body == null || body.isBlank()) return null;
        try {
            return mapper.readValue(body, LolApiError.class);
        } catch (IOException ignored) {
            return null;
        }
    }

    private String readBody(InputStream body, URI uri) {
        try (body) {
            return new String(body.readAllBytes(), java.nio.charset.StandardCharsets.UTF_8);
        } catch (IOException exception) {
            throw new R4BException("RESPONSE_READ_ERROR", exception.getMessage(), 0, uri, "", exception);
        }
    }

    private Throwable unwrap(Throwable exception) {
        Throwable result = exception;
        while ((result instanceof CompletionException || result instanceof java.util.concurrent.ExecutionException) && result.getCause() != null) {
            result = result.getCause();
        }
        if (result instanceof R4BException) return result;
        return new R4BException("NETWORK_ERROR", result.getMessage(), 0, null, "", result);
    }
}
