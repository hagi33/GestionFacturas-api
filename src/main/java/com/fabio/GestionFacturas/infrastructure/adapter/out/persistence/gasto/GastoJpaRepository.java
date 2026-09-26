package com.fabio.GestionFacturas.infrastructure.adapter.out.persistence.gasto;

import com.fabio.GestionFacturas.domain.gasto.Gasto;
import org.springframework.data.jpa.repository.JpaRepository;

import java.time.LocalDate;
import java.util.List;

/** Spring Data repository — CRUD comes from {@code JpaRepository}; only used by {@code GastoPersistenceAdapter}. */
public interface GastoJpaRepository extends JpaRepository<GastoJpaEntity, Long> {

    // Spring Data derives the query from the method name — no SQL/JPQL written here
    List<GastoJpaEntity> findByUsuarioId(Long usuarioId);

    // Between is inclusive on both ends: fechaEmision in [desde, hasta]
    List<GastoJpaEntity> findByUsuarioIdAndFechaEmisionBetween(Long usuarioId, LocalDate desde, LocalDate hasta);


}
