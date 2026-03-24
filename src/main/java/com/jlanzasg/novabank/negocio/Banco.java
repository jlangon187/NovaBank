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

    // Método con la verificación que hay que realizar antes de hacer la transferencia
    // 1 - Que las cuentas existan
    // 2 - Que la cuenta de origen y destino no sean las mismas
    // 3 - Que la cantidad a transferir sea mayor a 0 €
    // 4 - Que la cuenta de origen tenga el saldo suficiente para transferir
    public boolean esOperacionValida(String ibanOrigen, String ibanDestino, Double cantidad) {
        if (!cuentas.containsKey(ibanOrigen)) {
            System.out.println("El IBAN de origen no existe");
            return false;
        }
        if (!cuentas.containsKey(ibanDestino)) {
            System.out.println("El IBAN de destino no existe");
            return false;
        }
        if (ibanOrigen.equals(ibanDestino)) {
            System.out.println("El IBAN de origen y el de destino no pueden ser los mismos");
            return false;
        }
        if (cantidad <= 0) {
            System.out.println("El cantidad debe ser mayor a 0 €");
            return false;
        }
        CuentaBancaria cuentaOrigen = cuentas.get(ibanOrigen);
        if (cuentaOrigen.getBalance() < cantidad) {
            System.out.println("Saldo insuficiente");
            return false;
        }

        return true;
    }

    // Método que realiza la transferencia después de pasar la validación previa
    // Se mete en un try-catch por si tira alguna excepción, luego se obtienen los IBAN
    // y se procede a retirar primero el dinero de la cuenta de origen y si procede, se ingresa el en la cuenta de destino
    // Finalmente se crean los movimientos y se registran en el HashMap de las Cuenta Bancarias donde se han transferido el dinero
    public void realizarTransferencia(String ibanOrigen, String ibanDestino, Double cantidad) {
        try {
            if (esOperacionValida(ibanOrigen, ibanDestino, cantidad)) {
                CuentaBancaria cuentaOrigen = cuentas.get(ibanOrigen);
                CuentaBancaria cuentaDestino = cuentas.get(ibanDestino);
                if (cuentaOrigen.retirar(cantidad)) {
                    cuentaDestino.ingresar(cantidad);

                    Movimiento transferenciaOrigen = new Movimiento(cuentaOrigen, TipoMovimiento.TRANSFERENCIA_SALIENTE.name(), cantidad);
                    cuentaOrigen.registrarMovimiento(transferenciaOrigen);
                    Movimiento transferenciaDestino = new Movimiento(cuentaDestino, TipoMovimiento.TRANSFERENCIA_ENTRANTE.name(), cantidad);
                    cuentaDestino.registrarMovimiento(transferenciaDestino);
                    System.out.println("Transferencia realizada con éxito");
                }
            }
        } catch (RuntimeException e) {
            throw new RuntimeException("Error al realizar la transferencia");
        }
    }

    public Collection<Cliente> buscarClientePorDni(String dni) {
        return clientes.values().stream().filter(cliente -> cliente.getDni().equals(dni)).toList();
    }

    public Collection<Cliente> buscarClientePorId(int id) {
        return clientes.values().stream().filter(cliente -> cliente.getId() == id).toList();
    }
}
