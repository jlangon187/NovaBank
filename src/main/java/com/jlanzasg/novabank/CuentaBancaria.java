package com.jlanzasg.novabank;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class CuentaBancaria {

    private Cliente cliente;

    private String iban;
    private Double balance;
    private LocalDateTime fecha;
    private Map<Long, Movimiento> movimientos;

    private long numeroCuenta;
    private static long contadorCuenta = 0L;

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

    public void agregarMovimiento(Movimiento movimiento) {
        movimientos.putIfAbsent(movimiento.getId(), movimiento);
    }

    public String generarIban() {
        StringBuilder sb = new StringBuilder();
        String prefijo = "ES91210000";
        String numeroSecuencial = String.format("%012d", numeroCuenta);
        sb.append(prefijo);
        sb.append(numeroSecuencial);

        return sb.toString();
    }

    public void ingresar(String iban, Double cantidad) {
        if (!iban.equals(this.iban)) {
            System.out.println("El número de cuenta no existe");
            return;
        }
        if (cantidad <= 0) {
            System.out.println("El cantidad no puede ser menor o igual a 0");
            return;
        }
        this.balance += cantidad;
        System.out.println("Depósito realizado correctamente");
    }

    public boolean retirar(String iban, Double cantidad) {
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
        System.out.println("Retiro realizado correctamente");
        return true;
    }

    public String consultarSaldo() {
        return "Saldo: " + this.balance + " €";
    }

    public List<Movimiento> consultarMovimientosPorFecha(LocalDate inicio, LocalDate fin) {
        List<Movimiento> movimientosPorFecha = new ArrayList<>();

        if (fin.isBefore(inicio)) {
            System.out.println("La fecha de fin no puede ser anterior a la de inicio");
            return null;
        }
        if (inicio.isAfter(fin)) {
            System.out.println("La fecha de inicio no puede ser posterior a la de fin");
            return null;
        }
//        for (Map.Entry<Long, Movimiento> entry : movimientos.entrySet()) {
//            if (inicio.isAfter(entry.getValue().getFecha().toLocalDate())) {
//                movimientosPorFecha.add(entry.getValue());
//                System.out.println(entry.getValue().getFecha());
//            }
//        }
        return movimientosPorFecha;
    }

    @Override
    public String toString() {

        DateTimeFormatter dtf = DateTimeFormatter.ofPattern("dd/MM/yyyy HH:mm:ss");
        String formato = dtf.format(fecha);

        return "CuentaBancaria{" +
                "cliente=" + cliente +
                ", iban='" + iban + '\'' +
                ", balance=" + balance +
                ", fecha=" + formato +
                '}';
    }
}
