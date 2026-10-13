package com.R4B.client.api;

import com.R4B.api.dto.match.Match;
import com.R4B.client.core.EndpointCatalog;
import com.R4B.client.core.EndpointDefinition;
import com.R4B.client.core.R4BRequestBuilder;
import com.R4B.lol.LeagueShard;

public final class MatchApi {
    private static final EndpointDefinition DETAIL = EndpointCatalog.get("match.detail", "matches");

    public RequestBuilder get(String gameId, LeagueShard region) {
        String path = RoutePaths.shard(region) + "/match/" + RoutePaths.segment(gameId, "gameId");
        return new RequestBuilder(DETAIL, path, R4BRequestBuilder.type(Match.class));
    }

    public static final class RequestBuilder extends R4BRequestBuilder<Match> {
        private RequestBuilder(EndpointDefinition endpoint, String path, com.fasterxml.jackson.databind.JavaType type) {
            super(endpoint, path, type);
        }
    }
}
