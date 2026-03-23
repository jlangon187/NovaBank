package com.jlanzasg.novabank;

import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.Map;

public class CuentaBancaria {

    private Cliente cliente;

    private String iban;
    private Double balance;
    private LocalDateTime fecha;
    private Map<Long, Movimiento> movimientos;

    private long numeroCuenta;
    private long contadorCuenta = 0L;

    public CuentaBancaria(Cliente cliente) {
        this.numeroCuenta = ++contadorCuenta;
        this.cliente = cliente;
        this.iban = generarIban();
        this.balance = 0.0;
        this.fecha = LocalDateTime.now();
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

    public String generarIban() {
        StringBuilder sb = new StringBuilder();
        String prefijo = "ES91210000";
        String numeroSecuencial = String.format("%012d", numeroCuenta);
        sb.append(prefijo);
        sb.append(numeroSecuencial);

        return sb.toString();
    }

    public void ingresar(String iban,  String tipo, Double cantidad) {
        if (!iban.equals(this.iban)) {
            System.out.println("El número de cuenta no existe");
            return;
        }
        if (cantidad <= 0) {
            System.out.println("El cantidad no puede ser menor o igual a 0");
            return;
        }
        this.balance += cantidad;
        Movimiento movimiento = new Movimiento(iban, tipo, cantidad);
        movimientos.putIfAbsent(movimiento.getId(), movimiento);
    }

    public boolean retirar(String iban, String tipo, Double cantidad) {
        if (!iban.equals(this.iban)) {
            System.out.println("El número de cuenta no existe");
            return false;
        }
        if (cantidad <= 0) {
            System.out.println("El cantidad no puede ser menor o igual a 0");
            return false;
        }
        if (this.balance < cantidad) {
            System.out.println("No hay saldo suficiente para retirar");
            return false;
        }
        this.balance -= cantidad;
        Movimiento movimiento = new Movimiento(iban, tipo, cantidad);
        movimientos.putIfAbsent(movimiento.getId(), movimiento);
        return true;
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
