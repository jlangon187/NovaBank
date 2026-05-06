package com.jlanzasg.novabank.operacion.dto.cuenta.response;

import lombok.Data;
import lombok.EqualsAndHashCode;

import java.time.LocalDateTime;

/**
 * The type Cuenta response dto.
 */
@Data
@EqualsAndHashCode(callSuper = true)
public class CuentaResponseDTO extends CuentaSimpleResponseDTO {

    private LocalDateTime fecha;
    private Long clienteId;
    private String clienteName;
}