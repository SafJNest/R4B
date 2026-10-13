package com.R4B.client;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;
import static org.junit.Assert.fail;

import java.io.IOException;
import java.net.InetSocketAddress;
import java.nio.charset.StandardCharsets;
import java.util.List;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.concurrent.atomic.AtomicReference;

import com.R4B.client.exception.R4BException;
import com.R4B.client.exception.R4BPendingException;
import com.R4B.lol.GameQueueType;
import com.R4B.lol.LeagueShard;
import com.sun.net.httpserver.HttpExchange;
import com.sun.net.httpserver.HttpServer;
import org.junit.AfterClass;
import org.junit.BeforeClass;
import org.junit.Test;

public final class R4BHttpClientTest {
    private static HttpServer server;
    private static final AtomicInteger REQUEST_COUNT = new AtomicInteger();
    private static final AtomicReference<String> LAST_URI = new AtomicReference<>();
    private static final AtomicReference<String> LAST_METHOD = new AtomicReference<>();

    @BeforeClass
    public static void startServer() throws IOException {
        server = HttpServer.create(new InetSocketAddress("127.0.0.1", 0), 0);
        server.createContext("/", R4BHttpClientTest::handle);
        server.start();
        R4B.configure("http://127.0.0.1:" + server.getAddress().getPort());
    }

    @AfterClass
    public static void stopServer() {
        server.stop(0);
    }

    @Test
    public void searchBuildsShardPathAndEncodedQuery() {
        REQUEST_COUNT.set(0);
        List<?> result = R4B.summoner.search("A name", LeagueShard.EUW1).sync();

        assertTrue(result.isEmpty());
        assertEquals(1, REQUEST_COUNT.get());
        assertEquals("/api/lol/EUW1/search?q=A%20name", LAST_URI.get());
    }

    @Test
    public void refreshUsesPostAndAcceptsExpectedNoContent() {
        REQUEST_COUNT.set(0);
        assertEquals(null, R4B.summoner.refresh("puuid", LeagueShard.EUW1).sync());
        assertEquals(1, REQUEST_COUNT.get());
        assertEquals("POST", LAST_METHOD.get());
    }

    @Test
    public void profileTakesRegionInPathAndSupportsOptionalQueue() {
        try {
            R4B.summoner.getProfile("puuid", LeagueShard.EUW1).withQueue(GameQueueType.RANKED_SOLO_5X5).sync();
            fail("Expected HTTP error");
        } catch (R4BException exception) {
            assertTrue(exception.uri().getPath().startsWith("/api/lol/EUW1/profile/puuid"));
            assertTrue(exception.uri().getQuery().contains("queue=RANKED_SOLO_5X5"));
            assertTrue(!exception.uri().getQuery().contains("region="));
        }
    }

    @Test
    public void championRegionRemainsAnOptionalFilter() {
        try {
            R4B.champion.get("Ahri").withRegion(LeagueShard.EUW1).sync();
            fail("Expected HTTP error");
        } catch (R4BException exception) {
            assertTrue(exception.uri().getPath().equals("/api/lol/champion/Ahri"));
            assertTrue(exception.uri().getQuery().contains("region=EUW1"));
        }
    }

    @Test
    public void pendingResponseBecomesDedicatedException() {
        try {
            R4B.match.get("M_123", LeagueShard.EUW1).sync();
            fail("Expected pending response");
        } catch (R4BPendingException exception) {
            assertEquals("DATA_PENDING", exception.code());
            assertEquals(202, exception.statusCode());
            assertEquals("/api/lol/EUW1/match/M_123", exception.uri().getPath());
            assertNotNull(exception.error().metadata());
        }
    }

    @Test
    public void httpErrorPreservesServerCodeAndRawBody() {
        try {
            R4B.leaderboard.get().withRegion(LeagueShard.EUW1).withQueue(GameQueueType.RANKED_SOLO_5X5).sync();
            fail("Expected HTTP error");
        } catch (R4BException exception) {
            assertEquals("ACCESS_DENIED", exception.code());
            assertEquals(403, exception.statusCode());
            assertTrue(exception.responseBody().contains("ACCESS_DENIED"));
            assertTrue(exception.uri().toString().contains("region=EUW1"));
            assertTrue(exception.uri().toString().contains("queue="));
        }
    }

    @Test
    public void asyncUsesSameResponsePipeline() throws Exception {
        CompletableFuture<List<com.R4B.api.dto.summoner.SummonerView>> future =
            R4B.summoner.search("async", LeagueShard.EUW1).async();
        assertTrue(future.get().isEmpty());
    }

    @Test
    public void trainingEndpointReturnsCallerOwnedStream() throws Exception {
        try (var stream = R4B.ai.training().withPatch("14.1").sync()) {
            assertEquals("dataset", new String(stream.readAllBytes(), StandardCharsets.UTF_8));
        }
    }

    private static void handle(HttpExchange exchange) throws IOException {
        REQUEST_COUNT.incrementAndGet();
        LAST_URI.set(exchange.getRequestURI().toASCIIString());
        LAST_METHOD.set(exchange.getRequestMethod());
        String path = exchange.getRequestURI().getPath();
        int status = 200;
        String contentType = "application/json";
        String body = "[]";

        if (path.endsWith("/profile/puuid/refresh")) {
            status = 204;
            body = "";
        } else if (path.endsWith("/match/M_123")) {
            status = 202;
            body = "{\"status\":202,\"code\":\"DATA_PENDING\",\"message\":\"Still processing\",\"metadata\":{\"refresh\":true}}";
        } else if ("/api/lol/leaderboard".equals(path)) {
            status = 403;
            body = "{\"status\":403,\"code\":\"ACCESS_DENIED\",\"message\":\"Denied\"}";
        } else if (path.endsWith("/profile/puuid") || path.equals("/api/lol/champion/Ahri")) {
            status = 403;
            body = "{\"status\":403,\"code\":\"ACCESS_DENIED\",\"message\":\"Denied\"}";
        } else if (path.endsWith("/ai/training")) {
            contentType = "application/octet-stream";
            body = "dataset";
        }

        exchange.getResponseHeaders().set("Content-Type", contentType);
        byte[] bytes = body.getBytes(StandardCharsets.UTF_8);
        if (status == 204) exchange.sendResponseHeaders(status, -1);
        else {
            exchange.sendResponseHeaders(status, bytes.length);
            exchange.getResponseBody().write(bytes);
        }
        exchange.close();
    }
}
