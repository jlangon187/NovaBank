package com.jlanzasg.novabank.operacion.dto.cuenta.request;

import lombok.Data;

@Data
public class ActualizarSaldosRequestDTO {
    private String ibanOrigen;
    private Double nuevoSaldoOrigen;
    private String ibanDestino;
    private Double nuevoSaldoDestino;
}
