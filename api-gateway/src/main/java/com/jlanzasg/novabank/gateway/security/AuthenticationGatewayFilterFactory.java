package com.jlanzasg.novabank.gateway.security;

import org.springframework.cloud.gateway.filter.GatewayFilter;
import org.springframework.cloud.gateway.filter.factory.AbstractGatewayFilterFactory;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Component;
import org.springframework.web.server.ServerWebExchange;
import org.springframework.web.reactive.function.client.WebClientRequestException;
import org.springframework.web.reactive.function.client.WebClientResponseException;
import org.springframework.web.reactive.function.client.WebClient;

/**
 * The type Authentication filter.
 */
@Component
public class AuthenticationGatewayFilterFactory extends AbstractGatewayFilterFactory<AuthenticationGatewayFilterFactory.Config> {

    private final WebClient.Builder webClientBuilder;

    /**
     * Instantiates a new Authentication filter.
     *
     * @param webClientBuilder the web client builder
     */
    public AuthenticationGatewayFilterFactory(WebClient.Builder webClientBuilder) {
        super(Config.class);
        this.webClientBuilder = webClientBuilder;
    }

    /**
     * The type Config.
     */
    public static class Config {
        // Clase vacía necesaria para Spring Cloud Gateway
    }

    @Override
    public GatewayFilter apply(Config config) {
        return (exchange, chain) -> {
            String path = exchange.getRequest().getPath().value();

            boolean isAuthRoute = path.startsWith("/api/auth");
            boolean isSwaggerRoute = path.contains("/v3/api-docs")
                    || path.startsWith("/swagger-ui")
                    || path.startsWith("/webjars")
                    || path.startsWith("/swagger-ui.html");

            if (isAuthRoute || isSwaggerRoute) {
                return chain.filter(exchange); // Dejamos pasar sin pedir token
            }

            String authHeader = exchange.getRequest().getHeaders().getFirst(HttpHeaders.AUTHORIZATION);

            // Comprobar que existe y que es un Bearer token
            if (authHeader == null || !authHeader.startsWith("Bearer ")) {
                return unauthorized(exchange);
            }

            // Quitamos la palabra "Bearer " para quedarnos solo con el JWT
            String tokenLimpio = authHeader.substring(7);

            // 3. Llamar al Auth Server para validar el token
            return webClientBuilder.build()
                    .get()
                    .uri(uriBuilder -> uriBuilder
                            .scheme("lb")
                            .host("auth-service")
                            .path("/api/auth/validate")
                            .queryParam("token", tokenLimpio)
                            .build())
                    .retrieve()
                    .bodyToMono(Boolean.class)
                    .flatMap(isValid -> {
                        if (isValid != null && isValid) {
                            return chain.filter(exchange); // ¡Token válido! Pasa al microservicio
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

                        if (e instanceof WebClientRequestException) {
                            exchange.getResponse().setStatusCode(HttpStatus.SERVICE_UNAVAILABLE);
                            return exchange.getResponse().setComplete();
                        }

                        exchange.getResponse().setStatusCode(HttpStatus.SERVICE_UNAVAILABLE);
                        return exchange.getResponse().setComplete();
                    });
        };
    }

    private static reactor.core.publisher.Mono<Void> unauthorized(ServerWebExchange exchange) {
        exchange.getResponse().setStatusCode(HttpStatus.UNAUTHORIZED);
        return exchange.getResponse().setComplete();
    }
}
