package com.jlanzasg.novabank.cuenta.exception;

/**
 * The type Not found exception.
 */
public class NotFoundException extends RuntimeException {
    /**
     * Instantiates a new Not found exception.
     *
     * @param message the message
     */
    public NotFoundException(String message) {
        super(message);
    }
}
