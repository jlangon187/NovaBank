package com.jlanzasg.novabank.service;

/**
 * The interface Operacion service.
 */
public interface OperacionService {
    /**
     * Ingresar.
     *
     * @param iban     the iban
     * @param cantidad the cantidad
     */
    void ingresar(String iban, Double cantidad);

    /**
     * Retirar.
     *
     * @param iban     the iban
     * @param cantidad the cantidad
     */
    void retirar(String iban, Double cantidad);

    /**
     * Transferir.
     *
     * @param cantidad    the cantidad
     * @param ibanOrigen  the iban origen
     * @param ibanDestino the iban destino
     */
    void transferir(String ibanOrigen, String ibanDestino, Double cantidad);
}