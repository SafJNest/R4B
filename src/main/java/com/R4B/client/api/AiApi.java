package com.R4B.client.api;

import com.R4B.client.core.EndpointCatalog;
import com.R4B.client.core.EndpointDefinition;
import com.R4B.client.core.R4BStreamRequestBuilder;

public final class AiApi {
    private static final EndpointDefinition TRAINING = EndpointCatalog.get("ai.training", "ai-training", "patch");

    public R4BStreamRequestBuilder training() {
        return new R4BStreamRequestBuilder(TRAINING, "/api/lol/ai/training");
    }
}
