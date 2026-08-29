package com.fabio.GestionFacturas.applicaction.factura.port.in;

import com.fabio.GestionFacturas.domain.factura.Factura;

import java.util.List;
import java.util.Optional;

public interface ConsultarFacturasUseCase {

    List<Factura> listarPorUsuario(Long usuarioId);

    Optional<Factura> obtenerPorId(Long facturaId, Long usuarioId);

}
