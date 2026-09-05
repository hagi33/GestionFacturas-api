package com.fabio.GestionFacturas.infrastructure.config.security;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;
import static org.assertj.core.api.Assertions.*;




class JwtTokenProviderTest {

    private static final String SECRET =
            "clave-de-prueba-muy-larga-para-tests-con-mas-de-256-bits-1234567890";

    private static final long EXPIRACION_MS = 900000; // 15 min

    private final JwtTokenProvider provider = new JwtTokenProvider(SECRET, EXPIRACION_MS);


    @Test
    @DisplayName("genera un token del que se puede extraer el mismo id de usuario")
    void roundTripDelIdDeUsuario(){

        Long usuarioId = 42L;

        String token = provider.generarAccessToken(usuarioId, "fabio@test.com");
        Long idExtraido = provider.extraerUsuarioId(token);

        assertThat(idExtraido).isEqualTo(42L);
    }

    @Test
    @DisplayName("un token recién generado es válido")
    void tokenRecienGeneradoEsValido(){
        String token = provider.generarAccessToken(1L, "fabio@test.com");

        assertThat(provider.esValido(token)).isTrue();

    }

    @Test
    @DisplayName("una cadena que no es un token válido se rechaza")
    void cadenaInvalidaNoEsValida(){

        assertThat(provider.esValido("token-invalido")).isFalse();

    }


    @Test
    @DisplayName("un token caducado se rechaza")
    void tokenCaducadoNoEsValido(){

        JwtTokenProvider providerCaducado = new JwtTokenProvider(SECRET, -10L);
        String token = providerCaducado.generarAccessToken(1L, "fabio@test.com");

        assertThat(providerCaducado.esValido(token)).isFalse();
    }

}