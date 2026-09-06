package com.fabio.GestionFacturas.application.usuario.service;


import com.fabio.GestionFacturas.application.usuario.port.in.AutenticarUseCase;
import com.fabio.GestionFacturas.application.usuario.port.out.TokenGeneradorPort;
import com.fabio.GestionFacturas.application.usuario.port.out.UsuarioRepositoryPort;
import com.fabio.GestionFacturas.domain.usuario.CredencialesInvalidadException;
import com.fabio.GestionFacturas.domain.usuario.Usuario;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Service
public class AutenticarService implements AutenticarUseCase {


    private final UsuarioRepositoryPort usuarioRepositoryPort;
    private final PasswordEncoder passwordEncoder;
    private final TokenGeneradorPort tokenGeneradorPort;


    public AutenticarService(UsuarioRepositoryPort usuarioRepositoryPort,
                             PasswordEncoder passwordEncoder,
                             TokenGeneradorPort tokenGeneradorPort) {
        this.usuarioRepositoryPort = usuarioRepositoryPort;
        this.passwordEncoder = passwordEncoder;
        this.tokenGeneradorPort = tokenGeneradorPort;
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

        return new ResultadoAutenticacion(token, usuario.getId(), usuario.getEmail());


    }
}
