package com.jlanzasg.novabank.dto.operacion.response;

import com.jlanzasg.novabank.model.Cuenta;
import lombok.Data;

import java.time.LocalDateTime;

/**
 * The type Movimiento response.
 */
@Data
public class MovimientoResponseDTO {

    private Long idMovimiento;
    private String tipoMovimiento;
    private Double cantidad;
    private LocalDateTime fecha;
}
