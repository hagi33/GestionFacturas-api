package com.fabio.GestionFacturas.infrastructure.adapter.in.web.usuario.dto;

import jakarta.validation.constraints.NotBlank;

/** Request DTO for the token-refresh endpoint. */
public record RefreshRequest(
        @NotBlank String refreshToken
) {
}
