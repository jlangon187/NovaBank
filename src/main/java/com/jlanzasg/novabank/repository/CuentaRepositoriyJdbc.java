package com.jlanzasg.novabank.repository;

import com.jlanzasg.novabank.model.Cuenta;

import java.math.BigDecimal;
import java.util.List;
import java.util.Optional;

/**
 * The type Cuenta repositoriy jdbc.
 */
public class CuentaRepositoriyJdbc implements CuentaRepository {

    @Override
    public Cuenta guardar(Cuenta cuenta) {
        return null;
    }

    @Override
    public Optional<Cuenta> buscarPorId(Long id) {
        return Optional.empty();
    }

    @Override
    public Optional<Cuenta> buscarPorNumero(String numeroCuenta) {
        return Optional.empty();
    }

    @Override
    public List<Cuenta> buscarPorClienteId(Long clienteId) {
        return List.of();
    }

    @Override
    public Cuenta actualizarSaldo(Long cuentaId, BigDecimal nuevoSaldo) {
        return null;
    }
}
