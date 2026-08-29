package com.fabio.GestionFacturas.domain.factura;

public class FacturaInvalidaException extends RuntimeException {
    public FacturaInvalidaException(String message) {
        super(message);
    }
}
