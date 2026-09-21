package com.fabio.GestionFacturas.domain.ingreso;

import com.fabio.GestionFacturas.domain.shared.Dinero;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.cglib.core.Local;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;

import static org.assertj.core.api.Assertions.*;

class IngresoTest {


    private Ingreso ingresoPendiente(LocalDate fechaEmision) {
        Dinero base = new Dinero(new BigDecimal("100.00"), "EUR");
        Dinero iva = new Dinero(new BigDecimal("21.00"), "EUR");
        Dinero total = new Dinero(new BigDecimal("121.00"), "EUR");
        return Ingreso.crear(1L, 10L, "Diseño web", fechaEmision, base,
                iva, total);
    }

    @Test
    @DisplayName("registrarrCobro sobre un ingreso pendiente lo pasa a COBRADA con su fecha")
    void registrarCobroCambiaEstadoYFecha(){
        LocalDate enmision = LocalDate.of(2026, 1,15);
        Ingreso ingreso = ingresoPendiente(enmision);
        LocalDate fechaCobro = LocalDate.of(2026, 2,1);

        ingreso.registrarCobro(fechaCobro);

        assertThat(ingreso.getEstadoCobro()).isEqualTo(EstadoCobro.COBRADA);
        assertThat(ingreso.getFechaCobro()).isEqualTo(fechaCobro);
    }

    @Test
    @DisplayName("registrarCobro sobre un ingreso ya cobrado lanza IngresoInvalidoException")
    void registrarCobroFallaSiYaCobrado(){
        Ingreso ingreso = ingresoPendiente(LocalDate.of(2026, 1, 15));
        ingreso.registrarCobro(LocalDate.of(2026,2,1));

        assertThatThrownBy(() -> ingreso.registrarCobro(LocalDate.of(2026, 2,10)))
                .isInstanceOf(IngresoInvalidoException.class);
    }

    @Test
    @DisplayName("registrarCobro con fecha anterior a la emision lanza IngresoInvalidoException")
    void registrarCobroFallaSiFechaAnteriorAEmnision(){
        Ingreso ingreso = ingresoPendiente(LocalDate.of(2026,1,15));

        assertThatThrownBy(() -> ingreso.registrarCobro(LocalDate.of(2026,1,1)))
                .isInstanceOf(IngresoInvalidoException.class);
    }

    @Test
    @DisplayName("revertirCobro sobre un cobrado lo devuelve a PENDIENTE y limpia la fecha")
    void revertirCobroVuelveAPendiente(){
        Ingreso ingreso = ingresoPendiente(LocalDate.of(2026,1,15));
        ingreso.registrarCobro(LocalDate.of(2026,2,5));

        ingreso.revertirCobro();

        assertThat(ingreso.getEstadoCobro()).isEqualTo(EstadoCobro.PENDIENTE);
        assertThat(ingreso.getFechaCobro()).isNull();

    }

    @Test
    @DisplayName("revertirCobro sobre un ingreso pendiente lanza IngresoInvalidoException")
    void revertirCobroFallaSiPendiente(){
        Ingreso ingreso = ingresoPendiente(LocalDate.of(2026,1,15));

        assertThatThrownBy(() -> ingreso.revertirCobro())
                .isInstanceOf(IngresoInvalidoException.class);

    }


    @Test
    @DisplayName("crear produce un ingreso en estado PENDIENTE")
    void crearNaceEnEstadoPendiente() {
        // Arrange
        Dinero total = new Dinero(new BigDecimal("121.00"), "EUR");

        // Act
        Ingreso ingreso = Ingreso.crear(1L, 2L, "Factura consultoría", LocalDate.now(), null, null, total);

        // Assert
        assertThat(ingreso.getEstadoCobro()).isEqualTo(EstadoCobro.PENDIENTE);
        assertThat(ingreso.getFechaCobro()).isNull();
    }

    @Test
    @DisplayName("crear guarda correctamente los datos que recibe")
    void crearGuardaLosDatos() {
        // Arrange
        Dinero base = new Dinero(new BigDecimal("100.00"), "EUR");
        Dinero iva = new Dinero(new BigDecimal("21.00"), "EUR");
        Dinero total = new Dinero(new BigDecimal("121.00"), "EUR");
        LocalDate fecha = LocalDate.of(2026, 1, 15);

        // Act
        Ingreso ingreso = Ingreso.crear(1L, 2L, "Factura consultoría", fecha, base, iva, total);

        // Assert
        assertThat(ingreso.getUsuarioId()).isEqualTo(1L);
        assertThat(ingreso.getClienteId()).isEqualTo(2L);
        assertThat(ingreso.getConcepto()).isEqualTo("Factura consultoría");
        assertThat(ingreso.getFechaEmision()).isEqualTo(fecha);
        assertThat(ingreso.getBaseImponible()).isEqualTo(base);
        assertThat(ingreso.getIva()).isEqualTo(iva);
        assertThat(ingreso.getTotal()).isEqualTo(total);
    }

    @Test
    @DisplayName("el constructor lanza IngresoInvalidoException si falta el usuario")
    void constructorRechazaUsuarioNulo() {
        // Arrange
        Dinero total = new Dinero(new BigDecimal("121.00"), "EUR");

        // Act + Assert
        assertThatThrownBy(() ->
                new Ingreso(
                        null,                    // id
                        null,                    // usuarioId  <-- dispara la excepción
                        2L,                      // clienteId
                        "Factura consultoría",   // concepto
                        LocalDate.now(),         // fechaEmision
                        null,                    // baseImponible
                        null,                    // iva
                        total,                   // total
                        EstadoCobro.PENDIENTE,   // estadoCobro
                        null,                    // fechaCobro
                        LocalDateTime.now()      // creadoEn
                ))
                .isInstanceOf(IngresoInvalidoException.class);
    }
}
