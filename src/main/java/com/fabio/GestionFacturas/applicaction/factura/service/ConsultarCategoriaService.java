package com.fabio.GestionFacturas.applicaction.factura.service;

import com.fabio.GestionFacturas.applicaction.categoria.port.in.ConsultarCategoriaPort;
import com.fabio.GestionFacturas.applicaction.categoria.port.out.CategotiaRepositoryPort;

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
