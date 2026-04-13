package com.jlanzasg.novabank.model;

import lombok.*;

/**
 * The type Cliente.
 */
@Getter
@EqualsAndHashCode(onlyExplicitlyIncluded = true)
@ToString
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class Cliente {

    @EqualsAndHashCode.Include
    private String dni;

    @Setter
    private Long id;

    private String nombre;
    private String apellido;
    private String email;
    private String telefono;
}
