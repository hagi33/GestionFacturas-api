package com.fabio.GestionFacturas.application.usuario.port.in;

/**
 * Inbound port for exchanging a valid refresh token for a new access token.
 * Implemented by {@code RefreshTokenService}. No rotation yet (deferred intentionally,
 * per project convention) — the same refresh token stays valid across renewals.
 */
public interface RefreshTokenUseCase {

    ResultadoRenovacion renovar(ComandoRenovar comandoRenovar);

    record ComandoRenovar(String refreshToken){};
    record ResultadoRenovacion(String accessToken){};

}
