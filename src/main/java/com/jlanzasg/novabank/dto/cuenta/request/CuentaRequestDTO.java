package com.jlanzasg.novabank.dto.cuenta.request;

import jakarta.validation.constraints.NotNull;
import lombok.Data;

/**
 * The type Cuenta request dto.
 */
@Data
public class CuentaRequestDTO {

    @NotNull(message = "La ID del cliente es obligatoria")
    private Long clienteId;
}