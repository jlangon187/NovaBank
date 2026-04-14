package com.jlanzasg.novabank.repository.Impl;

import com.jlanzasg.novabank.config.DatabaseConnectionManager;
import com.jlanzasg.novabank.model.Movimiento;
import com.jlanzasg.novabank.repository.MovimientoRepository;

import java.sql.*;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

/**
 * The type Movimiento repository.
 */
public class MovimientoRepositoryImpl implements MovimientoRepository {

    @Override
    public Movimiento guardar(Movimiento movimiento) {
        try (Connection conn = DatabaseConnectionManager.getConexion()) {
            return guardar(movimiento, conn);
        } catch (SQLException e) {
            throw new RuntimeException("Error de conexión al guardar movimiento", e);
        }
    }

    @Override
    public Movimiento guardar(Movimiento movimiento, Connection conn) {
        String sql = "INSERT INTO movimientos (cuenta_id, tipo, cantidad) VALUES (?, ?, ?)";

        try (PreparedStatement stmt = conn.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {
            stmt.setLong(1, movimiento.getCuentaId());
            stmt.setString(2, movimiento.getTipo());
            stmt.setDouble(3, movimiento.getCantidad());

            stmt.executeUpdate();

            try (ResultSet rs = stmt.getGeneratedKeys()) {
                if (rs.next()) {
                    movimiento.setId(rs.getLong(1));
                }
            }
            return movimiento;
        } catch (SQLException e) {
            throw new RuntimeException("Error al guardar el movimiento en BD", e);
        }
    }

    @Override
    public List<Movimiento> buscarPorCuentaId(Long cuentaId) {
        String sql = "SELECT * FROM movimientos WHERE cuenta_id = ? ORDER BY fecha DESC";
        List<Movimiento> movimientos = new ArrayList<>();

        try (Connection conn = DatabaseConnectionManager.getConexion();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setLong(1, cuentaId);
            try (ResultSet rs = stmt.executeQuery()) {
                while (rs.next()) {
                    movimientos.add(mapearMovimiento(rs));
                }
            }
        } catch (SQLException e) {
            throw new RuntimeException("Error listando movimientos de la cuenta: " + cuentaId, e);
        }
        return movimientos;
    }

    @Override
    public List<Movimiento> buscarPorCuentaIdYFechas(Long cuentaId, LocalDateTime inicio, LocalDateTime fin) {
        return buscarPorCuentaId(cuentaId);
    }

    /**
     *
     * @param rs
     * @return
     * @throws SQLException
     */
    private Movimiento mapearMovimiento(ResultSet rs) throws SQLException {
        return Movimiento.builder()
                .id(rs.getLong("id"))
                .cuentaId(rs.getLong("cuenta_id"))
                .tipo(rs.getString("tipo"))
                .cantidad(rs.getDouble("cantidad"))
                .fecha(rs.getTimestamp("fecha").toLocalDateTime())
                .build();
    }
}

