package com.fabio.GestionFacturas.application.cliente.service;

import com.fabio.GestionFacturas.application.cliente.port.in.EditarClienteUseCase.ComandoEditarCliente;
import com.fabio.GestionFacturas.application.cliente.port.out.ClienteRepositoryPort;
import com.fabio.GestionFacturas.domain.cliente.Cliente;
import com.fabio.GestionFacturas.domain.cliente.ClienteInvalidoException;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Optional;

import static org.assertj.core.api.Assertions.*;
import static org.mockito.Mockito.*;

/** Unit tests for EditarClienteService: happy path plus the not-found / not-owned rejections. */
@ExtendWith(MockitoExtension.class)
class EditarClienteServiceTest {

    @Mock
    private ClienteRepositoryPort clienteRepositoryPort;

    @InjectMocks
    private EditarClienteService service;

    @Test
    @DisplayName("edita los datos mutables y guarda cuando el cliente existe y es del usuario")
    void editaYGuardaClienteExistente() {
        // Arrange
        Cliente cliente = Cliente.crearActivo(1L, "Proveedor SL", "12345678A", "viejo@test.com", "600000000");
        ComandoEditarCliente comando = new ComandoEditarCliente(10L, 1L, "Proveedor Nuevo SL", "nuevo@test.com", "600111222");

        when(clienteRepositoryPort.buscarPorId(10L)).thenReturn(Optional.of(cliente));
        when(clienteRepositoryPort.guardar(any(Cliente.class)))
                .thenAnswer(invocationOnMock -> invocationOnMock.getArgument(0));

        // Act
        Cliente resultado = service.editar(comando);

        // Assert
        assertThat(resultado.getNombre()).isEqualTo("Proveedor Nuevo SL");
        assertThat(resultado.getEmail()).isEqualTo("nuevo@test.com");
        assertThat(resultado.getTelefono()).isEqualTo("600111222");
        assertThat(resultado.getNif()).isEqualTo("12345678A");
        verify(clienteRepositoryPort).guardar(cliente);
    }

    @Test
    @DisplayName("lanza ClienteInvalidoException si el cliente no existe")
    void fallaSiClienteNoExiste() {
        // Arrange
        when(clienteRepositoryPort.buscarPorId(10L)).thenReturn(Optional.empty());
        ComandoEditarCliente comando = new ComandoEditarCliente(10L, 1L, "Proveedor SL", null, null);

        // Act + Assert
        assertThatThrownBy(() -> service.editar(comando))
                .isInstanceOf(ClienteInvalidoException.class);

        verify(clienteRepositoryPort, never()).guardar(any());
    }

    @Test
    @DisplayName("lanza ClienteInvalidoException si el cliente es de otro usuario")
    void fallaSiClienteEsDeOtroUsuario() {
        // Arrange: el cliente pertenece al usuario 2, pero edita el usuario 1
        Cliente clienteDeOtro = Cliente.crearActivo(2L, "Proveedor SL", "12345678A", null, null);
        when(clienteRepositoryPort.buscarPorId(10L)).thenReturn(Optional.of(clienteDeOtro));
        ComandoEditarCliente comando = new ComandoEditarCliente(10L, 1L, "Proveedor Nuevo SL", null, null);

        // Act + Assert
        assertThatThrownBy(() -> service.editar(comando))
                .isInstanceOf(ClienteInvalidoException.class);

        verify(clienteRepositoryPort, never()).guardar(any());
    }
}
