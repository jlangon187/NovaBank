package com.jlanzasg.novabank.vista;

import com.jlanzasg.novabank.modelo.Cliente;
import com.jlanzasg.novabank.modelo.CuentaBancaria;
import com.jlanzasg.novabank.negocio.Banco;
import com.jlanzasg.novabank.validaciones.Validacion;

import java.util.Scanner;
import java.util.function.Predicate;

public class Menu {
    MenuCliente menuCliente = new MenuCliente();
    MenuCuentas menuCuentas = new MenuCuentas();
    MenuOperaciones menuOperaciones = new MenuOperaciones();

    public void menuPrincipal(Banco banco) {
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

            op = Integer.parseInt(Validacion.leerEntero(sc, "\nSeleccione una opción: "));

            switch (op) {
                case 1:
                    gestionarClientes(banco);
                    break;
                case 2:
                    gestionarCuentas(banco);
                    break;
                case 3:
                    operacionesFinancieras(banco);
                    break;
                case 4:
                    consultas();
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

    public void gestionarClientes(Banco banco) {
        Scanner sc = new Scanner(System.in);
        int op;

        do {
            System.out.println("--- GESTIÓN DE CLIENTES ---");
            System.out.println("1. Crear cliente");
            System.out.println("2. Buscar cliente");
            System.out.println("3. Listar clientes");
            System.out.println("4. Volver");

            op = Integer.parseInt(Validacion.leerEntero(sc, "\nSeleccione una opción: "));

            switch (op) {
                case 1:
                    menuCliente.altaCliente(banco);
                    break;
                case 2:
                    buscarPorTipo(banco);
                    break;
                case 3:
                    menuCliente.listarClientes(banco);
                    break;
                case 4:
                    break;
                default:
                    System.out.println("No ha introducido una opción correcta");
                    break;
            }
        } while (op != 4);
    }

    public void buscarPorTipo(Banco banco) {
        Scanner sc = new Scanner(System.in);
        int op;

        do {
            System.out.println("--- TIPO DE BUSQUEDA ---");
            System.out.println("1. Buscar por DNI");
            System.out.println("2. Buscar por ID");
            System.out.println("3. Volver");

            op = Integer.parseInt(Validacion.leerEntero(sc, "\nSeleccione una opción: "));

            switch (op) {
                case 1:
                    menuCliente.buscarPorDni(banco);
                    break;
                case 2:
                    menuCliente.buscarPorId(banco);
                    break;
                case 3:
                    break;
                default:
                    System.out.println("No ha introducido una opción correcta");
                    break;
            }
        } while (op != 3);

    }

    public void gestionarCuentas(Banco banco) {
        Scanner sc = new Scanner(System.in);
        int op;

        do {
            System.out.println("--- GESTIÓN DE CUENTAS ---");
            System.out.println("1. Crear cuenta");
            System.out.println("2. Listar cuentas de cliente");
            System.out.println("3. Ver información de cuenta");
            System.out.println("4. Volver");

            op = Integer.parseInt(Validacion.leerEntero(sc, "\nSeleccione una opción: "));

            switch (op) {
                case 1:
                    String id = Validacion.leerEntero(sc, "Introduzca el ID del cliente: ");
                    menuCuentas.crearCuenta(banco, id);
                    break;
                case 2:
                    String idCliente = Validacion.leerEntero(sc, "Introduzca el ID del cliente: ");
                    menuCuentas.listarCuentas(banco, idCliente);
                    break;
                case 3:
                    String iban = Validacion.leerIban(sc);
                    menuCuentas.verCuenta(banco, iban);
                    break;
                case 4:
                    break;
                default:
                    System.out.println("No ha introducido una opción correcta");
                    break;
            }
        } while (op != 4);
    }

    public void operacionesFinancieras(Banco banco) {
        Scanner sc = new Scanner(System.in);
        int op;

        do {
            System.out.println("--- OPERACIONES FINANCIERAS ---");
            System.out.println("1. Depositar dinero");
            System.out.println("2. Retirar dinero");
            System.out.println("3. Transferencia entre cuentas");
            System.out.println("4. Volver");

            op = Integer.parseInt(Validacion.leerEntero(sc, "\nSeleccione una opción: "));

            switch (op) {
                case 1:
                    String ibanIng = Validacion.leerIban(sc);
                    String cantidadIng = Validacion.leerDouble(sc, "Introduzca la cantidad a ingresar: ");
                    menuOperaciones.ingresar(banco, ibanIng, cantidadIng);
                    break;
                case 2:
                    String ibanRet = Validacion.leerIban(sc);
                    String cantidadRet = Validacion.leerDouble(sc, "Introduzca la cantidad a ingresar: ");
                    menuOperaciones.ingresar(banco, ibanRet, cantidadRet);
                    break;
                case 3:
                    String ibanOrigen = Validacion.leerIban(sc);
                    String ibanDestino = Validacion.leerIban(sc);
                    String cantidadTransferencia = Validacion.leerDouble(sc, "Introduzca la cantidad a transferir: ");
                    menuOperaciones.realizarTransferencia(banco, ibanOrigen, ibanDestino, cantidadTransferencia);
                    break;
                case 4:
                    break;
                default:
                    System.out.println("No ha introducido una opción correcta");
                    break;
            }
        } while (op != 4);
    }

    public void consultas() {
        System.out.println("--- CONSULTAS ---");
        System.out.println("1. Consultar saldo");
        System.out.println("2. Historial de movimientos");
        System.out.println("3. Movimientos por rango de fechas");
        System.out.println("4. Volver");
    }
}
