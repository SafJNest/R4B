package com.R4B.client.core;

import java.net.http.HttpRequest;

@FunctionalInterface
public interface RequestPolicy {
    void apply(EndpointDefinition endpoint, HttpRequest.Builder request);
}
