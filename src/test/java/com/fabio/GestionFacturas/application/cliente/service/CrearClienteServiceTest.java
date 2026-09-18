package com.fabio.GestionFacturas.application.cliente.service;

import com.fabio.GestionFacturas.application.cliente.port.in.CrearClienteUseCase.*;
import com.fabio.GestionFacturas.application.cliente.port.out.ClienteRepositoryPort;
import com.fabio.GestionFacturas.domain.cliente.Cliente;
import com.fabio.GestionFacturas.domain.cliente.ClienteDuplicadoException;
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
class CrearClienteServiceTest {

    @Mock
    private ClienteRepositoryPort clienteRepositoryPort;

    @InjectMocks
    private CrearClienteService crearClienteService;

    @Test
    @DisplayName("crea un cliente nuevo cuando el NIF no existe para el usuario")
    void CrearClienteNuevo() {

        ComandoCrearCliente comandoCrearCliente = new ComandoCrearCliente(
                1L, "Empresa X", "B12345678", "correo@empresax.com", "600000000");

        when(clienteRepositoryPort.buscarPorUsuarioYNif(1L, "B12345678")).thenReturn(Optional.empty());
        when(clienteRepositoryPort.guardar(any(Cliente.class)))
                .thenAnswer(invocationOnMock -> invocationOnMock.getArgument(0));

        Cliente resultado = crearClienteService.crear(comandoCrearCliente);

        assertThat(resultado.getNombre()).isEqualTo("Empresa X");
        assertThat(resultado.getNif()).isEqualTo("B12345678");
        assertThat(resultado.isActivo()).isTrue();
        verify(clienteRepositoryPort).guardar(any(Cliente.class));

    }


    @Test
    @DisplayName("rechaza crear y no guarda si ya existe un usuario con ese NIF")
    void rechazarNifDuplicado() {
        ComandoCrearCliente comando = new ComandoCrearCliente(
                1L, "Empresa X", "B12345678", null, null);


        Cliente existente = new Cliente(5L, 1L, "Empresa X", "B12345678", null, null, true);
        when(clienteRepositoryPort.buscarPorUsuarioYNif(1L, "B12345678")).thenReturn(Optional.of(existente));

        assertThatThrownBy(() -> crearClienteService.crear(comando))
                .isInstanceOf(ClienteDuplicadoException.class);

        verify(clienteRepositoryPort, never()).guardar(any());


    }


}