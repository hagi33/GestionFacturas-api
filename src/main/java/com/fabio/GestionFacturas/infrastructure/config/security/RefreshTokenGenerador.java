package com.fabio.GestionFacturas.infrastructure.config.security;

import com.fabio.GestionFacturas.application.usuario.port.out.RefreshTokenGeneradorPort;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.security.SecureRandom;
import java.time.Duration;
import java.time.LocalDateTime;
import java.util.Base64;
import java.util.HexFormat;

@Component
public class RefreshTokenGenerador implements RefreshTokenGeneradorPort {

    private static final int LONGITUD_TOKEN_BYTES = 32;
    private static final String ALGORITMO_HASH = "SHA-256";

    private final SecureRandom secureRandom = new SecureRandom();
    private final long refreshExpirationMs;

    public RefreshTokenGenerador(@Value("${app.jwt.refresh-expiration-ms}") long refreshExpirationMs) {
        this.refreshExpirationMs = refreshExpirationMs;
    }

    @Override
    public String generar() {
        byte[] bytes = new byte[LONGITUD_TOKEN_BYTES];
        secureRandom.nextBytes(bytes);
        return Base64.getUrlEncoder().withoutPadding().encodeToString(bytes);
    }

    @Override
    public String hashear(String tokenPlano) {
        try {
            MessageDigest digest = MessageDigest.getInstance(ALGORITMO_HASH);
            byte[] hashBytes = digest.digest(tokenPlano.getBytes(StandardCharsets.UTF_8));
            return HexFormat.of().formatHex(hashBytes);
        } catch (NoSuchAlgorithmException e) {
            throw new IllegalStateException("Algoritmo de hash no disponible: " + ALGORITMO_HASH, e);
        }
    }

    @Override
    public LocalDateTime calcularExpiracion() {
        return LocalDateTime.now().plus(Duration.ofMillis(refreshExpirationMs));
    }
}
