package com.jlanzasg.novabank.operacion.config;

import org.springframework.stereotype.Component;
import org.springframework.web.server.ServerWebExchange;
import org.springframework.web.server.WebFilter;
import org.springframework.web.server.WebFilterChain;
import reactor.core.publisher.Mono;

import java.util.UUID;

/**
 * The type Trace filter.
 */
@Component
public class TraceFilter implements WebFilter {

    @Override
    public Mono<Void> filter(ServerWebExchange exchange, WebFilterChain chain) {
        // 1. Buscamos si el Gateway ya le puso matrícula. Si no, creamos una de 16 caracteres (estilo Micrometer).
        String traceId = exchange.getRequest().getHeaders().getFirst("X-Trace-Id");
        if (traceId == null) {
            traceId = UUID.randomUUID().toString().replace("-", "").substring(0, 16);
        }

        String finalTraceId = traceId;

        // 2. Guardamos la matrícula en el Contexto Reactivo (sobrevive a todos los saltos de hilo)
        return chain.filter(exchange)
                .contextWrite(ctx -> ctx.put("traceId", finalTraceId));
    }
}