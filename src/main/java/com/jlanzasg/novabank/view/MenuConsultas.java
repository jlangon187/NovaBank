package com.jlanzasg.novabank.view;

import com.jlanzasg.novabank.model.Cuenta;
import com.jlanzasg.novabank.model.Movimiento;
import com.jlanzasg.novabank.service.CuentaService;
import com.jlanzasg.novabank.service.MovimientoService;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.Optional;

/**
 * The type Menu consultas.
 */
public class MenuConsultas {

    private final CuentaService cuentaService;
    private final MovimientoService movimientoService;

    /**
     * Instantiates a new Menu consultas.
     *
     * @param cuentaService     the cuenta service
     * @param movimientoService the movimiento service
     */
    public MenuConsultas(CuentaService cuentaService, MovimientoService movimientoService) {
        this.cuentaService = cuentaService;
        this.movimientoService = movimientoService;
    }

    /**
     * Consultar saldo.
     *
     * @param iban the iban
     */
    public void consultarSaldo(String iban) {
        try {
            Optional<Cuenta> cuenta = cuentaService.consultarCuenta(iban);
            if (cuenta.isEmpty()) {
                System.out.println("\nNo existe cuenta con el IBAN: " + iban);
                return;
            }
            System.out.println("\nSaldo actual de la cuenta " + iban + ": " + cuenta.get().getBalance() + " €");
        } catch (Exception e) {
            System.out.println("\nError: " + e.getMessage());
        }
    }

    /**
     * Consultar movimientos.
     *
     * @param iban the iban
     */
    public void consultarMovimientos(String iban) {
        try {
            List<Movimiento> movimientos = movimientoService.consultarHistorial(iban);
            imprimirMovimientos(movimientos, iban);
        } catch (Exception e) {
            System.out.println("\nError: " + e.getMessage());
        }
    }

    /**
     * Consultar movimientos por fecha.
     *
     * @param iban   the iban
     * @param inicio the inicio
     * @param fin    the fin
     */
    public void consultarMovimientosPorFecha(String iban, LocalDate inicio, LocalDate fin) {
        try {
            List<Movimiento> movimientos = movimientoService.consultarHistorialPorFechas(
                    iban, inicio.atStartOfDay(), fin.atTime(23, 59, 59));
            imprimirMovimientos(movimientos, iban);
        } catch (Exception e) {
            System.out.println("\nError: " + e.getMessage());
        }
    }

    /**
     * Imprimir movimientos.
     * @param movimientos
     * @param iban
     */
    private void imprimirMovimientos(List<Movimiento> movimientos, String iban) {
        if (movimientos.isEmpty()) {
            System.out.println("\nNo hay movimientos registrados para este criterio en la cuenta " + iban);
            return;
        }

        System.out.println("\n--- Historial de movimientos (" + iban + ") ---");
        System.out.println("Fecha               | Tipo                   | Cantidad  ");
        System.out.println("--------------------|------------------------|-----------");

        DateTimeFormatter formatoFecha = DateTimeFormatter.ofPattern("dd/MM/yyyy HH:mm:ss");

        for (Movimiento mov : movimientos) {
            String fechaTexto = mov.getFecha().format(formatoFecha);
            String tipoTexto = mov.getTipo();
            String signo = (tipoTexto.contains("RETIRO") || tipoTexto.contains("SALIENTE")) ? "-" : "+";

            System.out.printf("%s | %-22s | %s%,.2f €%n", fechaTexto, tipoTexto, signo, mov.getCantidad());
        }
    }
}