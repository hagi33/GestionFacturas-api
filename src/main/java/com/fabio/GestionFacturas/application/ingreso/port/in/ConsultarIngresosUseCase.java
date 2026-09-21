package com.fabio.GestionFacturas.application.ingreso.port.in;

import com.fabio.GestionFacturas.domain.ingreso.Ingreso;

import java.util.List;
import java.util.Optional;

/** Inbound port for reading invoices. Implemented by {@code ConsultarIngresosService}. */
public interface ConsultarIngresosUseCase {

    List<Ingreso> listarPorUsuario(Long usuarioId);

    /** Optional, not an exception: an ingreso belonging to another user is treated as "not found" (access control). */
    Optional<Ingreso> obtenerPorId(Long ingresoId, Long usuarioId);

}
