package com.jlanzasg.novabank.operacion.model;

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

        @Column(name = "cuenta_iban", nullable = false)
        private String cuentaIban;

        @Enumerated(EnumType.STRING)
        @Column(name = "tipo", nullable = false)
        private TipoMovimiento tipo;

        @Column(name = "cantidad", nullable = false)
        private Double cantidad;

        @org.hibernate.annotations.CreationTimestamp
        @Column(name = "fecha", updatable = false, columnDefinition = "TIMESTAMP DEFAULT NOW()")
        private LocalDateTime fecha = LocalDateTime.now();
}
