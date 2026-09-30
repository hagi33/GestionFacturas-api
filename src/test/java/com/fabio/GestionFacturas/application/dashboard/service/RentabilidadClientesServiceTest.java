package com.fabio.GestionFacturas.application.dashboard.service;

import com.fabio.GestionFacturas.application.dashboard.port.in.ObtenerRentabilidadClientesUseCase.ComandoObtenerRentabilidad;
import com.fabio.GestionFacturas.application.dashboard.port.out.DashboardConsultaPort;
import com.fabio.GestionFacturas.domain.dashboard.CalculadoraRentabilidadClientes;
import com.fabio.GestionFacturas.domain.dashboard.RentabilidadCliente;
import com.fabio.GestionFacturas.domain.gasto.Gasto;
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

import static org.assertj.core.api.Assertions.*;
import static org.mockito.Mockito.*;

/** Unit tests for RentabilidadClientesService: orchestration only — the rules live in CalculadoraRentabilidadClientesTest. */
@ExtendWith(MockitoExtension.class)
class RentabilidadClientesServiceTest {

    private static final LocalDate DESDE = LocalDate.of(2026, 1, 1);
    private static final LocalDate HASTA = LocalDate.of(2026, 3, 31);

    @Mock
    private DashboardConsultaPort dashboardConsulta;

    @Mock
    private CalculadoraRentabilidadClientes calculadora;

    @InjectMocks
    private RentabilidadClientesService service;

    private Dinero eur(String cantidad) {
        return Dinero.deEuros(new BigDecimal(cantidad));
    }

    @Test
    @DisplayName("obtenerRentabilidad pasa los gastos e ingresos del usuario y periodo a la calculadora y devuelve su resultado")
    void obtenerRentabilidadDelegaEnLaCalculadora() {
        // Arrange
        List<Gasto> gastos = List.of(
                Gasto.crearBorrador(1L, 10L, "Proveedor", DESDE, eur("50.00"), eur("10.50"), eur("60.50")));
        List<Ingreso> ingresos = List.of(
                Ingreso.crear(1L, 10L, "Concepto", DESDE, eur("100.00"), eur("21.00"), eur("121.00")));
        List<RentabilidadCliente> esperado = List.of(new RentabilidadCliente(10L, null));
        when(dashboardConsulta.buscarGastosPorPeriodo(1L, DESDE, HASTA)).thenReturn(gastos);
        when(dashboardConsulta.buscarIngresosPorPeriodo(1L, DESDE, HASTA)).thenReturn(ingresos);
        when(calculadora.calcular(gastos, ingresos)).thenReturn(esperado);

        // Act
        List<RentabilidadCliente> resultado =
                service.obtenerRentabilidad(new ComandoObtenerRentabilidad(1L, DESDE, HASTA));

        // Assert
        assertThat(resultado).isSameAs(esperado);
        verify(dashboardConsulta).buscarGastosPorPeriodo(1L, DESDE, HASTA);
        verify(dashboardConsulta).buscarIngresosPorPeriodo(1L, DESDE, HASTA);
        verify(calculadora).calcular(gastos, ingresos);
    }

    @Test
    @DisplayName("obtenerRentabilidad con 'desde' posterior a 'hasta' lanza IllegalArgumentException sin consultar nada")
    void obtenerRentabilidadRechazaRangoInvertido() {
        // Act + Assert
        assertThatThrownBy(() -> service.obtenerRentabilidad(new ComandoObtenerRentabilidad(1L, HASTA, DESDE)))
                .isInstanceOf(IllegalArgumentException.class);

        verifyNoInteractions(dashboardConsulta, calculadora);
    }
}
