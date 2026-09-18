package com.fabio.GestionFacturas.application.cliente.port.out;

import com.fabio.GestionFacturas.domain.cliente.Cliente;

import java.util.List;
import java.util.Optional;

/**
 * Outbound port for Cliente persistence. Defined here (application layer) so services depend
 * only on this contract; {@code ClientePersistenceAdapter} (infrastructure) implements it with
 * JPA, keeping the dependency pointing inward per the hexagonal dependency rule.
 */
public interface ClienteRepositoryPort {

    Cliente guardar(Cliente cliente);

    Optional<Cliente> buscarPorId(Long id);

    /** Only active clients — inactive ones are excluded by the adapter's query, not filtered here. */
    List<Cliente> buscarPorUsuario(Long usuarioId);

    /** Used to enforce the one-NIF-per-user rule before creating a new client. */
    Optional<Cliente> buscarPorUsuarioYNif(Long usuarioId, String nif);
}
