package com.fabio.GestionFacturas.application.cliente.service;

import com.fabio.GestionFacturas.application.cliente.port.out.ClienteRepositoryPort;
import com.fabio.GestionFacturas.domain.cliente.Cliente;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.*;
import static org.mockito.Mockito.*;

/** Unit tests for ConsultarClientesService, notably the access-control rule (empty for someone else's cliente). */
@ExtendWith(MockitoExtension.class)
class ConsultarClientesServiceTest {

    @Mock
    private ClienteRepositoryPort clienteRepository;

    @InjectMocks
    private ConsultarClientesService service;

    // ---- Método auxiliar para no repetir la creación de un cliente en cada test ----
    private Cliente clienteDeUsuario(Long usuarioId) {
        return Cliente.crearActivo(usuarioId, "Proveedor SL", "12345678A", "proveedor@test.com", "600111222");
    }

    @Test
    @DisplayName("listarPorUsuario devuelve los clientes que retorna el repositorio")
    void listarPorUsuarioDevuelveLosClientes() {
        // Arrange
        List<Cliente> clientes = List.of(clienteDeUsuario(1L), clienteDeUsuario(1L));
        when(clienteRepository.buscarPorUsuario(1L)).thenReturn(clientes);

        // Act
        List<Cliente> resultado = service.listarPorUsuario(1L);

        // Assert
        assertThat(resultado).hasSize(2);
        verify(clienteRepository).buscarPorUsuario(1L);
    }

    @Test
    @DisplayName("obtenerPorId devuelve el cliente cuando existe y es del usuario")
    void obtenerPorIdDevuelveClienteDelUsuario() {
        // Arrange
        Cliente cliente = clienteDeUsuario(1L);
        when(clienteRepository.buscarPorId(10L)).thenReturn(Optional.of(cliente));

        // Act
        Optional<Cliente> resultado = service.obtenerPorId(10L, 1L);

        // Assert
        assertThat(resultado).isPresent();
        assertThat(resultado.get().getUsuarioId()).isEqualTo(1L);
    }

    @Test
    @DisplayName("obtenerPorId devuelve vacío cuando el cliente no existe")
    void obtenerPorIdVacioSiNoExiste() {
        // Arrange
        when(clienteRepository.buscarPorId(10L)).thenReturn(Optional.empty());

        // Act
        Optional<Cliente> resultado = service.obtenerPorId(10L, 1L);

        // Assert
        assertThat(resultado).isEmpty();
    }

    @Test
    @DisplayName("obtenerPorId devuelve vacío cuando el cliente es de otro usuario")
    void obtenerPorIdVacioSiEsDeOtroUsuario() {
        // Arrange: el cliente pertenece al usuario 2, pero pregunta el usuario 1
        Cliente clienteDeOtro = clienteDeUsuario(2L);
        when(clienteRepository.buscarPorId(10L)).thenReturn(Optional.of(clienteDeOtro));

        // Act
        Optional<Cliente> resultado = service.obtenerPorId(10L, 1L);

        // Assert
        assertThat(resultado).isEmpty();
    }
}
