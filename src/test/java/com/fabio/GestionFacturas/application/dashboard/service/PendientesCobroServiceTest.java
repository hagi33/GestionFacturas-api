package com.fabio.GestionFacturas.application.dashboard.service;

import com.fabio.GestionFacturas.application.dashboard.port.in.ObtenerPendientesCobroUseCase.ComandoObtenerPendientesCobro;
import com.fabio.GestionFacturas.application.dashboard.port.out.DashboardConsultaPort;
import com.fabio.GestionFacturas.domain.dashboard.CalculadoraPendientesCobro;
import com.fabio.GestionFacturas.domain.dashboard.PendientesCobro;
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

/** Unit tests for PendientesCobroService: orchestration only — the sum rules live in CalculadoraPendientesCobroTest. */
@ExtendWith(MockitoExtension.class)
class PendientesCobroServiceTest {

    private static final LocalDate FECHA = LocalDate.of(2026, 1, 15);

    @Mock
    private DashboardConsultaPort dashboardConsulta;

    @Mock
    private CalculadoraPendientesCobro calculadora;

    @InjectMocks
    private PendientesCobroService service;

    private Dinero eur(String cantidad) {
        return Dinero.deEuros(new BigDecimal(cantidad));
    }

    @Test
    @DisplayName("obtenerPendientes pasa los pendientes del usuario a la calculadora y devuelve su resultado")
    void obtenerPendientesDelegaEnLaCalculadora() {
        // Arrange
        List<Ingreso> pendientes = List.of(
                Ingreso.crear(1L, 10L, "Concepto", FECHA, eur("100.00"), eur("21.00"), eur("121.00")));
        PendientesCobro esperado = new PendientesCobro(pendientes, eur("121.00"));
        when(dashboardConsulta.buscarIngresosPendientesDeCobro(1L)).thenReturn(pendientes);
        when(calculadora.calcular(pendientes)).thenReturn(esperado);

        // Act
        PendientesCobro resultado = service.obtenerPendientes(new ComandoObtenerPendientesCobro(1L));

        // Assert
        assertThat(resultado).isSameAs(esperado);
        verify(calculadora).calcular(pendientes);
    }

    @Test
    @DisplayName("obtenerPendientes solo consulta los datos del usuario indicado")
    void obtenerPendientesSoloConsultaAlUsuarioIndicado() {
        // Arrange
        when(dashboardConsulta.buscarIngresosPendientesDeCobro(7L)).thenReturn(List.of());
        when(calculadora.calcular(List.of())).thenReturn(new PendientesCobro(List.of(), eur("0")));

        // Act
        service.obtenerPendientes(new ComandoObtenerPendientesCobro(7L));

        // Assert
        verify(dashboardConsulta).buscarIngresosPendientesDeCobro(7L);
        verifyNoMoreInteractions(dashboardConsulta);
    }
}
