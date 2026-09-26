package com.fabio.GestionFacturas.infrastructure.adapter.in.web.dashboard.dto;

import java.math.BigDecimal;

/** Response body for GET /api/dashboard/resumen — the period summary figures (total with IVA, base without). */
public record ResumenPeriodoResponse(
        BigDecimal facturadoTotal,
        BigDecimal facturadoBase,
        BigDecimal cobradoTotal,
        BigDecimal cobradoBase,
        BigDecimal gastosTotal,
        BigDecimal gastosBase,
        BigDecimal beneficioCajaTotal,
        BigDecimal beneficioCajaBase,
        BigDecimal beneficioFacturadoTotal,
        BigDecimal beneficioFacturadoBase) {
}
