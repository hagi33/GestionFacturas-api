package com.fabio.GestionFacturas.infrastructure.adapter.in.web.usuario.dto;

/** Response DTO for the registered user — deliberately excludes the password hash. */
public record UsuarioResponse(

        Long id,
        String email,
        String nombre
) {
}
