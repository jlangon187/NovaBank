package com.jlanzasg.novabank.exchange.controller;

import com.jlanzasg.novabank.exchange.dto.ExchangeRateResponseDTO;
import io.swagger.v3.oas.annotations.OpenAPIDefinition;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.web.bind.annotation.*;
import reactor.core.publisher.Mono;

import java.time.Duration;
import java.time.LocalDateTime;

/**
 * The type Exchange rate controller.
 */
@Tag(name = "Tasas de Cambio", description = "Endpoints para obtener tasas de cambio entre diferentes monedas en el sistema de NovaBank")
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
    @Operation(summary = "Obtener la tasa de cambio entre dos monedas", description = "Permite obtener la tasa de cambio actual entre dos monedas especificadas. " +
        "Se puede simular un retraso en la respuesta para probar el manejo de tiempos de espera en el cliente.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Tasa de cambio obtenida con éxito"),
            @ApiResponse(responseCode = "400", description = "Solicitud inválida, parámetros incorrectos o faltantes")
    })
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