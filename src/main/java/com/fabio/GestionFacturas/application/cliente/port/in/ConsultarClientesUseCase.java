package com.fabio.GestionFacturas.application.cliente.port.in;

import com.fabio.GestionFacturas.domain.cliente.Cliente;

import java.util.List;
import java.util.Optional;

/** Inbound port for reading clients. Implemented by {@code ConsultarClientesService}. */
public interface ConsultarClientesUseCase {

    List<Cliente> listarPorUsuario(Long usuarioId);

    /** Optional, not an exception: a cliente belonging to another user is treated as "not found" (access control). */
    Optional<Cliente> obtenerPorId(Long clienteId, Long usuarioId);
}
