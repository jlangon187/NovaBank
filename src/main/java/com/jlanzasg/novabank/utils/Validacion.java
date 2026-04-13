package com.jlanzasg.novabank.utils;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;

public class Validacion {

    public static boolean esEntero(String entrada) {
        if (entrada == null) return false;
        try {
            Integer.parseInt(entrada);
            return true;
        } catch (NumberFormatException e) {
            return false;
        }
    }

    public static boolean esLong(String entrada) {
        if (entrada == null) return false;
        try {
            Long.parseLong(entrada);
            return true;
        } catch (NumberFormatException e) {
            return false;
        }
    }

    public static boolean esDouble(String entrada) {
        if (entrada == null) return false;
        try {
            Double.parseDouble(entrada);
            return true;
        } catch (NumberFormatException e) {
            return false;
        }
    }

    public static boolean esTextoObligatorioSinNumeros(String entrada) {
        return entrada != null
                && !entrada.isBlank()
                && !entrada.chars().anyMatch(Character::isDigit);
    }

    public static boolean esNombreValido(String entrada) {
        return esTextoObligatorioSinNumeros(entrada)
                && entrada.length() >= 2
                && entrada.length() <= 50;
    }

    public static boolean esEmailValido(String email) {
        String regex = "^[^@\\s]+@[^@\\s]+\\.[^@\\s]+$";
        return email != null
                && !email.isBlank()
                && email.matches(regex);
    }

    public static boolean esDniValido(String dni) {
        String regex = "^[0-9]{8}[A-Za-z]$";
        return dni != null && dni.matches(regex);
    }

    public static boolean esTelefonoValido(String telefono) {
        if (telefono == null) return false;
        String limpio = telefono.replaceAll("\\s+", "");
        return limpio.matches("\\d{9}");
    }

    public static boolean esIbanValido(String iban) {
        if (iban == null) return false;
        iban = iban.replaceAll("\\s+", "").toUpperCase();
        return iban.matches("^ES\\d{20}$");
    }

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