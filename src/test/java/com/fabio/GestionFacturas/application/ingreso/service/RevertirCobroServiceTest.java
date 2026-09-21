package com.fabio.GestionFacturas.application.ingreso.service;

import com.fabio.GestionFacturas.application.ingreso.port.in.RevertirCobroUseCase.*;
import com.fabio.GestionFacturas.application.ingreso.port.out.IngresoRepositoryPort;
import com.fabio.GestionFacturas.domain.ingreso.EstadoCobro;
import com.fabio.GestionFacturas.domain.ingreso.Ingreso;
import com.fabio.GestionFacturas.domain.ingreso.IngresoNoEncontradoException;
import com.fabio.GestionFacturas.domain.shared.Dinero;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.Optional;

import static org.assertj.core.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;


@ExtendWith(MockitoExtension.class)
class RevertirCobroServiceTest {


    @Mock
    private IngresoRepositoryPort ingresoRepository;

    @InjectMocks
    private RevertirCobroService service;


    @Test
    @DisplayName("revierte el cobro de un ingreso propio y lo guarda")
    void revierteCobroDeIngresoPropio(){

        Ingreso ingreso = Ingreso.crear(1L, 5L, "Factura X", LocalDate.of(2026, 1, 1),
                Dinero.deEuros(BigDecimal.valueOf(100)), Dinero.deEuros(BigDecimal.valueOf(21)),
                Dinero.deEuros(BigDecimal.valueOf(121)));
        ingreso.registrarCobro(LocalDate.of(2026, 2, 1));

        when(ingresoRepository.buscarPorId(10L)).thenReturn(Optional.of(ingreso));
        when(ingresoRepository.guardar(any(Ingreso.class)))
                .thenAnswer(invocacion -> invocacion.getArgument(0));

        service.revertirCobro(new ComandoRevertirCobro(10L, 1L));


        assertThat(ingreso.getEstadoCobro()).isEqualTo(EstadoCobro.PENDIENTE);
        assertThat(ingreso.getFechaCobro()).isNull();
        verify(ingresoRepository).guardar(ingreso);
    }



    @Test
    @DisplayName("no revierte el cobro y lanza excepcion si el ingreso es de otro usuario")
    void controlDeAcceso(){
        Ingreso ingresoDeOtro = Ingreso.crear(2L, 5L, "Factura Y", LocalDate.of(2026, 1, 1),
                Dinero.deEuros(BigDecimal.valueOf(100)), Dinero.deEuros(BigDecimal.valueOf(21)),
                Dinero.deEuros(BigDecimal.valueOf(121)));
        ingresoDeOtro.registrarCobro(LocalDate.of(2026, 2, 1));
        when(ingresoRepository.buscarPorId(10L)).thenReturn(Optional.of(ingresoDeOtro));

        assertThatThrownBy(() -> service.revertirCobro(new ComandoRevertirCobro(10L, 1L)))
                .isInstanceOf(IngresoNoEncontradoException.class);

        verify(ingresoRepository, never()).guardar(any());


    }



}
