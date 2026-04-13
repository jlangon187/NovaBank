package com.jlanzasg.novabank.view;

import com.jlanzasg.novabank.model.Cliente;
import com.jlanzasg.novabank.service.ClienteService;
import com.jlanzasg.novabank.utils.Validacion;

import java.util.List;
import java.util.Optional;
import java.util.Scanner;

public class MenuCliente {

    private final ClienteService service;

    public MenuCliente(ClienteService service) {
        this.service = service;
    }

    public void altaCliente() {
        Scanner sc = new Scanner(System.in);
        System.out.println("\n--- ALTA DE NUEVO CLIENTE ---");

        boolean registrado = false;

        do {
            try {

                System.out.print("Nombre: ");
                String nombre = sc.nextLine();

                System.out.print("Apellido: ");
                String apellido = sc.nextLine();

                System.out.print("DNI: ");
                String dni = sc.nextLine().toUpperCase();

                System.out.print("Email: ");
                String email = sc.nextLine();

                System.out.print("Teléfono: ");
                String telefono = sc.nextLine();

                Cliente cliente = Cliente.builder()
                        .nombre(nombre)
                        .apellido(apellido)
                        .dni(dni)
                        .email(email)
                        .telefono(telefono)
                        .build();

                service.registrarCliente(cliente);
                System.out.println("\nCliente registrado correctamente con ID: " + cliente.getId());
                registrado = true;

            } catch (IllegalArgumentException | IllegalStateException e) {
                System.out.println("\nError: " + e.getMessage());
                System.out.println("Introduzca todos los datos correctamente");
            } catch (Exception e) {
                System.out.println("\nError crítico: " + e.getMessage());
            }
        } while (!registrado);
    }

    public void buscarPorId() {
        Scanner sc = new Scanner(System.in);

        String idStr;
        do {
            System.out.print("Introduzca el ID del cliente que desea buscar: ");
            idStr = sc.nextLine();
            if (!Validacion.esLong(idStr)) System.out.println("Error: Debe introducir un número entero válido.");
        } while (!Validacion.esLong(idStr));

        Long idBuscar = Long.parseLong(idStr);
        Optional<Cliente> clienteOptional = service.buscarClientePorId(idBuscar);

        if (clienteOptional.isEmpty()) {
            System.out.println("\nError: No se encontró ningún cliente con ID " + idBuscar);
        } else {
            Cliente cliente = clienteOptional.get();
            System.out.println("\n Cliente encontrado:");
            System.out.println("ID: " + cliente.getId());
            System.out.println("Nombre: " + cliente.getNombre() + " " + cliente.getApellido());
            System.out.println("DNI: " + cliente.getDni());
            System.out.println("Email: " + cliente.getEmail());
            System.out.println("Teléfono: " + cliente.getTelefono());
        }
    }

    public void buscarPorDni() {
        Scanner sc = new Scanner(System.in);

        String dniBuscar;
        do {
            System.out.print("Introduzca el DNI del cliente que desea buscar: ");
            dniBuscar = sc.nextLine().toUpperCase();
            if (!Validacion.esDniValido(dniBuscar)) System.out.println("Error: Formato de DNI incorrecto.");
        } while (!Validacion.esDniValido(dniBuscar));

        Optional<Cliente> clienteOptional = service.buscarClientePorDni(dniBuscar);

        if (clienteOptional.isEmpty()) {
            System.out.println("\nError: No se encontró ningún cliente con DNI " + dniBuscar);
        } else {
            Cliente cliente = clienteOptional.get();
            System.out.println("\nCliente encontrado:");
            System.out.println("ID: " + cliente.getId());
            System.out.println("Nombre: " + cliente.getNombre() + " " + cliente.getApellido());
            System.out.println("DNI: " + cliente.getDni());
            System.out.println("Email: " + cliente.getEmail());
            System.out.println("Teléfono: " + cliente.getTelefono());
        }
    }

    public void listarClientes() {

        List<Cliente> clientes = service.listarClientes();

        if (clientes.isEmpty()) {
            System.out.println("\nNo hay clientes registrados en la base de datos.");
            return;
        }

        System.out.println("\nLISTA DE CLIENTES");
        System.out.println("---------------------------------------------------------------------------------------------");
        System.out.printf("%-6s | %-20s | %-10s | %-30s | %-12s%n", "ID", "Nombre", "DNI", "Email", "Teléfono");
        System.out.println("---------------------------------------------------------------------------------------------");

        // Iteramos la lista de forma mucho más sencilla
        for (Cliente cliente : clientes) {
            System.out.printf("%-6d | %-20s | %-10s | %-30s | %-12s%n",
                    cliente.getId(),
                    cliente.getNombre() + " " + cliente.getApellido(),
                    cliente.getDni(),
                    cliente.getEmail(),
                    cliente.getTelefono()
            );
        }
    }
}