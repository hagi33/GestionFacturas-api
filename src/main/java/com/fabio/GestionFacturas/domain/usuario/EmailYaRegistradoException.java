package com.fabio.GestionFacturas.domain.usuario;

public class EmailYaRegistradoException extends RuntimeException {
    public EmailYaRegistradoException(String message) {
        super(message);
    }
}
