package com.jlanzasg.novabank.service;

import com.jlanzasg.novabank.model.Cuenta;

import java.util.List;
import java.util.Optional;

/**
 * The interface Cuenta service.
 */
public interface CuentaService {
    /**
     * Crear cuenta.
     *
     * @param clienteId the cliente id
     * @return the cuenta
     */
    Cuenta crearCuenta(Long clienteId);

    /**
     * Consultar cuentas de cliente list.
     *
     * @param clienteId the cliente id
     * @return the list
     */
    List<Cuenta> consultarCuentasDeCliente(Long clienteId);

    /**
     * Consultar cuenta optional.
     *
     * @param iban the iban
     * @return the optional
     */
    Optional<Cuenta> consultarCuenta(String iban);
}
