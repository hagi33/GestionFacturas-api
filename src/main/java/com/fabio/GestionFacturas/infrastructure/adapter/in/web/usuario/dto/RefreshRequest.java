package com.fabio.GestionFacturas.infrastructure.adapter.in.web.usuario.dto;

import jakarta.validation.constraints.NotBlank;

public record RefreshRequest(
        @NotBlank String refreshToken
) {
}
