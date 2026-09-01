package com.fabio.GestionFacturas.infrastructure.adapter.out.persistence.factura;

import com.fabio.GestionFacturas.domain.factura.Factura;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface FacturaJpaRepository extends JpaRepository<FacturaJpaEntity, Long> {

    List<FacturaJpaEntity> findByUsuarioId(Long usuarioId);


}
