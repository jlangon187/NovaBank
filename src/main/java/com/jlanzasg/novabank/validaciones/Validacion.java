package com.jlanzasg.novabank.validaciones;

import java.util.Scanner;

// Clase Validación
public class Validacion {

    // Método para validar si el carácter introducido es un número
    public static int validarInt (String entrada) {
        int valor;
        try {
            valor = Integer.parseInt(entrada);
            return valor;
        } catch (NumberFormatException e) {
            System.out.println("Debe ingresar un número entero");
        }
        return 0;
    }

    // Método para validar si el email introducido es correcto
    public static String isValidEmail() {
        String regex = "^[^@\\s]+@[^@\\s]+\\.[^@\\s]+$";

        System.out.print("Email: ");
        Scanner sc = new Scanner(System.in);
        String email = sc.nextLine();
        if (!email.matches(regex) || email.isEmpty()) {
            System.out.println("El formato de email no es correcto.");
            return isValidEmail();
        }
        return email;
    }
}
