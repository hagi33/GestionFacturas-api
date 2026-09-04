package com.fabio.GestionFacturas.domain.shared;

import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.math.BigDecimal;

import org.assertj.core.api.Assertions.*;


import static org.assertj.core.api.Assertions.*;


class DineroTest {


    @Test
    @DisplayName("crea un Dinero válido con cantidad y moneda correctas")
    void crearDineroValido(){
        //Arrange
        BigDecimal cantidad = new BigDecimal("100.00");

        //Act
        Dinero dinero = new Dinero(cantidad, "EUR");

        //Assert
        assertThat(dinero.cantidad()).isEqualTo(new BigDecimal("100.00"));
        assertThat(dinero.moneda()).isEqualTo("EUR");
    }

    @Test
    @DisplayName("rechaza una cantidad con más de dos decimales")
    void rechazarMasDeDosDecimales(){
        //Act + Assert (van juntos al probar excepciones)
        assertThatThrownBy(() -> new Dinero(new BigDecimal("10.999"), "EUR"))
                .isInstanceOf(IllegalArgumentException.class);
    }

    @Test
    @DisplayName("rechaza una moneda quen no tenga tres letras")
    void rechazarMonedaSinTresLetras(){

        assertThatThrownBy(() -> new Dinero(new BigDecimal("100.00"), "EURO"))
                .isInstanceOf(IllegalArgumentException.class);

    }


    @Test
    @DisplayName("rechaza una cantidad nula")
    void rechazarCantidadNula(){

        assertThatThrownBy(() -> new Dinero(null, "EUR"))
                .isInstanceOf(NullPointerException.class);

    }

    @Test
    @DisplayName("comprueba que la moneda es correcta")
    void comprobarMoneda(){

        Dinero dinero = Dinero.deEuros(new BigDecimal("50.00"));

        assertThat(dinero.moneda()).isEqualTo("EUR");

    }


    @Test
    @DisplayName("comprueba que se aceptan menos de dos decimales en la cantidad")
    void aceptarMenosDeDosDecimales(){

        BigDecimal cantidad = new BigDecimal("100.0");

        Dinero dinero = new Dinero(cantidad, "EUR");

        assertThat(dinero.cantidad()).isEqualTo(new BigDecimal("100.0"));

    }


    @Test
    @DisplayName("acepta una cantidad sin decimales")
    void aceptarSinDecimales() {
        Dinero dinero = new Dinero(new BigDecimal("100"), "EUR");

        assertThat(dinero.cantidad()).isEqualTo(new BigDecimal("100"));
    }


    @Test
    @DisplayName("rechaza moneda demasiado corta")
    void rechazarMonedaDemasiadoCorta(){

        assertThatThrownBy(() -> new Dinero(new BigDecimal("100.00"), "EU"))
                .isInstanceOf(IllegalArgumentException.class);

    }


    @Test
    @DisplayName("rechaza moneda nula")
    void rechazarMonedaNula(){

        assertThatThrownBy(() -> new Dinero(new BigDecimal("100.00"), null))
                .isInstanceOf(NullPointerException.class);


    }



}