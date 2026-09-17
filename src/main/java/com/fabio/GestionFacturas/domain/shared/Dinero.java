package com.fabio.GestionFacturas.domain.shared;

import java.math.BigDecimal;
import java.util.Objects;

/**
 * Value object wrapping a monetary amount + ISO currency code, so money is never a bare BigDecimal.
 * The compact constructor below runs on every instance (including record deserialization),
 * enforcing invariants (max 2 decimals, valid 3-letter currency) at construction time.
 */
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
