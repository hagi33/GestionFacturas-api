package com.fabio.GestionFacturas.infrastructure.adapter.in.web.dashboard.dto;

import java.math.BigDecimal;
import java.util.List;

/** Response body for GET /api/dashboard/pendientes-cobro — pending ingresos (oldest first) and the total owed (with IVA). */
public record PendientesCobroResponse(
        List<IngresoPendienteResponse> ingresos,
        BigDecimal total) {
}
