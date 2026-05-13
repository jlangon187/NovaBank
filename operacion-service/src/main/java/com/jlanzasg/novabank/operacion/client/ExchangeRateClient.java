package com.jlanzasg.novabank.operacion.client;

import com.jlanzasg.novabank.operacion.exception.ExchangeRateUnavailableException;
import io.github.resilience4j.circuitbreaker.annotation.CircuitBreaker;
import io.github.resilience4j.retry.annotation.Retry;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.core.publisher.Mono;
import java.time.Duration;

/**
 * The type Exchange rate client.
 */
@Component
public class ExchangeRateClient {

    private final WebClient exchangeWebClient;

    /**
     * Instantiates a new Exchange rate client.
     *
     * @param webClientBuilder the web client builder
     */
    @Autowired
    public ExchangeRateClient(WebClient.Builder webClientBuilder) {
        this.exchangeWebClient = webClientBuilder.clone().baseUrl("http://exchange-rate-mock-service").build();
    }

    /**
     * Instantiates a new Exchange rate client.
     *
     * @param webClientBuilder the web client builder
     * @param baseUrl          the base url
     */
    public ExchangeRateClient(WebClient.Builder webClientBuilder, String baseUrl) {
        this.exchangeWebClient = webClientBuilder.clone().baseUrl(baseUrl).build();
    }

    /**
     * Obtener tasa cambio segura mono.
     *
     * @param monedaOrigen  the moneda origen
     * @param monedaDestino the moneda destino
     * @return the mono
     */
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

    /**
     * Fallback exchange rate mono.
     *
     * @param monedaOrigen  the moneda origen
     * @param monedaDestino the moneda destino
     * @param t             the t
     * @return the mono
     */
    public Mono<Double> fallbackExchangeRate(String monedaOrigen, String monedaDestino, Throwable t) {
        System.err.println("¡ALERTA! Servicio de divisas caído. Circuit Breaker activado.");
        return Mono.error(new ExchangeRateUnavailableException("No se pudo obtener la tasa de cambio en este momento. Inténtelo más tarde."));
    }
}
