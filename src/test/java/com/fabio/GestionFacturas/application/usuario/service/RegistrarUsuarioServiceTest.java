package com.fabio.GestionFacturas.application.usuario.service;

import com.fabio.GestionFacturas.application.usuario.port.in.RegistrarUsuarioUseCase.ComandoRegistrar;
import com.fabio.GestionFacturas.application.usuario.port.out.UsuarioRepositoryPort;
import com.fabio.GestionFacturas.domain.usuario.EmailYaRegistradoException;
import com.fabio.GestionFacturas.domain.usuario.Usuario;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.crypto.password.PasswordEncoder;

import java.util.Optional;

import static org.assertj.core.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;


@ExtendWith(MockitoExtension.class)
class RegistrarUsuarioServiceTest {


    @Mock
    private UsuarioRepositoryPort usuarioRepositoryPort;
    @Mock
    private PasswordEncoder passwordEncoder;

    @InjectMocks
    private RegistrarUsuarioService registrarUsuarioService;

    @Test
    @DisplayName("registra un usuario nuevo hasheando la contraseña y guardándolo")
    void registrarUsuarioNuevo(){
        //Arrange
        ComandoRegistrar comandoRegistrar = new ComandoRegistrar("fabio@test.com", "Fabio", "miPassword");
        when(usuarioRepositoryPort.buscarPorEmail("fabio@test.com")).thenReturn(Optional.empty());
        when(passwordEncoder.encode("miPassword")).thenReturn("hash-generado");
        when(usuarioRepositoryPort.guardar(any(Usuario.class)))
                .thenAnswer(invocationOnMock -> invocationOnMock.getArgument(0));

        //Act
        Usuario resultado = registrarUsuarioService.registrar(comandoRegistrar);

        //Assert
        assertThat(resultado.getEmail()).isEqualTo("fabio@test.com");
        assertThat(resultado.getPasswordHash()).isEqualTo("hash-generado");
        verify(usuarioRepositoryPort).guardar(any(Usuario.class));

    }

    @Test
    @DisplayName("lanza EmailYaRegistradoException y no guarda si el email ya existe")
    void fallaSiEmailYaExiste() {
        // Arrange: el email ya está registrado
        ComandoRegistrar comando = new ComandoRegistrar("fabio@test.com", "Fabio", "miPassword");
        Usuario existente = new Usuario(1L, "fabio@test.com", "Fabio", "hashViejo");
        when(usuarioRepositoryPort.buscarPorEmail("fabio@test.com")).thenReturn(Optional.of(existente));

        // Act + Assert
        assertThatThrownBy(() -> registrarUsuarioService.registrar(comando))
                .isInstanceOf(EmailYaRegistradoException.class);

        // Y lo importante: nunca se llegó a guardar
        verify(usuarioRepositoryPort, never()).guardar(any());
    }

    @Test
    @DisplayName("guarda el hash de la contraseña, nunca el texto plano")
    void guardaElHashNoElTextoPlano() {
        // Arrange
        ComandoRegistrar comando = new ComandoRegistrar("fabio@test.com", "Fabio", "miPassword");
        when(usuarioRepositoryPort.buscarPorEmail("fabio@test.com")).thenReturn(Optional.empty());
        when(passwordEncoder.encode("miPassword")).thenReturn("hash-generado");
        when(usuarioRepositoryPort.guardar(any(Usuario.class)))
                .thenAnswer(invocacion -> invocacion.getArgument(0));

        // Act
        Usuario resultado = registrarUsuarioService.registrar(comando);

        // Assert
        assertThat(resultado.getPasswordHash()).isEqualTo("hash-generado");
        assertThat(resultado.getPasswordHash()).isNotEqualTo("miPassword");
    }


}