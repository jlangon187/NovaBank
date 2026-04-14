package com.jlanzasg.novabank.model;

/**
 * The type Movimiento factory.
 */
public class MovimientoFactory {

    /**
     * Crear deposito movimiento.
     *
     * @param cuentaId the cuenta id
     * @param cantidad the cantidad
     * @return the movimiento
     */
    public static Movimiento crearDeposito(Long cuentaId, Double cantidad) {
        return Movimiento.builder()
                .cuentaId(cuentaId)
                .tipo(TipoMovimiento.DEPOSITO.name())
                .cantidad(cantidad)
                .build();
    }

    /**
     * Crear retiro movimiento.
     *
     * @param cuentaId the cuenta id
     * @param cantidad the cantidad
     * @return the movimiento
     */
    public static Movimiento crearRetiro(Long cuentaId, Double cantidad) {
        return Movimiento.builder()
                .cuentaId(cuentaId)
                .tipo(TipoMovimiento.RETIRO.name())
                .cantidad(cantidad)
                .build();
    }

    /**
     * Crear transferencia saliente movimiento.
     *
     * @param cuentaId the cuenta id
     * @param cantidad the cantidad
     * @return the movimiento
     */
    public static Movimiento crearTransferenciaSaliente(Long cuentaId, Double cantidad) {
        return Movimiento.builder()
                .cuentaId(cuentaId)
                .tipo(TipoMovimiento.TRANSFERENCIA_SALIENTE.name())
                .cantidad(cantidad)
                .build();
    }

    /**
     * Crear transferencia entrante movimiento.
     *
     * @param cuentaId the cuenta id
     * @param cantidad the cantidad
     * @return the movimiento
     */
    public static Movimiento crearTransferenciaEntrante(Long cuentaId, Double cantidad) {
        return Movimiento.builder()
                .cuentaId(cuentaId)
                .tipo(TipoMovimiento.TRANSFERENCIA_ENTRANTE.name())
                .cantidad(cantidad)
                .build();
    }
}