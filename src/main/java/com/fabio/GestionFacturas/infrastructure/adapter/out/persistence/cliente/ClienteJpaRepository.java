package com.fabio.GestionFacturas.infrastructure.adapter.out.persistence.cliente;

import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

/** Spring Data repository; only used by {@code ClientePersistenceAdapter}. */
public interface ClienteJpaRepository extends JpaRepository<ClienteJpaEntity, Long> {

    // Spring Data derives the query from the method name — no SQL/JPQL written here
    List<ClienteJpaEntity> findByUsuarioIdAndActivoTrue(Long usuarioId);

    Optional<ClienteJpaEntity> findByUsuarioIdAndNif(Long usuarioId, String nif);
}
