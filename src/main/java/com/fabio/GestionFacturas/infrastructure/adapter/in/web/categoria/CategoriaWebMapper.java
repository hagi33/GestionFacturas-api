package com.fabio.GestionFacturas.infrastructure.adapter.in.web.categoria;

import com.fabio.GestionFacturas.domain.categoria.Categoria;
import com.fabio.GestionFacturas.infrastructure.adapter.in.web.categoria.dto.CategoriaResponse;

/** Converts domain {@link Categoria} to its response DTO — keeps the domain object out of the HTTP contract. */
public class CategoriaWebMapper {

    public CategoriaWebMapper() {
    }

    public static CategoriaResponse aRespuesta(Categoria categoria) {
        return new CategoriaResponse(
                categoria.getId(),
                categoria.getNombre(),
                categoria.isDeduciblePorDefecto()
        );
    }
}
