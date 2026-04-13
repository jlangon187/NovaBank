package com.jlanzasg.novabank.view;

import com.jlanzasg.novabank.model.Cliente;
import com.jlanzasg.novabank.model.Cuenta;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class MenuOperacionesTest {

    private MenuOperaciones menuOperaciones;
    private Cuenta cuentaOrigen;
    private Cuenta cuentaDestino;

    @BeforeEach
    void setUp() {
        menuOperaciones = new MenuOperaciones();

        //Cliente c1 = new Cliente("Emisor", "A", "11111111A", "a@a.com", "111111111");
        //Cliente c2 = new Cliente("Receptor", "B", "22222222B", "b@b.com", "222222222");

        //cuentaOrigen = new Cuenta(c1);
        //cuentaDestino = new Cuenta(c2);

        //banco.registrarCuenta(cuentaOrigen);
        //banco.registrarCuenta(cuentaDestino);

        //menuOperaciones.ingresar(banco, cuentaOrigen.getIban(), "500.0");
    }

    @Test
    void testEsOperacionValidaExitosa() {
        //boolean valida = menuOperaciones.esOperacionValida(banco, cuentaOrigen.getIban(), cuentaDestino.getIban(), "200.0");
        //assertTrue(valida, "La operación debería ser válida si hay saldo y las cuentas existen");
    }

    @Test
    void testEsOperacionValidaSaldoInsuficiente() {
        //boolean valida = menuOperaciones.esOperacionValida(banco, cuentaOrigen.getIban(), cuentaDestino.getIban(), "1000.0");
        //assertFalse(valida, "La operación no debe ser válida si no hay saldo suficiente");
    }

    @Test
    void testRealizarTransferencia() {
        //menuOperaciones.realizarTransferencia(banco, cuentaOrigen.getIban(), cuentaDestino.getIban(), "150.0");

        //assertEquals(350.0, cuentaOrigen.getBalance(), "Se debieron descontar 150.0 al emisor");
//        assertEquals(150.0, cuentaDestino.getBalance(), "Se debieron sumar 150.0 al receptor");
//
//        assertEquals(2, cuentaOrigen.getMovimiento().size(), "Debe haber 2 movimientos: el de ingreso y el de la transferencia de salida");
//        assertEquals(1, cuentaDestino.getMovimiento().size(), "Debe haber 1 movimiento: la transferencia de entrada");
    }
}