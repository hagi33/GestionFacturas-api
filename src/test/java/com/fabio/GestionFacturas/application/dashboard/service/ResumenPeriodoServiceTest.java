package com.fabio.GestionFacturas.application.dashboard.service;

import com.fabio.GestionFacturas.application.dashboard.port.in.ObtenerResumenPeriodoUseCase.ComandoObtenerResumen;
import com.fabio.GestionFacturas.application.dashboard.port.out.DashboardConsultaPort;
import com.fabio.GestionFacturas.domain.dashboard.CalculadoraResumenPeriodo;
import com.fabio.GestionFacturas.domain.dashboard.ResumenPeriodo;
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

/** Unit tests for ResumenPeriodoService: orchestration only — the calculation rules live in CalculadoraResumenPeriodoTest. */
@ExtendWith(MockitoExtension.class)
class ResumenPeriodoServiceTest {

    private static final LocalDate DESDE = LocalDate.of(2026, 1, 1);
    private static final LocalDate HASTA = LocalDate.of(2026, 3, 31);

    @Mock
    private DashboardConsultaPort dashboardConsulta;

    @Mock
    private CalculadoraResumenPeriodo calculadora;

    @InjectMocks
    private ResumenPeriodoService service;

    private Dinero eur(String cantidad) {
        return Dinero.deEuros(new BigDecimal(cantidad));
    }

    private ResumenPeriodo resumenDeEjemplo() {
        Dinero cero = Dinero.deEuros(BigDecimal.ZERO);
        return new ResumenPeriodo(eur("121.00"), eur("100.00"), cero, cero, eur("60.50"), eur("50.00"),
                eur("-60.50"), eur("-50.00"), eur("60.50"), eur("50.00"));
    }

    @Test
    @DisplayName("obtenerResumen pasa los gastos e ingresos del periodo a la calculadora y devuelve su resultado")
    void obtenerResumenDelegaEnLaCalculadora() {
        // Arrange
        List<Gasto> gastos = List.of(
                Gasto.crearBorrador(1L, null, "Proveedor", DESDE, eur("50.00"), eur("10.50"), eur("60.50")));
        List<Ingreso> ingresos = List.of(
                Ingreso.crear(1L, 10L, "Concepto", DESDE, eur("100.00"), eur("21.00"), eur("121.00")));
        ResumenPeriodo esperado = resumenDeEjemplo();
        when(dashboardConsulta.buscarGastosPorPeriodo(1L, DESDE, HASTA)).thenReturn(gastos);
        when(dashboardConsulta.buscarIngresosPorPeriodo(1L, DESDE, HASTA)).thenReturn(ingresos);
        when(calculadora.calcular(gastos, ingresos)).thenReturn(esperado);

        // Act
        ResumenPeriodo resultado = service.obtenerResumen(new ComandoObtenerResumen(1L, DESDE, HASTA));

        // Assert
        assertThat(resultado).isSameAs(esperado);
        verify(dashboardConsulta).buscarGastosPorPeriodo(1L, DESDE, HASTA);
        verify(dashboardConsulta).buscarIngresosPorPeriodo(1L, DESDE, HASTA);
        verify(calculadora).calcular(gastos, ingresos);
    }

    @Test
    @DisplayName("obtenerResumen con 'desde' posterior a 'hasta' lanza IllegalArgumentException sin consultar nada")
    void obtenerResumenRechazaRangoInvertido() {
        // Act + Assert
        assertThatThrownBy(() -> service.obtenerResumen(new ComandoObtenerResumen(1L, HASTA, DESDE)))
                .isInstanceOf(IllegalArgumentException.class);

        verifyNoInteractions(dashboardConsulta, calculadora);
    }
}
