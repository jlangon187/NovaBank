package com.jlanzasg.novabank.utils;

import com.jlanzasg.novabank.service.Banco;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;
import java.util.Scanner;
import java.util.function.Predicate;

public class Validacion {

    public static String leerDato(Scanner sc, String mensaje, Predicate<String> validador, String mensajeError) {

        String entrada;

        do {
            System.out.print(mensaje);
            entrada = sc.nextLine();

            if (!validador.test(entrada)) {
                System.out.println(mensajeError);
            }

        } while (!validador.test(entrada));

        return entrada;
    }

    public static boolean esEntero(String entrada) {
        try {
            Integer.parseInt(entrada);
            return true;
        } catch (NumberFormatException e) {
            return false;
        }
    }

    private static boolean esDouble(String entrada) {
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
        return entrada != null
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
        try {
            DateTimeFormatter formatter = DateTimeFormatter.ofPattern("dd/MM/yyyy");
            LocalDate.parse(fecha, formatter);
            return true;
        } catch (DateTimeParseException e) {
            return false;
        }
    }

    public static String leerEntero(Scanner sc, String mensaje) {
        return leerDato(
                sc,
                mensaje,
                Validacion::esEntero,
                "Debe ingresar un número entero válido."
        );
    }

    public static String leerDouble(Scanner sc, String mensaje) {
        return leerDato(
                sc,
                mensaje,
                Validacion::esDouble,
                "Debe ingresar un número decimal."
        );
    }

    public static String leerNombre(Scanner sc, String mensaje) {
        return leerDato(sc, mensaje, s -> esTextoObligatorioSinNumeros(s) && esNombreValido(s),
                "Debe tener entre 2 y 50 caracteres y no contener números.");
    }

    public static String leerEmail(Scanner sc) {
        return leerDato(sc, "Email: ", Validacion::esEmailValido, "El formato de email no es correcto.");
    }

    public static String leerDni(Scanner sc, Banco banco, String mensaje) {
        return leerDato(sc, mensaje, dni -> {
                    String dniMayus = dni.toUpperCase();
                    return esDniValido(dniMayus) && banco.buscarClientePorDni(dniMayus).isEmpty();
                },
                "Formato incorrecto o DNI ya existente.").toUpperCase();
    }

    public static String leerTelefono(Scanner sc) {
        String telefono = leerDato(sc, "Teléfono: ", Validacion::esTelefonoValido,
                "Formato de teléfono incorrecto, debe tener 9 dígitos");
        return telefono.replaceAll("\\s+", "");
    }

    public static String leerIban(Scanner sc) {
        return leerDato(sc, "IBAN: ", Validacion::esIbanValido, "Formato de IBAN incorrecto.");
    }

    public static String leerFecha(Scanner sc, String mensaje) {
        return leerDato(
                sc,
                mensaje,
                Validacion::esFechaValida,
                "Formato de fecha incorrecto o fecha inexistente. Debe ser dd/MM/yyyy."
        );
    }
}
