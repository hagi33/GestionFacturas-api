package com.fabio.GestionFacturas.infrastructure.adapter.out.persistence.factura;

import com.fabio.GestionFacturas.domain.factura.Factura;

import java.util.List;

public interface FacturaJpaRepository {

    List<FacturaJpaEntity> findByUsuarioId(Long usuarioId);

}
