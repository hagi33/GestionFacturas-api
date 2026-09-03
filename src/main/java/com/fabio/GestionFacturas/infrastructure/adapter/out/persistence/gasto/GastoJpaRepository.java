package com.fabio.GestionFacturas.infrastructure.adapter.out.persistence.gasto;

import com.fabio.GestionFacturas.domain.gasto.Gasto;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface GastoJpaRepository extends JpaRepository<GastoJpaEntity, Long> {

    List<GastoJpaEntity> findByUsuarioId(Long usuarioId);


}
