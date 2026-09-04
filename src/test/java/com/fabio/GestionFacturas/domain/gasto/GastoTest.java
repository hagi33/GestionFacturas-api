package com.fabio.GestionFacturas.domain.gasto;

import com.fabio.GestionFacturas.domain.shared.Dinero;
import org.junit.jupiter.api.Assertions;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;

import static org.assertj.core.api.Assertions.*;

class GastoTest {


    @Test
    @DisplayName("crearBorrador produce un gasto en estado BORRADOR")
    void crearBorradorNaceEnEstadoBorrador() {
        // Arrange
        Dinero base = new Dinero(new BigDecimal("100.00"), "EUR");
        Dinero iva = new Dinero(new BigDecimal("21.00"), "EUR");
        Dinero total = new Dinero(new BigDecimal("121.00"), "EUR");

        // Act
        Gasto gasto = Gasto.crearBorrador(1L, "Proveedor SL", LocalDate.now(), base, iva, total);

        // Assert
        assertThat(gasto.getEstado()).isEqualTo(EstadoGasto.BORRADOR);
    }

    @Test
    @DisplayName("crearBorrador guarda correctamente los datos que recibe")
    void crearBorradorGuardaLosDatos() {
        // Arrange
        Dinero base = new Dinero(new BigDecimal("100.00"), "EUR");
        Dinero iva = new Dinero(new BigDecimal("21.00"), "EUR");
        Dinero total = new Dinero(new BigDecimal("121.00"), "EUR");
        LocalDate fecha = LocalDate.of(2026, 1, 15);

        // Act
        Gasto gasto = Gasto.crearBorrador(1L, "Proveedor SL", fecha, base, iva, total);

        // Assert
        assertThat(gasto.getUsuarioId()).isEqualTo(1L);
        assertThat(gasto.getEmisor()).isEqualTo("Proveedor SL");
        assertThat(gasto.getFechaEmision()).isEqualTo(fecha);
        assertThat(gasto.getBaseImponible()).isEqualTo(base);
        assertThat(gasto.getIva()).isEqualTo(iva);
        assertThat(gasto.getTotal()).isEqualTo(total);
    }

    @Test
    @DisplayName("crearBorrador nace sin categoría y no deducible")
    void crearBorradorNaceSinCategoriaYNoDeducible() {
        // Arrange
        Dinero total = new Dinero(new BigDecimal("50.00"), "EUR");

        // Act
        Gasto gasto = Gasto.crearBorrador(1L, "Proveedor SL", LocalDate.now(), null, null, total);

        // Assert
        assertThat(gasto.getCategoriaId()).isNull();
        assertThat(gasto.isDeducible()).isFalse();
    }

    @Test
    @DisplayName("revisar cambia el estado a REVISADA y asigna categoría y deducible")
    void revisarActualizaEstadoCategoriaYDeducible() {
        // Arrange
        Dinero total = new Dinero(new BigDecimal("121.00"), "EUR");
        Gasto gasto = Gasto.crearBorrador(1L, "Proveedor SL", LocalDate.now(), null, null, total);

        // Act
        gasto.revisar(5L, true);

        // Assert
        assertThat(gasto.getEstado()).isEqualTo(EstadoGasto.REVISADA);
        assertThat(gasto.getCategoriaId()).isEqualTo(5L);
        assertThat(gasto.isDeducible()).isTrue();
    }

    @Test
    @DisplayName("el constructor lanza GastoInvalidoException si falta el usuario")
    void constructorRechazaUsuarioNulo() {
        // Arrange
        Dinero base = new Dinero(new BigDecimal("100.00"), "EUR");
        Dinero iva = new Dinero(new BigDecimal("21.00"), "EUR");
        Dinero total = new Dinero(new BigDecimal("121.00"), "EUR");

        // Act + Assert
        assertThatThrownBy(() ->
                new Gasto(
                        null,                    // id
                        null,                    // usuarioId  <-- dispara la excepción
                        null,                    // categoriaId
                        "Proveedor SL",          // emisor
                        LocalDate.now(),         // fechaEmision
                        base,                    // baseImponible
                        iva,                     // iva
                        total,                   // total
                        false,                   // deducible
                        EstadoGasto.BORRADOR,    // estado
                        LocalDateTime.now()      // creadoEn
                ))
                .isInstanceOf(GastoInvalidoException.class);
    }
}