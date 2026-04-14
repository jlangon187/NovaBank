package com.jlanzasg.novabank.repository;

import com.jlanzasg.novabank.config.DatabaseConnectionManager;
import com.jlanzasg.novabank.model.Cliente;
import com.jlanzasg.novabank.model.Cuenta;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

/**
 * The type Cuenta repositoriy jdbc.
 */
public class CuentaRepositoryJdbc implements CuentaRepository {

    @Override
    public Cuenta guardar(Cuenta cuenta) {
        String sql = "INSERT INTO cuentas (numero_cuenta, cliente_id, saldo) VALUES (?, ?, ?)";

        try (Connection conexion = DatabaseConnectionManager.getConexion();
             PreparedStatement stmt = conexion.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {

            stmt.setString(1, cuenta.getIban());
            stmt.setLong(2, cuenta.getCliente().getId());
            stmt.setDouble(3, cuenta.getBalance());

            stmt.executeUpdate();

            try (ResultSet rs = stmt.getGeneratedKeys()) {
                if (rs.next()) {
                    cuenta.setId(rs.getLong(1));
                }
            }
        } catch (SQLException e) {
            throw new RuntimeException("Error al guardar la cuenta.", e);
        }
        return cuenta;
    }

    @Override
    public Optional<Cuenta> buscarPorNumero(String iban) {
        String sql = "SELECT cu.*, cl.nombre, cl.apellidos " +
                "FROM cuentas cu " +
                "INNER JOIN clientes cl ON cu.cliente_id = cl.id " +
                "WHERE cu.numero_cuenta = ?";

        try (Connection conexion = DatabaseConnectionManager.getConexion();
             PreparedStatement stmt = conexion.prepareStatement(sql)) {

            stmt.setString(1, iban);

            try (ResultSet rs = stmt.executeQuery()) {
                if (rs.next()) {
                    return Optional.of(mapearCuenta(rs));
                }
            }
            return Optional.empty();
        } catch (SQLException e) {
            throw new RuntimeException("Error buscando la cuenta con IBAN: " + iban, e);
        }
    }

    @Override
    public Optional<Cuenta> buscarPorId(Long id) {
        String sql = ("SELECT * FROM cuentas WHERE cliente_id = ?");

        try (Connection conexion = DatabaseConnectionManager.getConexion();
             PreparedStatement stmt = conexion.prepareStatement(sql)) {
            stmt.setLong(1, id);

            try (ResultSet rs = stmt.executeQuery()) {
                if (rs.next()) {
                    return Optional.of(mapearCuenta(rs));
                }
            }
            return Optional.empty();
        } catch (SQLException e) {
            throw new RuntimeException("No se ha encontrado la cuenta del cliente con la ID : " + id, e);
        }
    }

    @Override
    public List<Cuenta> buscarPorClienteId(Long idCliente) {
        String sql = "SELECT cu.*, cl.nombre, cl.apellidos " +
                "FROM cuentas cu " +
                "INNER JOIN clientes cl ON cu.cliente_id = cl.id " +
                "WHERE cu.cliente_id = ?";
        List<Cuenta> cuentas = new ArrayList<>();

        try (Connection conexion = DatabaseConnectionManager.getConexion();
             PreparedStatement stmt = conexion.prepareStatement(sql)) {

            stmt.setLong(1, idCliente);

            try (ResultSet rs = stmt.executeQuery()) {
                while (rs.next()) {
                    cuentas.add(mapearCuenta(rs));
                }
            }
        } catch (SQLException e) {
            throw new RuntimeException("Error listando las cuentas del cliente ID: " + idCliente, e);
        }
        return cuentas;
    }

    @Override
    public Cuenta actualizarSaldo(Long cuentaId, Double nuevoSaldo) {
        String sql = ("UPDATE cuentas SET saldo = ? WHERE id = ?");

        try (Connection conexion = DatabaseConnectionManager.getConexion();
             PreparedStatement stmt = conexion.prepareStatement(sql)) {
            stmt.setDouble(1, nuevoSaldo);
            stmt.setLong(2, cuentaId);
            stmt.executeUpdate();
            return buscarPorNumero(cuentaId.toString()).get();
        } catch (SQLException e) {
            throw new RuntimeException("No se ha podido actualizar el saldo de la cuenta", e);
        }
    }

    @Override
    public Long obtenerUltimoId() {
        try (Statement stmt = DatabaseConnectionManager.getConexion().createStatement();
             ResultSet rs = stmt.executeQuery("SELECT MAX(id) FROM cuentas")) {
            if (rs.next()) {
                return rs.getLong(1);
            }
        } catch (SQLException e) {
            throw new RuntimeException("No se ha podido obtener el ultimo id de la cuenta", e);
        }
        return 0L;
    }

    /**
     * Mapea el resultado de la consulta a un objeto Cuenta
     * @param rs
     * @return
     * @throws SQLException
     */
    private Cuenta mapearCuenta(ResultSet rs) throws SQLException {

        Cliente cliente = Cliente.builder()
                .id(rs.getLong("cliente_id"))
                .nombre(rs.getString("nombre"))
                .apellido(rs.getString("apellidos"))
                .build();

        return Cuenta.builder()
                .id(rs.getLong("id"))
                .iban(rs.getString("numero_cuenta"))
                .cliente(cliente)
                .balance(rs.getDouble("saldo"))
                .build();
    }
}
