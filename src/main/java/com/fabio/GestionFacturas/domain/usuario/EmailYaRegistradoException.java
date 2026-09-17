package com.fabio.GestionFacturas.domain.usuario;

/** Thrown when registering with an email that already has an account. Mapped to HTTP 409. */
public class EmailYaRegistradoException extends RuntimeException {
    public EmailYaRegistradoException(String message) {
        super(message);
    }
}
