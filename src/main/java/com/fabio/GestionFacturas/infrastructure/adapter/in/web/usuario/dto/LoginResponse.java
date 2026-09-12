package com.fabio.GestionFacturas.infrastructure.adapter.in.web.usuario.dto;

public record LoginResponse(

        String accessToken,
        String refreshToken,
        Long usuarioId,
        String email

) {
}
