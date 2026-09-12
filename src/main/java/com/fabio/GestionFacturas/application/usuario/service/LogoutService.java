package com.fabio.GestionFacturas.application.usuario.service;

import com.fabio.GestionFacturas.application.usuario.port.in.LogoutUseCase;
import com.fabio.GestionFacturas.application.usuario.port.out.RefreshTokenGeneradorPort;
import com.fabio.GestionFacturas.application.usuario.port.out.RefreshTokenRepositoryPort;
import com.fabio.GestionFacturas.domain.usuario.RefreshToken;
import org.springframework.stereotype.Service;

import java.util.Optional;

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
