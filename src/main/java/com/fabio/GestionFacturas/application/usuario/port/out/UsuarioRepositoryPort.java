package com.fabio.GestionFacturas.application.usuario.port.out;

import com.fabio.GestionFacturas.domain.usuario.Usuario;

import java.util.Optional;

/** Outbound port implemented by {@code UsuarioPersistenceAdapter} (JPA) in infrastructure. */
public interface UsuarioRepositoryPort {

    Usuario guardar(Usuario usuario);

    Optional<Usuario> buscarPorId(Long id);

    Optional<Usuario> buscarPorEmail(String email);

}
