package com.fabio.GestionFacturas.infrastructure.adapter.out.persistence.cliente;

import com.fabio.GestionFacturas.application.cliente.port.out.ClienteRepositoryPort;
import com.fabio.GestionFacturas.domain.cliente.Cliente;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.Optional;

/**
 * Implements the outbound {@link ClienteRepositoryPort} with Spring Data JPA — the piece the
 * application layer never sees directly. Full round trip for a write:
 * domain Cliente -> ClienteMapper -> ClienteJpaEntity -> ClienteJpaRepository (Spring Data) -> DB,
 * then mapped straight back to domain before returning, so callers never touch the JPA type.
 */
@Component
public class ClientePersistenceAdapter implements ClienteRepositoryPort {

    private final ClienteJpaRepository clienteJpaRepository;

    public ClientePersistenceAdapter(ClienteJpaRepository clienteJpaRepository) {
        this.clienteJpaRepository = clienteJpaRepository;
    }

    @Override
    public Cliente guardar(Cliente cliente) {
        ClienteJpaEntity entidad = ClienteMapper.aEntidad(cliente);
        ClienteJpaEntity guardada = clienteJpaRepository.save(entidad);
        return ClienteMapper.aDominio(guardada);
    }

    @Override
    public Optional<Cliente> buscarPorId(Long id) {
        return clienteJpaRepository.findById(id).map(ClienteMapper::aDominio);
    }

    @Override
    public List<Cliente> buscarPorUsuario(Long usuarioId) {
        return clienteJpaRepository.findByUsuarioIdAndActivoTrue(usuarioId)
                .stream()
                .map(ClienteMapper::aDominio)
                .toList();
    }

    @Override
    public Optional<Cliente> buscarPorUsuarioYNif(Long usuarioId, String nif) {
        return clienteJpaRepository.findByUsuarioIdAndNif(usuarioId, nif).map(ClienteMapper::aDominio);
    }
}
