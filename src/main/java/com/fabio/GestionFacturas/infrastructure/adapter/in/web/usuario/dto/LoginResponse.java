package com.fabio.GestionFacturas.infrastructure.adapter.in.web.usuario.dto;

/** Login response DTO — the only place both tokens appear in plaintext; the client is expected to store them securely. */
public record LoginResponse(

        String accessToken,
        String refreshToken,
        Long usuarioId,
        String email

) {
}
