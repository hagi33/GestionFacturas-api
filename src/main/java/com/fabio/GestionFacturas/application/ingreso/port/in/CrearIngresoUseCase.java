package com.fabio.GestionFacturas.application.ingreso.port.in;

import com.fabio.GestionFacturas.domain.ingreso.Ingreso;

import java.math.BigDecimal;
import java.time.LocalDate;

/** Inbound port for registering a new invoice issued to a client. */
public interface CrearIngresoUseCase {

    Ingreso crear(ComandoCrearIngreso comando);

    /** Command object: the only shape the web layer is allowed to hand into the application layer. */
    record ComandoCrearIngreso(
            Long usuarioId,
            Long clienteId,
            String concepto,
            LocalDate fechaEmision,
            BigDecimal baseImponible,
            BigDecimal iva,
            BigDecimal total,
            String moneda
    ) {}
}
