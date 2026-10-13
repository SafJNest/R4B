package com.R4B.client.api;

import com.R4B.api.dto.leaderboard.LeaderboardDistribution;
import com.R4B.api.dto.leaderboard.LeaderboardPage;
import com.R4B.client.core.EndpointCatalog;
import com.R4B.client.core.EndpointDefinition;
import com.R4B.client.core.R4BRequestBuilder;
import com.R4B.lol.GameQueueType;
import com.R4B.lol.LaneType;
import com.R4B.lol.LeagueShard;
import com.R4B.lol.TierType;

public final class LeaderboardApi {
    private static final EndpointDefinition LEADERBOARD = EndpointCatalog.get("leaderboard.page", "leaderboard", "rank", "region", "queue", "role", "otp", "page", "limit");
    private static final EndpointDefinition DISTRIBUTION = EndpointCatalog.get("leaderboard.rankDistribution", "leaderboard", "region", "queue");
    private static final EndpointDefinition TOP_REGIONS = EndpointCatalog.get("leaderboard.topRegions", "leaderboard", "rank", "queue");

    public LeaderboardBuilder get() {
        return new LeaderboardBuilder(LEADERBOARD, "/api/lol/leaderboard");
    }

    public DistributionBuilder rankDistribution() {
        return new DistributionBuilder(DISTRIBUTION, "/api/lol/leaderboard/rank-distribution");
    }

    public TopRegionsBuilder topRegions(TierType rank) {
        return new TopRegionsBuilder(TOP_REGIONS, "/api/lol/leaderboard/top-regions", rank);
    }

    public static final class LeaderboardBuilder extends R4BRequestBuilder<LeaderboardPage> {
        private LeaderboardBuilder(EndpointDefinition endpoint, String path) { super(endpoint, path, R4BRequestBuilder.type(LeaderboardPage.class)); }
        public LeaderboardBuilder withRank(TierType value) { query("rank", value); return this; }
        public LeaderboardBuilder withRegion(LeagueShard value) { query("region", value); return this; }
        public LeaderboardBuilder withQueue(GameQueueType value) { query("queue", value); return this; }
        public LeaderboardBuilder withRole(LaneType value) { query("role", value); return this; }
        public LeaderboardBuilder withOtp(String value) { query("otp", value); return this; }
        public LeaderboardBuilder withPage(Integer value) { query("page", value); return this; }
        public LeaderboardBuilder withLimit(Integer value) { query("limit", value); return this; }
    }

    public static final class DistributionBuilder extends R4BRequestBuilder<LeaderboardDistribution> {
        private DistributionBuilder(EndpointDefinition endpoint, String path) { super(endpoint, path, R4BRequestBuilder.type(LeaderboardDistribution.class)); }
        public DistributionBuilder withRegion(LeagueShard value) { query("region", value); return this; }
        public DistributionBuilder withQueue(GameQueueType value) { query("queue", value); return this; }
    }

    public static final class TopRegionsBuilder extends R4BRequestBuilder<LeaderboardDistribution> {
        private TopRegionsBuilder(EndpointDefinition endpoint, String path, TierType rank) {
            super(endpoint, path, R4BRequestBuilder.type(LeaderboardDistribution.class));
            requiredQuery("rank", rank);
        }
        public TopRegionsBuilder withQueue(GameQueueType value) { query("queue", value); return this; }
    }
}
