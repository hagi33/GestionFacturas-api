package com.fabio.GestionFacturas.application.cliente.service;

import com.fabio.GestionFacturas.application.cliente.port.in.ConsultarClientesUseCase;
import com.fabio.GestionFacturas.application.cliente.port.out.ClienteRepositoryPort;
import com.fabio.GestionFacturas.domain.cliente.Cliente;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;

/** Implements the read-side {@link ConsultarClientesUseCase}, backed by {@link ClienteRepositoryPort}. */
@Service
public class ConsultarClientesService implements ConsultarClientesUseCase {

    private final ClienteRepositoryPort clienteRepository;

    public ConsultarClientesService(ClienteRepositoryPort clienteRepository) {
        this.clienteRepository = clienteRepository;
    }

    @Override
    @Transactional(readOnly = true)
    public List<Cliente> listarPorUsuario(Long usuarioId) {
        return clienteRepository.buscarPorUsuario(usuarioId);
    }

    @Override
    @Transactional(readOnly = true)
    public Optional<Cliente> obtenerPorId(Long clienteId, Long usuarioId) {
        Optional<Cliente> cliente = clienteRepository.buscarPorId(clienteId);
        if (cliente.isEmpty()) {
            return Optional.empty();
        }
        // Access control: a cliente that exists but belongs to someone else is reported as absent, not forbidden
        if (!cliente.get().getUsuarioId().equals(usuarioId)) {
            return Optional.empty();
        }
        return cliente;
    }
}
