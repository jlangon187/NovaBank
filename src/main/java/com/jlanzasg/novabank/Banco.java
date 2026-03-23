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

    public void registrarCuenta(CuentaBancaria cuenta){
        cuentas.putIfAbsent(cuenta.getIban(), cuenta);
    }
}
