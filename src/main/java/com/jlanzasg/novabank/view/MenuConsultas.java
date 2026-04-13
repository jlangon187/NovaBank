package com.jlanzasg.novabank.view;

import com.jlanzasg.novabank.model.CuentaBancaria;
import com.jlanzasg.novabank.model.Movimiento;
import com.jlanzasg.novabank.model.TipoMovimiento;
import com.jlanzasg.novabank.service.Banco;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.Map;

public class MenuConsultas {

    // Método para comprobar si existe la cuenta
    public boolean existeCuenta(Banco banco, String iban) {
        if (!banco.getCuentas().containsKey(iban)) {
            System.out.println("No existe cuenta con el IBAN: " + iban);
            return false;
        }
        return true;
    }

    // Método que consulta el saldo actual a través del IBAN si existe dicha cuenta
    public void consultarSaldo(Banco banco, String iban) {
        if (!banco.getCuentas().containsKey(iban)) {
            System.out.println("No existe cuenta con el IBAN: " + iban);
            return;
        }
        CuentaBancaria cuentaBancaria = banco.getCuentas().get(iban);
        System.out.println("Saldo actual: " + cuentaBancaria.getBalance() + " €");
    }

    // Método para consultar todos los movimientos de una cuenta haciendo la búsqueda por su IBAN, si existe, se crea un mapa con todos los
    // movimientos de esa cuenta si los tiene, si no se muestra un mensaje. Luego se recorre el bucle de movimientos y se va mostrando formateado
    public void consultarMovimientos(Banco banco, String iban) {
        if (existeCuenta(banco, iban)) {

            CuentaBancaria cuentaBancaria = banco.getCuentas().get(iban);
            Map<Long, Movimiento> movimientos = cuentaBancaria.getMovimientos();

            if (movimientos == null || movimientos.isEmpty()) {
                System.out.println("No hay movimientos registrados con este IBAN: " + iban);
                return;
            }

            System.out.println("Historial de movimientos - " + iban + ":");
            System.out.println("Fecha               | Tipo                   | Cantidad  ");
            System.out.println("--------------------|------------------------|-----------");

            DateTimeFormatter formatoFecha = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");

            for (Movimiento mov : movimientos.values()) {

                String fechaTexto = mov.getFecha().format(formatoFecha);
                String tipoTexto = mov.getTipo();

                String signo = (tipoTexto.equals(TipoMovimiento.RETIRO.name()) || tipoTexto.equals(TipoMovimiento.TRANSFERENCIA_SALIENTE.name())) ? "-" : "+";
                System.out.printf("%s | %-22s | %s%,.2f €%n", fechaTexto, tipoTexto, signo, mov.getCantidad());
            }
        }
    }

    // Método para consultar los movimientos de la cuenta por fecha de inicio y fin
    // Primero comprueba si la cuenta existe, seguidamente creamos un Map para meter los movimientos de esa cuenta
    // y se comprueba si la fecha de fin no es anterior a la de inicio.
    // Después se itera para filtrar los movimientos por fecha con el isBefore y isAfter y se muestra formateado
    public void consultarMovimientosPorFecha(Banco banco, String iban, LocalDate inicio, LocalDate fin) {
        if (existeCuenta(banco, iban)) {

            CuentaBancaria cuentaBancaria = banco.getCuentas().get(iban);
            Map<Long, Movimiento> movimientos = cuentaBancaria.getMovimientos();

            if (movimientos == null || movimientos.isEmpty()) {
                System.out.println("No hay movimientos registrados con este IBAN: " + iban);
                return;
            }

            if (fin.isBefore(inicio)) {
                System.out.println("La fecha de fin no puede ser anterior a la de inicio.");
                return;
            }

            System.out.println("Número de cuenta: " + iban);
            DateTimeFormatter formatoSalida = DateTimeFormatter.ofPattern("dd/MM/yyyy");
            System.out.println("Fecha inicio (dd/MM/yyyy): " + inicio.format(formatoSalida));
            System.out.println("Fecha fin (dd/MM/yyyy): " + fin.format(formatoSalida));

            DateTimeFormatter formatoFecha = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");
            boolean encontrado = false;

            for (Movimiento mov : movimientos.values()) {
                LocalDate fechaMov = mov.getFecha().toLocalDate();

                if (!fechaMov.isBefore(inicio) && !fechaMov.isAfter(fin)) {

                    if (!encontrado) {
                        System.out.println("Movimientos del " + inicio + " al " + fin + ":");
                        System.out.println("Fecha               | Tipo                   | Cantidad  ");
                        System.out.println("--------------------|------------------------|-----------");
                        encontrado = true;
                    }

                    String fechaTexto = mov.getFecha().format(formatoFecha);
                    String tipoTexto = mov.getTipo();
                    String signo = (tipoTexto.equals(TipoMovimiento.RETIRO.name()) || tipoTexto.equals(TipoMovimiento.TRANSFERENCIA_SALIENTE.name())) ? "-" : "+";

                    System.out.printf("%s | %-22s | %s%,.2f €%n", fechaTexto, tipoTexto, signo, mov.getCantidad());
                }
            }

            if (!encontrado) {
                System.out.println("No existe ningún movimiento en este rango de fechas.");
            }
        }
    }
}