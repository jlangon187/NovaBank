package com.jlanzasg.novabank.dto.cuenta.response;

import lombok.Data;

/**
 * The type Cuenta simple response dto.
 */
@Data
public class CuentaSimpleResponseDTO {

    private Long id;
    private String iban;
    private Double balance;
}