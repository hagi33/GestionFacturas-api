package com.fabio.GestionFacturas.infrastructure.adapter.out.persistence.ingreso;

import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

/** Spring Data repository — CRUD comes from {@code JpaRepository}; only used by {@code IngresoPersistenceAdapter}. */
public interface IngresoJpaRepository extends JpaRepository<IngresoJpaEntity, Long> {

    // Spring Data derives the query from the method name — no SQL/JPQL written here
    List<IngresoJpaEntity> findByUsuarioId(Long usuarioId);

}
