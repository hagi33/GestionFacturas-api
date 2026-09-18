package com.fabio.GestionFacturas.application.cliente.service;

import com.fabio.GestionFacturas.application.cliente.port.in.CrearClienteUseCase;
import com.fabio.GestionFacturas.application.cliente.port.out.ClienteRepositoryPort;
import com.fabio.GestionFacturas.domain.cliente.Cliente;
import com.fabio.GestionFacturas.domain.cliente.ClienteDuplicadoException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Optional;

/** Implements {@link CrearClienteUseCase}. */
@Service
public class CrearClienteService implements CrearClienteUseCase {

    private final ClienteRepositoryPort clienteRepositoryPort;

    public CrearClienteService(ClienteRepositoryPort clienteRepositoryPort) {
        this.clienteRepositoryPort = clienteRepositoryPort;
    }

    @Override
    @Transactional
    public Cliente crear(ComandoCrearCliente comando) {
        Optional<Cliente> existente = clienteRepositoryPort.buscarPorUsuarioYNif(comando.usuarioId(), comando.nif());
        if (existente.isPresent()) {
            throw new ClienteDuplicadoException("Ya existe un cliente con ese NIF");
        }

        Cliente cliente = Cliente.crearActivo(comando.usuarioId(), comando.nombre(), comando.nif(),
                comando.email(), comando.telefono());

        return clienteRepositoryPort.guardar(cliente);
    }
}
