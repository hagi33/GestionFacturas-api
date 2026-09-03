package com.fabio.GestionFacturas.application.gasto.port.in;

import com.fabio.GestionFacturas.domain.gasto.Gasto;

import java.util.List;
import java.util.Optional;

public interface ConsultarGastosUseCase {

    List<Gasto> listarPorUsuario(Long usuarioId);

    Optional<Gasto> obtenerPorId(Long gastoId, Long usuarioId);

}
