package com.fabio.GestionFacturas.application.ingreso.port.in;

import com.fabio.GestionFacturas.domain.ingreso.Ingreso;

import java.time.LocalDate;

/**
 * Inbound port for registering the collection of an invoice.
 * Implemented by {@code RegistrarCobroService}, currently a skeleton pending TDD by the project owner.
 */
public interface RegistrarCobroUseCase {

    Ingreso registrarCobro(ComandoRegistrarCobro comando);

    record ComandoRegistrarCobro(Long ingresoId, Long usuarioId, LocalDate fechaCobro) {}
}
