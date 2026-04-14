package com.jlanzasg.novabank.repository;

import com.jlanzasg.novabank.model.Cuenta;

import java.math.BigDecimal;
import java.sql.Connection;
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
     * Buscar por numero optional.
     *
     * @param numeroCuenta the numero cuenta
     * @return the optional
     */
    Optional<Cuenta> buscarPorNumero(String numeroCuenta);

    /**
     * Buscar por id optional.
     *
     * @param id the id
     * @return the optional
     */
    Optional<Cuenta> buscarPorId(Long id);

    /**
     * Listar cuentas por cliente list.
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
    Cuenta actualizarSaldo(Long cuentaId, Double nuevoSaldo);

    /**
     * Obtener ultimo id.
     *
     * @return the long
     */
    Long obtenerUltimoId();


    // Métodos transaccionales

    /**
     * Buscar por numero optional.
     *
     * @param numeroCuenta the numero cuenta
     * @param conn         the conn
     * @return the optional
     */
    Optional<Cuenta> buscarPorNumero(String numeroCuenta, Connection conn);

    /**
     * Actualizar saldo cuenta.
     *
     * @param cuentaId   the cuenta id
     * @param nuevoSaldo the nuevo saldo
     * @param conn       the conn
     * @return the cuenta
     */
    Cuenta actualizarSaldo(Long cuentaId, Double nuevoSaldo, Connection conn);
}
