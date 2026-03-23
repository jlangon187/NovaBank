package com.jlanzasg.novabank;

public class Aplicacion {
    public static void main(String[] args) {

        Banco banco = new Banco();

        Cliente cliente = new Cliente("Javier", "Torres", "12345678Z", "javi@mail.com", 666123456);
        CuentaBancaria cuentaBancaria = new CuentaBancaria(cliente);
        banco.registrarCuenta(cuentaBancaria);
        banco.registrarCliente(cliente);

        Cliente cliente2 = new Cliente("Maria", "Perez", "12345678X", "maria@mail.com", 666123456);
        CuentaBancaria cuentaBancaria2 = new CuentaBancaria(cliente2);
        banco.registrarCuenta(cuentaBancaria2);
        banco.registrarCliente(cliente2);

        System.out.println(cliente.toString());
        System.out.println(cuentaBancaria.toString());

        System.out.println(cliente2.toString());
        System.out.println(cuentaBancaria2.toString());

        System.out.println(banco.getClientes().toString());
        System.out.println(banco.getCuentas().toString());

        cuentaBancaria.ingresar(cuentaBancaria.getIban(), 3000.0);
        Movimiento movimiento = new Movimiento(cuentaBancaria.getIban(), TipoMovimiento.DEPOSITO.name(), 3000.0);
        cuentaBancaria.agregarMovimiento(movimiento);
        System.out.println(cuentaBancaria.getBalance());
        System.out.println(cuentaBancaria.getMovimiento());

        cuentaBancaria2.ingresar(cuentaBancaria2.getIban(), 3000.0);
        Movimiento movimiento1 = new Movimiento(cuentaBancaria2.getIban(),  TipoMovimiento.DEPOSITO.name(),  3000.0);
        cuentaBancaria2.agregarMovimiento(movimiento1);
        System.out.println(cuentaBancaria2.getBalance());
        System.out.println(cuentaBancaria2.getMovimiento());

        System.out.println("------------------------------------");

        banco.realizarTransferencia(cuentaBancaria.getIban(), cuentaBancaria2.getIban(), 500.0);
        System.out.println(cuentaBancaria.getMovimiento());
        System.out.println(cuentaBancaria2.getMovimiento());

        System.out.println(cuentaBancaria.consultarSaldo());
        System.out.println(cuentaBancaria2.consultarSaldo());
    }
}
