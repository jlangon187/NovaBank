package com.jlanzasg.novabank;

import java.util.HashMap;
import java.util.Map;

public class Banco {
    private Map<Long, Cliente> clientes;
    private Map<String, CuentaBancaria> cuentas;

    public Banco() {
        clientes = new HashMap<>();
        cuentas = new HashMap<>();
    }

    public Map<Long, Cliente> getClientes() {
        return clientes;
    }

    public Map<String, CuentaBancaria> getCuentas() {
        return cuentas;
    }

    public void registrarCliente(Cliente cliente) {
        clientes.putIfAbsent(cliente.getId(), cliente);
    }

    public void registrarCuenta(CuentaBancaria cuenta) {
        cuentas.putIfAbsent(cuenta.getIban(), cuenta);
    }

    public void realizarTransferencia(String ibanOrigen, String ibanDestino, Double cantidad) {
        if (!cuentas.containsKey(ibanOrigen)) {
            System.out.println("El IBAN de origen no existe");
            return;
        }
        if (!cuentas.containsKey(ibanDestino)) {
            System.out.println("El IBAN de destino no existe");
            return;
        }
        if (ibanOrigen.equals(ibanDestino)) {
            System.out.println("El IBAN de origen y el de destino no pueden ser los mismos");
            return;
        }
        if (cantidad <= 0) {
            System.out.println("El cantidad debe ser mayor a 0");
            return;
        }

        CuentaBancaria cuentaOrigen = cuentas.get(ibanOrigen);
        if (cuentaOrigen.getBalance() < cantidad) {
            System.out.println("Saldo insuficiente");
            return;
        }

        CuentaBancaria cuentaDestino = cuentas.get(ibanDestino);

        try {
            if (cuentaOrigen.retirar(ibanOrigen, cantidad)) {

                cuentaDestino.ingresar(ibanDestino, cantidad);

                Movimiento transferenciaOrigen = new Movimiento(ibanOrigen, TipoMovimiento.TRANSFERENCIA_SALIENTE.name(),  cantidad);
                cuentaOrigen.agregarMovimiento(transferenciaOrigen);
                Movimiento transferenciaDestino = new Movimiento(ibanDestino, TipoMovimiento.TRANSFERENCIA_ENTRANTE.name(),   cantidad);
                cuentaDestino.agregarMovimiento(transferenciaDestino);
                System.out.println("Transferencia realizada con éxito");
            }
        } catch (Exception e) {
            System.out.println("Error al realizar transferencia");
        }
    }
}
