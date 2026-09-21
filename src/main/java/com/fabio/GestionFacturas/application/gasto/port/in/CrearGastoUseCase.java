package com.fabio.GestionFacturas.application.gasto.port.in;

import com.fabio.GestionFacturas.domain.gasto.Gasto;

import java.math.BigDecimal;
import java.time.LocalDate;

/**
 * Inbound port for manually registering an expense (no OCR involved).
 * Workflow: {@code GastoController} depends only on this interface, never on the concrete
 * service — {@code CrearGastoService} implements it and is injected by Spring at runtime.
 * This is what lets the web layer stay ignorant of how expense creation actually works.
 */
public interface CrearGastoUseCase {

    Gasto crear(ComandoCrearGasto comando);

    /** Command object: the only shape the web layer is allowed to hand into the application layer. */
    record ComandoCrearGasto(
            Long usuarioId,
            String emisor,
            LocalDate fechaEmision,
            BigDecimal baseImponible,
            BigDecimal iva,
            BigDecimal total,
            String moneda,
            Long clienteId

    ){}


    }

