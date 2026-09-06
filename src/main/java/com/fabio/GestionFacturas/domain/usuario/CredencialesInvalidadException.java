package com.fabio.GestionFacturas.domain.usuario;

public class CredencialesInvalidadException extends RuntimeException {
    public CredencialesInvalidadException(String message) {
        super(message);
    }
}
