package com.jlanzasg.novabank.exchange.controller;

import com.jlanzasg.novabank.exchange.dto.ExchangeRateResponseDTO;
import org.springframework.web.bind.annotation.*;
import reactor.core.publisher.Mono;

import java.time.Duration;
import java.time.LocalDateTime;

/**
 * The type Exchange rate controller.
 */
@RestController
@RequestMapping("/api/exchange-rate")
public class ExchangeRateController {

    /**
     * Obtener tasa cambio mono.
     *
     * @param from  the from
     * @param to    the to
     * @param delay the delay
     * @return the mono
     */
    @GetMapping
    public Mono<ExchangeRateResponseDTO> obtenerTasaCambio(
            @RequestParam String from,
            @RequestParam String to,
            @RequestParam(required = false, defaultValue = "false") boolean delay) {

        double tasaSimulada;

        if ("EUR".equalsIgnoreCase(from) && "USD".equalsIgnoreCase(to)) {
            tasaSimulada = 1.05;
        } else if ("USD".equalsIgnoreCase(from) && "EUR".equalsIgnoreCase(to)) {
            tasaSimulada = 1.0 / 1.05;
        } else {
            tasaSimulada = 1.0;
        }

        ExchangeRateResponseDTO response = new ExchangeRateResponseDTO(
                from.toUpperCase(),
                to.toUpperCase(),
                tasaSimulada,
                LocalDateTime.now()
        );

        Mono<ExchangeRateResponseDTO> respuestaReactiva = Mono.just(response);

        if (delay) {
            return respuestaReactiva.delayElement(Duration.ofSeconds(5));
        }

        return respuestaReactiva;
    }
}