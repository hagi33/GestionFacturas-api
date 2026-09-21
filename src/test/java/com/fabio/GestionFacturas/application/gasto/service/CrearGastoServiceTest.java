package com.fabio.GestionFacturas.application.gasto.service;

import com.fabio.GestionFacturas.application.cliente.port.out.ClienteRepositoryPort;
import com.fabio.GestionFacturas.application.gasto.port.in.CrearGastoUseCase;
import com.fabio.GestionFacturas.application.gasto.port.out.GastoRepositoryPort;
import com.fabio.GestionFacturas.domain.cliente.Cliente;
import com.fabio.GestionFacturas.domain.cliente.ClienteNoEncontradoException;
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
import java.util.Optional;

import static org.assertj.core.api.Assertions.*;


/**
 * Unit tests for CrearGastoService with the outbound ports mocked ({@code GastoRepositoryPort},
 * {@code ClienteRepositoryPort}) — verifies the service's own logic (Dinero conversion, EUR
 * default, cliente validation) in isolation from any real persistence adapter, per the
 * project's "mock the ports" testing convention.
 */
@ExtendWith(MockitoExtension.class)
class CrearGastoServiceTest {


    @Mock
    private GastoRepositoryPort gastoRepositoryPort;

    @Mock
    private ClienteRepositoryPort clienteRepositoryPort;


    @InjectMocks
    private CrearGastoService service;


    @Test
    @DisplayName("crear convierte los importes a Dinero y delega en el repositorio")
    void crearConvierteImportesYGuarda(){
        //Arrange
        ComandoCrearGasto comando = new ComandoCrearGasto(
                1L, "Proveedor SL", LocalDate.of(2026, 1, 15),
                new BigDecimal("100.00"), new BigDecimal("21.00"), new BigDecimal("121.00"), "EUR", null);

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
                new BigDecimal("100.00"), new BigDecimal("21.00"), new BigDecimal("121.00"), null, null);


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
                null, null, new BigDecimal("121.00"), "EUR", null);

        when(gastoRepositoryPort.guardar(any(Gasto.class)))
                .thenAnswer(invocationOnMock -> invocationOnMock.getArgument(0));


        Gasto resultado = service.crear(comando);


        assertThat(resultado.getBaseImponible()).isNull();
        assertThat(resultado.getIva()).isNull();

    }

    @Test
    @DisplayName("crear con un cliente propio y activo lo asocia al gasto")
    void crearConClientePropioLoAsocia(){
        Cliente cliente = new Cliente(5L, 1L, "Cliente SL", "B11111111", null, null, true);
        when(clienteRepositoryPort.buscarPorId(5L)).thenReturn(Optional.of(cliente));
        when(gastoRepositoryPort.guardar(any(Gasto.class)))
                .thenAnswer(invocationOnMock -> invocationOnMock.getArgument(0));

        ComandoCrearGasto comando = new ComandoCrearGasto(
                1L, "Proveedor SL", LocalDate.of(2026, 1, 15),
                new BigDecimal("100.00"), new BigDecimal("21.00"), new BigDecimal("121.00"), "EUR", 5L);

        Gasto resultado = service.crear(comando);

        assertThat(resultado.getClienteId()).isEqualTo(5L);
        verify(gastoRepositoryPort).guardar(any(Gasto.class));
    }

    @Test
    @DisplayName("crear con un cliente inexistente lanza ClienteNoEncontradoException y no guarda")
    void crearConClienteInexistenteLanzaExcepcion(){
        when(clienteRepositoryPort.buscarPorId(5L)).thenReturn(Optional.empty());

        ComandoCrearGasto comando = new ComandoCrearGasto(
                1L, "Proveedor SL", LocalDate.of(2026, 1, 15),
                new BigDecimal("100.00"), new BigDecimal("21.00"), new BigDecimal("121.00"), "EUR", 5L);

        assertThatThrownBy(() -> service.crear(comando))
                .isInstanceOf(ClienteNoEncontradoException.class);

        verify(gastoRepositoryPort, never()).guardar(any());
    }

    @Test
    @DisplayName("crear con un cliente de otro usuario lanza ClienteNoEncontradoException y no guarda")
    void crearConClienteDeOtroUsuarioLanzaExcepcion(){
        Cliente clienteDeOtro = new Cliente(5L, 2L, "Cliente SL", "B11111111", null, null, true);
        when(clienteRepositoryPort.buscarPorId(5L)).thenReturn(Optional.of(clienteDeOtro));

        ComandoCrearGasto comando = new ComandoCrearGasto(
                1L, "Proveedor SL", LocalDate.of(2026, 1, 15),
                new BigDecimal("100.00"), new BigDecimal("21.00"), new BigDecimal("121.00"), "EUR", 5L);

        assertThatThrownBy(() -> service.crear(comando))
                .isInstanceOf(ClienteNoEncontradoException.class);

        verify(gastoRepositoryPort, never()).guardar(any());
    }

    @Test
    @DisplayName("crear con un cliente inactivo lanza ClienteNoEncontradoException y no guarda")
    void crearConClienteInactivoLanzaExcepcion(){
        Cliente clienteInactivo = new Cliente(5L, 1L, "Cliente SL", "B11111111", null, null, false);
        when(clienteRepositoryPort.buscarPorId(5L)).thenReturn(Optional.of(clienteInactivo));

        ComandoCrearGasto comando = new ComandoCrearGasto(
                1L, "Proveedor SL", LocalDate.of(2026, 1, 15),
                new BigDecimal("100.00"), new BigDecimal("21.00"), new BigDecimal("121.00"), "EUR", 5L);

        assertThatThrownBy(() -> service.crear(comando))
                .isInstanceOf(ClienteNoEncontradoException.class);

        verify(gastoRepositoryPort, never()).guardar(any());
    }

    @Test
    @DisplayName("crear sin cliente no valida y funciona igual que antes")
    void crearSinClienteNoValidaYFunciona(){
        when(gastoRepositoryPort.guardar(any(Gasto.class)))
                .thenAnswer(invocationOnMock -> invocationOnMock.getArgument(0));

        ComandoCrearGasto comando = new ComandoCrearGasto(
                1L, "Proveedor SL", LocalDate.of(2026, 1, 15),
                new BigDecimal("100.00"), new BigDecimal("21.00"), new BigDecimal("121.00"), "EUR", null);

        Gasto resultado = service.crear(comando);

        assertThat(resultado.getClienteId()).isNull();
        verifyNoInteractions(clienteRepositoryPort);
        verify(gastoRepositoryPort).guardar(any(Gasto.class));
    }



}
