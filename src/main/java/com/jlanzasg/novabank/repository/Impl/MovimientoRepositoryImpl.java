package com.jlanzasg.novabank.repository.Impl;

import com.jlanzasg.novabank.model.Movimiento;
import com.jlanzasg.novabank.repository.MovimientoRepository;

import java.sql.Connection;
import java.time.LocalDateTime;
import java.util.List;

/**
 * The type Movimiento repository jdbc.
 */
public class MovimientoRepositoryImpl implements MovimientoRepository {

    @Override
    public Movimiento guardar(Movimiento movimiento) {
        return null;
    }

    @Override
    public List<Movimiento> buscarPorCuentaId(Long cuentaId) {
        return List.of();
    }

    @Override
    public List<Movimiento> buscarPorCuentaIdYFechas(Long cuentaId, LocalDateTime inicio, LocalDateTime fin) {
        return List.of();
    }

    @Override
    public Movimiento guardar(Movimiento movimiento, Connection conn) {
        return null;
    }
}
