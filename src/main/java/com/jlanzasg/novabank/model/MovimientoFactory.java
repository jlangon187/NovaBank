package com.jlanzasg.novabank.model;

public class MovimientoFactory {

    public static Movimiento crearDeposito(Long cuentaId, Double cantidad) {
        return Movimiento.builder()
                .cuentaId(cuentaId)
                .tipo(TipoMovimiento.DEPOSITO.name())
                .cantidad(cantidad)
                .build();
    }

    public static Movimiento crearRetiro(Long cuentaId, Double cantidad) {
        return Movimiento.builder()
                .cuentaId(cuentaId)
                .tipo(TipoMovimiento.RETIRO.name())
                .cantidad(cantidad)
                .build();
    }

    public static Movimiento crearTransferenciaSaliente(Long cuentaId, Double cantidad) {
        return Movimiento.builder()
                .cuentaId(cuentaId)
                .tipo(TipoMovimiento.TRANSFERENCIA_SALIENTE.name())
                .cantidad(cantidad)
                .build();
    }

    public static Movimiento crearTransferenciaEntrante(Long cuentaId, Double cantidad) {
        return Movimiento.builder()
                .cuentaId(cuentaId)
                .tipo(TipoMovimiento.TRANSFERENCIA_ENTRANTE.name())
                .cantidad(cantidad)
                .build();
    }
}