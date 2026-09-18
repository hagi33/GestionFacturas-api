package com.fabio.GestionFacturas.domain.cliente;

/** Thrown when a Cliente does not exist or does not belong to the requesting user. Mapped to HTTP 404 by GlobalExceptionHandler. */
public class ClienteNoEncontradoException extends RuntimeException {
    public ClienteNoEncontradoException(String message) {
        super(message);
    }
}
