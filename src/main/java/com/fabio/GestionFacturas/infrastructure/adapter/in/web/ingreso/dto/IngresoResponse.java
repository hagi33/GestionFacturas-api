package com.fabio.GestionFacturas.infrastructure.adapter.in.web.ingreso.dto;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;

/** Response DTO built by {@code IngresoWebMapper} from a domain Ingreso — the outbound half of the web contract. */
public record IngresoResponse(
        Long id,
        Long usuarioId,
        Long clienteId,
        String concepto,
        LocalDate fechaEmision,
        BigDecimal baseImponible,
        BigDecimal iva,
        BigDecimal total,
        String moneda,
        String estadoCobro,
        LocalDate fechaCobro,
        LocalDateTime creadoEn
) {}
