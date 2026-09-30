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

/** Per-client grouping rules; the figures themselves reuse CalculadoraResumenPeriodo (real instance, not mocked). */
class CalculadoraRentabilidadClientesTest {

    private static final LocalDate FECHA = LocalDate.of(2026, 1, 15);

    private final CalculadoraRentabilidadClientes calculadora =
            new CalculadoraRentabilidadClientes(new CalculadoraResumenPeriodo());

    private Dinero eur(String cantidad) {
        if (cantidad == null) {
            return null;
        }
        return Dinero.deEuros(new BigDecimal(cantidad));
    }

    private Gasto gasto(Long clienteId, String base, String iva, String total) {
        return Gasto.crearBorrador(1L, clienteId, "Proveedor", FECHA, eur(base), eur(iva), eur(total));
    }

    private Ingreso ingresoPendiente(Long clienteId, String base, String iva, String total) {
        return Ingreso.crear(1L, clienteId, "Concepto", FECHA, eur(base), eur(iva), eur(total));
    }

    private Ingreso ingresoCobrado(Long clienteId, String base, String iva, String total) {
        Ingreso ingreso = ingresoPendiente(clienteId, base, iva, total);
        ingreso.registrarCobro(FECHA.plusDays(10));
        return ingreso;
    }

    @Test
    @DisplayName("con varios clientes devuelve una entrada por cliente, ordenadas por clienteId, con sus cifras")
    void unaEntradaPorClienteConSusCifras() {
        // Arrange
        List<Ingreso> ingresos = List.of(
                ingresoCobrado(20L, "500.00", "105.00", "605.00"),
                ingresoCobrado(10L, "1000.00", "210.00", "1210.00"));
        List<Gasto> gastos = List.of(
                gasto(10L, "200.00", "42.00", "242.00"),
                gasto(20L, "100.00", "21.00", "121.00"));

        // Act
        List<RentabilidadCliente> r = calculadora.calcular(gastos, ingresos);

        // Assert
        assertThat(r).extracting(RentabilidadCliente::clienteId).containsExactly(10L, 20L);
        ResumenPeriodo c10 = r.get(0).resumen();
        assertThat(c10.facturadoTotal().cantidad()).isEqualByComparingTo("1210.00");
        assertThat(c10.gastosTotal().cantidad()).isEqualByComparingTo("242.00");
        assertThat(c10.beneficioCajaTotal().cantidad()).isEqualByComparingTo("968.00");
        assertThat(c10.beneficioCajaBase().cantidad()).isEqualByComparingTo("800.00");
        ResumenPeriodo c20 = r.get(1).resumen();
        assertThat(c20.facturadoTotal().cantidad()).isEqualByComparingTo("605.00");
        assertThat(c20.gastosTotal().cantidad()).isEqualByComparingTo("121.00");
        assertThat(c20.beneficioCajaTotal().cantidad()).isEqualByComparingTo("484.00");
    }

    @Test
    @DisplayName("los gastos sin cliente van a una entrada 'sin cliente' al final, solo con gastos")
    void gastosSinClienteVanAEntradaSinCliente() {
        // Arrange
        List<Ingreso> ingresos = List.of(ingresoCobrado(10L, "1000.00", "210.00", "1210.00"));
        List<Gasto> gastos = List.of(
                gasto(null, "50.00", "10.50", "60.50"),
                gasto(null, "100.00", "21.00", "121.00"));

        // Act
        List<RentabilidadCliente> r = calculadora.calcular(gastos, ingresos);

        // Assert
        assertThat(r).hasSize(2);
        assertThat(r.get(0).clienteId()).isEqualTo(10L);
        assertThat(r.get(0).resumen().gastosTotal().cantidad()).isEqualByComparingTo("0");
        RentabilidadCliente sinCliente = r.get(1);
        assertThat(sinCliente.esSinCliente()).isTrue();
        assertThat(sinCliente.resumen().facturadoTotal().cantidad()).isEqualByComparingTo("0");
        assertThat(sinCliente.resumen().gastosTotal().cantidad()).isEqualByComparingTo("181.50");
        assertThat(sinCliente.resumen().beneficioCajaTotal().cantidad()).isEqualByComparingTo("-181.50");
    }

    @Test
    @DisplayName("sin gastos con clienteId null no hay entrada 'sin cliente' (ni siquiera a cero)")
    void sinGastosSinClienteNoHayEntradaSinCliente() {
        // Arrange
        List<Ingreso> ingresos = List.of(ingresoCobrado(10L, "1000.00", "210.00", "1210.00"));
        List<Gasto> gastos = List.of(gasto(10L, "200.00", "42.00", "242.00"));

        // Act
        List<RentabilidadCliente> r = calculadora.calcular(gastos, ingresos);

        // Assert
        assertThat(r).hasSize(1);
        assertThat(r).noneMatch(RentabilidadCliente::esSinCliente);
    }

    @Test
    @DisplayName("un cliente con ingresos y sin gastos: gastos a cero y beneficio igual a lo facturado/cobrado")
    void clienteSinGastosBeneficioIgualAIngresos() {
        // Arrange
        List<Ingreso> ingresos = List.of(ingresoCobrado(10L, "1000.00", "210.00", "1210.00"));

        // Act
        List<RentabilidadCliente> r = calculadora.calcular(List.of(), ingresos);

        // Assert
        assertThat(r).hasSize(1);
        ResumenPeriodo res = r.get(0).resumen();
        assertThat(res.gastosTotal().cantidad()).isEqualByComparingTo("0");
        assertThat(res.gastosBase().cantidad()).isEqualByComparingTo("0");
        assertThat(res.beneficioCajaTotal().cantidad()).isEqualByComparingTo("1210.00");
        assertThat(res.beneficioCajaBase().cantidad()).isEqualByComparingTo("1000.00");
        assertThat(res.beneficioFacturadoTotal().cantidad()).isEqualByComparingTo("1210.00");
        assertThat(res.beneficioFacturadoBase().cantidad()).isEqualByComparingTo("1000.00");
    }

    @Test
    @DisplayName("con listas vacías devuelve una lista vacía")
    void listasVaciasDevuelvenListaVacia() {
        // Act
        List<RentabilidadCliente> r = calculadora.calcular(List.of(), List.of());

        // Assert
        assertThat(r).isEmpty();
    }

    @Test
    @DisplayName("con un ingreso COBRADA y otro PENDIENTE, cobrado cuenta solo el cobrado y facturado ambos")
    void cobradoSoloCuentaCobradasFacturadoAmbas() {
        // Arrange
        List<Ingreso> ingresos = List.of(
                ingresoCobrado(10L, "1000.00", "210.00", "1210.00"),
                ingresoPendiente(10L, "500.00", "105.00", "605.00"));

        // Act
        List<RentabilidadCliente> r = calculadora.calcular(List.of(), ingresos);

        // Assert
        assertThat(r).hasSize(1);
        ResumenPeriodo res = r.get(0).resumen();
        assertThat(res.cobradoTotal().cantidad()).isEqualByComparingTo("1210.00");
        assertThat(res.cobradoBase().cantidad()).isEqualByComparingTo("1000.00");
        assertThat(res.facturadoTotal().cantidad()).isEqualByComparingTo("1815.00");
        assertThat(res.facturadoBase().cantidad()).isEqualByComparingTo("1500.00");
    }

    @Test
    @DisplayName("un cliente con gastos pero sin ingresos también aparece, con beneficio negativo")
    void clienteSoloConGastosAparece() {
        // Arrange
        List<Gasto> gastos = List.of(gasto(30L, "100.00", "21.00", "121.00"));

        // Act
        List<RentabilidadCliente> r = calculadora.calcular(gastos, List.of());

        // Assert
        assertThat(r).extracting(RentabilidadCliente::clienteId).containsExactly(30L);
        assertThat(r.get(0).resumen().beneficioFacturadoTotal().cantidad()).isEqualByComparingTo("-121.00");
    }
}
