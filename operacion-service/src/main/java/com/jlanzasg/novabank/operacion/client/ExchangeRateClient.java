package com.jlanzasg.novabank.operacion.client;

import com.jlanzasg.novabank.operacion.exception.ExchangeRateUnavailableException;
import io.github.resilience4j.circuitbreaker.annotation.CircuitBreaker;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.core.publisher.Mono;

import java.time.Duration;
import java.util.Map;

@Component
public class ExchangeRateClient {

    private final WebClient webClient;

    @Autowired
    public ExchangeRateClient(WebClient.Builder webClientBuilder) {
        this.webClient = webClientBuilder.baseUrl("http://exchange-rate-mock-service").build();
    }

    public ExchangeRateClient(WebClient.Builder webClientBuilder, String baseUrl) {
        this.webClient = webClientBuilder.baseUrl(baseUrl).build();
    }

    @CircuitBreaker(name = "exchangeRateService", fallbackMethod = "obtenerTasaCambioFallback")
    public Mono<Double> obtenerTasaCambio(String from, String to) {
        return webClient.get()
                .uri(uriBuilder -> uriBuilder
                        .path("/api/exchange-rate")
                        .queryParam("from", from)
                        .queryParam("to", to)
                        .build())
                .retrieve()
                .bodyToMono(Map.class)
                .map(res -> (Double) res.get("tasaCambio"))
                .timeout(Duration.ofSeconds(3))
                .onErrorMap(ex -> new ExchangeRateUnavailableException(from, to, ex));
    }

    private Mono<Double> obtenerTasaCambioFallback(String from, String to, Throwable throwable) {
        return Mono.error(new ExchangeRateUnavailableException(from, to, throwable));
    }

    public Mono<Double> obtenerTasaCambioSegura(String monedaOrigen, String monedaDestino) {
        String from = (monedaOrigen == null || monedaOrigen.isBlank()) ? "EUR" : monedaOrigen;
        String to = (monedaDestino == null || monedaDestino.isBlank()) ? "EUR" : monedaDestino;
        if (from.equalsIgnoreCase(to)) {
            return Mono.just(1.0);
        }
        return obtenerTasaCambio(from, to);
    }

    public Mono<Double> fallbackExchangeRate(String monedaOrigen, String monedaDestino, Throwable throwable) {
        String from = (monedaOrigen == null || monedaOrigen.isBlank()) ? "EUR" : monedaOrigen;
        String to = (monedaDestino == null || monedaDestino.isBlank()) ? "EUR" : monedaDestino;
        return obtenerTasaCambioFallback(from, to, throwable);
    }
}
