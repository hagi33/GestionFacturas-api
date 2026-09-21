package com.fabio.GestionFacturas.application.ingreso.service;

import com.fabio.GestionFacturas.application.ingreso.port.in.CrearIngresoUseCase.ComandoCrearIngreso;
import com.fabio.GestionFacturas.application.ingreso.port.out.IngresoRepositoryPort;
import com.fabio.GestionFacturas.domain.ingreso.Ingreso;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.math.BigDecimal;
import java.time.LocalDate;

import static org.assertj.core.api.Assertions.*;
import static org.mockito.Mockito.*;

/**
 * Unit tests for CrearIngresoService with the outbound port mocked ({@code IngresoRepositoryPort})
 * — verifies the service's own logic (Dinero conversion, EUR default) in isolation from any real
 * persistence adapter, per the project's "mock the ports" testing convention.
 */
@ExtendWith(MockitoExtension.class)
class CrearIngresoServiceTest {

    @Mock
    private IngresoRepositoryPort ingresoRepositoryPort;

    @InjectMocks
    private CrearIngresoService service;

    @Test
    @DisplayName("crear convierte los importes a Dinero y delega en el repositorio")
    void crearConvierteImportesYGuarda() {
        // Arrange
        ComandoCrearIngreso comando = new ComandoCrearIngreso(
                1L, 2L, "Factura consultoría", LocalDate.of(2026, 1, 15),
                new BigDecimal("100.00"), new BigDecimal("21.00"), new BigDecimal("121.00"), "EUR");

        when(ingresoRepositoryPort.guardar(any(Ingreso.class)))
                .thenAnswer(invocationOnMock -> invocationOnMock.getArgument(0));

        // Act
        Ingreso resultado = service.crear(comando);

        // Assert
        assertThat(resultado.getTotal().cantidad()).isEqualTo(new BigDecimal("121.00"));
        assertThat(resultado.getTotal().moneda()).isEqualTo("EUR");
        verify(ingresoRepositoryPort).guardar(any(Ingreso.class));
    }

    @Test
    @DisplayName("aplica el valor EUR por defecto cuando la moneda es null")
    void asignaValorEURCuandoEsNull() {
        // Arrange
        ComandoCrearIngreso comando = new ComandoCrearIngreso(
                1L, 2L, "Factura consultoría", LocalDate.of(2026, 1, 15),
                new BigDecimal("100.00"), new BigDecimal("21.00"), new BigDecimal("121.00"), null);

        when(ingresoRepositoryPort.guardar(any(Ingreso.class)))
                .thenAnswer(invocationOnMock -> invocationOnMock.getArgument(0));

        // Act
        Ingreso resultado = service.crear(comando);

        // Assert
        assertThat(resultado.getTotal().moneda()).isEqualTo("EUR");
        verify(ingresoRepositoryPort).guardar(any(Ingreso.class));
    }

    @Test
    @DisplayName("maneja valores nulos sin reventar")
    void manejarValoresNulos() {
        // Arrange
        ComandoCrearIngreso comando = new ComandoCrearIngreso(
                1L, 2L, "Factura consultoría", LocalDate.of(2026, 1, 15),
                null, null, new BigDecimal("121.00"), "EUR");

        when(ingresoRepositoryPort.guardar(any(Ingreso.class)))
                .thenAnswer(invocationOnMock -> invocationOnMock.getArgument(0));

        // Act
        Ingreso resultado = service.crear(comando);

        // Assert
        assertThat(resultado.getBaseImponible()).isNull();
        assertThat(resultado.getIva()).isNull();
    }
}
