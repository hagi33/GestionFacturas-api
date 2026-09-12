package com.fabio.GestionFacturas.application.usuario.service;

import com.fabio.GestionFacturas.application.usuario.port.in.LogoutUseCase.ComandoLogout;
import com.fabio.GestionFacturas.application.usuario.port.out.RefreshTokenGeneradorPort;
import com.fabio.GestionFacturas.application.usuario.port.out.RefreshTokenRepositoryPort;
import com.fabio.GestionFacturas.domain.usuario.RefreshToken;
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
class LogoutServiceTest {

    @Mock
    private RefreshTokenRepositoryPort refreshTokenRepositoryPort;
    @Mock
    private RefreshTokenGeneradorPort refreshTokenGeneradorPort;

    @InjectMocks
    private LogoutService logoutService;


    @Test
    @DisplayName("revoca y guarda el refresh token existente")
    void revocaYGuardaTokenExistente(){
        RefreshToken refreshToken = new RefreshToken(false, 1L, 1L, "hash-de-prueba",
                LocalDateTime.now().plusDays(1), LocalDateTime.now());

        when(refreshTokenGeneradorPort.hashear("refresh-plano")).thenReturn("hash-de-prueba");
        when(refreshTokenRepositoryPort.buscarPorTokenHash("hash-de-prueba")).thenReturn(Optional.of(refreshToken));

        // Act
        logoutService.logout(new ComandoLogout("refresh-plano"));

        // Assert
        assertThat(refreshToken.isRevocado()).isTrue();
        verify(refreshTokenRepositoryPort).guardar(refreshToken);
    }

    @Test
    @DisplayName("no hace nada si el refresh token no existe")
    void noHaceNadaSiTokenNoExiste(){
        when(refreshTokenGeneradorPort.hashear("refresh-desconocido")).thenReturn("hash-desconocido");
        when(refreshTokenRepositoryPort.buscarPorTokenHash("hash-desconocido")).thenReturn(Optional.empty());

        // Act
        logoutService.logout(new ComandoLogout("refresh-desconocido"));

        // Assert
        verify(refreshTokenRepositoryPort, never()).guardar(any());
    }
}
