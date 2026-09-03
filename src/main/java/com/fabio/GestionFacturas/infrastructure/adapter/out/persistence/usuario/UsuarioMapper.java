package com.fabio.GestionFacturas.infrastructure.adapter.out.persistence.usuario;

import com.fabio.GestionFacturas.domain.usuario.Usuario;

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
