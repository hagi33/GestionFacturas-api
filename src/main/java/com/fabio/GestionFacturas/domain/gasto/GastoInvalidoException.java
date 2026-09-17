package com.fabio.GestionFacturas.domain.gasto;

/** Thrown when a Gasto invariant is violated. Mapped to HTTP 400 by GlobalExceptionHandler. */
public class GastoInvalidoException extends RuntimeException {
    public GastoInvalidoException(String message) {
        super(message);
    }
}
