package com.jlanzasg.novabank.gateway.security;

import org.springframework.cloud.gateway.filter.GatewayFilter;
import org.springframework.cloud.gateway.filter.factory.AbstractGatewayFilterFactory;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Component;
import org.springframework.web.reactive.function.client.WebClient;

@Component
public class AuthenticationFilter extends AbstractGatewayFilterFactory<AuthenticationFilter.Config> {

    private final WebClient.Builder webClientBuilder;

    public AuthenticationFilter(WebClient.Builder webClientBuilder) {
        super(Config.class);
        this.webClientBuilder = webClientBuilder;
    }

    public static class Config {
        // Clase vacía necesaria para Spring Cloud Gateway
    }

    @Override
    public GatewayFilter apply(Config config) {
        return (exchange, chain) -> {
            // 1. Si es la ruta de login o registro, dejamos pasar directamente
            if (exchange.getRequest().getURI().getPath().contains("/api/auth")) {
                return chain.filter(exchange);
            }

            // 2. Extraer la cabecera "Authorization" de forma segura
            String authHeader = exchange.getRequest().getHeaders().getFirst(HttpHeaders.AUTHORIZATION);

            // Comprobar que existe y que es un Bearer token
            if (authHeader == null || !authHeader.startsWith("Bearer ")) {
                exchange.getResponse().setStatusCode(HttpStatus.UNAUTHORIZED);
                return exchange.getResponse().setComplete(); // Cortamos el paso
            }

            // Quitamos la palabra "Bearer " para quedarnos solo con el JWT
            String tokenLimpio = authHeader.substring(7);

            // 3. Llamar al Auth Server para validar el token
            return webClientBuilder.build()
                    .get()
                    .uri("lb://auth-service/api/auth/validate?token=" + tokenLimpio)
                    .retrieve()
                    .bodyToMono(Boolean.class)
                    .flatMap(isValid -> {
                        if (isValid != null && isValid) {
                            return chain.filter(exchange); // ¡Token válido! Pasa al microservicio
                        } else {
                            exchange.getResponse().setStatusCode(HttpStatus.UNAUTHORIZED);
                            return exchange.getResponse().setComplete(); // Token inventado
                        }
                    })
                    .onErrorResume(e -> {
                        exchange.getResponse().setStatusCode(HttpStatus.UNAUTHORIZED);
                        return exchange.getResponse().setComplete(); // Token expirado o Auth Server caído
                    });
        };
    }
}