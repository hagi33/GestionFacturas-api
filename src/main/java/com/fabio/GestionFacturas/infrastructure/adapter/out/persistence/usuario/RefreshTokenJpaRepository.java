package com.fabio.GestionFacturas.infrastructure.adapter.out.persistence.usuario;

import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

/** Spring Data repository; only used by {@code RefreshTokenPersistenceAdapter}. */
public interface RefreshTokenJpaRepository extends JpaRepository<RefreshTokenJpaEntity, Long> {

    Optional<RefreshTokenJpaEntity> findByTokenHash(String tokenHash);

}
