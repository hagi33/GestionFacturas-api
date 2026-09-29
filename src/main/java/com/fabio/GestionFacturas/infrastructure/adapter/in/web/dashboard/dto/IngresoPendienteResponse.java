package com.fabio.GestionFacturas.infrastructure.adapter.in.web.dashboard.dto;

import java.math.BigDecimal;
import java.time.LocalDate;

/** One item of GET /api/dashboard/pendientes-cobro — an ingreso still awaiting collection. */
public record IngresoPendienteResponse(
        Long id,
        Long clienteId,
        String concepto,
        LocalDate fechaEmision,
        BigDecimal baseImponible,
        BigDecimal iva,
        BigDecimal total) {
}
