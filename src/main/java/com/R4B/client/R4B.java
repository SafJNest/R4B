package com.R4B.client;

import java.net.URI;
import java.time.Duration;

import com.R4B.client.api.AiApi;
import com.R4B.client.api.ChampionApi;
import com.R4B.client.api.LeaderboardApi;
import com.R4B.client.api.MatchApi;
import com.R4B.client.api.RecordsApi;
import com.R4B.client.api.StatusApi;
import com.R4B.client.api.SummonerApi;
import com.R4B.client.core.R4BConfig;
import com.R4B.client.core.R4BRuntime;

public final class R4B {

    public static final SummonerApi summoner = new SummonerApi();
    public static final MatchApi match = new MatchApi();
    public static final ChampionApi champion = new ChampionApi();
    public static final LeaderboardApi leaderboard = new LeaderboardApi();
    public static final RecordsApi records = new RecordsApi();
    public static final StatusApi status = new StatusApi();
    public static final AiApi ai = new AiApi();

    private R4B() {}

    public static void configure(String baseUrl) {
        configure(R4BConfig.of(baseUrl));
    }

    public static void configure(URI baseUri) {
        configure(R4BConfig.of(baseUri));
    }

    public static void configure(R4BConfig config) {
        R4BRuntime.configure(config);
    }
}
