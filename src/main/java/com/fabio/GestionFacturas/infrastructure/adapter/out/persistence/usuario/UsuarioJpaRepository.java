package com.fabio.GestionFacturas.infrastructure.adapter.out.persistence.usuario;

import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

/** Spring Data repository; only used by {@code UsuarioPersistenceAdapter}. */
public interface UsuarioJpaRepository extends JpaRepository<UsuarioJpaEntity, Long> {

    Optional<UsuarioJpaEntity> findByEmail(String email);
}
