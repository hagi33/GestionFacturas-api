package com.fabio.GestionFacturas.application.usuario.port.out;

/** Outbound port for issuing access tokens. Implemented by {@code JwtTokenProvider} (infrastructure/config/security). */
public interface TokenGeneradorPort {

    String generarAccessToken(Long usuarioId, String email);

}
