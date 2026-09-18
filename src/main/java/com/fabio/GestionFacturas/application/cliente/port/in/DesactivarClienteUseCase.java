package com.fabio.GestionFacturas.application.cliente.port.in;

/**
 * Inbound port for soft-deleting a client (sets activo=false, row is kept). Implemented by
 * {@code DesactivarClienteService} — currently a TDD stub, see that class's Javadoc.
 */
public interface DesactivarClienteUseCase {

    void desactivar(ComandoDesactivarCliente comando);

    record ComandoDesactivarCliente(Long clienteId, Long usuarioId) {}
}
