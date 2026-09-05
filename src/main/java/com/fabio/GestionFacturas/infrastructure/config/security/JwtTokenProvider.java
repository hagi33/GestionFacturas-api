package com.fabio.GestionFacturas.infrastructure.config.security;


import com.fabio.GestionFacturas.application.usuario.port.out.TokenGeneradorPort;
import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import io.jsonwebtoken.security.Keys;


import javax.crypto.SecretKey;
import java.util.Date;

@Component
public class JwtTokenProvider implements TokenGeneradorPort{


    private final SecretKey secretKey;

    private final long accessExpirationMs;


    public JwtTokenProvider(
            @Value("${app.jwt.secret}") String secret,
            @Value("${app.jwt.access-expiration-ms}") long accessExpirationMs) {
        this.secretKey = Keys.hmacShaKeyFor(secret.getBytes());
        this.accessExpirationMs = accessExpirationMs;
    }

    @Override
    public String generarAccessToken(Long usuarioId, String email){

        Date ahora = new Date();
        Date expiracion = new Date(ahora.getTime() + accessExpirationMs);

        return Jwts.builder()
                .subject(String.valueOf(usuarioId))
                .claim("email", email)
                .issuedAt(ahora)
                .expiration(expiracion)
                .signWith(secretKey)
                .compact();
    }

    //Saca el id que lleva el token dentro
    public Long extraerUsuarioId(String token){
        Claims claims = Jwts.parser()
                .verifyWith(secretKey)
                .build()
                .parseSignedClaims(token)
                .getPayload();

        return  Long.valueOf(claims.getSubject());

    }


    public boolean esValido(String token){
        try {
            Jwts.parser()
                    .verifyWith(secretKey)
                    .build()
                    .parseSignedClaims(token);
            return true;
        } catch (Exception e) {
            return false;
        }

    }



}
