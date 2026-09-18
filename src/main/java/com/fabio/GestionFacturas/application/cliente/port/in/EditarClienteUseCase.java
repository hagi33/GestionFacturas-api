package com.fabio.GestionFacturas.application.cliente.port.in;

import com.fabio.GestionFacturas.domain.cliente.Cliente;

/**
 * Inbound port for editing a client's mutable data. Implemented by {@code EditarClienteService},
 * which owns the ownership check (a client belonging to another user is rejected, not edited).
 */
public interface EditarClienteUseCase {

    Cliente editar(ComandoEditarCliente comando);

    /** nif is intentionally absent here — it's immutable once the client is created. */
    record ComandoEditarCliente(
            Long clienteId,
            Long usuarioId,
            String nombre,
            String email,
            String telefono
    ) {}
}
