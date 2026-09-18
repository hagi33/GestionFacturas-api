package com.fabio.GestionFacturas.application.cliente.port.in;

import com.fabio.GestionFacturas.domain.cliente.Cliente;

/**
 * Inbound port for registering a new client. {@code ClienteController} depends only on this
 * interface, never on the concrete service — {@code CrearClienteService} implements it and is
 * injected by Spring at runtime.
 */
public interface CrearClienteUseCase {

    Cliente crear(ComandoCrearCliente comando);

    /** Command object: the only shape the web layer is allowed to hand into the application layer. */
    record ComandoCrearCliente(
            Long usuarioId,
            String nombre,
            String nif,
            String email,
            String telefono
    ) {}
}
