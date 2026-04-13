package com.jlanzasg.novabank.view;

public class MenuCuentas {

    /*
    // Método para crear una cuenta para un cliente
    public void crearCuenta(Banco banco, String id) {
        try {
            if (banco.buscarClientePorId(Integer.parseInt(id)).isEmpty()) {
                throw new RuntimeException("El cliente no existe");
            }
            Cliente cliente = banco.buscarClientePorId(Integer.parseInt(id)).iterator().next();
            Cuenta cuenta = new Cuenta(cliente);
            banco.registrarCuenta(cuenta);
            System.out.println("La cuenta se ha creado correctamente");
        } catch (RuntimeException e) {
            throw new RuntimeException("No se ha podido crear la cuenta");
        }
    }

    // Método para listar las cuentas de un cliente
    public void listarCuentas(Banco banco, String id) {

        int idCliente = Integer.parseInt(id);
        var clientes = banco.buscarClientePorId(idCliente);

        if (clientes.isEmpty()) {
            System.out.println("El cliente no existe");
            return;
        }

        Cliente cliente = clientes.iterator().next();

        System.out.println("\nCuentas del cliente "
                + cliente.getNombre() + " " + cliente.getApellido() + ":");

        System.out.println("-------------------------------------------------------------");
        System.out.printf("%-26s | %-12s%n", "Número de cuenta", "Saldo");
        System.out.println("-------------------------------------------------------------");

        banco.getCuentas().values().stream()
                .filter(cuenta -> cuenta.getCliente().getId() == idCliente)
                .forEach(cuenta ->
                        System.out.printf("%-26s | %12.2f €%n",
                                cuenta.getIban(),
                                cuenta.getBalance()
                        )
                );
    }

    public void verCuenta(Banco banco, String iban) {

        if (!banco.getCuentas().containsKey(iban)) {
            System.out.println("La cuenta no existe.");
            return;
        }

        Cuenta cuenta = banco.getCuentas().get(iban);
        DateTimeFormatter formatoFecha = DateTimeFormatter.ofPattern("dd-MM-yyyy HH:mm:ss");

        System.out.println("\nNúmero de cuenta: " + cuenta.getIban());
        System.out.println("Titular: " + cuenta.getCliente().getNombre() + " " + cuenta.getCliente().getApellido());
        System.out.println("Saldo: " + cuenta.getBalance() + " €");
        System.out.println("Fecha de creación: " + cuenta.getFecha().format(formatoFecha));
    }*/
}
