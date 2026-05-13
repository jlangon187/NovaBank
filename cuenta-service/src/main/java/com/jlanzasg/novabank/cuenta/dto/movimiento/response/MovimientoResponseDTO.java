package com.jlanzasg.novabank.cuenta.dto.movimiento.response;

import lombok.Data;

import java.time.LocalDateTime;

@Data
public class MovimientoResponseDTO {
    private Long idMovimiento;
    private String tipoMovimiento;
    private Double cantidad;
    private LocalDateTime fecha;
    private String iban;
}
