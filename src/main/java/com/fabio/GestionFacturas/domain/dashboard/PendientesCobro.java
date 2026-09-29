package com.fabio.GestionFacturas.domain.dashboard;

import com.fabio.GestionFacturas.domain.ingreso.Ingreso;
import com.fabio.GestionFacturas.domain.shared.Dinero;

import java.util.List;

/**
 * Ingresos still awaiting collection, with the amount owed.
 *
 * @param ingresos the PENDIENTE ingresos, in the order they were given
 * @param total    sum of their total (with IVA); never null, an absent amount is zero
 */
public record PendientesCobro(List<Ingreso> ingresos, Dinero total) {
}
