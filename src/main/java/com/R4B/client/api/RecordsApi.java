package com.R4B.client.api;

import com.R4B.api.dto.record.RecordPage;
import com.R4B.api.dto.record.RecordsOverview;
import com.R4B.client.core.EndpointCatalog;
import com.R4B.client.core.EndpointDefinition;
import com.R4B.client.core.R4BRequestBuilder;
import com.R4B.lol.LeagueShard;
import com.R4B.lol.RecordMetric;
import com.fasterxml.jackson.databind.JavaType;

public final class RecordsApi {
    private static final EndpointDefinition OVERVIEW = EndpointCatalog.get("records.overview", "records", "region");
    private static final EndpointDefinition MASTERY = EndpointCatalog.get("records.highestMastery", "records", "region", "limit", "offset");
    private static final EndpointDefinition METRIC = EndpointCatalog.get("records.metric", "records", "region", "limit", "offset");

    public OverviewBuilder overview() { return new OverviewBuilder(OVERVIEW, "/api/lol/records"); }

    public PageBuilder highestMasteryByChampion(String champion) {
        String path = "/api/lol/records/HIGHEST_MASTERY/" + RoutePaths.segment(champion, "champion");
        return new PageBuilder(MASTERY, path);
    }

    public PageBuilder metric(RecordMetric metric) {
        if (metric == null) throw new IllegalArgumentException("metric is required");
        return new PageBuilder(METRIC, "/api/lol/records/" + RoutePaths.segment(metric.name(), "metric"));
    }

    public static final class OverviewBuilder extends R4BRequestBuilder<RecordsOverview> {
        private OverviewBuilder(EndpointDefinition endpoint, String path) { super(endpoint, path, R4BRequestBuilder.type(RecordsOverview.class)); }
        public OverviewBuilder withRegion(LeagueShard value) { query("region", value); return this; }
    }

    public static final class PageBuilder extends R4BRequestBuilder<RecordPage> {
        private PageBuilder(EndpointDefinition endpoint, String path) { super(endpoint, path, R4BRequestBuilder.type(RecordPage.class)); }
        public PageBuilder withRegion(LeagueShard value) { query("region", value); return this; }
        public PageBuilder withLimit(Integer value) { query("limit", value); return this; }
        public PageBuilder withOffset(Integer value) { query("offset", value); return this; }
    }
}
