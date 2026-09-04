package com.fabio.GestionFacturas.domain.shared;

import java.math.BigDecimal;
import java.util.Objects;

public record Dinero(BigDecimal cantidad, String moneda) {

    public Dinero {
        Objects.requireNonNull(cantidad, "La cantidad no puede ser nula");
        Objects.requireNonNull(moneda, "La moneda no puede ser nula");

        if (cantidad.scale() > 2){
            throw new IllegalArgumentException("La cantidad no puede tener más de 2 decimales");
        }
        if (moneda.length() != 3){
            throw new IllegalArgumentException("La moneda debe ser un códio ISO de 3 dígitos");
        }
    }

    public static Dinero deEuros(BigDecimal cantidad){
        return new Dinero(cantidad, "EUR");
    }



}
