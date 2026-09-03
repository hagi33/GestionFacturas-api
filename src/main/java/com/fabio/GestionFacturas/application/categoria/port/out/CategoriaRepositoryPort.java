package com.fabio.GestionFacturas.application.categoria.port.out;

import com.fabio.GestionFacturas.domain.categoria.Categoria;

import java.util.List;
import java.util.Optional;

public interface CategoriaRepositoryPort {

    List<Categoria> buscarTodas();

    Optional<Categoria> buscarPorId(Long id);

}
