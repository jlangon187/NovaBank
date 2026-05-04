package com.jlanzasg.novabank.cliente.model;

import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDateTime;

/**
 * The type Cliente.
 */
@Getter
@Setter
@EqualsAndHashCode(onlyExplicitlyIncluded = true)
@ToString (exclude = "cuentas")
@Builder
@AllArgsConstructor
@NoArgsConstructor
@Entity
@Table (name = "clientes")
public class Cliente {

    @Setter
    @Id
    @GeneratedValue (strategy = GenerationType.IDENTITY)
    private Long id;

    @EqualsAndHashCode.Include
    @Column (name = "dni", unique = true, nullable = false)
    private String dni;

    @Column (name = "nombre", nullable = false, length = 20)
    private String nombre;

    @Column (name = "apellidos", nullable = false)
    private String apellidos;

    @Column (name = "email", unique = true, nullable = false)
    private String email;

    @Column (name = "telefono", unique = true, nullable = false)
    private String telefono;

    @org.hibernate.annotations.CreationTimestamp
    @Column(name = "fecha_creacion", updatable = false, columnDefinition = "TIMESTAMP DEFAULT NOW()")
    private LocalDateTime fecha;
}