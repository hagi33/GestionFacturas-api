package com.fabio.GestionFacturas.infrastructure.adapter.in.web.usuario.dto;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;

/** Sign-up request DTO — carries the raw password only as far as the controller/service, which hashes it. */
public record RegistrarRequest(

        @NotBlank @Email String email,
        @NotBlank String nombre,
        @NotBlank String password
) {
}
