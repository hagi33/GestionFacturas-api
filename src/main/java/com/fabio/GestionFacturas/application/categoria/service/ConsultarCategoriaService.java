package com.fabio.GestionFacturas.application.categoria.service;

import com.fabio.GestionFacturas.application.categoria.port.in.ConsultarCategoriaPort;
import com.fabio.GestionFacturas.application.categoria.port.out.CategotiaRepositoryPort;

import com.fabio.GestionFacturas.domain.categoria.Categoria;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
public class ConsultarCategoriaService implements ConsultarCategoriaPort{


    private final CategotiaRepositoryPort categotiaRepositoryPort;

    public ConsultarCategoriaService(CategotiaRepositoryPort categotiaRepositoryPort) {
        this.categotiaRepositoryPort = categotiaRepositoryPort;
    }


    @Override
    @Transactional(readOnly = true)
    public List<Categoria> listarTodas() {
        return categotiaRepositoryPort.buscarTodas();
    }
}
