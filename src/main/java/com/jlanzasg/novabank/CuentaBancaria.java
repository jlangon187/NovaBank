package com.jlanzasg.novabank;

import java.time.LocalDateTime;

public class CuentaBancaria {
    private Cliente cliente;
    private String iban;
    private Double balance;
    private LocalDateTime fecha;
    private long numeroCuenta;
    private long contadorCuenta = 0L;

    public CuentaBancaria(Cliente cliente) {
        this.numeroCuenta = ++contadorCuenta;
        this.cliente = cliente;
        this.iban = generarIban();
        this.balance = 0.0;
        this.fecha = LocalDateTime.now();
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

    public String generarIban() {
        StringBuilder sb = new StringBuilder();
        String prefijo = "ES91210000";
        String numeroSecuencial = String.format("%012d", numeroCuenta);
        sb.append(prefijo);
        sb.append(numeroSecuencial);

        return sb.toString();
    }

    @Override
    public String toString() {
        return "CuentaBancaria{" +
                "cliente=" + cliente +
                ", iban='" + iban + '\'' +
                ", balance=" + balance +
                ", fecha=" + fecha +
                '}';
    }
}
