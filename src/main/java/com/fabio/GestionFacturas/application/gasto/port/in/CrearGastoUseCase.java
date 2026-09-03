package com.fabio.GestionFacturas.application.gasto.port.in;

import com.fabio.GestionFacturas.domain.gasto.Gasto;

import java.math.BigDecimal;
import java.time.LocalDate;

public interface CrearGastoUseCase {

    Gasto crear(ComandoCrearGasto comando);

    record ComandoCrearGasto(
            Long usuarioId,
            String emisor,
            LocalDate fechaEmision,
            BigDecimal baseImponible,
            BigDecimal iva,
            BigDecimal total,
            String moneda

    ){}


    }

