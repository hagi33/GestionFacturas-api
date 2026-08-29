package com.fabio.GestionFacturas.applicaction.factura.port.out;

import com.fabio.GestionFacturas.domain.factura.Factura;

import java.util.List;
import java.util.Optional;

public interface FacturaRepositoryPort {

    Factura guardar(Factura factura);

    Optional <Factura> buscarPorId(Long id);

    List<Factura> buscarPorUsuario(Long usuarioId);

}
