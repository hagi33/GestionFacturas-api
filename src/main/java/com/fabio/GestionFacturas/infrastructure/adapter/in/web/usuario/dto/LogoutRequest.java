package com.fabio.GestionFacturas.infrastructure.adapter.in.web.usuario.dto;

import jakarta.validation.constraints.NotBlank;

/** Request DTO for logout. */
public record LogoutRequest(
        @NotBlank String refreshToken
) {
}
