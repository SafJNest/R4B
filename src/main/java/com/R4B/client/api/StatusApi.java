package com.R4B.client.api;

import com.R4B.api.dto.status.BotStatus;
import com.R4B.client.core.EndpointCatalog;
import com.R4B.client.core.EndpointDefinition;
import com.R4B.client.core.R4BRequestBuilder;

public final class StatusApi {
    private static final EndpointDefinition CURRENT = EndpointCatalog.get("status.current", "status");

    public R4BRequestBuilder<BotStatus> get() {
        return new R4BRequestBuilder<>(CURRENT, "/api/status", R4BRequestBuilder.type(BotStatus.class)) {};
    }
}
