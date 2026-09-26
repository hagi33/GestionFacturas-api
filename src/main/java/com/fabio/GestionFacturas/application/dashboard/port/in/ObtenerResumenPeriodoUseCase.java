package com.fabio.GestionFacturas.application.dashboard.port.in;

import com.fabio.GestionFacturas.domain.dashboard.ResumenPeriodo;

import java.time.LocalDate;

/** Inbound port for the dashboard period summary. Implemented by {@code ResumenPeriodoService}. */
public interface ObtenerResumenPeriodoUseCase {

    ResumenPeriodo obtenerResumen(ComandoObtenerResumen comando);

    record ComandoObtenerResumen(Long usuarioId, LocalDate desde, LocalDate hasta) {}
}
