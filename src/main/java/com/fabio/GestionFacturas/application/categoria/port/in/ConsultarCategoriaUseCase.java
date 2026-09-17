package com.fabio.GestionFacturas.application.categoria.port.in;

import com.fabio.GestionFacturas.domain.categoria.Categoria;

import java.util.List;

/** Inbound port consumed by {@code CategoriaController}; implemented by {@code ConsultarCategoriaService}. */
public interface ConsultarCategoriaUseCase {

    List<Categoria> listarTodas();

}
