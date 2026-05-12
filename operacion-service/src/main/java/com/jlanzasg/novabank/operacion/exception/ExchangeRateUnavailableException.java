package com.jlanzasg.novabank.operacion.exception;

/**
 * The type Exchange rate unavailable exception.
 */
public class ExchangeRateUnavailableException extends RuntimeException {

    /**
     * Instantiates a new Exchange rate unavailable exception.
     *
     * @param from  the from
     * @param to    the to
     * @param cause the cause
     */
    public ExchangeRateUnavailableException(String from, String to, Throwable cause) {
        super("Servicio de cambio no disponible para conversión " + from + " -> " + to, cause);
    }

    /**
     * Instantiates a new Exchange rate unavailable exception.
     *
     * @param message the message
     */
    public ExchangeRateUnavailableException(String message) {
        super(message);
    }
}