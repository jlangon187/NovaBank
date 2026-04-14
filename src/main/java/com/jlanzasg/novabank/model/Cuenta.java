package com.jlanzasg.novabank.model;

import lombok.*;

import java.time.LocalDateTime;

@ToString
@Getter
@Builder
@AllArgsConstructor
@NoArgsConstructor
@EqualsAndHashCode(onlyExplicitlyIncluded = true)
public class Cuenta {

    @Setter
    private Long id;

    @EqualsAndHashCode.Include
    private String iban;

    private Cliente cliente;

    @Setter
    @Builder.Default
    private Double balance = 0.0;

    @Builder.Default
    private LocalDateTime fecha = LocalDateTime.now();
}
