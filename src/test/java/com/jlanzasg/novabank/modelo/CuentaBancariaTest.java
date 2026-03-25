package com.jlanzasg.novabank.modelo;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class CuentaBancariaTest {

    private Cliente cliente;
    private CuentaBancaria cuenta;

    @BeforeEach
    void setUp() {
        // Se ejecuta antes de cada test para tener un entorno limpio
        cliente = new Cliente("Juan", "Perez", "12345678A", "juan@test.com", "123456789");
        cuenta = new CuentaBancaria(cliente);
    }

    @Test
    void testIngresarCantidadPositiva() {
        cuenta.ingresar(150.0);
        assertEquals(150.0, cuenta.getBalance(), "El saldo debería ser 150.0 tras el ingreso");
    }

    @Test
    void testIngresarCantidadNegativa() {
        cuenta.ingresar(-50.0);
        assertEquals(0.0, cuenta.getBalance(), "El saldo no debe cambiar si se ingresa una cantidad negativa");
    }

    @Test
    void testRetirarConSaldoSuficiente() {
        cuenta.ingresar(200.0);
        boolean resultado = cuenta.retirar(50.0);

        assertTrue(resultado, "El retiro debería ser exitoso");
        assertEquals(150.0, cuenta.getBalance(), "El saldo restante debería ser 150.0");
    }

    @Test
    void testRetirarConSaldoInsuficiente() {
        cuenta.ingresar(50.0);
        boolean resultado = cuenta.retirar(100.0);

        assertFalse(resultado, "El retiro debería fallar por saldo insuficiente");
        assertEquals(50.0, cuenta.getBalance(), "El saldo no debería cambiar tras un retiro fallido");
    }

    @Test
    void testGenerarIban() {
        String iban = cuenta.getIban();
        assertNotNull(iban);
        assertTrue(iban.startsWith("ES91210000"), "El IBAN debe empezar con el prefijo correcto");
        assertEquals(22, iban.length(), "El IBAN debe tener 22 caracteres en total");
    }

    @Test
    void testRegistrarMovimiento() {
        Movimiento mov = new Movimiento(cuenta, TipoMovimiento.DEPOSITO.name(), 100.0);
        cuenta.registrarMovimiento(mov);

        assertFalse(cuenta.getMovimiento().isEmpty(), "La lista de movimientos no debería estar vacía");
        assertEquals(1, cuenta.getMovimiento().size());
        assertEquals(mov, cuenta.getMovimiento().get(mov.getId()));
    }
}