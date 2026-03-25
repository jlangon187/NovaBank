package com.jlanzasg.novabank.negocio;

import com.jlanzasg.novabank.modelo.Cliente;
import com.jlanzasg.novabank.modelo.CuentaBancaria;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.Collection;

import static org.junit.jupiter.api.Assertions.*;

class BancoTest {

    private Banco banco;

    @BeforeEach
    void setUp() {
        banco = new Banco();
    }

    @Test
    void testRegistrarYBuscarClientePorDni() {
        Cliente cliente = new Cliente("Ana", "Gomez", "87654321B", "ana@test.com", "987654321");
        banco.registrarCliente(cliente);

        Collection<Cliente> resultado = banco.buscarClientePorDni("87654321B");

        assertFalse(resultado.isEmpty(), "Debería encontrar al cliente");
        assertEquals(1, resultado.size());
        assertEquals("Ana", resultado.iterator().next().getNombre());
    }

    @Test
    void testRegistrarCuenta() {
        Cliente cliente = new Cliente("Luis", "Martinez", "11223344C", "luis@test.com", "666555444");
        CuentaBancaria cuenta = new CuentaBancaria(cliente);

        banco.registrarCuenta(cuenta);

        assertTrue(banco.getCuentas().containsKey(cuenta.getIban()), "La cuenta debería estar registrada en el banco");
        assertEquals(cuenta, banco.getCuentas().get(cuenta.getIban()));
    }
}