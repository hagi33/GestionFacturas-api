package com.fabio.GestionFacturas.application.gasto.service;

import com.fabio.GestionFacturas.application.gasto.port.in.CrearGastoUseCase;
import com.fabio.GestionFacturas.application.gasto.port.out.GastoRepositoryPort;
import com.fabio.GestionFacturas.domain.gasto.Gasto;
import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import com.fabio.GestionFacturas.application.gasto.port.in.CrearGastoUseCase.ComandoCrearGasto;
import static org.mockito.Mockito.*;

import java.math.BigDecimal;
import java.time.LocalDate;

import static org.assertj.core.api.Assertions.*;


@ExtendWith(MockitoExtension.class)
class CrearGastoServiceTest {


    @Mock
    private GastoRepositoryPort gastoRepositoryPort;


    @InjectMocks
    private CrearGastoService service;


    @Test
    @DisplayName("crear convierte los importes a Dinero y delega en el repositorio")
    void crearConvierteImportesYGuarda(){
        //Arrange
        ComandoCrearGasto comando = new ComandoCrearGasto(
                1L, "Proveedor SL", LocalDate.of(2026, 1, 15),
                new BigDecimal("100.00"), new BigDecimal("21.00"), new BigDecimal("121.00"), "EUR");

        when(gastoRepositoryPort.guardar(any(Gasto.class)))
                .thenAnswer(invocationOnMock -> invocationOnMock.getArgument(0));


        //Act
        Gasto resultado = service.crear(comando);

        //Assert
        assertThat(resultado.getTotal().cantidad()).isEqualTo(new BigDecimal("121.00"));
        assertThat(resultado.getTotal().moneda()).isEqualTo("EUR");
        verify(gastoRepositoryPort).guardar(any(Gasto.class));

    }


    @Test
    @DisplayName("aplica el valor EUR por defecto cuando la moneda es null")
    void asignaValorEURCuandoEsNull(){

        ComandoCrearGasto comando = new ComandoCrearGasto(
                1L, "Proveedor SL", LocalDate.of(2026, 1, 15),
                new BigDecimal("100.00"), new BigDecimal("21.00"), new BigDecimal("121.00"), null);


        when(gastoRepositoryPort.guardar(any(Gasto.class)))
                .thenAnswer(invocationOnMock -> invocationOnMock.getArgument(0));


        Gasto resultado = service.crear(comando);

        assertThat(resultado.getTotal().moneda()).isEqualTo("EUR");
        verify(gastoRepositoryPort).guardar(any(Gasto.class));

    }

    @Test
    @DisplayName("maneja valores nulos sin reventar")
    void manejarValoresNulos(){

        ComandoCrearGasto comando = new ComandoCrearGasto(
                1L, "Proveedor SL", LocalDate.of(2026, 1, 15),
                null, null, new BigDecimal("121.00"), "EUR");

        when(gastoRepositoryPort.guardar(any(Gasto.class)))
                .thenAnswer(invocationOnMock -> invocationOnMock.getArgument(0));


        Gasto resultado = service.crear(comando);


        assertThat(resultado.getBaseImponible()).isNull();
        assertThat(resultado.getIva()).isNull();

    }



}