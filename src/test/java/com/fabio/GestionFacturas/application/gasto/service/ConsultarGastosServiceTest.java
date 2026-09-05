package com.fabio.GestionFacturas.application.gasto.service;

import com.fabio.GestionFacturas.domain.gasto.Gasto;
import com.fabio.GestionFacturas.application.gasto.port.out.GastoRepositoryPort;
import com.fabio.GestionFacturas.domain.shared.Dinero;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class ConsultarGastosServiceTest {

    @Mock
    private GastoRepositoryPort gastoRepository;

    @InjectMocks
    private ConsultarGastosService service;

    // ---- Método auxiliar para no repetir la creación de un gasto en cada test ----
    private Gasto gastoDeUsuario(Long usuarioId) {
        Dinero total = new Dinero(new BigDecimal("121.00"), "EUR");
        return Gasto.crearBorrador(usuarioId, "Proveedor SL", LocalDate.now(), null, null, total);
    }

    @Test
    @DisplayName("listarPorUsuario devuelve los gastos que retorna el repositorio")
    void listarPorUsuarioDevuelveLosGastos() {
        // Arrange
        List<Gasto> gastos = List.of(gastoDeUsuario(1L), gastoDeUsuario(1L));
        when(gastoRepository.buscarPorUsuario(1L)).thenReturn(gastos);

        // Act
        List<Gasto> resultado = service.listarPorUsuario(1L);

        // Assert
        assertThat(resultado).hasSize(2);
        verify(gastoRepository).buscarPorUsuario(1L);
    }

    @Test
    @DisplayName("obtenerPorId devuelve el gasto cuando existe y es del usuario")
    void obtenerPorIdDevuelveGastoDelUsuario() {
        // Arrange
        Gasto gasto = gastoDeUsuario(1L);
        when(gastoRepository.buscarPorId(10L)).thenReturn(Optional.of(gasto));

        // Act
        Optional<Gasto> resultado = service.obtenerPorId(10L, 1L);

        // Assert
        assertThat(resultado).isPresent();
        assertThat(resultado.get().getUsuarioId()).isEqualTo(1L);
    }

    @Test
    @DisplayName("obtenerPorId devuelve vacío cuando el gasto no existe")
    void obtenerPorIdVacioSiNoExiste() {
        // Arrange
        when(gastoRepository.buscarPorId(10L)).thenReturn(Optional.empty());

        // Act
        Optional<Gasto> resultado = service.obtenerPorId(10L, 1L);

        // Assert
        assertThat(resultado).isEmpty();
    }

    @Test
    @DisplayName("obtenerPorId devuelve vacío cuando el gasto es de otro usuario")
    void obtenerPorIdVacioSiEsDeOtroUsuario() {
        // Arrange: el gasto pertenece al usuario 2, pero pregunta el usuario 1
        Gasto gastoDeOtro = gastoDeUsuario(2L);
        when(gastoRepository.buscarPorId(10L)).thenReturn(Optional.of(gastoDeOtro));

        // Act
        Optional<Gasto> resultado = service.obtenerPorId(10L, 1L);

        // Assert
        assertThat(resultado).isEmpty();
    }
}