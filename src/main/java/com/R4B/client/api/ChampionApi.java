package com.R4B.client.api;

import java.util.List;

import com.R4B.api.dto.ChampionIndexable;
import com.R4B.api.dto.ChampionTierList;
import com.R4B.api.dto.ChampionView;
import com.R4B.client.core.EndpointCatalog;
import com.R4B.client.core.EndpointDefinition;
import com.R4B.client.core.R4BRequestBuilder;
import com.R4B.lol.LaneType;
import com.R4B.lol.LeagueShard;
import com.R4B.lol.GameQueueType;
import com.R4B.lol.TierType;
import com.fasterxml.jackson.databind.JavaType;

public final class ChampionApi {
    private static final EndpointDefinition INDEXABLES = EndpointCatalog.get("champion.indexables", "champion");
    private static final EndpointDefinition TIER_LIST = EndpointCatalog.get("champion.tierList", "champion", "patch", "rank", "region", "queue");
    private static final EndpointDefinition CHAMPION = EndpointCatalog.get("champion.detail", "champion", "patch", "rank", "region", "queue", "role");

    public RequestBuilder<List<ChampionIndexable>> indexables() {
        return new RequestBuilder<>(INDEXABLES, "/api/lol/champion/indexables", ApiTypes.list(ChampionIndexable.class));
    }

    public ChampionFilterBuilder<ChampionTierList> tierList() {
        return new ChampionFilterBuilder<>(TIER_LIST, "/api/lol/champions/tier-list", R4BRequestBuilder.type(ChampionTierList.class), false);
    }

    public ChampionFilterBuilder<ChampionView> get(String champion) {
        String path = "/api/lol/champion/" + RoutePaths.segment(champion, "champion");
        return new ChampionFilterBuilder<>(CHAMPION, path, R4BRequestBuilder.type(ChampionView.class), true);
    }

    public static final class RequestBuilder<T> extends R4BRequestBuilder<T> {
        private RequestBuilder(EndpointDefinition endpoint, String path, JavaType type) { super(endpoint, path, type); }
    }

    public static final class ChampionFilterBuilder<T> extends R4BRequestBuilder<T> {
        private final boolean supportsRole;
        private ChampionFilterBuilder(EndpointDefinition endpoint, String path, JavaType type, boolean supportsRole) {
            super(endpoint, path, type);
            this.supportsRole = supportsRole;
        }
        public ChampionFilterBuilder<T> withRegion(LeagueShard value) { query("region", value); return this; }
        public ChampionFilterBuilder<T> withQueue(GameQueueType value) { query("queue", value); return this; }
        public ChampionFilterBuilder<T> withPatch(String value) { query("patch", value); return this; }
        public ChampionFilterBuilder<T> withRank(TierType value) { query("rank", value); return this; }
        public ChampionFilterBuilder<T> withRole(LaneType value) {
            if (!supportsRole) throw new IllegalStateException("role is not supported by the tier-list endpoint");
            query("role", value);
            return this;
        }
    }
}
