package com.fabio.GestionFacturas.application.cliente.service;

import com.fabio.GestionFacturas.application.cliente.port.in.DesactivarClienteUseCase;
import com.fabio.GestionFacturas.application.cliente.port.out.ClienteRepositoryPort;
import com.fabio.GestionFacturas.domain.cliente.Cliente;
import com.fabio.GestionFacturas.domain.cliente.ClienteNoEncontradoException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Optional;

/** Implements {@link DesactivarClienteUseCase}. */
@Service
public class DesactivarClienteService implements DesactivarClienteUseCase {

    private final ClienteRepositoryPort clienteRepositoryPort;

    public DesactivarClienteService(ClienteRepositoryPort clienteRepositoryPort) {
        this.clienteRepositoryPort = clienteRepositoryPort;
    }

    @Override
    @Transactional
    public void desactivar(ComandoDesactivarCliente comando) {
        Optional<Cliente> clienteOpt = clienteRepositoryPort.buscarPorId(comando.clienteId());
        if (clienteOpt.isEmpty()) {
            throw new ClienteNoEncontradoException("El cliente no existe");
        }

        Cliente cliente = clienteOpt.get();

        // Access control: don't reveal that a cliente exists if it belongs to someone else
        if (!cliente.getUsuarioId().equals(comando.usuarioId())) {
            throw new ClienteNoEncontradoException("El cliente no existe");
        }

        cliente.desactivar();
        clienteRepositoryPort.guardar(cliente);
    }
}
