package com.jlanzasg.novabank.utils;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class ValidacionTest {

    @Test
    void testEsEmailValido() {
        assertTrue(Validacion.esEmailValido("usuario@correo.com"));
        assertTrue(Validacion.esEmailValido("test.apellido@empresa.es"));

        assertFalse(Validacion.esEmailValido("usuariocorreo.com"));
        assertFalse(Validacion.esEmailValido("usuario@correo"));
        assertFalse(Validacion.esEmailValido(""));
    }

    @Test
    void testEsDniValido() {
        assertTrue(Validacion.esDniValido("12345678A"));

        assertFalse(Validacion.esDniValido("1234567A"));
        assertFalse(Validacion.esDniValido("123456789"));
        assertFalse(Validacion.esDniValido("ABCDEFGHJ"));
    }

    @Test
    void testEsTelefonoValido() {
        assertTrue(Validacion.esTelefonoValido("600123456"));
        assertTrue(Validacion.esTelefonoValido("600 12 34 56"));

        assertFalse(Validacion.esTelefonoValido("60012345"));
        assertFalse(Validacion.esTelefonoValido("600123456A"));
    }

    @Test
    void testEsFechaValida() {
        assertTrue(Validacion.esFechaValida("25/12/2023"));

        assertFalse(Validacion.esFechaValida("2023-12-25"));
        assertFalse(Validacion.esFechaValida("32/01/2023"));
    }
}