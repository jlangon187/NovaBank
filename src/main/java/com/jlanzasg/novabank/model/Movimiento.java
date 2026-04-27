package com.jlanzasg.novabank.model;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotNull;
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
@Entity
@Table(name = "movimientos")
public class Movimiento {

        @Id
        @GeneratedValue(strategy = GenerationType.IDENTITY)
        private Long id;

        @ManyToOne(fetch = FetchType.LAZY)
        @JoinColumn(name = "cuenta_id")
        private Cuenta cuenta;

        @NotNull
        @Enumerated(EnumType.STRING)
        @Column(name = "tipo")
        private TipoMovimiento tipo;

        @NotNull
        @Column(name = "cantidad")
        private Double cantidad;

        @Builder.Default
        @Column(name = "fecha", insertable = false, updatable = false)
        private LocalDateTime fecha = LocalDateTime.now();
}
