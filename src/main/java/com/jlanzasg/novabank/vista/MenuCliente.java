package com.jlanzasg.novabank.vista;

import com.jlanzasg.novabank.modelo.Cliente;
import com.jlanzasg.novabank.negocio.Banco;

import java.util.Collection;
import java.util.Scanner;

public class MenuCliente {

    public void buscarPorDni(Banco banco) {
        Scanner sc = new Scanner(System.in);

        System.out.print("Introduzca el DNI del cliente que desea buscar:");
        String dniBuscar = sc.nextLine();
        Collection<Cliente> clientes = banco.buscarClientePorDni(dniBuscar);
        if (clientes.isEmpty()) {
            System.out.println("\nERROR: No se encontró ningún cliente con DNI " + dniBuscar);
        } else {
            System.out.println("\nCliente encontrado:");
            System.out.println("ID: " + clientes.iterator().next().getId());
            System.out.println("Nombre: " + clientes.iterator().next().getNombre() + " " + clientes.iterator().next().getApellido());
            System.out.println("DNI: " + clientes.iterator().next().getDni());
            System.out.println("Email: " + clientes.iterator().next().getEmail());
            System.out.println("Teléfono: " + clientes.iterator().next().getTelefono());
        }
    }
}
