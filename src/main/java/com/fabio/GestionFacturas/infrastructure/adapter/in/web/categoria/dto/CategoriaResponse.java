package com.fabio.GestionFacturas.infrastructure.adapter.in.web.categoria.dto;

/** Response DTO for the category listing endpoint. */
public record CategoriaResponse(
        Long id,
        String nombre,
        boolean deduciblePorDefecto
) {}
