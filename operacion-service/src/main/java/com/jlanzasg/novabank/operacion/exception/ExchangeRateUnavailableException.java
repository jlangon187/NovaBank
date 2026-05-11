package com.jlanzasg.novabank.operacion.exception;

public class ExchangeRateUnavailableException extends RuntimeException {
    public ExchangeRateUnavailableException(String from, String to, Throwable cause) {
        super("Servicio de cambio no disponible para conversión " + from + " -> " + to, cause);
    }
}
