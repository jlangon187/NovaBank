package com.jlanzasg.novabank.cliente.model;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDateTime;

import org.hibernate.annotations.CreationTimestamp;

/**
 * The type Cliente.
 */
@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Entity
@EqualsAndHashCode(onlyExplicitlyIncluded = true)
@Table (name = "clientes")
public class Cliente {

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

    @CreationTimestamp
    @Column(name = "fecha_creacion", updatable = false)
    private LocalDateTime fecha;
}