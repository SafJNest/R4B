package com.R4B.client.api;

import java.util.List;

import com.R4B.api.dto.ProfileIndexable;
import com.R4B.api.dto.match.LiveGame;
import com.R4B.api.dto.match.MatchPage;
import com.R4B.api.dto.match.RankHistory;
import com.R4B.api.dto.record.ProfileRecordPage;
import com.R4B.api.dto.statistics.ProfileActivity;
import com.R4B.api.dto.statistics.ProfileMatchups;
import com.R4B.api.dto.summoner.SummonerView;
import com.R4B.client.core.EndpointCatalog;
import com.R4B.client.core.EndpointDefinition;
import com.R4B.client.core.R4BRequestBuilder;
import com.R4B.lol.GameQueueType;
import com.R4B.lol.LaneType;
import com.R4B.lol.LeagueShard;
import com.R4B.lol.MatchOrder;
import com.R4B.lol.RankHistoryView;
import com.fasterxml.jackson.databind.JavaType;

public final class SummonerApi {
    private static final EndpointDefinition SEARCH = EndpointCatalog.get("summoner.search", "summoner", "q");
    private static final EndpointDefinition PROFILE = EndpointCatalog.get("summoner.profile", "summoner", "queue", "patch", "season");
    private static final EndpointDefinition REFRESH = EndpointCatalog.postNoContent("summoner.refresh", "summoner");
    private static final EndpointDefinition MATCHES = EndpointCatalog.get("summoner.matches", "matches", "queue", "limit", "offset", "timeStart", "timeEnd", "sort", "champion", "lane", "patch");
    private static final EndpointDefinition RANK_HISTORY = EndpointCatalog.get("summoner.rankHistory", "matches", "queue", "view", "season", "patch", "timeStart", "timeEnd", "sort");
    private static final EndpointDefinition ACTIVITY = EndpointCatalog.get("summoner.activity", "profile", "start", "end", "queue", "champion");
    private static final EndpointDefinition MATCHUPS = EndpointCatalog.get("summoner.matchups", "profile", "start", "end", "queue", "patch", "role", "minGames");
    private static final EndpointDefinition PROFILE_RECORDS = EndpointCatalog.get("summoner.records", "records");
    private static final EndpointDefinition LIVE_GAME = EndpointCatalog.get("summoner.liveGame", "summoner");
    private static final EndpointDefinition INDEXABLES = EndpointCatalog.get("summoner.indexables", "profile");

    public RequestBuilder<List<SummonerView>> search(String query, LeagueShard region) {
        String path = RoutePaths.shard(region) + "/search";
        SearchBuilder builder = new SearchBuilder(SEARCH, path, ApiTypes.list(SummonerView.class));
        builder.withSearch(required(query, "query"));
        return builder;
    }

    public ProfileBuilder getProfile(String puuid, LeagueShard region) {
        return new ProfileBuilder(PROFILE, RoutePaths.shard(region) + "/profile/" + RoutePaths.segment(puuid, "puuid"),
            R4BRequestBuilder.type(SummonerView.class));
    }

    public ProfileBuilder getProfileByName(String name, String tag, LeagueShard region) {
        String path = RoutePaths.shard(region) + "/profile-by-name/" + RoutePaths.segment(name, "name") + "/" + RoutePaths.segment(tag, "tag");
        return new ProfileBuilder(PROFILE, path, R4BRequestBuilder.type(SummonerView.class));
    }

    public RequestBuilder<Void> refresh(String puuid, LeagueShard region) {
        return new RequestBuilder<>(REFRESH, RoutePaths.shard(region) + "/profile/" + RoutePaths.segment(puuid, "puuid") + "/refresh", R4BRequestBuilder.type(Void.class));
    }

    public MatchesBuilder matches(String puuid, LeagueShard region) {
        return new MatchesBuilder(MATCHES, RoutePaths.shard(region) + "/profile/" + RoutePaths.segment(puuid, "puuid") + "/matches",
            R4BRequestBuilder.type(MatchPage.class));
    }

    public RankHistoryBuilder rankHistory(String puuid, LeagueShard region) {
        return new RankHistoryBuilder(RANK_HISTORY, RoutePaths.shard(region) + "/profile/" + RoutePaths.segment(puuid, "puuid") + "/rank-history",
            R4BRequestBuilder.type(RankHistory.class));
    }

    public ActivityBuilder activity(String puuid, LeagueShard region) {
        return new ActivityBuilder(ACTIVITY, RoutePaths.shard(region) + "/profile/" + RoutePaths.segment(puuid, "puuid") + "/activity",
            R4BRequestBuilder.type(ProfileActivity.class));
    }

    public MatchupsBuilder matchups(String puuid, LeagueShard region) {
        return new MatchupsBuilder(MATCHUPS, RoutePaths.shard(region) + "/profile/" + RoutePaths.segment(puuid, "puuid") + "/matchups",
            R4BRequestBuilder.type(ProfileMatchups.class));
    }

    public RequestBuilder<ProfileRecordPage> records(String puuid, LeagueShard region) {
        return new RequestBuilder<>(PROFILE_RECORDS, RoutePaths.shard(region) + "/profile/" + RoutePaths.segment(puuid, "puuid") + "/records",
            R4BRequestBuilder.type(ProfileRecordPage.class));
    }

    public RequestBuilder<LiveGame> liveGame(String puuid, LeagueShard region) {
        return new RequestBuilder<>(LIVE_GAME, RoutePaths.shard(region) + "/livegame/" + RoutePaths.segment(puuid, "puuid"),
            R4BRequestBuilder.type(LiveGame.class));
    }

    public RequestBuilder<LiveGame> liveGameByName(String name, String tag, LeagueShard region) {
        String path = RoutePaths.shard(region) + "/livegame-by-name/" + RoutePaths.segment(name, "name") + "/" + RoutePaths.segment(tag, "tag");
        return new RequestBuilder<>(LIVE_GAME, path, R4BRequestBuilder.type(LiveGame.class));
    }

    public RequestBuilder<List<ProfileIndexable>> indexables() {
        return new RequestBuilder<>(INDEXABLES, "/api/lol/profile/indexables", ApiTypes.list(ProfileIndexable.class));
    }

    private static String required(String value, String name) {
        if (value == null || value.isBlank()) throw new IllegalArgumentException(name + " is required");
        return value.trim();
    }

    public static class RequestBuilder<T> extends R4BRequestBuilder<T> {
        protected RequestBuilder(EndpointDefinition endpoint, String path, JavaType type) { super(endpoint, path, type); }
    }

    public static final class SearchBuilder extends RequestBuilder<List<SummonerView>> {
        private SearchBuilder(EndpointDefinition endpoint, String path, JavaType type) { super(endpoint, path, type); }
        private SearchBuilder withSearch(String value) { query("q", value); return this; }
    }

    public static final class ProfileBuilder extends QueryBuilder<SummonerView, ProfileBuilder> {
        private ProfileBuilder(EndpointDefinition endpoint, String path, JavaType type) { super(endpoint, path, type); }
        public ProfileBuilder withSeason(Integer value) { query("season", value); return this; }
    }

    public static final class MatchesBuilder extends QueryBuilder<MatchPage, MatchesBuilder> {
        private MatchesBuilder(EndpointDefinition endpoint, String path, JavaType type) { super(endpoint, path, type); }
        public MatchesBuilder withLane(LaneType value) { query("lane", value); return this; }
        public MatchesBuilder withLimit(Integer value) { query("limit", value); return this; }
        public MatchesBuilder withOffset(Integer value) { query("offset", value); return this; }
        public MatchesBuilder withTimeStart(Long value) { query("timeStart", value); return this; }
        public MatchesBuilder withTimeEnd(Long value) { query("timeEnd", value); return this; }
        public MatchesBuilder withSort(MatchOrder value) { query("sort", value); return this; }
        public MatchesBuilder withChampion(String value) { query("champion", value); return this; }
    }

    public static final class RankHistoryBuilder extends QueryBuilder<RankHistory, RankHistoryBuilder> {
        private RankHistoryBuilder(EndpointDefinition endpoint, String path, JavaType type) { super(endpoint, path, type); }
        public RankHistoryBuilder withSeason(Integer value) { query("season", value); return this; }
        public RankHistoryBuilder withView(RankHistoryView value) { query("view", value); return this; }
        public RankHistoryBuilder withTimeStart(Long value) { query("timeStart", value); return this; }
        public RankHistoryBuilder withTimeEnd(Long value) { query("timeEnd", value); return this; }
        public RankHistoryBuilder withSort(MatchOrder value) { query("sort", value); return this; }
    }

    public static final class ActivityBuilder extends QueryBuilder<ProfileActivity, ActivityBuilder> {
        private ActivityBuilder(EndpointDefinition endpoint, String path, JavaType type) { super(endpoint, path, type); }
        public ActivityBuilder withStart(Long value) { query("start", value); return this; }
        public ActivityBuilder withEnd(Long value) { query("end", value); return this; }
        public ActivityBuilder withChampion(Integer value) { query("champion", value); return this; }
    }

    public static final class MatchupsBuilder extends QueryBuilder<ProfileMatchups, MatchupsBuilder> {
        private MatchupsBuilder(EndpointDefinition endpoint, String path, JavaType type) { super(endpoint, path, type); }
        public MatchupsBuilder withRole(LaneType value) { query("role", value); return this; }
        public MatchupsBuilder withStart(Long value) { query("start", value); return this; }
        public MatchupsBuilder withEnd(Long value) { query("end", value); return this; }
        public MatchupsBuilder withMinGames(Integer value) { query("minGames", value); return this; }
    }
}
