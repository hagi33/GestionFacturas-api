package com.fabio.GestionFacturas.domain.gasto;

public class GastoInvalidoException extends RuntimeException {
    public GastoInvalidoException(String message) {
        super(message);
    }
}
