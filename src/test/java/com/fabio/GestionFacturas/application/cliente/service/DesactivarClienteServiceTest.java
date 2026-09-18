package com.fabio.GestionFacturas.application.cliente.service;

import com.fabio.GestionFacturas.application.cliente.port.in.DesactivarClienteUseCase.*;
import com.fabio.GestionFacturas.application.cliente.port.out.ClienteRepositoryPort;
import com.fabio.GestionFacturas.domain.cliente.Cliente;
import com.fabio.GestionFacturas.domain.cliente.ClienteNoEncontradoException;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Optional;

import static org.assertj.core.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;


@ExtendWith(MockitoExtension.class)
class DesactivarClienteServiceTest {


    @Mock
    private ClienteRepositoryPort clienteRepository;

    @InjectMocks
    private DesactivarClienteService service;


    @Test
    @DisplayName("desactiva un cliente propio dejándolo inactivo y lo guarda")
    void desactivaClientePropio(){

        Cliente cliente = new Cliente(10L, 1L, "Empresa X", "B12345678", null, null, true);
        when(clienteRepository.buscarPorId(10L)).thenReturn(Optional.of(cliente));
        when(clienteRepository.guardar(any(Cliente.class)))
                .thenAnswer(invocacion -> invocacion.getArgument(0));

        service.desactivar(new ComandoDesactivarCliente(10L, 1L));


        assertThat(cliente.isActivo()).isFalse();
        verify(clienteRepository).guardar(cliente);
    }



    @Test
    @DisplayName("no desactuva y lanza excepcion si el cliente es de otro usuario")
    void controlDeAcceso(){
        Cliente clienteDeOtro = new Cliente(10L, 2L, "Empresa Y", "B87654321", null, null, true);
        when(clienteRepository.buscarPorId(10L)).thenReturn(Optional.of(clienteDeOtro));

        assertThatThrownBy(() -> service.desactivar(new ComandoDesactivarCliente(10L, 1L)))
                .isInstanceOf(ClienteNoEncontradoException.class);

        verify(clienteRepository, never()).guardar(any());


    }



}