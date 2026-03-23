package com.jlanzasg.novabank;

import java.time.LocalDateTime;

public class Movimiento {
    private long id;
    private String iban;
    private String tipo;
    private double cantidad;
    private LocalDateTime fecha;
    private static long contadorMovimiento = 0L;

    public Movimiento(String iban, String tipo, double cantidad) {
        this.id = ++contadorMovimiento;
        this.iban = iban;
        this.tipo = tipo;
        this.cantidad = cantidad;
        this.fecha = LocalDateTime.now();
    }

    public long getId() { return id; }

    public void setId(long id) {}

    public String getIban() { return iban; }

    public void setIban(String iban) {
        this.iban = iban;
    }

    public String getTipo() {
        return tipo;
    }

    public void setTipo(String tipo) {
        this.tipo = tipo;
    }

    public double getCantidad() {
        return cantidad;
    }

    public void setCantidad(double cantidad) {
        this.cantidad = cantidad;
    }

    public LocalDateTime getFecha() { return fecha; }

    @Override
    public String toString() {
        return "Movimiento{" +
                "id=" + id +
                ", iban='" + iban + '\'' +
                ", tipo='" + tipo + '\'' +
                ", cantidad=" + cantidad +
                ", fecha=" + fecha +
                '}';
    }
}
