package com.fabio.GestionFacturas.domain.ingreso;

/** Thrown when an Ingreso does not exist or does not belong to the requesting user. Mapped to HTTP 404 by GlobalExceptionHandler. */
public class IngresoNoEncontradoException extends RuntimeException {
    public IngresoNoEncontradoException(String message) {
        super(message);
    }
}
