package com.fabio.GestionFacturas.application.ingreso.service;

import com.fabio.GestionFacturas.application.ingreso.port.out.IngresoRepositoryPort;
import com.fabio.GestionFacturas.domain.ingreso.Ingreso;
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

/** Unit tests for ConsultarIngresosService, notably the access-control rule (empty for someone else's ingreso). */
@ExtendWith(MockitoExtension.class)
class ConsultarIngresosServiceTest {

    @Mock
    private IngresoRepositoryPort ingresoRepository;

    @InjectMocks
    private ConsultarIngresosService service;

    private Ingreso ingresoDeUsuario(Long usuarioId) {
        Dinero total = new Dinero(new BigDecimal("121.00"), "EUR");
        return Ingreso.crear(usuarioId, 2L, "Factura consultoría", LocalDate.now(), null, null, total);
    }

    @Test
    @DisplayName("listarPorUsuario devuelve los ingresos que retorna el repositorio")
    void listarPorUsuarioDevuelveLosIngresos() {
        // Arrange
        List<Ingreso> ingresos = List.of(ingresoDeUsuario(1L), ingresoDeUsuario(1L));
        when(ingresoRepository.buscarPorUsuario(1L)).thenReturn(ingresos);

        // Act
        List<Ingreso> resultado = service.listarPorUsuario(1L);

        // Assert
        assertThat(resultado).hasSize(2);
        verify(ingresoRepository).buscarPorUsuario(1L);
    }

    @Test
    @DisplayName("obtenerPorId devuelve el ingreso cuando existe y es del usuario")
    void obtenerPorIdDevuelveIngresoDelUsuario() {
        // Arrange
        Ingreso ingreso = ingresoDeUsuario(1L);
        when(ingresoRepository.buscarPorId(10L)).thenReturn(Optional.of(ingreso));

        // Act
        Optional<Ingreso> resultado = service.obtenerPorId(10L, 1L);

        // Assert
        assertThat(resultado).isPresent();
        assertThat(resultado.get().getUsuarioId()).isEqualTo(1L);
    }

    @Test
    @DisplayName("obtenerPorId devuelve vacío cuando el ingreso no existe")
    void obtenerPorIdVacioSiNoExiste() {
        // Arrange
        when(ingresoRepository.buscarPorId(10L)).thenReturn(Optional.empty());

        // Act
        Optional<Ingreso> resultado = service.obtenerPorId(10L, 1L);

        // Assert
        assertThat(resultado).isEmpty();
    }

    @Test
    @DisplayName("obtenerPorId devuelve vacío cuando el ingreso es de otro usuario")
    void obtenerPorIdVacioSiEsDeOtroUsuario() {
        // Arrange: el ingreso pertenece al usuario 2, pero pregunta el usuario 1
        Ingreso ingresoDeOtro = ingresoDeUsuario(2L);
        when(ingresoRepository.buscarPorId(10L)).thenReturn(Optional.of(ingresoDeOtro));

        // Act
        Optional<Ingreso> resultado = service.obtenerPorId(10L, 1L);

        // Assert
        assertThat(resultado).isEmpty();
    }
}
