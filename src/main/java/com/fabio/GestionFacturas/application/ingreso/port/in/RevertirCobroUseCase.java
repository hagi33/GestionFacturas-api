package com.fabio.GestionFacturas.application.ingreso.port.in;

import com.fabio.GestionFacturas.domain.ingreso.Ingreso;

/**
 * Inbound port for reverting a previously registered collection.
 * Implemented by {@code RevertirCobroService}, currently a skeleton pending TDD by the project owner.
 */
public interface RevertirCobroUseCase {

    Ingreso revertirCobro(ComandoRevertirCobro comando);

    record ComandoRevertirCobro(Long ingresoId, Long usuarioId) {}
}
