package com.jlanzasg.novabank.cuenta.dto.cuenta.request;

import lombok.Data;

/**
 * The type Actualizar saldos request dto.
 */
@Data
public class ActualizarSaldosRequestDTO {
    private String ibanOrigen;
    private Double nuevoSaldoOrigen;
    private String ibanDestino;
    private Double nuevoSaldoDestino;
}