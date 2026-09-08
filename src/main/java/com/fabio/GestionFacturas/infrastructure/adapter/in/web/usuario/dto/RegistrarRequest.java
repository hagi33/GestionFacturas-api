package com.fabio.GestionFacturas.infrastructure.adapter.in.web.usuario.dto;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;

public record RegistrarRequest(

        @NotBlank @Email String email,
        @NotBlank String nombre,
        @NotBlank String password
) {
}
