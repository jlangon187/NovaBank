package com.jlanzasg.novabank.model;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

// Clase Movimiento
public class Movimiento {

    // Variables
    private CuentaBancaria cuentaBancaria; // Variable CuentaBancaria para usar su (.getIban) como iban
    private long id;
    private String tipo;
    private double cantidad;
    private LocalDateTime fecha;
    private static long contadorMovimiento = 0L; // contador para el ID de movimientos
    private static final DateTimeFormatter FORMATO_FECHA = DateTimeFormatter.ofPattern("dd/MM/yyyy HH:mm:ss");

    // Constructor
    public Movimiento(CuentaBancaria cuentaBancaria, String tipo, double cantidad) {
        this.id = ++contadorMovimiento; // Se incrementa el ID con el contador
        this.cuentaBancaria = cuentaBancaria;
        this.tipo = tipo;
        this.cantidad = cantidad;
        fecha = LocalDateTime.now(); // Se crea el movimiento con la fecha y hora actual
    }

    public long getId() { return id; }

    public CuentaBancaria getCuenta() { return cuentaBancaria; }

    public String getTipo() {
        return tipo;
    }

    public double getCantidad() {
        return cantidad;
    }

    public LocalDateTime getFecha() { return fecha; }

    @Override
    public String toString() {

        return "Movimiento{" +
                "id=" + id +
                ", iban='" + cuentaBancaria.getIban() + '\'' +
                ", tipo='" + tipo + '\'' +
                ", cantidad=" + cantidad +
                ", fecha=" + fecha.format(FORMATO_FECHA) +
                '}';
    }
}
