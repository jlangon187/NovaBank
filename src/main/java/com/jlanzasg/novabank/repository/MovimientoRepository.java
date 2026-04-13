package com.jlanzasg.novabank.repository;

import com.jlanzasg.novabank.model.Movimiento;

import java.time.LocalDateTime;
import java.util.List;

/**
 * The interface Movimiento repository.
 */
public interface MovimientoRepository {

    /**
     * Guardar movimiento.
     *
     * @param movimiento the movimiento
     * @return the movimiento
     */
    Movimiento guardar(Movimiento movimiento);

    /**
     * Buscar por cuenta id list.
     *
     * @param cuentaId the cuenta id
     * @return the list
     */
    List<Movimiento> buscarPorCuentaId(Long cuentaId);

    /**
     * Buscar por cuenta id y fechas list.
     *
     * @param cuentaId the cuenta id
     * @param inicio   the inicio
     * @param fin      the fin
     * @return the list
     */
    List<Movimiento> buscarPorCuentaIdYFechas(Long cuentaId, LocalDateTime inicio, LocalDateTime fin);
}
