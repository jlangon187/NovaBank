package com.jlanzasg.novabank.cuenta.model;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDateTime;

import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.annotation.Id;
import org.springframework.data.annotation.Version;
import org.springframework.data.relational.core.mapping.Table;

/**
 * The type Cuenta.
 */
@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
@EqualsAndHashCode(onlyExplicitlyIncluded = true)
@Table( name = "cuentas")
public class Cuenta {

    @Id
    private Long id;
    @EqualsAndHashCode.Include
    private String iban;
    @Builder.Default
    private Double balance = 0.0;
    @CreatedDate
    private LocalDateTime fecha;
    private Long clienteId;
    @Version
    private Long version;
}
