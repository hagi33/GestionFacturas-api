package com.fabio.GestionFacturas.infrastructure.adapter.in.web.cliente.dto;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;

/** Request DTO for client registration — the web contract, never the domain Cliente itself. */
public record CrearClienteRequest(
        @NotBlank String nombre,
        @NotBlank String nif,
        @Email String email,
        String telefono
) {}
