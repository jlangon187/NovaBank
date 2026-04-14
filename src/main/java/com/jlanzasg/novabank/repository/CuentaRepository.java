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
     * Buscar por numero optional.
     *
     * @param numeroCuenta the numero cuenta
     * @return the optional
     */
    Optional<Cuenta> buscarPorNumero(String numeroCuenta);

    /**
     * Buscar por id optional.
     * @param id
     * @return
     */
    Optional<Cuenta> buscarPorId(Long id);

    /**
     * Listar cuentas por cliente list.
     * @return
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
     * @return
     */
    Long obtenerUltimoId();
}
