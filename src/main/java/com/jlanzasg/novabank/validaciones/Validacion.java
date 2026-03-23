package com.jlanzasg.novabank.validaciones;

// Clase Validación
public class Validacion {

    // Metodo para validar si el caracter introducido es un número
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
}
