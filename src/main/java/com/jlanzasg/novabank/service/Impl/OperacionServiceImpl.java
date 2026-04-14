package com.jlanzasg.novabank.service.Impl;

import com.jlanzasg.novabank.service.OperacionService;

public class OperacionServiceImpl implements OperacionService {


    // Método para ingresar el dinero
    public void ingresar(Double cantidad) {
//        if (cantidadNoZero(cantidad)) {
//            this.balance += cantidad;
//        }
    }

    // Método para retirar el dinero que verifica tambien si en la cuenta hay saldo suficiente
    public void retirar(Double cantidad) {
//        if (!cantidadNoZero(cantidad)) {
//            return false;
//        }
//        if (this.balance < cantidad) {
//            System.out.println("ERROR: Saldo insuficiente.");
//            System.out.println("Saldo disponible: " + this.balance + " €");
//            System.out.println("Importe solicitado: " + cantidad + " €");
//            return false;
//        }
//        this.balance -= cantidad;
    }

    @Override
    public void transferir(Double cantidad, String ibanOrigen, String ibanDestino) {

    }
}
