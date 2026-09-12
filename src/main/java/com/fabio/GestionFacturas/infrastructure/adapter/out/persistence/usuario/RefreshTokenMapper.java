package com.fabio.GestionFacturas.infrastructure.adapter.out.persistence.usuario;

import com.fabio.GestionFacturas.domain.usuario.RefreshToken;

public class RefreshTokenMapper {

    public RefreshTokenMapper() {
    }

    public static RefreshTokenJpaEntity aEntidad(RefreshToken refreshToken) {
        return new RefreshTokenJpaEntity(
                refreshToken.getId(),
                refreshToken.getUsuarioId(),
                refreshToken.getTokenHash(),
                refreshToken.getExpiraEn(),
                refreshToken.isRevocado()
        );
    }

    public static RefreshToken aDominio(RefreshTokenJpaEntity entidad) {
        return new RefreshToken(
                entidad.isRevocado(),
                entidad.getId(),
                entidad.getUsuarioId(),
                entidad.getTokenHash(),
                entidad.getExpiraEn(),
                entidad.getCreadoEn()
        );
    }
}
