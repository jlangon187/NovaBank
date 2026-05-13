package com.jlanzasg.novabank.exchange.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import java.time.LocalDateTime;

/**
 * The type Exchange rate response dto.
 */
@Data
@AllArgsConstructor
@NoArgsConstructor
public class ExchangeRateResponseDTO {
    private String monedaOrigen;
    private String monedaDestino;
    private Double tasaCambio;
    private LocalDateTime timestamp;
}