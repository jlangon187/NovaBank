package com.jlanzasg.novabank.model;

import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;

class MovimientoFactoryTest {

    @Test
    void crearDeposito_creaMovimientoCorrectamente() {
        Movimiento mov = MovimientoFactory.crearDeposito(1L, 150.0);

        assertEquals(1L, mov.getCuentaId());
        assertEquals(150.0, mov.getCantidad());
        assertEquals("DEPOSITO", mov.getTipo());
        assertNotNull(mov.getFecha());
    }

    @Test
    void crearTransferenciaSaliente_creaMovimientoCorrectamente() {
        Movimiento mov = MovimientoFactory.crearTransferenciaSaliente(2L, 500.0);

        assertEquals(2L, mov.getCuentaId());
        assertEquals(500.0, mov.getCantidad());
        assertEquals("TRANSFERENCIA_SALIENTE", mov.getTipo());
    }
}