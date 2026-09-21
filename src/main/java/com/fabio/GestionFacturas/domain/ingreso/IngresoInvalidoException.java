package com.fabio.GestionFacturas.domain.ingreso;

/** Thrown when an Ingreso invariant is violated. Mapped to HTTP 400 by GlobalExceptionHandler. */
public class IngresoInvalidoException extends RuntimeException {
    public IngresoInvalidoException(String message) {
        super(message);
    }
}
