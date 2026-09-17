package com.fabio.GestionFacturas.application.usuario.port.out;

import com.fabio.GestionFacturas.domain.usuario.RefreshToken;

import java.util.Optional;

/** Outbound port implemented by {@code RefreshTokenPersistenceAdapter} (JPA) in infrastructure. */
public interface RefreshTokenRepositoryPort {

    RefreshToken guardar(RefreshToken refreshToken);

    /** Lookup is by hash — the raw token from the client is hashed first, never compared/stored in the clear. */
    Optional<RefreshToken> buscarPorTokenHash(String tokenHash);

}
