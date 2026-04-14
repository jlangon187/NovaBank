package com.jlanzasg.novabank.repository.Impl;

import com.jlanzasg.novabank.config.DatabaseConnectionManager;
import com.jlanzasg.novabank.model.Cliente;
import com.jlanzasg.novabank.repository.ClienteRepository;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

/**
 * The type Cliente repositoriy jdbc.
 */
public class ClienteRepositoryImpl implements ClienteRepository {

    @Override
    public Cliente guardar(Cliente cliente) {

        String sql = ("INSERT INTO clientes (nombre, apellidos, dni, email, telefono) VALUES (?, ?, ?, ?, ?)");

        try (Connection conexion = DatabaseConnectionManager.getConexion();
             PreparedStatement stmt = conexion.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {

            stmt.setString(1, cliente.getNombre());
            stmt.setString(2, cliente.getApellido());
            stmt.setString(3, cliente.getDni());
            stmt.setString(4, cliente.getEmail());
            stmt.setString(5, cliente.getTelefono());

            stmt.executeUpdate();
            try (ResultSet rs = stmt.getGeneratedKeys()) {
                if (rs.next()) {
                    Long id = rs.getLong(1);
                    cliente.setId(id);
                }
            }
        } catch (SQLException e) {
            throw new RuntimeException("Error al guardar el cliente con el DNI: " + cliente.getDni(), e);
        }
        return cliente;
    }

    @Override
    public Optional<Cliente> buscarPorId(Long id) {

        String sql = ("SELECT * FROM clientes WHERE id = ?");

        try (Connection conexion = DatabaseConnectionManager.getConexion();
             PreparedStatement stmt = conexion.prepareStatement(sql)) {
            stmt.setLong(1, id);

            try (ResultSet rs = stmt.executeQuery()) {
                if (rs.next()) {
                    return Optional.of(mapearCliente(rs));
                }
            }
            return Optional.empty();
        } catch (SQLException e) {
            throw new RuntimeException("No se ha encontrado el cliente con la ID: " + id, e);
        }
    }

    @Override
    public Optional<Cliente> buscarPorDni(String dni) {

        String sql = ("SELECT * FROM clientes WHERE dni = ?");

        try (Connection conexion = DatabaseConnectionManager.getConexion();
             PreparedStatement stmt = conexion.prepareStatement(sql)) {
            stmt.setString(1, dni);

            try (ResultSet rs = stmt.executeQuery()) {
                if (rs.next()) {
                    return Optional.of(mapearCliente(rs));
                }
            }
            return Optional.empty();
        } catch (SQLException e) {
            throw new RuntimeException("No se ha encontrado el cliente con el DNI: " + dni, e);
        }
    }

    @Override
    public List<Cliente> listarTodos() {
        List<Cliente> clientes = new ArrayList<>();
        try (Statement stmt = DatabaseConnectionManager.getConexion().createStatement();
             ResultSet rs = stmt.executeQuery("SELECT * FROM clientes")) {
            while (rs.next()) {
                clientes.add(mapearCliente(rs));
            }
        } catch (SQLException e) {
            throw new RuntimeException("No hay clientes encontrados", e);
        }
        return clientes;
    }

    @Override
    public void eliminar(Long id) {
        String sql = ("DELETE FROM clientes WHERE id = ?");

        try (Connection conexion = DatabaseConnectionManager.getConexion();
             PreparedStatement stmt = conexion.prepareStatement(sql)) {
            stmt.setLong(1, id);
            int filasEliminadas = stmt.executeUpdate();
            if (filasEliminadas == 0) {
                throw new IllegalArgumentException("No se ha encontrado el cliente con ID: " + id);
            }
        } catch (SQLException e) {
            throw new RuntimeException("No se ha podido eliminar el cliente con el ID: " + id, e);
        }
    }

    /**
     *
     * @param rs
     * @return
     * @throws SQLException
     */
    private Cliente mapearCliente(ResultSet rs) throws SQLException {
        Cliente cliente = Cliente.builder()
                .nombre(rs.getString("nombre"))
                .apellido(rs.getString("apellidos"))
                .dni(rs.getString("dni"))
                .email(rs.getString("email"))
                .telefono(rs.getString("telefono"))
                .build();

        cliente.setId(rs.getLong("id"));

        return cliente;
    }
}