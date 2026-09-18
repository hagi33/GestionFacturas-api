package com.fabio.GestionFacturas.domain.cliente;

/** Thrown when a Cliente invariant is violated. Mapped to HTTP 400 by GlobalExceptionHandler. */
public class ClienteInvalidoException extends RuntimeException {
    public ClienteInvalidoException(String message) {
        super(message);
    }
}
