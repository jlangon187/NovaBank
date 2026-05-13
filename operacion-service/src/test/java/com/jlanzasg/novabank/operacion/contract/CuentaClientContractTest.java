package com.jlanzasg.novabank.operacion.contract;

import com.jlanzasg.novabank.operacion.client.ExchangeRateClient;
import okhttp3.mockwebserver.MockResponse;
import okhttp3.mockwebserver.MockWebServer;
import org.junit.jupiter.api.Test;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.test.StepVerifier;

import java.io.IOException;

class CuentaClientContractTest {

    @Test
    void obtenerTasaCambioSegura_CuandoServidorRespondeOk_RetornaTasa() throws IOException, InterruptedException {
        try (MockWebServer server = new MockWebServer()) {
            server.enqueue(new MockResponse()
                    .setHeader("Content-Type", "application/json")
                    .setBody("{\"tasaCambio\":1.25}"));
            server.start();

            ExchangeRateClient client = new ExchangeRateClient(
                    WebClient.builder(),
                    String.format("http://localhost:%s", server.getPort())
            );

            StepVerifier.create(client.obtenerTasaCambioSegura("USD", "EUR"))
                    .expectNext(1.25)
                    .verifyComplete();

            okhttp3.mockwebserver.RecordedRequest request = server.takeRequest();
            org.assertj.core.api.Assertions.assertThat(request.getMethod()).isEqualTo("GET");
            org.assertj.core.api.Assertions.assertThat(request.getPath()).isEqualTo("/api/exchange-rate?from=USD&to=EUR");
        }
    }

    @Test
    void obtenerTasaCambioSegura_CuandoMonedaOrigenIgualDestino_NoLlamaHttpYDevuelveUno() throws IOException {
        try (MockWebServer server = new MockWebServer()) {
            server.start();

            ExchangeRateClient client = new ExchangeRateClient(
                    WebClient.builder(),
                    String.format("http://localhost:%s", server.getPort())
            );

            StepVerifier.create(client.obtenerTasaCambioSegura("EUR", "EUR"))
                    .expectNext(1.0)
                    .verifyComplete();

            org.assertj.core.api.Assertions.assertThat(server.getRequestCount()).isEqualTo(0);
        }
    }
}
