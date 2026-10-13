package com.R4B.client.exception;

import java.net.URI;

public class R4BException extends RuntimeException {
    private final String code;
    private final int statusCode;
    private final URI uri;
    private final String responseBody;

    public R4BException(String code, String message, int statusCode, URI uri, String responseBody) {
        super(message);
        this.code = code;
        this.statusCode = statusCode;
        this.uri = uri;
        this.responseBody = responseBody;
    }

    public R4BException(String code, String message, int statusCode, URI uri, String responseBody, Throwable cause) {
        super(message, cause);
        this.code = code;
        this.statusCode = statusCode;
        this.uri = uri;
        this.responseBody = responseBody;
    }

    public String code() { return code; }
    public int statusCode() { return statusCode; }
    public URI uri() { return uri; }
    public String responseBody() { return responseBody; }
}
