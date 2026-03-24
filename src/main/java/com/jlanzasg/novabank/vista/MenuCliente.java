package com.jlanzasg.novabank.vista;

import com.jlanzasg.novabank.modelo.Cliente;
import com.jlanzasg.novabank.negocio.Banco;
import com.jlanzasg.novabank.validaciones.Validacion;

import java.util.Collection;
import java.util.Scanner;

public class MenuCliente {

    public static void altaCliente(Banco banco) {
        Scanner sc = new Scanner(System.in);
        System.out.println("Introduzca los datos del cliente:");
        System.out.print("Nombre: ");
        String nombre = sc.nextLine();
        System.out.print("Apellido: ");
        String apellido = sc.nextLine();
        String dni = Validacion.leerDni(sc, banco);
        String email = Validacion.leerEmail(sc);
        System.out.print("Teléfono:");
        String telefono = Validacion.leerTelefono(sc);
        Cliente cliente = new Cliente(nombre, apellido, dni, email, telefono);
        banco.registrarCliente(cliente);
        System.out.println("Cliente registrado correctamente");
        System.out.println("ID cliente: " + cliente.getId());
    }

    public static void buscarPorDni(Banco banco) {
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

    public static void buscarPorId(Banco banco) {
        Scanner sc = new Scanner(System.in);

        String valor = Validacion.leerEntero(sc, "Introduzca el ID del cliente que desea buscar: ");
        int idBuscar = Integer.parseInt(valor);
        Collection<Cliente> clientes = banco.buscarClientePorId(idBuscar);
        if (clientes.isEmpty()) {
            System.out.println("\nERROR: No se encontró ningún cliente con ID " + idBuscar);
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
