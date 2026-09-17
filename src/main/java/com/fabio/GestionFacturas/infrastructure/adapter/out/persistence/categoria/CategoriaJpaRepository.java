package com.fabio.GestionFacturas.infrastructure.adapter.out.persistence.categoria;

import org.springframework.data.jpa.repository.JpaRepository;

/** Spring Data repository; standard CRUD is enough here, no custom queries needed. */
public interface CategoriaJpaRepository extends JpaRepository<CategoriaJpaEntity, Long> {
}
