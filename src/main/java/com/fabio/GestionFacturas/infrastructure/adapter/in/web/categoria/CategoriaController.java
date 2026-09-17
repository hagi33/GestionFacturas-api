package com.fabio.GestionFacturas.infrastructure.adapter.in.web.categoria;

import com.fabio.GestionFacturas.application.categoria.port.in.ConsultarCategoriaUseCase;
import com.fabio.GestionFacturas.infrastructure.adapter.in.web.categoria.dto.CategoriaResponse;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

/**
 * Web adapter for categories. Minimal example of the full chain:
 * controller -> ConsultarCategoriaUseCase (port) -> ConsultarCategoriaService -> CategoriaRepositoryPort
 * (port) -> CategoriaPersistenceAdapter (JPA) -> domain Categoria -> CategoriaWebMapper -> response DTO.
 */
@RestController
@RequestMapping("/api/categorias")
public class CategoriaController {

    private final ConsultarCategoriaUseCase consultarCategoriaUseCase;

    public CategoriaController(ConsultarCategoriaUseCase consultarCategoriaUseCase) {
        this.consultarCategoriaUseCase = consultarCategoriaUseCase;
    }

    @GetMapping
    public List<CategoriaResponse> listar() {
        return consultarCategoriaUseCase.listarTodas()
                .stream()
                .map(CategoriaWebMapper::aRespuesta)
                .toList();
    }
}
