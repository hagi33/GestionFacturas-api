package com.fabio.GestionFacturas.application.usuario.service;

import com.fabio.GestionFacturas.application.usuario.port.in.RefreshTokenUseCase;
import com.fabio.GestionFacturas.application.usuario.port.out.RefreshTokenGeneradorPort;
import com.fabio.GestionFacturas.application.usuario.port.out.RefreshTokenRepositoryPort;
import com.fabio.GestionFacturas.application.usuario.port.out.TokenGeneradorPort;
import com.fabio.GestionFacturas.application.usuario.port.out.UsuarioRepositoryPort;
import com.fabio.GestionFacturas.domain.usuario.CredencialesInvalidadException;
import com.fabio.GestionFacturas.domain.usuario.RefreshToken;
import com.fabio.GestionFacturas.domain.usuario.Usuario;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Service
public class RefreshTokenService implements RefreshTokenUseCase {

    private final RefreshTokenRepositoryPort refreshTokenRepositoryPort;
    private final RefreshTokenGeneradorPort refreshTokenGeneradorPort;
    private final UsuarioRepositoryPort usuarioRepositoryPort;
    private final TokenGeneradorPort tokenGeneradorPort;

    public RefreshTokenService(RefreshTokenRepositoryPort refreshTokenRepositoryPort,
                               RefreshTokenGeneradorPort refreshTokenGeneradorPort,
                               UsuarioRepositoryPort usuarioRepositoryPort,
                               TokenGeneradorPort tokenGeneradorPort) {
        this.refreshTokenRepositoryPort = refreshTokenRepositoryPort;
        this.refreshTokenGeneradorPort = refreshTokenGeneradorPort;
        this.usuarioRepositoryPort = usuarioRepositoryPort;
        this.tokenGeneradorPort = tokenGeneradorPort;
    }

    @Override
    public ResultadoRenovacion renovar(ComandoRenovar comandoRenovar) {

        String hash = refreshTokenGeneradorPort.hashear(comandoRenovar.refreshToken());

        Optional<RefreshToken> refreshTokenOpt = refreshTokenRepositoryPort.buscarPorTokenHash(hash);
        if (refreshTokenOpt.isEmpty()){
            throw new CredencialesInvalidadException("Refresh token inválido");
        }

        RefreshToken refreshToken = refreshTokenOpt.get();

        if (!refreshToken.esValido()){
            throw new CredencialesInvalidadException("Refresh token expirado o revocado");
        }

        Optional<Usuario> usuarioOpt = usuarioRepositoryPort.buscarPorId(refreshToken.getUsuarioId());
        if (usuarioOpt.isEmpty()){
            throw new CredencialesInvalidadException("Usuario no encontrado");
        }

        Usuario usuario = usuarioOpt.get();

        String accessToken = tokenGeneradorPort.generarAccessToken(usuario.getId(), usuario.getEmail());

        return new ResultadoRenovacion(accessToken);
    }
}
