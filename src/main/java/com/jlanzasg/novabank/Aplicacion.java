package com.jlanzasg.novabank;

public class Aplicacion {
    public static void main(String[] args) {

        Cliente cliente = new Cliente("Javier", "Torres", "12345678Z", "javi@mail.com", 666123456);
        CuentaBancaria cuentaBancaria = new CuentaBancaria(cliente);
        System.out.println(cliente.toString());
        System.out.println(cuentaBancaria.toString());
    }
}
