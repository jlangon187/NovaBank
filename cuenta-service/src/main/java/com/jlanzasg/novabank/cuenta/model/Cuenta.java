package com.jlanzasg.novabank.cuenta.model;

import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDateTime;

/**
 * The type Cuenta.
 */
@Getter
@Builder
@AllArgsConstructor
@NoArgsConstructor
@EqualsAndHashCode(onlyExplicitlyIncluded = true)
@ToString
@Entity
@Table( name = "cuentas")
public class Cuenta {

    @Setter
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @EqualsAndHashCode.Include
    @Column(name = "numero_cuenta")
    private String iban;

    @Setter
    @Builder.Default
    @Column(name = "balance")
    private Double balance = 0.0;

    @org.hibernate.annotations.CreationTimestamp
    @Column(name = "fecha_creacion", updatable = false, columnDefinition = "TIMESTAMP DEFAULT NOW()")
    private LocalDateTime fecha = LocalDateTime.now();

    @Column(name = "cliente_id", nullable = false)
    private Long clienteId;
}
