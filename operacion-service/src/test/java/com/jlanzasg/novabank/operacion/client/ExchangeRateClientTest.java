package com.jlanzasg.novabank.operacion.client;

import com.jlanzasg.novabank.operacion.exception.ExchangeRateUnavailableException;
import okhttp3.mockwebserver.MockResponse;
import okhttp3.mockwebserver.MockWebServer;
import org.junit.jupiter.api.Test;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.test.StepVerifier;

import java.io.IOException;
import java.util.concurrent.TimeUnit;

class ExchangeRateClientTest {

    @Test
    void obtenerTasaCambioSegura_WhenServerTimesOut_EmitsExchangeRateUnavailable() throws IOException {
        try (MockWebServer server = new MockWebServer()) {
            server.enqueue(new MockResponse()
                    .setHeader("Content-Type", "application/json")
                    .setBody("{\"tasaCambio\":1.11}")
                    .setBodyDelay(4, TimeUnit.SECONDS));
            server.start();

            ExchangeRateClient client = new ExchangeRateClient(
                    WebClient.builder(),
                    String.format("http://localhost:%s", server.getPort())
            );

            StepVerifier.create(client.obtenerTasaCambioSegura("USD", "EUR"))
                    .expectError(ExchangeRateUnavailableException.class)
                    .verify();
        }
    }

    @Test
    void obtenerTasaCambioSegura_WhenResponseIsMalformed_EmitsExchangeRateUnavailable() throws IOException {
        try (MockWebServer server = new MockWebServer()) {
            server.enqueue(new MockResponse()
                    .setHeader("Content-Type", "application/json")
                    .setBody("{\"otherField\":999}"));
            server.start();

            ExchangeRateClient client = new ExchangeRateClient(
                    WebClient.builder(),
                    String.format("http://localhost:%s", server.getPort())
            );

            StepVerifier.create(client.obtenerTasaCambioSegura("USD", "EUR"))
                    .expectError(ExchangeRateUnavailableException.class)
                    .verify();
        }
    }

    @Test
    void fallbackExchangeRate_AlwaysEmitsDomainException() {
        ExchangeRateClient client = new ExchangeRateClient(WebClient.builder(), "http://localhost:9999");

        StepVerifier.create(client.fallbackExchangeRate("USD", "EUR", new RuntimeException("boom")))
                .expectError(ExchangeRateUnavailableException.class)
                .verify();
    }
}
