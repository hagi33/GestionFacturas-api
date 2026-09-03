package com.fabio.GestionFacturas.infrastructure.adapter.out.persistence.categoria;

import com.fabio.GestionFacturas.domain.categoria.Categoria;

public class CategoriaMapper {

    public CategoriaMapper() {
    }

    public static CategoriaJpaEntity aEntidad(Categoria categoria) {
        return new CategoriaJpaEntity(
                categoria.getId(),
                categoria.getNombre(),
                categoria.isDeduciblePorDefecto()
        );
    }

    public static Categoria aDominio(CategoriaJpaEntity entidad) {
        return new Categoria(
                entidad.getId(),
                entidad.getNombre(),
                entidad.isDeduciblePorDefecto()
        );
    }
}
