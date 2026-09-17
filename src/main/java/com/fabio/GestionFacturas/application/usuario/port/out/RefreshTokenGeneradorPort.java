package com.fabio.GestionFacturas.application.usuario.port.out;

import java.time.LocalDateTime;

/** Outbound port implemented by {@code RefreshTokenGenerador} (infrastructure/config/security). */
public interface RefreshTokenGeneradorPort {

    String generar();

    /** SHA-256, not BCrypt: the token must be looked up by hash on refresh, which BCrypt's random salt prevents. */
    String hashear(String tokenPlano);

    LocalDateTime calcularExpiracion();

}
