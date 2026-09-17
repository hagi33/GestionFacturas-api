package com.fabio.GestionFacturas.application.gasto.port.in;

import com.fabio.GestionFacturas.domain.gasto.Gasto;

import java.util.List;
import java.util.Optional;

/** Inbound port for reading expenses. Implemented by {@code ConsultarGastosService}. */
public interface ConsultarGastosUseCase {

    List<Gasto> listarPorUsuario(Long usuarioId);

    /** Optional, not an exception: a gasto belonging to another user is treated as "not found" (access control). */
    Optional<Gasto> obtenerPorId(Long gastoId, Long usuarioId);

}
