package com.jlanzasg.novabank.dto.operacion.request;

import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Pattern;
import lombok.Data;

/**
 * The type Transferencia request dto.
 */
@Data
public class TransferenciaRequestDTO {

    @NotNull(message = "El IBAN es obligatorio")
    @Pattern(regexp = "^ES\\d{20}$", message = "El IBAN debe tener un formato válido")
    private String cuentaOrigen;

    @NotNull(message = "El IBAN es obligatorio")
    @Pattern(regexp = "^ES\\d{20}$", message = "El IBAN debe tener un formato válido")
    private String cuentaDestino;

    @NotNull(message = "El importe es obligatorio")
    @DecimalMin(value = "0.01", message = "El importe debe ser mayor que cero")
    private Double importe;
}
