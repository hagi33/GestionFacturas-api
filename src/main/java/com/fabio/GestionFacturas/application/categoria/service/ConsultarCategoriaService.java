package com.fabio.GestionFacturas.application.categoria.service;

import com.fabio.GestionFacturas.application.categoria.port.in.ConsultarCategoriaUseCase;
import com.fabio.GestionFacturas.application.categoria.port.out.CategoriaRepositoryPort;

import com.fabio.GestionFacturas.domain.categoria.Categoria;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

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
