package com.fabio.GestionFacturas.domain.dashboard;

import com.fabio.GestionFacturas.domain.ingreso.Ingreso;
import com.fabio.GestionFacturas.domain.shared.Dinero;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;

import static org.assertj.core.api.Assertions.*;

class CalculadoraPendientesCobroTest {

    private static final LocalDate FECHA = LocalDate.of(2026, 1, 15);

    private final CalculadoraPendientesCobro calculadora = new CalculadoraPendientesCobro();

    private Dinero eur(String cantidad) {
        if (cantidad == null) {
            return null;
        }
        return Dinero.deEuros(new BigDecimal(cantidad));
    }

    private Ingreso ingresoPendiente(String concepto, String base, String iva, String total) {
        return Ingreso.crear(1L, 10L, concepto, FECHA, eur(base), eur(iva), eur(total));
    }

    @Test
    @DisplayName("con varios pendientes suma el total (con IVA) de todos")
    void sumaElTotalDeTodosLosPendientes() {
        // Arrange
        List<Ingreso> pendientes = List.of(
                ingresoPendiente("A", "1000.00", "210.00", "1210.00"),
                ingresoPendiente("B", "800.00", "168.00", "968.00"),
                ingresoPendiente("C", "50.00", "10.50", "60.50"));

        // Act
        PendientesCobro resultado = calculadora.calcular(pendientes);

        // Assert
        assertThat(resultado.total().cantidad()).isEqualByComparingTo("2238.50");
    }

    @Test
    @DisplayName("sin pendientes el total es cero (nunca null) y la lista está vacía")
    void sinPendientesTotalCero() {
        // Act
        PendientesCobro resultado = calculadora.calcular(List.of());

        // Assert
        assertThat(resultado.total()).isNotNull();
        assertThat(resultado.total().cantidad()).isEqualByComparingTo("0");
        assertThat(resultado.ingresos()).isEmpty();
    }

    @Test
    @DisplayName("un pendiente con total null cuenta como cero en la suma")
    void totalNullCuentaComoCero() {
        // Arrange
        List<Ingreso> pendientes = List.of(
                ingresoPendiente("A", "100.00", "21.00", "121.00"),
                ingresoPendiente("B", null, null, null));

        // Act
        PendientesCobro resultado = calculadora.calcular(pendientes);

        // Assert
        assertThat(resultado.total().cantidad()).isEqualByComparingTo("121.00");
        assertThat(resultado.ingresos()).hasSize(2);
    }

    @Test
    @DisplayName("devuelve los mismos ingresos en el mismo orden recibido")
    void conservaLosIngresosYSuOrden() {
        // Arrange
        Ingreso primero = ingresoPendiente("A", "100.00", "21.00", "121.00");
        Ingreso segundo = ingresoPendiente("B", "200.00", "42.00", "242.00");

        // Act
        PendientesCobro resultado = calculadora.calcular(List.of(primero, segundo));

        // Assert
        assertThat(resultado.ingresos()).containsExactly(primero, segundo);
    }
}
