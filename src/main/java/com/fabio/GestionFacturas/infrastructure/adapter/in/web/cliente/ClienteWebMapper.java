package com.fabio.GestionFacturas.infrastructure.adapter.in.web.cliente;

import com.fabio.GestionFacturas.application.cliente.port.in.CrearClienteUseCase.ComandoCrearCliente;
import com.fabio.GestionFacturas.application.cliente.port.in.EditarClienteUseCase.ComandoEditarCliente;
import com.fabio.GestionFacturas.domain.cliente.Cliente;
import com.fabio.GestionFacturas.infrastructure.adapter.in.web.cliente.dto.ClienteResponse;
import com.fabio.GestionFacturas.infrastructure.adapter.in.web.cliente.dto.CrearClienteRequest;
import com.fabio.GestionFacturas.infrastructure.adapter.in.web.cliente.dto.EditarClienteRequest;

/**
 * Converts between web DTOs and the domain {@link Cliente} — the boundary that keeps the
 * three models (DTO / domain / JPA entity) separate. Static/stateless: no port dependency, pure mapping.
 */
public class ClienteWebMapper {

    public ClienteWebMapper() {
    }

    public static ComandoCrearCliente aComandoCrear(CrearClienteRequest request, Long usuarioId) {
        return new ComandoCrearCliente(
                usuarioId,
                request.nombre(),
                request.nif(),
                request.email(),
                request.telefono()
        );
    }

    public static ComandoEditarCliente aComandoEditar(Long clienteId, EditarClienteRequest request, Long usuarioId) {
        return new ComandoEditarCliente(
                clienteId,
                usuarioId,
                request.nombre(),
                request.email(),
                request.telefono()
        );
    }

    public static ClienteResponse aRespuesta(Cliente cliente) {
        return new ClienteResponse(
                cliente.getId(),
                cliente.getUsuarioId(),
                cliente.getNombre(),
                cliente.getNif(),
                cliente.getEmail(),
                cliente.getTelefono(),
                cliente.isActivo()
        );
    }
}
