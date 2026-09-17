package com.fabio.GestionFacturas.infrastructure.adapter.out.persistence.usuario;

import com.fabio.GestionFacturas.domain.usuario.Usuario;

/** Converts between domain {@link Usuario} and {@link UsuarioJpaEntity}; used only by {@code UsuarioPersistenceAdapter}. */
public class UsuarioMapper {

    public UsuarioMapper() {
    }

    public static UsuarioJpaEntity aEntidad(Usuario usuario) {
        return new UsuarioJpaEntity(
                usuario.getId(),
                usuario.getEmail(),
                usuario.getPasswordHash(),
                usuario.getNombre()
        );
    }

    public static Usuario aDominio(UsuarioJpaEntity entidad) {
        return new Usuario(
                entidad.getId(),
                entidad.getEmail(),
                entidad.getNombre(),
                entidad.getPassword()
        );
    }
}
