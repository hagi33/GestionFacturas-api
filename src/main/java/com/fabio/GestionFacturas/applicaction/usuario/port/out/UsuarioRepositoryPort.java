package com.fabio.GestionFacturas.applicaction.usuario.port.out;

import com.fabio.GestionFacturas.domain.usuario.Usuario;

import java.util.Optional;

public interface UsuarioRepositoryPort {

    Usuario guardar(Usuario usuario);

    Optional<Usuario> buscarPorId(Long id);

    Optional<Usuario> buscarPorEmail(String email);

}
