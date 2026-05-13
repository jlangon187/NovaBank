package com.jlanzasg.novabank.gateway.security;

import org.springframework.cloud.gateway.filter.GatewayFilter;
import org.springframework.cloud.gateway.filter.factory.AbstractGatewayFilterFactory;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Component;
import org.springframework.web.server.ServerWebExchange;
import org.springframework.web.reactive.function.client.WebClientRequestException;
import org.springframework.web.reactive.function.client.WebClientResponseException;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.core.publisher.Mono;

/**
 * The type Authentication filter.
 */
@Component
public class AuthenticationGatewayFilterFactory extends AbstractGatewayFilterFactory<AuthenticationGatewayFilterFactory.Config> {

    private final WebClient webClient;

    /**
     * Instantiates a new Authentication gateway filter factory.
     *
     * @param webClientBuilder the web client builder
     */
    public AuthenticationGatewayFilterFactory(WebClient.Builder webClientBuilder) {
        super(Config.class);
        this.webClient = webClientBuilder.build();
    }

    /**
     * The type Config.
     */
    public static class Config { }

    @Override
    public GatewayFilter apply(Config config) {
        return (exchange, chain) -> {

            if (exchange.getRequest().getMethod() != null && exchange.getRequest().getMethod().equals(HttpMethod.OPTIONS)) {
                return chain.filter(exchange);
            }

            String path = exchange.getRequest().getPath().value();

            boolean isAuthRoute = path.startsWith("/api/auth");
            boolean isSwaggerRoute = path.contains("/v3/api-docs")
                    || path.contains("/swagger-ui")
                    || path.contains("/webjars");

            if (isAuthRoute || isSwaggerRoute) {
                return chain.filter(exchange);
            }

            String authHeader = exchange.getRequest().getHeaders().getFirst(HttpHeaders.AUTHORIZATION);

            if (authHeader == null || !authHeader.startsWith("Bearer ")) {
                return unauthorized(exchange);
            }

            String tokenLimpio = authHeader.substring(7);

            return webClient
                    .get()
                    .uri(uriBuilder -> uriBuilder
                            .scheme("lb")
                            .host("auth-service") // ¡OJO! Esto asume que en Eureka se llama exactamente "auth-service"
                            .path("/api/auth/validate")
                            .queryParam("token", tokenLimpio)
                            .build())
                    .retrieve()
                    .bodyToMono(Boolean.class)
                    .flatMap(isValid -> {
                        if (Boolean.TRUE.equals(isValid)) {
                            return chain.filter(exchange);
                        } else {
                            return unauthorized(exchange);
                        }
                    })
                    .onErrorResume(e -> {
                        if (e instanceof WebClientResponseException webClientResponseException) {
                            HttpStatus authStatus = HttpStatus.resolve(webClientResponseException.getStatusCode().value());
                            if (authStatus == HttpStatus.UNAUTHORIZED || authStatus == HttpStatus.FORBIDDEN) {
                                return unauthorized(exchange);
                            }
                        }
                        exchange.getResponse().setStatusCode(HttpStatus.SERVICE_UNAVAILABLE);
                        return exchange.getResponse().setComplete();
                    });
        };
    }

    private Mono<Void> unauthorized(ServerWebExchange exchange) {
        exchange.getResponse().setStatusCode(HttpStatus.UNAUTHORIZED);
        return exchange.getResponse().setComplete();
    }
}