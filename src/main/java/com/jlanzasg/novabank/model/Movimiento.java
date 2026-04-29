package com.jlanzasg.novabank.model;

import jakarta.persistence.*;
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
        @JoinColumn(name = "cuenta_id", nullable = false)
        private Cuenta cuenta;

        @Enumerated(EnumType.STRING)
        @Column(name = "tipo", nullable = false)
        private TipoMovimiento tipo;

        @Column(name = "cantidad", nullable = false)
        private Double cantidad;

        @Builder.Default
        @Column(name = "fecha", nullable = false)
        private LocalDateTime fecha = LocalDateTime.now();
}
