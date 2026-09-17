package com.fabio.GestionFacturas.application.categoria.service;

import com.fabio.GestionFacturas.application.categoria.port.in.ConsultarCategoriaUseCase;
import com.fabio.GestionFacturas.application.categoria.port.out.CategoriaRepositoryPort;

import com.fabio.GestionFacturas.domain.categoria.Categoria;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

/**
 * Implements {@link ConsultarCategoriaUseCase}. Straightforward pass-through to the repository
 * port — a good minimal example of the controller -> use-case-port -> service -> repo-port -> adapter chain.
 */
@Service
public class ConsultarCategoriaService implements ConsultarCategoriaUseCase {


    private final CategoriaRepositoryPort categoriaRepositoryPort;

    public ConsultarCategoriaService(CategoriaRepositoryPort categoriaRepositoryPort) {
        this.categoriaRepositoryPort = categoriaRepositoryPort;
    }


    @Override
    @Transactional(readOnly = true)
    public List<Categoria> listarTodas() {
        return categoriaRepositoryPort.buscarTodas();
    }
}
