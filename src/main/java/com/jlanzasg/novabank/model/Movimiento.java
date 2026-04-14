package com.jlanzasg.novabank.model;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

/**
 * The type Movimiento.
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class Movimiento {

    private Long id;
    private Long cuentaId;
    private String tipo;
    private Double cantidad;

    @Builder.Default
    private LocalDateTime fecha = LocalDateTime.now();
}
