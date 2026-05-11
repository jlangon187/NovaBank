package com.jlanzasg.novabank.exchange.controller;

import com.jlanzasg.novabank.exchange.dto.ExchangeRateResponseDTO;
import org.springframework.web.bind.annotation.*;
import reactor.core.publisher.Mono;

import java.time.Duration;
import java.time.LocalDateTime;

@RestController
@RequestMapping("/api/exchange-rate")
public class ExchangeRateController {

    @GetMapping
    public Mono<ExchangeRateResponseDTO> obtenerTasaCambio(
            @RequestParam String from,
            @RequestParam String to,
            @RequestParam(required = false, defaultValue = "false") boolean delay) {

        double tasaSimulada = ("EUR".equalsIgnoreCase(from) && "USD".equalsIgnoreCase(to)) ? 1.05 : 1.0;

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