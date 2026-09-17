package com.fabio.GestionFacturas.infrastructure.adapter.in.web.gasto.dto;

import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.PositiveOrZero;

import java.math.BigDecimal;
import java.time.LocalDate;

/** Request DTO for manual expense creation — the web contract, never the domain Gasto itself. */
public record CrearGastoRequest(
        String emisor,
        LocalDate fechaEmision,
        @PositiveOrZero BigDecimal baseImponible,
        @PositiveOrZero BigDecimal iva,
        @NotNull @PositiveOrZero BigDecimal total,
        String moneda
        ) {}
