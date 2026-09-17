package com.fabio.GestionFacturas.domain.usuario;

/** Thrown on failed login/refresh (wrong password, expired or revoked token). Mapped to HTTP 401. */
public class CredencialesInvalidadException extends RuntimeException {
    public CredencialesInvalidadException(String message) {
        super(message);
    }
}
