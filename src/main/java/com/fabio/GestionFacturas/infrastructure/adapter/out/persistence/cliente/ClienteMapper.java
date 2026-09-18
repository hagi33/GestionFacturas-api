package com.fabio.GestionFacturas.infrastructure.adapter.out.persistence.cliente;

import com.fabio.GestionFacturas.domain.cliente.Cliente;

/**
 * Converts between the domain {@link Cliente} and {@link ClienteJpaEntity} — the third of the
 * three models (DTO / domain / JPA entity), used only by {@code ClientePersistenceAdapter}.
 */
public class ClienteMapper {

    public ClienteMapper() {
    }

    public static ClienteJpaEntity aEntidad(Cliente cliente) {
        return new ClienteJpaEntity(
                cliente.getId(),
                cliente.getUsuarioId(),
                cliente.getNombre(),
                cliente.getNif(),
                cliente.getEmail(),
                cliente.getTelefono(),
                cliente.isActivo()
        );
    }

    public static Cliente aDominio(ClienteJpaEntity entidad) {
        return new Cliente(
                entidad.getId(),
                entidad.getUsuarioId(),
                entidad.getNombre(),
                entidad.getNif(),
                entidad.getEmail(),
                entidad.getTelefono(),
                entidad.isActivo()
        );
    }
}
