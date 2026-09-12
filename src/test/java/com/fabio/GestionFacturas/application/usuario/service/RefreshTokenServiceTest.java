package com.fabio.GestionFacturas.application.usuario.service;

import com.fabio.GestionFacturas.application.usuario.port.in.RefreshTokenUseCase.ComandoRenovar;
import com.fabio.GestionFacturas.application.usuario.port.in.RefreshTokenUseCase.ResultadoRenovacion;
import com.fabio.GestionFacturas.application.usuario.port.out.RefreshTokenGeneradorPort;
import com.fabio.GestionFacturas.application.usuario.port.out.RefreshTokenRepositoryPort;
import com.fabio.GestionFacturas.application.usuario.port.out.TokenGeneradorPort;
import com.fabio.GestionFacturas.application.usuario.port.out.UsuarioRepositoryPort;
import com.fabio.GestionFacturas.domain.usuario.CredencialesInvalidadException;
import com.fabio.GestionFacturas.domain.usuario.RefreshToken;
import com.fabio.GestionFacturas.domain.usuario.Usuario;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.LocalDateTime;
import java.util.Optional;

import static org.assertj.core.api.Assertions.*;
import static org.mockito.Mockito.*;


@ExtendWith(MockitoExtension.class)
class RefreshTokenServiceTest {

    @Mock
    private RefreshTokenRepositoryPort refreshTokenRepositoryPort;
    @Mock
    private RefreshTokenGeneradorPort refreshTokenGeneradorPort;
    @Mock
    private UsuarioRepositoryPort usuarioRepositoryPort;
    @Mock
    private TokenGeneradorPort tokenGeneradorPort;

    @InjectMocks
    private RefreshTokenService refreshTokenService;


    @Test
    @DisplayName("renueva el access token si el refresh token es válido")
    void renuevaCorrectamenteConTokenValido(){
        RefreshToken refreshToken = new RefreshToken(false, 1L, 1L, "hash-de-prueba",
                LocalDateTime.now().plusDays(1), LocalDateTime.now());
        Usuario usuario = new Usuario(1L, "fabio@test.com", "Fabio", "hashGuardado");

        when(refreshTokenGeneradorPort.hashear("refresh-plano")).thenReturn("hash-de-prueba");
        when(refreshTokenRepositoryPort.buscarPorTokenHash("hash-de-prueba")).thenReturn(Optional.of(refreshToken));
        when(usuarioRepositoryPort.buscarPorId(1L)).thenReturn(Optional.of(usuario));
        when(tokenGeneradorPort.generarAccessToken(1L, "fabio@test.com")).thenReturn("nuevo-access-token");

        // Act
        ResultadoRenovacion resultado = refreshTokenService.renovar(new ComandoRenovar("refresh-plano"));

        // Assert
        assertThat(resultado.accessToken()).isEqualTo("nuevo-access-token");
    }

    @Test
    @DisplayName("lanza excepción si el refresh token no existe")
    void fallaSiTokenNoExiste(){
        when(refreshTokenGeneradorPort.hashear("refresh-desconocido")).thenReturn("hash-desconocido");
        when(refreshTokenRepositoryPort.buscarPorTokenHash("hash-desconocido")).thenReturn(Optional.empty());

        // Act + Assert
        assertThatThrownBy(() ->
                refreshTokenService.renovar(new ComandoRenovar("refresh-desconocido")))
                .isInstanceOf(CredencialesInvalidadException.class);

        verify(tokenGeneradorPort, never()).generarAccessToken(any(), any());
    }

    @Test
    @DisplayName("lanza excepción si el refresh token está revocado")
    void fallaSiTokenRevocado(){
        RefreshToken refreshTokenRevocado = new RefreshToken(true, 1L, 1L, "hash-de-prueba",
                LocalDateTime.now().plusDays(1), LocalDateTime.now());

        when(refreshTokenGeneradorPort.hashear("refresh-plano")).thenReturn("hash-de-prueba");
        when(refreshTokenRepositoryPort.buscarPorTokenHash("hash-de-prueba")).thenReturn(Optional.of(refreshTokenRevocado));

        // Act + Assert
        assertThatThrownBy(() ->
                refreshTokenService.renovar(new ComandoRenovar("refresh-plano")))
                .isInstanceOf(CredencialesInvalidadException.class);

        verify(tokenGeneradorPort, never()).generarAccessToken(any(), any());
    }

    @Test
    @DisplayName("lanza excepción si el refresh token está expirado")
    void fallaSiTokenExpirado(){
        RefreshToken refreshTokenExpirado = new RefreshToken(false, 1L, 1L, "hash-de-prueba",
                LocalDateTime.now().minusDays(1), LocalDateTime.now().minusDays(8));

        when(refreshTokenGeneradorPort.hashear("refresh-plano")).thenReturn("hash-de-prueba");
        when(refreshTokenRepositoryPort.buscarPorTokenHash("hash-de-prueba")).thenReturn(Optional.of(refreshTokenExpirado));

        // Act + Assert
        assertThatThrownBy(() ->
                refreshTokenService.renovar(new ComandoRenovar("refresh-plano")))
                .isInstanceOf(CredencialesInvalidadException.class);

        verify(tokenGeneradorPort, never()).generarAccessToken(any(), any());
    }
}
