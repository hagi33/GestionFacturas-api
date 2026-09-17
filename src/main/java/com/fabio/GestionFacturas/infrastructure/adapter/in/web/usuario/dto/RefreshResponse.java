package com.fabio.GestionFacturas.infrastructure.adapter.in.web.usuario.dto;

/** Response DTO for token refresh — only a new access token, no refresh token (no rotation yet). */
public record RefreshResponse(
        String accessToken
) {
}
