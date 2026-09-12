package com.fabio.GestionFacturas.application.usuario.port.out;

import com.fabio.GestionFacturas.domain.usuario.RefreshToken;

import java.util.Optional;

public interface RefreshTokenRepositoryPort {

    RefreshToken guardar(RefreshToken refreshToken);

    Optional<RefreshToken> buscarPorTokenHash(String tokenHash);

}
