package com.fabio.GestionFacturas.application.gasto.port.out;

import com.fabio.GestionFacturas.domain.gasto.Gasto;

import java.util.List;
import java.util.Optional;

public interface GastoRepositoryPort {

    Gasto guardar(Gasto gasto);

    Optional <Gasto> buscarPorId(Long id);

    List<Gasto> buscarPorUsuario(Long usuarioId);

}
