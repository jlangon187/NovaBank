package com.jlanzasg.novabank.repository;

import com.jlanzasg.novabank.config.DatabaseConnectionManager;
import com.jlanzasg.novabank.model.Cliente;
import com.jlanzasg.novabank.repository.Impl.ClienteRepositoryImpl;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.sql.Connection;
import java.sql.Statement;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;

class ClienteRepositoryImplTest {

    private ClienteRepository repository;

    @BeforeEach
    void setUp() {
        repository = new ClienteRepositoryImpl();
        try (Connection conn = DatabaseConnectionManager.getConexion();
             Statement stmt = conn.createStatement()) {
            stmt.execute("DELETE FROM movimientos");
            stmt.execute("DELETE FROM cuentas");
            stmt.execute("DELETE FROM clientes");
        } catch (Exception e) {
            System.out.println("Error limpiando la base de datos de test: " + e.getMessage());
        }
    }

    @Test
    void guardar_clienteValido_asignaIdYPersisteEnPostgreSQL() {
        Cliente nuevoCliente = Cliente.builder()
                .nombre("Laura")
                .apellido("Gomez")
                .dni("11111111X")
                .email("laura@test.com")
                .telefono("600000000")
                .build();

        Cliente guardado = repository.guardar(nuevoCliente);

        assertNotNull(guardado.getId(), "El ID debería haber sido generado por PostgreSQL");
        assertEquals("11111111X", guardado.getDni());

        Optional<Cliente> recuperado = repository.buscarPorId(guardado.getId());
        assertTrue(recuperado.isPresent());
        assertEquals("Laura", recuperado.get().getNombre());
    }

    @Test
    void buscarPorDni_clienteNoExiste_devuelveVacio() {
        Optional<Cliente> resultado = repository.buscarPorDni("99999999Z");

        assertTrue(resultado.isEmpty());
    }
}