package com.fabio.GestionFacturas.application.usuario.service;


import com.fabio.GestionFacturas.application.usuario.port.in.AutenticarUseCase;
import com.fabio.GestionFacturas.application.usuario.port.out.RefreshTokenGeneradorPort;
import com.fabio.GestionFacturas.application.usuario.port.out.RefreshTokenRepositoryPort;
import com.fabio.GestionFacturas.application.usuario.port.out.TokenGeneradorPort;
import com.fabio.GestionFacturas.application.usuario.port.out.UsuarioRepositoryPort;
import com.fabio.GestionFacturas.domain.usuario.CredencialesInvalidadException;
import com.fabio.GestionFacturas.domain.usuario.RefreshToken;
import com.fabio.GestionFacturas.domain.usuario.Usuario;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.Optional;

@Service
public class AutenticarService implements AutenticarUseCase {


    private final UsuarioRepositoryPort usuarioRepositoryPort;
    private final PasswordEncoder passwordEncoder;
    private final TokenGeneradorPort tokenGeneradorPort;
    private final RefreshTokenGeneradorPort refreshTokenGeneradorPort;
    private final RefreshTokenRepositoryPort refreshTokenRepositoryPort;


    public AutenticarService(UsuarioRepositoryPort usuarioRepositoryPort,
                             PasswordEncoder passwordEncoder,
                             TokenGeneradorPort tokenGeneradorPort,
                             RefreshTokenGeneradorPort refreshTokenGeneradorPort,
                             RefreshTokenRepositoryPort refreshTokenRepositoryPort) {
        this.usuarioRepositoryPort = usuarioRepositoryPort;
        this.passwordEncoder = passwordEncoder;
        this.tokenGeneradorPort = tokenGeneradorPort;
        this.refreshTokenGeneradorPort = refreshTokenGeneradorPort;
        this.refreshTokenRepositoryPort = refreshTokenRepositoryPort;
    }

    @Override
    public ResultadoAutenticacion autenticar(ComandoLogin comandoLogin) {

        Optional<Usuario> usuarioOpt = usuarioRepositoryPort.buscarPorEmail(comandoLogin.email());
        if (usuarioOpt.isEmpty()){
            throw new CredencialesInvalidadException("Email incorrecto");
        }

        Usuario usuario = usuarioOpt.get();

        boolean coincide = passwordEncoder.matches(comandoLogin.password(), usuario.getPasswordHash());

        if (!coincide){
            throw new CredencialesInvalidadException("Contraseña incorrecta");

        }

        String token = tokenGeneradorPort.generarAccessToken(usuario.getId(), usuario.getEmail());

        String refreshTokenPlano = refreshTokenGeneradorPort.generar();
        String refreshTokenHash = refreshTokenGeneradorPort.hashear(refreshTokenPlano);
        LocalDateTime expiraEn = refreshTokenGeneradorPort.calcularExpiracion();

        RefreshToken refreshToken = new RefreshToken(false, null, usuario.getId(), refreshTokenHash, expiraEn, null);
        refreshTokenRepositoryPort.guardar(refreshToken);

        return new ResultadoAutenticacion(token, refreshTokenPlano, usuario.getId(), usuario.getEmail());


    }
}
