package com.jlanzasg.novabank.modelo;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.HashMap;
import java.util.Map;

// Clase CuentaBancaria que maneja operaciones en la cuenta bancaria como los movimientos de la cuenta
public class CuentaBancaria {

    // Variables
    private Cliente cliente; // Variable Cliente para pasársela al constructor
    private String iban;
    private Double balance;
    private LocalDateTime fecha;
    private long numeroCuenta;
    private static long contadorCuenta = 0L; // contador para el ID de la cuenta
    private Map<Long, Movimiento> movimientos; // Map con la lista de movimientos
    private static final DateTimeFormatter FORMATO_FECHA = DateTimeFormatter.ofPattern("dd/MM/yyyy HH:mm:ss");

    public CuentaBancaria(Cliente cliente) {
        this.numeroCuenta = ++contadorCuenta; // Se incrementa el ID con el contador
        this.cliente = cliente;
        this.iban = generarIban(); // Se genera el IBAN a través del generador
        this.balance = 0.0; // Se inicia la cuenta con un saldo de 0 €
        this.fecha = LocalDateTime.now(); // Se crea la cuenta con la fecha y hora actual
        this.movimientos = new HashMap<>();
    }

    public String getIban() {
        return iban;
    }

    public Double getBalance() {
        return balance;
    }

    public LocalDateTime getFecha() {
        return fecha;
    }

    public Map<Long, Movimiento> getMovimiento() {
        return movimientos;
    }

    public Cliente getCliente() { return cliente; }

    // Registro de movimientos en el HashMap
    public void registrarMovimiento(Movimiento movimiento) {
        movimientos.putIfAbsent(movimiento.getId(), movimiento);
    }

    // Método para generar el IBAN con StringBuilder pasandole primero el prefijo ES91210000 y luego usando la variable
    // del número de cuenta y rellenándolo con ceros hasta tener 12 dígitos y concatenando la cadena.
    public String generarIban() {
        StringBuilder sb = new StringBuilder();
        String prefijo = "ES91210000";
        String numeroSecuencial = String.format("%012d", numeroCuenta);
        sb.append(prefijo);
        sb.append(numeroSecuencial);

        return sb.toString();
    }

    // Método para validar si la cantidad a operar no es menor o igual a cero
    public boolean cantidadNoZero(Double cantidad) {
        if (cantidad <= 0) {
            System.out.println("El cantidad no puede ser menor o igual a 0");
            return false;
        }
        return true;
    }

    // Método para ingresar el dinero
    public void ingresar(Double cantidad) {
        if (cantidadNoZero(cantidad)) {
            this.balance += cantidad;
        }
    }

    // Método para retirar el dinero que verifica tambien si en la cuenta hay saldo suficiente
    public boolean retirar(Double cantidad) {
        if (cantidadNoZero(cantidad)) {
            return false;
        }
        if (this.balance < cantidad) {
            System.out.println("ERROR: Saldo insuficiente.");
            System.out.println("Saldo disponible: " + this.balance + " €");
            System.out.println("Importe solicitado: " + cantidad + " €");
            return false;
        }
        this.balance -= cantidad;
        return true;
    }

    @Override
    public String toString() {
        return "CuentaBancaria{" +
                "cliente=" + cliente +
                ", iban='" + iban + '\'' +
                ", balance=" + balance +
                ", fecha=" + fecha.format(FORMATO_FECHA) +
                '}';
    }
}
