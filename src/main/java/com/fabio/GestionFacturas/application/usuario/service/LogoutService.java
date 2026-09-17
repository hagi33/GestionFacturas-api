package com.fabio.GestionFacturas.application.usuario.service;

import com.fabio.GestionFacturas.application.usuario.port.in.LogoutUseCase;
import com.fabio.GestionFacturas.application.usuario.port.out.RefreshTokenGeneradorPort;
import com.fabio.GestionFacturas.application.usuario.port.out.RefreshTokenRepositoryPort;
import com.fabio.GestionFacturas.domain.usuario.RefreshToken;
import org.springframework.stereotype.Service;

import java.util.Optional;

/**
 * Implements {@link LogoutUseCase}, called from {@code AuthController}. Hashes the
 * presented token to find it via {@link RefreshTokenRepositoryPort}, then flips
 * {@code revocado} and saves — a silent no-op if the token isn't found, since logout
 * on an already-invalid token shouldn't surface as an error to the client.
 */
@Service
public class LogoutService implements LogoutUseCase {

    private final RefreshTokenRepositoryPort refreshTokenRepositoryPort;
    private final RefreshTokenGeneradorPort refreshTokenGeneradorPort;

    public LogoutService(RefreshTokenRepositoryPort refreshTokenRepositoryPort,
                         RefreshTokenGeneradorPort refreshTokenGeneradorPort) {
        this.refreshTokenRepositoryPort = refreshTokenRepositoryPort;
        this.refreshTokenGeneradorPort = refreshTokenGeneradorPort;
    }

    @Override
    public void logout(ComandoLogout comandoLogout) {

        String hash = refreshTokenGeneradorPort.hashear(comandoLogout.refreshToken());

        Optional<RefreshToken> refreshTokenOpt = refreshTokenRepositoryPort.buscarPorTokenHash(hash);
        if (refreshTokenOpt.isPresent()){
            RefreshToken refreshToken = refreshTokenOpt.get();
            refreshToken.revocar();
            refreshTokenRepositoryPort.guardar(refreshToken);
        }
    }
}
