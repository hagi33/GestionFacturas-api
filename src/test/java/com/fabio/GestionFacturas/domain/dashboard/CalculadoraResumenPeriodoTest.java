package com.fabio.GestionFacturas.domain.dashboard;

import com.fabio.GestionFacturas.domain.gasto.Gasto;
import com.fabio.GestionFacturas.domain.ingreso.Ingreso;
import com.fabio.GestionFacturas.domain.shared.Dinero;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;

import static org.assertj.core.api.Assertions.*;

class CalculadoraResumenPeriodoTest {

    private static final LocalDate FECHA = LocalDate.of(2026, 1, 15);

    private final CalculadoraResumenPeriodo calculadora = new CalculadoraResumenPeriodo();

    private Dinero eur(String cantidad) {
        if (cantidad == null) {
            return null;
        }
        return Dinero.deEuros(new BigDecimal(cantidad));
    }

    private Gasto gasto(String base, String iva, String total) {
        return Gasto.crearBorrador(1L, null, "Proveedor", FECHA, eur(base), eur(iva), eur(total));
    }

    private Ingreso ingresoPendiente(String base, String iva, String total) {
        return Ingreso.crear(1L, 10L, "Concepto", FECHA, eur(base), eur(iva), eur(total));
    }

    private Ingreso ingresoCobrado(String base, String iva, String total) {
        Ingreso ingreso = ingresoPendiente(base, iva, total);
        ingreso.registrarCobro(FECHA.plusDays(10));
        return ingreso;
    }

    @Test
    @DisplayName("con ingresos cobrados, pendientes y gastos calcula todas las cifras")
    void calculaTodasLasCifrasConMezcla() {
        // Arrange
        List<Ingreso> ingresos = List.of(
                ingresoCobrado("1000.00", "210.00", "1210.00"),
                ingresoCobrado("500.00", "105.00", "605.00"),
                ingresoPendiente("800.00", "168.00", "968.00"));
        List<Gasto> gastos = List.of(
                gasto("200.00", "42.00", "242.00"),
                gasto("50.00", "10.50", "60.50"));

        // Act
        ResumenPeriodo r = calculadora.calcular(gastos, ingresos);

        // Assert
        assertThat(r.facturadoTotal().cantidad()).isEqualByComparingTo("2783.00");
        assertThat(r.facturadoBase().cantidad()).isEqualByComparingTo("2300.00");
        assertThat(r.cobradoTotal().cantidad()).isEqualByComparingTo("1815.00");
        assertThat(r.cobradoBase().cantidad()).isEqualByComparingTo("1500.00");
        assertThat(r.gastosTotal().cantidad()).isEqualByComparingTo("302.50");
        assertThat(r.gastosBase().cantidad()).isEqualByComparingTo("250.00");
        assertThat(r.beneficioCajaTotal().cantidad()).isEqualByComparingTo("1512.50");
        assertThat(r.beneficioCajaBase().cantidad()).isEqualByComparingTo("1250.00");
        assertThat(r.beneficioFacturadoTotal().cantidad()).isEqualByComparingTo("2480.50");
        assertThat(r.beneficioFacturadoBase().cantidad()).isEqualByComparingTo("2050.00");
    }

    @Test
    @DisplayName("si todos los ingresos están cobrados, cobrado es igual a facturado")
    void soloCobradosCobradoIgualAFacturado() {
        // Arrange
        List<Ingreso> ingresos = List.of(
                ingresoCobrado("1000.00", "210.00", "1210.00"),
                ingresoCobrado("500.00", "105.00", "605.00"));
        List<Gasto> gastos = List.of(gasto("200.00", "42.00", "242.00"));

        // Act
        ResumenPeriodo r = calculadora.calcular(gastos, ingresos);

        // Assert
        assertThat(r.facturadoTotal().cantidad()).isEqualByComparingTo("1815.00");
        assertThat(r.facturadoBase().cantidad()).isEqualByComparingTo("1500.00");
        assertThat(r.cobradoTotal().cantidad()).isEqualByComparingTo("1815.00");
        assertThat(r.cobradoBase().cantidad()).isEqualByComparingTo("1500.00");
        assertThat(r.beneficioCajaTotal().cantidad()).isEqualByComparingTo("1573.00");
        assertThat(r.beneficioCajaBase().cantidad()).isEqualByComparingTo("1300.00");
        assertThat(r.beneficioFacturadoTotal().cantidad()).isEqualByComparingTo("1573.00");
        assertThat(r.beneficioFacturadoBase().cantidad()).isEqualByComparingTo("1300.00");
    }

    @Test
    @DisplayName("si todos los ingresos están pendientes, cobrado es cero y facturado no")
    void soloPendientesCobradoCero() {
        // Arrange
        List<Ingreso> ingresos = List.of(
                ingresoPendiente("800.00", "168.00", "968.00"),
                ingresoPendiente("400.00", "84.00", "484.00"));
        List<Gasto> gastos = List.of(gasto("200.00", "42.00", "242.00"));

        // Act
        ResumenPeriodo r = calculadora.calcular(gastos, ingresos);

        // Assert
        assertThat(r.cobradoTotal().cantidad()).isEqualByComparingTo(BigDecimal.ZERO);
        assertThat(r.cobradoBase().cantidad()).isEqualByComparingTo(BigDecimal.ZERO);
        assertThat(r.facturadoTotal().cantidad()).isEqualByComparingTo("1452.00");
        assertThat(r.facturadoBase().cantidad()).isEqualByComparingTo("1200.00");
        assertThat(r.beneficioCajaTotal().cantidad()).isEqualByComparingTo("-242.00");
        assertThat(r.beneficioCajaBase().cantidad()).isEqualByComparingTo("-200.00");
        assertThat(r.beneficioFacturadoTotal().cantidad()).isEqualByComparingTo("1210.00");
        assertThat(r.beneficioFacturadoBase().cantidad()).isEqualByComparingTo("1000.00");
    }

    @Test
    @DisplayName("sin ingresos, facturado y cobrado son cero y el beneficio es el gasto en negativo")
    void sinIngresosBeneficioNegativo() {
        // Arrange
        List<Gasto> gastos = List.of(
                gasto("200.00", "42.00", "242.00"),
                gasto("50.00", "10.50", "60.50"));

        // Act
        ResumenPeriodo r = calculadora.calcular(gastos, List.of());

        // Assert
        assertThat(r.facturadoTotal().cantidad()).isEqualByComparingTo(BigDecimal.ZERO);
        assertThat(r.facturadoBase().cantidad()).isEqualByComparingTo(BigDecimal.ZERO);
        assertThat(r.cobradoTotal().cantidad()).isEqualByComparingTo(BigDecimal.ZERO);
        assertThat(r.cobradoBase().cantidad()).isEqualByComparingTo(BigDecimal.ZERO);
        assertThat(r.gastosTotal().cantidad()).isEqualByComparingTo("302.50");
        assertThat(r.gastosBase().cantidad()).isEqualByComparingTo("250.00");
        assertThat(r.beneficioCajaTotal().cantidad()).isEqualByComparingTo("-302.50");
        assertThat(r.beneficioCajaBase().cantidad()).isEqualByComparingTo("-250.00");
        assertThat(r.beneficioFacturadoTotal().cantidad()).isEqualByComparingTo("-302.50");
        assertThat(r.beneficioFacturadoBase().cantidad()).isEqualByComparingTo("-250.00");
    }

    @Test
    @DisplayName("sin gastos, el beneficio es igual al ingreso correspondiente")
    void sinGastosBeneficioIgualAIngreso() {
        // Arrange
        List<Ingreso> ingresos = List.of(
                ingresoCobrado("1000.00", "210.00", "1210.00"),
                ingresoPendiente("800.00", "168.00", "968.00"));

        // Act
        ResumenPeriodo r = calculadora.calcular(List.of(), ingresos);

        // Assert
        assertThat(r.gastosTotal().cantidad()).isEqualByComparingTo(BigDecimal.ZERO);
        assertThat(r.gastosBase().cantidad()).isEqualByComparingTo(BigDecimal.ZERO);
        assertThat(r.cobradoTotal().cantidad()).isEqualByComparingTo("1210.00");
        assertThat(r.cobradoBase().cantidad()).isEqualByComparingTo("1000.00");
        assertThat(r.facturadoTotal().cantidad()).isEqualByComparingTo("2178.00");
        assertThat(r.facturadoBase().cantidad()).isEqualByComparingTo("1800.00");
        assertThat(r.beneficioCajaTotal().cantidad()).isEqualByComparingTo("1210.00");
        assertThat(r.beneficioCajaBase().cantidad()).isEqualByComparingTo("1000.00");
        assertThat(r.beneficioFacturadoTotal().cantidad()).isEqualByComparingTo("2178.00");
        assertThat(r.beneficioFacturadoBase().cantidad()).isEqualByComparingTo("1800.00");
    }

    @Test
    @DisplayName("con listas vacías todas las cifras son cero, nunca null")
    void listasVaciasTodoCero() {
        // Act
        ResumenPeriodo r = calculadora.calcular(List.of(), List.of());

        // Assert
        List<Dinero> cifras = List.of(
                r.facturadoTotal(), r.facturadoBase(),
                r.cobradoTotal(), r.cobradoBase(),
                r.gastosTotal(), r.gastosBase(),
                r.beneficioCajaTotal(), r.beneficioCajaBase(),
                r.beneficioFacturadoTotal(), r.beneficioFacturadoBase());
        for (Dinero cifra : cifras) {
            assertThat(cifra).isNotNull();
            assertThat(cifra.cantidad()).isEqualByComparingTo(BigDecimal.ZERO);
        }
    }

    @Test
    @DisplayName("las sumas con decimales son exactas (BigDecimal, sin error de coma flotante)")
    void decimalesExactos() {
        // Arrange
        List<Gasto> gastos = List.of(
                gasto("0.10", "0.00", "0.10"),
                gasto("0.20", "0.00", "0.20"),
                gasto("0.70", "0.00", "0.70"));
        List<Ingreso> ingresos = List.of(
                ingresoCobrado("33.33", "7.00", "40.33"),
                ingresoCobrado("33.33", "7.00", "40.33"),
                ingresoCobrado("33.33", "7.00", "40.33"));

        // Act
        ResumenPeriodo r = calculadora.calcular(gastos, ingresos);

        // Assert
        assertThat(r.gastosTotal().cantidad()).isEqualByComparingTo("1.00");
        assertThat(r.gastosBase().cantidad()).isEqualByComparingTo("1.00");
        assertThat(r.facturadoTotal().cantidad()).isEqualByComparingTo("120.99");
        assertThat(r.facturadoBase().cantidad()).isEqualByComparingTo("99.99");
        assertThat(r.cobradoTotal().cantidad()).isEqualByComparingTo("120.99");
        assertThat(r.cobradoBase().cantidad()).isEqualByComparingTo("99.99");
        assertThat(r.beneficioCajaTotal().cantidad()).isEqualByComparingTo("119.99");
        assertThat(r.beneficioCajaBase().cantidad()).isEqualByComparingTo("98.99");
        assertThat(r.beneficioFacturadoTotal().cantidad()).isEqualByComparingTo("119.99");
        assertThat(r.beneficioFacturadoBase().cantidad()).isEqualByComparingTo("98.99");
    }

    @Test
    @DisplayName("base e IVA nulos cuentan como cero y no rompen la suma")
    void baseEIvaNulosCuentanComoCero() {
        // Arrange
        List<Gasto> gastos = List.of(
                gasto(null, null, "50.00"),
                gasto("100.00", "21.00", "121.00"));
        List<Ingreso> ingresos = List.of(
                ingresoCobrado(null, null, "121.00"),
                ingresoCobrado("200.00", "42.00", "242.00"));

        // Act
        ResumenPeriodo r = calculadora.calcular(gastos, ingresos);

        // Assert
        assertThat(r.gastosTotal().cantidad()).isEqualByComparingTo("171.00");
        assertThat(r.gastosBase().cantidad()).isEqualByComparingTo("100.00");
        assertThat(r.facturadoTotal().cantidad()).isEqualByComparingTo("363.00");
        assertThat(r.facturadoBase().cantidad()).isEqualByComparingTo("200.00");
        assertThat(r.cobradoTotal().cantidad()).isEqualByComparingTo("363.00");
        assertThat(r.cobradoBase().cantidad()).isEqualByComparingTo("200.00");
        assertThat(r.beneficioCajaTotal().cantidad()).isEqualByComparingTo("192.00");
        assertThat(r.beneficioCajaBase().cantidad()).isEqualByComparingTo("100.00");
        assertThat(r.beneficioFacturadoTotal().cantidad()).isEqualByComparingTo("192.00");
        assertThat(r.beneficioFacturadoBase().cantidad()).isEqualByComparingTo("100.00");
    }
}
