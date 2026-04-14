package com.jlanzasg.novabank.utils;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;

/**
 * The type Validacion.
 */
public class Validacion {

    /**
     * Es texto obligatorio sin numeros boolean.
     *
     * @param entrada the entrada
     * @return the boolean
     */
    public static boolean esTextoObligatorioSinNumeros(String entrada) {
        return entrada != null
                && !entrada.isBlank()
                && !entrada.chars().anyMatch(Character::isDigit);
    }

    /**
     * Es nombre valido boolean.
     *
     * @param entrada the entrada
     * @return the boolean
     */
    public static boolean esNombreValido(String entrada) {
        return esTextoObligatorioSinNumeros(entrada)
                && entrada.length() >= 2
                && entrada.length() <= 50;
    }

    /**
     * Es email valido boolean.
     *
     * @param email the email
     * @return the boolean
     */
    public static boolean esEmailValido(String email) {
        String regex = "^[^@\\s]+@[^@\\s]+\\.[^@\\s]+$";
        return email != null
                && !email.isBlank()
                && email.matches(regex);
    }

    /**
     * Es dni valido boolean.
     *
     * @param dni the dni
     * @return the boolean
     */
    public static boolean esDniValido(String dni) {
        String regex = "^[0-9]{8}[A-Za-z]$";
        return dni != null && dni.matches(regex);
    }

    /**
     * Es telefono valido boolean.
     *
     * @param telefono the telefono
     * @return the boolean
     */
    public static boolean esTelefonoValido(String telefono) {
        if (telefono == null) return false;
        String limpio = telefono.replaceAll("\\s+", "");
        return limpio.matches("\\d{9}");
    }

    /**
     * Es iban valido boolean.
     *
     * @param iban the iban
     * @return the boolean
     */
    public static boolean esIbanValido(String iban) {
        if (iban == null) return false;
        iban = iban.replaceAll("\\s+", "").toUpperCase();
        return iban.matches("^ES\\d{20}$");
    }

    /**
     * Es fecha valida boolean.
     *
     * @param fecha the fecha
     * @return the boolean
     */
    public static boolean esFechaValida(String fecha) {
        if (fecha == null) return false;
        try {
            DateTimeFormatter formatter = DateTimeFormatter.ofPattern("dd/MM/yyyy");
            LocalDate.parse(fecha, formatter);
            return true;
        } catch (DateTimeParseException e) {
            return false;
        }
    }
}