package com.jlanzasg.novabank.negocio;

import com.jlanzasg.novabank.modelo.Cliente;
import com.jlanzasg.novabank.modelo.CuentaBancaria;
import com.jlanzasg.novabank.modelo.Movimiento;
import com.jlanzasg.novabank.modelo.TipoMovimiento;

import java.util.Collection;
import java.util.HashMap;
import java.util.Map;

// Clase Banco que almacena los HashMap con los clientes y las cuentas bancarias
public class Banco {

    // Variables
    private Map<Long, Cliente> clientes; // Map con la lista de clientes
    private Map<String, CuentaBancaria> cuentas; // Map con la lista de cuentas

    // Constructor que inicia los HashMaps
    public Banco() {
        clientes = new HashMap<>();
        cuentas = new HashMap<>();
    }

    // Getters
    public Map<Long, Cliente> getClientes() {
        return clientes;
    }

    public Map<String, CuentaBancaria> getCuentas() {
        return cuentas;
    }

    // Registro de clientes en el HashMap
    public void registrarCliente(Cliente cliente) {
        clientes.putIfAbsent(cliente.getId(), cliente);
    }

    // Registro de cuentas en el HashMap
    public void registrarCuenta(CuentaBancaria cuenta) {
        cuentas.putIfAbsent(cuenta.getIban(), cuenta);
    }

    public Collection<Cliente> buscarClientePorDni(String dni) {
        return clientes.values().stream().filter(cliente -> cliente.getDni().equals(dni)).toList();
    }

    public Collection<Cliente> buscarClientePorId(int id) {
        return clientes.values().stream().filter(cliente -> cliente.getId() == id).toList();
    }
}
