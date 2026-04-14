package com.jlanzasg.novabank.view;

import com.jlanzasg.novabank.model.Cuenta;
import com.jlanzasg.novabank.service.CuentaService;

import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.Optional;

/**
 * The type Menu cuentas.
 */
public class MenuCuentas {

    private final CuentaService service;

    /**
     * Instantiates a new Menu cuentas.
     *
     * @param service the service
     */
    public MenuCuentas(CuentaService service) {
        this.service = service;
    }

    /**
     * Crear cuenta.
     *
     * @param idStr the id str
     */
    public void crearCuenta(String idStr) {
        try {
            Long idCliente = Long.parseLong(idStr);
            Cuenta cuentaCreada = service.crearCuenta(idCliente);

            System.out.println("\nLa cuenta se ha creado correctamente.");
            System.out.println("IBAN asignado: " + cuentaCreada.getIban());

        } catch (NumberFormatException e) {
            System.out.println("\nError: El ID debe ser un número entero.");
        } catch (IllegalArgumentException e) {
            System.out.println("\nError de validación: " + e.getMessage());
        } catch (Exception e) {
            System.out.println("\nError crítico del sistema: " + e.getMessage());
        }
    }

    /**
     * Listar cuentas.
     *
     * @param idClienteStr the id cliente str
     */
    public void listarCuentas(String idClienteStr) {
        try {
            Long idCliente = Long.parseLong(idClienteStr);
            List<Cuenta> cuentas = service.consultarCuentasDeCliente(idCliente);

            if (cuentas.isEmpty()) {
                System.out.println("\nEl cliente no tiene cuentas registradas.");
                return;
            }

            System.out.println("\n--- CUENTAS DEL CLIENTE ID: " + idCliente + " ---");
            System.out.println("-------------------------------------------------------------");
            System.out.printf("%-26s | %-12s%n", "Número de cuenta", "Saldo");
            System.out.println("-------------------------------------------------------------");

            for (Cuenta cuenta : cuentas) {
                System.out.printf("%-26s | %12.2f €%n", cuenta.getIban(), cuenta.getBalance());
            }

        } catch (NumberFormatException e) {
            System.out.println("\nError: El ID debe ser un número entero.");
        } catch (IllegalArgumentException e) {
            System.out.println("\nError: " + e.getMessage());
        }
    }

    /**
     * Ver cuenta.
     *
     * @param iban the iban
     */
    public void verCuenta(String iban) {
        try {
            Optional<Cuenta> cuentaOpt = service.consultarCuenta(iban);

            if (cuentaOpt.isEmpty()) {
                System.out.println("\nLa cuenta con IBAN " + iban + " no existe.");
                return;
            }

            Cuenta cuenta = cuentaOpt.get();
            DateTimeFormatter formatoFecha = DateTimeFormatter.ofPattern("dd-MM-yyyy HH:mm:ss");

            System.out.println("\n--- DETALLES DE LA CUENTA ---");
            System.out.println("Número de cuenta: " + cuenta.getIban());
            System.out.println("ID Titular: " + cuenta.getCliente().getId());
            System.out.println("Nombre Titular: " + cuenta.getCliente().getNombre() + " " + cuenta.getCliente().getApellido());
            System.out.println("Saldo: " + cuenta.getBalance() + " €");
            System.out.println("Fecha de creación: " + cuenta.getFecha().format(formatoFecha));

        } catch (IllegalArgumentException e) {
            System.out.println("\nError: " + e.getMessage());
        }
    }
}