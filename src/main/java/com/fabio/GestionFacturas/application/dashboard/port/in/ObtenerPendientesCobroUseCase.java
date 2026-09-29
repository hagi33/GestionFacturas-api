package com.fabio.GestionFacturas.application.dashboard.port.in;

import com.fabio.GestionFacturas.domain.dashboard.PendientesCobro;

/** Inbound port for the dashboard's pending-collection list. Implemented by {@code PendientesCobroService}. */
public interface ObtenerPendientesCobroUseCase {

    PendientesCobro obtenerPendientes(ComandoObtenerPendientesCobro comando);

    record ComandoObtenerPendientesCobro(Long usuarioId) {}
}
