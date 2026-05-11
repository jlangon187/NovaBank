package com.jlanzasg.novabank.operacion.model;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.annotation.Id;
import org.springframework.data.relational.core.mapping.Table;

import java.time.LocalDateTime;

/**
 * The type Movimiento.
 */
@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Table(name = "movimientos")
public class Movimiento {

        @Id
        private Long id;
        private String iban;
        private TipoMovimiento tipo;
        private Double cantidad;
        @CreatedDate
        private LocalDateTime fecha;
}
