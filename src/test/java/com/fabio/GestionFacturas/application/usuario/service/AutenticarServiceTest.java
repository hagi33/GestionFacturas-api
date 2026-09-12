package com.fabio.GestionFacturas.application.usuario.service;

import com.fabio.GestionFacturas.application.usuario.port.in.AutenticarUseCase.ComandoLogin;
import com.fabio.GestionFacturas.application.usuario.port.in.AutenticarUseCase.ResultadoAutenticacion;
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
import org.springframework.security.crypto.password.PasswordEncoder;

import java.time.LocalDateTime;
import java.util.Optional;

import static org.assertj.core.api.Assertions.*;
import static org.mockito.Mockito.*;


@ExtendWith(MockitoExtension.class)
class AutenticarServiceTest {


    @Mock
    private UsuarioRepositoryPort usuarioRepositoryPort;
    @Mock
    private PasswordEncoder passwordEncoder;
    @Mock
    private TokenGeneradorPort tokenGeneradorPort;
    @Mock
    private RefreshTokenGeneradorPort refreshTokenGeneradorPort;
    @Mock
    private RefreshTokenRepositoryPort refreshTokenRepositoryPort;

    @InjectMocks
    private AutenticarService autenticarService;



    @Test
    @DisplayName("autentica correctamente con credenciales válidas y devuelve un token")
    void autenticaConCredencialesValidas(){
        Usuario usuario = new Usuario(1L, "fabio@test.com", "Fabio", "hashGuardado");
        when(usuarioRepositoryPort.buscarPorEmail("fabio@test.com")).thenReturn(Optional.of(usuario));
        when(passwordEncoder.matches("miPassword", "hashGuardado")).thenReturn(true);
        when(tokenGeneradorPort.generarAccessToken(1L, "fabio@test.com")).thenReturn("token-de-prueba");
        when(refreshTokenGeneradorPort.generar()).thenReturn("refresh-plano-de-prueba");
        when(refreshTokenGeneradorPort.hashear("refresh-plano-de-prueba")).thenReturn("hash-de-prueba");
        when(refreshTokenGeneradorPort.calcularExpiracion()).thenReturn(LocalDateTime.now().plusDays(7));

        // Act
        ResultadoAutenticacion resultado = autenticarService.autenticar(new ComandoLogin("fabio@test.com", "miPassword"));

        // Assert
        assertThat(resultado.accessToken()).isEqualTo("token-de-prueba");
        assertThat(resultado.refreshToken()).isEqualTo("refresh-plano-de-prueba");
        assertThat(resultado.usuarioId()).isEqualTo(1L);
        verify(refreshTokenRepositoryPort).guardar(any(RefreshToken.class));

    }


    @Test
    @DisplayName("lanza excepción correspondiente si el email no existe")
    void fallaSiEmailNoExiste(){

        // Arrange: el repositorio no encuentra ningún usuario con ese email
        when(usuarioRepositoryPort.buscarPorEmail("noexiste@test.com")).thenReturn(Optional.empty());

        //Act + Assert
        assertThatThrownBy(() ->
                autenticarService.autenticar(new ComandoLogin("noexiste@test.com", "x")))
                .isInstanceOf(CredencialesInvalidadException.class);

        verify(refreshTokenRepositoryPort, never()).guardar(any());

    }

    @Test
    @DisplayName("lanza excepción correspondiente si la contraseña no coincide")
    void fallaSiContraseñaIncorrecta(){

        //Arrange: el usuario existe pero la contraseña no coincide
        Usuario usuario = new Usuario(1L, "fabio@test.com", "Fabio","hashGuardado");
        when(usuarioRepositoryPort.buscarPorEmail("fabio@test.com")).thenReturn(Optional.of(usuario));
        when(passwordEncoder.matches("passwordErronea", "hashGuardado")).thenReturn(false);


        // Act + Assert
        assertThatThrownBy(() ->
                autenticarService.autenticar(new ComandoLogin("fabio@test.com", "passwordErronea")))
                .isInstanceOf(CredencialesInvalidadException.class);

        verify(refreshTokenRepositoryPort, never()).guardar(any());
    }





}