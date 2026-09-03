package com.fabio.GestionFacturas.infrastructure.adapter.in.web.gasto.dto;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;

public record GastoResponse(
        Long id,
        Long usuarioId,
        Long categoriaId,
        String emisor,
        LocalDate fechaEmision,
        BigDecimal baseImponible,
        BigDecimal iva,
        String moneda,
        boolean deducible,
        String estado,
        LocalDateTime creadoEn
) {}
