package com.R4B.client.exception;

import java.net.URI;

import com.R4B.api.dto.LolApiError;

public final class R4BPendingException extends R4BException {
    private final LolApiError error;

    public R4BPendingException(String code, String message, int statusCode, URI uri, String responseBody, LolApiError error) {
        super(code, message, statusCode, uri, responseBody);
        this.error = error;
    }

    public LolApiError error() { return error; }
}
