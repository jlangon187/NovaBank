package com.jlanzasg.novabank;

public class Aplicacion {
    public static void main(String[] args) {

        Banco banco = new Banco();

        Cliente cliente = new Cliente("Javier", "Torres", "12345678Z", "javi@mail.com", 666123456);
        CuentaBancaria cuentaBancaria = new CuentaBancaria(cliente);
        banco.registrarCuenta(cuentaBancaria);
        banco.registrarCliente(cliente);

        System.out.println(cliente.toString());
        System.out.println(cuentaBancaria.toString());

        System.out.println(banco.getClientes().toString());
        System.out.println(banco.getCuentas().toString());

        cuentaBancaria.ingresar("ES91210000000000000001", TipoMovimiento.DEPOSITO.name(), 3000.0);
        System.out.println(cuentaBancaria.getBalance());

        System.out.println(cuentaBancaria.getMovimiento());


        cuentaBancaria.retirar("ES91210000000000000001", TipoMovimiento.RETIRO.name(), 1500.0);
        System.out.println(cuentaBancaria.getBalance());

        System.out.println(cuentaBancaria.getMovimiento());
    }
}
