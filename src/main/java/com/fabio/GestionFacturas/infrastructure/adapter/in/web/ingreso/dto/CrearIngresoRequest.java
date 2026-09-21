package com.fabio.GestionFacturas.infrastructure.adapter.in.web.ingreso.dto;

import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.PositiveOrZero;

import java.math.BigDecimal;
import java.time.LocalDate;

/** Request DTO for issuing a new invoice — the web contract, never the domain Ingreso itself. */
public record CrearIngresoRequest(
        Long clienteId,
        String concepto,
        LocalDate fechaEmision,
        @PositiveOrZero BigDecimal baseImponible,
        @PositiveOrZero BigDecimal iva,
        @NotNull @PositiveOrZero BigDecimal total,
        String moneda
) {}
