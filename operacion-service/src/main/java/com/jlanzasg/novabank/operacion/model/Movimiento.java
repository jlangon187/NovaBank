package com.jlanzasg.novabank.operacion.model;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDateTime;

import org.hibernate.annotations.CreationTimestamp;

/**
 * The type Movimiento.
 */
@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Entity
@Table(name = "movimientos")
public class Movimiento {

        @Id
        @GeneratedValue(strategy = GenerationType.IDENTITY)
        private Long id;

        @Column(name = "cuenta_iban", nullable = false)
        private String cuentaIban;

        @Enumerated(EnumType.STRING)
        @Column(name = "tipo", nullable = false)
        private TipoMovimiento tipo;

        @Column(name = "cantidad", nullable = false)
        private Double cantidad;

        @CreationTimestamp
        @Column(name = "fecha", updatable = false)
        private LocalDateTime fecha;
}
