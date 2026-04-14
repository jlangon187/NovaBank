package com.jlanzasg.novabank.service.Impl;

import com.jlanzasg.novabank.model.Cuenta;
import com.jlanzasg.novabank.model.Movimiento;
import com.jlanzasg.novabank.repository.CuentaRepository;
import com.jlanzasg.novabank.repository.MovimientoRepository;
import com.jlanzasg.novabank.service.MovimientoService;

import java.time.LocalDateTime;
import java.util.List;

/**
 * The type Movimiento service.
 */
public class MovimientoServiceImpl implements MovimientoService {

    private final MovimientoRepository movimientoRepository;
    private final CuentaRepository cuentaRepository;

    /**
     * Instantiates a new Movimiento service.
     *
     * @param movimientoRepository the movimiento repository
     * @param cuentaRepository     the cuenta repository
     */
    public MovimientoServiceImpl(MovimientoRepository movimientoRepository, CuentaRepository cuentaRepository) {
        this.movimientoRepository = movimientoRepository;
        this.cuentaRepository = cuentaRepository;
    }

    @Override
    public List<Movimiento> consultarHistorial(String iban) {
        Cuenta cuenta = cuentaRepository.buscarPorNumero(iban)
                .orElseThrow(() -> new IllegalArgumentException("La cuenta con IBAN " + iban + " no existe."));

        return movimientoRepository.buscarPorCuentaId(cuenta.getId());
    }

    @Override
    public List<Movimiento> consultarHistorialPorFechas(String iban, LocalDateTime inicio, LocalDateTime fin) {
        if (fin.isBefore(inicio)) {
            throw new IllegalArgumentException("La fecha de fin no puede ser anterior a la de inicio.");
        }

        Cuenta cuenta = cuentaRepository.buscarPorNumero(iban)
                .orElseThrow(() -> new IllegalArgumentException("La cuenta con IBAN " + iban + " no existe."));

        return movimientoRepository.buscarPorCuentaIdYFechas(cuenta.getId(), inicio, fin);
    }
}