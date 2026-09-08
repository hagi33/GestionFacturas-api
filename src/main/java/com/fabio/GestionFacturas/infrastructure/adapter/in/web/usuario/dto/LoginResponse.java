package com.fabio.GestionFacturas.infrastructure.adapter.in.web.usuario.dto;

public record LoginResponse(

        String accessToken,
        Long usuarioId,
        String email

) {
}
