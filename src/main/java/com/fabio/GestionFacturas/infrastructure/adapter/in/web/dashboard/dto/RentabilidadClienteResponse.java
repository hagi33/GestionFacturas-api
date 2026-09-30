package com.fabio.GestionFacturas.infrastructure.adapter.in.web.dashboard.dto;

/**
 * One item of GET /api/dashboard/rentabilidad-clientes. {@code clienteId} is null for the
 * "sin cliente" entry (gastos not linked to any client).
 */
public record RentabilidadClienteResponse(
        Long clienteId,
        ResumenPeriodoResponse resumen) {
}
