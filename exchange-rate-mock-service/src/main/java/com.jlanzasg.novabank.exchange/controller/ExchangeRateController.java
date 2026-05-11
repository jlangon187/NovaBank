package com.jlanzasg.novabank.exchange.controller;

import com.jlanzasg.novabank.exchange.dto.ExchangeRateResponseDTO;
import org.springframework.web.bind.annotation.*;
import reactor.core.publisher.Mono;

import java.time.LocalDateTime;

@RestController
@RequestMapping("/api/exchange-rate")
public class ExchangeRateController {

    @GetMapping
    public Mono<ExchangeRateResponseDTO> obtenerTasaCambio(
            @RequestParam String from,
            @RequestParam String to) {

        double tasaSimulada = ("EUR".equalsIgnoreCase(from) && "USD".equalsIgnoreCase(to)) ? 1.05 : 1.0;

        ExchangeRateResponseDTO response = new ExchangeRateResponseDTO(
                from.toUpperCase(),
                to.toUpperCase(),
                tasaSimulada,
                LocalDateTime.now()
        );

        return Mono.just(response);
    }
}