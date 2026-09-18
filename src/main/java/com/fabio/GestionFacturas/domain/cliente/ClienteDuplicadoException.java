package com.fabio.GestionFacturas.domain.cliente;

/** Thrown when a user already has a client with the given NIF. Mapped to HTTP 409 by GlobalExceptionHandler. */
public class ClienteDuplicadoException extends RuntimeException {
    public ClienteDuplicadoException(String message) {
        super(message);
    }
}
