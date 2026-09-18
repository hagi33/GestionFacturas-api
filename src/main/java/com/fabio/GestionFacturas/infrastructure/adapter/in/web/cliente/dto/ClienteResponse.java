package com.fabio.GestionFacturas.infrastructure.adapter.in.web.cliente.dto;

/** Response DTO built by {@code ClienteWebMapper} from a domain Cliente — the outbound half of the web contract. */
public record ClienteResponse(
        Long id,
        Long usuarioId,
        String nombre,
        String nif,
        String email,
        String telefono,
        boolean activo
) {}
