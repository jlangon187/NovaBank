package com.jlanzasg.novabank.service;

import com.jlanzasg.novabank.model.Movimiento;
import java.time.LocalDateTime;
import java.util.List;

/**
 * The interface Movimiento service.
 */
public interface MovimientoService {

    /**
     * Consultar historial list.
     *
     * @param iban the iban
     * @return the list
     */
    List<Movimiento> consultarHistorial(String iban);

    /**
     * Consultar historial por fechas list.
     *
     * @param iban   the iban
     * @param inicio the inicio
     * @param fin    the fin
     * @return the list
     */
    List<Movimiento> consultarHistorialPorFechas(String iban, LocalDateTime inicio, LocalDateTime fin);
}