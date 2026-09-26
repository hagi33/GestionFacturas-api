package com.fabio.GestionFacturas.application.dashboard.port.out;

import com.fabio.GestionFacturas.domain.gasto.Gasto;
import com.fabio.GestionFacturas.domain.ingreso.Ingreso;

import java.time.LocalDate;
import java.util.List;

/**
 * Outbound port for the dashboard's read queries. Only the user's own data, filtered by
 * fechaEmision within [desde, hasta] (both inclusive). Implemented by {@code DashboardPersistenceAdapter}.
 */
public interface DashboardConsultaPort {

    List<Gasto> buscarGastosPorPeriodo(Long usuarioId, LocalDate desde, LocalDate hasta);

    List<Ingreso> buscarIngresosPorPeriodo(Long usuarioId, LocalDate desde, LocalDate hasta);
}
