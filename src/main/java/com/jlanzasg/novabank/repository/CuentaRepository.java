package com.jlanzasg.novabank.repository;

import com.jlanzasg.novabank.model.Cuenta;

import java.math.BigDecimal;
import java.util.List;
import java.util.Optional;

/**
 * The interface Cuenta repository.
 */
public interface CuentaRepository {

    /**
     * Guardar cuenta.
     *
     * @param cuenta the cuenta
     * @return the cuenta
     */
    Cuenta guardar(Cuenta cuenta);

    /**
     * Buscar por id optional.
     *
     * @param id the id
     * @return the optional
     */
    Optional<Cuenta> buscarPorId(Long id);

    /**
     * Buscar por numero optional.
     *
     * @param numeroCuenta the numero cuenta
     * @return the optional
     */
    Optional<Cuenta> buscarPorNumero(String numeroCuenta);

    /**
     * Buscar por cliente id list.
     *
     * @param clienteId the cliente id
     * @return the list
     */
    List<Cuenta> buscarPorClienteId(Long clienteId);

    /**
     * Actualizar saldo cuenta.
     *
     * @param cuentaId   the cuenta id
     * @param nuevoSaldo the nuevo saldo
     * @return the cuenta
     */
    Cuenta actualizarSaldo(Long cuentaId, BigDecimal nuevoSaldo);
}
