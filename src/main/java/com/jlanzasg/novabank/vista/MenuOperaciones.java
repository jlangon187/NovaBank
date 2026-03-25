package com.jlanzasg.novabank.vista;

import com.jlanzasg.novabank.modelo.CuentaBancaria;
import com.jlanzasg.novabank.modelo.Movimiento;
import com.jlanzasg.novabank.modelo.TipoMovimiento;
import com.jlanzasg.novabank.negocio.Banco;

import java.util.Map;

public class MenuOperaciones {

    // Método para ingresar dinero a una cuenta
    public void ingresar(Banco banco, String iban, String cantidad) {
        Double cantidadDouble = Double.parseDouble(cantidad);
        CuentaBancaria cuenta = banco.getCuentas().get(iban);
        if (cuenta == null) {
            System.out.println("La cuenta no existe.");
            return;
        }
        try {
            cuenta.ingresar(cantidadDouble);
            Movimiento movimiento = new Movimiento(cuenta, TipoMovimiento.DEPOSITO.name(), cantidadDouble);
            cuenta.registrarMovimiento(movimiento);
            System.out.println("Depósito realizado correctamente.");
            System.out.println("Cuenta: " + cuenta.getIban());
            System.out.println("Importe: +" + cantidadDouble + " €");
            System.out.println("Nuevo saldo: " + cuenta.getBalance() + " €");
        } catch (RuntimeException e) {
            throw new RuntimeException("Error al ingresar el dinero.");
        }
    }

    // Método para retirar dinero de una cuenta
    public void retirar(Banco banco, String iban, String cantidad) {
        double cantidadDouble = Double.parseDouble(cantidad);
        CuentaBancaria cuenta = banco.getCuentas().get(iban);
        if (cuenta == null) {
            System.out.println("La cuenta no existe.");
            return;
        }
        try {
            if (cuenta.retirar(cantidadDouble)) {
                Movimiento movimiento = new Movimiento(cuenta, TipoMovimiento.RETIRO.name(), cantidadDouble);
                cuenta.registrarMovimiento(movimiento);
                System.out.println("Retiro realizado correctamente.");
                System.out.println("Cuenta: " + cuenta.getIban());
                System.out.println("Importe: -" + cantidadDouble + " €");
                System.out.println("Nuevo saldo: " + cuenta.getBalance() + " €");
            }
        } catch (RuntimeException e) {
            throw new RuntimeException("Error al retirar el dinero.");
        }
    }

    // Método con la verificación que hay que realizar antes de hacer la transferencia
    // 1 - Que las cuentas existan
    // 2 - Que la cuenta de origen y destino no sean las mismas
    // 3 - Que la cantidad a transferir sea mayor a 0 €
    // 4 - Que la cuenta de origen tenga el saldo suficiente para transferir
    public boolean esOperacionValida(Banco banco, String ibanOrigen, String ibanDestino, String cantidad) {
        double cantidadDouble = Double.parseDouble(cantidad);
        Map<String, CuentaBancaria> cuentas = banco.getCuentas();

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
        if (cantidadDouble <= 0) {
            System.out.println("El cantidad debe ser mayor a 0 €");
            return false;
        }
        CuentaBancaria cuentaOrigen = cuentas.get(ibanOrigen);
        if (cuentaOrigen.getBalance() < cantidadDouble) {
            System.out.println("Saldo insuficiente");
            return false;
        }

        return true;
    }

    // Método que realiza la transferencia después de pasar la validación previa
    // Se mete en un try-catch por si tira alguna excepción, luego se obtienen los IBAN
    // y se procede a retirar primero el dinero de la cuenta de origen y si procede, se ingresa el en la cuenta de destino
    // Finalmente se crean los movimientos y se registran en el HashMap de las Cuenta Bancarias donde se han transferido el dinero
    public void realizarTransferencia(Banco banco, String ibanOrigen, String ibanDestino, String cantidad) {
        try {
            double cantidadDouble = Double.parseDouble(cantidad);

            if (esOperacionValida(banco, ibanOrigen, ibanDestino, cantidad)) {

                Map<String, CuentaBancaria> cuentas = banco.getCuentas();
                CuentaBancaria cuentaOrigen = cuentas.get(ibanOrigen);
                CuentaBancaria cuentaDestino = cuentas.get(ibanDestino);

                if (cuentaOrigen.retirar(cantidadDouble)) {
                    cuentaDestino.ingresar(cantidadDouble);

                    Movimiento transferenciaOrigen = new Movimiento(cuentaOrigen, TipoMovimiento.TRANSFERENCIA_SALIENTE.name(), cantidadDouble);
                    cuentaOrigen.registrarMovimiento(transferenciaOrigen);

                    Movimiento transferenciaDestino = new Movimiento(cuentaDestino, TipoMovimiento.TRANSFERENCIA_ENTRANTE.name(), cantidadDouble);
                    cuentaDestino.registrarMovimiento(transferenciaDestino);

                    System.out.println("Transferencia realizada correctamente.");
                    System.out.println("Cuenta origen: " + ibanOrigen + " -> -" + cantidadDouble + " €");
                    System.out.println("Cuenta destino: " + ibanDestino + " -> +" + cantidadDouble + " €");
                } else {
                    System.out.println("Error: La operación fue cancelada porque falló el retiro en la cuenta origen.");
                }
            }
        } catch (RuntimeException e) {
            throw new RuntimeException("Error inesperado al realizar la transferencia");
        }
    }
}
