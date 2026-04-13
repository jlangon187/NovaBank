package com.jlanzasg.novabank.view;

import com.jlanzasg.novabank.model.Cliente;
import com.jlanzasg.novabank.service.Banco;
import com.jlanzasg.novabank.utils.Validacion;

import java.util.Collection;
import java.util.Scanner;

public class MenuCliente {

    public void altaCliente(Banco banco) {
        Scanner sc = new Scanner(System.in);
        System.out.println("Introduzca los datos del cliente:");
        String nombre = Validacion.leerNombre(sc,"Nombre: ");
        String apellido = Validacion.leerNombre(sc,"Apellido: ");
        String dni = Validacion.leerDni(sc, banco, "DNI: ");
        String email = Validacion.leerEmail(sc);
        String telefono = Validacion.leerTelefono(sc);
        Cliente cliente = new Cliente(nombre, apellido, dni, email, telefono);
        banco.registrarCliente(cliente);
        System.out.println("Cliente registrado correctamente");
        System.out.println("ID cliente: " + cliente.getId());
    }

    public void buscarPorDni(Banco banco) {
        Scanner sc = new Scanner(System.in);

        System.out.print("Introduzca el DNI del cliente que desea buscar: ");
        String dniBuscar = sc.nextLine().toUpperCase();
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

    public void buscarPorId(Banco banco) {
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

    public void listarClientes(Banco banco) {

        if (banco.getClientes().isEmpty()) {
            System.out.println("No hay clientes registrados");
            return;
        }

        System.out.println("LISTA DE CLIENTES");
        System.out.println("---------------------------------------------------------------------------------------------");

        // Encabezado
        System.out.printf("%-6s | %-20s | %-10s | %-30s | %-12s%n",
                "ID", "Nombre", "DNI", "Email", "Teléfono");

        System.out.println("---------------------------------------------------------------------------------------------");

        // Filas
        banco.getClientes().forEach((id, cliente) ->
                System.out.printf("%-6d | %-20s | %-10s | %-30s | %-12s%n",
                        id,
                        cliente.getNombre() + " " + cliente.getApellido(),
                        cliente.getDni(),
                        cliente.getEmail(),
                        cliente.getTelefono()
                )
        );
    }


}
