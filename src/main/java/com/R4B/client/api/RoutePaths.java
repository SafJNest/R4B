package com.R4B.client.api;

import com.R4B.client.core.ParameterParser;
import com.R4B.lol.LeagueShard;

final class RoutePaths {
    private RoutePaths() {}

    static String shard(LeagueShard region) {
        if (region == null || region == LeagueShard.UNKNOWN) throw new IllegalArgumentException("region must be a valid League shard");
        return "/api/lol/" + ParameterParser.path(region.getValue(), "region");
    }

    static String segment(String value, String name) {
        return ParameterParser.path(value, name);
    }
}
