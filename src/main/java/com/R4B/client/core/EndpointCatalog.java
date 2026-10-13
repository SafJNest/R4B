package com.R4B.client.core;

import java.util.Arrays;
import java.util.Set;

public final class EndpointCatalog {
    private EndpointCatalog() {}

    public static EndpointDefinition get(String id, String bucket, String... queryParameters) {
        return create(id, EndpointDefinition.HttpMethod.GET, false, bucket, queryParameters);
    }

    public static EndpointDefinition postNoContent(String id, String bucket, String... queryParameters) {
        return create(id, EndpointDefinition.HttpMethod.POST, true, bucket, queryParameters);
    }

    private static EndpointDefinition create(String id, EndpointDefinition.HttpMethod method, boolean noContent,
            String bucket, String... queryParameters) {
        return new EndpointDefinition(id, method, Set.copyOf(Arrays.asList(queryParameters)), AccessScope.PUBLIC, bucket, noContent);
    }
}
