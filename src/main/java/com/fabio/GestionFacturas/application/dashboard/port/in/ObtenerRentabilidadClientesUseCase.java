package com.fabio.GestionFacturas.application.dashboard.port.in;

import com.fabio.GestionFacturas.domain.dashboard.RentabilidadCliente;

import java.time.LocalDate;
import java.util.List;

/** Inbound port for the dashboard's per-client profitability. Implemented by {@code RentabilidadClientesService}. */
public interface ObtenerRentabilidadClientesUseCase {

    List<RentabilidadCliente> obtenerRentabilidad(ComandoObtenerRentabilidad comando);

    record ComandoObtenerRentabilidad(Long usuarioId, LocalDate desde, LocalDate hasta) {}
}
