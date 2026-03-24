package com.jlanzasg.novabank.vista;

import com.jlanzasg.novabank.modelo.Cliente;
import com.jlanzasg.novabank.negocio.Banco;
import com.jlanzasg.novabank.validaciones.Validacion;

import java.util.Collection;
import java.util.Scanner;

public class Menu {

    public static void menuPrincipal(Banco banco) {
        Scanner sc = new Scanner(System.in);
        int op;

        do {
            System.out.println("=======================================");
            System.out.println("|  NOVABANK - SISTEMA DE OPERACIONES  |");
            System.out.println("=======================================");
            System.out.println("1. Gestión de clientes");
            System.out.println("2. Gestión de cuentas");
            System.out.println("3. Operaciones financieras");
            System.out.println("4. Consultas");
            System.out.println("5. Salir");

            System.out.print("\nSeleccione una opción: ");
            op = Validacion.validarInt(sc.nextLine());

            switch (op) {
                case 1:
                    Menu.gestionarClientes(banco);
                    break;
                case 2:
                    Menu.gestionarCuentas();
                    break;
                case 3:
                    Menu.operacionesFinancieras();
                    break;
                case 4:
                    Menu.consultas();
                    break;
                case 5:
                    System.out.println("Saliendo de la aplicación");
                    break;
                default:
                    System.out.println("No ha introducido una opción correcta");
                    break;
            }
        } while (op != 5);
    }

    public static void gestionarClientes(Banco banco) {
        Scanner sc = new Scanner(System.in);
        int op;

        do {
            System.out.println("--- GESTIÓN DE CLIENTES ---");
            System.out.println("1. Crear cliente");
            System.out.println("2. Buscar cliente");
            System.out.println("3. Listar clientes");
            System.out.println("4. Volver");

            System.out.print("\nSeleccione una opción: ");
            op = Validacion.validarInt(sc.nextLine());

            switch (op) {
                case 1:
                    System.out.println("Introduzca los datos del cliente:");
                    System.out.print("Nombre: ");
                    String nombre = sc.nextLine();
                    System.out.print("Apellido: ");
                    String apellido = sc.nextLine();
                    System.out.print("DNI: ");
                    String dni = sc.nextLine();
                    String email = Validacion.isValidEmail();
                    System.out.print("Teléfono:");
                    int telefono = Validacion.validarInt(sc.nextLine());
                    Cliente cliente = new Cliente(nombre, apellido, dni, email, telefono);
                    banco.registrarCliente(cliente);
                    System.out.println("Cliente registrado correctamente");
                    break;
                case 2:
                    System.out.print("Introduzca el DNI del cliente que desea buscar:");
                    String dniBuscar = sc.nextLine();
                    Collection <Cliente> clientes = banco.buscarClientePorDni(dniBuscar);
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
                    break;
                case 3:
                    Menu.operacionesFinancieras();
                    break;
                case 4:
                    break;
                default:
                    System.out.println("No ha introducido una opción correcta");
                    break;
            }
        } while (op != 4);
    }

        public static void gestionarCuentas () {
            System.out.println("--- GESTIÓN DE CUENTAS ---");
            System.out.println("1. Crear cuenta");
            System.out.println("2. Listar cuentas de cliente");
            System.out.println("3. Ver información de cuenta");
            System.out.println("4. Volver");
        }

        public static void operacionesFinancieras () {
            System.out.println("--- OPERACIONES FINANCIERAS ---");
            System.out.println("1. Depositar dinero");
            System.out.println("2. Retirar dinero");
            System.out.println("3. Transferencia entre cuentas");
            System.out.println("4. Volver");
        }

        public static void consultas () {
            System.out.println("--- CONSULTAS ---");
            System.out.println("1. Consultar saldo");
            System.out.println("2. Historial de movimientos");
            System.out.println("3. Movimientos por rango de fechas");
            System.out.println("4. Volver");
        }
    }
