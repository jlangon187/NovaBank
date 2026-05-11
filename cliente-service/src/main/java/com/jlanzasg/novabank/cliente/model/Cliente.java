package com.jlanzasg.novabank.cliente.model;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDateTime;

import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.annotation.Id;
import org.springframework.data.relational.core.mapping.Table;

/**
 * The type Cliente.
 */
@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
@EqualsAndHashCode(onlyExplicitlyIncluded = true)
@Table(name = "clientes")
public class Cliente {

    @Id
    private Long id;
    @EqualsAndHashCode.Include
    private String dni;
    private String nombre;
    private String apellidos;
    private String email;
    private String telefono;
    @CreatedDate
    private LocalDateTime fecha;
}