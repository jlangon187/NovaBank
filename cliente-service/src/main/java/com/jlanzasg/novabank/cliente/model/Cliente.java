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
import org.springframework.data.relational.core.mapping.Column;
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
    @Column("id")
    private Long id;
    @EqualsAndHashCode.Include
    @Column("dni")
    private String dni;
    @Column("nombre")
    private String nombre;
    @Column("apellidos")
    private String apellidos;
    @Column("email")
    private String email;
    @Column("telefono")
    private String telefono;
    @CreatedDate
    @Column("fecha")
    private LocalDateTime fecha;
}
