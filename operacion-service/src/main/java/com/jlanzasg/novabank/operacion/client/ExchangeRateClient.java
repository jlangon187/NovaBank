package com.jlanzasg.novabank.operacion.client;

import com.jlanzasg.novabank.operacion.exception.ExchangeRateUnavailableException;
import io.github.resilience4j.circuitbreaker.annotation.CircuitBreaker;
import io.github.resilience4j.retry.annotation.Retry;
import org.springframework.stereotype.Component;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.core.publisher.Mono;
import java.time.Duration;

@Component
public class ExchangeRateClient {

    private final WebClient exchangeWebClient;

    public ExchangeRateClient(WebClient.Builder webClientBuilder) {
        this.exchangeWebClient = webClientBuilder.clone().baseUrl("http://exchange-rate-mock-service").build();
    }

    @CircuitBreaker(name = "exchange-service", fallbackMethod = "fallbackExchangeRate")
    @Retry(name = "exchange-service")
    public Mono<Double> obtenerTasaCambioSegura(String monedaOrigen, String monedaDestino) {
        String from = (monedaOrigen == null || monedaOrigen.isBlank()) ? "EUR" : monedaOrigen;
        String to = (monedaDestino == null || monedaDestino.isBlank()) ? "EUR" : monedaDestino;

        if (from.equalsIgnoreCase(to)) {
            return Mono.just(1.0);
        }

        return exchangeWebClient.get()
                .uri(uriBuilder -> uriBuilder
                        .path("/api/exchange-rate")
                        .queryParam("from", from)
                        .queryParam("to", to)
                        .build())
                .retrieve()
                .bodyToMono(java.util.Map.class)
                .map(res -> Double.valueOf(res.get("tasaCambio").toString()))
                .timeout(Duration.ofSeconds(3))
                .onErrorMap(ex -> new ExchangeRateUnavailableException(from, to, ex));
    }

    public Mono<Double> fallbackExchangeRate(String monedaOrigen, String monedaDestino, Throwable t) {
        System.err.println("¡ALERTA! Servicio de divisas caído. Circuit Breaker activado.");
        return Mono.error(new ExchangeRateUnavailableException("No se pudo obtener la tasa de cambio en este momento. Inténtelo más tarde."));
    }
}