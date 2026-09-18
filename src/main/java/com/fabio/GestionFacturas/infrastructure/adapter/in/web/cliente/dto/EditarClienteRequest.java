package com.fabio.GestionFacturas.infrastructure.adapter.in.web.cliente.dto;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;

/** Request DTO for editing a client's mutable fields. nif isn't here — it can't be changed after creation. */
public record EditarClienteRequest(
        @NotBlank String nombre,
        @Email String email,
        String telefono
) {}
