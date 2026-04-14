package com.jlanzasg.novabank.view;

import com.jlanzasg.novabank.service.OperacionService;

/**
 * The type Menu operaciones.
 */
public class MenuOperaciones {

    private final OperacionService operacionService;

    /**
     * Instantiates a new Menu operaciones.
     *
     * @param operacionService the operacion service
     */
    public MenuOperaciones(OperacionService operacionService) {
        this.operacionService = operacionService;
    }

    /**
     * Ingresar.
     *
     * @param iban        the iban
     * @param cantidadStr the cantidad str
     */
    public void ingresar(String iban, String cantidadStr) {
        try {
            Double cantidad = Double.parseDouble(cantidadStr);
            operacionService.ingresar(iban, cantidad);

            System.out.println("\nDepósito realizado correctamente.");

        } catch (NumberFormatException e) {
            System.out.println("\nError: La cantidad debe ser un número válido.");
        } catch (Exception e) {
            System.out.println(e.getMessage());
        }
    }

    /**
     * Retirar.
     *
     * @param iban        the iban
     * @param cantidadStr the cantidad str
     */
    public void retirar(String iban, String cantidadStr) {
        try {
            Double cantidad = Double.parseDouble(cantidadStr);
            operacionService.retirar(iban, cantidad);

            System.out.println("\nRetiro realizado correctamente.");

        } catch (NumberFormatException e) {
            System.out.println("\nError: La cantidad debe ser un número válido.");
        } catch (Exception e) {
            System.out.println(e.getMessage());
        }
    }

    /**
     * Realizar transferencia.
     *
     * @param ibanOrigen  the iban origen
     * @param ibanDestino the iban destino
     * @param cantidadStr the cantidad str
     */
    public void realizarTransferencia(String ibanOrigen, String ibanDestino, String cantidadStr) {
        try {
            Double cantidad = Double.parseDouble(cantidadStr);
            operacionService.transferir(ibanOrigen, ibanDestino, cantidad);

            System.out.println("\nTransferencia de " + cantidad + "€ realizada correctamente.");

        } catch (NumberFormatException e) {
            System.out.println("\nError: La cantidad debe ser un número válido.");
        } catch (Exception e) {
            System.out.println(e.getMessage());
        }
    }
}