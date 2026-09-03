package com.fabio.GestionFacturas.application.categoria.port.in;

import com.fabio.GestionFacturas.domain.categoria.Categoria;

import java.util.List;

public interface ConsultarCategoriaUseCase {

    List<Categoria> listarTodas();

}
