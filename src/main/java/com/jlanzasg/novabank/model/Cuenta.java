package com.jlanzasg.novabank.model;

import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDateTime;
import java.util.HashSet;
import java.util.LinkedHashSet;
import java.util.Set;

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
    @GeneratedValue(strategy = jakarta.persistence.GenerationType.IDENTITY)
    private Long id;

    @EqualsAndHashCode.Include
    @Column(name = "numero_cuenta")
    private String iban;

    @Setter
    @Builder.Default
    @Column(name = "balance")
    private Double balance = 0.0;

    @Builder.Default
    @Column(name = "fecha_creacion", insertable = false, updatable = false)
    private LocalDateTime fecha = LocalDateTime.now();

    @Setter
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "cliente_id")
    private Cliente cliente;

    @Builder.Default
    @OneToMany(mappedBy = "cuenta", cascade = CascadeType.ALL, orphanRemoval = true)
    Set<Movimiento> movimientos = new LinkedHashSet<>();
}
