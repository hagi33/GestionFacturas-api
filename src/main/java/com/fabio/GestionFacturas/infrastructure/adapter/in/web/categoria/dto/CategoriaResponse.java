package com.fabio.GestionFacturas.infrastructure.adapter.in.web.categoria.dto;

public record CategoriaResponse(
        Long id,
        String nombre,
        boolean deduciblePorDefecto
) {}
