package com.jlanzasg.novabank.service.Impl;

import com.jlanzasg.novabank.config.DatabaseConnectionManager;
import com.jlanzasg.novabank.model.Cuenta;
import com.jlanzasg.novabank.model.MovimientoFactory;
import com.jlanzasg.novabank.repository.CuentaRepository;
import com.jlanzasg.novabank.repository.MovimientoRepository;
import com.jlanzasg.novabank.service.OperacionService;

import java.sql.Connection;
import java.sql.SQLException;

/**
 * The type Operacion service.
 */
public class OperacionServiceImpl implements OperacionService {

    private final CuentaRepository cuentaRepository;
    private final MovimientoRepository movimientoRepository;

    /**
     * Instantiates a new Operacion service.
     *
     * @param cuentaRepository     the cuenta repository
     * @param movimientoRepository the movimiento repository
     */
    public OperacionServiceImpl(CuentaRepository cuentaRepository, MovimientoRepository movimientoRepository) {
        this.cuentaRepository = cuentaRepository;
        this.movimientoRepository = movimientoRepository;
    }

    @Override
    public void ingresar(String iban, Double cantidad) {
        if (cantidad == null || cantidad <= 0) {
            throw new IllegalArgumentException("La cantidad a ingresar debe ser mayor a cero.");
        }

        try (Connection conn = DatabaseConnectionManager.getConexion()) {
            conn.setAutoCommit(false);

            try {
                Cuenta cuenta = cuentaRepository.buscarPorNumero(iban, conn)
                        .orElseThrow(() -> new IllegalArgumentException("La cuenta no existe."));

                cuentaRepository.actualizarSaldo(cuenta.getId(), cuenta.getBalance() + cantidad, conn);

                movimientoRepository.guardar(
                        MovimientoFactory.crearDeposito(cuenta.getId(), cantidad), conn);

                conn.commit();
            } catch (Exception e) {
                conn.rollback();
                throw new RuntimeException("Error en el ingreso. Operación cancelada: " + e.getMessage(), e);
            }
        } catch (SQLException e) {
            throw new RuntimeException("Error de conexión: " + e.getMessage(), e);
        }
    }

    @Override
    public void retirar(String iban, Double cantidad) {
        if (cantidad == null || cantidad <= 0) {
            throw new IllegalArgumentException("La cantidad a retirar debe ser mayor a cero.");
        }

        try (Connection conn = DatabaseConnectionManager.getConexion()) {
            conn.setAutoCommit(false);

            try {
                Cuenta cuenta = cuentaRepository.buscarPorNumero(iban, conn)
                        .orElseThrow(() -> new IllegalArgumentException("La cuenta no existe."));

                if (cuenta.getBalance() < cantidad) {
                    throw new IllegalStateException("Saldo insuficiente.");
                }

                cuentaRepository.actualizarSaldo(cuenta.getId(), cuenta.getBalance() - cantidad, conn);

                movimientoRepository.guardar(
                        MovimientoFactory.crearRetiro(cuenta.getId(), cantidad), conn);

                conn.commit();
            } catch (Exception e) {
                conn.rollback();
                throw new RuntimeException("Error en el retiro. Operación cancelada: " + e.getMessage(), e);
            }
        } catch (SQLException e) {
            throw new RuntimeException("Error de conexión: " + e.getMessage(), e);
        }
    }

    @Override
    public void transferir(String ibanOrigen, String ibanDestino, Double cantidad) {
        if (cantidad == null || cantidad <= 0) {
            throw new IllegalArgumentException("La cantidad a transferir debe ser mayor a cero.");
        }
        if (ibanOrigen.equals(ibanDestino)) {
            throw new IllegalArgumentException("No puedes transferir dinero a la misma cuenta.");
        }

        try (Connection conn = DatabaseConnectionManager.getConexion()) {
            conn.setAutoCommit(false);

            try {
                Cuenta origen = cuentaRepository.buscarPorNumero(ibanOrigen, conn)
                        .orElseThrow(() -> new IllegalArgumentException("La cuenta origen no existe."));

                if (origen.getBalance() < cantidad) {
                    throw new IllegalStateException("Saldo insuficiente en la cuenta origen.");
                }

                Cuenta destino = cuentaRepository.buscarPorNumero(ibanDestino, conn)
                        .orElseThrow(() -> new IllegalArgumentException("La cuenta destino no existe."));

                cuentaRepository.actualizarSaldo(origen.getId(), origen.getBalance() - cantidad, conn);
                cuentaRepository.actualizarSaldo(destino.getId(), destino.getBalance() + cantidad, conn);

                movimientoRepository.guardar(
                        MovimientoFactory.crearTransferenciaSaliente(origen.getId(), cantidad), conn);
                movimientoRepository.guardar(
                        MovimientoFactory.crearTransferenciaEntrante(destino.getId(), cantidad), conn);

                conn.commit();
            } catch (Exception e) {
                conn.rollback();
                throw new RuntimeException("Error en la transferencia. Operación cancelada: " + e.getMessage(), e);
            }
        } catch (SQLException e) {
            throw new RuntimeException("Error crítico de base de datos: " + e.getMessage(), e);
        }
    }
}