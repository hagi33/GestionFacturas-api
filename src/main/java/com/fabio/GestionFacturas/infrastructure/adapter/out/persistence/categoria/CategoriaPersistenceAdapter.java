package com.fabio.GestionFacturas.infrastructure.adapter.out.persistence.categoria;

import com.fabio.GestionFacturas.application.categoria.port.out.CategoriaRepositoryPort;
import com.fabio.GestionFacturas.domain.categoria.Categoria;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.Optional;

/** Implements {@link CategoriaRepositoryPort} with Spring Data JPA — see {@code CategoriaController} for the full inbound chain. */
@Component
public class CategoriaPersistenceAdapter implements CategoriaRepositoryPort {

    private final CategoriaJpaRepository categoriaJpaRepository;

    public CategoriaPersistenceAdapter(CategoriaJpaRepository categoriaJpaRepository) {
        this.categoriaJpaRepository = categoriaJpaRepository;
    }

    @Override
    public List<Categoria> buscarTodas() {
        return categoriaJpaRepository.findAll()
                .stream()
                .map(CategoriaMapper::aDominio)
                .toList();
    }

    @Override
    public Optional<Categoria> buscarPorId(Long id) {
        return categoriaJpaRepository.findById(id).map(CategoriaMapper::aDominio);
    }
}
