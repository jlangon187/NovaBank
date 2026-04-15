package com.jlanzasg.novabank.view;

import com.jlanzasg.novabank.utils.Validacion;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.Scanner;

/**
 * The type Menu.
 */
public class Menu {

    private final MenuCliente menuCliente;
    private final MenuCuentas menuCuentas;
    private final MenuOperaciones menuOperaciones;
    private final MenuConsultas menuConsultas;

    /**
     * Instantiates a new Menu.
     *
     * @param menuCliente     the menu cliente
     * @param menuCuentas     the menu cuentas
     * @param menuOperaciones the menu operaciones
     * @param menuConsultas   the menu consultas
     */
    public Menu(MenuCliente menuCliente, MenuCuentas menuCuentas,
                MenuOperaciones menuOperaciones, MenuConsultas menuConsultas) {
        this.menuCliente = menuCliente;
        this.menuCuentas = menuCuentas;
        this.menuOperaciones = menuOperaciones;
        this.menuConsultas = menuConsultas;
    }

    private int leerOpcion(Scanner sc, String mensaje) {
        while (true) {
            System.out.print(mensaje);
            try {
                return Integer.parseInt(sc.nextLine());
            } catch (NumberFormatException e) {
                System.out.println("Error: Debe introducir un número entero válido.");
            }
        }
    }

    /**
     * Menu principal.
     */
    public void menuPrincipal() {
        Scanner sc = new Scanner(System.in);
        int op;

        do {
            System.out.println("\n=======================================");
            System.out.println("|  NOVABANK - SISTEMA DE OPERACIONES  |");
            System.out.println("=======================================");
            System.out.println("1. Gestión de clientes");
            System.out.println("2. Gestión de cuentas");
            System.out.println("3. Operaciones financieras");
            System.out.println("4. Consultas");
            System.out.println("5. Salir");

            op = leerOpcion(sc, "\nSeleccione una opción: ");

            switch (op) {
                case 1: gestionarClientes(); break;
                case 2: gestionarCuentas(); break;
                case 3: operacionesFinancieras(); break;
                case 4: consultas(); break;
                case 5: System.out.println("Saliendo de la aplicación..."); break;
                default: System.out.println("Opción incorrecta."); break;
            }
        } while (op != 5);
    }

    /**
     * Menu de gestion de clientes
     */
    private void gestionarClientes() {
        Scanner sc = new Scanner(System.in);
        int op;

        do {
            System.out.println("\n--- GESTIÓN DE CLIENTES ---");
            System.out.println("1. Crear cliente");
            System.out.println("2. Buscar cliente");
            System.out.println("3. Listar clientes");
            System.out.println("4. Eliminar cliente");
            System.out.println("5. Volver");

            op = leerOpcion(sc, "\nSeleccione una opción: ");

            switch (op) {
                case 1: menuCliente.altaCliente(); break;
                case 2: buscarPorTipo(); break;
                case 3: menuCliente.listarClientes(); break;
                case 4: menuCliente.bajaCliente(); break;
                case 5: break;
                default: System.out.println("Opción incorrecta."); break;
            }
        } while (op != 5);
    }

    /**
     * Menu de busqueda por tipo (DNI o ID)
     */
    private void buscarPorTipo() {
        Scanner sc = new Scanner(System.in);
        int op;

        do {
            System.out.println("\n--- TIPO DE BÚSQUEDA ---");
            System.out.println("1. Buscar por DNI");
            System.out.println("2. Buscar por ID");
            System.out.println("3. Volver");

            op = leerOpcion(sc, "\nSeleccione una opción: ");

            switch (op) {
                case 1: menuCliente.buscarPorDni(); break;
                case 2: menuCliente.buscarPorId(); break;
                case 3: break;
                default: System.out.println("Opción incorrecta."); break;
            }
        } while (op != 3);
    }

    /**
     * Menu de gestion de cuentas
     */
    private void gestionarCuentas() {
        Scanner sc = new Scanner(System.in);
        int op;

        do {
            System.out.println("\n--- GESTIÓN DE CUENTAS ---");
            System.out.println("1. Crear cuenta");
            System.out.println("2. Listar cuentas de cliente");
            System.out.println("3. Ver información de cuenta");
            System.out.println("4. Volver");

            op = leerOpcion(sc, "\nSeleccione una opción: ");

            switch (op) {
                case 1:
                    String id = String.valueOf(leerOpcion(sc, "Introduzca el ID del cliente: "));
                    menuCuentas.crearCuenta(id); // Actualizaremos este menú más adelante
                    break;
                case 2:
                    String idCliente = String.valueOf(leerOpcion(sc, "Introduzca el ID del cliente: "));
                    menuCuentas.listarCuentas(idCliente);
                    break;
                case 3:
                    System.out.print("Introduzca IBAN: ");
                    String iban = sc.nextLine();
                    menuCuentas.verCuenta(iban);
                    break;
                case 4: break;
                default: System.out.println("Opción incorrecta."); break;
            }
        } while (op != 4);
    }

    /**
     * Menu de operaciones financieras
     */
    private void operacionesFinancieras() {
        Scanner sc = new Scanner(System.in);
        int op;

        do {
            System.out.println("\n--- OPERACIONES FINANCIERAS ---");
            System.out.println("1. Depositar dinero");
            System.out.println("2. Retirar dinero");
            System.out.println("3. Transferencia entre cuentas");
            System.out.println("4. Volver");

            op = leerOpcion(sc, "\nSeleccione una opción: ");

            switch (op) {
                case 1:
                    System.out.print("IBAN de ingreso: ");
                    String ibanIng = sc.nextLine();
                    System.out.print("Cantidad a ingresar: ");
                    String cantidadIng = sc.nextLine();
                    menuOperaciones.ingresar(ibanIng, cantidadIng);
                    break;
                case 2:
                    System.out.print("IBAN de retiro: ");
                    String ibanRet = sc.nextLine();
                    System.out.print("Cantidad a retirar: ");
                    String cantidadRet = sc.nextLine();
                    menuOperaciones.retirar(ibanRet, cantidadRet);
                    break;
                case 3:
                    System.out.print("IBAN de origen: ");
                    String ibanOrigen = sc.nextLine();
                    System.out.print("IBAN de destino: ");
                    String ibanDestino = sc.nextLine();
                    System.out.print("Cantidad a transferir: ");
                    String cantidadTransferencia = sc.nextLine();
                    menuOperaciones.realizarTransferencia(ibanOrigen, ibanDestino, cantidadTransferencia);
                    break;
                case 4: break;
                default: System.out.println("Opción incorrecta."); break;
            }
        } while (op != 4);
    }

    /**
     * Menu de consultas
     */
    private void consultas() {
        Scanner sc = new Scanner(System.in);
        int op;

        do {
            System.out.println("\n--- CONSULTAS ---");
            System.out.println("1. Consultar saldo");
            System.out.println("2. Historial de movimientos");
            System.out.println("3. Movimientos por rango de fechas");
            System.out.println("4. Volver");

            op = leerOpcion(sc, "\nSeleccione una opción: ");

            switch (op) {
                case 1:
                    System.out.print("Introduzca IBAN: ");
                    String ibanSaldo = sc.nextLine();
                    menuConsultas.consultarSaldo(ibanSaldo);
                    break;
                case 2:
                    System.out.print("Introduzca IBAN: ");
                    String ibanMov = sc.nextLine();
                    menuConsultas.consultarMovimientos(ibanMov);
                    break;
                case 3:
                    System.out.print("Introduzca IBAN: ");
                    String ibanFecha = sc.nextLine();
                    System.out.print("Fecha de inicio (dd/MM/yyyy): ");
                    String fechaInicioStr = sc.nextLine();
                    System.out.print("Fecha de fin (dd/MM/yyyy): ");
                    String fechaFinStr = sc.nextLine();

                    try {
                        DateTimeFormatter formato = DateTimeFormatter.ofPattern("dd/MM/yyyy");
                        LocalDate fechaInicio = LocalDate.parse(fechaInicioStr, formato);
                        LocalDate fechaFin = LocalDate.parse(fechaFinStr, formato);
                        menuConsultas.consultarMovimientosPorFecha(ibanFecha, fechaInicio, fechaFin);
                    } catch (Exception e) {
                        System.out.println("Error: El formato de las fechas no es correcto.");
                    }
                    break;
                case 4: break;
                default: System.out.println("Opción incorrecta."); break;
            }
        } while (op != 4);
    }
}