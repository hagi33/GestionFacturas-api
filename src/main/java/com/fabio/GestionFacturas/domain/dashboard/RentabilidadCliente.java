package com.fabio.GestionFacturas.domain.dashboard;

/**
 * Profitability of one client in a period: the same figures as {@link ResumenPeriodo}, restricted to
 * the ingresos and gastos linked to that client.
 *
 * @param clienteId the client, or {@code null} for the "sin cliente" entry (gastos without a client)
 * @param resumen   the client's figures; never null
 */
public record RentabilidadCliente(Long clienteId, ResumenPeriodo resumen) {

    public boolean esSinCliente() {
        return clienteId == null;
    }
}
