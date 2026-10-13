package com.R4B.client.api;

import com.R4B.client.core.EndpointDefinition;
import com.R4B.client.core.R4BRequestBuilder;
import com.R4B.lol.GameQueueType;
import com.fasterxml.jackson.databind.JavaType;

public abstract class QueryBuilder<T, B extends QueryBuilder<T, B>> extends R4BRequestBuilder<T> {
    protected QueryBuilder(EndpointDefinition endpoint, String path, JavaType responseType) {
        super(endpoint, path, responseType);
    }

    @SuppressWarnings("unchecked")
    protected B self() { return (B) this; }

    public B withQueue(GameQueueType value) { query("queue", value); return self(); }
    public B withPatch(String value) { query("patch", value); return self(); }
}
