package com.fabio.GestionFacturas.application.cliente.service;

import com.fabio.GestionFacturas.application.cliente.port.in.EditarClienteUseCase;
import com.fabio.GestionFacturas.application.cliente.port.out.ClienteRepositoryPort;
import com.fabio.GestionFacturas.domain.cliente.Cliente;
import com.fabio.GestionFacturas.domain.cliente.ClienteInvalidoException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Optional;

/**
 * Implements {@link EditarClienteUseCase}. A missing client and one owned by another user are
 * both reported as {@link ClienteInvalidoException} (400) — unlike the read side, editing has
 * no natural "empty" return, so this is the simplest reuse of an existing mapped exception.
 */
@Service
public class EditarClienteService implements EditarClienteUseCase {

    private final ClienteRepositoryPort clienteRepositoryPort;

    public EditarClienteService(ClienteRepositoryPort clienteRepositoryPort) {
        this.clienteRepositoryPort = clienteRepositoryPort;
    }

    @Override
    @Transactional
    public Cliente editar(ComandoEditarCliente comando) {
        Optional<Cliente> clienteOpt = clienteRepositoryPort.buscarPorId(comando.clienteId());
        if (clienteOpt.isEmpty()) {
            throw new ClienteInvalidoException("El cliente no existe");
        }

        Cliente cliente = clienteOpt.get();

        // Access control: don't reveal that a cliente exists if it belongs to someone else
        if (!cliente.getUsuarioId().equals(comando.usuarioId())) {
            throw new ClienteInvalidoException("El cliente no existe");
        }

        cliente.actualizarDatos(comando.nombre(), comando.email(), comando.telefono());

        return clienteRepositoryPort.guardar(cliente);
    }
}
